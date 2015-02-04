package edu.virginia.aid.symex;

public interface IdentifierValue {

    public IdentifierValue negate();

    public IdentifierValue simplify();

    public boolean isDisjointWith(IdentifierValue iv);
}
