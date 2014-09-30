package edu.virginia.aid.detectors;

import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.CommentInfo;
import edu.virginia.aid.data.MethodFeatures;

/**
 * Feature processor for handling word stemming within comments.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class StemmingProcessor implements FeatureDetector {

	static final String[] DEFAULT_SUFFIXES = { "ion", "ions", "ive", "ed", "ing" };

	/**
	 * Processes the comments, reducing words down to appropriate stems.
	 *
	 * @param method
	 *            The method to process
	 * @param features
	 *            The parsed features object to update when processing
	 */
	@Override
	public void process(MethodDeclaration method, MethodFeatures features) {

		// Handle Javadoc from features first.
		String newJavadoc = this.removeSuffixes(features.getJavadoc());
		features.setJavadoc(newJavadoc);

		// Next handle internal comments.
		List<CommentInfo> comments = features.getComments();
		for (CommentInfo c : comments) {
			String newCommentText = this.removeSuffixes(c.getCommentText());
			c.setCommentText(newCommentText);
		}
	}

	/**
	 * Runs a given string through the default suffix-list, stripping any suffixes.
	 * 
	 * @param s
	 *            The given string, to be run through the suffix-list.
	 * @return The original string with any suffixes removed.
	 */
	private String removeSuffixes(String s) {
		String[] split = s.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
		String newS = "";
		for (String word : split) {
			// If only this were functional :(
			for (String suffix : DEFAULT_SUFFIXES) {
				if (word.endsWith(suffix)) {
					newS += word.substring(0, word.lastIndexOf(suffix)) + " ";
				} else {
					newS += word + " ";
				}
			}
		}
		return newS.trim();
	}

}
