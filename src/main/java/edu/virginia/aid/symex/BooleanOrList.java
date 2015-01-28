package edu.virginia.aid.symex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * List of multiple IdentifierValues ORed together
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class BooleanOrList implements IdentifierValue {

    private List<IdentifierValue> terms;

    public BooleanOrList(IdentifierValue... terms) {
        this.terms = new ArrayList<>(Arrays.asList(terms));
    }

    public BooleanOrList(BooleanOrList booleanOrList) {
        this.terms = new ArrayList<>(booleanOrList.getTerms());
    }

    public void addTerm(IdentifierValue term) {
        terms.add(term);
    }

    public List<IdentifierValue> getTerms() {
        return terms;
    }

    @Override
    public IdentifierValue negate() {
        BooleanAndList negatedValue = new BooleanAndList();
        for (IdentifierValue term : terms) {
            negatedValue.addTerm(term.negate());
        }

        return negatedValue;
    }

    public BooleanOrList simplifyKeepType() {
        BooleanOrList booleanOrList = new BooleanOrList(this);
        for (int i = 0; i < booleanOrList.getTerms().size(); i++) {
            booleanOrList.getTerms().add(i, booleanOrList.getTerms().get(i).simplify());
        }

        for (int i = 0; i < booleanOrList.getTerms().size(); i++) {
            for (int j = i + 1; j < booleanOrList.getTerms().size(); j++) {
                if (booleanOrList.getTerms().get(i).subsumes(booleanOrList.getTerms().get(j))) {
                    booleanOrList.getTerms().remove(j);
                    j--;
                } else if (booleanOrList.getTerms().get(j).subsumes(booleanOrList.getTerms().get(i))) {
                    booleanOrList.getTerms().remove(i);
                    i--;
                    break;
                }
            }
        }

        return booleanOrList;
    }

    @Override
    public IdentifierValue simplify() {
        return simplifyKeepType();
    }

    @Override
    public boolean subsumes(IdentifierValue identifierValue) {
        return false; // TODO: implement
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
                    joinedTerms += " or " + terms.get(i);
                }
            }
            return "( " + joinedTerms + " )";
        }
    }
}
