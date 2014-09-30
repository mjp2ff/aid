package edu.virginia.aid;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.comparison.AntBuildfileParser;
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
 * A Driver is used to analyze a file or project, parse out the code and comments, and split
 * it up into the different methods contained. The Driver then calls out to
 * individual method analysis tools.
 * 
 * @author Matt Pearson-Beck & Jeff Principe
 *
 */
public class Driver {

    /**
     * Finds and parses each source file in an Ant Java project, returning information
     * for each method
     *
     * @param projectDirectory The directory of the project to load
     * @return A list of methods with feature information in the source files for this project
     */
    public List<MethodFeatures> getMethodsFromAntProject(String projectDirectory) {
        AntBuildfileParser antParser = new AntBuildfileParser(projectDirectory);
        Set<File> sourceFiles = antParser.getSourceFiles();

        // Hold methods made so far
        List<MethodFeatures> methods = new ArrayList<MethodFeatures>();

        for (File sourceFile : sourceFiles) {
            methods.addAll(getMethodsFromFile(sourceFile.getPath()));
        }

        return methods;
    }

	/**
	 * Parses a file into an AST, then gets the methods from the AST.
	 *
     * @param filepath The path to the file containing source code
	 * @return A list of methods with feature information in this file.
	 */
	public List<MethodFeatures> getMethodsFromFile(String filepath) {

        String fileData = readFile(filepath);

		// Create parser handle through Java 1.7
		ASTParser parser = ASTParser.newParser(AST.JLS4);

		// Point to appropriate data read from file.
		parser.setSource(fileData.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		// Parse the file into an AST
		CompilationUnit ast = (CompilationUnit) parser.createAST(null);

        // Get class information
        ClassInformation classInformation = getClassInformation(ast, filepath);

		return handleMethods(classInformation, fileData);
    }

    private ClassInformation getClassInformation(CompilationUnit cu, String filepath) {
        ClassVisitor classVisitor = new ClassVisitor(filepath);
        cu.accept(classVisitor);
        return classVisitor.getClassInformation();
    }

	/**
	 * Handles the methods read in from the file.
	 * 
	 * @param classInformation The class whose methods are to be analyzed
	 */
	private List<MethodFeatures> handleMethods(ClassInformation classInformation, String fileData) {
        List<MethodDeclaration> methods = classInformation.getMethodDeclarations();
		List<MethodFeatures> methodFeaturesList = new ArrayList<MethodFeatures>();

		// Print the content and comments of each method.
		for (MethodDeclaration m : methods) {

			// Print the method name.
            MethodProcessor methodProcessor = new MethodProcessor(m, classInformation.getClassName(), classInformation.getFilepath());

			// Add detector to process comments
            methodProcessor.addFeatureDetector(new CommentDetector(fileData));
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


    /**
	 * @return The text of the specified file.
	 */
	public static String readFile(String filePath) {
		String fileData = "";

		// Read all data from specified file.
        try {
            fileData = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.out.println("Error reading file from path " + filePath);
		}

		return fileData;
	}

    /**
     * Runs tool on input files as specified in the command line parameters. There are two modes, either project or file,
     * set using the -projects or -files flag as shown below.
     *
     * Usage:
     *      java Driver [-projects|-files] project1/file1 project2/file2 ...
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Driver driver = new Driver();

        if (args.length > 0) {
            if (args[0].equals("-files")) {
                for(int i = 1; i < args.length; i++) {
                    // Parse this file to get the appropriate data
                    List<MethodFeatures> methods = driver.getMethodsFromFile(readFile(args[i]));

                    // Get differences for each method and rank them by most different to least different
                    List<MethodDifferences> differences = driver.compareAndRank(methods);

                    System.out.println(differences);
                }
            } else if (args[0].equals("-projects")) {
                for(int i = 1; i < args.length; i++) {
                    // Parse this file to get the appropriate data
                    List<MethodFeatures> methods = driver.getMethodsFromAntProject(args[i]);

                    // Get differences for each method and rank them by most different to least different
                    List<MethodDifferences> differences = driver.compareAndRank(methods);

                    System.out.println(differences);
                }
            }        	
        } else {
        	final String DEFAULT_FILEPATH = "src\\test\\java\\edu\\virginia\\aid\\TestClass.java";
        	// Parse our sample test file to get the appropriate data
        	List<MethodFeatures> methods = driver.getMethodsFromFile(readFile(DEFAULT_FILEPATH));

        	// Get differences for each method and rank them by most different to least different
        	List<MethodDifferences> differences = driver.compareAndRank(methods);

        	System.out.println(differences);
        }
    }
}
