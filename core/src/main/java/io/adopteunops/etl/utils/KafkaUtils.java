package io.adopteunops.etl.utils;

import io.adopteunops.etl.config.KafkaConfiguration;
import io.adopteunops.etl.domain.ProcessConsumer;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@AllArgsConstructor
public class KafkaUtils {

    private final KafkaConfiguration kafkaConfiguration;

    public KafkaConsumer<String, String> kafkaConsumer() {
        return kafkaConsumer(kafkaConfiguration.getAutoOffsetReset().toString(), kafkaConfiguration.getBootstrapServers(), kafkaConfiguration.getGroupId(), kafkaConfiguration.getPollRecord());
    }

    public KafkaConsumer<String, String> kafkaConsumer(String offReset, String bootStrapServers, String groupId, String maxPollRecords) {
        Properties props = new Properties();
        props.put("auto.offset.reset", offReset);
        props.put("bootstrap.servers", bootStrapServers);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("max.poll.records", maxPollRecords);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "io.adopteunops.etl.domain.SimulateDataDeserializer");
        return new KafkaConsumer<>(props);
    }

    public Producer<String, String> kafkaProducer(String bootstrapServer) {
        return kafkaProducer(bootstrapServer, StringSerializer.class, StringSerializer.class);
    }

    public static <K, V> Producer<K, V> kafkaProducer(String bootstrapServer, Class<? extends Serializer<K>> keySerializer, Class<? extends Serializer<V>> valueSerializer) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServer);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 1);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", keySerializer.getName());
        props.put("value.serializer", valueSerializer.getName());
        return new KafkaProducer<>(props);
    }

    public Producer<String, String> kafkaProducer() {
        return kafkaProducer(kafkaConfiguration.getBootstrapServers());
    }

    public Producer<String, String> kafkaProducer(ProcessConsumer processConsumer) {
        return kafkaProducer(processConsumer.getProcessInput().bootstrapServer());
    }

    public static Properties createKStreamProperties(String nameProcess, String bootstrapServers) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "application-process" + nameProcess);
        props.put(StreamsConfig.CLIENT_ID_CONFIG, nameProcess);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        return props;
    }
}
