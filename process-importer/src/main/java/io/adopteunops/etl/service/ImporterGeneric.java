package io.adopteunops.etl.service;

import io.adopteunops.etl.admin.KafkaAdminService;
import io.adopteunops.etl.config.ProcessConfiguration;
import io.adopteunops.etl.domain.ProcessConsumer;
import io.adopteunops.etl.domain.ProcessFilter;
import io.adopteunops.etl.rules.filters.GenericFilter;
import io.adopteunops.etl.rules.filters.RuleFilterExecutor;
import io.adopteunops.etl.service.processor.ValidateDataToElasticSearchProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
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
    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        sendToRegistry("addService");
    }

    public ImporterGeneric(ESErrorRetryWriter esErrorRetryWriter, GenericValidator genericValidator, GenericTransformator transformValidator, GenericParser genericParser, RuleFilterExecutor ruleFilterExecutor, KafkaAdminService kafkaAdminService, ProcessConfiguration processConfiguration, ExternalHTTPService externalHTTPService, ApplicationContext applicationContext) {
        super(genericValidator, transformValidator, genericParser, processConfiguration, externalHTTPService);
        this.ruleFilterExecutor = ruleFilterExecutor;
        this.esErrorRetryWriter = esErrorRetryWriter;
        this.kafkaAdminService = kafkaAdminService;
        this.applicationContext = applicationContext;
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
                applicationContext.getBean(ValidateDataToElasticSearchProcessor.class)
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
