package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner.mysql._MySQLInsert;
import io.army.criteria.mysql.MySQLInsert;
import io.army.criteria.mysql.MySQLQuery;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util._ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

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
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
abstract class MySQLInserts extends InsertSupport {

    private MySQLInserts() {
        throw new UnsupportedOperationException();
    }

    static <I extends Item> MySQLInsert._PrimaryOptionSpec<I> primaryInsert(Function<Insert, I> function) {
        return new PrimaryInsertIntoClause<>(function);
    }


    private static final class PrimaryInsertIntoClause<I extends Item>
            extends InsertSupport.NonQueryInsertOptionsImpl<
            MySQLInsert._PrimaryNullOptionSpec<I>,
            MySQLInsert._PrimaryPreferLiteralSpec<I>,
            MySQLInsert._PrimaryInsertIntoSpec<I>>
            implements MySQLInsert._PrimaryOptionSpec<I>,
            MySQLInsert._PrimaryIntoClause<I> {


        private final Function<Insert, I> function;

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private PrimaryInsertIntoClause(Function<Insert, I> function) {
            super(CriteriaContexts.primaryInsertContext(null));
            ContextStack.push(this.context);
            this.function = function;
        }


        @Override
        public MySQLInsert._PrimaryIntoClause<I> insert(Supplier<List<Hint>> supplier, List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, supplier.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<I, T> into(SingleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, this::createSingleInsert);
        }


        @Override
        public <P> MySQLInsert._PartitionSpec<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<I, P>>, P> into(ParentTableMeta<P> table) {
            return new MySQLComplexValuesClause<>(this, table, this::createParentInsert);
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<I, T> insertInto(SimpleTableMeta<T> table) {
            return new MySQLComplexValuesClause<>(this, table, this::createSingleInsert);
        }

        @Override
        public <P> MySQLInsert._PartitionSpec<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<I, P>>, P> insertInto(ParentTableMeta<P> table) {
            return new MySQLComplexValuesClause<>(this, table, this::createParentInsert);
        }


        private I createSingleInsert(final MySQLComplexValuesClause<?, ?> clause) {
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
            return this.function.apply(spec.asInsert());
        }

        private <P> Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<I, P>> createParentInsert(
                final MySQLComplexValuesClause<?, ?> clause) {
            final InsertMode mode;
            mode = clause.getInsertMode();
            final Statement._DmlInsertClause<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<I, P>>> spec;
            switch (mode) {
                case DOMAIN:
                    spec = new PrimaryParentDomainInsertStatement<>(clause, this.function);
                    break;
                case VALUES:
                    spec = new PrimaryParentValueInsertStatement<>(clause, this.function);
                    break;
                case ASSIGNMENT:
                    spec = new PrimaryParentAssignmentInsertStatement<>(clause, this.function);
                    break;
                case QUERY:
                    spec = new PrimaryParentQueryInsertStatement<>(clause, this.function);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(mode);
            }
            return spec.asInsert();
        }


    }//PrimaryInsertIntoClause


    private static final class ChildInsertIntoClause<I extends Item, P> extends ChildOptionClause
            implements MySQLInsert._ChildInsertIntoSpec<I, P>,
            MySQLInsert._ChildIntoClause<I, P> {

        private final Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction;

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        /**
         * @see PrimaryParentDomainInsertStatement
         */
        private ChildInsertIntoClause(ValueSyntaxOptions options,
                                      Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, CriteriaContexts.primaryInsertContext(null));
            this.dmlFunction = dmlFunction;
            ContextStack.push(this.context);
        }


        @Override
        public MySQLInsert._ChildIntoClause<I, P> insert(Supplier<List<Hint>> supplier, List<MySQLs.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, supplier.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::insertModifier);
            return this;
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<I, T> insertInto(ComplexTableMeta<P, T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.dmlFunction);
        }

        @Override
        public <T> MySQLInsert._PartitionSpec<I, T> into(ComplexTableMeta<P, T> table) {
            return new MySQLComplexValuesClause<>(this, table, this.dmlFunction);
        }


    }//ChildInsertIntoClause


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
        public <R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, R> valueOperator, Expression expression) {
            return this.comma(field, valueOperator, expression);
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator, @Nullable Object value) {
            return this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
        }

        @Override
        public <E> MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier) {
            return this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, supplier.get())));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator, Function<String, ?> function,
                String keyName) {
            return this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, function.apply(keyName))));
        }


        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                BiFunction<FieldMeta<T>, Expression, Expression> valueOperator, Expression expression) {
            return this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, expression)));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                BiFunction<FieldMeta<T>, Object, Expression> valueOperator, Object value) {
            return this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
        }

        @Override
        public <E> MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier) {
            return this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, supplier.get())));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> update(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                BiFunction<FieldMeta<T>, Object, Expression> valueOperator, Function<String, ?> function,
                String keyName) {
            return this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, function.apply(keyName))));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifUpdate(FieldMeta<T> field, Supplier<Expression> supplier) {
            return this.ifComma(field, supplier);
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifUpdate(FieldMeta<T> field,
                                                                           Function<FieldMeta<T>, Expression> function) {
            return this.ifComma(field, function);
        }

        @Override
        public <E> MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifUpdate(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> getter) {
            return this.ifComma(field, valueOperator, getter);
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifUpdate(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                Function<String, ?> function, String keyName) {
            return this.ifComma(field, valueOperator, function, keyName);
        }


        @Override
        public <E> MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifUpdate(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> getter) {
            return this.ifComma(field, fieldOperator, valueOperator, getter);
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifUpdate(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                BiFunction<FieldMeta<T>, Object, Expression> valueOperator, Function<String, ?> function,
                String keyName) {
            return this.ifComma(field, fieldOperator, valueOperator, function, keyName);
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
        public <R extends AssignmentItem> MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, R> valueOperator, Expression expression) {
            final R item;
            item = valueOperator.apply(field, expression);

            if (item instanceof Expression) {
                this.onAddItemPair(SQLs._itemPair(field, null, (Expression) item));
            } else if (item instanceof ItemPair) {
                this.onAddItemPair((ItemPair) item);
            } else {
                throw CriteriaUtils.illegalAssignmentItem(this.clause.context, item);
            }
            return this;
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator, @Nullable Object value) {
            return this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
        }

        @Override
        public <E> MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier) {
            return this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, supplier.get())));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                Function<String, ?> function, String keyName) {
            return this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, function.apply(keyName))));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                BiFunction<FieldMeta<T>, Expression, Expression> valueOperator, Expression expression) {
            return this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, expression)));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                BiFunction<FieldMeta<T>, Object, Expression> valueOperator, Object value) {
            return this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
        }

        @Override
        public <E> MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> supplier) {
            return this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, supplier.get())));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> comma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                BiFunction<FieldMeta<T>, Object, Expression> valueOperator, Function<String, ?> function,
                String keyName) {
            return this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, function.apply(keyName))));
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field,
                                                                          Supplier<Expression> supplier) {
            final Expression expression;
            if ((expression = supplier.get()) != null) {
                this.onAddItemPair(SQLs._itemPair(field, null, expression));
            }
            return this;
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifComma(FieldMeta<T> field,
                                                                          Function<FieldMeta<T>, Expression> function) {
            final Expression expression;
            if ((expression = function.apply(field)) != null) {
                this.onAddItemPair(SQLs._itemPair(field, null, expression));
            }
            return this;
        }


        @Override
        public <E> MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifComma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> getter) {
            final E value;
            if ((value = getter.get()) != null) {
                this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
            }
            return this;
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifComma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Object, Expression> valueOperator,
                Function<String, ?> function, String keyName) {
            final Object value;
            if ((value = function.apply(keyName)) != null) {
                this.onAddItemPair(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
            }
            return this;
        }

        @Override
        public <E> MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifComma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                BiFunction<FieldMeta<T>, E, Expression> valueOperator, Supplier<E> getter) {
            final E value;
            if ((value = getter.get()) != null) {
                this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
            }
            return this;
        }

        @Override
        public MySQLInsert._StaticConflictUpdateCommaClause<I, T> ifComma(
                FieldMeta<T> field, BiFunction<FieldMeta<T>, Expression, ItemPair> fieldOperator,
                BiFunction<FieldMeta<T>, Object, Expression> valueOperator, Function<String, ?> function,
                String keyName) {
            final Object value;
            if ((value = function.apply(keyName)) != null) {
                this.onAddItemPair(fieldOperator.apply(field, valueOperator.apply(field, value)));
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
                pairList = _CollectionUtils.unmodifiableList(pairList);
                this.itemPairList = pairList;
            } else {
                throw ContextStack.castCriteriaApi(this.clause.context);
            }
            return pairList;
        }

        private MySQLInsert._StaticConflictUpdateCommaClause<I, T> onAddItemPair(final ItemPair pair) {
            if (!(pair instanceof SQLs.FieldItemPair)) {
                throw CriteriaUtils.illegalItemPair(this.clause.context, pair);
            }
            List<_ItemPair> pairList = this.itemPairList;
            if (pairList == null) {
                pairList = new ArrayList<>();
                this.itemPairList = pairList;
            } else if (!(pairList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.clause.context);
            }
            pairList.add((SQLs.FieldItemPair) pair);
            return this;
        }


    }//StaticOnDuplicateKeyClause


    private static final class MySQLStaticValuesClause<I extends Item, T>
            extends InsertSupport.StaticColumnValuePairClause<T, MySQLInsert._StaticValuesLeftParenSpec<I, T>>
            implements MySQLInsert._StaticValuesLeftParenSpec<I, T> {

        private final MySQLComplexValuesClause<I, T> valuesClause;

        private MySQLStaticValuesClause(MySQLComplexValuesClause<I, T> valuesClause) {
            super(valuesClause.context, valuesClause::validateField);
            this.valuesClause = valuesClause;
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
        public Statement._DmlInsertClause<I> onDuplicateKeyUpdate(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
            return this.valuesClause.staticValuesClauseEnd(this.endValuesClause())
                    .onDuplicateKeyUpdate(consumer);
        }

        @Override
        public Statement._DmlInsertClause<I> ifOnDuplicateKeyUpdate(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
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
            implements MySQLInsert._PartitionSpec<I, T>
            , MySQLInsert._ComplexColumnDefaultSpec<I, T>
            , MySQLInsert._StaticAssignmentSpec<I, T>
            , MySQLInsert._OnAsRowAliasSpec<I, T> {

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction;

        private List<String> partitionList;

        private String rowAlias;

        private List<_ItemPair> conflictPairList;

        private MySQLComplexValuesClause(PrimaryInsertIntoClause<?> options, SingleTableMeta<T> table
                , Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.hintList = _CollectionUtils.safeList(options.hintList);
            this.modifierList = _CollectionUtils.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }

        private MySQLComplexValuesClause(ChildInsertIntoClause<?, ?> options, ChildTableMeta<T> table
                , Function<MySQLComplexValuesClause<?, ?>, I> dmlFunction) {
            super(options, table);
            this.hintList = _CollectionUtils.safeList(options.hintList);
            this.modifierList = _CollectionUtils.safeList(options.modifierList);
            this.dmlFunction = dmlFunction;
        }


        @Override
        public MySQLInsert._ColumnListSpec<I, T> partition(String first, String... rest) {
            this.partitionList = _ArrayUtils.unmodifiableListOf(first, rest);
            return this;
        }

        @Override
        public MySQLInsert._ColumnListSpec<I, T> partition(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() == 0) {
                throw MySQLUtils.partitionListIsEmpty(this.context);
            }
            this.partitionList = _CollectionUtils.unmodifiableList(list);
            return this;
        }

        @Override
        public MySQLInsert._ColumnListSpec<I, T> ifPartition(Consumer<Consumer<String>> consumer) {
            final List<String> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() > 0) {
                this.partitionList = _CollectionUtils.unmodifiableList(list);
            } else {
                this.partitionList = null;
            }
            return this;
        }

        @Override
        public MySQLInsert._MySQLStaticValuesLeftParenClause<I, T> values() {
            return new MySQLStaticValuesClause<>(this);
        }

        @Override
        public MySQLQuery._WithSpec<MySQLInsert._OnDuplicateKeyUpdateSpec<I, T>> space() {
            return MySQLQueries.subQuery(null, this.context, this::staticSpaceQueryEnd);
        }

        @Override
        public MySQLInsert._OnDuplicateKeyUpdateSpec<I, T> as(final String rowAlias) {
            this.context.onInsertRowAlias(rowAlias);
            this.rowAlias = rowAlias;
            return this;
        }

        @Override
        public MySQLInsert._StaticConflictUpdateClause<I, T> onDuplicateKey() {
            return new StaticOnDuplicateKeyClause<>(this);
        }

        @Override
        public Statement._DmlInsertClause<I> onDuplicateKeyUpdate(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
            if (this.conflictPairList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final List<_ItemPair> list = new ArrayList<>();
            consumer.accept(CriteriaSupports.simpleFieldItemPairs(this.context, this.insertTable, list::add));
            if (list.size() == 0) {
                throw CriteriaUtils.conflictClauseIsEmpty(this.context);
            }
            this.conflictPairList = _CollectionUtils.unmodifiableList(list);
            return this;
        }

        @Override
        public Statement._DmlInsertClause<I> ifOnDuplicateKeyUpdate(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
            if (this.conflictPairList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final List<_ItemPair> list = new ArrayList<>();
            consumer.accept(CriteriaSupports.simpleFieldItemPairs(this.context, this.insertTable, list::add));

            if (list.size() > 0) {
                this.conflictPairList = _CollectionUtils.unmodifiableList(list);
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

        private Statement._DmlInsertClause<I> onDuplicateKeyClauseEnd(final List<_ItemPair> itemPairList) {
            if (this.conflictPairList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.conflictPairList = itemPairList;
            return this;
        }


    }//MySQLComplexValuesClause


    static abstract class MySQLValueSyntaxStatement<I extends Statement.DmlInsert>
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
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowAlias = clause.rowAlias;

            this.conflictPairList = _CollectionUtils.safeList(clause.conflictPairList);
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
        final Dialect statementDialect() {
            return MySQLDialect.MySQL80;
        }


    }//MySQLValueSyntaxStatement


    static abstract class DomainInsertStatement<I extends Statement.DmlInsert> extends MySQLValueSyntaxStatement<I>
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

        private final PrimaryParentDomainInsertStatement<?, ?> parentStatement;

        private PrimaryChildDomainInsertStatement(PrimaryParentDomainInsertStatement<?, ?> parentStatement
                , MySQLComplexValuesClause<?, ?> childClause) {
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


    private static final class PrimaryParentDomainInsertStatement<I extends Item, P>
            extends DomainInsertStatement<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<I, P>>>
            implements Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<I, P>> {

        private final Function<Insert, I> function;

        private final List<?> originalDomainList;

        private final List<?> domainList;

        /**
         * @see PrimaryInsertIntoClause#createParentInsert(MySQLComplexValuesClause)
         */
        private PrimaryParentDomainInsertStatement(MySQLComplexValuesClause<?, ?> clause, Function<Insert, I> function) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
            this.function = function;
            this.originalDomainList = clause.originalDomainList();
            this.domainList = _CollectionUtils.unmodifiableList(this.originalDomainList);
        }

        @Override
        public List<?> domainList() {
            return this.domainList;
        }

        @Override
        public _ChildInsertIntoSpec<I, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private I childInsertEnd(final MySQLComplexValuesClause<?, ?> childClause) {
            childClause.domainListForChild(this.originalDomainList);
            final Insert insert;
            insert = new PrimaryChildDomainInsertStatement(this, childClause)
                    .asInsert();
            return this.function.apply(insert);
        }


    }//PrimaryParentDomainInsertStatement


    static abstract class ValueInsertStatement<I extends Statement.DmlInsert> extends MySQLValueSyntaxStatement<I>
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


    private static final class PrimarySimpleValueInsertStatement extends ValueInsertStatement<Insert>
            implements Insert {

        /**
         * @see PrimaryInsertIntoClause#createSingleInsert(MySQLComplexValuesClause)
         */
        private PrimarySimpleValueInsertStatement(MySQLComplexValuesClause<?, ?> clause) {
            super(clause);
            assert clause.insertTable instanceof SingleTableMeta;
        }

    }//PrimarySimpleValueStatement


    private static final class PrimaryChildValueStatement extends ValueInsertStatement<Insert>
            implements _MySQLInsert._MySQLChildValueInsert, Insert {

        private final PrimaryParentValueInsertStatement<?, ?> parentStatement;

        private PrimaryChildValueStatement(PrimaryParentValueInsertStatement<?, ?> parentStatement
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


    private static final class PrimaryParentValueInsertStatement<I extends Item, P>
            extends ValueInsertStatement<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<I, P>>>
            implements Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<I, P>> {

        private final Function<Insert, I> function;

        /**
         * @see PrimaryInsertIntoClause#createParentInsert(MySQLComplexValuesClause)
         */
        private PrimaryParentValueInsertStatement(MySQLComplexValuesClause<?, ?> clause, Function<Insert, I> function) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
            this.function = function;
        }

        @Override
        public _ChildInsertIntoSpec<I, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private I childInsertEnd(final MySQLComplexValuesClause<?, ?> childClause) {
            if (childClause.rowPairList().size() != this.valuePairList.size()) {
                throw CriteriaUtils.childParentRowNotMatch(childClause, this);
            }
            final Insert insert;
            insert = new PrimaryChildValueStatement(this, childClause)
                    .asInsert();
            return this.function.apply(insert);
        }


    }//PrimarySimpleValueStatement


    static abstract class PrimaryAssignmentStatement<I extends Statement.DmlInsert>
            extends InsertSupport.AssignmentInsertStatement<I>
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
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowAlias = clause.rowAlias;

            this.conflictPairList = _CollectionUtils.safeList(clause.conflictPairList);
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

        private final PrimaryParentAssignmentInsertStatement<?, ?> parentStatement;

        private PrimaryChildAssignmentStatement(PrimaryParentAssignmentInsertStatement<?, ?> parentStatement
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

    private static final class PrimaryParentAssignmentInsertStatement<I extends Item, P>
            extends PrimaryAssignmentStatement<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<I, P>>>
            implements Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<I, P>> {

        private final Function<Insert, I> function;

        /**
         * @see PrimaryInsertIntoClause#createParentInsert(MySQLComplexValuesClause)
         */
        private PrimaryParentAssignmentInsertStatement(MySQLComplexValuesClause<?, ?> clause,
                                                       Function<Insert, I> function) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
            this.function = function;
        }

        @Override
        public MySQLInsert._ChildInsertIntoSpec<I, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private I childInsertEnd(MySQLComplexValuesClause<?, ?> childClause) {
            final Insert insert;
            insert = new PrimaryChildAssignmentStatement(this, childClause)
                    .asInsert();
            return this.function.apply(insert);
        }


    }//PrimaryParentAssignmentStatement


    static abstract class PrimaryQueryInsertStatement<I extends Statement.DmlInsert>
            extends InsertSupport.QuerySyntaxInsertStatement<I>
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
            this.partitionList = _CollectionUtils.safeList(clause.partitionList);
            this.rowAlias = clause.rowAlias;

            this.conflictPairList = _CollectionUtils.safeList(clause.conflictPairList);
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

        private final PrimaryParentQueryInsertStatement<?, ?> parentStatement;

        private PrimaryChildQueryInsertStatement(PrimaryParentQueryInsertStatement<?, ?> parentStatement
                , MySQLComplexValuesClause<?, ?> childClause) {
            super(childClause);
            assert childClause.insertTable instanceof ChildTableMeta;
            this.parentStatement = parentStatement;
        }

        @Override
        public _MySQLQueryInsert parentStmt() {
            return this.parentStatement;
        }


    }//PrimaryChildQueryStatement


    private static final class PrimaryParentQueryInsertStatement<I extends Item, P>
            extends PrimaryQueryInsertStatement<Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<I, P>>>
            implements Insert._ParentInsert<MySQLInsert._ChildInsertIntoSpec<I, P>> {

        private final Function<Insert, I> function;

        /**
         * @see PrimaryInsertIntoClause#createParentInsert(MySQLComplexValuesClause)
         */
        private PrimaryParentQueryInsertStatement(MySQLComplexValuesClause<?, ?> clause,
                                                  Function<Insert, I> function) {
            super(clause);
            assert clause.insertTable instanceof ParentTableMeta;
            this.function = function;
        }

        @Override
        public _ChildInsertIntoSpec<I, P> child() {
            this.prepared();
            return new ChildInsertIntoClause<>(this, this::childInsertEnd);
        }

        private I childInsertEnd(final MySQLComplexValuesClause<?, ?> childClause) {
            final Insert insert;
            insert = new PrimaryChildQueryInsertStatement(this, childClause)
                    .asInsert();
            return this.function.apply(insert);
        }


    }//PrimarySimpleQueryStatement


}
