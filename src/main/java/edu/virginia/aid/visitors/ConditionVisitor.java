package edu.virginia.aid.visitors;

import edu.virginia.aid.symex.Path;
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
    private boolean containsNextStatement;

    public ConditionVisitor(Statement nextStatement) {
        this.nextStatement = nextStatement;
    }

    public boolean visit(IfStatement node) {
        if (condition == null) {
            condition = node.getExpression();
        }

        return matchNode(node);
    }

    public boolean visit(ForStatement node) {
        if (condition == null) {
            condition = node.getExpression();
        }

        return matchNode(node);
    }

    public boolean visit(AssertStatement node) {
        return matchNode(node);
    }

    public boolean visit(Block node) {
        return matchNode(node);
    }

    public boolean visit(BreakStatement node) {
        return matchNode(node);
    }

    public boolean visit(ConstructorInvocation node) {
        return matchNode(node);
    }

    public boolean visit(ContinueStatement node) {
        return matchNode(node);
    }

    public boolean visit(DoStatement node) {
        return matchNode(node);
    }

    public boolean visit(EmptyStatement node) {
        return matchNode(node);
    }

    public boolean visit(EnhancedForStatement node) {
        return matchNode(node);
    }

    public boolean visit(ExpressionStatement node) {
        return matchNode(node);
    }

    public boolean visit(LabeledStatement node) {
        return matchNode(node);
    }

    public boolean visit(ReturnStatement node) {
        return matchNode(node);
    }

    public boolean visit(SuperConstructorInvocation node) {
        return matchNode(node);
    }

    public boolean visit(SwitchCase node) {
        return matchNode(node);
    }

    public boolean visit(SwitchStatement node) {
        return matchNode(node);
    }

    public boolean visit(SynchronizedStatement node) {
        return matchNode(node);
    }

    public boolean visit(ThrowStatement node) {
        return matchNode(node);
    }

    public boolean visit(TryStatement node) {
        return matchNode(node);
    }

    public boolean visit(TypeDeclarationStatement node) {
        return matchNode(node);
    }

    public boolean visit(VariableDeclarationStatement node) {
        return matchNode(node);
    }

    public boolean visit(WhileStatement node) {
        return matchNode(node);
    }

    private boolean matchNode(Statement node) {
        if (node == nextStatement) {
            negated = false;
            return false;
        }

        return true;
    }

    public Expression getCondition() {
        return condition;
    }

    public boolean isNegated() {
        return negated;
    }
}
