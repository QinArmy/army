package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._StandardQuery;
import io.army.criteria.standard.StandardCrosses;
import io.army.criteria.standard.StandardJoins;
import io.army.criteria.standard.StandardQuery;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.Collections;
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
        SQLsSyntax.Modifier,
        StandardQuery._StandardSelectCommaClause<I>, // SR
        StandardQuery._FromSpec<I>, // SD
        StandardQuery._JoinSpec<I>,// FT
        Statement._AsClause<StandardQuery._JoinSpec<I>>,// FS
        Void,                          //FC
        Statement._OnClause<StandardQuery._JoinSpec<I>>, // JT
        Statement._AsClause<Statement._OnClause<StandardQuery._JoinSpec<I>>>, // JS
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

        implements StandardQuery,
        StandardQuery._SelectSpec<I>,
        StandardQuery._StandardSelectCommaClause<I>,
        StandardQuery._JoinSpec<I>,
        StandardQuery._WhereAndSpec<I>,
        StandardQuery._HavingSpec<I>,
        ArmyStmtSpec,
        _StandardQuery {


    static _SelectSpec<Select> simpleQuery() {
        return new SimpleSelect<>(null, SQLs._SELECT_IDENTITY, null);
    }

    static <I extends Item> _SelectSpec<I> subQuery(CriteriaContext outerContext,
                                                    Function<? super SubQuery, I> function) {
        return new SimpleSubQuery<>(outerContext, function, null);
    }


    private StandardLockMode lockMode;


    private StandardQueries(CriteriaContext context) {
        super(context);
    }

    @Override
    public final _JoinSpec<I> from(Function<_NestedLeftParenSpec<_JoinSpec<I>>, _JoinSpec<I>> function) {
        return function.apply(StandardNestedJoins.nestedJoin(this.context, _JoinType.NONE, this::fromNestedEnd));
    }

    @Override
    public final _JoinSpec<I> crossJoin(Function<_NestedLeftParenSpec<_JoinSpec<I>>, _JoinSpec<I>> function) {
        return function.apply(StandardNestedJoins.nestedJoin(this.context, _JoinType.CROSS_JOIN, this::fromNestedEnd));
    }

    @Override
    public final _OnClause<_JoinSpec<I>> leftJoin(Function<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>, _OnClause<_JoinSpec<I>>> function) {
        return function.apply(StandardNestedJoins.nestedJoin(this.context, _JoinType.LEFT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_JoinSpec<I>> join(Function<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>, _OnClause<_JoinSpec<I>>> function) {
        return function.apply(StandardNestedJoins.nestedJoin(this.context, _JoinType.JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_JoinSpec<I>> rightJoin(Function<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>, _OnClause<_JoinSpec<I>>> function) {
        return function.apply(StandardNestedJoins.nestedJoin(this.context, _JoinType.RIGHT_JOIN, this::joinNestedEnd));
    }

    @Override
    public final _OnClause<_JoinSpec<I>> fullJoin(Function<_NestedLeftParenSpec<_OnClause<_JoinSpec<I>>>, _OnClause<_JoinSpec<I>>> function) {
        return function.apply(StandardNestedJoins.nestedJoin(this.context, _JoinType.FULL_JOIN, this::joinNestedEnd));
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
    public final boolean isRecursive() {
        return false;
    }

    @Override
    public final List<_Cte> cteList() {
        return Collections.emptyList();
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
    final _JoinSpec<I> onFromTable(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table,
                                   String alias) {
        this.blockConsumer.accept(TableBlocks.fromTableBlock(joinType, modifier, table, alias));
        return this;
    }

    @Override
    final _AsClause<_JoinSpec<I>> onFromDerived(final _JoinType joinType, @Nullable DerivedModifier modifier,
                                                final DerivedTable table) {
        return alias -> {
            this.blockConsumer.accept(TableBlocks.fromDerivedBlock(joinType, modifier, table, alias));
            return this;
        };
    }


    @Override
    final _OnClause<_JoinSpec<I>> onJoinTable(final _JoinType joinType, @Nullable TableModifier modifier,
                                              final TableMeta<?> table, final String alias) {
        final TableBlocks.JoinClauseTableBlock<_JoinSpec<I>> block;
        block = TableBlocks.joinTableBlock(joinType, modifier, table, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final _AsClause<_OnClause<_JoinSpec<I>>> onJoinDerived(final _JoinType joinType, @Nullable DerivedModifier modifier,
                                                           final DerivedTable table) {
        return alias -> {
            final TableBlocks.JoinClauseDerivedBlock<_JoinSpec<I>> block;
            block = TableBlocks.joinDerivedBlock(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    final Void onFromCte(_JoinType joinType, @Nullable DerivedModifier modifier, _Cte cteItem, String alias) {
        throw ContextStack.castCriteriaApi(this.context);
    }

    @Override
    final Void onJoinCte(_JoinType joinType, @Nullable DerivedModifier modifier, _Cte cteItem, String alias) {
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
     * @see #from(Function)
     * @see #crossJoin(Function)
     */
    private _JoinSpec<I> fromNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        this.blockConsumer.accept(TableBlocks.fromNestedBlock(joinType, nestedItems));
        return this;
    }

    /**
     * @see #leftJoin(Function)
     * @see #join(Function)
     * @see #rightJoin(Function)
     * @see #fullJoin(Function)
     */
    private _OnClause<_JoinSpec<I>> joinNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        final TableBlocks.JoinClauseBlock<_JoinSpec<I>> block;
        block = TableBlocks.joinNestedBlock(joinType, nestedItems, this);
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


    static class SimpleSelect<I extends Item> extends StandardQueries<I> implements Select {

        private final Function<? super Select, I> function;

        /**
         * <p>
         * Primary constructor
         * </p>
         */
        private SimpleSelect(@Nullable CriteriaContext outerBracketContext, Function<? super Select, I> function,
                             @Nullable CriteriaContext leftContext) {
            super(CriteriaContexts.primaryQueryContext(null, outerBracketContext, leftContext));
            this.function = function;
        }


        @Override
        public _UnionOrderBySpec<I> parens(Function<_SelectSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this, this.function);

            return function.apply(new SimpleSelect<>(bracket.context, bracket::parensEnd, null));
        }


        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _SelectSpec<I> createQueryUnion(final UnionType unionType) {
            final Function<Select, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSelect(this, unionType, right));
            return new SimpleSelect<>(null, unionFunc, this.context);
        }


    }//SimpleSelect


    static class SimpleSubQuery<I extends Item> extends StandardQueries<I>
            implements ArmySubQuery {

        private final Function<? super SubQuery, I> function;

        private SimpleSubQuery(CriteriaContext outerContext, Function<? super SubQuery, I> function,
                               @Nullable CriteriaContext leftContext) {
            super(CriteriaContexts.subQueryContext(null, outerContext, leftContext));
            this.function = function;
        }


        @Override
        public _UnionOrderBySpec<I> parens(Function<_SelectSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this, this.function);
            return function.apply(StandardQueries.subQuery(bracket.context, bracket::parensEnd));
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _SelectSpec<I> createQueryUnion(final UnionType unionType) {
            final Function<SubQuery, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSubQuery(this, unionType, right));
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
            _SelectSpec<I>>
            implements StandardQuery._UnionOrderBySpec<I>,
            StandardQuery {


        private StandardBracketQuery(ArmyStmtSpec spec) {
            super(spec);
        }


        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }

    }//StandardBracketQuery


    private static final class BracketSelect<I extends Item> extends StandardBracketQuery<I>
            implements Select {

        private final Function<? super Select, I> function;

        private BracketSelect(ArmyStmtSpec spec, Function<? super Select, I> function) {
            super(spec);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _SelectSpec<I> createUnionRowSet(final UnionType unionType) {
            final Function<Select, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSelect(this, unionType, right));
            return new SimpleSelect<>(null, unionFunc, this.context);
        }


    }//BracketSelect

    private static final class BracketSubQuery<I extends Item> extends StandardBracketQuery<I>
            implements ArmySubQuery {

        private final Function<? super SubQuery, I> function;

        private BracketSubQuery(ArmyStmtSpec spec, Function<? super SubQuery, I> function) {
            super(spec);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _SelectSpec<I> createUnionRowSet(final UnionType unionType) {
            final Function<SubQuery, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSubQuery(this, unionType, right));
            return new SimpleSubQuery<>(this.context.getNonNullOuterContext(), unionFunc, this.context);
        }


    }//BracketSubQuery


}
