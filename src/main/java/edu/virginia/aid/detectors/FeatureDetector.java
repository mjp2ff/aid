package edu.virginia.aid.detectors;

import edu.virginia.aid.MethodFeatures;
import org.eclipse.jdt.core.dom.MethodDeclaration;

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

}
