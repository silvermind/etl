package io.adopteunops.etl.service.validate;

import com.fasterxml.jackson.databind.JsonNode;
import io.adopteunops.etl.domain.ProcessValidation;
import io.adopteunops.etl.domain.TypeValidation;
import io.adopteunops.etl.domain.ValidateData;
import io.adopteunops.etl.utils.StatusCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FieldExistValidator extends ValidatorProcess {

    public FieldExistValidator(TypeValidation type) {
        super(type);
    }

    public ValidateData process(ProcessValidation processValidation, JsonNode jsonValue, String value) {
        // fields count
        if (jsonValue.get(processValidation.getParameterValidation().getFieldExist()) == null) {
            return ValidateData.builder()
                    .success(false)
                    .statusCode(StatusCode.field_not_exist)
                    .typeValidation(TypeValidation.MAX_FIELD)
                    .message("Field " + processValidation.getParameterValidation().getFieldExist() + " is not present")
                    .jsonValue(jsonValue)
                    .build();

        } else {
            return ValidateData.builder()
                    .success(true)
                    .typeValidation(TypeValidation.FIELD_EXIST)
                    .jsonValue(jsonValue)
                    .build();
        }

    }


}
