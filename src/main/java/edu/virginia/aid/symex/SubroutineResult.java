package edu.virginia.aid.symex;

/**
 * Placeholder for the return value of a method
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class SubroutineResult implements IdentifierValue {

    private String subroutineName;

    public SubroutineResult(String subroutineName) {
        this.subroutineName = subroutineName;
    }

    @Override
    public IdentifierValue negate() {
        return this;
    }

    public String toString() {
        return subroutineName;
    }
}
