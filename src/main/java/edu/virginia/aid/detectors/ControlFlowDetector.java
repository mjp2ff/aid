package edu.virginia.aid.detectors;

import edu.virginia.aid.data.ExpressionInfo;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.visitors.ReturnVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * Adds information about the control flow to the class
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ControlFlowDetector implements FeatureDetector {

    @Override
    public void process(MethodDeclaration method, MethodFeatures features) {
        // Return value
        ReturnVisitor returnVisitor = new ReturnVisitor();
        method.accept(returnVisitor);

        // Create return value expression
        ExpressionInfo returnValue = new ExpressionInfo();

        // Add resolved identifiers
        for (String identifierName : returnVisitor.getIdentifierReads().keySet()) {
            returnValue.addIdentifier(features.getClosestVariable(identifierName));
        }

        // Add resolved fields
        for (String fieldName : returnVisitor.getFieldReads().keySet()) {
            returnValue.addIdentifier(features.getField(fieldName));
        }

        // Add return value to MethodFeatures
        features.setReturnValue(returnValue);
    }
}
