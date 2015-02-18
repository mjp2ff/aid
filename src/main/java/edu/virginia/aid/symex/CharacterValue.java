package edu.virginia.aid.symex;

/**
 * Data wrapper for a character literal in symbolic execution evaluation
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class CharacterValue implements IdentifierValue {

    private char value;

    public CharacterValue(char value) {
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
        return false;
    }

    @Override
    public IdentifierValue getIntersection(IdentifierValue iv) {
        return null;
    }

    @Override
    public boolean isConstantType() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CharacterValue) {
            return ((CharacterValue) o).value == value;
        }

        return false;
    }

    @Override
    public String toString() {
        return "" + this.value;
    }
}
