package io.army.criteria.impl;

import io.army.criteria.ScalarExpression;
import io.army.criteria.Select;
import io.army.criteria.SubQuery;
import io.army.criteria.mysql.MySQL80Query;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLUpdate;

import java.util.Objects;

public abstract class MySQLs80 extends MySQLSyntax {

    /**
     * protected constructor, application developer can extend this util class.
     */
    protected MySQLs80() {
    }

    public static MySQL80Query._WithSpec<Void, Select> query() {
        return MySQL80SimpleQuery.simpleSelect(null);
    }

    public static <C> MySQL80Query._WithSpec<C, Select> query(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.simpleSelect(criteria);
    }

    public static MySQL80Query._WithSpec<Void, SubQuery> subQuery() {
        return MySQL80SimpleQuery.subQuery(false, null);
    }

    public static MySQL80Query._WithSpec<Void, SubQuery> lateralSubQuery() {
        return MySQL80SimpleQuery.subQuery(true, null);
    }

    public static <C> MySQL80Query._WithSpec<C, SubQuery> subQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.subQuery(false, criteria);
    }

    public static <C> MySQL80Query._WithSpec<C, SubQuery> lateralSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.subQuery(true, criteria);
    }


    public static MySQL80Query._WithSpec<Void, ScalarExpression> scalarSubQuery() {
        return MySQL80SimpleQuery.scalarSubQuery(false, null);
    }

    public static MySQL80Query._WithSpec<Void, ScalarExpression> lateralScalarSubQuery() {
        return MySQL80SimpleQuery.scalarSubQuery(true, null);
    }


    public static <C> MySQL80Query._WithSpec<C, ScalarExpression> scalarSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.scalarSubQuery(false, criteria);
    }

    public static <C> MySQL80Query._WithSpec<C, ScalarExpression> lateralScalarSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.scalarSubQuery(true, criteria);
    }


    public static MySQLUpdate._SingleWithAndUpdateSpec<Void> singleUpdate() {
        return MySQLSingleUpdate.simple80(null);
    }

    public static <C> MySQLUpdate._SingleWithAndUpdateSpec<C> singleUpdate(C criteria) {
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




}
