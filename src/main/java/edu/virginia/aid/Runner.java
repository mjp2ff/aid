package edu.virginia.aid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class Runner {

	public static void main(String[] args) {
		System.out.print("Input full path of file to read: ");
		Scanner s = new Scanner(System.in);

		// Read in file
		final String filePath = s.nextLine();
		final String fileData = readFile(filePath);

		// Initialize driver.
		Driver driver = new Driver(fileData);

		// Parse this file to get the appropriate data.
		List<MethodDeclaration> methods = driver.getMethodsFromFile();

		// Handle the methods appropriately.
		driver.handleMethods(methods);

		// Close the scanner.
		s.close();
	}

	/**
	 * @param filePath
	 *            The full path to the file to be read.
	 * @return The text of the specified file.
	 */
	public static String readFile(String filePath) {
		// Read all data from specified file.
		String fileData = "";
		try {
			fileData = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			System.out.println("Error reading file from path " + filePath);
			e.printStackTrace();
		}
		return fileData;
	}

	// Matt: test on D:\Documents\aid\src\main\java\edu\virginia\aid\TestClass.java
}
