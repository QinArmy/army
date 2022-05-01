package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.impl.inner._StandardQuery;
import io.army.criteria.impl.inner._TableBlock;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is a implementation of {@link StandardQuery}.
 * </p>
 *
 * @see StandardUnionQuery
 * @since 1.0
 */
abstract class StandardSimpleQuery<C, Q extends Query> extends SimpleQuery<
        C,
        Q,
        Distinct,
        StandardQuery._StandardFromSpec<C, Q>, // SR
        StandardQuery._JoinSpec<C, Q>,// FT
        StandardQuery._JoinSpec<C, Q>,// FS
        Void,                               // FP
        Statement._OnClause<C, StandardQuery._JoinSpec<C, Q>>, // JT
        Statement._OnClause<C, StandardQuery._JoinSpec<C, Q>>, // JS
        Void,                               // JP
        StandardQuery._StandardLestBracketClause<C, Q>,// JE
        StandardQuery._GroupBySpec<C, Q>, // WR
        StandardQuery._WhereAndSpec<C, Q>, // AR
        StandardQuery._HavingSpec<C, Q>, // GR
        StandardQuery._OrderBySpec<C, Q>, // HR
        StandardQuery._LimitSpec<C, Q>, // OR
        StandardQuery._LockSpec<C, Q>, // LR
        StandardQuery._UnionOrderBySpec<C, Q>, // UR
        StandardQuery._StandardSelectClause<C, Q>> // SP

        implements StandardQuery, StandardQuery._StandardSelectClause<C, Q>, StandardQuery._StandardFromSpec<C, Q>
        , StandardQuery._JoinSpec<C, Q>, StandardQuery._WhereAndSpec<C, Q>, StandardQuery._HavingSpec<C, Q>
        , StandardQuery._StandardLestBracketClause<C, Q>, _StandardQuery {


    static <C> _StandardSelectClause<C, Select> query(@Nullable C criteria) {
        return new SimpleSelect<>(criteria);
    }

    static <C> _StandardSelectClause<C, SubQuery> subQuery(@Nullable C criteria) {
        return new SimpleSubQuery<>(criteria);
    }

    static <C> _StandardSelectClause<C, ScalarExpression> scalarSubQuery(@Nullable C criteria) {
        return new SimpleScalarSubQuery<>(criteria);
    }

    @SuppressWarnings("unchecked")
    static <C, Q extends Query> _StandardSelectClause<C, Q> unionAndQuery(Q query, UnionType unionType) {
        final _StandardSelectClause<C, ?> spec;
        if (query instanceof Select) {
            spec = new UnionAndSelect<>((Select) query, unionType);
        } else if (query instanceof ScalarSubQuery) {
            spec = new UnionAndScalarSubQuery<>((ScalarExpression) query, unionType);
        } else if (query instanceof SubQuery) {
            spec = new UnionAndSubQuery<>((SubQuery) query, unionType);
        } else {
            throw _Exceptions.unknownRowSetType(query);
        }
        return (_StandardSelectClause<C, Q>) spec;
    }


    private LockMode lockMode;

    StandardSimpleQuery(@Nullable C criteria) {
        super(CriteriaContexts.queryContext(criteria));

    }

    StandardSimpleQuery(CriteriaContext context) {
        super(context);
        if (!(this instanceof StandardSimpleQuery.UnionAndQuery)) {
            throw new IllegalStateException("this error.");
        }

    }

    @Override
    public final _UnionSpec<C, Q> lock(LockMode lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> lock(Function<C, LockMode> function) {
        final LockMode lockMode;
        lockMode = function.apply(this.criteria);
        Objects.requireNonNull(lockMode);
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> ifLock(@Nullable LockMode lockMode) {
        if (lockMode != null) {
            this.lockMode = lockMode;
        }
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> ifLock(Supplier<LockMode> supplier) {
        final LockMode lockMode;
        lockMode = supplier.get();
        if (lockMode != null) {
            this.lockMode = lockMode;
        }
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> ifLock(Function<C, LockMode> function) {
        final LockMode lockMode;
        lockMode = function.apply(this.criteria);
        if (lockMode != null) {
            this.lockMode = lockMode;
        }
        return this;
    }


    @Override
    final _StandardSelectClause<C, Q> asUnionAndRowSet(final UnionType unionType) {
        return StandardSimpleQuery.unionAndQuery(this.asQuery(), unionType);
    }


    @Override
    final _TableBlock createNoOnTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        final _TableBlock block;
        switch (joinType) {
            case NONE:
                block = TableBlock.noneBlock(table, tableAlias);
                break;
            case CROSS_JOIN:
                block = TableBlock.crossBlock(table, tableAlias);
                break;
            default:
                throw _Exceptions.castCriteriaApi();
        }
        return block;
    }

    @Override
    final Void createNextNoOnClause(_JoinType joinType, TableMeta<?> table) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    final _OnClause<C, _JoinSpec<C, Q>> createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        return new OnClauseTableBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    final _OnClause<C, _JoinSpec<C, Q>> createItemBlock(_JoinType joinType, TableItem tableItem, String alias) {
        return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
    }

    @Override
    final Dialect defaultDialect() {
        return Dialect.MySQL57;
    }

    @Override
    final void validateDialect(Dialect mode) {
        // no-op
    }


    @Override
    final Q onAsQuery(final boolean fromAsQueryMethod) {
        return this.finallyAsQuery(fromAsQueryMethod);
    }


    @Override
    final void onClear() {
        this.lockMode = null;
    }

    @Override
    final _UnionOrderBySpec<C, Q> createBracketQuery(RowSet rowSet) {
        return StandardUnionQuery.bracketQuery(rowSet);
    }

    @SuppressWarnings("unchecked")
    @Override
    final _UnionOrderBySpec<C, Q> createUnionRowSet(RowSet left, UnionType unionType, RowSet right) {
        return StandardUnionQuery.unionQuery((Q) left, unionType, right);
    }

    @Override
    public final LockMode lockMode() {
        return this.lockMode;
    }




    /*################################## blow private inter class method ##################################*/


    private static final class SimpleSelect<C> extends StandardSimpleQuery<C, Select>
            implements Select {

        private SimpleSelect(@Nullable C criteria) {
            super(criteria);
        }

    }//SimpleSelect


    /**
     * @see #subQuery(Object)
     */
    private static class SimpleSubQuery<C, Q extends SubQuery> extends StandardSimpleQuery<C, Q>
            implements SubQuery, _SelfDescribed {

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


    } // SimpleSubQuery

    /**
     * @see #scalarSubQuery(Object)
     */
    private static final class SimpleScalarSubQuery<C> extends SimpleSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private SimpleScalarSubQuery(@Nullable C criteria) {
            super(criteria);
        }


    }// SimpleScalarSubQuery


    private static abstract class UnionAndQuery<C, Q extends Query> extends StandardSimpleQuery<C, Q>
            implements UnionAndRowSet {

        private final Q left;

        private final UnionType unionType;

        UnionAndQuery(Q left, UnionType unionType) {
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

    }//UnionAndQuery


    private static final class UnionAndSelect<C> extends UnionAndQuery<C, Select>
            implements Select {

        private UnionAndSelect(Select left, UnionType unionType) {
            super(left, unionType);
        }

    } // UnionAndSelect


    private static class UnionAndSubQuery<C, Q extends SubQuery> extends UnionAndQuery<C, Q>
            implements SubQuery, _SelfDescribed {

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


}
