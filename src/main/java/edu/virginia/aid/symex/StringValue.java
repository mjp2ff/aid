package edu.virginia.aid.symex;

/**
 * Data wrapper for a string literal
 */
public class StringValue implements IdentifierValue {

    private String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public IdentifierValue negate() {
        return this;
    }

    public String toString() {
        return value;
    }
}
