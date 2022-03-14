package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Update;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.util.CollectionUtils;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
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
abstract class JoinableUpdate<C, JT, JS, WR, WA, SR> extends JoinableDml<C, JT, JS, WR, WA>
        implements Update, Update.UpdateSpec, Update.SimpleSetClause<C, SR>, Update.BatchSetClause<C, SR>, _Update {

    final CriteriaContext criteriaContext;

    private List<SetLeftItem> leftList = new ArrayList<>();

    private List<SetRightItem> rightList = new ArrayList<>();

    private boolean prepared;

    JoinableUpdate(CriteriaContext criteriaContext) {
        super(criteriaContext);
        this.criteriaContext = criteriaContext;

    }

    /*################################## blow SetClause method ##################################*/

    @Override
    public final SR setPairs(List<ItemPair> pairList) {
        if (pairList.size() == 0) {
            throw new CriteriaException("pair list must not empty.");
        }
        final List<SetLeftItem> leftList = this.leftList;
        final List<SetRightItem> rightList = this.rightList;

        for (ItemPair itemPair : pairList) {
            if (!(itemPair instanceof SQLs.ItemPairImpl)) {
                throw new CriteriaException("pair is Illegal");
            }
            leftList.add(((SQLs.ItemPairImpl) itemPair).left);
            rightList.add(((SQLs.ItemPairImpl) itemPair).right);
        }
        return (SR) this;
    }

    @Override
    public final SR setPairs(Supplier<List<ItemPair>> supplier) {
        return this.setPairs(supplier.get());
    }

    @Override
    public final SR setPairs(Function<C, List<ItemPair>> function) {
        return this.setPairs(function.apply(this.criteria));
    }

    @Override
    public final SR setPairs(Consumer<List<ItemPair>> consumer) {
        final List<ItemPair> list = new ArrayList<>();
        consumer.accept(list);
        return this.setPairs(list);
    }

    @Override
    public final SR ifSetPairs(List<ItemPair> pairList) {
        if (pairList.size() > 0) {
            this.setPairs(pairList);
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetPairs(Supplier<List<ItemPair>> supplier) {
        return this.ifSetPairs(supplier.get());
    }

    @Override
    public final SR ifSetPairs(Function<C, List<ItemPair>> function) {
        return this.ifSetPairs(function.apply(this.criteria));
    }

    @Override
    public final SR setExp(FieldMeta<?> field, Function<C, Expression> function) {
        return this.setExp(field, function.apply(this.criteria));
    }

    @Override
    public final SR setExp(FieldMeta<?> field, Supplier<Expression> supplier) {
        return this.setExp(field, supplier.get());
    }

    @Override
    public final SR ifSetExp(FieldMeta<?> field, Function<C, Expression> function) {
        final Expression exp;
        exp = function.apply(this.criteria);
        if (exp != null) {
            this.setExp(field, exp);
        }
        return (SR) this;
    }

    /*################################## blow SimpleSetClause method ##################################*/

    @Override
    public final SR set(FieldMeta<?> field, @Nullable Object paramOrExp) {
        return this.setExp(field, SQLs.nullableParam(field, paramOrExp));
    }

    @Override
    public final SR setLiteral(FieldMeta<?> field, @Nullable Object paramOrExp) {
        return this.setExp(field, SQLs.nullableLiteral(field, paramOrExp));
    }

    @Override
    public final SR setPlus(FieldMeta<?> field, Object paramOrExp) {
        return this.setExp(field, field.plus(paramOrExp));
    }

    @Override
    public final SR setPlusLiteral(FieldMeta<?> field, Object paramOrExp) {
        return this.setExp(field, field.plusLiteral(paramOrExp));
    }

    @Override
    public final SR setMinus(FieldMeta<?> field, Object paramOrExp) {
        return this.setExp(field, field.minus(paramOrExp));
    }

    @Override
    public final SR setMinusLiteral(FieldMeta<?> field, Object paramOrExp) {
        return this.setExp(field, field.minusLiteral(paramOrExp));
    }

    @Override
    public final SR setMultiply(FieldMeta<?> field, Object paramOrExp) {
        return this.setExp(field, field.multiply(paramOrExp));
    }

    @Override
    public final SR setMultiplyLiteral(FieldMeta<?> field, Object paramOrExp) {
        return this.setExp(field, field.multiplyLiteral(paramOrExp));
    }

    @Override
    public final SR setDivide(FieldMeta<?> field, Object paramOrExp) {
        return this.setExp(field, field.divide(paramOrExp));
    }

    @Override
    public final SR setDivideLiteral(FieldMeta<?> field, Object paramOrExp) {
        return this.setExp(field, field.divideLiteral(paramOrExp));
    }

    @Override
    public final SR setMod(FieldMeta<?> field, Object paramOrExp) {
        return this.setExp(field, field.mod(paramOrExp));
    }

    @Override
    public final SR setModLiteral(FieldMeta<?> field, Object paramOrExp) {
        return this.setExp(field, field.modLiteral(paramOrExp));
    }

    @Override
    public final SR ifSet(FieldMeta<?> field, Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, SQLs.nonNullParam(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(FieldMeta<?> field, Supplier<Object> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, SQLs.nonNullParam(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(FieldMeta<?> field, Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, SQLs.nonNullLiteral(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(FieldMeta<?> field, Supplier<Object> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, SQLs.nonNullLiteral(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetPlus(FieldMeta<?> field, Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.plus(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetPlus(FieldMeta<?> field, Supplier<Object> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.plus(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMinus(FieldMeta<?> field, Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.minus(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMinus(FieldMeta<?> field, Supplier<Object> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.minus(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMultiply(FieldMeta<?> field, Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.multiply(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMultiply(FieldMeta<?> field, Supplier<Object> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.multiply(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetDivide(FieldMeta<?> field, Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.divide(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetDivide(FieldMeta<?> field, Supplier<Object> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.divide(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMod(FieldMeta<?> field, Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.mod(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMod(FieldMeta<?> field, Supplier<Object> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.mod(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetPlusLiteral(FieldMeta<?> field, Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.plusLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetPlusLiteral(FieldMeta<?> field, Supplier<Object> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.plusLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMinusLiteral(FieldMeta<?> field, Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.minusLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMinusLiteral(FieldMeta<?> field, Supplier<Object> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.minusLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMultiplyLiteral(FieldMeta<?> field, Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.multiplyLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMultiplyLiteral(FieldMeta<?> field, Supplier<Object> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.multiplyLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetDivideLiteral(FieldMeta<?> field, Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.divideLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetDivideLiteral(FieldMeta<?> field, Supplier<Object> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.divideLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetModLiteral(FieldMeta<?> field, Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.modLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetModLiteral(FieldMeta<?> field, Supplier<Object> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.modLiteral(value));
        }
        return (SR) this;
    }

    /*################################## blow BatchSetClause  method ##################################*/

    @Override
    public final SR setExp(FieldMeta<?> field, Expression value) {
        Objects.requireNonNull(value);
        this.leftList.add(field);
        this.rightList.add(value);
        return (SR) this;
    }

    @Override
    public final SR ifSetExp(FieldMeta<?> field, Supplier<Expression> supplier) {
        final Expression exp;
        exp = supplier.get();
        if (exp != null) {
            this.setExp(field, exp);
        }
        return (SR) this;
    }

    @Override
    public final SR setNullable(List<FieldMeta<?>> fieldList) {
        if (fieldList.size() == 0) {
            throw batchSetLisEmpty();
        }
        final List<SetLeftItem> leftList = this.leftList;
        final List<SetRightItem> rightList = this.rightList;
        for (FieldMeta<?> field : fieldList) {
            leftList.add(field);
            rightList.add(SQLs.nullableNamedParam(field));
        }
        return (SR) this;
    }

    @Override
    public final SR set(List<FieldMeta<?>> fieldList) {
        if (fieldList.size() == 0) {
            throw batchSetLisEmpty();
        }
        final List<SetLeftItem> leftList = this.leftList;
        final List<SetRightItem> rightList = this.rightList;
        for (FieldMeta<?> field : fieldList) {
            leftList.add(field);
            rightList.add(SQLs.namedParam(field));
        }
        return (SR) this;
    }

    @Override
    public final SR set(Consumer<List<FieldMeta<?>>> consumer) {
        final List<FieldMeta<?>> list = new ArrayList<>();
        consumer.accept(list);
        return this.set(list);
    }


    @Override
    public final SR setNullable(Consumer<List<FieldMeta<?>>> consumer) {
        final List<FieldMeta<?>> list = new ArrayList<>();
        consumer.accept(list);
        return this.setNullable(list);
    }

    @Override
    public final SR set(Function<C, List<FieldMeta<?>>> function) {
        return this.set(function.apply(this.criteria));
    }

    @Override
    public final SR setNullable(Function<C, List<FieldMeta<?>>> function) {
        return this.setNullable(function.apply(this.criteria));
    }

    @Override
    public final SR set(Supplier<List<FieldMeta<?>>> supplier) {
        return this.set(supplier.get());
    }

    @Override
    public final SR setNullable(Supplier<List<FieldMeta<?>>> supplier) {
        return this.setNullable(supplier.get());
    }

    @Override
    public final SR setNullable(FieldMeta<?> field) {
        return this.setExp(field, SQLs.nullableNamedParam(field));
    }

    @Override
    public final SR set(FieldMeta<?> field) {
        return this.setExp(field, SQLs.namedParam(field));
    }

    @Override
    public final SR setPlus(FieldMeta<?> field) {
        return this.setExp(field, field.plus(SQLs.namedParam(field)));
    }

    @Override
    public final SR setMinus(FieldMeta<?> field) {
        return this.setExp(field, field.minus(SQLs.namedParam(field)));
    }

    @Override
    public final SR setMultiply(FieldMeta<?> field) {
        return this.setExp(field, field.multiply(SQLs.namedParam(field)));
    }

    @Override
    public final SR setDivide(FieldMeta<?> field) {
        return this.setExp(field, field.divide(SQLs.namedParam(field)));
    }

    @Override
    public final SR setMod(FieldMeta<?> field) {
        return this.setExp(field, field.mod(SQLs.namedParam(field)));
    }

    @Override
    public final SR ifSet(Function<C, List<FieldMeta<?>>> function) {
        final List<FieldMeta<?>> fieldList;
        fieldList = function.apply(this.criteria);
        if (fieldList.size() > 0) {
            this.set(fieldList);
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetNullable(Function<C, List<FieldMeta<?>>> function) {
        final List<FieldMeta<?>> fieldList;
        fieldList = function.apply(this.criteria);
        if (fieldList.size() > 0) {
            this.setNullable(fieldList);
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(Predicate<C> test, FieldMeta<?> field) {
        if (test.test(this.criteria)) {
            this.setExp(field, SQLs.namedParam(field));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetNullable(Predicate<C> test, FieldMeta<?> field) {
        if (test.test(this.criteria)) {
            this.setExp(field, SQLs.nullableNamedParam(field));
        }
        return (SR) this;
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        return this.prepared;
    }

    @Override
    public final Update asUpdate() {
        _Assert.nonPrepared(this.prepared);

        if (this instanceof WithElement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        final List<SetLeftItem> targetParts = this.leftList;
        final List<SetRightItem> valueParts = this.rightList;
        if (CollectionUtils.isEmpty(targetParts)) {
            throw _Exceptions.updateFieldListEmpty();
        }
        if (targetParts.size() != valueParts.size()) {
            // no bug ,never here
            throw new IllegalStateException("target and value size not match.");
        }
        this.leftList = CollectionUtils.unmodifiableList(targetParts);
        this.rightList = CollectionUtils.unmodifiableList(valueParts);

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
        this.leftList = null;
        this.rightList = null;
        this.predicateList = null;

        this.onClear();
    }


    @Override
    public final List<? extends SetLeftItem> fieldList() {
        _Assert.prepared(this.prepared);
        return this.leftList;
    }

    @Override
    public final List<? extends SetRightItem> valueExpList() {
        return this.rightList;
    }


    void onAsUpdate() {

    }

    void onClear() {

    }


    private static CriteriaException batchSetLisEmpty() {
        return new CriteriaException("Batch set clause field list must non empty.");
    }


}
