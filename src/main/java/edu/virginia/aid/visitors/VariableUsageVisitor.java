package edu.virginia.aid.visitors;

import edu.virginia.aid.data.IdentifierName;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.data.MethodInvocationProperties;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;

import java.util.*;

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

    public List<IdentifierName> getIdentifierUses() {
        List<IdentifierName> allIdentifierUses = new ArrayList<>();
        allIdentifierUses.addAll(identifierUses);
        allIdentifierUses.addAll(identifiers);
        return allIdentifierUses;
    }

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
     * Find all reads and writes of variables within an assignment
     *
     * @param node
     * @return
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
        visitor.clearNames();
        rhs.accept(visitor);
        identifierUses.addAll(visitor.getIdentifiers());
        methodInvocations.addAll(visitor.getMethods());

        boolean tempInAssignment = inAssignment;
        inAssignment = true;
        rhs.accept(this);
        inAssignment = tempInAssignment;

        return false;
    }
}