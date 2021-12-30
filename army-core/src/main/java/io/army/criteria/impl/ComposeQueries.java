package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._ComposeQuery;
import io.army.criteria.impl.inner._SortPart;
import io.army.criteria.impl.inner._StandardComposeQuery;
import io.army.dialect.Constant;
import io.army.dialect._SqlContext;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class ComposeQueries<Q extends Query, C> extends AbstractComposeQuery<Q, C> implements
        Query.StandardUnionResultSpec<Q, C>, _StandardComposeQuery {

    static <Q extends Query, C> StandardUnionResultSpec<Q, C> brackets(C criteria, Q enclosedQuery) {
        return new BracketsQuery<>(criteria, enclosedQuery);
    }

    @SuppressWarnings("unchecked")
    static <Q extends Query, C> StandardUnionResultSpec<Q, C> compose(C criteria, Q leftQuery, SQLModifier modifier
            , Supplier<Q> supplier) {
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

    @SuppressWarnings("unchecked")
    static <Q extends Query, C> StandardUnionResultSpec<Q, C> compose(C criteria, Q leftQuery, SQLModifier modifier
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

    static <C> StandardUnionResultSpec<Select, C> unionSelect(C criteria, Select left, UnionType unionType
            , Supplier<Select> supplier) {
        left.prepared();
        final Select right;
        right = supplier.get();
        assert right != null;
        return null;
    }

    static <C> StandardUnionResultSpec<Select, C> unionSelect(C criteria, Select left, UnionType unionType
            , Function<C, Select> function) {
        return null;
    }

    static void composeRightSelect(C criteria, Select left, UnionType unionType
            , Select select) {

    }

    static <C> StandardUnionResultSpec<Select, C> bracketsSelect(C criteria, Select select) {
        return null;
    }

    static Select bracketsSelect(Select select) {
        return new BracketsSelect(select);
    }


    private ComposeQueries(C criteria, Q firstSelect) {
        super(criteria, firstSelect);
    }


    @SuppressWarnings("unchecked")
    @Override
    public final StandardUnionResultSpec<Q, C> bracketsQuery() {
        return new BracketsQuery<>(criteria, (Q) this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final StandardUnionResultSpec<Q, C> union(Function<C, Q> function) {
        return compose(this.criteria, (Q) this, UnionType.UNION, function);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final StandardUnionResultSpec<Q, C> unionAll(Function<C, Q> function) {
        return compose(this.criteria, (Q) this, UnionType.UNION_ALL, function);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final StandardUnionResultSpec<Q, C> unionDistinct(Function<C, Q> function) {
        return compose(this.criteria, (Q) this, UnionType.UNION_DISTINCT, function);
    }

    /*################################## blow OrderByClause method ##################################*/

    @Override
    public final StandardQuery.StandardLimitClause<Q, C> orderBy(SortPart sortPart) {
        doOrderBy(sortPart);
        return this;
    }

    @Override
    public final StandardQuery.StandardLimitClause<Q, C> orderBy(SortPart sortPart1, SortPart sortPart2) {
        doOrderBy(Arrays.asList(sortPart1, sortPart2));
        return this;
    }

    @Override
    public final StandardQuery.StandardLimitClause<Q, C> orderBy(List<SortPart> sortPartList) {
        doOrderBy(sortPartList);
        return this;
    }

    @Override
    public final StandardQuery.StandardLimitClause<Q, C> orderBy(Function<C, List<SortPart>> function) {
        List<SortPart> list = function.apply(this.criteria);
        _Assert.notEmpty(list, "sortPartList must not empty.");
        doOrderBy(list);
        return this;
    }

    @Override
    public final StandardQuery.StandardLimitClause<Q, C> ifOrderBy(Function<C, List<SortPart>> function) {
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


    private static class BracketsQuery<Q extends Query> implements _ComposeQuery {

        private final Q query;

        private BracketsQuery(Q query) {
            query.prepared();
            this.query = query;
        }

        public final boolean requiredBrackets() {
            return false;
        }

        public final void prepared() {
            this.query.prepared();
        }


        @Override
        public final List<_SortPart> orderByList() {
            return Collections.emptyList();
        }

        @Override
        public final long offset() {
            return -1L;
        }

        @Override
        public final long rowCount() {
            return -1L;
        }

        @Override
        public final List<? extends SelectPart> selectPartList() {
            return Collections.emptyList();
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);
            final Q query = this.query;
            if (query instanceof Select) {
                context.dialect().select((Select) query, context);
            } else if (query instanceof SubQuery) {
                context.dialect().subQuery((SubQuery) query, context);
            } else {
                throw _Exceptions.unknownStatement(query, context.dialect().sessionFactory());
            }
            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);
        }

        @Override
        public final void clear() {
            //no-op
        }


    }

    private static final class BracketsSelect extends BracketsQuery<Select> implements Select {

        private BracketsSelect(Select query) {
            super(query);
        }


    }


}
