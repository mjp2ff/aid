package edu.virginia.aid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.comparison.MethodDifferences;
import edu.virginia.aid.data.ClassInformation;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.detectors.CommentDetector;
import edu.virginia.aid.detectors.IdentifierDetector;
import edu.virginia.aid.detectors.ParameterDetector;
import edu.virginia.aid.detectors.StemmingProcessor;
import edu.virginia.aid.detectors.StoplistProcessor;
import edu.virginia.aid.visitors.ClassVisitor;

/**
 * A Driver is used to analyze a file, parse out the code and comments, and split
 * it up into the different methods contained. The Driver then calls out to
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
	 * @return A list of methods with feature information in this file.
	 */
	public List<MethodFeatures> getMethodsFromFile() {

		// Create parser handle through Java 1.7
		ASTParser parser = ASTParser.newParser(AST.JLS4);

		// Point to appropriate data read from file.
		parser.setSource(this.fileData.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		// Parse the file into an AST
		CompilationUnit ast = (CompilationUnit) parser.createAST(null);

        // Get class information
        ClassInformation classInformation = getClassInformation(ast);
        System.out.println(classInformation);

		return handleMethods(classInformation);
    }

    private ClassInformation getClassInformation(CompilationUnit cu) {
        ClassVisitor classVisitor = new ClassVisitor();
        cu.accept(classVisitor);
        return classVisitor.getClassInformation();
    }

	/**
	 * Handles the methods read in from the file.
	 * 
	 * @param classInformation The class whose methods are to be analyzed
	 */
	private List<MethodFeatures> handleMethods(ClassInformation classInformation) {
        List<MethodDeclaration> methods = classInformation.getMethodDeclarations();
		List<MethodFeatures> methodFeaturesList = new ArrayList<MethodFeatures>();

		// Print the content and comments of each method.
		for (int i = 0; i < methods.size(); ++i) {
			MethodDeclaration m = methods.get(i);

			// Print the method name.
			System.out.println("Method " + i);
            MethodProcessor methodProcessor = new MethodProcessor(m);

			// Add detector to process comments
            methodProcessor.addFeatureDetector(new CommentDetector(this.fileData));
            // Add detector to process methods
            methodProcessor.addFeatureDetector(new IdentifierDetector());
            // Add detector to process parameters
            methodProcessor.addFeatureDetector(new ParameterDetector());
            // Add detector to remove words in stoplist.
            methodProcessor.addFeatureDetector(new StoplistProcessor());
            // Add detector to reduce words to stems.
            methodProcessor.addFeatureDetector(new StemmingProcessor());
            // Run all detectors
            MethodFeatures methodFeatures = methodProcessor.runDetectors();
            System.out.println("Processed method : " + methodFeatures.getMethodName());
            System.out.println("Identifiers: " + methodFeatures.getIdentifierNames());

            System.out.println("----------------");

            System.out.println(methodFeatures);
            methodFeaturesList.add(methodFeatures);
		}

        return methodFeaturesList;
	}

    /**
     * Performs comparison check on each method and sorts them from most to least different
     *
     * @param methodFeaturesList Feature information for each method
     * @return Sorted list of differences for each method
     */
    public List<MethodDifferences> compareAndRank(List<MethodFeatures> methodFeaturesList) {
        List<MethodDifferences> differences = new ArrayList<MethodDifferences>();

        for (MethodFeatures methodFeatures : methodFeaturesList) {
            differences.add(methodFeatures.getDifferences());
        }

        Collections.sort(differences);

        return differences;
    }
}
