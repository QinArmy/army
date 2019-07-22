package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.Predicate;
import io.army.criteria.Selection;
import io.army.criteria.SubQuery;

import java.util.Collection;

/**
 * created  on 2018/11/24.
 */
public abstract class AbstractExpression<E> implements Expression<E>, Selection {



    protected String alias;


    @Override
    public Selection as(String alias) {
        this.alias = alias;
        return this;
    }


    @Override
    public Predicate eq(Expression<E> expression) {
        return new DualPredicate(this, DualOperator.EQ, expression);
    }

    @Override
    public Predicate eq(E constant) {
        return new DualConstantPredicate(this, DualOperator.EQ, constant);
    }

    @Override
    public Predicate lt(Expression<? extends Comparable<E>> expression) {
        return new DualPredicate(this, DualOperator.LT, expression);
    }

    @Override
    public Predicate lt(Comparable<E> constant) {
        return new DualConstantPredicate(this, DualOperator.LT, constant);
    }

    @Override
    public Predicate le(Expression<? extends Comparable<E>> expression) {
        return new DualPredicate(this, DualOperator.LE, expression);
    }

    @Override
    public Predicate le(Comparable<E> constant) {
        return new DualConstantPredicate(this, DualOperator.LE, constant);
    }

    @Override
    public Predicate gt(Expression<? extends Comparable<E>> expression) {
        return new DualPredicate(this, DualOperator.GT, expression);
    }

    @Override
    public Predicate gt(Comparable<E> constant) {
        return new DualConstantPredicate(this, DualOperator.GT, constant);
    }

    @Override
    public Predicate ge(Expression<? extends Comparable<E>> expression) {
        return new DualPredicate(this, DualOperator.GE, expression);
    }

    @Override
    public Predicate ge(Comparable<E> constant) {
        return new DualConstantPredicate(this, DualOperator.GE, constant);
    }

    @Override
    public Predicate notEq(Expression<E> expression) {
        return new DualPredicate(this, DualOperator.NOT_EQ, expression);
    }

    @Override
    public Predicate notEq(Comparable<E> constant) {
        return new DualConstantPredicate(this, DualOperator.NOT_EQ, constant);
    }

    @Override
    public Predicate not() {
        return NotPredicate.getNotPredicate(this);
    }

    @Override
    public Predicate between(Expression<E> first, Expression<E> second) {
        return null;
    }

    @Override
    public Predicate between(E first, E second) {
        return null;
    }

    @Override
    public Predicate between(Expression<E> first, E second) {
        return null;
    }

    @Override
    public Predicate between(E first, Expression<E> second) {
        return null;
    }

    @Override
    public Predicate isNull() {
        return null;
    }

    @Override
    public Predicate isNotNull() {
        return null;
    }


    @Override
    public Predicate in(Collection<E> values) {
        return null;
    }

    @Override
    public Predicate in(Expression<Collection<E>> values) {
        return null;
    }

    @Override
    public Predicate in(SubQuery<E> subQuery) {
        return null;
    }

    @Override
    public Predicate like(String pattern) {
        return null;
    }

    @Override
    public Predicate notLike(String pattern) {
        return null;
    }

    @Override
    public Expression<E> asc() {
        return null;
    }

    @Override
    public Expression<E> desc() {
        return null;
    }

    @Override
    public Predicate all(SubQuery<E> subQuery) {
        return null;
    }

    @Override
    public Predicate any(SubQuery<E> subQuery) {
        return null;
    }

    @Override
    public Predicate some(SubQuery<E> subQuery) {
        return null;
    }
}
