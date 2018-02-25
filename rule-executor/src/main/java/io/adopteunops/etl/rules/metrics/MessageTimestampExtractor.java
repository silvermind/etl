package io.adopteunops.etl.rules.metrics;

import io.adopteunops.etl.domain.ValidateData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class MessageTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
        long timestamp = -1;
        final ValidateData validateData = (ValidateData) record.value();
        if (validateData != null) {
            timestamp = validateData.getTimestamp().toInstant().toEpochMilli();
        }
        if (false) {
            if (timestamp < 0) {
                // Invalid timestamp!  Attempt to estimate a new timestamp,
                // otherwise fall back to wall-clock time (processing-time).
                if (previousTimestamp >= 0) {
                    return previousTimestamp;
                } else {
                    return System.currentTimeMillis();
                }
            }
        }
        return timestamp;
    }
}
