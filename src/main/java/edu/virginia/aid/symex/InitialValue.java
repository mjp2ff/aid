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

    public String toString() {
        return identifier.getName();
    }
}
