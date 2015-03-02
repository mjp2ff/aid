package edu.virginia.aid.data;

import java.util.Arrays;
import java.util.List;

/**
 * Data wrapper for unresolved identifier names
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class IdentifierName extends SourceElement implements StringListable {

    public String name;
    public IdentifierType type;
    public IdentifierScope scope;
    public IdentifierUse use;

    public String processedName;

    /**
     * Creates a new IdentifierName with the name and properties passed in
     *
     * @param name The string name of the identifier
     * @param type The type of the identifier
     * @param scope The closest scope of the identifier
     * @param use The type of use of the identifier
     * @param startPos Start position of the identifier use
     * @param endPos End position of the identifier use
     * @param sourceContext The source context of the element
     */
    public IdentifierName(String name, IdentifierType type, IdentifierScope scope, IdentifierUse use, int startPos, int endPos, SourceContext sourceContext) {
        super(startPos, endPos, sourceContext);

        this.name = name;
        this.type = type;
        this.scope = scope;
        this.use = use;

        this.processedName = name;
    }

    /**
     * Creates a resolved identifier from the information contained within the unresolved identifier
     *
     * @return Resolved identifier
     */
    public IdentifierProperties buildResolvedIdentifier() {
        if (type == IdentifierType.METHOD) {
            return new MethodInvocationProperties(name, getStartPos(), getEndPos(), getSourceContext());
        } else {
            IdentifierProperties.IdentifierContext context = (scope == IdentifierScope.CLASS ?
                    IdentifierProperties.IdentifierContext.FIELD : IdentifierProperties.IdentifierContext.LOCAL_VARIABLE);

            return new IdentifierProperties(name, "", context, getStartPos(), getEndPos(), getSourceContext());
        }
    }

    /**
     * Finds and returns the resolved identifier associated with the name, if it exists
     *
     * @param features The method to search for a resolved identifier
     * @return The resolved identifier or null if none exists
     */
    public IdentifierProperties getResolvedIdentifier(MethodFeatures features) {
        if (type == IdentifierType.VARIABLE) {
            if (scope == IdentifierScope.CLASS) {
                return features.getScope().getField(name);
            } else {
                return features.getScope().getClosestVariable(name);
            }
        }

        // Return null for methods
        return null;
    }

    /**
     * Gets the name of the identifier
     *
     * @return The name of the identifier
     */
    public String getName() {
        return name;
    }

    /**
     * Tests whether or not the identifier exists within class scope
     *
     * @return Whether or not the identifier exists within class scope
     */
    public boolean hasClassScope() {
        return scope == IdentifierScope.CLASS;
    }

    /**
     * Tests whether or not the identifier is a variable
     *
     * @return Whether or not the identifier is a variable
     */
    public boolean isVariable() {
        return type == IdentifierType.VARIABLE;
    }

    /**
     * Gets the type of the use of the identifier
     *
     * @return The type of the use of the identifier
     */
    public IdentifierUse getUse() {
        return use;
    }

    /**
     * Gets the name of the identifier after processing (e.g. stemmed)
     *
     * @return The name of the identifier after processing
     */
    public String getProcessedName() {
        return processedName;
    }

    /**
     * Updates the processed name of the identifier
     *
     * @param processedName The newly processed identifier name
     */
    public void setProcessedName(String processedName) {
        this.processedName = processedName;
    }

    /**
     * Creates a singleton list of the processed name of the identifier
     *
     * @return A singleton list of the processed name of the identifier
     */
    public List<String> getData() {
    	return Arrays.asList(processedName);
    }
}
