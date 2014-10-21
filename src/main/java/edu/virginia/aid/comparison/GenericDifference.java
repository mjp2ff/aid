package edu.virginia.aid.comparison;

public class GenericDifference extends Difference {

    private String differenceMessage;

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
        return differenceMessage;
    }
}
