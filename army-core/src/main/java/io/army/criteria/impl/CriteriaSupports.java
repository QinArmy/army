package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.dialect.Returnings;
import io.army.criteria.impl.inner.*;
import io.army.dialect.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;
import io.army.stmt.Stmt;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

abstract class CriteriaSupports {

    CriteriaSupports() {
        throw new UnsupportedOperationException();
    }


    static <RR> Statement._LeftParenStringQuadraOptionalSpec<RR> stringQuadra(CriteriaContext context
            , Function<List<String>, RR> function) {
        return new ParenStringConsumerClause<>(context, function);
    }


    static StringObjectSpaceClause stringObjectSpace(boolean required, Consumer<String> consumer) {
        return new StringObjectSpaceClause(required, consumer);
    }

    static StringObjectConsumer stringObjectConsumer(boolean required, Consumer<String> consumer) {
        return new StringObjectConsumer(required, consumer);
    }


    static StaticObjectConsumer staticObjectConsumer(boolean required, Consumer<Object> consumer) {
        return new StaticObjectConsumer(required, consumer);
    }

    static DynamicObjectConsumer dynamicObjectConsumer(boolean required, Consumer<Object> consumer) {
        return new DynamicObjectConsumer(required, consumer);
    }


    static Returnings returningBuilder(Consumer<_SelectItem> consumer) {
        return new ReturningBuilderImpl(consumer);
    }


    static TypeMeta delayWrapper(TypeMeta.DelayTypeMeta delayType, Function<MappingType, MappingType> function) {
        return new DelayTypeWrapper(delayType, function);
    }

    static TypeMeta unaryInfer(TypeInfer.DelayTypeInfer infer, UnaryOperator<MappingType> function) {
        return new UnaryDelayInferWrapper(infer, function);
    }

    static TypeMeta dualInfer(TypeInfer leftInfer, TypeInfer rightInfer,
                              BinaryOperator<MappingType> function) {
        return new DualDelayInferWrapper(leftInfer, rightInfer, function);
    }


    static TypeMeta biDelayWrapper(TypeMeta type1, TypeMeta type2,
                                   BiFunction<MappingType, MappingType, MappingType> function) {
        return new BiDelayTypeWrapper(type1, type2, function);
    }

    static TypeMeta delayParamMeta(Supplier<MappingType> supplier) {
        return new SimpleDelayParamMeta(supplier);
    }

    static <F extends SQLField> UpdateStatement._ItemPairs<F> itemPairs(Consumer<ItemPair> consumer) {
        return new ItemPairsImpl<>(consumer);
    }

    static <F extends SQLField> UpdateStatement._BatchItemPairs<F> batchItemPairs(Consumer<ItemPair> consumer) {
        return new BatchItemPairsImpl<>(consumer);
    }

    static <F extends SQLField> UpdateStatement._RowPairs<F> rowPairs(Consumer<ItemPair> consumer) {
        return new RowItemPairsImpl<>(consumer);
    }

    static <F extends SQLField> UpdateStatement._BatchRowPairs<F> batchRowPairs(Consumer<ItemPair> consumer) {
        return new BatchRowItemPairsImpl<>(consumer);
    }

    static <F extends SQLField> UpdateStatement._ItemPairs<F> simpleFieldItemPairs(CriteriaContext context
            , @Nullable TableMeta<?> updateTable, Consumer<_ItemPair> consumer) {
        assert updateTable != null;
        return new SimpleFieldItemPairs<>(context, updateTable, consumer);
    }


    /**
     * This interface is base interface of All implementation of {@link CteBuilderSpec}
     */
    interface CteBuilder extends CteBuilderSpec {

        void endLastCte();
    }


    static abstract class WithClause<B extends CteBuilderSpec, WE extends Item>
            implements DialectStatement._DynamicWithClause<B, WE>,
            _Statement._WithClauseSpec {

        final CriteriaContext context;

        private boolean recursive;

        private List<_Cte> cteList;

        WithClause(@Nullable _Statement._WithClauseSpec spec, CriteriaContext context) {
            if (spec != null) {
                this.recursive = spec.isRecursive();
                this.cteList = spec.cteList();
            }
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


        @SuppressWarnings("unchecked")
        final WE endStaticWithClause(final boolean recursive) {
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, true);//static with syntax is required
            return (WE) this;
        }


        abstract B createCteBuilder(boolean recursive);


        @SuppressWarnings("unchecked")
        private WE endDynamicWithClause(final B builder, final boolean required) {
            ((CriteriaSupports.CteBuilder) builder).endLastCte();

            final boolean recursive;
            recursive = builder.isRecursive();
            this.recursive = recursive;
            this.cteList = this.context.endWithClause(recursive, required);
            return (WE) this;
        }

    }//WithClause

    static abstract class StatementMockSupport implements Statement.StatementMockSpec {

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
            if (this instanceof PrimaryStatement && ((PrimaryStatement) this).isPrepared()) {
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
            } else if (this instanceof InsertStatement) {
                stmt = parser.insert((InsertStatement) this, visible);
            } else if (this instanceof UpdateStatement) {
                stmt = parser.update((UpdateStatement) this, visible);
            } else if (this instanceof DeleteStatement) {
                stmt = parser.delete((DeleteStatement) this, visible);
            } else if (this instanceof Values) {
                stmt = parser.values((Values) this, visible);
            } else if (this instanceof DqlStatement) {
                stmt = parser.dialectDql((DqlStatement) this, visible);
            } else if (this instanceof DmlStatement) {
                stmt = parser.dialectDml((DmlStatement) this, visible);
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
                stringList = _Collections.unmodifiableList(stringList);
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

    @SuppressWarnings("unchecked")
    static abstract class CteParensClause<R> implements Statement._OptionalParensStringClause<R> {

        final String name;

        final CriteriaContext context;

        List<String> columnAliasList;

        CteParensClause(String name, CriteriaContext context) {
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


    private static final class DelayTypeWrapper implements TypeMeta.DelayTypeMeta {

        private final DelayTypeMeta delayType;

        private final Function<MappingType, MappingType> function;

        private MappingType actualType;

        /**
         * @see #delayWrapper(DelayTypeMeta, Function)
         */
        private DelayTypeWrapper(DelayTypeMeta delayType, Function<MappingType, MappingType> function) {
            this.delayType = delayType;
            this.function = function;
            ContextStack.peek().addEndEventListener(this::contextEnd);
        }

        @Override
        public MappingType mappingType() {
            MappingType actualType = this.actualType;
            if (actualType == null) {
                if (!this.delayType.isDelay()) {
                    String m = String.format("%s %s isn't prepared.", DelayTypeMeta.class.getName(), this.delayType);
                    throw new IllegalStateException(m);
                }
                actualType = this.function.apply(this.delayType.mappingType());
                this.actualType = actualType;
            }
            return actualType;
        }

        @Override
        public boolean isDelay() {
            return this.delayType.isDelay();
        }

        private void contextEnd() {
            if (this.actualType == null) {
                this.actualType = this.function.apply(this.delayType.mappingType());
            }
        }


    }//DelayTypeWrapper


    private static final class UnaryDelayInferWrapper implements TypeMeta.DelayTypeMeta {

        private final TypeInfer.DelayTypeInfer infer;

        private final UnaryOperator<MappingType> function;

        private MappingType type;

        /**
         * @see #unaryInfer(TypeInfer.DelayTypeInfer, UnaryOperator)
         */
        private UnaryDelayInferWrapper(TypeInfer.DelayTypeInfer infer, UnaryOperator<MappingType> function) {
            this.infer = infer;
            this.function = function;
            ContextStack.peek().addEndEventListener(this::onContextEnd);
        }

        @Override
        public MappingType mappingType() {
            MappingType type = this.type;
            if (type == null) {
                if (this.infer.isDelay()) {
                    throw CriteriaUtils.delayTypeInfer(this.infer);
                }
                final TypeMeta typeMeta;
                typeMeta = this.infer.typeMeta();
                if (typeMeta instanceof MappingType) {
                    type = (MappingType) typeMeta;
                } else {
                    type = typeMeta.mappingType();
                }
                type = this.function.apply(type);
                this.type = type;
            }
            return type;
        }

        @Override
        public boolean isDelay() {
            return this.type == null && this.infer.isDelay();
        }

        private void onContextEnd() {
            if (!this.infer.isDelay()) {
                this.mappingType();
            } else if (ContextStack.isEmpty()) {
                throw CriteriaUtils.delayTypeInfer(this.infer);
            } else {
                //here, possibly recursive reference in WITH RECURSIVE clause
                ContextStack.peek().addEndEventListener(this::onContextEnd);
            }
        }


    }//DelayInferWrapper


    private static final class DualDelayInferWrapper implements TypeMeta.DelayTypeMeta {

        private final TypeInfer leftInfer;

        private final TypeInfer rightInfer;

        private final BinaryOperator<MappingType> function;

        private MappingType type;

        /**
         * @see #dualInfer(TypeInfer, TypeInfer, BinaryOperator)
         */
        private DualDelayInferWrapper(TypeInfer leftInfer, TypeInfer rightInfer,
                                      BinaryOperator<MappingType> function) {
            this.leftInfer = leftInfer;
            this.rightInfer = rightInfer;
            this.function = function;
            ContextStack.peek().addEndEventListener(this::onContextEnd);
        }

        @Override
        public MappingType mappingType() {
            MappingType type = this.type;
            if (type == null) {
                type = this.function.apply(this.leftInfer.typeMeta().mappingType(),
                        this.rightInfer.typeMeta().mappingType());
                this.type = type;
            }
            return type;
        }

        @Override
        public boolean isDelay() {
            final TypeInfer leftInfer = this.leftInfer, rightInfer = this.rightInfer;
            return (leftInfer instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) leftInfer).isDelay())
                    || (rightInfer instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) rightInfer).isDelay());
        }

        private void onContextEnd() {
            MappingType type = this.type;
            if (type != null) {
                return;
            }
            final TypeInfer leftInfer = this.leftInfer, rightInfer = this.rightInfer;
            final boolean leftDelay, rightDelay;

            leftDelay = leftInfer instanceof TypeInfer.DelayTypeInfer
                    && ((TypeInfer.DelayTypeInfer) leftInfer).isDelay();
            rightDelay = rightInfer instanceof TypeInfer.DelayTypeInfer
                    && ((TypeInfer.DelayTypeInfer) rightInfer).isDelay();
            if (!(leftDelay || rightDelay)) {
                this.mappingType();
            } else if (!ContextStack.isEmpty()) {
                ContextStack.peek().addEndEventListener(this::onContextEnd);
            } else if (leftDelay) {
                throw CriteriaUtils.delayTypeInfer((TypeInfer.DelayTypeInfer) leftInfer);
            } else {
                throw CriteriaUtils.delayTypeInfer((TypeInfer.DelayTypeInfer) rightInfer);
            }


        }


    }//DualDelayInferWrapper

    private static final class BiDelayTypeWrapper implements TypeMeta.DelayTypeMeta {

        private final TypeMeta type1;

        private final TypeMeta type2;

        private final BiFunction<MappingType, MappingType, MappingType> function;

        private MappingType actualType;

        /**
         * @see #biDelayWrapper(TypeMeta, TypeMeta, BiFunction)
         */
        private BiDelayTypeWrapper(TypeMeta type1, TypeMeta type2,
                                   BiFunction<MappingType, MappingType, MappingType> function) {
            this.type1 = type1;
            this.type2 = type2;
            this.function = function;
            ContextStack.peek().addEndEventListener(this::contextEnd);
        }

        @Override
        public MappingType mappingType() {
            MappingType actualType = this.actualType;
            if (actualType == null) {
                if (!this.isDelay()) {
                    String m = String.format("%s isn't prepared.", DelayTypeMeta.class.getName());
                    throw new IllegalStateException(m);
                }
                actualType = this.function.apply(this.type1.mappingType(), this.type2.mappingType());
                this.actualType = actualType;
            }
            return actualType;
        }

        @Override
        public boolean isDelay() {
            boolean prepared1 = true, prepared2 = true;
            if (this.type1 instanceof DelayTypeMeta) {
                prepared1 = ((DelayTypeMeta) this.type1).isDelay();
            } else if (this.type2 instanceof DelayTypeMeta) {
                prepared2 = ((DelayTypeMeta) this.type2).isDelay();
            }
            return prepared1 && prepared2;
        }

        private void contextEnd() {
            if (this.actualType == null) {
                this.actualType = this.function.apply(this.type1.mappingType(), this.type2.mappingType());
            }
        }


    }//BiDelayTypeWrapper


    @SuppressWarnings("unchecked")
    private static abstract class UpdateSetClause<F extends SQLField, SR>
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
            this.consumer.accept(SQLs._itemPair(field, null, value));
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
        public final SR setNamed(F field, BiFunction<F, String, Expression> valueOperator) {
            this.consumer.accept(SQLs._itemPair(field, null, valueOperator.apply(field, field.fieldName())));
            return (SR) this;
        }

        @Override
        public final <R extends AssignmentItem> SR setNamed(F field, BiFunction<F, Expression, R> fieldOperator,
                                                            BiFunction<F, String, Expression> valueOperator) {
            return this.onAddAssignmentItemPair(field, fieldOperator.apply(field, valueOperator.apply(field, field.fieldName())));
        }


        @Override
        public final SR setRow(F field1, F field2, Supplier<SubQuery> supplier) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2);
            this.consumer.accept(SQLs._itemPair(fieldList, supplier.get()));
            return (SR) this;
        }

        @Override
        public final SR setRow(F field1, F field2, F field3, Supplier<SubQuery> supplier) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2, field3);
            this.consumer.accept(SQLs._itemPair(fieldList, supplier.get()));
            return (SR) this;
        }

        @Override
        public final SR setRow(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier) {
            final List<F> fieldList;
            fieldList = Arrays.asList(field1, field2, field3, field4);
            this.consumer.accept(SQLs._itemPair(fieldList, supplier.get()));
            return (SR) this;
        }

        @Override
        public final SR setRow(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier) {
            final List<F> fieldList = _Collections.arrayList();
            consumer.accept(fieldList::add);
            this.consumer.accept(SQLs._itemPair(fieldList, supplier.get()));
            return (SR) this;
        }

        @Override
        public final SR ifSetRow(F field1, F field2, Supplier<SubQuery> supplier) {
            final SubQuery query;
            if ((query = supplier.get()) != null) {
                final List<F> fieldList;
                fieldList = Arrays.asList(field1, field2);
                this.consumer.accept(SQLs._itemPair(fieldList, query));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetRow(F field1, F field2, F field3, Supplier<SubQuery> supplier) {
            final SubQuery query;
            if ((query = supplier.get()) != null) {
                final List<F> fieldList;
                fieldList = Arrays.asList(field1, field2, field3);
                this.consumer.accept(SQLs._itemPair(fieldList, query));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetRow(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier) {
            final SubQuery query;
            if ((query = supplier.get()) != null) {
                final List<F> fieldList;
                fieldList = Arrays.asList(field1, field2, field3, field4);
                this.consumer.accept(SQLs._itemPair(fieldList, query));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetRow(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier) {
            final List<F> fieldList = _Collections.arrayList();
            consumer.accept(fieldList::add);
            final SubQuery query;
            if (fieldList.size() > 0 && (query = supplier.get()) != null) {
                this.consumer.accept(SQLs._itemPair(fieldList, query));
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
                pair = SQLs._itemPair(field, null, (Expression) item);
            } else if (item instanceof ItemPair) {
                pair = (ItemPair) item;
            } else {
                throw CriteriaUtils.illegalAssignmentItem(null, item);
            }
            this.consumer.accept(pair);
            return (SR) this;
        }

    }//UpdateSetClause


    private static final class SimpleFieldItemPairs<F extends SQLField>
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

    private static final class ItemPairsImpl<F extends SQLField> extends UpdateSetClause<F, UpdateStatement._ItemPairs<F>>
            implements UpdateStatement._ItemPairs<F> {

        private ItemPairsImpl(Consumer<ItemPair> consumer) {
            super(consumer);
        }


    }//ItemPairsImpl

    private static final class BatchItemPairsImpl<F extends SQLField> extends UpdateSetClause<F, UpdateStatement._BatchItemPairs<F>>
            implements UpdateStatement._BatchItemPairs<F> {

        private BatchItemPairsImpl(Consumer<ItemPair> consumer) {
            super(consumer);
        }


    }//BatchItemPairsImpl

    private static final class RowItemPairsImpl<F extends SQLField> extends UpdateSetClause<F, UpdateStatement._RowPairs<F>>
            implements UpdateStatement._RowPairs<F> {

        private RowItemPairsImpl(Consumer<ItemPair> consumer) {
            super(consumer);
        }

    } //RowItemPairsImpl

    private static final class BatchRowItemPairsImpl<F extends SQLField> extends UpdateSetClause<F, UpdateStatement._BatchRowPairs<F>>
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


    static final class RowExpressionImpl extends OperationRowExpression implements ArmyRowExpression, FunctionArg.SingleFunctionArg {

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
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" ROW(");

            final List<ArmySQLExpression> columnList = this.columnList;
            final int size;
            size = columnList.size();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                columnList.get(i).appendSql(context);
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


    static final class StringObjectSpaceClause implements Statement._StringObjectSpaceClause,
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

    static final class StringObjectConsumer implements Statement._StringObjectConsumer {

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

    static final class StaticObjectConsumer implements Statement._StaticObjectSpaceClause
            , Statement._StaticObjectCommaClause {

        private final boolean required;

        private final Consumer<Object> consumer;

        private Boolean state;

        private StaticObjectConsumer(boolean required, Consumer<Object> consumer) {
            this.required = required;
            this.consumer = consumer;
        }

        @Override
        public Statement._StaticObjectCommaClause space(String keyName, Object value) {
            if (this.state != null) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.state = Boolean.TRUE;
            return this.onAddPair(keyName, value);
        }

        @Override
        public Statement._StaticObjectCommaClause space(Expression key, Object value) {
            if (this.state != null) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.state = Boolean.TRUE;
            return this.onAddPair(key, value);
        }

        @Override
        public Statement._StaticObjectCommaClause comma(String keyName, Object value) {
            return this.onAddPair(keyName, value);
        }

        @Override
        public Statement._StaticObjectCommaClause comma(Expression key, Object value) {
            return this.onAddPair(key, value);
        }


        void endConsumer() {
            if (this.state == null && this.required) {
                throw CriteriaUtils.dontAddAnyItem();
            }
            this.state = Boolean.FALSE;
        }

        private Statement._StaticObjectCommaClause onAddPair(final @Nullable Object key, final Object value) {
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

    static final class DynamicObjectConsumer implements Statement._DynamicObjectConsumer {

        private final boolean required;

        private final Consumer<Object> consumer;

        private Boolean state;

        private DynamicObjectConsumer(boolean required, Consumer<Object> consumer) {
            this.required = required;
            this.consumer = consumer;
        }

        @Override
        public Statement._DynamicObjectConsumer accept(String keyName, Object value) {
            return this.onAddPair(keyName, value);
        }

        @Override
        public Statement._DynamicObjectConsumer accept(Expression key, Object value) {
            return this.onAddPair(key, value);
        }

        void endConsumer() {
            if (this.state == null && this.required) {
                throw CriteriaUtils.dontAddAnyItem();
            }
            this.state = Boolean.FALSE;
        }


        private Statement._DynamicObjectConsumer onAddPair(final @Nullable Object key, final Object value) {
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


}
