package edu.virginia.aid.visitors;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

public class NameVisitor extends ASTVisitor {

    List<String> names = new ArrayList<>();

    public List<String> getNames() {
        return names;
    }

    public void clearNames() {
        names = new ArrayList<>();
    }

    /**
     * Get and log the name of an identifier
     *
     * @param node The identifier's SimpleName ast node
     * @return false
     */
    @Override
    public boolean visit(SimpleName node) {
        names.add(node.getIdentifier());
        return false;
    }

    /**
     * Process method invocations, ignoring the method name and recursively
     * processing the arguments to the method.
     *
     * @param node Method invocation ast node
     * @return false
     */
    @Override
    public boolean visit(MethodInvocation node) {
        for(Object argument : node.arguments()) {
            if (argument instanceof Expression) {
                ((Expression) argument).accept(this);
            }
        }

        return false;
    }

}
