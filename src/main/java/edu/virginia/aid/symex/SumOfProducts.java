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

    public String toString() {
        BooleanOrList booleanOrList = new BooleanOrList();
        for (IdentifierValue value : products) {
            booleanOrList.addTerm(value);
        }
        return booleanOrList.toString();
    }
}
