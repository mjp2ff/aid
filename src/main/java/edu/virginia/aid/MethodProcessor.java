package edu.virginia.aid;

import java.util.ArrayList;
import java.util.List;

import edu.virginia.aid.data.MethodSignature;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.ClassInformation;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.detectors.FeatureDetector;

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

    private ClassInformation parentClass;
    private String filepath;

    /**
     * Creates a new MethodProcessor with the method to process
     *
     * @param method The method to process
     */
    public MethodProcessor(MethodDeclaration method, ClassInformation parentClass, String filepath) {
        this.method = method;
        this.parentClass = parentClass;
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
        MethodFeatures features = new MethodFeatures(method.getName().getIdentifier(),
                                                    this.parentClass,
                                                    this.filepath,
                                                    method.getReturnType2(),
                                                    method.getStartPosition(),
                                                    method.getStartPosition() + method.getLength(),
                                                    parentClass.getSourceContext());
        for (FeatureDetector detector : detectors) {
            detector.process(method, features);
        }

        return features;
    }

    /**
     * Gets the primary method called by this method, if any exists
     *
     * @return MethodSignature for the method called
     */
    public MethodSignature getPrimaryCalledMethod() {
        PrimaryMethodInvocationVisitor visitor = new PrimaryMethodInvocationVisitor();
        method.accept(visitor);
        if (visitor.getStatementCount() == 1 && visitor.getMethodInvocations().size() > 0) {
            return visitor.getMethodInvocations().get(0);
        }

        return null;
    }

}
