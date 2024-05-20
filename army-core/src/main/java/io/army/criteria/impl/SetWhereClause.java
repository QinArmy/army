/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._DomainUpdate;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.mysql.inner._Statement;
import io.army.meta.ChildTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see JoinableUpdate
 */

@SuppressWarnings("unchecked")
public abstract class SetWhereClause<F extends TableField, SR, WR, WA, OR, OD, LR, LO, LF>
        extends WhereClause<WR, WA, OR, OD, LR, LO, LF>
        implements UpdateStatement._StaticBatchSetClause<F, SR>,
        UpdateStatement._StaticBatchRowSetClause<F, SR>,
        _Statement._ItemPairList,
        _Statement._TableMetaSpec {

    private List<_ItemPair> itemPairList = _Collections.arrayList();

    protected final TableMeta<?> updateTable;

    protected final String tableAlias;

    /**
     * @param tableAlias for {@link SingleUpdateStatement} non-null and  non-empty,for other non-null
     */
    protected SetWhereClause(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
        super(context);
        ContextStack.assertNonNull(updateTable);
        ContextStack.assertNonNull(tableAlias);
        this.updateTable = updateTable;
        this.tableAlias = tableAlias;

    }


    @Override
    public final SR set(F field, Expression value) {
        return this.onAddItemPair(Armies._itemPair(field, null, value));
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
    public final SR setSpace(F field, BiFunction<F, String, Expression> valueOperator) {
        return this.onAddItemPair(Armies._itemPair(field, null, valueOperator.apply(field, field.fieldName())));
    }

    @Override
    public final <R extends AssignmentItem> SR setSpace(F field, BiFunction<F, Expression, R> fieldOperator
            , BiFunction<F, String, Expression> valueOperator) {
        return this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, field.fieldName())));
    }

    @Override
    public final SR setRow(F field1, F field2, Supplier<SubQuery> supplier) {
        final List<F> fieldList;
        fieldList = Arrays.asList(field1, field2);
        return this.onAddItemPair(Armies._itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR setRow(F field1, F field2, F field3, Supplier<SubQuery> supplier) {
        final List<F> fieldList;
        fieldList = Arrays.asList(field1, field2, field3);
        return this.onAddItemPair(Armies._itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR setRow(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier) {
        final List<F> fieldList;
        fieldList = Arrays.asList(field1, field2, field3, field4);
        return this.onAddItemPair(Armies._itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR setRow(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier) {
        final List<F> fieldList = _Collections.arrayList();
        consumer.accept(fieldList::add);
        return this.onAddItemPair(Armies._itemPair(fieldList, supplier.get()));
    }

    @Override
    public final SR ifSetRow(F field1, F field2, Supplier<SubQuery> supplier) {
        final SubQuery query;
        if ((query = supplier.get()) != null) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2);
            this.onAddItemPair(Armies._itemPair(fieldList, query));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetRow(F field1, F field2, F field3, Supplier<SubQuery> supplier) {
        final SubQuery query;
        if ((query = supplier.get()) != null) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2, field3);
            this.onAddItemPair(Armies._itemPair(fieldList, query));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetRow(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier) {
        final SubQuery query;
        if ((query = supplier.get()) != null) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2, field3, field4);
            this.onAddItemPair(Armies._itemPair(fieldList, query));
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetRow(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier) {
        final List<F> fieldList = _Collections.arrayList();
        consumer.accept(fieldList::add);
        final SubQuery query;
        if (fieldList.size() > 0 && (query = supplier.get()) != null) {
            this.onAddItemPair(Armies._itemPair(fieldList, query));
        }
        return (SR) this;
    }

    @Override
    public final TableMeta<?> table() {
        return this.updateTable;
    }

    @Override
    public final List<_ItemPair> itemPairList() {
        final List<_ItemPair> itemPairList = this.itemPairList;
        if (itemPairList == null || itemPairList instanceof ArrayList) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return itemPairList;
    }

    protected void onAddChildItemPair(Armies.ArmyItemPair pair) {
        throw new UnsupportedOperationException();
    }

    protected boolean isNoChildItemPair() {
        throw new UnsupportedOperationException();
    }

    public final List<_ItemPair> endUpdateSetClause() {
        List<_ItemPair> itemPairList = this.itemPairList;
        if (itemPairList == null || itemPairList.size() == 0) {
            if (!(this instanceof _DomainUpdate) || this.isNoChildItemPair()) {
                throw ContextStack.criteriaError(this.context, _Exceptions::setClauseNotExists);
            }
            itemPairList = Collections.emptyList();
            this.itemPairList = itemPairList;
        } else if (itemPairList instanceof ArrayList) {
            itemPairList = _Collections.unmodifiableList(itemPairList);
            this.itemPairList = itemPairList;
        } else {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return itemPairList;

    }


    protected final SR onAddItemPair(final ItemPair pair) {
        final List<_ItemPair> itemPairList = this.itemPairList;
        if (!(itemPairList instanceof ArrayList)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }

        final Armies.FieldItemPair fieldPair;
        final TableField field;
        if (pair instanceof Armies.RowItemPair) {
            assert !(this instanceof _DomainUpdate);
            itemPairList.add((Armies.RowItemPair) pair);
        } else if (!(pair instanceof Armies.FieldItemPair)) {
            throw ContextStack.criteriaError(this.context, String.format("unknown %s", ItemPair.class.getName()));
        } else if (!((fieldPair = (Armies.FieldItemPair) pair).field instanceof TableField)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
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
        } else if (field.tableMeta() instanceof ChildTableMeta) {
            this.onAddChildItemPair(fieldPair);
        } else {
            itemPairList.add(fieldPair);
        }
        return (SR) this;
    }

    private SR onAddAssignmentItemPair(final F field, final @Nullable AssignmentItem item) {
        final ItemPair pair;
        if (item == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (item instanceof Expression) {
            pair = Armies._itemPair(field, null, (Expression) item);
        } else if (item instanceof ItemPair) {
            pair = (ItemPair) item;
        } else {
            throw CriteriaUtils.illegalAssignmentItem(this.context, item);
        }
        return this.onAddItemPair(pair);
    }

    public static abstract class SetWhereClauseClause<F extends TableField, SR, WR, WA>
            extends SetWhereClause<F, SR, WR, WA, Object, Object, Object, Object, Object> {

        protected SetWhereClauseClause(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
            super(context, updateTable, tableAlias);
        }


    }//SetWhereClauseClause


}
