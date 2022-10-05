package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._Assert;
import io.army.util._ClassUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
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
abstract class JoinableUpdate<C, F extends DataField, SR, FT, FS, FP, FJ, JT, JS, JP, WR, WA, U extends DmlStatement.DmlUpdate>
        extends DmlWhereClause<C, FT, FS, FP, FJ, JT, JS, JP, WR, WA>
        implements DmlStatement.DmlUpdate, DmlStatement._DmlUpdateSpec<U>
        , Update._SimpleSetClause<C, F, SR>, Update._BatchSetClause<C, F, SR>
        , _Update {


    private final boolean supportRowLeftItem;

    private List<_ItemPair> itemPairList;

    private List<_ItemPair> childItemPairList;

    private Boolean prepared;


    JoinableUpdate(CriteriaContext context) {
        super(context);
        assert this instanceof MultiUpdate;
        this.supportRowLeftItem = this.isSupportRowLeftItem();

        if (this instanceof SubStatement) {
            ContextStack.push(context);
        } else {
            ContextStack.setContextStack(context);
        }
    }

    JoinableUpdate(CriteriaContext context, ClauseCreator<FP, JT, JS, JP> clauseCreator) {
        super(context, clauseCreator);
        assert this instanceof SingleUpdate;

        this.supportRowLeftItem = this.isSupportRowLeftItem();
        //single update no CriteriaContextStack operation
    }

    /*################################## blow SetClause method ##################################*/


    @Override
    public final SR setPairs(Consumer<Consumer<ItemPair>> consumer) {
        consumer.accept(this::addItemPair);
        return (SR) this;
    }

    @Override
    public final SR setPairs(BiConsumer<C, Consumer<ItemPair>> consumer) {
        consumer.accept(this.criteria, this::addItemPair);
        return (SR) this;
    }

    @Override
    public final SR set(final F field, final @Nullable Expression value) {
        if (value == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.addItemPair(SQLs._itemPair(field, null, value));
    }

    @Override
    public final SR set(F field, Supplier<Expression> supplier) {
        return this.set(field, supplier.get());
    }

    @Override
    public final SR set(F field, Function<C, Expression> function) {
        return this.set(field, function.apply(this.criteria));
    }

    @Override
    public final <T> SR set(F field, BiFunction<F, T, Expression> valueOperator, @Nullable T value) {
        return this.addItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
    }

    @Override
    public final <T> SR set(F field, BiFunction<F, T, Expression> valueOperator, Supplier<T> supplier) {
        return this.addItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, supplier.get())));
    }

    @Override
    public final SR set(F field, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return this.addItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, function.apply(keyName))));
    }

    @Override
    public final <T> SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, T, Expression> valueOperator, @Nullable T value) {
        return this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
    }

    @Override
    public final <T> SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, T, Expression> valueOperator, Supplier<T> supplier) {
        return this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, supplier.get())));
    }

    @Override
    public final SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, function.apply(keyName))));
    }

    @Override
    public final <T> SR ifSet(F field, BiFunction<F, T, Expression> valueOperator, @Nullable T value) {
        if (value != null) {
            this.addItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final <T> SR ifSet(F field, BiFunction<F, T, Expression> valueOperator, Supplier<T> supplier) {
        final T value;
        value = supplier.get();
        if (value != null) {
            this.addItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(F field, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.addItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    @Override
    public <T> SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, T, Expression> valueOperator, @Nullable T value) {
        if (value != null) {
            this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final <T> SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, T, Expression> valueOperator, Supplier<T> supplier) {
        final T value;
        value = supplier.get();
        if (value != null) {
            this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
        }
        return (SR) this;
    }

    /*################################## blow _SimpleSetClause method ##################################*/


    /*################################## blow _BatchSetClause method ##################################*/

    @Override
    public final SR set(F field, BiFunction<F, String, Expression> valueOperator) {
        return this.addItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, field.fieldName())));
    }

    @Override
    public final SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, String, Expression> valueOperator) {
        return this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, field.fieldName())));
    }

    @Override
    public final SR setList(List<F> fieldList, BiFunction<F, String, Expression> valueOperator) {
        if (fieldList.size() == 0) {
            throw ContextStack.criteriaError(this.context, "fieldList is null");
        }
        for (F field : fieldList) {
            this.addItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, field.fieldName())));
        }
        return (SR) this;
    }

    @Override
    public final SR setList(List<F> fieldList, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, String, Expression> valueOperator) {
        if (fieldList.size() == 0) {
            throw ContextStack.criteriaError(this.context, "fieldList is null");
        }
        for (F field : fieldList) {
            this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, field.fieldName())));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetList(List<F> fieldList, BiFunction<F, String, Expression> valueOperator) {
        for (F field : fieldList) {
            this.addItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, field.fieldName())));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetList(List<F> fieldList, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, String, Expression> valueOperator) {
        for (F field : fieldList) {
            this.addItemPair(fieldOperator.apply(field, valueOperator.apply(field, field.fieldName())));
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
    public final U asUpdate() {
        _Assert.nonPrepared(this.prepared);
        if (this instanceof SubStatement) {
            ContextStack.pop(this.context);
        } else {
            ContextStack.clearContextStack(this.context);
        }
        if (this instanceof SingleUpdate) {
            /// only single update clear context.
            this.context.endContext();
        }
        super.asDmlStatement();

        final List<_ItemPair> itemPairList = this.itemPairList;
        final List<_ItemPair> childItemPairList = this.childItemPairList;
        if ((itemPairList == null || itemPairList.size() == 0)
                && (childItemPairList == null || childItemPairList.size() == 0)) {
            throw _Exceptions.updateFieldListEmpty();
        }

        if (itemPairList == null) {
            this.itemPairList = Collections.emptyList();
        } else {
            this.itemPairList = _CollectionUtils.unmodifiableList(itemPairList);
        }
        if (childItemPairList == null) {
            this.childItemPairList = Collections.emptyList();
        } else {
            this.childItemPairList = _CollectionUtils.unmodifiableList(childItemPairList);
        }

        this.onAsUpdate();
        this.prepared = Boolean.TRUE;
        return (U) this;
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = Boolean.FALSE;
        super.clearWherePredicate();
        this.onClear();
    }


    @Override
    public final List<_ItemPair> itemPairList() {
        _Assert.prepared(this.prepared);
        return this.itemPairList;
    }

    @Override
    public final List<_ItemPair> childItemPairList() {
        _Assert.prepared(this.prepared);
        return this.childItemPairList;
    }

    void onAsUpdate() {

    }

    void onClear() {

    }

    abstract boolean isSupportRowLeftItem();


    @Nullable
    abstract Dialect dialect();


    private SR addItemPair(final ItemPair pair) {
        if (!(pair instanceof SQLs.ArmyItemPair)) {
            throw illegalItemPair(pair);
        }
        if (pair instanceof SQLs.FieldItemPair) {//TODO validate update mode,nullable etc.
            final DataField field = ((SQLs.FieldItemPair) pair).field;
            if (this instanceof _MultiUpdate) {
                this.doAddItemPair((SQLs.ArmyItemPair) pair);
            } else if (!(field instanceof FieldMeta)) {
                String m = "not multi-table update support only %s" + FieldMeta.class.getName();
                throw ContextStack.criteriaError(this.context, m);
            } else if (this instanceof _DomainUpdate) {
                if (((TableField) field).tableMeta() instanceof ChildTableMeta) {
                    this.doAddChildItemPair((SQLs.ArmyItemPair) pair);
                } else {
                    this.doAddItemPair((SQLs.ArmyItemPair) pair);
                }
            } else if (!(this instanceof _SingleUpdate)) {
                //no bug,never here
                throw new IllegalStateException("unknown update statement");
            } else if (((TableField) field).tableMeta() instanceof ChildTableMeta) {
                String m = "single update don't support child field";
                throw ContextStack.criteriaError(this.context, m);
            } else {
                this.doAddItemPair((SQLs.ArmyItemPair) pair);
            }
        } else if (!(pair instanceof SQLs.RowItemPair)) {
            //no bug,never here
            throw new IllegalStateException("unknown ItemPair");
        } else if (!this.supportRowLeftItem) {
            throw ContextStack.criteriaError(this.context, _Exceptions::dontSupportRowLeftItem
                    , this.dialect());
        } else if (this instanceof _MultiUpdate || !isChildTable((SQLs.RowItemPair) pair)) {
            this.doAddItemPair((SQLs.ArmyItemPair) pair);
        } else {
            this.doAddChildItemPair((SQLs.ArmyItemPair) pair);
        }
        return (SR) this;
    }

    private void doAddChildItemPair(final SQLs.ArmyItemPair pair) {
        List<_ItemPair> childItemPairList = this.childItemPairList;
        if (childItemPairList == null) {
            childItemPairList = new ArrayList<>();
            this.childItemPairList = childItemPairList;
        } else if (!(childItemPairList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        childItemPairList.add(pair);
    }

    private void doAddItemPair(final SQLs.ArmyItemPair pair) {
        List<_ItemPair> itemPairList = this.itemPairList;
        if (itemPairList == null) {
            itemPairList = new ArrayList<>();
            this.itemPairList = itemPairList;
        } else if (!(itemPairList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        itemPairList.add(pair);
    }

    private CriteriaException illegalItemPair(@Nullable ItemPair itemPair) {
        String m = String.format("%s Illegal %s", _ClassUtils.safeClassName(itemPair), ItemPair.class.getName());
        throw ContextStack.criteriaError(this.context, m);
    }

    private static boolean isChildTable(final SQLs.RowItemPair pair) {
        TableMeta<?> table = null;
        for (DataField field : pair.fieldList) {
            if (!(field instanceof TableField)) {
                // no derived field,because don't support multi-table update
                throw _Exceptions.immutableField(field);
            }
            if (table == null) {
                table = ((TableField) field).tableMeta();
            } else if (((TableField) field).tableMeta() != table) {
                throw rowColumnTableNotMatch(pair.fieldList);
            }
        }
        assert table != null;
        return table instanceof ChildTableMeta;
    }


    private static CriteriaException rowColumnTableNotMatch(List<? extends DataField> fieldList) {
        String m = String.format("Row columns %s table not match", fieldList);
        return new CriteriaException(m);
    }


}
