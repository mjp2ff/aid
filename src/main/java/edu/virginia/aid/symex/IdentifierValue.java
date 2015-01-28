package edu.virginia.aid.symex;

public interface IdentifierValue {

    public IdentifierValue negate();

    public IdentifierValue simplify();

    boolean subsumes(IdentifierValue identifierValue);
}
