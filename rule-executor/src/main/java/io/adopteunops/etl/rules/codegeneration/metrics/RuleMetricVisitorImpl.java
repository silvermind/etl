package io.adopteunops.etl.rules.codegeneration.metrics;

import io.adopteunops.etl.rules.RuleMetricBaseVisitor;
import io.adopteunops.etl.rules.codegeneration.exceptions.RuleVisitorException;
import lombok.Getter;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

import static io.adopteunops.etl.rules.RuleMetricParser.*;
import static io.adopteunops.etl.rules.codegeneration.RuleToJava.*;

@Getter
public class RuleMetricVisitorImpl extends RuleMetricBaseVisitor<String> {

    private String from;
    private String window;
    private String where;
    private String groupBy;
    private String having;
    private String destination;
    private String aggFunction;
    private String aggFunctionField;

    @Override
    public String visitParse(ParseContext ctx) {
        try {
            visit(ctx.select_clause());
            from = visitFrom(ctx.from());
            window = visitWindow(ctx.window());
            if (ctx.where() != null) {
                where = visit(ctx.where());
            }
            if (ctx.group_by() != null) {
                groupBy = visit(ctx.group_by());
            }
            if (ctx.having() != null) {
                having = visitHaving(ctx.having());
            }
            destination = visitDestination(ctx.destination());
            return "";
        } catch (Exception e) {
            throw new RuleVisitorException(e);
        }
    }

    @Override
    public String visitToSystemOutExpression(ToSystemOutExpressionContext ctx) {
        return "toSystemOut(result)";
    }

    @Override
    public String visitToKafkaTopic(ToKafkaTopicContext ctx) {
        return "toKafkaTopic(result,\"" + visit(ctx.target()) + "\")";
    }

    @Override
    public String visitToElasticsearch(ToElasticsearchContext ctx) {
        return "toElasticsearch(result," + visit(ctx.target()) + ")";
    }

    @Override
    public String visitTumblingWindowExpression(TumblingWindowExpressionContext ctx) {
        return "aggregateTumblingWindow(kGroupedStream," +
                visit(ctx.INT()) +
                "," +
                visit(ctx.timeunit()) + ")";
    }

    @Override
    public String visitHoppingWindowExpression(HoppingWindowExpressionContext ctx) {
        return "aggregateHoppingWindow(kGroupedStream," +
                visit(ctx.INT(0)) +
                "," + visit(ctx.timeunit(0)) + "," +
                visit(ctx.INT(1)) +
                "," + visit(ctx.timeunit(1)) + ")";
    }

    @Override
    public String visitSessionWindowExpression(SessionWindowExpressionContext ctx) {
        return "aggregateSessionWindow(kGroupedStream," +
                visit(ctx.INT()) +
                "," +
                visit(ctx.timeunit()) + ")";
    }

    @Override
    public String visitHaving(HavingContext ctx) {
        return visit(ctx.RESULT()) + " " + visit(ctx.COMPARISON_OPERATION()) + " " + visit(ctx.INT());
    }

    @Override
    public String visitAggfunction(AggfunctionContext ctx) {
        aggFunction = visit(ctx.function_name());
        aggFunctionField = visit(ctx.target());
        return aggFunction;
    }

    @Override
    public String visitTimeunit(TimeunitContext ctx) {
        switch (ctx.getText()) {
            case "SECONDS":
            case "S":
            case "s":
                return "SECONDS";
            case "MINUTES":
            case "M":
            case "m":
                return "MINUTES";
            case "HOURS":
            case "H":
            case "h":
                return "HOURS";
            case "DAYS":
            case "D":
            case "d":
                return "DAYS";
            default:
                throw new RuntimeException(ctx.getText() + " is not a timeunit");
        }
    }

    @Override
    public String visitTerminal(TerminalNode node) {
        return node.getText();
    }

    @Override
    public String visitFloatAtom(FloatAtomContext ctx) {
        return ctx.getText() + "f";
    }

    @Override
    public String visitFieldvalue(FieldvalueContext ctx) {
        return "get(jsonValue,\"" + ctx.getText() + "\")";
    }

    protected String text(RuleNode node) {
        return node == null ? "" : node.getText();
    }

    @Override
    public String visitSubExpr(SubExprContext ctx) {
        return "(" + visit(ctx.expr()) + ")";
    }

    @Override
    public String visitExponentExpr(ExponentExprContext ctx) {
        return exp(visit(ctx.expr().get(0)), visit(ctx.expr().get(1)));
    }

    @Override
    public String visitHighPriorityOperationExpr(HighPriorityOperationExprContext ctx) {
        String operation = ctx.HIGH_PRIORITY_OPERATION().getText();
        String expr1 = visit(ctx.expr(0));
        String expr2 = visit(ctx.expr(1));
        return highPriorityOperation(operation, expr1, expr2);
    }

    @Override
    public String visitLowPriorityOperationExpr(LowPriorityOperationExprContext ctx) {
        String operation = ctx.LOW_PRIORITY_OPERATION().getText();
        String expr1 = visit(ctx.expr(0));
        String expr2 = visit(ctx.expr(1));
        return lowPriorityOperation(operation, expr1, expr2);
    }

    @Override
    public String visitComparisonExpr(ComparisonExprContext ctx) {
        return comparisonMethod(ctx.COMPARISON_OPERATION().getText(), visit(ctx.expr(0)), visit(ctx.expr(1)));
    }

    @Override
    public String visitTimeCondition(TimeConditionContext ctx) {
        return timeComparisonMethod(ctx.COMPARISON_OPERATION().getText(), visit(ctx.fieldvalue()), visit(ctx.INT()), visit(ctx.timeunit()));
    }

    @Override
    public String visitAndCondition(AndConditionContext ctx) {
        return and(visit(ctx.expr(0)), visit(ctx.expr(1)));
    }

    @Override
    public String visitOrCondition(OrConditionContext ctx) {
        return or(visit(ctx.expr(0)), visit(ctx.expr(1)));
    }

    @Override
    public String visitNotCondition(NotConditionContext ctx) {
        return not(visit(ctx.expr()));
    }

    @Override
    public String visitIfCondition(IfConditionContext ctx) {
        return ifCondition(visit(ctx.expr(0)), visit(ctx.expr(1)), visit(ctx.expr(2)));
    }

    @Override
    public String visitOneArgCondition(OneArgConditionContext ctx) {
        String functionName = visit(ctx.functionname());
        return oneArgCondition(functionName, visit(ctx.fieldvalue()));
    }

    @Override
    public String visitVarArgCondition(VarArgConditionContext ctx) {
        String functionName = visit(ctx.functionname());
        String notOperation = ctx.NOT_OPERATION() != null ? "!" : "";
        String fieldValue = visit(ctx.fieldvalue());
        String args = visit(ctx.expr(), ",", "", "");
        return notOperation + varArgCondition(functionName, fieldValue, args);
    }


    protected String visit(List<ExprContext> exprs, String visitSeparators, String appendToVisitResultBegin, String appendToVisitResultEnd) {
        String args = "";
        for (ExprContext expr : exprs) {
            if (!args.isEmpty()) {
                args += visitSeparators;
            }
            args += appendToVisitResultBegin + visit(expr) + appendToVisitResultEnd;
        }
        return args;
    }
}
