package io.army.criteria.impl;

import io.army.criteria.ScalarExpression;
import io.army.criteria.Select;
import io.army.criteria.SubQuery;
import io.army.criteria.mysql.MySQL57Query;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLUpdate;

import java.util.Objects;

@Deprecated
public abstract class MySQLs_ extends MySQLSyntax {

    protected MySQLs_() {

    }

    public static MySQL57Query._Select57Clause<Void, Select> query() {
        throw new UnsupportedOperationException();
    }

    public static <C> MySQL57Query._Select57Clause<C, Select> query(C criteria) {
        throw new UnsupportedOperationException();
    }

    public static MySQL57Query._Select57Clause<Void, SubQuery> subQuery() {
        throw new UnsupportedOperationException();
    }

    public static <C> MySQL57Query._Select57Clause<C, SubQuery> subQuery(C criteria) {
        throw new UnsupportedOperationException();
    }


    public static MySQL57Query._Select57Clause<Void, ScalarExpression> scalarSubQuery() {
        throw new UnsupportedOperationException();
    }

    public static <C, E> MySQL57Query._Select57Clause<C, ScalarExpression> scalarSubQuery(C criteria) {
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
     * @return MySQL 5.7 single-table update api
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static MySQLUpdate._SingleUpdate57Clause<Void> singleUpdate() {
        return MySQLSingleUpdate.simple(null);
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
    public static <C> MySQLUpdate._SingleUpdate57Clause<C> singleUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleUpdate.simple(criteria);
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
    public static MySQLUpdate._BatchSingleUpdateClause<Void> batchSingleUpdate() {
        return MySQLSingleUpdate.batch(null);
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
    public static <C> MySQLUpdate._BatchSingleUpdateClause<C> batchSingleUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleUpdate.batch(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static MySQLUpdate._MultiUpdate57Clause<Void> multiUpdate() {
        return MySQLMultiUpdate.simple(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static <C> MySQLUpdate._MultiUpdate57Clause<C> multiUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiUpdate.simple(criteria);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static MySQLUpdate._BatchMultiUpdateClause<Void> batchMultiUpdate() {
        return MySQLMultiUpdate.batch(null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/update.html">UPDATE Statement</a>
     */
    public static <C> MySQLUpdate._BatchMultiUpdateClause<C> batchMultiUpdate(C criteria) {
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
