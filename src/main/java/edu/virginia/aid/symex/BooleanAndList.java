package edu.virginia.aid.symex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * List of multiple IdentifierValues ANDed together
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class BooleanAndList implements IdentifierValue {

    private List<IdentifierValue> terms;

    public BooleanAndList(BooleanAndList bal) {
        this.terms = new ArrayList<>();
        for (IdentifierValue term : bal.terms) {
            this.terms.add(term);
        }
    }

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

    public BooleanAndList simplifyKeepType() {
        BooleanAndList booleanAndList = new BooleanAndList(this);
        for (int i = 0; i < booleanAndList.getTerms().size(); i++) {
            IdentifierValue simplifiedTerm = booleanAndList.getTerms().get(i).simplify();
            booleanAndList.getTerms().remove(i);
            booleanAndList.getTerms().add(i, simplifiedTerm);
        }

        // Remove equivalent terms
        for (int i = 0; i < booleanAndList.getTerms().size(); i++) {
            for (int j = i + 1; j < booleanAndList.getTerms().size(); j++) {
                if (booleanAndList.getTerms().get(i).equals(booleanAndList.getTerms().get(j))) {
                    booleanAndList.getTerms().remove(j);
                    j--;
                } else {
                    IdentifierValue intersection = booleanAndList.getTerms().get(i).getIntersection(booleanAndList.getTerms().get(j));
                    if (intersection != null) {
                        booleanAndList.getTerms().remove(j);
                        booleanAndList.getTerms().remove(i);
                        booleanAndList.getTerms().add(i, intersection);
                        i--;
                        break;
                    }
                }
            }
        }

        // Remove true terms
        for (int i = 0; i < booleanAndList.getTerms().size(); i++) {
            IdentifierValue term = booleanAndList.getTerms().get(i);
            if (term instanceof BooleanValue && ((BooleanValue) term).getValue() == true) {
                booleanAndList.getTerms().remove(i--);
            }
        }

        return booleanAndList;
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

    public boolean isUnsatisfiable() {
        for (int i = 0; i < terms.size(); i++) {
            for (int j = i + 1; j < terms.size(); j++) {
                if (terms.get(i).isDisjointWith(terms.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSubsetOf(BooleanAndList bal) {
        for (IdentifierValue term : this.terms) {
            boolean inList = false;
            for (IdentifierValue term2 : bal.terms) {
                if (term.equals(term2)) {
                    inList = true;
                    break;
                }
            }
            if (!inList) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BooleanAndList) {
            BooleanAndList bal = (BooleanAndList) o;
            if (bal.terms.size() != this.terms.size()) {
                return false;
            }

            for (IdentifierValue value1 : this.terms) {
                boolean foundInOtherList = false;
                for (IdentifierValue value2 : bal.terms) {
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
                    joinedTerms += " and " + terms.get(i);
                }
            }
            return "( " + joinedTerms + " )";
        }
    }

    @Override
    public boolean isComplete() {
    	if (terms == null) return false;
    	for (IdentifierValue iV : terms) {
    		if (!iV.isComplete()) return false;
    	}
    	return true;
    }
}
