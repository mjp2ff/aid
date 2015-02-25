package edu.virginia.aid.comparison;

import java.util.ArrayList;
import java.util.Collections;

import edu.virginia.aid.data.MethodFeatures;

/**
 * Wrapper for tracking differences between a method and it's comments
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
@SuppressWarnings("serial")
public class MethodDifferences extends ArrayList<Difference> implements Comparable<MethodDifferences> {

    /**
     * The method being tracked
     */
    private MethodFeatures method;

    /**
     * Create a difference tracker with the given method
     *
     * @param method The method for which to track differences
     */
    public MethodDifferences(MethodFeatures method) {
        this.method = method;
    }

    /**
     * Finds and returns the total difference score for the method
     *
     * @return Total difference score for the method
     */
    public double getDifferenceScore() {
        double differenceScore = 0;
        for (Difference difference : this) {
            differenceScore += difference.getDifferenceScore();
        }

        return differenceScore;
    }

    /**
     * Sorts from highest difference score to lowest difference score
     *
     * @param m MethodDifferences to compare to
     * @return Comparison value
     */
    @Override
    public int compareTo(MethodDifferences m) {
    	if (m.getDifferenceScore() < this.getDifferenceScore()) {
    		return -1;
    	} else if (m.getDifferenceScore() > this.getDifferenceScore()) {
    		return 1;
    	} else {
    		return 0;
    	}
    }

    /**
     * Presents a human-readable representation of all differences in the method, sorted from highest
     * to lowest differenceScore
     *
     * @return Human-readable representation of differences
     */
    @Override
    public String toString() {
        String result = "Total difference score for " + method.getParentClass().getClassName() +
        		"." + method.getMethodSignature() + " (" + method.getFilepath() + ", line " +
        		method.getElementLineNumber() + "): " + getDifferenceScore() + "\n";
        Collections.sort(this);
        for (Difference difference : this) {
            result += "\t" + difference.toString() + "\n";
        }
        return result;
    }
}