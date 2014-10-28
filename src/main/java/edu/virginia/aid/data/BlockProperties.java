package edu.virginia.aid.data;

import java.util.*;

public class BlockProperties extends SourceElement {

    private List<IfStatementProperties> conditionals = new ArrayList<>();
    private List<LoopProperties> loops = new ArrayList<>();
    private ScopeProperties scope = new ScopeProperties();
    private ScopeProperties methodScope = new ScopeProperties();
    private List<MethodInvocationProperties> methodInvocations = new ArrayList<>();

    /**
     * Creates an element with the given location and context
     *
     * @param methodScope   The scope of the enclosing method
     * @param startPos      The start position of the element in the source context
     * @param endPos        The end position of the element in the source context
     * @param sourceContext The content of the file that contains the element
     */
    public BlockProperties(ScopeProperties methodScope, int startPos, int endPos, SourceContext sourceContext) {
        super(startPos, endPos, sourceContext);

        this.methodScope = methodScope;
    }

    /**
     * Adds a conditional to the list of conditionals for the block
     *
     * @param conditional The conditional to add
     */
    public void addConditional(IfStatementProperties conditional) {
        conditionals.add(conditional);
    }

    /**
     * Adds a loop to the list of loops for the block
     *
     * @param loop The loop to add
     */
    public void addLoop(LoopProperties loop) {
        loops.add(loop);
    }

    /**
     * Gets and returns all of the conditionals in the block
     *
     * @return The conditionals in the block
     */
    public List<IfStatementProperties> getConditionals() {
        return conditionals;
    }

    /**
     * Gets and returns all of the loops in the block
     *
     * @return The loops in the block
     */
    public List<LoopProperties> getLoops() {
        return loops;
    }

    public ScopeProperties getScope() {
        return scope;
    }

    /**
     * Returns all of the method invocations within the block
     *
     * @return The method invocations within the block
     */
    public List<MethodInvocationProperties> getMethodInvocations() {
        return methodInvocations;
    }
}
