package io.army.criteria;

import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.mapping.MappingType;

import java.math.BigInteger;
import java.sql.JDBCType;
import java.util.Collection;
import java.util.List;

/**
 * extends {@link ParamWrapper} to avoid new instance of {@link ParamWrapper}
 * created  on 2018/12/4.
 */
public interface ParamExpression<E> extends Expression<E>, ParamWrapper {

    String MSG = "operation isn'table supported by ParamExpression";

    E value();

    /**
     *
     * @param sql
     * @param builder
     * @param paramWrapperList
     */
    @Override
    void appendSQL(SQL sql, StringBuilder builder, List<ParamWrapper> paramWrapperList);

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
    default <N extends Number> Expression<N> mod(Expression<N> operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<N> mod(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<N> multiply(Expression<N> multiplicand) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<N> multiply(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<N> add(Expression<N> augend) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<N> add(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<N> subtract(Expression<N> subtrahend) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<N> subtract(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<N> divide(Expression<N> divisor) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<N> divide(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<N> negate() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<BigInteger> and(Expression<O> operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Expression<BigInteger> and(Long operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<BigInteger> or(Expression<O> operator) {
        return null;
    }

    @Override
    default Expression<BigInteger> or(Long operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<BigInteger> xor(Expression<O> operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Expression<BigInteger> xor(Long operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<BigInteger> inversion(Expression<O> operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Expression<BigInteger> inversion(Long operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Expression<BigInteger> rightShift(Integer bitNumber) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<BigInteger> rightShift(Expression<O> bitNumber) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Expression<BigInteger> leftShift(Integer bitNumber) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<BigInteger> leftShift(Expression<O> bitNumber) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<E> plusOther(Expression<O> other) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<E> minusOther(Expression<O> other) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<O> asType(Class<O> convertType) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<O> asType(Class<O> convertType, MappingType longMapping) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Expression<E> brackets() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Selection as(String alias) {
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
