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
import io.army.criteria.standard.SQLFunction;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.function.BetweenOperator;
import io.army.function.BetweenValueOperator;
import io.army.function.ExpressionOperator;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.mapping.TextType;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.*;

abstract class LiteralFunctions {

    LiteralFunctions() {
        throw new UnsupportedOperationException();
    }

    static SimpleExpression zeroArgFunc(String name, TypeMeta returnType) {
        return new ZeroArgFunc(name, true, returnType);
    }

    static SimpleExpression myZeroArgFunc(String name, TypeMeta returnType) {
        return new ZeroArgFunc(name, false, returnType);
    }

    static SimpleExpression oneArgFunc(String name, @Nullable Object arg, TypeMeta returnType) {
        return new OneArgFunc(name, true, arg, returnType);
    }

    static SimpleExpression myOneArgFunc(String name, @Nullable Object arg, TypeMeta returnType) {
        return new OneArgFunc(name, false, arg, returnType);
    }

    static SimpleExpression twoArgFunc(String name, @Nullable Object one, @Nullable Object two, TypeMeta returnType) {
        return new TwoArgFunc(name, true, one, two, returnType);
    }

    static SimpleExpression myTwoArgFunc(String name, @Nullable Object one, @Nullable Object two, TypeMeta returnType) {
        return new TwoArgFunc(name, false, one, two, returnType);
    }

    static SimpleExpression threeArgFunc(String name, @Nullable Object one, @Nullable Object two,
                                         @Nullable Object three, TypeMeta returnType) {
        return new ThreeArgFunc(name, true, one, two, three, returnType);
    }

    static SimpleExpression myThreeArgFunc(String name, @Nullable Object one, @Nullable Object two,
                                           @Nullable Object three, TypeMeta returnType) {
        return new ThreeArgFunc(name, false, one, two, three, returnType);
    }

    static SimpleExpression fourArgFunc(String name, @Nullable Object one, @Nullable Object two,
                                        @Nullable Object three, @Nullable Object four, TypeMeta returnType) {
        final List<Object> argList = _Collections.arrayList(4);

        argList.add(one);
        argList.add(two);
        argList.add(three);
        argList.add(four);
        return new MultiArgFunc(name, true, argList, returnType);
    }

    static SimpleExpression fiveArgFunc(String name, @Nullable Object one, @Nullable Object two,
                                        @Nullable Object three, @Nullable Object four, @Nullable Object five,
                                        TypeMeta returnType) {
        final List<Object> argList = _Collections.arrayList(5);

        argList.add(one);
        argList.add(two);
        argList.add(three);
        argList.add(four);

        argList.add(five);
        return new MultiArgFunc(name, true, argList, returnType);
    }


    static SimpleExpression multiArgFunc(String name, List<?> argList, TypeMeta returnType) {
        return new MultiArgFunc(name, true, argList, returnType);
    }

    static SimpleExpression myMultiArgFunc(String name, List<?> argList, TypeMeta returnType) {
        return new MultiArgFunc(name, false, argList, returnType);
    }

    static SimpleExpression compositeFunc(String name, List<?> argList, TypeMeta returnType) {
        return new CompositeFunc(name, argList, returnType);
    }

    static SimpleExpression noParensFunc(String name, TypeMeta returnType) {
        return new NoParensFunctionExpression(name, returnType);
    }


    public static SimpleExpression jsonMapFunc(String name, Map<String, ?> map, TypeMeta returnType) {
        return new JsonMapFunc(name, map, returnType);
    }


    /*-------------------below predicate function methods -------------------*/

    static SimplePredicate zeroArgPredicate(String name) {
        return new ZeroArgPredicate(name, true);
    }

    static SimplePredicate myZeroArgPredicate(String name) {
        return new ZeroArgPredicate(name, false);
    }

    static SimplePredicate oneArgPredicate(String name, @Nullable Object one) {
        return new OneArgPredicate(name, true, one);
    }

    static SimplePredicate myOneArgPredicate(String name, @Nullable Object one) {
        return new OneArgPredicate(name, false, one);
    }

    static SimplePredicate twoArgPredicate(String name, @Nullable Object one, @Nullable Object two) {
        return new TwoArgPredicate(name, true, one, two);
    }

    static SimplePredicate myTwoArgPredicate(String name, @Nullable Object one, @Nullable Object two) {
        return new TwoArgPredicate(name, false, one, two);
    }

    static SimplePredicate threeArgPredicate(String name, @Nullable Object one, @Nullable Object two, @Nullable Object three) {
        return new ThreeArgPredicate(name, true, one, two, three);
    }

    static SimplePredicate myThreeArgPredicate(String name, @Nullable Object one, @Nullable Object two, @Nullable Object three) {
        return new ThreeArgPredicate(name, false, one, two, three);
    }

    static SimplePredicate multiArgPredicate(String name, List<?> argList) {
        return new MultiArgPredicate(name, true, argList);
    }

    static SimplePredicate myMultiArgPredicate(String name, List<?> argList) {
        return new MultiArgPredicate(name, false, argList);
    }

    static SQLFunction._CaseFuncWhenClause caseFunc(final @Nullable Object caseValue) {
        return new CaseFunction(caseValue);
    }


    private static final class ZeroArgFunc extends OperationExpression.SqlFunctionExpression
            implements FunctionUtils.NoArgFunction {

        private ZeroArgFunc(String name, boolean buildIn, TypeMeta returnType) {
            super(name, buildIn, returnType);
        }

        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            // no-op
        }

        @Override
        void argToString(StringBuilder builder) {
            // no-op
        }


    } // ZeroArgFunc


    private static final class OneArgFunc extends OperationExpression.SqlFunctionExpression {

        private final Object arg;

        /**
         * @see #oneArgFunc(String, Object, TypeMeta)
         * @see #myOneArgFunc(String, Object, TypeMeta)
         */
        private OneArgFunc(String name, boolean buildIn, @Nullable Object arg, TypeMeta returnType) {
            super(name, buildIn, returnType);
            this.arg = arg;
        }


        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            FuncExpUtils.appendLiteral(this.name, this.arg, sqlBuilder, context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.arg);
        }


    } // OneArgFunc

    private static final class TwoArgFunc extends OperationExpression.SqlFunctionExpression {

        private final Object one;

        private final Object two;

        private TwoArgFunc(String name, boolean buildIn, @Nullable Object one, @Nullable Object two, TypeMeta returnType) {
            super(name, buildIn, returnType);
            this.one = one;
            this.two = two;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, _SqlContext context) {
            FuncExpUtils.appendLiteral(this.name, this.one, sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            FuncExpUtils.appendLiteral(this.name, this.two, sqlBuilder, context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two);
        }


    } // TwoArgFunc

    private static final class ThreeArgFunc extends OperationExpression.SqlFunctionExpression {

        private final Object one;

        private final Object two;

        private final Object three;

        private ThreeArgFunc(String name, boolean buildIn, @Nullable Object one, @Nullable Object two,
                             @Nullable Object three, TypeMeta returnType) {
            super(name, buildIn, returnType);
            this.one = one;
            this.two = two;
            this.three = three;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, _SqlContext context) {
            FuncExpUtils.appendLiteral(this.name, this.one, sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_COMMA);
            FuncExpUtils.appendLiteral(this.name, this.two, sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_COMMA);
            FuncExpUtils.appendLiteral(this.name, this.three, sqlBuilder, context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three);
        }


    } // TwoArgFunc

    private static final class MultiArgFunc extends OperationExpression.SqlFunctionExpression {

        private final List<?> argList;

        private MultiArgFunc(String name, boolean buildIn, List<?> argList, TypeMeta returnType) {
            super(name, buildIn, returnType);
            this.argList = argList;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            FuncExpUtils.appendLiteralList(this.name, this.argList, sqlBuilder, context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            FuncExpUtils.literalListToString(this.argList, builder);

        }


    } // MultiArgFunc


    private static final class CompositeFunc extends OperationExpression.SqlFunctionExpression {

        private final List<?> argList;

        private CompositeFunc(String name, List<?> argList, TypeMeta returnType) {
            super(name, returnType);
            this.argList = argList;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            FuncExpUtils.appendCompositeList(this.name, this.argList, sqlBuilder, context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            FuncExpUtils.compositeListToString(this.argList, builder);
        }


    } // CompositeFunc




    /*-------------------below predicate functions -------------------*/

    private static final class ZeroArgPredicate extends OperationPredicate.SqlFunctionPredicate
            implements FunctionUtils.NoArgFunction {

        private ZeroArgPredicate(String name, boolean buildIn) {
            super(name, buildIn);
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, _SqlContext context) {
            //no-op
        }

        @Override
        void argToString(final StringBuilder builder) {
            //no-op
        }


    } // ZeroArgPredicate


    private static final class OneArgPredicate extends OperationPredicate.SqlFunctionPredicate {

        private final Object one;

        private OneArgPredicate(String name, boolean buildIn, @Nullable Object one) {
            super(name, buildIn);
            this.one = one;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, _SqlContext context) {
            FuncExpUtils.appendLiteral(this.name, this.one, sqlBuilder, context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one);
        }


    } // OneArgPredicate

    private static final class TwoArgPredicate extends OperationPredicate.SqlFunctionPredicate {

        private final Object one;

        private final Object two;

        private TwoArgPredicate(String name, boolean buildIn, @Nullable Object one, @Nullable Object two) {
            super(name, buildIn);
            this.one = one;
            this.two = two;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, _SqlContext context) {
            FuncExpUtils.appendLiteral(this.name, this.one, sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            FuncExpUtils.appendLiteral(this.name, this.two, sqlBuilder, context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two);
        }


    } // TwoArgPredicate


    private static final class ThreeArgPredicate extends OperationPredicate.SqlFunctionPredicate {

        private final Object one;

        private final Object two;

        private final Object three;

        private ThreeArgPredicate(String name, boolean buildIn, @Nullable Object one, @Nullable Object two,
                                  @Nullable Object three) {
            super(name, buildIn);
            this.one = one;
            this.two = two;
            this.three = three;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, _SqlContext context) {
            FuncExpUtils.appendLiteral(this.name, this.one, sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_COMMA);
            FuncExpUtils.appendLiteral(this.name, this.two, sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_COMMA);
            FuncExpUtils.appendLiteral(this.name, this.three, sqlBuilder, context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three);
        }


    } // TwoArgPredicate


    private static final class MultiArgPredicate extends OperationPredicate.SqlFunctionPredicate {

        private final List<?> argList;

        private MultiArgPredicate(String name, boolean buildIn, List<?> argList) {
            super(name, buildIn);
            this.argList = argList;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            FuncExpUtils.appendLiteralList(this.name, this.argList, sqlBuilder, context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            FuncExpUtils.literalListToString(this.argList, builder);

        }


    } // MultiArgPredicate


    /**
     * only accept {@link Expression} not {@link RowExpression} ,for example : MySQL
     */
    private static final class JsonMapFunc extends OperationExpression.SqlFunctionExpression {

        private final Map<String, ?> map;

        /**
         * @see #jsonMapFunc(String, Map, TypeMeta)
         */
        private JsonMapFunc(String name, Map<String, ?> map, TypeMeta returnType) {
            super(name, returnType);
            this.map = Collections.unmodifiableMap(_Collections.hashMap(map));
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            int count = 0;
            for (Map.Entry<String, ?> entry : this.map.entrySet()) {
                if (count > 0) {
                    sqlBuilder.append(_Constant.COMMA);
                }
                context.appendLiteral(StringType.INSTANCE, entry.getKey());
                sqlBuilder.append(_Constant.COMMA);
                FuncExpUtils.appendLiteral(this.name, entry.getValue(), sqlBuilder, context);

                count++;

            } // for loop
        }

        @Override
        void argToString(final StringBuilder builder) {
            int count = 0;
            for (Map.Entry<String, ?> entry : this.map.entrySet()) {
                if (count > 0) {
                    builder.append(_Constant.COMMA);
                }
                builder.append(entry.getKey())
                        .append(_Constant.COMMA)
                        .append(entry.getValue());

                count++;

            } // for loop

        }


    } // JsonMapFunc


    private static final class CaseFunction extends OperationExpression.OperationSimpleExpression
            implements SQLFunction._CaseWhenSpec,
            SQLFunction._CaseFuncWhenClause,
            SQLFunction._StaticCaseThenClause,
            SQLFunction._CaseElseClause,
            CaseWhens,
            SQLFunction._DynamicCaseThenClause,
            SQLFunction._DynamicWhenSpaceClause,
            ArmySQLFunction {

        private final Object caseValue;

        private final CriteriaContext outerContext;

        private List<_Pair<Object, Object>> expPairList;

        private Object whenExpression;

        private Object elseExpression;

        private TypeMeta returnType;

        private boolean dynamicWhenSpace;

        private CaseFunction(@Nullable Object caseValue) {
            this.caseValue = caseValue;
            this.outerContext = ContextStack.peek();
        }

        @Override
        public String name() {
            return "CASE";
        }


        @Override
        public MappingType typeMeta() {
            final TypeMeta returnType = this.returnType;
            final MappingType t;
            if (returnType instanceof MappingType) {
                t = (MappingType) returnType;
            } else {
                t = returnType.mappingType();
            }
            return t;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            final int pairSize;
            final List<_Pair<Object, Object>> expPairList = this.expPairList;
            if (expPairList == null || (pairSize = expPairList.size()) == 0) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }

            final String funcName = "CASE";

            context.appendFuncName(true, funcName);

            final Object caseValue = this.caseValue;
            if (caseValue != null) {
                FuncExpUtils.appendLiteral(funcName, caseValue, sqlBuilder, context);
            }
            _Pair<Object, Object> pair;
            for (int i = 0; i < pairSize; i++) {
                pair = expPairList.get(i);

                sqlBuilder.append(" WHEN");
                FuncExpUtils.appendLiteral(funcName, pair.first, sqlBuilder, context);
                sqlBuilder.append(" THEN");
                FuncExpUtils.appendLiteral(funcName, pair.second, sqlBuilder, context);

            }

            final Object elseExpression = this.elseExpression;
            if (elseExpression != null) {
                sqlBuilder.append(" ELSE");
                FuncExpUtils.appendLiteral(funcName, elseExpression, sqlBuilder, context);
            }

            sqlBuilder.append(" END");

        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            final int pairSize;
            final List<_Pair<Object, Object>> expPairList = this.expPairList;
            if (expPairList == null || (pairSize = expPairList.size()) == 0) {
                return super.toString();
            }
            builder.append(" CASE");

            final Object caseValue = this.caseValue;
            if (caseValue != null) {
                builder.append(caseValue);
            }
            _Pair<Object, Object> pair;
            for (int i = 0; i < pairSize; i++) {
                pair = expPairList.get(i);

                builder.append(" WHEN")
                        .append(pair.first)
                        .append(" THEN")
                        .append(pair.second);

            }

            final Object elseExpression = this.elseExpression;
            if (elseExpression != null) {
                builder.append(" ELSE")
                        .append(elseExpression);
            }
            return builder.append(" END")
                    .toString();
        }

        @Override
        public _CaseElseClause whens(Consumer<CaseWhens> consumer) {
            consumer.accept(this);
            return this;
        }

        @Override
        public CaseFunction when(final @Nullable Object expression) {
            if (this.whenExpression != null) {
                throw ContextStack.criteriaError(this.outerContext, "last when clause not end.");
            } else if (expression == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            this.whenExpression = expression;
            return this;
        }

        @Override
        public CaseFunction when(UnaryOperator<IPredicate> valueOperator, IPredicate predicate) {
            return this.when(valueOperator.apply(predicate));
        }

        @Override
        public CaseFunction when(Function<Expression, Expression> valueOperator, Expression expression) {
            return this.when(valueOperator.apply(expression));
        }

        @Override
        public <T> CaseFunction when(Function<T, Expression> valueOperator, Supplier<T> getter) {
            return when(valueOperator.apply(getter.get()));
        }

        @Override
        public <T> CaseFunction when(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> valueOperator, T value) {
            return this.when(expOperator.apply(valueOperator, value));
        }

        @Override
        public <T> CaseFunction when(BetweenValueOperator<T> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> operator, T firstValue,
                                     SQLs.WordAnd and, T secondValue) {
            return this.when(expOperator.apply(operator, firstValue, and, secondValue));
        }

        @Override
        public CaseFunction when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second) {
            return this.when(expOperator.apply(first, and, second));
        }

        @Override
        public CaseFunction ifWhen(Consumer<_DynamicWhenSpaceClause> consumer) {
            this.dynamicWhenSpace = true;
            consumer.accept(this);
            this.dynamicWhenSpace = false;
            return this;
        }

        @Override
        public _SqlCaseThenClause space(Object expression) {
            if (!this.dynamicWhenSpace) {
                throw ContextStack.criteriaError(this.outerContext, "duplication ifWhen space.");
            }
            this.dynamicWhenSpace = false;
            return this.when(expression);
        }

        @Override
        public _SqlCaseThenClause space(UnaryOperator<IPredicate> valueOperator, IPredicate predicate) {
            return this.space(valueOperator.apply(predicate));
        }

        @Override
        public _SqlCaseThenClause space(Function<Expression, Expression> valueOperator, Expression expression) {
            return this.space(valueOperator.apply(expression));
        }


        @Override
        public <T> _SqlCaseThenClause space(Function<T, Expression> valueOperator, Supplier<T> getter) {
            return this.space(valueOperator.apply(getter.get()));
        }

        @Override
        public <T> _SqlCaseThenClause space(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                            BiFunction<SimpleExpression, T, Expression> valueOperator, T value) {
            return this.space(expOperator.apply(valueOperator, value));
        }

        @Override
        public <T> _SqlCaseThenClause space(BetweenValueOperator<T> expOperator,
                                            BiFunction<SimpleExpression, T, Expression> operator, T firstValue,
                                            SQLs.WordAnd and, T secondValue) {
            return this.space(expOperator.apply(operator, firstValue, and, secondValue));
        }

        @Override
        public _SqlCaseThenClause space(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second) {
            return this.space(expOperator.apply(first, and, second));
        }

        @Override
        public CaseFunction then(final @Nullable Object expression) {
            final Object whenExp = this.whenExpression;
            if (whenExp == null) {
                throw ContextStack.criteriaError(this.outerContext, "no when clause");
            } else if (expression == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            List<_Pair<Object, Object>> pairList = this.expPairList;
            if (pairList == null) {
                this.expPairList = pairList = _Collections.arrayList();
            } else if (!(pairList instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            pairList.add(_Pair.create(whenExp, expression));
            this.whenExpression = null; // clear for next
            return this;
        }

        @Override
        public CaseFunction then(UnaryOperator<IPredicate> valueOperator, IPredicate value) {
            return this.then(valueOperator.apply(value));
        }

        @Override
        public CaseFunction then(Function<Expression, Expression> valueOperator, Expression value) {
            return this.then(valueOperator.apply(value));
        }

        @Override
        public <T> CaseFunction then(Function<T, Expression> valueOperator, Supplier<T> supplier) {
            return this.then(valueOperator.apply(supplier.get()));
        }

        @Override
        public <T> CaseFunction then(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> valueOperator, T value) {
            return this.then(expOperator.apply(valueOperator, value));
        }


        @Override
        public _CaseEndClause elseValue(final @Nullable Object expression) {
            if (this.expPairList == null) {
                throw noWhenClause();
            } else if (this.whenExpression != null) {
                throw lastWhenClauseNotEnd();
            } else if (this.elseExpression != null) {
                throw ContextStack.criteriaError(this.outerContext, "duplicate else clause.");
            } else if (expression == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            this.elseExpression = expression;
            return this;
        }


        @Override
        public _CaseEndClause elseValue(UnaryOperator<IPredicate> valueOperator, IPredicate value) {
            return this.elseValue(valueOperator.apply(value));
        }

        @Override
        public _CaseEndClause elseValue(Function<Expression, Expression> valueOperator, Expression value) {
            return this.elseValue(valueOperator.apply(value));
        }


        @Override
        public <T> _CaseEndClause elseValue(Function<T, Expression> valueOperator, Supplier<T> supplier) {
            return this.elseValue(valueOperator.apply(supplier.get()));
        }

        @Override
        public <T> _CaseEndClause elseValue(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                            BiFunction<SimpleExpression, T, Expression> valueOperator, T value) {
            return this.elseValue(expOperator.apply(valueOperator, value));
        }

        @Override
        public _CaseEndClause ifElse(Supplier<?> supplier) {
            final Object expression;
            expression = supplier.get();
            if (expression != null) {
                this.elseValue(expression);
            }
            return this;
        }

        @Override
        public <T> _CaseEndClause ifElse(Function<T, Expression> valueOperator, Supplier<T> getter) {
            final T operand;
            operand = getter.get();
            if (operand != null) {
                this.elseValue(valueOperator.apply(operand));
            }
            return this;
        }

        @Override
        public <K, V> _CaseEndClause ifElse(Function<V, Expression> valueOperator, Function<K, V> function, K key) {
            final V value;
            if ((value = function.apply(key)) != null) {
                this.elseValue(valueOperator.apply(value));
            }
            return this;
        }

        @Override
        public <T> _CaseEndClause ifElse(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                         BiFunction<SimpleExpression, T, Expression> valueOperator, Supplier<T> getter) {
            final T value;
            if ((value = getter.get()) != null) {
                this.elseValue(expOperator.apply(valueOperator, value));
            }
            return this;
        }

        @Override
        public <K, V> _CaseEndClause ifElse(ExpressionOperator<SimpleExpression, V, Expression> expOperator,
                                            BiFunction<SimpleExpression, V, Expression> valueOperator,
                                            Function<K, V> function, K key) {
            final V value;
            if ((value = function.apply(key)) != null) {
                this.elseValue(expOperator.apply(valueOperator, value));
            }
            return this;
        }

        @Override
        public Expression end() {
            return this.endCaseFunction(TextType.INSTANCE);
        }


        @Override
        public Expression end(final @Nullable TypeInfer type) {
            if (type == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            MappingType resultType;
            if (type instanceof MappingType) {
                resultType = (MappingType) type;
            } else {
                resultType = type.typeMeta().mappingType();
            }
            return this.endCaseFunction(resultType);
        }

        private Expression endCaseFunction(final MappingType type) {
            if (this.whenExpression != null) {
                throw lastWhenClauseNotEnd();
            } else if (this.returnType != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }

            final List<_Pair<Object, Object>> expPairList = this.expPairList;
            if (expPairList == null) {
                throw noWhenClause();
            } else if (expPairList instanceof ArrayList) {
                this.expPairList = _Collections.unmodifiableList(expPairList);
            } else {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            this.returnType = type;
            return this;
        }

        private CriteriaException noWhenClause() {
            return ContextStack.criteriaError(this.outerContext, "Not found any when clause.");
        }

        private CriteriaException lastWhenClauseNotEnd() {
            return ContextStack.criteriaError(this.outerContext, "current when clause not end");
        }


    }//CaseFunc


    private static final class NoParensFunctionExpression extends OperationExpression.SqlFunctionExpression
            implements FunctionUtils.NoParensFunction {

        /**
         * @see #noParensFunc(String, TypeMeta)
         */
        private NoParensFunctionExpression(String name, TypeMeta returnType) {
            super(name, true, returnType); //no parens function must be build-in
        }

        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            //no-op
        }

        @Override
        void argToString(StringBuilder builder) {
            //no-op
        }


    } // NoParensFunction

    private static final class StandardCastFunc extends OperationExpression.SqlFunctionExpression {

        private final Object expression;

        private final MappingType type;

        private StandardCastFunc(Object expression, MappingType type) {
            super("CAST", true, type);
            this.expression = expression;
            this.type = typeMeta();
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {

            FuncExpUtils.appendLiteral(this.name, this.expression, sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_AS_SPACE);

            final ServerMeta serverMeta = context.parser().serverMeta();
            final DataType dataType = this.type.map(serverMeta);
            final String typeName;
            switch (serverMeta.serverDatabase()) {
                case MySQL:
                    typeName = castFuncAppendMySQLDataType(dataType, serverMeta);
                    break;
                case PostgreSQL:
                    castFuncAppendPostgreDataType(dataType, sqlBuilder);
                    break;
                default:
            }


        }

        @Override
        void argToString(final StringBuilder builder) {

        }


    } // StandardCastFunc


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/cast-functions.html#function_cast">CAST(expr AS type [ARRAY])</a>
     */
    private static String castFuncAppendMySQLDataType(final DataType dataTyp, final ServerMeta meta) {
        if (!(dataTyp instanceof MySQLType)) {
            throw new CriteriaException("");
        }
        String typeName;
        switch ((MySQLType) dataTyp) {

            case TINYINT:
            case SMALLINT:
            case MEDIUMINT:
            case INT:
            case BIGINT:
                typeName = "SIGNED";
                break;
            case TINYINT_UNSIGNED:
            case SMALLINT_UNSIGNED:
            case MEDIUMINT_UNSIGNED:
            case INT_UNSIGNED:
            case BIGINT_UNSIGNED:
                typeName = "UNSIGNED";
                break;
            case DECIMAL:
            case DECIMAL_UNSIGNED:
                typeName = "DECIMAL";
                break;
            case DOUBLE: {
                if (meta.major() > 8) {
                    typeName = "DOUBLE";
                } else {
                    typeName = "FLOAT";
                }
            }
            break;
            case FLOAT:
                typeName = "FLOAT";
                break;
            case TIME:
            case DATE:
            case DATETIME:
            case YEAR:
            case JSON:
            case GEOMETRY:
            case POINT:
            case LINESTRING:
            case POLYGON:
            case MULTIPOINT:
            case MULTIPOLYGON:
            case MULTILINESTRING:
            case GEOMETRYCOLLECTION:
                typeName = dataTyp.typeName();
                break;
            case ENUM:
            case CHAR:
            case VARCHAR:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
                typeName = "CHAR";
                break;
            case BIT:
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:
                typeName = "BINARY";
                break;
            case BOOLEAN:
            case SET:
            case NULL:
            case UNKNOWN:
            default: {
                String m = String.format("function[CAST] database[%s] don't support %s",
                        meta.serverDatabase().name(), dataTyp.typeName());
                throw new CriteriaException(m);
            }

        } // switch

        return typeName;
    }

    private static void castFuncAppendPostgreDataType(final DataType dataTyp, final StringBuilder sqlBuilder) {

    }


}
