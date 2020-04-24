package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect.DQL;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class ComposeSubQueries<C> extends AbstractComposeQuery<C> implements ComposeSubQuery,
        SubQuery.SubQueryUnionAble<C> {

    static <C> SubQuery.SubQueryUnionAble<C> brackets(C criteria, SubQuery encloseSubQuery) {
        return new ComposeSubQueries.BracketsSubQuery<>(criteria, encloseSubQuery);
    }

    static <C, S extends SubQuery> SubQuery.SubQueryUnionAble<C> compose(C criteria, SubQuery leftQuery
            , SQLModifier modifier
            , Function<C, S> function) {
        return new ComposeSubQueryImpl<>(criteria, leftQuery, modifier, function.apply(criteria));
    }


    private ComposeSubQueries(C criteria) {
        super(criteria);
    }

    @Override
    public String toString() {
        return "#ComposeSubQuery@" + System.identityHashCode(this);
    }


    @Override
    public final SubQueryUnionAble<C> brackets() {
        return new BracketsSubQuery<>(this.criteria, this);
    }

    @Override
    public final <S extends SubQuery> SubQueryUnionAble<C> union(Function<C, S> function) {
        return compose(this.criteria, this, UnionType.UNION, function);
    }

    @Override
    public final <S extends SubQuery> SubQueryUnionAble<C> unionAll(Function<C, S> function) {
        return compose(this.criteria, this, UnionType.UNION_ALL, function);
    }

    @Override
    public final <S extends SubQuery> SubQueryUnionAble<C> unionDistinct(Function<C, S> function) {
        return compose(this.criteria, this, UnionType.UNION_DISTINCT, function);
    }

    /*################################## blow SubQueryOrderByClause method ##################################*/

    @Override
    public final SubQueryLimitClause<C> orderBy(SortPart sortPart) {
        doOrderBy(sortPart);
        return this;
    }

    @Override
    public final SubQueryLimitClause<C> orderBy(List<SortPart> sortPartList) {
        doOrderBy(sortPartList);
        return this;
    }

    @Override
    public final SubQueryLimitClause<C> orderBy(Function<C, List<SortPart>> function) {
        doOrderBy(function);
        return this;
    }

    @Override
    public final SubQueryLimitClause<C> ifOrderBy(Predicate<C> test, SortPart sortPart) {
        doOrderBy(test, sortPart);
        return this;
    }

    @Override
    public final SubQueryLimitClause<C> ifOrderBy(Predicate<C> test, Function<C, List<SortPart>> function) {
        doOrderBy(test, function);
        return this;
    }

    /*################################## blow SubQueryLimitClause method ##################################*/

    @Override
    public final SubQueryAble limit(int rowCount) {
        doLimit(rowCount);
        return this;
    }

    @Override
    public final SubQueryAble limit(int offset, int rowCount) {
        doLimit(offset, rowCount);
        return this;
    }

    @Override
    public final SubQueryAble limit(Function<C, Pair<Integer, Integer>> function) {
        doLimit(function);
        return this;
    }

    @Override
    public final SubQueryAble ifLimit(Predicate<C> predicate, int rowCount) {
        doLimit(predicate, rowCount);
        return this;
    }

    @Override
    public final SubQueryAble ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        doLimit(predicate, offset, rowCount);
        return this;
    }

    @Override
    public final SubQueryAble ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function) {
        doLimit(predicate, function);
        return this;
    }

    /*################################## blow SelectAble method ##################################*/

    @Override
    public final SubQuery asSubQuery() {
        asQuery();
        return this;
    }

    @Override
    public void clear() {

    }


    /*################################## blow static inner class ##################################*/

    private static final class BracketsSubQuery<C> extends ComposeSubQueries<C> {

        private final SubQuery encloseSubQuery;

        public BracketsSubQuery(C criteria, SubQuery encloseSubQuery) {
            super(criteria);
            this.encloseSubQuery = encloseSubQuery;
        }

        @Override
        void beforePart(SQLContext context) {
            StringBuilder builder = context.sqlBuilder()
                    .append(" (");
            context.dql().subQuery(this.encloseSubQuery, context);
            builder.append(" )");
        }

        @Override
        public List<SelectPart> selectPartList() {
            return encloseSubQuery.selectPartList();
        }

        @Override
        public Selection selection(String derivedFieldName) {
            return encloseSubQuery.selection(derivedFieldName);
        }
    }

    private static final class ComposeSubQueryImpl<C> extends ComposeSubQueries<C> {

        private final SubQuery leftSubQuery;

        private final SQLModifier modifier;

        private final SubQuery rightSubQuery;

        public ComposeSubQueryImpl(C criteria, SubQuery leftSubQuery, SQLModifier modifier, SubQuery rightSubQuery) {
            super(criteria);
            this.leftSubQuery = leftSubQuery;
            this.modifier = modifier;
            this.rightSubQuery = rightSubQuery;
        }

        @Override
        void beforePart(SQLContext context) {
            DQL dql = context.dql();
            dql.subQuery(leftSubQuery, context);

            context.sqlBuilder().append(" ")
                    .append(modifier.render());

            dql.subQuery(rightSubQuery, context);

        }

        @Override
        public List<SelectPart> selectPartList() {
            return leftSubQuery.selectPartList();
        }

        @Override
        public Selection selection(String derivedFieldName) {
            return leftSubQuery.selection(derivedFieldName);
        }
    }

}
