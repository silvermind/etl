package io.adopteunops.etl.rules.codegeneration;

import com.fasterxml.jackson.databind.JsonNode;
import io.adopteunops.etl.rules.codegeneration.filters.RuleFilterToJava;
import io.adopteunops.etl.rules.filters.GenericFilter;
import io.adopteunops.etl.rules.filters.RuleFilterExecutor;
import org.junit.Test;

import static io.adopteunops.etl.rules.JSONUtils.createJsonNode;
import static org.assertj.core.api.Assertions.assertThat;


public class RuleExecutorTest {

    private RuleFilterExecutor ruleExecutor = new RuleFilterExecutor(new RuleFilterToJava());

    @Test
    public void shouldCompile() {
        GenericFilter myCompileFilter = ruleExecutor.instanciate("myCompileFilter", "key1 >= 3");
        String test = "{\"key1\": 100}";
        JsonNode jsonObject = createJsonNode(test);
        assertThat(myCompileFilter.filter(jsonObject)).isTrue();
    }

}