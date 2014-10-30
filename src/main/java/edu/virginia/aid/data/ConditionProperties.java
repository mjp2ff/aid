package edu.virginia.aid.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Expression;

public class ConditionProperties {

    private List<Expression> standardConditions;
    private List<Expression> negatedConditions;

    /**
     * Creates an empty condition element
     */
    public ConditionProperties() {
        standardConditions = new ArrayList<>();
        negatedConditions = new ArrayList<>();
    }

    public ConditionProperties(ConditionProperties conditionProperties) {
        standardConditions = new ArrayList<>();
        negatedConditions = new ArrayList<>();

        conditionProperties.getStandardConditions().stream()
                .forEach(condition -> addCondition(condition, false));

        conditionProperties.getNegatedConditions().stream()
                .forEach(condition -> addCondition(condition, true));
    }

    /**
     * Adds the given condition along with whether or not it is negated relative to the expression passed in
     *
     * @param condition The expression to be added
     * @param isNegated Whether or not the expression should be negated
     */
    public void addCondition(Expression condition, boolean isNegated) {
        if (!isNegated) {
            standardConditions.add(condition);
        } else {
            negatedConditions.add(condition);
        }
    }

    public List<Expression> getStandardConditions() {
        return standardConditions;
    }

    public List<Expression> getNegatedConditions() {
        return negatedConditions;
    }
}
