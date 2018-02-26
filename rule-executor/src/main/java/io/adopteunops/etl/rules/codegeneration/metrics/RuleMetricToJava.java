package io.adopteunops.etl.rules.codegeneration.metrics;

import io.adopteunops.etl.rules.RuleMetricLexer;
import io.adopteunops.etl.rules.RuleMetricParser;
import io.adopteunops.etl.rules.codegeneration.SyntaxErrorListener;
import io.adopteunops.etl.rules.codegeneration.domain.RuleCode;
import io.adopteunops.etl.rules.codegeneration.exceptions.TemplatingException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.adopteunops.etl.rules.codegeneration.RuleToJava.nullSafePredicate;

@Component
public class RuleMetricToJava {

    public RuleCode convert(String name, String dsl) {
        checkNotNull(name);
        checkNotNull(dsl);
        RuleMetricVisitorImpl ruleMetricVisitor = new RuleMetricVisitorImpl();
        ruleMetricVisitor.visit(parser(dsl).parse());
        try {
            return templating(name, dsl, ruleMetricVisitor);
        } catch (Exception e) {
            throw new TemplatingException(e);
        }
    }

    private RuleCode templating(String name, String dsl, RuleMetricVisitorImpl ruleMetricVisitor) {
        String ruleClassName = StringUtils.replace(name, "\"", "");
        String packageName = "io.adopteunops.etl.metrics.generated";
        String javaCode = "package " + packageName + ";\n" +
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
                "public class " + ruleClassName + " extends GenericMetricProcessor {\n" +
                "    public " + ruleClassName + "() {\n" +
                "        super(\"" + ruleClassName + "\", \"" + StringEscapeUtils.escapeJava(dsl) + "\", \"" + ruleMetricVisitor.getFrom() + "\");\n" +
                "    }\n" +
                "    \n" +
                "    @Override\n" +
                "    protected AggregateFunction aggInitializer() {\n" +
                "        return aggFunction(\"" + ruleMetricVisitor.getAggFunction() + "\");\n" +
                "    }\n" +
                "    \n" +
                "    @Override\n" +
                "    protected KTable<Windowed<Keys>, Double> aggregate(KGroupedStream<Keys, Double> kGroupedStream) {\n" +
                "        return " + ruleMetricVisitor.getWindow() + ";\n" +
                "    }\n" +
                "    \n" +
                "    @Override\n" +
                "    protected Double mapValues(JsonNode value) {\n" +
                "        return value.path(\"" + ruleMetricVisitor.getAggFunctionField() + "\").asDouble();\n" +
                "    }\n";
        if (StringUtils.isNotBlank(ruleMetricVisitor.getWhere())) {
            javaCode += "    \n" +
                    "    @Override\n" +
                    "    protected boolean filter(String key, JsonNode jsonValue) {\n" +
                    "        return " + nullSafePredicate(ruleMetricVisitor.getWhere()) + ";\n" +
                    "    }\n";
        }

        if (StringUtils.isNotBlank(ruleMetricVisitor.getGroupBy())) {
            javaCode += "    \n" +
                    "    @Override\n" +
                    "    protected Keys selectKey(String key, JsonNode value) {\n" +
                    "        Keys keys = super.selectKey(key,value);\n";
            for (String groupByField : ruleMetricVisitor.getGroupBy().split(",")) {
                javaCode += "        keys.addKey(\"" + groupByField + "\", value.get(\"" + groupByField + "\").asText());\n";
            }
            javaCode += "        return keys;\n" +
                    "    }\n";
        }
        javaCode += "    \n" +
                "    @Override\n" +
                "    public void routeResult(KStream<Keys, MetricResult> result) {\n" +
                "        " + ruleMetricVisitor.getDestination() + ";\n" +
                "    }\n" +
                "}";

        return new RuleCode(ruleClassName, dsl, packageName + "." + ruleClassName, javaCode);
    }

    public static RuleMetricParser parser(String dsl) {
        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener(dsl);

        RuleMetricLexer lexer = new RuleMetricLexer(new ANTLRInputStream(dsl));
        lexer.removeErrorListeners();
        lexer.addErrorListener(syntaxErrorListener);

        RuleMetricParser parser = new RuleMetricParser(new CommonTokenStream(lexer));
        parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
        parser.removeErrorListeners();
        parser.addErrorListener(syntaxErrorListener);

        return parser;
    }

}
