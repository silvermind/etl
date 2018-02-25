package io.adopteunops.etl.rules.metrics.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import io.adopteunops.etl.domain.ESBuffer;
import io.adopteunops.etl.domain.RetentionLevel;
import io.adopteunops.etl.rules.metrics.domain.Keys;
import io.adopteunops.etl.service.ESErrorRetryWriter;
import io.adopteunops.etl.service.processor.AbstractElasticsearchProcessor;
import io.adopteunops.etl.utils.JSONUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class MetricsElasticsearchProcessor extends AbstractElasticsearchProcessor<Keys, Double> {

    private final ISO8601DateFormat dateFormat = new ISO8601DateFormat();
    private final RetentionLevel retentionLevel;

    public MetricsElasticsearchProcessor(ESBuffer esBuffer, ESErrorRetryWriter esErrorRetryWriter, RetentionLevel retention) {
        super(esBuffer, esErrorRetryWriter);
        retentionLevel = retention;
    }

    @Override
    public void process(Keys key, Double value) {
        ESMetrics esMetrics = new ESMetrics(dateFormat.format(new Date()), key, value);


        try {
            String valueAsString = JSONUtils.getInstance().asJsonString(esMetrics);
            processToElasticsearch(new Date(), key.getProject(), "metrics", retentionLevel, valueAsString);
        } catch (JsonProcessingException e) {
            log.error("Couldn't transform value as metric " + key, e);
        }
    }

    @AllArgsConstructor
    @Getter
    private class ESMetrics {
        private final String timestamp;
        private final Keys keys;
        private final Double value;
    }
}
