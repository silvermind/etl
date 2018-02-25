package io.adopteunops.etl.service;

import io.adopteunops.etl.config.KafkaConfiguration;
import io.adopteunops.etl.config.ProcessConfiguration;
import io.adopteunops.etl.domain.*;
import io.adopteunops.etl.serdes.ValidateDataDeserializer;
import io.adopteunops.etl.serdes.ValidateDataSerializer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.adopteunops.etl.utils.KafkaUtils.createKStreamProperties;
import static java.util.stream.Collectors.toList;

@Component
@Lazy(value = false)
@Slf4j
@AllArgsConstructor
public class ReferentialImporter {

    private final KafkaConfiguration kafkaConfiguration;
    private final ProcessConfiguration processConfiguration;
    private final ReferentialService referentialService;
    private final Map<ProcessReferential, List<KafkaStreams>> runningProcessReferential = new HashMap();
    public static final String TOPIC_PARSED_PROCESS = "parsedprocess";

    @PostConstruct
    public void init() {
        sendToRegistry("addService");
    }

    public void activate(ProcessReferential processReferential) {
        // init TODO --> modify for reload
        runningProcessReferential.put(processReferential, new ArrayList<>());
        log.info("creating {} Process Referential", processReferential.getName());
        //getProcessConsumer().getIdProcess() + TOPIC_PARSED_PROCESS
        processReferential.getListIdProcessConsumer().stream().forEach(consumerId -> buildStream(processReferential, consumerId + TOPIC_PARSED_PROCESS));
    }

    public void deactivate(ProcessReferential processReferential) {
        log.info("deactivating {} Process Referential", processReferential.getName());
        runningProcessReferential.get(processReferential).stream().forEach(kafkaStreams -> kafkaStreams.close());
        runningProcessReferential.remove(processReferential);
    }

    private void buildStream(ProcessReferential processReferential, String topicSource) {
        StreamsBuilder builder = new StreamsBuilder();
        final Serde<ValidateData> validateDataSerdes = Serdes.serdeFrom(new ValidateDataSerializer(), new ValidateDataDeserializer());
        KStream<String, ValidateData> streamToRef = builder.stream(topicSource, Consumed.with(Serdes.String(), validateDataSerdes));
        streamToRef.process(() -> new ReferentialProcessor(processReferential, referentialService));
        KafkaStreams streams = new KafkaStreams(builder.build(), createKStreamProperties(processReferential.getIdProcess() + "#" + topicSource, kafkaConfiguration.getBootstrapServers()));
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        runningProcessReferential.get(processReferential).add(streams);
        streams.start();
    }

    private void sendToRegistry(String action) {
        RegistryWorker registry = null;
        try {
            registry = RegistryWorker.builder()
                    .workerType(WorkerType.REFERENTIAL_PROCESS)
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
        return runningProcessReferential.keySet().stream()
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

