package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQL57Query;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.mysql.MySQL57Query;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class MySQL57SimpleQuery<C, Q extends Query> extends MySQLSimpleQuery<
        C,
        Q,
        MySQL57Query.From57Spec<C, Q>, //SR
        MySQL57Query.IndexHintJoin57Spec<C, Q>, //FT
        MySQL57Query.Join57Spec<C, Q>,          //FS
        MySQL57Query.PartitionJoin57Spec<C, Q>, //FP
        MySQL57Query.IndexPurposeJoin57Spec<C, Q>, //IR
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
        implements MySQL57Query, MySQL57Query.Select57Spec<C, Q>, MySQL57Query.From57Spec<C, Q>
        , MySQL57Query.Where57Spec<C, Q>, MySQL57Query.WhereAnd57Spec<C, Q>, MySQL57Query.WithRollup57Spec<C, Q>
        , MySQL57Query.Having57Spec<C, Q>, MySQL57Query.IndexHintJoin57Spec<C, Q>
        , MySQL57Query.IndexPurposeJoin57Spec<C, Q>, _MySQL57Query {


    static <C> Select57Spec<C, Select> simpleSelect(@Nullable C criteria) {
        return new SimpleSelect<>(criteria);
    }

    static <C> Select57Spec<C, SubQuery> subQuery(@Nullable C criteria) {
        return new SimpleSubQuery<>(criteria);
    }

    static <C> Select57Spec<C, RowSubQuery> rowSubQuery(@Nullable C criteria) {
        return new SimpleRowSubQuery<>(criteria);
    }

    static <C> Select57Spec<C, ColumnSubQuery> columnSubQuery(@Nullable C criteria) {
        return new SimpleColumnSubQuery<>(criteria);
    }

    static <C, E> Select57Spec<C, ScalarQueryExpression<E>> scalarSubQuery(@Nullable C criteria) {
        return new SimpleScalarSubQuery<>(criteria);
    }


    static <C, Q extends Query> Select57Spec<C, Q> unionAndSelect(final Q left, final UnionType unionType) {
        final Select57Spec<C, ?> select57Spec;
        final C criteria = CriteriaUtils.getCriteria(left);
        if (left instanceof Select) {
            select57Spec = new UnionAndSelect<>((Select) left, unionType, criteria);
        } else if (left instanceof ScalarSubQuery) {
            select57Spec = new UnionAndScalarSubQuery<>((ScalarQueryExpression<?>) left, unionType, criteria);
        } else if (left instanceof ColumnSubQuery) {
            select57Spec = new UnionAndColumnSubQuery<>((ColumnSubQuery) left, unionType, criteria);
        } else if (left instanceof RowSubQuery) {
            select57Spec = new UnionAndRowSubQuery<>((RowSubQuery) left, unionType, criteria);
        } else if (left instanceof SubQuery) {
            select57Spec = new UnionAndSubQuery<>((SubQuery) left, unionType, criteria);
        } else {
            throw _Exceptions.unknownQueryType(left);
        }
        return (Select57Spec<C, Q>) select57Spec;
    }


    private boolean withRollup;

    private SQLModifier lockModifier;

    private MySQL57SimpleQuery(@Nullable C criteria) {
        super(CriteriaUtils.primaryContext(criteria));
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }


    @Override
    public final Having57Spec<C, Q> withRollup() {
        if (hasGroupBy()) {
            this.withRollup = true;
        }
        return this;
    }

    @Override
    public final Having57Spec<C, Q> ifWithRollup(Predicate<C> predicate) {
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
        final UnionOrderBy57Spec<C, Q> unionSpec;
        if (this instanceof AbstractUnionAndQuery) {
            final AbstractUnionAndQuery<C, Q> andQuery = (AbstractUnionAndQuery<C, Q>) this;
            final Q thisQuery = this.asQueryAndQuery();
            if (this instanceof ScalarSubQuery) {
                if (!(thisQuery instanceof ScalarSubQueryExpression)
                        || ((ScalarSubQueryExpression<?>) thisQuery).subQuery != this) {
                    throw asQueryMethodError();
                }
            } else if (thisQuery != this) {
                throw asQueryMethodError();
            }
            final Q right;
            right = MySQL57UnionQuery.bracketQuery(thisQuery)
                    .asQuery();
            unionSpec = MySQL57UnionQuery.unionQuery(andQuery.left, andQuery.unionType, right);
        } else {
            unionSpec = MySQL57UnionQuery.bracketQuery(this.asQuery());
        }
        return unionSpec;
    }


    @Override
    final Q onAsQuery(final boolean justAsQuery) {
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
        this.withRollup = false;
        this.lockModifier = null;
    }

    @Override
    final UnionOrderBy57Spec<C, Q> createUnionQuery(Q left, UnionType unionType, Q right) {
        return MySQL57UnionQuery.unionQuery(left, unionType, right);
    }

    @Override
    final Select57Spec<C, Q> asQueryAndQuery(UnionType unionType) {
        return MySQL57SimpleQuery.unionAndSelect(this.asQuery(), unionType);
    }

    @Override
    final PartitionOn57Spec<C, Q> createPartitionOnBlock(JoinType joinType, TableMeta<?> table) {
        return new PartitionOnBlock<>(joinType, table, this);
    }

    @Override
    final IndexHintOn57Spec<C, Q> createIndexHintOnBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
        return new IndexHintOnBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    final On57Spec<C, Q> createOnBlock(JoinType joinType, TablePart tablePart, String alias) {
        return new OnBlock<>(joinType, tablePart, alias, this);
    }

    @Override
    final IndexHintOn57Spec<C, Q> createNoActionTableBlock() {
        return new NoActionIndexHintOnBlock<>(this);
    }

    @Override
    final On57Spec<C, Q> createNoActionOnBlock() {
        return new NoActionOnBlock<>(this);
    }


    @Override
    final PartitionOn57Spec<C, Q> createNoActionPartitionBlock() {
        return new NoActionPartitionOnBlock<>(this);
    }

    @Override
    final PartitionJoin57Spec<C, Q> createFromBlockWithPartition(TableMeta<?> table
            , Function<MySQLFromTableBlock, IndexHintJoin57Spec<C, Q>> function) {
        return new PartitionJoinImpl<>(table, function, this.criteria);
    }

    /*################################## blow private method ##################################*/






    /*################################## blow inner class ##################################*/

    private static final class SimpleSelect<C> extends MySQL57SimpleQuery<C, Select>
            implements Select {

        private SimpleSelect(@Nullable C criteria) {
            super(criteria);
        }


    }// SimpleSelect

    private static class SimpleSubQuery<C, Q extends SubQuery> extends MySQL57SimpleQuery<C, Q>
            implements SubQuery {

        private SimpleSubQuery(@Nullable C criteria) {
            super(criteria);
        }


    }// SimpleSubQuery

    private static final class SimpleRowSubQuery<C> extends SimpleSubQuery<C, RowSubQuery> implements RowSubQuery {

        private SimpleRowSubQuery(@Nullable C criteria) {
            super(criteria);
        }

    }// SimpleRowSubQuery

    private static final class SimpleColumnSubQuery<C> extends SimpleSubQuery<C, ColumnSubQuery>
            implements ColumnSubQuery {

        private SimpleColumnSubQuery(@Nullable C criteria) {
            super(criteria);
        }

    }// SimpleColumnSubQuery

    private static final class SimpleScalarSubQuery<C, E> extends SimpleSubQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery<E> {

        private SimpleScalarSubQuery(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public ParamMeta paramMeta() {
            return ((Selection) this.selectPartList().get(0)).paramMeta();
        }

    }// SimpleScalarSubQuery


    private static abstract class AbstractUnionAndQuery<C, Q extends Query> extends MySQL57SimpleQuery<C, Q> {

        final Q left;

        final UnionType unionType;

        private AbstractUnionAndQuery(Q left, UnionType unionType, @Nullable C criteria) {
            super(criteria);
            this.left = left;
            this.unionType = unionType;
        }


    }//AbstractUnionAndQuery

    private static final class UnionAndSelect<C> extends AbstractUnionAndQuery<C, Select> implements Select {

        private UnionAndSelect(Select left, UnionType unionType, @Nullable C criteria) {
            super(left, unionType, criteria);
        }

    }// UnionAndSelect

    private static final class UnionAndSubQuery<C> extends AbstractUnionAndQuery<C, SubQuery> implements SubQuery {

        private UnionAndSubQuery(SubQuery left, UnionType unionType, @Nullable C criteria) {
            super(left, unionType, criteria);
        }

    }// UnionAndSubQuery

    private static final class UnionAndRowSubQuery<C> extends AbstractUnionAndQuery<C, RowSubQuery>
            implements RowSubQuery {

        private UnionAndRowSubQuery(RowSubQuery left, UnionType unionType, @Nullable C criteria) {
            super(left, unionType, criteria);
        }

    }// UnionAndRowSubQuery

    private static final class UnionAndColumnSubQuery<C> extends AbstractUnionAndQuery<C, ColumnSubQuery>
            implements ColumnSubQuery {

        private UnionAndColumnSubQuery(ColumnSubQuery left, UnionType unionType, @Nullable C criteria) {
            super(left, unionType, criteria);
        }

    }// UnionAndColumnSubQuery

    private static final class UnionAndScalarSubQuery<C, E> extends AbstractUnionAndQuery<C, ScalarQueryExpression<E>>
            implements ScalarSubQuery<E> {

        private UnionAndScalarSubQuery(ScalarQueryExpression<E> left, UnionType unionType, @Nullable C criteria) {
            super(left, unionType, criteria);
        }

        @Override
        public ParamMeta paramMeta() {
            return ((Selection) this.selectPartList().get(0)).paramMeta();
        }


    }// UnionAndScalarSubQuery


    /**
     * @see MySQL57SimpleQuery#from(TableMeta)
     */
    private static final class PartitionJoinImpl<C, Q extends Query>
            extends MySQLPartitionClause<C, MySQL57Query.As57JoinSpec<C, Q>>
            implements MySQL57Query.PartitionJoin57Spec<C, Q>, MySQL57Query.As57JoinSpec<C, Q> {

        private final TableMeta<?> table;

        private final Function<MySQLFromTableBlock, IndexHintJoin57Spec<C, Q>> function;

        private PartitionJoinImpl(TableMeta<?> table, Function<MySQLFromTableBlock, IndexHintJoin57Spec<C, Q>> function
                , @Nullable C criteria) {
            super(criteria);
            this.table = table;
            this.function = function;
        }

        @Override
        public IndexHintJoin57Spec<C, Q> as(String alias) {
            Objects.requireNonNull(alias);
            List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                partitionList = Collections.emptyList();
            }
            return this.function.apply(new MySQLFromTableBlock(this.table, alias, partitionList));
        }


    }// PartitionJoinBlock


    /**
     * @see #createOnBlock(JoinType, TablePart, String)
     */
    private static class OnBlock<C, Q extends Query> extends OnClauseTableBlock<C, MySQL57Query.Join57Spec<C, Q>>
            implements MySQL57Query.On57Spec<C, Q> {


        private final MySQL57SimpleQuery<C, Q> query;

        private OnBlock(JoinType joinType, TablePart tablePart, String alias, MySQL57SimpleQuery<C, Q> query) {
            super(joinType, tablePart, alias);
            this.query = query;
        }

        @Override
        final C getCriteria() {
            return this.query.criteria;
        }

        @Override
        final Join57Spec<C, Q> endOnClause() {
            return this.query;
        }

    }// OnBlock


    /**
     * @see #createIndexHintOnBlock(JoinType, TableMeta, String)
     */
    private static final class IndexHintOnBlock<C, Q extends Query> extends MySQLIndexHintOnBlock<
            C,
            MySQL57Query.IndexPurposeOn57Spec<C, Q>,
            MySQL57Query.IndexHintOn57Spec<C, Q>,
            MySQL57Query.Join57Spec<C, Q>>
            implements MySQL57Query.IndexPurposeOn57Spec<C, Q>, MySQL57Query.IndexHintOn57Spec<C, Q> {

        private final MySQL57SimpleQuery<C, Q> query;

        private IndexHintOnBlock(JoinType joinType, TableMeta<?> table, String alias, MySQL57SimpleQuery<C, Q> query) {
            super(joinType, table, alias);
            this.query = query;
        }


        @Override
        C getCriteria() {
            return this.query.criteria;
        }

        @Override
        Join57Spec<C, Q> endOnClause() {
            return this.query;
        }

    }// IndexHintOnBlock


    /**
     * @see #createPartitionOnBlock(JoinType, TableMeta)
     */
    private static final class PartitionOnBlock<C, Q extends Query>
            extends MySQLPartitionClause<C, MySQL57Query.As57OnSpec<C, Q>>
            implements MySQL57Query.As57OnSpec<C, Q>, MySQL57Query.PartitionOn57Spec<C, Q>, _MySQLTableBlock {

        private final JoinType joinType;

        private final TableMeta<?> table;

        private final MySQL57SimpleQuery<C, Q> query;

        private IndexHintOnBlock<C, Q> hintOnBlock;

        private PartitionOnBlock(JoinType joinType, TableMeta<?> table, MySQL57SimpleQuery<C, Q> query) {
            super(query.criteria);
            this.joinType = joinType;
            this.table = table;
            this.query = query;
        }

        @Override
        public IndexHintOn57Spec<C, Q> as(String alias) {
            if (this.hintOnBlock != null) {
                throw _Exceptions.castCriteriaApi();
            }
            Objects.requireNonNull(alias);
            final IndexHintOnBlock<C, Q> hintOnBlock;
            hintOnBlock = new IndexHintOnBlock<>(this.joinType, this.table, alias, this.query);
            this.hintOnBlock = hintOnBlock;
            return hintOnBlock;
        }

        @Override
        public TablePart table() {
            return this.table;
        }

        @Override
        public String alias() {
            final IndexHintOnBlock<C, Q> hintOnBlock = this.hintOnBlock;
            assert hintOnBlock != null;
            return hintOnBlock.alias;
        }

        @Override
        public SQLModifier jointType() {
            return this.joinType;
        }

        @Override
        public List<_Predicate> predicates() {
            final IndexHintOnBlock<C, Q> hintOnBlock = this.hintOnBlock;
            assert hintOnBlock != null;
            return hintOnBlock.predicates();
        }

        @Override
        public List<String> partitionList() {
            List<String> partitionList = this.partitionList;
            if (partitionList == null) {
                partitionList = Collections.emptyList();
            }
            return partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            final IndexHintOnBlock<C, Q> hintOnBlock = this.hintOnBlock;
            assert hintOnBlock != null;
            return hintOnBlock.indexHintList();
        }


    }// PartitionOnBlock

    /**
     * @see #createNoActionOnBlock()
     */
    private static final class NoActionOnBlock<C, Q extends Query> extends NoActionOnClause<C, MySQL57Query.Join57Spec<C, Q>>
            implements MySQL57Query.On57Spec<C, Q> {

        private NoActionOnBlock(Join57Spec<C, Q> stmt) {
            super(stmt);
        }

    }//NoActionOnBlock

    /**
     * @see #createNoActionTableBlock()
     */
    private static final class NoActionIndexHintOnBlock<C, Q extends Query> extends MySQLNoActionIndexHintOnBlock<
            C,
            MySQL57Query.IndexPurposeOn57Spec<C, Q>,
            MySQL57Query.IndexHintOn57Spec<C, Q>,
            MySQL57Query.Join57Spec<C, Q>>
            implements MySQL57Query.IndexPurposeOn57Spec<C, Q>, MySQL57Query.IndexHintOn57Spec<C, Q> {

        private NoActionIndexHintOnBlock(Join57Spec<C, Q> stmt) {
            super(stmt);
        }

    }// NoActionIndexHintOnBlock

    /**
     * @see #createNoActionPartitionBlock()
     */
    private static final class NoActionPartitionOnBlock<C, Q extends Query>
            extends MySQLNoActionPartitionClause<C, MySQL57Query.As57OnSpec<C, Q>>
            implements MySQL57Query.PartitionOn57Spec<C, Q>, MySQL57Query.As57OnSpec<C, Q> {

        private final MySQL57Query.IndexHintOn57Spec<C, Q> hintOn57Spec;

        private NoActionPartitionOnBlock(Join57Spec<C, Q> stmt) {
            this.hintOn57Spec = new NoActionIndexHintOnBlock<>(stmt);
        }

        @Override
        public IndexHintOn57Spec<C, Q> as(String alias) {
            return this.hintOn57Spec;
        }

    }//NoActionPartitionOnBlock


}
