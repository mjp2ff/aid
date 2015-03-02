package edu.virginia.aid.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Container and associated operations for all of the identifiers that are in scope for a certain block of code.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ScopeProperties {

    /**
     * The list of identifiers in scope
     */
    public List<IdentifierProperties> identifiers;

    /**
     * The scope for the containing block/class
     */
    public ScopeProperties parentScope;

    /**
     * Creates a new ScopeProperties with no identifiers in scope
     */
    public ScopeProperties() {
        identifiers = new ArrayList<>();
    }

    /**
     * Creates a new ScopeProperties with the given parent scope
     *
     * @param parent The parent scope of this scope
     */
    public ScopeProperties(ScopeProperties parent) {
        identifiers = new ArrayList<>();
        parentScope = parent;
    }

    /**
     * Adds a new identifier to the scope
     *
     * @param identifier The identifier to add
     */
    public void addVariable(IdentifierProperties identifier) {
        identifiers.add(identifier);
    }

    /**
     * Adds an identifier use to the identifiers in the method
     *
     * @param identifierUse The identifier use to add
     */
    public void addIdentifierUse(IdentifierName identifierUse) {
        if (identifierUse.isVariable()) {

            IdentifierProperties identifierProperties;
            if (!identifierUse.hasClassScope()) {
                identifierProperties = getClosestVariable(identifierUse.getName());
                if (identifierProperties == null && parentScope != null) {
                    IdentifierProperties parentVariable = parentScope.getClosestVariable(identifierUse.getName());
                    if (parentVariable != null) {
                        addVariable(new IdentifierProperties(parentVariable));
                        identifierProperties = parentVariable;
                    }
                }
            } else {
                identifierProperties = getField(identifierUse.getName());
                if (identifierProperties == null && parentScope != null) {
                    IdentifierProperties parentVariable = parentScope.getField(identifierUse.getName());
                    if (parentVariable != null) {
                        addVariable(new IdentifierProperties(parentVariable));
                        identifierProperties = parentVariable;
                    }
                }
            }

            if (identifierProperties != null) {
                if (identifierUse.getUse() == IdentifierUse.READ) {
                    identifierProperties.addReads(1);
                } else if (identifierUse.getUse() == IdentifierUse.WRITE) {
                    identifierProperties.addWrites(1);
                } else if (identifierUse.getUse() == IdentifierUse.INVOCATION) {
                    identifierProperties.addInvocations(1);
                }
            }
        }
    }

    /**
     * Finds and returns the closest scoped variable with the given name
     *
     * @param name Name of the variable to return
     * @return Closest scoped variable
     */
    public IdentifierProperties getClosestVariable(String name) {

        // Search local variables
        IdentifierProperties localVariable = getLocalVariable(name);
        if (localVariable != null) {
            return localVariable;
        }

        // Search parameters
        IdentifierProperties parameter = getParameter(name);
        if (parameter != null) {
            return parameter;
        }

        // Search fields
        IdentifierProperties field = getField(name);
        if (field != null) {
            return field;
        }

        // Return null if none found
        return null;
    }

    /**
     * Finds and returns the local variable with the given name, or null if none exists
     *
     * @param name Name of the variable to find
     * @return Local variable with name or null if none exists
     */
    public IdentifierProperties getLocalVariable(String name) {
        return searchIdentifierSet(name, identifiers
                .stream()
                .filter(p -> p.getContext() == IdentifierProperties.IdentifierContext.LOCAL_VARIABLE)
                .collect(Collectors.toSet()));
    }

    /**
     * Finds and returns the parameter with the given name, or null if none exists
     *
     * @param name Name of the parameter to find
     * @return Parameter with name or null if none exists
     */
    public IdentifierProperties getParameter(String name) {
        return searchIdentifierSet(name, identifiers
                .stream()
                .filter(p -> p.getContext() == IdentifierProperties.IdentifierContext.FORMAL_PARAMETER)
                .collect(Collectors.toSet()));
    }

    /**
     * Finds and returns the field with the given name, or null if none exists
     *
     * @param name Name of the field to find
     * @return Field with the name or null if none exists
     */
    public IdentifierProperties getField(String name) {
        return searchIdentifierSet(name, identifiers
                .stream()
                .filter(p -> p.getContext() == IdentifierProperties.IdentifierContext.FIELD)
                .collect(Collectors.toSet()));
    }

    /**
     * Gets a list of all identifiers in the scope
     *
     * @return List of all identifiers in the scope
     */
    public List<IdentifierProperties> getIdentifiers() {
        return identifiers;
    }

    /**
     * Private helper method which finds and returns an identifier from a set if
     * its name matches the search name, or null if it is not found
     *
     * @param name The name of the variable to find
     * @param set The set to search for the variable name
     * @return The identifier with the given name or null if not found
     */
    private static IdentifierProperties searchIdentifierSet(String name, Set<IdentifierProperties> set) {
        for (IdentifierProperties identifier : set) {
            if (identifier.getName().equals(name)) {
                return identifier;
            }
        }

        return null;
    }

    /**
     * Returns all identifiers with type local variable
     *
     * @return Local variables
     */
    public List<IdentifierProperties> getLocalVariables() {
        return identifiers
                .stream()
                .filter(p -> p.getContext() == IdentifierProperties.IdentifierContext.LOCAL_VARIABLE)
                .collect(Collectors.toList());
    }

    /**
     * Returns all identifiers in scope with type parameter
     *
     * @return Parameters
     */
    public List<IdentifierProperties> getParameters() {
        return identifiers
                .stream()
                .filter(p -> p.getContext() == IdentifierProperties.IdentifierContext.FORMAL_PARAMETER)
                .collect(Collectors.toList());
    }

    /**
     * Returns all identifiers in scope with type field
     *
     * @return Fields
     */
    public List<IdentifierProperties> getFields() {
        return identifiers
                .stream()
                .filter(p -> p.getContext() == IdentifierProperties.IdentifierContext.FIELD)
                .collect(Collectors.toList());
    }

    /**
     * Finds and returns all identifier processed names in this method
     *
     * @return Set of all identifier processed names in the method
     */
    public Set<String> getIdentifierProcessedNames() {
        Set<String> names = new HashSet<String>();

        // Get Parameters
        getParameters().stream()
                .filter(parameter -> parameter.hasBeenProcessed())
                .forEach(parameter -> names.add(parameter.getProcessedName()));

        // Get Local Variables
        getLocalVariables().stream()
                .filter(localVar -> localVar.hasBeenProcessed())
                .forEach(localVar -> names.add(localVar.getProcessedName()));

        // Get Fields
        getFields().stream()
                .filter(field -> field.hasBeenProcessed())
                .forEach(field -> names.add(field.getProcessedName()));

        return names;
    }

    /**
     * Gets the total number of field reads in this scope
     *
     * @return Total number of field reads
     */
    public int getNumFieldReads() {
        return getFields()
                .stream()
                .mapToInt(f -> f.getReads())
                .sum();
    }

    /**
     * Gets the total number of field writes in this scope
     *
     * @return Total number of field writes
     */
    public int getNumFieldWrites() {
        return getFields()
                .stream()
                .mapToInt(f -> f.getWrites())
                .sum();
    }

    /**
     * Gets the total number of parameter reads in this scope
     *
     * @return Total number of parameter reads
     */
    public int getNumParameterReads() {
        return getParameters()
                .stream()
                .mapToInt(p -> p.getReads())
                .sum();
    }

    /**
     * Gets the total number of parameter writes in this scope
     *
     * @return Total number of parameter writes
     */
    public int getNumParameterWrites() {
        return getParameters()
                .stream()
                .mapToInt(p -> p.getWrites())
                .sum();
    }
}