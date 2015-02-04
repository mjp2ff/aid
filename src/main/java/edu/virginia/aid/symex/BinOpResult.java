package edu.virginia.aid.symex;

import org.eclipse.jdt.core.dom.InfixExpression;

import java.util.*;

public class BinOpResult implements IdentifierValue {

    private InfixExpression.Operator operator;
    private IdentifierValue operand1;
    private IdentifierValue operand2;

    private static final Map<InfixExpression.Operator, List<InfixExpression.Operator>> disjoint = new HashMap<>();
    static {
        disjoint.put(InfixExpression.Operator.GREATER, Arrays.asList(
                InfixExpression.Operator.EQUALS,
                InfixExpression.Operator.LESS,
                InfixExpression.Operator.LESS_EQUALS));
        disjoint.put(InfixExpression.Operator.GREATER_EQUALS, Arrays.asList(
                InfixExpression.Operator.LESS));
        disjoint.put(InfixExpression.Operator.EQUALS, Arrays.asList(
                InfixExpression.Operator.GREATER,
                InfixExpression.Operator.LESS,
                InfixExpression.Operator.NOT_EQUALS));
        disjoint.put(InfixExpression.Operator.NOT_EQUALS, Arrays.asList(
                InfixExpression.Operator.EQUALS));
        disjoint.put(InfixExpression.Operator.LESS_EQUALS, Arrays.asList(
                InfixExpression.Operator.GREATER));
        disjoint.put(InfixExpression.Operator.LESS, Arrays.asList(
                InfixExpression.Operator.EQUALS,
                InfixExpression.Operator.GREATER,
                InfixExpression.Operator.GREATER_EQUALS));
    }

    private static final Map<InfixExpression.Operator, InfixExpression.Operator> reverse = new HashMap<>();
    static {
        reverse.put(InfixExpression.Operator.GREATER, InfixExpression.Operator.LESS);
        reverse.put(InfixExpression.Operator.GREATER_EQUALS, InfixExpression.Operator.LESS_EQUALS);
        reverse.put(InfixExpression.Operator.EQUALS, InfixExpression.Operator.NOT_EQUALS);
        reverse.put(InfixExpression.Operator.NOT_EQUALS, InfixExpression.Operator.EQUALS);
        reverse.put(InfixExpression.Operator.LESS, InfixExpression.Operator.GREATER);
        reverse.put(InfixExpression.Operator.LESS_EQUALS, InfixExpression.Operator.GREATER_EQUALS);
    }

    public BinOpResult(InfixExpression.Operator operator, IdentifierValue operand1, IdentifierValue operand2) {
        this.operator = operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public IdentifierValue getOperand1() {
        return operand1;
    }

    public IdentifierValue getOperand2() {
        return operand2;
    }

    @Override
    public IdentifierValue negate() {
        if (operator.equals(InfixExpression.Operator.CONDITIONAL_AND)) {
            return new BinOpResult(InfixExpression.Operator.CONDITIONAL_OR, (operand1 == null ? operand1 : operand1.negate()), (operand2 == null ? operand2 : operand2.negate()));
        } else if (operator.equals(InfixExpression.Operator.CONDITIONAL_OR)) {
            return new BinOpResult(InfixExpression.Operator.CONDITIONAL_AND, (operand1 == null ? operand1 : operand1.negate()), (operand2 == null ? operand2 : operand2.negate()));
        } else if (operator.equals(InfixExpression.Operator.EQUALS)) {
            return new BinOpResult(InfixExpression.Operator.NOT_EQUALS, operand1, operand2);
        } else if (operator.equals(InfixExpression.Operator.NOT_EQUALS)) {
            return new BinOpResult(InfixExpression.Operator.EQUALS, operand1, operand2);
        } else if (operator.equals(InfixExpression.Operator.GREATER)) {
            return new BinOpResult(InfixExpression.Operator.LESS_EQUALS, operand1, operand2);
        } else if (operator.equals(InfixExpression.Operator.GREATER_EQUALS)) {
            return new BinOpResult(InfixExpression.Operator.LESS, operand1, operand2);
        } else if (operator.equals(InfixExpression.Operator.LESS_EQUALS)) {
            return new BinOpResult(InfixExpression.Operator.GREATER, operand1, operand2);
        } else if (operator.equals(InfixExpression.Operator.LESS)) {
            return new BinOpResult(InfixExpression.Operator.GREATER_EQUALS, operand1, operand2);
        } else {
            return this;
        }
    }

    @Override
    public IdentifierValue simplify() {
        return this;
    }

    @Override
    public boolean isDisjointWith(IdentifierValue iv) {
        if (iv instanceof BinOpResult && disjoint.containsKey(operator)) {
            if (disjoint.containsKey(operator) && disjoint.get(operator).contains(((BinOpResult) iv).operator)) {
                if (((BinOpResult) iv).operand1.equals(operand1) && ((BinOpResult) iv).operand2.equals(operand2)) {
                    return true;
                }
            } else if (reverse.containsKey(operator) && disjoint.get(reverse.get(operator)).contains(((BinOpResult) iv).operator)) {
                return ((BinOpResult) iv).operand2.equals(operand1) && ((BinOpResult) iv).operand1.equals(operand2);
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BinOpResult) {
            return ((BinOpResult) o).operator.equals(this.operator) &&
                    ((BinOpResult) o).operand1.equals(this.operand1) &&
                    ((BinOpResult) o).operand2.equals(this.operand2);
        }

        return false;
    }

    public String toString() {
        String operatorString = "uninitialized";
        if (operator.equals(InfixExpression.Operator.AND)) {
            operatorString = "logical and";
        } else if (operator.equals(InfixExpression.Operator.CONDITIONAL_AND)) {
            operatorString = "and";
        } else if (operator.equals(InfixExpression.Operator.CONDITIONAL_OR)) {
            operatorString = "or";
        } else if (operator.equals(InfixExpression.Operator.DIVIDE)) {
            operatorString = "divided by";
        } else if (operator.equals(InfixExpression.Operator.EQUALS)) {
            operatorString = "equals";
        } else if (operator.equals(InfixExpression.Operator.LESS)) {
            operatorString = "less than";
        } else if (operator.equals(InfixExpression.Operator.LESS_EQUALS)) {
            operatorString = "less than or equal to";
        } else if (operator.equals(InfixExpression.Operator.LEFT_SHIFT)) {
            operatorString = "left shift";
        } else if (operator.equals(InfixExpression.Operator.GREATER)) {
            operatorString = "greater than";
        } else if (operator.equals(InfixExpression.Operator.GREATER_EQUALS)) {
            operatorString = "greater than or equal to";
        } else if (operator.equals(InfixExpression.Operator.MINUS)) {
            operatorString = "minus";
        } else if (operator.equals(InfixExpression.Operator.NOT_EQUALS)) {
            operatorString = "not equal to";
        } else if (operator.equals(InfixExpression.Operator.OR)) {
            operatorString = "or";
        } else if (operator.equals(InfixExpression.Operator.PLUS)) {
            operatorString = "plus";
        } else if (operator.equals(InfixExpression.Operator.REMAINDER)) {
            operatorString = "modulus";
        } else if (operator.equals(InfixExpression.Operator.RIGHT_SHIFT_SIGNED)) {
            operatorString = "right shift";
        } else if (operator.equals(InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED)) {
            operatorString = "right shift";
        } else if (operator.equals(InfixExpression.Operator.TIMES)) {
            operatorString = "times";
        }

        return operand1 + " " + operatorString + " " + operand2;
    }
}