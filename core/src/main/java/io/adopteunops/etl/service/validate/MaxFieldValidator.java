package io.adopteunops.etl.service.validate;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Iterators;
import io.adopteunops.etl.domain.ProcessValidation;
import io.adopteunops.etl.domain.TypeValidation;
import io.adopteunops.etl.domain.ValidateData;
import io.adopteunops.etl.utils.StatusCode;
import io.prometheus.client.Histogram;
import lombok.extern.slf4j.Slf4j;

import static io.adopteunops.etl.service.UtilsValidateData.createValidateData;

@Slf4j
public class MaxFieldValidator extends ValidatorProcess {

    private static final Histogram eventSizeHistogram = Histogram.build()
            .name("event_size")
            .help("event size.")
            .linearBuckets(1, 100, 10)
            .register();

    public MaxFieldValidator(TypeValidation type) {
        super(type);
    }

    public ValidateData process(ProcessValidation processValidation, JsonNode jsonValue, String value) {
        // fields count
        int nbFields = Iterators.size(jsonValue.fieldNames());
        eventSizeHistogram.observe(nbFields);
        if (nbFields > processValidation.getParameterValidation().getMaxFields()) {
            return createValidateData(false, StatusCode.max_fields, TypeValidation.MAX_FIELD, value, String.valueOf(nbFields));
        }
        return ValidateData.builder()
                .success(true)
                .typeValidation(TypeValidation.MAX_FIELD)
                .jsonValue(jsonValue)
                .build();
    }


}
