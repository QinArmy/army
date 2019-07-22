package io.army.criteria;

import java.util.Collection;

/**
 * created  on 2018/10/8.
 */
public interface Expression<E> extends GroupElement<E>, OrderElement<E> {


    Selection as(String alias);

    Predicate eq(Expression<E> expression);

    Predicate eq(E constant);

    Predicate lt(Expression<? extends Comparable<E>> expression);

    Predicate lt(Comparable<E> constant);

    Predicate le(Expression<? extends Comparable<E>> expression);

    Predicate le(Comparable<E> constant);

    Predicate gt(Expression<? extends Comparable<E>> expression);

    Predicate gt(Comparable<E> constant);

    Predicate ge(Expression<? extends Comparable<E>> expression);

    Predicate ge(Comparable<E> constant);

    Predicate notEq(Expression<E> expression);

    Predicate notEq(Comparable<E> constant);

    Predicate not();

    Predicate between(Expression<E> first, Expression<E> second);

    Predicate between(E first, E second);

    Predicate between(Expression<E> first, E second);

    Predicate between(E first, Expression<E> second);

    Predicate isNull();

    Predicate isNotNull();

    Predicate in(Collection<E> values);

    Predicate in(Expression<Collection<E>> values);

    Predicate in(SubQuery<E> subQuery);

    Predicate like(String pattern);

    Predicate notLike(String pattern);

    Predicate all(SubQuery<E> subQuery);

    Predicate any(SubQuery<E> subQuery);

    Predicate some(SubQuery<E> subQuery);

    @Override
    String toString();


}
