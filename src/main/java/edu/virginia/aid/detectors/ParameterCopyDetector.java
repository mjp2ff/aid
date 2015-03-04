package edu.virginia.aid.detectors;

import java.util.Map;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.MethodFeatures;

/**
 * Feature detector for copying method information from a method to an alias for it. This is used in
 * cases where one method serves as an alias for a call to another method. These "one-liner" methods
 * inherit the properties computed for the methods they call for classification purposes.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ParameterCopyDetector implements FeatureDetector {

    /**
     * The method whose machine learning model parameters should be copied
     */
    private MethodDeclaration methodToCopy;

    /**
     * Mapping of method declarations to their associated property objects
     */
    private Map<MethodDeclaration, MethodFeatures> methodFeaturesMap;

    /**
     * Creates a new ParameterCopyDetector with the method to copy and a map of method declarations to feature objects
     *
     * @param methodToCopy The method called by the current "one-liner" method
     * @param methodFeaturesMap Mapping of method declarations to their associated property objects
     */
    public ParameterCopyDetector(MethodDeclaration methodToCopy, Map<MethodDeclaration, MethodFeatures> methodFeaturesMap) {
        this.methodToCopy = methodToCopy;
        this.methodFeaturesMap = methodFeaturesMap;
    }

    /**
     * Checks if there is a distinct method whose properties should be copied and, if so, copies them over
     *
     * @param method The method to process
     * @param features The parsed features object to update when processing
     */
    @Override
    public void process(MethodDeclaration method, MethodFeatures features) {
        if (method != methodToCopy) {
            MethodFeatures root = methodFeaturesMap.get(methodToCopy);
            for (String property : root.getNumericFeatures().keySet()) {
                features.addNumericFeature(property, root.getNumericFeature(property));
            }
            for (String property : root.getBooleanFeatures().keySet()) {
                features.addBooleanFeature(property, root.getBooleanFeature(property));
            }
        }
    }
}
