package edu.virginia.aid.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SourceContext {

    private final String text;
    private final Integer[] newlinePositions;

    public SourceContext(final String text) {
        this.text = text;

        // Loop through characters in string looking for newlines
        List<Integer> newlinePositions = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                newlinePositions.add(i);
            }
        }

        this.newlinePositions = newlinePositions.toArray(new Integer[newlinePositions.size()]);
    }

    public String getText() {
        return this.text;
    }

    /**
     * Gets and returns the line number for the character position provided
     *
     * @param charPosition The character index for which to determine the line number
     * @return The line number of the character
     */
    public int getLine(int charPosition) {
        return -1 * Arrays.binarySearch(newlinePositions, charPosition);
    }
}
