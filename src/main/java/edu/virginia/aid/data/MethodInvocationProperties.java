package edu.virginia.aid.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Data wrapper for a method invocation within a source file
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class MethodInvocationProperties extends IdentifierProperties {

    private List<IdentifierName> unresolvedArguments;

    /**
     * Creates a method invocation with the given name, location and context
     *
     * @param name The method name
     * @param startPos The start position of the invocation
     * @param endPos The end position of the invocation
     * @param sourceContext The source file for the invocation
     */
    public MethodInvocationProperties(String name, int startPos, int endPos, SourceContext sourceContext) {
        super(name, startPos, endPos, sourceContext);
        setContext(IdentifierContext.METHOD);

        unresolvedArguments = new ArrayList<>();
    }

    /**
     * Adds all of the arguments given to the list of arguments for the method
     *
     * @param arguments The arguments to add
     */
    public void addArguments(Collection<IdentifierName> arguments) {
        unresolvedArguments.addAll(arguments);
    }

    /**
     * Adds the single argument given to the list of arguments ofr the method
     *
     * @param argument The argument to add
     */
    public void addArgument(IdentifierName argument) {
        unresolvedArguments.add(argument);
    }
}
