package io.adopteunops.etl.service.processor;

import io.adopteunops.etl.domain.TypeOutput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingProcessor<K, V> extends AbstractOutputProcessor<K, V> {
    @Override
    public void process(K key, V value) {
        log.info("key {} value {}", key, value);
    }


    @Override
    public boolean support(TypeOutput typeOutput) {
        return typeOutput == TypeOutput.SYSTEM_OUT;
    }
}
