package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._StandardQuery;
import io.army.criteria.impl.inner._TableBlock;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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
abstract class StandardQueries<I extends Item> extends SimpleQueries<
        I,
        SQLs.SelectModifier,
        StandardQuery._FromSpec<I>, // SR
        StandardQuery._JoinSpec<I>,// FT
        StandardQuery._JoinSpec<I>,// FS
        Void,                          //FC
        Statement._OnClause<StandardQuery._JoinSpec<I>>, // JT
        Statement._OnClause<StandardQuery._JoinSpec<I>>, // JS
        Void,                               // JC
        StandardQuery._GroupBySpec<I>, // WR
        StandardQuery._WhereAndSpec<I>, // AR
        StandardQuery._HavingSpec<I>, // GR
        StandardQuery._OrderBySpec<I>, // HR
        StandardQuery._LimitSpec<I>, // OR
        StandardQuery._UnionAndQuerySpec<I>> // SP

        implements StandardQuery, StandardQuery._SelectSpec<I>, StandardQuery._FromSpec<I>
        , StandardQuery._JoinSpec<I>, StandardQuery._WhereAndSpec<I>, StandardQuery._HavingSpec<I>
        , _StandardQuery {


    static <Q extends Item> SimpleSelect<Q> primaryQuery(Function<Select, Q> function) {
        return new SimpleSelect<>(function);
    }

    static <Q extends Item> StandardQuery._ParenQueryClause<Q> parenPrimaryQuery(Function<Select, Q> function) {
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
    public final _LockSpec<I> limit(long rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final _LockSpec<I> limit(Supplier<? extends Number> supplier) {
        this.rowCount = CriteriaUtils.asLimitParam(this.context, supplier.get());
        return this;
    }

    @Override
    public final _LockSpec<I> limit(Function<String, ?> function, String keyName) {
        this.rowCount = CriteriaUtils.asLimitParam(this.context, function.apply(keyName));
        return this;
    }

    @Override
    public final _LockSpec<I> ifLimit(Supplier<? extends Number> supplier) {
        this.rowCount = CriteriaUtils.asIfLimitParam(this.context, supplier.get());
        return this;
    }


    @Override
    public final _LockSpec<I> ifLimit(Function<String, ?> function, String keyName) {
        this.rowCount = CriteriaUtils.asIfLimitParam(this.context, function.apply(keyName));
        return this;
    }


    @Override
    public final _LockSpec<I> limit(long offset, long rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final _LockSpec<I> limit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier) {
        CriteriaUtils.limitPair(this.context, offsetSupplier.get(), rowCountSupplier.get(), this::limit);
        return this;
    }

    @Override
    public final _LockSpec<I> limit(Function<String, ?> function, String offsetKey, String rowCountKey) {
        CriteriaUtils.limitPair(this.context, function.apply(offsetKey), function.apply(rowCountKey), this::limit);
        return this;
    }

    @Override
    public final _LockSpec<I> limit(Consumer<BiConsumer<Long, Long>> consumer) {
        consumer.accept(this::limit);
        if (this.offset < 0) {
            throw CriteriaUtils.limitParamError(this.context, this.offset);
        } else if (this.rowCount < 0) {
            throw CriteriaUtils.limitParamError(this.context, this.rowCount);
        }
        return this;
    }

    @Override
    public final _LockSpec<I> ifLimit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier) {
        CriteriaUtils.ifLimitPair(this.context, offsetSupplier.get(), rowCountSupplier.get(), this::limit);
        return this;
    }

    @Override
    public final _LockSpec<I> ifLimit(Function<String, ?> function, String offsetKey, String rowCountKey) {
        CriteriaUtils.ifLimitPair(this.context, function.apply(offsetKey), function.apply(rowCountKey), this::limit);
        return this;
    }

    @Override
    public _LockSpec<I> ifLimit(Consumer<BiConsumer<Long, Long>> consumer) {
        consumer.accept(this::limit);
        return this;
    }

    @Override
    public final _QuerySpec<I> lock(@Nullable LockMode lockMode) {
        if (lockMode == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final _QuerySpec<I> ifLock(Supplier<LockMode> supplier) {
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
    final _OnClause<_JoinSpec<I>> createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
        if (itemWord != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    final _OnClause<_JoinSpec<I>> createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
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


    private static final class UnionLeftParenSubQueryClause<I extends Item, U extends Item>
            extends SelectClauseDispatcher<SQLs.SelectModifier, StandardQuery._FromSpec<U>>
            implements _UnionAndQuerySpec<U> {
        private final Function<SubQuery, I> function;

        private UnionLeftParenSubQueryClause(Function<SubQuery, I> function) {
            this.function = function;
        }

        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<U>>> leftParen() {
            return new UnionLeftParenSubQueryClause<>(this.function);
        }

        @Override
        _DynamicHintModifierSelectClause<SQLs.SelectModifier, _FromSpec<U>> createSelectClause() {
            return null;
        }


    }//UnionLeftParenSubQueryClause


    private static abstract class StandardParenthesizedQueries<I extends Item, Q extends Query>
            extends ParenthesizedRowSet<
            I,
            Q,
            StandardQuery._UnionOrderBySpec<I>,
            StandardQuery._UnionLimitSpec<I>,
            StandardQuery._UnionAndQuerySpec<I>,
            RowSet,
            Void> implements StandardQuery._UnionOrderBySpec<I>
            , Statement._RightParenClause<_UnionOrderBySpec<I>> {


        private StandardParenthesizedQueries(CriteriaContext context) {
            super(context);
        }

        @Override
        public final _QuerySpec<I> limit(long rowCount) {
            if (rowCount < 0) {
                throw CriteriaUtils.limitParamError(this.context, rowCount);
            }
            this.updateLimitCount(rowCount);
            return this;
        }

        @Override
        public final _QuerySpec<I> limit(Supplier<? extends Number> supplier) {
            this.updateLimitCount(CriteriaUtils.asLimitParam(this.context, supplier.get()));
            return this;
        }

        @Override
        public final _QuerySpec<I> limit(Function<String, ?> function, String keyName) {
            this.updateLimitCount(CriteriaUtils.asLimitParam(this.context, function.apply(keyName)));
            return this;
        }

        @Override
        public final _QuerySpec<I> ifLimit(Supplier<? extends Number> supplier) {
            this.updateLimitCount(CriteriaUtils.asIfLimitParam(this.context, supplier.get()));
            return this;
        }

        @Override
        public final _QuerySpec<I> ifLimit(Function<String, ?> function, String keyName) {
            this.updateLimitCount(CriteriaUtils.asIfLimitParam(this.context, function.apply(keyName)));
            return this;
        }

        @Override
        public final _QuerySpec<I> limit(final long offset, final long rowCount) {
            if (offset < 0) {
                throw CriteriaUtils.limitParamError(this.context, offset);
            } else if (rowCount < 0) {
                throw CriteriaUtils.limitParamError(this.context, rowCount);
            }
            this.updateLimitClause(offset, rowCount);
            return this;
        }

        @Override
        public final _QuerySpec<I> limit(Supplier<? extends Number> offsetSupplier, Supplier<? extends Number> rowCountSupplier) {
            CriteriaUtils.limitPair(this.context, offsetSupplier.get(), rowCountSupplier.get(), this::limit);
            return this;
        }

        @Override
        public final _QuerySpec<I> limit(Function<String, ?> function, String offsetKey, String rowCountKey) {
            CriteriaUtils.limitPair(this.context, function.apply(offsetKey), function.apply(rowCountKey), this::limit);
            return this;
        }

        @Override
        public _QuerySpec<I> limit(Consumer<BiConsumer<Long, Long>> consumer) {
            consumer.accept(this::limit);
            return this;
        }

        @Override
        public final _QuerySpec<I> ifLimit(Supplier<? extends Number> offsetSupplier
                , Supplier<? extends Number> rowCountSupplier) {
            CriteriaUtils.ifLimitPair(this.context, offsetSupplier.get(), rowCountSupplier.get(), this::limit);
            return this;
        }

        @Override
        public final _QuerySpec<I> ifLimit(Function<String, ?> function, String offsetKey, String rowCountKey) {
            CriteriaUtils.ifLimitPair(this.context, function.apply(offsetKey), function.apply(rowCountKey), this::limit);
            return this;
        }

        @Override
        public final _QuerySpec<I> ifLimit(Consumer<BiConsumer<Long, Long>> consumer) {
            consumer.accept(this::limit);
            return this;
        }


        @Override
        final Void createRowSetUnion(UnionType unionType, RowSet right) {
            //standard query don't support union VALUES statement
            throw ContextStack.castCriteriaApi(this.context);
        }


    }//StandardParenthesizedQueries

    private static final class StandardParenthesizedSubQuery<I extends Item>
            extends StandardParenthesizedQueries<I, SubQuery>
            implements SubQuery {

        private final Function<SubQuery, I> function;

        private StandardParenthesizedSubQuery(CriteriaContext context, Function<SubQuery, I> function) {
            super(context);
            this.function = function;
        }


    }//StandardParenthesizedSubQuery


    private static final class UnionSubQueryClause<Q extends Item>
            extends SelectClauseDispatcher<SQLs.SelectModifier, StandardQuery._FromSpec<Q>>
            implements _UnionAndQuerySpec<Q> {

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
            return new UnionLeftParenSubQueryClause<>(this::union);
        }


        @Override
        _DynamicHintModifierSelectClause<SQLs.SelectModifier, _FromSpec<Q>> createSelectClause() {
            final CriteriaContext leftContext, newContext;
            leftContext = ((CriteriaContextSpec) this.left).getContext();

            newContext = CriteriaContexts.unionSubQueryContext(leftContext);
            return StandardQueries.subQuery(newContext, this::union);
        }

        private Q union(final SubQuery right) {
            return this.function.apply(new UnionSubQuery(this.left, this.unionType, right));
        }


    }//UnionSubQueryClause


}
