package edu.virginia.aid.detectors;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.CommentInfo;
import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;

/**
 * Feature processor for removing words based on a stopist.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class StoplistProcessor implements FeatureDetector {

	static final String[] DEFAULT_STOPLIST = { "a", "an", "and", "are", "as", "at", "be", "by",
			"for", "from", "has", "he", "in", "is", "it", "its", "of", "on", "that", "the", "to",
			"was", "were", "will", "with" };

	/**
	 * Processes the comments, removing words in the stoplist.
	 *
	 * @param method
	 *            The method to process
	 * @param features
	 *            The parsed features object to update when processing
	 */
	@Override
	public void process(MethodDeclaration method, MethodFeatures features) {

		// Handle Javadoc from features first.
		String newJavadocComment = this.checkStoplist(features.getJavadoc().getComment());
		features.getJavadoc().setComment(newJavadocComment);

		// Next handle internal comments.
		List<CommentInfo> comments = features.getComments();
		for (CommentInfo c : comments) {
			String newCommentText = this.checkStoplist(c.getCommentText());
			c.setCommentText(newCommentText);
		}

		// Finally, handle identifiers (parameters, local variables, fields).
		for (IdentifierProperties parameter : features.getParameters()) {
			parameter.setProcessedName(this.checkStoplist(parameter.getProcessedName()));
		}
		for (IdentifierProperties localVariable : features.getLocalVariables()) {
			localVariable.setProcessedName(this.checkStoplist(localVariable.getProcessedName()));
		}
		for (IdentifierProperties field : features.getFields()) {
			field.setProcessedName(this.checkStoplist(field.getProcessedName()));
		}
	}

	/**
	 * Runs a given string through the default stoplist, removing any words that match.
	 * 
	 * @param s
	 *            The given string, to be run through the stoplist.
	 * @return The original string with any 'stoplist words' removed.
	 */
	private String checkStoplist(String s) {
		String[] split = s.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
		String newS = "";
		for (String word : split) {
			if (!Arrays.asList(DEFAULT_STOPLIST).contains(word)) {
				newS += word + " ";
			}
		}
		return newS.trim();
	}
}
