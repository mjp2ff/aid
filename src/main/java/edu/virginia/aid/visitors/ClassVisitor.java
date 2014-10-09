package edu.virginia.aid.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.virginia.aid.data.ClassInformation;
import edu.virginia.aid.data.CommentInfo;
import edu.virginia.aid.data.IdentifierProperties;

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

        return true;
    }

	/**
	 * Adds BlockComment to comments for class information and stops traversal of its subtree
	 *
	 * @param node
	 * @return false
	 */
	@Override
	public boolean visit(BlockComment node) {
		int startPos = node.getStartPosition();
		int endPos = startPos + node.getLength();
        classInformation.addComment(new CommentInfo(startPos, endPos, classInformation.getSourceContext()));
        return false;
	}

	/**
	 * Adds LineComment to comments for class information and stops traversal of its subtree
	 *
	 * @param node
	 * @return
	 */
	@Override
	public boolean visit(LineComment node) {
		int startPos = node.getStartPosition();
		int endPos = startPos + node.getLength();
        classInformation.addComment(new CommentInfo(startPos, endPos, classInformation.getSourceContext()));
        return false;
	}
}
