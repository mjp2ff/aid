package edu.virginia.aid.symex;

import edu.virginia.aid.util.ControlFlowGraph;
import org.eclipse.jdt.core.dom.Statement;

import java.util.*;

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

        } while (paths.equals(extendedPaths));

        return paths;
    }

    private Set<Path> addPreviousStatement(Map<Statement, Set<Statement>> predecessors) {
        Set<Path> extendedPaths = new HashSet<>();
        for (Statement predecessor : predecessors.get(getFirstStatement())) {
            if (!containsStatement(predecessor)) {
                Path copy = new Path(this);
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
        return pathElements.contains(statement);
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
}
