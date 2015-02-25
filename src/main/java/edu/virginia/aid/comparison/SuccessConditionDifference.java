package edu.virginia.aid.comparison;

import edu.virginia.aid.symex.IdentifierValue;

public class SuccessConditionDifference extends Difference {

    private IdentifierValue condition;

    public SuccessConditionDifference(IdentifierValue condition, double v) {
        super(v);
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "Comments do not discuss the following condition for success: " + condition.toString() + ": " + getDifferenceScore();
    }
}
