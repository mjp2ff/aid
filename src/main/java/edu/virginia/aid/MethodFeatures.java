package edu.virginia.aid;

import java.util.*;

/**
 * Data wrapper for a feature list for a single method
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class MethodFeatures {

    private String methodName;
    private Map<String, Boolean> booleanFeatures;
    private List<IdentifierProperties> identifiers;

    public MethodFeatures(String methodName) {
        this.methodName = methodName;
        this.booleanFeatures = new HashMap<String, Boolean>();
        this.identifiers = new ArrayList<IdentifierProperties>();
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
     * Removes a boolean feature with the given name
     *
     * @param name Name of the boolean feature to remove
     * @return Whether or not the boolean feature was removed
     */
    public boolean removeBooleanFeature(String name) {
        booleanFeatures.remove(name);
        return booleanFeatures.containsKey(name);
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
     * Gets and returns the value of the boolean feature with the given name
     *
     * @param name The name of the boolean feature to search for
     * @return The value of the feature (or null if it is not present)
     */
    public boolean getBooleanFeature(String name) {
        return booleanFeatures.get(name);
    }

    /**
     * Adds the identifier to the method's identifier list
     *
     * @param properties Identifier information
     */
    public void addIdentifier(IdentifierProperties properties) {
        identifiers.add(properties);
    }

    /**
     * Removes all identifiers present with the given name
     *
     * @param name The name of the identifier(s) to remove
     */
    public void removeIdentifierByName(String name) {
        for (IdentifierProperties identifier : identifiers) {
            if (identifier.getName() == name) {
                identifiers.remove(identifier);
            }
        }
    }

    /**
     * Finds and returns all identifiers with the given name
     *
     * @param name The name of the identifier(s) to remove
     * @return All identifiers with the given name
     */
    public List<IdentifierProperties> getIdentifiersByName(String name) {
        List<IdentifierProperties> identifiersToReturn = new ArrayList<IdentifierProperties>();

        for (IdentifierProperties identifier : identifiers) {
            if (identifier.getName() == name) {
                identifiersToReturn.add(identifier);
            }
        }

        return identifiersToReturn;
    }

    /**
     * Finds and returns all identifier names in this method
     *
     * @return Set of all identifier names in the method
     */
    public Set<String> getIdentifierNames() {
        Set<String> names = new HashSet<String>();
        for (IdentifierProperties identifier : identifiers) {
            names.add(identifier.getName());
        }

        return names;
    }
}
