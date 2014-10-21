package edu.virginia.aid.data;

import java.util.Arrays;
import java.util.List;

/**
 * Data wrapper with information about an individual comment
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class CommentInfo extends SourceElement implements StringListable {

    private String commentText;

    public CommentInfo(int startPos, int endPos, final SourceContext sourceContext) {
        super(startPos, endPos, sourceContext);

        this.commentText = getElementText();
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public List<String> getData() {
		return Arrays.asList(commentText.split(" "));
    	
    }
}
