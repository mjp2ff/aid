package edu.virginia.aid.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;

/**
 * Visitor that finds and stores each comment in an ASTNode subtree
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class CommentVisitor extends ASTVisitor {

	/**
	 * Comments found while traversing the AST
	 */
	private List<Comment> comments = new ArrayList<Comment>();

	/**
	 * Clears out the list of comments
	 */
	public void clearComments() {
		this.comments = new ArrayList<Comment>();
	}

	/**
	 * Returns the list of comments that have been found
	 *
	 * @return List of comments found traversing AST (if it has been traversed)
	 */
	public List<Comment> getComments() {
		return this.comments;
	}

	/**
	 * Adds Javadoc to Comment list when encountered and stops traversal of its subtree
	 *
	 * @param node
	 * @return
	 */
	@Override
	public boolean visit(Javadoc node) {
		this.comments.add(node);
		return false;
	}

	/**
	 * Adds BlockComment to Comment list when encountered and stops traversal of its subtree
	 *
	 * @param node
	 * @return false
	 */
	@Override
	public boolean visit(BlockComment node) {
		this.comments.add(node);
		return false;
	}

	/**
	 * Adds LineComment to Comment list when encountered and stops traversal of its subtree
	 *
	 * @param node
	 * @return
	 */
	@Override
	public boolean visit(LineComment node) {
		this.comments.add(node);
		return false;
	}
}
