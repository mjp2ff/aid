package edu.virginia.aid.symex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Sum of products form for IdentifierValues
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class SumOfProducts implements IdentifierValue {

    private List<BooleanAndList> products;

    public SumOfProducts(BooleanAndList... products) {
        this.products = new ArrayList<>(Arrays.asList(products));
    }

    public void addProduct(BooleanAndList product) {
        products.add(product);
    }

    public List<BooleanAndList> getProducts() {
        return products;
    }

    @Override
    public IdentifierValue negate() {
        BooleanOrList booleanOrList = new BooleanOrList();
        for (IdentifierValue value : products) {
            booleanOrList.addTerm(value);
        }
        return booleanOrList.negate();
    }

    public SumOfProducts simplifyKeepType() {
        SumOfProducts simplified = new SumOfProducts();

        // Simplify each term
        for (int i = 0; i < products.size(); i++) {
            BooleanAndList simplifiedProduct = products.get(i).simplifyKeepType();
            if (!simplifiedProduct.isUnsatisfiable()) {
                simplified.products.add(simplifiedProduct);
            }
        }

        SumOfProducts simplified2 = new SumOfProducts();

        // Reduce the number of terms
        for (int i = 0; i < simplified.products.size(); i++) {
            boolean found = false;
            for (int j = i + 1; j < simplified.products.size(); j++) {
                if (simplified.products.get(i).isSubsetOf(simplified.products.get(j))) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                for (BooleanAndList product : simplified2.products) {
                    if (simplified.products.get(i).isSubsetOf(product)) {
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                simplified2.products.add(simplified.products.get(i));
            }
        }
        return simplified2;
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

    public String toString() {
        BooleanOrList booleanOrList = new BooleanOrList();
        for (IdentifierValue value : products) {
            booleanOrList.addTerm(value);
        }
        return booleanOrList.toString();
    }
}
