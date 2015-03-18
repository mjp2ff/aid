package edu.virginia.aid.comparison;

import java.text.DecimalFormat;

import edu.virginia.aid.data.IdentifierProperties;

/**
 * Difference class specific to an identifier
 *
 * @author Matt Pearson-Beck and Jeff Principe
 */
public class MissingIdentifierDifference extends Difference {

    /**
     * The identifier missing in the comments
     */
    private IdentifierProperties identifier;

    /**
     * Create instance with a given identifier
     *
     * @param identifier The identifier that is missing
     * @param differenceScore The difference score for the identifier
     */
    public MissingIdentifierDifference(IdentifierProperties identifier, double differenceScore) {
        super(differenceScore);

        this.identifier = identifier;
    }

    /**
     * Presents human-readable string describing difference
     *
     * @return String describing difference
     */
    @Override
    public String toString() {
        return "No reference to " + identifier.getContextString() + " '" + identifier.getName() + "' in comments (reads: "
                + identifier.getReads() + ", writes: " + identifier.getWrites() + (identifier.isInReturnStatement() ? ", in return statment" : "") + ")" +
                ": " + getDifferenceScore();
    }

    @Override
    public String dumpData() {
    	DecimalFormat df = new DecimalFormat("0.000");
    	return df.format(getDifferenceScore()) + ";" + "No reference to " + identifier.getContextString() + " '" + identifier.getName() + "' in comments (reads: "
        + identifier.getReads() + ", writes: " + identifier.getWrites() + (identifier.isInReturnStatement() ? ", in return statment" : "") + ")" + ";";
    }
}
