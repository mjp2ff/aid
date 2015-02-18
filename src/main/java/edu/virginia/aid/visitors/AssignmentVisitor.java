package edu.virginia.aid.visitors;

import edu.virginia.aid.data.*;
import org.eclipse.jdt.core.dom.*;

/**
 * ASTVisitor for finding assignment expressions within a given statement
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class AssignmentVisitor extends ASTVisitor {

    private IdentifierName variable = null;
    private Expression value = null;
    private MethodFeatures method;

    public AssignmentVisitor(MethodFeatures method) {
        this.method = method;
    }

    public boolean visit(Assignment node) {
        NameVisitor visitor = new NameVisitor(method, true);
        node.getLeftHandSide().accept(visitor);
        if (visitor.getIdentifiers().size() > 0) {
            variable = visitor.getIdentifiers().get(0);
            value = node.getRightHandSide();
        }
        return true;
    }

    public boolean visit(VariableDeclarationFragment node) {
        variable = new IdentifierName(node.getName().getIdentifier(),
                IdentifierType.VARIABLE,
                IdentifierScope.LOCAL,
                IdentifierUse.WRITE,
                node.getStartPosition(),
                node.getStartPosition() + node.getLength(),
                method.getSourceContext());
        value = node.getInitializer();
        return true;
    }

    public boolean isAssignment() {
        return variable != null && value != null;
    }

    public IdentifierName getVariable() {
        return variable;
    }

    public Expression getValue() {
        return value;
    }
}
