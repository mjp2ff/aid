package edu.virginia.aid.symex;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;

/**
 * A Statement or Expression that exists within a Path
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class PathElement {

    private Statement statement = null;
    private Expression expression = null;
    private boolean negated = false;
    private boolean isStatement;

    public PathElement(Statement statement) {
        this.statement = statement;
        isStatement = true;
    }

    public PathElement(Expression expression) {
        this.expression = expression;
        isStatement = false;
    }

    public PathElement(Expression expression, boolean negated) {
        this.expression = expression;
        isStatement = false;
        this.negated = negated;
    }

    public boolean isStatement() {
        return isStatement;
    }

    public boolean isExpression() {
        return !isStatement;
    }

    public Statement getStatement() {
        return statement;
    }

    public Expression getExpression() {
        return expression;
    }

    public boolean isNegated() {
        return negated;
    }

    @Override
    public String toString() {
        if (isStatement) {
            return "Statement: " + statement.toString();
        } else {
            return "Expression: " + expression.toString() + (negated ? " (negated)" : "");
        }
    }
    
    @Override
    public boolean equals(Object o) {
    	try {
    		PathElement p = (PathElement) o;
    		return (this.isStatement && p.isStatement && this.statement.equals(p.statement)) ||
    				(!this.isStatement && !p.isStatement && this.expression.equals(p.expression));
    	} catch (Exception e) {
    		return false;
    	}
    }
}
