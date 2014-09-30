package edu.virginia.aid;

import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.detectors.FeatureDetector;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * The MethodProcessor analyzes an individual MethodDeclaration and runs a set of
 * FeatureDetectors on that method, returning a MethodFeatures object containing
 * the parsed information.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class MethodProcessor {

    /**
     * The list of FeatureDetectors to run on the method
     */
    private List<FeatureDetector> detectors = new ArrayList<FeatureDetector>();

    /**
     * The method to run the detectors on
     */
    private MethodDeclaration method;

    private String className;
    private String filepath;

    /**
     * Creates a new MethodProcessor with the method to process
     *
     * @param method The method to process
     */
    public MethodProcessor(MethodDeclaration method, String className, String filepath) {
        this.method = method;
        this.className = className;
        this.filepath = filepath;
    }

    /**
     * Add a FeatureDetector to run on the method
     *
     * @param detector The FeatureDetector to run on the method
     */
    public void addFeatureDetector(FeatureDetector detector) {
        detectors.add(detector);
    }

    /**
     * Runs each FeatureDetector that has been added on the method
     *
     * @return Parsed method information
     */
    public MethodFeatures runDetectors() {
        MethodFeatures features = new MethodFeatures(method.getName().getIdentifier(), this.className, this.filepath);
        for (FeatureDetector detector : detectors) {
            detector.process(method, features);
        }

        return features;
    }

}
