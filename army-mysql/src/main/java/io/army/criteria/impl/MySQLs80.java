package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.MySQL80Query;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLUpdate;

import java.util.Objects;

public abstract class MySQLs80 extends MySQLs {

    protected MySQLs80() {
    }

    public static MySQL80Query.With80Spec<Void, Select> tableSelect() {
        return MySQL80SimpleQuery.simpleSelect(null);
    }

    public static <C> MySQL80Query.With80Spec<C, Select> tableSelect(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.simpleSelect(criteria);
    }

    public static MySQL80Query.With80Spec<Void, SubQuery> subQuery() {
        return MySQL80SimpleQuery.subQuery(null);
    }

    public static <C> MySQL80Query.With80Spec<C, SubQuery> subQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.subQuery(criteria);
    }

    public static MySQL80Query.With80Spec<Void, RowSubQuery> rowSubQuery() {
        return MySQL80SimpleQuery.rowSubQuery(null);
    }

    public static <C> MySQL80Query.With80Spec<C, RowSubQuery> rowSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.rowSubQuery(criteria);
    }

    public static MySQL80Query.With80Spec<Void, ColumnSubQuery> columnSubQuery() {
        return MySQL80SimpleQuery.columnSubQuery(null);
    }

    public static <C> MySQL80Query.With80Spec<C, ColumnSubQuery> columnSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.columnSubQuery(criteria);
    }

    public static <E> MySQL80Query.With80Spec<Void, ScalarQueryExpression<E>> scalarSubQuery() {
        return MySQL80SimpleQuery.scalarSubQuery(null);
    }

    public static <E> MySQL80Query.With80Spec<Void, ScalarQueryExpression<E>> scalarSubQuery(Class<E> type) {
        return MySQL80SimpleQuery.scalarSubQuery(null);
    }

    public static <C, E> MySQL80Query.With80Spec<C, ScalarQueryExpression<E>> scalarSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.scalarSubQuery(criteria);
    }

    public static <C, E> MySQL80Query.With80Spec<C, ScalarQueryExpression<E>> scalarSubQuery(Class<E> type, C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.scalarSubQuery(criteria);
    }

    public static MySQLUpdate.SingleWithAndUpdateSpec<Void> singleUpdate() {
        return MySQLSingleUpdate.simple80(null);
    }

    public static <C> MySQLUpdate.SingleWithAndUpdateSpec<C> singleUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleUpdate.simple80(criteria);
    }

    public static MySQLUpdate.BatchSingleWithAndUpdateSpec<Void> batchSingleUpdate() {
        return MySQLSingleUpdate.batch80(null);
    }

    public static <C> MySQLUpdate.BatchSingleWithAndUpdateSpec<C> batchSingleUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleUpdate.batch80(criteria);
    }


    public static MySQLDelete.SingleDelete80Spec<Void> singleDelete() {
        return MySQLSingleDelete.simple80(null);
    }

    public static <C> MySQLDelete.SingleDelete80Spec<C> singleDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleDelete.simple80(criteria);
    }

    public static MySQLDelete.BatchSingleDelete80Spec<Void> batchSingleDelete() {
        return MySQLSingleDelete.batch80(null);
    }

    public static <C> MySQLDelete.BatchSingleDelete80Spec<C> batchSingleDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSingleDelete.batch80(criteria);
    }

    public static MySQLDelete.WithMultiDeleteSpec<Void> multiDelete() {
        return MySQLMultiDelete.simple80(null);
    }

    public static <C> MySQLDelete.WithMultiDeleteSpec<C> multiDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiDelete.simple80(criteria);
    }


    public static MySQLDelete.BatchWithMultiDeleteSpec<Void> batchMultiDelete() {
        return MySQLMultiDelete.batch80(null);
    }

    public static <C> MySQLDelete.BatchWithMultiDeleteSpec<C> batchMultiDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiDelete.batch80(criteria);
    }

    static MySQLUpdate.WithAndMultiUpdateSpec<Void> multiUpdate() {
        return MySQLMultiUpdate.simple80(null);
    }

    static <C> MySQLUpdate.WithAndMultiUpdateSpec<C> multiUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiUpdate.simple80(criteria);
    }

    static MySQLUpdate.BatchWithAndMultiUpdateSpec<Void> batchMultiUpdate() {
        return MySQLMultiUpdate.batch80(null);
    }

    static <C> MySQLUpdate.BatchWithAndMultiUpdateSpec<C> batchMultiUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLMultiUpdate.batch80(criteria);
    }


}
