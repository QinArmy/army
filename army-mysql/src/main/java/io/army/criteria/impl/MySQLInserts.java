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

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner.mysql._MySQLInsert;
import io.army.criteria.mysql.MySQLInsert;
import io.army.criteria.mysql.MySQLQuery;
import io.army.dialect.Dialect;
import io.army.dialect.MySQLDialect;
import io.army.meta.*;
import io.army.struct.CodeEnum;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;

import io.army.lang.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is the container of  MySQL insert syntax api implementation class.
 * * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * * @since 0.6.0
 */
abstract class MySQLInserts extends InsertSupports {

    private MySQLInserts() {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * create single-table INSERT statement that is primary statement and support {@link io.army.meta.ChildTableMeta}.
     *     */
    static MySQLInsert._PrimaryOptionSpec singleInsert() {
        return new PrimaryInsertIntoClause();
    }

    /**
     * <p>
     * create single-table INSERT statement that is primary statement for multi-statement and support only {@link SingleTableMeta}.
     *     */
    static <I extends Item> MySQLInsert._PrimarySingleOptionSpec<I> singleInsert(ArmyStmtSpec spec,
                                                                                 Function<? super Insert, I> function) {
        return new PrimarySingleInsertIntoClause<>(spec, function);
    }

    /*-------------------below private method -------------------*/

    private static Insert createSingleInsert(final MySQLComplexValuesClause<?, ?> clause) {
        final InsertMode mode;
        mode = clause.getInsertMode();
        final Statement._DmlInsertClause<Insert> spec;
        switch (mode) {
            case DOMAIN:
                spec = new PrimarySingleDomainInsertStatement(clause);
                break;
            case VALUES:
                spec = new PrimarySimpleValueInsertStatement(clause);
                break;
            case ASSIGNMENT:
                spec = new PrimarySimpleAssignmentInsertStatement(clause);
                break;
            case QUERY:
                spec = new PrimarySimpleQueryInsertStatement(clause);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asInsert();
    }

    private static <P> InsertStatement._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>> createParentInsert(
            final MySQLComplexValuesClause<?, ?> clause) {
        final InsertMode mode;
        mode = clause.getInsertMode();
        final Statement._DmlInsertClause<InsertStatement._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>> spec;
        switch (mode) {
            case DOMAIN:
                spec = new PrimaryParentDomainInsertStatement<>(clause);
                break;
            case VALUES:
                spec = new PrimaryParentValueInsertStatement<>(clause);
                break;
            case ASSIGNMENT:
                spec = new PrimaryParentAssignmentInsertStatement<>(clause);
                break;
            case QUERY:
                spec = new PrimaryParentQueryInsertStatement<>(clause);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
        return spec.asInsert();
    }


    /**
     * <p>
     * This class is the implementation of {@link MySQLInsert._PrimaryOptionSpec}.
     *     */
    private static final class PrimaryInsertIntoClause
            extends InsertSupports.NonQueryInsertOptionsImpl<MySQLInsert._PrimaryNullOptionSpec>
            implements MySQLInsert._PrimaryOptionSpec,
            MySQLInsert._PrimaryIntoClause {

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private PrimaryInsertIntoClause() {
            super(CriteriaContexts.primaryInsertContext(MySQLUtils.DIALECT, null));
            ContextStack.push(this.context);
        }


        @Override
        public MySQLInsert._PrimaryIntoClause insert(Supplier<List<Hint>> supplier, List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, supplier.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<Insert, T> into(SimpleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, MySQLInserts::createSingleInsert);
        }


        @Override
        public <P> MySQLInsert._PartitionSpec<InsertStatement._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>, P> into(ParentTableMeta<P> table) {
            return new MySQLComplexValuesClause<>(this, table, MySQLInserts::createParentInsert);
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<Insert, T> insertInto(SimpleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, MySQLInserts::createSingleInsert);
        }

        @Override
        public <P> MySQLInsert._PartitionSpec<InsertStatement._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>, P> insertInto(ParentTableMeta<P> table) {
            return new MySQLComplexValuesClause<>(this, table, MySQLInserts::createParentInsert);
        }


    }//PrimaryInsertIntoClause


    private static final class ChildInsertIntoClause<P> extends ChildOptionClause
            implements MySQLInsert._ChildInsertIntoSpec<P>,
            MySQLInsert._ChildIntoClause<P> {

        private final Function<MySQLComplexValuesClause<?, ?>, Insert> dmlFunction;

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        /**
         * @see PrimaryParentDomainInsertStatement
         */
        private ChildInsertIntoClause(ValueSyntaxOptions options,
                                      Function<MySQLComplexValuesClause<?, ?>, Insert> dmlFunction) {
            super(options, CriteriaContexts.primaryInsertContext(options.getContext().dialect(), null));
            this.dmlFunction = dmlFunction;
            ContextStack.push(this.context);
        }


        @Override
        public MySQLInsert._ChildIntoClause<P> insert(Supplier<List<Hint>> supplier, List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, supplier.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<Insert, T> insertInto(ComplexTableMeta<P, T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.dmlFunction);
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<Insert, T> into(ComplexTableMeta<P, T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.dmlFunction);
        }


    }//ChildInsertIntoClause


    /**
     * <p>
     * This class is the implementation of {@link MySQLInsert._PrimarySingleOptionSpec}.
     *     */
    private static final class PrimarySingleInsertIntoClause<I extends Item>
            extends InsertSupports.NonQueryInsertOptionsImpl<MySQLInsert._PrimarySingleNullOptionSpec<I>>
            implements MySQLInsert._PrimarySingleOptionSpec<I>,
            MySQLInsert._PrimarySingleIntoClause<I> {

        private final Function<? super Insert, I> function;

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private PrimarySingleInsertIntoClause(ArmyStmtSpec spec, Function<? super Insert, I> function) {
            super(CriteriaContexts.primaryInsertContext(MySQLUtils.DIALECT, spec));
            this.function = function;
            ContextStack.push(this.context);
        }

        @Override
        public MySQLInsert._PrimarySingleIntoClause<I> insert(Supplier<List<Hint>> supplier, List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, supplier.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<I, T> into(SingleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.function.compose(MySQLInserts::createSingleInsert));
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<I, T> insertInto(SingleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.function.compose(MySQLInserts::createSingleInsert));
        }


    }//PrimarySingleInsertIntoClause

    private static final class StaticOnDuplicateKeyClause<I extends Item, T>
            implements MySQLInsert._StaticConflictUpdateClause<I, T>,
            MySQLInsert._StaticConflictUpdateCommaClause<I, T> {

        private final MySQLComplexValuesClause<I, ?> clause;


        private List<_ItemPair> itemPairList;

        private StaticOnDuplicateKeyClause(MySQLComplexValuesClause<I, ?> clause) {
            this.clause = clause;
        }


        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, Expression value) {
            return this.onAddItemPair(SQLs._itemPair(field, null, value));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, Supplier<Expression> supplier) {
            return this.onAddItemPair(SQLs._itemPair(field, null, supplier.get()));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(FieldMeta<T> field, Function<FieldMeta<T>, Expression> function) {
            return this.onAddItemPair(SQLs._itemPair(field, null, function.apply(field)));
        }


        @Override
        public <E, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, E, R> valueOperator, @Nullable E value) {
            return this.onAddAssignmentItemPair(field, valueOperator.apply(field, value));
        }

        @Override
        public <K, V, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, V, R> valueOperator, Function<K, V> function, K key) {
            return this.onAddAssignmentItemPair(field, valueOperator.apply(field, function.apply(key)));
        }

        @Override
        public <E, U, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                BiFunction<FieldMeta<T>, E, U> valueOperator, @Nullable E value) {
            return this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, value)));
        }

        @Override
        public <K, V, U, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                BiFunction<FieldMeta<T>, V, U> valueOperator, Function<K, V> function, K key) {
            return this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, function.apply(key))));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> updateIf(FieldMeta<T> field, Supplier<Expression> supplier) {
            return this.ifComma(field, supplier);
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> updateIf(FieldMeta<T> field,
                                                                           Function<FieldMeta<T>, Expression> function) {
            return this.ifComma(field, function);
        }

        @Override
        public <E, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> updateIf(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, E, R> valueOperator, Supplier<E> getter) {
            return this.ifComma(field, valueOperator, getter);
        }

        @Override
        public <K, V, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> updateIf(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, V, R> valueOperator, Function<K, V> function, K key) {
            return this.ifComma(field, valueOperator, function, key);
        }

        @Override
        public <E, U, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> updateIf(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                BiFunction<FieldMeta<T>, E, U> valueOperator, Supplier<E> getter) {
            return this.ifComma(field, fieldOperator, valueOperator, getter);
        }

        @Override
        public <K, V, U, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> updateIf(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                BiFunction<FieldMeta<T>, V, U> valueOperator, Function<K, V> function, K key) {
            return this.ifComma(field, fieldOperator, valueOperator, function, key);
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field, Expression value) {
            return this.onAddItemPair(SQLs._itemPair(field, null, value));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field,
                                                                        Supplier<Expression> supplier) {
            return this.onAddItemPair(SQLs._itemPair(field, null, supplier.get()));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(FieldMeta<T> field,
                                                                        Function<FieldMeta<T>, Expression> function) {
            return this.onAddItemPair(SQLs._itemPair(field, null, function.apply(field)));
        }

        @Override
        public <E, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, E, R> valueOperator, @Nullable E value) {
            return this.onAddAssignmentItemPair(field, valueOperator.apply(field, value));
        }

        @Override
        public <K, V, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, V, R> valueOperator, Function<K, V> function, K key) {
            return this.onAddAssignmentItemPair(field, valueOperator.apply(field, function.apply(key)));
        }

        @Override
        public <E, U, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                BiFunction<FieldMeta<T>, E, U> valueOperator, @Nullable E value) {
            return this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, value)));
        }

        @Override
        public <K, V, U, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                BiFunction<FieldMeta<T>, V, U> valueOperator, Function<K, V> function, K key) {
            return this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, function.apply(key))));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field, Supplier<Expression> supplier) {
            final Expression value;
            if ((value = supplier.get()) != null) {
                this.onAddItemPair(SQLs._itemPair(field, null, value));
            }
            return this;
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field,
                                                                          Function<FieldMeta<T>, Expression> function) {
            final Expression value;
            if ((value = function.apply(field)) != null) {
                this.onAddItemPair(SQLs._itemPair(field, null, value));
            }
            return this;
        }

        @Override
        public <E, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifComma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, E, R> valueOperator, Supplier<E> getter) {
            final E value;
            if ((value = getter.get()) != null) {
                this.onAddAssignmentItemPair(field, valueOperator.apply(field, value));
            }
            return this;
        }

        @Override
        public <K, V, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifComma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, V, R> valueOperator, Function<K, V> function, K key) {
            final V value;
            if ((value = function.apply(key)) != null) {
                this.onAddAssignmentItemPair(field, valueOperator.apply(field, value));
            }
            return this;
        }

        @Override
        public <E, U, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifComma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                BiFunction<FieldMeta<T>, E, U> valueOperator, Supplier<E> getter) {
            final E value;
            if ((value = getter.get()) != null) {
                this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, value)));
            }
            return this;
        }

        @Override
        public <K, V, U, R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifComma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, U, R> fieldOperator,
                BiFunction<FieldMeta<T>, V, U> valueOperator, Function<K, V> function, K key) {
            final V value;
            if ((value = function.apply(key)) != null) {
                this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, value)));
            }
            return this;
        }

        @Override
        public I asInsert() {
            return this.clause.onDuplicateKeyClauseEnd(this.endUpdateSetClause())
                    .asInsert();
        }

        private List<_ItemPair> endUpdateSetClause() {
            List<_ItemPair> pairList = this.itemPairList;
            if (pairList == null) {
                pairList = Collections.emptyList();
                this.itemPairList = pairList;
            } else if (pairList instanceof ArrayList) {
                pairList = _Collections.unmodifiableList(pairList);
                this.itemPairList = pairList;
            } else {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return pairList;
        }

        private MySQLInsert._StaticConflictUpdateCommaClause<I, T> onAddItemPair(final ItemPair pair) {
            if (!(pair instanceof SQLs.FieldItemPair)) {
                throw CriteriaUtils.illegalItemPair(this.clause.context, pair);
            }
            List<_ItemPair> pairList = this.itemPairList;
            if (pairList == null) {
                pairList = _Collections.arrayList();
                this.itemPairList = pairList;
            } else if (!(pairList instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            pairList.add((SQLs.FieldItemPair) pair);
            return this;
        }

        private MySQLInsert._StaticConflictUpdateCommaClause<I, T> onAddAssignmentItemPair(final FieldMeta<T> field, final @Nullable AssignmentItem item) {
            final ItemPair pair;
            if (item == null) {
                throw ContextStack.nullPointer(this.clause.context);
            } else if (item instanceof Expression) {
                pair = SQLs._itemPair(field, null, (Expression) item);
            } else if (item instanceof ItemPair) {
                pair = (ItemPair) item;
            } else {
                throw CriteriaUtils.illegalAssignmentItem(this.clause.context, item);
            }
            return this.onAddItemPair(pair);
        }


    }//StaticOnDuplicateKeyClause


    private static final class MySQLStaticValuesClause<I extends Item, T>
            extends ValuesParensClauseImpl<T, MySQLInsert._ValuesStaticParensCommaSpec<I, T>>
            implements MySQLInsert._ValuesStaticParensCommaSpec<I, T>,
            MySQLInsert._MySQLValuesStaticParensClause<I, T> {

        private final MySQLComplexValuesClause<I, T> valuesClause;

        private MySQLStaticValuesClause(MySQLComplexValuesClause<I, T> valuesClause) {
            super(valuesClause.context, valuesClause.migration, valuesClause::validateField);
            this.valuesClause = valuesClause;
        }

        @Override
        public MySQLInsert._MySQLValuesStaticParensClause<I, T> comma() {
            return this;
        }

        @Override
        public I asInsert() {
            return this.valuesClause.staticValuesClauseEnd(this.endValuesClause())
                    .asInsert();
        }

        @Override
        public MySQLInsert._StaticConflictUpdateClause<I, T> onDuplicateKey() {
            return this.valuesClause.staticValuesClauseEnd(this.endValuesClause())
                    .onDuplicateKey();
        }

        @Override
        public Statement._DmlInsertClause<I> onDuplicateKeyUpdate(Consumer<UpdateStatement._ItemPairs<FieldMeta<T>>> consumer) {
            return this.valuesClause.staticValuesClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Statement._DmlInsertClause<I> ifOnDuplicateKeyUpdate(Consumer<UpdateStatement._ItemPairs<FieldMeta<T>>> consumer) {
            return this.valuesClause.staticValuesClauseEnd(this.endValuesClause())
                    .ifOnDuplicateKeyUpdate(consumer);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateSpec<I, T> as(String rowAlias) {
            return this.valuesClause.staticValuesClauseEnd(this.endValuesClause())
                    .as(rowAlias);
        }


    }//MySQLStaticValuesClause


    private static final class MySQLComplexValuesClause<I extends Item, T> extends ComplexInsertValuesAssignmentClause<
            T,
            MySQLInsert._ComplexColumnDefaultSpec<I, T>,
            MySQLInsert._ValuesColumnDefaultSpec<I, T>,
            MySQLInsert._OnAsRowAliasSpec<I, T>,
            MySQLInsert._StaticAssignmentSpec<I, T>>
            implements MySQLInsert._PartitionSpec<I, T>,
            MySQLInsert._ComplexColumnDefaultSpec<I, T>,
            MySQLInsert._StaticAssignmentSpec<I, T>,
            MySQLInsert._OnAsRowAliasSpec<I, T> {

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction;

        private List<String> partitionList;

        private String rowAlias;

        private List<_ItemPair> conflictPairList;

        private MySQLComplexValuesClause(PrimaryInsertIntoClause options, SingleTableMeta<T> table,
                                         Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table, true);
            this.hintList = _Collections.safeList(options.hintList);
            this.modifierList = _Collections.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }

        private MySQLComplexValuesClause(ChildInsertIntoClause<?> options, ChildTableMeta<T> table,
                                         Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table, true);
            this.hintList = _Collections.safeList(options.hintList);
            this.modifierList = _Collections.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }

        private MySQLComplexValuesClause(PrimarySingleInsertIntoClause<?> options, SingleTableMeta<T> table,
                                         Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table, true);
            this.hintList = _Collections.safeList(options.hintList);
            this.modifierList = _Collections.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }


        @Override
        public MySQLInsert._ColumnListSpec<I, T> partition(String first, String... rest) {
            this.partitionList = ArrayUtils.unmodifiableListOf(first, rest);
            return this;
        }

        @Override
        public MySQLInsert._ColumnListSpec<I, T> partition(Consumer<Consumer<String>> consumer) {
            this.partitionList = CriteriaUtils.stringList(this.context, true, consumer);
            return this;
        }

        @Override
        public MySQLInsert._ColumnListSpec<I, T> ifPartition(Consumer<Consumer<String>> consumer) {
            this.partitionList = CriteriaUtils.stringList(this.context, false, consumer);
            return this;
        }

        @Override
        public MySQLInsert._MySQLValuesStaticParensClause<I, T> values() {
            return new MySQLStaticValuesClause<>(this);
        }

        @Override
        public MySQLQuery.WithSpec<MySQLInsert._OnDuplicateKeyUpdateSpec<I, T>> space() {
            return MySQLQueries.subQuery(this.context, this::spaceQueryEnd);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateSpec<I, T> space(Supplier<SubQuery> supplier) {
            return this.spaceQueryEnd(supplier.get());
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateSpec<I, T> space(Function<MySQLQuery.WithSpec<MySQLInsert._OnDuplicateKeyUpdateSpec<I, T>>, MySQLInsert._OnDuplicateKeyUpdateSpec<I, T>> function) {
            return function.apply(MySQLQueries.subQuery(this.context, this::spaceQueryEnd));
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateSpec<I, T> as(final String rowAlias) {
            this.context.insertRowAlias(this.insertTable, rowAlias);
            this.rowAlias = rowAlias;
            return this;
        }

        @Override
        public MySQLInsert._StaticConflictUpdateClause<I, T> onDuplicateKey() {
            return new StaticOnDuplicateKeyClause<>(this);
        }

        @Override
        public Statement._DmlInsertClause<I> onDuplicateKeyUpdate(Consumer<UpdateStatement._ItemPairs<FieldMeta<T>>> consumer) {
            if (this.conflictPairList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final List<_ItemPair> list = _Collections.arrayList();
            consumer.accept(CriteriaSupports.simpleFieldItemPairs(this.context, this.insertTable, list::add));
            if (list.size() == 0) {
                throw CriteriaUtils.conflictClauseIsEmpty(this.context);
            }
            this.conflictPairList = _Collections.unmodifiableList(list);
            return this;
        }

        @Override
        public Statement._DmlInsertClause<I> ifOnDuplicateKeyUpdate(Consumer<UpdateStatement._ItemPairs<FieldMeta<T>>> consumer) {
            if (this.conflictPairList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final List<_ItemPair> list = _Collections.arrayList();
            consumer.accept(CriteriaSupports.simpleFieldItemPairs(this.context, this.insertTable, list::add));

            if (list.size() > 0) {
                this.conflictPairList = _Collections.unmodifiableList(list);
            }
            return this;
        }

        @Override
        public I asInsert() {
            this.endStaticAssignmentClauseIfNeed();
            if (this.getInsertMode() == InsertMode.QUERY && this.modifierList.contains(MySQLs.DELAYED)) {
                String m = String.format("MySQL query insert don't support modifier[%s].", MySQLs.DELAYED);
                throw ContextStack.criteriaError(this.context, m);
            }
            return this.dmlFunction.apply(this);
        }

        @Override
        public String tableAlias() {
            //null,MySQL don't support table alias
            return null;
        }

        private Statement._DmlInsertClause<I> onDuplicateKeyClauseEnd(final List<_ItemPair> itemPairList) {
            if (this.conflictPairList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.conflictPairList = itemPairList;
            return this;
        }


    }//MySQLComplexValuesClause


    static abstract class MySQLValueSyntaxStatement<I extends Statement>
            extends ValueSyntaxInsertStatement<I>
            implements MySQLInsert, _MySQLInsert, Insert {

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;

        private final String rowAlias;

        private final List<_ItemPair> conflictPairList;

        private MySQLValueSyntaxStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _Collections.safeList(clause.partitionList);
            this.rowAlias = clause.rowAlias;

            this.conflictPairList = _Collections.safeList(clause.conflictPairList);
        }


        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLs.Modifier> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final String rowAlias() {
            return this.rowAlias;
        }

        @Override
        public final List<_ItemPair> updateSetClauseList() {
            return this.conflictPairList;
        }

        @Override
        public final boolean hasConflictAction() {
            return this.conflictPairList.size() > 0;
        }

        @Override
        public final boolean isIgnorableConflict() {
            //false ,MySQL ON DUPLICATE KEY don't support
            return false;
        }

        @Override
        public final boolean isDoNothing() {
            //false,MySQL don't support
            return false;
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//MySQLValueSyntaxStatement


    static abstract class DomainInsertStatement<I extends Statement> extends MySQLValueSyntaxStatement<I>
            implements _MySQLInsert._MySQLDomainInsert {

        private DomainInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);

        }


    }//DomainInsertStatement

    private static final class PrimarySingleDomainInsertStatement extends DomainInsertStatement<Insert> {

        private final List<?> domainList;

        /**
         * @see PrimaryInsertIntoClause#createSingleInsert(MySQLComplexValuesClause)
         */
        private PrimarySingleDomainInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SingleTableMeta;
            this.domainList = clause.domainListForSingle();
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }


    }//PrimarySingleDomainInsertStatement


    private static final class PrimaryChildDomainInsertStatement extends DomainInsertStatement<Insert>
            implements _MySQLInsert._MySQLChildDomainInsert {

        private final PrimaryParentDomainInsertStatement<?> parentStatement;

        private PrimaryChildDomainInsertStatement(PrimaryParentDomainInsertStatement<?> parentStatement,
                                                  MySQLComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public List<?> domainList() {
            return this.parentStatement.domainList;
        }

        @Override
        public _MySQLDomainInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildDomainInsertStatement


    private static final class PrimaryParentDomainInsertStatement<P>
            extends DomainInsertStatement<InsertStatement._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>>
            implements InsertStatement._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>> {

        private final List<?> originalDomainList;

        private final List<?> domainList;

        /**
         * @see PrimaryInsertIntoClause#createParentInsert(MySQLComplexValuesClause)
         */
        private PrimaryParentDomainInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
            this.originalDomainList = clause.originalDomainList();
            this.domainList = _Collections.unmodifiableList(this.originalDomainList);
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _ChildInsertIntoSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private Insert childInsertEnd(final MySQLComplexValuesClause<?, ?> childClause) {
            childClause.domainListForChild(this.originalDomainList);
            return new PrimaryChildDomainInsertStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentDomainInsertStatement


    static abstract class ValueInsertStatement<I extends Statement> extends MySQLValueSyntaxStatement<I>
            implements _MySQLInsert._MySQLValueInsert {

        final List<Map<FieldMeta<?>, _Expression>> valuePairList;

        private ValueInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            this.valuePairList = clause.rowPairList();
        }

        @Override
        public final List<Map<FieldMeta<?>, _Expression>> rowPairList() {
            return this.valuePairList;
        }


    }//ValuesStatement


    private static final class PrimarySimpleValueInsertStatement extends ValueInsertStatement<Insert> {

        /**
         * @see PrimaryInsertIntoClause#createSingleInsert(MySQLComplexValuesClause)
         */
        private PrimarySimpleValueInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SingleTableMeta;
        }

    }//PrimarySimpleValueStatement


    private static final class PrimaryChildValueStatement extends ValueInsertStatement<Insert>
            implements _MySQLInsert._MySQLChildValueInsert {

        private final PrimaryParentValueInsertStatement<?> parentStatement;

        private PrimaryChildValueStatement(PrimaryParentValueInsertStatement<?> parentStatement
                , MySQLComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _MySQLValueInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimarySimpleValueStatement


    private static final class PrimaryParentValueInsertStatement<P>
            extends ValueInsertStatement<InsertStatement._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>>
            implements InsertStatement._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>> {

        /**
         * @see PrimaryInsertIntoClause#createParentInsert(MySQLComplexValuesClause)
         */
        private PrimaryParentValueInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public _ChildInsertIntoSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private Insert childInsertEnd(final MySQLComplexValuesClause<?, ?> childClause) {
            if (childClause.rowPairList().size() != this.valuePairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            return new PrimaryChildValueStatement(this, childClause)
                    .asInsert();
        }


    }//PrimarySimpleValueStatement


    static abstract class PrimaryAssignmentStatement<I extends Statement>
            extends InsertSupports.AssignmentInsertStatement<I>
            implements MySQLInsert, _MySQLInsert._MySQLAssignmentInsert, Insert {

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;

        private final String rowAlias;

        private final List<_ItemPair> conflictPairList;

        private PrimaryAssignmentStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _Collections.safeList(clause.partitionList);
            this.rowAlias = clause.rowAlias;

            this.conflictPairList = _Collections.safeList(clause.conflictPairList);
        }


        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLs.Modifier> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final String rowAlias() {
            return this.rowAlias;
        }

        @Override
        public final List<_ItemPair> updateSetClauseList() {
            return this.conflictPairList;
        }

        @Override
        public final boolean hasConflictAction() {
            return this.conflictPairList.size() > 0;
        }

        @Override
        public final boolean isIgnorableConflict() {
            //false ,MySQL don't support do nothing clause
            return false;
        }

        @Override
        public final boolean isDoNothing() {
            //false,MySQL don't support
            return false;
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//PrimaryAssignmentStatement


    private static final class PrimarySimpleAssignmentInsertStatement extends PrimaryAssignmentStatement<Insert> {

        /**
         * @see PrimaryInsertIntoClause#createSingleInsert(MySQLComplexValuesClause)
         */
        private PrimarySimpleAssignmentInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SingleTableMeta;
        }

    }//PrimarySimpleAssignmentStatement

    private static final class PrimaryChildAssignmentStatement extends PrimaryAssignmentStatement<Insert>
            implements _MySQLInsert._MySQLChildAssignmentInsert {

        private final PrimaryParentAssignmentInsertStatement<?> parentStatement;

        private PrimaryChildAssignmentStatement(PrimaryParentAssignmentInsertStatement<?> parentStatement
                , MySQLComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _MySQLAssignmentInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildAssignmentStatement

    private static final class PrimaryParentAssignmentInsertStatement<P>
            extends PrimaryAssignmentStatement<InsertStatement._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>>
            implements InsertStatement._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>> {

        /**
         * @see PrimaryInsertIntoClause#createParentInsert(MySQLComplexValuesClause)
         */
        private PrimaryParentAssignmentInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public MySQLInsert._ChildInsertIntoSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private Insert childInsertEnd(MySQLComplexValuesClause<?, ?> childClause) {
            return new PrimaryChildAssignmentStatement(this, childClause)
                    .asInsert();
        }


    }//PrimaryParentAssignmentStatement


    static abstract class PrimaryQueryInsertStatement<I extends Statement>
            extends InsertSupports.QuerySyntaxInsertStatement<I>
            implements MySQLInsert, _MySQLInsert._MySQLQueryInsert, Insert {


        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;

        private final String rowAlias;

        private final List<_ItemPair> conflictPairList;

        private PrimaryQueryInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = _Collections.safeList(clause.partitionList);
            this.rowAlias = clause.rowAlias;

            this.conflictPairList = _Collections.safeList(clause.conflictPairList);
        }


        @Override
        public final List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public final List<MySQLs.Modifier> modifierList() {
            return this.modifierList;
        }

        @Override
        public final List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public final String rowAlias() {
            return this.rowAlias;
        }

        @Override
        public final List<_ItemPair> updateSetClauseList() {
            return this.conflictPairList;
        }

        @Override
        public final boolean hasConflictAction() {
            return this.conflictPairList.size() > 0;
        }

        @Override
        public final boolean isIgnorableConflict() {
            //false ,MySQL don't support do nothing clause
            return false;
        }

        @Override
        public final boolean isDoNothing() {
            //false,MySQL don't support
            return false;
        }

        @Override
        final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//PrimaryQueryInsertStatement

    private static final class PrimarySimpleQueryInsertStatement extends PrimaryQueryInsertStatement<Insert> {

        /**
         * @see PrimaryInsertIntoClause#createSingleInsert(MySQLComplexValuesClause)
         */
        private PrimarySimpleQueryInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SimpleTableMeta;
        }


    }//PrimarySimpleQueryStatement


    private static final class PrimaryChildQueryInsertStatement extends PrimaryQueryInsertStatement<Insert>
            implements _MySQLInsert._MySQLChildQueryInsert {

        private final PrimaryParentQueryInsertStatement<?> parentStatement;

        private PrimaryChildQueryInsertStatement(PrimaryParentQueryInsertStatement<?> parentStatement
                , MySQLComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _MySQLParentQueryInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildQueryStatement


    private static final class PrimaryParentQueryInsertStatement<P>
            extends PrimaryQueryInsertStatement<InsertStatement._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>>
            implements InsertStatement._ParentInsert<MySQLInsert._ChildInsertIntoSpec<P>>,
            _MySQLInsert._MySQLParentQueryInsert, ParentQueryInsert {

        private CodeEnum discriminatorValue;

        /**
         * @see PrimaryInsertIntoClause#createParentInsert(MySQLComplexValuesClause)
         */
        private PrimaryParentQueryInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
        }

        @Override
        public void onValidateEnd(final CodeEnum discriminatorValue) {
            assert this.discriminatorValue == null;
            this.discriminatorValue = discriminatorValue;
        }

        @Override
        public CodeEnum discriminatorEnum() {
            final CodeEnum value = this.discriminatorValue;
            assert value != null;
            return value;
        }

        @Override
        public _ChildInsertIntoSpec<P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private Insert childInsertEnd(final MySQLComplexValuesClause<?, ?> childClause) {
            return new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
        }


    }//PrimarySimpleQueryStatement


}
