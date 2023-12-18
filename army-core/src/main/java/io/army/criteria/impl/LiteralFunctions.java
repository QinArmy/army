package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.RowExpression;
import io.army.criteria.SimpleExpression;
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


    static SimpleExpression multiArgFunc(String name, List<?> argList, TypeMeta returnType) {
        return new MultiArgFunc(name, true, argList, returnType);
    }

    public static SimpleExpression jsonMapFunc(String name, Map<String, ?> map, TypeMeta returnType) {
        return new JsonMapFunc(name, map, returnType);
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

    private static final class MultiArgFunc extends OperationExpression.SqlFunctionExpression {

        private final List<?> argList;

        private MultiArgFunc(String name, boolean buildIn, List<?> argList, TypeMeta returnType) {
            super(name, buildIn, returnType);
            this.argList = argList;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<?> argList = this.argList;
            final int size = argList.size();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                FuncExpUtils.appendLiteral(argList.get(i), sqlBuilder, context);
            }

        }

        @Override
        void argToString(final StringBuilder builder) {
            final List<?> argList = this.argList;
            final int size = argList.size();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(argList.get(i));
            }

        }


    } // MultiArgFunc


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
