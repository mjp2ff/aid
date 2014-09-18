package edu.virginia.aid;

public class TestClass {

	/**
	 * Here's some block comments about my method with words. We use this file to test out our
	 * stuff!
	 */
	public static void main(String[] args) {
		// Here's another comment too.
		System.out.println("Hello world!");
		anotherMethod("Hello World");
	}

	/**
	 * Another method
	 */
	public static void anotherMethod(String hello) {
		/* Some block comment */
		String me = hello;
		System.out.println(me);
	}
}
