package io.army.criteria;

import io.army.dialect.ParamWrapper;
import io.army.meta.mapping.MappingType;

import java.sql.JDBCType;
import java.util.Collection;
import java.util.List;

/**
 * created  on 2018/12/4.
 */
public interface ParamExpression<E> extends Expression<E> {

    String MSG = "operation supported by ParamExpression";

    E value();

    @Override
    void appendSQL(StringBuilder builder, List<ParamWrapper> paramWrapperList);

    @Override
    MappingType mappingType();

    @Override
    String toString();


    @Override
    default Predicate eq(Expression<E> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate eq(E constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate lt(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate lt(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate le(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate le(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate gt(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate gt(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate ge(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate ge(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate notEq(Expression<E> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate notEq(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate not() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate between(Expression<E> first, Expression<E> second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate between(E first, E second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate between(Expression<E> first, E second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate between(E first, Expression<E> second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate isNull() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate isNotNull() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate in(Collection<E> values) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate in(Expression<Collection<E>> values) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate notIn(Collection<E> values) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate notIn(Expression<Collection<E>> values) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate like(String pattern) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate notLike(String pattern) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate all(SubQuery<E> subQuery) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate any(SubQuery<E> subQuery) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Predicate some(SubQuery<E> subQuery) {
        throw new UnsupportedOperationException(MSG);
    }


}
