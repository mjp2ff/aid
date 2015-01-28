package edu.virginia.aid.detectors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import edu.virginia.aid.data.IdentifierName;
import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.util.ControlFlowGraph;
import edu.virginia.aid.visitors.VariableUsageVisitor;

/**
 * Detector that finds a single noun that is the most important within the method
 */
public class PrimaryObjectDetector implements FeatureDetector {

    /**
     * Creates a detector
     */
    public PrimaryObjectDetector() {}

    /**
     * Examines control flow graph of the method to determine the primary object of the method.
     * Go through each statement in the method, and set the primary object to the variable
     * identifier that appears in the most statements that are part of some path to a return
     * statement. 
     *
     * @param method The method to process
     * @param features The parsed features object to update and refer to when processing
     */
    @Override
    public void process(MethodDeclaration method, MethodFeatures features) {
//    	processMethodOne(method, features);
    	processMethodTwo(method, features);
    }
    
	/**
	 * Method 1: Find the identifier that appears in the most statements on any path
	 * leading to a successful exit. Each statement is considered zero or one times.
	 */
    public void processMethodOne(MethodDeclaration method, MethodFeatures features) {
    	// Creating a CFG for this method also populates it.
    	ControlFlowGraph cfg = new ControlFlowGraph(method);
    	VariableUsageVisitor visitor = new VariableUsageVisitor(features, false /* writing */);
    	Set<Statement> statementsSeen = new HashSet<>();
    	Queue<Statement> statementsToProcess = new LinkedList<>();
    	Map<IdentifierProperties, Integer> statementCounts = new HashMap<>();

    	Statement last = cfg.getEnd();
        if (last != null) {
            statementsToProcess.add(last);
        	statementsSeen.add(last);
        }

    	while (!statementsToProcess.isEmpty()) {
    		Statement current = statementsToProcess.poll();
    		visitor.clearFields();
    		current.accept(visitor);

    		// Change to identifierProperties and put into a set, to ensure we only count
    		// each use once even if it's used multiple times in a statement.
    		Set<IdentifierProperties> identifierProperties = new HashSet<>();
    		for (IdentifierName iN : visitor.getIdentifierUses()) {
    			identifierProperties.add(iN.getResolvedIdentifier(features));
    		}

    		// Update uses of this statement in the statementCounts map.
    		for (IdentifierProperties iP : identifierProperties) {
    			// Ignore methods and other non-variable crap.
    			if (iP != null) {
    				// If not found before, put in 0 instead of 1. Every identifier is counted an extra time,
    				// because the entire method is contained in one overarching statement representing the
    				// method body, in addition to the specific statement containing the identifier.
        			statementCounts.put(iP, statementCounts.get(iP) != null ? statementCounts.get(iP) + 1 : 0);	
    			}
    		}
    		
    		// Add all predecessors to queue for processing.
    		Set<Statement> currentPredecessors = cfg.getPredecessors().get(current);
    		if (currentPredecessors == null || currentPredecessors.isEmpty()) continue;
    		for (Statement s : currentPredecessors) {
    			if (!statementsSeen.contains(s)) {
    				statementsToProcess.add(s);
    				statementsSeen.add(s);
    			}
    		}
    	}

    	IdentifierProperties primaryObject = getPrimaryObjectFromMap(statementCounts);
        features.setPrimaryObject(primaryObject != null ? primaryObject.getName() : "");    	
    }

    /**
     * Method 2: Find the identifier that appears on the most number of paths leading to
     * any successful function exit. Duplicate paths are not considered, but statements
     * can be considered on multiple paths.
     */
    public void processMethodTwo(MethodDeclaration method, MethodFeatures features) {
    	// Creating a CFG for this method also populates it.
    	ControlFlowGraph cfg = new ControlFlowGraph(method);

    	// All the paths in this method, sorted from last statements (exits) to beginning.
    	Map<IdentifierProperties, Integer> statementCounts = new HashMap<>();

    	Statement last = cfg.getEnd();
    	// If we have no "last" then we can't perform primary object analysis in this way.
        if (last != null) {
            // Traverse up paths, counting identifier uses as you go. Keep track of which ones
            // have already been counted on this path, to avoid double counting. Kick off
            // a recursive method call in order to accomplish this.
    		updateIdentifiersOnAllPredecessorPaths(cfg, statementCounts, features,
    				new HashSet<Statement>() /* statementsOnPath */, last);
        }

    	IdentifierProperties primaryObject = getPrimaryObjectFromMap(statementCounts);
        features.setPrimaryObject(primaryObject != null ? primaryObject.getName() : "");
    }

    /**
     * Recursive method to update the identifier counts along all paths going backwards
     * from some statement.
     */
    public void updateIdentifiersOnAllPredecessorPaths(ControlFlowGraph cfg,
    		Map<IdentifierProperties, Integer> statementCounts, MethodFeatures features,
    		Set<Statement> statementsOnPath, Statement curStatement) {
		if (curStatement == null) return;

		statementsOnPath.add(curStatement);
		Set<Statement> currentPredecessors = cfg.getPredecessors().get(curStatement);

		// If it's not null/empty, then make sure we haven't seen the predecessor on this path
		// then recursively call this method on the predecessor statement.
		if (currentPredecessors != null && !currentPredecessors.isEmpty()) {
			for (Statement curPredecessor : currentPredecessors) {
				if (!statementsOnPath.contains(curStatement)) {
					updateIdentifiersOnAllPredecessorPaths(cfg, statementCounts, features,
							statementsOnPath, curPredecessor);	
				}
			}

		// If it's null/empty, then we're at the beginning of a path, so handle the identifiers .
		} else {
			// No more predecessors, count the statements on this path and update statementCounts.
			// Update uses of this statement in the statementCounts map.
			VariableUsageVisitor visitor = new VariableUsageVisitor(features, false /* writing */);
			for (Statement statementOnPath : statementsOnPath) {
				visitor.clearFields();
				statementOnPath.accept(visitor);
				for (IdentifierName iN : visitor.getIdentifierUses()) {
					IdentifierProperties iP = iN.getResolvedIdentifier(features);
					// Ignore methods and other non-variable crap.
					if (iP != null) {
						// If not found before, put in 0 instead of 1. Every identifier is counted
						// an extra time, because the entire method is contained in one overarching
						// statement representing the method body, in addition to the specific
						// statement containing the identifier.
		    			statementCounts.put(iP,
		    					statementCounts.get(iP) != null ? statementCounts.get(iP) + 1 : 0);
					}
				}
			}
		}
    }

    /**
     * Method 3: Incorporate TFIDF in detection of primaryObject somehow.
     */
    public void processMethodThree(MethodDeclaration method, MethodFeatures features) {
    	
    }

    /**
     * Helper method to find the primary object from a map of the usages of each statement.
     */
    public IdentifierProperties getPrimaryObjectFromMap(Map<IdentifierProperties, Integer> statementCounts) {
    	IdentifierProperties primaryObject = null;
    	int maxCountSoFar = -1;
    	for (IdentifierProperties iP : statementCounts.keySet()) {
    		if (statementCounts.get(iP) > maxCountSoFar) {
    			maxCountSoFar = statementCounts.get(iP);
    			primaryObject = iP;
    		}
    	}
    	return primaryObject;
    }
}
