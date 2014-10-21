package edu.virginia.aid.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data wrapper for information gleaned from a generic expression
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ExpressionInfo implements StringListable {

    private List<IdentifierProperties> identifiers = new ArrayList<>();

    /**
     * Adds an identifier to the list of those used in an expression
     *
     * @param identifier The identifier to add to the expression
     * @return Whether or not the identifier was added to the list
     */
    public boolean addIdentifier(IdentifierProperties identifier) {
        if (identifier != null) {
            identifiers.add(identifier);
            return true;
        } else {
            return false;
        }
    }

    public List<IdentifierProperties> getIdentifiers() {
        return identifiers;
    }
    
    public List<String> getData() {
    	ArrayList<String> retVal = new ArrayList<>();
    	for (IdentifierProperties identifier : identifiers) {
    		retVal.addAll(identifier.getData());
    	}
    	return retVal;
    }
}

