package edu.virginia.aid.data;

import java.util.List;

/**
 * Interface for classes that can output their data as a list of strings.
 * 
 * @author Matt Pearson-Beck, Jeff Principe
 *
 */
public interface StringListable {

	/**
	 * Gets the data of the current object in string list form.
	 * 
	 * @return A list of strings representing the data of the object.
	 */
	public List<String> getData();
}
