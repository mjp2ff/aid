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
        return (o instanceof SubroutineResult) && (subroutineName.equals(((SubroutineResult) o).subroutineName));
    }

    public String toString() {
        return subroutineName;
    }
}
