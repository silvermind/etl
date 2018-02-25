package io.adopteunops.etl.service.transform;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.adopteunops.etl.domain.ParameterTransformation;
import io.adopteunops.etl.domain.TypeValidation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteFieldTransformator extends TransformatorProcess {

    public DeleteFieldTransformator(TypeValidation type) {
        super(type);
    }

    public void apply(String idProcess, ParameterTransformation parameterTransformation, ObjectNode jsonValue, String value) {
        jsonValue.remove(parameterTransformation.getKeyField());
    }
}