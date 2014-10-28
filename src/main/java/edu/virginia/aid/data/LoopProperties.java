package edu.virginia.aid.data;

public class LoopProperties extends SourceElement {

    ConditionProperties condition;
    BlockProperties loopBody;
    IdentifierProperties iteratedElement;

    /**
     * Creates an element with the given loop information, location and context
     *
     * @param condition     The loop condition, if one exists
     * @param loopBody      The block information for the body of the loop
     * @param iteratedElement   The element that is iterated over (in the case of an iterator loop)
     * @param startPos      The start position of the element in the source context
     * @param endPos        The end position of the element in the source context
     * @param sourceContext The content of the file that contains the element
     */
    public LoopProperties(ConditionProperties condition, BlockProperties loopBody, IdentifierProperties iteratedElement,
                          int startPos, int endPos, SourceContext sourceContext) {

        super(startPos, endPos, sourceContext);

        this.condition = condition;
        this.loopBody = loopBody;
        this.iteratedElement = iteratedElement;
    }


}