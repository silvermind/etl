package io.adopteunops.etl.rules.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.adopteunops.etl.kafka.KafkaUnit;
import io.adopteunops.etl.rules.metrics.domain.Keys;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.nio.ByteBuffer;

public class KeysDoubleMessageExtractor implements KafkaUnit.MessageExtractor<Keys, Double> {

    @Override
    public KafkaUnit.Message<Keys, Double> extract(ConsumerRecord<byte[], byte[]> record) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Keys keys = objectMapper.readValue(record.key(), Keys.class);
            double value = ByteBuffer.wrap(record.value()).getDouble();

            return new KafkaUnit.Message<>(keys, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
