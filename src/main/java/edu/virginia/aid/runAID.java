package edu.virginia.aid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.MethodDeclaration;


public class runAID {

	public static void main(String[] args) {
		System.out.print("Input full path of file to read: ");
		Scanner s = new Scanner(System.in);
		
		// Read in file path.
		final String filePath = s.nextLine();

		// Read all data from specified file.
		String fileData = "";
		try {
			fileData = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			System.out.println("Error reading file from path " + filePath);
			e.printStackTrace();
		}

		// Initialize driver to this file.
		Driver driver = new Driver(fileData);

		// Parse this file to get the appropriate data.
		List<MethodDeclaration> methods = driver.parseFile();

        for (MethodDeclaration method : methods) {
            System.out.println(method);

            // Read each comment data.
            Comment c = method.getJavadoc();
            int start = c.getStartPosition();
            int length = c.getLength();
            String cString = fileData.substring(start, start + length);
            System.out.println(cString);
        }
	}
	// Matt: Test on D:\Documents\aid\src\main\java\edu\virginia\aid\TestClass.java
}
