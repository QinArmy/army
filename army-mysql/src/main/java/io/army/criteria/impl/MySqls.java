package io.army.criteria.impl;

import io.army.criteria.EmptyObject;
import io.army.criteria.mysql.*;
import io.army.mapping.MappingFactory;
import io.army.mapping.MappingType;

public abstract class MySqls extends Sqls {

    /*################################## blow update  method ##################################*/
/*
    public static MySQLUpdate.MySQLSingleUpdateAble<EmptyObject> singleUpdate() {
        return null;
    }

    public static <C> MySQLUpdate.MySQLSingleUpdateAble<C> singleUpdate(C criteria) {
        return null;
    }

    public static MySQLUpdate.MySQLMultiUpdateAble<EmptyObject> multiUpdate() {
        return null;
    }

    public static <C> MySQLUpdate.MySQLMultiUpdateAble<C> multiUpdate(C criteria) {
        return null;
    }*/

    /*################################## blow delete method ##################################*/

  /*  public static MySQLDelete.MySQLSingleDeleteAble<EmptyObject> singleDelete() {
        return null;
    }

    public static <C> MySQLDelete.MySQLSingleDeleteAble<C> singleDelete(C criteria) {
        return null;
    }

    public static MySQLDelete.MySQLMultiDeleteAble<EmptyObject> multiDelete() {
        return null;
    }

    public static <C> MySQLDelete.MySQLMultiDeleteAble<C> multiDelete(C criteria) {
        return null;
    }*/

    /*################################## blow select method ##################################*/


    public static <C> MySQL57Query.MySQLSelectPartSpec<MySQL57Select, C> mySQL57Select(C criteria) {
        return MySQL57ContextualSelect.build(criteria);
    }

    public static MySQL57Query.MySQLSelectPartSpec<MySQL57Select, EmptyObject> mySQL57Select() {
        return MySQL57ContextualSelect.build(EmptyObject.getInstance());
    }

    public static <C> MySQL57Query.MySQLSelectPartSpec<MySQL57SubQuery, C> mySQL57SubQuery(C criteria) {
        return MySQL57SubQueries.build(criteria);
    }

    public static MySQL57Query.MySQLSelectPartSpec<MySQL57SubQuery, EmptyObject> mySQL57SubQuery() {
        return MySQL57SubQueries.build(EmptyObject.getInstance());
    }

    public static <C> MySQL57Query.MySQLSelectPartSpec<MySQL57RowSubQuery, C> mySQL57RowSubQuery(C criteria) {
        return MySQL57SubQueries.buildRowSubQuery(criteria);
    }

    public static MySQL57Query.MySQLSelectPartSpec<MySQL57RowSubQuery, EmptyObject> mySQL57RowSubQuery() {
        return MySQL57SubQueries.buildRowSubQuery(EmptyObject.getInstance());
    }

    public static <E, C> MySQL57ColumnSubQuery.MySQLColumnSelectionSpec<E, C> mySQL57ColumnSubQuery(
            Class<E> columnType, C criteria) {
        return MySQL57SubQueries.buildColumnSubQuery(columnType, criteria);
    }

    public static <E> MySQL57ColumnSubQuery.MySQLColumnSelectionSpec<E, EmptyObject> mySQL57ColumnSubQuery(
            Class<E> columnType) {
        return MySQL57SubQueries.buildColumnSubQuery(columnType, EmptyObject.getInstance());
    }

    public static <E, C> MySQL57ScalarSubQuery.MySQLScalarSelectionSpec<E, C> mySQL57ScalarSubQuery(
            Class<E> javaType, MappingType mappingType, C criteria) {
        return MySQL57SubQueries.buildScalarSubQuery(javaType, mappingType, criteria);
    }

    public static <E> MySQL57ScalarSubQuery.MySQLScalarSelectionSpec<E, EmptyObject> mySQL57ScalarSubQuery(
            Class<E> javaType, MappingType mappingType) {
        return MySQL57SubQueries.buildScalarSubQuery(javaType, mappingType, EmptyObject.getInstance());
    }

    public static <E, C> MySQL57ScalarSubQuery.MySQLScalarSelectionSpec<E, C> mySQL57ScalarSubQuery(
            Class<E> javaType, C criteria) {
        return MySQL57SubQueries.buildScalarSubQuery(javaType, MappingFactory.getDefaultMapping(javaType), criteria);
    }

    public static <E> MySQL57ScalarSubQuery.MySQLScalarSelectionSpec<E, EmptyObject> mySQL57ScalarSubQuery(
            Class<E> javaType) {
        return MySQL57SubQueries.buildScalarSubQuery(javaType, MappingFactory.getDefaultMapping(javaType)
                , EmptyObject.getInstance());
    }

}
