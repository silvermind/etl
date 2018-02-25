package io.adopteunops.etl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.adopteunops.etl.domain.ESBuffer;
import io.adopteunops.etl.domain.ErrorData;
import io.adopteunops.etl.domain.RetentionLevel;
import io.adopteunops.etl.service.processor.AbstractElasticsearchProcessor;
import io.adopteunops.etl.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.MDC;

import java.util.Date;

@Slf4j
public class ErrorToElasticsearchProcessor extends AbstractElasticsearchProcessor<String, ErrorData> {

    private static final String NO_PROJECT = "no-project";

    public ErrorToElasticsearchProcessor(ESBuffer esBuffer, ESErrorRetryWriter esErrorRetryWriter) {
        super(esBuffer, esErrorRetryWriter);
    }

    @Override
    public void process(String key, ErrorData errorData) {
        try {
            String valueAsString = JSONUtils.getInstance().asJsonString(errorData);
            processToElasticsearch(new Date(errorData.timestamp), NO_PROJECT, errorData.type, RetentionLevel.week, valueAsString);
        } catch (JsonProcessingException e) {
            log.error("Couldn't transform value " + errorData, e);
        }
    }


    @Override
    protected void parseResultErrors(BulkResponse bulkItemResponses) {
        for (BulkItemResponse bir : bulkItemResponses) {
            MDC.put("item_error", bir.getFailureMessage());
            log.info("EsError");
            MDC.remove("item_error");
            //TODO ...
        }
    }
}
