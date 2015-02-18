package edu.virginia.aid.symex;

/**
 * Data wrapper for a numeric value in an expression
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class Constant implements IdentifierValue {

    private double value;

    public Constant(double value) {
        this.value = value;
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
    public boolean isDisjointWith(IdentifierValue iv) {
        return iv instanceof BooleanValue && !((BooleanValue) iv).getValue();
    }

    @Override
    public IdentifierValue getIntersection(IdentifierValue iv) {
        return null;
    }

    @Override
    public boolean isConstantType() {
        return true;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Constant) && (((Constant) o).value == value);
    }

    public String toString() {
        return Double.toString(value);
    }
}
