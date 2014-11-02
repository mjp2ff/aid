package edu.virginia.aid.parsers;

import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.data.MethodSignature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IndividualMethodParser extends MethodParser {

    Map<String, List<MethodSignature>> methods;

    /**
     * Constructs a parser pointing to the given file that parses the given methods
     *
     * @param methods Map of files to the internal methods to be parsed
     */
    public IndividualMethodParser(Map<String, List<MethodSignature>> methods) {
        this.methods = methods;
    }

    public List<MethodFeatures> parseMethods() {
        List<MethodFeatures> methodFeatures = new ArrayList<>();
        for (String filepath : methods.keySet()) {
            methodFeatures.addAll(getMethodsFromFile(filepath).stream()
                    .filter(method -> methods.get(filepath).contains(method.getMethodSignature()))
                    .collect(Collectors.toList()));
        }

        return methodFeatures;
    }
}
