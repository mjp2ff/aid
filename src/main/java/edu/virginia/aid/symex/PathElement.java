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
    private boolean isStatement;

    public PathElement(Statement statement) {
        this.statement = statement;
        isStatement = true;
    }

    public PathElement(Expression expression) {
        this.expression = expression;
        isStatement = false;
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
}
