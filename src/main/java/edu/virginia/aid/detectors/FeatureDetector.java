package edu.virginia.aid.detectors;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.MethodFeatures;

/**
 * Interface for a single feature detector designed to run on a method
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public interface FeatureDetector {

    /**
     * Process the method, modifying the passed features object accordingly
     *
     * @param method The method to process
     * @param features The parsed features object to update when processing
     */
	public void process(MethodDeclaration method, MethodFeatures features);

	// TODO: Rename this class to FeatureProcessor
	// TODO: Rename the containing package to edu.virginia.aid.processors

}
