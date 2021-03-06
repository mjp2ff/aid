package edu.virginia.aid.comparison;

/**
 * Data wrapper for weighting information in differences
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public final class DifferenceWeights {

    // Variable usage
    public static final double PARAMETER_READ = 2;
    public static final double PARAMETER_WRITE = 4;
    public static final double FIELD_READ = 1;
    public static final double FIELD_WRITE = 2;

    // Concepts
    public static final double METHOD_NAME = 5;

    // Components
    public static final double PRIMARY_VERB = 25;
    public static final double PRIMARY_OBJECT = 25;
    public static final double CONDITIONS_FOR_SUCCESS = 15;
    public static final double VARIABLE_USAGE = 25;
}
