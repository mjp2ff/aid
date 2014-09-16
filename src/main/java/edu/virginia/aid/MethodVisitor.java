package edu.virginia.aid;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * A edu.virginia.aid.MethodVisitor is used to manage a list of methods.
 * 
 * @author Matt Pearson-Beck & Jeff Principe
 *
 */
public class MethodVisitor extends ASTVisitor {

	/**
	 * A list of methods to be visited.
	 */
	private List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

	/**
	 * Clears the methods from the list.
	 */
	public void clearMethods() {
		methods = new ArrayList<MethodDeclaration>();
	}

	/**
	 * Getter for methods.
	 * 
	 * @return the methods
	 */
	public List<MethodDeclaration> getMethods() {
		return methods;
	}

	/**
	 * Adds a specified method to the list.
	 * 
	 * @param node
	 *            The method to add.
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		methods.add(node);
		return false;
	}
}
