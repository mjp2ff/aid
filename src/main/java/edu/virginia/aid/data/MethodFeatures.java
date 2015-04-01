package edu.virginia.aid.data;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.virginia.aid.comparison.*;
import edu.virginia.aid.symex.BooleanAndList;
import edu.virginia.aid.symex.IdentifierValue;
import edu.virginia.aid.symex.SumOfProducts;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.Type;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;

/**
 * Overarching container for complete processed information about a single method to be analyzed.
 * Each method has a single MethodFeatures object that is updated as the various data processing
 * steps happen during execution.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class MethodFeatures extends SourceElement {

    /**
     * The absolute system file path to the file containing this method
     */
    private String filepath;

    /**
     * Information about the class that contains this method
     */
    private ClassInformation parentClass;

    /**
     * The name of this method
     */
    private String methodName;

    /**
     * The static return type of this method
     */
    private Type returnType;

    /**
     * The name of the method, optionally processed for comparison
     */
    private String processedMethodName;

    /**
     * Map of various boolean-valued features about the method to their values
     */
    private Map<String, Boolean> booleanFeatures;

    /**
     * Map of various string-valued features about the method to their values
     */
    private Map<String, String> stringFeatures;

    /**
     * Map of various integer-valued features about the method to their values
     */
    private Map<String, Integer> numericFeatures;

    /**
     * All of the variables that are in scope for the method
     */
    private ScopeProperties scope;

    /**
     * The Javadoc comment provided with the method in source code
     */
    private Javadoc javadoc;

    /**
     * Map of words to TF/IDF values
     */
    private Map<String, Double> TFIDF;

    /**
     * Map of words to the number of times each occurs
     */
    private Map<String, Integer> wordFrequencies;

    /**
     * Set of all words in the corpus of the program analyzed, not including comments
     */
    private Set<String> allWordsNoComments;

    /**
     * The main action performed by the method, as determined by the tool
     */
    private String primaryAction = "";

    /**
     * The main object acted upon or used in the method, as determined by the tool
     */
    private String primaryObject = "";

    /**
     * The predicate that must be satisfied in order for the method to not throw an explicit exception.
     * This is based on a static analysis and is neither complete nor consistent, but is rather an approximation.
     */
    private SumOfProducts conditionsForSuccess = null;

    // Boolean parameters
    public static final String RETURNS_BOOLEAN = "returns_boolean";
    public static final String IS_CONSTRUCTOR = "is_constructor";
    public static final String ONE_FIELD_INVOKED_OR_WRITTEN = "one_field_invoked_or_written";

    // Numeric parameters
    public static final String NUM_PARAM_READS = "num_param_reads";
    public static final String NUM_FIELD_READS = "num_field_reads";
    public static final String NUM_FIELD_WRITES = "num_field_writes";

    /**
     * Creates a MethodFeatures using the given parameters:
     * 
     * @param methodName The name of the current method
     * @param parentClass The parent class of the current method
     * @param filepath The filepath for the class containing the current method
     * @param returnType The return type of the current method
     * @param startPos The start position in the file of the current method
     * @param endPos The end position in the file of the current method
     * @param sourceContext Additional source context information for the current method
     */
    public MethodFeatures(String methodName, ClassInformation parentClass, String filepath,
                          Type returnType, int startPos, int endPos, final SourceContext sourceContext) {
        super(startPos, endPos, sourceContext);

        this.methodName = methodName;
        this.parentClass = parentClass;
        this.filepath = filepath;
        this.returnType = returnType;
        this.booleanFeatures = new HashMap<>();
        this.stringFeatures = new HashMap<>();
        this.numericFeatures = new HashMap<>();
        this.scope = new ScopeProperties();
        this.javadoc = null;
        this.TFIDF = new HashMap<>();
        this.wordFrequencies = null;
        this.allWordsNoComments = null;

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

    /**
     * Gets and returns the processed name of the method
     *
     * @return The processed method name
     */
    public String getProcessedMethodName() {
        return processedMethodName;
    }

    /**
     * Sets the processed method name
     * 
     * @param processedMethodName The new processed method name
     */
    public void setProcessedMethodName(String processedMethodName) {
        this.processedMethodName = processedMethodName;
    }

    /**
     * Gets and returns the filepath
     *
     * @return The filepath
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * Gets and returns the parent class
     *
     * @return The parent class
     */
    public ClassInformation getParentClass() {
        return parentClass;
    }

    /**
     * Gets and returns the return type
     *
     * @return The return type
     */
    public Type getReturnType() {
        return returnType;
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
     * Adds a new numeric feature with the information passed
     *
     * @param name Name of the string feature to add
     * @param value Value of the string feature to add
     * @return Whether or not the string feature was added
     */
    public boolean addNumericFeature(String name, int value) {
        if (!numericFeatures.containsKey(name)) {
            numericFeatures.put(name, value);
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
     * Returns a map of numeric features to value for a method
     *
     * @return Map of numeric features to values
     */
    public Map<String, Integer> getNumericFeatures() {
        return numericFeatures;
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

    /**
     * Gets and returns the value of the numeric feature with the given name
     *
     * @param name The name of the numeric feature to search for
     * @return The value of the feature (or null if it is not present)
     */
    public int getNumericFeature(String name) {
        return numericFeatures.get(name);
    }

    /**
     * Gets and returns the primary action for this method
     * 
     * @return The primary action for this method
     */
    public String getPrimaryAction() {
        return primaryAction;
    }

    /**
     * Sets the primary action for this method
     * 
     * @param primaryAction The primary action to set
     */
    public void setPrimaryAction(String primaryAction) {
        this.primaryAction = primaryAction;
    }

    /**
     * Gets and returns the primary object for this method
     * 
     * @return The primary object for this method
     */
    public String getPrimaryObject() {
        return primaryObject;
    }

    /**
     * Sets the primary object for this method
     * 
     * @param primaryObject The primary object to set
     */
    public void setPrimaryObject(String primaryObject) {
        this.primaryObject = primaryObject;
    }

    /**
     * Gets the tool's approximation of the predicate that has to be satisfied for the method to not
     * throw an explicit exception.
     *
     * @return The predicate that has to be satisfied for the method to not throw an explicit exception
     */
    public IdentifierValue getConditionsForSuccess() {
        return conditionsForSuccess;
    }

    /**
     * Sets the predicate that must be satisfied for the method to not throw an explicit exception.
     *
     * @param conditionsForSuccess Updated predicate that has to be satisfied for the method to not throw
     *                             an explicit exception
     */
    public void setConditionsForSuccess(SumOfProducts conditionsForSuccess) {
        this.conditionsForSuccess = conditionsForSuccess;
    }

    /**
     * Getter for word frequencies, calculating them if necessary.
     * 
     * @return Word frequencies for this method.
     */
    public Map<String, Integer> getWordFrequencies() {
    	if (wordFrequencies == null) {
    		calculateWordFrequencies();
    	}
    	return wordFrequencies;
    }

    /**
     * Calculates the word frequencies for this method, and return a list of all the words in the method.
     */
    public void calculateWordFrequencies() {
    	wordFrequencies = new HashMap<>();
    	allWordsNoComments= new HashSet<>();
    	
    	incrementFrequenciesMap(processedMethodName);
		allWordsNoComments.add(processedMethodName);
		
    	for (IdentifierProperties identifier : scope.getParameters()) {
    		for (String s : identifier.getData()) {
    			incrementFrequenciesMap(s);
    			allWordsNoComments.add(s);
    		}
    	}
    	for (IdentifierProperties identifier : scope.getLocalVariables()) {
    		for (String s : identifier.getData()) {
    			incrementFrequenciesMap(s);
    			allWordsNoComments.add(s);
    		}
    	}
    	for (IdentifierProperties identifier : scope.getFields()) {
    		for (String s : identifier.getData()) {
    			incrementFrequenciesMap(s);
    			allWordsNoComments.add(s);
    		}
    	}
    	for (CommentInfo comment : getComments()) {
    		for (String s : comment.getData()) {
    			incrementFrequenciesMap(s);
    		}
    	}

        if (javadoc != null) {
            String[] javadocWords = javadoc.toString().split(" ");
            for (String s : javadocWords) {
    			incrementFrequenciesMap(s);
            }
        }
    }
    
    /**
     * Helper method to increment the frequency for a given string.
     * 
     * @param s The string whose frequency should be incremented.
     */
    private void incrementFrequenciesMap(String s) {
    	Integer i = this.wordFrequencies.get(s);
    	if (i == null) {
    		this.wordFrequencies.put(s, 1);
    	} else {
    		this.wordFrequencies.put(s, i);
    	}
    }
    
    /**
     * Creates string for method signature
     *
     * @return The string representation of the method signature
     */
    public MethodSignature getMethodSignature() {
        List<IdentifierProperties> parameters = scope.getParameters();
        String[] parameterNames = new String[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            parameterNames[i] = parameters.get(i).getName();
        }

        return new MethodSignature(getMethodName(), parameterNames);
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

    /**
     * Gets and returns the javadoc for this method
     * 
     * @return The javadoc for this method
     */
    public Javadoc getJavadoc() {
        return javadoc;
    }

    /**
     * Sets the javadoc for this method
     * 
     * @param javadoc The new javadoc to set
     */
    public void setJavadoc(Javadoc javadoc) {
        this.javadoc = javadoc;
    }

    /**
     * Gets and returns the scope of this method
     * 
     * @return The scope of this method
     */
    public ScopeProperties getScope() {
        return scope;
    }

    /**
     * Find and return a list of differences between the method contents and its comments
     *
     * @param wordNetDictionary A WordNet dictionary used to detect synsets.
     * @return The list of differences between the comments and the method
     */
    public MethodDifferences getDifferences(IDictionary wordNetDictionary) {
        MethodDifferences differences = new MethodDifferences(this);

        // Process the method
        {
            boolean foundInComment = containedInComments(wordNetDictionary, processedMethodName);

            if (!foundInComment) {
            	String differenceMessage = "The method name (" + methodName + ") is not discussed in the comments";
            	double differenceScore = DifferenceWeights.METHOD_NAME * getTFIDF(processedMethodName);
                differences.add(new GenericDifference(differenceMessage, differenceScore));
            }
        }

        // Process fields
        for (IdentifierProperties field : scope.getFields()) {

            String identifier = field.getProcessedName();
            boolean foundInComment = containedInComments(wordNetDictionary, identifier);
            
            if (!foundInComment) {
                double differenceScore = field.getReadWriteDifferenceValue(scope) * getTFIDF(identifier);
                if (differenceScore > 0) {
                    differences.add(new MissingIdentifierDifference(field, differenceScore));
                }
            }
        }

        // Process parameters
        for (IdentifierProperties parameter : scope.getParameters()) {

            String identifier = parameter.getProcessedName();
            boolean foundInComment = containedInComments(wordNetDictionary, identifier);
            
            if (!foundInComment) {
                double differenceScore = parameter.getReadWriteDifferenceValue(scope) * getTFIDF(identifier);
                if (differenceScore > 0) {
                    differences.add(new MissingIdentifierDifference(parameter, differenceScore));
                }
            }
        }

        if (!primaryAction.isEmpty() && !containedInComments(wordNetDictionary, primaryAction)) {
            differences.add(new GenericDifference("The primary method action (" + primaryAction + ") is not discussed in the comments", DifferenceWeights.PRIMARY_VERB * getTFIDF(primaryAction)));
        }

        if (!primaryObject.isEmpty() && !containedInComments(wordNetDictionary, primaryObject)) {
            differences.add(new GenericDifference("The primary object acted upon (" + primaryObject + ") is not discussed in the comments", DifferenceWeights.PRIMARY_OBJECT * getTFIDF(primaryObject)));
        }

        // Process conditions for success
        if (conditionsForSuccess != null) {
            for (BooleanAndList product : conditionsForSuccess.getProducts()) {
                if (!containedInComments(wordNetDictionary, product.toString())) {
                    differences.add(new SuccessConditionDifference(product,
                            DifferenceWeights.CONDITIONS_FOR_SUCCESS *
                                    getTFIDF(product.toString()) /
                                    Math.pow(conditionsForSuccess.getProducts().size(), 2)));
                }
            }
        }

        return differences;
    }

    /**
     * Calculates all TFIDF values for this method.
     * 
     * @param allProjectWordFrequencies A list containing a list of words in each method in the project.
     */
    public void calculateTFIDF(List<Map<String, Integer>> allProjectWordFrequencies) {
    	// Shouldn't happen here, but just in case.
    	if (wordFrequencies == null) {
    		calculateWordFrequencies();
    	}
    	
    	Map<String, Double> TF = new HashMap<>();
    	Map<String, Double> IDF = new HashMap<>();

        for (String s : allWordsNoComments) {
        	if (!TFIDF.containsKey(s)) {
        		double tf = 0;
            	if (!TF.containsKey(s)) {
                	// Calculate logarithmically scaled TF frequency
            		tf = 1 + Math.log(wordFrequencies.get(s));
            		TF.put(s, tf);
            	} else {
            		tf = TF.get(s);
            	}

            	double idf = 0;
            	if (!IDF.containsKey(s)) {
            		// Calculate logarithmically scaled IDF frequency
            		double totalDocs = allProjectWordFrequencies.size();
            		double numDocsContainingWord = 0;
            		for (Map<String, Integer> curMap : allProjectWordFrequencies) {
            			numDocsContainingWord += curMap.get(s) != null ? 1 : 0;
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
        double totalTFIDF = 0;
        for (String word : s.split(" ")) {
            Double retVal = TFIDF.get(word);
            totalTFIDF += (retVal != null ? retVal : 1.0);
        }
        return Math.min(2, totalTFIDF / s.split(" ").length);
    }
        
    /**
     * Checks whether a string is contained within the text of the method's comments
     *
     * @param wordNetDictionary A WordNet dictionary used to detect synsets.
     * @param term The term to search for
     * @return Whether or not the term was found in the comments
     */
    public boolean containedInComments(IDictionary wordNetDictionary, String term) {

    	// Don't do anything if it's empty string, space, etc. as this breaks JWI
    	if (term.isEmpty()) return false;
    	
    	// Create set of synonyms with just the term, build it up with WordNet through JWI.
		Set<String> synonyms = new HashSet<>();
		synonyms.add(term);

		// Check each part of speech for thoroughness.
		for (POS pos : new POS[]{POS.NOUN, POS.ADJECTIVE, POS.ADVERB, POS.VERB}) {
			// Get index word, verify it exists.
			IIndexWord idxWord = wordNetDictionary.getIndexWord(term, pos);
			if (idxWord == null) continue;
			// Get all word IDs for this index word.
			for (IWordID wordID : idxWord.getWordIDs()) {
				// Get word for this wordID and verify it exists.
				IWord word = wordNetDictionary.getWord(wordID);
				if (word == null) continue;
				// Add this word's synset word lemmas to the list of synonyms.
				for (IWord synonym : word.getSynset().getWords()) {
					synonyms.add(synonym.getLemma());
				}
			}
		}
    	
        boolean foundInComments = false;

        String javadocSummary = getJavadocSummary();

        List<String> synonymsList = new ArrayList<>(synonyms);
        for (int i = 0; i < synonymsList.size() && !foundInComments; ++i) {
        	String synonym = synonymsList.get(i);
        	// Ignoring internal comments for now.
//    		for (CommentInfo comment : getComments()) {
//                if (comment.getCommentText().contains(synonym)) {
//                    foundInComments = true;
//                    break;
//                }
//        	}

        	// Direct comparison for single words
        	if (!synonym.contains(" ")) {
                if (javadocSummary.contains(synonym)) {
                    foundInComments = true;
                }
        	}
        	// Compare word-by-word for multi-word identifiers.
        	else {
        		boolean foundInAll = true;
        		String[] multiWordSplit = synonym.split(" +");
        		for (String word : multiWordSplit) {
        			if (!javadocSummary.contains(word)) {
        				foundInAll = false;
        			}
        		}
        		if (foundInAll) {
        			foundInComments = true;
        		}
        	}
        }

        return foundInComments;
    }

    /**
     * Creates an instance to be passed into a Weka classifier for classification purposes
     *
     * @return Weka Instance
     */
    public Instance buildWekaInstance(Attribute classAttribute) {
        FastVector attributes = new FastVector(booleanFeatures.size() + numericFeatures.size() + 1);

        for (String property : booleanFeatures.keySet()) {
            FastVector values = new FastVector(2);
            values.addElement("true");
            values.addElement("false");
            attributes.addElement(new Attribute(property, values));
        }

        for (String property : numericFeatures.keySet()) {
            attributes.addElement(new Attribute(property));
        }

        attributes.addElement(classAttribute);

        Instances instances = new Instances("unlabeled", attributes, 0);
        instances.setClass(classAttribute);

        Instance instance = new Instance(booleanFeatures.size() + numericFeatures.size() + 1);
        instance.setDataset(instances);
        instance.setClassMissing();

        int i = 0;
        for (String booleanProperty : booleanFeatures.keySet()) {
            instance.setValue((Attribute) attributes.elementAt(i++), getBooleanFeature(booleanProperty) ? "true" : "false");
        }
        for (String numericProperty : numericFeatures.keySet()) {
            instance.setValue((Attribute) attributes.elementAt(i++), getNumericFeature(numericProperty));
        }

        return instance;
    }

    /**
     * Gets the summary portion of the Javadoc for the method, returning the empty string if there is none
     *
     * @return the summary portion of the method's Javadoc, or empty string if there is none
     */
    public String getJavadocSummary() {
        String summary = "";

        if (javadoc != null) {
            for (TagElement tag : (List<TagElement>) javadoc.tags()) {
                if (tag.getTagName() == null) {
                    summary = tag.toString();
                }
            }
        }

        return summary;
    }

    /**
     * Basic toString implementation for a MethodFeatures object.
     * 
     * @return String representation of the methodFeatures
     */
    @Override
    public String toString() {
        return "Method " + getMethodSignature() + ":\n" +
                "\tParameters: " + scope.getParameters() + "\n" +
                "\tLocal Variables: " + scope.getLocalVariables() + "\n" +
                "\tFields: " + scope.getFields() + "\n";
    }
}