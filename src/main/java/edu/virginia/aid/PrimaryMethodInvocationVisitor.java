package edu.virginia.aid;

import edu.virginia.aid.data.MethodSignature;
import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

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
