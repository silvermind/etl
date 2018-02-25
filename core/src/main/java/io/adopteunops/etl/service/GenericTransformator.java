package io.adopteunops.etl.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.adopteunops.etl.domain.ProcessConsumer;
import io.adopteunops.etl.domain.ProcessTransformation;
import io.adopteunops.etl.domain.TypeValidation;
import io.adopteunops.etl.service.transform.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GenericTransformator {

    private List<TransformatorProcess> listTransformator = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExternalHTTPService externalHTTPService;

    public GenericTransformator(ExternalHTTPService externalHTTPService) {
        this.externalHTTPService = externalHTTPService;
    }

    @PostConstruct
    public void init() {
        listTransformator.add(new AddFieldTransformator(TypeValidation.ADD_FIELD));
        listTransformator.add(new BooleanTransformator(TypeValidation.FORMAT_BOOLEAN));
        listTransformator.add(new DeleteFieldTransformator(TypeValidation.DELETE_FIELD));
        listTransformator.add(new DoubleFieldTransformator(TypeValidation.FORMAT_DOUBLE));
        listTransformator.add(new FormatDateTransformator(TypeValidation.FORMAT_DATE));
        listTransformator.add(new GeoPointTransformator(TypeValidation.FORMAT_GEOPOINT));
        listTransformator.add(new IpFieldTransformator(TypeValidation.FORMAT_IP));
        listTransformator.add(new LongFieldTransformator(TypeValidation.FORMAT_LONG));
        listTransformator.add(new RenameFieldTransformator(TypeValidation.RENAME_FIELD));
        listTransformator.add(new LookupListTransformator(TypeValidation.LOOKUP_LIST));
        listTransformator.add(new LookupHTTPServiceTransformator(TypeValidation.LOOKUP_EXTERNAL, externalHTTPService));
    }

    public JsonNode createJsonObject(String value) {
        try {
            return objectMapper.readTree(value);
        } catch (IOException e) {
            return null;
        }
    }

    public String apply(String value, ProcessConsumer processConsumer) {
        ObjectNode jsonValue = (ObjectNode) createJsonObject(value);
        if (jsonValue != null && processConsumer.getProcessTransformation() != null && !processConsumer.getProcessTransformation().isEmpty()) {
            for (ProcessTransformation pt : processConsumer.getProcessTransformation()) {
                listTransformator.stream()
                        .filter(e -> e.type(pt.getTypeTransformation()))
                        .forEach(e -> e.apply(processConsumer.getIdProcess(), pt.getParameterTransformation(), jsonValue, value));
            }
            return jsonValue.toString();
        } else {
            return value;
        }
    }

}
