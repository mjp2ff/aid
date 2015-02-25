package edu.virginia.aid.data;

import java.util.Arrays;
import java.util.List;

/**
 * Data wrapper with information about an individual comment
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class CommentInfo extends SourceElement implements StringListable {

    /**
     * The content of the comment
     */
    private String commentText;

    /**
     * Creates a CommentInfo with the following properties
     *
     * @param startPos      The position of the first character of the comment in the containing file
     * @param endPos        The position of the last character of the comment in the containing file
     * @param sourceContext Information and contents for the containing file
     */
    public CommentInfo(int startPos, int endPos, final SourceContext sourceContext) {
        super(startPos, endPos, sourceContext);

        this.commentText = getElementText();
    }

    /**
     * Gets the content of the comment
     *
     * @return The contents of the comment
     */
    public String getCommentText() {
        return commentText;
    }

    /**
     * Sets the content of the comment to the commentText provided
     *
     * @param commentText The new contents for the comment
     */
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    /**
     * Gets the list of all words in the comment
     *
     * @return List of all words in the comment
     */
    public List<String> getData() {
		return Arrays.asList(commentText.split(" "));
    	
    }
}
