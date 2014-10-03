package edu.virginia.aid.parsers;

import edu.virginia.aid.data.MethodFeatures;

import java.util.List;

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
     */
    public FileMethodParser(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Parses and returns all methods contained within the source file
     *
     * @return Processed methods from file
     */
    public List<MethodFeatures> parseMethods() {
        return getMethodsFromFile(this.filePath);
    }
}
