package edu.virginia.aid.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import edu.virginia.aid.data.MethodSignature;

public class PrimaryMethodInvocationVisitor extends ASTVisitor {

    private List<MethodSignature> methodInvocations = new ArrayList<>();
    private int statementCount = 0;

    public List<MethodSignature> getMethodInvocations() {
        return methodInvocations;
    }

    public int getStatementCount() {
        return statementCount;
    }

    public boolean visit(MethodInvocation node) {
        methodInvocations.add(new MethodSignature(node.getName().getIdentifier(), node.arguments().size()));
        return true;
    }

    public boolean visit(AssertStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(BreakStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(ContinueStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(DoStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(EmptyStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(EnhancedForStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(ExpressionStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(ForStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(IfStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(LabeledStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(ReturnStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(SwitchStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(SynchronizedStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(ThrowStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(TryStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(TypeDeclarationStatement node) {
        statementCount++;
        return true;
    }

    public boolean visit(VariableDeclarationStatement node) {
        statementCount++;
        return true;
    }
}
