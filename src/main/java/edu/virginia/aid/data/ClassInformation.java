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
public class ClassInformation extends SourceElement {

    /**
     * The name of the class
     */
    private String className;

    /**
     * The path to the file containing the class
     */
    private String filepath;

    /**
     * List of fields in the class
     */
    private List<IdentifierProperties> fields;

    /**
     * List of methods declared in the class
     */
    private List<MethodDeclaration> methodDeclarations;

    /**
     * List of comments contained within the method (not including method JavaDoc)
     */
    private List<CommentInfo> comments;

    /**
     * Creates a ClassInformation with the following properties:
     *
     * @param className         The name of the class
     * @param filepath          The path to the file containing the class
     * @param startPos          The position of the first character of the class in the file
     * @param endPos            The position of the last character of the class in the file
     * @param sourceContext     Information and content for the containing file
     */
    public ClassInformation(String className, String filepath, int startPos, int endPos, final SourceContext sourceContext) {
        super(startPos, endPos, sourceContext);

        this.className = className;
        this.filepath = filepath;
        this.fields = new ArrayList<>();
        this.methodDeclarations = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    /**
     * Get the name of the class
     *
     * @return The name of the class
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * Get the path to the file containing the class
     *
     * @return The path to the file containing the class
     */
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

    /**
     * Returns all of the Comments in this class.
     * 
     * @return The list of comments inside this class.
     */
    public List<CommentInfo> getComments() {
    	return this.comments;
    }

    /**
     * Adds a CommentInfo node to the comments
     * 
     * @param comment The comment to add
     */
    public void addComment(CommentInfo comment) {
    	comments.add(comment);
    }

    /**
     * Converts the class's information to a human-readable form
     *
     * @return Human-readable output for the class
     */
    @Override
    public String toString() {
        return "Class " + className + ":\n" +
                "\tFields: " + fields;
    }
}
