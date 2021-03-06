package io.adopteunops.etl.service.transform;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.adopteunops.etl.domain.ParameterTransformation;
import io.adopteunops.etl.domain.TypeValidation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class IpFieldTransformator extends TransformatorProcess {

    public IpFieldTransformator(TypeValidation type) {
        super(type);
    }

    public void apply(String idProcess, ParameterTransformation parameterTransformation, ObjectNode jsonValue, String value) {
        String valueToFormat = jsonValue.path(parameterTransformation.getKeyField()).asText();
        if (StringUtils.isNotBlank(valueToFormat)) {
            jsonValue.put(parameterTransformation.getKeyField() + "_ip", valueToFormat);
            jsonValue.remove(parameterTransformation.getKeyField());
        }
    }
}
