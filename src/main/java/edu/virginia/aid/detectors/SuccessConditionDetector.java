package edu.virginia.aid.detectors;

import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;
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

        List<List<Expression>> conditions = new ArrayList<>();
        for (ThrowStatement throwStatement : throwStatements) {
            List<Expression> statementConditions = new ArrayList<>();
            Queue<Statement> statementsToProcess = new LinkedList<>();
            statementsToProcess.add(throwStatement);
            Set<Statement> visitedStatements = new HashSet<>(statementsToProcess);
            while (!statementsToProcess.isEmpty()) {
                Statement statement = statementsToProcess.remove();
                if (statement instanceof IfStatement
                        && statement.getStartPosition() <= throwStatement.getStartPosition()
                        && statement.getStartPosition() + statement.getLength() >= throwStatement.getStartPosition() + throwStatement.getLength()) {
                    statementConditions.add(((IfStatement) statement).getExpression());
                }

                conditions.add(statementConditions);

                Set<Statement> predecessors = cfg.getPredecessors().getOrDefault(statement, new HashSet<>());
                statementsToProcess.addAll(predecessors
                        .stream()
                        .filter(p -> !visitedStatements.contains(p))
                        .collect(Collectors.toSet()));
                visitedStatements.addAll(predecessors);
            }
        }

        // Process expressions for names
        // TODO: capture the content of these more precisely
        Set<IdentifierProperties> conditionsForSuccess = new HashSet<>();
        for (List<Expression> expressionList : conditions) {
            for (Expression expression : expressionList) {
                VariableUsageVisitor visitor = new VariableUsageVisitor(features, false);
                expression.accept(visitor);
                conditionsForSuccess.addAll(visitor.getResolvedIdentifiers());
            }
        }

        // Set conditions for success in the method
        features.setConditionsForSuccess(conditionsForSuccess);
    }
}
