package edu.virginia.aid.detectors;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.MethodFeatures;

/**
 * Feature processor for removing words based on a stopist.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class StoplistProcessor implements FeatureDetector {

    /**
     * Processes the comments, removing words in the stoplist.
     *
     * @param method The method to process
     * @param features The parsed features object to update when processing
     */
	@Override
	public void process(MethodDeclaration method, MethodFeatures features) {
		
	}
}
