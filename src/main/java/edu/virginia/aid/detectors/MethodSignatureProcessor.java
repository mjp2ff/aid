package edu.virginia.aid.detectors;

import edu.virginia.aid.data.MethodFeatures;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodSignatureProcessor implements FeatureDetector {
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
