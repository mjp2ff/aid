package edu.virginia.aid.detectors;

import java.util.Map;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.MethodFeatures;

public class ParameterCopyDetector implements FeatureDetector {

    private MethodDeclaration methodToCopy;
    private Map<MethodDeclaration, MethodFeatures> methodFeaturesMap;

    public ParameterCopyDetector(MethodDeclaration methodToCopy, Map<MethodDeclaration, MethodFeatures> methodFeaturesMap) {
        this.methodToCopy = methodToCopy;
        this.methodFeaturesMap = methodFeaturesMap;
    }

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
