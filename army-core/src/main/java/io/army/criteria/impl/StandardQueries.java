package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._StandardQuery;
import io.army.criteria.impl.inner._TableBlock;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is a implementation of {@link StandardQuery}.
 * </p>
 *
 * @see StandardUnionQueries
 * @since 1.0
 */
abstract class StandardQueries<Q extends Item> extends SimpleQueries<
        Q,
        SQLs.SelectModifier,
        StandardQuery._FromSpec<Q>, // SR
        StandardQuery._JoinSpec<Q>,// FT
        StandardQuery._JoinSpec<Q>,// FS
        Void,                          //FC
        Statement._OnClause<StandardQuery._JoinSpec<Q>>, // JT
        Statement._OnClause<StandardQuery._JoinSpec<Q>>, // JS
        Void,                               // JC
        StandardQuery._GroupBySpec<Q>, // WR
        StandardQuery._WhereAndSpec<Q>, // AR
        StandardQuery._HavingSpec<Q>, // GR
        StandardQuery._OrderBySpec<Q>, // HR
        StandardQuery._LimitSpec<Q>, // OR
        StandardQuery._UnionAndQuerySpec<Q>> // SP

        implements StandardQuery, StandardQuery._SelectSpec<Q>, StandardQuery._FromSpec<Q>
        , StandardQuery._JoinSpec<Q>, StandardQuery._WhereAndSpec<Q>, StandardQuery._HavingSpec<Q>
        , _StandardQuery {


    static <C, Q extends Item> SimpleSelect<Q> primaryQuery(Function<Select, Q> function) {
        return new SimpleSelect<>(function);
    }

    static <C, Q extends Item> StandardQuery._ParenQueryClause<Q> parenPrimaryQuery(Function<Select, Q> function) {
        throw new UnsupportedOperationException();
    }


    static <C, Q extends Item> SimpleSubQuery<Q> subQuery(CriteriaContext outerContext
            , Function<SubQuery, Q> function) {
        return new SimpleSubQuery<>(CriteriaContexts.subQueryContext(outerContext), function);
    }

    static <C, Q extends Item> StandardQuery._ParenQueryClause<Q> parenSubQuery(CriteriaContext outerContext
            , Function<SubQuery, Q> function) {
        throw new UnsupportedOperationException();
    }


    private LockMode lockMode;

    private long offset;

    private long rowCount;

    private StandardQueries(CriteriaContext context) {
        super(context);

    }


    @Override
    public final _LockSpec<Q> limit(long rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final _LockSpec<Q> limit(Supplier<? extends Number> supplier) {
        this.rowCount = CriteriaUtils.asLimitParam(this.context, supplier.get());
        return this;
    }

    @Override
    public final _LockSpec<Q> limit(Function<String, ?> function, String keyName) {
        this.rowCount = CriteriaUtils.asLimitParam(this.context, function.apply(keyName));
        return this;
    }

    @Override
    public final _LockSpec<Q> ifLimit(Supplier<? extends Number> supplier) {
        this.rowCount = CriteriaUtils.asIfLimitParam(this.context, supplier.get());
        return this;
    }


    @Override
    public final _LockSpec<Q> ifLimit(Function<String, ?> function, String keyName) {
        this.rowCount = CriteriaUtils.asIfLimitParam(this.context, function.apply(keyName));
        return this;
    }


    @Override
    public final _LockSpec<Q> limit(long offset, long rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final _LockSpec<Q> limit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier) {
        CriteriaUtils.limitPair(this.context, offsetSupplier.get(), rowCountSupplier.get(), this::limit);
        return this;
    }

    @Override
    public final _LockSpec<Q> limit(Function<String, ?> function, String offsetKey, String rowCountKey) {
        CriteriaUtils.limitPair(this.context, function.apply(offsetKey), function.apply(rowCountKey), this::limit);
        return this;
    }

    @Override
    public final _LockSpec<Q> ifLimit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier) {
        CriteriaUtils.ifLimitPair(this.context, offsetSupplier.get(), rowCountSupplier.get(), this::limit);
        return this;
    }

    @Override
    public final _LockSpec<Q> ifLimit(Function<String, ?> function, String offsetKey, String rowCountKey) {
        CriteriaUtils.ifLimitPair(this.context, function.apply(offsetKey), function.apply(rowCountKey), this::limit);
        return this;
    }

    @Override
    public final _UnionSpec<Q> lock(@Nullable LockMode lockMode) {
        if (lockMode == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final _UnionSpec<Q> ifLock(Supplier<LockMode> supplier) {
        this.lockMode = supplier.get();
        return this;
    }

    @Override
    public final String toString() {
        final String s;
        if (this instanceof Select && this.isPrepared()) {
            s = this.mockAsString(MySQLDialect.MySQL57, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }


    @Override
    public final LockMode lockMode() {
        return this.lockMode;
    }


    @Override
    final List<Hint> asHintList(@Nullable List<Hint> hints) {
        //standard statement don't hints
        throw ContextStack.castCriteriaApi(this.context);
    }

    @Override
    final List<SQLs.SelectModifier> asModifierList(final @Nullable List<SQLs.SelectModifier> modifiers) {
        if (modifiers == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return CriteriaUtils.asModifierList(this.context, modifiers, CriteriaUtils::standardModifier);
    }

    @Override
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String alias) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new TableBlock.NoOnTableBlock(joinType, table, alias);
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new TableBlock.NoOnTableBlock(joinType, tableItem, alias);
    }

    @Override
    final _OnClause<_JoinSpec<Q>> createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    final _OnClause<_JoinSpec<Q>> createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
    }


    @Override
    final Void createCteBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
        throw ContextStack.castCriteriaApi(this.context);
    }


    @Override
    final void onEndQuery() {
        //no-op
    }


    @Override
    public final long offset() {
        return this.offset;
    }

    @Override
    public final long rowCount() {
        return this.rowCount;
    }

    @Override
    final void onClear() {
        this.lockMode = null;
    }




    /*################################## blow private inter class method ##################################*/


    static class SimpleSelect<Q extends Item> extends StandardQueries<Q>
            implements Select {

        private final Function<Select, Q> function;

        /**
         * <p>
         * Primary constructor
         * </p>
         */
        private SimpleSelect(Function<Select, Q> function) {
            super(CriteriaContexts.primaryQueryContext());
            this.function = function;
        }

        @Override
        Q onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _UnionAndQuerySpec<Q> createQueryUnion(UnionType unionType) {
            return null;
        }


    }//SimpleSelect


    static class SimpleSubQuery<Q extends Item> extends StandardQueries<Q>
            implements SubQuery {

        private final Function<SubQuery, Q> function;

        private SimpleSubQuery(CriteriaContext context, Function<SubQuery, Q> function) {
            super(context);
            this.function = function;
        }

        @Override
        Q onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _UnionAndQuerySpec<Q> createQueryUnion(UnionType unionType) {
            return new UnionSubQueryClause<>(this, unionType, this.function);
        }


    } // SimpleSubQuery


    private static final class UnionSelectClause<Q extends Item>
            extends SelectClauseDispatcher<SQLs.SelectModifier, StandardQuery._FromSpec<Q>>
            implements _UnionAndQuerySpec<Q> {


        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<Q>>> leftParen() {
            return null;
        }

        @Override
        _DynamicHintModifierSelectClause<SQLs.SelectModifier, _FromSpec<Q>> createSelectClause() {
            return null;
        }


    }//UnionSelectClause


    private static final class UnionLeftParenSubQueryClause<Q extends Item>
            extends SelectClauseDispatcher<SQLs.SelectModifier, StandardQuery._FromSpec<Q>>
            implements _UnionAndQuerySpec<Q>, _RightParenClause<_UnionOrderBySpec<Q>> {

        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<Q>>> leftParen() {
            return null;
        }

        @Override
        public _UnionOrderBySpec<Q> rightParen() {
            return null;
        }

        @Override
        _DynamicHintModifierSelectClause<SQLs.SelectModifier, _FromSpec<Q>> createSelectClause() {
            return null;
        }

    }//UnionLeftParenSubQueryClause


    private static final class UnionSubQueryClause<Q extends Item>
            extends SelectClauseDispatcher<SQLs.SelectModifier, StandardQuery._FromSpec<Q>>
            implements _UnionAndQuerySpec<Q>, _RightParenClause<_UnionOrderBySpec<Q>> {

        private final SubQuery left;

        private final UnionType unionType;

        private final Function<SubQuery, Q> function;

        private UnionSubQueryClause(SubQuery left, UnionType unionType, Function<SubQuery, Q> function) {
            this.left = left;
            this.unionType = unionType;
            this.function = function;
        }

        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<Q>>> leftParen() {
            return null;
        }

        @Override
        public _UnionOrderBySpec<Q> rightParen() {
            return null;
        }

        @Override
        _DynamicHintModifierSelectClause<SQLs.SelectModifier, _FromSpec<Q>> createSelectClause() {
            final CriteriaContext leftContext, newContext;
            leftContext = ((CriteriaContextSpec) this.left).getContext();

            newContext = CriteriaContexts.unionSubQueryContext(leftContext);
            return new SimpleSubQuery<>(newContext, this::union);
        }


        private Q union(final SubQuery right) {
            return StandardUnionQueries.unionSubQuery(this.left, this.unionType, right, this.function)
                    .asQuery();
        }


    }//UnionSubQueryClause


}
