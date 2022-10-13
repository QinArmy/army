package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._Update;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


@SuppressWarnings("unchecked")
abstract class SingleUpdate<I extends Item, Q extends Item, F extends TableField, PS extends Update._ItemPairBuilder, SR, SD, WR, WA, OR, LR>
        extends WhereClause<WR, WA, OR, LR>
        implements Statement
        , Update._StaticBatchSetClause<F, SR>
        , Update._DynamicSetClause<F, PS, SD>
        , Update._RowSetClause<F, SR>
        , Statement._DmlUpdateSpec<I>
        , Statement._DqlUpdateSpec<Q>
        , _Update, _SingleUpdate {


    private final TableMeta<?> updateTable;

    private final String tableAlias;


    SingleUpdate(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
        super(context);
        this.updateTable = updateTable;
        this.tableAlias = tableAlias;
    }


    @Override
    public final SD setPairs(Consumer<PS> consumer) {
        consumer.accept(this.createItemPairBuilder(this::onAddItemPair));
        return (SD) this;
    }

    @Override
    public final SR set(final F field, final Expression value) {
        return this.onAddItemPair(SQLs._itemPair(field, null, value));
    }

    @Override
    public final SR set(F field, Supplier<Expression> supplier) {
        return this.onAddItemPair(SQLs._itemPair(field, null, supplier.get()));
    }

    @Override
    public final <E> SR set(F field, BiFunction<F, E, Expression> valueOperator, @Nullable E value) {
        return this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
    }

    @Override
    public final <E> SR set(F field, BiFunction<F, E, Expression> valueOperator, Supplier<E> supplier) {
        return this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, supplier.get())));
    }

    @Override
    public final SR set(F field, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function
            , String keyName) {
        return this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, function.apply(keyName))));
    }

    @Override
    public final <E> SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator
            , BiFunction<F, E, Expression> valueOperator, @Nullable E value) {
        return this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
    }

    @Override
    public final <E> SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator
            , BiFunction<F, E, Expression> valueOperator, Supplier<E> supplier) {
        return this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, supplier.get())));
    }

    @Override
    public final SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator
            , BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, function.apply(keyName))));
    }

    @Override
    public final <E> SR ifSet(F field, BiFunction<F, E, Expression> valueOperator, @Nullable E value) {
        if (value != null) {
            this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final <E> SR ifSet(F field, BiFunction<F, E, Expression> valueOperator, Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(F field, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function
            , String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final <E> SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator
            , BiFunction<F, E, Expression> valueOperator, @Nullable E value) {
        if (value != null) {
            this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final <E> SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator
            , BiFunction<F, E, Expression> valueOperator, Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator
            , BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final SR set(F field, BiFunction<F, String, Expression> valueOperator) {
        return this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, field.fieldName())));
    }

    @Override
    public final SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator
            , BiFunction<F, String, Expression> valueOperator) {
        return this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, field.fieldName())));
    }

    @Override
    public final SR set(F field1, F field2, Supplier<SubQuery> supplier) {
        final List<F> fieldList;
        fieldList = Arrays.asList(field1, field2);
        return this.onAddItemPair(SQLs.itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR set(F field1, F field2, F field3, Supplier<SubQuery> supplier) {
        final List<F> fieldList;
        fieldList = Arrays.asList(field1, field2, field3);
        return this.onAddItemPair(SQLs.itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR set(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier) {
        final List<F> fieldList;
        fieldList = Arrays.asList(field1, field2, field3, field4);
        return this.onAddItemPair(SQLs.itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR set(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier) {
        final List<F> fieldList = new ArrayList<>();
        consumer.accept(fieldList::add);
        return this.onAddItemPair(SQLs.itemPair(fieldList, supplier.get()));
    }

    @Override
    public final void prepared() {

    }

    @Override
    public final boolean isPrepared() {
        return false;
    }


    @Override
    public final I asUpdate() {
        this.endUpdateStatement();
        return this.onAsUpdate();
    }

    @Override
    public final Q asReturningUpdate() {
        this.endUpdateStatement();
        return this.onAsReturningUpdate();
    }

    @Override
    public final void clear() {

    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public final TableMeta<?> table() {
        return this.updateTable;
    }

    @Override
    public final List<_ItemPair> itemPairList() {
        return null;
    }

    @Override
    public final List<_ItemPair> childItemPairList() {
        return Collections.emptyList();
    }


    void onClear() {
        //no-op
    }

    void onAddChildItem(SQLs.FieldItemPair pair) {
        throw new UnsupportedOperationException();
    }

    abstract I onAsUpdate();

    Q onAsReturningUpdate() {
        throw ContextStack.castCriteriaApi(this.context);
    }

    abstract PS createItemPairBuilder(Consumer<ItemPair> consumer);


    private SR onAddItemPair(final ItemPair pair) {

        return (SR) this;
    }


    private void endUpdateStatement() {
        this.endOrderByClause();
        this.endWhereClause();
        ContextStack.pop(this.context);
    }


    static abstract class DomainUpdate<I extends Item, Q extends Item, F extends TableField, PS extends Update._ItemPairBuilder, SR, SD, WR, WA, OR, LR>
            extends SingleUpdate<I, Q, F, PS, SR, SD, WR, WA, OR, LR> {

        private List<_ItemPair._FieldItemPair> childPairList;


        DomainUpdate(CriteriaContext context) {
            super(context);
        }


        @Override
        final void onAddChildItem(final SQLs.FieldItemPair pair) {

        }


    }//DomainUpdate


}
