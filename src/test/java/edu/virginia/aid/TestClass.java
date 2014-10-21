package edu.virginia.aid;

public class TestClass {

	public String field1;
	public boolean anotherField = false;

	/**
	 * Here's some block comments about my method with words. We use this file to test out our
	 * stuff! 
	 */
	public static void main(String[] args) {
		// Here's another comment too.
		anotherMethod("bye", "hi");
	}

	/**
	 * Here's a block comment
	 */
	public static void aMethod(String tested, String testing, String nest, String nested,
			String wordWordWord, String and, String has, String running) {
		// None of these variables
		/* are mentioned in any comments. */
		System.out.println(tested);			// Should be stemmed.
		System.out.println(testing);		// Should be stemmed.
		System.out.println(nest);			// OK.
		System.out.println(nested);			// Should be stemmed.
		System.out.println(wordWordWord);	// Should be split.
		System.out.println(and);			// Should be stop listed.
		System.out.println(has);			// Should be stop listed.
		System.out.println(running);		// Should be stemmed.
	}

	/**
	 * hello
	 */
	public static void anotherMethod(String hello, String other) {
		/* other stuff */
		// me
		String me = hello;
		String stuff = "hi!";
		System.out.println(me + other + stuff);
	}
}
