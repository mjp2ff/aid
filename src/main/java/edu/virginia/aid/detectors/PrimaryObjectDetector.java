package edu.virginia.aid.detectors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
    	// Creating a CFG for this method also populates it.
    	ControlFlowGraph cfg = new ControlFlowGraph(method);
    	VariableUsageVisitor visitor = new VariableUsageVisitor(features, false /* writing */);
    	Set<Statement> statementsSeen = new HashSet<>();
    	Queue<Statement> statementsToProcess = new LinkedList<>();
    	Map<IdentifierProperties, Integer> statementCounts = new HashMap<>();

    	Statement last = cfg.getEnd();
        if (last != null) {
            statementsToProcess.add(last);
        }
    	statementsSeen.add(last);
    	
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
    		if (currentPredecessors == null) continue;
    		for (Statement s : currentPredecessors) {
    			if (!statementsSeen.contains(s)) {
    				statementsToProcess.add(s);
    				statementsSeen.add(s);
    			}
    		}
    	}
    	
    	IdentifierProperties primaryObject = null;
    	int maxCountSoFar = -1;
    	for (IdentifierProperties iP : statementCounts.keySet()) {
    		if (statementCounts.get(iP) > maxCountSoFar) {
    			maxCountSoFar = statementCounts.get(iP);
    			primaryObject = iP;
    		}
    	}

        features.setPrimaryObject(primaryObject != null ? primaryObject.getName() : "");
    }
}
