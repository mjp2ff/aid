package edu.virginia.aid.comparison;

/**
 * Data wrapper for information about one difference between
 * a method's comments and its contents
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public abstract class Difference implements Comparable<Difference> {

    private int differenceScore;

    public Difference(int differenceScore) {
        this.differenceScore = differenceScore;
    }

    public int getDifferenceScore() {
        return differenceScore;
    }

    /**
     * Compares two Differences, ranking them from highest to lowest differenceScore
     *
     * @param other The Difference to compare to
     * @return Integer comparison value
     */
    public int compareTo(Difference other) {
        return other.getDifferenceScore() - differenceScore;
    }
}
