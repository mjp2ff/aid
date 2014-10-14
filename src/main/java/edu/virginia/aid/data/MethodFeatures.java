package edu.virginia.aid.data;

import edu.virginia.aid.comparison.*;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.Javadoc;

/**
 * Data wrapper for a feature list for a single method
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class MethodFeatures extends SourceElement {

    private String filepath;
    private ClassInformation parentClass;
    private String methodName;
    private String processedMethodName;
    private Map<String, Boolean> booleanFeatures;
    private List<IdentifierProperties> parameters;
    private List<IdentifierProperties> localVariables;
    private List<IdentifierProperties> fields;
    private Javadoc javadoc;
    private ExpressionInfo returnValue;

    public MethodFeatures(String methodName, ClassInformation parentClass, String filepath, int startPos, int endPos, final SourceContext sourceContext) {
        super(startPos, endPos, sourceContext);

        this.methodName = methodName;
        this.parentClass = parentClass;
        this.filepath = filepath;
        this.booleanFeatures = new HashMap<>();
        this.parameters = new ArrayList<>();
        this.localVariables = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.javadoc = null;
        this.returnValue = null;

        this.processedMethodName = methodName;
    }

    /**
     * Gets and returns the name of the method
     *
     * @return Method name
     */
    public String getMethodName() {
        return this.methodName;
    }

    public String getProcessedMethodName() {
        return processedMethodName;
    }

    public void setProcessedMethodName(String processedMethodName) {
        this.processedMethodName = processedMethodName;
    }

    public String getFilepath() {
        return filepath;
    }

    public ClassInformation getParentClass() {
        return parentClass;
    }

    public ExpressionInfo getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(ExpressionInfo returnValue) {
        this.returnValue = returnValue;
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
     * Creates string for method signature
     *
     * @return The string representation of the method signature
     */
    public String getMethodSignature() {
        String[] parameterNames = new String[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            parameterNames[i] = parameters.get(i).getName();
        }

        return methodName + "(" + StringUtils.join(parameterNames, ", ") + ")";
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
     * Gets and returns all of the comments associated with the method
     *
     * @return The list of all comments associated with the method
     */
    public List<CommentInfo> getComments() {
        List<CommentInfo> comments = parentClass.getComments();

        // Process comments to only show ones for the current method.
        List<CommentInfo> processedComments = new ArrayList<CommentInfo>();
        for (CommentInfo commentInfo : comments) {
        	if (commentInfo.getStartPos() >= this.getStartPos()
        			&& commentInfo.getEndPos() <= this.getEndPos()) {
        		processedComments.add(commentInfo);
        	}
        }
        
        return processedComments;
    }

    public Javadoc getJavadoc() {
        return javadoc;
    }

    public void setJavadoc(Javadoc javadoc) {
        this.javadoc = javadoc;
    }

    /**
     * Finds and returns the closest scoped variable with the given name
     *
     * @param name Name of the variable to return
     * @return Closest scoped variable
     */
    public IdentifierProperties getClosestVariable(String name) {

        // Search local variables
        IdentifierProperties localVariable = getLocalVariable(name);
        if (localVariable != null) {
            return localVariable;
        }

        // Search parameters
        IdentifierProperties parameter = getParameter(name);
        if (parameter != null) {
            return parameter;
        }

        // Search fields
        IdentifierProperties field = getField(name);
        if (field != null) {
            return field;
        }

        // Return null if none found
        return null;
    }

    /**
     * Finds and returns the local variable with the given name, or null if none exists
     *
     * @param name Name of the variable to find
     * @return Local variable with name or null if none exists
     */
    public IdentifierProperties getLocalVariable(String name) {
        return searchIdentifierList(name, localVariables);
    }

    /**
     * Finds and returns the parameter with the given name, or null if none exists
     *
     * @param name Name of the parameter to find
     * @return Parameter with name or null if none exists
     */
    public IdentifierProperties getParameter(String name) {
        return searchIdentifierList(name, parameters);
    }

    /**
     * Finds and returns the field with the given name, or null if none exists
     *
     * @param name Name of the field to find
     * @return Field with the name or null if none exists
     */
    public IdentifierProperties getField(String name) {
        return searchIdentifierList(name, fields);
    }

    /**
     * Private helper method which finds and returns an identifier from a list if
     * its name matches the search name, or null if it is not found
     *
     * @param name The name of the variable to find
     * @param list The list to search for the variable name
     * @return The identifier with the given name or null if not found
     */
    private static IdentifierProperties searchIdentifierList(String name, List<IdentifierProperties> list) {
        for (IdentifierProperties identifier : list) {
            if (identifier.getName().equals(name)) {
                return identifier;
            }
        }

        return null;
    }

    /**
     * Find and return a list of differences between the method contents and its comments
     *
     * @return The list of differences between the comments and the method
     */
    public MethodDifferences getDifferences() {
        MethodDifferences differences = new MethodDifferences(this);

        // Process the method
        {
            boolean foundInComment = false;
            for (CommentInfo comment : getComments()) {
                if (comment.getCommentText().contains(processedMethodName)) {
                    foundInComment = true;
                    break;
                }
            }

            if (!foundInComment) {
                differences.add(new GenericDifference("The method name (" + methodName + ") is not discussed in the comments", DifferenceWeights.METHOD_NAME));
            }
        }

        for (IdentifierProperties field : fields) {

            String identifier = field.getProcessedName();

            boolean foundInComment = false;
            for (CommentInfo comment : getComments()) {
                if (comment.getCommentText().contains(identifier)) {
                    foundInComment = true;
                    break;
                }
            }

            if (!foundInComment) {
                int differenceScore = (DifferenceWeights.FIELD_READ * field.getReads()) + (DifferenceWeights.FIELD_WRITE * field.getWrites());
                if (differenceScore > 0) {
                    differences.add(new MissingIdentifierDifference(field, differenceScore));
                }
            }
        }

        for (IdentifierProperties parameter : parameters) {

            String identifier = parameter.getProcessedName();

            boolean foundInComment = false;
            for (CommentInfo comment : getComments()) {
                if (comment.getCommentText().contains(identifier)) {
                    foundInComment = true;
                    break;
                }
            }

            Javadoc javadocElem = getJavadoc();
            if (javadocElem != null && javadocElem.toString().contains(identifier)) {
            	foundInComment = true;
            }
            
            if (!foundInComment) {
                int differenceScore = (DifferenceWeights.PARAMETER_READ * parameter.getReads()) + (DifferenceWeights.PARAMETER_WRITE * parameter.getWrites());
                if (differenceScore > 0) {
                    differences.add(new MissingIdentifierDifference(parameter, differenceScore));
                }
            }
        }

        return differences;
    }

    @Override
    public String toString() {
        return "Method " + getMethodSignature() + ":\n" +
                "\tParameters: " + parameters + "\n" +
                "\tLocal Variables: " + localVariables + "\n" +
                "\tFields: " + fields + "\n";
    }
}
