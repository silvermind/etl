package io.adopteunops.etl.rules.codegeneration.filters;

import io.adopteunops.etl.rules.RuleFilterLexer;
import io.adopteunops.etl.rules.RuleFilterParser;
import io.adopteunops.etl.rules.codegeneration.SyntaxErrorListener;
import io.adopteunops.etl.rules.codegeneration.domain.RuleCode;
import io.adopteunops.etl.rules.codegeneration.exceptions.TemplatingException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.adopteunops.etl.rules.codegeneration.RuleToJava.nullSafePredicate;

@Component
public class RuleFilterToJava {

    public RuleCode convert(String name, String dsl) {
        checkNotNull(name);
        checkNotNull(dsl);
        RuleFilterVisitorImpl ruleFilterVisitor = new RuleFilterVisitorImpl();
        ruleFilterVisitor.visit(parser(dsl).parse());
        try {
            return templating(name, dsl, ruleFilterVisitor);
        } catch (Exception e) {
            throw new TemplatingException(e);
        }
    }


    private RuleCode templating(String name, String dsl, RuleFilterVisitorImpl ruleFilterVisitor) {
        String ruleClassName = StringUtils.replace(name, "\"", "") + "Filter";
        String packageName = "io.adopteunops.etl.rules.generated";
        String javaCode = "package " + packageName + ";\n" +
                "\n" +
                "import java.util.concurrent.*;\n" +
                "import static java.util.concurrent.TimeUnit.*;\n" +
                "\n" +
                "import static io.adopteunops.etl.rules.UtilsValidator.*;\n" +
                "import javax.annotation.Generated;\n" +
                "import com.fasterxml.jackson.databind.JsonNode;\n" +
                "import io.adopteunops.etl.rules.filters.GenericFilter;\n" +
                "\n" +
                "/*\n" +
                dsl + "\n" +
                "*/\n" +
                "@Generated(\"etlFilter\")\n" +
                "public class " + ruleClassName + " extends GenericFilter {\n" +
                "    @Override\n" +
                "    protected boolean doFilter(JsonNode jsonValue) {\n" +
                "        return " + nullSafePredicate(ruleFilterVisitor.getFilter()) + ";\n" +
                "    }\n" +
                "}";

        return new RuleCode(ruleClassName, dsl, packageName + "." + ruleClassName, javaCode);
    }

    public static RuleFilterParser parser(String dsl) {
        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener(dsl);

        RuleFilterLexer lexer = new RuleFilterLexer(new ANTLRInputStream(dsl));
        lexer.removeErrorListeners();
        lexer.addErrorListener(syntaxErrorListener);

        RuleFilterParser parser = new RuleFilterParser(new CommonTokenStream(lexer));
        parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
        parser.removeErrorListeners();
        parser.addErrorListener(syntaxErrorListener);

        return parser;
    }

}
