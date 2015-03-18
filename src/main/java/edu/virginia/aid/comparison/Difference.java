package edu.virginia.aid.comparison;

/**
 * Data wrapper for information about a single difference between
 * a method's comments and its contents.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public abstract class Difference implements Comparable<Difference> {

    /**
     * Numerical value for this difference's severity and significance (higher = more severe/significant)
     */
    private double differenceScore;

    /**
     * Create a new difference with the provided differenceScore
     *
     * @param differenceScore The numeric weight of this difference
     */
    public Difference(double differenceScore) {
        this.differenceScore = differenceScore;
    }

    /**
     * Get the weighted numeric  value for the difference
     *
     * @return The weighted numeric value for the difference
     */
    public double getDifferenceScore() {
        return differenceScore;
    }

    /**
     * Compares two Differences, ranking them from highest to lowest differenceScore
     *
     * @param other The Difference to compare to
     * @return Integer comparison value
     */
    public int compareTo(Difference other) {
    	if (other.getDifferenceScore() < differenceScore) {
    		return -1;
    	} else if (other.getDifferenceScore() > differenceScore) {
    		return 1;
    	} else {
    		return 0;
    	}
    }

    public abstract String dumpData();
}
