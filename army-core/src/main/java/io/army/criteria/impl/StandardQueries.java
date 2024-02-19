/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner.*;
import io.army.criteria.standard.StandardCrosses;
import io.army.criteria.standard.StandardCtes;
import io.army.criteria.standard.StandardJoins;
import io.army.criteria.standard.StandardQuery;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect.mysql.MySQLDialect;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a implementation of {@link StandardQuery}.
 *
 * @since 0.6.0
 */
abstract class StandardQueries<I extends Item> extends SimpleQueries<
        I,
        StandardCtes,
        StandardQuery.SelectSpec<I>,
        SQLs.Modifier,
        StandardQuery._StandardSelectCommaClause<I>, // SR
        StandardQuery._FromSpec<I>, // SD
        StandardQuery._JoinSpec<I>,// FT
        Statement._AsClause<StandardQuery._JoinSpec<I>>,// FS
        StandardQuery._JoinSpec<I>,                          //FC
        Void,
        Statement._OnClause<StandardQuery._JoinSpec<I>>, // JT
        Statement._AsClause<Statement._OnClause<StandardQuery._JoinSpec<I>>>, // JS
        Statement._OnClause<StandardQuery._JoinSpec<I>>,                               // JC
        Void,
        StandardQuery._GroupBySpec<I>, // WR
        StandardQuery._WhereAndSpec<I>, // AR
        StandardQuery._GroupByCommaSpec<I>, // GR
        StandardQuery._HavingSpec<I>, // GD
        StandardQuery._HavingAndSpec<I>, // HR
        StandardQuery._WindowSpec<I>, // HD
        StandardQuery._OrderByCommaSpec<I>, // OR
        StandardQuery._LimitSpec<I>, // OD
        StandardQuery._LockSpec<I>, // LR
        Object,
        Object,
        StandardQuery.SelectSpec<I>> // SP

        implements StandardQuery,
        StandardQuery.WithSpec<I>,
        StandardQuery._StandardSelectCommaClause<I>,
        StandardQuery._JoinSpec<I>,
        StandardQuery._WhereAndSpec<I>,
        StandardQuery._GroupByCommaSpec<I>,
        StandardQuery._HavingSpec<I>,
        StandardQuery._HavingAndSpec<I>,
        StandardQuery._WindowCommaSpec<I>,
        StandardQuery._OrderByCommaSpec<I>,
        _StandardQuery,
        ArmyStmtSpec {


    static <I extends Item> WithSpec<I> simpleQuery(StandardDialect dialect, Function<? super Select, I> function) {
        return new SimpleSelect<>(dialect, null, null, function, null);
    }

    static WithSpec<_BatchSelectParamSpec> batchQuery(StandardDialect dialect) {
        return new SimpleSelect<>(dialect, null, null, StandardQueries::mapToBatchSelect, null);
    }


    static <I extends Item> WithSpec<I> subQuery(StandardDialect dialect, CriteriaContext outerContext,
                                                 Function<? super SubQuery, I> function) {
        return new SimpleSubQuery<>(dialect, null, outerContext, function, null);
    }

    static <I extends Item> StandardQuery._CteComma<I> staticCteComma(CriteriaContext context, boolean recursive,
                                                                      Function<Boolean, I> function) {
        if (context.dialect() == StandardDialect.STANDARD10) {
            throw CriteriaUtils.standard10DontSupportWithClause();
        }
        return new StaticCteComma<>(context, recursive, function);
    }

    static Window._StandardPartitionBySpec anonymousWindow(CriteriaContext context,
                                                           @Nullable String existingWindowName) {
        if (context.dialect() == StandardDialect.STANDARD10) {
            throw CriteriaUtils.standard10DontSupportWindowFunc();
        }
        return new StandardWindow(context, existingWindowName);
    }

    static StandardCtes cteBuilder(boolean recursive, CriteriaContext context) {
        if (context.dialect() == StandardDialect.STANDARD10) {
            throw CriteriaUtils.standard10DontSupportWithClause();
        }
        return new StandardCteBuilder(recursive, context);
    }

    private static _BatchSelectParamSpec mapToBatchSelect(final Select select) {
        final _BatchSelectParamSpec spec;
        if (select instanceof _Query) {
            spec = ((SimpleSelect<?>) select)::wrapToBatchSelect;
        } else if (select instanceof UnionSelect) {
            spec = ((UnionSelect) select)::wrapToBatchSelect;
        } else if (select instanceof BracketSelect) {
            spec = ((BracketSelect<?>) select)::wrapToBatchSelect;
        } else {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return spec;
    }


    private List<_Window> windowList;
    private StandardLockMode lockStrength;


    private StandardQueries(@Nullable ArmyStmtSpec spec, CriteriaContext context) {
        super(spec, context);
    }

    @Override
    public final _StaticCteParensSpec<SelectSpec<I>> with(String name) {
        return StandardQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final _StaticCteParensSpec<SelectSpec<I>> withRecursive(String name) {
        return StandardQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                .comma(name);
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
    public final Window._WindowAsClause<Window._StandardPartitionBySpec, _WindowCommaSpec<I>> window(String windowName) {
        if (this.context.dialect() == StandardDialect.STANDARD10) {// here required for WINDOW AS clause
            throw CriteriaUtils.standard10DontSupportWindowClause();
        }
        return new NamedWindowAsClause<>(this.context, windowName, this::onAddWindow, StandardQueries::namedWindow);
    }


    @Override
    public final Window._WindowAsClause<Window._StandardPartitionBySpec, _WindowCommaSpec<I>> comma(String windowName) {
        if (this.context.dialect() == StandardDialect.STANDARD10) {// here required for WINDOW AS clause
            throw CriteriaUtils.standard10DontSupportWindowClause();
        }
        return new NamedWindowAsClause<>(this.context, windowName, this::onAddWindow, StandardQueries::namedWindow);
    }

    @Override
    public final _OrderBySpec<I> windows(Consumer<Window.Builder<Window._StandardPartitionBySpec>> consumer) {
        consumer.accept(this::dynamicNamedWindow);
        if (this.windowList == null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::windowListIsEmpty);
        }
        return this;
    }

    @Override
    public final _OrderBySpec<I> ifWindows(Consumer<Window.Builder<Window._StandardPartitionBySpec>> consumer) {
        consumer.accept(this::dynamicNamedWindow);
        return this;
    }

    @Override
    public final _AsQueryClause<I> forUpdate() {
        if (this.lockStrength != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        this.lockStrength = StandardLockMode.FOR_UPDATE;
        return this;
    }

    @Override
    public final _AsQueryClause<I> ifForUpdate(BooleanSupplier predicate) {
        if (this.lockStrength != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        } else if (predicate.getAsBoolean()) {
            this.lockStrength = StandardLockMode.FOR_UPDATE;
        } else {
            this.lockStrength = null;
        }
        return this;
    }

    @Override
    public final List<_Window> windowList() {
        final List<_Window> list = this.windowList;
        if (list == null || list instanceof ArrayList) {
            throw _Exceptions.castCriteriaApi();
        }
        return list;
    }

    @Override
    public final SQLWords lockStrength() {
        return this.lockStrength;
    }

    @Override
    final List<Hint> asHintList(@Nullable List<Hint> hints) {
        //standard statement don't hints
        throw ContextStack.clearStackAndCastCriteriaApi();
    }


    @Override
    final List<SQLs.Modifier> asModifierList(final @Nullable List<SQLs.Modifier> modifiers) {
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
    final SQLs.Modifier allModifier() {
        return SQLs.ALL;
    }

    @Override
    final SQLs.Modifier distinctModifier() {
        return SQLs.DISTINCT;
    }

    @Override
    final boolean isIllegalDerivedModifier(@Nullable SQLs.DerivedModifier modifier) {
        return CriteriaUtils.isIllegalLateral(modifier);
    }


    @Override
    final _JoinSpec<I> onFromTable(_JoinType joinType, @Nullable SQLs.TableModifier modifier, TableMeta<?> table,
                                   String alias) {
        this.blockConsumer.accept(TabularBlocks.fromTableBlock(joinType, modifier, table, alias));
        return this;
    }

    @Override
    final _AsClause<_JoinSpec<I>> onFromDerived(final _JoinType joinType, @Nullable SQLs.DerivedModifier modifier,
                                                final DerivedTable table) {
        return alias -> {
            this.blockConsumer.accept(TabularBlocks.fromDerivedBlock(joinType, modifier, table, alias));
            return this;
        };
    }

    @Override
    final _JoinSpec<I> onFromCte(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem, String alias) {
        this.blockConsumer.accept(TabularBlocks.fromCteBlock(joinType, cteItem, alias));
        return this;
    }


    @Override
    final _OnClause<_JoinSpec<I>> onJoinTable(final _JoinType joinType, @Nullable SQLs.TableModifier modifier,
                                              final TableMeta<?> table, final String alias) {
        final TabularBlocks.JoinClauseTableBlock<_JoinSpec<I>> block;
        block = TabularBlocks.joinTableBlock(joinType, modifier, table, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final _AsClause<_OnClause<_JoinSpec<I>>> onJoinDerived(final _JoinType joinType, @Nullable SQLs.DerivedModifier modifier,
                                                           final DerivedTable table) {
        return alias -> {
            final TabularBlocks.JoinClauseDerivedBlock<_JoinSpec<I>> block;
            block = TabularBlocks.joinDerivedBlock(joinType, modifier, table, alias, this);
            this.blockConsumer.accept(block);
            return block;
        };
    }

    @Override
    final _OnClause<_JoinSpec<I>> onJoinCte(_JoinType joinType, @Nullable SQLs.DerivedModifier modifier, _Cte cteItem, String alias) {
        final TabularBlocks.JoinClauseCteBlock<_JoinSpec<I>> block;
        block = TabularBlocks.joinCteBlock(joinType, cteItem, alias, this);
        this.blockConsumer.accept(block);
        return block;
    }

    @Override
    final StandardCtes createCteBuilder(boolean recursive) {
        return new StandardCteBuilder(recursive, this.context);
    }

    @Override
    final void onEndQuery() {
        final boolean standard10;
        standard10 = this.context.dialect() == StandardDialect.STANDARD10;
        if (standard10 && this.cteList().size() > 0) {
            throw CriteriaUtils.standard10DontSupportWithClause();
        }

        final List<_Window> windowList = this.windowList;
        if (windowList != null && standard10) {
            throw CriteriaUtils.standard10DontSupportWindowClause();
        }

        this.windowList = _Collections.safeUnmodifiableList(windowList);
    }


    @Override
    final void onClear() {
        this.windowList = null;
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
        this.blockConsumer.accept(TabularBlocks.fromNestedBlock(joinType, nestedItems));
        return this;
    }

    /**
     * @see #leftJoin(Function)
     * @see #join(Function)
     * @see #rightJoin(Function)
     * @see #fullJoin(Function)
     */
    private _OnClause<_JoinSpec<I>> joinNestedEnd(final _JoinType joinType, final _NestedItems nestedItems) {
        final TabularBlocks.JoinClauseBlock<_JoinSpec<I>> block;
        block = TabularBlocks.joinNestedBlock(joinType, nestedItems, this);
        this.blockConsumer.accept(block);
        return block;
    }

    /**
     * @see #windows(Consumer)
     * @see #window(String)
     */
    private _WindowCommaSpec<I> onAddWindow(final ArmyWindow window) {
        window.endWindowClause();
        List<_Window> windowList = this.windowList;
        if (windowList == null) {
            windowList = _Collections.arrayList();
            this.windowList = windowList;
        } else if (!(window instanceof ArrayList)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        windowList.add(window);
        return this;
    }

    private Window._WindowAsClause<Window._StandardPartitionBySpec, Item> dynamicNamedWindow(String windowName) {
        if (context.dialect() == StandardDialect.STANDARD10) {
            throw CriteriaUtils.standard10DontSupportWindowClause();
        }
        return new NamedWindowAsClause<>(this.context, windowName, this::onAddWindow, StandardQueries::namedWindow);
    }


    private static Window._StandardPartitionBySpec namedWindow(String windowName, CriteriaContext context,
                                                               @Nullable String existingWindowName) {
        if (context.dialect() == StandardDialect.STANDARD10) {
            throw CriteriaUtils.standard10DontSupportWindowClause();
        }
        return new StandardWindow(windowName, context, existingWindowName);
    }


    /*################################## blow private inter class method ##################################*/

    private enum StandardLockMode implements SQLWords {

        FOR_UPDATE(_Constant.SPACE_FOR_UPDATE);

        private final String spaceWords;

        StandardLockMode(String spaceWords) {
            this.spaceWords = spaceWords;
        }


        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }


    } // StandardLockMode


    static final class SimpleSelect<I extends Item> extends StandardQueries<I> implements ArmySelect {

        private final Function<? super Select, I> function;

        /**
         * <p>
         * Primary constructor
         */
        private SimpleSelect(StandardDialect dialect, @Nullable ArmyStmtSpec spec,
                             @Nullable CriteriaContext outerBracketContext, Function<? super Select, I> function,
                             @Nullable CriteriaContext leftContext) {
            super(spec, CriteriaContexts.primaryQueryContext(dialect, spec, outerBracketContext, leftContext));
            this.function = function;
        }


        @Override
        public _UnionOrderBySpec<I> parens(Function<WithSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketSelect<I> bracket;
            bracket = new BracketSelect<>(this, this.function);

            return ClauseUtils.invokeFunction(function, new SimpleSelect<>(this.context.dialect(StandardDialect.class), null, bracket.context,
                    bracket::parensEnd, null)
            );
        }

//        @Override
//        public _UnionSpec<I> whiteSpace(Consumer<_UnionClause<Item>> consumer) {
//
//            return null;
//        }
//
//        @Override
//        public _UnionSpec<I> ifWhiteSpace(Consumer<_UnionClause<Item>> consumer) {
//            return null;
//        }


        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        SelectSpec<I> createQueryUnion(final _UnionType unionType) {
            final Function<Select, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSelect(this, unionType, right));
            return new SimpleSelect<>(this.context.dialect(StandardDialect.class), null, null, unionFunc, this.context);
        }

        private StandardBatchSimpleSelect wrapToBatchSelect(List<?> paramList) {
            return new StandardBatchSimpleSelect(this, CriteriaUtils.paramList(paramList));
        }


    } // SimpleSelect



    static class SimpleSubQuery<I extends Item> extends StandardQueries<I>
            implements ArmySubQuery {

        private final Function<? super SubQuery, I> function;

        private SimpleSubQuery(StandardDialect dialect, @Nullable ArmyStmtSpec spec, CriteriaContext outerContext,
                               Function<? super SubQuery, I> function, @Nullable CriteriaContext leftContext) {
            super(spec, CriteriaContexts.subQueryContext(dialect, spec, outerContext, leftContext));
            this.function = function;
        }


        @Override
        public _UnionOrderBySpec<I> parens(Function<WithSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketSubQuery<I> bracket;
            bracket = new BracketSubQuery<>(this, this.function);
            return ClauseUtils.invokeFunction(function, StandardQueries.subQuery(this.context.dialect(StandardDialect.class),
                    bracket.context, bracket::parensEnd)
            );
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        SelectSpec<I> createQueryUnion(final _UnionType unionType) {
            final Function<SubQuery, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSubQuery(this, unionType, right));
            return new SimpleSubQuery<>(this.context.dialect(StandardDialect.class), null,
                    this.context.getNonNullOuterContext(), unionFunc, this.context
            );
        }

//        @Override
//        public _UnionSpec<I> whiteSpace(Consumer<_UnionClause<Item>> consumer) {
//            return null;
//        }
//
//        @Override
//        public _UnionSpec<I> ifWhiteSpace(Consumer<_UnionClause<Item>> consumer) {
//            return null;
//        }

    } // SimpleSubQuery

    private static abstract class StandardQueryUnionClause<I extends Item>
            extends QueryUnionClause<StandardQuery.SelectSpec<I>>
            implements StandardQuery._UnionClause<I> {

        private StandardQueryUnionClause(@Nullable Runnable firstQueryEnd) {
            super(firstQueryEnd);
        }

        @Override
        SelectSpec<I> createQuery(_UnionType unionType) {
            return null;
        }


    } // StandardQueryUnionClause


    static abstract class StandardBracketQuery<I extends Item>
            extends BracketRowSet<
            I,
            StandardQuery._UnionOrderBySpec<I>,
            StandardQuery._UnionOrderByCommaSpec<I>,
            StandardQuery._UnionLimitSpec<I>,
            Query._AsQueryClause<I>,
            Object,
            Object,
            SelectSpec<I>>
            implements StandardQuery._UnionOrderBySpec<I>,
            StandardQuery._UnionOrderByCommaSpec<I>,
            StandardQuery {


        private StandardBracketQuery(ArmyStmtSpec spec) {
            super(spec);
        }


        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }

    } // StandardBracketQuery


    private static final class BracketSelect<I extends Item> extends StandardBracketQuery<I>
            implements ArmySelect {

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
        SelectSpec<I> createUnionRowSet(final _UnionType unionType) {
            final Function<Select, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSelect(this, unionType, right));
            return new SimpleSelect<>(this.context.dialect(StandardDialect.class), null, null, unionFunc, this.context);
        }

        StandardBatchBracketSelect wrapToBatchSelect(List<?> paramList) {
            return new StandardBatchBracketSelect(this, CriteriaUtils.paramList(paramList));
        }


    } // BracketSelect


    static final class StandardBatchBracketSelect extends BracketRowSet.ArmyBatchBracketSelect
            implements StandardQuery {

        private StandardBatchBracketSelect(BracketSelect<?> select, List<?> paramList) {
            super(select, paramList);
        }

        @Override
        Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }


    } // StandardBatchBracketSelect


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
        SelectSpec<I> createUnionRowSet(final _UnionType unionType) {
            final Function<SubQuery, I> unionFunc;
            unionFunc = right -> this.function.apply(new UnionSubQuery(this, unionType, right));
            return new SimpleSubQuery<>(this.context.dialect(StandardDialect.class), null,
                    this.context.getNonNullOuterContext(), unionFunc, this.context
            );
        }


    } //BracketSubQuery


    static final class StandardWindow extends SQLWindow<
            Window._StandardPartitionByCommaSpec,
            Window._StandardOrderByCommaSpec,
            Window._StandardFrameExtentSpec,
            Item,
            Window._StandardFrameBetweenClause,
            Item,
            Window._StandardFrameUnitSpaceSpec,
            Item> implements Window._StandardPartitionBySpec,
            Window._StandardPartitionByCommaSpec,
            Window._StandardOrderByCommaSpec,
            Window._StandardFrameBetweenClause,
            Window._StandardFrameUnitSpaceSpec {

        private StandardWindow(String windowName, CriteriaContext context, @Nullable String existingWindowName) {
            super(windowName, context, existingWindowName);
        }

        private StandardWindow(CriteriaContext context, @Nullable String existingWindowName) {
            super(context, existingWindowName);
        }


    }//StandardWindow


    private static final class StaticCteComma<I extends Item> implements StandardQuery._CteComma<I> {

        private final CriteriaContext context;

        private final boolean recursive;

        private final Function<Boolean, I> function;

        /**
         * @see #staticCteComma(CriteriaContext, boolean, Function)
         */
        private StaticCteComma(CriteriaContext context, final boolean recursive, Function<Boolean, I> function) {
            context.onBeforeWithClause(recursive);
            this.context = context;
            this.recursive = recursive;
            this.function = function;
        }

        @Override
        public _StaticCteParensSpec<I> comma(final @Nullable String name) {
            if (name == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.context.onStartCte(name);
            return new StaticCteParensClause<>(this, name);
        }

        @Override
        public I space() {
            return this.function.apply(this.recursive);
        }


    }//StaticCteComma


    private static final class StaticCteParensClause<I extends Item>
            implements _StaticCteParensSpec<I> {

        private final StaticCteComma<I> comma;

        private final String name;

        private List<String> columnAliasList;

        /**
         * @see StaticCteComma#comma(String)
         */
        private StaticCteParensClause(StaticCteComma<I> comma, String name) {
            this.comma = comma;
            this.name = name;
        }

        @Override
        public _StaticCteAsClause<I> parens(String first, String... rest) {
            return this.onColumnAliasList(ArrayUtils.unmodifiableListOf(first, rest));
        }

        @Override
        public _StaticCteAsClause<I> parens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAliasList(CriteriaUtils.stringList(this.comma.context, true, consumer));
        }

        @Override
        public _StaticCteAsClause<I> ifParens(Consumer<Consumer<String>> consumer) {
            return this.onColumnAliasList(CriteriaUtils.stringList(this.comma.context, false, consumer));
        }

        @Override
        public _CteComma<I> as(Function<SelectSpec<_CteComma<I>>, _CteComma<I>> function) {
            final CriteriaContext context = this.comma.context;
            return ClauseUtils.invokeFunction(function, StandardQueries.subQuery(context.dialect(StandardDialect.class), context,
                    this::subQueryEnd)
            );
        }

        private _StaticCteAsClause<I> onColumnAliasList(final List<String> list) {
            if (this.columnAliasList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.columnAliasList = list;
            if (list.size() > 0) {
                this.comma.context.onCteColumnAlias(this.name, list);
            }
            return this;
        }

        private _CteComma<I> subQueryEnd(final SubQuery query) {
            CriteriaUtils.createAndAddCte(this.comma.context, this.name, this.columnAliasList, query);
            return this.comma;
        }


    } // StaticCteParensClause

    private static final class DynamicCteQueryParensSpec
            extends CriteriaSupports.CteParensClause<StandardQuery._QueryDynamicCteAsClause>
            implements StandardQuery._DynamicCteParensSpec {

        private final StandardCteBuilder builder;

        private DynamicCteQueryParensSpec(StandardCteBuilder builder, String name) {
            super(name, builder.context);
            this.builder = builder;
        }


        @Override
        public DialectStatement._CommaClause<StandardCtes> as(Function<WithSpec<_CommaClause<StandardCtes>>, _CommaClause<StandardCtes>> function) {
            return function.apply(StandardQueries.subQuery(this.context.dialect(StandardDialect.class), this.context,
                    this::subQueryEnd)
            );
        }

        private DialectStatement._CommaClause<StandardCtes> subQueryEnd(final SubQuery query) {
            CriteriaUtils.createAndAddCte(this.context, this.name, this.columnAliasList, query);
            return this.builder;
        }


    }//DynamicCteQueryParensSpec


    private static final class StandardCteBuilder implements StandardCtes, CriteriaSupports.CteBuilder,
            Statement._CommaClause<StandardCtes> {

        private final boolean recursive;

        private final CriteriaContext context;


        private StandardCteBuilder(boolean recursive, CriteriaContext context) {
            this.recursive = recursive;
            this.context = context;
            context.onBeforeWithClause(recursive);
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public StandardQuery._DynamicCteParensSpec subQuery(String name) {
            return new DynamicCteQueryParensSpec(this, name);
        }

        @Override
        public void endLastCte() {
            //no-op
        }

        @Override
        public StandardCtes comma() {
            return this;
        }


    }//StandardCteBuilder


    static final class StandardBatchSimpleSelect extends ArmyBatchSimpleSelect
            implements StandardQuery, _StandardQuery {

        private final SQLWords lockStrength;

        private final List<_Window> windowList;


        private StandardBatchSimpleSelect(SimpleSelect<?> query, List<?> paramList) {
            super(query, paramList);
            this.lockStrength = query.lockStrength();
            this.windowList = query.windowList();
        }


        @Override
        Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }

        @Override
        public List<_Window> windowList() {
            return this.windowList;
        }

        @Override
        public SQLWords lockStrength() {
            return this.lockStrength;
        }


    }//StandardBatchSimpleSelect


}
