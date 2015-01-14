package edu.virginia.aid.symex;

import org.eclipse.jdt.core.dom.InfixExpression;

public class BinOpResult implements IdentifierValue {

    private InfixExpression.Operator operator;
    private IdentifierValue operand1;
    private IdentifierValue operand2;

    public BinOpResult(InfixExpression.Operator operator, IdentifierValue operand1, IdentifierValue operand2) {
        this.operator = operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
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
