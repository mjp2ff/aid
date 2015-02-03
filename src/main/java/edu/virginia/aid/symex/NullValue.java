package edu.virginia.aid.symex;

/**
 * Data wrapper for the special Java value null
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class NullValue implements IdentifierValue {

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
        return o instanceof NullValue;
    }

    public String toString() {
        return "null";
    }
}
