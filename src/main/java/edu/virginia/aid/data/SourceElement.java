package edu.virginia.aid.data;

/**
 * Element that can be directly traced back to a portion of text from a source file
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class SourceElement {

    // The source of the file that contains the element
    private final SourceContext sourceContext;

    // Information about the location of the element in the source
    private int startPos;
    private int endPos;

    /**
     * Creates an element with the given location and context
     *
     * @param startPos The start position of the element in the source context
     * @param endPos The end position of the element in the source context
     * @param sourceContext The content of the file that contains the element
     */
    public SourceElement(int startPos, int endPos, final SourceContext sourceContext) {
        this.sourceContext = sourceContext;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public SourceContext getSourceContext() {
        return sourceContext;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    /**
     * Gets source text for the element based on its position information
     *
     * @return The element's text
     */
    public String getElementText() {
        return sourceContext.getText().substring(startPos, endPos);
    }

    /**
     * Finds the line number for the element's start
     *
     * @return The line number on which the element starts
     */
    public int getElementLineNumber() {
        return sourceContext.getLine(startPos);
    }

    @Override
    public String toString() {
        return getElementText();
    }
}
