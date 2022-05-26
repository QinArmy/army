package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Update;
import io.army.lang.Nullable;
import io.army.util._Assert;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

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
abstract class JoinableUpdate<C, F extends DataField, SR, FT, FS, FP, JT, JS, JP, WR, WA>
        extends DmlWhereClause<C, FT, FS, FP, JT, JS, JP, WR, WA>
        implements Update, Update._UpdateSpec, Update._SimpleSetClause<C, F, SR>, Update._BatchSetClause<C, F, SR>
        , _Update {


    final CriteriaContext criteriaContext;

    private List<_ItemPair> parentPairList = new ArrayList<>();

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
    public final SR setPairs(Consumer<Consumer<ItemPair>> consumer) {
        return null;
    }

    @Override
    public final SR setPairs(BiConsumer<C, Consumer<ItemPair>> consumer) {
        return null;
    }

    @Override
    public final SR ifSetPairs(Consumer<Consumer<ItemPair>> consumer) {
        return null;
    }

    @Override
    public final SR ifSetPairs(BiConsumer<C, Consumer<ItemPair>> consumer) {
        return null;
    }

    @Override
    public final SR setExp(F field, Expression value) {
        return null;
    }

    @Override
    public final SR setExp(F field, Supplier<? extends Expression> supplier) {
        return null;
    }

    @Override
    public final SR setExp(F field, Function<C, ? extends Expression> function) {
        return null;
    }

    @Override
    public final SR ifSetExp(F field, Supplier<? extends Expression> supplier) {
        return null;
    }

    @Override
    public final SR ifSetExp(F field, Function<C, ? extends Expression> function) {
        return null;
    }

    /*################################## blow _SimpleSetClause method ##################################*/

    @Override
    public final SR set(F field, @Nullable Object value) {
        return this.setExp(field, SQLs._nullableParam(field, value));
    }

    @Override
    public final SR setDefault(F field) {
        return this.setExp(field, SQLs.defaultWord());
    }

    @Override
    public final SR setLiteral(F field, @Nullable Object value) {
        return this.setExp(field, SQLs._nullableLiteral(field, value));
    }

    @Override
    public final SR setPlus(F field, Object value) {

        return null;
    }

    @Override
    public final SR setMinus(F field, Object value) {
        return null;
    }

    @Override
    public final SR setPlusLiteral(F field, Object value) {
        return null;
    }

    @Override
    public final SR setMinusLiteral(F field, Object value) {
        return null;
    }

    @Override
    public final SR set(F field, BiFunction<DataField, Object, ItemPair> function, Object value) {
        return null;
    }

    @Override
    public final SR setExp(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<? extends Expression> supplier) {
        return null;
    }

    @Override
    public final SR setExp(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ? extends Expression> function) {
        return null;
    }

    @Override
    public final SR setLiteral(F field, BiFunction<DataField, Object, ItemPair> function, Object value) {
        return null;
    }

    @Override
    public final SR setLiteralExp(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<? extends Expression> supplier) {
        return null;
    }

    @Override
    public final SR setLiteralExp(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ? extends Expression> function) {
        return null;
    }

    @Override
    public final SR ifSet(F field, Supplier<?> supplier) {
        return null;
    }

    @Override
    public final SR ifSet(F field, Function<String, ?> function, String keyName) {
        return null;
    }

    @Override
    public final SR ifSet(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<?> supplier) {
        return null;
    }

    @Override
    public final SR ifSet(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ?> function) {
        return null;
    }

    @Override
    public final SR ifSet(F field, BiFunction<DataField, Object, ItemPair> operator, Function<String, ?> function, String keyName) {
        return null;
    }

    @Override
    public final SR ifSetLiteral(F field, Supplier<?> supplier) {
        return null;
    }

    @Override
    public final SR ifSetLiteral(F field, Function<String, ?> function, String keyName) {
        return null;
    }

    @Override
    public final SR ifSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<?> supplier) {
        return null;
    }

    @Override
    public final SR ifSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ?> function) {
        return null;
    }

    @Override
    public final SR ifSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Function<String, ?> function, String keyName) {
        return null;
    }

    /*################################## blow _BatchSetClause method ##################################*/

    @Override
    public final SR set(F field) {
        return null;
    }

    @Override
    public final SR setNullable(F field) {
        return null;
    }

    @Override
    public final SR setNamed(F field, String parameterName) {
        return null;
    }

    @Override
    public final SR set(F field, BiFunction<DataField, Object, ItemPair> operator) {
        return null;
    }

    @Override
    public final SR setNamed(F field, BiFunction<DataField, Object, ItemPair> operator, String parameterName) {
        return null;
    }

    @Override
    public final SR set(Consumer<Consumer<F>> consumer) {
        return null;
    }

    @Override
    public final SR set(BiConsumer<C, Consumer<F>> consumer) {
        return null;
    }

    @Override
    public final SR setNullable(Consumer<Consumer<F>> consumer) {
        return null;
    }

    @Override
    public final SR setNullable(BiConsumer<C, Consumer<F>> consumer) {
        return null;
    }

    @Override
    public final SR ifSet(Consumer<Consumer<F>> consumer) {
        return null;
    }

    @Override
    public final SR ifSet(BiConsumer<C, Consumer<F>> consumer) {
        return null;
    }

    @Override
    public final SR ifSetNullable(Consumer<Consumer<F>> consumer) {
        return null;
    }

    @Override
    public final SR ifSetNullable(BiConsumer<C, Consumer<F>> consumer) {
        return null;
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
        if (this instanceof SingleUpdate) {
            this.criteriaContext.clear();
        }
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


    abstract void addChildItemPair(ItemPair pair);


    private static CriteriaException batchSetLisEmpty() {
        return new CriteriaException("Batch set clause field list must non empty.");
    }


}
