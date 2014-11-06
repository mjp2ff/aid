package edu.virginia.aid.parsers;

import java.io.File;
import java.util.*;

import edu.virginia.aid.data.MethodFeatures;

public class DirectoryMethodParser extends MethodParser {

    String directory;

    public DirectoryMethodParser(String directory) {
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
