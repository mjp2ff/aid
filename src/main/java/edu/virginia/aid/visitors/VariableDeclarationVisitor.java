package edu.virginia.aid.visitors;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * Field variable usages found while traversing the AST
     */
    private Set<String> fieldUsages = new HashSet<>();

    /**
     * Clears out the list of declarations
     */
    public void clearDeclarations() {
        declarations = new ArrayList<>();
        fieldUsages = new HashSet<>();
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
     * Returns the list of fields used in a method
     *
     * @return List of used field names
     */
    public Set<String> getFieldUsages() {
        return fieldUsages;
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

    /**
     * Adds each accessed field names to the set of those accessed
     *
     * @param node
     * @return false
     */
    @Override
    public boolean visit(FieldAccess node) {
        fieldUsages.add(node.getName().getIdentifier());
        return false;
    }
}
