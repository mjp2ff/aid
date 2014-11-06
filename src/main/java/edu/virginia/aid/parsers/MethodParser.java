package edu.virginia.aid.parsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.virginia.aid.Driver;
import org.eclipse.jdt.core.dom.*;

import edu.virginia.aid.MethodProcessor;
import edu.virginia.aid.data.ClassInformation;
import edu.virginia.aid.data.CommentInfo;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.detectors.CommentDetector;
import edu.virginia.aid.detectors.ControlFlowDetector;
import edu.virginia.aid.detectors.IdentifierDetector;
import edu.virginia.aid.detectors.PrimaryActionDetector;
import edu.virginia.aid.detectors.StemmingProcessor;
import edu.virginia.aid.detectors.StoplistProcessor;
import edu.virginia.aid.visitors.ClassVisitor;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instances;

public abstract class MethodParser {

    private Classifier primaryActionClassifier;
    private Attribute primaryActionClassAttribute;

    /**
     * Returns the primaryAction classifier for the current instance, instantiating it if necessary
     *
     * @return classifier
     */
    public Classifier getPrimaryActionClassifier() {
        if (primaryActionClassifier == null) {
            initializeClassifier();
        }

        return primaryActionClassifier;
    }

    public Attribute getPrimaryAcitonClassAttribute() {
        if (primaryActionClassAttribute == null) {
            initializeClassifier();
        }

        return primaryActionClassAttribute;
    }

    /**
     * Initializes classifier and class attribute values. This should only be called once per method parser
     */
    private void initializeClassifier() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Driver.CLASSIFICATION_TRAINING_SET_FILEPATH));
            Instances trainingData = new Instances(reader);
            trainingData.setClassIndex(trainingData.numAttributes() - 1);

            primaryActionClassAttribute = trainingData.classAttribute();

            primaryActionClassifier = new NaiveBayes();
            primaryActionClassifier.buildClassifier(trainingData);
        } catch (IOException e) {
            throw new RuntimeException("Could not find training set for primary action. "
                    + "Please build training set with -train mode before running the classifier. "
                    + "The training set should be stored in " + Driver.CLASSIFICATION_TRAINING_SET_FILEPATH);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<MethodFeatures> parseMethods() {
        return parseMethods(false);
    }

    /**
     * Parses information from the methods given to the parser.
     *
     * @param trainingMode Whether the methods to parse represent
     * @return
     */
    protected abstract List<MethodFeatures> parseMethods(boolean trainingMode);

    /**
	 * Parses a file into an AST, then gets the methods from the AST.
	 *
     * @param filepath The path to the file containing source code
	 * @return A list of methods with feature information in this file.
	 */
	protected List<MethodFeatures> getMethodsFromFile(String filepath, boolean trainingMode) {

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

		return handleMethods(classInformation, trainingMode);
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

	/**
	 * Get the class information from the CompilationUnit
	 * 
	 * @param cu The compilation unit for this class
	 * @param filepath The full path to the file containing this class
	 * @param fileData The data from the file containing this class
	 * @return Appropriate information for this class.
	 */
    protected ClassInformation getClassInformation(CompilationUnit cu, String filepath, final String fileData) {
        ClassVisitor classVisitor = new ClassVisitor(filepath, fileData);
        cu.accept(classVisitor);
        ClassInformation classInformation = classVisitor.getClassInformation();

        // After getting basic information, process comments.
        processComments(cu, classInformation);

        return classInformation;
    }
    
    /**
     * Processes the comments from the class information. Must be done here because we need the compilation unit.
     * 
     * @param cu The compilation unit for this class
     * @param classInformation The modified class information with the comments added.
     */
    private void processComments(CompilationUnit cu, ClassInformation classInformation) {
        if (classInformation != null) {
            @SuppressWarnings("unchecked")
            List<Comment> comments = (List<Comment>) cu.getCommentList();
            for (Comment comment : comments) {
                int startPos = comment.getStartPosition();
                int endPos = startPos + comment.getLength();
                classInformation.addComment(new CommentInfo(startPos, endPos, classInformation.getSourceContext()));
            }
        }
    }

	/**
	 * Handles the methods read in from the file.
	 *
	 * @param classInformation The class whose methods are to be analyzed
	 */
	private List<MethodFeatures> handleMethods(ClassInformation classInformation, boolean trainingMode) {

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
                // Add detector to process control flow
                methodProcessor.addFeatureDetector(new ControlFlowDetector());
                // Add detector to reduce words to stems.
                methodProcessor.addFeatureDetector(new StemmingProcessor());
                // Add detector to remove words in stoplist. Stoplist should be LAST! so words aren't re-added in.
                methodProcessor.addFeatureDetector(new StoplistProcessor());

                if (!trainingMode) {
                    // Add detector to parse out the information for primary action of the method
                    methodProcessor.addFeatureDetector(new PrimaryActionDetector(getPrimaryActionClassifier(), getPrimaryAcitonClassAttribute()));
                }

                // Run all detectors
                MethodFeatures methodFeatures = methodProcessor.runDetectors();

                methodFeaturesList.add(methodFeatures);
            }
        }

        return methodFeaturesList;
    }

    /**
     * Builds a training set for a given property from labeled methods in the parsed set
     *
     * @param labeledProperty The property to search for labeled instances of
     * @return Map of methods to their labels
     */
    public Map<String, List<MethodFeatures>> createTrainingSet(String labeledProperty) {
        List<MethodFeatures> methods = parseMethods(true);
        Map<String, List<MethodFeatures>> labeledMethods = new HashMap<>();
        for (MethodFeatures method : methods) {
            if (method.getJavadoc() != null) {
                List<TagElement> tags = ((List<TagElement>) method.getJavadoc().tags()).stream()
                        .filter(tag -> (tag.getTagName() == null ? "" : tag.getTagName()).equals("@" + labeledProperty))
                        .collect(Collectors.toList());
                if (tags.size() > 0 && tags.get(0).fragments().size() > 0) {
                    String label = tags.get(0).fragments().get(0).toString().trim();
                    if (labeledMethods.containsKey(label)) {
                        labeledMethods.get(label).add(method);
                    } else {
                        List<MethodFeatures> labelMethods = new ArrayList<>();
                        labelMethods.add(method);
                        labeledMethods.put(label, labelMethods);
                    }
                }
            }
        }

        return labeledMethods;
    }
}
