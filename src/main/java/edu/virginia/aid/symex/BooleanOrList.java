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
            IdentifierValue simplifiedTerm = booleanOrList.getTerms().get(i).simplify();
            booleanOrList.getTerms().remove(i);
            booleanOrList.getTerms().add(i, simplifiedTerm);
        }

        for (int i = 0; i < booleanOrList.getTerms().size(); i++) {
            for (int j = i + 1; j < booleanOrList.getTerms().size(); j++) {
                if (booleanOrList.getTerms().get(i).equals(booleanOrList.getTerms().get(j))) {
                    booleanOrList.getTerms().remove(j);
                    j--;
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
    public boolean isDisjointWith(IdentifierValue iv) {
        return iv instanceof BooleanValue && !((BooleanValue) iv).getValue();
    }

    @Override
    public IdentifierValue getIntersection(IdentifierValue iv) {
        return null;
    }

    @Override
    public boolean isConstantType() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BooleanOrList) {
            BooleanOrList bol = (BooleanOrList) o;
            if (bol.terms.size() != this.terms.size()) {
                return false;
            }

            for (IdentifierValue value1 : this.terms) {
                boolean foundInOtherList = false;
                for (IdentifierValue value2 : bol.terms) {
                    if (value1.equals(value2)) {
                        foundInOtherList = true;
                        break;
                    }
                }
                if (!foundInOtherList) {
                    return false;
                }
            }

            return true;
        }

        return false;
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

    @Override
    public boolean isComplete() {
    	if (terms == null) return false;
    	for (IdentifierValue iV : terms) {
    		if (iV == null || !iV.isComplete()) return false;
    	}
    	return true;
    }
}
