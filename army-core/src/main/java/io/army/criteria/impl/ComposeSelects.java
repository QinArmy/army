package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.criteria.SQLModifier;
import io.army.criteria.Select;
import io.army.criteria.SelfDescribedSelect;

import java.util.function.Function;

abstract class ComposeSelects<C> implements SelfDescribedSelect<C>, Select.UnionAble<C> {

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


    private final C criteria;

    ComposeSelects(C criteria) {
        this.criteria = criteria;
    }


    @Override
    public final boolean requiredBrackets() {
        return false;
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

    @Override
    public final Select asSelect() {
        return this;
    }

    /*################################## blow static inner class ##################################*/

    private static final class BracketsSelect<C> extends ComposeSelects<C> {

        private final Select enclosedSelect;

        public BracketsSelect(C criteria, Select enclosedSelect) {
            super(criteria);
            this.enclosedSelect = enclosedSelect;
        }

        @Override
        public final void appendSQL(SQLContext context) {
            StringBuilder builder = context.stringBuilder()
                    .append(" (");
            context.dml().select(this.enclosedSelect, context);
            builder.append(" )");
        }

    }

    private static final class ComposeSelectImpl<C> extends ComposeSelects<C> {

        private final Select leftSelect;

        private final SQLModifier modifier;

        private final Select rightSelect;

        public ComposeSelectImpl(C criteria, Select leftSelect, SQLModifier modifier, Select rightSelect) {
            super(criteria);
            this.leftSelect = leftSelect;
            this.modifier = modifier;
            this.rightSelect = rightSelect;
        }

        @Override
        public final void appendSQL(SQLContext context) {
            context.dml().select(leftSelect, context);

            context.stringBuilder()
                    .append(" ")
                    .append(modifier.render())
                    .append(" ");

            context.dml().select(rightSelect, context);
        }


    }

}
