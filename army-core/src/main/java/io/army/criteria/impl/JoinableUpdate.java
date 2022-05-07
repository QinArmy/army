package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Update;
import io.army.lang.Nullable;
import io.army.util._Assert;
import io.army.util._CollectionUtils;
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
abstract class JoinableUpdate<C, SR, FT, FS, FP, JT, JS, JP, WR, WA>
        extends DmlWhereClause<C, FT, FS, FP, JT, JS, JP, WR, WA>
        implements Update, Update._UpdateSpec, Update._SimpleSetClause<C, SR>, Update._BatchSetClause<C, SR>, _Update {


    final CriteriaContext criteriaContext;

    private List<SetLeftItem> leftList = new ArrayList<>();

    private List<SetRightItem> rightList = new ArrayList<>();

    private boolean prepared;

    JoinableUpdate(ClauseSupplier clauseSupplier, CriteriaContext criteriaContext) {
        super(clauseSupplier, criteriaContext.criteria());
        this.criteriaContext = criteriaContext;
    }

    JoinableUpdate(CriteriaContext criteriaContext) {
        super(criteriaContext.criteria());
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
    public final SR setExp(TableField<?> field, Function<C, ? extends Expression> function) {
        return this.setExp(field, function.apply(this.criteria));
    }

    @Override
    public final SR setExp(TableField<?> field, Supplier<? extends Expression> supplier) {
        return this.setExp(field, supplier.get());
    }

    @Override
    public final SR ifSetExp(TableField<?> field, Function<C, ? extends Expression> function) {
        final Expression exp;
        exp = function.apply(this.criteria);
        if (exp != null) {
            this.setExp(field, exp);
        }
        return (SR) this;
    }

    /*################################## blow SimpleSetClause method ##################################*/

    @Override
    public final SR set(TableField<?> field, @Nullable Object paramOrExp) {
        return this.setExp(field, SQLs.nullableParam(field, paramOrExp));
    }

    @Override
    public final SR setLiteral(TableField<?> field, @Nullable Object paramOrExp) {
        return this.setExp(field, SQLs.nullableLiteral(field, paramOrExp));
    }

    @Override
    public final SR setPlus(TableField<?> field, Object paramOrExp) {
        return this.setExp(field, field.plus(paramOrExp));
    }

    @Override
    public final SR setPlusLiteral(TableField<?> field, Object paramOrExp) {
        return this.setExp(field, field.plusLiteral(paramOrExp));
    }

    @Override
    public final SR setMinus(TableField<?> field, Object paramOrExp) {
        return this.setExp(field, field.minus(paramOrExp));
    }

    @Override
    public final SR setMinusLiteral(TableField<?> field, Object paramOrExp) {
        return this.setExp(field, field.minusLiteral(paramOrExp));
    }

    @Override
    public final SR setMultiply(TableField<?> field, Object paramOrExp) {
        return this.setExp(field, field.multiply(paramOrExp));
    }

    @Override
    public final SR setMultiplyLiteral(TableField<?> field, Object paramOrExp) {
        return this.setExp(field, field.multiplyLiteral(paramOrExp));
    }

    @Override
    public final SR setDivide(TableField<?> field, Object paramOrExp) {
        return this.setExp(field, field.divide(paramOrExp));
    }

    @Override
    public final SR setDivideLiteral(TableField<?> field, Object paramOrExp) {
        return this.setExp(field, field.divideLiteral(paramOrExp));
    }

    @Override
    public final SR setMod(TableField<?> field, Object paramOrExp) {
        return this.setExp(field, field.mod(paramOrExp));
    }

    @Override
    public final SR setModLiteral(TableField<?> field, Object paramOrExp) {
        return this.setExp(field, field.modLiteral(paramOrExp));
    }

    @Override
    public final SR ifSet(TableField<?> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, SQLs.nonNullParam(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(TableField<?> field, Supplier<?> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, SQLs.nonNullParam(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(TableField<?> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, SQLs.nonNullLiteral(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(TableField<?> field, Supplier<?> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, SQLs.nonNullLiteral(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetPlus(TableField<?> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.plus(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetPlus(TableField<?> field, Supplier<?> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.plus(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMinus(TableField<?> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.minus(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMinus(TableField<?> field, Supplier<?> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.minus(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMultiply(TableField<?> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.multiply(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMultiply(TableField<?> field, Supplier<?> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.multiply(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetDivide(TableField<?> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.divide(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetDivide(TableField<?> field, Supplier<?> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.divide(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMod(TableField<?> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.mod(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMod(TableField<?> field, Supplier<?> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.mod(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetPlusLiteral(TableField<?> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.plusLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetPlusLiteral(TableField<?> field, Supplier<?> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.plusLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMinusLiteral(TableField<?> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.minusLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMinusLiteral(TableField<?> field, Supplier<?> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.minusLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMultiplyLiteral(TableField<?> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.multiplyLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetMultiplyLiteral(TableField<?> field, Supplier<?> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.multiplyLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetDivideLiteral(TableField<?> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.divideLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetDivideLiteral(TableField<?> field, Supplier<?> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.divideLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetModLiteral(TableField<?> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.setExp(field, field.modLiteral(value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetModLiteral(TableField<?> field, Supplier<?> paramOrExp) {
        final Object value;
        value = paramOrExp.get();
        if (value != null) {
            this.setExp(field, field.modLiteral(value));
        }
        return (SR) this;
    }

    /*################################## blow BatchSetClause  method ##################################*/

    @Override
    public final SR setExp(TableField<?> field, Expression value) {
        Objects.requireNonNull(value);
        this.leftList.add(field);
        this.rightList.add(value);
        return (SR) this;
    }

    @Override
    public final SR ifSetExp(TableField<?> field, Supplier<? extends Expression> supplier) {
        final Expression exp;
        exp = supplier.get();
        if (exp != null) {
            this.setExp(field, exp);
        }
        return (SR) this;
    }

    @Override
    public final SR setNullable(List<? extends TableField<?>> fieldList) {
        if (fieldList.size() == 0) {
            throw batchSetLisEmpty();
        }
        final List<SetLeftItem> leftList = this.leftList;
        final List<SetRightItem> rightList = this.rightList;
        for (TableField<?> field : fieldList) {
            leftList.add(field);
            rightList.add(SQLs.nullableNamedParam(field));
        }
        return (SR) this;
    }

    @Override
    public final SR set(List<TableField<?>> fieldList) {
        if (fieldList.size() == 0) {
            throw batchSetLisEmpty();
        }
        final List<SetLeftItem> leftList = this.leftList;
        final List<SetRightItem> rightList = this.rightList;
        for (TableField<?> field : fieldList) {
            leftList.add(field);
            rightList.add(SQLs.namedParam(field));
        }
        return (SR) this;
    }

    @Override
    public final SR set(Consumer<List<TableField<?>>> consumer) {
        final List<TableField<?>> list = new ArrayList<>();
        consumer.accept(list);
        return this.set(list);
    }


    @Override
    public final SR setNullable(Consumer<List<TableField<?>>> consumer) {
        final List<TableField<?>> list = new ArrayList<>();
        consumer.accept(list);
        return this.setNullable(list);
    }

    @Override
    public final SR set(Function<C, List<TableField<?>>> function) {
        return this.set(function.apply(this.criteria));
    }

    @Override
    public final SR setNullable(Function<C, List<TableField<?>>> function) {
        return this.setNullable(function.apply(this.criteria));
    }

    @Override
    public final SR set(Supplier<List<TableField<?>>> supplier) {
        return this.set(supplier.get());
    }

    @Override
    public final SR setNullable(Supplier<List<TableField<?>>> supplier) {
        return this.setNullable(supplier.get());
    }

    @Override
    public final SR setNullable(TableField<?> field) {
        return this.setExp(field, SQLs.nullableNamedParam(field));
    }

    @Override
    public final SR set(TableField<?> field) {
        return this.setExp(field, SQLs.namedParam(field));
    }

    @Override
    public final SR setPlus(TableField<?> field) {
        return this.setExp(field, field.plus(SQLs.namedParam(field)));
    }

    @Override
    public final SR setMinus(TableField<?> field) {
        return this.setExp(field, field.minus(SQLs.namedParam(field)));
    }

    @Override
    public final SR setMultiply(TableField<?> field) {
        return this.setExp(field, field.multiply(SQLs.namedParam(field)));
    }

    @Override
    public final SR setDivide(TableField<?> field) {
        return this.setExp(field, field.divide(SQLs.namedParam(field)));
    }

    @Override
    public final SR setMod(TableField<?> field) {
        return this.setExp(field, field.mod(SQLs.namedParam(field)));
    }

    @Override
    public final SR ifSet(Function<C, List<TableField<?>>> function) {
        final List<TableField<?>> fieldList;
        fieldList = function.apply(this.criteria);
        if (fieldList.size() > 0) {
            this.set(fieldList);
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetNullable(Function<C, List<TableField<?>>> function) {
        final List<? extends TableField<?>> fieldList;
        fieldList = function.apply(this.criteria);
        if (fieldList.size() > 0) {
            this.setNullable(fieldList);
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(Predicate<C> test, TableField<?> field) {
        if (test.test(this.criteria)) {
            this.setExp(field, SQLs.namedParam(field));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetNullable(Predicate<C> test, TableField<?> field) {
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
        if (this instanceof SubStatement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        super.asDmlStatement();

        final List<SetLeftItem> leftItemList = this.leftList;
        final List<SetRightItem> rightItemList = this.rightList;
        if (leftItemList == null || leftItemList.size() == 0) {
            throw _Exceptions.updateFieldListEmpty();
        }
        if (rightItemList == null || rightItemList.size() != leftItemList.size()) {
            // no bug ,never here
            throw new IllegalStateException("target and value size not match.");
        }
        this.leftList = _CollectionUtils.unmodifiableList(leftItemList);
        this.rightList = _CollectionUtils.unmodifiableList(rightItemList);
        this.onAsUpdate();
        this.prepared = true;
        return this;
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = false;
        super.clearWherePredicate();
        this.leftList = null;

        this.rightList = null;
        this.onClear();
    }


    @Override
    public final List<? extends SetLeftItem> leftItemList() {
        _Assert.prepared(this.prepared);
        return this.leftList;
    }

    @Override
    public final List<? extends SetRightItem> rightItemList() {
        _Assert.prepared(this.prepared);
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
