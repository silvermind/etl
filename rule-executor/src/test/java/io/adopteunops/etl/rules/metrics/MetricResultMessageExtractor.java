package io.adopteunops.etl.rules.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.adopteunops.etl.kafka.KafkaUnit;
import io.adopteunops.etl.rules.metrics.domain.Keys;
import io.adopteunops.etl.rules.metrics.domain.MetricResult;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public class MetricResultMessageExtractor implements KafkaUnit.MessageExtractor<Keys, MetricResult> {

    @Override
    public KafkaUnit.Message<Keys, MetricResult> extract(ConsumerRecord<byte[], byte[]> record) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Keys keys = objectMapper.readValue(record.key(), Keys.class);
            MetricResult value = objectMapper.readValue(record.value(), MetricResult.class);

            return new KafkaUnit.Message<>(keys, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
