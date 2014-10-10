package edu.virginia.aid.visitors;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

public class NameVisitor extends ASTVisitor {

    List<String> identifiers = new ArrayList<>();
    List<String> fields = new ArrayList<>();
    List<String> methods = new ArrayList<>();

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public List<String> getFields() {
        return fields;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void clearNames() {
        identifiers = new ArrayList<>();
        fields = new ArrayList<>();
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
        identifiers.add(node.getIdentifier());
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

        methods.add(node.getName().getIdentifier());

        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }

        for(Object argument : node.arguments()) {
            if (argument instanceof Expression) {
                ((Expression) argument).accept(this);
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
        fields.add(node.getName().getIdentifier());
        return false;
    }
}
