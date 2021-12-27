package io.army.criteria.impl;

import io.army.criteria.EmptyObject;
import io.army.criteria.mysql.*;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;

import java.util.Objects;

public abstract class MySQLs extends SQLs {

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

    /**
     * <p>
     * MySQL 5.7 single-table update api,this api can only update below fields:
     *     <ul>
     *         <li>The fields of {@link io.army.meta.SingleTableMeta}</li>
     *         <li>The fields of the parent of {@link io.army.meta.ChildTableMeta}</li>
     *     </ul>
     * </p>
     *
     * @return MySQL 5.7 single-table update api
     */
    public static MySQLUpdate.SingleUpdateSpec<Void> singleUpdate57() {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * MySQL 5.7 single-table update api,this api can only update below fields:
     *     <ul>
     *         <li>The fields of {@link io.army.meta.SingleTableMeta}</li>
     *         <li>The fields of the parent of {@link io.army.meta.ChildTableMeta}</li>
     *     </ul>
     * </p>
     *
     * @param criteria criteria instance(map or bean) used to create dynamic update statement
     * @return MySQL 5.7 single-table update api
     */
    public static <C> MySQLUpdate.SingleUpdateSpec<C> singleUpdate57(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * MySQL 5.7 batch single-table update api,this api can only update below fields:
     *     <ul>
     *         <li>The fields of {@link io.army.meta.SingleTableMeta}</li>
     *         <li>The fields of the parent of {@link io.army.meta.ChildTableMeta}</li>
     *     </ul>
     * </p>
     *
     * @return MySQL 5.7 batch single-table update api instance
     */
    public static MySQLUpdate.BatchSingleUpdateSpec<Void> batchSingleUpdate57() {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * MySQL 5.7 batch single-table update api,this api can only update below fields:
     *     <ul>
     *         <li>The fields of {@link io.army.meta.SingleTableMeta}</li>
     *         <li>The fields of the parent of {@link io.army.meta.ChildTableMeta}</li>
     *     </ul>
     * </p>
     *
     * @param criteria criteria instance(map or bean) used to create dynamic update statement
     * @return MySQL 5.7 batch single-table update api instance
     */
    public static <C> MySQLUpdate.BatchSingleUpdateSpec<C> batchSingleUpdate57(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }


    public static <C> MySQL57Query.SelectPart57Spec<MySQL57Select, C> mySQL57Select(C criteria) {
        return MySQL57ContextualSelect.build(criteria);
    }

    public static MySQL57Query.SelectPart57Spec<MySQL57Select, EmptyObject> mySQL57Select() {
        return MySQL57ContextualSelect.build(EmptyObject.getInstance());
    }

    public static <C> MySQL57Query.SelectPart57Spec<MySQL57SubQuery, C> mySQL57SubQuery(C criteria) {
        return MySQL57SubQueries.build(criteria);
    }

    public static MySQL57Query.SelectPart57Spec<MySQL57SubQuery, EmptyObject> mySQL57SubQuery() {
        return MySQL57SubQueries.build(EmptyObject.getInstance());
    }

    public static <C> MySQL57Query.SelectPart57Spec<MySQL57RowSubQuery, C> mySQL57RowSubQuery(C criteria) {
        return MySQL57SubQueries.buildRowSubQuery(criteria);
    }

    public static MySQL57Query.SelectPart57Spec<MySQL57RowSubQuery, EmptyObject> mySQL57RowSubQuery() {
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
        return MySQL57SubQueries.buildScalarSubQuery(javaType, _MappingFactory.getMapping(javaType), criteria);
    }

    public static <E> MySQL57ScalarSubQuery.MySQLScalarSelectionSpec<E, EmptyObject> mySQL57ScalarSubQuery(
            Class<E> javaType) {
        return MySQL57SubQueries.buildScalarSubQuery(javaType, _MappingFactory.getMapping(javaType)
                , EmptyObject.getInstance());
    }

}
