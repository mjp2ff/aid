package edu.virginia.aid.data;

import edu.virginia.aid.comparison.*;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.Type;

/**
 * Data wrapper for a feature list for a single method
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class MethodFeatures extends SourceElement {

    private String filepath;
    private ClassInformation parentClass;
    private String methodName;
    private Type returnType;
    private String processedMethodName;
    private Map<String, Boolean> booleanFeatures;
    private Map<String, String> stringFeatures;
    private List<IdentifierProperties> parameters;
    private List<IdentifierProperties> localVariables;
    private List<IdentifierProperties> fields;
    private List<MethodInvocationProperties> methodInvocations;
    private Javadoc javadoc;
    private ExpressionInfo returnValue;
    private Map<String, Double> TFIDF;
    private List<String> allWords;

    // Constants
    public static final String PRIMARY_VERB = "primary verb";
    public static final String PRIMARY_OBJECT = "primary object";

    public MethodFeatures(String methodName, ClassInformation parentClass, String filepath,
                          Type returnType, int startPos, int endPos, final SourceContext sourceContext) {
        super(startPos, endPos, sourceContext);

        this.methodName = methodName;
        this.parentClass = parentClass;
        this.filepath = filepath;
        this.returnType = returnType;
        this.booleanFeatures = new HashMap<>();
        this.stringFeatures = new HashMap<>();
        this.parameters = new ArrayList<>();
        this.localVariables = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.methodInvocations = new ArrayList<>();
        this.javadoc = null;
        this.returnValue = null;
        this.TFIDF = new HashMap<>();
        this.allWords = new ArrayList<>();

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
     * Adds a new string feature with the information passed
     *
     * @param name Name of the string feature to add
     * @param value Value of the string feature to add
     * @return Whether or not the string feature was added
     */
    public boolean addStringFeature(String name, String value) {
        if (!stringFeatures.containsKey(name)) {
            stringFeatures.put(name, value);
            return true;
        } else {
            return false;
        }
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
     * Returns a map of string features to value for a method
     *
     * @return Map of string features to values
     */
    public Map<String, String> getStringFeatures() {
        return stringFeatures;
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

    /**
     * Gets and returns the value of the string feature with the given name
     *
     * @param name The name of the string feature to search for
     * @return The value of the feature (or null if it is not present)
     */
    public String getStringFeature(String name) {
        return stringFeatures.get(name);
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

    public List<String> getAllWords() {
    	allWords = new ArrayList<>();
    	allWords.add(processedMethodName);
    	for (IdentifierProperties identifier : parameters) {
    		allWords.addAll(identifier.getData());
    	}
    	for (IdentifierProperties identifier : localVariables) {
    		allWords.addAll(identifier.getData());
    	}
    	for (IdentifierProperties identifier : fields) {
    		allWords.addAll(identifier.getData());
    	}
    	for (MethodInvocationProperties methodInvocation : methodInvocations) {
    		allWords.addAll(methodInvocation.getData());
    	}
    	for (CommentInfo comment : getComments()) {
    		allWords.addAll(comment.getData());
    	}

        if (javadoc != null) {
            allWords.addAll(Arrays.asList(javadoc.toString().split(" ")));
        }
    	allWords.addAll(returnValue.getData());
    	
    	return allWords;
    }
    
    public List<IdentifierProperties> getIdentifiers() {
        List<IdentifierProperties> identifiers = new ArrayList<>();
        identifiers.addAll(parameters);
        identifiers.addAll(fields);
        identifiers.addAll(localVariables);
        return identifiers;
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
            case METHOD:
            	break;
            default:
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
     * Adds an identifier use to the identifiers in the method
     *
     * @param identifierUse The identifier use to add
     */
    public void addIdentifierUse(IdentifierName identifierUse) {
        if (identifierUse.isVariable()) {

            IdentifierProperties identifierProperties;
            if (!identifierUse.hasClassScope()) {
                identifierProperties = getClosestVariable(identifierUse.getName());
            } else {
                identifierProperties = getField(identifierUse.getName());
            }

            if (identifierProperties != null) {
                if (identifierUse.getUse() == IdentifierUse.READ) {
                    identifierProperties.addReads(1);
                } else if (identifierUse.getUse() == IdentifierUse.WRITE) {
                    identifierProperties.addWrites(1);
                }
            }
        }
    }

    /**
     * Adds a method invocation to the list of method invocations within the method
     *
     * @param methodInvocation The invocation to add
     */
    public void addMethodInvocation(MethodInvocationProperties methodInvocation) {
        methodInvocations.add(methodInvocation);
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
            // Check comments for method name
            for (CommentInfo comment : getComments()) {
                if (comment.getCommentText().contains(processedMethodName)) {
                    foundInComment = true;
                    break;
                }
            }
            
            // Check javadoc for method name
            Javadoc javadocElem = getJavadoc();
            if (javadocElem != null && javadocElem.toString().contains(processedMethodName)) {
            	foundInComment = true;
            }

            if (!foundInComment) {
            	String differenceMessage = "The method name (" + methodName + ") is not discussed in the comments";
            	double differenceScore = DifferenceWeights.METHOD_NAME * getTFIDF(processedMethodName);
                differences.add(new GenericDifference(differenceMessage, differenceScore));
            }
        }

        // Process fields
        for (IdentifierProperties field : fields) {

            String identifier = field.getProcessedName();

            boolean foundInComment = false;
            // Check comments for field
            for (CommentInfo comment : getComments()) {
                if (comment.getCommentText().contains(identifier)) {
                    foundInComment = true;
                    break;
                }
            }

            // Check javadoc for method name
            Javadoc javadocElem = getJavadoc();
            if (javadocElem != null && javadocElem.toString().contains(identifier)) {
            	foundInComment = true;
            }
            
            if (!foundInComment) {
                double differenceScore = ((DifferenceWeights.FIELD_READ * field.getReads()) +
                        (DifferenceWeights.FIELD_WRITE * field.getWrites()) +
                        (field.isInReturnStatement() ? DifferenceWeights.IN_RETURN_STATEMENT : 0)) * getTFIDF(identifier);
                if (differenceScore > 0) {
                    differences.add(new MissingIdentifierDifference(field, differenceScore));
                }
            }
        }

        // Process parameters
        for (IdentifierProperties parameter : parameters) {

            String identifier = parameter.getProcessedName();

            boolean foundInComment = false;
            // Check comments for parameter
            for (CommentInfo comment : getComments()) {
                if (comment.getCommentText().contains(identifier)) {
                    foundInComment = true;
                    break;
                }
            }

            // Check javadoc for parameter
            Javadoc javadocElem = getJavadoc();
            if (javadocElem != null && javadocElem.toString().contains(identifier)) {
            	foundInComment = true;
            }
            
            if (!foundInComment) {
                double differenceScore = ((DifferenceWeights.PARAMETER_READ * parameter.getReads()) +
                        (DifferenceWeights.PARAMETER_WRITE * parameter.getWrites()) +
                        (parameter.isInReturnStatement() ? DifferenceWeights.IN_RETURN_STATEMENT : 0)) * getTFIDF(identifier);
                if (differenceScore > 0) {
                    differences.add(new MissingIdentifierDifference(parameter, differenceScore));
                }
            }
        }

        // Process method invocations
        if (methodInvocations.size() == 1) {
            MethodInvocationProperties methodInvocation = methodInvocations.get(0);

            boolean foundInComment = false;
            for (CommentInfo comment : getComments()) {
                if (comment.getCommentText().contains(methodInvocation.getProcessedName())) {
                    foundInComment = true;
                    break;
                }
            }

            if (!foundInComment) {
            	String differenceMessage = "Method " + methodInvocation.getName() + " is invoked but not discussed in comments";
            	double differenceScore = DifferenceWeights.ONLY_METHOD_INVOCATION * getTFIDF(methodInvocation.getProcessedName());
                differences.add(new GenericDifference(differenceMessage, differenceScore));
            }
        }

        for (String key : stringFeatures.keySet()) {
            String value = stringFeatures.get(key);
            if (!containedInComments(value)) {
                switch (key) {
                    case MethodFeatures.PRIMARY_VERB:
                        differences.add(new GenericDifference("The primary method action (" + value + ") is not discussed in the comments", DifferenceWeights.PRIMARY_VERB * TFIDF.get(value)));
                        break;
                    case MethodFeatures.PRIMARY_OBJECT:
                        differences.add(new GenericDifference("The primary object acted upon (" + value + ") is not discussed in the comments", DifferenceWeights.PRIMARY_OBJECT * TFIDF.get(value)));
                        break;
                    default:
                        break;
                }
            }
        }

        return differences;
    }

    /**
     * Calculates all TFIDF values for this method.
     * 
     * @param allProjectWords A list containing a list of words in each method in the project.
     */
    public void calculateTFIDF(List<List<String>> allProjectWords) {
    	List<String> currentMethodWords = getAllWords();
    	
    	Map<String, Double> TF = new HashMap<>();
    	Map<String, Double> IDF = new HashMap<>();

        for (String s : currentMethodWords) {
        	if (!TFIDF.containsKey(s)) {
        		double tf = 0;
            	if (!TF.containsKey(s)) {
                	// Calculate logarithmically scaled TF frequency
            		tf = 1 + Math.log(Collections.frequency(currentMethodWords, s));
            		TF.put(s, tf);
            	} else {
            		tf = TF.get(s);
            	}

            	double idf = 0;
            	if (!IDF.containsKey(s)) {
            		// Calculate logarithmically scaled IDF frequency
            		double totalDocs = allProjectWords.size();
            		double numDocsContainingWord = 0;
            		for (List<String> curList : allProjectWords) {
            			numDocsContainingWord += curList.contains(s) ? 1 : 0;
            		}
            		idf = Math.log(totalDocs / numDocsContainingWord);
            		IDF.put(s, idf);
            	} else {
            		idf = IDF.get(s);
            	}
            	
            	double tfidf = tf*idf;
            	TFIDF.put(s, tfidf);
        	}
        }
	}

    /**
     * Helper method to get the TFIDF value for a string, null-safe.
     * 
     * @param s The string to look up.
     * @return TFIDF value for the given string.
     */
    public double getTFIDF(String s) {
    	Double retVal = TFIDF.get(s);
    	return retVal != null ? retVal : 1.0;
    }
        
    /**
     * Checks whether a string is contained within the text of the method's comments
     *
     * @param term The term to search for
     * @return Whether or not the term was found in the comments
     */
    public boolean containedInComments(String term) {
        boolean foundInComments = false;
        for (CommentInfo comment : getComments()) {
            if (comment.getCommentText().contains(term)) {
                foundInComments = true;
                break;
            }
        }

        return foundInComments;
    }

    public List<MethodInvocationProperties> getMethodInvocations() {
        return methodInvocations;
    }

    @Override
    public String toString() {
        return "Method " + getMethodSignature() + ":\n" +
                "\tParameters: " + parameters + "\n" +
                "\tLocal Variables: " + localVariables + "\n" +
                "\tFields: " + fields + "\n";
    }
}
