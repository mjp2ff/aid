package edu.virginia.aid.comparison;

import java.text.DecimalFormat;

/**
 * Concrete implementation of a Difference that simply takes in and provides a string description of the Difference
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class GenericDifference extends Difference {

    /**
     * Message describing the difference in human-readable form
     */
    private String differenceMessage;

    /**
     * Create a GenericDifference with the given description of the difference and weighting
     *
     * @param differenceMessage Human-readable description of the difference
     * @param differenceScore Numeric weighting given to the significance of the difference
     */
    public GenericDifference(String differenceMessage, double differenceScore) {
        super(differenceScore);

        this.differenceMessage = differenceMessage;
    }

    /**
     * Presents human-readable string describing difference
     *
     * @return String describing difference
     */
    @Override
    public String toString() {
        return differenceMessage + ": " + getDifferenceScore();
    }

    @Override
    public String dumpData() {
    	DecimalFormat df = new DecimalFormat("0.000");
    	return df.format(getDifferenceScore()) + ";" + differenceMessage + ";";
    }
}
