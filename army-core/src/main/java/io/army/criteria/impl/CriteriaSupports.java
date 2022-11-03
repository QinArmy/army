package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner._Statement;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._MockDialects;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;
import io.army.stmt.Stmt;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class CriteriaSupports {

    CriteriaSupports() {
        throw new UnsupportedOperationException();
    }


    static <RR> Statement._LeftParenStringQuadraOptionalSpec<RR> stringQuadra(CriteriaContext context
            , Function<List<String>, RR> function) {
        return new ParenStringConsumerClause<>(context, function);
    }

    @Deprecated
    static <OR> Statement._StaticOrderByClause<OR> orderByClause(CriteriaContext criteriaContext
            , Function<List<ArmySortItem>, OR> function) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    static <OR> Statement._StaticOrderByClause<OR> voidOrderByClause(CriteriaContext criteriaContext
            , Function<List<ArmySortItem>, OR> function) {
        throw new UnsupportedOperationException();
    }

    static ReturningBuilder returningBuilder(Consumer<Selection> consumer) {
        return new ReturningBuilderImpl(consumer);
    }


    static TypeMeta delayParamMeta(TypeMeta.Delay paramMeta, Function<MappingType, MappingType> function) {
        return new DelayParamMetaWrapper(paramMeta, function);
    }

    static TypeMeta delayParamMeta(TypeMeta paramMeta1, TypeMeta paramMeta2
            , BiFunction<MappingType, MappingType, MappingType> function) {
        return new BiDelayParamMetaWrapper(paramMeta1, paramMeta2, function);
    }

    static TypeMeta delayParamMeta(Supplier<MappingType> supplier) {
        return new SimpleDelayParamMeta(supplier);
    }

    static <F extends DataField> ItemPairs<F> itemPairs(Consumer<ItemPair> consumer) {
        return new ItemPairsImpl<>(consumer);
    }

    static <F extends DataField> BatchItemPairs<F> batchItemPairs(Consumer<ItemPair> consumer) {
        return new BatchItemPairsImpl<>(consumer);
    }

    static <F extends DataField> RowPairs<F> rowPairs(Consumer<ItemPair> consumer) {
        return new RowItemPairsImpl<>(consumer);
    }

    static <F extends DataField> BatchRowPairs<F> batchRowPairs(Consumer<ItemPair> consumer) {
        return new BatchRowItemPairsImpl<>(consumer);
    }

    static <F extends DataField> ItemPairs<F> simpleFieldItemPairs(CriteriaContext context
            , @Nullable TableMeta<?> updateTable, Consumer<_ItemPair> consumer) {
        assert updateTable != null;
        return new SimpleFieldItemPairs<>(context, updateTable, consumer);
    }


    static abstract class WithClause<B extends CteBuilderSpec, WE> implements DialectStatement._DynamicWithClause<B, WE>
            , _Statement._WithClauseSpec {

        final CriteriaContext context;

        private boolean recursive;

        private List<_Cte> cteList;

        WithClause(CriteriaContext context) {
            this.context = context;
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


        final WE endStaticWithClause(final boolean recursive) {
            if (this.cteList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(true);//static with syntax is required
            return (WE) this;
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

    }//WithClause

    static abstract class StatementMockSupport implements Statement.StatementMockSpec, Statement {

        final CriteriaContext context;

        StatementMockSupport(CriteriaContext context) {
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
            if (this instanceof PrimaryStatement && this.isPrepared()) {
                s = this.mockAsString(this.statementDialect(), Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

        abstract Dialect statementDialect();

        private Stmt parseStatement(final DialectParser parser, final Visible visible) {
            if (!(this instanceof PrimaryStatement)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final Stmt stmt;
            if (this instanceof Select) {
                stmt = parser.select((Select) this, visible);
            } else if (this instanceof Update) {
                stmt = parser.update((Update) this, visible);
            } else if (this instanceof Delete) {
                stmt = parser.delete((Delete) this, visible);
            } else if (this instanceof DialectStatement) {
                stmt = parser.dialectStatement((DialectStatement) this, visible);
            } else {
                throw new IllegalStateException("unknown statement");
            }
            return stmt;
        }

    }//StatementMockSupport



    static class ParenStringConsumerClause<RR>
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
         * </p>
         */
        private ParenStringConsumerClause(CriteriaContext context, Function<List<String>, RR> function) {
            this.context = context;
            this.function = function;
        }

        /**
         * <p>
         * package constructor for sub class
         * </p>
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
                stringList = new ArrayList<>();
                this.stringList = stringList;
            } else if (!(stringList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
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
                stringList = _CollectionUtils.unmodifiableList(stringList);
            } else if (stringList != null) {
                throw ContextStack.castCriteriaApi(this.context);
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


    private static final class DelayParamMetaWrapper implements TypeMeta.Delay {

        private final Delay paramMeta;

        private final Function<MappingType, MappingType> function;

        private CriteriaContext criteriaContext;

        private MappingType actualType;

        private DelayParamMetaWrapper(Delay paramMeta, Function<MappingType, MappingType> function) {
            this.paramMeta = paramMeta;
            this.function = function;
            final CriteriaContext criteriaContext;
            criteriaContext = ContextStack.peek();
            this.criteriaContext = criteriaContext;
            criteriaContext.addEndEventListener(this::contextEnd);
        }

        @Override
        public MappingType mappingType() {
            final MappingType actualType = this.actualType;
            if (actualType == null) {
                String m = String.format("%s isn't prepared.", TypeMeta.Delay.class.getName());
                throw new IllegalStateException(m);
            }
            return actualType;
        }

        @Override
        public boolean isPrepared() {
            return this.paramMeta.isPrepared();
        }

        private void contextEnd() {
            if (this.criteriaContext == null) {
                throw _Exceptions.castCriteriaApi();
            }
            this.actualType = this.function.apply(this.paramMeta.mappingType());
            this.criteriaContext = null;
        }


    }//DelayParamMetaWrapper

    private static final class BiDelayParamMetaWrapper implements TypeMeta.Delay {

        private final TypeMeta paramMeta1;

        private final TypeMeta paramMeta2;

        private final BiFunction<MappingType, MappingType, MappingType> function;

        private CriteriaContext criteriaContext;

        private MappingType actualType;

        private BiDelayParamMetaWrapper(TypeMeta paramMeta1, TypeMeta paramMeta2
                , BiFunction<MappingType, MappingType, MappingType> function) {
            this.paramMeta1 = paramMeta1;
            this.paramMeta2 = paramMeta2;
            this.function = function;

            final CriteriaContext criteriaContext;
            criteriaContext = ContextStack.peek();
            this.criteriaContext = criteriaContext;
            criteriaContext.addEndEventListener(this::contextEnd);
        }

        @Override
        public MappingType mappingType() {
            final MappingType actualType = this.actualType;
            if (actualType == null) {
                String m = String.format("%s isn't prepared.", TypeMeta.Delay.class.getName());
                throw new IllegalStateException(m);
            }
            return actualType;
        }

        @Override
        public boolean isPrepared() {
            boolean prepared1 = true, prepared2 = true;
            if (this.paramMeta1 instanceof TypeMeta.Delay) {
                prepared1 = ((Delay) this.paramMeta1).isPrepared();
            } else if (this.paramMeta2 instanceof TypeMeta.Delay) {
                prepared2 = ((Delay) this.paramMeta2).isPrepared();
            }
            return prepared1 && prepared2;
        }

        private void contextEnd() {
            if (this.criteriaContext == null) {
                throw _Exceptions.castCriteriaApi();
            }
            this.actualType = this.function.apply(this.paramMeta1.mappingType(), this.paramMeta2.mappingType());
            this.criteriaContext = null;
        }


    }//BiDelayParamMetaWrapper


    @SuppressWarnings("unchecked")
    private static abstract class UpdateSetClause<F extends DataField, SR>
            implements Update._StaticBatchSetClause<F, SR>
            , Update._StaticRowSetClause<F, SR> {

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
            this.consumer.accept(SQLs._itemPair(field, null, value));
            return (SR) this;
        }

        @Override
        public final SR set(F field, Supplier<Expression> supplier) {
            this.consumer.accept(SQLs._itemPair(field, null, supplier.get()));
            return (SR) this;
        }

        @Override
        public final SR set(F field, Function<F, Expression> function) {
            this.consumer.accept(SQLs._itemPair(field, null, function.apply(field)));
            return (SR) this;
        }

        @Override
        public final <E> SR set(F field, BiFunction<F, E, Expression> valueOperator, @Nullable E value) {
            this.consumer.accept(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
            return (SR) this;
        }

        @Override
        public final <E> SR set(F field, BiFunction<F, E, Expression> valueOperator, Supplier<E> supplier) {
            this.consumer.accept(SQLs._itemPair(field, null, valueOperator.apply(field, supplier.get())));
            return (SR) this;
        }

        @Override
        public final SR set(F field, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            this.consumer.accept(SQLs._itemPair(field, null, valueOperator.apply(field, function.apply(keyName))));
            return (SR) this;
        }

        @Override
        public final <E> SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator
                , BiFunction<F, E, Expression> valueOperator, @Nullable E value) {
            this.consumer.accept(fieldOperator.apply(field, valueOperator.apply(field, value)));
            return (SR) this;
        }

        @Override
        public final <E> SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator
                , BiFunction<F, E, Expression> valueOperator, Supplier<E> supplier) {
            this.consumer.accept(fieldOperator.apply(field, valueOperator.apply(field, supplier.get())));
            return (SR) this;
        }

        @Override
        public final SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator
                , BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
            this.consumer.accept(fieldOperator.apply(field, valueOperator.apply(field, function.apply(keyName))));
            return (SR) this;
        }

        @Override
        public final SR ifSet(F field, Supplier<Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression != null) {
                this.consumer.accept(SQLs._itemPair(field, null, expression));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSet(F field, Function<F, Expression> function) {
            final Expression expression;
            expression = function.apply(field);
            if (expression != null) {
                this.consumer.accept(SQLs._itemPair(field, null, expression));
            }
            return (SR) this;
        }

        @Override
        public final <E> SR ifSet(F field, BiFunction<F, E, Expression> valueOperator, @Nullable E value) {
            if (value != null) {
                this.consumer.accept(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
            }
            return (SR) this;
        }

        @Override
        public final <E> SR ifSet(F field, BiFunction<F, E, Expression> valueOperator, Supplier<E> supplier) {
            final E value;
            value = supplier.get();
            if (value != null) {
                this.consumer.accept(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSet(F field, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.consumer.accept(SQLs._itemPair(field, null, valueOperator.apply(field, value)));
            }
            return (SR) this;
        }

        @Override
        public final <E> SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator
                , BiFunction<F, E, Expression> valueOperator, @Nullable E value) {
            if (value != null) {
                this.consumer.accept(fieldOperator.apply(field, valueOperator.apply(field, value)));
            }
            return (SR) this;
        }

        @Override
        public final <E> SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator
                , BiFunction<F, E, Expression> valueOperator, Supplier<E> supplier) {
            final E value;
            value = supplier.get();
            if (value != null) {
                this.consumer.accept(fieldOperator.apply(field, valueOperator.apply(field, value)));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator
                , BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.consumer.accept(fieldOperator.apply(field, valueOperator.apply(field, value)));
            }
            return (SR) this;
        }

        @Override
        public final SR set(F field, BiFunction<F, String, Expression> valueOperator) {
            this.consumer.accept(SQLs._itemPair(field, null, valueOperator.apply(field, field.fieldName())));
            return (SR) this;
        }

        @Override
        public final SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator
                , BiFunction<F, String, Expression> valueOperator) {
            this.consumer.accept(fieldOperator.apply(field, valueOperator.apply(field, field.fieldName())));
            return (SR) this;
        }

        @Override
        public final SR set(F field1, F field2, Supplier<SubQuery> supplier) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2);
            this.consumer.accept(SQLs._itemPair(fieldList, supplier.get()));
            return (SR) this;
        }

        @Override
        public final SR set(F field1, F field2, F field3, Supplier<SubQuery> supplier) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2, field3);
            this.consumer.accept(SQLs._itemPair(fieldList, supplier.get()));
            return (SR) this;
        }

        @Override
        public final SR set(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2, field3, field4);
            this.consumer.accept(SQLs._itemPair(fieldList, supplier.get()));
            return (SR) this;
        }

        @Override
        public final SR set(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier) {
            final List<F> fieldList = new ArrayList<>();
            consumer.accept(fieldList::add);
            this.consumer.accept(SQLs._itemPair(fieldList, supplier.get()));
            return (SR) this;
        }

        void onAddSimpleField(final ItemPair pair) {
            throw new UnsupportedOperationException();
        }

    }//UpdateSetClause


    private static final class SimpleFieldItemPairs<F extends DataField> extends UpdateSetClause<F, ItemPairs<F>>
            implements ItemPairs<F> {

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
            final SQLs.FieldItemPair fieldPair;
            final TableField field;
            if (!(pair instanceof SQLs.FieldItemPair)) {
                //here, support only simple filed
                throw ContextStack.castCriteriaApi(this.context);
            } else if (!((fieldPair = (SQLs.FieldItemPair) pair).field instanceof TableField)) {
                throw ContextStack.castCriteriaApi(this.context);
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

    private static final class ItemPairsImpl<F extends DataField> extends UpdateSetClause<F, ItemPairs<F>>
            implements ItemPairs<F> {

        private ItemPairsImpl(Consumer<ItemPair> consumer) {
            super(consumer);
        }


    }//ItemPairsImpl

    private static final class BatchItemPairsImpl<F extends DataField> extends UpdateSetClause<F, BatchItemPairs<F>>
            implements BatchItemPairs<F> {

        private BatchItemPairsImpl(Consumer<ItemPair> consumer) {
            super(consumer);
        }


    }//BatchItemPairsImpl

    private static final class RowItemPairsImpl<F extends DataField> extends UpdateSetClause<F, RowPairs<F>>
            implements RowPairs<F> {

        private RowItemPairsImpl(Consumer<ItemPair> consumer) {
            super(consumer);
        }

    } //RowItemPairsImpl

    private static final class BatchRowItemPairsImpl<F extends DataField> extends UpdateSetClause<F, BatchRowPairs<F>>
            implements BatchRowPairs<F> {

        private BatchRowItemPairsImpl(Consumer<ItemPair> consumer) {
            super(consumer);
        }

    } //RowItemPairsImpl


    private static final class ReturningBuilderImpl implements ReturningBuilder {

        private final Consumer<Selection> consumer;

        private ReturningBuilderImpl(Consumer<Selection> consumer) {
            this.consumer = consumer;
        }

        @Override
        public ReturningBuilder selection(Selection selection) {
            this.consumer.accept(selection);
            return this;
        }

        @Override
        public ReturningBuilder selection(Expression expression, SQLs.WordAs wordAs, String alias) {
            this.consumer.accept(Selections.forExp((ArmyExpression) expression, alias));
            return this;
        }

        @Override
        public ReturningBuilder selection(Supplier<Selection> supplier) {
            this.consumer.accept(supplier.get());
            return this;
        }

        @Override
        public ReturningBuilder selection(NamedExpression exp1, NamedExpression exp2) {
            final Consumer<Selection> consumer = this.consumer;
            consumer.accept(exp1);
            consumer.accept(exp2);
            return this;
        }

        @Override
        public ReturningBuilder selection(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3) {
            final Consumer<Selection> consumer = this.consumer;
            consumer.accept(exp1);
            consumer.accept(exp2);
            consumer.accept(exp3);
            return this;
        }

        @Override
        public ReturningBuilder selection(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3
                , NamedExpression exp4) {
            final Consumer<Selection> consumer = this.consumer;
            consumer.accept(exp1);
            consumer.accept(exp2);
            consumer.accept(exp3);
            consumer.accept(exp4);
            return this;
        }


    }//ReturningBuilderImpl


}
