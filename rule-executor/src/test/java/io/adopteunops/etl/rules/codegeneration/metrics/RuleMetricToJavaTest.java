package io.adopteunops.etl.rules.codegeneration.metrics;

import io.adopteunops.etl.rules.codegeneration.CodeGenerationUtils;
import io.adopteunops.etl.rules.codegeneration.SyntaxErrorListener;
import io.adopteunops.etl.rules.codegeneration.domain.RuleCode;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleMetricToJavaTest {

    @Test
    public void min() {
        RuleMetricToJava ruleToJava = new RuleMetricToJava();
        String dsl = "SELECT MIN(duration) FROM mytopic WINDOW TUMBLING(5 MINUTES) TO KAFKA targettopic";
        RuleCode rule = ruleToJava.convert("MyMinRule", dsl);
        assertThat(rule)
                .isEqualTo(new RuleCode("MyMinRule",
                        dsl,
                        "io.adopteunops.etl.metrics.generated.MyMinRule",
                        "package io.adopteunops.etl.metrics.generated;\n" +
                                "\n" +
                                "import com.fasterxml.jackson.databind.JsonNode;\n" +
                                "import io.adopteunops.etl.rules.metrics.GenericMetricProcessor;\n" +
                                "import io.adopteunops.etl.rules.metrics.udaf.AggregateFunction;\n" +
                                "import io.adopteunops.etl.rules.metrics.domain.Keys;\n" +
                                "import io.adopteunops.etl.rules.metrics.domain.MetricResult;\n" +
                                "import static java.util.concurrent.TimeUnit.*;\n" +
                                "\n" +
                                "import javax.annotation.Generated;\n" +
                                "import static io.adopteunops.etl.rules.UtilsValidator.*;\n" +
                                "import static io.adopteunops.etl.domain.RetentionLevel.*;\n" +
                                "\n" +
                                "import org.apache.kafka.streams.kstream.KStream;\n" +
                                "import org.apache.kafka.streams.kstream.KGroupedStream;\n" +
                                "import org.apache.kafka.streams.kstream.KTable;\n" +
                                "import org.apache.kafka.streams.kstream.Windowed;\n" +
                                "\n" +
                                "/*\n" +
                                dsl + "\n" +
                                "*/\n" +
                                "@Generated(\"etlMetric\")\n" +
                                "public class MyMinRule extends GenericMetricProcessor {\n" +
                                "    public MyMinRule() {\n" +
                                "        super(\"MyMinRule\", \"" + StringEscapeUtils.escapeJava(dsl) + "\", \"mytopic\");\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected AggregateFunction aggInitializer() {\n" +
                                "        return aggFunction(\"MIN\");\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected KTable<Windowed<Keys>, Double> aggregate(KGroupedStream<Keys, Double> kGroupedStream) {\n" +
                                "        return aggregateTumblingWindow(kGroupedStream,5,MINUTES);\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected Double mapValues(JsonNode value) {\n" +
                                "        return value.path(\"duration\").asDouble();\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    public void routeResult(KStream<Keys, MetricResult> result) {\n" +
                                "        toKafkaTopic(result,\"targettopic\");\n" +
                                "    }\n" +
                                "}"));
        rule.compile();
    }


    @Test
    public void filterWithFilter() {
        RuleMetricToJava ruleToJava = new RuleMetricToJava();
        String dsl = "SELECT MIN(duration) FROM mytopic WINDOW TUMBLING(5 MINUTES) WHERE type = \"something\" TO KAFKA targettopic";
        RuleCode rule = ruleToJava.convert("MyMinRule", dsl);
        assertThat(rule)
                .isEqualTo(new RuleCode("MyMinRule",
                        dsl,
                        "io.adopteunops.etl.metrics.generated.MyMinRule",
                        "package io.adopteunops.etl.metrics.generated;\n" +
                                "\n" +
                                "import com.fasterxml.jackson.databind.JsonNode;\n" +
                                "import io.adopteunops.etl.rules.metrics.GenericMetricProcessor;\n" +
                                "import io.adopteunops.etl.rules.metrics.udaf.AggregateFunction;\n" +
                                "import io.adopteunops.etl.rules.metrics.domain.Keys;\n" +
                                "import io.adopteunops.etl.rules.metrics.domain.MetricResult;\n" +
                                "import static java.util.concurrent.TimeUnit.*;\n" +
                                "\n" +
                                "import javax.annotation.Generated;\n" +
                                "import static io.adopteunops.etl.rules.UtilsValidator.*;\n" +
                                "import static io.adopteunops.etl.domain.RetentionLevel.*;\n" +
                                "\n" +
                                "import org.apache.kafka.streams.kstream.KStream;\n" +
                                "import org.apache.kafka.streams.kstream.KGroupedStream;\n" +
                                "import org.apache.kafka.streams.kstream.KTable;\n" +
                                "import org.apache.kafka.streams.kstream.Windowed;\n" +
                                "\n" +
                                "/*\n" +
                                dsl + "\n" +
                                "*/\n" +
                                "@Generated(\"etlMetric\")\n" +
                                "public class MyMinRule extends GenericMetricProcessor {\n" +
                                "    public MyMinRule() {\n" +
                                "        super(\"MyMinRule\", \"" + StringEscapeUtils.escapeJava(dsl) + "\", \"mytopic\");\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected AggregateFunction aggInitializer() {\n" +
                                "        return aggFunction(\"MIN\");\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected KTable<Windowed<Keys>, Double> aggregate(KGroupedStream<Keys, Double> kGroupedStream) {\n" +
                                "        return aggregateTumblingWindow(kGroupedStream,5,MINUTES);\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected Double mapValues(JsonNode value) {\n" +
                                "        return value.path(\"duration\").asDouble();\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected boolean filter(String key, JsonNode jsonValue) {\n" +
                                "        return isEqualTo(get(jsonValue,\"type\"),\"something\");\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    public void routeResult(KStream<Keys, MetricResult> result) {\n" +
                                "        toKafkaTopic(result,\"targettopic\");\n" +
                                "    }\n" +
                                "}"));
        rule.compile();
    }


    @Test
    public void groupBy() {
        RuleMetricToJava ruleToJava = new RuleMetricToJava();
        String dsl = "SELECT MIN(duration) FROM mytopic WINDOW TUMBLING(5 MINUTES) GROUP BY type TO KAFKA targettopic";
        RuleCode rule = ruleToJava.convert("MyMinRule", dsl);
        assertThat(rule)
                .isEqualTo(new RuleCode("MyMinRule",
                        dsl,
                        "io.adopteunops.etl.metrics.generated.MyMinRule",
                        "package io.adopteunops.etl.metrics.generated;\n" +
                                "\n" +
                                "import com.fasterxml.jackson.databind.JsonNode;\n" +
                                "import io.adopteunops.etl.rules.metrics.GenericMetricProcessor;\n" +
                                "import io.adopteunops.etl.rules.metrics.udaf.AggregateFunction;\n" +
                                "import io.adopteunops.etl.rules.metrics.domain.Keys;\n" +
                                "import io.adopteunops.etl.rules.metrics.domain.MetricResult;\n" +
                                "import static java.util.concurrent.TimeUnit.*;\n" +
                                "\n" +
                                "import javax.annotation.Generated;\n" +
                                "import static io.adopteunops.etl.rules.UtilsValidator.*;\n" +
                                "import static io.adopteunops.etl.domain.RetentionLevel.*;\n" +
                                "\n" +
                                "import org.apache.kafka.streams.kstream.KStream;\n" +
                                "import org.apache.kafka.streams.kstream.KGroupedStream;\n" +
                                "import org.apache.kafka.streams.kstream.KTable;\n" +
                                "import org.apache.kafka.streams.kstream.Windowed;\n" +
                                "\n" +
                                "/*\n" +
                                dsl + "\n" +
                                "*/\n" +
                                "@Generated(\"etlMetric\")\n" +
                                "public class MyMinRule extends GenericMetricProcessor {\n" +
                                "    public MyMinRule() {\n" +
                                "        super(\"MyMinRule\", \"" + StringEscapeUtils.escapeJava(dsl) + "\", \"mytopic\");\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected AggregateFunction aggInitializer() {\n" +
                                "        return aggFunction(\"MIN\");\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected KTable<Windowed<Keys>, Double> aggregate(KGroupedStream<Keys, Double> kGroupedStream) {\n" +
                                "        return aggregateTumblingWindow(kGroupedStream,5,MINUTES);\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected Double mapValues(JsonNode value) {\n" +
                                "        return value.path(\"duration\").asDouble();\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected Keys selectKey(String key, JsonNode value) {\n" +
                                "        Keys keys = super.selectKey(key,value);\n" +
                                "        keys.addKey(\"type\", value.get(\"type\").asText());\n" +
                                "        return keys;\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    public void routeResult(KStream<Keys, MetricResult> result) {\n" +
                                "        toKafkaTopic(result,\"targettopic\");\n" +
                                "    }\n" +
                                "}"));
        rule.compile();
    }

    @Test
    public void having() {
        RuleMetricToJava ruleToJava = new RuleMetricToJava();
        String dsl = "SELECT MIN(duration) FROM mytopic WINDOW TUMBLING(5 MINUTES) HAVING result > 10 TO KAFKA targettopic";
        RuleCode rule = ruleToJava.convert("MyMinRule", dsl);
        assertThat(rule)
                .isEqualTo(new RuleCode("MyMinRule",
                        dsl,
                        "io.adopteunops.etl.metrics.generated.MyMinRule",
                        "package io.adopteunops.etl.metrics.generated;\n" +
                                "\n" +
                                "import com.fasterxml.jackson.databind.JsonNode;\n" +
                                "import io.adopteunops.etl.rules.metrics.GenericMetricProcessor;\n" +
                                "import io.adopteunops.etl.rules.metrics.udaf.AggregateFunction;\n" +
                                "import io.adopteunops.etl.rules.metrics.domain.Keys;\n" +
                                "import io.adopteunops.etl.rules.metrics.domain.MetricResult;\n" +
                                "import static java.util.concurrent.TimeUnit.*;\n" +
                                "\n" +
                                "import javax.annotation.Generated;\n" +
                                "import static io.adopteunops.etl.rules.UtilsValidator.*;\n" +
                                "import static io.adopteunops.etl.domain.RetentionLevel.*;\n" +
                                "\n" +
                                "import org.apache.kafka.streams.kstream.KStream;\n" +
                                "import org.apache.kafka.streams.kstream.KGroupedStream;\n" +
                                "import org.apache.kafka.streams.kstream.KTable;\n" +
                                "import org.apache.kafka.streams.kstream.Windowed;\n" +
                                "\n" +
                                "/*\n" +
                                dsl + "\n" +
                                "*/\n" +
                                "@Generated(\"etlMetric\")\n" +
                                "public class MyMinRule extends GenericMetricProcessor {\n" +
                                "    public MyMinRule() {\n" +
                                "        super(\"MyMinRule\", \"" + StringEscapeUtils.escapeJava(dsl) + "\", \"mytopic\");\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected AggregateFunction aggInitializer() {\n" +
                                "        return aggFunction(\"MIN\");\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected KTable<Windowed<Keys>, Double> aggregate(KGroupedStream<Keys, Double> kGroupedStream) {\n" +
                                "        return aggregateTumblingWindow(kGroupedStream,5,MINUTES);\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected Double mapValues(JsonNode value) {\n" +
                                "        return value.path(\"duration\").asDouble();\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    protected boolean having(Windowed<Keys> keys, Double result) {\n" +
                                "        return result > 10;\n" +
                                "    }\n" +
                                "    \n" +
                                "    @Override\n" +
                                "    public void routeResult(KStream<Keys, MetricResult> result) {\n" +
                                "        toKafkaTopic(result,\"targettopic\");\n" +
                                "    }\n" +
                                "}"));
        rule.compile();
    }

    @Test(expected = SyntaxErrorListener.SyntaxException.class)
    public void wrongSyntax() {
        RuleMetricToJava ruleToJava = new RuleMetricToJava();
        String dsl = "SELECT MIN(duration) GROUP BY type TO targettopic FROM mytopic ";
        ruleToJava.convert("MyMinRule", dsl);
    }

    @Test
    @Ignore
    public void generateCode() {
        RuleMetricToJava ruleToJava = new RuleMetricToJava();
        String dsl = "SELECT MIN(duration) FROM mytopic WINDOW TUMBLING(5 MINUTES) GROUP BY type TO ELASTICSEARCH week";
        RuleCode myMetricRule = ruleToJava.convert("MyMetricRule", dsl);
        File home = new File("target/generated-test-sources");
        CodeGenerationUtils.write(myMetricRule, home);
    }
}