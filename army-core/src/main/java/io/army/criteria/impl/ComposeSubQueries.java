package io.army.criteria.impl;

import io.army.criteria.*;

import java.util.List;
import java.util.function.Function;

abstract class ComposeSubQueries<C> implements ComposeSubQuery, SubQuery.SubQueryUnionAble<C> {

    static <C> SubQuery.SubQueryUnionAble<C> brackets(C criteria, SubQuery encloseSubQuery) {
        return new ComposeSubQueries.BracketsSubQuery<>(criteria, encloseSubQuery);
    }

    static <C, S extends SubQuery> SubQuery.SubQueryUnionAble<C> compose(C criteria, SubQuery leftQuery
            , SQLModifier modifier
            , Function<C, S> function) {
        return new ComposeSubQueryImpl<>(criteria, leftQuery, modifier, function.apply(criteria));
    }


    private final C criteria;

    private ComposeSubQueries(C criteria) {
        this.criteria = criteria;
    }

    @Override
    public String toString() {
        return "#ComposeSubQuery@" + System.identityHashCode(this);
    }

    @Override
    public final SubQuery asSubQuery() {
        return this;
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

    /*################################## blow static inner class ##################################*/

    private static final class BracketsSubQuery<C> extends ComposeSubQueries<C> {

        private final SubQuery encloseSubQuery;

        public BracketsSubQuery(C criteria, SubQuery encloseSubQuery) {
            super(criteria);
            this.encloseSubQuery = encloseSubQuery;
        }

        @Override
        public final void appendSQL(SQLContext context) {
            StringBuilder builder = context.stringBuilder()
                    .append(" (");
            context.dml().subQuery(this.encloseSubQuery, context);
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
        public final void appendSQL(SQLContext context) {

            context.dml().subQuery(leftSubQuery, context);

            context.stringBuilder().append(" ")
                    .append(modifier.render())
                    .append(" ");

            context.dml().subQuery(rightSubQuery, context);

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
