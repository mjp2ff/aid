package edu.virginia.aid.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;

import edu.virginia.aid.data.IdentifierName;
import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.data.MethodInvocationProperties;

/**
 * Scans an AST for all variable usages
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class VariableUsageVisitor extends NameVisitor {

    private List<IdentifierName> identifierUses = new ArrayList<>();
    private List<MethodInvocationProperties> methodInvocations = new ArrayList<>();

    /**
     * Tracking variable for whether or not the AST is currently in an assignment
     */
    private boolean inAssignment = false;

    /**
     * Creates a visitor with the method and context described below
     *
     * @param methodFeatures The containing method
     * @param writing        Whether or not the scope is the lhs of an assignment
     */
    public VariableUsageVisitor(MethodFeatures methodFeatures, boolean writing) {
        super(methodFeatures, writing);
    }

    /**
     * Gets the list of all uses of identifiers in the AST
     *
     * @return List of identifier uses in the AST
     */
    public List<IdentifierName> getIdentifierUses() {
        List<IdentifierName> allIdentifierUses = new ArrayList<>();
        allIdentifierUses.addAll(identifierUses);
        allIdentifierUses.addAll(identifiers);
        return allIdentifierUses;
    }

    /**
     * Gets all of the method invocations in the AST
     *
     * @return List of method invocations in the AST
     */
    public List<MethodInvocationProperties> getMethodInvocations() {
        List<MethodInvocationProperties> allMethodInvocations = new ArrayList<>();
        allMethodInvocations.addAll(methodInvocations);
        allMethodInvocations.addAll(methods);
        return allMethodInvocations;
    }

    /**
     * Gets all of the names of fields used in the AST
     *
     * @return Field names
     */
    public Set<String> getFieldNames() {
        Set<String> fieldNames = new HashSet<>();
        for (IdentifierName identifier : getIdentifierUses()) {
            if (identifier.isVariable() && identifier.hasClassScope()) {
                fieldNames.add(identifier.getName());
            }
        }

        return fieldNames;
    }

    /**
     * Gets all of the names of idneitifers used in the AST
     *
     * @return Identifier names
     */
    public Set<String> getIdentifierNames() {
        Set<String> identifierNames = new HashSet<>();
        for (IdentifierName identifier : getIdentifierUses()) {
            if (identifier.isVariable()) {
                identifierNames.add(identifier.getName());
            }
        }

        return identifierNames;
    }

    /**
     * Find all reads and writes of variables within an assignment
     *
     * @param node The assigment node to analyze
     * @return false
     */
    @Override
    public boolean visit(Assignment node) {
        Expression lhs = node.getLeftHandSide();
        NameVisitor visitor = new NameVisitor(methodFeatures, true);
        lhs.accept(visitor);
        identifierUses.addAll(visitor.getIdentifiers());
        methodInvocations.addAll(visitor.getMethods());

        if (inAssignment) {
            NameVisitor visitor1 = new NameVisitor(methodFeatures, false);
            lhs.accept(visitor);
            identifierUses.addAll(visitor1.getIdentifiers());
        }

        Expression rhs = node.getRightHandSide();
        visitor.clearFields();
        rhs.accept(visitor);
        identifierUses.addAll(visitor.getIdentifiers());
        methodInvocations.addAll(visitor.getMethods());

        boolean tempInAssignment = inAssignment;
        inAssignment = true;
        rhs.accept(this);
        inAssignment = tempInAssignment;

        return false;
    }

    /**
     * Gets all of the IdentifierProperties that can be resolved from the uses found by the visitor
     *
     * @return The resolved identifiers
     */
    public Set<IdentifierProperties> getResolvedIdentifiers() {
        Set<IdentifierProperties> resolvedIdentifiers = new HashSet<>();
        for (IdentifierName identifierUse : getIdentifierUses()) {
            IdentifierProperties resolvedIdentifier = identifierUse.getResolvedIdentifier(this.methodFeatures);
            if (resolvedIdentifier != null) {
                resolvedIdentifiers.add(resolvedIdentifier);
            }
        }

        return resolvedIdentifiers;
    }
}