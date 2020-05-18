package io.army.criteria;

public interface SpecialPredicate extends IPredicate {

    void appendPredicate(SQLContext context);
}
