package io.adopteunops.etl.validate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.adopteunops.etl.RawDataGen;
import io.adopteunops.etl.domain.ParameterValidation;
import io.adopteunops.etl.domain.ProcessValidation;
import io.adopteunops.etl.domain.TypeValidation;
import io.adopteunops.etl.domain.ValidateData;
import io.adopteunops.etl.service.validate.MaxFieldValidator;
import io.adopteunops.etl.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MaxFieldValidatorTest {
    @Test
    public void should_Process_Ko() throws Exception {
        MaxFieldValidator maxFieldValidator = new MaxFieldValidator(TypeValidation.MAX_FIELD);
        RawDataGen rd = RawDataGen.builder().messageSend("gni").project("project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        JsonNode jsonValue = JSONUtils.getInstance().parse(value);

        ValidateData v = maxFieldValidator.process(ProcessValidation.builder()
                .parameterValidation(ParameterValidation.builder()
                        .maxFields(2L).build()
                ).build(), jsonValue, value);
        assertThat(v.success).isFalse();
    }

    @Test
    public void should_Process_Ok() throws Exception {
        MaxFieldValidator maxFieldValidator = new MaxFieldValidator(TypeValidation.MAX_FIELD);
        RawDataGen rd = RawDataGen.builder().messageSend("gni").project("project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        JsonNode jsonValue = JSONUtils.getInstance().parse(value);

        ValidateData v = maxFieldValidator.process(ProcessValidation.builder()
                .parameterValidation(ParameterValidation.builder()
                        .maxFields(7L).build()
                ).build(), jsonValue, value);
        assertThat(v.success).isTrue();
    }

}
