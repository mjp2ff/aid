package edu.virginia.aid.detectors;

import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.util.IdentifierTreeNode;

/**
 * Detector that finds a single verb to describe the method
 */
public class DataflowDetector implements FeatureDetector {

    /**
     * Examines dataflow starting from return statements, creating a dependency
     * tree of all the things that the return value is based on.
     *
     * @param method The method to process
     * @param features The parsed features object to update when processing
     */
    @Override
    public void process(MethodDeclaration method, MethodFeatures features) {
        Set<IdentifierTreeNode> returnValueDataflows = features.getReturnValueDataflows();

        // Fill in complete reference tree for each of the root nodes with dataflow analysis.
        for (IdentifierTreeNode returnValue : returnValueDataflows) {
        	fillReferences(method, features, returnValue);
        }
    }
    
    /**
     * Finds all references inside the specified method that directly affect the desired root node,
     * and adds them as references to the root node. Calls itself recursively on each of the
     * discovered references until a full path has been reached.
     * 
     * @param method The method to explore
     * @param features The parsed feature objects, for reference (not modified here)
     * @param root The current node to find references to
     */
    public void fillReferences(MethodDeclaration method, MethodFeatures features, IdentifierTreeNode root) {
    	
    }
}
