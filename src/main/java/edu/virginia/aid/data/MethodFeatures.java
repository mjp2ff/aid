package edu.virginia.aid.data;

import edu.virginia.aid.comparison.Difference;
import edu.virginia.aid.comparison.MethodDifferences;

import java.util.*;

/**
 * Data wrapper for a feature list for a single method
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class MethodFeatures extends SourceElement {

    private String filepath;
    private ClassInformation parentClass;
    private String methodName;
    private Map<String, Boolean> booleanFeatures;
    private List<IdentifierProperties> parameters;
    private List<IdentifierProperties> localVariables;
    private List<IdentifierProperties> fields;
    private List<CommentInfo> comments;
    private String javadoc;

    public MethodFeatures(String methodName, ClassInformation parentClass, String filepath, int startPos, int endPos, final String sourceContext) {
        super(startPos, endPos, sourceContext);

        this.methodName = methodName;
        this.parentClass = parentClass;
        this.filepath = filepath;
        this.booleanFeatures = new HashMap<>();
        this.parameters = new ArrayList<>();
        this.localVariables = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.javadoc = "";
    }

    /**
     * Gets and returns the name of the method
     *
     * @return Method name
     */
    public String getMethodName() {
        return this.methodName;
    }

    public String getFilepath() {
        return filepath;
    }

    public ClassInformation getParentClass() {
        return parentClass;
    }

    /**
     * Adds a new boolean feature with the information passed
     *
     * @param name Name of the boolean feature to add
     * @param value Value of the boolean feature to add
     * @return Whether or not the boolean feature was added
     */
    public boolean addBooleanFeature(String name, boolean value) {
        if (!booleanFeatures.containsKey(name)) {
            booleanFeatures.put(name, value);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes a boolean feature with the given name
     *
     * @param name Name of the boolean feature to remove
     * @return Whether or not the boolean feature was removed
     */
    public boolean removeBooleanFeature(String name) {
        booleanFeatures.remove(name);
        return booleanFeatures.containsKey(name);
    }

    /**
     * Returns a map of boolean features to values for a method
     *
     * @return Map of boolean features to values
     */
    public Map<String, Boolean> getBooleanFeatures() {
        return booleanFeatures;
    }

    /**
     * Gets and returns the value of the boolean feature with the given name
     *
     * @param name The name of the boolean feature to search for
     * @return The value of the feature (or null if it is not present)
     */
    public boolean getBooleanFeature(String name) {
        return booleanFeatures.get(name);
    }

    public List<IdentifierProperties> getParameters() {
    	return parameters;
    }

    public List<IdentifierProperties> getLocalVariables() {
    	return localVariables;
    }

    public List<IdentifierProperties> getFields() {
    	return fields;
    }
    
    /**
     * Adds the identifier to the method's identifier list
     *
     * @param properties Identifier information
     */
    public void addIdentifier(IdentifierProperties properties) {
        switch (properties.getContext()) {
            case FORMAL_PARAMETER:
                parameters.add(properties);
                break;
            case LOCAL_VARIABLE:
                localVariables.add(properties);
                break;
            case FIELD:
                fields.add(properties);
                break;
        }
    }

    /**
     * Finds and returns all identifier processed names in this method
     *
     * @return Set of all identifier processed names in the method
     */
    public Set<String> getIdentifierProcessedNames() {
        Set<String> names = new HashSet<String>();

        // Get Parameters
        for (IdentifierProperties identifier : parameters) {
        	if (identifier.hasBeenProcessed()) {
                names.add(identifier.getProcessedName());
        	}
        }

        // Get Local Variables
        for (IdentifierProperties identifier : localVariables) {
        	if (identifier.hasBeenProcessed()) {
                names.add(identifier.getProcessedName());
        	}
        }

        // Get Fields
        for (IdentifierProperties identifier : fields) {
        	if (identifier.hasBeenProcessed()) {
                names.add(identifier.getProcessedName());
        	}
        }

        return names;
    }

    /**
     * Adds a comment to the method's information
     *
     * @param comment The comment to add
     */
    public void addComment(CommentInfo comment) {
        comments.add(comment);
    }

    /**
     * Gets and returns all of the comments associated with the method
     *
     * @return The list of all comments associated with the method
     */
    public List<CommentInfo> getComments() {
        return comments;
    }

    public String getJavadoc() {
        return javadoc;
    }

    public void setJavadoc(String javadoc) {
        this.javadoc = javadoc;
    }

    /**
     * Find and return a list of differences between the method contents and its comments
     *
     * @return The list of differences between the comments and the method
     */
    public MethodDifferences getDifferences() {
        MethodDifferences differences = new MethodDifferences(methodName, parentClass.getClassName(), filepath);

        for (IdentifierProperties field : fields) {

            String identifier = field.getProcessedName();

            boolean foundInComment = false;
            for (CommentInfo comment : comments) {
                if (comment.getCommentText().contains(identifier)) {
                    foundInComment = true;
                }
            }

            if (!foundInComment) {
                differences.add(new Difference("", identifier, field.getReads() + (2 * field.getWrites())));
            }
        }

        for (IdentifierProperties parameter : parameters) {

            String identifier = parameter.getProcessedName();

            boolean foundInComment = false;
            for (CommentInfo comment : comments) {
                if (comment.getCommentText().contains(identifier)) {
                    foundInComment = true;
                }
            }

            if (!foundInComment) {
                int differenceScore = (2 * parameter.getReads()) + (4 * parameter.getWrites());
                if (differenceScore > 0) {
                    differences.add(new Difference("", identifier, differenceScore));
                }
            }
        }

        return differences;
    }

    @Override
    public String toString() {
        return "Method " + methodName + ":\n" +
                "\tParameters: " + parameters + "\n" +
                "\tLocal Variables: " + localVariables + "\n" +
                "\tFields: " + fields + "\n";
    }
}
