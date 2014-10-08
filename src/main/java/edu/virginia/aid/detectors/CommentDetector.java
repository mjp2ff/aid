package edu.virginia.aid.detectors;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.MethodFeatures;

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
		features.setJavadoc(method.getJavadoc());
	}
}
