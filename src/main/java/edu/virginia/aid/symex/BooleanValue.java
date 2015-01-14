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

    @Override
    public IdentifierValue negate() {
        return this;
    }

    public String toString() {
        return (value ? "true" : "false");
    }
}
