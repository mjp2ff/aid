package edu.virginia.aid.symex;

import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.visitors.AssignmentVisitor;
import edu.virginia.aid.visitors.EvaluationVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static class for performing symbolic execution on one or more paths
 */
public class SymbolicExecution {

    public static IdentifierValue inverseSymEx(MethodFeatures method, List<Path> paths) {
        List<BooleanAndList> allConditions = new ArrayList<>();
        for (Path path : paths) {
            System.out.println(path);
            allConditions.add(execute(method, path));
        }

        ProductOfSums negatedConditions = new ProductOfSums();
        if (allConditions.size() > 0) {
            for (BooleanAndList condition : allConditions) {
                BooleanOrList negatedCondition = new BooleanOrList();
                for (IdentifierValue conditionTerm : condition.getTerms()) {
                    negatedCondition.addTerm(conditionTerm.negate());
                }
                negatedConditions.addSum(negatedCondition);
            }

            return negatedConditions.convertToSumOfProducts();
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
                if (element.isNegated()) {
                    conditions.addTerm(evaluationVisitor.getResult().negate());
                } else {
                    conditions.addTerm(evaluationVisitor.getResult());
                }
            }
        }

        return conditions;
    }
}
