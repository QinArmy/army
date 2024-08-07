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
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner.postgre._PostgreValues;
import io.army.criteria.postgre.PostgreCtes;
import io.army.criteria.postgre.PostgreQuery;
import io.army.criteria.postgre.PostgreStatement;
import io.army.criteria.postgre.PostgreValues;
import io.army.dialect.Dialect;
import io.army.dialect.PostgreDialect;

import io.army.lang.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This class is abstract implementation of {@link PostgreValues}.
 *
 * @since 0.6.0
 */
abstract class PostgreSimpleValues<I extends Item> extends SimpleValues<
        I,
        PostgreValues._OrderByCommaSpec<I>,
        PostgreValues._LimitSpec<I>,
        PostgreValues._OffsetSpec<I>,
        PostgreValues._FetchSpec<I>,
        PostgreStatement._AsValuesClause<I>,
        PostgreValues._QueryComplexSpec<I>>
        implements PostgreValues.ValuesSpec<I>,
        PostgreValues._StaticValuesRowClause<I>,
        PostgreValues._StaticValuesRowCommaSpec<I>,
        PostgreValues._OffsetSpec<I>,
        PostgreValues._OrderByCommaSpec<I>,
        ValuesParens,
        ArmyStmtSpec,
        _PostgreValues,
        PostgreValues {


    /**
     * <p>
     * create primary VALUES statement.
     */
    static ValuesSpec<Values> simpleValues() {
        return new SimplePrimaryValues<>(null, null, SQLs::identity, null);
    }

    /**
     * create primary VALUES statement for dispatcher.
     */
    static <I extends Item> ValuesSpec<I> fromDispatcher(ArmyStmtSpec spec,
                                                       Function<? super Values, I> function) {
        return new SimplePrimaryValues<>(spec, null, function, null);
    }

    /**
     * create sub VALUES statement for dispatcher.
     */
    static <I extends Item> ValuesSpec<I> fromSubDispatcher(ArmyStmtSpec spec,
                                                          Function<? super SubValues, I> function) {
        return new SimpleSubValues<>(spec, null, function, null);
    }

    /**
     * create sub VALUES statement.
     */
    static <I extends Item> ValuesSpec<I> subValues(CriteriaContext outerContext,
                                                  Function<? super SubValues, I> function) {
        return new SimpleSubValues<>(null, outerContext, function, null);
    }


    private PostgreSimpleValues(CriteriaContext context) {
        super(context);
    }


    @Override
    public final _OrderBySpec<I> values(Consumer<ValuesParens> consumer) {
        CriteriaUtils.invokeConsumer(this, consumer);
        return this;
    }

    @Override
    public final _StaticValuesRowClause<I> values() {
        return this;
    }

    @Override
    public final PostgreSimpleValues<I> parens(Consumer<Values._ValueStaticColumnSpaceClause> consumer) {
        this.context.onValuesRowStart();
        CriteriaUtils.invokeConsumer(this, consumer);
        endCurrentRow();
        return this;
    }

    @Override
    public final PostgreSimpleValues<I> parens(SQLs.SymbolSpace space, Consumer<Values._ValuesDynamicColumnClause> consumer) {
        this.context.onValuesRowStart();
        CriteriaUtils.invokeConsumer(this, consumer);
        endCurrentRow();
        return this;
    }

    @Override
    public final _StaticValuesRowClause<I> comma() {
        return this;
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
    final Dialect statementDialect() {
        return PostgreUtils.DIALECT;
    }


    @Override
    final String columnAlias(int columnIndex) {
        return "column" + (++columnIndex);
    }


    private static final class SimplePrimaryValues<I extends Item> extends PostgreSimpleValues<I>
            implements ArmyValues {

        private final Function<? super Values, I> function;


        /**
         * @param outerBracketContext outer bracket context
         */
        private SimplePrimaryValues(@Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerBracketContext,
                                    Function<? super Values, I> function, @Nullable CriteriaContext leftContext) {
            super(CriteriaContexts.primaryValuesContext(PostgreUtils.DIALECT, spec, outerBracketContext, leftContext));
            this.function = function;
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<ValuesSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketValues<I> bracket;
            bracket = new BracketValues<>(this, this.function);
            return ClauseUtils.invokeFunction(function, new SimplePrimaryValues<>(null, bracket.context, bracket::parensEnd, null));
        }

        @Override
        I onAsValues() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createUnionValues(final _UnionType unionType) {
            final Function<RowSet, I> unionFun;
            unionFun = right -> this.function.apply(new UnionValues(this, unionType, right));
            return new ValuesDispatcher<>(this.context, unionFun);
        }

    } // SimplePrimaryValues


    private static final class SimpleSubValues<I extends Item> extends PostgreSimpleValues<I>
            implements ArmySubValues {

        private final Function<? super SubValues, I> function;

        private SimpleSubValues(@Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerContext,
                                Function<? super SubValues, I> function, @Nullable CriteriaContext leftContext) {
            super(CriteriaContexts.subValuesContext(PostgreUtils.DIALECT, spec, outerContext, leftContext));
            this.function = function;
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<ValuesSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketSubValues<I> bracket;
            bracket = new BracketSubValues<>(this, this.function);

            return ClauseUtils.invokeFunction(function, new SimpleSubValues<>(null, bracket.context, bracket::parensEnd, null));
        }

        @Override
        I onAsValues() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createUnionValues(final _UnionType unionType) {
            final Function<RowSet, I> unionFun;
            unionFun = rowSet -> this.function.apply(new UnionSubValues(this, unionType, rowSet));
            return new SubValuesDispatcher<>(this.context, unionFun);
        }

    }//SimpleSubValues


    static abstract class PostgreBracketValues<I extends Item> extends BracketRowSet<
            I,
            PostgreValues._UnionOrderBySpec<I>,
            PostgreValues._UnionOrderByCommaSpec<I>,
            PostgreValues._UnionLimitSpec<I>,
            PostgreValues._UnionOffsetSpec<I>,
            PostgreValues._UnionFetchSpec<I>,
            PostgreValues._AsValuesClause<I>,
            PostgreValues._QueryComplexSpec<I>>
            implements PostgreValues,
            PostgreValues._UnionOrderBySpec<I>,
            PostgreValues._UnionOrderByCommaSpec<I>,
            PostgreValues._UnionOffsetSpec<I>,
            PostgreValues._UnionFetchSpec<I> {

        private PostgreBracketValues(ArmyStmtSpec spec) {
            super(spec);
        }


        @Override
        public final I asValues() {
            return this.asQuery();
        }

        @Override
        final Dialect statementDialect() {
            return PostgreDialect.POSTGRE15;
        }


    } // PostgreBracketValues


    private static final class BracketValues<I extends Item> extends PostgreBracketValues<I>
            implements ArmyValues {

        private final Function<? super Values, I> function;

        private BracketValues(ArmyStmtSpec spec, Function<? super Values, I> function) {
            super(spec);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createUnionRowSet(final _UnionType unionType) {
            final Function<RowSet, I> unionFun;
            unionFun = rowSet -> this.function.apply(new UnionValues(this, unionType, rowSet));
            return new ValuesDispatcher<>(this.context, unionFun);
        }


    } // BracketValues


    private static final class BracketSubValues<I extends Item> extends PostgreBracketValues<I>
            implements ArmySubValues {

        private final Function<? super SubValues, I> function;

        private BracketSubValues(ArmyStmtSpec spec, Function<? super SubValues, I> function) {
            super(spec);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createUnionRowSet(final _UnionType unionType) {
            final Function<RowSet, I> unionFun;
            unionFun = rowSet -> this.function.apply(new UnionSubValues(this, unionType, rowSet));
            return new SubValuesDispatcher<>(this.context, unionFun);
        }


    } // BracketSubValues


    private static abstract class PostgreValuesDispatcher<I extends Item>
            extends PostgreQueries.PostgreSelectClauseDispatcher<
            I,
            PostgreQuery._PostgreSelectClause<I>>
            implements PostgreValues._QueryWithComplexSpec<I> {

        final Function<RowSet, I> function;

        private PostgreValuesDispatcher(CriteriaContext dispatcherContext, Function<RowSet, I> function) {
            super(dispatcherContext);
            this.function = function;
        }


        @Override
        public final PostgreQuery._StaticCteParensSpec<PostgreQuery._PostgreSelectClause<I>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final PostgreQuery._StaticCteParensSpec<PostgreQuery._PostgreSelectClause<I>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        final PostgreCtes createCteBuilder(boolean recursive) {
            return PostgreSupports.postgreCteBuilder(recursive, this.context);
        }


    }//PostgreComplexValues

    private static final class ValuesDispatcher<I extends Item> extends PostgreValuesDispatcher<I> {

        private ValuesDispatcher(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(CriteriaContexts.primaryDispatcherContext(PostgreUtils.DIALECT, leftContext.getOuterContext(), leftContext), function);
        }

        private ValuesDispatcher(BracketValues<?> bracket, Function<RowSet, I> function) {
            super(CriteriaContexts.primaryDispatcherContext(PostgreUtils.DIALECT, bracket.context, null), function);
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<_QueryWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endDispatcher();

            final BracketValues<I> bracket;
            bracket = new BracketValues<>(this, this.function);

            return ClauseUtils.invokeFunction(function, new ValuesDispatcher<>(bracket, bracket::parensEnd));
        }


        @Override
        public _OrderBySpec<I> values(Consumer<ValuesParens> consumer) {
            this.endDispatcher();

            return PostgreSimpleValues.fromDispatcher(this, this.function)
                    .values(consumer);
        }

        @Override
        public _StaticValuesRowClause<I> values() {
            this.endDispatcher();

            return PostgreSimpleValues.fromDispatcher(this, this.function)
                    .values();
        }

        @Override
        PostgreQueries<I> createSelectClause() {
            this.endDispatcher();

            return PostgreQueries.fromDispatcher(this, this.function);
        }


    }//ValuesDispatcher

    private static final class SubValuesDispatcher<I extends Item> extends PostgreValuesDispatcher<I> {


        private SubValuesDispatcher(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(CriteriaContexts.subDispatcherContext(leftContext.getNonNullOuterContext(), leftContext), function);
        }

        private SubValuesDispatcher(BracketSubValues<?> bracket, Function<RowSet, I> function) {
            super(CriteriaContexts.subDispatcherContext(bracket.context, null), function);
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<_QueryWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endDispatcher();

            final BracketSubValues<I> bracket;
            bracket = new BracketSubValues<>(this, this.function);

            return ClauseUtils.invokeFunction(function, new SubValuesDispatcher<>(bracket, bracket::parensEnd));
        }


        @Override
        public _OrderBySpec<I> values(Consumer<ValuesParens> consumer) {
            this.endDispatcher();

            return PostgreSimpleValues.fromSubDispatcher(this, this.function)
                    .values(consumer);
        }

        @Override
        public _StaticValuesRowClause<I> values() {
            this.endDispatcher();

            return PostgreSimpleValues.fromSubDispatcher(this, this.function)
                    .values();
        }

        @Override
        PostgreQueries<I> createSelectClause() {
            this.endDispatcher();

            return PostgreQueries.fromSubDispatcher(this, this.function);
        }


    } // SubValuesDispatcher


}
