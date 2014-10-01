package edu.virginia.aid;

@SuppressWarnings("unused")
public class TestClass {

    public String field1;
    public boolean anotherField = false;

	/**
	 * Here's some block comments about my method with words. We use this file to test out our
	 * stuff!
	 */
	public static void main(String[] args) {
		// Here's another comment too.
		String tested = "should say test, and match comment";
		String testing = "also OK";

		String the = "this should be ignored by stoplist";
		String notInComments = "Hello world!";
		String and = "ignore";
		String has = "ignore";
		String running = "should be runn";
		System.out.println("notInComments");
		anotherMethod(notInComments);
	}

	/**
	 * hello me
	 */
	public static void anotherMethod(String hello) {
		/* Some block comment */
		String me = hello;
		System.out.println(me);
	}
}
