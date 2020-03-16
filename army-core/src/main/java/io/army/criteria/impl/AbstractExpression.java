package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;
import io.army.util.StringUtils;

import java.math.BigInteger;
import java.util.Collection;

/**
 * created  on 2018/11/24.
 */
abstract class AbstractExpression<E> implements Expression<E>, Selection {

    protected String alias;

    AbstractExpression() {
    }


    @Override
    public Selection as(String alias) {
        Assert.isTrue(StringUtils.hasText(alias), "alias required");
        this.alias = alias;
        return this;
    }

    @Override
    public String alias() {
        if (this.alias == null) {
            throw new IllegalStateException("alias is null,expression state error.");
        }
        return alias;
    }

    @Override
    public final IPredicate eq(Expression<E> expression) {
        return new DualPredicateImpl(this, DualOperator.EQ, expression);
    }

    @Override
    public final IPredicate eq(E constant) {
        return new DualPredicateImpl(this, DualOperator.EQ, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final IPredicate eq(String subQueryAlias, String fieldAlias) {
        return new DualPredicateImpl(this, DualOperator.EQ, SQLS.ref(subQueryAlias,fieldAlias));
    }

    @Override
    public final IPredicate eq(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualPredicateImpl(this, DualOperator.EQ, SQLS.field(tableAlias,fieldMeta));
    }

    @Override
    public final IPredicate eq(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
        return new ColumnSubQueryPredicate(this,DualOperator.EQ,keyOperator,subQuery);
    }

    @Override
    public final IPredicate lt(Expression<? extends Comparable<E>> expression) {
        return new DualPredicateImpl(this, DualOperator.LT, expression);
    }

    @Override
    public final IPredicate lt(Comparable<E> constant) {
        return new DualPredicateImpl(this, DualOperator.LT, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final IPredicate lt(String subQueryAlias, String fieldAlias) {
        return new DualPredicateImpl(this, DualOperator.LT, SQLS.ref(subQueryAlias,fieldAlias));
    }

    @Override
    public final IPredicate lt(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualPredicateImpl(this, DualOperator.LT, SQLS.field(tableAlias,fieldMeta));
    }

    @Override
    public final IPredicate lt(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
        return new ColumnSubQueryPredicate(this,DualOperator.LT,keyOperator,subQuery);
    }

    @Override
    public final IPredicate le(Expression<? extends Comparable<E>> expression) {
        return new DualPredicateImpl(this, DualOperator.LE, expression);
    }

    @Override
    public final IPredicate le(Comparable<E> constant) {
        return new DualPredicateImpl(this, DualOperator.LE, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final IPredicate le(String subQueryAlias, String fieldAlias) {
        return new DualPredicateImpl(this, DualOperator.LE, SQLS.ref(subQueryAlias,fieldAlias));
    }

    @Override
    public final IPredicate le(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualPredicateImpl(this, DualOperator.LE, SQLS.field(tableAlias,fieldMeta));
    }

    @Override
    public final IPredicate le(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
        return new ColumnSubQueryPredicate(this,DualOperator.LE,keyOperator,subQuery);
    }

    @Override
    public final IPredicate gt(Expression<? extends Comparable<E>> expression) {
        return new DualPredicateImpl(this, DualOperator.GT, expression);
    }

    @Override
    public final IPredicate gt(Comparable<E> constant) {
        return new DualPredicateImpl(this, DualOperator.GT, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final IPredicate gt(String subQueryAlias, String fieldAlias) {
        return new DualPredicateImpl(this, DualOperator.GT, SQLS.ref(subQueryAlias,fieldAlias));
    }

    @Override
    public final IPredicate gt(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualPredicateImpl(this, DualOperator.GT, SQLS.field(tableAlias,fieldMeta));
    }

    @Override
    public final IPredicate gt(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
        return new ColumnSubQueryPredicate(this,DualOperator.GT,keyOperator,subQuery);
    }

    @Override
    public final IPredicate ge(Expression<? extends Comparable<E>> expression) {
        return new DualPredicateImpl(this, DualOperator.GE, expression);
    }

    @Override
    public final IPredicate ge(Comparable<E> constant) {
        return new DualPredicateImpl(this, DualOperator.GE, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final IPredicate ge(String subQueryAlias, String fieldAlias) {
        return new DualPredicateImpl(this, DualOperator.GE, SQLS.ref(subQueryAlias,fieldAlias));
    }

    @Override
    public final IPredicate ge(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualPredicateImpl(this, DualOperator.GE, SQLS.field(tableAlias,fieldMeta));
    }

    @Override
    public final IPredicate ge(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
        return new ColumnSubQueryPredicate(this,DualOperator.GE,keyOperator,subQuery);
    }

    @Override
    public final IPredicate notEq(Expression<E> expression) {
        return new DualPredicateImpl(this, DualOperator.NOT_EQ, expression);
    }

    @Override
    public final IPredicate notEq(Comparable<E> constant) {
        return new DualPredicateImpl(this, DualOperator.NOT_EQ, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final IPredicate notEq(String subQueryAlias, String fieldAlias) {
        return new DualPredicateImpl(this, DualOperator.NOT_EQ, SQLS.ref(subQueryAlias,fieldAlias));
    }

    @Override
    public final IPredicate notEq(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualPredicateImpl(this, DualOperator.NOT_EQ, SQLS.field(tableAlias,fieldMeta));
    }

    @Override
    public final IPredicate notEq(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
        return new ColumnSubQueryPredicate(this,DualOperator.NOT_EQ,keyOperator,subQuery);
    }

    @Override
    public final IPredicate not() {
        return NotPredicate.build(this);
    }

    @Override
    public final IPredicate between(Expression<E> first, Expression<E> second) {
        return new BetweenPredicate(this, first, second);
    }

    @Override
    public final IPredicate between(E first, E second) {
        return new BetweenPredicate(this, SQLS.param(first, this), SQLS.param(second, this));
    }

    @Override
    public final IPredicate between(Expression<E> first, E second) {
        return new BetweenPredicate(this, first, SQLS.param(second, this.mappingType()));
    }

    @Override
    public final IPredicate between(E first, Expression<E> second) {
        return new BetweenPredicate(this, SQLS.param(first, this), second);
    }

    @Override
    public final IPredicate between(String subQueryAlias, String derivedFieldName, Expression<E> second) {
        return new BetweenPredicate(this, SQLS.ref(subQueryAlias,derivedFieldName), second);
    }

    @Override
    public final IPredicate between(String subQueryAlias, String derivedFieldName, E second) {
        return new BetweenPredicate(this, SQLS.ref(subQueryAlias,derivedFieldName), SQLS.param(second, this));
    }

    @Override
    public final IPredicate between(String subQueryAlias1, String derivedFieldName1
            , String subQueryAlias2, String derivedFieldName2) {
        return new BetweenPredicate(this, SQLS.ref(subQueryAlias1,derivedFieldName1)
                , SQLS.ref(subQueryAlias2, derivedFieldName2));
    }

    @Override
    public final IPredicate between(Expression<E> first, String subQueryAlias, String derivedFieldName) {
        return new BetweenPredicate(this,first,SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final IPredicate between(E first, String subQueryAlias, String derivedFieldName) {
        return new BetweenPredicate(this,SQLS.param(first, this),SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final IPredicate isNull() {
        return new UnaryPredicate(UnaryOperator.IS_NULL, this);
    }

    @Override
    public final IPredicate isNotNull() {
        return new UnaryPredicate(UnaryOperator.IS_NOT_NULL, this);
    }

    @Override
    public final IPredicate in(Collection<E> values) {
        return new InPredicate(true, this, values);
    }

    @Override
    public final IPredicate in(Expression<Collection<E>> values) {
        return new InPredicate(true, this, values);
    }

    @Override
    public final IPredicate in(ColumnSubQuery<E> subQuery) {
        return new InPredicate(true, this, subQuery);
    }

    @Override
    public final IPredicate notIn(Collection<E> values) {
        return new InPredicate(false, this, values);
    }

    @Override
    public final IPredicate notIn(Expression<Collection<E>> values) {
        return new InPredicate(false, this, values);
    }

    @Override
    public final IPredicate notIn(ColumnSubQuery<E> subQuery) {
        return new InPredicate(false, this, subQuery);
    }

    @Override
    public final IPredicate like(String pattern) {
        return new DualPredicateImpl(this, DualOperator.LIKE, SQLS.constant(pattern, this.mappingType()));
    }

    @Override
    public final IPredicate like(Expression<String> pattern) {
        return new DualPredicateImpl(this, DualOperator.LIKE, pattern);
    }

    @Override
    public final IPredicate notLike(String pattern) {
        return new DualPredicateImpl(this, DualOperator.NOT_LIKE, SQLS.param(pattern, this.mappingType()));
    }

    @Override
    public final IPredicate notLike(Expression<String> pattern) {
        return new DualPredicateImpl(this, DualOperator.NOT_LIKE, pattern);
    }

    @Override
    public final <N extends Number> Expression<E> mod(Expression<N> operator) {
        return new DualExpresion<>(this, DualOperator.MOD, operator);
    }

    @Override
    public final <N extends Number> Expression<E> mod(N operator) {
        return new DualExpresion<>(this, DualOperator.MOD, SQLS.param(operator));
    }

    @Override
    public final Expression<E> mod(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.MOD, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> mod(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.MOD, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <N extends Number> Expression<E> multiply(Expression<N> multiplicand) {
        return new DualExpresion<>(this, DualOperator.MULTIPLY, multiplicand);
    }

    @Override
    public final <N extends Number> Expression<E> multiply(N multiplicand) {
        return new DualExpresion<>(this, DualOperator.MULTIPLY, SQLS.param(multiplicand));
    }

    @Override
    public final Expression<E> multiply(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.MULTIPLY, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> multiply(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.MULTIPLY, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <N extends Number> Expression<E> add(Expression<N> augend) {
        return new DualExpresion<>(this, DualOperator.ADD, augend);
    }

    @Override
    public final <N extends Number> Expression<E> add(N augend) {
        return new DualExpresion<>(this, DualOperator.ADD, SQLS.param(augend));
    }

    @Override
    public final Expression<E> add(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.ADD, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> add(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.ADD, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <N extends Number> Expression<E> subtract(Expression<N> subtrahend) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, subtrahend);
    }

    @Override
    public final <N extends Number> Expression<E> subtract(N subtrahend) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, SQLS.param(subtrahend));
    }

    @Override
    public final Expression<E> subtract(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> subtract(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <N extends Number> Expression<E> divide(Expression<N> divisor) {
        return new DualExpresion<>(this, DualOperator.DIVIDE, divisor);
    }

    @Override
    public final <N extends Number> Expression<E> divide(N divisor) {
        return new DualExpresion<>(this, DualOperator.DIVIDE, SQLS.param(divisor));
    }

    @Override
    public final Expression<E> divide(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.DIVIDE, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> divide(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.DIVIDE, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final Expression<E> negate() {
        return new UnaryExpression<>(this, UnaryOperator.NEGATED);
    }

    @Override
    public final <O> Expression<BigInteger> and(Expression<O> operator) {
        return new DualExpresion<>(this, DualOperator.AND, operator);
    }

    @Override
    public final Expression<BigInteger> and(Long operator) {
        return new DualExpresion<>(this, DualOperator.AND, SQLS.param(operator));
    }

    @Override
    public final Expression<BigInteger> and(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.AND, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> and(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.AND, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <O> Expression<BigInteger> or(Expression<O> operator) {
        return new DualExpresion<>(this, DualOperator.OR, operator);
    }

    @Override
    public final Expression<BigInteger> or(Long operator) {
        return new DualExpresion<>(this, DualOperator.OR, SQLS.param(operator));
    }

    @Override
    public final Expression<BigInteger> or(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.OR, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> or(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.OR, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <O> Expression<BigInteger> xor(Expression<O> operator) {
        return new DualExpresion<>(this, DualOperator.XOR, operator);
    }

    @Override
    public final Expression<BigInteger> xor(Long operator) {
        return new DualExpresion<>(this, DualOperator.XOR, SQLS.param(operator));
    }

    @Override
    public final Expression<BigInteger> xor(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.XOR, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> xor(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.XOR, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <O> Expression<BigInteger> inversion(Expression<O> operator) {
        return new DualExpresion<>(this, DualOperator.INVERT, operator);
    }

    @Override
    public final Expression<BigInteger> inversion(Long operator) {
        return new DualExpresion<>(this, DualOperator.INVERT, SQLS.param(operator));
    }

    @Override
    public final Expression<BigInteger> inversion(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.INVERT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> inversion(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.INVERT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final Expression<BigInteger> rightShift(Integer bitNumber) {
        return new DualExpresion<>(this, DualOperator.RIGHT_SHIFT, SQLS.param(bitNumber));
    }

    @Override
    public final <O> Expression<BigInteger> rightShift(Expression<O> bitNumber) {
        return new DualExpresion<>(this, DualOperator.RIGHT_SHIFT, bitNumber);
    }

    @Override
    public final Expression<BigInteger> rightShift(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.RIGHT_SHIFT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> rightShift(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.RIGHT_SHIFT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final Expression<BigInteger> leftShift(Integer bitNumber) {
        return new DualExpresion<>(this, DualOperator.LEFT_SHIFT, SQLS.param(bitNumber));
    }

    @Override
    public final <O> Expression<BigInteger> leftShift(Expression<O> bitNumber) {
        return new DualExpresion<>(this, DualOperator.LEFT_SHIFT, bitNumber);
    }

    @Override
    public final Expression<BigInteger> leftShift(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.LEFT_SHIFT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> leftShift(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.LEFT_SHIFT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <O> Expression<E> plusOther(Expression<O> other) {
        return new DualExpresion<>(this, DualOperator.ADD, other);
    }

    @Override
    public final Expression<E> plusOther(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.ADD, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<E> plusOther(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.ADD, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <O> Expression<E> minusOther(Expression<O> other) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, other);
    }

    @Override
    public final Expression<E> minusOther(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<E> minusOther(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType) {
        return new ConvertExpressionImpl<>(this, MappingFactory.getDefaultMapping(convertType));
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, MappingType longMapping) {
        return new ConvertExpressionImpl<>(this, longMapping);
    }

    @Override
    public final Expression<E> brackets() {
        return new BracketsExpression<>(this);
    }

    @Override
    public final Expression<E> sort(@Nullable Boolean asc) {
        if (asc == null) {
            return this;
        } else {
            return new SortExpressionImpl<>(this, asc);
        }
    }


    @Override
    public final void appendSQL(SQLContext context) {
        context.stringBuilder().append(" ");
        afterSpace(context);
    }

    @Override
    public final String toString() {
        String text = beforeAs();
        if (StringUtils.hasText(this.alias)) {
            text = text + " AS " + this.alias;
        }
        return text;
    }

    /*################################## blow protected template method ##################################*/

    protected abstract void afterSpace(SQLContext context);

    protected abstract String beforeAs();

}
