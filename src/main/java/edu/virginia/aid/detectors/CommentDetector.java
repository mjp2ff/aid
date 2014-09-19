package edu.virginia.aid.detectors;

import edu.virginia.aid.CommentInfo;
import edu.virginia.aid.MethodFeatures;
import edu.virginia.aid.visitors.CommentVisitor;

import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.List;

/**
 * Feature detector for finding and tagging comments
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class CommentDetector implements FeatureDetector {

	/**
	 * Data read in from a file.
	 */
	private String fileData;

	/**
	 * Creates a new CommentDetector based on some file's data.
	 * 
	 * @param fileData
	 *            The text of the file to be analyzed.
	 */
	public CommentDetector(String fileData) {
		this.fileData = fileData;
	}

	/**
	 * Processes the method, adding each identifier found to the MethodFeatures
	 *
	 * @param method
	 *            The method to process
	 * @param features
	 *            The parsed features object to update when processing
	 */
	public void process(MethodDeclaration method, MethodFeatures features) {
		CommentVisitor visitor = new CommentVisitor();
		visitor.clearComments();
        method.accept(visitor);

		System.out.println("====== Comments: ");
		
		List<Comment> comments = visitor.getComments();

		System.out.println("Found " + comments.size() + " comments");

		for (Comment comment : comments) {
			int startPos = comment.getStartPosition();
			int endPos = startPos + comment.getLength();
			String commentString = this.fileData.substring(startPos, endPos);
            features.addComment(new CommentInfo(commentString, startPos, endPos));
			System.out.println(commentString);
		}
	}
}
