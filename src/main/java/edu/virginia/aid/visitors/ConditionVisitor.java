package edu.virginia.aid.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;

/**
 * Visitor for getting conditions on a path
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ConditionVisitor extends ASTVisitor {

    /**
     * The next statement in control flow on a path
     */
    private Statement nextStatement;

    /**
     * The Expression for the condition
     */
    private Expression condition = null;

    /**
     * Whether the Expression should be negated to form the true condition
     */
    private boolean negated = true;

    /**
     * Creates and ASTVisitor for identifying the predicate for a condition containing the given statement
     * in a path
     *
     * @param nextStatement The statement that the condition is taken relative to
     */
    public ConditionVisitor(Statement nextStatement) {
        this.nextStatement = nextStatement;
    }

    /**
     * Build a condition from visiting an if statement
     *
     * @param node The IfStatement node to visit
     * @return false
     */
    public boolean visit(IfStatement node) {
        condition = node.getExpression();

        ContainsStatementVisitor visitor = new ContainsStatementVisitor(nextStatement);
        node.getThenStatement().accept(visitor);
        negated = !visitor.containsStatement();

        return false;
    }

    /**
     * Build a condition from visiting a for loop
     *
     * @param node The ForStatement node currently being visited
     * @return false
     */
    public boolean visit(ForStatement node) {
        condition = node.getExpression();

        ContainsStatementVisitor visitor = new ContainsStatementVisitor(nextStatement);
        node.getBody().accept(visitor);
        negated = !visitor.containsStatement();

        return false;
    }

    /**
     * Gets the condition wrapping the statement provided to the visitor (if any)
     *
     * @return The condition wrapping the statement provided to the visitor, or null
     */
    public Expression getCondition() {
        return condition;
    }

    /**
     * Tests whether the Expression returned by getCondition() should be negated to form
     * the actual condition for reaching the statement provided to the visitor
     *
     * @return Whether the Expression returned by getCondition() should be negated
     */
    public boolean isNegated() {
        return negated;
    }
}
