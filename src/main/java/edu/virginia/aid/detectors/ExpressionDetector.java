package edu.virginia.aid.detectors;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.visitors.ExpressionVisitor;

public class ExpressionDetector implements FeatureDetector {

    @Override
    public void process(MethodDeclaration method, MethodFeatures features) {
        ExpressionVisitor visitor = new ExpressionVisitor();
        method.accept(visitor);
//        features.addNumericFeature(MethodFeatures.NUM_COMPARISONS, visitor.getNumComparisons());
    }
}
