package edu.virginia.aid;

import java.util.List;

import edu.virginia.aid.detectors.CommentDetector;
import edu.virginia.aid.detectors.IdentifierDetector;
import edu.virginia.aid.visitors.MethodVisitor;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * A edu.virginia.aid.Driver is used to analyze a file, parse out the code and comments, and split
 * it up into the different methods contained. The edu.virginia.aid.Driver then calls out to
 * individual method analysis tools.
 * 
 * @author Matt Pearson-Beck & Jeff Principe
 *
 */
public class Driver {

	/**
	 * Data read in from a file.
	 */
	private String fileData;

	/**
	 * Creates a new Driver based on some file's data.
	 * 
	 * @param fileData
	 *            The text of the file to be analyzed.
	 */
	public Driver(String fileData) {
		this.fileData = fileData;
	}

	/**
	 * Parses a file into an AST, then gets the methods from the AST.
	 * 
	 * @return A list of method declarations in this file.
	 */
	public List<MethodDeclaration> getMethodsFromFile() {

		// Create parser handle through Java 1.7
		ASTParser parser = ASTParser.newParser(AST.JLS4);

		// Point to appropriate data read from file.
		parser.setSource(this.fileData.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		// Parse the file into an AST
		CompilationUnit ast = (CompilationUnit) parser.createAST(null);
		return this.findMethods(ast);
	}

	/**
	 * Finds the methods inside a parsed AST for a file.
	 * 
	 * @param cu
	 *            the AST for a Java file.
	 * @return A list of methods contained in that file.
	 */
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

			// Print the method name.
			System.out.println("Method " + i + ": " + m.getName());
            MethodProcessor processor = new MethodProcessor(m);

			// Add detector to process comments.
            processor.addFeatureDetector(new CommentDetector(this.fileData));
            // Add detector to process methods.
            processor.addFeatureDetector(new IdentifierDetector());

            // Run all detectors
            processor.runDetectors();

            // Separator between methods.
            System.out.println("----------------");
		}
	}
}