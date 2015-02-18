package edu.virginia.aid.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * ASTVisitor to determine if an AST subtree contains a provided statement
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ContainsStatementVisitor extends ASTVisitor {

    private Statement statement;
    private boolean containsStatement = false;

    public ContainsStatementVisitor(Statement statement) {
        this.statement = statement;
    }

    public boolean containsStatement() {
        return containsStatement;
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
        if (node == statement) {
            containsStatement = true;
            return false;
        }

        return true;
    }

}
