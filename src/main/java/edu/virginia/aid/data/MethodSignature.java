package edu.virginia.aid.data;

import org.apache.commons.lang3.StringUtils;

/**
 * Packages information gleaned from the signature of a method, including name and params.
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class MethodSignature {

    /**
     * The name of the method
     */
    private String name;

    /**
     * Array of parameters provided for the method
     */
    private String[] params;

    /**
     * Creates a new MethodSignature with the given name and parameters
     *
     * @param name The name of the method
     * @param params The parameters to the method
     */
    public MethodSignature(String name, String[] params) {
        this.name = name;
        this.params = params;
    }

    /**
     * Creates a new MethodSignature with the given name and number of parameters
     *
     * @param name The name of the method
     * @param length The number of parameters to the method
     */
    public MethodSignature(String name, int length) {
        this.name = name;
        this.params = new String[length];
    }

    /**
     * Gets the name of the method
     *
     * @return The name of the method
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the name of the method
     *
     * @param name The new name of the method
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets an array of the parameters to the method
     *
     * @return An array of names of parameters to the method
     */
    public String[] getParams() {
        return params;
    }

    /**
     * Sets the parameters to the method
     *
     * @param params The parameters to the method
     */
    public void setParams(String[] params) {
        this.params = params;
    }

    /**
     * Tests whether two signatures have the same method name and parameter names/ordering
     *
     * @param o The MethodSignature to compare against
     * @return Whether the two MethodSignatures are equivalent
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof MethodSignature) {
            MethodSignature other = (MethodSignature) o;
            if (name.equals(other.getName()) && params.length == other.getParams().length) {
                boolean paramsEqual = true;
                for (int i = 0; i < params.length; i++) {
                    if (!params[i].equals(other.getParams()[i])) {
                        paramsEqual = false;
                    }
                }

                if (paramsEqual) return true;
            }
        }

        return false;
    }

    /**
     * Generates a string representation of the method and parameter list similar to that seen in the signature
     * of a method (without types)
     *
     * @return String representation of the MethodSignature
     */
    public String toString() {
        return name + "(" + StringUtils.join(params, ", ") + ")";
    }
}
