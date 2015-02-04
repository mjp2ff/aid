package edu.virginia.aid.symex;

/**
 * Data wrapper for a boolean constant
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class BooleanValue implements IdentifierValue {

    private boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
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
        return !value || (iv instanceof BooleanValue && !((BooleanValue) iv).getValue());
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof BooleanValue) && (value == ((BooleanValue) o).value);
    }

    public String toString() {
        return (value ? "true" : "false");
    }
}
