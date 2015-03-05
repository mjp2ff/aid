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

    /**
     * The statement to search for
     */
    private Statement statement;

    /**
     * Whether or not the ASTNode contains the search statement
     */
    private boolean containsStatement = false;

    /**
     * Creates an ASTVisitor for determining whether an ASTNode subtree contains the given statement
     *
     * @param statement The statement to search for in the subtree
     */
    public ContainsStatementVisitor(Statement statement) {
        this.statement = statement;
    }

    /**
     * Tests whether the ASTNode subtree contains the provided statement
     *
     * @return Whether the visited subtree contains the provided statement
     */
    public boolean containsStatement() {
        return containsStatement;
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(AssertStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(Block node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(BreakStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(ConstructorInvocation node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(ContinueStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(DoStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(EmptyStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(EnhancedForStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(ExpressionStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(LabeledStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(ReturnStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(SuperConstructorInvocation node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(SwitchCase node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(SwitchStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(SynchronizedStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(ThrowStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(TryStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(TypeDeclarationStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(VariableDeclarationStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the given statement node matches the statement provided
     *
     * @param node The node to check against the statement
     * @return False if the statement is found, otherwise true (keep searching)
     */
    public boolean visit(WhileStatement node) {
        return matchNode(node);
    }

    /**
     * Checks if the provided node matches the statement that is being searched for using direct
     * name equivalence.
     *
     * @param node The statement to search for
     * @return False if the statements match, otherwise true (keep searching)
     */
    private boolean matchNode(Statement node) {
        if (node == statement) {
            containsStatement = true;
            return false;
        }

        return true;
    }

}
