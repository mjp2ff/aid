package edu.virginia.aid.detectors;

import java.util.List;

import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.CommentInfo;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.visitors.CommentVisitor;

/**
 * Feature detector for finding and tagging comments
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class CommentDetector implements FeatureDetector {

	/**
	 * Processes the method, adding each identifier found to the MethodFeatures
	 *
	 * @param method
	 *            The method to process
	 * @param features
	 *            The parsed features object to update when processing
	 */
	@Override
	public void process(MethodDeclaration method, MethodFeatures features) {
		CommentVisitor visitor = new CommentVisitor();
		visitor.clearComments();
        method.accept(visitor);

		List<Comment> comments = visitor.getComments();

		for (Comment comment : comments) {
			int startPos = comment.getStartPosition();
			int endPos = startPos + comment.getLength();
            features.addComment(new CommentInfo(startPos, endPos, features.getSourceContext()));
		}
	}
}
