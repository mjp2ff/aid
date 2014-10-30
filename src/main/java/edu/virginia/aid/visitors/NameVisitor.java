package edu.virginia.aid.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;

import edu.virginia.aid.data.IdentifierName;
import edu.virginia.aid.data.IdentifierScope;
import edu.virginia.aid.data.IdentifierType;
import edu.virginia.aid.data.IdentifierUse;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.data.MethodInvocationProperties;

public class NameVisitor extends ASTVisitor {

    List<IdentifierName> identifiers = new ArrayList<>();
    List<MethodInvocationProperties> methods = new ArrayList<>();

    MethodFeatures methodFeatures;
    boolean writing;

    /**
     * Creates a visitor with the method and context described below
     *
     * @param methodFeatures The containing method
     * @param writing Whether or not the scope is the lhs of an assignment
     */
    public NameVisitor(MethodFeatures methodFeatures, boolean writing) {
        this.methodFeatures = methodFeatures;
        this.writing = writing;
    }

    public List<IdentifierName> getIdentifiers() {
        return identifiers;
    }

    public List<MethodInvocationProperties> getMethods() {
        return methods;
    }

    public void clearNames() {
        identifiers = new ArrayList<>();
        methods = new ArrayList<>();
    }

    /**
     * Get and log the name of an identifier
     *
     * @param node The identifier's SimpleName ast node
     * @return false
     */
    @Override
    public boolean visit(SimpleName node) {
        identifiers.add(new IdentifierName(node.getIdentifier(),
                                            IdentifierType.VARIABLE,
                                            IdentifierScope.LOCAL,
                                            (writing ? IdentifierUse.WRITE : IdentifierUse.READ),
                                            node.getStartPosition(),
                                            node.getStartPosition() + node.getLength(),
                                            methodFeatures.getSourceContext()));
        return false;
    }

    /**
     * Process method invocations, processing the method name and recursively
     * processing the arguments to the method.
     *
     * @param node Method invocation ast node
     * @return false
     */
    @Override
    public boolean visit(MethodInvocation node) {

        MethodInvocationProperties methodInvocationProperties = new MethodInvocationProperties(node.getName().getIdentifier(), node.getStartPosition(),
                node.getLength() + node.getStartPosition(), methodFeatures.getSourceContext());

        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }

        for(Object argument : node.arguments()) {
            if (argument instanceof Expression) {
                NameVisitor nameVisitor = new NameVisitor(methodFeatures, writing);
                ((Expression) argument).accept(nameVisitor);

                methodInvocationProperties.addArguments(nameVisitor.getIdentifiers());

                // Update our lists with information from parameters
                identifiers.addAll(nameVisitor.getIdentifiers());
                methods.addAll(nameVisitor.getMethods());
            }
        }

        return false;
    }

    /**
     * Process field accesses, ignoring any internal names
     *
     * @param node FieldAccess ast node
     * @return false
     */
    @Override
    public boolean visit(FieldAccess node) {
        identifiers.add(new IdentifierName(node.getName().getIdentifier(),
                IdentifierType.VARIABLE,
                IdentifierScope.CLASS,
                (writing ? IdentifierUse.WRITE : IdentifierUse.READ),
                node.getStartPosition(),
                node.getStartPosition() + node.getLength(),
                methodFeatures.getSourceContext()));
        return false;
    }
}
