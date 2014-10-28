package edu.virginia.aid.detectors;

import edu.virginia.aid.data.*;
import edu.virginia.aid.visitors.ControlFlowVisitor;
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

        // Control flow
        ControlFlowVisitor controlFlowVisitor = new ControlFlowVisitor(features);
        method.accept(controlFlowVisitor);
        BlockProperties blockProperties = new BlockProperties(features.getScope(), method.getStartPosition(),
                method.getLength() + method.getStartPosition(), features.getSourceContext());
        controlFlowVisitor.getConditionals().stream().forEach(conditional -> blockProperties.addConditional(conditional));
        controlFlowVisitor.getLoops().stream().forEach(loop -> blockProperties.addLoop(loop));
        features.setBlockProperties(blockProperties);

        // Return value
        ReturnVisitor returnVisitor = new ReturnVisitor(features);
        method.accept(returnVisitor);

        // Create return value expression
        ExpressionInfo returnValue = new ExpressionInfo();

        // Add resolved identifiers
        for (IdentifierName identifierName : returnVisitor.getIdentifierUses()) {
            IdentifierProperties identifier = identifierName.getResolvedIdentifier(features);
            if (identifier != null) {
                returnValue.addIdentifier(identifier);
                identifier.setInReturnStatement(true);
            }
        }

        // Add return value to MethodFeatures
        features.setReturnValue(returnValue);
    }
}
