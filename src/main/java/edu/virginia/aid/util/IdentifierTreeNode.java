package edu.virginia.aid.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Data object representing a node in a tree of method identifiers.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class IdentifierTreeNode {

    private String value;
    private List<String> references;

    /**
     * Constructs a default identifier tree node
     */
    public IdentifierTreeNode() {
    	this.value = "";
    	this.references = new ArrayList<>();
    }

    /**
     * Constructs an identifier tree node for a given value
     *
     * @param value The string value for the current identifier
     */
    public IdentifierTreeNode(String value) {
        this.value = value;
        this.references = new ArrayList<>();
    }

    /**
     * Adds a reference to the list of references
     * 
     * @param reference The reference to add
     */
    public void addReference(String reference) {
    	this.references.add(reference);
    }
    
    /**
     * Gets and returns the references list
     * 
     * @return The list of references
     */
    public List<String> getReferences() {
    	return this.references;
    }

    /**
     * Sets the value for this identifier node
     * 
     * @param value The new value to set
     */
    public void setValue(String value) {
    	this.value = value;
    }

    /**
     * Gets and returns the current value
     * 
     * @return The value of the current node
     */
    public String getValue() {
    	return this.value;
    }
}
