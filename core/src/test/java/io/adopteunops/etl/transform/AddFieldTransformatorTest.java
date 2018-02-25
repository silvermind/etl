package io.adopteunops.etl.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.adopteunops.etl.RawDataGen;
import io.adopteunops.etl.domain.ParameterTransformation;
import io.adopteunops.etl.domain.ProcessKeyValue;
import io.adopteunops.etl.domain.TypeValidation;
import io.adopteunops.etl.service.transform.AddFieldTransformator;
import io.adopteunops.etl.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class AddFieldTransformatorTest {
    @Test
    public void should_Process_Ok() throws Exception {
        AddFieldTransformator addFieldValidator = new AddFieldTransformator(TypeValidation.ADD_FIELD);
        RawDataGen rd = RawDataGen.builder().messageSend("gni").project("project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        ObjectNode jsonValue = JSONUtils.getInstance().parseObj(value);

        addFieldValidator.apply(null, ParameterTransformation.builder()
                .composeField(ProcessKeyValue.builder()
                        .key("addField")
                        .value("value")
                        .build()
                ).build(), jsonValue, value);
        assertThat(jsonValue.path("addField").asText()).isEqualTo("value");
    }

}
