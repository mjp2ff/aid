package edu.virginia.aid.visitors;

import edu.virginia.aid.data.*;
import edu.virginia.aid.symex.*;
import org.eclipse.jdt.core.dom.*;

import java.util.Map;

/**
 * ASTVisitor for building up an IdentifierValue from an Expression
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class EvaluationVisitor extends ASTVisitor {

    private Map<IdentifierProperties, IdentifierValue> memory;
    private MethodFeatures method;
    private IdentifierValue result;

    public EvaluationVisitor(Map<IdentifierProperties, IdentifierValue> memory, MethodFeatures method) {
        this.memory = memory;
        this.method = method;
        this.result = null;
    }

    public boolean visit(SimpleName node) {
        IdentifierName name = new IdentifierName(node.getIdentifier(),
                IdentifierType.VARIABLE,
                IdentifierScope.LOCAL,
                IdentifierUse.READ,
                node.getStartPosition(),
                node.getStartPosition() + node.getLength(),
                method.getSourceContext());
        IdentifierProperties identifier = name.getResolvedIdentifier(method);
        if (identifier != null && memory.get(identifier) != null) {
            result = memory.get(identifier);
        }

        return false;
    }

    public boolean visit(FieldAccess node) {
        IdentifierName name = new IdentifierName(node.getName().getIdentifier(),
                IdentifierType.VARIABLE,
                IdentifierScope.CLASS,
                IdentifierUse.READ,
                node.getStartPosition(),
                node.getStartPosition() + node.getLength(),
                method.getSourceContext());
        IdentifierProperties identifier = name.getResolvedIdentifier(method);
        if (identifier != null && memory.get(identifier) != null) {
            result = memory.get(identifier);
        }

        return false;
    }

    public boolean visit(MethodInvocation node) {
        result = new SubroutineResult(node.getName().getIdentifier());
        return false;
    }

    public boolean visit(Assignment node) {
        EvaluationVisitor visitor = new EvaluationVisitor(memory, method);
        node.getRightHandSide().accept(visitor);
        result = visitor.getResult();

        return false;
    }

    public boolean visit(InfixExpression node) {
        EvaluationVisitor leftVisitor = new EvaluationVisitor(memory, method);
        node.getLeftOperand().accept(leftVisitor);
        IdentifierValue leftResult = leftVisitor.getResult();

        EvaluationVisitor rightVisitor = new EvaluationVisitor(memory, method);
        node.getRightOperand().accept(rightVisitor);
        IdentifierValue rightResult = rightVisitor.getResult();

        result = new BinOpResult(node.getOperator(), leftResult, rightResult);
        return false;
    }

    public boolean visit(NullLiteral node) {
        result = new NullValue();
        return false;
    }

    public boolean visit(NumberLiteral node) {
        if (node.getToken().startsWith("0x")) {
            result = new Constant(Integer.valueOf(node.getToken().substring(2), 16));
        } else if (node.getToken().startsWith("0b")) {
            result = new Constant(Integer.valueOf(node.getToken().substring(2), 2));
        } else if (node.getToken().startsWith("0") && !node.getToken().contains(".") && node.getToken().length() > 1) {
            result = new Constant(Integer.valueOf(node.getToken().substring(1), 8));
        } else if (node.getToken().endsWith("l")) {
            result = new Constant(Long.parseLong(node.getToken().substring(0, node.getToken().length() - 1)));
        } else if (node.getToken().endsWith("f")) {
            result = new Constant(Float.parseFloat(node.getToken().substring(0, node.getToken().length() - 1)));
        } else {
            result = new Constant(Double.parseDouble(node.getToken()));
        }
        return false;
    }

    public boolean visit(PostfixExpression node) {
        EvaluationVisitor visitor = new EvaluationVisitor(memory, method);
        node.getOperand().accept(visitor);
        result = visitor.getResult(); // TODO: update identifier in Expression when this is seen
        return false;
    }

    public boolean visit(PrefixExpression node) {
        EvaluationVisitor visitor = new EvaluationVisitor(memory, method);
        node.getOperand().accept(visitor);
        IdentifierValue operandValue = visitor.getResult();
        if (node.getOperator().equals(PrefixExpression.Operator.INCREMENT)) {
            result = new BinOpResult(InfixExpression.Operator.PLUS, operandValue, new Constant(1)); // TODO: update identifier in Expression when this is seen
        } else if (node.getOperator().equals(PrefixExpression.Operator.DECREMENT)) {
            result = new BinOpResult(InfixExpression.Operator.MINUS, operandValue, new Constant(1)); // TODO: update identifier in Expression when this is seen
        } else {
            result = new UnOpResult(node.getOperator(), operandValue);
        }

        return false;
    }

    public boolean visit(BooleanLiteral node) {
        result = new BooleanValue(node.booleanValue());
        return false;
    }

    public boolean visit(StringLiteral node) {
        result = new StringValue(node.getLiteralValue());
        return false;
    }

    public IdentifierValue getResult() {
        return result;
    }
}
