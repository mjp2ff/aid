package edu.virginia.aid.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ReturnStatement;

import edu.virginia.aid.data.IdentifierName;
import edu.virginia.aid.data.MethodFeatures;

/**
 * Finds and processes all return statements within an ast
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ReturnVisitor extends ASTVisitor {

    private List<IdentifierName> identifierUses;

    private MethodFeatures methodFeatures;

    /**
     * Creates ReturnVisitor with the given method
     *
     * @param methodFeatures The containing method
     */
    public ReturnVisitor(MethodFeatures methodFeatures) {
        this.methodFeatures = methodFeatures;

        this.identifierUses = new ArrayList<>();
    }

    /**
     * Gets all identifier and field reads contained within a return statement
     *
     * @param node The return statement to process
     * @return false
     */
    @Override
    public boolean visit(ReturnStatement node) {
        VariableUsageVisitor usageVisitor = new VariableUsageVisitor(methodFeatures, false);
        if (node.getExpression() != null) {
            node.getExpression().accept(usageVisitor);
            identifierUses = usageVisitor.getIdentifierUses();
        }
        return false;
    }

    public List<IdentifierName> getIdentifierUses() {
        return identifierUses;
    }
}
