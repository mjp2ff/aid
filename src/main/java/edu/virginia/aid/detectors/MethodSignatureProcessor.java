package edu.virginia.aid.detectors;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.MethodFeatures;

/**
 * Feature detector for extracting information from the method's signature
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class MethodSignatureProcessor implements FeatureDetector {

    /**
     * Processes the method, adding features based on information from the method signature
     *
     * @param method The method to process
     * @param features The parsed features object to update when processing
     */
    @Override
    public void process(MethodDeclaration method, MethodFeatures features) {

        // Check whether the method returns a boolean
        features.addBooleanFeature(MethodFeatures.RETURNS_BOOLEAN,
                features.getReturnType() != null && features.getReturnType().toString().equals("boolean"));

        // Check whether the method is a constructor
        features.addBooleanFeature(MethodFeatures.IS_CONSTRUCTOR,
                features.getMethodName().equals(features.getParentClass().getClassName()));
    }
}
