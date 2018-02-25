package io.adopteunops.etl.validate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.adopteunops.etl.RawDataGen;
import io.adopteunops.etl.domain.ParameterValidation;
import io.adopteunops.etl.domain.ProcessValidation;
import io.adopteunops.etl.domain.TypeValidation;
import io.adopteunops.etl.domain.ValidateData;
import io.adopteunops.etl.service.validate.MaxMessageSizeValidator;
import io.adopteunops.etl.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MaxMessageSizeValidatorTest {
    @Test
    public void should_Process_Ko() throws Exception {
        MaxMessageSizeValidator maxMessageSizeValidator = new MaxMessageSizeValidator(TypeValidation.MAX_MESSAGE_SIZE);
        RawDataGen rd = RawDataGen.builder().messageSend("gni").project("project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        ObjectNode jsonValue = JSONUtils.getInstance().parseObj(value);

        ValidateData v = maxMessageSizeValidator.process(ProcessValidation.builder()
                .parameterValidation(ParameterValidation.builder()
                        .maxMessageSize(100L).build()
                ).build(), jsonValue, value);
        assertThat(v.success).isFalse();
    }

    @Test
    public void should_Process_Ok() throws Exception {
        MaxMessageSizeValidator maxMessageSizeValidator = new MaxMessageSizeValidator(TypeValidation.MAX_MESSAGE_SIZE);
        RawDataGen rd = RawDataGen.builder().messageSend("gni").project("project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        ObjectNode jsonValue = JSONUtils.getInstance().parseObj(value);

        ValidateData v = maxMessageSizeValidator.process(ProcessValidation.builder()
                .parameterValidation(ParameterValidation.builder()
                        .maxMessageSize(124L).build()
                ).build(), jsonValue, value);
        assertThat(v.success).isTrue();
    }
}
