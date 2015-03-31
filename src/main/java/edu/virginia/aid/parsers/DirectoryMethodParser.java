package edu.virginia.aid.parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.virginia.aid.data.MethodFeatures;

/**
 * Parser for finding and processing all methods located within a given directory on disk.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class DirectoryMethodParser extends MethodParser {

    /**
     * The directory containing the methods to parse
     */
    String directory;

    /**
     * Creates a new parser for methods within the given directory
     *
     * @param directory The directory containing the methods to parse
     * @param documentedOnly Whether to exclude methods that have no javadoc summary
     */
    public DirectoryMethodParser(String directory, boolean documentedOnly) {
        super(documentedOnly);
        this.directory = directory;
    }

    /**
     * Recursively finds and returns all source files in a directory
     *
     * @param directory The directory to search
     * @return The set of source files found in the directory
     */
    protected Set<File> getDirectorySourceFiles(File directory) {
        Set<File> sourceFiles = new HashSet<File>();

        if (directory.isDirectory() && directory.canRead()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for(File file : files) {
                    if (file.isDirectory()) {
                        sourceFiles.addAll(getDirectorySourceFiles(file));
                    } else {
                        if (file.getName().endsWith(".java")) {
                            sourceFiles.add(file);
                        }
                    }
                }
            }
        }

        return sourceFiles;
    }

    /**
     * Finds and processes all methods located within a source directory
     *
     * @return The processed methods in the directory
     */
    protected List<MethodFeatures> parseMethods(boolean trainingMode) {
        Set<File> sourceFiles = getDirectorySourceFiles(new File(directory));

        List<MethodFeatures> methods = new ArrayList<>();

        System.out.print("Processed 0 methods");
        for (File sourceFile : sourceFiles) {
            methods.addAll(getMethodsFromFile(sourceFile.getPath(), trainingMode));
            System.out.print("\rProcessed " + methods.size() + " methods");
        }

        System.out.println();

        return methods;
    }
}
