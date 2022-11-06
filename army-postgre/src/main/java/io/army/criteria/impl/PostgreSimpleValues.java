package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.postgre.PostgreCtes;
import io.army.criteria.postgre.PostgreQuery;
import io.army.criteria.postgre.PostgreStatement;
import io.army.criteria.postgre.PostgreValues;
import io.army.dialect.Dialect;
import io.army.dialect.postgre.PostgreDialect;
import io.army.lang.Nullable;
import io.army.util._Exceptions;

import java.util.function.Consumer;
import java.util.function.Function;

abstract class PostgreSimpleValues<I extends Item> extends SimpleValues.WithSimpleValues<
        I,
        PostgreCtes,
        PostgreValues.ValuesSpec<I>,
        PostgreValues._ValuesLeftParenSpec<I>,
        PostgreValues._OrderByCommaSpec<I>,
        PostgreValues._OffsetSpec<I>,
        PostgreValues._FetchSpec<I>,
        PostgreStatement._AsValuesClause<I>,
        PostgreValues._QueryWithComplexSpec<I>>
        implements PostgreValues._WithSpec<I>
        , PostgreValues._ValuesLeftParenSpec<I>
        , PostgreValues._OrderByCommaSpec<I>
        , PostgreValues._OffsetSpec<I>
        , PostgreValues {


    static <I extends Item> PostgreSimpleValues<I> primaryValues(@Nullable _WithClauseSpec spec
            , @Nullable CriteriaContext outerContext, Function<Values, I> function) {
        return new SimplePrimaryValues<>(spec, outerContext, function);
    }

    static <I extends Item> PostgreSimpleValues<I> subValues(@Nullable _WithClauseSpec spec
            , CriteriaContext outerContext, Function<SubValues, I> function) {
        return new SimpleSubValues<>(spec, outerContext, function);
    }


    private PostgreSimpleValues(@Nullable _WithClauseSpec spec, CriteriaContext context) {
        super(spec, context);
    }


    @Override
    public final _StaticCteLeftParenSpec<_CteComma<I>> with(String name) {
        final boolean recursive = false;
        this.context.onBeforeWithClause(recursive);
        return new CteComma<>(recursive, this).function.apply(name);
    }

    @Override
    public final _StaticCteLeftParenSpec<_CteComma<I>> withRecursive(String name) {
        final boolean recursive = true;
        this.context.onBeforeWithClause(recursive);
        return new CteComma<>(recursive, this).function.apply(name);
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
    public final _LimitSpec<I> orderBy(Consumer<SortItems> consumer) {
        consumer.accept(new OrderBySortItems(this));
        if (!this.hasOrderByClause()) {
            throw ContextStack.criteriaError(this.context, _Exceptions::sortItemListIsEmpty);
        }
        return this;
    }

    @Override
    public final _LimitSpec<I> ifOrderBy(Consumer<SortItems> consumer) {
        consumer.accept(new OrderBySortItems(this));
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
            implements Values {

        private final Function<? super Values, I> function;


        private SimplePrimaryValues(@Nullable _WithClauseSpec spec, @Nullable CriteriaContext outerContext
                , Function<? super Values, I> function) {
            super(spec, CriteriaContexts.primaryValuesContext(spec, outerContext));
            this.function = function;
        }

        @Override
        public ValuesSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketValues<I> bracket;
            bracket = new BracketValues<>(null, this.context, this::bracketEnd);
            return new SimplePrimaryValues<>(null, bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        I onAsValues() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createUnionValues(final UnionType unionType) {
            final Function<RowSet, I> unionFun;
            unionFun = right -> this.function.apply(new UnionValues( this, unionType, right));
            UnionType.exceptType(this.context, unionType);
            return new ComplexValues<>(this.context.getOuterContext(), unionFun);
        }

        private I bracketEnd(Values values) {
            ContextStack.pop(this.context)
                    .endContext();
            return this.function.apply(values);
        }

    }//SimplePrimaryValues


    private static final class SimpleSubValues<I extends Item> extends PostgreSimpleValues<I>
            implements SubValues {

        private final Function<? super SubValues, I> function;

        private SimpleSubValues(@Nullable _WithClauseSpec spec, CriteriaContext outerContext
                , Function<? super SubValues, I> function) {
            super(spec, CriteriaContexts.subValuesContext(spec, outerContext));
            this.function = function;
        }

        @Override
        public ValuesSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketSubValues<I> bracket;
            bracket = new BracketSubValues<>(null, this.context, this::bracketEnd);
            return new SimpleSubValues<>(null, bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        I onAsValues() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createUnionValues(final UnionType unionType) {
            UnionType.exceptType(this.context, unionType);
            final Function<RowSet, I> unionFun;
            unionFun = rowSet -> this.function.apply(new UnionSubValues(this, unionType, rowSet));
            return new ComplexSubValues<>(this.context.getNonNullOuterContext(), unionFun);
        }

        private I bracketEnd(SubValues values) {
            ContextStack.pop(this.context)
                    .endContext();
            return this.function.apply(values);
        }

    }//SimpleSubValues


    private static abstract class PostgreBracketValues<I extends Item> extends BracketRowSet<
            I,
            PostgreValues._UnionOrderBySpec<I>,
            PostgreValues._UnionOrderByCommaSpec<I>,
            PostgreValues._UnionOffsetSpec<I>,
            PostgreValues._UnionFetchSpec<I>,
            PostgreValues._AsValuesClause<I>,
            PostgreValues._QueryWithComplexSpec<I>>
            implements PostgreValues
            , PostgreValues._UnionOrderBySpec<I>
            , PostgreValues._UnionOrderByCommaSpec<I>
            , PostgreValues._UnionOffsetSpec<I>
            , PostgreValues._UnionFetchSpec<I> {

        private PostgreBracketValues(@Nullable _WithClauseSpec spec, @Nullable CriteriaContext outerContext) {
            super(CriteriaContexts.bracketContext(spec, outerContext));
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
            implements Values {

        private final Function<? super Values, I> function;

        private BracketValues(@Nullable _WithClauseSpec spec, @Nullable CriteriaContext outerContext
                , Function<? super Values, I> function) {
            super(spec, outerContext);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createUnionRowSet(final UnionType unionType) {
            UnionType.exceptType(this.context, unionType);
            final Function<RowSet, I> unionFun;
            unionFun = rowSet -> this.function.apply(new UnionValues( this, unionType, rowSet));
            return new ComplexValues<>(this.context.getOuterContext(), unionFun);
        }


    }//BracketValues


    private static final class BracketSubValues<I extends Item> extends PostgreBracketValues<I>
            implements SubValues {

        private final Function<? super SubValues, I> function;

        private BracketSubValues(@Nullable _WithClauseSpec spec, CriteriaContext outerContext
                , Function<? super SubValues, I> function) {
            super(spec, outerContext);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _QueryWithComplexSpec<I> createUnionRowSet(final UnionType unionType) {
            UnionType.exceptType(this.context, unionType);
            final Function<RowSet, I> unionFun;
            unionFun = rowSet -> this.function.apply(new UnionSubValues(this, unionType, rowSet));
            return new ComplexSubValues<>(this.context.getNonNullOuterContext(), unionFun);
        }


    }//BracketSubValues


    private static abstract class PostgreComplexValues<I extends Item>
            extends SimpleQueries.WithBuilderSelectClauseDispatcher<
            PostgreCtes,
            _QueryComplexSpec<I>,
            Postgres.Modifier,
            PostgreQuery._FromSpec<I>>
            implements _QueryWithComplexSpec<I> {

        final Function<RowSet, I> function;

        private PostgreComplexValues(@Nullable CriteriaContext outerContext, Function<RowSet, I> function) {
            super(outerContext);
            this.function = function;
        }

        @Override
        final PostgreCtes createCteBuilder(boolean recursive, CriteriaContext withClauseContext) {
            return PostgreSupports.postgreCteBuilder(recursive, withClauseContext);
        }


    }//PostgreComplexValues

    private static final class ComplexValues<I extends Item> extends PostgreComplexValues<I> {

        private ComplexValues(@Nullable CriteriaContext outerContext, Function<RowSet, I> function) {
            super(outerContext, function);
        }

        @Override
        public _QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketValues<I> bracket;
            bracket = new BracketValues<>(this.getWithClause(), this.outerContext, this.function);
            return new ComplexValues<>(bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        public _OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            return new SimplePrimaryValues<>(this.getWithClause(), this.outerContext, this.function)
                    .values(consumer);
        }

        @Override
        public _PostgreValuesLeftParenClause<I> values() {
            return new SimplePrimaryValues<>(this.getWithClause(), this.outerContext, this.function)
                    .values();
        }

        @Override
        PostgreQueries<I> onSelectClause(@Nullable _WithClauseSpec spec) {
            return PostgreQueries.primaryQuery(spec, this.outerContext, this::queryEnd);
        }

        private I queryEnd(Select query) {
            return this.function.apply(query);
        }

    }//ComplexValues

    private static final class ComplexSubValues<I extends Item> extends PostgreComplexValues<I> {


        private ComplexSubValues(CriteriaContext outerContext, Function<RowSet, I> function) {
            super(outerContext, function);
        }

        @Override
        public _QueryWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            final BracketSubValues<I> bracket;
            bracket = new BracketSubValues<>(this.getWithClause(), outerContext, this.function);
            return new ComplexSubValues<>(bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        public _OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            return new SimpleSubValues<>(this.getWithClause(), outerContext, this.function)
                    .values(consumer);
        }

        @Override
        public _PostgreValuesLeftParenClause<I> values() {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            return new SimpleSubValues<>(this.getWithClause(), outerContext, this.function)
                    .values();
        }

        @Override
        PostgreQueries<I> onSelectClause(@Nullable _WithClauseSpec spec) {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            return PostgreQueries.subQuery(spec, outerContext, this::queryEnd);
        }

        private I queryEnd(SubQuery query) {
            return this.function.apply(query);
        }

    }//ComplexSubValues

    private static final class CteComma<I extends Item> implements _CteComma<I> {

        private final boolean recursive;

        private final PostgreSimpleValues<I> statement;

        private final Function<String, _StaticCteLeftParenSpec<_CteComma<I>>> function;

        private CteComma(boolean recursive, PostgreSimpleValues<I> statement) {
            this.recursive = recursive;
            this.statement = statement;
            this.function = PostgreQueries.complexCte(statement.context, this);
        }

        @Override
        public _StaticCteLeftParenSpec<_CteComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            return this.statement.endStaticWithClause(this.recursive)
                    .values(consumer);
        }

        @Override
        public _PostgreValuesLeftParenClause<I> values() {
            return this.statement.endStaticWithClause(this.recursive)
                    .values();
        }


    }//CteComma

}
