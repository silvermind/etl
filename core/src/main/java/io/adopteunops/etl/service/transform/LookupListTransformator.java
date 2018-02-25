package io.adopteunops.etl.service.transform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.adopteunops.etl.domain.ParameterTransformation;
import io.adopteunops.etl.domain.TypeValidation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.Map;

@Slf4j
public class LookupListTransformator extends TransformatorProcess {

    public LookupListTransformator(TypeValidation type) {
        super(type);
    }

    public void apply(String idProcess, ParameterTransformation parameterTransformation, ObjectNode jsonValue, String value) {
        String key = parameterTransformation.getKeyField();
        if (StringUtils.isNotBlank(key)) {
            if (jsonValue.has(key)) {
                String oldValue = jsonValue.path(key).asText();
                parameterTransformation.getMapLookup().entrySet().stream()
                        .filter(entry -> entry.getKey().equals(oldValue))
                        .forEach(entry -> jsonValue.put(key, entry.getValue()));
            }
        } else {
            //All Keys
            parameterTransformation.getMapLookup().entrySet().stream()
                    .forEach(entry -> applyMap(jsonValue, entry.getKey(), entry.getValue()));
        }
    }

    private void applyMap(ObjectNode jsonValue, String oldValue, String newValue) {
        for (Iterator<Map.Entry<String, JsonNode>> it = jsonValue.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            if (entry.getValue() != null && entry.getValue().asText().equals(oldValue)) {
                //update
                jsonValue.put(entry.getKey(), newValue);
            }
        }
    }
}