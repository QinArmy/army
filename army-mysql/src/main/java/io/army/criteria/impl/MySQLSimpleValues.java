package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.mysql.MySQLCtes;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLValues;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class MySQLSimpleValues<I extends Item>
        extends SimpleValues<
        I,
        MySQLValues._ValuesLeftParenSpec<I>,
        MySQLValues._OrderByCommaSpec<I>,
        MySQLValues._LimitSpec<I>,
        Statement._AsValuesClause<I>,
        Object,
        Object,
        MySQLValues._ValueWithComplexSpec<I>>
        implements MySQLValues._ValueSpec<I>,
        MySQLValues._ValuesLeftParenSpec<I>,
        MySQLValues._OrderByCommaSpec<I>,
        ArmyStmtSpec,
        MySQLValues {

    static <I extends Item> MySQLValues._ValueSpec<I> simpleValues(Function<? super Values, I> function) {
        return new SimplePrimaryValues<>(null, null, function);
    }


    static <I extends Item> MySQLValues._ValueSpec<I> fromDispatcher(ArmyStmtSpec spec,
                                                                     Function<? super Values, I> function) {
        return new SimplePrimaryValues<>(spec, null, function);
    }

    static <I extends Item> MySQLValues._ValueSpec<I> subValues(CriteriaContext outerContext,
                                                                Function<? super SubValues, I> function) {
        return new SimpleSubValues<>(null, outerContext, function);
    }

    static <I extends Item> MySQLValues._ValueSpec<I> fromSubDispatcher(ArmyStmtSpec spec,
                                                                        Function<? super SubValues, I> function) {
        return new SimpleSubValues<>(spec, null, function);
    }


    private MySQLSimpleValues(CriteriaContext context) {
        super(context);
    }

    @Override
    public final _OrderBySpec<I> values(Consumer<ValuesRowConstructor> consumer) {
        consumer.accept(new RowConstructorImpl(this));
        return this;
    }

    @Override
    public final _ValuesLeftParenClause<I> values() {
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
                                    Function<? super Values, I> function) {
            super(CriteriaContexts.primaryValuesContext(spec, outerBracketContext));
            this.function = function;
        }


        @Override
        public _UnionOrderBySpec<I> parens(Function<_ValueSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketValues<I> bracket;
            bracket = new BracketValues<>(this, this.function);
            return function.apply(new SimplePrimaryValues<>(null, bracket.context, bracket::parensEnd));
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


    }//SimplePrimaryValues

    private static final class SimpleSubValues<I extends Item> extends MySQLSimpleValues<I>
            implements ArmySubValues {

        private final Function<? super SubValues, I> function;

        private SimpleSubValues(@Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerContext,
                                Function<? super SubValues, I> function) {
            super(CriteriaContexts.subValuesContext(spec, outerContext));
            this.function = function;
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<_ValueSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketSubValues<I> bracket;
            bracket = new BracketSubValues<>(this, this.function);
            return function.apply(MySQLSimpleValues.subValues(bracket.context, bracket::parensEnd));
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
            return MySQLDialect.MySQL80;
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

    }//BracketValues

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

    }//BracketSubValues


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
            super(leftContext.getOuterContext(), leftContext);
            this.function = function;
        }

        private MySQLValuesDispatcher(MySQLBracketValues<?> bracket, Function<RowSet, I> function) {
            super(bracket.context, null);
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
        final MySQLCtes createCteBuilder(boolean recursive, CriteriaContext context) {
            return MySQLSupports.mysqlLCteBuilder(recursive, context);
        }


    }//MySQLValuesDispatcher


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
        public _OrderBySpec<I> values(Consumer<ValuesRowConstructor> consumer) {
            this.endDispatcher();

            return MySQLSimpleValues.fromDispatcher(this, this.function)
                    .values(consumer);
        }

        @Override
        public _ValuesLeftParenClause<I> values() {
            this.endDispatcher();

            return MySQLSimpleValues.fromDispatcher(this, this.function)
                    .values();
        }

        @Override
        MySQLQueries<I> createSelectClause() {
            this.endDispatcher();

            return MySQLQueries.fromDispatcher(this, this.function);
        }


    }//ValuesDispatcher

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
        public _OrderBySpec<I> values(Consumer<ValuesRowConstructor> consumer) {
            this.endDispatcher();

            return MySQLSimpleValues.fromSubDispatcher(this, this.function)
                    .values(consumer);
        }

        @Override
        public _ValuesLeftParenClause<I> values() {
            this.endDispatcher();

            return MySQLSimpleValues.fromSubDispatcher(this, this.function)
                    .values();
        }

        @Override
        MySQLQueries<I> createSelectClause() {
            this.endDispatcher();

            return MySQLQueries.fromSubDispatcher(this, this.function);
        }

    }//SubValuesDispatcher


}
