package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.InnerStandardComposeQuery;
import io.army.dialect.DQL;
import io.army.dialect.SQLBuilder;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class ComposeSelects<C> extends AbstractComposeQuery<C> implements
        Select.UnionAble<C>, Select, SelfDescribed, InnerStandardComposeQuery {

    static <C> UnionAble<C> brackets(C criteria, Select enclosedSelect) {
        return new BracketsSelect<>(criteria, enclosedSelect);
    }

    static <C, S extends Select> UnionAble<C> compose(C criteria, Select leftSelect, SQLModifier modifier
            , Function<C, S> function) {
        Select left = leftSelect, right;
        if (left.requiredBrackets()) {
            left = new BracketsSelect<>(criteria, left);
        }
        right = function.apply(criteria);
        if (right.requiredBrackets()) {
            right = new BracketsSelect<>(criteria, right);
        }
        return new ComposeSelectImpl<>(criteria, left, modifier, right);
    }

    private ComposeSelects(C criteria, Select firstSelect) {
        super(criteria, firstSelect);
    }


    @Override
    public final UnionAble<C> brackets() {
        return new BracketsSelect<>(criteria, this);
    }

    @Override
    public final <S extends Select> UnionAble<C> union(Function<C, S> function) {
        return compose(this.criteria, this, UnionType.UNION, function);
    }

    @Override
    public final <S extends Select> UnionAble<C> unionAll(Function<C, S> function) {
        return compose(this.criteria, this, UnionType.UNION_ALL, function);
    }

    @Override
    public final <S extends Select> UnionAble<C> unionDistinct(Function<C, S> function) {
        return compose(this.criteria, this, UnionType.UNION_DISTINCT, function);
    }

    /*################################## blow OrderByClause method ##################################*/

    @Override
    public final LimitClause<C> orderBy(SortPart sortPart) {
        doOrderBy(sortPart);
        return this;
    }

    @Override
    public final LimitClause<C> orderBy(List<SortPart> sortPartList) {
        doOrderBy(sortPartList);
        return this;
    }

    @Override
    public final LimitClause<C> orderBy(Function<C, List<SortPart>> function) {
        doOrderBy(function);
        return this;
    }


    /*################################## blow LimitClause method ##################################*/

    @Override
    public final SelectAble limit(int rowCount) {
        doLimit(rowCount);
        return this;
    }

    @Override
    public final SelectAble limit(int offset, int rowCount) {
        doLimit(offset, rowCount);
        return this;
    }


    @Override
    public final SelectAble ifLimit(Predicate<C> predicate, int rowCount) {
        doLimit(predicate, rowCount);
        return this;
    }

    @Override
    public final SelectAble ifLimit(Predicate<C> predicate, int offset, int rowCount) {
        doLimit(predicate, offset, rowCount);
        return this;
    }

    @Override
    public final SelectAble ifLimit(Function<C, LimitOption> function) {
        doIfLimit(function);
        return this;
    }

    @Override
    public final Select asSelect() {
        asQuery();
        return this;
    }

    @Override
    public final void clear() {

    }

    /*################################## blow static inner class ##################################*/

    private static final class BracketsSelect<C> extends ComposeSelects<C> {

        private final Select enclosedSelect;

        BracketsSelect(C criteria, Select enclosedSelect) {
            super(criteria, enclosedSelect);
            this.enclosedSelect = enclosedSelect;
        }

        @Override
        public boolean requiredBrackets() {
            return false;
        }

        @Override
        public void appendSQL(SQLContext context) {
            SQLBuilder builder = context.sqlBuilder()
                    .append(" (");
            context.dql().select(this.enclosedSelect, context);
            builder.append(" )");
        }

    }

    private static final class ComposeSelectImpl<C> extends ComposeSelects<C> implements Select, SelfDescribed {

        private final Select leftSelect;

        private final SQLModifier modifier;

        private final Select rightSelect;

        ComposeSelectImpl(C criteria, Select leftSelect, SQLModifier modifier, Select rightSelect) {
            super(criteria, leftSelect);
            this.leftSelect = leftSelect;
            this.modifier = modifier;
            this.rightSelect = rightSelect;
        }

        @Override
        public boolean requiredBrackets() {
            return true;
        }

        @Override
        public void appendSQL(SQLContext context) {
            DQL dql = context.dql();
            dql.select(leftSelect, context);

            context.sqlBuilder()
                    .append(" ")
                    .append(modifier.render())
                    .append(" ");

            dql.select(rightSelect, context);
        }

    }

}
