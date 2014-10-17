package edu.virginia.aid.data;

/**
 * Enumerated type indicating how an identifier is used
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public enum IdentifierUse {
    DECLARATION,    // Identifier is declared
    READ,           // Value of the identifier is read/invoked
    WRITE,          // Value of the identifier is written
}
