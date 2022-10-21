package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.util._Assert;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of multi-table update implementation.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class MultiUpdate<I extends Item, Q extends Item, F extends DataField, PS extends Update._ItemPairBuilder, SR, FT, FS, FC, JT, JS, JC, WR, WA, OR, LR>
        extends JoinableClause<FT, FS, FC, JT, JS, JC, WR, WA, OR, LR>
        implements _MultiUpdate
        , Update._StaticBatchSetClause<F, SR>
        , Update._DynamicSetClause<PS, SR>
        , Update._StaticRowSetClause<F, SR>
        , _Statement._ItemPairList
        , Statement._DmlUpdateSpec<I>
        , Statement._DqlUpdateSpec<Q>
        , Statement {

    private List<_TableBlock> tableBlockList;

    private List<_ItemPair> itemPairList;

    private Boolean prepared;

    MultiUpdate(CriteriaContext context) {
        super(context);
        ContextStack.push(context);
    }


    @Override
    public final SR set(F field, Expression value) {
        return this.onAddItemPair(SQLs._itemPair(field, null, value));
    }

    @Override
    public final SR set(F field, Supplier<Expression> supplier) {
        return this.onAddItemPair(SQLs._itemPair(field, null, supplier.get()));
    }

    @Override
    public final SR set(F field, Function<F, Expression> function) {
        return this.onAddItemPair(SQLs._itemPair(field, null, function.apply(field)));
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
    public final SR ifSet(F field, Supplier<Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression != null) {
            this.onAddItemPair(SQLs._itemPair(field, null, expression));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(F field, Function<F, Expression> function) {
        final Expression expression;
        expression = function.apply(field);
        if (expression != null) {
            this.onAddItemPair(SQLs._itemPair(field, null, expression));
        }
        return (SR) this;
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
    public final SR ifSet(F field, BiFunction<F, Object, Expression> valueOperator
            , Function<String, ?> function, String keyName) {
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
        return this.onAddItemPair(SQLs._itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR set(F field1, F field2, F field3, Supplier<SubQuery> supplier) {
        final List<F> fieldList;
        fieldList = Arrays.asList(field1, field2, field3);
        return this.onAddItemPair(SQLs._itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR set(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier) {
        final List<F> fieldList;
        fieldList = Arrays.asList(field1, field2, field3, field4);
        return this.onAddItemPair(SQLs._itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR set(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier) {
        final List<F> fieldList = new ArrayList<>();
        consumer.accept(fieldList::add);
        return this.onAddItemPair(SQLs._itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR set(Consumer<PS> consumer) {
        consumer.accept(this.createItemPairBuilder(this::onAddItemPair));
        return (SR) this;
    }

    @Override
    public final List<_TableBlock> tableBlockList() {
        final List<_TableBlock> list = this.tableBlockList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<_ItemPair> itemPairList() {
        final List<_ItemPair> list = this.itemPairList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        final Boolean prepared = this.prepared;
        return prepared != null && prepared;
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
        _Assert.prepared(this.prepared);
        this.prepared = Boolean.FALSE;
        this.clearWhereClause();
        this.tableBlockList = null;
        this.itemPairList = null;
        this.onClear();
    }

    abstract PS createItemPairBuilder(Consumer<ItemPair> consumer);

    abstract I onAsUpdate();

    Q onAsReturningUpdate() {
        throw new UnsupportedOperationException();
    }

    void onClear() {
        //no-op
    }

    private SR onAddItemPair(final ItemPair pair) {
        if (!(pair instanceof SQLs.ArmyItemPair)) {
            String m = String.format("unknown %s[%s]", ItemPair.class.getName(), _ClassUtils.safeClassName(pair));
            throw ContextStack.criteriaError(this.context, m);
        }
        List<_ItemPair> itemPairList = this.itemPairList;
        if (itemPairList == null) {
            itemPairList = new ArrayList<>();
            this.itemPairList = itemPairList;
        } else if (!(itemPairList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        itemPairList.add((_ItemPair) pair);
        return (SR) this;
    }

    private void endUpdateStatement() {
        _Assert.nonPrepared(this.prepared);

        final CriteriaContext context = this.context;

        final List<_ItemPair> itemPairList = this.itemPairList;
        if (itemPairList == null || itemPairList.size() == 0) {
            throw ContextStack.criteriaError(this.context, _Exceptions::setClauseNotExists);
        }
        this.itemPairList = Collections.unmodifiableList(itemPairList);
        if (this.endWhereClause().size() == 0) {
            throw ContextStack.criteriaError(this.context, _Exceptions::dmlNoWhereClause);
        }

        this.tableBlockList = context.endContext();

        ContextStack.pop(context);
        this.prepared = Boolean.TRUE;
    }


    static abstract class WithMultiUpdate<I extends Item, Q extends Item, B extends CteBuilderSpec, WE, F extends DataField, PS extends Update._ItemPairBuilder, SR, FT, FS, FC, JT, JS, JC, WR, WA, OR, LR>
            extends MultiUpdate<I, Q, F, PS, SR, FT, FS, FC, JT, JS, JC, WR, WA, OR, LR>
            implements Query._DynamicWithClause<B, WE>
            , _Statement._WithClauseSpec {

        private boolean recursive;

        private List<_Cte> cteList;

        WithMultiUpdate(CriteriaContext context) {
            super(context);
        }

        @Override
        public final WE with(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, true);
        }


        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, true);
        }


        @Override
        public final WE ifWith(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(false);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, false);
        }


        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            final B builder;
            builder = this.createCteBuilder(true);
            consumer.accept(builder);
            return this.endDynamicWithClause(builder, false);
        }

        @Override
        public final boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public final List<_Cte> cteList() {
            List<_Cte> cteList = this.cteList;
            if (cteList == null) {
                cteList = Collections.emptyList();
                this.cteList = cteList;
            }
            return cteList;
        }


        final void endStaticWithClause(final boolean recursive) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(true);//static with syntax is required
        }


        abstract B createCteBuilder(boolean recursive);


        @SuppressWarnings("unchecked")
        private WE endDynamicWithClause(final B builder, final boolean required) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = builder.isRecursive();
            this.cteList = this.context.endWithClause(required);
            return (WE) this;
        }


    }//WithMultiUpdate


}
