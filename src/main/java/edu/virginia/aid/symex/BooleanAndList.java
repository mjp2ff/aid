package edu.virginia.aid.symex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * List of multiple IdentifierValues ANDed together
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class BooleanAndList implements IdentifierValue {

    private List<IdentifierValue> terms;

    public BooleanAndList(IdentifierValue... terms) {
        this.terms = new ArrayList<>(Arrays.asList(terms));
    }

    public void addTerm(IdentifierValue term) {
        terms.add(term);
    }

    public List<IdentifierValue> getTerms() {
        return terms;
    }

    @Override
    public IdentifierValue negate() {
        BooleanOrList negatedValue = new BooleanOrList();
        for (IdentifierValue term : terms) {
            negatedValue.addTerm(term.negate());
        }

        return negatedValue;
    }

    @Override
    public String toString() {
        if (terms.size() == 0) {
            return "";
        } else {
            String joinedTerms = "";
            for (int i = 0; i < terms.size(); i++) {
                if (i == 0) {
                    joinedTerms += terms.get(i);
                } else {
                    joinedTerms += " and " + terms.get(i);
                }
            }
            return "( " + joinedTerms + " )";
        }
    }
}
