package io.adopteunops.etl.service.transform;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.adopteunops.etl.domain.ParameterTransformation;
import io.adopteunops.etl.domain.TypeValidation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class DoubleFieldTransformator extends TransformatorProcess {

    public DoubleFieldTransformator(TypeValidation type) {
        super(type);
    }

    public void apply(String idProcess, ParameterTransformation parameterTransformation, ObjectNode jsonValue, String value) {
        String valueToFormat = jsonValue.path(parameterTransformation.getKeyField()).asText();
        if (StringUtils.isNotBlank(valueToFormat)) {
            jsonValue.put(parameterTransformation.getKeyField() + "_double", Double.valueOf(valueToFormat));
            jsonValue.remove(parameterTransformation.getKeyField());
        }
    }
}
