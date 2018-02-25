package io.adopteunops.etl.rules.functions.strings;

import io.adopteunops.etl.rules.functions.OneArgFilterFunction;
import org.apache.commons.lang.StringUtils;

public class IsNotBlank extends OneArgFilterFunction<String> {
    @Override
    public Boolean evaluateOneArg(String arg) {
        return StringUtils.isNotBlank(arg);
    }
}
