package edu.virginia.aid.detectors;

import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.symex.IdentifierValue;
import edu.virginia.aid.symex.Path;
import edu.virginia.aid.symex.SymbolicExecution;
import edu.virginia.aid.util.ControlFlowGraph;
import edu.virginia.aid.visitors.VariableUsageVisitor;
import org.eclipse.jdt.core.dom.*;

import java.util.*;
import java.util.stream.Collectors;

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

        IdentifierValue successConditions = SymbolicExecution.inverseSymEx(features, exceptionalPaths);

        // Set conditions for success in the method
        features.setConditionsForSuccess(successConditions);
    }
}
