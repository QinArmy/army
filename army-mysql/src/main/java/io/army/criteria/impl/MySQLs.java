package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.MySQL57Query;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLUpdate;

import java.util.Objects;

public abstract class MySQLs extends SQLs {

    public static MySQL57Query.Select57Spec<Void, Select> select57() {
        return MySQL57SimpleQuery.simpleSelect(null);
    }

    public static <C> MySQL57Query.Select57Spec<C, Select> select57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL57SimpleQuery.simpleSelect(criteria);
    }

    public static MySQL57Query.Select57Spec<Void, SubQuery> subQuery57() {
        return MySQL57SimpleQuery.subQuery(null);
    }

    public static <C> MySQL57Query.Select57Spec<C, SubQuery> subQuery57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL57SimpleQuery.subQuery(criteria);
    }

    public static MySQL57Query.Select57Spec<Void, RowSubQuery> rowSubQuery57() {
        return MySQL57SimpleQuery.rowSubQuery(null);
    }

    public static <C> MySQL57Query.Select57Spec<C, RowSubQuery> rowSubQuery57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL57SimpleQuery.rowSubQuery(criteria);
    }

    public static MySQL57Query.Select57Spec<Void, ColumnSubQuery> columnSubQuery57() {
        return MySQL57SimpleQuery.columnSubQuery(null);
    }

    public static <C> MySQL57Query.Select57Spec<C, ColumnSubQuery> columnSubQuery57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL57SimpleQuery.columnSubQuery(criteria);
    }

    public static <E> MySQL57Query.Select57Spec<Void, ScalarQueryExpression<E>> scalarSubQuery57() {
        return MySQL57SimpleQuery.scalarSubQuery(null);
    }

    public static <E> MySQL57Query.Select57Spec<Void, ScalarQueryExpression<E>> scalarSubQuery57(Class<E> type) {
        return MySQL57SimpleQuery.scalarSubQuery(null);
    }

    public static <C, E> MySQL57Query.Select57Spec<C, ScalarQueryExpression<E>> scalarSubQuery57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL57SimpleQuery.scalarSubQuery(criteria);
    }

    public static <C, E> MySQL57Query.Select57Spec<C, ScalarQueryExpression<E>> scalarSubQuery57(Class<E> type, C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL57SimpleQuery.scalarSubQuery(criteria);
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
     * @return MySQL 5.7 single-table update api
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static MySQLUpdate.SingleUpdateSpec<Void> singleUpdate57() {
        return MySQLSingleUpdate.single57(null);
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
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static <C> MySQLUpdate.SingleUpdateSpec<C> singleUpdate57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleUpdate.single57(criteria);
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
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static MySQLUpdate.BatchSingleUpdateSpec<Void> batchSingleUpdate57() {
        return MySQLSingleUpdate.batch57(null);
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
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static <C> MySQLUpdate.BatchSingleUpdateSpec<C> batchSingleUpdate57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleUpdate.batch57(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static MySQLUpdate.MultiUpdateSpec<Void> multiUpdate57() {
        return MySQLMultiUpdate.simple57(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static <C> MySQLUpdate.MultiUpdateSpec<C> multiUpdate57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiUpdate.simple57(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static MySQLUpdate.BatchMultiUpdateSpec<Void> batchMultiUpdate57() {
        return MySQLMultiUpdate.batch57(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static <C> MySQLUpdate.BatchMultiUpdateSpec<C> batchMultiUpdate57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiUpdate.batch57(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Single-Table Syntax</a>
     */
    public static MySQLDelete.SingleDeleteSpec<Void> singleDelete57() {
        return MySQLSingleDelete.simple57(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Single-Table Syntax</a>
     */
    public static <C> MySQLDelete.SingleDeleteSpec<C> singleDelete57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleDelete.simple57(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Single-Table Syntax</a>
     */
    public static MySQLDelete.BatchSingleDeleteSpec<Void> batchSingleDelete57() {
        return MySQLSingleDelete.batch57(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Single-Table Syntax</a>
     */
    public static <C> MySQLDelete.BatchSingleDeleteSpec<C> batchSingleDelete57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleDelete.batch57(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Multiple-Table Syntax</a>
     */
    public static MySQLDelete.MultiDeleteSpec<Void> multiDelete57() {
        return MySQLMultiDelete.simple57(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Multiple-Table Syntax</a>
     */
    public static <C> MySQLDelete.MultiDeleteSpec<C> multiDelete57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiDelete.simple57(criteria);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Multiple-Table Syntax</a>
     */
    public static MySQLDelete.BatchMultiDeleteSpec<Void> batchMultiDelete57() {
        return MySQLMultiDelete.batch57(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Multiple-Table Syntax</a>
     */
    public static <C> MySQLDelete.BatchMultiDeleteSpec<C> batchMultiDelete57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiDelete.batch57(criteria);
    }


}
