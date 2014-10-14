package edu.virginia.aid.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ReturnStatement;

import java.util.Map;

/**
 * Finds and processes all return statements within an ast
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ReturnVisitor extends ASTVisitor {

    public Map<String, Integer> identifierReads;
    public Map<String, Integer> fieldReads;

    /**
     * Gets all identifier and field reads contained within a return statement
     *
     * @param node The return statement to process
     * @return false
     */
    @Override
    public boolean visit(ReturnStatement node) {
        VariableUsageVisitor usageVisitor = new VariableUsageVisitor();
        node.getExpression().accept(usageVisitor);
        identifierReads = usageVisitor.getIdentifierReads();
        fieldReads = usageVisitor.getFieldReads();
        return false;
    }

    /**
     * Gets and returns all identifier reads contained within the return statement
     *
     * @return Identifier reads within return statement
     */
    public Map<String, Integer> getIdentifierReads() {
        return identifierReads;
    }

    /**
     * Gets and returns all field reads contained within the return statement
     *
     * @return Field reads within return statement
     */
    public Map<String, Integer> getFieldReads() {
        return fieldReads;
    }
}
