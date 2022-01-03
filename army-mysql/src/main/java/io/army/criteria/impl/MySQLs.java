package io.army.criteria.impl;

import io.army.criteria.mysql.MySQLUpdate;

import java.util.Objects;

public abstract class MySQLs extends SQLs {


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
     */
    public static <C> MySQLUpdate.BatchSingleUpdateSpec<C> batchSingleUpdate57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleUpdate.batch57(criteria);
    }

    public static MySQLUpdate.MultiUpdateSpec<Void> multiUpdate57() {
        return MySQLMultiUpdate.simple57(null);
    }

    public static <C> MySQLUpdate.MultiUpdateSpec<C> multiUpdate57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiUpdate.simple57(criteria);
    }

    public static MySQLUpdate.BatchMultiUpdateSpec<Void> batchMultiUpdate57() {
        return MySQLMultiUpdate.batch57(null);
    }

    public static <C> MySQLUpdate.BatchMultiUpdateSpec<C> batchMultiUpdate57(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiUpdate.batch57(criteria);
    }



}
