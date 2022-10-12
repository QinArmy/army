package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.RowConstructor;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._Expression;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TypeMeta;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
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

    static <OR> Statement._OrderByClause<OR> orderByClause(CriteriaContext criteriaContext
            , Function<List<ArmySortItem>, OR> function) {
        throw new UnsupportedOperationException();
    }

    static <OR> Statement._OrderByClause<OR> voidOrderByClause(CriteriaContext criteriaContext
            , Function<List<ArmySortItem>, OR> function) {
        throw new UnsupportedOperationException();
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
                    throw ContextStack.castCriteriaApi(this.criteriaContext);
                }
                firstColumnSize = 0;
            } else if (columnList.size() == 0) {
                String m = "You don't add any column.";
                throw ContextStack.criteriaError(this.criteriaContext, m);
            } else if (rowList == null) {
                rowList = new ArrayList<>();
                this.rowList = rowList;
                firstColumnSize = 0;
            } else if (!(rowList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
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
                    throw ContextStack.criteriaError(this.criteriaContext, m);
                } else {
                    throw ContextStack.castCriteriaApi(this.criteriaContext);
                }
            } else if (!(columnList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }

            if (rowList == null) {
                rowList = Collections.singletonList(_CollectionUtils.unmodifiableList(columnList));
                this.rowList = rowList;
            } else if (rowList instanceof ArrayList) {
                rowList.add(_CollectionUtils.unmodifiableList(columnList));
                rowList = _CollectionUtils.unmodifiableList(rowList);
                this.rowList = rowList;
            } else {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.columnList = null;
            return rowList;
        }

        private RowConstructor addColumn(final @Nullable Expression value) {
            final List<_Expression> columnList = this.columnList;
            if (columnList == null) {
                String m = "Not found any row,please use row() method create new row.";
                throw ContextStack.criteriaError(this.criteriaContext, m);
            }
            if (value instanceof ParamExpression) {
                throw ContextStack.criteriaError(criteriaContext, _Exceptions::valuesStatementDontSupportParam);
            }
            if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.criteriaContext);
            }
            columnList.add((ArmyExpression) value);
            return this;
        }

    }//RowConstructorImpl


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


    interface CteBuilderSpec {

        void endWithClause(boolean required);
    }


}
