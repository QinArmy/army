package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.mysql.MySQLCtes;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLValues;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.util._Exceptions;

import java.util.function.Consumer;
import java.util.function.Function;

abstract class MySQLSimpleValues<I extends Item>
        extends SimpleValues<
        I,
        MySQLValues._ValuesLeftParenSpec<I>,
        MySQLValues._OrderByCommaSpec<I>,
        Statement._AsValuesClause<I>,
        Object,
        Object,
        MySQLValues._ValueWithComplexSpec<I>>
        implements MySQLValues._ValueSpec<I>
        , MySQLValues._ValuesLeftParenSpec<I>
        , MySQLValues._OrderByCommaSpec<I>
        , MySQLValues {

    static <I extends Item> MySQLValues._ValueSpec<I> primaryValues(final @Nullable CriteriaContext outerContext
            , Function<Values, I> function) {
        return new SimplePrimaryValues<>(outerContext, function);
    }

    static <I extends Item> MySQLValues._ValueSpec<I> subValues(CriteriaContext outerContext
            , Function<SubValues, I> function) {
        return new SimpleSubValues<>(outerContext, function);
    }


    private MySQLSimpleValues(CriteriaContext context) {
        super(context);
    }

    @Override
    public final _OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
        consumer.accept(new RowConstructorImpl(this));
        return this;
    }

    @Override
    public final _ValuesLeftParenClause<I> values() {
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
    final String columnAlias(final int columnIndex) {
        return "column_" + columnIndex;
    }

    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL80;
    }

    private static final class SimplePrimaryValues<I extends Item> extends MySQLSimpleValues<I>
            implements Values {

        private final Function<? super Values, I> function;

        private SimplePrimaryValues(@Nullable CriteriaContext outerContext, Function<? super Values, I> function) {
            super(CriteriaContexts.primaryValuesContext(null, outerContext));
            this.function = function;
        }

        @Override
        public _ValueSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketValues<I> bracket;
            bracket = new BracketValues<>(null, this.context, this::bracketEnd);
            return new SimplePrimaryValues<>(bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        I onAsValues() {
            return this.function.apply(this);
        }

        @Override
        _ValueWithComplexSpec<I> createUnionValues(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);

            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionValues( this, unionType, rowSet));
            return new ComplexValues<>(this.context.getOuterContext(), unionFunc);
        }

        private I bracketEnd(Values values) {
            ContextStack.pop(this.context)
                    .endContext();
            return this.function.apply(values);
        }


    }//SimplePrimaryValues

    private static final class SimpleSubValues<I extends Item> extends MySQLSimpleValues<I>
            implements SubValues {

        private final Function<? super SubValues, I> function;

        private SimpleSubValues(CriteriaContext outerContext, Function<? super SubValues, I> function) {
            super(CriteriaContexts.subValuesContext(null, outerContext));
            this.function = function;
        }

        @Override
        public _ValueSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketSubValues<I> bracket;
            bracket = new BracketSubValues<>(null, this.context, this::bracketEnd);
            return new SimpleSubValues<>(bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        I onAsValues() {
            return this.function.apply(this);
        }

        @Override
        _ValueWithComplexSpec<I> createUnionValues(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);

            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSubValues(this, unionType, rowSet));
            final CriteriaContext outerContext;
            outerContext = this.context.getOuterContext();
            assert outerContext != null;
            return new ComplexSubValues<>(outerContext, unionFunc);
        }

        private I bracketEnd(SubValues values) {
            ContextStack.pop(this.context)
                    .endContext();
            return this.function.apply(values);
        }


    }//SimpleSubValueValues


     static abstract class MySQLBracketValues<I extends Item>
             extends BracketRowSet<
             I,
             MySQLValues._UnionOrderBySpec<I>,
             MySQLValues._UnionOrderByCommaSpec<I>,
             _AsValuesClause<I>,
             Object,
             Object,
             MySQLValues._ValueWithComplexSpec<I>>
             implements MySQLValues._UnionOrderBySpec<I>
             , MySQLValues {

        private MySQLBracketValues(@Nullable _WithClauseSpec spec, @Nullable CriteriaContext outerContext) {
            super(CriteriaContexts.bracketContext(spec, outerContext));
        }

        @Override
        public final _UnionLimitSpec<I> orderBy(Consumer<SortItems> consumer) {
            consumer.accept(new OrderBySortItems(this));
            if (!this.hasOrderByClause()) {
                throw ContextStack.criteriaError(this.context, _Exceptions::sortItemListIsEmpty);
            }
            return this;
        }

        @Override
        public final _UnionLimitSpec<I> ifOrderBy(Consumer<SortItems> consumer) {
            consumer.accept(new OrderBySortItems(this));
            return this;
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
        _ValueWithComplexSpec<I> createUnionRowSet(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);

            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionValues( this, unionType, rowSet));
            return new ComplexValues<>(this.context.getOuterContext(), unionFunc);
        }

    }//BracketValues

    private static final class BracketSubValues<I extends Item> extends MySQLBracketValues<I>
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
        _ValueWithComplexSpec<I> createUnionRowSet(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);

            final CriteriaContext outerContext;
            outerContext = this.context.getOuterContext();
            assert outerContext != null;

            final Function<RowSet, I> unionFunc;
            unionFunc = rowSet -> this.function.apply(new UnionSubValues(this, unionType, rowSet));
            return new ComplexSubValues<>(outerContext, unionFunc);
        }

    }//BracketSubValues


    private static final class ComplexSubValues<I extends Item>
            extends SimpleQueries.WithBuilderSelectClauseDispatcher<
            MySQLCtes,
            MySQLQuery._SelectSpec<I>,
            MySQLs.Modifier,
            MySQLQuery._MySQLSelectCommaSpec<I>,
            MySQLQuery._FromSpec<I>>
            implements _ValueWithComplexSpec<I> {


        private final Function<RowSet, I> function;

        private ComplexSubValues(CriteriaContext outerContext, Function<RowSet, I> function) {
            super(outerContext);
            this.function = function;
        }

        @Override
        public _OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            return new SimpleSubValues<>(outerContext, this.function)
                    .values(consumer);
        }

        @Override
        public _ValuesLeftParenClause<I> values() {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            return new SimpleSubValues<>(outerContext, this.function)
                    .values();
        }

        @Override
        public _ValueWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            final BracketSubValues<I> bracket;
            bracket = new BracketSubValues<>(this.getWithClause(), outerContext, this.function);
            return new ComplexSubValues<>(bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        MySQLCtes createCteBuilder(boolean recursive, CriteriaContext withClauseContext) {
            return MySQLSupports.mySQLCteBuilder(recursive, withClauseContext);
        }

        @Override
        MySQLQueries<I, ?> onSelectClause(final @Nullable _WithClauseSpec spec) {
            final CriteriaContext outerContext = this.outerContext;
            assert outerContext != null;
            return MySQLQueries.subQuery(spec, outerContext, this::queryEnd);
        }

        private I queryEnd(SubQuery query) {
            return this.function.apply(query);
        }


    }//ComplexSubValues


    private static final class ComplexValues<I extends Item>
            extends SimpleQueries.WithBuilderSelectClauseDispatcher<
            MySQLCtes,
            MySQLQuery._SelectSpec<I>,
            MySQLs.Modifier,
            MySQLQuery._MySQLSelectCommaSpec<I>,
            MySQLQuery._FromSpec<I>>
            implements _ValueWithComplexSpec<I> {


        private final Function<RowSet, I> function;

        /**
         * @see SimplePrimaryValues#createUnionValues(UnionType)
         */
        private ComplexValues(@Nullable CriteriaContext outerContext, Function<RowSet, I> function) {
            super(outerContext);
            this.function = function;
        }

        @Override
        public _OrderBySpec<I> values(Consumer<RowConstructor> consumer) {
            return new SimplePrimaryValues<>(this.outerContext, this.function)
                    .values(consumer);
        }

        @Override
        public _ValuesLeftParenClause<I> values() {
            return new SimplePrimaryValues<>(this.outerContext, this.function)
                    .values();
        }

        @Override
        public _ValueWithComplexSpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final BracketValues<I> bracket;
            bracket = new BracketValues<>(this.getWithClause(), this.outerContext, this.function);
            return new ComplexValues<>(bracket.context, bracket::parenRowSetEnd);
        }

        @Override
        MySQLCtes createCteBuilder(boolean recursive, CriteriaContext withClauseContext) {
            return MySQLSupports.mySQLCteBuilder(recursive, withClauseContext);
        }

        @Override
        MySQLQueries<I, ?> onSelectClause(final @Nullable _WithClauseSpec spec) {
            return MySQLQueries.primaryQuery(spec, this.outerContext, this::queryEnd);
        }

        private I queryEnd(Select query) {
            return this.function.apply(query);
        }


    }//ComplexValues


}
