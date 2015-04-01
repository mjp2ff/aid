package edu.virginia.aid.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.virginia.aid.comparison.Difference;
import edu.virginia.aid.comparison.MethodDifferences;
import edu.virginia.aid.data.MethodFeatures;
import edu.virginia.aid.data.MethodSignature;
import edu.virginia.aid.parsers.AntProjectMethodParser;
import edu.virginia.aid.parsers.DirectoryMethodParser;
import edu.virginia.aid.parsers.FileMethodParser;
import edu.virginia.aid.parsers.IndividualMethodParser;
import edu.virginia.aid.parsers.MethodParser;
import org.apache.commons.cli.*;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * A Driver is used to analyze a file or project, parse out the code and comments, and split
 * it up into the different methods contained. The Driver then calls out to
 * individual method analysis tools.
 * 
 * @author Matt Pearson-Beck & Jeff Principe
 *
 */
public class Driver {
	
    public static final String WORDNET_FILEPATH = "wordnet/dict";
    public static final String CLASSIFICATION_TRAINING_SET_FILEPATH = "training/primaryAction.arff";

    /**
     * Performs comparison check on each method and sorts them from most to least different
     *
     * @param methodFeaturesList Feature information for each method
     * @return Sorted list of differences for each method
     */
    public static List<MethodDifferences> compareAndRank(List<MethodFeatures> methodFeaturesList) {
        List<MethodDifferences> differences = new ArrayList<MethodDifferences>();

        // Pass one to get words for TFIDF
        List<Map<String, Integer>> allProjectWordFrequencies = new ArrayList<>();

        for (MethodFeatures methodFeatures : methodFeaturesList) {
        	allProjectWordFrequencies.add(methodFeatures.getWordFrequencies());
        }

        File wordNetFile = new File(WORDNET_FILEPATH);
        IDictionary wordNetDictionary = new Dictionary(wordNetFile);
        try {
			wordNetDictionary.open();
		} catch (IOException e) {
			e.printStackTrace();
		}

        System.out.print("Computed differences for 0 methods");

        // Pass two to calculate differences.
        for (MethodFeatures methodFeatures : methodFeaturesList) {
            // TODO: Create phases to guarantee TFIDF is calculated before differences are found.
            methodFeatures.calculateTFIDF(allProjectWordFrequencies);
            differences.add(methodFeatures.getDifferences(wordNetDictionary));

            System.out.print("\rComputed differences for " + differences.size() + " methods");
        }

        System.out.print("\n Ranking methods by difference score... ");
        Collections.sort(differences);
        System.out.println("DONE\n");

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
            if (displayIndex >= rankedDifferences.size()) break;
            if (!keyboard.nextLine().equalsIgnoreCase("y")) break;
        }
    }

    public static void displayMethodDetails(List<MethodDifferences> rankedDifferences, Scanner keyboard) {
        int displayIndex = 0;

        while (displayIndex < rankedDifferences.size()) {
            System.out.println("\n");
            System.out.println("\033[1m" + rankedDifferences.get(displayIndex).getMethod().getParentClass().getClassName()
                    + "." + rankedDifferences.get(displayIndex).getMethod().getMethodName() + "()\033[0m");
            System.out.println(rankedDifferences.get(displayIndex));
            System.out.println("Method Source:");
            System.out.println(rankedDifferences.get(displayIndex).getMethod().getElementText());

            if (displayIndex < rankedDifferences.size() - 1) {
                System.out.print("Display next method? (y/n): ");
            }

            if (!keyboard.nextLine().equalsIgnoreCase("y")) break;
        }
    }

    public static Options getCommandLineOptions() {
        Options options = new Options();
        options.addOption("m", "mode", true, "The mode that the tool should run in. Value can be any of the following: " +
                "train, files, projects, directories, methods");
        options.addOption("d", "documented-only", false, "Limits output to only methods that contain a Javadoc summary");
        options.addOption("i", "individual", false, "Displays methods one at a time with both the differences and the " +
                "method's source code");
        return options;
    }

    /**
     * Runs tool on input files as specified in the command line parameters. There are two modes, either project or file,
     * set using the -projects or -files flag as shown below.
     *
     * Usage:
     *      java Driver -m [train/files/projects/directories/methods] path1 path2 ...
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) throws ParseException {
        CommandLineParser argParser = new GnuParser();
        CommandLine cmd = argParser.parse(getCommandLineOptions(), args);

        if (cmd.hasOption("mode")) {
            if (cmd.getOptionValue("mode").equals("train")) {
                if (cmd.getArgs().length == 2) {

                    MethodParser parser = new DirectoryMethodParser(cmd.getArgs()[0], cmd.hasOption('d'));

                    // Parse this directory to get the appropriate data
                    Map<String, List<MethodFeatures>> labeledMethods = parser.createTrainingSet("primaryAction");

                    // Create training data set
                    WekaHelper.buildTrainingDataFile(labeledMethods, "primaryAction", cmd.getArgs()[1]);

                } else {
                    throw new RuntimeException("No directory provided for training set and/or location for training data file");
                }
            } else if (cmd.getOptionValue("mode").equals("files")) {
                for(int i = 0; i < cmd.getArgs().length; i++) {

                    MethodParser parser = new FileMethodParser(cmd.getArgs()[i], cmd.hasOption('d'));

                    // Parse this file to get the appropriate data
                    List<MethodFeatures> methods = parser.parseMethods();

                    // Get differences for each method and rank them by most different to least different
                    List<MethodDifferences> differences = compareAndRank(methods);

                    if (cmd.hasOption("i")) {
                        displayMethodDetails(differences, new Scanner(System.in));
                    } else {
                        displayDifferences(differences, 10, new Scanner(System.in));
                    }
                }
            } else if (cmd.getOptionValue("mode").equals("projects")) {
                for(int i = 0; i < cmd.getArgs().length; i++) {

                    MethodParser parser = new AntProjectMethodParser(cmd.getArgs()[i], cmd.hasOption('d'));

                    // Parse this file to get the appropriate data
                    List<MethodFeatures> methods = parser.parseMethods();

                    // Get differences for each method and rank them by most different to least different
                    List<MethodDifferences> differences = compareAndRank(methods);

                    if (cmd.hasOption("i")) {
                        displayMethodDetails(differences, new Scanner(System.in));
                    } else {
                        displayDifferences(differences, 10, new Scanner(System.in));
                    }
                }
            } else if (cmd.getOptionValue("mode").equals("directories")) {
                for(int i = 0; i < cmd.getArgs().length; i++) {

                    MethodParser parser = new DirectoryMethodParser(cmd.getArgs()[i], cmd.hasOption('d'));

                    // Parse this directory to get the appropriate data
                    List<MethodFeatures> methods = parser.parseMethods();

                    // Get differences for each method and rank them by most different to least different
                    List<MethodDifferences> differences = compareAndRank(methods);

                    if (cmd.hasOption("i")) {
                        displayMethodDetails(differences, new Scanner(System.in));
                    } else {
                        displayDifferences(differences, 10, new Scanner(System.in));
                    }
                }
            } else if (cmd.getOptionValue("mode").equals("methods")) {
                Map<String, List<MethodSignature>> methodsToParse = new HashMap<>();
                try {
                    Scanner csvReader = new Scanner(new File(cmd.getArgs()[0]));
                    while (csvReader.hasNextLine()) {
                        String line = csvReader.nextLine();
                        if (!line.startsWith("#")) {
                            String[] parts = line.split(Pattern.quote(","));
                            if (parts.length >= 2) {
                                if (!methodsToParse.containsKey(parts[0])) {
                                    methodsToParse.put(parts[0], new ArrayList<>());
                                }

                                methodsToParse.get(parts[0]).add(new MethodSignature(parts[1], Arrays.copyOfRange(parts, 2, parts.length)));
                            }
                        }
                    }
                    csvReader.close();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                MethodParser parser = new IndividualMethodParser(methodsToParse, cmd.hasOption('d'));

                // Parse these methods to get the appropriate data
                List<MethodFeatures> methods = parser.parseMethods();

                // Get differences for each method and rank them by most different to least different
                List<MethodDifferences> differences = compareAndRank(methods);

                if (cmd.hasOption("i")) {
                    displayMethodDetails(differences, new Scanner(System.in));
                } else {
                    displayDifferences(differences, 10, new Scanner(System.in));
                }
            }
        } else {
            Scanner keyboard = new Scanner(System.in);

            String mode = promptUser("Would you like to load a project or a file? (p/f): ", Pattern.compile("p|f"), keyboard);

            boolean dataDump = promptUser("Would you like a full data dump? (y/n): ",
            		Pattern.compile("y|n"), keyboard).equalsIgnoreCase("y");
            String fileName = "";
            if (dataDump) fileName = promptUser("Specify file name to dump to (will overrite existing files): ", Pattern.compile("^(?=\\s*\\S).*$"), keyboard);	

            // Project mode
            if (mode.equalsIgnoreCase("p")) {
                System.out.print("Provide the path to the project to analyze: ");
                String projectPath = keyboard.nextLine();

                MethodParser parser = new AntProjectMethodParser(projectPath, cmd.hasOption('d'));

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
                    
                    // Get differences for each method and rank them by most different to least different
                    List<MethodDifferences> differences = compareAndRank(classMethods);

                    if (cmd.hasOption("i")) {
                        displayMethodDetails(differences, keyboard);
                    } else {
                        displayDifferences(differences, 10, keyboard);
                    }
                    if (dataDump) dumpData(differences, fileName);
                } else if (analysisMode.equalsIgnoreCase("p")) {
                    // Get differences for each method and rank them by most different to least different
                    List<MethodDifferences> differences = compareAndRank(methods);

                    if (cmd.hasOption("i")) {
                        displayMethodDetails(differences, keyboard);
                    } else {
                        displayDifferences(differences, 10, keyboard);
                    }
                    if (dataDump) dumpData(differences, fileName);
                }

                System.out.println();
            } else if (mode.equalsIgnoreCase("f")) {
                String filePath;

                System.out.print("Provide the path to the file to analyze: ");
                filePath = keyboard.nextLine();

                MethodParser parser = new FileMethodParser(filePath, cmd.hasOption('d'));

                List<MethodFeatures> methods = parser.parseMethods();

                // Get differences for each method and rank them by most different to least different
                List<MethodDifferences> differences = compareAndRank(methods);

                if (cmd.hasOption("i")) {
                    displayMethodDetails(differences, keyboard);
                } else {
                    displayDifferences(differences, 10, keyboard);
                }
                if (dataDump) dumpData(differences, fileName);
            }
        }
    }
    
    /**
     * Dumps all the data about the given methods into a specified file.
     * @param differences The differences in the method to write data about
     * @param filename The name of a file to create and write data to
     */
    public static void dumpData(List<MethodDifferences> differences, String filename) {
    	DecimalFormat df = new DecimalFormat("0.000");
        try {
			PrintWriter writer = new PrintWriter(filename, "UTF-8");			

			for (int i = 0; i < differences.size(); ++i) {
				MethodDifferences curMethodDiff = differences.get(i);
				writer.print(df.format(curMethodDiff.getDifferenceScore()) + ";");
				for (Difference curDiff : curMethodDiff) {
					writer.print(curDiff.dumpData());
				}
				writer.println();
			}
			writer.close();
        } catch (Exception e) {
			System.out.println("Error writing to file.");
			e.printStackTrace();
        }
    }
}
