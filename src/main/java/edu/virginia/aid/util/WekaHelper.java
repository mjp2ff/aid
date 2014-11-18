package edu.virginia.aid.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import edu.virginia.aid.data.MethodFeatures;


/**
 * Performs machine learning analysis through Weka.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class WekaHelper {

	/**
	 * Builds the training data file from the given methods.
	 * 
	 * @param labeledMethods The methods to use in building the file, already labeled.
	 * @param property The property that the current file is based around.
	 * @param filepath Full path to the appropriate location for the file.
	 */
    public static void buildTrainingDataFile(Map<String, List<MethodFeatures>> labeledMethods, String property, String filepath) {
        Set<String> values = labeledMethods.keySet();

        List<String> booleanFeatureProperties = new ArrayList<>();
        List<String> numericFeatureProperties = new ArrayList<>();

        if (values.size() > 0) {
            String value = values.iterator().next();
            if (labeledMethods.get(value).size() > 0) {
                MethodFeatures method = labeledMethods.get(value).get(0);
                booleanFeatureProperties.addAll(method.getBooleanFeatures().keySet());
                numericFeatureProperties.addAll(method.getNumericFeatures().keySet());
            }
        }

        FastVector attributes = new FastVector(booleanFeatureProperties.size() + numericFeatureProperties.size() + 1);

        for (String label : booleanFeatureProperties) {
            FastVector attributeValues = new FastVector(2);
            attributeValues.addElement("true");
            attributeValues.addElement("false");
            attributes.addElement(new Attribute(label, attributeValues));
        }

        for (String label : numericFeatureProperties) {
            attributes.addElement(new Attribute(label));
        }

        FastVector classValues = new FastVector(values.size());
        for (String value : values) {
            classValues.addElement(value);
        }
        attributes.addElement(new Attribute(property, classValues));

        Instances trainingData = new Instances(property, attributes, 100);
        trainingData.setClassIndex(trainingData.numAttributes() - 1);

        for (String value : values) {
            for (MethodFeatures method : labeledMethods.get(value)) {
                Instance dataPoint = new Instance(booleanFeatureProperties.size() + numericFeatureProperties.size() + 1);
                int i = 0;

                for (String booleanProperty : booleanFeatureProperties) {
                    dataPoint.setValue((Attribute) attributes.elementAt(i++), method.getBooleanFeature(booleanProperty) ? "true" : "false");
                }
                for (String numericProperty : numericFeatureProperties) {
                    dataPoint.setValue((Attribute) attributes.elementAt(i++), method.getNumericFeature(numericProperty));
                }
                dataPoint.setValue((Attribute) attributes.elementAt(i), value);
                trainingData.add(dataPoint);
            }
        }

        crossValidate(trainingData, 10);

        String arffData = trainingData.toString();

        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), "utf-8"));
            writer.write(arffData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                // Let original exception through (it's more important)
            }
        }
    }

    /**
     * Performs Weka cross-validation on the training set.
     * 
     * @param trainingSet The set on which to perform cross-validation.
     * @param folds The number of folds to use for cross validation.
     */
    public static void crossValidate(Instances trainingSet, int folds) {
        try {
            Evaluation crossValidationEvaluation = new Evaluation(trainingSet);
            crossValidationEvaluation.crossValidateModel(new NaiveBayes(), trainingSet, folds, new Random());
            System.out.println("\nCross-Validation Results");
            System.out.println("======================");
            System.out.println(crossValidationEvaluation.toSummaryString());
            System.out.println(crossValidationEvaluation.toClassDetailsString());

            Evaluation evaluation = new Evaluation(trainingSet);
            Classifier classifier = new NaiveBayes();
            classifier.buildClassifier(trainingSet);
            evaluation.evaluateModel(classifier, trainingSet);
            System.out.println("Training Set Results");
            System.out.println("======================");
            System.out.println(evaluation.toSummaryString());
            System.out.println(evaluation.toClassDetailsString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
