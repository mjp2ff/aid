package edu.virginia.aid.parsers;

import edu.virginia.aid.MethodProcessor;
import edu.virginia.aid.data.ClassInformation;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.detectors.*;
import edu.virginia.aid.visitors.ClassVisitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class MethodParser {

    public abstract List<MethodFeatures> parseMethods();

    /**
	 * Parses a file into an AST, then gets the methods from the AST.
	 *
     * @param filepath The path to the file containing source code
	 * @return A list of methods with feature information in this file.
	 */
	protected List<MethodFeatures> getMethodsFromFile(String filepath) {

        String fileData = readFile(filepath);

		// Create parser handle through Java 1.7
		ASTParser parser = ASTParser.newParser(AST.JLS4);

		// Point to appropriate data read from file.
		parser.setSource(fileData.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		// Parse the file into an AST
		CompilationUnit ast = (CompilationUnit) parser.createAST(null);

        // Get class information
        ClassInformation classInformation = getClassInformation(ast, filepath, fileData);

		return handleMethods(classInformation);
    }

    /**
	 * @return The text of the specified file.
	 */
	protected static String readFile(String filePath) {
		String fileData = "";

		// Read all data from specified file.
        try {
            fileData = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.out.println("Error reading file from path " + filePath);
		}

		return fileData;
	}

    protected ClassInformation getClassInformation(CompilationUnit cu, String filepath, final String fileData) {
        ClassVisitor classVisitor = new ClassVisitor(filepath, fileData);
        cu.accept(classVisitor);
        return classVisitor.getClassInformation();
    }

	/**
	 * Handles the methods read in from the file.
	 *
	 * @param classInformation The class whose methods are to be analyzed
	 */
	private List<MethodFeatures> handleMethods(ClassInformation classInformation) {

        List<MethodFeatures> methodFeaturesList = new ArrayList<>();

        if (classInformation != null) {
            List<MethodDeclaration> methods = classInformation.getMethodDeclarations();

            // Print the content and comments of each method.
            for (MethodDeclaration m : methods) {

                // Print the method name.
                MethodProcessor methodProcessor = new MethodProcessor(m, classInformation, classInformation.getFilepath());

                // Add detector to process comments
                methodProcessor.addFeatureDetector(new CommentDetector());
                // Add detector to process methods
                methodProcessor.addFeatureDetector(new IdentifierDetector());
                // Add detector to process parameters
                methodProcessor.addFeatureDetector(new ParameterDetector());
                // Add detector to reduce words to stems.
                methodProcessor.addFeatureDetector(new StemmingProcessor());
                // Add detector to remove words in stoplist. Stoplist should be LAST! so words aren't re-added in.
                methodProcessor.addFeatureDetector(new StoplistProcessor());
                // Run all detectors
                MethodFeatures methodFeatures = methodProcessor.runDetectors();
                methodFeaturesList.add(methodFeatures);
            }
        }

        return methodFeaturesList;
    }
}
