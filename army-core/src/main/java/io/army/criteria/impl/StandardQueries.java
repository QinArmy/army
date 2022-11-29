package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.SubQuery;
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

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is a implementation of {@link StandardQuery}.
 * </p>
 *
 * @since 1.0
 */
abstract class StandardQueries<I extends Item> extends SimpleQueries<
        I,
        SQLsSyntax.Modifier,
        StandardQuery._StandardSelectCommaClause<I>, // SR
        StandardQuery._FromSpec<I>, // SD
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
        StandardQuery._SelectComplexUnionSpec<I>> // SP

        implements StandardQuery,
        StandardQuery._SelectComplexUnionSpec<I>,//standard query no with clause,no values statement,so extends _SelectComplexUnionSpec
        StandardQuery._StandardSelectCommaClause<I>,
        StandardQuery._JoinSpec<I>,
        StandardQuery._WhereAndSpec<I>,
        StandardQuery._HavingSpec<I>,
        _StandardQuery {


    static <I extends Item> _SelectSpec<I> primaryQuery(@Nullable CriteriaContext outerContext,
                                                        Function<Select, I> function) {
        // primary no outer context
        return new SimpleSelect<>(outerContext, function, null);
    }

    static <I extends Item> _SelectSpec<I> subQuery(CriteriaContext outerContext, Function<SubQuery, I> function) {
        return new SimpleSubQuery<>(outerContext, function, null);
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
    final List<SQLs.Modifier> asModifierList(final @Nullable List<SQLsSyntax.Modifier> modifiers) {
        if (modifiers == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return CriteriaUtils.asModifierList(this.context, modifiers, CriteriaUtils::standardModifier);
    }

    @Override
    final boolean isErrorModifier(SQLs.Modifier modifier) {
        return CriteriaUtils.standardModifier(modifier) < 0;
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
            return CriteriaUtils.sqlWordsToString(this);
        }

    }//StandardLockMode


    static class SimpleSelect<I extends Item> extends StandardQueries<I>
            implements Select {

        private final Function<Select, I> function;

        /**
         * <p>
         * Primary constructor
         * </p>
         */
        private SimpleSelect(@Nullable CriteriaContext outerContext, Function<Select, I> function,
                             @Nullable CriteriaContext leftContext) {
            super(CriteriaContexts.primaryQuery(null, outerContext, leftContext));
            this.function = function;
        }

        @Override
        public _SelectSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            this.endQueryBeforeSelect();

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this.context.getOuterContext(), this.function, null);
            return new SimpleSelect<>(bracket.context, bracket::parenRowSetEnd, null);
        }

        @Override
        public final <S extends RowSet> _RightParenClause<_UnionOrderBySpec<I>> leftParen(Supplier<S> supplier) {
            this.endQueryBeforeSelect();

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this.context.getOuterContext(), this.function, null);

            final RowSet rowSet;
            rowSet = ContextStack.unionQuerySupplier(supplier);
            if (!(rowSet instanceof Select && rowSet instanceof StandardQuery)) {
                String m = String.format("%s not standard Select statement.", rowSet.getClass().getName());
                throw ContextStack.criteriaError(bracket.context, m);
            }
            bracket.parenRowSetEnd(rowSet);
            return bracket;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _SelectComplexUnionSpec<I> createQueryUnion(final UnionType unionType) {
            final Function<Select, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSelect(this, unionType, right));
            UnionType.standardUnionType(this.context, unionType);
            return new SimpleSelect<>(this.context.getOuterContext(), unionFunc, this.context);
        }


    }//SimpleSelect


    static class SimpleSubQuery<I extends Item> extends StandardQueries<I>
            implements SubQuery, ArmyDerivedTable {

        private final Function<SubQuery, I> function;

        private SimpleSubQuery(CriteriaContext outerContext, Function<SubQuery, I> function,
                               @Nullable CriteriaContext leftContext) {
            super(CriteriaContexts.subQueryContext(null, outerContext, leftContext));
            this.function = function;
        }

        @Override
        public _SelectSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            this.endQueryBeforeSelect();

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this.context.getNonNullOuterContext(), this.function, null);
            return new SimpleSubQuery<>(bracket.context, bracket::parenRowSetEnd, null);
        }

        @Override
        public final <S extends RowSet> _RightParenClause<_UnionOrderBySpec<I>> leftParen(Supplier<S> supplier) {
            this.endQueryBeforeSelect();

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this.context.getNonNullOuterContext(), this.function, null);

            final RowSet rowSet;
            rowSet = ContextStack.unionQuerySupplier(supplier);
            if (!(rowSet instanceof SubQuery && rowSet instanceof StandardQuery)) {
                String m = String.format("%s not standard SubQuery statement.", rowSet.getClass().getName());
                throw ContextStack.criteriaError(bracket.context, m);
            }
            bracket.parenRowSetEnd(rowSet);
            return bracket;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _SelectComplexUnionSpec<I> createQueryUnion(final UnionType unionType) {
            final Function<SubQuery, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSubQuery(this, unionType, right));
            UnionType.standardUnionType(this.context, unionType);
            return new SimpleSubQuery<>(this.context.getNonNullOuterContext(), unionFunc, this.context);
        }


    } // SimpleSubQuery


    static abstract class StandardBracketQuery<I extends Item>
            extends BracketRowSet<
            I,
            _UnionOrderBySpec<I>,
            _UnionLimitSpec<I>,
            _AsQueryClause<I>,
            Object,
            Object,
            _SelectComplexUnionSpec<I>> implements StandardQuery._UnionOrderBySpec<I>,
            StandardQuery {


        private StandardBracketQuery(@Nullable CriteriaContext outerContext, @Nullable CriteriaContext leftContext) {
            super(CriteriaContexts.bracketContext(null, outerContext, leftContext));
        }


        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }

    }//StandardBracketQuery


    private static final class BracketSelect<I extends Item>
            extends StandardBracketQuery<I>
            implements Select {

        private final Function<Select, I> function;

        private BracketSelect(@Nullable CriteriaContext outerContext, Function<Select, I> function,
                              @Nullable CriteriaContext leftContext) {
            super(outerContext, leftContext);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _SelectComplexUnionSpec<I> createUnionRowSet(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
            final Function<Select, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSelect(this, unionType, right));
            return new SimpleSelect<>(this.context.getOuterContext(), unionFunc, this.context);
        }


    }//BracketSelect

    private static final class BracketSubQuery<I extends Item>
            extends StandardBracketQuery<I>
            implements SubQuery, ArmyDerivedTable {

        private final Function<SubQuery, I> function;

        private BracketSubQuery(@Nullable CriteriaContext outerContext, Function<SubQuery, I> function,
                                @Nullable CriteriaContext leftContext) {
            super(outerContext, leftContext);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _SelectComplexUnionSpec<I> createUnionRowSet(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
            final Function<SubQuery, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSubQuery(this, unionType, right));
            return new SimpleSubQuery<>(this.context.getNonNullOuterContext(), unionFunc, this.context);
        }


    }//BracketSubQuery


}
