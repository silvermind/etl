package io.adopteunops.etl.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.adopteunops.etl.RawDataGen;
import io.adopteunops.etl.domain.ParameterTransformation;
import io.adopteunops.etl.domain.TypeValidation;
import io.adopteunops.etl.domain.WorkerHTTPService;
import io.adopteunops.etl.service.ExternalHTTPService;
import io.adopteunops.etl.service.transform.LookupHTTPServiceTransformator;
import io.adopteunops.etl.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class LookupHTTPServiceTransformatorTest {


    @Test
    public void should_Process_Limit_Ok() throws Exception {
        ExternalHTTPService service = new ExternalHTTPService();
        HashMap<String, String> mapTest = new HashMap<>();
        mapTest.put("gni", "test of gni");
        mapTest.put("messageSend", "new value of message");
        mapTest.put("gna", "test of gna");
        service.getMapExternalService().put("10", WorkerHTTPService.builder().mapResult(mapTest).build());
        LookupHTTPServiceTransformator lookupHTTPServiceTransformator = new LookupHTTPServiceTransformator(TypeValidation.LOOKUP_EXTERNAL, service);
        RawDataGen rd = RawDataGen.builder().messageSend("gni").project("value of project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        ObjectNode jsonValue = JSONUtils.getInstance().parseObj(value);

        lookupHTTPServiceTransformator.apply("10",
                ParameterTransformation.builder()
                        .keyField("messageSend")
                        .build(),
                jsonValue, value);
        assertThat(jsonValue.get("messageSend").asText()).isEqualTo("test of gni");
        assertThat(jsonValue.get("project").asText()).isEqualTo("value of project");
        assertThat(jsonValue.get("type").asText()).isEqualTo("type");
    }

    @Test
    public void should_Process_Limit_Ko() throws Exception {
        ExternalHTTPService service = new ExternalHTTPService();
        HashMap<String, String> mapTest = new HashMap<>();
        mapTest.put("gni", "test of gni");
        mapTest.put("messageSend", "new value of message");
        mapTest.put("gna", "test of gna");
        service.getMapExternalService().put("10", WorkerHTTPService.builder().mapResult(mapTest).build());
        LookupHTTPServiceTransformator lookupHTTPServiceTransformator = new LookupHTTPServiceTransformator(TypeValidation.LOOKUP_EXTERNAL, service);
        RawDataGen rd = RawDataGen.builder().messageSend("gni").project("value of project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        ObjectNode jsonValue = JSONUtils.getInstance().parseObj(value);

        lookupHTTPServiceTransformator.apply("10",
                ParameterTransformation.builder()
                        .keyField("gni")
                        .build(),
                jsonValue, value);
        assertThat(jsonValue.get("messageSend").asText()).isEqualTo("gni");
        assertThat(jsonValue.get("project").asText()).isEqualTo("value of project");
        assertThat(jsonValue.get("type").asText()).isEqualTo("type");
    }

    @Test
    public void should_Process_Ok() throws Exception {
        ExternalHTTPService service = new ExternalHTTPService();
        HashMap<String, String> mapTest = new HashMap<>();
        mapTest.put("gni", "test of gni");
        mapTest.put("messageSend", "new value of message");
        mapTest.put("type", "test of type");
        service.getMapExternalService().put("10", WorkerHTTPService.builder().mapResult(mapTest).build());
        LookupHTTPServiceTransformator lookupHTTPServiceTransformator = new LookupHTTPServiceTransformator(TypeValidation.LOOKUP_EXTERNAL, service);
        RawDataGen rd = RawDataGen.builder().messageSend("gni").project("value of project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        ObjectNode jsonValue = JSONUtils.getInstance().parseObj(value);

        lookupHTTPServiceTransformator.apply("10",
                ParameterTransformation.builder()
                        .build(),
                jsonValue, value);
        assertThat(jsonValue.get("messageSend").asText()).isEqualTo("test of gni");
        assertThat(jsonValue.get("project").asText()).isEqualTo("value of project");
        assertThat(jsonValue.get("type").asText()).isEqualTo("test of type");
    }

    @Test
    public void should_Process_Ko() throws Exception {
        ExternalHTTPService service = new ExternalHTTPService();
        HashMap<String, String> mapTest = new HashMap<>();
        mapTest.put("gnia", "test of gni");
        mapTest.put("messageSend", "new value of message");
        mapTest.put("typea", "test of type");
        service.getMapExternalService().put("10", WorkerHTTPService.builder().mapResult(mapTest).build());
        LookupHTTPServiceTransformator lookupHTTPServiceTransformator = new LookupHTTPServiceTransformator(TypeValidation.LOOKUP_EXTERNAL, service);
        RawDataGen rd = RawDataGen.builder().messageSend("gni").project("value of project").type("type").build();
        ObjectMapper obj = new ObjectMapper();
        String value = obj.writeValueAsString(rd);
        ObjectNode jsonValue = JSONUtils.getInstance().parseObj(value);

        lookupHTTPServiceTransformator.apply("10",
                ParameterTransformation.builder()
                        .build(),
                jsonValue, value);
        assertThat(jsonValue.get("messageSend").asText()).isEqualTo("gni");
        assertThat(jsonValue.get("project").asText()).isEqualTo("value of project");
        assertThat(jsonValue.get("type").asText()).isEqualTo("type");
    }


}
