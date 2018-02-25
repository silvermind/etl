package io.adopteunops.etl.rules.codegeneration.filters;

import io.adopteunops.etl.rules.RuleFilterBaseVisitor;
import io.adopteunops.etl.rules.codegeneration.exceptions.RuleVisitorException;
import lombok.Getter;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

import static io.adopteunops.etl.rules.RuleFilterParser.*;
import static io.adopteunops.etl.rules.codegeneration.RuleToJava.*;

@Getter
public class RuleFilterVisitorImpl extends RuleFilterBaseVisitor<String> {

    private String filter;

    @Override
    public String visitParse(ParseContext ctx) {
        try {
            filter = visit(ctx.filter());
            return "filters";
        } catch (Exception e) {
            throw new RuleVisitorException(e);
        }
    }

    @Override
    public String visitTimeunit(TimeunitContext ctx) {
        return timeunit(ctx.getText());
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
    public String visitFieldname(FieldnameContext ctx) {
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
        return timeComparisonMethod(ctx.COMPARISON_OPERATION().getText(), visit(ctx.fieldname()), visit(ctx.INT()), visit(ctx.timeunit()));
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
        return oneArgCondition(functionName, visit(ctx.fieldname()));
    }

    @Override
    public String visitVarArgCondition(VarArgConditionContext ctx) {
        String functionName = visit(ctx.functionname());
        String notOperation = ctx.NOT_OPERATION() != null ? "!" : "";

        String fieldValue = visit(ctx.fieldname());
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
