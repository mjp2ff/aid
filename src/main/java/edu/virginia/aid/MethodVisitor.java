package edu.virginia.aid;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class MethodVisitor extends ASTVisitor {

    private List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

    public void clearMethods() {
        methods = new ArrayList<MethodDeclaration>();
    }

    public List<MethodDeclaration> getMethods() {
        return methods;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        methods.add(node);
        return false;
    }
}
