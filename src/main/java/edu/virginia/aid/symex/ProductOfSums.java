package edu.virginia.aid.symex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public ProductOfSums simplifyKeepType() {
        ProductOfSums simplified = new ProductOfSums();

        // Simplify each term
        for (int i = 0; i < sums.size(); i++) {
            simplified.sums.add(i, sums.get(i).simplifyKeepType());
        }

        ProductOfSums simplified2 = new ProductOfSums();

        // Reduce the number of terms
        for (int i = 0; i < simplified.sums.size(); i++) {
            boolean found = false;
            for (int j = i + 1; j < simplified.sums.size(); j++) {
                if (simplified.sums.get(i).equals(simplified.sums.get(j))) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                simplified2.sums.add(simplified.sums.get(i));
            }
        }
        return simplified2;
    }

    @Override
    public IdentifierValue simplify() {
        return simplifyKeepType();
    }

    public SumOfProducts convertToSumOfProducts() {
        SumOfProducts sumOfProducts = new SumOfProducts();
        long solutions = sums.stream().mapToLong(s -> s.getTerms().size()).reduce(1l, (len, acc) -> acc * len);
        for (int i = 0; i < solutions; i++) {
            BooleanAndList product = new BooleanAndList();
            long j = 1;
            for(BooleanOrList sum : sums) {
                product.addTerm(sum.getTerms().get((int) (i/j)%sum.getTerms().size()));
                j *= sum.getTerms().size();
            }
            sumOfProducts.addProduct(product);
        }
        return sumOfProducts;
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
    public String toString() {
        BooleanAndList booleanAndList = new BooleanAndList();
        for (IdentifierValue value : sums) {
            booleanAndList.addTerm(value);
        }
        return booleanAndList.toString();
    }


}
