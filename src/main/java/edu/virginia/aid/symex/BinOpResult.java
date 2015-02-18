package edu.virginia.aid.symex;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.InfixExpression;

public class BinOpResult implements IdentifierValue {

    private InfixExpression.Operator operator;
    private IdentifierValue operand1;
    private IdentifierValue operand2;

    private static final Map<InfixExpression.Operator, List<InfixExpression.Operator>> disjoint = new HashMap<>();
    static {
        disjoint.put(InfixExpression.Operator.GREATER, Arrays.asList(
                InfixExpression.Operator.EQUALS,
                InfixExpression.Operator.LESS,
                InfixExpression.Operator.LESS_EQUALS));
        disjoint.put(InfixExpression.Operator.GREATER_EQUALS, Arrays.asList(
                InfixExpression.Operator.LESS));
        disjoint.put(InfixExpression.Operator.EQUALS, Arrays.asList(
                InfixExpression.Operator.GREATER,
                InfixExpression.Operator.LESS,
                InfixExpression.Operator.NOT_EQUALS));
        disjoint.put(InfixExpression.Operator.NOT_EQUALS, Arrays.asList(
                InfixExpression.Operator.EQUALS));
        disjoint.put(InfixExpression.Operator.LESS_EQUALS, Arrays.asList(
                InfixExpression.Operator.GREATER));
        disjoint.put(InfixExpression.Operator.LESS, Arrays.asList(
                InfixExpression.Operator.EQUALS,
                InfixExpression.Operator.GREATER,
                InfixExpression.Operator.GREATER_EQUALS));
    }

    private static final Map<InfixExpression.Operator, InfixExpression.Operator> reverse = new HashMap<>();
    static {
        reverse.put(InfixExpression.Operator.GREATER, InfixExpression.Operator.LESS);
        reverse.put(InfixExpression.Operator.GREATER_EQUALS, InfixExpression.Operator.LESS_EQUALS);
        reverse.put(InfixExpression.Operator.EQUALS, InfixExpression.Operator.NOT_EQUALS);
        reverse.put(InfixExpression.Operator.NOT_EQUALS, InfixExpression.Operator.EQUALS);
        reverse.put(InfixExpression.Operator.LESS, InfixExpression.Operator.GREATER);
        reverse.put(InfixExpression.Operator.LESS_EQUALS, InfixExpression.Operator.GREATER_EQUALS);
    }

    private static final Map<AbstractMap.SimpleEntry<InfixExpression.Operator, InfixExpression.Operator>, InfixExpression.Operator> intersect = new HashMap();
    static {
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.GREATER, InfixExpression.Operator.GREATER_EQUALS), InfixExpression.Operator.GREATER);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.GREATER, InfixExpression.Operator.NOT_EQUALS), InfixExpression.Operator.GREATER);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.GREATER_EQUALS, InfixExpression.Operator.GREATER), InfixExpression.Operator.GREATER);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.GREATER_EQUALS, InfixExpression.Operator.EQUALS), InfixExpression.Operator.EQUALS);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.GREATER_EQUALS, InfixExpression.Operator.NOT_EQUALS), InfixExpression.Operator.GREATER);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.GREATER_EQUALS, InfixExpression.Operator.LESS_EQUALS), InfixExpression.Operator.EQUALS);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.EQUALS, InfixExpression.Operator.GREATER_EQUALS), InfixExpression.Operator.EQUALS);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.EQUALS, InfixExpression.Operator.LESS_EQUALS), InfixExpression.Operator.EQUALS);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.NOT_EQUALS, InfixExpression.Operator.GREATER), InfixExpression.Operator.GREATER);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.NOT_EQUALS, InfixExpression.Operator.GREATER_EQUALS), InfixExpression.Operator.GREATER);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.NOT_EQUALS, InfixExpression.Operator.LESS_EQUALS), InfixExpression.Operator.LESS);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.NOT_EQUALS, InfixExpression.Operator.LESS), InfixExpression.Operator.LESS);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.LESS_EQUALS, InfixExpression.Operator.GREATER_EQUALS), InfixExpression.Operator.EQUALS);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.LESS_EQUALS, InfixExpression.Operator.EQUALS), InfixExpression.Operator.EQUALS);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.LESS_EQUALS, InfixExpression.Operator.NOT_EQUALS), InfixExpression.Operator.LESS);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.LESS_EQUALS, InfixExpression.Operator.LESS), InfixExpression.Operator.LESS);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.LESS, InfixExpression.Operator.NOT_EQUALS), InfixExpression.Operator.LESS);
        intersect.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.LESS, InfixExpression.Operator.LESS_EQUALS), InfixExpression.Operator.LESS);
    }

    private static final Map<AbstractMap.SimpleEntry<InfixExpression.Operator, IdentifierValue>, String> commonExpressions = new HashMap();
    static {
        commonExpressions.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.GREATER_EQUALS, new Constant(0)), "is not negative");
        commonExpressions.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.GREATER, new Constant(0)), "is positive");
        commonExpressions.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.LESS, new Constant(0)), "is negative");
        commonExpressions.put(new AbstractMap.SimpleEntry<>(InfixExpression.Operator.LESS_EQUALS, new Constant(0)), "is not positive");
    }

    public BinOpResult(InfixExpression.Operator operator, IdentifierValue operand1, IdentifierValue operand2) {
        this.operator = operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public IdentifierValue getOperand1() {
        return operand1;
    }

    public IdentifierValue getOperand2() {
        return operand2;
    }

    @Override
    public IdentifierValue negate() {
        if (operator.equals(InfixExpression.Operator.CONDITIONAL_AND)) {
            return new BinOpResult(InfixExpression.Operator.CONDITIONAL_OR, (operand1 == null ? operand1 : operand1.negate()), (operand2 == null ? operand2 : operand2.negate()));
        } else if (operator.equals(InfixExpression.Operator.CONDITIONAL_OR)) {
            return new BinOpResult(InfixExpression.Operator.CONDITIONAL_AND, (operand1 == null ? operand1 : operand1.negate()), (operand2 == null ? operand2 : operand2.negate()));
        } else if (operator.equals(InfixExpression.Operator.EQUALS)) {
            return new BinOpResult(InfixExpression.Operator.NOT_EQUALS, operand1, operand2);
        } else if (operator.equals(InfixExpression.Operator.NOT_EQUALS)) {
            return new BinOpResult(InfixExpression.Operator.EQUALS, operand1, operand2);
        } else if (operator.equals(InfixExpression.Operator.GREATER)) {
            return new BinOpResult(InfixExpression.Operator.LESS_EQUALS, operand1, operand2);
        } else if (operator.equals(InfixExpression.Operator.GREATER_EQUALS)) {
            return new BinOpResult(InfixExpression.Operator.LESS, operand1, operand2);
        } else if (operator.equals(InfixExpression.Operator.LESS_EQUALS)) {
            return new BinOpResult(InfixExpression.Operator.GREATER, operand1, operand2);
        } else if (operator.equals(InfixExpression.Operator.LESS)) {
            return new BinOpResult(InfixExpression.Operator.GREATER_EQUALS, operand1, operand2);
        } else {
            return this;
        }
    }

    @Override
    public IdentifierValue simplify() {
        // Attempt to evaluate expression with literals
        IdentifierValue operand1 = this.operand1.simplify();
        IdentifierValue operand2 = this.operand2.simplify();
        if (operand1 instanceof Constant && operand2 instanceof Constant) {
            double value1 = ((Constant) operand1).getValue();
            double value2 = ((Constant) operand2).getValue();

            if (operator.equals(InfixExpression.Operator.PLUS)) {
                return new Constant(value1 + value2);
            } else if (operator.equals(InfixExpression.Operator.MINUS)) {
                return new Constant(value1 - value2);
            } else if (operator.equals(InfixExpression.Operator.TIMES)) {
                return new Constant(value1 * value2);
            } else if (operator.equals(InfixExpression.Operator.DIVIDE)) {
                return new Constant(value1 / value2);
            } else if (operator.equals(InfixExpression.Operator.REMAINDER)) {
                return new Constant(value1 % value2);
            } else if (operator.equals(InfixExpression.Operator.EQUALS)) {
                return new BooleanValue(value1 == value2);
            } else if (operator.equals(InfixExpression.Operator.NOT_EQUALS)) {
                return new BooleanValue(value1 != value2);
            } else if (operator.equals(InfixExpression.Operator.GREATER)) {
                return new BooleanValue(value1 > value2);
            } else if (operator.equals(InfixExpression.Operator.GREATER_EQUALS)) {
                return new BooleanValue(value1 >= value2);
            } else if (operator.equals(InfixExpression.Operator.LESS)) {
                return new BooleanValue(value1 < value2);
            } else if (operator.equals(InfixExpression.Operator.LESS_EQUALS)) {
                return new BooleanValue(value1 <= value2);
            }
        } else if (operand1 instanceof BooleanValue && operand2 instanceof BooleanValue) {
            boolean value1 = ((BooleanValue) operand1).getValue();
            boolean value2 = ((BooleanValue) operand2).getValue();

            if (operator.equals(InfixExpression.Operator.CONDITIONAL_AND)) {
                return new BooleanValue(value1 && value2);
            } else if (operator.equals(InfixExpression.Operator.CONDITIONAL_OR)) {
                return new BooleanValue(value1 || value2);
            } else if (operator.equals(InfixExpression.Operator.EQUALS)) {
                return new BooleanValue(value1 == value2);
            } else if (operator.equals(InfixExpression.Operator.NOT_EQUALS)) {
                return new BooleanValue(value1 != value2);
            }
        }

        return new BinOpResult(operator, operand2, operand1);
    }

    @Override
    public boolean isDisjointWith(IdentifierValue iv) {
        if (iv instanceof BinOpResult && disjoint.containsKey(operator)) {
            if (disjoint.containsKey(operator) && disjoint.get(operator).contains(((BinOpResult) iv).operator)) {
                if (((BinOpResult) iv).operand1.equals(operand1) && ((BinOpResult) iv).operand2.equals(operand2)) {
                    return true;
                }
            } else if (reverse.containsKey(operator) && disjoint.get(reverse.get(operator)).contains(((BinOpResult) iv).operator)) {
                return ((BinOpResult) iv).operand2.equals(operand1) && ((BinOpResult) iv).operand1.equals(operand2);
            }
        }

        return false;
    }

    @Override
    public IdentifierValue getIntersection(IdentifierValue iv) {
        if (iv instanceof BinOpResult) {
            if (operand1.equals(((BinOpResult) iv).operand1) && operand2.equals(((BinOpResult) iv).operand2)) {
                AbstractMap.SimpleEntry<InfixExpression.Operator, InfixExpression.Operator> key = new AbstractMap.SimpleEntry<>(operator, ((BinOpResult) iv).operator);
                if (intersect.containsKey(key)) {
                    return new BinOpResult(intersect.get(key), operand1, operand2);
                }
            } else if (operand2.equals(((BinOpResult) iv).operand1) && operand1.equals(((BinOpResult) iv).operand2) && reverse.containsKey(((BinOpResult) iv).operator)) {
                AbstractMap.SimpleEntry<InfixExpression.Operator, InfixExpression.Operator> key = new AbstractMap.SimpleEntry<>(operator, reverse.get(((BinOpResult) iv).operator));
                if (intersect.containsKey(key)) {
                    return new BinOpResult(intersect.get(key), operand1, operand2);
                }
            }
        }

        return null;
    }

    @Override
    public boolean isConstantType() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BinOpResult) {
            return ((BinOpResult) o).operator.equals(this.operator) &&
                    ((BinOpResult) o).operand1.equals(this.operand1) &&
                    ((BinOpResult) o).operand2.equals(this.operand2);
        }

        return false;
    }

    public String toString() {
        // Process special cases
        if (commonExpressions.containsKey(new AbstractMap.SimpleEntry<>(operator, operand2))) {
            return operand1 + " " + commonExpressions.get(new AbstractMap.SimpleEntry<>(operator, operand2));
        }

        String operatorString = "uninitialized";
        if (operator.equals(InfixExpression.Operator.AND)) {
            operatorString = "logical and";
        } else if (operator.equals(InfixExpression.Operator.CONDITIONAL_AND)) {
            operatorString = "and";
        } else if (operator.equals(InfixExpression.Operator.CONDITIONAL_OR)) {
            operatorString = "or";
        } else if (operator.equals(InfixExpression.Operator.DIVIDE)) {
            operatorString = "/";
        } else if (operator.equals(InfixExpression.Operator.EQUALS)) {
            operatorString = "is";
        } else if (operator.equals(InfixExpression.Operator.LESS)) {
            operatorString = "less than";
        } else if (operator.equals(InfixExpression.Operator.LESS_EQUALS)) {
            operatorString = "less than or equal to";
        } else if (operator.equals(InfixExpression.Operator.LEFT_SHIFT)) {
            operatorString = "left shift";
        } else if (operator.equals(InfixExpression.Operator.GREATER)) {
            operatorString = "greater than";
        } else if (operator.equals(InfixExpression.Operator.GREATER_EQUALS)) {
            operatorString = "greater than or equal to";
        } else if (operator.equals(InfixExpression.Operator.MINUS)) {
            operatorString = "-";
        } else if (operator.equals(InfixExpression.Operator.NOT_EQUALS)) {
            operatorString = "is not";
        } else if (operator.equals(InfixExpression.Operator.OR)) {
            operatorString = "or";
        } else if (operator.equals(InfixExpression.Operator.PLUS)) {
            operatorString = "+";
        } else if (operator.equals(InfixExpression.Operator.REMAINDER)) {
            operatorString = "%";
        } else if (operator.equals(InfixExpression.Operator.RIGHT_SHIFT_SIGNED)) {
            operatorString = "right shift";
        } else if (operator.equals(InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED)) {
            operatorString = "right shift";
        } else if (operator.equals(InfixExpression.Operator.TIMES)) {
            operatorString = "*";
        }

        return operand1 + " " + operatorString + " " + operand2;
    }
}
