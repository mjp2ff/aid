package edu.virginia.aid.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.InfixExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extracts information about arithmetic expressions from a method
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ExpressionVisitor extends ASTVisitor {

    public int numComparisons = 0;

    /**
     * Count infix expressions
     *
     * @param node
     * @return
     */
    @Override
    public boolean visit (InfixExpression node) {
        List<InfixExpression.Operator> arithmeticOperators = new ArrayList<>(Arrays.asList(
                InfixExpression.Operator.TIMES,
                InfixExpression.Operator.DIVIDE,
                InfixExpression.Operator.REMAINDER,
                InfixExpression.Operator.PLUS,
                InfixExpression.Operator.MINUS,
                InfixExpression.Operator.LEFT_SHIFT,
                InfixExpression.Operator.RIGHT_SHIFT_SIGNED,
                InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED,
                InfixExpression.Operator.XOR,
                InfixExpression.Operator.AND,
                InfixExpression.Operator.OR
        ));

        List<InfixExpression.Operator> comparisonOperators = new ArrayList<>(Arrays.asList(
                InfixExpression.Operator.EQUALS,
                InfixExpression.Operator.LESS,
                InfixExpression.Operator.GREATER,
                InfixExpression.Operator.LESS_EQUALS,
                InfixExpression.Operator.GREATER_EQUALS
        ));

        if (comparisonOperators.contains(node.getOperator())) {
            numComparisons++;
        }

        return true;
    }

    public void clearNumComparisons() {
        numComparisons = 0;
    }

    public int getNumComparisons() {
        return numComparisons;
    }
}
