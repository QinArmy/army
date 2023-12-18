package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.RowExpression;
import io.army.criteria.SimpleExpression;
import io.army.criteria.SimplePredicate;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.StringType;
import io.army.meta.TypeMeta;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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


    static SimpleExpression multiArgFunc(String name, List<?> argList, TypeMeta returnType) {
        return new MultiArgFunc(name, true, argList, returnType);
    }

    static SimpleExpression myMultiArgFunc(String name, List<?> argList, TypeMeta returnType) {
        return new MultiArgFunc(name, false, argList, returnType);
    }

    public static SimpleExpression jsonMapFunc(String name, Map<String, ?> map, TypeMeta returnType) {
        return new JsonMapFunc(name, map, returnType);
    }


    /*-------------------below predicate function methods -------------------*/

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
            FuncExpUtils.appendLiteral(this.arg, sqlBuilder, context);
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
            FuncExpUtils.appendLiteral(this.one, sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            FuncExpUtils.appendLiteral(this.two, sqlBuilder, context);
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
            FuncExpUtils.appendLiteral(this.one, sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_COMMA);
            FuncExpUtils.appendLiteral(this.two, sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_COMMA);
            FuncExpUtils.appendLiteral(this.three, sqlBuilder, context);
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
            FuncExpUtils.appendLiteralList(this.argList, sqlBuilder, context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            FuncExpUtils.literalListToString(this.argList, builder);

        }


    } // MultiArgFunc

    /*-------------------below predicate functions -------------------*/

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
            FuncExpUtils.appendLiteral(this.one, sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            FuncExpUtils.appendLiteral(this.two, sqlBuilder, context);
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
            FuncExpUtils.appendLiteral(this.one, sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_COMMA);
            FuncExpUtils.appendLiteral(this.two, sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_COMMA);
            FuncExpUtils.appendLiteral(this.three, sqlBuilder, context);
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
            FuncExpUtils.appendLiteralList(this.argList, sqlBuilder, context);
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
                FuncExpUtils.appendLiteral(entry.getValue(), sqlBuilder, context);

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


}
