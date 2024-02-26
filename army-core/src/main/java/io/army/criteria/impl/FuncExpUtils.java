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
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.*;
import io.army.sqltype.SQLType;
import io.army.util.ClassUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._TimeUtils;

import javax.annotation.Nullable;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

abstract class FuncExpUtils {
    private FuncExpUtils() {
        throw new UnsupportedOperationException();
    }


    static Expression jsonPathExp(final Object path) {
        final Expression pathExp;
        if (path instanceof String) {
            pathExp = SQLs.literal(StringType.INSTANCE, path);
        } else if (path instanceof Expression) {
            pathExp = (Expression) path;
        } else {
            throw CriteriaUtils.mustExpressionOrType("path", String.class);
        }
        return pathExp;
    }

    static Expression jsonDocExp(final Object json) {
        final Expression jsonExp;
        if (json instanceof Expression) {
            jsonExp = (Expression) json;
        } else {
            jsonExp = SQLs.literal(JsonType.TEXT, json);
        }
        return jsonExp;
    }


    static Object localDateLiteralExp(final Object date) {
        if (date instanceof Expression || date instanceof LocalDate) {
            return date;
        }

        if (!(date instanceof String)) {
            String m = String.format("value must be %s or %s or %s", Expression.class.getName(),
                    LocalDate.class.getName(), String.class.getName());
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        try {
            return LocalDate.parse((String) date);
        } catch (DateTimeException e) {
            throw ContextStack.clearStackAndCriteriaError("date format error");
        }

    }

    static Object localTimeLiteralExp(final Object date) {
        if (date instanceof Expression || date instanceof LocalTime) {
            return date;
        }

        if (!(date instanceof String)) {
            String m = String.format("value must be %s or %s or %s", Expression.class.getName(),
                    LocalTime.class.getName(), String.class.getName());
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        try {
            return LocalTime.parse((String) date, _TimeUtils.TIME_FORMATTER_6);
        } catch (DateTimeException e) {
            throw ContextStack.clearStackAndCause(e, "format error");
        }

    }

    static Object localDateTimeLiteralExp(final Object date) {
        if (date instanceof Expression || date instanceof LocalDateTime) {
            return date;
        }

        if (!(date instanceof String)) {
            String m = String.format("value must be %s or %s or %s", Expression.class.getName(),
                    LocalDateTime.class.getName(), String.class.getName());
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        try {
            return LocalDateTime.parse((String) date, _TimeUtils.DATETIME_FORMATTER_6);
        } catch (DateTimeException e) {
            throw ContextStack.clearStackAndCause(e, "format error");
        }

    }

    static Object localOffsetDateTimeLiteralExp(final Object date) {
        if (date instanceof Expression
                || date instanceof LocalDateTime
                || date instanceof OffsetDateTime
                || date instanceof ZonedDateTime) {
            return date;
        }

        if (!(date instanceof String)) {
            String m = String.format("value must be %s or %s or %s", Expression.class.getName(),
                    LocalDateTime.class.getName(), String.class.getName());
            throw ContextStack.clearStackAndCriteriaError(m);
        }

        try {
            final String str = (String) date;
            final int length;
            length = str.length();

            final char ch;
            final Temporal temporal;
            if (length > 24 && ((ch = str.charAt(length - 6)) == '-' || ch == '+')) {
                temporal = OffsetDateTime.parse(str, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
            } else {
                temporal = LocalDateTime.parse((String) date, _TimeUtils.DATETIME_FORMATTER_6);
            }
            return temporal;

        } catch (DateTimeException e) {
            throw ContextStack.clearStackAndCause(e, "format error");
        }

    }


    static void assertLiteralExp(final Object exp) {
        if (!(exp instanceof Expression || !(exp instanceof Item))) {
            throw CriteriaUtils.mustExpressionOrLiteral("argument");
        }
    }

    static void assertPathExp(final Object path) {
        if (!(path instanceof String || path instanceof Expression)) {
            throw CriteriaUtils.mustExpressionOrType("path", String.class);
        }
    }

    static void assertTextExp(final @Nullable Object path) {
        if (!(path instanceof String || path instanceof Expression)) {
            throw CriteriaUtils.mustExpressionOrType("text", String.class);
        }
    }

    static void assertIntExp(final Object intValue) {
        if (!(intValue instanceof Integer || intValue instanceof Expression)) {
            throw CriteriaUtils.mustExpressionOrType("integer value", Integer.class);
        }
    }

    static void assertWord(Object param, Object word) {
        if (param != word) {
            throw CriteriaUtils.unknownWords(param);
        }
    }

    static void assertDistinct(SQLs.ArgDistinct distinct, SQLs.ArgDistinct dialect) {
        if (distinct != SQLs.DISTINCT && distinct != dialect) {
            throw CriteriaUtils.unknownWords(distinct);
        }
    }

    static void assertTrimSpec(SQLs.TrimSpec position) {
        if (!(position instanceof SqlWords.WordTrimPosition)) {
            throw CriteriaUtils.unknownWords(position);
        }
    }

    static void assertIntOrLongExp(final Object value) {
        if (!(value instanceof Integer || value instanceof Long || value instanceof Expression)) {
            String m = String.format("value must be %s or %s or %s", Expression.class.getName(),
                    Integer.class.getName(), Long.class.getName());
            throw ContextStack.clearStackAndCriteriaError(m);
        }
    }

    static void assertNumberExp(final Object value) {
        if (!(value instanceof Number || value instanceof Expression)) {
            throw CriteriaUtils.mustExpressionOrType("number value", Number.class);
        }
    }

    static void appendJsonDoc(final Object jsonDoc, final StringBuilder sqlBuilder, final _SqlContext context) {
        if (jsonDoc instanceof Expression) {
            ((ArmyExpression) jsonDoc).appendSql(sqlBuilder, context);
        } else {
            context.appendLiteral(JsonType.TEXT, jsonDoc, true);
        }
    }

    static void appendPathExp(final Object pathExp, final StringBuilder sqlBuilder, final _SqlContext context) {
        if (pathExp instanceof String) {
            context.appendLiteral(StringType.INSTANCE, pathExp, true);
        } else if (pathExp instanceof Expression) {
            ((ArmyExpression) pathExp).appendSql(sqlBuilder, context);
        } else {
            // no bug, never here
            throw new IllegalArgumentException();
        }
    }


    static void appendIntExp(final Object intExp, final StringBuilder sqlBuilder, final _SqlContext context) {
        if (intExp instanceof Expression) {
            ((ArmyExpression) intExp).appendSql(sqlBuilder, context);
        } else {
            context.appendLiteral(IntegerType.INSTANCE, intExp, true);
        }
    }

    static void addTextExpList(final List<Object> argList, String argName, final List<?> expList) {
        for (Object exp : expList) {
            if (exp instanceof String || exp instanceof Expression) {
                argList.add(exp);
                continue;
            }
            throw CriteriaUtils.mustExpressionOrType(argName, String.class);
        }
    }

    static void addAllTextExp(final List<Object> argList, String argName, final Object[] expArray) {
        for (Object exp : expArray) {
            if (exp instanceof String || exp instanceof Expression) {
                argList.add(exp);
                continue;
            }
            throw CriteriaUtils.mustExpressionOrType(argName, String.class);
        }
    }


    static void appendLiteral(final String funcName, final @Nullable Object literal, final StringBuilder sqlBuilder,
                              final _SqlContext context) {
        if (literal == null) {
            sqlBuilder.append(_Constant.SPACE_NULL);
        } else if (literal instanceof Expression) {
            ((ArmyExpression) literal).appendSql(sqlBuilder, context);
        } else {
            final MappingType type;
            type = _MappingFactory.getDefaultIfMatch(literal.getClass());
            if (type == null) {
                throw _Exceptions.funcNotFoundMappingType(funcName, literal);
            }
            context.appendLiteral(type, literal, true);
        }

    }

    static void appendLiteralList(final String funcName, final List<?> literalList, final StringBuilder sqlBuilder,
                                  final _SqlContext context) {
        final int size = literalList.size();

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            FuncExpUtils.appendLiteral(funcName, literalList.get(i), sqlBuilder, context);
        }

    }

    static void literalListToString(final List<?> literalList, final StringBuilder builder) {
        final int size = literalList.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            literalToString(literalList.get(i), builder);
        }

    }

    static void literalToString(final @Nullable Object literal, final StringBuilder builder) {
        if (literal == null) {
            builder.append(_Constant.SPACE_NULL);
        } else if (literal instanceof Expression) {
            builder.append(literal);
        } else {
            builder.append(_Constant.SPACE)
                    .append(literal);
        }
    }

    static void appendCompositeList(final String name, final List<?> argList, final StringBuilder sqlBuilder,
                                    final _SqlContext context) {

        for (final Object value : argList) {

            if (value == null) {
                sqlBuilder.append(_Constant.SPACE_NULL);
            } else if (value instanceof Expression) {
                ((ArmyExpression) value).appendSql(sqlBuilder, context);
            } else if (value instanceof SQLWords) {
                if (!(value instanceof SQLs.ArmyKeyWord)) {
                    throw new CriteriaException(String.format("SQL function[%s] illegal words %s", name, value));
                }
                sqlBuilder.append(((SQLWords) value).spaceRender());
            } else if (value instanceof SQLIdentifier) {
                sqlBuilder.append(_Constant.SPACE);
                context.identifier(((SQLIdentifier) value).render(), sqlBuilder);
            } else if (value instanceof TypeDef) {
                if (value instanceof SQLType) {
                    if (!value.getClass().getPackage().getName().equals("io.army.sqltype")) {
                        throw new CriteriaException(String.format("SQL function[%s] illegal SqlType %s", name, value));
                    }
                    sqlBuilder.append(_Constant.SPACE)
                            .append(((SQLType) value).typeName());
                } else if (value instanceof TypeDefs) {
                    ((_SelfDescribed) value).appendSql(sqlBuilder, context);
                } else {
                    throw new CriteriaException(String.format("SQL function[%s] illegal TypeDef %s", name, value));
                }
            } else if (value instanceof Clause) {
                ((_SelfDescribed) value).appendSql(sqlBuilder, context);
            } else {
                final MappingType type;
                type = _MappingFactory.getDefaultIfMatch(value.getClass());
                if (type == null) {
                    String m = String.format("SQL function[%s] not found default %s for %s.", name,
                            MappingType.class.getName(), ClassUtils.safeClassName(value));
                    throw new CriteriaException(m);
                }
                context.appendLiteral(type, value, true);
            }

        } // for loop

    }

    static void compositeListToString(final List<?> argList, final StringBuilder builder) {
        for (final Object value : argList) {

            if (value == null) {
                builder.append(_Constant.SPACE_NULL);
            } else if (value instanceof Expression) {
                builder.append(value);
            } else if (value instanceof SQLWords) {
                if (!(value instanceof SQLs.ArmyKeyWord)) {
                    throw new CriteriaException(String.format("Illegal words %s", value));
                }
                builder.append(((SQLWords) value).spaceRender());
            } else if (value instanceof SQLIdentifier) {
                builder.append(_Constant.SPACE)
                        .append(((SQLIdentifier) value).render());
            } else if (value instanceof TypeDef) {
                if (value instanceof SQLType) {
                    builder.append(_Constant.SPACE)
                            .append(((SQLType) value).typeName());
                } else {
                    builder.append(value);
                }
            } else {
                builder.append(value);
            }

        } // for loop

    }


    static List<?> twoAndVariadic(Object arg1, Object arg2, Object... variadic) {
        final List<Object> argList = _Collections.arrayList(2 + variadic.length);
        argList.add(arg1);
        argList.add(arg2);
        Collections.addAll(argList, variadic);
        return argList;
    }


    static VariadicClause variadicClause(final boolean required, @Nullable SQLWords separator, ArrayList<Object> arrayList) {
        return new VariadicClause(required, separator, arrayList, null, null);
    }

    static List<?> variadicList(final boolean required, Consumer<? super VariadicClause> consumer) {
        return variadicList(required, null, consumer);
    }

    static List<?> pariVariadicList(final boolean required, Consumer<? super PairVariadicClause> consumer) {
        return pariVariadicList(required, null, consumer);
    }

    static List<?> variadicList(final boolean required, @Nullable Class<?> literalClass, Consumer<? super VariadicClause> consumer) {
        final VariadicClause clause = new VariadicClause(required, null, null, null, literalClass);
        CriteriaUtils.invokeConsumer(clause, consumer);
        return clause.endClause();
    }

    static List<?> variadicList(final boolean required, ArrayList<Object> argList, @Nullable Class<?> literalClass,
                                Consumer<? super VariadicClause> consumer) {
        final VariadicClause clause = new VariadicClause(required, null, argList, null, literalClass);
        CriteriaUtils.invokeConsumer(clause, consumer);
        return clause.endClause();
    }


    static List<?> pariVariadicList(final boolean required, @Nullable Class<?> literalClass, Consumer<? super PairVariadicClause> consumer) {
        final PairVariadicClause clause = new PairVariadicClause(required, literalClass);
        CriteriaUtils.invokeConsumer(clause, consumer);
        return clause.endClause();
    }

    static List<?> pariVariadicExpList(final boolean required, ArrayList<Object> argList, MappingType type,
                                       Consumer<? super PairVariadicClause> consumer) {
        final PairVariadicClause clause = new PairVariadicClause(required, argList, type);
        CriteriaUtils.invokeConsumer(clause, consumer);
        return clause.endClause();
    }


    static final class VariadicClause implements Clause._VariadicSpaceClause,
            Clause._VariadicCommaClause, Clause._VariadicConsumer {

        private final boolean required;

        private final SQLWords separator;

        private final MappingType type;

        private final Class<?> literalClass;

        private final int startLength;


        private List<Object> expList;

        private VariadicClause(boolean required, @Nullable SQLWords separator, @Nullable ArrayList<Object> expList,
                               @Nullable MappingType type, @Nullable Class<?> literalClass) {
            this.required = required;
            this.separator = separator;
            this.type = type;
            this.literalClass = literalClass;

            if (expList == null) {
                this.startLength = 0;
            } else {
                this.startLength = expList.size();
            }
            this.expList = expList;
        }


        @Override
        public Clause._VariadicCommaClause space(@Nullable Object exp) {
            return comma(exp);
        }

        @Override
        public VariadicClause comma(final @Nullable Object exp) {
            List<Object> list = this.expList;
            if (list == null) {
                this.expList = list = _Collections.arrayList();
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }

            final SQLWords separator = this.separator;
            if (separator != null && list.size() > this.startLength) {
                list.add(separator);
            }

            final Class<?> literalClass;
            final MappingType type;
            if (exp instanceof Expression) {
                list.add(exp);
            } else if ((type = this.type) != null) {
                list.add(SQLs.literal(type, exp));
            } else if ((literalClass = this.literalClass) == null || literalClass.isInstance(exp)) {
                list.add(exp);
            } else {
                throw CriteriaUtils.mustExpressionOrType("exp", literalClass);
            }
            return this;
        }


        @Override
        public Clause._VariadicConsumer accept(@Nullable Object exp) {
            return comma(exp);
        }


        private List<?> endClause() {
            return ClauseUtils.endSingleClause(this.required, this.startLength, this.expList, this::setExpList);
        }


        private void setExpList(List<Object> expList) {
            this.expList = expList;
        }


    } // VariadicClause


    static final class PairVariadicClause implements Clause._PairVariadicSpaceClause,
            Clause._PairVariadicCommaClause, Clause._PairVariadicConsumerClause {

        private final boolean required;

        private final Class<?> literalClass;

        private final int startLength;

        private final MappingType type;

        private List<Object> expList;

        private PairVariadicClause(boolean required, @Nullable Class<?> literalClass) {
            this.required = required;
            this.literalClass = literalClass;
            this.startLength = 0;
            this.type = null;
        }

        private PairVariadicClause(boolean required, ArrayList<Object> expList, MappingType type) {
            this.required = required;
            this.literalClass = null;
            this.startLength = expList.size();
            this.type = type;

            this.expList = expList;
        }


        @Override
        public Clause._PairVariadicCommaClause comma(String keyName, @Nullable Object value) {
            return addPair(keyName, value);
        }

        @Override
        public Clause._PairVariadicCommaClause comma(Expression key, @Nullable Object value) {
            return addPair(key, value);
        }

        @Override
        public Clause._PairVariadicCommaClause space(String keyName, @Nullable Object value) {
            return addPair(keyName, value);
        }

        @Override
        public Clause._PairVariadicCommaClause space(Expression key, @Nullable Object value) {
            return addPair(key, value);
        }

        @Override
        public Clause._PairVariadicConsumerClause accept(String keyName, @Nullable Object value) {
            return addPair(keyName, value);
        }

        @Override
        public Clause._PairVariadicConsumerClause accept(Expression key, @Nullable Object value) {
            return addPair(key, value);
        }

        private PairVariadicClause addPair(final @Nullable Object key, final @Nullable Object value) {
            if (key == null) {
                throw ContextStack.clearStackAndCriteriaError("key must non-null");
            }
            List<Object> list = this.expList;
            if (list == null) {
                this.expList = list = _Collections.arrayList();
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }

            list.add(key);

            final Class<?> literalClass;
            final MappingType type;
            if (value instanceof Expression) {
                list.add(value);
            } else if ((type = this.type) != null) {
                list.add(SQLs.literal(type, value));
            } else if ((literalClass = this.literalClass) == null || literalClass.isInstance(value)) {
                list.add(value);
            } else {
                throw CriteriaUtils.mustExpressionOrType("value", literalClass);
            }
            return this;
        }

        private List<?> endClause() {
            List<Object> list = this.expList;
            final int pairLength;
            if (list == null) {
                if (this.required) {
                    throw CriteriaUtils.dontAddAnyItem();
                }
                this.expList = list = Collections.emptyList();
            } else if ((pairLength = (list.size() - this.startLength)) == 0 && this.required) {
                throw CriteriaUtils.dontAddAnyItem();
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if ((pairLength & 1) == 0) {
                this.expList = list = _Collections.unmodifiableList(list);
            } else {
                // no bug ,never here
                throw new IllegalStateException();
            }
            return list;
        }

    } // PairVariadicClause


}
