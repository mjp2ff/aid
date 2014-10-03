package edu.virginia.aid.detectors;

import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

/**
 * Finds and logs information about each parameter to the method
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ParameterDetector implements FeatureDetector {

    @Override
    public void process(MethodDeclaration method, MethodFeatures features) {
        for (Object o : method.parameters()) {
            if (o instanceof VariableDeclaration) {
                VariableDeclaration parameter = (VariableDeclaration) o;
                features.addIdentifier(new IdentifierProperties(parameter.getName().getIdentifier(), "", IdentifierProperties.IdentifierContext.FORMAL_PARAMETER, parameter.getStartPosition(), parameter.getStartPosition() + parameter.getLength(), features.getSourceContext()));
            }
        }
    }
}
