package io.army.criteria.impl;

import io.army.criteria.Clause;
import io.army.criteria.Expression;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.*;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
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


    static void assertPathExp(final Object path) {
        if (!(path instanceof String || path instanceof Expression)) {
            throw CriteriaUtils.mustExpressionOrType("path", String.class);
        }
    }

    static void assertTextExp(final Object path) {
        if (!(path instanceof String || path instanceof Expression)) {
            throw CriteriaUtils.mustExpressionOrType("text", String.class);
        }
    }

    static void assertIntExp(final Object intValue) {
        if (!(intValue instanceof Integer || intValue instanceof Expression)) {
            throw CriteriaUtils.mustExpressionOrType("integer value", Integer.class);
        }
    }

    static void appendJsonDoc(final Object jsonDoc, final StringBuilder sqlBuilder, final _SqlContext context) {
        if (jsonDoc instanceof Expression) {
            ((ArmyExpression) jsonDoc).appendSql(sqlBuilder, context);
        } else {
            context.appendLiteral(JsonType.TEXT, jsonDoc);
        }
    }

    static void appendPathExp(final Object pathExp, final StringBuilder sqlBuilder, final _SqlContext context) {
        if (pathExp instanceof String) {
            context.appendLiteral(StringType.INSTANCE, pathExp);
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
            context.appendLiteral(IntegerType.INSTANCE, intExp);
        }
    }


    static void appendLiteral(final @Nullable Object literal, final StringBuilder sqlBuilder, final _SqlContext context) {
        if (literal == null) {
            sqlBuilder.append(_Constant.SPACE_NULL);
        } else if (literal instanceof Expression) {
            ((ArmyExpression) literal).appendSql(sqlBuilder, context);
        } else {
            final MappingType type;
            type = _MappingFactory.getDefaultIfMatch(literal.getClass());
            if (type == null) {
                throw CriteriaUtils.clearStackAndNonDefaultType(literal);
            }
            context.appendLiteral(type, literal);
        }

    }


    static List<?> twoAndVariadic(Object arg1, Object arg2, Object... variadic) {
        final List<Object> argList = _Collections.arrayList(2 + variadic.length);
        argList.add(arg1);
        argList.add(arg2);
        Collections.addAll(argList, variadic);
        return argList;
    }


    static List<?> variadicList(final boolean required, Consumer<? super VariadicClause> consumer) {
        final VariadicClause clause = new VariadicClause(required);
        CriteriaUtils.invokeConsumer(clause, consumer);
        return clause.endClause();
    }

    static List<?> pariVariadicList(final boolean required, Consumer<? super PairVariadicClause> consumer) {
        final PairVariadicClause clause = new PairVariadicClause(required);
        CriteriaUtils.invokeConsumer(clause, consumer);
        return clause.endClause();
    }


    private static final class VariadicClause implements Clause._VariadicSpaceClause,
            Clause._VariadicCommaClause, Clause._VariadicConsumer {

        private final boolean required;

        private List<Object> expList;

        private VariadicClause(boolean required) {
            this.required = required;
        }

        @Override
        public Clause._VariadicCommaClause space(@Nullable Object exp) {
            return comma(exp);
        }

        @Override
        public VariadicClause comma(@Nullable Object exp) {
            List<Object> list = this.expList;
            if (list == null) {
                this.expList = list = _Collections.arrayList();
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            list.add(exp);
            return this;
        }


        @Override
        public Clause._VariadicConsumer accept(@Nullable Object exp) {
            return comma(exp);
        }


        private List<?> endClause() {
            List<Object> list = this.expList;
            if (list == null) {
                if (this.required) {
                    throw CriteriaUtils.dontAddAnyItem();
                }
                this.expList = list = Collections.emptyList();
            } else if (list instanceof ArrayList) {
                this.expList = list = _Collections.unmodifiableList(list);
            } else {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            return list;
        }


    } // VariadicClause


    private static final class PairVariadicClause implements Clause._PairVariadicSpaceClause,
            Clause._PairVariadicCommaClause, Clause._PairVariadicConsumerClause {

        private final boolean required;

        private List<Object> expList;

        private PairVariadicClause(boolean required) {
            this.required = required;
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
            list.add(value);
            return this;
        }

        private List<?> endClause() {
            List<Object> list = this.expList;
            if (list == null) {
                if (this.required) {
                    throw CriteriaUtils.dontAddAnyItem();
                }
                this.expList = list = Collections.emptyList();
            } else if (!(list instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if ((list.size() & 1) == 0) {
                this.expList = list = _Collections.unmodifiableList(list);
            } else {
                // no bug ,never here
                throw new IllegalStateException();
            }
            return list;
        }

    } // PairVariadicClause


}
