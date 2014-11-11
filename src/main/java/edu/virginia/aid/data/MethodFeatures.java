package edu.virginia.aid.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.Type;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.virginia.aid.comparison.DifferenceWeights;
import edu.virginia.aid.comparison.GenericDifference;
import edu.virginia.aid.comparison.MethodDifferences;
import edu.virginia.aid.comparison.MissingIdentifierDifference;

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
    private Map<String, Integer> numericFeatures;
    private ScopeProperties scope;
    private Javadoc javadoc;
    private Map<String, Double> TFIDF;
    private Map<String, Integer> wordFrequencies;
    private Set<String> allWordsNoComments;

    private String primaryAction;
    private String primaryObject;

    // Boolean parameters
    public static final String RETURNS_BOOLEAN = "returns_boolean";
    public static final String IS_CONSTRUCTOR = "is_constructor";
    public static final String ONE_FIELD_INVOKED_OR_WRITTEN = "one_field_invoked_or_written";

    // Numeric parameters
    public static final String NUM_PARAM_READS = "num_param_reads";
    public static final String NUM_FIELD_READS = "num_field_reads";
    public static final String NUM_FIELD_WRITES = "num_field_writes";

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

    public String getPrimaryAction() {
        return primaryAction;
    }

    public void setPrimaryAction(String primaryAction) {
        this.primaryAction = primaryAction;
    }

    public String getPrimaryObject() {
        return primaryObject;
    }

    public void setPrimaryObject(String primaryObject) {
        this.primaryObject = primaryObject;
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
     * Calculate the word frequencies for this method, and return a list of all the words in the method.
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

    public Javadoc getJavadoc() {
        return javadoc;
    }

    public void setJavadoc(Javadoc javadoc) {
        this.javadoc = javadoc;
    }

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
                double differenceScore = ((DifferenceWeights.FIELD_READ * field.getReads()) +
                        (DifferenceWeights.FIELD_WRITE * field.getWrites()) +
                        (field.isInReturnStatement() ? DifferenceWeights.IN_RETURN_STATEMENT : 0)) * getTFIDF(identifier);
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
                double differenceScore = ((DifferenceWeights.PARAMETER_READ * parameter.getReads()) +
                        (DifferenceWeights.PARAMETER_WRITE * parameter.getWrites()) +
                        (parameter.isInReturnStatement() ? DifferenceWeights.IN_RETURN_STATEMENT : 0)) * getTFIDF(identifier);
                if (differenceScore > 0) {
                    differences.add(new MissingIdentifierDifference(parameter, differenceScore));
                }
            }
        }

        if (!containedInComments(wordNetDictionary, primaryAction)) {
            differences.add(new GenericDifference("The primary method action (" + primaryAction + ") is not discussed in the comments", DifferenceWeights.PRIMARY_VERB * getTFIDF(primaryAction)));
        }

        if (!containedInComments(wordNetDictionary, primaryObject)) {
            differences.add(new GenericDifference("The primary object acted upon (" + primaryObject + ") is not discussed in the comments", DifferenceWeights.PRIMARY_OBJECT * getTFIDF(primaryObject)));
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
    	Double retVal = TFIDF.get(s);
    	return retVal != null ? retVal : 1.0;
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
        List<String> synonymsList = new ArrayList<>(synonyms);
        for (int i = 0; i < synonymsList.size() && !foundInComments; ++i) {
        	String synonym = synonymsList.get(i);
    		for (CommentInfo comment : getComments()) {
                if (comment.getCommentText().contains(synonym)) {
                    foundInComments = true;
                    break;
                }	
        	}

            Javadoc javadocElem = getJavadoc();
            if (!foundInComments && javadocElem != null && javadocElem.toString().contains(synonym)) {
            	foundInComments = true;
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

    @Override
    public String toString() {
        return "Method " + getMethodSignature() + ":\n" +
                "\tParameters: " + scope.getParameters() + "\n" +
                "\tLocal Variables: " + scope.getLocalVariables() + "\n" +
                "\tFields: " + scope.getFields() + "\n";
    }
}
