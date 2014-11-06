package edu.virginia.aid.detectors;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import edu.virginia.aid.data.IdentifierName;
import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.data.MethodInvocationProperties;
import edu.virginia.aid.visitors.VariableDeclarationVisitor;
import edu.virginia.aid.visitors.VariableUsageVisitor;

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
                features.getScope().addVariable(new IdentifierProperties(parameter.getName().getIdentifier(), "", IdentifierProperties.IdentifierContext.FORMAL_PARAMETER, parameter.getStartPosition(), parameter.getStartPosition() + parameter.getLength(), features.getSourceContext()));
            }
        }

        // Look for local variables
        VariableDeclarationVisitor declarationVisitor = new VariableDeclarationVisitor();
        declarationVisitor.clearDeclarations();

        // Grab all writes and associated reads of variables from method
        VariableUsageVisitor usageVisitor = new VariableUsageVisitor(features, false);

        Block methodBody = method.getBody();
        if (methodBody != null) {
            methodBody.accept(declarationVisitor);
            methodBody.accept(usageVisitor);
        }

        List<VariableDeclaration> declarations = declarationVisitor.getDeclarations();
        for (VariableDeclaration declaration : declarations) {
            IdentifierProperties identifier = new IdentifierProperties(declaration.getName().getIdentifier(), declaration.getStartPosition(), declaration.getStartPosition() + declaration.getLength(), features.getSourceContext());
            identifier.setContext(IdentifierProperties.IdentifierContext.LOCAL_VARIABLE);
            features.getScope().addVariable(identifier);
        }

        // Finds corresponding fields for all field usages, adding the discovered fields to MethodFeatures
        for (String fieldName : usageVisitor.getFieldNames()) {
            IdentifierProperties field = features.getParentClass().getFieldByName(fieldName);
            if (field != null) {
                features.getScope().addVariable(new IdentifierProperties(field));
            }
        }

        for (IdentifierName identifierName : usageVisitor.getIdentifierUses()) {
           features.getScope().addIdentifierUse(identifierName);
        }

        // Set field reads
        int fieldReads = 0;
        for (IdentifierProperties field : features.getScope().getFields()) {
            fieldReads += field.getReads();
        }
        features.addNumericFeature(MethodFeatures.NUM_FIELD_READS, fieldReads);

        // Set parameter reads
        int parameterReads = 0;
        for (IdentifierProperties param : features.getScope().getParameters()) {
            parameterReads += param.getReads();
        }
        features.addNumericFeature(MethodFeatures.NUM_PARAM_READS, parameterReads);

        // Set field writes
        int fieldWrites = 0;
        for (IdentifierProperties field : features.getScope().getFields()) {
            fieldWrites += field.getWrites();
        }
        features.addNumericFeature(MethodFeatures.NUM_FIELD_WRITES, fieldWrites);
    }
}
