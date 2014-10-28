package edu.virginia.aid.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Data wrapper for conditional statements
 *
 * @author Mat Pearson-Beck & Jeff Principe
 */
public class IfStatementProperties extends SourceElement {

    private Map<ConditionProperties, BlockProperties> blocks;

    /**
     * Creates an element with the given location and context
     *
     * @param startPos      The start position of the element in the source context
     * @param endPos        The end position of the element in the source context
     * @param sourceContext The content of the file that contains the element
     */
    public IfStatementProperties(int startPos, int endPos, SourceContext sourceContext) {
        super(startPos, endPos, sourceContext);

        blocks = new HashMap<>();
    }

    public void addBranch(ConditionProperties condition, BlockProperties block) {
        blocks.put(condition, block);
    }
}
