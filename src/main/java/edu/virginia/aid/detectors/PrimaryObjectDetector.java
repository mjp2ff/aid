package edu.virginia.aid.detectors;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.util.ControlFlowGraph;

/**
 * Detector that finds a single noun that is the most important within the method
 */
public class PrimaryObjectDetector implements FeatureDetector {

    /**
     * Creates a detector
     */
    public PrimaryObjectDetector() {}

    /**
     * Examines control flow graph of the method to determine the
     * primary object of the method
     *
     * @param method The method to process
     * @param features The parsed features object to update when processing
     */
    @Override
    public void process(MethodDeclaration method, MethodFeatures features) {
    	// Creating a CFG for this method also populates it.
    	ControlFlowGraph cfg = new ControlFlowGraph(method);
    	
    	System.out.println(cfg.toString());

        // TODO: Define this based on classification
        features.setPrimaryObject("");
    }
}
