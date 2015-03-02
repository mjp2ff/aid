package edu.virginia.aid.data;

import java.util.Arrays;
import java.util.List;

import edu.virginia.aid.comparison.DifferenceWeights;
import edu.virginia.aid.util.StringHelper;

/**
 * Data wrapper for information about an individual variable declared/referenced in a method
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class IdentifierProperties extends SourceElement implements StringListable {

    /**
     * The type of value that the identifier refers to
     */
	public enum IdentifierContext {
		LOCAL_VARIABLE, FORMAL_PARAMETER, FIELD, METHOD
	}

    /**
     * The name of the identifier
     */
	private String name;

    /**
     * The type of the identifier (e.g. String)
     */
	private String type;

    /**
     * Type of value that the identifier is
     */
	private IdentifierContext context;

    /**
     * How many times the identifier is read/accessed
     */
    private int reads;

    /**
     * How many times the identifier is written/updated
     */
    private int writes;

    /**
     * The number of invocations of the identifier (method types only)
     */
    private int invocations;

    /**
     * Whether the identifier is in a return state
     */
    private boolean inReturnStatement;

	/**
	 * Whether or not the identifier has been processed at all.
	 */
	private boolean hasBeenProcessed;
	/**
	 * The name above after stemming/stoplist processing.
	 */
	private String processedName;

    /**
     * Creates an IdentifierProperties with the properties below:
     *
     * @param name          The name of the identifier
     * @param startPos      The position of the first character of the identifier declaration in the containing file
     * @param endPos        The position of the last character of the identifier declaration in the containing file
     * @param sourceContext Information and contents for the file containing the identifier
     */
	public IdentifierProperties(String name, int startPos, int endPos, final SourceContext sourceContext) {
        super(startPos, endPos, sourceContext);

        this.setName(name);
		this.type = null;
		this.context = null;
		this.hasBeenProcessed = false;
		this.processedName = name;

        this.reads = 0;
        this.writes = 0;
        this.invocations = 0;
        this.inReturnStatement = false;
	}

    /**
     * Creates an IdentifierProperties with the properties below:
     *
     * @param name          The name of the identifier
     * @param type          The type of the identifier (e.g. String)
     * @param context       The type of use of the identifier
     * @param startPos      The position of the first character of the identifier declaration in the containing file
     * @param endPos        The position of the last character of the identifier declaration in the containing file
     * @param sourceContext Information and contents for the file containing the identifier
     */
	public IdentifierProperties(String name, String type, IdentifierContext context, int startPos, int endPos, final SourceContext sourceContext) {
        super(startPos, endPos, sourceContext);

        this.setName(name);
		this.type = type;
		this.context = context;
		this.hasBeenProcessed = false;
		this.processedName = name;

        this.reads = 0;
        this.writes = 0;
        this.invocations = 0;
        this.inReturnStatement = false;
	}

    /**
     * Copy constructor
     *
     * @param properties Object to copy
     */
    public IdentifierProperties(IdentifierProperties properties) {
        super(properties.getStartPos(), properties.getEndPos(), properties.getSourceContext());

        this.name = properties.getName();
        this.type = properties.getType();
        this.context = properties.getContext();
        this.hasBeenProcessed = properties.hasBeenProcessed();
        this.processedName = properties.getProcessedName();

        this.reads = 0;
        this.writes = 0;
        this.invocations = 0;
        this.inReturnStatement = false;
    }

    /**
     * Sets the type of the identifier (e.g. String)
     *
     * @param type the type of the identifier (e.g. String)
     */
	public void setType(String type) {
		this.type = type;
	}

    /**
     * Sets the the type of value for the identifier
     *
     * @param context The type of value for the identifier
     */
	public void setContext(IdentifierContext context) {
		this.context = context;
	}

    /**
     * Sets the name of the identifier, splitting into words based on camel case
     *
     * @param name The new name for the identifier
     */
	public void setName(String name) {
		this.name = StringHelper.splitCamelCase(name);
	}

    /**
     * Updates the processed name of the identifier with the result of a processing step
     *
     * @param processedName The new processed name of the identifier
     */
	public void setProcessedName(String processedName) {
		this.processedName = processedName;
		this.hasBeenProcessed = true;
	}

    /**
     * Gets the type (e.g. String) of the identifier
     *
     * @return The type (e.g. String) of the identifier
     */
	public String getType() {
		return this.type;
	}

    /**
     * Gets the type of value of the identifier
     *
     * @return The type of value that the identifier is
     */
	public IdentifierContext getContext() {
		return this.context;
	}

    /**
     * Generates a string representation of the IdentifierContext
     *
     * @return String representation of the IdentifierContext
     */
    public String getContextString() {
        switch (this.context) {
            case FIELD:
                return "field";
            case FORMAL_PARAMETER:
                return "parameter";
            case LOCAL_VARIABLE:
                return "variable";
            case METHOD:
                return "method";
            default:
                return "unknown context";
        }
    }

    /**
     * Gets the name of the identifier
     *
     * @return The name of the identifier
     */
	public String getName() {
		return this.name;
	}

    /**
     * Tests whether or not the name of the identifier has been processed
     *
     * @return
     */
	public boolean hasBeenProcessed() {
		return this.hasBeenProcessed;
	}

    /**
     * Gets the processed name for the identifier
     *
     * @return The processed name for the identifier
     */
	public String getProcessedName() {
		return this.processedName;
	}

    /**
     * Gets the number of times that the identifier has been read
     *
     * @return The number of times that the identifier has been read
     */
    public int getReads() {
        return reads;
    }

    /**
     * Gets the number of times that the identifier had been written to
     *
     * @return The number of times that the identifier had been written to
     */
    public int getWrites() {
        return writes;
    }

    /**
     * Gets the number of times that the identifier has been invoked (only applicable for method identifiers)
     *
     * @return The number of times that the identifier has been invoked
     */
    public int getInvocations() {
        return invocations;
    }

    /**
     * Adds the given amount to the current value of the variable reads
     *
     * @param amount The amount to add
     */
    public void addReads(int amount) {
        reads += amount;
    }

    /**
     * Adds the given amount to the current value of the variable writes
     *
     * @param amount The amount to add
     */
    public void addWrites(int amount) {
        writes += amount;
    }

    /**
     * Adds the given amount to the current value of the variable invocations
     *
     * @param amount The amount to add
     */
    public void addInvocations(int amount) {
        invocations += amount;
    }

    /**
     * Tests whether the identifier is contained in a return statement
     *
     * @return
     */
    public boolean isInReturnStatement() {
        return inReturnStatement;
    }

    /**
     * Sets a flag as to whether an identifier appears in a return statement
     *
     * @param inReturnStatement boolean expressing whether or not the identifier has appeared in a return statement
     */
    public void setInReturnStatement(boolean inReturnStatement) {
        this.inReturnStatement = inReturnStatement;
    }

    /**
     * Gets the list of words that makes up the processed name of the identifier
     *
     * @return The list of words that makes up the processed name of the identifier
     */
    public List<String> getData() {
    	return Arrays.asList(processedName);
    }

    /**
     * Tests if two IdentifierProperties are equal
     *
     * @param o The identifier to compare to
     * @return Whether or not the two identifiers are equal
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof IdentifierProperties) {
            IdentifierProperties other = (IdentifierProperties) o;
            return this.getName().equals(other.getName()) && this.getContext() == other.getContext();
        }
        return false;
    }

    /**
     * Computes the hash code for the function using the name and context
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return name.hashCode() ^ context.ordinal();
    }

    /**
     * Gets the name of the identifier
     *
     * @return The name of the identifier
     */
    @Override
	public String toString() {
		return name;
	}

    /**
     * Gets the weighted value of the identifier relative to the total variable reads/writes for the method
     *
     * @param scope The method's scope
     * @return Difference value (does not include NLP adjustments like TF/IDF)
     */
    public double getReadWriteDifferenceValue(ScopeProperties scope) {
        double maxDifference = scope.getNumFieldReads() * DifferenceWeights.FIELD_READ +
                scope.getNumFieldWrites() * DifferenceWeights.FIELD_WRITE +
                scope.getNumParameterReads() * DifferenceWeights.PARAMETER_READ +
                scope.getNumParameterWrites() * DifferenceWeights.PARAMETER_WRITE;

        double identifierDifference = getReads() * (context == IdentifierContext.FIELD ? DifferenceWeights.FIELD_READ : DifferenceWeights.PARAMETER_READ) +
                getWrites() * (context == IdentifierContext.FIELD ? DifferenceWeights.FIELD_WRITE : DifferenceWeights.PARAMETER_WRITE);

        return (identifierDifference / maxDifference) * DifferenceWeights.VARIABLE_USAGE;
    }
}
