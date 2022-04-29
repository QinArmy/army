package io.army.criteria.impl;

import io.army.criteria.ScalarExpression;
import io.army.criteria.Select;
import io.army.criteria.Statement;
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

    public static MySQL80Query.With80Spec<Void, Select> query() {
        return MySQL80SimpleQuery.simpleSelect(null);
    }

    public static <C> MySQL80Query.With80Spec<C, Select> query(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.simpleSelect(criteria);
    }

    public static MySQL80Query.With80Spec<Void, SubQuery> subQuery() {
        return MySQL80SimpleQuery.subQuery(false, null);
    }

    public static MySQL80Query.With80Spec<Void, SubQuery> lateralSubQuery() {
        return MySQL80SimpleQuery.subQuery(true, null);
    }

    public static <C> MySQL80Query.With80Spec<C, SubQuery> subQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.subQuery(false, criteria);
    }

    public static <C> MySQL80Query.With80Spec<C, SubQuery> lateralSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.subQuery(true, criteria);
    }


    public static MySQL80Query.With80Spec<Void, ScalarExpression> scalarSubQuery() {
        return MySQL80SimpleQuery.scalarSubQuery(false, null);
    }

    public static MySQL80Query.With80Spec<Void, ScalarExpression> lateralScalarSubQuery() {
        return MySQL80SimpleQuery.scalarSubQuery(true, null);
    }


    public static <C> MySQL80Query.With80Spec<C, ScalarExpression> scalarSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.scalarSubQuery(false, criteria);
    }

    public static <C> MySQL80Query.With80Spec<C, ScalarExpression> lateralScalarSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQL80SimpleQuery.scalarSubQuery(true, criteria);
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


    static final class MySQLWindow<C, R> extends SimpleWindow<
            C,
            MySQL80Query.WindowLeftBracketClause<C, R>,      //AR
            MySQL80Query.WindowPartitionBySpec<C, R>,        //LR
            MySQL80Query.WindowOrderBySpec<C, R>,            //PR,
            MySQL80Query.WindowFrameUnitsSpec<C, R>,         //OR
            MySQL80Query.WindowFrameBetweenClause<C, R>,     //FR
            MySQL80Query.WindowFrameEndNonExpBoundClause<R>, //FC
            MySQL80Query.WindowFrameNonExpBoundClause<C, R>, //BR
            MySQL80Query.WindowFrameExpBoundClause<C, R>,    //BC
            MySQL80Query.WindowFrameEndExpBoundClause<R>,    //NC
            Statement.Clause,                                //MA
            Statement.Clause,                                //MB
            R>                                               //R
            implements MySQL80Query.WindowAsClause<C, R>, MySQL80Query.WindowLeftBracketClause<C, R>
            , MySQL80Query.WindowPartitionBySpec<C, R>, MySQL80Query.WindowFrameBetweenClause<C, R>,
            MySQL80Query.WindowFrameEndNonExpBoundClause<R>, MySQL80Query.WindowFrameNonExpBoundClause<C, R>
            , MySQL80Query.WindowFrameExpBoundClause<C, R>, MySQL80Query.WindowFrameEndExpBoundClause<R>
            , MySQL80Query.WindowFrameBetweenAndClause<C, R> {

        MySQLWindow(String windowName, CriteriaContext criteriaContext) {
            super(windowName, criteriaContext);
        }

        MySQLWindow(String windowName, R stmt) {
            super(windowName, stmt);
        }

        @Override
        public MySQLWindow<C, R> currentRow() {
            this.bound(FrameBound.CURRENT_ROW);
            return this;
        }

        @Override
        public MySQLWindow<C, R> unboundedPreceding() {
            this.bound(FrameBound.UNBOUNDED_PRECEDING);
            return this;
        }

        @Override
        public MySQLWindow<C, R> unboundedFollowing() {
            this.bound(FrameBound.UNBOUNDED_FOLLOWING);
            return this;
        }

        @Override
        public MySQLWindow<C, R> preceding() {
            this.bound(FrameBound.PRECEDING);
            return this;
        }

        @Override
        public MySQLWindow<C, R> following() {
            this.bound(FrameBound.FOLLOWING);
            return this;
        }


    }//MySQLWindow


}
