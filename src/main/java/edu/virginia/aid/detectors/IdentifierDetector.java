package edu.virginia.aid.detectors;

import edu.virginia.aid.MethodFeatures;
import edu.virginia.aid.visitors.VariableDeclarationVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import java.util.List;

/**
 * Feature detector for finding and tagging variable declarations, including paramters and local variables
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
        VariableDeclarationVisitor visitor = new VariableDeclarationVisitor();
        visitor.clearDeclarations();
        method.accept(visitor);

        List<VariableDeclaration> declarations = visitor.getDeclarations();
        for (VariableDeclaration declaration : declarations) {
            SimpleName name = declaration.getName();
            System.out.println(name);
        }
    }
}
