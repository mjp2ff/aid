package edu.virginia.aid.detectors;

import java.util.Arrays;
import java.util.List;

import edu.virginia.aid.data.MethodInvocationProperties;
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

	static final String[] DEFAULT_STOPLIST = { "a", "able", "about", "across", "after", "all",
			"almost", "also", "am", "among", "an", "and", "any", "are", "as", "at", "be",
			"because", "been", "but", "by", "can", "cannot", "could", "dear", "did", "do", "does",
			"either", "else", "ever", "every", "for", "from", "get", "got", "had", "has", "have",
			"he", "her", "hers", "him", "his", "how", "however", "i", "if", "in", "into", "is",
			"it", "its", "just", "least", "let", "like", "likely", "may", "me", "might", "most",
			"must", "my", "neither", "no", "nor", "not", "of", "off", "often", "on", "only", "or",
			"other", "our", "own", "rather", "said", "say", "says", "she", "should", "since", "so",
			"some", "than", "that", "the", "their", "them", "then", "there", "these", "they",
			"this", "tis", "to", "too", "twas", "us", "wants", "was", "we", "were", "what", "when",
			"where", "which", "while", "who", "whom", "why", "will", "with", "would", "yet", "you",
			"your" };

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

		// TODO: Handle Javadoc from features first.
		// String newJavadocComment = this.checkStoplist(features.getJavadoc().getComment());
		// features.getJavadoc().setComment(newJavadocComment);

		// Next handle internal comments.
		List<CommentInfo> comments = features.getComments();
		for (CommentInfo c : comments) {
			String newCommentText = this.checkStoplist(c.getCommentText());
			c.setCommentText(newCommentText);
		}

        // Process Method Name
        features.setProcessedMethodName(features.getProcessedMethodName());

		// Finally, handle identifiers (parameters, local variables, fields, methods).
		for (IdentifierProperties parameter : features.getScope().getParameters()) {
			parameter.setProcessedName(this.checkStoplist(parameter.getProcessedName()));
		}
		for (IdentifierProperties localVariable : features.getScope().getLocalVariables()) {
			localVariable.setProcessedName(this.checkStoplist(localVariable.getProcessedName()));
		}
		for (IdentifierProperties field : features.getScope().getFields()) {
			field.setProcessedName(this.checkStoplist(field.getProcessedName()));
		}
        for (MethodInvocationProperties methodInvocation : features.getMethodInvocations()) {
            methodInvocation.setProcessedName(this.checkStoplist(methodInvocation.getProcessedName()));
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
