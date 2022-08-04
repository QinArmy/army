package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.RowConstructor;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._Expression;
import io.army.lang.Nullable;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class CriteriaSupports {

    CriteriaSupports() {
        throw new UnsupportedOperationException();
    }


    static <C, RR> Statement._LeftParenStringQuadraOptionalSpec<C, RR> stringQuadra(CriteriaContext criteriaContext
            , Function<List<String>, RR> function) {
        return new ParenStringConsumerClause<>(criteriaContext, function);
    }

    static <C, RR> Statement._LeftParenStringQuadraOptionalSpec<C, RR> noActionStringQuadra(RR clause) {
        return new NoActionParenStringConsumerClause<>(clause);
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


}
