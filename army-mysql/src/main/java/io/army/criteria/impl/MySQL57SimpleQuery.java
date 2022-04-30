package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQL57Query;
import io.army.criteria.mysql.MySQL57Query;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.session.Database;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * <p>
 * This class is the implementation of {@link MySQL57Query}
 * </p>
 *
 * @param <C> java criteria object java type
 * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
 * @since 1.0
 */
abstract class MySQL57SimpleQuery<C, Q extends Query> extends MySQLSimpleQuery<
        C,
        Q,
        Void,                       //WE
        MySQL57Query._FromSpec<C, Q>, //SR
        MySQL57Query._IndexHintJoinSpec<C, Q>, //FT
        MySQL57Query._JoinSpec<C, Q>,          //FS
        MySQL57Query._PartitionJoinClause<C, Q>, //FP
        MySQL57Query._IndexPurposeJoinClause<C, Q>, //IR
        MySQL57Query._IndexHintOnSpec<C, Q>,   //JT
        Statement._OnClause<C, MySQL57Query._JoinSpec<C, Q>>,            //JS
        MySQL57Query._PartitionOnClause<C, Q>,   //JP
        MySQL57Query._LestBracket57Clause<C, Q>,//JE
        MySQL57Query._GroupBySpec<C, Q>,       //WR
        MySQL57Query._WhereAndSpec<C, Q>,     //AR
        MySQL57Query._WithRollupSpec<C, Q>,  //GR
        MySQL57Query._OrderBySpec<C, Q>,    //HR
        MySQL57Query._LimitSpec<C, Q>,     //OR
        MySQL57Query._LockSpec<C, Q>,    //LR
        MySQL57Query._UnionOrderBySpec<C, Q>,   //UR
        MySQL57Query._Select57Clause<C, Q>>   //SP
        implements MySQL57Query, MySQL57Query._Select57Clause<C, Q>, MySQL57Query._FromSpec<C, Q>
        , MySQL57Query._JoinSpec<C, Q>, MySQL57Query._WhereAndSpec<C, Q>, MySQL57Query._WithRollupSpec<C, Q>
        , MySQL57Query._HavingSpec<C, Q>, MySQL57Query._IndexHintJoinSpec<C, Q>, _MySQL57Query
        , MySQL57Query._IndexPurposeJoinClause<C, Q> {


    static <C> _Select57Clause<C, Select> simpleSelect(@Nullable C criteria) {
        return new SimpleSelect<>(criteria);
    }

    static <C> _Select57Clause<C, SubQuery> subQuery(@Nullable C criteria) {
        return new SimpleSubQuery<>(criteria);
    }

    static <C> _Select57Clause<C, ScalarExpression> scalarSubQuery(@Nullable C criteria) {
        return new SimpleScalarSubQuery<>(criteria);
    }


    @SuppressWarnings("unchecked")
    static <C, Q extends Query> _Select57Clause<C, Q> unionAndSelect(final Q left, final UnionType unionType) {
        final _Select57Clause<C, ?> select57Spec;
        if (left instanceof Select) {
            select57Spec = new UnionAndSelect<>((Select) left, unionType);
        } else if (left instanceof ScalarSubQuery) {
            select57Spec = new UnionAndScalarSubQuery<>((ScalarExpression) left, unionType);
        } else if (left instanceof SubQuery) {
            select57Spec = new UnionAndSubQuery<>((SubQuery) left, unionType);
        } else {
            throw _Exceptions.unknownRowSetType(left);
        }
        return (_Select57Clause<C, Q>) select57Spec;
    }


    private boolean withRollup;

    private SQLWords lockModifier;

    private MySQL57SimpleQuery(@Nullable C criteria) {
        super(CriteriaContexts.queryContext(criteria));

    }

    private MySQL57SimpleQuery(CriteriaContext criteriaContext) {
        super(criteriaContext);
        if (!(this instanceof UnionAndQuery)) {
            throw new IllegalStateException("this error.");
        }
    }


    @Override
    public final _HavingSpec<C, Q> withRollup() {
        if (hasGroupBy()) {
            this.withRollup = true;
        }
        return this;
    }

    @Override
    public final _HavingSpec<C, Q> ifWithRollup(Predicate<C> predicate) {
        if (hasGroupBy() && predicate.test(this.criteria)) {
            this.withRollup = true;
        }
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> forUpdate() {
        this.lockModifier = MySQLLock.FOR_UPDATE;
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> lockInShareMode() {
        this.lockModifier = MySQLLock.LOCK_IN_SHARE_MODE;
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> ifForUpdate(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lockModifier = MySQLLock.FOR_UPDATE;
        }
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> ifLockInShareMode(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.lockModifier = MySQLLock.LOCK_IN_SHARE_MODE;
        }
        return this;
    }

    @Override
    public final SQLWords lockMode() {
        return this.lockModifier;
    }


    @Override
    public final boolean groupByWithRollUp() {
        return this.withRollup;
    }

    @Override
    final Q onAsQuery(final boolean fromAsQueryMethod) {
        super.onAsQuery(fromAsQueryMethod);
        return this.finallyAsQuery(fromAsQueryMethod);
    }

    @Override
    final void onClear() {
        super.onClear();
        this.withRollup = false;
        this.lockModifier = null;
    }


    @Override
    final _Select57Clause<C, Q> asUnionAndRowSet(UnionType unionType) {
        return MySQL57SimpleQuery.unionAndSelect(this.asQuery(), unionType);
    }

    @Override
    final _PartitionOnClause<C, Q> createNextClauseWithOnClause(_JoinType joinType, TableMeta<?> table) {
        return new PartitionOnBlock<>(joinType, table, this);
    }

    @Override
    final _IndexHintOnSpec<C, Q> createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        return new IndexHintOnBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    final _OnClause<C, _JoinSpec<C, Q>> createOnBlock(_JoinType joinType, TableItem tableItem, String alias) {
        Objects.requireNonNull(tableItem);
        return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
    }

    @Override
    final _UnionOrderBySpec<C, Q> createBracketQuery(RowSet rowSet) {
        return MySQL57UnionQuery.bracketQuery(rowSet);
    }

    @Override
    final _UnionOrderBySpec<C, Q> createUnionRowSet(RowSet left, UnionType unionType, RowSet right) {
        return MySQL57UnionQuery.unionQuery((Q) left, unionType, right);
    }

    @Override
    final _TableBlock createTableBlockWithoutOnClause(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        return new MySQLNoOnBlock(joinType, table, tableAlias);
    }

    @Override
    final _PartitionJoinClause<C, Q> createNextClauseWithoutOnClause(_JoinType joinType, TableMeta<?> table) {
        return new PartitionJoinImpl<>(joinType, table, this);
    }

    @Override
    final void doWithCte(boolean recursive, List<Cte> cteList) {
        //MySQL 5.7 don't support WITH clause.
        throw _Exceptions.castCriteriaApi();
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

        private Map<String, Selection> selectionMap;

        private SimpleSubQuery(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final Selection selection(String derivedFieldName) {
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                selectionMap = CriteriaUtils.createSelectionMap(this.selectItemList());
                this.selectionMap = selectionMap;
            }
            return selectionMap.get(derivedFieldName);
        }


    }// SimpleSubQuery


    private static final class SimpleScalarSubQuery<C> extends SimpleSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private SimpleScalarSubQuery(@Nullable C criteria) {
            super(criteria);
        }


    }// SimpleScalarSubQuery


    private static abstract class UnionAndQuery<C, Q extends Query> extends MySQL57SimpleQuery<C, Q>
            implements UnionAndRowSet {

        final Q left;

        final UnionType unionType;

        private UnionAndQuery(Q left, UnionType unionType) {
            super(CriteriaContexts.unionAndContext(left));
            this.left = left;
            this.unionType = unionType;
        }

        @Override
        public final RowSet leftRowSet() {
            return this.left;
        }

        @Override
        public final UnionType unionType() {
            return this.unionType;
        }

    }//AbstractUnionAndQuery

    private static final class UnionAndSelect<C> extends UnionAndQuery<C, Select> implements Select {

        private UnionAndSelect(Select left, UnionType unionType) {
            super(left, unionType);
        }

    }// UnionAndSelect

    private static class UnionAndSubQuery<C, Q extends SubQuery> extends UnionAndQuery<C, Q> implements SubQuery {

        private Map<String, Selection> selectionMap;

        private UnionAndSubQuery(Q left, UnionType unionType) {
            super(left, unionType);
        }

        @Override
        public final Selection selection(String derivedFieldName) {
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                selectionMap = CriteriaUtils.createSelectionMap(this.selectItemList());
                this.selectionMap = selectionMap;
            }
            return selectionMap.get(derivedFieldName);
        }

    }// UnionAndSubQuery


    private static final class UnionAndScalarSubQuery<C> extends UnionAndSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private UnionAndScalarSubQuery(ScalarExpression left, UnionType unionType) {
            super(left, unionType);
        }


    }// UnionAndScalarSubQuery


    private static final class PartitionJoinImpl<C, Q extends Query>
            extends MySQLPartitionClause<C, _AsJoinClause<C, Q>>
            implements _PartitionJoinClause<C, Q>, _AsJoinClause<C, Q> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final MySQL57SimpleQuery<C, Q> query;

        private PartitionJoinImpl(_JoinType joinType, TableMeta<?> table, MySQL57SimpleQuery<C, Q> query) {
            super(query.criteria);
            this.joinType = joinType;
            this.table = table;
            this.query = query;
        }

        @Override
        public _IndexHintJoinSpec<C, Q> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final MySQLNoOnBlock block;
            if (partitionList == null) {
                block = new MySQLNoOnBlock(this.joinType, this.table, alias);
            } else {
                block = new MySQLNoOnBlock(this.joinType, this.table, alias, partitionList);
            }
            this.query.criteriaContext.onBlockWithoutOnClause(block);
            return this.query;
        }


    }// PartitionJoinBlock


    /**
     * @see #createTableBlock(_JoinType, TableMeta, String)
     * @see PartitionOnBlock#as(String)
     */
    private static final class IndexHintOnBlock<C, Q extends Query> extends MySQLIndexHintOnBlock<
            C,
            _IndexPurposeOn57Clause<C, Q>,
            _IndexHintOnSpec<C, Q>,
            _JoinSpec<C, Q>>
            implements _IndexPurposeOn57Clause<C, Q>, _IndexHintOnSpec<C, Q> {

        private final List<String> partitionList;

        private IndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias, MySQL57SimpleQuery<C, Q> query) {
            super(joinType, table, alias, query);
            this.partitionList = Collections.emptyList();
        }

        private IndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias
                , List<String> partitionList, MySQL57SimpleQuery<C, Q> query) {
            super(joinType, table, alias, query);
            this.partitionList = _CollectionUtils.unmodifiableList(partitionList);
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }


    }// IndexHintOnBlock


    /**
     * @see #createNextClauseWithOnClause(_JoinType, TableMeta)
     */
    private static final class PartitionOnBlock<C, Q extends Query>
            extends MySQLPartitionClause<C, _AsOnClause<C, Q>>
            implements _AsOnClause<C, Q>, _PartitionOnClause<C, Q> {

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
        public _IndexHintOnSpec<C, Q> as(String alias) {
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


}
