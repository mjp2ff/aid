package edu.virginia.aid.data;

/**
 * Enumerated type indicating the (closest nested) scope that the identifier must be within
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public enum IdentifierScope {
    LOCAL,      // local (closest) scope
    CLASS       // class (field) scope
}
