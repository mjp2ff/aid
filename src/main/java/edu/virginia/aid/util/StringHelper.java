package edu.virginia.aid.util;

public class StringHelper {
	
    /**
     * Helper method to replace camel casing with spaces between words. Regex found from NPE on StackOverflow.
     * 
     * @param s String in camelCase form
     * @return String with spaces instead of camelCase
     */
    public static String splitCamelCase(String s) {
    	String[] wordSplit = s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
    	String newWord = "";
    	for (String w : wordSplit) {
    		newWord += w + " ";
        }
    	return newWord.trim();
    }
}
