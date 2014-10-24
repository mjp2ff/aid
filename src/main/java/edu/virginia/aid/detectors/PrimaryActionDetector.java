package edu.virginia.aid.detectors;

import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Detector that finds a single verb to describe the method
 */
public class PrimaryActionDetector implements FeatureDetector {

    /**
     * Examines identifier use and control flow in the method to determine the
     * primary action of the method, summed up in a single verb
     *
     * @param method The method to process
     * @param features The parsed features object to update when processing
     */
    @Override
    public void process(MethodDeclaration method, MethodFeatures features) {

        // Get all identifiers that have been read from
        List<IdentifierProperties> readIdentifiers = features.getIdentifiers().stream()
                .filter(p -> p.getReads() > 0 && p.getContext() != IdentifierProperties.IdentifierContext.LOCAL_VARIABLE)
                .collect(Collectors.toList());

        // Get all identifiers that have been written to
        List<IdentifierProperties> writeIdentifiers = features.getIdentifiers().stream()
                .filter(p -> p.getWrites() > 0 && p.getContext() != IdentifierProperties.IdentifierContext.LOCAL_VARIABLE)
                .collect(Collectors.toList());

        if (readIdentifiers.size() == 1 && writeIdentifiers.size() == 0) {
            features.addStringFeature(MethodFeatures.PRIMARY_VERB, "gets");
            features.addStringFeature(MethodFeatures.PRIMARY_OBJECT, readIdentifiers.get(0).getProcessedName());
        } else if (writeIdentifiers.size() == 1) {
            features.addStringFeature(MethodFeatures.PRIMARY_VERB, "sets");
            features.addStringFeature(MethodFeatures.PRIMARY_OBJECT, writeIdentifiers.get(0).getProcessedName());
        }
    }
}