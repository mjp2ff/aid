package edu.virginia.aid.symex;

/**
 * Data wrapper for qualified names in a program, referring to external values.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ExternalValue implements IdentifierValue {

    private String name;

    public ExternalValue(String name) {
        this.name = name;
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
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ExternalValue) {
            return ((ExternalValue) o).name.equals(name);
        }
        return false;
    }
}
