package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.impl.inner._StandardQuery;
import io.army.dialect.Dialect;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see StandardUnionQuery
 */
abstract class StandardSimpleQuery<C, Q extends Query> extends SimpleQuery<
        C,
        Q,
        StandardQuery.StandardFromSpec<C, Q>, // SR
        StandardQuery.StandardJoinSpec<C, Q>,// FT
        StandardQuery.StandardJoinSpec<C, Q>,// FS
        StandardQuery.StandardOnSpec<C, Q>, // JT
        StandardQuery.StandardOnSpec<C, Q>, // JS
        Void,
        StandardQuery.StandardGroupBySpec<C, Q>, // WR
        StandardQuery.StandardWhereAndSpec<C, Q>, // AR
        StandardQuery.StandardHavingSpec<C, Q>, // GR
        StandardQuery.StandardOrderBySpec<C, Q>, // HR
        StandardQuery.StandardLimitSpec<C, Q>, // OR
        StandardQuery.StandardLockSpec<C, Q>, // LR
        StandardQuery.StandardUnionSpec<C, Q>, // UR
        StandardQuery.StandardSelectSpec<C, Q>> // SP

        implements StandardQuery, StandardQuery.StandardSelectSpec<C, Q>, StandardQuery.StandardFromSpec<C, Q>
        , StandardQuery.StandardJoinSpec<C, Q>, StandardQuery.StandardGroupBySpec<C, Q>
        , StandardQuery.StandardWhereAndSpec<C, Q>, StandardQuery.StandardHavingSpec<C, Q>
        , StandardQuery.StandardOrderBySpec<C, Q>, StandardQuery.StandardLimitSpec<C, Q>
        , StandardQuery.StandardLockSpec<C, Q>, _StandardQuery {


    static <C> StandardSelectSpec<C, Select> query(@Nullable C criteria) {
        return new SimpleSelect<>(criteria);
    }

    static <C> StandardSelectSpec<C, SubQuery> subQuery(@Nullable C criteria) {
        return new SimpleSubQuery<>(criteria);
    }

    static <C, E> StandardSelectSpec<C, ScalarExpression> scalarSubQuery(@Nullable C criteria) {
        return new SimpleScalarSubQuery<>(criteria);
    }


    static <C, Q extends Query> StandardSelectSpec<C, Q> asQueryAndQuery(Q query, UnionType unionType) {
        final StandardSelectSpec<C, ?> spec;
        if (query instanceof Select) {
            spec = new UnionAndSelect<>((Select) query, unionType);
        } else if (query instanceof ScalarSubQuery) {
            spec = new UnionAndScalarSubQuery<>((ScalarExpression) query, unionType);
        } else if (query instanceof SubQuery) {
            spec = new UnionAndSubQuery<>((SubQuery) query, unionType);
        } else {
            throw _Exceptions.unknownQueryType(query);
        }
        return (StandardSelectSpec<C, Q>) spec;
    }


    private LockMode lockMode;

    StandardSimpleQuery(@Nullable C criteria) {
        super(CriteriaContexts.queryContext(criteria));
        if (this instanceof Select) {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        } else {
            CriteriaContextStack.push(this.criteriaContext);
        }
    }

    StandardSimpleQuery(CriteriaContext context) {
        super(context);
        if (!(this instanceof AbstractUnionAndQuery)) {
            throw new IllegalStateException("this error.");
        }
        if (this instanceof Select) {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        } else {
            CriteriaContextStack.push(this.criteriaContext);
        }

    }

    @Override
    public final StandardUnionClause<C, Q> lock(LockMode lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final StandardUnionClause<C, Q> lock(Function<C, LockMode> function) {
        final LockMode lockMode;
        lockMode = function.apply(this.criteria);
        Objects.requireNonNull(lockMode);
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final StandardUnionClause<C, Q> ifLock(@Nullable LockMode lockMode) {
        if (lockMode != null) {
            this.lockMode = lockMode;
        }
        return this;
    }

    @Override
    public final StandardUnionClause<C, Q> ifLock(Supplier<LockMode> supplier) {
        final LockMode lockMode;
        lockMode = supplier.get();
        if (lockMode != null) {
            this.lockMode = lockMode;
        }
        return this;
    }

    @Override
    public final StandardUnionClause<C, Q> ifLock(Function<C, LockMode> function) {
        final LockMode lockMode;
        lockMode = function.apply(this.criteria);
        if (lockMode != null) {
            this.lockMode = lockMode;
        }
        return this;
    }

    @Override
    public final StandardUnionSpec<C, Q> bracket() {
        final StandardUnionSpec<C, Q> unionSpec;
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
            right = StandardUnionQuery.bracketQuery(thisQuery)
                    .asQuery();
            unionSpec = StandardUnionQuery.unionQuery(andQuery.left, andQuery.unionType, right);
        } else {
            unionSpec = StandardUnionQuery.bracketQuery(this.asQuery());
        }
        return unionSpec;
    }


    @Override
    final StandardUnionSpec<C, Q> createUnionQuery(final Q left, final UnionType unionType, final Q right) {
        return StandardUnionQuery.unionQuery(left, unionType, right);
    }

    @Override
    final StandardSelectSpec<C, Q> asQueryAndQuery(final UnionType unionType) {
        return StandardSimpleQuery.asQueryAndQuery(this.asQuery(), unionType);
    }


    @Override
    final StandardJoinSpec<C, Q> addFirstTableBlock(TableMeta<?> table, String tableAlias) {
        this.criteriaContext.onFirstBlock(TableBlock.firstBlock(table, tableAlias));
        return this;
    }

    @Override
    final StandardJoinSpec<C, Q> addFirstTablePartBlock(TableItem tableItem, String alias) {
        this.criteriaContext.onFirstBlock(TableBlock.firstBlock(tableItem, alias));
        return this;
    }

    @Override
    final StandardOnSpec<C, Q> createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        return new OnBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    final StandardOnSpec<C, Q> createOnBlock(_JoinType joinType, TableItem tableItem, String alias) {
        return new OnBlock<>(joinType, tableItem, alias, this);
    }

    @Override
    final StandardOnSpec<C, Q> createNoActionTableBlock() {
        return new NoActionOnBlock<>(this);
    }

    @Override
    final StandardOnSpec<C, Q> createNoActionOnBlock() {
        return new NoActionOnBlock<>(this);
    }


    @Override
    final Dialect defaultDialect() {
        return Dialect.MySQL57;
    }

    @Override
    final void validateDialect(Dialect mode) {
        // no-op
    }

    @SuppressWarnings("unchecked")
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
            resultQuery = StandardUnionQuery.unionQuery(unionAndQuery.left, unionAndQuery.unionType, thisQuery)
                    .asQuery();
        } else {
            resultQuery = thisQuery;
        }
        return resultQuery;
    }


    @Override
    final void onClear() {
        this.lockMode = null;
    }


    @Override
    public final LockMode lockMode() {
        return this.lockMode;
    }




    /*################################## blow private inter class method ##################################*/


    private static final class NoActionOnBlock<C, Q extends Query> extends NoActionOnClause<C, StandardJoinSpec<C, Q>>
            implements StandardOnSpec<C, Q> {

        private NoActionOnBlock(StandardJoinSpec<C, Q> joinSpec) {
            super(joinSpec);
        }

    }// NoActionOnBlock

    private static final class OnBlock<C, Q extends Query> extends OnClauseTableBlock<C, StandardJoinSpec<C, Q>>
            implements StandardOnSpec<C, Q> {

        private final StandardSimpleQuery<C, Q> query;

        OnBlock(_JoinType joinType, TableItem tableItem, String alias, StandardSimpleQuery<C, Q> query) {
            super(joinType, tableItem, alias);
            this.query = query;
        }

        @Override
        CriteriaContext getCriteriaContext() {
            return this.query.criteriaContext;
        }

        @Override
        StandardJoinSpec<C, Q> endOnClause() {
            return this.query;
        }

    } // OnBlock


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

        @Override
        public final void appendSql(final _SqlContext context) {
            context.dialect().subQuery(this, context);
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

        @Override
        public Selection selection() {
            return (Selection) this.selectItemList().get(0);
        }

        @Override
        public ParamMeta paramMeta() {
            return this.selection().paramMeta();
        }


    }// SimpleScalarSubQuery


    private static abstract class AbstractUnionAndQuery<C, Q extends Query> extends StandardSimpleQuery<C, Q> {

        private final Q left;

        private final UnionType unionType;

        AbstractUnionAndQuery(Q left, UnionType unionType) {
            super(CriteriaContexts.unionAndContext(left));
            this.left = left;
            this.unionType = unionType;
        }


    }//AbstractUnionAndQuery


    private static final class UnionAndSelect<C> extends AbstractUnionAndQuery<C, Select>
            implements Select {

        private UnionAndSelect(Select left, UnionType unionType) {
            super(left, unionType);
        }

    } // UnionAndSelect


    private static class UnionAndSubQuery<C, Q extends SubQuery> extends AbstractUnionAndQuery<C, Q>
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

        @Override
        public final void appendSql(final _SqlContext context) {
            context.dialect().subQuery(this, context);
        }

    }// UnionAndSubQuery


    private static final class UnionAndScalarSubQuery<C> extends UnionAndSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private UnionAndScalarSubQuery(ScalarExpression left, UnionType unionType) {
            super(left, unionType);
        }

        @Override
        public Selection selection() {
            return (Selection) this.selectItemList().get(0);
        }

        @Override
        public ParamMeta paramMeta() {
            return this.selection().paramMeta();
        }


    }// UnionAndScalarSubQuery


}
