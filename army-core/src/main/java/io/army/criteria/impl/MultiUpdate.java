package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Update;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class MultiUpdate<C, JT, JS, WR, WA, SR> extends MultiQueryDmlStatement<C, JT, JS, WR, WA>
        implements Update.MultiSetSpec<C, SR> {


    MultiUpdate(@Nullable C criteria) {
        super(criteria);
    }


    @Override
    public final SR set(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList) {
        final int fieldSize = fieldList.size();
        if (fieldSize == 0) {
            throw _Exceptions.updateFieldListEmpty();
        }
        if (fieldSize != valueList.size()) {
            throw _Exceptions.fieldAndValueSizeNotMatch(fieldSize, valueList.size());
        }
        for (int i = 0; i < fieldSize; i++) {
            this.set(fieldList.get(i), valueList.get(i));
        }
        return (SR) this;
    }

    @Override
    public final SR set(FieldMeta<?, ?> field, @Nullable Object value) {
        final Expression<?> expression;
        if (value == null) {
            expression = SQLs.nullWord();
        } else {
            expression = SQLs.paramWithExp(field, value);
        }
        return this.set(field, expression);
    }

    @Override
    public final SR set(FieldMeta<?, ?> field, Expression<?> value) {
        return (SR) this;
    }

    @Override
    public final SR set(FieldMeta<?, ?> field, Function<C, Expression<?>> function) {
        return null;
    }

    @Override
    public final SR set(FieldMeta<?, ?> field, Supplier<Expression<?>> supplier) {
        return null;
    }

    @Override
    public final SR setNull(FieldMeta<?, ?> field) {
        return null;
    }

    @Override
    public final SR setDefault(FieldMeta<?, ?> field) {
        return null;
    }

    @Override
    public final SR ifSetNull(Predicate<C> predicate, FieldMeta<?, ?> field) {
        return null;
    }

    @Override
    public final SR ifSetDefault(Predicate<C> predicate, FieldMeta<?, ?> field) {
        return null;
    }

    @Override
    public final <F extends Number> SR setPlus(FieldMeta<?, F> field, F value) {
        return null;
    }

    @Override
    public final <F extends Number> SR setPlus(FieldMeta<?, F> field, Expression<?> value) {
        return null;
    }

    @Override
    public final <F extends Number> SR setMinus(FieldMeta<?, F> field, F value) {
        return null;
    }

    @Override
    public final <F extends Number> SR setMinus(FieldMeta<?, F> field, Expression<?> value) {
        return null;
    }

    @Override
    public final <F extends Number> SR setMultiply(FieldMeta<?, F> field, F value) {
        return null;
    }

    @Override
    public final <F extends Number> SR setMultiply(FieldMeta<?, F> field, Expression<?> value) {
        return null;
    }

    @Override
    public final <F extends Number> SR setDivide(FieldMeta<?, F> field, F value) {
        return null;
    }

    @Override
    public final <F extends Number> SR setDivide(FieldMeta<?, F> field, Expression<F> value) {
        return null;
    }

    @Override
    public final <F extends Number> SR setMod(FieldMeta<?, F> field, F value) {
        return null;
    }

    @Override
    public final <F extends Number> SR setMod(FieldMeta<?, F> field, Expression<F> value) {
        return null;
    }

    @Override
    public final SR ifSet(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList) {
        return null;
    }

    @Override
    public final SR ifSet(FieldMeta<?, ?> field, @Nullable Object value) {
        return null;
    }

    @Override
    public final SR ifSet(FieldMeta<?, ?> field, Function<C, Expression<?>> function) {
        return null;
    }

    @Override
    public final SR ifSet(FieldMeta<?, ?> field, Supplier<Expression<?>> supplier) {
        return null;
    }

    @Override
    public final <F extends Number> SR ifSetPlus(FieldMeta<?, ?> field, @Nullable F value) {
        return null;
    }

    @Override
    public final <F extends Number> SR ifSetMinus(FieldMeta<?, ?> field, @Nullable F value) {
        return null;
    }

    @Override
    public final <F extends Number> SR ifSetMultiply(FieldMeta<?, ?> field, @Nullable F value) {
        return null;
    }

    @Override
    public final <F extends Number> SR ifSetDivide(FieldMeta<?, ?> field, @Nullable F value) {
        return null;
    }

    @Override
    public final <F extends Number> SR ifSetMod(FieldMeta<?, ?> field, @Nullable F value) {
        return null;
    }


}
