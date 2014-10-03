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

		this.name = name;
		this.type = null;
		this.context = null;
		this.hasBeenProcessed = false;
		this.processedName = name;
	}

	public IdentifierProperties(String name, String type, IdentifierContext context, int startPos, int endPos, final String sourceContext) {
        super(startPos, endPos, sourceContext);

		this.name = name;
		this.type = type;
		this.context = context;
		this.hasBeenProcessed = false;
		this.processedName = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setContext(IdentifierContext context) {
		this.context = context;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public String toString() {
		return name;
	}
}
