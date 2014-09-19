package edu.virginia.aid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import edu.virginia.aid.comparison.Difference;
import edu.virginia.aid.comparison.MethodDifferences;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class Runner {

	public static void main(String[] args) {
		final String fileData = readFile();

		// Initialize driver.
		Driver driver = new Driver(fileData);

		// Parse this file to get the appropriate data.
		List<MethodDeclaration> methods = driver.getMethodsFromFile();

		// Handle the methods appropriately.
		List<MethodFeatures> methodFeaturesList = driver.handleMethods(methods);

        // Get differences for each method and rank them by most different to least different
        List<MethodDifferences> differences = driver.compareAndRank(methodFeaturesList);

        System.out.println("Printing out method differences");
        System.out.println("=============================");
        for (MethodDifferences methodDifferences : differences) {
            System.out.println("Total difference score for " + methodDifferences.getMethodName() + ": " + methodDifferences.getDifferenceScore());
            for (Difference difference : methodDifferences) {
                System.out.println("\tExpected '" + difference.getMethodContent() + "' in comment but got '" + difference.getCommentContent() + "' instead");
            }
        }
	}

	/**
	 * @return The text of the specified file.
	 */
	public static String readFile() {
		Scanner s = new Scanner(System.in);
		String fileData = "";

		// Read all data from specified file.
		while (fileData.isEmpty()) {
			System.out.print("Input full path of file to read: ");
			String filePath = s.nextLine();
			try {
				fileData = new String(Files.readAllBytes(Paths.get(filePath)));
			} catch (IOException e) {
				System.out.println("Error reading file from path " + filePath);
			}
		}

		s.close();
		return fileData;
	}
}
