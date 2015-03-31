package edu.virginia.aid.parsers;

import java.util.List;

import edu.virginia.aid.data.MethodFeatures;

/**
 * Parses methods for a single file input
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class FileMethodParser extends MethodParser {

    private String filePath;

    /**
     * Constructs a parser pointing to the given file
     *
     * @param filePath The path to the source file to consider
     * @param documentedOnly Whether to exclude methods that have no javadoc summary
     */
    public FileMethodParser(String filePath, boolean documentedOnly) {
        super(documentedOnly);
        this.filePath = filePath;
    }

    /**
     * Parses and returns all methods contained within the source file
     *
     * @return Processed methods from file
     */
    protected List<MethodFeatures> parseMethods(boolean trainingMode) {
        return getMethodsFromFile(this.filePath, trainingMode);
    }
}
