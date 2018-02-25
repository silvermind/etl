package io.adopteunops.etl.rules.functions.strings;

import io.adopteunops.etl.rules.functions.VarArgFilterFunction;

import java.util.List;

public class ContainsFunction extends VarArgFilterFunction<String> {
    @Override
    public Boolean evaluateVarArgs(String fieldValue, List<String> values) {
        if (fieldValue == null) {
            return false;
        }
        for (String value : values) {
            if (fieldValue.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
