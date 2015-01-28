package edu.virginia.aid.symex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Product of sums form of IdentifierValue
 *
 * @author Matt Pearson-Beck & Jeff Principe
 */
public class ProductOfSums implements IdentifierValue {

    private List<BooleanOrList> sums;

    public ProductOfSums(BooleanOrList... sums) {
        this.sums = new ArrayList<>(Arrays.asList(sums));
    }

    public void addSum(BooleanOrList sum) {
        sums.add(sum);
    }

    public List<BooleanOrList> getSums() {
        return this.sums;
    }

    @Override
    public IdentifierValue negate() {
        BooleanAndList booleanAndList = new BooleanAndList();
        for (IdentifierValue value : sums) {
            booleanAndList.addTerm(value);
        }
        return booleanAndList.negate();
    }

    @Override
    public IdentifierValue simplify() {
        for (int i = 0; i < sums.size(); i++) {
            sums.add(i, sums.get(i).simplifyKeepType());
        }
    }

    public SumOfProducts convertToSumOfProducts() {
        SumOfProducts sumOfProducts = new SumOfProducts();
        int solutions = sums.stream().mapToInt(s -> s.getTerms().size()).sum();
        for (int i = 0; i < solutions; i++) {
            BooleanAndList product = new BooleanAndList();
            int j = 1;
            for(BooleanOrList sum : sums) {
                product.addTerm(sum.getTerms().get((i/j)%sum.getTerms().size()));
                j *= sum.getTerms().size();
            }
            sumOfProducts.addProduct(product);
        }
        return sumOfProducts;
    }

    @Override
    public String toString() {
        BooleanAndList booleanAndList = new BooleanAndList();
        for (IdentifierValue value : sums) {
            booleanAndList.addTerm(value);
        }
        return booleanAndList.toString();
    }


}
