package edu.virginia.aid.detectors;

import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.visitors.VariableDeclarationVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import java.util.List;

/**
 * Feature detector for finding and tagging variable declarations, including parameters and local variables
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class IdentifierDetector implements FeatureDetector {

    /**
     * Processes the method, adding each identifier found to the MethodFeatures
     *
     * @param method The method to process
     * @param features The parsed features object to update when processing
     */
	@Override
    public void process(MethodDeclaration method, MethodFeatures features) {
        VariableDeclarationVisitor declarationVisitor = new VariableDeclarationVisitor();
        declarationVisitor.clearDeclarations();

        Block methodBody = method.getBody();
        if (methodBody != null) {
            methodBody.accept(declarationVisitor);
        }

        List<VariableDeclaration> declarations = declarationVisitor.getDeclarations();
        for (VariableDeclaration declaration : declarations) {
            IdentifierProperties identifier = new IdentifierProperties(declaration.getName().getIdentifier());
            identifier.setContext(IdentifierProperties.IdentifierContext.LOCAL_VARIABLE);
            features.addIdentifier(identifier);
        }

        for (String fieldName : declarationVisitor.getFieldUsages()) {
            IdentifierProperties field = features.getParentClass().getFieldByName(fieldName);
            if (field != null) {
                features.addIdentifier(features.getParentClass().getFieldByName(fieldName));
            }
        }
    }
}
