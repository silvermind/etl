package io.adopteunops.etl.service;

import io.adopteunops.etl.admin.KafkaAdminService;
import io.adopteunops.etl.config.ESBufferConfiguration;
import io.adopteunops.etl.config.ESConfiguration;
import io.adopteunops.etl.config.ProcessConfiguration;
import io.adopteunops.etl.domain.ESBuffer;
import io.adopteunops.etl.domain.ProcessConsumer;
import io.adopteunops.etl.domain.ProcessFilter;
import io.adopteunops.etl.rules.filters.GenericFilter;
import io.adopteunops.etl.rules.filters.RuleFilterExecutor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static io.adopteunops.etl.service.AbstractStreamProcess.TOPIC_PARSED_PROCESS;
import static io.adopteunops.etl.service.AbstractStreamProcess.TOPIC_TREAT_PROCESS;

@Component
@Lazy(value = false)
@Slf4j
public class ImporterGeneric extends AbstractGenericImporter {

    private final RuleFilterExecutor ruleFilterExecutor;
    private final ESErrorRetryWriter esErrorRetryWriter;
    private final KafkaAdminService kafkaAdminService;

    @PostConstruct
    public void init() {
        sendToRegistry("addService");
    }

    public ImporterGeneric(RestHighLevelClient elasticsearchClient, ESErrorRetryWriter esErrorRetryWriter, ESBufferConfiguration esBufferConfiguration, ESConfiguration esConfiguration, GenericValidator genericValidator, GenericTransformator transformValidator, GenericParser genericParser, RuleFilterExecutor ruleFilterExecutor, KafkaAdminService kafkaAdminService, ProcessConfiguration processConfiguration, ExternalHTTPService externalHTTPService) {
        super(elasticsearchClient, genericValidator, transformValidator, genericParser, esBufferConfiguration, esConfiguration, processConfiguration, externalHTTPService);
        this.ruleFilterExecutor = ruleFilterExecutor;
        this.esErrorRetryWriter = esErrorRetryWriter;
        this.kafkaAdminService = kafkaAdminService;
    }

    public void createProcessGeneric(ProcessConsumer processConsumer) {

        processConsumer.setTimestamp(new Date());
        log.info("Create topic for importer");
        kafkaAdminService.buildTopic(processConsumer.getProcessInput().getTopicInput(),
                processConsumer.getIdProcess() + TOPIC_PARSED_PROCESS,
                processConsumer.getIdProcess() + TOPIC_TREAT_PROCESS,
                processConsumer.getProcessOutput().getParameterOutput().getTopicOut()
        );
        getExternalHTTPService().buildCache(processConsumer);
        log.info("Create process importer {}", processConsumer.getName());
        ESBuffer esBuffer = new ESBuffer(getElasticsearchClient(), getEsBufferConfiguration(), getEsConfiguration());
        List<GenericFilter> genericFilters = new ArrayList<>();
        for (ProcessFilter processFilter : processConsumer.getProcessFilter()) {
            genericFilters.add(ruleFilterExecutor.instanciate(processFilter.getName(), processFilter.getCriteria()));
        }
        ProcessStreamService processStreamService = new ProcessStreamService(
                getGenericValidator(),
                getGenericTransformator(),
                getGenericParser(),
                processConsumer,
                genericFilters,
                esErrorRetryWriter,
                esBuffer
        );
        getListConsumer().add(processStreamService);
        getExecutor().submit(processStreamService);
        sendToRegistry("refresh");
    }

    @Scheduled(initialDelay = 20 * 1000, fixedRate = 5 * 60 * 1000)
    public void refresh() {
        sendToRegistry("refresh");
    }
}
