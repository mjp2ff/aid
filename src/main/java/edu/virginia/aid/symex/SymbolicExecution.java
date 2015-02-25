package edu.virginia.aid.symex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.Statement;

import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.visitors.AssignmentVisitor;
import edu.virginia.aid.visitors.EvaluationVisitor;

/**
 * Static class for performing symbolic execution on one or more paths
 */
public class SymbolicExecution {

    public static SumOfProducts inverseSymEx(MethodFeatures method, List<Path> paths) {
        SumOfProducts allConditions = new SumOfProducts();
        for (Path path : paths) {
            allConditions.addProduct(execute(method, path));
        }

        SumOfProducts allConditionsSimplified = allConditions.simplifyKeepType();

        ProductOfSums negatedConditions = new ProductOfSums();
        if (allConditionsSimplified.getProducts().size() > 0) {
            for (BooleanAndList condition : allConditionsSimplified.getProducts()) {
                BooleanOrList negatedCondition = new BooleanOrList();
                for (IdentifierValue conditionTerm : condition.getTerms()) {
                    negatedCondition.addTerm(conditionTerm.negate());
                }
                negatedConditions.addSum(negatedCondition);
            }

            long numProducts = negatedConditions.getSums().stream()
                    .mapToLong(s -> s.getTerms().size())
                    .reduce(1l, (len, acc) -> acc * len);
            if (numProducts > 1000) {
                return null;
            } else {
                return negatedConditions.convertToSumOfProducts().simplifyKeepType();
            }
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
    public static BooleanAndList execute(MethodFeatures method, Path path) {
        Map<IdentifierProperties, IdentifierValue> memory = new HashMap<>();
        for (IdentifierProperties variable : method.getScope().getIdentifiers()) {
            memory.put(variable, new InitialValue(variable));
        }

        BooleanAndList conditions = new BooleanAndList();
        boolean containsLastStatement = false;
        boolean hasAssignment = false;
        Statement lastStatement = path.getPathElements().get(path.getPathElements().size() - 1).getStatement();

        if (lastStatement != null) {
            for (PathElement element : path.getPathElements()) {
                if (element.isStatement()) {
                    AssignmentVisitor visitor = new AssignmentVisitor(method);
                    element.getStatement().accept(visitor);
                    hasAssignment = visitor.isAssignment();

                    if (visitor.getValue() != null) {
                        EvaluationVisitor evaluationVisitor = new EvaluationVisitor(memory, method);
                        visitor.getValue().accept(evaluationVisitor);
                        memory.put(visitor.getVariable().getResolvedIdentifier(method),evaluationVisitor.getResult());
                    }

                    // Check if this statement contains the last statement (such as if/for/while)
                    if (element.getStatement().getStartPosition() <= lastStatement.getStartPosition() &&
                        element.getStatement().getStartPosition() + element.getStatement().getLength() >=
                                lastStatement.getStartPosition() + lastStatement.getLength()) {
                        containsLastStatement = true;
                    } else {
                        containsLastStatement = false;
                    }
                } else { // Only add condition if it is part of a containing statement or has an assignment
                    if (containsLastStatement || hasAssignment) {
                        EvaluationVisitor evaluationVisitor = new EvaluationVisitor(memory, method);
                        element.getExpression().accept(evaluationVisitor);
                        if (evaluationVisitor.getResult() != null) {
                            if (element.isNegated()) {
                                conditions.addTerm(evaluationVisitor.getResult().negate());
                            } else {
                                conditions.addTerm(evaluationVisitor.getResult());
                            }
                        }
                    }
                }
            }
        }

        return conditions;
    }
}
