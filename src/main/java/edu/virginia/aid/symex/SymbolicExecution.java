package edu.virginia.aid.symex;

import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.visitors.AssignmentVisitor;
import edu.virginia.aid.visitors.EvaluationVisitor;
import org.eclipse.jdt.core.dom.InfixExpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static class for performing symbolic execution on one or more paths
 */
public class SymbolicExecution {

    public static IdentifierValue inverseSymEx(MethodFeatures method, List<Path> paths) {
        List<IdentifierValue> conditions = new ArrayList<>();
        for (Path path : paths) {
            conditions.add(execute(method, path));
        }

        IdentifierValue allConditions = orExpressions(conditions);
        if (allConditions != null) {
            return allConditions.negate();
        } else  {
            return null;
        }
    }

    /**
     * Perform symbolic execution on a path with a given scope of variables
     *
     * @param method The method containing the Path to symbolically execute
     * @param path The Path to symbolically execute
     * @return Map of identifiers to the expressions that must be true about them in this path
     */
    public static IdentifierValue execute(MethodFeatures method, Path path) {
        Map<IdentifierProperties, IdentifierValue> memory = new HashMap<>();
        for (IdentifierProperties variable : method.getScope().getIdentifiers()) {
            memory.put(variable, new InitialValue(variable));
        }

        List<IdentifierValue> conditions = new ArrayList<>();

        for (PathElement element : path.getPathElements()) {
            if (element.isStatement()) {
                AssignmentVisitor visitor = new AssignmentVisitor(method);
                element.getStatement().accept(visitor);

                if (visitor.getValue() != null) {
                    EvaluationVisitor evaluationVisitor = new EvaluationVisitor(memory, method);
                    visitor.getValue().accept(evaluationVisitor);
                    memory.put(visitor.getVariable().getResolvedIdentifier(method),evaluationVisitor.getResult());
                }
            } else {
                EvaluationVisitor evaluationVisitor = new EvaluationVisitor(memory, method);
                element.getExpression().accept(evaluationVisitor);
                if (evaluationVisitor.getResult() != null) {
                    if (element.isNegated()) {
                        conditions.add(evaluationVisitor.getResult().negate());
                    } else {
                        conditions.add(evaluationVisitor.getResult());
                    }
                }
            }
        }

        return andExpressions(conditions);
    }

    public static IdentifierValue andExpressions(List<IdentifierValue> expressions) {
        if (expressions.size() == 0) {
            return null;
        } else if (expressions.size() == 1) {
            return expressions.get(0);
        }

        IdentifierValue value = expressions.get(0);
        for (IdentifierValue expression : expressions.subList(1, expressions.size())) {
            value = new BinOpResult(InfixExpression.Operator.CONDITIONAL_AND, value, expression);
        }

        return value;
    }

    public static IdentifierValue orExpressions(List<IdentifierValue> expressions) {
        if (expressions.size() == 0) {
            return null;
        } else if (expressions.size() == 1) {
            return expressions.get(0);
        }

        IdentifierValue value = expressions.get(0);
        for (IdentifierValue expression : expressions.subList(1, expressions.size())) {
            value = new BinOpResult(InfixExpression.Operator.CONDITIONAL_OR, value, expression);
        }

        return value;
    }
}
