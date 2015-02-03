package edu.virginia.aid.symex;

import org.eclipse.jdt.core.dom.PrefixExpression;

/**
 * Data wrapper for the result of a unary operation on a value
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class UnOpResult implements IdentifierValue {

    private PrefixExpression.Operator operator;
    private IdentifierValue operand;

    public UnOpResult(PrefixExpression.Operator operator, IdentifierValue operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public IdentifierValue negate() {
        return this;
    }

    @Override
    public IdentifierValue simplify() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof UnOpResult) && (((UnOpResult) o).operator.equals(operator)) && (operand.equals(((UnOpResult) o).operand));
    }

    public String toString() {
        String operatorString = "undefined";
        if (operator.equals(PrefixExpression.Operator.COMPLEMENT)) {
            operatorString = "complement";
        } else if (operator.equals(PrefixExpression.Operator.MINUS)) {
            operatorString = "negative";
        } else if (operator.equals(PrefixExpression.Operator.NOT)) {
            operatorString = "logical not";
        } else if (operator.equals(PrefixExpression.Operator.PLUS)) {
            operatorString = "plus";
        }

        return operatorString + " " + operand;
    }
}
