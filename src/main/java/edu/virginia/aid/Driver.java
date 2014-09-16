package edu.virginia.aid;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/*
 * A edu.virginia.aid.Driver is used to analyze a file, parse out the code and comments, and
 * split it up into the different methods contained. The edu.virginia.aid.Driver then calls
 * out to individual method analysis tools.
 */
public class Driver {

	// String representing the data in a file to analyze.
	private String fileData;

	// Initialize a driver to a specific file.
	public Driver(String fileData) {
		this.fileData = fileData;
	}

	public CompilationUnit parseFile() {

		// Create parser handle through Java 1.7
		ASTParser parser = ASTParser.newParser(AST.JLS4);

		// Point to appropriate data read from file.
		parser.setSource(this.fileData.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		// Return the created AST created from this file.
		return (CompilationUnit) parser.createAST(null);
	}
}
