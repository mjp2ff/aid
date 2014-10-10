package edu.virginia.aid.visitors;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitor that finds and stores each VariableDeclaration in an ASTNode subtree
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class VariableDeclarationVisitor extends ASTVisitor {

    /**
     * Variable declarations found while traversing the AST
     */
    private List<VariableDeclaration> declarations = new ArrayList<VariableDeclaration>();

    /**
     * Clears out the list of declarations
     */
    public void clearDeclarations() {
        declarations = new ArrayList<>();
    }

    /**
     * Returns the list of declarations that have been found
     *
     * @return List of variable declarations found traversing AST (if it has been traversed)
     */
    public List<VariableDeclaration> getDeclarations() {
        return declarations;
    }

    /**
     * Adds SingleVariableDeclaration to VariableDeclaration list when encountered and stops
     * traversal of its subtree
     *
     * @param node
     * @return false
     */
    @Override
    public boolean visit(SingleVariableDeclaration node) {
        declarations.add(node);
        return false;
    }

    /**
     * Adds VariableDeclarationFragment to VariableDeclaration list when encountered and stops
     * traversal of its subtree
     *
     * @param node
     * @return
     */
    @Override
    public boolean visit(VariableDeclarationFragment node) {
        declarations.add(node);
        return false;
    }
}
