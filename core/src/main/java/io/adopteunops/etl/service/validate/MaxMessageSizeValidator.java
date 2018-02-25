package io.adopteunops.etl.service.validate;

import com.fasterxml.jackson.databind.JsonNode;
import io.adopteunops.etl.domain.ProcessValidation;
import io.adopteunops.etl.domain.TypeValidation;
import io.adopteunops.etl.domain.ValidateData;
import io.adopteunops.etl.utils.StatusCode;
import io.prometheus.client.Histogram;
import lombok.extern.slf4j.Slf4j;

import static io.adopteunops.etl.service.UtilsValidateData.createValidateData;

@Slf4j
public class MaxMessageSizeValidator extends ValidatorProcess {

    private static final Histogram nbFieldsHistogram = Histogram.build()
            .name("nb_fields")
            .help("nb fields.")
            .linearBuckets(1, 10, 10)
            .register();

    public MaxMessageSizeValidator(TypeValidation type) {
        super(type);
    }


    public ValidateData process(ProcessValidation processValidation, JsonNode jsonValue, String value) {
        int eventSize = value.length();
        nbFieldsHistogram.observe(eventSize);
        if (eventSize > processValidation.getParameterValidation().getMaxMessageSize()) {
            return createValidateData(false, StatusCode.event_size, TypeValidation.MAX_MESSAGE_SIZE, value, String.valueOf(eventSize));
        }
        return ValidateData.builder()
                .success(true)
                .typeValidation(TypeValidation.MAX_MESSAGE_SIZE)
                .jsonValue(jsonValue)
                .build();
    }


}