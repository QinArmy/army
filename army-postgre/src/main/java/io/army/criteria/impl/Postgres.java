package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.postgre.PostgreSelect;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingType;
import io.army.sqltype.PostgreSQLType;

import java.util.Collections;
import java.util.List;

public abstract class Postgres extends SQLS {

    Postgres() {
    }


    public static PostgreSelect.PostgreWithAble<EmptyObject> multiSelect() {
        return null;
    }

    public static <C> PostgreSelect.PostgreSelectPartAble<C> multiSelect(C criteria) {
        return null;
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


    public static PostgreSelect.PostgreWindowNameAble<EmptyObject> window() {
        return new PostgreWindowImpl<>(EmptyObject.getInstance());
    }

    public static <C> PostgreSelect.PostgreWindowNameAble<C> window(C criteria) {
        return new PostgreWindowImpl<>(criteria);
    }

}
