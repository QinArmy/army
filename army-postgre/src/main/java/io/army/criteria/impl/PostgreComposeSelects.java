package io.army.criteria.impl;

import io.army.criteria.ComposeSelect;
import io.army.criteria.SQLContext;
import io.army.criteria.SQLModifier;
import io.army.criteria.Select;
import io.army.criteria.postgre.PostgreSelect;

import java.util.function.Function;

abstract class PostgreComposeSelects<C> implements ComposeSelect<C>, PostgreSelect, PostgreSelect.PostgreComposeAble<C> {

    static <C> PostgreSelect.PostgreComposeAble<C> brackets(C criteria, Select enclosedSelect) {
        return new BracketsSelect<>(criteria, enclosedSelect);
    }

    static <C, S extends Select> PostgreSelect.PostgreComposeAble<C> compose(
            C criteria, Select leftSelect, SQLModifier modifier, Function<C, S> function) {

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

    private PostgreComposeSelects(C criteria) {
        this.criteria = criteria;
    }


    @Override
    public final boolean requiredBrackets() {
        return false;
    }

    @Override
    public final PostgreSelect asSelect() {
        return this;
    }

    @Override
    public final PostgreComposeAble<C> brackets() {
        return null;
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> union(Function<C, S> function) {
        return null;
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> unionAll(Function<C, S> function) {
        return null;
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> unionDistinct(Function<C, S> function) {
        return null;
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> intersect(Function<C, S> function) {
        return null;
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> intersectAll(Function<C, S> function) {
        return null;
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> intersectDistinct(Function<C, S> function) {
        return null;
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> except(Function<C, S> function) {
        return null;
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> exceptAll(Function<C, S> function) {
        return null;
    }

    @Override
    public final <S extends Select> PostgreComposeAble<C> exceptDistinct(Function<C, S> function) {
        return null;
    }


    /*################################## blow static inner class ##################################*/

    private static final class BracketsSelect<C> extends PostgreComposeSelects<C> {

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

    private static final class ComposeSelectImpl<C> extends PostgreComposeSelects<C> {

        private final Select leftSelect;

        private final SQLModifier modifier;

        private final Select rightSelect;

        public ComposeSelectImpl(C criteria, Select leftSelect, SQLModifier modifier
                , Select rightSelect) {
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
