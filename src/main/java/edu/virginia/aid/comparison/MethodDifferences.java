package edu.virginia.aid.comparison;

import java.util.ArrayList;

/**
 * Wrapper for tracking differences between a method and it's comments
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
@SuppressWarnings("serial")
public class MethodDifferences extends ArrayList<Difference> implements Comparable<MethodDifferences> {

    private String methodName;
    private String className;
    private String filepath;

    public MethodDifferences(String methodName, String className, String filepath) {
        this.methodName = methodName;
        this.className = className;
        this.filepath = filepath;
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
     * @param m MethodDifferences to compare to
     * @return Comparison value
     */
    @Override
    public int compareTo(MethodDifferences m) {
        return m.getDifferenceScore() - this.getDifferenceScore();
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        String result = "Total difference score for " + className + "." + methodName + ": " + getDifferenceScore() + "\n";
        for (Difference difference : this) {
            result += "\tExpected '" + difference.getMethodContent() + "' in comment but got '" + difference.getCommentContent() + "' instead\n";
        }
        return result;
    }
}