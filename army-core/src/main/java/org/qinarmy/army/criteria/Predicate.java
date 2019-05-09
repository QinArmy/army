package org.qinarmy.army.criteria;

/**
 * created  on 2018/10/8.
 */
public interface Predicate extends Expression<Boolean> {


    Predicate and(Predicate predicate);

    Predicate or(Predicate... predicates);


}
