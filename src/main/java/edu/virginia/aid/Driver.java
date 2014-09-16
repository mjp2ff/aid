package edu.virginia.aid;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/*
 * A edu.virginia.aid.Driver is used to analyze a file, parse out the code and comments, and
 * split it up into the different methods contained. The edu.virginia.aid.Driver then calls
 * out to individual method analysis tools.
 */
public class Driver {

	private String fileData;

	/**
	 * 
	 * @param fileData The text of the file to be analyzed.
	 */
	public Driver(String fileData) {
		this.fileData = fileData;
	}

	/**
	 * @param fileData
	 *            The text of the file to be analyzed.
	 * @return A list of method declarations in this file.
	 */
	public List<MethodDeclaration> parseFile() {

		// Create parser handle through Java 1.7
		ASTParser parser = ASTParser.newParser(AST.JLS4);

		// Point to appropriate data read from file.
		parser.setSource(this.fileData.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		// Parse the file into an AST
		CompilationUnit ast = (CompilationUnit) parser.createAST(null);
		return this.findMethods(ast);
	}

	private List<MethodDeclaration> findMethods(CompilationUnit cu) {
		MethodVisitor mv = new MethodVisitor();
		mv.clearMethods();
		cu.accept(mv);
		return mv.getMethods();
	}

	/**
	 * Handles the methods read in from the file.
	 * 
	 * @param methods
	 *            The methods to be analyzed.
	 */
	public void handleMethods(List<MethodDeclaration> methods) {
		// Print the content and comments of each method.
		for (int i = 0; i < methods.size(); ++i) {
			MethodDeclaration m = methods.get(i);

			// Print the method itself.
			System.out.println("Method " + i + ":");
			System.out.println(m);

			// Read each comment data.
			System.out.println("Comments for method " + i + ":");
			System.out.println(this.readComment(m));
		}
	}

	/**
	 * @param method
	 *            The method whose comments we're reading.
	 * @return The comment, as a string.
	 */
	public String readComment(MethodDeclaration method) {
		Comment c = method.getJavadoc();
		int start = c.getStartPosition();
		int length = c.getLength();
		String cString = this.fileData.substring(start, start + length);
		return cString;
	}
}
