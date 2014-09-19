package edu.virginia.aid.comparison;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for tracking differences between a method and it's comments
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class MethodDifferences extends ArrayList<Difference> implements Comparable {

    private String methodName = "";

    public MethodDifferences(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Finds and returns the total difference score for the method
     *
     * @return Total difference score for the method
     */
    public int getDifferenceScore() {
        int differenceScore = 0;
        for (Difference difference : this) {
            differenceScore += difference.getDifference();
        }

        return differenceScore;
    }

    /**
     * Sorts from highest difference score to lowest difference score
     *
     * @param o MethodDifferences to compare to
     * @return Comparison value
     */
    @Override
    public int compareTo(Object o) {
        return ((MethodDifferences) o).getDifferenceScore() - this.getDifferenceScore();
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}