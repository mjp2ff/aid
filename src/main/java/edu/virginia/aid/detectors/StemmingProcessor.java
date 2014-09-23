package edu.virginia.aid.detectors;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.MethodFeatures;

/**
 * Feature processor for handling word stemming within comments.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class StemmingProcessor implements FeatureDetector {

    /**
     * Processes the comments, reducing words down to appropriate stems.
     *
     * @param method The method to process
     * @param features The parsed features object to update when processing
     */
	@Override
	public void process(MethodDeclaration method, MethodFeatures features) {
		
	}

}
