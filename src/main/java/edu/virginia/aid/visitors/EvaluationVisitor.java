package edu.virginia.aid.visitors;

import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.ThisExpression;

import edu.virginia.aid.data.IdentifierName;
import edu.virginia.aid.data.IdentifierProperties;
import edu.virginia.aid.data.IdentifierScope;
import edu.virginia.aid.data.IdentifierType;
import edu.virginia.aid.data.IdentifierUse;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.symex.BinOpResult;
import edu.virginia.aid.symex.BooleanValue;
import edu.virginia.aid.symex.CharacterValue;
import edu.virginia.aid.symex.Constant;
import edu.virginia.aid.symex.ExternalValue;
import edu.virginia.aid.symex.IdentifierValue;
import edu.virginia.aid.symex.NullValue;
import edu.virginia.aid.symex.StringValue;
import edu.virginia.aid.symex.SubroutineResult;
import edu.virginia.aid.symex.UnOpResult;

/**
 * ASTVisitor for building up an IdentifierValue from an Expression
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class EvaluationVisitor extends ASTVisitor {

    /**
     * The current mapping of identifiers to values in execution
     */
    private Map<IdentifierProperties, IdentifierValue> memory;

    /**
     * The method currently being processed
     */
    private MethodFeatures method;

    /**
     * The expression found in the AST subtree visited (or null)
     */
    private IdentifierValue result;

    /**
     * Creates an ASTVisitor for creating IdentifierValues from Expressions. This is mainly
     * useful for symbolic execution.
     *
     * @param memory The current mapping of identifiers to values in execution
     * @param method The method currently being processed
     */
    public EvaluationVisitor(Map<IdentifierProperties, IdentifierValue> memory, MethodFeatures method) {
        this.memory = memory;
        this.method = method;
        this.result = null;
    }

    /**
     * Replaces an identifier with its value in memory
     *
     * @param node The identifier to replace
     * @return False
     */
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
        } else if (identifier == null) {
            result = new ExternalValue(node.getIdentifier()); // TODO: figure out why some fields aren't being resolved
        }

        return false;
    }

    /**
     * Replaces a QualifiedName with its corresponding IdentifierValue
     *
     * @param node The QualifiedName to replace
     */
    public void endVisit(QualifiedName node) {
        if (result == null) {
            result = new ExternalValue(node.getFullyQualifiedName());
        }
    }

    /**
     * Replaces "this" with its corresponding IdentifierValue
     *
     * @param node "this" expression
     */
    public void endVisit(ThisExpression node) {
        if (result == null) {
            result = new ExternalValue("this");
        }
    }

    /**
     * Replaces a field reference with its corresponding value in memory
     *
     * @param node The field reference to replace
     * @return false
     */
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

    /**
     * Replaces a method invocation with its IdentifierValue representation
     *
     * @param node The method invocation to replace
     * @return false
     */
    public boolean visit(MethodInvocation node) {
        result = new SubroutineResult(node.getName().getIdentifier());
        return false;
    }

    /**
     * Replaces an assignment statement with the result of evaluating its right and side
     *
     * @param node The assignment expression to evaluate
     * @return false
     */
    public boolean visit(Assignment node) {
        EvaluationVisitor visitor = new EvaluationVisitor(memory, method);
        node.getRightHandSide().accept(visitor);
        result = visitor.getResult();

        return false;
    }

    /**
     * Replaces an infix expression with a recursive contruction of its equivalent IdentifierValue
     * representation.
     *
     * @param node The InfixExpression to evaluate and replace
     * @return false
     */
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

    /**
     * Replaces a null literal with its IdentifierValue representation
     *
     * @param node Null literal
     * @return false
     */
    public boolean visit(NullLiteral node) {
        result = new NullValue();
        return false;
    }

    /**
     * Replace a numeric literal with an equivalent representation as an IdentifierValue
     *
     * @param node The number literal to replace
     * @return false
     */
    public boolean visit(NumberLiteral node) {
    	try {
            if (node.getToken().startsWith("0x")) {
                result = new Constant(Long.decode(node.getToken()));
            } else if (node.getToken().startsWith("0b")) {
                result = new Constant(Long.valueOf(node.getToken().substring(2), 2));
            } else if (node.getToken().charAt(node.getToken().length() - 1) <= 57 && node.getToken().startsWith("0") && !node.getToken().contains(".") && node.getToken().length() > 1) {
                result = new Constant(Long.valueOf(node.getToken().substring(1), 8));
            } else if (node.getToken().endsWith("l") || node.getToken().endsWith("L")) {
                result = new Constant(Long.parseLong(node.getToken().substring(0, node.getToken().length() - 1)));
            } else if (node.getToken().endsWith("f") || node.getToken().endsWith("F")) {
                result = new Constant(Float.parseFloat(node.getToken().substring(0, node.getToken().length() - 1)));
            } else {
                result = new Constant(Double.parseDouble(node.getToken()));
            }    		
    	} catch (Exception e) {
    		// Don't set result, will catch null later.
    	}
        return false;
    }

    /**
     * Replace a character literal with an equivalent representation as an IdentifierValue
     *
     * @param node The character literal to replace
     * @return false
     */
    public boolean visit(CharacterLiteral node) {
        result = new CharacterValue(node.charValue());
        return false;
    }

    /**
     * Replace a postfix expression with an equivalent representation as an IdentifierValue. The
     * operand of the expression is recursively evaluated and transformed
     *
     * @param node The expression to replace
     * @return false
     */
    public boolean visit(PostfixExpression node) {
        EvaluationVisitor visitor = new EvaluationVisitor(memory, method);
        node.getOperand().accept(visitor);
        result = visitor.getResult(); // TODO: update identifier in Expression when this is seen
        return false;
    }

    /**
     * Replace a prefix expression with an equivalent representation as an IdentifierValue. The
     * operand of the expression is recursively evaluated and transformed
     *
     * @param node The expression to replace
     * @return false
     */
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

    /**
     * Replaces a boolean literal with its IdentifierValue representation
     *
     * @param node The boolean literal to transform
     * @return false
     */
    public boolean visit(BooleanLiteral node) {
        result = new BooleanValue(node.booleanValue());
        return false;
    }

    /**
     * Replaces a string literal with its IdentifierValue representation
     *
     * @param node The string literal to transform
     * @return false
     */
    public boolean visit(StringLiteral node) {
        result = new StringValue(node.getLiteralValue());
        return false;
    }

    /**
     * Gets the result of transformation of the expression to an IdentifierValue (or null if
     * no such expression was present)
     *
     * @return IdentifierValue representing the current symbolic execution value of an expression
     */
    public IdentifierValue getResult() {
        return result;
    }
}
