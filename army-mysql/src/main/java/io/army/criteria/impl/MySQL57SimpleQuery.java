package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.mysql._MySQL57Query;
import io.army.criteria.mysql.MySQL57Query;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.session.Database;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

abstract class MySQL57SimpleQuery<C, Q extends Query> extends MySQLSimpleQuery<
        C,
        Q,
        MySQL57Query.From57Spec<C, Q>, //SR
        MySQL57Query.IndexHintJoin57Spec<C, Q>, //FT
        MySQL57Query.Join57Spec<C, Q>,          //FS
        MySQL57Query.PartitionJoin57Clause<C, Q>, //FP
        MySQL57Query.IndexPurposeJoin57Clause<C, Q>, //IR
        MySQL57Query.IndexHintOn57Spec<C, Q>,   //JT
        MySQL57Query.On57Clause<C, Q>,            //JS
        MySQL57Query.PartitionOn57Clause<C, Q>,   //JP
        MySQL57Query.IndexHintNoOn57Spec<C, Q>,//JC
        MySQL57Query.LestBracket57Clause<C, Q>,//JE
        MySQL57Query.PartitionNoOn57Clause<C, Q>,//JF
        MySQL57Query.GroupBy57Spec<C, Q>,       //WR
        MySQL57Query.WhereAnd57Spec<C, Q>,     //AR
        MySQL57Query.WithRollup57Spec<C, Q>,  //GR
        MySQL57Query.OrderBy57Spec<C, Q>,    //HR
        MySQL57Query.Limit57Spec<C, Q>,     //OR
        MySQL57Query.Lock57Spec<C, Q>,    //LR
        MySQL57Query.UnionOrderBy57Spec<C, Q>,   //UR
        MySQL57Query.Select57Clause<C, Q>>   //SP
        implements MySQL57Query, MySQL57Query.Select57Clause<C, Q>, MySQL57Query.From57Spec<C, Q>
        , MySQL57Query.Join57Spec<C, Q>, MySQL57Query.WhereAnd57Spec<C, Q>, MySQL57Query.WithRollup57Spec<C, Q>
        , MySQL57Query.Having57Spec<C, Q>, MySQL57Query.IndexHintJoin57Spec<C, Q>
        , MySQL57Query.IndexPurposeJoin57Clause<C, Q>, _MySQL57Query {


    static <C> Select57Clause<C, Select> simpleSelect(@Nullable C criteria) {
        return new SimpleSelect<>(criteria);
    }

    static <C> Select57Clause<C, SubQuery> subQuery(@Nullable C criteria) {
        return new SimpleSubQuery<>(criteria);
    }

    static <C, E> Select57Clause<C, ScalarExpression> scalarSubQuery(@Nullable C criteria) {
        return new SimpleScalarSubQuery<>(criteria);
    }


    static <C, Q extends Query> Select57Clause<C, Q> unionAndSelect(final Q left, final UnionType unionType) {
        final Select57Clause<C, ?> select57Spec;
        final C criteria = CriteriaUtils.getCriteria(left);
        if (left instanceof Select) {
            select57Spec = new UnionAndSelect<>((Select) left, unionType, criteria);
        } else if (left instanceof ScalarSubQuery) {
            select57Spec = new UnionAndScalarSubQuery<>((ScalarExpression) left, unionType, criteria);
        } else if (left instanceof SubQuery) {
            select57Spec = new UnionAndSubQuery<>((SubQuery) left, unionType, criteria);
        } else {
            throw _Exceptions.unknownQueryType(left);
        }
        return (Select57Clause<C, Q>) select57Spec;
    }


    private boolean withRollup;

    private SQLModifier lockModifier;

    private MySQL57SimpleQuery(@Nullable C criteria) {
        super(CriteriaContexts.queryContext(criteria));
        if (this instanceof Select) {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        } else {
            CriteriaContextStack.push(this.criteriaContext);
        }

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
    public final UnionOrderBy57Spec<C, Q> bracket() {
        final UnionOrderBy57Spec<C, Q> unionSpec;
        if (this instanceof AbstractUnionAndQuery) {
            final AbstractUnionAndQuery<C, Q> andQuery = (AbstractUnionAndQuery<C, Q>) this;
            final Q thisQuery = this.asQueryAndQuery();
            if (this instanceof ScalarSubQuery) {
                if (!(thisQuery instanceof ScalarSubQueryExpression)
                        || ((ScalarSubQueryExpression) thisQuery).subQuery != this) {
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
    final Q onAsQuery(final boolean outer) {
        final Q thisQuery, resultQuery;
        if (this instanceof ScalarSubQuery) {
            thisQuery = (Q) ScalarSubQueryExpression.create((ScalarSubQuery) this);
        } else {
            thisQuery = (Q) this;
        }
        if (outer && this instanceof AbstractUnionAndQuery) {
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
    final Select57Clause<C, Q> asQueryAndQuery(UnionType unionType) {
        return MySQL57SimpleQuery.unionAndSelect(this.asQuery(), unionType);
    }

    @Override
    final PartitionJoin57Clause<C, Q> createNoneBlockBeforeAs(TableMeta<?> table) {
        return new PartitionJoinImpl<>(table, this);
    }

    @Override
    final PartitionOn57Clause<C, Q> createBlockBeforeAs(_JoinType joinType, TableMeta<?> table) {
        return new PartitionOnBlock<>(joinType, table, this);
    }

    @Override
    final IndexHintOn57Spec<C, Q> createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        return new IndexHintOnBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    final On57Clause<C, Q> createOnBlock(_JoinType joinType, TableItem tableItem, String alias) {
        Objects.requireNonNull(tableItem);
        return new OnBlock<>(joinType, tableItem, alias, this);
    }

    @Override
    final IndexHintNoOn57Spec<C, Q> createNextClauseForCross() {
        return null;
    }

    @Override
    final PartitionNoOn57Clause<C, Q> createNextClauseForCross(TableMeta<?> table) {
        return null;
    }

    @Override
    final IndexHintOn57Spec<C, Q> createNoActionTableBlock() {
        return new NoActionIndexHintOnBlock<>(this);
    }

    @Override
    final On57Clause<C, Q> createNoActionOnBlock() {
        return new NoActionOnBlock<>(this);
    }


    @Override
    final PartitionOn57Clause<C, Q> createNoActionPartitionBlock() {
        return new NoActionPartitionOnBlock<>(this);
    }


    @Override
    final IndexHintNoOn57Spec<C, Q> createNoActionClauseForCross() {
        return null;
    }

    @Override
    final Dialect defaultDialect() {
        return Dialect.MySQL57;
    }

    @Override
    final void validateDialect(Dialect mode) {
        if (mode.database() != Database.MySQL) {
            throw new IllegalArgumentException(String.format("Don't support dialect[%s]", mode));
        }
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


    private static final class SimpleScalarSubQuery<C> extends SimpleSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private SimpleScalarSubQuery(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public ParamMeta paramMeta() {
            return ((Selection) this.selectItemList().get(0)).paramMeta();
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


    private static final class UnionAndScalarSubQuery<C> extends AbstractUnionAndQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private UnionAndScalarSubQuery(ScalarExpression left, UnionType unionType, @Nullable C criteria) {
            super(left, unionType, criteria);
        }

        @Override
        public ParamMeta paramMeta() {
            return ((Selection) this.selectItemList().get(0)).paramMeta();
        }


    }// UnionAndScalarSubQuery


    /**
     * @see #createFirstPartitionBlock(TableMeta)
     */
    private static final class PartitionJoinImpl<C, Q extends Query>
            extends MySQLPartitionClause<C, AsJoin57Clause<C, Q>>
            implements PartitionJoin57Clause<C, Q>, AsJoin57Clause<C, Q> {

        private final TableMeta<?> table;

        private final MySQL57SimpleQuery<C, Q> query;

        private PartitionJoinImpl(TableMeta<?> table, MySQL57SimpleQuery<C, Q> query) {
            super(query.criteria);
            this.table = table;
            this.query = query;
        }

        @Override
        public IndexHintJoin57Spec<C, Q> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final MySQLNoneBlock<C, MySQL57SimpleQuery<C, Q>> block;
            if (partitionList == null) {
                block = new MySQLNoneBlock<>(this.table, alias, this.query);
            } else {
                block = new MySQLNoneBlock<>(this.table, alias, partitionList, this.query);
            }
            this.query.criteriaContext.onNoneBlock(block);
            return this.query;
        }


    }// PartitionJoinBlock


    /**
     * @see #createOnBlock(_JoinType, TableItem, String)
     */
    private static class OnBlock<C, Q extends Query> extends OnClauseTableBlock<C, MySQL57Query.Join57Spec<C, Q>>
            implements On57Clause<C, Q> {


        private final MySQL57SimpleQuery<C, Q> query;

        private OnBlock(_JoinType joinType, TableItem tableItem, String alias, MySQL57SimpleQuery<C, Q> query) {
            super(joinType, tableItem, alias);
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
     * @see #createTableBlock(_JoinType, TableMeta, String)
     * @see PartitionOnBlock#as(String)
     */
    private static final class IndexHintOnBlock<C, Q extends Query> extends MySQLIndexHintOnBlock<
            C,
            MySQL57Query.IndexPurposeOn57Spec<C, Q>,
            MySQL57Query.IndexHintOn57Spec<C, Q>,
            MySQL57Query.Join57Spec<C, Q>>
            implements MySQL57Query.IndexPurposeOn57Spec<C, Q>, MySQL57Query.IndexHintOn57Spec<C, Q> {

        private final List<String> partitionList;

        private final MySQL57SimpleQuery<C, Q> query;

        private IndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias, MySQL57SimpleQuery<C, Q> query) {
            super(joinType, table, alias);
            this.query = query;
            this.partitionList = Collections.emptyList();
        }

        private IndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias
                , List<String> partitionList, MySQL57SimpleQuery<C, Q> query) {
            super(joinType, table, alias);
            this.query = query;
            this.partitionList = _CollectionUtils.unmodifiableList(partitionList);
        }

        @Override
        C getCriteria() {
            return this.query.criteria;
        }

        @Override
        Join57Spec<C, Q> endOnClause() {
            return this.query;
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }


    }// IndexHintOnBlock


    /**
     * @see #createBlockBeforeAs(_JoinType, TableMeta)
     */
    private static final class PartitionOnBlock<C, Q extends Query>
            extends MySQLPartitionClause<C, AsOn57Clause<C, Q>>
            implements AsOn57Clause<C, Q>, PartitionOn57Clause<C, Q> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final MySQL57SimpleQuery<C, Q> query;

        private PartitionOnBlock(_JoinType joinType, TableMeta<?> table, MySQL57SimpleQuery<C, Q> query) {
            super(query.criteria);
            this.joinType = joinType;
            this.table = table;
            this.query = query;
        }

        @Override
        public IndexHintOn57Spec<C, Q> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final IndexHintOnBlock<C, Q> hintOnBlock;
            if (partitionList == null) {
                hintOnBlock = new IndexHintOnBlock<>(this.joinType, this.table, alias, this.query);
            } else {
                hintOnBlock = new IndexHintOnBlock<>(this.joinType, this.table, alias, partitionList, this.query);
            }
            this.query.criteriaContext.onAddBlock(hintOnBlock);
            return hintOnBlock;
        }


    }// PartitionOnBlock

    /**
     * @see #createNoActionOnBlock()
     */
    private static final class NoActionOnBlock<C, Q extends Query> extends NoActionOnClause<C, MySQL57Query.Join57Spec<C, Q>>
            implements On57Clause<C, Q> {

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
            extends MySQLNoActionPartitionClause<C, AsOn57Clause<C, Q>>
            implements PartitionOn57Clause<C, Q>, AsOn57Clause<C, Q> {

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
