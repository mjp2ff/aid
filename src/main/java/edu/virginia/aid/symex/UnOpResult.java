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
        if (operator.equals(PrefixExpression.Operator.NOT)) {
            return operand;
        } else {
            return this;
        }
    }

    @Override
    public IdentifierValue simplify() {
        IdentifierValue operand = this.operand.simplify();
        if (operand instanceof Constant) {
            double value = ((Constant) operand).getValue();
            if (operator.equals(PrefixExpression.Operator.MINUS)) {
                return new Constant(-value);
            } else if (operator.equals(PrefixExpression.Operator.PLUS)) {
                return new Constant(+value);
            }
        } else if (operand instanceof BooleanValue) {
            boolean value = ((BooleanValue) operand).getValue();
            if (operator.equals(PrefixExpression.Operator.NOT)) {
                return new BooleanValue(!value);
            }
        }
        return new UnOpResult(operator, operand);
    }

    @Override
    public boolean isDisjointWith(IdentifierValue iv) {
        return iv instanceof BooleanValue && !((BooleanValue) iv).getValue();
    }

    @Override
    public IdentifierValue getIntersection(IdentifierValue iv) {
        return null;
    }

    @Override
    public boolean isConstantType() {
        return false;
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
            operatorString = "not";
        } else if (operator.equals(PrefixExpression.Operator.PLUS)) {
            operatorString = "plus";
        }

        return operatorString + " " + operand;
    }
}
