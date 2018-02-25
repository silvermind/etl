package io.adopteunops.etl.service;

import io.adopteunops.etl.config.KafkaConfiguration;
import io.adopteunops.etl.domain.ErrorData;
import io.adopteunops.etl.domain.ValidateData;
import io.adopteunops.etl.serdes.ErrorDataSerializer;
import io.adopteunops.etl.serdes.ValidateDataSerializer;
import io.adopteunops.etl.utils.JSONUtils;
import io.adopteunops.etl.utils.KafkaUtils;
import io.prometheus.client.Counter;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

@Component

public class ESErrorRetryWriter {

    private final KafkaConfiguration kafkaConfiguration;
    private final Producer<String, ErrorData> errorProducer;
    private final Producer<String, ValidateData> retryProducer;
    private static final Counter producerErrorKafkaCount = Counter.build()
            .name("nb_produce_error_kafka_count")
            .help("count nb error elements.")
            .labelNames("processConsumerName", "type", "reason")
            .register();
    private static final Counter produceMessageToKafka = Counter.build()
            .name("nb_produce_message_kafka_count")
            .help("count nb produce kafka")
            .labelNames("processConsumerName", "topic", "type")
            .register();

    public ESErrorRetryWriter(KafkaConfiguration kafkaConfiguration) {
        this.kafkaConfiguration = kafkaConfiguration;
        this.errorProducer = KafkaUtils.kafkaProducer(kafkaConfiguration.getBootstrapServers(), StringSerializer.class, ErrorDataSerializer.class);
        this.retryProducer = KafkaUtils.kafkaProducer(kafkaConfiguration.getBootstrapServers(), StringSerializer.class, ValidateDataSerializer.class);
    }


    public void sendToErrorTopic(String applicationId, ErrorData errorData) {
        producerErrorKafkaCount.labels(applicationId, errorData.type, errorData.errorReason).inc();
        produceMessageToKafka.labels(applicationId, kafkaConfiguration.getErrorTopic(), errorData.getType()).inc();
        errorProducer.send(new ProducerRecord<>(kafkaConfiguration.getErrorTopic(), errorData));
    }

    public void sendToRetryTopic(String applicationId, String validateDataAsString) {
        sendToRetryTopic(applicationId, JSONUtils.getInstance().parse(validateDataAsString, ValidateData.class));
    }

    public void sendToRetryTopic(String applicationId, ValidateData validateData) {
        produceMessageToKafka.labels(applicationId, kafkaConfiguration.getRetryTopic(), validateData.getType()).inc();
        retryProducer.send(new ProducerRecord<>(kafkaConfiguration.getRetryTopic(), validateData));
    }

}
