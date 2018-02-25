package io.adopteunops.etl.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.adopteunops.etl.RawDataGen;
import io.adopteunops.etl.domain.FormatDateValue;
import io.adopteunops.etl.domain.ParameterTransformation;
import io.adopteunops.etl.domain.TypeValidation;
import io.adopteunops.etl.service.transform.FormatDateTransformator;
import io.adopteunops.etl.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class FormatDateTest {

    @Test
    public void should_Process_Ok() throws Exception {
        FormatDateTransformator formatDateTransformator = new FormatDateTransformator(TypeValidation.FORMAT_DATE);
        RawDataGen rd = RawDataGen.builder().messageSend("2018-01-15").project("project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        ObjectNode jsonValue = JSONUtils.getInstance().parseObj(value);

        formatDateTransformator.apply(null,
                ParameterTransformation.builder()
                        .formatDateValue(FormatDateValue.builder()
                                .keyField("messageSend")
                                .srcFormat("yyyy-MM-dd")
                                .targetFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                                .build())
                        .build(),
                jsonValue, value);
        //FIXME: TIMEZONE ISSUE
        assertThat(jsonValue.path("messageSend").asText()).startsWith("2018-01-15T00:00:00.000");
    }


    @Test
    public void should_Process_Ko() throws Exception {
        FormatDateTransformator formatDateTransformator = new FormatDateTransformator(TypeValidation.FORMAT_DATE);
        RawDataGen rd = RawDataGen.builder().messageSend("2018-01-15").project("project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        ObjectNode jsonValue = JSONUtils.getInstance().parseObj(value);

        formatDateTransformator.apply(null,
                ParameterTransformation.builder()
                        .formatDateValue(FormatDateValue.builder()
                                .keyField("toto")
                                .srcFormat("yyyy-MM-dd")
                                .targetFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                                .build())
                        .build(),
                jsonValue, value);
        assertThat(jsonValue.path("toto").asText()).isEqualTo("");
    }
}
