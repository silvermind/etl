package io.adopteunops.etl.rules.functions.numbers;

import io.adopteunops.etl.rules.functions.OneArgFilterFunction;
import org.apache.commons.lang.StringUtils;

public class IsNumberFunction extends OneArgFilterFunction<Number> {
    @Override
    public Boolean evaluateOneArg(Number arg) {
        return StringUtils.isNumeric(arg.toString());
    }
}
