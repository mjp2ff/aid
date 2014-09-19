package edu.virginia.aid.comparison;

/**
 * Data wrapper for information about one difference between
 * a method's comments and its contents
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class Difference {

    private String commentContent;
    private String methodContent;
    private int difference;

    public Difference(String commentContent, String methodContent, int difference) {
        this.commentContent = commentContent;
        this.methodContent = methodContent;
        this.difference = difference;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getMethodContent() {
        return methodContent;
    }

    public void setMethodContent(String methodContent) {
        this.methodContent = methodContent;
    }

    public int getDifference() {
        return difference;
    }

    public void setDifference(int difference) {
        this.difference = difference;
    }
}
