package edu.virginia.aid.comparison;

/**
 * Data wrapper for weighting information in differences
 */
public final class DifferenceWeights {

    // Variable usage
    public static final int PARAMETER_READ = 1;
    public static final int PARAMETER_WRITE = 2;
    public static final int FIELD_READ = 2;
    public static final int FIELD_WRITE = 4;

    // Concepts
    public static final int METHOD_NAME = 5;

    // Structures
    public static final int ONLY_METHOD_INVOCATION = 4;
}
