package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Update;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.modelgen._MetaBridge;
import io.army.util.CollectionUtils;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link SingleUpdate}</li>
 *         <li>{@link MultiUpdate}</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class AbstractUpdate<C, JT, JS, WR, WA, SR> extends AbstractDml<C, JT, JS, WR, WA>
        implements Update, Update.UpdateSpec, Update.SimpleSetClause<C, SR>, Update.BatchSetClause<C, SR>, _Update {

    final CriteriaContext criteriaContext;

    private List<SetTargetPart> fieldList = new ArrayList<>();

    private List<SetValuePart> valueExpList = new ArrayList<>();

    private boolean prepared;

    AbstractUpdate(CriteriaContext criteriaContext) {
        super(criteriaContext.criteria());
        this.criteriaContext = criteriaContext;

    }

    @Override
    public final SR set(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList) {
        final int fieldSize = fieldList.size();
        if (fieldSize != valueList.size()) {
            throw _Exceptions.fieldAndValueSizeNotMatch(fieldSize, valueList.size());
        }
        if (fieldSize == 0) {
            throw new CriteriaException("fieldList must not empty.");
        }
        for (int i = 0; i < fieldSize; i++) {
            this.set(fieldList.get(i), valueList.get(i));
        }
        return (SR) this;
    }

    @Override
    public final SR set(FieldMeta<?, ?> field, @Nullable Object value) {
        if (value != null) {
            this.set(field, SQLs.paramWithExp(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR set(FieldMeta<?, ?> field, Expression<?> value) {
        if (field.updateMode() == UpdateMode.IMMUTABLE) {
            throw _Exceptions.immutableField(field);
        }
        final String fieldName = field.fieldName();
        if (fieldName.equals(_MetaBridge.UPDATE_TIME) || fieldName.equals(_MetaBridge.VERSION)) {
            throw _Exceptions.armyManageField(field);
        }
        if (!field.nullable() && ((_Expression<?>) value).nullableExp()) {
            throw _Exceptions.nonNullField(field);
        }
        this.fieldList.add(field);
        this.valueExpList.add((_Expression<?>) value);
        return (SR) this;
    }

    @Override
    public final <F> SR set(FieldMeta<?, F> field, Function<C, Expression<F>> function) {
        return this.set(field, function.apply(this.criteria));
    }

    @Override
    public final <F> SR set(FieldMeta<?, F> field, Supplier<Expression<F>> supplier) {
        return this.set(field, supplier.get());
    }

    @Override
    public final SR setNull(FieldMeta<?, ?> field) {
        return this.set(field, SQLs.nullWord());
    }

    @Override
    public final SR setDefault(FieldMeta<?, ?> field) {
        return this.set(field, SQLs.defaultWord());
    }

    @Override
    public final <F extends Number> SR setPlus(FieldMeta<?, F> field, F value) {
        Objects.requireNonNull(value);
        return this.set(field, field.plus(value));
    }

    @Override
    public final <F extends Number> SR setPlus(FieldMeta<?, F> field, Expression<F> value) {
        Objects.requireNonNull(value);
        return this.set(field, field.plus(value));
    }

    @Override
    public final <F extends Number> SR setMinus(FieldMeta<?, F> field, F value) {
        Objects.requireNonNull(value);
        return this.set(field, field.minus(value));
    }

    @Override
    public final <F extends Number> SR setMinus(FieldMeta<?, F> field, Expression<F> value) {
        Objects.requireNonNull(value);
        return this.set(field, field.minus(value));
    }

    @Override
    public final <F extends Number> SR setMultiply(FieldMeta<?, F> field, F value) {
        Objects.requireNonNull(value);
        return this.set(field, field.multiply(value));
    }

    @Override
    public final <F extends Number> SR setMultiply(FieldMeta<?, F> field, Expression<F> value) {
        Objects.requireNonNull(value);
        return this.set(field, field.multiply(value));
    }

    @Override
    public final <F extends Number> SR setDivide(FieldMeta<?, F> field, F value) {
        Objects.requireNonNull(value);
        return this.set(field, field.divide(value));
    }

    @Override
    public final <F extends Number> SR setDivide(FieldMeta<?, F> field, Expression<F> value) {
        Objects.requireNonNull(value);
        return this.set(field, field.divide(value));
    }

    @Override
    public final <F extends Number> SR setMod(FieldMeta<?, F> field, F value) {
        Objects.requireNonNull(value);
        return this.set(field, field.mod(value));
    }

    @Override
    public final <F extends Number> SR setMod(FieldMeta<?, F> field, Expression<F> value) {
        Objects.requireNonNull(value);
        return this.set(field, field.mod(value));
    }

    @Override
    public final SR ifSet(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList) {
        if (fieldList.size() > 0) {
            this.set(fieldList, valueList);
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetNull(Predicate<C> predicate, FieldMeta<?, ?> field) {
        if (predicate.test(this.criteria)) {
            this.set(field, SQLs.nullWord());
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetDefault(Predicate<C> predicate, FieldMeta<?, ?> field) {
        if (predicate.test(this.criteria)) {
            this.set(field, SQLs.defaultWord());
        }
        return (SR) this;
    }

    @Override
    public final <F> SR ifSet(FieldMeta<?, F> field, @Nullable F value) {
        if (value != null) {
            this.set(field, SQLs.paramWithExp(field, value));
        }
        return (SR) this;
    }

    @Override
    public final <F> SR ifSet(FieldMeta<?, F> field, Function<C, Expression<F>> function) {
        final Expression<F> value;
        value = function.apply(this.criteria);
        if (value != null) {
            this.set(field, value);
        }
        return (SR) this;
    }

    @Override
    public final <F> SR ifSet(FieldMeta<?, F> field, Supplier<Expression<F>> supplier) {
        final Expression<F> value;
        value = supplier.get();
        if (value != null) {
            this.set(field, value);
        }
        return (SR) this;
    }

    @Override
    public final <F extends Number> SR ifSetPlus(FieldMeta<?, F> field, @Nullable F value) {
        if (value != null) {
            this.set(field, field.plus(value));
        }
        return (SR) this;
    }

    @Override
    public final <F extends Number> SR ifSetMinus(FieldMeta<?, F> field, @Nullable F value) {
        if (value != null) {
            this.set(field, field.minus(value));
        }
        return (SR) this;
    }

    @Override
    public final <F extends Number> SR ifSetMultiply(FieldMeta<?, F> field, @Nullable F value) {
        if (value != null) {
            this.set(field, field.multiply(value));
        }
        return (SR) this;
    }

    @Override
    public final <F extends Number> SR ifSetDivide(FieldMeta<?, F> field, @Nullable F value) {
        if (value != null) {
            this.set(field, field.divide(value));
        }
        return (SR) this;
    }

    @Override
    public final <F extends Number> SR ifSetMod(FieldMeta<?, F> field, @Nullable F value) {
        if (value != null) {
            this.set(field, field.mod(value));
        }
        return (SR) this;
    }

    /*################################## blow batch set clause  method ##################################*/

    @Override
    public final SR set(List<FieldMeta<?, ?>> fieldList) {
        if (fieldList.size() == 0) {
            throw _Exceptions.selectListIsEmpty();
        }
        for (FieldMeta<?, ?> field : fieldList) {
            this.set(field, SQLs.namedParam(field));
        }
        return (SR) this;
    }

    @Override
    public final <F> SR set(FieldMeta<?, F> field) {
        return this.set(field, SQLs.namedParam(field));
    }

    @Override
    public final <F extends Number> SR setPlus(FieldMeta<?, F> field) {
        return this.set(field, field.plus(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public final <F extends Number> SR setMinus(FieldMeta<?, F> field) {
        return this.set(field, field.minus(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public final <F extends Number> SR setMultiply(FieldMeta<?, F> field) {
        return this.set(field, field.multiply(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public final <F extends Number> SR setDivide(FieldMeta<?, F> field) {
        return this.set(field, field.divide(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public final <F extends Number> SR setMod(FieldMeta<?, F> field) {
        return this.set(field, field.mod(SQLs.nonNullNamedParam(field)));
    }

    @Override
    public final SR ifSet(Function<C, List<FieldMeta<?, ?>>> function) {
        final List<FieldMeta<?, ?>> fieldList;
        fieldList = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(fieldList)) {
            this.set(fieldList);
        }
        return (SR) this;
    }

    @Override
    public final <F> SR ifSet(Predicate<C> test, FieldMeta<?, F> field) {
        if (test.test(this.criteria)) {
            this.set(field, SQLs.namedParam(field));
        }
        return (SR) this;
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final Update asUpdate() {
        _Assert.nonPrepared(this.prepared);

        if (this instanceof WithElement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        final List<SetTargetPart> targetParts = this.fieldList;
        final List<SetValuePart> valueParts = this.valueExpList;
        if (CollectionUtils.isEmpty(targetParts)) {
            throw _Exceptions.updateFieldListEmpty();
        }
        if (targetParts.size() != valueParts.size()) {
            // no bug ,never here
            throw new IllegalStateException("target and value size not match.");
        }
        this.fieldList = CollectionUtils.unmodifiableList(targetParts);
        this.valueExpList = CollectionUtils.unmodifiableList(valueParts);

        final List<_Predicate> predicates = this.predicateList;
        if (CollectionUtils.isEmpty(predicates)) {
            throw _Exceptions.dmlNoWhereClause();
        }
        this.predicateList = CollectionUtils.unmodifiableList(predicates);

        this.onAsUpdate();
        this.prepared = true;
        return this;
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);

        this.prepared = false;
        this.fieldList = null;
        this.valueExpList = null;
        this.predicateList = null;

        this.onClear();
    }


    @Override
    public final List<? extends SetTargetPart> fieldList() {
        _Assert.prepared(this.prepared);
        return this.fieldList;
    }

    @Override
    public final List<? extends SetValuePart> valueExpList() {
        return this.valueExpList;
    }


    void onAsUpdate() {

    }

    void onClear() {

    }


}
