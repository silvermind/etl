package io.adopteunops.etl.validate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.adopteunops.etl.RawDataGen;
import io.adopteunops.etl.domain.ParameterValidation;
import io.adopteunops.etl.domain.ProcessValidation;
import io.adopteunops.etl.domain.TypeValidation;
import io.adopteunops.etl.domain.ValidateData;
import io.adopteunops.etl.service.validate.FieldExistValidator;
import io.adopteunops.etl.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class FieldExistValidatorTest {

    @Test
    public void should_Process_Ok() throws Exception {
        FieldExistValidator fieldExistValidator = new FieldExistValidator(TypeValidation.FIELD_EXIST);
        RawDataGen rd = RawDataGen.builder().messageSend("gni").project("project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        JsonNode jsonValue = JSONUtils.getInstance().parse(value);

        ValidateData v = fieldExistValidator.process(ProcessValidation.builder()
                .parameterValidation(ParameterValidation.builder()
                        .fieldExist("messageSend")
                        .build()
                ).build(), jsonValue, value);
        assertThat(v.success).isTrue();
    }

    @Test
    public void should_Process_Ko() throws Exception {
        FieldExistValidator fieldExistValidator = new FieldExistValidator(TypeValidation.FIELD_EXIST);
        RawDataGen rd = RawDataGen.builder().messageSend("gni").project("project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        JsonNode jsonValue = JSONUtils.getInstance().parse(value);

        ValidateData v = fieldExistValidator.process(ProcessValidation.builder()
                .parameterValidation(ParameterValidation.builder()
                        .fieldExist("toto")
                        .build()
                ).build(), jsonValue, value);
        assertThat(v.success).isFalse();
    }

}
