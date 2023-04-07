package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.postgre.PostgreCtes;
import io.army.criteria.postgre.PostgreQuery;
import io.army.criteria.postgre.PostgreStatement;
import io.army.criteria.postgre.PostgreValues;
import io.army.dialect.Dialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This class is abstract implementation of {@link PostgreValues}.
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreSimpleValues<I extends Item> extends SimpleValues.WithSimpleValues<
        I,
        PostgreCtes,
        PostgreValues._ValuesSpec<I>,
        PostgreValues._ValuesLeftParenSpec<I>,
        PostgreValues._LimitSpec<I>,
        PostgreValues._OffsetSpec<I>,
        PostgreValues._FetchSpec<I>,
        PostgreStatement._AsValuesClause<I>,
        PostgreValues._QueryWithComplexSpec<I>>
        implements PostgreValues._WithSpec<I>,
        PostgreValues._ValuesLeftParenSpec<I>,
        PostgreValues._OffsetSpec<I>,
        PostgreValues {


    /**
     * <p>
     * create primary VALUES statement.
     * </p>
     */
    static PostgreValues._WithSpec<Values> simpleValues() {
        return new SimplePrimaryValues<>(null, null, SQLs::_identity);
    }

    /**
     * create primary VALUES statement for dispatcher.
     */
    static <I extends Item> PostgreValues._WithSpec<I> fromDispatcher(ArmyStmtSpec spec,
                                                                      Function<? super Values, I> function) {
        return new SimplePrimaryValues<>(spec, null, function);
    }

    /**
     * create sub VALUES statement for dispatcher.
     */
    static <I extends Item> PostgreValues._WithSpec<I> fromSubDispatcher(ArmyStmtSpec spec,
                                                                         Function<? super SubValues, I> function) {
        return new SimpleSubValues<>(spec, null, function);
    }

    /**
     * create sub VALUES statement.
     */
    static <I extends Item> PostgreValues._WithSpec<I> subValues(CriteriaContext outerContext,
                                                                 Function<? super SubValues, I> function) {
        return new SimpleSubValues<>(null, outerContext, function);
    }


    private PostgreSimpleValues(@Nullable _WithClauseSpec spec, CriteriaContext context) {
        super(spec, context);
    }


    @Override
    public final PostgreQuery._StaticCteParensSpec<_ValuesSpec<I>> with(String name) {
        return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final PostgreQuery._StaticCteParensSpec<_ValuesSpec<I>> withRecursive(String name) {
        return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final _OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
        consumer.accept(new RowConstructorImpl(this));
        return this;
    }

    @Override
    public final _PostgreValuesLeftParenClause<I> values() {
        return this;
    }

    @Override
    final Dialect statementDialect() {
        return PostgreDialect.POSTGRE15;
    }

    @Override
    final PostgreCtes createCteBuilder(boolean recursive) {
        return PostgreSupports.postgreCteBuilder(recursive, this.context);
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
                                    Function<? super Values, I> function) {
            super(spec, CriteriaContexts.primaryValuesContext(spec, outerBracketContext));
            this.function = function;
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<_WithSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
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
        _QueryWithComplexSpec<I> createUnionValues(final UnionType unionType) {
            final Function<RowSet, I> unionFun;
            unionFun = right -> this.function.apply(new UnionValues(this, unionType, right));
            return new ValuesDispatcher<>(this.context, unionFun);
        }

    }//SimplePrimaryValues


    private static final class SimpleSubValues<I extends Item> extends PostgreSimpleValues<I>
            implements ArmySubValues {

        private final Function<? super SubValues, I> function;

        private SimpleSubValues(@Nullable ArmyStmtSpec spec, @Nullable CriteriaContext outerContext,
                                Function<? super SubValues, I> function) {
            super(spec, CriteriaContexts.subValuesContext(spec, outerContext));
            this.function = function;
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<_WithSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endStmtBeforeCommand();

            final BracketSubValues<I> bracket;
            bracket = new BracketSubValues<>(this, this.function);

            return function.apply(PostgreSimpleValues.subValues(bracket.context, bracket::parensEnd));
        }

        @Override
        I onAsValues() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createUnionValues(final UnionType unionType) {
            final Function<RowSet, I> unionFun;
            unionFun = rowSet -> this.function.apply(new UnionSubValues(this, unionType, rowSet));
            return new SubValuesDispatcher<>(this.context, unionFun);
        }

    }//SimpleSubValues


     static abstract class PostgreBracketValues<I extends Item> extends BracketRowSet<
             I,
             PostgreValues._UnionOrderBySpec<I>,
             PostgreValues._UnionLimitSpec<I>,
             PostgreValues._UnionOffsetSpec<I>,
             PostgreValues._UnionFetchSpec<I>,
             PostgreValues._AsValuesClause<I>,
             PostgreValues._QueryWithComplexSpec<I>>
             implements PostgreValues,
             PostgreValues._UnionOrderBySpec<I>,
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


    }//PostgreBracketValues


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
        _QueryWithComplexSpec<I> createUnionRowSet(final UnionType unionType) {
            final Function<RowSet, I> unionFun;
            unionFun = rowSet -> this.function.apply(new UnionValues(this, unionType, rowSet));
            return new ValuesDispatcher<>(this.context, unionFun);
        }


    }//BracketValues


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
        _QueryWithComplexSpec<I> createUnionRowSet(final UnionType unionType) {
            final Function<RowSet, I> unionFun;
            unionFun = rowSet -> this.function.apply(new UnionSubValues(this, unionType, rowSet));
            return new SubValuesDispatcher<>(this.context, unionFun);
        }


    }//BracketSubValues


    private static abstract class PostgreValuesDispatcher<I extends Item>
            extends PostgreQueries.PostgreSelectClauseDispatcher<
            I,
            PostgreValues._QueryComplexSpec<I>>
            implements PostgreValues._QueryWithComplexSpec<I> {

        final Function<RowSet, I> function;

        private PostgreValuesDispatcher(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(leftContext.getOuterContext(), leftContext);
            this.function = function;
        }

        private PostgreValuesDispatcher(PostgreBracketValues<?> bracket, Function<RowSet, I> function) {
            super(bracket.context, null);
            this.function = function;
        }


        @Override
        public final PostgreQuery._StaticCteParensSpec<_QueryComplexSpec<I>> with(String name) {
            return PostgreQueries.complexCte(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final PostgreQuery._StaticCteParensSpec<_QueryComplexSpec<I>> withRecursive(String name) {
            return PostgreQueries.complexCte(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        final PostgreCtes createCteBuilder(boolean recursive, CriteriaContext context) {
            return PostgreSupports.postgreCteBuilder(recursive, context);
        }


    }//PostgreComplexValues

    private static final class ValuesDispatcher<I extends Item> extends PostgreValuesDispatcher<I> {

        private ValuesDispatcher(CriteriaContext leftContext, Function<RowSet, I> function) {
            super(leftContext, function);
        }

        private ValuesDispatcher(BracketValues<?> bracket, Function<RowSet, I> function) {
            super(bracket, function);
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<_QueryWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endDispatcher();

            final BracketValues<I> bracket;
            bracket = new BracketValues<>(this, this.function);

            return function.apply(new ValuesDispatcher<>(bracket, bracket::parensEnd));
        }

        @Override
        public PostgreValues._OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            this.endDispatcher();

            return PostgreSimpleValues.fromDispatcher(this, this.function)
                    .values(consumer);
        }


        @Override
        public _PostgreValuesLeftParenClause<I> values() {
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
            super(leftContext, function);
        }

        private SubValuesDispatcher(BracketSubValues<?> bracket, Function<RowSet, I> function) {
            super(bracket, function);
        }

        @Override
        public _UnionOrderBySpec<I> parens(Function<_QueryWithComplexSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> function) {
            this.endDispatcher();

            final BracketSubValues<I> bracket;
            bracket = new BracketSubValues<>(this, this.function);

            return function.apply(new SubValuesDispatcher<>(bracket, bracket::parensEnd));
        }


        @Override
        public _OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            this.endDispatcher();

            return PostgreSimpleValues.fromSubDispatcher(this, this.function)
                    .values(consumer);
        }

        @Override
        public _PostgreValuesLeftParenClause<I> values() {
            this.endDispatcher();

            return PostgreSimpleValues.fromSubDispatcher(this, this.function)
                    .values();
        }

        @Override
        PostgreQueries<I> createSelectClause() {
            this.endDispatcher();

            return PostgreQueries.fromSubDispatcher(this, this.function);
        }


    }//SubValuesDispatcher


}
