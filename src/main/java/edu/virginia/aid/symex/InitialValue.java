package edu.virginia.aid.symex;

import edu.virginia.aid.data.IdentifierProperties;

public class InitialValue implements IdentifierValue {

    private IdentifierProperties identifier;

    public InitialValue(IdentifierProperties identifier) {
        this.identifier = identifier;
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
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof InitialValue) && (((InitialValue) o).identifier.equals(identifier));
    }

    public String toString() {
        return identifier.getName();
    }
}
