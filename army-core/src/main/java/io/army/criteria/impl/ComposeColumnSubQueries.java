package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class ComposeColumnSubQueries<E, C> extends AbstractComposeQuery<C> implements ComposeSubQuery
        , ColumnSubQuery<E>, ColumnSubQuery.ColumnSubQueryUnionAble<E, C> {

    static <E, C> ColumnSubQuery.ColumnSubQueryUnionAble<E, C> brackets(C criteria, ColumnSubQuery<E> encloseSubQuery) {
        return new BracketsColumnSubQuery<>(criteria, encloseSubQuery);
    }

    static <E, C, S extends ColumnSubQuery<E>> ColumnSubQuery.ColumnSubQueryUnionAble<E, C> compose(
            C criteria, ColumnSubQuery<E> leftQuery, SQLModifier modifier, Function<C, S> function) {
        return new ComposeColumnSubQueryImpl<>(criteria, leftQuery, modifier, function.apply(criteria));
    }


    private ComposeColumnSubQueries(C criteria) {
        super(criteria);
    }


    @Override
    public final String toString() {
        return "#ComposeColumnSubQuery@" + System.identityHashCode(this);
    }


    @Override
    public final ColumnSubQueryUnionAble<E, C> brackets() {
        return new BracketsColumnSubQuery<>(this.criteria, this);
    }

    @Override
    public final <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> union(Function<C, S> function) {
        return compose(this.criteria, this, UnionType.UNION, function);
    }

    @Override
    public final <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> unionAll(Function<C, S> function) {
        return compose(this.criteria, this, UnionType.UNION_ALL, function);
    }

    @Override
    public final <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> unionDistinct(Function<C, S> function) {
        return compose(this.criteria, this, UnionType.UNION_DISTINCT, function);
    }

    /*################################## blow ColumnSubQueryOrderByClause method ##################################*/

    @Override
    public ColumnSubQueryLimitClause<E, C> orderBy(SortPart sortPart) {
        doOrderBy(sortPart);
        return this;
    }

    @Override
    public ColumnSubQueryLimitClause<E, C> orderBy(List<SortPart> sortPartList) {
        doOrderBy(sortPartList);
        return this;
    }

    @Override
    public ColumnSubQueryLimitClause<E, C> orderBy(Function<C, List<SortPart>> function) {
        doOrderBy(function);
        return this;
    }

    @Override
    public ColumnSubQueryLimitClause<E, C> ifOrderBy(Predicate<C> test, SortPart sortPart) {
        doOrderBy(test, sortPart);
        return this;
    }

    @Override
    public ColumnSubQueryLimitClause<E, C> ifOrderBy(Predicate<C> test, Function<C, List<SortPart>> function) {
        doOrderBy(test, function);
        return this;
    }

    /*################################## blow ColumnSubQueryLimitClause method ##################################*/

    @Override
    public ColumnSubQueryAble<E> limit(int rowCount) {
        doLimit(rowCount);
        return this;
    }

    @Override
    public ColumnSubQueryAble<E> limit(int offset, int rowCount) {
        doLimit(offset, rowCount);
        return this;
    }

    @Override
    public ColumnSubQueryAble<E> limit(Function<C, Pair<Integer, Integer>> function) {
        doLimit(function);
        return this;
    }

    @Override
    public ColumnSubQueryAble<E> ifLimit(Predicate<C> predicate, int rowCount) {
        doLimit(predicate, rowCount);
        return this;
    }

    @Override
    public ColumnSubQueryAble<E> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        doLimit(predicate, offset, rowCount);
        return this;
    }

    @Override
    public ColumnSubQueryAble<E> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function) {
        doLimit(predicate, function);
        return this;
    }

    /*################################## blow ColumnSubQueryAble method ##################################*/

    @Override
    public final ColumnSubQuery<E> asSubQuery() {
        asQuery();
        return this;
    }


    /*################################## blow static inner class ##################################*/

    private static final class BracketsColumnSubQuery<E, C> extends ComposeColumnSubQueries<E, C> {

        private final ColumnSubQuery<E> encloseSubQuery;

        private BracketsColumnSubQuery(C criteria, ColumnSubQuery<E> encloseSubQuery) {
            super(criteria);
            this.encloseSubQuery = encloseSubQuery;
        }

        @Override
        final void beforePart(SQLContext context) {
            StringBuilder builder = context.sqlBuilder()
                    .append(" (");
            context.dql().subQuery(this.encloseSubQuery, context);
            builder.append(" )");
        }

        @Override
        public final List<SelectPart> selectPartList() {
            return encloseSubQuery.selectPartList();
        }

        @Override
        public final Selection selection(String derivedFieldName) {
            return encloseSubQuery.selection(derivedFieldName);
        }

        @Override
        public final Selection selection() {
            return encloseSubQuery.selection();
        }
    }

    private static final class ComposeColumnSubQueryImpl<E, C> extends ComposeColumnSubQueries<E, C> {

        private final ColumnSubQuery<E> leftSubQuery;

        private final SQLModifier modifier;

        private final ColumnSubQuery<E> rightSubQuery;

        private ComposeColumnSubQueryImpl(C criteria, ColumnSubQuery<E> leftSubQuery, SQLModifier modifier
                , ColumnSubQuery<E> rightSubQuery) {
            super(criteria);
            this.leftSubQuery = leftSubQuery;
            this.modifier = modifier;
            this.rightSubQuery = rightSubQuery;
        }

        @Override
        final void beforePart(SQLContext context) {

            context.dql().subQuery(leftSubQuery, context);

            context.sqlBuilder().append(" ")
                    .append(modifier.render())
                    .append(" ");

            context.dql().subQuery(rightSubQuery, context);

        }

        @Override
        public final List<SelectPart> selectPartList() {
            return leftSubQuery.selectPartList();
        }

        @Override
        public final Selection selection(String derivedFieldName) {
            return leftSubQuery.selection(derivedFieldName);
        }

        @Override
        public final Selection selection() {
            return leftSubQuery.selection();
        }
    }
}
