package edu.virginia.aid.detectors;

import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.visitors.VariableDeclarationVisitor;
import edu.virginia.aid.visitors.VariableUsageVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import java.util.List;
import java.util.Map;

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

        // Add parameters to identifier list
        for (Object o : method.parameters()) {
            if (o instanceof VariableDeclaration) {
                VariableDeclaration parameter = (VariableDeclaration) o;
                features.addIdentifier(new IdentifierProperties(parameter.getName().getIdentifier(), "", IdentifierProperties.IdentifierContext.FORMAL_PARAMETER, parameter.getStartPosition(), parameter.getStartPosition() + parameter.getLength(), features.getSourceContext()));
            }
        }

        // Look for local variables
        VariableDeclarationVisitor declarationVisitor = new VariableDeclarationVisitor();
        declarationVisitor.clearDeclarations();

        // Grab all writes and associated reads of variables from method
        VariableUsageVisitor usageVisitor = new VariableUsageVisitor();

        Block methodBody = method.getBody();
        if (methodBody != null) {
            methodBody.accept(declarationVisitor);
            methodBody.accept(usageVisitor);
        }

        List<VariableDeclaration> declarations = declarationVisitor.getDeclarations();
        for (VariableDeclaration declaration : declarations) {
            IdentifierProperties identifier = new IdentifierProperties(declaration.getName().getIdentifier(), declaration.getStartPosition(), declaration.getStartPosition() + declaration.getLength(), features.getSourceContext());
            identifier.setContext(IdentifierProperties.IdentifierContext.LOCAL_VARIABLE);
            features.addIdentifier(identifier);
        }

        // Finds corresponding fields for all field usages, adding the discovered fields to MethodFeatures
        for (String fieldName : declarationVisitor.getFieldUsages()) {
            IdentifierProperties field = features.getParentClass().getFieldByName(fieldName);
            if (field != null) {
                features.addIdentifier(new IdentifierProperties(field));
            }
        }

        // Adds variable read information to the appropriate variables
        Map<String, Integer> reads = usageVisitor.getReads();
        for (String name : reads.keySet()) {
            IdentifierProperties variable = getClosestVariable(name, features);
            if (variable != null) {
                variable.addReads(reads.get(name));
            }
        }

        // Adds variable write information to the appropriate variables
        Map<String, Integer> writes = usageVisitor.getWrites();
        for (String name : writes.keySet()) {
            IdentifierProperties variable = getClosestVariable(name, features);
            if (variable != null) {
                variable.addWrites(writes.get(name));
            }
        }
    }

    /**
     * Finds and returns the closest scoped variable with the given name
     *
     * @param name Name of the variable to return
     * @param method The method to search for variables
     * @return Closest scoped variable
     */
    private static IdentifierProperties getClosestVariable(String name, MethodFeatures method) {
        // Search local variables
        for (IdentifierProperties variable : method.getLocalVariables()) {
            if (variable.getName().equals(name)) {
                return variable;
            }
        }

        // Search parameters
        for (IdentifierProperties parameter : method.getParameters()) {
            if (parameter.getName().equals(name)) {
                return parameter;
            }
        }

        // Search fields
        for (IdentifierProperties field : method.getFields()) {
            if (field.getName().equals(name)) {
                return field;
            }
        }

        // Return null if none found
        return null;
    }
}
