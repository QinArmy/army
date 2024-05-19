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
import io.army.criteria.dialect.Returnings;
import io.army.criteria.impl.inner.*;
import io.army.dialect.*;
import io.army.dialect.mysql.MySQLDialect;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;
import io.army.stmt.Stmt;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class CriteriaSupports {

    protected CriteriaSupports() {
        throw new UnsupportedOperationException();
    }

    public static <RR> Statement._LeftParenStringQuadraOptionalSpec<RR> stringQuadra(CriteriaContext context
            , Function<List<String>, RR> function) {
        return new ParenStringConsumerClause<>(context, function);
    }

    public static StringObjectSpaceClause stringObjectSpace(boolean required, Consumer<String> consumer) {
        return new StringObjectSpaceClause(required, consumer);
    }

    public static StringObjectConsumer stringObjectConsumer(boolean required, Consumer<String> consumer) {
        return new StringObjectConsumer(required, consumer);
    }

    public static StaticObjectConsumer staticObjectConsumer(boolean required, Consumer<Object> consumer) {
        return new StaticObjectConsumer(required, consumer);
    }

    public static DynamicObjectConsumer dynamicObjectConsumer(boolean required, Consumer<Object> consumer) {
        return new DynamicObjectConsumer(required, consumer);
    }

    public static Returnings returningBuilder(Consumer<_SelectItem> consumer) {
        return new ReturningBuilderImpl(consumer);
    }

    public static <F extends SqlField> UpdateStatement._ItemPairs<F> itemPairs(Consumer<ItemPair> consumer) {
        return new ItemPairsImpl<>(consumer);
    }

    public static <F extends SqlField> UpdateStatement._BatchItemPairs<F> batchItemPairs(Consumer<ItemPair> consumer) {
        return new BatchItemPairsImpl<>(consumer);
    }

    public static <F extends SqlField> UpdateStatement._RowPairs<F> rowPairs(Consumer<ItemPair> consumer) {
        return new RowItemPairsImpl<>(consumer);
    }

    public static <F extends SqlField> UpdateStatement._BatchRowPairs<F> batchRowPairs(Consumer<ItemPair> consumer) {
        return new BatchRowItemPairsImpl<>(consumer);
    }

    public static <F extends SqlField> UpdateStatement._ItemPairs<F> simpleFieldItemPairs(CriteriaContext context
            , @Nullable TableMeta<?> updateTable, Consumer<_ItemPair> consumer) {
        assert updateTable != null;
        return new SimpleFieldItemPairs<>(context, updateTable, consumer);
    }

    public static ObjectVariadic objectVariadicClause() {
        return new ObjectVariadic();
    }


    /**
     * This interface is base interface of All implementation of {@link CteBuilderSpec}
     */
    public interface CteBuilder extends CteBuilderSpec {

        void endLastCte();
    }

    public static abstract class WithClause<B extends CteBuilderSpec, WE extends Item> extends StatementMockSupport
            implements DialectStatement._DynamicWithClause<B, WE>,
            _Statement._WithClauseSpec, CriteriaContextSpec {

        private boolean recursive;

        private List<_Cte> cteList;

        protected WithClause(@Nullable _Statement._WithClauseSpec spec, CriteriaContext context) {
            super(context);
            if (spec != null) {
                this.recursive = spec.isRecursive();
                this.cteList = spec.cteList();
            }
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }


        @Override
        public final WE with(Consumer<B> consumer) {
            return endDynamicWithClause(false, consumer, true);
        }


        @Override
        public final WE withRecursive(Consumer<B> consumer) {
            return endDynamicWithClause(true, consumer, true);
        }


        @Override
        public final WE ifWith(Consumer<B> consumer) {
            return endDynamicWithClause(false, consumer, false);
        }


        @Override
        public final WE ifWithRecursive(Consumer<B> consumer) {
            return endDynamicWithClause(true, consumer, false);
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


        @SuppressWarnings("unchecked")
        protected final WE endStaticWithClause(final boolean recursive) {
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, true); // static with syntax is required
            return (WE) this;
        }


        protected abstract B createCteBuilder(boolean recursive);


        @SuppressWarnings("unchecked")
        private WE endDynamicWithClause(final boolean recursive, final Consumer<B> consumer, final boolean required) {
            final B builder;
            builder = createCteBuilder(recursive);

            CriteriaUtils.invokeConsumer(builder, consumer);

            ((CriteriaSupports.CteBuilder) builder).endLastCte();

            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, required);
            return (WE) this;
        }

    }//WithClause

    public static abstract class StatementMockSupport implements Statement.StatementMockSpec {

        public final CriteriaContext context;

        protected StatementMockSupport(CriteriaContext context) {
            this.context = context;
        }

        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            final DialectParser parser;
            parser = _MockDialects.from(dialect);
            final Stmt stmt;
            stmt = this.parseStatement(parser, visible);
            return parser.printStmt(stmt, none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            return this.parseStatement(_MockDialects.from(dialect), visible);
        }

        @Override
        public final String toString() {
            final String s;
            if (this instanceof PrimaryStatement && ((PrimaryStatement) this).isPrepared()) {
                Dialect dialect = this.context.dialect();
                if (dialect instanceof StandardDialect) {
                    dialect = MySQLDialect.MySQL57;
                }
                s = this.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }


        private Stmt parseStatement(final DialectParser parser, final Visible visible) {
            if (!(this instanceof PrimaryStatement)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            final Stmt stmt;
            if (this instanceof SelectStatement) {
                stmt = parser.select((SelectStatement) this, false, _MockDialects.sessionSpecFor(visible));
            } else if (this instanceof InsertStatement) {
                stmt = parser.insert((InsertStatement) this, _MockDialects.sessionSpecFor(visible));
            } else if (this instanceof UpdateStatement) {
                stmt = parser.update((UpdateStatement) this, false, _MockDialects.sessionSpecFor(visible));
            } else if (this instanceof DeleteStatement) {
                stmt = parser.delete((DeleteStatement) this, false, _MockDialects.sessionSpecFor(visible));
            } else if (this instanceof Values) {
                stmt = parser.values((Values) this, _MockDialects.sessionSpecFor(visible));
            } else if (this instanceof DqlStatement) {
                stmt = parser.dialectDql((DqlStatement) this, _MockDialects.sessionSpecFor(visible));
            } else if (this instanceof DmlStatement) {
                stmt = parser.dialectDml((DmlStatement) this, _MockDialects.sessionSpecFor(visible));
            } else {
                throw new IllegalStateException("unknown statement");
            }
            return stmt;
        }

    }//StatementMockSupport

    public static class ParenStringConsumerClause<RR>
            implements Statement._LeftParenStringQuadraOptionalSpec<RR>
            , Statement._LeftParenStringDualOptionalSpec<RR>
            , Statement._CommaStringDualSpec<RR>
            , Statement._CommaStringQuadraSpec<RR> {

        final CriteriaContext context;

        private final Function<List<String>, RR> function;

        private List<String> stringList;

        private boolean optionalClause;


        /**
         * <p>
         * private constructor for {@link  #stringQuadra(CriteriaContext, Function)}
         */
        private ParenStringConsumerClause(CriteriaContext context, Function<List<String>, RR> function) {
            this.context = context;
            this.function = function;
        }

        /**
         * <p>
         * package constructor for sub class
         */
        ParenStringConsumerClause(CriteriaContext context) {
            assert this.getClass() != ParenStringConsumerClause.class;
            this.context = context;
            this.function = this::stringConsumerEnd;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(String string) {
            this.optionalClause = false;
            return this.comma(string);
        }

        @Override
        public final Statement._CommaStringDualSpec<RR> leftParen(String string1, String string2) {
            this.optionalClause = false;
            this.comma(string1);
            this.comma(string2);
            return this;
        }

        @Override
        public final Statement._CommaStringQuadraSpec<RR> leftParen(String string1, String string2, String string3, String string4) {
            this.optionalClause = false;
            this.comma(string1);
            this.comma(string2);
            this.comma(string3);
            this.comma(string4);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(Consumer<Consumer<String>> consumer) {
            this.optionalClause = false;
            consumer.accept(this::comma);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParenIf(Consumer<Consumer<String>> consumer) {
            this.optionalClause = true;
            consumer.accept(this::comma);
            return this;
        }


        @Override
        public final Statement._RightParenClause<RR> comma(String string) {
            List<String> stringList = this.stringList;
            if (stringList == null) {
                stringList = _Collections.arrayList();
                this.stringList = stringList;
            } else if (!(stringList instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            stringList.add(string);
            return this;
        }

        @Override
        public final Statement._CommaStringDualSpec<RR> comma(String string1, String string2) {
            this.comma(string1);
            this.comma(string2);
            return this;
        }


        @Override
        public final Statement._RightParenClause<RR> comma(String string1, String string2, String string3) {
            this.comma(string1);
            this.comma(string2);
            this.comma(string3);
            return this;
        }

        @Override
        public final Statement._CommaStringQuadraSpec<RR> comma(String string1, String string2, String string3, String string4) {
            this.comma(string1);
            this.comma(string2);
            this.comma(string3);
            this.comma(string4);
            return this;
        }

        @Override
        public final RR rightParen() {
            List<String> stringList = this.stringList;
            if (stringList instanceof ArrayList) {
                stringList = _Collections.unmodifiableList(stringList);
            } else if (stringList != null) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else if (this.optionalClause) {
                stringList = Collections.emptyList();
            } else {
                throw ContextStack.criteriaError(this.context, "You don't add any string item");
            }
            //clear below for reuse this instance
            this.stringList = null;
            this.optionalClause = false;
            return this.function.apply(stringList);
        }

        RR stringConsumerEnd(List<String> stringList) {
            throw new UnsupportedOperationException();
        }


    }//ParenStringConsumerClause

    @SuppressWarnings("unchecked")
    public static abstract class CteParensClause<R> implements Statement._OptionalParensStringClause<R> {

        public final String name;

        public final CriteriaContext context;

        protected List<String> columnAliasList;

        public CteParensClause(String name, CriteriaContext context) {
            context.onStartCte(name);
            this.name = name;
            this.context = context;
        }

        @Override
        public final R parens(String first, String... rest) {
            this.columnAliasList = ArrayUtils.unmodifiableListOf(first, rest);
            return (R) this;
        }

        @Override
        public final R parens(Consumer<Consumer<String>> consumer) {
            this.columnAliasList = CriteriaUtils.stringList(this.context, true, consumer);
            return (R) this;
        }

        @Override
        public final R ifParens(Consumer<Consumer<String>> consumer) {
            final List<String> list;
            list = CriteriaUtils.stringList(this.context, false, consumer);
            this.columnAliasList = list.size() == 0 ? null : list;
            return (R) this;
        }


    }//CteParensClause


    private static final class SimpleDelayParamMeta implements TypeMeta {

        private final Supplier<MappingType> supplier;

        private SimpleDelayParamMeta(Supplier<MappingType> supplier) {
            this.supplier = supplier;
        }

        @Override
        public MappingType mappingType() {
            return this.supplier.get();
        }


    }//SimpleDelayParamMeta


    @SuppressWarnings("unchecked")
    private static abstract class UpdateSetClause<F extends SqlField, SR>
            implements UpdateStatement._StaticBatchSetClause<F, SR>
            , UpdateStatement._StaticRowSetClause<F, SR> {

        private final Consumer<ItemPair> consumer;

        private UpdateSetClause(Consumer<ItemPair> consumer) {
            this.consumer = consumer;
        }

        private UpdateSetClause() {
            assert this instanceof SimpleFieldItemPairs;
            this.consumer = this::onAddSimpleField;
        }


        @Override
        public final SR set(F field, Expression value) {
            this.consumer.accept(Armies._itemPair(field, null, value));
            return (SR) this;
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
        public final <E, R extends AssignmentItem> SR set(F field, BiFunction<F, E, R> valueOperator, @Nullable E value) {
            return this.onAddAssignmentItemPair(field, valueOperator.apply(field, value));
        }

        @Override
        public final <K, V, R extends AssignmentItem> SR set(F field, BiFunction<F, V, R> valueOperator,
                                                             Function<K, V> function, K key) {
            return this.onAddAssignmentItemPair(field, valueOperator.apply(field, function.apply(key)));
        }

        @Override
        public final <E, V, R extends AssignmentItem> SR set(F field, BiFunction<F, V, R> fieldOperator,
                                                             BiFunction<F, E, V> valueOperator, E value) {
            return this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, value)));
        }

        @Override
        public final <K, V, U, R extends AssignmentItem> SR set(F field, BiFunction<F, U, R> fieldOperator,
                                                                BiFunction<F, V, U> valueOperator,
                                                                Function<K, V> function, K key) {
            return this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, function.apply(key))));
        }

        @Override
        public final <R extends AssignmentItem> SR ifSet(F field, Supplier<R> supplier) {
            final R item;
            if ((item = supplier.get()) != null) {
                this.onAddAssignmentItemPair(field, item);
            }
            return (SR) this;
        }

        @Override
        public final <R extends AssignmentItem> SR ifSet(F field, Function<F, R> function) {
            final R item;
            if ((item = function.apply(field)) != null) {
                this.onAddAssignmentItemPair(field, item);
            }
            return (SR) this;
        }

        @Override
        public final <E, R extends AssignmentItem> SR ifSet(F field, BiFunction<F, E, R> valueOperator,
                                                            Supplier<E> supplier) {
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
            this.consumer.accept(Armies._itemPair(field, null, valueOperator.apply(field, field.fieldName())));
            return (SR) this;
        }

        @Override
        public final <R extends AssignmentItem> SR setSpace(F field, BiFunction<F, Expression, R> fieldOperator,
                                                            BiFunction<F, String, Expression> valueOperator) {
            return this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, field.fieldName())));
        }


        @Override
        public final SR setRow(F field1, F field2, Supplier<SubQuery> supplier) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2);
            this.consumer.accept(Armies._itemPair(fieldList, supplier.get()));
            return (SR) this;
        }

        @Override
        public final SR setRow(F field1, F field2, F field3, Supplier<SubQuery> supplier) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2, field3);
            this.consumer.accept(Armies._itemPair(fieldList, supplier.get()));
            return (SR) this;
        }

        @Override
        public final SR setRow(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2, field3, field4);
            this.consumer.accept(Armies._itemPair(fieldList, supplier.get()));
            return (SR) this;
        }

        @Override
        public final SR setRow(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier) {
            final List<F> fieldList = _Collections.arrayList();
            consumer.accept(fieldList::add);
            this.consumer.accept(Armies._itemPair(fieldList, supplier.get()));
            return (SR) this;
        }

        @Override
        public final SR ifSetRow(F field1, F field2, Supplier<SubQuery> supplier) {
            final SubQuery query;
            if ((query = supplier.get()) != null) {
                final List<F> fieldList;
                fieldList = Arrays.asList(field1, field2);
                this.consumer.accept(Armies._itemPair(fieldList, query));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetRow(F field1, F field2, F field3, Supplier<SubQuery> supplier) {
            final SubQuery query;
            if ((query = supplier.get()) != null) {
                final List<F> fieldList;
                fieldList = Arrays.asList(field1, field2, field3);
                this.consumer.accept(Armies._itemPair(fieldList, query));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetRow(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier) {
            final SubQuery query;
            if ((query = supplier.get()) != null) {
                final List<F> fieldList;
                fieldList = Arrays.asList(field1, field2, field3, field4);
                this.consumer.accept(Armies._itemPair(fieldList, query));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetRow(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier) {
            final List<F> fieldList = _Collections.arrayList();
            consumer.accept(fieldList::add);
            final SubQuery query;
            if (fieldList.size() > 0 && (query = supplier.get()) != null) {
                this.consumer.accept(Armies._itemPair(fieldList, query));
            }
            return (SR) this;
        }

        void onAddSimpleField(final ItemPair pair) {
            throw new UnsupportedOperationException();
        }


        private SR onAddAssignmentItemPair(final F field, final @Nullable AssignmentItem item) {
            final ItemPair pair;
            if (item == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (item instanceof Expression) {
                pair = Armies._itemPair(field, null, (Expression) item);
            } else if (item instanceof ItemPair) {
                pair = (ItemPair) item;
            } else {
                throw CriteriaUtils.illegalAssignmentItem(null, item);
            }
            this.consumer.accept(pair);
            return (SR) this;
        }

    }//UpdateSetClause


    private static final class SimpleFieldItemPairs<F extends SqlField>
            extends UpdateSetClause<F, UpdateStatement._ItemPairs<F>>
            implements UpdateStatement._ItemPairs<F> {

        private final CriteriaContext context;

        private final TableMeta<?> updateTable;

        private final Consumer<_ItemPair> fieldConsumer;

        private SimpleFieldItemPairs(CriteriaContext context, TableMeta<?> updateTable
                , Consumer<_ItemPair> fieldConsumer) {
            super();
            this.updateTable = updateTable;
            this.context = context;
            this.fieldConsumer = fieldConsumer;
        }


        @Override
        void onAddSimpleField(final ItemPair pair) {
            final Armies.FieldItemPair fieldPair;
            final TableField field;
            if (!(pair instanceof Armies.FieldItemPair)) {
                //here, support only simple filed
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else if (!((fieldPair = (Armies.FieldItemPair) pair).field instanceof TableField)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            } else if ((field = (TableField) fieldPair.field).tableMeta() != this.updateTable) {
                throw ContextStack.criteriaError(this.context, _Exceptions::unknownColumn, field);
            } else if (field.updateMode() == UpdateMode.IMMUTABLE) {
                throw ContextStack.criteriaError(this.context, _Exceptions::immutableField, field);
            } else if (!field.nullable() && ((ArmyExpression) fieldPair.right).isNullValue()) {
                throw ContextStack.criteriaError(this.context, _Exceptions::nonNullField, field);
            } else {
                this.fieldConsumer.accept(fieldPair);
            }
        }


    }//SimpleFieldItemPairs

    private static final class ItemPairsImpl<F extends SqlField> extends UpdateSetClause<F, UpdateStatement._ItemPairs<F>>
            implements UpdateStatement._ItemPairs<F> {

        private ItemPairsImpl(Consumer<ItemPair> consumer) {
            super(consumer);
        }


    }//ItemPairsImpl

    private static final class BatchItemPairsImpl<F extends SqlField> extends UpdateSetClause<F, UpdateStatement._BatchItemPairs<F>>
            implements UpdateStatement._BatchItemPairs<F> {

        private BatchItemPairsImpl(Consumer<ItemPair> consumer) {
            super(consumer);
        }


    }//BatchItemPairsImpl

    private static final class RowItemPairsImpl<F extends SqlField> extends UpdateSetClause<F, UpdateStatement._RowPairs<F>>
            implements UpdateStatement._RowPairs<F> {

        private RowItemPairsImpl(Consumer<ItemPair> consumer) {
            super(consumer);
        }

    } //RowItemPairsImpl

    private static final class BatchRowItemPairsImpl<F extends SqlField> extends UpdateSetClause<F, UpdateStatement._BatchRowPairs<F>>
            implements UpdateStatement._BatchRowPairs<F> {

        private BatchRowItemPairsImpl(Consumer<ItemPair> consumer) {
            super(consumer);
        }

    } //RowItemPairsImpl


    private static final class ReturningBuilderImpl implements Returnings {

        private final Consumer<_SelectItem> consumer;

        private ReturningBuilderImpl(Consumer<_SelectItem> consumer) {
            this.consumer = consumer;
        }

        @Override
        public Returnings selection(Selection selection) {
            this.consumer.accept((_Selection) selection);
            return this;
        }

        @Override
        public Returnings selection(Selection selection1, Selection selection2) {
            final Consumer<_SelectItem> consumer = this.consumer;
            consumer.accept((_Selection) selection1);
            consumer.accept((_Selection) selection2);
            return this;
        }

        @Override
        public Returnings selection(Function<String, Selection> function, String alias) {
            this.consumer.accept((_Selection) function.apply(alias));
            return this;
        }

        @Override
        public Returnings selection(Function<String, Selection> function1, String alias1,
                                    Function<String, Selection> function2, String alias2) {
            final Consumer<_SelectItem> consumer = this.consumer;
            consumer.accept((_Selection) function1.apply(alias1));
            consumer.accept((_Selection) function2.apply(alias2));
            return this;
        }

        @Override
        public Returnings selection(Function<String, Selection> function, String alias, Selection selection) {
            final Consumer<_SelectItem> consumer = this.consumer;
            consumer.accept((_Selection) function.apply(alias));
            consumer.accept((_Selection) selection);
            return this;
        }

        @Override
        public Returnings selection(Selection selection, Function<String, Selection> function, String alias) {
            final Consumer<_SelectItem> consumer = this.consumer;
            consumer.accept((_Selection) selection);
            consumer.accept((_Selection) function.apply(alias));
            return this;
        }

        @Override
        public Returnings selection(TableField field1, TableField field2, TableField field3) {
            final Consumer<_SelectItem> consumer = this.consumer;
            consumer.accept((_Selection) field1);
            consumer.accept((_Selection) field2);
            consumer.accept((_Selection) field3);
            return this;
        }

        @Override
        public Returnings selection(TableField field1, TableField field2, TableField field3, TableField field4) {
            final Consumer<_SelectItem> consumer = this.consumer;
            consumer.accept((_Selection) field1);
            consumer.accept((_Selection) field2);
            consumer.accept((_Selection) field3);
            consumer.accept((_Selection) field4);
            return this;
        }

    }//ReturningBuilderImpl

    public static final class RowExpressionImpl extends OperationRowExpression implements ArmyRowExpression, FunctionArg.SingleFunctionArg {

        private final List<ArmySQLExpression> columnList;

        private RowExpressionImpl(List<ArmySQLExpression> columnList) {
            assert columnList.size() > 0;
            this.columnList = columnList;
        }


        @Override
        public int columnSize() {
            return this.columnList.size();
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            sqlBuilder.append(" ROW(");

            final List<ArmySQLExpression> columnList = this.columnList;
            final int size;
            size = columnList.size();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                columnList.get(i).appendSql(sqlBuilder, context);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }

        @Override
        public String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder()
                    .append(" ROW(");

            final List<ArmySQLExpression> columnList = this.columnList;
            final int size;
            size = columnList.size();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                sqlBuilder.append(columnList.get(i));
            }
            return sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//RowExpressionImpl

    public static final class StringObjectSpaceClause implements Statement._StringObjectSpaceClause,
            Statement._StringObjectCommaClause {

        private final boolean required;

        private final Consumer<String> consumer;

        private Boolean state;

        private StringObjectSpaceClause(boolean required, Consumer<String> consumer) {
            this.required = required;
            this.consumer = consumer;
        }

        @Override
        public Statement._StringObjectCommaClause space(String key, String value) {
            if (this.state != null) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.state = Boolean.TRUE;
            return this.comma(key, value);
        }

        @Override
        public Statement._StringObjectCommaClause comma(final @Nullable String key, final String value) {
            if (key == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (!_StringUtils.hasText(key)) {
                throw ContextStack.clearStackAndCriteriaError("key must have text.");
            }
            this.consumer.accept(key);
            this.consumer.accept(value);
            return this;
        }

        void endClause() {
            if (this.required && this.state == null) {
                throw CriteriaUtils.dontAddAnyItem();
            }
            this.state = Boolean.FALSE;
        }


    }//StringObjectSpaceClause

    public static final class StringObjectConsumer implements Statement._StringObjectConsumer {

        private final boolean required;

        private final Consumer<String> consumer;

        private Boolean state;

        private StringObjectConsumer(boolean required, Consumer<String> consumer) {
            this.required = required;
            this.consumer = consumer;
        }

        @Override
        public Statement._StringObjectConsumer accept(final @Nullable String key, String value) {
            final Boolean state = this.state;
            if (state == null) {
                this.state = Boolean.TRUE;
            } else if (state == Boolean.FALSE) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }

            if (key == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (!_StringUtils.hasText(key)) {
                throw ContextStack.clearStackAndCriteriaError("key must have text.");
            }
            this.consumer.accept(key);
            this.consumer.accept(value);
            return this;
        }

        void endConsumer() {
            if (this.required && this.state == null) {
                throw CriteriaUtils.dontAddAnyItem();
            }
            this.state = Boolean.FALSE;
        }

    }//StringObjectConsumer

    public static final class StaticObjectConsumer implements Clause._PairVariadicSpaceClause
            , Clause._PairVariadicCommaClause {

        private final boolean required;

        private final Consumer<Object> consumer;

        private Boolean state;

        private StaticObjectConsumer(boolean required, Consumer<Object> consumer) {
            this.required = required;
            this.consumer = consumer;
        }

        @Override
        public Clause._PairVariadicCommaClause space(String keyName, @Nullable Object value) {
            if (this.state != null) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.state = Boolean.TRUE;
            return this.onAddPair(keyName, value);
        }

        @Override
        public Clause._PairVariadicCommaClause space(Expression key, @Nullable Object value) {
            if (this.state != null) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.state = Boolean.TRUE;
            return this.onAddPair(key, value);
        }

        @Override
        public Clause._PairVariadicCommaClause comma(String keyName, @Nullable Object value) {
            return this.onAddPair(keyName, value);
        }

        @Override
        public Clause._PairVariadicCommaClause comma(Expression key, @Nullable Object value) {
            return this.onAddPair(key, value);
        }


        void endConsumer() {
            if (this.state == null && this.required) {
                throw CriteriaUtils.dontAddAnyItem();
            }
            this.state = Boolean.FALSE;
        }

        private Clause._PairVariadicCommaClause onAddPair(final @Nullable Object key, final Object value) {
            if (this.state != Boolean.TRUE) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (key == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.consumer.accept(key);
            this.consumer.accept(value);
            return this;
        }


    }//StaticObjectConsumer

    public static final class DynamicObjectConsumer implements Clause._PairVariadicConsumerClause {

        private final boolean required;

        private final Consumer<Object> consumer;

        private Boolean state;

        private DynamicObjectConsumer(boolean required, Consumer<Object> consumer) {
            this.required = required;
            this.consumer = consumer;
        }

        @Override
        public Clause._PairVariadicConsumerClause accept(String keyName, Object value) {
            return this.onAddPair(keyName, value);
        }

        @Override
        public Clause._PairVariadicConsumerClause accept(Expression key, Object value) {
            return this.onAddPair(key, value);
        }

        void endConsumer() {
            if (this.state == null && this.required) {
                throw CriteriaUtils.dontAddAnyItem();
            }
            this.state = Boolean.FALSE;
        }


        private Clause._PairVariadicConsumerClause onAddPair(final @Nullable Object key, final Object value) {
            final Boolean state = this.state;
            if (state == Boolean.FALSE) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (state == null) {
                this.state = Boolean.TRUE;
            }
            if (key == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.consumer.accept(key);
            this.consumer.accept(value);
            return this;
        }


    }//DynamicObjectConsumer

    public static final class ObjectVariadic implements Clause._VariadicSpaceClause, Clause._VariadicCommaClause {

        private List<ArmyExpression> list;

        private ObjectVariadic() {
        }

        @Override
        public Clause._VariadicCommaClause comma(final @Nullable Object exp) {
            List<ArmyExpression> list = this.list;
            if (list == null) {
                this.list = list = _Collections.arrayList();
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            list.add((ArmyExpression) Armies._nullableExp(exp));
            return this;
        }

        @Override
        public Clause._VariadicCommaClause space(@Nullable Object exp) {
            return this.comma(exp);
        }

        List<ArmyExpression> endClause() {
            List<ArmyExpression> list = this.list;
            if (list == null) {
                this.list = list = Collections.emptyList();
            } else if (list instanceof ArrayList) {
                this.list = list = _Collections.unmodifiableList(list);
            } else {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            return list;
        }

    } // ObjectVariadic


}
