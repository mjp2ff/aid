package edu.virginia.aid.comparison;

public class GenericDifference extends Difference {

    private String commentContent;
    private String methodContent;

    public GenericDifference(String commentContent, String methodContent, int differenceScore) {
        super(differenceScore);

        this.commentContent = commentContent;
        this.methodContent = methodContent;
    }

    /**
     * Presents human-readable string describing difference
     *
     * @return String describing difference
     */
    @Override
    public String toString() {
        return "Expected '" + methodContent + "' in comment but got '" + commentContent + "' instead";
    }
}
