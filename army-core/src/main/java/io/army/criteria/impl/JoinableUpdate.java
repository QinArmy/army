package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.lang.Nullable;
import io.army.util._Assert;
import io.army.util._ClassUtils;
import io.army.util._Collections;
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
 * @see SetWhereClause
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class JoinableUpdate<I extends Item, B extends CteBuilderSpec, WE extends Item, F extends SQLField, SR, FT, FS, FC, FF, JT, JS, JC, JF, WR, WA>
        extends JoinableClause<FT, FS, FC, FF, JT, JS, JC, JF, WR, WA, Object, Object, Object, Object, Object>
        implements _Update,
        DialectStatement._DynamicWithClause<B, WE>,
        _Statement._WithClauseSpec,
        _Statement._JoinableStatement,
        UpdateStatement._StaticBatchSetClause<F, SR>,
        UpdateStatement._StaticBatchRowSetClause<F, SR>,
        _Statement._ItemPairList,
        Statement._DmlUpdateSpec<I>,
        Statement {

    private boolean recursive;

    private List<_Cte> cteList;

    private List<_TabularBlock> tableBlockList;

    private List<_ItemPair> itemPairList;

    private Boolean prepared;

    JoinableUpdate(@Nullable ArmyStmtSpec spec, CriteriaContext context) {
        super(context);
        if (spec != null) {
            this.recursive = spec.isRecursive();
            this.cteList = spec.cteList();
        }
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

    @Override
    public final SR set(F field, Expression value) {
        return this.onAddItemPair(SQLs._itemPair(field, null, value));
    }

    @Override
    public final <R extends AssignmentItem> SR set(F field, Supplier<R> supplier) {
        return this.onAddAssignmentItemPair(field, supplier.get());
    }


    @Override
    public final <R extends AssignmentItem> SR set(F field, Function<F, R> function) {
        return this.onAddAssignmentItemPair(field, function.apply(field));
    }


    @Override
    public final <E, R extends AssignmentItem> SR set(final F field, final BiFunction<F, E, R> valueOperator,
                                                      final @Nullable E value) {
        return this.onAddAssignmentItemPair(field, valueOperator.apply(field, value));
    }


    @Override
    public final <K, V, R extends AssignmentItem> SR set(final F field, final BiFunction<F, V, R> valueOperator,
                                                         final Function<K, V> function, final K key) {
        return this.onAddAssignmentItemPair(field, valueOperator.apply(field, function.apply(key)));
    }

    @Override
    public final <E, V, R extends AssignmentItem> SR set(F field, BiFunction<F, V, R> fieldOperator,
                                                         BiFunction<F, E, V> valueOperator, E value) {
        return this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, value)));
    }

    @Override
    public final <K, V, U, R extends AssignmentItem> SR set(F field, BiFunction<F, U, R> fieldOperator,
                                                            BiFunction<F, V, U> valueOperator, Function<K, V> function,
                                                            K key) {
        return this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, function.apply(key))));
    }

    @Override
    public final <R extends AssignmentItem> SR ifSet(final F field, Supplier<R> supplier) {
        final R item;
        if ((item = supplier.get()) != null) {
            this.onAddAssignmentItemPair(field, item);
        }
        return (SR) this;
    }

    @Override
    public final <R extends AssignmentItem> SR ifSet(final F field, Function<F, R> function) {
        final R item;
        if ((item = function.apply(field)) != null) {
            this.onAddAssignmentItemPair(field, item);
        }
        return (SR) this;
    }

    @Override
    public final <E, R extends AssignmentItem> SR ifSet(F field, BiFunction<F, E, R> valueOperator, Supplier<E> supplier) {
        final E value;
        if ((value = supplier.get()) != null) {
            this.onAddAssignmentItemPair(field, valueOperator.apply(field, value));
        }
        return (SR) this;
    }

    @Override
    public final <K, V, R extends AssignmentItem> SR ifSet(F field, BiFunction<F, V, R> valueOperator,
                                                           Function<K, V> function, K key) {
        final V value;
        if ((value = function.apply(key)) != null) {
            this.onAddAssignmentItemPair(field, valueOperator.apply(field, value));
        }
        return (SR) this;
    }

    @Override
    public final <E, V, R extends AssignmentItem> SR ifSet(F field, BiFunction<F, V, R> fieldOperator,
                                                           BiFunction<F, E, V> valueOperator, Supplier<E> getter) {
        final E value;
        if ((value = getter.get()) != null) {
            this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final <K, V, U, R extends AssignmentItem> SR ifSet(F field, BiFunction<F, U, R> fieldOperator,
                                                              BiFunction<F, V, U> valueOperator,
                                                              Function<K, V> function, K key) {
        final V value;
        if ((value = function.apply(key)) != null) {
            this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final SR setNamed(F field, BiFunction<F, String, Expression> valueOperator) {
        return this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, field.fieldName())));
    }

    @Override
    public final <R extends AssignmentItem> SR setNamed(F field, BiFunction<F, Expression, R> fieldOperator
            , BiFunction<F, String, Expression> valueOperator) {
        return this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, field.fieldName())));
    }

    @Override
    public final SR setRow(F field1, F field2, Supplier<SubQuery> supplier) {
        final List<F> fieldList;
        fieldList = Arrays.asList(field1, field2);
        return this.onAddItemPair(SQLs._itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR setRow(F field1, F field2, F field3, Supplier<SubQuery> supplier) {
        final List<F> fieldList;
        fieldList = Arrays.asList(field1, field2, field3);
        return this.onAddItemPair(SQLs._itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR setRow(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier) {
        final List<F> fieldList;
        fieldList = Arrays.asList(field1, field2, field3, field4);
        return this.onAddItemPair(SQLs._itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR setRow(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier) {
        final List<F> fieldList = _Collections.arrayList();
        consumer.accept(fieldList::add);
        return this.onAddItemPair(SQLs._itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR ifSetRow(F field1, F field2, Supplier<SubQuery> supplier) {
        final SubQuery query;
        if ((query = supplier.get()) != null) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2);
            this.onAddItemPair(SQLs._itemPair(fieldList, query));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetRow(F field1, F field2, F field3, Supplier<SubQuery> supplier) {
        final SubQuery query;
        if ((query = supplier.get()) != null) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2, field3);
            this.onAddItemPair(SQLs._itemPair(fieldList, query));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetRow(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier) {
        final SubQuery query;
        if ((query = supplier.get()) != null) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2, field3, field4);
            this.onAddItemPair(SQLs._itemPair(fieldList, query));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetRow(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier) {
        final List<F> fieldList = _Collections.arrayList();
        consumer.accept(fieldList::add);
        final SubQuery query;
        if (fieldList.size() > 0 && (query = supplier.get()) != null) {
            this.onAddItemPair(SQLs._itemPair(fieldList, query));
        }
        return (SR) this;
    }

    @Override
    public final List<_TabularBlock> tableBlockList() {
        final List<_TabularBlock> list = this.tableBlockList;
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
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = Boolean.FALSE;
        this.clearWhereClause();
        this.tableBlockList = null;
        this.itemPairList = null;
        this.onClear();
    }


    abstract I onAsUpdate();

    void onClear() {
        //no-op
    }

    void onBeforeContextEnd() {
        //no-op
    }


    abstract B createCteBuilder(boolean recursive);

    final SR onAddItemPair(final ItemPair pair) {
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

    final WE endStaticWithClause(final boolean recursive) {
        this.recursive = recursive;
        this.cteList = this.context.endWithClause(recursive, true);//static with syntax is required
        return (WE) this;
    }


    final void endUpdateStatement() {
        _Assert.nonPrepared(this.prepared);

        final CriteriaContext context = this.context;

        final List<_ItemPair> itemPairList = this.itemPairList;
        if (itemPairList == null || itemPairList.size() == 0) {
            throw ContextStack.criteriaError(this.context, _Exceptions::setClauseNotExists);
        }
        this.itemPairList = Collections.unmodifiableList(itemPairList);
        if (this.endWhereClauseIfNeed().size() == 0) {
            throw ContextStack.criteriaError(this.context, _Exceptions::dmlNoWhereClause);
        }

        this.onBeforeContextEnd();
        this.tableBlockList = context.endContext();

        ContextStack.pop(context);
        this.prepared = Boolean.TRUE;
    }


    @SuppressWarnings("unchecked")
    private WE endDynamicWithClause(final B builder, final boolean required) {
        ((CriteriaSupports.CteBuilder) builder).endLastCte();

        final boolean recursive;
        recursive = builder.isRecursive();
        this.recursive = recursive;
        this.cteList = this.context.endWithClause(recursive, required);
        return (WE) this;
    }


    private SR onAddAssignmentItemPair(final F field, final @Nullable AssignmentItem item) {
        final ItemPair pair;
        if (item == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (item instanceof Expression) {
            pair = SQLs._itemPair(field, null, (Expression) item);
        } else if (item instanceof ItemPair) {
            pair = (ItemPair) item;
        } else {
            throw CriteriaUtils.illegalAssignmentItem(this.context, item);
        }
        return this.onAddItemPair(pair);
    }



}
