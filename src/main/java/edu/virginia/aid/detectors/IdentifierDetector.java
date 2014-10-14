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
        for (String fieldName : usageVisitor.getFieldNames()) {
            IdentifierProperties field = features.getParentClass().getFieldByName(fieldName);
            if (field != null) {
                features.addIdentifier(new IdentifierProperties(field));
            }
        }

        // Adds variable read information to the appropriate variables
        Map<String, Integer> reads = usageVisitor.getIdentifierReads();
        for (String name : reads.keySet()) {
            IdentifierProperties variable = features.getClosestVariable(name);
            if (variable != null) {
                variable.addReads(reads.get(name));
            }
        }

        // Adds variable write information to the appropriate variables
        Map<String, Integer> writes = usageVisitor.getIdentifierWrites();
        for (String name : writes.keySet()) {
            IdentifierProperties variable = features.getClosestVariable(name);
            if (variable != null) {
                variable.addWrites(writes.get(name));
            }
        }

        // Adds field read information to the appropriate field (if present)
        Map<String, Integer> fieldReads = usageVisitor.getFieldReads();
        for (String name : fieldReads.keySet()) {
            for (IdentifierProperties field : features.getFields()) {
                if (field.getName().equals(name)) {
                    field.addReads(fieldReads.get(name));
                    break;
                }
            }
        }

        // Adds field write information to the appropriate field (if present)
        Map<String, Integer> fieldWrites = usageVisitor.getFieldWrites();
        for (String name : fieldWrites.keySet()) {
            for (IdentifierProperties field : features.getFields()) {
                if (field.getName().equals(name)) {
                    field.addWrites(fieldWrites.get(name));
                }
            }
        }
    }

}
