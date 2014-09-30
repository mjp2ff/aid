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
		String notInComments = "Hello world!";
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
