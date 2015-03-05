package edu.virginia.aid.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.virginia.aid.data.IdentifierName;
import edu.virginia.aid.data.IdentifierScope;
import edu.virginia.aid.data.IdentifierType;
import edu.virginia.aid.data.IdentifierUse;
import edu.virginia.aid.data.MethodFeatures;

/**
 * ASTVisitor for finding assignment expressions within a given statement
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class AssignmentVisitor extends ASTVisitor {

    private IdentifierName variable = null;
    private Expression value = null;
    private MethodFeatures method;

    /**
     * Creates an AssignmentVisitor for the given method
     *
     * @param method The method in which to find assignment expressions
     */
    public AssignmentVisitor(MethodFeatures method) {
        this.method = method;
    }

    /**
     * Add an assignment for each Assignment node visited
     *
     * @param node The Assignment ASTNode visited
     * @return true
     */
    public boolean visit(Assignment node) {
        NameVisitor visitor = new NameVisitor(method, true);
        node.getLeftHandSide().accept(visitor);
        if (visitor.getIdentifiers().size() > 0) {
            variable = visitor.getIdentifiers().get(0);
            value = node.getRightHandSide();
        }
        return true;
    }

    /**
     * Add an assignment for each variable declaration
     *
     * @param node The variable declaration ASTNode
     * @return true
     */
    public boolean visit(VariableDeclarationFragment node) {
        variable = new IdentifierName(node.getName().getIdentifier(),
                IdentifierType.VARIABLE,
                IdentifierScope.LOCAL,
                IdentifierUse.WRITE,
                node.getStartPosition(),
                node.getStartPosition() + node.getLength(),
                method.getSourceContext());
        value = node.getInitializer();
        return true;
    }

    /**
     * Tests whether the visitor is currently in an assignment subtree
     *
     * @return Whether the visitor is currently in an assignment subtree
     */
    public boolean isAssignment() {
        return variable != null && value != null;
    }

    /**
     * Gets the variable (lval) from the assignment (if one exists)
     *
     * @return The variable (lval) from the assignment or null if none exists
     */
    public IdentifierName getVariable() {
        return variable;
    }

    /**
     * Gets the value (rhs) of the assignment (if an assignment is present)
     *
     * @return The value (rhs) of the assignment or null if there is no assignment
     */
    public Expression getValue() {
        return value;
    }
}
