package edu.virginia.aid.detectors;

import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.symex.Path;
import edu.virginia.aid.symex.SumOfProducts;
import edu.virginia.aid.symex.SymbolicExecution;
import edu.virginia.aid.util.ControlFlowGraph;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Feature detector for finding the predicate that must be satisfied for the method to execute without
 * throwing an exception.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class SuccessConditionDetector implements FeatureDetector {

    /**
     * Generates all exceptional paths in the method and determines the predicate that must be true to
     * avoid all of these paths during execution.
     *
     * @param method The method to process
     * @param features The parsed features object to update when processing
     */
    @Override
    public void process(MethodDeclaration method, MethodFeatures features) {
        ControlFlowGraph cfg = new ControlFlowGraph(method);
        Set<Statement> statements = cfg.getSuccessors().keySet();

        // Get throws statements
        Set<ThrowStatement> throwStatements = new HashSet<>();
        for (Statement statement : statements) {
            if (statement instanceof ThrowStatement) {
                throwStatements.add((ThrowStatement) statement);
            }
        }

        List<Path> exceptionalPaths = new ArrayList<>();
        for (ThrowStatement throwStatement : throwStatements) {
            exceptionalPaths.addAll(Path.getPathsToStatement(cfg, throwStatement));
        }

        SumOfProducts successConditions = SymbolicExecution.inverseSymEx(features, exceptionalPaths);

        // Set conditions for success in the method
        features.setConditionsForSuccess(successConditions);
    }
}
