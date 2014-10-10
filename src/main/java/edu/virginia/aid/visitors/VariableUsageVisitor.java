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

    private Map<String, Integer> identifierWrites = new HashMap<>();
    private Map<String, Integer> identifierReads = new HashMap<>();
    private Map<String, Integer> fieldWrites = new HashMap<>();
    private Map<String, Integer> fieldReads = new HashMap<>();

    private boolean inAssignment = false;

    public Map<String, Integer> getIdentifierWrites() {
        return identifierWrites;
    }

    public Map<String, Integer> getIdentifierReads() {
        Map<String, Integer> totalReads = new HashMap<>();
        Set<String> allNames = new HashSet<>();
        allNames.addAll(identifierReads.keySet());
        allNames.addAll(identifiers);
        for (String name : allNames) {
            totalReads.put(name, (identifierReads.containsKey(name) ? identifierReads.get(name) : 0) + Collections.frequency(identifiers, name));
        }

        return totalReads;
    }

    public Map<String, Integer> getFieldWrites() {
        return fieldWrites;
    }

    public Map<String, Integer> getFieldReads() {
        Map<String, Integer> totalReads = new HashMap<>();
        Set<String> allNames = new HashSet<>();
        allNames.addAll(fieldReads.keySet());
        allNames.addAll(fields);
        for (String name : allNames) {
            totalReads.put(name, (fieldReads.containsKey(name) ? fieldReads.get(name) : 0) + Collections.frequency(fields, name));
        }

        return totalReads;
    }

    public Set<String> getFieldNames() {
        Set<String> names = new HashSet<>();
        names.addAll(getFieldReads().keySet());
        names.addAll(fieldWrites.keySet());
        return names;
    }

    @Override
    public boolean visit(Assignment node) {
        Expression lhs = node.getLeftHandSide();

        NameVisitor visitor = new NameVisitor();
        lhs.accept(visitor);
        for (String name : visitor.getIdentifiers()) {
            incrementValue(identifierWrites, name);
            if (inAssignment) {
                incrementValue(identifierReads, name);
            }
        }

        for (String name : visitor.getFields()) {
            incrementValue(fieldWrites, name);
            if (inAssignment) {
                incrementValue(fieldReads, name);
            }
        }

        Expression rhs = node.getRightHandSide();

        if (!inAssignment) {
            visitor.clearNames();
            rhs.accept(visitor);
            for (String name : visitor.getIdentifiers()) {
                incrementValue(identifierReads, name);
            }

            for (String name : visitor.getFields()) {
                incrementValue(fieldReads, name);
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