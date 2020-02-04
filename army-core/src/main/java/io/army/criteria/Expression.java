package io.army.criteria;

import io.army.dialect.ParamWrapper;
import io.army.meta.mapping.MappingType;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * created  on 2018/10/8.
 */
public interface Expression<E> {

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

    Predicate notIn(Collection<E> values);

    Predicate notIn(Expression<Collection<E>> values);

    Expression<E> mod(Expression<E> divisor);

    Expression<E> mod(E divisor);

    Expression<E> multiply(Expression<E> multiplicand);

    Expression<E> multiply(E multiplicand);

    Expression<E> add(Expression<E> augend);

    Expression<E> add(E augend);

    Expression<E> subtract(Expression<E> subtrahend);

    Expression<E> subtract(E e);

    Expression<E> divide(Expression<E> divisor);

    Expression<E> divide(E divisor);

    Expression<E> negate();

    <O> Expression<E> plusOther(Expression<O> other);

    <O> Expression<E> minusOther(Expression<O> other);

    <O>  Expression<O> as(Class<O> convertType);

    <O>  Expression<O> as(Class<O> convertType,MappingType longMapping);

    Predicate like(String pattern);

    Predicate notLike(String pattern);

    Predicate all(SubQuery<E> subQuery);

    Predicate any(SubQuery<E> subQuery);

    Predicate some(SubQuery<E> subQuery);

    default void appendSQL(StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        throw new UnsupportedOperationException();
    }

    default Class<E> javaType() {
        throw new UnsupportedOperationException();
    }

    default MappingType mappingType() {
        throw new UnsupportedOperationException();
    }

    @Override
    String toString();


}
