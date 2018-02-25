package io.adopteunops.etl.rules.functions.strings;

import io.adopteunops.etl.rules.functions.VarArgFilterFunction;

import java.util.List;

public class RegexpFunction extends VarArgFilterFunction<String> {
    @Override
    public Boolean evaluateVarArgs(String fieldValue, List<String> regexps) {
        return regexps.stream().filter(regexp -> fieldValue.matches(regexp)).findFirst().isPresent();
    }
}
