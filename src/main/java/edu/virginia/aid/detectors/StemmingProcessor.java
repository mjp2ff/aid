package edu.virginia.aid.detectors;

import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.CommentInfo;
import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;

import org.tartarus.snowball.*;

/**
 * Feature processor for handling word stemming within comments.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class StemmingProcessor implements FeatureDetector {

	static final SnowballStemmer stemmer = new org.tartarus.snowball.ext.englishStemmer();
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

		// TODO: Handle Javadoc from features first.
//		String newJavadocComment = this.removeSuffixes(features.getJavadoc().getComment());
//		features.getJavadoc().setComment(newJavadocComment);

		// Next handle internal comments.
		List<CommentInfo> comments = features.getComments();
		for (CommentInfo c : comments) {			
			String newCommentText = this.stem(c.getCommentText());
			c.setCommentText(newCommentText);
		}

        // Process Method Name
        features.setProcessedMethodName(stem(features.getProcessedMethodName()));

		// Finally, handle identifiers (parameters, local variables, fields).
		for (IdentifierProperties parameter : features.getParameters()) {
			parameter.setProcessedName(this.stem(parameter.getProcessedName()));
		}
		for (IdentifierProperties localVariable : features.getLocalVariables()) {
			localVariable.setProcessedName(this.stem(localVariable.getProcessedName()));
		}
		for (IdentifierProperties field : features.getFields()) {
			field.setProcessedName(this.stem(field.getProcessedName()));
		}
	}

	/**
	 * Helper method to stem words, using Snowball Stemming library.
	 *
	 * @param s The string to be stemmed
	 * @return The stemmed word
	 */
	public String stem(String s) {
		stemmer.setCurrent(s);
		stemmer.stem();
		return stemmer.getCurrent();
	}

}
