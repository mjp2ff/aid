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

    @Override
    public IdentifierValue simplify() {
        return this;
    }

    @Override
    public boolean isDisjointWith(IdentifierValue iv) {
        return iv instanceof BooleanValue && !((BooleanValue) iv).getValue();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof StringValue) && (value.equals(((StringValue) o).value));
    }

    public String toString() {
        return value;
    }
}
