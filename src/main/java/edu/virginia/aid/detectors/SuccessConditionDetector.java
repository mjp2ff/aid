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

public class SuccessConditionDetector implements FeatureDetector {

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
