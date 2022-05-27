package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner._Update;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Assert;
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
abstract class JoinableUpdate<C, F extends DataField, SR, FT, FS, FP, JT, JS, JP, WR, WA>
        extends DmlWhereClause<C, FT, FS, FP, JT, JS, JP, WR, WA>
        implements Update, Update._UpdateSpec, Update._SimpleSetClause<C, F, SR>, Update._BatchSetClause<C, F, SR>
        , _Update {


    final CriteriaContext criteriaContext;

    private final boolean supportRowLeftItem;

    private final boolean supportMultiTableUpdate;

    private List<_ItemPair> itemPairList;

    private List<_ItemPair> childItemPairList;

    private boolean prepared;

    JoinableUpdate(ClauseSupplier clauseSupplier, CriteriaContext criteriaContext) {
        super(clauseSupplier, criteriaContext.criteria());
        this.criteriaContext = criteriaContext;
        final Dialect dialect;
        dialect = this.dialect();
        this.supportRowLeftItem = dialect != null && dialect.supportRowLeftItem();
        this.supportMultiTableUpdate = dialect != null && dialect.supportMultiTableUpdate();
    }

    JoinableUpdate(CriteriaContext criteriaContext) {
        super(criteriaContext.criteria());
        this.criteriaContext = criteriaContext;
        final Dialect dialect;
        dialect = this.dialect();
        this.supportRowLeftItem = dialect != null && dialect.supportRowLeftItem();
        this.supportMultiTableUpdate = dialect != null && dialect.supportMultiTableUpdate();
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
    public final SR setExp(final F field, final Expression value) {
        return this.addItemPair(SQLs._itemExpPair(field, value));
    }

    @Override
    public final SR setExp(F field, Supplier<? extends Expression> supplier) {
        return this.addItemPair(SQLs._itemExpPair(field, supplier.get()));
    }

    @Override
    public final SR setExp(F field, Function<C, ? extends Expression> function) {
        return this.addItemPair(SQLs._itemExpPair(field, function.apply(this.criteria)));
    }

    @Override
    public final SR ifSetExp(F field, Supplier<? extends Expression> supplier) {
        final Expression value;
        value = supplier.get();
        if (value != null) {
            this.addItemPair(SQLs._itemExpPair(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetExp(F field, Function<C, ? extends Expression> function) {
        final Expression value;
        value = function.apply(this.criteria);
        if (value != null) {
            this.addItemPair(SQLs._itemExpPair(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR setDefault(F field) {
        return this.addItemPair(SQLs._itemExpPair(field, SQLs.defaultWord()));
    }

    @Override
    public final SR setNull(F field) {
        return this.addItemPair(SQLs._itemExpPair(field, SQLs.nullWord()));
    }

    /*################################## blow _SimpleSetClause method ##################################*/

    @Override
    public final SR set(F field, @Nullable Object value) {
        return this.addItemPair(SQLs.itemPair(field, value));
    }


    @Override
    public final SR setLiteral(F field, @Nullable Object value) {
        return this.addItemPair(SQLs._itemExpPair(field, SQLs._nullableLiteral(field, value)));
    }

    @Override
    public final SR setPlus(F field, Object value) {
        return this.addItemPair(SQLs._itemPair(field, AssignOperator.PLUS_EQUAL, value));
    }

    @Override
    public final SR setMinus(F field, Object value) {
        return this.addItemPair(SQLs._itemPair(field, AssignOperator.MINUS_EQUAL, value));
    }

    @Override
    public final SR setPlusLiteral(F field, Object value) {
        return this.addItemPair(SQLs._itemPair(field, AssignOperator.PLUS_EQUAL, SQLs._nonNullLiteral(field, value)));
    }

    @Override
    public final SR setMinusLiteral(F field, Object value) {
        return this.addItemPair(SQLs._itemPair(field, AssignOperator.MINUS_EQUAL, SQLs._nonNullLiteral(field, value)));
    }

    @Override
    public final SR set(F field, BiFunction<DataField, Object, ItemPair> operator, Object value) {
        return this.addItemPair(operator.apply(field, value));
    }

    @Override
    public final SR setExp(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<? extends Expression> supplier) {
        final Expression value;
        value = supplier.get();
        assert value != null;
        return this.addItemPair(operator.apply(field, value));
    }

    @Override
    public final SR setExp(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ? extends Expression> function) {
        final Expression value;
        value = function.apply(this.criteria);
        assert value != null;
        return this.addItemPair(operator.apply(field, value));
    }

    @Override
    public final SR setLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Object value) {
        return this.addItemPair(operator.apply(field, SQLs._nonNullLiteral(field, value)));
    }

    @Override
    public final SR ifSet(F field, Supplier<?> supplier) {
        final Object value;
        value = supplier.get();
        if (value != null) {
            this.addItemPair(SQLs.itemPair(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(F field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.addItemPair(SQLs.itemPair(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<?> supplier) {
        final Object value;
        value = supplier.get();
        if (value != null) {
            this.addItemPair(operator.apply(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ?> function) {
        final Object value;
        value = function.apply(this.criteria);
        if (value != null) {
            this.addItemPair(operator.apply(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSet(F field, BiFunction<DataField, Object, ItemPair> operator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.addItemPair(operator.apply(field, value));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(F field, Supplier<?> supplier) {
        final Object value;
        value = supplier.get();
        if (value != null) {
            this.addItemPair(SQLs._itemExpPair(field, SQLs._nonNullLiteral(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(F field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.addItemPair(SQLs._itemExpPair(field, SQLs._nonNullLiteral(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<?> supplier) {
        final Object value;
        value = supplier.get();
        if (value != null) {
            this.addItemPair(operator.apply(field, SQLs._nonNullLiteral(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ?> function) {
        final Object value;
        value = function.apply(this.criteria);
        if (value != null) {
            this.addItemPair(operator.apply(field, SQLs._nonNullLiteral(field, value)));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.addItemPair(operator.apply(field, SQLs._nonNullLiteral(field, value)));
        }
        return (SR) this;
    }


    /*################################## blow _BatchSetClause method ##################################*/

    @Override
    public final SR set(F field) {
        return this.addItemPair(SQLs._itemExpPair(field, SQLs.namedParam(field)));
    }

    @Override
    public final SR setNullable(F field) {
        return this.addItemPair(SQLs._itemExpPair(field, SQLs.nullableNamedParam(field)));
    }

    @Override
    public final SR setNamed(F field, String parameterName) {
        return this.addItemPair(SQLs._itemExpPair(field, SQLs.namedParam(field.paramMeta(), parameterName)));
    }

    @Override
    public final SR setNullableNamed(F field, String parameterName) {
        return this.addItemPair(SQLs._itemExpPair(field, SQLs.nullableNamedParam(field.paramMeta(), parameterName)));
    }

    @Override
    public final SR setPlus(F field) {
        return this.addItemPair(SQLs._itemPair(field, AssignOperator.PLUS_EQUAL, SQLs.namedParam(field)));
    }

    @Override
    public final SR setMinus(F field) {
        return this.addItemPair(SQLs._itemPair(field, AssignOperator.MINUS_EQUAL, SQLs.namedParam(field)));
    }

    @Override
    public final SR set(F field, BiFunction<DataField, Object, ItemPair> operator) {
        return this.addItemPair(operator.apply(field, SQLs.namedParam(field)));
    }

    @Override
    public final SR setNamed(F field, BiFunction<DataField, Object, ItemPair> operator, String parameterName) {
        return this.addItemPair(operator.apply(field, SQLs.namedParam(field.paramMeta(), parameterName)));
    }

    @Override
    public final SR setFields(Consumer<Consumer<F>> consumer) {
        consumer.accept(this::set);
        return (SR) this;
    }

    @Override
    public final SR setFields(BiConsumer<C, Consumer<F>> consumer) {
        consumer.accept(this.criteria, this::set);
        return (SR) this;
    }

    @Override
    public final SR setNullableFields(Consumer<Consumer<F>> consumer) {
        consumer.accept(this::setNullable);
        return (SR) this;
    }

    @Override
    public final SR setNullableFields(BiConsumer<C, Consumer<F>> consumer) {
        consumer.accept(this.criteria, this::setNullable);
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
        if (this instanceof SingleUpdate) {
            this.criteriaContext.clear();
        }
        if (this instanceof SubStatement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
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
        this.prepared = true;
        return this;
    }

    @Override
    public final void clear() {
        _Assert.prepared(this.prepared);
        this.prepared = false;
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

    @Nullable
    abstract Dialect dialect();


    private SR addItemPair(final ItemPair pair) {
        if (!(pair instanceof SQLs.ArmyItemPair)) {
            throw new CriteriaException(String.format("Illegal %s", ItemPair.class.getName()));
        }

        if (pair instanceof SQLs.FieldItemPair) {
            final DataField field = ((SQLs.FieldItemPair) pair).field;
            if (this.supportMultiTableUpdate) {
                this.doAddItemPair((SQLs.ArmyItemPair) pair);
            } else if (!(field instanceof TableField)) {
                // no derived field,because don't support multi-table update
                throw _Exceptions.immutableField(field);
            } else if (((TableField) field).tableMeta() instanceof ChildTableMeta) {
                this.doAddChildItemPair((SQLs.ArmyItemPair) pair);
            } else {
                this.doAddItemPair((SQLs.ArmyItemPair) pair);
            }
        } else if (!(pair instanceof SQLs.RowItemPair)) {
            throw new IllegalStateException("unknown ItemPair");
        } else if (!this.supportRowLeftItem) {
            throw _Exceptions.dontSupportRowLeftItem(this.dialect());
        } else if (this.supportMultiTableUpdate || !isChildTable((SQLs.RowItemPair) pair)) {
            this.doAddItemPair((SQLs.ArmyItemPair) pair);
        } else {
            this.doAddChildItemPair((SQLs.ArmyItemPair) pair);
        }
        return (SR) this;
    }

    private void doAddChildItemPair(final SQLs.ArmyItemPair pair) {
        assert !this.supportMultiTableUpdate;
        List<_ItemPair> childItemPairList = this.childItemPairList;
        if (childItemPairList == null) {
            childItemPairList = new ArrayList<>();
            this.childItemPairList = childItemPairList;
        }
        childItemPairList.add(pair);
    }

    private void doAddItemPair(final SQLs.ArmyItemPair pair) {
        List<_ItemPair> itemPairList = this.itemPairList;
        if (itemPairList == null) {
            itemPairList = new ArrayList<>();
            this.itemPairList = itemPairList;
        }
        itemPairList.add(pair);
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
