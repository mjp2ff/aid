package edu.virginia.aid.data;

/**
 * Data wrapper with information about an individual comment
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class CommentInfo {

    private String commentText;
    private int startPos;
    private int endPos;

    public CommentInfo(String commentText, int startPos, int endPos) {
        this.commentText = commentText;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public String getCommentText() {
        return commentText;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }
}
