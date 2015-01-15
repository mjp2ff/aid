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

    public String toString() {
        return "null";
    }
}
