package edu.virginia.aid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import edu.virginia.aid.comparison.MethodDifferences;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.parsers.AntProjectMethodParser;
import edu.virginia.aid.parsers.DirectoryMethodParser;
import edu.virginia.aid.parsers.FileMethodParser;
import edu.virginia.aid.parsers.MethodParser;

/**
 * A Driver is used to analyze a file or project, parse out the code and comments, and split
 * it up into the different methods contained. The Driver then calls out to
 * individual method analysis tools.
 * 
 * @author Matt Pearson-Beck & Jeff Principe
 *
 */
public class Driver {

    /**
     * Performs comparison check on each method and sorts them from most to least different
     *
     * @param methodFeaturesList Feature information for each method
     * @return Sorted list of differences for each method
     */
    public static List<MethodDifferences> compareAndRank(List<MethodFeatures> methodFeaturesList) {
        List<MethodDifferences> differences = new ArrayList<MethodDifferences>();

        // Pass one to get words for TFIDF
        List<List<String>> allProjectWords = new ArrayList<List<String>>();
        for (MethodFeatures methodFeatures : methodFeaturesList) {
        	allProjectWords.add(methodFeatures.getAllWords());
        }

        // Pass two to calculate differences.
        for (MethodFeatures methodFeatures : methodFeaturesList) {
        	// TODO: Create phases to guarantee TFIDF is calculated before differences are found.
        	methodFeatures.calculateTFIDF(allProjectWords);
            differences.add(methodFeatures.getDifferences());
        }

        Collections.sort(differences);

        return differences;
    }

    /**
     * Prompts the user for input until the pattern passed is satisfied
     *
     * @param prompt The prompt for the user to respond to
     * @param inputPattern The regular expression user responses need to match
     * @param keyboard Scanner to get input from
     * @return
     */
    public static String promptUser(String prompt, Pattern inputPattern, Scanner keyboard) {
        String userInput;
        do {
            System.out.print(prompt);
            userInput = keyboard.nextLine();
        } while (!inputPattern.matcher(userInput).matches());
        return userInput;
    }

    /**
     * Provides interactive interface for displaying method differences given a list of differences to display
     *
     * @param rankedDifferences The differences to display
     * @param methodsPerPage The number of methods to show per page
     * @param keyboard Scanner for collecting user input
     */
    public static void displayDifferences(List<MethodDifferences> rankedDifferences, int methodsPerPage, Scanner keyboard) {
        int displayIndex = 0;

        while (displayIndex < rankedDifferences.size()) {
            for (int i = 0; i < methodsPerPage && displayIndex < rankedDifferences.size(); i++, displayIndex++) {
                System.out.println(rankedDifferences.get(displayIndex));
            }

            System.out.print("\n" + (displayIndex) + "/" + rankedDifferences.size() + " displayed.");
            if (displayIndex < rankedDifferences.size()) {
                System.out.print(" Display next " + Math.min(methodsPerPage, rankedDifferences.size() - displayIndex) + " methods? (y/n): ");
            }
            if (!keyboard.nextLine().equalsIgnoreCase("y")) break;
        }
    }

    /**
     * Runs tool on input files as specified in the command line parameters. There are two modes, either project or file,
     * set using the -projects or -files flag as shown below.
     *
     * Usage:
     *      java Driver [-projects|-files] [project1/file1 project2/file2 ...]
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("-files")) {
                for(int i = 1; i < args.length; i++) {

                    MethodParser parser = new FileMethodParser(args[i]);

                    // Parse this file to get the appropriate data
                    List<MethodFeatures> methods = parser.parseMethods();

                    // Get differences for each method and rank them by most different to least different
                    List<MethodDifferences> differences = compareAndRank(methods);

                    displayDifferences(differences, 10, new Scanner(System.in));
                }
            } else if (args[0].equals("-projects")) {
                for(int i = 1; i < args.length; i++) {

                    MethodParser parser = new AntProjectMethodParser(args[i]);

                    // Parse this file to get the appropriate data
                    List<MethodFeatures> methods = parser.parseMethods();

                    // Get differences for each method and rank them by most different to least different
                    List<MethodDifferences> differences = compareAndRank(methods);

                    displayDifferences(differences, 10, new Scanner(System.in));
                }
            } else if (args[0].equals("-directories")) {
                for(int i = 1; i < args.length; i++) {

                    MethodParser parser = new DirectoryMethodParser(args[i]);

                    // Parse this directory to get the appropriate data
                    List<MethodFeatures> methods = parser.parseMethods();

                    // Get differences for each method and rank them by most different to least different
                    List<MethodDifferences> differences = compareAndRank(methods);

                    displayDifferences(differences, 10, new Scanner(System.in));
                }
            }
        } else {
            Scanner keyboard = new Scanner(System.in);

            String mode = promptUser("Would you like to load a project or a file? (p/f): ", Pattern.compile("p|f"), keyboard);

            // Project mode
            if (mode.equalsIgnoreCase("p")) {
                System.out.print("Provide the path to the project to analyze: ");
                String projectPath = keyboard.nextLine();

                MethodParser parser = new AntProjectMethodParser(projectPath);

                List<MethodFeatures> methods = parser.parseMethods();

                String analysisMode = promptUser("Would you like to analyze methods from a class or the entire project? (c/p): ", Pattern.compile("c|p"), keyboard);

                if (analysisMode.equalsIgnoreCase("c")) {
                    System.out.print("Please specify class name: ");
                    String className = keyboard.nextLine();

                    List<MethodFeatures> classMethods = new ArrayList<>();
                    for (MethodFeatures method : methods) {
                        if (method.getParentClass().getClassName().equals(className)) {
                            classMethods.add(method);
                        }
                    }

                    displayDifferences(compareAndRank(classMethods), 10, keyboard);
                } else if (analysisMode.equalsIgnoreCase("p")) {
                    displayDifferences(compareAndRank(methods), 10, keyboard);
                }

                System.out.println();
            } else if (mode.equalsIgnoreCase("f")) {
                String filePath;

                System.out.print("Provide the path to the file to analyze: ");
                filePath = keyboard.nextLine();

                MethodParser parser = new FileMethodParser(filePath);

                List<MethodFeatures> methods = parser.parseMethods();

                List<MethodDifferences> differences = compareAndRank(methods);
                displayDifferences(differences, 10, keyboard);
            }
        }
    }
}
