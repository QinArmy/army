package io.army.criteria.impl;

import io.army.criteria.EmptyObject;
import io.army.criteria.Expression;
import io.army.criteria.SQLOperator;
import io.army.criteria.postgre.*;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingType;
import io.army.sqltype.PostgreSQLType;
import io.army.util.Assert;

import java.util.Collections;
import java.util.List;

public abstract class Postgres extends SQLS {

    Postgres() {
    }


    public static PostgreSelect.PostgreWithAble<EmptyObject> specialSelect() {
        return new PostgreContextualMultiSelect<>(EmptyObject.getInstance());
    }

    public static <C> PostgreSelect.PostgreWithAble<C> specialSelect(C criteria) {
        return new PostgreContextualMultiSelect<>(criteria);
    }


    public static PostgreFuncTable funcTable(Expression<?> funcExp, List<PostgreFuncColExp<?>> colDefExpList) {
        return PostgreFuncTableImpl.build(funcExp, colDefExpList);
    }

    public static PostgreAliasFuncTable funcTable(Expression<?> funcExp, List<PostgreFuncColExp<?>> colDefExpList
            , String tableAlias) {
        return PostgreFuncTableImpl.build(funcExp, colDefExpList, tableAlias);
    }

    public static PostgreRowsFromTable rowsFromTable(List<PostgreFuncTable> tableList
            , @Nullable MappingType withOrdinalityType
            , String tableAlias
            , List<String> aliasList) {
        return PostgreRowsFromTableImpl.build(tableList, withOrdinalityType, tableAlias, aliasList);
    }

    public static PostgreRowsFromTable rowsFromTable(List<PostgreFuncTable> tableList
            , @Nullable MappingType withOrdinalityType
            , String tableAlias) {
        return PostgreRowsFromTableImpl.build(tableList, withOrdinalityType, tableAlias, Collections.emptyList());
    }

    public static <E> PostgreFuncColExp<E> funColumnExp(String columnName, MappingType mappingType, PostgreSQLType sqlType) {
        return PostgreFunColDefExpImpl.build(columnName, mappingType, sqlType);
    }

    public static <E> PostgreFuncColExp<E> funColumnExp(String columnName, MappingType mappingType) {
        return PostgreFunColDefExpImpl.build(columnName, mappingType, null);
    }


    /**
     * create window clause builder
     */
    public static PostgreSelect.PostgreWindowNameAble<EmptyObject> window() {
        return new PostgreWindowImpl<>(EmptyObject.getInstance());
    }

    /**
     * create window clause builder
     */
    public static <C> PostgreSelect.PostgreWindowNameAble<C> window(C criteria) {
        return new PostgreWindowImpl<>(criteria);
    }

    /**
     * append {@code NULLS FIRST} after {@code expression}
     */
    public static <E> Expression<E> nullsFirst(Expression<E> expression) {
        Assert.isTrue(!(expression instanceof NullsOrderExpression), "expression has NULLS { FIRST | LAST }");
        return new NullsOrderExpressionImpl<>(expression, NullsOrderExpression.Nulls.FIRST);
    }

    /**
     * append {@code NULLS LAST} after {@code expression}
     */
    public static <E> Expression<E> nullsLast(Expression<E> expression) {
        Assert.isTrue(!(expression instanceof NullsOrderExpression), "expression has NULLS { FIRST | LAST }");
        return new NullsOrderExpressionImpl<>(expression, NullsOrderExpression.Nulls.LAST);
    }

    /**
     * append {@code USING operator} after {@code expression}
     */
    public static <E> Expression<E> sortUsing(Expression<E> expression, SQLOperator operator) {
        Assert.isTrue(!(expression instanceof SortUsingOperatorExpression), "expression has USING operator clause.");
        Assert.isTrue(!(expression instanceof SortExpression), "expression has ASC/DESC clause.");
        return new SortUsingOperatorExpressionImpl<>(expression, operator);
    }

}
