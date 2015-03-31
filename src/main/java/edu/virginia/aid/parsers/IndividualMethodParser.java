package edu.virginia.aid.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.data.MethodSignature;

/**
 * Parser for finding and processing specific methods as defined by an external description file.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class IndividualMethodParser extends MethodParser {

    /**
     * Mapping of file paths to the methods in that file to parse
     */
    Map<String, List<MethodSignature>> methods;

    /**
     * Constructs a parser pointing to the given file that parses the given methods
     *
     * @param methods Map of files to the internal methods to be parsed
     * @param documentedOnly Whether to exclude methods that have no javadoc summary
     */
    public IndividualMethodParser(Map<String, List<MethodSignature>> methods, boolean documentedOnly) {
        super(documentedOnly);
        this.methods = methods;
    }

    /**
     * Finds and returns all of the methods to be processed based on the mappings provided on initialization.
     *
     * @param trainingMode Whether the methods to parse are being used to generate a training set
     *
     * @return List of property objects, one for each method parsed
     */
    protected List<MethodFeatures> parseMethods(boolean trainingMode) {
        List<MethodFeatures> methodFeatures = new ArrayList<>();
        for (String filepath : methods.keySet()) {
            methodFeatures.addAll(getMethodsFromFile(filepath, trainingMode).stream()
                    .filter(method -> methods.get(filepath).contains(method.getMethodSignature()))
                    .collect(Collectors.toList()));
        }

        return methodFeatures;
    }
}
