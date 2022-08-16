package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.RowConstructor;
import io.army.criteria.SortItem;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._Expression;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

abstract class CriteriaSupports {

    CriteriaSupports() {
        throw new UnsupportedOperationException();
    }


    static <C, RR> Statement._LeftParenStringQuadraOptionalSpec<C, RR> stringQuadra(CriteriaContext criteriaContext
            , Function<List<String>, RR> function) {
        return new ParenStringConsumerClause<>(criteriaContext, function);
    }

    static <C, OR> Statement._OrderByClause<C, OR> orderByClause(CriteriaContext criteriaContext
            , Function<List<ArmySortItem>, OR> function) {
        return new OrderByClause<>(criteriaContext, function);
    }

    static <C, OR> Statement._OrderByClause<C, OR> voidOrderByClause(CriteriaContext criteriaContext
            , Function<List<ArmySortItem>, OR> function) {
        return new OrderByClause<>(function, criteriaContext);
    }

    static ParamMeta delayParamMeta(ParamMeta.Delay paramMeta, Function<MappingType, MappingType> function) {
        return new DelayParamMetaWrapper(paramMeta, function);
    }

    static ParamMeta delayParamMeta(ParamMeta paramMeta1, ParamMeta paramMeta2
            , BiFunction<MappingType, MappingType, MappingType> function) {
        return new BiDelayParamMetaWrapper(paramMeta1, paramMeta2, function);
    }


    static final class RowConstructorImpl implements RowConstructor {

        final CriteriaContext criteriaContext;

        private List<List<_Expression>> rowList;

        private List<_Expression> columnList;

        RowConstructorImpl(CriteriaContext criteriaContext) {
            this.criteriaContext = criteriaContext;
        }

        @Override
        public RowConstructor add(final Object value) {
            return this.addColumn(CriteriaUtils.constantLiteral(this.criteriaContext, value));
        }


        @Override
        public RowConstructor row() {
            final List<_Expression> columnList = this.columnList;
            List<List<_Expression>> rowList = this.rowList;

            final int firstColumnSize;
            if (columnList == null) {
                if (rowList != null) {
                    throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
                }
                firstColumnSize = 0;
            } else if (columnList.size() == 0) {
                String m = "You don't add any column.";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else if (rowList == null) {
                rowList = new ArrayList<>();
                this.rowList = rowList;
                firstColumnSize = 0;
            } else if (!(rowList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            } else if (columnList.size() != (firstColumnSize = rowList.get(0).size())) {
                throw _Exceptions.valuesColumnSizeNotMatch(firstColumnSize, rowList.size(), columnList.size());
            }

            if (columnList != null) {
                rowList.add(_CollectionUtils.unmodifiableList(columnList));
            }

            if (firstColumnSize == 0) {
                this.columnList = new ArrayList<>();
            } else {
                this.columnList = new ArrayList<>(firstColumnSize);
            }
            return this;
        }

        List<List<_Expression>> endConstructor() {
            final List<_Expression> columnList = this.columnList;
            List<List<_Expression>> rowList = this.rowList;

            if (columnList == null) {
                String m = "You don't add any row.";
                if (rowList == null) {
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
                } else {
                    throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
                }
            } else if (!(columnList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }

            if (rowList == null) {
                rowList = Collections.singletonList(_CollectionUtils.unmodifiableList(columnList));
                this.rowList = rowList;
            } else if (rowList instanceof ArrayList) {
                rowList.add(_CollectionUtils.unmodifiableList(columnList));
                rowList = _CollectionUtils.unmodifiableList(rowList);
                this.rowList = rowList;
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.columnList = null;
            return rowList;
        }

        private RowConstructor addColumn(final @Nullable Expression value) {
            final List<_Expression> columnList = this.columnList;
            if (columnList == null) {
                String m = "Not found any row,please use row() method create new row.";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            if (value instanceof ParamExpression) {
                throw CriteriaContextStack.criteriaError(criteriaContext, _Exceptions::valuesStatementDontSupportParam);
            }
            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            columnList.add((ArmyExpression) value);
            return this;
        }

    }//RowConstructorImpl


    static class ParenStringConsumerClause<C, RR>
            implements Statement._LeftParenStringQuadraOptionalSpec<C, RR>
            , Statement._LeftParenStringDualOptionalSpec<C, RR>
            , Statement._CommaStringDualSpec<RR>
            , Statement._CommaStringQuadraSpec<RR> {

        final CriteriaContext criteriaContext;

        private final Function<List<String>, RR> function;

        private List<String> stringList;

        private boolean optionalClause;


        /**
         * <p>
         * private constructor for {@link  #stringQuadra(CriteriaContext, Function)}
         * </p>
         */
        private ParenStringConsumerClause(CriteriaContext criteriaContext, Function<List<String>, RR> function) {
            this.criteriaContext = criteriaContext;
            this.function = function;
        }

        /**
         * <p>
         * package constructor for sub class
         * </p>
         */
        ParenStringConsumerClause(CriteriaContext criteriaContext) {
            assert this.getClass() != ParenStringConsumerClause.class;
            this.criteriaContext = criteriaContext;
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
        public final Statement._RightParenClause<RR> leftParen(BiConsumer<C, Consumer<String>> consumer) {
            this.optionalClause = false;
            consumer.accept(this.criteriaContext.criteria(), this::comma);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParenIf(Consumer<Consumer<String>> consumer) {
            this.optionalClause = true;
            consumer.accept(this::comma);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParenIf(BiConsumer<C, Consumer<String>> consumer) {
            this.optionalClause = true;
            consumer.accept(this.criteriaContext.criteria(), this::comma);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> comma(String string) {
            List<String> stringList = this.stringList;
            if (stringList == null) {
                stringList = new ArrayList<>();
                this.stringList = stringList;
            } else if (!(stringList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
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
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            } else if (this.optionalClause) {
                stringList = Collections.emptyList();
            } else {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "You don't add any string item");
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

    static class NoActionParenStringConsumerClause<C, RR>
            implements Statement._LeftParenStringQuadraOptionalSpec<C, RR>
            , Statement._LeftParenStringDualOptionalSpec<C, RR>
            , Statement._CommaStringDualSpec<RR>
            , Statement._CommaStringQuadraSpec<RR> {

        private final RR clause;

        NoActionParenStringConsumerClause(RR clause) {
            this.clause = clause;
        }


        @Override
        public final Statement._RightParenClause<RR> leftParen(String string) {
            //no-op
            return this;
        }

        @Override
        public final Statement._CommaStringDualSpec<RR> leftParen(String string1, String string2) {
            //no-op
            return this;
        }

        @Override
        public final Statement._CommaStringQuadraSpec<RR> leftParen(String string1, String string2, String string3, String string4) {
            //no-op
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(Consumer<Consumer<String>> consumer) {
            //no-op
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(BiConsumer<C, Consumer<String>> consumer) {
            //no-op
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParenIf(Consumer<Consumer<String>> consumer) {
            //no-op
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParenIf(BiConsumer<C, Consumer<String>> consumer) {
            //no-op
            return this;
        }


        @Override
        public final Statement._RightParenClause<RR> comma(String string) {
            //no-op
            return this;
        }


        @Override
        public final Statement._CommaStringDualSpec<RR> comma(String string1, String string2) {
            //no-op
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> comma(String string1, String string2, String string3) {
            //no-op
            return this;
        }

        @Override
        public final Statement._CommaStringQuadraSpec<RR> comma(String string1, String string2, String string3, String string4) {
            //no-op
            return this;
        }


        @Override
        public final RR rightParen() {
            return this.clause;
        }


    }//NoActionParenStringConsumerClause


    private static final class OrderByClause<C, OR> implements Statement._OrderByClause<C, OR> {

        private final CriteriaContext criteriaContext;

        private final C criteria;

        private final Function<List<ArmySortItem>, OR> function;

        private List<ArmySortItem> orderByList;

        /**
         * @see #orderByClause(CriteriaContext, Function)
         */
        private OrderByClause(CriteriaContext criteriaContext, Function<List<ArmySortItem>, OR> function) {
            this.criteriaContext = criteriaContext;
            this.criteria = criteriaContext.criteria();
            this.function = function;
        }

        /**
         * @see #voidOrderByClause(CriteriaContext, Function)
         */
        private OrderByClause(Function<List<ArmySortItem>, OR> function, CriteriaContext criteriaContext) {
            this.criteria = null;
            this.criteriaContext = criteriaContext;
            this.function = function;
        }

        @Override
        public OR orderBy(SortItem sortItem) {
            return this.function.apply(Collections.singletonList((ArmySortItem) sortItem));
        }

        @Override
        public OR orderBy(SortItem sortItem1, SortItem sortItem2) {
            final List<ArmySortItem> itemList;
            itemList = ArrayUtils.asUnmodifiableList(
                    (ArmySortItem) sortItem1,
                    (ArmySortItem) sortItem2
            );
            return this.function.apply(itemList);
        }

        @Override
        public OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
            final List<ArmySortItem> itemList;
            itemList = ArrayUtils.asUnmodifiableList(
                    (ArmySortItem) sortItem1,
                    (ArmySortItem) sortItem2,
                    (ArmySortItem) sortItem3
            );
            return this.function.apply(itemList);
        }

        @Override
        public OR orderBy(Consumer<Consumer<SortItem>> consumer) {
            consumer.accept(this::addOrderByItem);
            return this.endOrderByClause(true);
        }

        @Override
        public OR orderBy(BiConsumer<C, Consumer<SortItem>> consumer) {
            consumer.accept(this.criteria, this::addOrderByItem);
            return this.endOrderByClause(true);
        }

        @Override
        public OR ifOrderBy(Function<Object, ? extends SortItem> operator, Supplier<?> operand) {
            final List<ArmySortItem> itemList;
            final Object value;
            if ((value = operand.get()) == null) {
                itemList = Collections.emptyList();
            } else {
                itemList = Collections.singletonList((ArmySortItem) operator.apply(value));
            }
            return this.function.apply(itemList);
        }

        @Override
        public OR ifOrderBy(Function<Object, ? extends SortItem> operator, Function<String, ?> operand, String operandKey) {
            final List<ArmySortItem> itemList;
            final Object value;
            if ((value = operand.apply(operandKey)) == null) {
                itemList = Collections.emptyList();
            } else {
                itemList = Collections.singletonList((ArmySortItem) operator.apply(value));
            }
            return this.function.apply(itemList);
        }

        @Override
        public OR ifOrderBy(BiFunction<Object, Object, ? extends SortItem> operator, Supplier<?> firstOperand, Supplier<?> secondOperand) {
            final List<ArmySortItem> itemList;
            final Object firstValue, secondValue;
            if ((firstValue = firstOperand.get()) != null && (secondValue = secondOperand.get()) != null) {
                itemList = Collections.singletonList((ArmySortItem) operator.apply(firstValue, secondValue));
            } else {
                itemList = Collections.emptyList();
            }
            return this.function.apply(itemList);
        }

        @Override
        public OR ifOrderBy(BiFunction<Object, Object, ? extends SortItem> operator, Function<String, ?> operand, String firstKey, String secondKey) {
            final List<ArmySortItem> itemList;
            final Object firstValue, secondValue;
            if ((firstValue = operand.apply(firstKey)) != null && (secondValue = operand.apply(secondKey)) != null) {
                itemList = Collections.singletonList((ArmySortItem) operator.apply(firstValue, secondValue));
            } else {
                itemList = Collections.emptyList();
            }
            return this.function.apply(itemList);
        }

        @Override
        public OR ifOrderBy(Consumer<Consumer<SortItem>> consumer) {
            consumer.accept(this::addOrderByItem);
            return this.endOrderByClause(false);
        }

        @Override
        public OR ifOrderBy(BiConsumer<C, Consumer<SortItem>> consumer) {
            consumer.accept(this.criteria, this::addOrderByItem);
            return this.endOrderByClause(false);
        }

        private void addOrderByItem(@Nullable SortItem sortItem) {
            if (sortItem == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            List<ArmySortItem> itemList = this.orderByList;
            if (itemList == null) {
                this.orderByList = itemList = new ArrayList<>();
            } else if (!(itemList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            itemList.add((ArmySortItem) sortItem);
        }

        private OR endOrderByClause(final boolean required) {
            List<ArmySortItem> itemList = this.orderByList;
            if (itemList == null) {
                if (required) {
                    throw CriteriaUtils.orderByIsEmpty(this.criteriaContext);
                }
                itemList = Collections.emptyList();
            } else if (itemList instanceof ArrayList) {
                itemList = _CollectionUtils.unmodifiableList(itemList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.orderByList = null;
            return this.function.apply(itemList);
        }


    }//OrderByClause


    private static final class DelayParamMetaWrapper implements ParamMeta.Delay {

        private final Delay paramMeta;

        private final Function<MappingType, MappingType> function;

        private DelayParamMetaWrapper(Delay paramMeta, Function<MappingType, MappingType> function) {
            this.paramMeta = paramMeta;
            this.function = function;
        }

        @Override
        public MappingType mappingType() {
            return this.function.apply(this.paramMeta.mappingType());
        }

        @Override
        public boolean isPrepared() {
            return this.paramMeta.isPrepared();
        }


    }//DelayParamMetaWrapper

    private static final class BiDelayParamMetaWrapper implements ParamMeta.Delay {

        private final ParamMeta paramMeta1;

        private final ParamMeta paramMeta2;

        private final BiFunction<MappingType, MappingType, MappingType> function;

        private BiDelayParamMetaWrapper(ParamMeta paramMeta1, ParamMeta paramMeta2
                , BiFunction<MappingType, MappingType, MappingType> function) {
            this.paramMeta1 = paramMeta1;
            this.paramMeta2 = paramMeta2;
            this.function = function;
        }

        @Override
        public MappingType mappingType() {
            return this.function.apply(this.paramMeta1.mappingType(), this.paramMeta2.mappingType());
        }

        @Override
        public boolean isPrepared() {
            boolean prepared1 = true, prepared2 = true;
            if (this.paramMeta1 instanceof ParamMeta.Delay) {
                prepared1 = ((Delay) this.paramMeta1).isPrepared();
            } else if (this.paramMeta2 instanceof ParamMeta.Delay) {
                prepared2 = ((Delay) this.paramMeta2).isPrepared();
            }
            return prepared1 && prepared2;
        }


    }//BiDelayParamMetaWrapper


}
