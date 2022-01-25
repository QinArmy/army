package io.army.criteria.impl;

import io.army.criteria.ScalarQueryExpression;
import io.army.criteria.Select;
import io.army.criteria.SubQuery;
import io.army.criteria.mysql.MySQL57Query;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLUpdate;

import java.util.Objects;

public abstract class MySQLs extends MySQLSyntax {

    protected MySQLs() {

    }

    public static MySQL57Query.Select57Spec<Void, Select> query() {
        return MySQL57SimpleQuery.simpleSelect(null);
    }

    public static <C> MySQL57Query.Select57Spec<C, Select> query(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL57SimpleQuery.simpleSelect(criteria);
    }

    public static MySQL57Query.Select57Spec<Void, SubQuery> subQuery() {
        return MySQL57SimpleQuery.subQuery(null);
    }

    public static <C> MySQL57Query.Select57Spec<C, SubQuery> subQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL57SimpleQuery.subQuery(criteria);
    }


    public static MySQL57Query.Select57Spec<Void, ScalarQueryExpression> scalarSubQuery() {
        return MySQL57SimpleQuery.scalarSubQuery(null);
    }

    public static <C, E> MySQL57Query.Select57Spec<C, ScalarQueryExpression> scalarSubQuery(C criteria) {
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
    public static MySQLUpdate.SingleUpdateSpec<Void> singleUpdate() {
        return MySQLSingleUpdate.simple57(null);
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
    public static <C> MySQLUpdate.SingleUpdateSpec<C> singleUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleUpdate.simple57(criteria);
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
    public static MySQLUpdate.BatchSingleUpdateSpec<Void> batchSingleUpdate() {
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
    public static <C> MySQLUpdate.BatchSingleUpdateSpec<C> batchSingleUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleUpdate.batch57(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static MySQLUpdate.MultiUpdateSpec<Void> multiUpdate() {
        return MySQLMultiUpdate.simple(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static <C> MySQLUpdate.MultiUpdateSpec<C> multiUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiUpdate.simple(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static MySQLUpdate.BatchMultiUpdateSpec<Void> batchMultiUpdate() {
        return MySQLMultiUpdate.batch(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static <C> MySQLUpdate.BatchMultiUpdateSpec<C> batchMultiUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiUpdate.batch(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Single-Table Syntax</a>
     */
    public static MySQLDelete.SingleDeleteSpec<Void> singleDelete() {
        return MySQLSingleDelete.simple57(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Single-Table Syntax</a>
     */
    public static <C> MySQLDelete.SingleDeleteSpec<C> singleDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleDelete.simple57(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Single-Table Syntax</a>
     */
    public static MySQLDelete.BatchSingleDeleteSpec<Void> batchSingleDelete() {
        return MySQLSingleDelete.batch57(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Single-Table Syntax</a>
     */
    public static <C> MySQLDelete.BatchSingleDeleteSpec<C> batchSingleDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleDelete.batch57(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Multiple-Table Syntax</a>
     */
    public static MySQLDelete.MultiDeleteSpec<Void> multiDelete() {
        return MySQLMultiDelete.simple(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Multiple-Table Syntax</a>
     */
    public static <C> MySQLDelete.MultiDeleteSpec<C> multiDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiDelete.simple(criteria);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Multiple-Table Syntax</a>
     */
    public static MySQLDelete.BatchMultiDeleteSpec<Void> batchMultiDelete() {
        return MySQLMultiDelete.batch(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/delete.html">DELETE Statement Multiple-Table Syntax</a>
     */
    public static <C> MySQLDelete.BatchMultiDeleteSpec<C> batchMultiDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiDelete.batch(criteria);
    }


}
