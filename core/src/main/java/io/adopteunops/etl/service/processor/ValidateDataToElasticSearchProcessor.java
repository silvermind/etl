package io.adopteunops.etl.service.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.adopteunops.etl.domain.ESBuffer;
import io.adopteunops.etl.domain.RetentionLevel;
import io.adopteunops.etl.domain.ValidateData;
import io.adopteunops.etl.service.ESErrorRetryWriter;
import io.adopteunops.etl.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidateDataToElasticSearchProcessor extends AbstractElasticsearchProcessor<String, ValidateData> {

    public ValidateDataToElasticSearchProcessor(ESBuffer esBuffer, ESErrorRetryWriter esErrorRetryWriter) {
        super(esBuffer, esErrorRetryWriter);
    }

    @Override
    public void process(String key, ValidateData validateData) {
        RetentionLevel retentionLevel = validateData.jsonValue.has("retention") ? RetentionLevel.valueOf(validateData.jsonValue.path("retention").asText()) : RetentionLevel.week;

        try {
            String valueAsString = JSONUtils.getInstance().asJsonString(validateData);
            processToElasticsearch(validateData.timestamp, validateData.project, validateData.type, retentionLevel, valueAsString);
        } catch (JsonProcessingException e) {
            log.error("Couldn't transform value " + validateData, e);
        }
    }
}
