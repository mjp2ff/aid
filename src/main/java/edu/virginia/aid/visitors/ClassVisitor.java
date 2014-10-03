package edu.virginia.aid.visitors;

import edu.virginia.aid.data.ClassInformation;
import edu.virginia.aid.data.IdentifierProperties;

import org.eclipse.jdt.core.dom.*;

/**
 * Finds class definitions in an AST
 */
public class ClassVisitor extends ASTVisitor {

    private String filepath;
    private final String fileData;
    private ClassInformation classInformation = null;
    public ClassInformation getClassInformation() {
        return classInformation;
    }

    public ClassVisitor(String filepath, final String fileData) {
        this.filepath = filepath;
        this.fileData = fileData;
    }

    /**
     * Finds the class declaration and related information
     *
     * @param node Class declaration node
     * @return false
     */
    @Override
    public boolean visit(TypeDeclaration node) {
        classInformation = new ClassInformation(node.getName().getIdentifier(), this.filepath, node.getStartPosition(), node.getStartPosition() + node.getLength(), fileData);

        // Extract field information
        for (FieldDeclaration field : node.getFields()) {
            for (Object o : field.fragments()) {
                if (o instanceof VariableDeclarationFragment) {
                    VariableDeclarationFragment variable = (VariableDeclarationFragment) o;
                    IdentifierProperties identifier = new IdentifierProperties(variable.getName().getIdentifier(), variable.getStartPosition(), variable.getStartPosition() + variable.getLength(), fileData);
                    identifier.setContext(IdentifierProperties.IdentifierContext.FIELD);
                    classInformation.addField(identifier);
                    break;
                }
            }
        }

        // Extract method declaration information
        for (MethodDeclaration methodDeclaration : node.getMethods()) {
            classInformation.addMethodDeclaration(methodDeclaration);
        }

        return false;
    }
}
