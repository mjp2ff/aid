package edu.virginia.aid.detectors;

import edu.virginia.aid.data.MethodFeatures;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import weka.classifiers.Classifier;
import weka.core.Attribute;

/**
 * Detector that finds a single verb to describe the method
 */
public class PrimaryActionDetector implements FeatureDetector {

    private Classifier classifier;
    private Attribute classAttribute;

    /**
     * Creates a detector with the given classifier
     *
     * @param classifier The classifier for primary action verbs
     */
    public PrimaryActionDetector(Classifier classifier, Attribute classAttribute) {
        this.classifier = classifier;
        this.classAttribute = classAttribute;
    }

    /**
     * Examines identifier use and control flow in the method to determine the
     * primary action of the method, summed up in a single verb
     *
     * @param method The method to process
     * @param features The parsed features object to update when processing
     */
    @Override
    public void process(MethodDeclaration method, MethodFeatures features) {
        try {
            int labelIndex = (int) classifier.classifyInstance(features.buildWekaInstance(classAttribute));
            String label = classAttribute.value(labelIndex);
            features.setPrimaryAction(label.equals("methodName") ? features.getProcessedMethodName() : label);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // TODO: Define this based on classification
        features.setPrimaryObject("");
    }
}
