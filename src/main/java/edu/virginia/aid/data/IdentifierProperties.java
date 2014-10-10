package edu.virginia.aid.data;

/**
 * Data wrapper for information about an individual variable declared/referenced in a method
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class IdentifierProperties extends SourceElement {

	public enum IdentifierContext {
		LOCAL_VARIABLE, FORMAL_PARAMETER, FIELD
	}

	private String name;
	private String type;
	private IdentifierContext context;

    private int reads;
    private int writes;

	/**
	 * Whether or not the identifier has been processed at all.
	 */
	private boolean hasBeenProcessed;
	/**
	 * The name above after stemming/stoplist processing.
	 */
	private String processedName;

	public IdentifierProperties(String name, int startPos, int endPos, final String sourceContext) {
        super(startPos, endPos, sourceContext);

        this.setName(name);
		this.type = null;
		this.context = null;
		this.hasBeenProcessed = false;
		this.processedName = name;

        this.reads = 0;
        this.writes = 0;
	}

	public IdentifierProperties(String name, String type, IdentifierContext context, int startPos, int endPos, final String sourceContext) {
        super(startPos, endPos, sourceContext);

        this.setName(name);
		this.type = type;
		this.context = context;
		this.hasBeenProcessed = false;
		this.processedName = name;

        this.reads = 0;
        this.writes = 0;
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
    }

	public void setType(String type) {
		this.type = type;
	}

	public void setContext(IdentifierContext context) {
		this.context = context;
	}

	public void setName(String name) {
		this.name = splitCamelCase(name);
	}

	public void setProcessedName(String processedName) {
		this.processedName = processedName;
		this.hasBeenProcessed = true;
	}

	public String getType() {
		return this.type;
	}

	public IdentifierContext getContext() {
		return this.context;
	}

	public String getName() {
		return this.name;
	}

	public boolean hasBeenProcessed() {
		return this.hasBeenProcessed;
	}

	public String getProcessedName() {
		return this.processedName;
	}

    public int getReads() {
        return reads;
    }

    public int getWrites() {
        return writes;
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

    @Override
	public String toString() {
		return name;
	}

    // TODO: Move this method to somewhere it belongs.
    /**
     * Helper method to replace camel casing with spaces between words. Regex found from NPE on StackOverflow.
     * 
     * @param s String in camelCase form
     * @return String with spaces instead of camelCase
     */
    private String splitCamelCase(String s) {
//    	String[] wordSplit = s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
//    	String newWord = "";
//    	for (String w : wordSplit) {
//    		newWord += w + " ";
//        }
//    	return newWord.trim();
    	// TODO: Fix this cuz I broke it :(
    	return s;
    }
}
