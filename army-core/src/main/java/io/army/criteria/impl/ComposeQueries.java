package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._StandardComposeQuery;
import io.army.dialect.DqlDialect;
import io.army.dialect.SqlBuilder;
import io.army.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class ComposeQueries<Q extends Query, C> extends AbstractComposeQuery<Q, C> implements
        Query.UnionSpec<Q, C>, _StandardComposeQuery {

    static <Q extends Query, C> UnionSpec<Q, C> brackets(C criteria, Q enclosedQuery) {
        return new BracketsQuery<>(criteria, enclosedQuery);
    }

    @SuppressWarnings("unchecked")
    static <Q extends Query, C> UnionSpec<Q, C> compose(C criteria, Q leftQuery, SQLModifier modifier
            , Function<C, Q> function) {
        Q left = leftQuery, right;
        if (left.requiredBrackets()) {
            left = (Q) new BracketsQuery<>(criteria, left);
        }
        right = function.apply(criteria);
        if (right.requiredBrackets()) {
            right = (Q) new BracketsQuery<>(criteria, right);
        }
        return new ComposeQueryImpl<>(criteria, left, modifier, right);
    }

    private ComposeQueries(C criteria, Q firstSelect) {
        super(criteria, firstSelect);
    }


    @SuppressWarnings("unchecked")
    @Override
    public final UnionSpec<Q, C> bracketsQuery() {
        return new BracketsQuery<>(criteria, (Q) this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final UnionSpec<Q, C> union(Function<C, Q> function) {
        return compose(this.criteria, (Q) this, UnionType.UNION, function);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final UnionSpec<Q, C> unionAll(Function<C, Q> function) {
        return compose(this.criteria, (Q) this, UnionType.UNION_ALL, function);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final UnionSpec<Q, C> unionDistinct(Function<C, Q> function) {
        return compose(this.criteria, (Q) this, UnionType.UNION_DISTINCT, function);
    }

    /*################################## blow OrderByClause method ##################################*/

    @Override
    public final LimitClause<Q, C> orderBy(SortPart sortPart) {
        doOrderBy(sortPart);
        return this;
    }

    @Override
    public final LimitClause<Q, C> orderBy(SortPart sortPart1, SortPart sortPart2) {
        doOrderBy(Arrays.asList(sortPart1, sortPart2));
        return this;
    }

    @Override
    public final LimitClause<Q, C> orderBy(List<SortPart> sortPartList) {
        doOrderBy(sortPartList);
        return this;
    }

    @Override
    public final LimitClause<Q, C> orderBy(Function<C, List<SortPart>> function) {
        List<SortPart> list = function.apply(this.criteria);
        Assert.notEmpty(list, "sortPartList must not empty.");
        doOrderBy(list);
        return this;
    }

    @Override
    public final LimitClause<Q, C> ifOrderBy(Function<C, List<SortPart>> function) {
        doOrderBy(function);
        return this;
    }


    /*################################## blow LimitClause method ##################################*/

    @Override
    public final QuerySpec<Q> limit(int rowCount) {
        doLimit(rowCount);
        return this;
    }

    @Override
    public final QuerySpec<Q> limit(int offset, int rowCount) {
        doLimit(offset, rowCount);
        return this;
    }


    @Override
    public final QuerySpec<Q> ifLimit(Predicate<C> predicate, int rowCount) {
        doLimit(predicate, rowCount);
        return this;
    }

    @Override
    public final QuerySpec<Q> ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        doLimit(predicate, offset, rowCount);
        return this;
    }

    @Override
    public final QuerySpec<Q> ifLimit(Function<C, LimitOption> function) {
        doIfLimit(function);
        return this;
    }


    /*################################## blow static inner class ##################################*/

    private static final class BracketsQuery<Q extends Query, C> extends ComposeQueries<Q, C> {

        private final Q enclosedQuery;

        BracketsQuery(C criteria, Q enclosedQuery) {
            super(criteria, enclosedQuery);
            this.enclosedQuery = enclosedQuery;
        }

        @Override
        public boolean requiredBrackets() {
            return false;
        }

        @Override
        public void appendSQL(_SqlContext context) {
            SqlBuilder builder = context.sqlBuilder()
                    .append(" (");
            if (this.enclosedQuery instanceof Select) {
                context.dql().select((Select) this.enclosedQuery, context);
            } else if (this.enclosedQuery instanceof SubQuery) {
                context.dql().subQuery((SubQuery) this.enclosedQuery, context);
            } else {
                throw new IllegalStateException(String.format("%s isn't Select or SubQuery.", this.enclosedQuery));
            }
            builder.append(" )");
        }

    }

    private static final class ComposeQueryImpl<Q extends Query, C> extends ComposeQueries<Q, C> {

        private final Q leftQuery;

        private final SQLModifier modifier;

        private final Q rightQuery;

        ComposeQueryImpl(C criteria, Q leftQuery, SQLModifier modifier, Q rightQuery) {
            super(criteria, leftQuery);
            this.leftQuery = leftQuery;
            this.modifier = modifier;
            this.rightQuery = rightQuery;
        }

        @Override
        public boolean requiredBrackets() {
            return true;
        }

        @Override
        public void appendSQL(_SqlContext context) {
            DqlDialect dql = context.dql();

            if (this.leftQuery instanceof Select) {
                dql.select((Select) this.leftQuery, context);

                context.sqlBuilder()
                        .append(" ")
                        .append(this.modifier.render())
                        .append(" ");

                dql.select((Select) this.rightQuery, context);
            } else if (this.leftQuery instanceof SubQuery) {
                dql.subQuery((SubQuery) this.leftQuery, context);

                context.sqlBuilder()
                        .append(" ")
                        .append(this.modifier.render())
                        .append(" ");

                dql.subQuery((SubQuery) this.rightQuery, context);
            } else {
                throw new IllegalStateException(String.format("%s isn't Select or SubQuery.", this.leftQuery));
            }

        }

    }

}
