package io.adopteunops.etl.rules.filters;


import io.adopteunops.etl.rules.codegeneration.domain.RuleCode;
import io.adopteunops.etl.rules.codegeneration.filters.RuleFilterToJava;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class RuleFilterExecutor {

    private final RuleFilterToJava ruleFilterToJava;

    public GenericFilter instanciate(String name, String dsl) {
        return instanciate(ruleFilterToJava.convert(name, dsl));
    }

    private GenericFilter instanciate(RuleCode ruleCode) {
        try {
            return instanciate(ruleCode.compile());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private GenericFilter instanciate(Class aClass) throws Exception {
        return (GenericFilter) aClass.newInstance();
    }

}
