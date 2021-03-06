package edu.virginia.aid.symex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;

import edu.virginia.aid.util.ControlFlowGraph;
import edu.virginia.aid.visitors.ConditionVisitor;

/**
 * Representation of a single execution path through all or part of a method.
 * Each path has a sequence of statements and predicates that are executed in
 * order when running the code.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class Path {

    private List<PathElement> pathElements;

    /**
     * Create a new empty path
     */
    public Path() {
        pathElements = new ArrayList<>();
    }

    /**
     * Copy constructor
     *
     * @param p The path to copy
     */
    public Path(Path p) {
        this();
        for (PathElement elem : p.getPathElements()) {
            pathElements.add(elem);
        }
    }

    public Iterator<PathElement> iterator() {
        return pathElements.iterator();
    }

    /**
     * Finds all paths from the beginning of a method to the given statement as
     * defined by the control flow graph provided
     *
     * @param cfg The control flow graph for the method
     * @param statement The statement to find paths to
     * @return All paths that reach the given statement
     */
    public static Collection<Path> getPathsToStatement(ControlFlowGraph cfg, Statement statement) {
        Map<Statement, Set<Statement>> predecessors = cfg.getPredecessors();

        Path p = new Path();
        p.prependElement(new PathElement(statement));

        Set<Path> paths = new HashSet<Path>();
        paths.add(p);

        Set<Path> extendedPaths = new HashSet<Path>();
        extendedPaths.add(p);

        do {
            paths = extendedPaths;
            extendedPaths = new HashSet<>();

            for (Path path : paths) {
                extendedPaths.addAll(path.addPreviousStatement(predecessors));
            }
        } while (!paths.equals(extendedPaths) && extendedPaths.size() <= 100);

        return extendedPaths;
    }

    private Set<Path> addPreviousStatement(Map<Statement, Set<Statement>> predecessors) {
        if (predecessors.get(getFirstStatement()) == null) {
            return new HashSet<>(Arrays.asList(this));
        }

        Set<Path> extendedPaths = new HashSet<>();
        for (Statement predecessor : predecessors.get(getFirstStatement())) {
            if (!containsStatement(predecessor) && !(predecessor instanceof ThrowStatement)) {
                Path copy = new Path(this);

                ConditionVisitor visitor = new ConditionVisitor(getFirstStatement());
                predecessor.accept(visitor);

                if (visitor.getCondition() != null) {
                    copy.prependElement(new PathElement(visitor.getCondition(), visitor.isNegated()));
                }

                copy.prependElement(new PathElement(predecessor));
                extendedPaths.add(copy);
            }
        }

        if (extendedPaths.size() == 0) {
            extendedPaths.add(this);
        }

        return extendedPaths;
    }

    private boolean containsStatement(Statement statement) {
    	// Wrap statement in a PathElement for better comparison.
    	PathElement p = new PathElement(statement);
        return pathElements.contains(p);
    }

    public int hashCode() {
        return pathElements.hashCode();
    }

    public List<PathElement> getPathElements() {
        return pathElements;
    }

    public Statement getFirstStatement() {
        for (PathElement element : pathElements) {
            if (element.isStatement()) {
                return element.getStatement();
            }
        }

        return null;
    }

    public void prependElement(PathElement element) {
        List<PathElement> newList = new ArrayList<>();
        newList.add(element);
        newList.addAll(pathElements);
        pathElements = newList;
    }

    @Override
    public String toString() {
        String value = "Path: \n";
        for (PathElement element : pathElements) {
            value += "\t" + element.toString() + "\n";
        }

        return value;
    }
}
