package io.adopteunops.etl.rules.metrics.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import io.adopteunops.etl.domain.ESBuffer;
import io.adopteunops.etl.domain.RetentionLevel;
import io.adopteunops.etl.rules.metrics.domain.Keys;
import io.adopteunops.etl.rules.metrics.domain.MetricResult;
import io.adopteunops.etl.service.ESErrorRetryWriter;
import io.adopteunops.etl.service.processor.AbstractElasticsearchProcessor;
import io.adopteunops.etl.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricsElasticsearchProcessor extends AbstractElasticsearchProcessor<Keys, MetricResult> {

    private final ISO8601DateFormat dateFormat = new ISO8601DateFormat();
    private final RetentionLevel retentionLevel;

    public MetricsElasticsearchProcessor(ESBuffer esBuffer, ESErrorRetryWriter esErrorRetryWriter, RetentionLevel retention) {
        super(esBuffer, esErrorRetryWriter);
        retentionLevel = retention;
    }

    @Override
    public void process(Keys key, MetricResult value) {
        try {
            String valueAsString = JSONUtils.getInstance().asJsonString(value);
            processToElasticsearch(value.getTimestamp(), value.getProject(), "metrics", retentionLevel, valueAsString);
        } catch (JsonProcessingException e) {
            log.error("Couldn't transform value as metric " + key, e);
        }
    }
}
