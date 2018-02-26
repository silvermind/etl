package io.adopteunops.etl.service;

import com.google.common.base.Preconditions;
import io.adopteunops.etl.config.KafkaConfiguration;
import io.adopteunops.etl.config.ProcessConfiguration;
import io.adopteunops.etl.domain.*;
import io.adopteunops.etl.rules.metrics.GenericMetricProcessor;
import io.adopteunops.etl.rules.metrics.RuleMetricExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@AllArgsConstructor
@Component
public class MetricImporter {
    private final RuleMetricExecutor ruleMetricExecutor;
    private final KafkaConfiguration kafkaConfiguration;
    private final ProcessConfiguration processConfiguration;
    private final ApplicationContext applicationContext;
    private final Map<ProcessMetric, KafkaStreams> runningMetricProcessors = new HashMap();

    @PostConstruct
    public void init() {
        sendToRegistry("addService");
    }

    public void activate(ProcessMetric processMetric) {
        Preconditions.checkArgument(!processMetric.getFromTopic().equalsIgnoreCase(processMetric.getToTopic()), "destination topic cannot be equal to source topic");
        log.info("creating {} Metric Stream Process", processMetric.getName());
        GenericMetricProcessor metricProcessor = ruleMetricExecutor.instanciate(processMetric.getName(), processMetric.toDSL());
        metricProcessor.setApplicationContext(applicationContext);

        KafkaStreams metricStream = metricProcessor.buildStream(createProperties(kafkaConfiguration.getBootstrapServers()));
        metricStream.start();
        ProcessMetric processMetricDefinition = processMetric.withTimestamp(new Date());
        runningMetricProcessors.put(processMetricDefinition, metricStream);
    }

    public void deactivate(ProcessMetric processMetric) {
        log.info("deactivating {} Metric Stream Process", processMetric.getName());
        runningMetricProcessors.get(processMetric).close();
        runningMetricProcessors.remove(processMetric);
    }

    private Properties createProperties(String bootstrapServers) {
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        return props;
    }

    private void sendToRegistry(String action) {
        RegistryWorker registry = null;
        try {
            registry = RegistryWorker.builder()
                    .workerType(WorkerType.METRIC_PROCESS)
                    .ip(InetAddress.getLocalHost().getHostName())
                    .name(InetAddress.getLocalHost().getHostName())
                    .port(processConfiguration.getPortClient())
                    .statusConsumerList(statusExecutor())
                    .build();
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<RegistryWorker> request = new HttpEntity<>(registry);
            String url = processConfiguration.getUrlRegistry();
            String res = restTemplate.postForObject(url + "/process/registry/" + action, request, String.class);
            log.debug("sendToRegistry result {}", res);
        } catch (Exception e) {
            log.error("Exception on sendToRegistry", e);
        }

    }

    public List<StatusConsumer> statusExecutor() {
        return runningMetricProcessors.keySet().stream()
                .map(e -> StatusConsumer.builder()
                        .statusProcess(StatusProcess.ENABLE)
                        .creation(e.getTimestamp())
                        .idProcessConsumer(e.getIdProcess())
                        .build())
                .collect(toList());
    }

    @Scheduled(initialDelay = 20 * 1000, fixedRate = 5 * 60 * 1000)
    public void refresh() {
        sendToRegistry("refresh");
    }

}
