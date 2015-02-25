package edu.virginia.aid.comparison;

import edu.virginia.aid.symex.IdentifierValue;

/**
 * Data wrapper for a single success condition that is missing
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class SuccessConditionDifference extends Difference {

    /**
     * The condition that is missing from the comments
     */
    private IdentifierValue condition;

    /**
     * Create a SuccessConditionDifference with the given condition and numeric weight
     *
     * @param condition The condition that is missing from the comments
     * @param v The numeric weight of the difference
     */
    public SuccessConditionDifference(IdentifierValue condition, double v) {
        super(v);
        this.condition = condition;
    }

    /**
     * Converts the difference into a human-readable representation of the missing condition
     *
     * @return Human-readable representation of the difference
     */
    @Override
    public String toString() {
        return "Comments do not discuss the following condition for success: " + condition.toString() + ": " + getDifferenceScore();
    }
}
