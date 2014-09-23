package edu.virginia.aid.comparison;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Given a project directory, this class will find the Ant buildfile if one exists
 * and process it for desired information.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class AntBuildfileParser {

    String projectDirectory;
    Document buildFile;

    /**
     * Creates a new buildfile parser, pointing it to the build.xml file in the given
     * project directory
     *
     * @param projectDirectory The directory of the project for which to analyze a buildfile
     */
    public AntBuildfileParser(String projectDirectory) {
        this.projectDirectory = projectDirectory;

        final String buildfileName = "build.xml";
        String path = ((projectDirectory.charAt(projectDirectory.length() - 1)) == File.separatorChar) ?
                projectDirectory + buildfileName :
                projectDirectory + File.separatorChar + buildfileName;

        File buildfile = new File(path);

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            buildFile = documentBuilder.parse(buildfile);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds and returns all of the directories to build in all of the javac elements
     * in the buildfile for the project
     *
     * @return Set of directories present in the buildfile's javac srcdir attributes
     */
    public Set<String> getBuildDirectories() {
        Set<String> buildDirectories = new HashSet<String>();
        NodeList javacElements = buildFile.getElementsByTagName("javac");

        for (int i = 0; i < javacElements.getLength(); i++) {
            Node javacNode = javacElements.item(i);
            if (javacNode.getNodeType() == Node.ELEMENT_NODE) {
                buildDirectories.add(((Element) javacNode).getAttribute("srcdir"));
            }
        }

        return buildDirectories;
    }

    /**
     * Finds and returns all java files within the build directories of the project
     *
     * @return Set of java files in the project
     */
    public Set<File> getSourceFiles() {
        Set<File> sourceFiles = new HashSet<File>();
        for (String subdirectory : getBuildDirectories()) {
            String srcDirectory;
            if (projectDirectory.charAt(projectDirectory.length() - 1) == File.separatorChar) {
                srcDirectory = projectDirectory + subdirectory;
            } else {
                srcDirectory = projectDirectory + File.separatorChar + subdirectory;
            }

            File srcFolder = new File(srcDirectory);
            sourceFiles.addAll(getDirectorySourceFiles(srcFolder));
        }

        return sourceFiles;
    }

    /**
     * Recursively finds and returns all source files in a directory
     *
     * @param directory The directory to search
     * @return The set of source files found in the directory
     */
    private Set<File> getDirectorySourceFiles(File directory) {
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
}
