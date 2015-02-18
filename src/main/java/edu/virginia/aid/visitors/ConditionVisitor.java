package edu.virginia.aid.visitors;

import org.eclipse.jdt.core.dom.*;

/**
 * Visitor for getting conditions on a path
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ConditionVisitor extends ASTVisitor {

    private Statement nextStatement;
    private Expression condition = null;
    private boolean negated = true;

    public ConditionVisitor(Statement nextStatement) {
        this.nextStatement = nextStatement;
    }

    public boolean visit(IfStatement node) {
        condition = node.getExpression();

        ContainsStatementVisitor visitor = new ContainsStatementVisitor(nextStatement);
        node.getThenStatement().accept(visitor);
        negated = !visitor.containsStatement();

        return false;
    }

    public boolean visit(ForStatement node) {
        condition = node.getExpression();

        ContainsStatementVisitor visitor = new ContainsStatementVisitor(nextStatement);
        node.getBody().accept(visitor);
        negated = !visitor.containsStatement();

        return false;
    }

    public Expression getCondition() {
        return condition;
    }

    public boolean isNegated() {
        return negated;
    }
}
