package edu.virginia.aid.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;

import edu.virginia.aid.data.BlockProperties;
import edu.virginia.aid.data.ConditionProperties;
import edu.virginia.aid.data.IfStatementProperties;
import edu.virginia.aid.data.LoopProperties;
import edu.virginia.aid.data.MethodFeatures;

/**
 * Finds top-level control flow structures and the information contained within them
 */
public class ControlFlowVisitor extends ASTVisitor {

    private List<IfStatementProperties> conditionals = new ArrayList<>();
    private List<LoopProperties> loops = new ArrayList<>();
    private MethodFeatures method;

    /**
     * Creates a ControlFlowVisitor with the given context
     *
     * @param method The method that is beign parsed currently
     */
    public ControlFlowVisitor(MethodFeatures method) {
        this.method = method;
    }

    /**
     * Finds top-level if statements and parses relevant information
     *
     * @param node The if statement to parse
     * @return false
     */
    @Override
    public boolean visit(IfStatement node) {
        IfStatementProperties ifStatementProperties = new IfStatementProperties(node.getStartPosition(),
                node.getLength() + node.getStartPosition(), method.getSourceContext());
        getConditionalBranches(node, ifStatementProperties, new ConditionProperties());

        return false;
    }

    private void getConditionalBranches(IfStatement node, IfStatementProperties ifStatementProperties, ConditionProperties conditions) {
        Expression condition = node.getExpression();

        ConditionProperties conditionProperties = new ConditionProperties(conditions);
        conditionProperties.addCondition(condition, false);

        addConditionalBranch(node.getThenStatement(), ifStatementProperties, conditionProperties);

        // Get other branches recursively if necessary
        Statement elseStatement = node.getElseStatement();
        ConditionProperties elseConditionProperties = new ConditionProperties(conditions);
        elseConditionProperties.addCondition(condition, true);
        if (elseStatement != null) {
            if (elseStatement.getNodeType() == ASTNode.IF_STATEMENT) {
                getConditionalBranches((IfStatement) elseStatement, ifStatementProperties, elseConditionProperties);
            } else {
                addConditionalBranch(elseStatement, ifStatementProperties, elseConditionProperties);
            }
        }
    }

    private void addConditionalBranch(Statement block, IfStatementProperties ifStatement, ConditionProperties conditionProperties) {
        // Get information for then branch
        VariableUsageVisitor usageVisitor = new VariableUsageVisitor(method, false);
        block.accept(usageVisitor);

        BlockProperties blockProperties = new BlockProperties(method.getScope(), block.getStartPosition(), block.getLength() + block.getStartPosition(), method.getSourceContext());
        usageVisitor.getIdentifierUses().forEach(identifierUse -> blockProperties.getScope().addIdentifierUse(identifierUse));

        // Add the branch
        ifStatement.addBranch(conditionProperties, blockProperties);
    }

    /**
     * Finds top-level for loops and parses relevant information
     *
     * @param node The for loop to parse
     * @return false
     */
    @Override
    public boolean visit(ForStatement node) {
        return false;
    }

    /**
     * Finds top-level for-each loops and parses relevant information
     *
     * @param node The for-each loop to parse
     * @return false
     */
    @Override
    public boolean visit(EnhancedForStatement node) {
        return false;
    }

    /**
     * Finds top-level while loops and parses relevant information
     *
     * @param node The while loop to parse
     * @return false
     */
    @Override
    public boolean visit(WhileStatement node) {
        return false;
    }

    /**
     * Finds top-level do while loops and parses relevant information
     *
     * @param node The do while loop to parse
     * @return false
     */
    @Override
    public boolean visit(DoStatement node) {
        return false;
    }

    public List<IfStatementProperties> getConditionals() {
        return conditionals;
    }

    public List<LoopProperties> getLoops() {
        return loops;
    }
}
