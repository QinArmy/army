package io.army.criteria.impl;

import io.army.criteria.*;

import java.util.List;
import java.util.function.Function;

abstract class ComposeColumnSubQueries<E, C> implements ComposeSubQuery, ColumnSubQuery<E>
        , ColumnSubQuery.ColumnSubQueryUnionAble<E, C> {

    static <E, C> ColumnSubQuery.ColumnSubQueryUnionAble<E, C> brackets(C criteria, ColumnSubQuery<E> encloseSubQuery) {
        return new BracketsColumnSubQuery<>(criteria, encloseSubQuery);
    }

    static <E, C, S extends ColumnSubQuery<E>> ColumnSubQuery.ColumnSubQueryUnionAble<E, C> compose(
            C criteria, ColumnSubQuery<E> leftQuery, SQLModifier modifier, Function<C, S> function) {
        return new ComposeColumnSubQueryImpl<>(criteria, leftQuery, modifier, function.apply(criteria));
    }

    private final C criteria;

    private ComposeColumnSubQueries(C criteria) {
        this.criteria = criteria;
    }


    @Override
    public final String toString() {
        return "#ComposeColumnSubQuery@" + System.identityHashCode(this);
    }


    @Override
    public final ColumnSubQuery<E> asSubQuery() {
        return this;
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


    /*################################## blow static inner class ##################################*/

    private static final class BracketsColumnSubQuery<E, C> extends ComposeColumnSubQueries<E, C> {

        private final ColumnSubQuery<E> encloseSubQuery;

        private BracketsColumnSubQuery(C criteria, ColumnSubQuery<E> encloseSubQuery) {
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

        @Override
        public Selection selection() {
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

        @Override
        public Selection selection() {
            return leftSubQuery.selection();
        }
    }
}
