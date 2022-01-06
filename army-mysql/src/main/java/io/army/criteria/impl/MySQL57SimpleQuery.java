package io.army.criteria.impl;

import io.army.criteria.Query;
import io.army.criteria.SQLModifier;
import io.army.criteria.ScalarSubQuery;
import io.army.criteria.impl.inner.mysql._MySQL57Query;
import io.army.criteria.mysql.MySQL57Query;
import io.army.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

abstract class MySQL57SimpleQuery<C, Q extends Query> extends MySQLSimpleQuery<
        C,
        Q,
        MySQL57Query.From57Spec<C, Q>, //SR
        MySQL57Query.IndexHintJoin57Spec<C, Q>, //FT
        MySQL57Query.Join57Spec<C, Q>,          //FS
        MySQL57Query.PartitionJoin57Spec<C, Q>, //FP
        MySQL57Query.IndexHintOn57Spec<C, Q>,   //JT
        MySQL57Query.On57Spec<C, Q>,            //JS
        MySQL57Query.PartitionOn57Spec<C, Q>,   //IT
        MySQL57Query.GroupBy57Spec<C, Q>,       //WR
        MySQL57Query.WhereAnd57Spec<C, Q>,     //AR
        MySQL57Query.WithRollup57Spec<C, Q>,  //GR
        MySQL57Query.OrderBy57Spec<C, Q>,    //HR
        MySQL57Query.Limit57Spec<C, Q>,     //OR
        MySQL57Query.Lock57Spec<C, Q>,    //LR
        MySQL57Query.UnionOrderBy57Spec<C, Q>,   //UR
        MySQL57Query.Select57Spec<C, Q>>   //SP
        implements MySQL57Query, MySQL57Query.Where57Spec<C, Q>
        , MySQL57Query.WhereAnd57Spec<C, Q>, MySQL57Query.WithRollup57Spec<C, Q>, MySQL57Query.Having57Spec<C, Q>
        , _MySQL57Query {


    static <C, Q extends Query> MySQL57Query.Select57Spec<C, Q> unionAndSelect(Q left, UnionType unionType) {
        return null;
    }


    private boolean withRollup;

    private SQLModifier lockModifier;

    private MySQL57SimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }


    @Override
    public final Having57Spec<C, Q> withRollup() {
        if (hasGroupBy()) {
            this.withRollup = true;
        }
        return this;
    }

    @Override
    public final Having57Spec<C, Q> withRollup(Predicate<C> predicate) {
        if (hasGroupBy() && predicate.test(this.criteria)) {
            this.withRollup = true;
        }
        return this;
    }

    @Override
    public final Union57Spec<C, Q> forUpdate() {
        this.lockModifier = MySQLLock.FOR_UPDATE;
        return this;
    }

    @Override
    public final Union57Spec<C, Q> lockInShareMode() {
        this.lockModifier = MySQLLock.LOCK_IN_SHARE_MODE;
        return this;
    }

    @Override
    public final Union57Spec<C, Q> ifForUpdate(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lockModifier = MySQLLock.FOR_UPDATE;
        }
        return this;
    }

    @Override
    public final Union57Spec<C, Q> ifLockInShareMode(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lockModifier = MySQLLock.LOCK_IN_SHARE_MODE;
        }
        return this;
    }

    @Override
    public final SQLModifier lockMode() {
        return this.lockModifier;
    }

    @Override
    public final boolean groupByWithRollUp() {
        return this.withRollup;
    }

    @Override
    public final UnionOrderBy57Spec<C, Q> bracketsQuery() {
        return null;
    }


    @Override
    final Q onAsQuery(final boolean justAsQuery) {
        final List<TableBlock> tableBlockList = this.tableBlockList;
        if (CollectionUtils.isEmpty(tableBlockList)) {
            this.tableBlockList = Collections.emptyList();
        } else {
            this.tableBlockList = CollectionUtils.unmodifiableList(tableBlockList);
        }
        final Q thisQuery, resultQuery;
        if (this instanceof ScalarSubQuery) {
            thisQuery = (Q) ScalarSubQueryExpression.create((ScalarSubQuery<?>) this);
        } else {
            thisQuery = (Q) this;
        }
        if (justAsQuery && this instanceof AbstractUnionAndQuery) {
            final AbstractUnionAndQuery<C, Q> unionAndQuery = (AbstractUnionAndQuery<C, Q>) this;
            resultQuery = MySQL57UnionQuery.unionQuery(unionAndQuery.left, unionAndQuery.unionType, thisQuery)
                    .asQuery();
        } else {
            resultQuery = thisQuery;
        }
        return resultQuery;
    }

    @Override
    final void onClear() {

    }

    @Override
    final UnionOrderBy57Spec<C, Q> createUnionQuery(Q left, UnionType unionType, Q right) {
        return MySQL57UnionQuery.unionQuery(left, unionType, right);
    }

    @Override
    final Select57Spec<C, Q> asQueryAndQuery(UnionType unionType) {
        return MySQL57SimpleQuery.unionAndSelect(this.asQuery(), unionType);
    }


    private static abstract class AbstractUnionAndQuery<C, Q extends Query> extends MySQL57SimpleQuery<C, Q> {

        final Q left;

        final UnionType unionType;

        private AbstractUnionAndQuery(Q left, UnionType unionType) {
            super(CriteriaContextImpl.from(left));
            this.left = left;
            this.unionType = unionType;
        }


    }//AbstractUnionAndQuery


}
