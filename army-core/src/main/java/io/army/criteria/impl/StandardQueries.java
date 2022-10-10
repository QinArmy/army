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


    static SimpleSelect<Select> primaryQuery() {
        // primary no outer context
        return new SimpleSelect<>(CriteriaContexts.queryContext(null), SQLs::_identity);
    }

    static StandardQuery._ParenQueryClause<Select> parenPrimaryQuery() {
        return new ParenSelect();
    }


    static <Q extends Item> SimpleSubQuery<Q> subQuery(CriteriaContext outerContext
            , Function<SubQuery, Q> function) {
        return new SimpleSubQuery<>(CriteriaContexts.subQueryContext(outerContext), function);
    }

    static <I extends Item> StandardQuery._ParenQuerySpec<I> parenSubQuery(CriteriaContext outerContext
            , Function<SubQuery, I> function) {
        return new ParenSubQuery<>(outerContext, function);
    }


    private static void assertUnionType(final CriteriaContext context, final UnionType unionType) {
        switch (unionType) {
            case UNION:
            case UNION_ALL:
            case UNION_DISTINCT:
                break;
            default:
                throw ContextStack.castCriteriaApi(context);
        }
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
        private SimpleSelect(CriteriaContext context, Function<Select, Q> function) {
            super(context);
            this.function = function;
        }


        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(MySQLDialect.MySQL57, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


        @Override
        Q onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _UnionAndQuerySpec<Q> createQueryUnion(final UnionType unionType) {
            StandardQueries.assertUnionType(this.context, unionType);
            return new UnionAndSelectClause<>(this, unionType, this.function);
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
        _UnionAndQuerySpec<Q> createQueryUnion(final UnionType unionType) {
            StandardQueries.assertUnionType(this.context, unionType);
            return new UnionAndSubQueryClause<>(this, unionType, this.function);
        }


    } // SimpleSubQuery


    private static abstract class StandardBracketQueries<I extends Item, Q extends Query>
            extends BracketRowSet<
            I,
            Q,
            _UnionOrderBySpec<I>,
            _UnionLimitSpec<I>,
            _UnionAndQuerySpec<I>,
            RowSet,
            Void> implements StandardQuery._UnionOrderBySpec<I>
            , Statement._RightParenClause<_UnionOrderBySpec<I>> {

        private long offset;

        private long rowCount;

        private StandardBracketQueries(CriteriaContext context) {
            super(context);
        }

        @Override
        public final _QuerySpec<I> limit(long rowCount) {
            if (rowCount < 0) {
                throw CriteriaUtils.limitParamError(this.context, rowCount);
            }
            this.rowCount = rowCount;
            return this;
        }

        @Override
        public final _QuerySpec<I> limit(Supplier<? extends Number> supplier) {
            this.rowCount = CriteriaUtils.asLimitParam(this.context, supplier.get());
            return this;
        }

        @Override
        public final _QuerySpec<I> limit(Function<String, ?> function, String keyName) {
            this.rowCount = CriteriaUtils.asLimitParam(this.context, function.apply(keyName));
            return this;
        }

        @Override
        public final _QuerySpec<I> ifLimit(Supplier<? extends Number> supplier) {
            this.rowCount = CriteriaUtils.asIfLimitParam(this.context, supplier.get());
            return this;
        }

        @Override
        public final _QuerySpec<I> ifLimit(Function<String, ?> function, String keyName) {
            this.rowCount = CriteriaUtils.asIfLimitParam(this.context, function.apply(keyName));
            return this;
        }

        @Override
        public final _QuerySpec<I> limit(final long offset, final long rowCount) {
            if (offset < 0) {
                throw CriteriaUtils.limitParamError(this.context, offset);
            } else if (rowCount < 0) {
                throw CriteriaUtils.limitParamError(this.context, rowCount);
            }
            this.offset = offset;
            this.rowCount = rowCount;
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
        public final _QuerySpec<I> limit(Consumer<BiConsumer<Long, Long>> consumer) {
            consumer.accept(this::limit);
            if (this.rowCount < 0) {
                throw CriteriaUtils.limitParamError(this.context, this.rowCount);
            } else if (this.offset < 0) {
                throw CriteriaUtils.limitParamError(this.context, this.offset);
            }
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

        @Override
        public final long offset() {
            return this.offset;
        }

        @Override
        public final long rowCount() {
            return this.rowCount;
        }


    }//StandardParenthesizedQueries


    private static final class StandardBracketSelect<I extends Item>
            extends StandardBracketQueries<I, Select>
            implements Select {

        private final Function<Select, I> function;

        private StandardBracketSelect(CriteriaContext context, Function<Select, I> function) {
            super(context);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _UnionAndQuerySpec<I> createQueryUnion(final UnionType unionType) {
            StandardQueries.assertUnionType(this.context, unionType);
            return new UnionAndSelectClause<>(this, unionType, this.function);
        }

        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(MySQLDialect.MySQL57, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


    }//StandardBracketSelect

    private static final class StandardBracketSubQuery<I extends Item>
            extends StandardBracketQueries<I, SubQuery>
            implements SubQuery {

        private final Function<SubQuery, I> function;

        private StandardBracketSubQuery(CriteriaContext context, Function<SubQuery, I> function) {
            super(context);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _UnionAndQuerySpec<I> createQueryUnion(final UnionType unionType) {
            StandardQueries.assertUnionType(this.context, unionType);
            return new UnionAndSubQueryClause<>(this, unionType, this.function);
        }


    }//StandardBracketSubQuery


    /**
     * @see #parenPrimaryQuery()
     */
    private static final class ParenSelect implements StandardQuery._ParenQueryClause<Select> {

        private ParenSelect() {
        }

        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<Select>>> leftParen() {
            final StandardBracketSelect<Select> bracket;
            bracket = new StandardBracketSelect<>(CriteriaContexts.bracketContext(null), SQLs::_identity);
            return new UnionLeftParenSelectClause<>(bracket);
        }

    }//ParenSelect

    /**
     * @see #parenSubQuery(CriteriaContext, Function)
     */
    private static final class ParenSubQuery<I extends Item>
            extends SelectClauseDispatcher<SQLs.SelectModifier, StandardQuery._FromSpec<I>>
            implements StandardQuery._ParenQuerySpec<I> {

        private final CriteriaContext outerContext;

        private final Function<SubQuery, I> function;


        private ParenSubQuery(CriteriaContext outerContext, Function<SubQuery, I> function) {
            this.outerContext = outerContext;
            this.function = function;
        }

        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final StandardBracketSubQuery<I> bracket;
            bracket = new StandardBracketSubQuery<>(CriteriaContexts.bracketContext(this.outerContext), this.function);
            return new UnionLeftParenSubQueryClause<>(bracket);
        }

        @Override
        _DynamicHintModifierSelectClause<SQLs.SelectModifier, _FromSpec<I>> createSelectClause() {
            return new SimpleSubQuery<>(CriteriaContexts.subQueryContext(this.outerContext), this.function);
        }


    }//ParenSubQuery

    private static final class UnionLeftParenSelectClause<I extends Item>
            extends SelectClauseDispatcher<SQLs.SelectModifier, StandardQuery._FromSpec<_RightParenClause<_UnionOrderBySpec<I>>>>
            implements _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<I>>> {

        private final StandardBracketSelect<I> bracket;

        private UnionLeftParenSelectClause(StandardBracketSelect<I> bracket) {
            this.bracket = bracket;
        }

        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<_RightParenClause<_UnionOrderBySpec<I>>>>> leftParen() {
            final CriteriaContext context;
            context = CriteriaContexts.bracketContext(this.bracket.context);

            final StandardBracketSelect<_RightParenClause<_UnionOrderBySpec<I>>> newBracket;
            newBracket = new StandardBracketSelect<>(context, this.bracket::parenRowSetEnd);
            return new UnionLeftParenSelectClause<>(newBracket);
        }

        @Override
        _DynamicHintModifierSelectClause<SQLs.SelectModifier, _FromSpec<_RightParenClause<_UnionOrderBySpec<I>>>> createSelectClause() {
            final CriteriaContext context;
            context = CriteriaContexts.queryContext(this.bracket.context);
            return new SimpleSelect<>(context, this.bracket::parenRowSetEnd);
        }


    }//UnionLeftParenSelectClause

    private static final class UnionAndSelectClause<I extends Item>
            extends SelectClauseDispatcher<SQLs.SelectModifier, StandardQuery._FromSpec<I>>
            implements _UnionAndQuerySpec<I> {

        private final Select left;

        private final UnionType unionType;

        private final Function<Select, I> function;

        private UnionAndSelectClause(Select left, UnionType unionType, Function<Select, I> function) {
            this.left = left;
            this.unionType = unionType;
            this.function = function;
        }

        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext leftContext, context;
            leftContext = ((CriteriaContextSpec) this.left).getContext();
            context = CriteriaContexts.unionBracketContext(leftContext);

            final StandardBracketSelect<I> select;
            select = new StandardBracketSelect<>(context, this::unionRight);
            return new UnionLeftParenSelectClause<>(select);
        }

        @Override
        _DynamicHintModifierSelectClause<SQLs.SelectModifier, _FromSpec<I>> createSelectClause() {
            final CriteriaContext leftContext, context;
            leftContext = ((CriteriaContextSpec) this.left).getContext();

            context = CriteriaContexts.unionSelectContext(leftContext);
            return new SimpleSelect<>(context, this::unionRight);
        }

        private I unionRight(final Select right) {
            return this.function.apply(new UnionSelect(MySQLDialect.MySQL57, this.left, this.unionType, right));
        }


    }//UnionSelectClause


    private static final class UnionLeftParenSubQueryClause<I extends Item>
            extends SelectClauseDispatcher<SQLs.SelectModifier, StandardQuery._FromSpec<_RightParenClause<_UnionOrderBySpec<I>>>>
            implements _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<I>>> {
        private final StandardBracketSubQuery<I> outerBracket;

        private UnionLeftParenSubQueryClause(StandardBracketSubQuery<I> outerBracket) {
            this.outerBracket = outerBracket;
        }

        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<_RightParenClause<_UnionOrderBySpec<I>>>>> leftParen() {
            final CriteriaContext context;
            context = CriteriaContexts.bracketContext(this.outerBracket.context);

            final StandardBracketSubQuery<_RightParenClause<_UnionOrderBySpec<I>>> subQuery;
            subQuery = new StandardBracketSubQuery<>(context, this.outerBracket::parenRowSetEnd);
            return new UnionLeftParenSubQueryClause<>(subQuery);
        }

        @Override
        _DynamicHintModifierSelectClause<SQLs.SelectModifier, _FromSpec<_RightParenClause<_UnionOrderBySpec<I>>>> createSelectClause() {
            final CriteriaContext context;
            context = CriteriaContexts.subQueryContext(this.outerBracket.context);
            return new SimpleSubQuery<>(context, this.outerBracket::parenRowSetEnd);
        }


    }//UnionLeftParenSubQueryClause


    private static final class UnionAndSubQueryClause<I extends Item>
            extends SelectClauseDispatcher<SQLs.SelectModifier, StandardQuery._FromSpec<I>>
            implements _UnionAndQuerySpec<I> {

        private final SubQuery left;

        private final UnionType unionType;

        private final Function<SubQuery, I> function;

        private UnionAndSubQueryClause(SubQuery left, UnionType unionType, Function<SubQuery, I> function) {
            this.left = left;
            this.unionType = unionType;
            this.function = function;
        }

        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext leftContext, context;
            leftContext = ((CriteriaContextSpec) this.left).getContext();
            context = CriteriaContexts.unionBracketContext(leftContext);

            final StandardBracketSubQuery<I> subQuery;
            subQuery = new StandardBracketSubQuery<>(context, this::unionRight);
            return new UnionLeftParenSubQueryClause<>(subQuery);
        }


        @Override
        _DynamicHintModifierSelectClause<SQLs.SelectModifier, _FromSpec<I>> createSelectClause() {
            final CriteriaContext leftContext, context;
            leftContext = ((CriteriaContextSpec) this.left).getContext();

            context = CriteriaContexts.unionSubQueryContext(leftContext);
            return new SimpleSubQuery<>(context, this::unionRight);
        }

        private I unionRight(final SubQuery right) {
            return this.function.apply(new UnionSubQuery(this.left, this.unionType, right));
        }


    }//UnionAndSubQueryClause


}
