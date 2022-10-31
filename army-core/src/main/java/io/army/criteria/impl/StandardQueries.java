package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._StandardQuery;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.standard.StandardCrosses;
import io.army.criteria.standard.StandardJoins;
import io.army.criteria.standard.StandardQuery;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._StringUtils;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a implementation of {@link StandardQuery}.
 * </p>
 *
 * @since 1.0
 */
abstract class StandardQueries<I extends Item> extends SimpleQueries<
        I,
        StandardSyntax.Modifier,
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
        StandardQuery._LockSpec<I>, // LR
        Object,
        Object,
        StandardQuery._SelectSpec<I>> // SP

        implements StandardQuery, StandardQuery._SelectSpec<I>, StandardQuery._FromSpec<I>
        , StandardQuery._JoinSpec<I>, StandardQuery._WhereAndSpec<I>, StandardQuery._HavingSpec<I>
        , _StandardQuery {


    static <I extends Item> _SelectSpec<I> primaryQuery(Function<Select, I> function) {
        // primary no outer context
        return new SimpleSelect<>(CriteriaContexts.primaryQuery(null), function);
    }

    static <Q extends Item> _SelectSpec<Q> subQuery(CriteriaContext outerContext
            , Function<SubQuery, Q> function) {
        return new SimpleSubQuery<>(CriteriaContexts.subQueryContext(outerContext), function);
    }



    private StandardLockMode lockMode;


    private StandardQueries(CriteriaContext context) {
        super(context);
    }

    @Override
    public final _NestedLeftParenSpec<_JoinSpec<I>> from() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.NONE, this::nestedNonCrossEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> leftJoin() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.LEFT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> join() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> rightJoin() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.RIGHT_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> fullJoin() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.FULL_JOIN, this::nestedJoinEnd);
    }

    @Override
    public final _NestedLeftParenSpec<_JoinSpec<I>> crossJoin() {
        return StandardNestedJoins.nestedItem(this.context, _JoinType.CROSS_JOIN, this::nestedNonCrossEnd);
    }

    @Override
    public final _JoinSpec<I> ifLeftJoin(Consumer<StandardJoins> consumer) {
        consumer.accept(StandardDynamicJoins.joinBuilder(this.context, _JoinType.LEFT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifJoin(Consumer<StandardJoins> consumer) {
        consumer.accept(StandardDynamicJoins.joinBuilder(this.context, _JoinType.JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifRightJoin(Consumer<StandardJoins> consumer) {
        consumer.accept(StandardDynamicJoins.joinBuilder(this.context, _JoinType.RIGHT_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifFullJoin(Consumer<StandardJoins> consumer) {
        consumer.accept(StandardDynamicJoins.joinBuilder(this.context, _JoinType.FULL_JOIN, this.blockConsumer));
        return this;
    }

    @Override
    public final _JoinSpec<I> ifCrossJoin(Consumer<StandardCrosses> consumer) {
        consumer.accept(StandardDynamicJoins.crossBuilder(this.context, this.blockConsumer));
        return this;
    }

    @Override
    public final _AsQueryClause<I> forUpdate() {
        this.lockMode = StandardLockMode.FOR_UPDATE;
        return this;
    }

    @Override
    public final _AsQueryClause<I> ifForUpdate(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.lockMode = StandardLockMode.FOR_UPDATE;
        } else {
            this.lockMode = null;
        }
        return this;
    }

    @Override
    public final SQLWords lockMode() {
        return this.lockMode;
    }

    @Override
    final List<Hint> asHintList(@Nullable List<Hint> hints) {
        //standard statement don't hints
        throw ContextStack.castCriteriaApi(this.context);
    }


    @Override
    final List<StandardSyntax.Modifier> asModifierList(final @Nullable List<StandardSyntax.Modifier> modifiers) {
        if (modifiers == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return CriteriaUtils.asModifierList(this.context, modifiers, CriteriaUtils::standardModifier);
    }

    @Override
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table
            , String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new TableBlock.NoOnTableBlock(joinType, table, alias);
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable TabularModifier modifier, TabularItem tableItem
            , String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new TableBlock.NoOnTableBlock(joinType, tableItem, alias);
    }

    @Override
    final _OnClause<_JoinSpec<I>> createTableBlock(_JoinType joinType, @Nullable TableModifier modifier
            , TableMeta<?> table, String tableAlias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    final _OnClause<_JoinSpec<I>> createItemBlock(_JoinType joinType, @Nullable TabularModifier modifier
            , TabularItem tableItem, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
    }


    @Override
    final Void createCteBlock(_JoinType joinType, @Nullable TabularModifier modifier, TabularItem tableItem
            , String alias) {
        throw ContextStack.castCriteriaApi(this.context);
    }


    @Override
    final void onEndQuery() {
        //no-op
    }


    @Override
    final void onClear() {
        this.lockMode = null;
    }


    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL57;
    }

    /**
     * @see #from()
     * @see #crossJoin()
     */
    private _JoinSpec<I> nestedNonCrossEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertNoneCrossType();
        final TableBlock.NoOnTableBlock block;
        block = new TableBlock.NoOnTableBlock(joinType, nestedItems, "");
        this.blockConsumer.accept(block);
        return this;
    }

    /**
     * @see #leftJoin()
     * @see #join()
     * @see #rightJoin()
     * @see #fullJoin()
     */
    private _OnClause<_JoinSpec<I>> nestedJoinEnd(final _JoinType joinType, final NestedItems nestedItems) {
        joinType.assertStandardJoinType();

        final OnClauseTableBlock<_JoinSpec<I>> block;
        block = new OnClauseTableBlock<>(joinType, nestedItems, "", this);
        this.blockConsumer.accept(block);
        return block;
    }



    /*################################## blow private inter class method ##################################*/

    private enum StandardLockMode implements SQLWords {

        FOR_UPDATE(_Constant.SPACE_FOR_UPDATE);

        private final String spaceWords;

        StandardLockMode(String spaceWords) {
            this.spaceWords = spaceWords;
        }


        @Override
        public final String render() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(StandardLockMode.class.getSimpleName())
                    .append(this.name())
                    .toString();
        }

    }//StandardLockMode


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
        public _SelectSpec<_RightParenClause<_UnionOrderBySpec<Q>>> leftParen() {
            final CriteriaContext bracketContext, queryContext;
            bracketContext = CriteriaContexts.bracketContext(this.context.endContextBeforeSelect());

            final StandardBracketSelect<Q> bracket;
            bracket = new StandardBracketSelect<>(bracketContext, this.function);

            queryContext = CriteriaContexts.primaryQuery(bracketContext);
            return new SimpleSelect<>(queryContext, bracket::parenRowSetEnd);
        }

        @Override
        Q onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _SelectSpec<Q> createQueryUnion(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
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
        public _SelectSpec<_RightParenClause<_UnionOrderBySpec<Q>>> leftParen() {
            final CriteriaContext bracketContext, queryContext;
            bracketContext = CriteriaContexts.bracketContext(this.context.endContextBeforeSelect());

            final StandardBracketSubQuery<Q> bracket;
            bracket = new StandardBracketSubQuery<>(bracketContext, this.function);

            queryContext = CriteriaContexts.subQueryContext(bracketContext);
            return new SimpleSubQuery<>(queryContext, bracket::parenRowSetEnd);
        }

        @Override
        Q onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _SelectSpec<Q> createQueryUnion(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
            return new UnionAndSubQueryClause<>(this, unionType, this.function);
        }


    } // SimpleSubQuery


    private static abstract class StandardBracketQueries<I extends Item, Q extends Query>
            extends BracketRowSet<
            I,
            Q,
            _UnionOrderBySpec<I>,
            _UnionLimitSpec<I>,
            _AsQueryClause<I>,
            Object,
            Object,
            _SelectSpec<I>,
            RowSet,
            Object> implements StandardQuery._UnionOrderBySpec<I>
            , Statement._RightParenClause<_UnionOrderBySpec<I>> {


        private StandardBracketQueries(CriteriaContext context) {
            super(context);
        }

        @Override
        final Object createRowSetUnion(UnionType unionType, RowSet right) {
            //standard query don't support union VALUES statement
            throw ContextStack.castCriteriaApi(this.context);
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }

    }//StandardBracketQueries


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
        _SelectSpec<I> createUnionRowSet(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
            return new UnionAndSelectClause<>(this, unionType, this.function);
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
        _SelectSpec<I> createUnionRowSet(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
            return new UnionAndSubQueryClause<>(this, unionType, this.function);
        }


    }//StandardBracketSubQuery


    private static final class UnionAndSelectClause<I extends Item>
            extends SelectClauseDispatcher<SQLs.Modifier, _FromSpec<I>>
            implements _SelectSpec<I> {

        private final Select left;

        private final UnionType unionType;

        private final Function<Select, I> function;

        private UnionAndSelectClause(Select left, UnionType unionType, Function<Select, I> function) {
            this.left = left;
            this.unionType = unionType;
            this.function = function;
        }


        @Override
        public _SelectSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext bracketContext, selectContext;
            bracketContext = CriteriaContexts.unionBracketContext(((CriteriaContextSpec) this.left).getContext());

            final StandardBracketSelect<I> bracket;
            bracket = new StandardBracketSelect<>(bracketContext, this::unionRight);

            selectContext = CriteriaContexts.primaryQuery(bracketContext);
            return new SimpleSelect<>(selectContext, bracket::parenRowSetEnd);
        }

        @Override
        StandardQueries<I> createSelectClause() {
            final CriteriaContext selectContext;
            selectContext = CriteriaContexts.unionSelectContext(((CriteriaContextSpec) this.left).getContext());
            return new SimpleSelect<>(selectContext, this::unionRight);
        }

        private I unionRight(final Select right) {
            return this.function.apply(new UnionSelect(MySQLDialect.MySQL57, this.left, this.unionType, right));
        }


    }//UnionSelectClause


    /**
     * @see SimpleSubQuery#createQueryUnion(UnionType)
     * @see StandardBracketSubQuery#createUnionRowSet(UnionType)
     */
    private static final class UnionAndSubQueryClause<I extends Item>
            extends SelectClauseDispatcher<SQLs.Modifier, _FromSpec<I>>
            implements _SelectSpec<I> {

        private final SubQuery left;

        private final UnionType unionType;

        private final Function<SubQuery, I> function;

        private UnionAndSubQueryClause(SubQuery left, UnionType unionType, Function<SubQuery, I> function) {
            this.left = left;
            this.unionType = unionType;
            this.function = function;
        }


        @Override
        public _SelectSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext bracketContext, selectContext;
            bracketContext = CriteriaContexts.unionBracketContext(((CriteriaContextSpec) this.left).getContext());

            final StandardBracketSubQuery<I> bracket;
            bracket = new StandardBracketSubQuery<>(bracketContext, this::unionRight);

            selectContext = CriteriaContexts.subQueryContext(bracketContext);
            return new SimpleSubQuery<>(selectContext, bracket::parenRowSetEnd);
        }

        @Override
        _DynamicHintModifierSelectClause<StandardSyntax.Modifier, _FromSpec<I>> createSelectClause() {
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
