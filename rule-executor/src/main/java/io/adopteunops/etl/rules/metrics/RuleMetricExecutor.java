package io.adopteunops.etl.rules.metrics;


import io.adopteunops.etl.rules.codegeneration.domain.RuleCode;
import io.adopteunops.etl.rules.codegeneration.metrics.RuleMetricToJava;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class RuleMetricExecutor {

    private final RuleMetricToJava ruleMetricToJava;

    public GenericMetricProcessor instanciate(String name, String dsl) {
        return instanciate(ruleMetricToJava.convert(name, dsl));
    }

    private GenericMetricProcessor instanciate(RuleCode ruleCode) {
        try {
            return instanciate(ruleCode.compile());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private GenericMetricProcessor instanciate(Class aClass) throws Exception {
        return (GenericMetricProcessor) aClass.newInstance();
    }


}
