package io.adopteunops.etl.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.adopteunops.etl.RawDataGen;
import io.adopteunops.etl.domain.ParameterTransformation;
import io.adopteunops.etl.domain.TypeValidation;
import io.adopteunops.etl.service.transform.IpFieldTransformator;
import io.adopteunops.etl.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class IpFieldTransformatorTest {
    @Test
    public void should_Process_Ok() throws Exception {
        IpFieldTransformator ipFieldTransformator = new IpFieldTransformator(TypeValidation.FORMAT_IP);
        RawDataGen rd = RawDataGen.builder().messageSend("gni").project("project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        ObjectNode jsonValue = JSONUtils.getInstance().parseObj(value);

        ipFieldTransformator.apply(null,
                ParameterTransformation.builder()
                        .keyField("messageSend")
                        .build(),
                jsonValue, value);
        assertThat(jsonValue.path("messageSend_ip").asText()).isEqualTo("gni");
        assertThat(jsonValue.path("messageSend").asText()).isEqualTo("");
    }

    @Test
    public void should_Process_Ko() throws Exception {
        IpFieldTransformator ipFieldTransformator = new IpFieldTransformator(TypeValidation.FORMAT_IP);
        RawDataGen rd = RawDataGen.builder().messageSend("gni").project("project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        ObjectNode jsonValue = JSONUtils.getInstance().parseObj(value);

        ipFieldTransformator.apply(null,
                ParameterTransformation.builder()
                        .keyField("messageSend2")
                        .build(),
                jsonValue, value);
        assertThat(jsonValue.path("messageSend2_gp").asText()).isEqualTo("");
    }

}
