package edu.virginia.aid.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * Information relevant to a method obtained from the containing class
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ClassInformation {

    private String className;

    // File path
    private String filepath;

    // Fields
    private List<IdentifierProperties> fields;

    // Method Declarations
    private List<MethodDeclaration> methodDeclarations;

    public ClassInformation(String className, String filepath) {
        this.className = className;
        this.filepath = filepath;
        fields = new ArrayList<>();
        methodDeclarations = new ArrayList<>();
    }

    public String getClassName() {
        return this.className;
    }

    public String getFilepath() {
        return filepath;
    }

    /**
     * Adds the given field to the class's fields
     *
     * @param field The field to add
     */
    public void addField(IdentifierProperties field) {
        fields.add(field);
    }

    /**
     * Finds and returns a field with the given name or null if none exists
     *
     * @param name The name of the field to find
     * @return The field with the given name (or null if none exists)
     */
    public IdentifierProperties getFieldByName(String name) {
        for (IdentifierProperties field : fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }

        return null;
    }

    /**
     * Finds and returns all field names in this class
     *
     * @return Set of all field names in the class
     */
    public Set<String> getFieldNames() {
        Set<String> names = new HashSet<String>();
        for (IdentifierProperties field : fields) {
            names.add(field.getName());
        }

        return names;
    }

    /**
     * Adds a MethodDeclaration node to the class information
     *
     * @param method The method to add
     */
    public void addMethodDeclaration(MethodDeclaration method) {
        methodDeclarations.add(method);
    }

    /**
     * Returns all of the MethodDeclarations associated with the class
     *
     * @return The list of method declarations associated with the class
     */
    public List<MethodDeclaration> getMethodDeclarations() {
        return methodDeclarations;
    }

    @Override
    public String toString() {
        return "Class " + className + ":\n" +
                "\tFields: " + fields;
    }
}
