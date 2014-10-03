package edu.virginia.aid;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

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

        System.out.print("Processed 0 methods");
        for (File sourceFile : sourceFiles) {
            methods.addAll(getMethodsFromFile(sourceFile.getPath()));
            System.out.print("\rProcessed " + methods.size() + " methods");
        }

        System.out.println();

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
            MethodProcessor methodProcessor = new MethodProcessor(m, classInformation, classInformation.getFilepath());

			// Add detector to process comments
            methodProcessor.addFeatureDetector(new CommentDetector(fileData));
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
     * Prompts the user for input until the pattern passed is satisfied
     *
     * @param prompt The prompt for the user to respond to
     * @param inputPattern The regular expression user responses need to match
     * @param keyboard Scanner to get input from
     * @return
     */
    public static String promptUser(String prompt, Pattern inputPattern, Scanner keyboard) {
        String userInput;
        do {
            System.out.print(prompt);
            userInput = keyboard.nextLine();
        } while (!inputPattern.matcher(userInput).matches());
        return userInput;
    }

    /**
     * Provides interactive interface for displaying method differences given a list of differences to display
     *
     * @param rankedDifferences The differences to display
     * @param methodsPerPage The number of methods to show per page
     * @param keyboard Scanner for collecting user input
     */
    public static void displayDifferences(List<MethodDifferences> rankedDifferences, int methodsPerPage, Scanner keyboard) {
        int displayIndex = 0;

        while (displayIndex < rankedDifferences.size()) {
            for (int i = 0; i < methodsPerPage && displayIndex < rankedDifferences.size(); i++, displayIndex++) {
                System.out.println(rankedDifferences.get(displayIndex));
            }

            System.out.print("\n" + (displayIndex) + "/" + rankedDifferences.size() + " displayed.");
            if (displayIndex < rankedDifferences.size()) {
                System.out.print(" Display next " + Math.min(methodsPerPage, rankedDifferences.size() - displayIndex) + " methods? (y/n): ");
            }
            if (!keyboard.nextLine().equalsIgnoreCase("y")) break;
        }
    }

    /**
     * Runs tool on input files as specified in the command line parameters. There are two modes, either project or file,
     * set using the -projects or -files flag as shown below.
     *
     * Usage:
     *      java Driver [-projects|-files] [project1/file1 project2/file2 ...]
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Driver driver = new Driver();

        if (args.length > 0) {
            if (args[0].equals("-files")) {
                for(int i = 1; i < args.length; i++) {
                    // Parse this file to get the appropriate data
                    List<MethodFeatures> methods = driver.getMethodsFromFile(args[i]);

                    // Get differences for each method and rank them by most different to least different
                    List<MethodDifferences> differences = driver.compareAndRank(methods);

                    displayDifferences(differences, 10, new Scanner(System.in));
                }
            } else if (args[0].equals("-projects")) {
                for(int i = 1; i < args.length; i++) {
                    // Parse this file to get the appropriate data
                    List<MethodFeatures> methods = driver.getMethodsFromAntProject(args[i]);

                    // Get differences for each method and rank them by most different to least different
                    List<MethodDifferences> differences = driver.compareAndRank(methods);

                    displayDifferences(differences, 10, new Scanner(System.in));
                }
            }        	
        } else {
            Scanner keyboard = new Scanner(System.in);

            String mode = promptUser("Would you like to load a project or a file? (p/f): ", Pattern.compile("p|f"), keyboard);

            // Project mode
            if (mode.equalsIgnoreCase("p")) {
                System.out.print("Provide the path to the project to analyze (q to exit): ");
                String projectPath = keyboard.nextLine();

                if (!projectPath.equalsIgnoreCase("q")) {
                    try {
                        List<MethodFeatures> methods = driver.getMethodsFromAntProject(projectPath);

                        String analysisMode = promptUser("Would you like to analyze methods from a class, file or the entire project? (c/f/p): ", Pattern.compile("c|f|p"), keyboard);

                        if (analysisMode.equalsIgnoreCase("c")) {
                            System.out.print("Please specify class name: ");
                            String className = keyboard.nextLine();

                            List<MethodFeatures> classMethods = new ArrayList<>();
                            for (MethodFeatures method : methods) {
                                if (method.getParentClass().getClassName().equals(className)) {
                                    classMethods.add(method);
                                }
                            }

                            displayDifferences(driver.compareAndRank(classMethods), 10, keyboard);
                        } else if (analysisMode.equalsIgnoreCase("f")) {
                            System.out.print("Please specify the file path: ");
                            String filePath = keyboard.nextLine();

                            List<MethodFeatures> fileMethods = new ArrayList<>();
                            for (MethodFeatures method : methods) {
                                if (method.getFilepath().equals(filePath)) {
                                    fileMethods.add(method);
                                }
                            }

                            displayDifferences(driver.compareAndRank(fileMethods), 10, keyboard);
                        } else if (analysisMode.equalsIgnoreCase("p")) {
                            displayDifferences(driver.compareAndRank(methods), 10, keyboard);
                        }

                        System.out.println();
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                    }
                }
            } else if (mode.equalsIgnoreCase("f")) {
                String filePath;
                do {
                    System.out.print("Provide the path to the file to analyze (q to exit): ");
                    filePath = keyboard.nextLine();

                    if (!filePath.equalsIgnoreCase("q")) {
                        try {
                            List<MethodFeatures> methods = driver.getMethodsFromFile(filePath);
                            List<MethodDifferences> differences = driver.compareAndRank(methods);
                            displayDifferences(differences, 10, keyboard);
                        } catch (Exception e) {
                            System.out.println("Error: " + e);
                        }
                    }

                } while (!filePath.equalsIgnoreCase("q"));
            }
        }
    }
}
