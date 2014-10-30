package edu.virginia.aid.parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.virginia.aid.data.MethodFeatures;

/**
 * Given a project directory, this class will find the Ant buildfile if one exists
 * and process it for desired information.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class AntProjectMethodParser extends DirectoryMethodParser {

    Document buildFile;

    /**
     * Creates a new buildfile parser, pointing it to the build.xml file in the given
     * project directory
     *
     * @param directory The directory of the project for which to analyze a buildfile
     */
    public AntProjectMethodParser(String directory) {

        super(directory);

        final String buildfileName = "build.xml";
        String path = ((directory.charAt(directory.length() - 1)) == File.separatorChar) ?
                directory + buildfileName :
                directory + File.separatorChar + buildfileName;

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
    protected Set<String> getBuildDirectories() {
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
    protected Set<File> getSourceFiles() {
        Set<File> sourceFiles = new HashSet<File>();
        for (String subdirectory : getBuildDirectories()) {
            String srcDirectory;
            if (directory.charAt(directory.length() - 1) == File.separatorChar) {
                srcDirectory = directory + subdirectory;
            } else {
                srcDirectory = directory + File.separatorChar + subdirectory;
            }

            File srcFolder = new File(srcDirectory);
            sourceFiles.addAll(getDirectorySourceFiles(srcFolder));
        }

        return sourceFiles;
    }

    /**
     * Finds and processes all methods in the project as specified by the Ant buildfile
     *
     * @return The list of processed methods
     */
    @Override
    public List<MethodFeatures> parseMethods() {
        Set<File> sourceFiles = getSourceFiles();

        List<MethodFeatures> methods = new ArrayList<>();

        System.out.print("Processed 0 methods");
        for (File sourceFile : sourceFiles) {
            methods.addAll(getMethodsFromFile(sourceFile.getPath()));
            System.out.print("\rProcessed " + methods.size() + " methods");
        }

        System.out.println();

        return methods;
    }
}
