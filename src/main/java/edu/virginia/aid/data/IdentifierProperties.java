package edu.virginia.aid.data;

/**
 * Data wrapper for information about an individual variable declared/referenced in a method
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class IdentifierProperties {

	public enum IdentifierContext {
		LOCAL_VARIABLE, FORMAL_PARAMETER, FIELD
	}

	private String name;
	private String type;
	private IdentifierContext context;

	/**
	 * The name above after stemming/stoplist processing.
	 */
	private String processedName;

	public IdentifierProperties(String name) {
		this.name = name;
		this.type = null;
		this.context = null;
	}

	public IdentifierProperties(String name, String type, IdentifierContext context) {
		this.name = name;
		this.type = type;
		this.context = context;
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

	public String getProcessedName() {
		return this.processedName;
	}

	@Override
	public String toString() {
		return name;
	}
}
