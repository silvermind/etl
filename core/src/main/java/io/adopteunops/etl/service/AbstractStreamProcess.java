package io.adopteunops.etl.service;

import io.adopteunops.etl.domain.ProcessConsumer;
import io.prometheus.client.Counter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
@Getter
@AllArgsConstructor
public abstract class AbstractStreamProcess implements Runnable {

    private final GenericValidator genericValidator;
    private final GenericTransformator genericTransformator;
    private final GenericParser genericParser;
    private final ProcessConsumer processConsumer;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final HashMap<String, KafkaStreams> mapStreams = new HashMap<>();
    public static final String ES_PROCESS = "esprocess";
    public static final String REFERENTIAL_PROCESS = "referentialprocess";
    public static final String SIMULATE_INPUT = "simulateinput";
    public static final String SIMULATE_OUTPUT = "simulateoutput";
    public static final String INPUT_PROCESS = "inputprocess";
    public static final String SIMULATE_PROCESS = "simulateprocess";
    public static final String VALIDATE_PROCESS = "validateprocess";
    public static final String KAFKA_PROCESS = "kafkaprocess";
    public static final String SYSOUT_PROCESS = "sysoutprocess";
    public static final String TOPIC_TREAT_PROCESS = "treatprocess";
    public static final String TOPIC_PARSED_PROCESS = "parsedprocess";
    public static final String ERROR_PARSING = "ERROR_PARSING";
    public static final String TIMESTAMP = "@timestamp";
    public static final String TYPE = "type";
    public static final String PROJECT = "project";
    public static final String RETENTION = "retention";

    public static final Counter readKafkaCount = Counter.build()
            .name("nb_read_kafka_count")
            .help("nb read")
            .labelNames("processConsumerName")
            .register();
    public static final Counter transformationAndValidationCount = Counter.build()
            .name("nb_transformation_validation_count")
            .help("nb transfo & validation")
            .labelNames("processConsumerName")
            .register();
    public static final Counter producerErrorKafkaCount = Counter.build()
            .name("nb_produce_error_kafka_count")
            .help("count nb error elements.")
            .labelNames("processConsumerName", "type", "reason")
            .register();
    public static final Counter produceMessageToKafka = Counter.build()
            .name("nb_produce_message_kafka_count")
            .help("count nb produce kafka")
            .labelNames("processConsumerName", "topic", "type")
            .register();


    public abstract void createStreamProcess();

    public void addStreams(String key, KafkaStreams streams) {
        mapStreams.put(key, streams);
    }

    public void shutdownAllStreams() {
        mapStreams.values().stream()
                .forEach(e -> e.close());
    }

    @Override
    public void run() {
        createStreamProcess();
    }


    public String getBootstrapServer() {
        return getProcessConsumer().getProcessInput().getHost() + ":" + getProcessConsumer().getProcessInput().getPort();
    }

}
