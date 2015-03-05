package edu.virginia.aid.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.virginia.aid.data.ClassInformation;
import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.SourceContext;

/**
 * Finds class definitions in an AST
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ClassVisitor extends ASTVisitor {

    /**
     * The path to the file containing this class on disk
     */
    private String filepath;

    /**
     * The contents of the file containing this class
     */
    private final String fileData;

    /**
     * Property object to populate with information about the class found
     */
    private ClassInformation classInformation = null;

    /**
     * Gets the property object populated with information about the class
     *
     * @return The property object populated with information about the class
     */
    public ClassInformation getClassInformation() {
        return classInformation;
    }

    /**
     * Creates a new ASTVisitor for finding a class in the file at the specified
     * filepath and with the given file data
     *
     * @param filepath The location of the file to search on disk
     * @param fileData The contents of the file to search on disk
     */
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
        SourceContext sourceContext = new SourceContext(fileData);

        classInformation = new ClassInformation(node.getName().getIdentifier(), this.filepath, node.getStartPosition(), node.getStartPosition() + node.getLength(), sourceContext);

        // Extract field information
        for (FieldDeclaration field : node.getFields()) {
            for (Object o : field.fragments()) {
                if (o instanceof VariableDeclarationFragment) {
                    VariableDeclarationFragment variable = (VariableDeclarationFragment) o;
                    IdentifierProperties identifier = new IdentifierProperties(variable.getName().getIdentifier(), variable.getStartPosition(), variable.getStartPosition() + variable.getLength(), sourceContext);
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
