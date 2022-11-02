package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._DomainUpdate;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner._Statement;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


@SuppressWarnings("unchecked")
abstract class SetWhereClause<F extends TableField, SR, WR, WA, OR, LR, LO, LF>
        extends WhereClause<WR, WA, OR, LR, LO, LF>
        implements Update._StaticBatchSetClause<F, SR>
        , Update._StaticRowSetClause<F, SR>
        , _Statement._ItemPairList
        , _Statement._TableMetaSpec {

    private List<_ItemPair> itemPairList = new ArrayList<>();

    final TableMeta<?> updateTable;

     final String tableAlias;

    /**
     * @param tableAlias for {@link SingleUpdate} non-null and  non-empty,for other non-null
     */
    SetWhereClause(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
        super(context);
        Objects.requireNonNull(updateTable);
        Objects.requireNonNull(tableAlias);
        this.updateTable = updateTable;
        this.tableAlias = tableAlias;

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
    public final TableMeta<?> table() {
        return this.updateTable;
    }

    @Override
    public final List<_ItemPair> itemPairList() {
        final List<_ItemPair> itemPairList = this.itemPairList;
        if (itemPairList == null || itemPairList instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return itemPairList;
    }

    void onAddChildItemPair(SQLs.ArmyItemPair pair) {
        throw new UnsupportedOperationException();
    }

    final List<_ItemPair> endUpdateSetClause() {
        List<_ItemPair> itemPairList = this.itemPairList;
        if (itemPairList == null || itemPairList.size() == 0) {
            throw ContextStack.criteriaError(this.context, _Exceptions::setClauseNotExists);
        } else if (itemPairList instanceof ArrayList) {
            itemPairList = _CollectionUtils.unmodifiableList(itemPairList);
            this.itemPairList = itemPairList;
        } else {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return itemPairList;

    }


    final SR onAddItemPair(final ItemPair pair) {
        final List<_ItemPair> itemPairList = this.itemPairList;
        if (!(itemPairList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }

        final SQLs.FieldItemPair fieldPair;
        final TableField field;
        if (pair instanceof SQLs.RowItemPair) {
            assert !(this instanceof _DomainUpdate);
            itemPairList.add((SQLs.RowItemPair) pair);
        } else if (!(pair instanceof SQLs.FieldItemPair)) {
            throw ContextStack.criteriaError(this.context, String.format("unknown %s", ItemPair.class.getName()));
        } else if (!((fieldPair = (SQLs.FieldItemPair) pair).field instanceof TableField)) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if ((field = (TableField) fieldPair.field).updateMode() == UpdateMode.IMMUTABLE) {
            throw ContextStack.criteriaError(this.context, _Exceptions::immutableField, field);
        } else if (!field.nullable() && ((ArmyExpression) fieldPair.right).isNullValue()) {
            throw ContextStack.criteriaError(this.context, _Exceptions::nonNullField, field);
        } else if (!(this instanceof _DomainUpdate)) {
            if (field instanceof QualifiedField
                    && !this.tableAlias.equals(((QualifiedField<?>) field).tableAlias())) {
                throw ContextStack.criteriaError(this.context, _Exceptions::unknownColumn, field);
            }
            itemPairList.add(fieldPair);
        } else if (!this.updateTable.isComplexField(field.fieldMeta())) {
            throw ContextStack.criteriaError(this.context, _Exceptions::unknownColumn, field);
        } else if (this.updateTable instanceof ChildTableMeta) {
            this.onAddChildItemPair(fieldPair);
        } else {
            itemPairList.add(fieldPair);
        }
        return (SR) this;
    }


    static abstract class SetWhereClauseClause<F extends TableField, SR, WR, WA>
            extends SetWhereClause<F, SR, WR, WA, Object, Object, Object, Object> {

        SetWhereClauseClause(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
            super(context, updateTable, tableAlias);
        }

        @Override
        public final void prepared() {
            throw ContextStack.castCriteriaApi(this.context);
        }

        @Override
        public final boolean isPrepared() {
            throw ContextStack.castCriteriaApi(this.context);
        }

        @Override
        final Dialect statementDialect() {
            throw ContextStack.castCriteriaApi(this.context);
        }

    }//SetWhereClauseClause


}
