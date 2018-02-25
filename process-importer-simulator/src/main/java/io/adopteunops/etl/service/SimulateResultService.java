package io.adopteunops.etl.service;

import io.adopteunops.etl.domain.SimulateData;
import io.adopteunops.etl.utils.KafkaUtils;
import io.adopteunops.etl.utils.Rebalancer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.adopteunops.etl.service.AbstractStreamProcess.SIMULATE_OUTPUT;

@Component
@AllArgsConstructor
@Slf4j
public class SimulateResultService {

    private final KafkaUtils kafkaUtils;

    public List<SimulateData> readOutPut(String bootStrapServers, String maxPollRecords, String pollingTime) {
        KafkaConsumer kafkaConsumer = kafkaUtils.kafkaConsumer("latest", bootStrapServers, "simulate", maxPollRecords);
        log.info("Subscribe Topic for {}", SIMULATE_OUTPUT);
        kafkaConsumer.subscribe(Arrays.asList(SIMULATE_OUTPUT), new Rebalancer());
        List<SimulateData> res = new ArrayList<>();
        try {
            ConsumerRecords<String, SimulateData> records = kafkaConsumer.poll(Integer.valueOf(pollingTime));
            for (ConsumerRecord<String, SimulateData> record : records) {
                res.add(record.value());
            }
            kafkaConsumer.commitSync();
        } catch (WakeupException e) {
            // Ignore exception if closing
            throw e;
        } catch (RuntimeException re) {
            log.error("RuntimeException {}", re);
        } finally {
            kafkaConsumer.close();
        }
        return res;
    }
}
