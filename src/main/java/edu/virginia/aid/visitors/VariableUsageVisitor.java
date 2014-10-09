package edu.virginia.aid.visitors;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;

import java.util.*;

/**
 * Scans an AST for al variable usages
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class VariableUsageVisitor extends NameVisitor {

    private Map<String, Integer> writes = new HashMap<>();
    private Map<String, Integer> reads = new HashMap<>();

    private boolean inAssignment = false;

    public Map<String, Integer> getWrites() {
        return writes;
    }

    public Map<String, Integer> getReads() {
        Map<String, Integer> totalReads = new HashMap<>();
        Set<String> allNames = new HashSet<>();
        allNames.addAll(reads.keySet());
        allNames.addAll(names);
        for (String name : allNames) {
            totalReads.put(name, (reads.containsKey(name) ? reads.get(name) : 0) + Collections.frequency(names, name));
        }

        return totalReads;
    }

    @Override
    public boolean visit(Assignment node) {
        Expression lhs = node.getLeftHandSide();

        NameVisitor visitor = new NameVisitor();
        lhs.accept(visitor);
        for (String name : visitor.getNames()) {
            incrementValue(writes, name);
            if (inAssignment) {
                incrementValue(reads, name);
            }
        }

        Expression rhs = node.getRightHandSide();

        if (!inAssignment) {
            visitor.clearNames();
            rhs.accept(visitor);
            for (String name : visitor.getNames()) {
                incrementValue(reads, name);
            }
        }

        boolean tempInAssignment = inAssignment;

        inAssignment = true;
        rhs.accept(this);
        inAssignment = tempInAssignment;

        return false;
    }

    private void incrementValue(Map<String, Integer> map, String key) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + 1);
        } else {
            map.put(key, 1);
        }
    }
}