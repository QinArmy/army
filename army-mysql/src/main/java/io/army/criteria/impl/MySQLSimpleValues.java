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
import io.army.criteria.mysql.MySQLCtes;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLValues;
import io.army.dialect.Dialect;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class MySQLSimpleValues<I extends Item>
        extends SimpleValues<
        I,
        MySQLValues._OrderByCommaSpec<I>,
        MySQLValues._LimitSpec<I>,
        Statement._AsValuesClause<I>,
        Object,
        Object,
        MySQLValues._ValueWithComplexSpec<I>>
        implements MySQLValues.ValuesSpec<I>,
        ValuesRows,
        MySQLValues._StaticValuesRowClause<I>,
        MySQLValues._StaticValuesRowCommaSpec<I>,
        MySQLValues._OrderByCommaSpec<I>,
        ArmyStmtSpec,
        MySQLValues {

    static <I extends Item> ValuesSpec<I> simpleValues(Function<? super Values, I> function) {
        return new SimplePrimaryValues<>(null, null, function, null);
    }


    static <I extends Item> ValuesSpec<I> fromDispatcher(ArmyStmtSpec spec,
                                                         Function<? super Values, I> function) {
        return new SimplePrimaryValues<>(spec, null, function, null);
    }

    static <I extends Item> ValuesSpec<I> subValues(CriteriaContext outerContext,
                                                    Function<? super SubValues, I> function) {
        return new SimpleSubValues<>(null, outerContext, function, null);
    }

    static <I extends Item> ValuesSpec<I> fromSubDispatcher(ArmyStmtSpec spec,
                                                            Function<? super SubValues, I> function) {
        return new SimpleSubValues<>(spec, null, function, null);
    }


    private MySQLSimpleValues(CriteriaContext context) {
        super(context);
    }

    @Override
    public final _OrderBySpec<I> values(Consumer<ValuesRows> consumer) {
        CriteriaUtils.invokeConsumer(this, consumer);
        return this;
    }

    @Override
    public final _StaticValuesRowClause<I> values() {
        return this;
    }


    @Override
    public final MySQLSimpleValues<I> row(Consumer<Values._ValueStaticColumnSpaceClause> consumer) {
        CriteriaUtils.invokeConsumer(this, consumer);
        endCurrentRow();
        return this;
    }

    @Override
    public final MySQLSimpleValues<I> row(SQLs.SymbolSpace space, Consumer<Values._ValuesDynamicColumnClause> consumer) {
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
    final String columnAlias(final int columnIndex) {
        return "column_" + columnIndex;
    }

    @Override
    final Dialect statementDialect() {
        return MySQLUtils.DIALECT;
    }

    private static final class SimplePrimaryValues<I extends Item> extends MySQLSimpleValues<I>
            implements ArmyValues {

        private final Function<? super Values, I> function;

        private SimplePrimaryValues(@Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerBracketContext,
                                    Function<? super Values, I> function, @Nullable CriteriaContext leftContext) {
            super(CriteriaContexts.primaryValuesContext(MySQLUtils.DIALECT, spec, outerBracketContext, leftContext));
            this.function = function;
        }


        @Override
        public _UnionOrderBySpec<I> parens(Function<ValuesSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketValues<I> bracket;
            bracket = new BracketValues<>(this, this.function);
            return CriteriaUtils.invokeFunction(function, new SimplePrimaryValues<>(null, bracket.context, bracket::parensEnd, null));
        }

        @Override
        I onAsValues() {
            return this.function.apply(this);
        }

        @Override
        _ValueWithComplexSpec<I> createUnionValues(final _UnionType unionType) {

            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionValues(this, unionType, rowSet));
            return new ValuesDispatcher<>(this.context, unionFunc);
        }


    } // SimplePrimaryValues

    private static final class SimpleSubValues<I extends Item> extends MySQLSimpleValues<I>
            implements ArmySubValues {

        private final Function<? super SubValues, I> function;

        private SimpleSubValues(@Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerContext,
                                Function<? super SubValues, I> function, @Nullable CriteriaContext leftContext) {
            super(CriteriaContexts.subValuesContext(MySQLUtils.DIALECT, spec, outerContext, leftContext));
            this.function = function;
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<ValuesSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketSubValues<I> bracket;
            bracket = new BracketSubValues<>(this, this.function);
            return CriteriaUtils.invokeFunction(function, new SimpleSubValues<>(null, bracket.context, bracket::parensEnd, null));
        }


        @Override
        I onAsValues() {
            return this.function.apply(this);
        }

        @Override
        _ValueWithComplexSpec<I> createUnionValues(final _UnionType unionType) {

            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSubValues(this, unionType, rowSet));
            return new SubValuesDispatcher<>(this.context, unionFunc);
        }

    }//SimpleSubValueValues


    static abstract class MySQLBracketValues<I extends Item>
            extends BracketRowSet<
            I,
            MySQLValues._UnionOrderBySpec<I>,
            MySQLValues._UnionOrderByCommaSpec<I>,
            MySQLValues._UnionLimitSpec<I>,
            _AsValuesClause<I>,
            Object,
            Object,
            MySQLValues._ValueWithComplexSpec<I>>
            implements MySQLValues._UnionOrderBySpec<I>,
            MySQLValues._UnionOrderByCommaSpec<I>,
            MySQLValues {

        private MySQLBracketValues(ArmyStmtSpec spec) {
            super(spec);
        }

        @Override
        public final I asValues() {
            return this.asQuery();
        }

        @Override
        final Dialect statementDialect() {
            return MySQLUtils.DIALECT;
        }


    }//MySQLBracketValues

    private static final class BracketValues<I extends Item> extends MySQLBracketValues<I>
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
        _ValueWithComplexSpec<I> createUnionRowSet(final _UnionType unionType) {
            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionValues(this, unionType, rowSet));
            return new ValuesDispatcher<>(this.context, unionFunc);
        }

    } // BracketValues

    private static final class BracketSubValues<I extends Item> extends MySQLBracketValues<I>
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
        _ValueWithComplexSpec<I> createUnionRowSet(final _UnionType unionType) {

            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSubValues(this, unionType, rowSet));
            return new SubValuesDispatcher<>(this.context, unionFunc);
        }

    } // BracketSubValues


    private static abstract class MySQLValuesDispatcher<I extends Item>
            extends SimpleQueries.WithBuilderSelectClauseDispatcher<
            MySQLCtes,
            _SelectComplexCommandSpec<I>,
            MySQLs.Modifier,
            MySQLQuery._MySQLSelectCommaSpec<I>,
            MySQLQuery._FromSpec<I>>
            implements _ValueWithComplexSpec<I> {


        final Function<RowSet, I> function;

        private MySQLValuesDispatcher(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(MySQLUtils.DIALECT, leftContext.getOuterContext(), leftContext);
            this.function = function;
        }

        private MySQLValuesDispatcher(MySQLBracketValues<?> bracket, Function<RowSet, I> function) {
            super(MySQLUtils.DIALECT, bracket.context, null);
            this.function = function;
        }


        @Override
        public final MySQLQuery._StaticCteParensSpec<_SelectComplexCommandSpec<I>> with(String name) {
            return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final MySQLQuery._StaticCteParensSpec<_SelectComplexCommandSpec<I>> withRecursive(String name) {
            return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        final MySQLCtes createCteBuilder(boolean recursive) {
            return MySQLSupports.mysqlLCteBuilder(recursive, this.context);
        }


    } // MySQLValuesDispatcher


    private static final class ValuesDispatcher<I extends Item> extends MySQLValuesDispatcher<I> {

        private ValuesDispatcher(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(leftContext, function);
        }

        private ValuesDispatcher(BracketValues<?> bracket, Function<RowSet, I> function) {
            super(bracket, function);
        }


        @Override
        public _UnionOrderBySpec<I> parens(Function<_ValueWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endDispatcher();

            final BracketValues<I> bracket;
            bracket = new BracketValues<>(this, this.function);

            return function.apply(new ValuesDispatcher<>(bracket, bracket::parensEnd));
        }

        @Override
        public _OrderBySpec<I> values(Consumer<ValuesRows> consumer) {
            this.endDispatcher();

            return MySQLSimpleValues.fromDispatcher(this, this.function)
                    .values(consumer);
        }

        @Override
        public _StaticValuesRowClause<I> values() {
            this.endDispatcher();

            return MySQLSimpleValues.fromDispatcher(this, this.function)
                    .values();
        }

        @Override
        MySQLQueries<I> createSelectClause() {
            this.endDispatcher();

            return MySQLQueries.fromDispatcher(this, this.function);
        }

    } // ValuesDispatcher

    private static final class SubValuesDispatcher<I extends Item> extends MySQLValuesDispatcher<I> {

        private SubValuesDispatcher(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(leftContext, function);
        }

        private SubValuesDispatcher(BracketSubValues<?> bracket, Function<RowSet, I> function) {
            super(bracket, function);
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<_ValueWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endDispatcher();

            final BracketSubValues<I> bracket;
            bracket = new BracketSubValues<>(this, this.function);

            return function.apply(new SubValuesDispatcher<>(bracket, bracket::parensEnd));
        }

        @Override
        public _OrderBySpec<I> values(Consumer<ValuesRows> consumer) {
            this.endDispatcher();

            return MySQLSimpleValues.fromSubDispatcher(this, this.function)
                    .values(consumer);
        }

        @Override
        public _StaticValuesRowClause<I> values() {
            this.endDispatcher();

            return MySQLSimpleValues.fromSubDispatcher(this, this.function)
                    .values();
        }

        @Override
        MySQLQueries<I> createSelectClause() {
            this.endDispatcher();

            return MySQLQueries.fromSubDispatcher(this, this.function);
        }

    } // SubValuesDispatcher


}
