package edu.virginia.aid.visitors;

import edu.virginia.aid.data.ClassInformation;
import edu.virginia.aid.data.IdentifierProperties;

import org.eclipse.jdt.core.dom.*;

/**
 * Finds class definitions in an AST
 */
public class ClassVisitor extends ASTVisitor {

    private ClassInformation classInformation = null;
    public ClassInformation getClassInformation() {
        return classInformation;
    }

    /**
     * Finds the class declaration and related information
     *
     * @param node Class declaration node
     * @return false
     */
    @Override
    public boolean visit(TypeDeclaration node) {
        classInformation = new ClassInformation(node.getName().getIdentifier());

        // Extract field information
        for (FieldDeclaration field : node.getFields()) {
            for (Object o : field.fragments()) {
                if (o instanceof VariableDeclarationFragment) {
                    IdentifierProperties identifier = new IdentifierProperties(((VariableDeclarationFragment) o).getName().getIdentifier());
                    identifier.setContext(IdentifierProperties.IdentifierContext.FIELD);
                    classInformation.addField(identifier);
                    break;
                }
            }
        }

        // Extract method decalration information
        for (MethodDeclaration methodDeclaration : node.getMethods()) {
            classInformation.addMethodDeclaration(methodDeclaration);
        }

        return false;
    }
}
