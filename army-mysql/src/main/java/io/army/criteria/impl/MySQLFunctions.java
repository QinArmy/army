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
import io.army.criteria.impl.inner.*;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLFunction;
import io.army.criteria.mysql.MySQLWindow;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.mysql.MySQLDialect;
import io.army.mapping.*;
import io.army.mapping.optional.JsonPathType;
import io.army.meta.ChildTableMeta;
import io.army.meta.TypeMeta;
import io.army.sqltype.MySQLType;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleParam;
import io.army.stmt.Stmt;
import io.army.util.ClassUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

abstract class MySQLFunctions extends DialectFunctionUtils {

    private MySQLFunctions() {
    }


    static MySQLWindowFunctions._OverSpec zeroArgWindowFunc(String name, TypeMeta returnType) {
        return new NoArgWindowFunction(name, returnType);
    }

    static MySQLWindowFunctions._OverSpec oneArgWindowFunc(String name, Expression arg, TypeMeta returnType) {
        return new OneArgWindowFunction(name, arg, returnType);
    }

    static MySQLWindowFunctions._OverSpec twoArgWindowFunc(
            String name, Expression one, Expression two, TypeMeta returnType) {
        return new MultiArgWindowFunction0(name, null, twoExpList(name, one, two), returnType);
    }

    static MySQLWindowFunctions._OverSpec threeArgWindow(String name, Expression one, Expression two, Expression three,
                                                         TypeMeta returnType) {
        return new MultiArgWindowFunction0(name, null, threeExpList(name, one, two, three), returnType);
    }


    static MySQLWindowFunctions._FromFirstLastOverSpec twoArgFromFirstWindowFunc(String name, Expression one,
                                                                                 Expression two, TypeMeta returnType) {
        return new FromFirstLastMultiArgWindowFunc(name, twoExpList(name, one, two), returnType);
    }


    static MySQLWindowFunctions._ItemAggregateWindowFunc oneArgAggregate(String name, Object arg,
                                                                         TypeMeta returnType) {
        return new OneArgAggregateWindowFunc(name, arg, returnType);
    }

    static MySQLWindowFunctions._AggregateWindowFunc compositeAggWindowFunc(String name, List<?> argList, TypeMeta returnType) {
        return new AggregateCompositeWindowFunc(name, argList, returnType);
    }

    static MySQLWindowFunctions._ItemAggregateWindowFunc multiArgAggregateWindowFunc(
            String name, List<Expression> argList, TypeMeta returnType) {
        return new MultiArgAggregateWindowFunc(name, argList, returnType);
    }


    static SimpleExpression jsonValueFunc(Object jsonDoc, Object path, Consumer<? super JsonValueOptionClause> consumer) {
        FuncExpUtils.assertPathExp(path);

        final JsonValueOptionClause clause = new JsonValueOptionClause();
        CriteriaUtils.invokeConsumer(clause, consumer);
        clause.endEventClause();
        return new JsonValueFunc(jsonDoc, path, clause);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    static SimpleExpression charFunc(Consumer<? super FuncExpUtils.VariadicClause> consumer, @Nullable String charName) {
        if (charName != null && !_StringUtils.hasText(charName)) {
            throw ContextStack.clearStackAndCriteriaError("CHAR function charset_name must have text");
        }
        final ArrayList<Object> argList = _Collections.arrayList(4);

        final FuncExpUtils.VariadicClause clause;
        clause = FuncExpUtils.variadicClause(true, SQLs.COMMA, argList);
        CriteriaUtils.invokeConsumer(clause, consumer);

        if (charName != null) {
            argList.add(SQLs.USING);
            argList.add(SQLs._identifier(charName));
        }
        return LiteralFunctions.compositeFunc("CHAR", argList, StringType.INSTANCE);
    }


    /**
     * <p>Create jsonTable function.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-table-functions.html#function_json-table">JSON_TABLE(expr, path COLUMNS (column_list) [AS] alias)</a>
     */
    static Functions._TabularFunction jsonTable(final Object jsonDoc, final Object path,
                                                final Consumer<? super MySQLJsonTableColumns> consumer) {

        FuncExpUtils.assertPathExp(path);

        final MySQLFunctions.MySQLJsonTableColumns tableColumns;
        tableColumns = new MySQLJsonTableColumns();

        CriteriaUtils.invokeConsumer(tableColumns, consumer);
        return new JsonTableFunc(jsonDoc, path, tableColumns.endClause());
    }


    static Clause groupConcatClause(Consumer<? super MySQLFunction._GroupConcatOrderBySpec> consumer) {
        return CriteriaUtils.invokeConsumer(new GroupConcatInnerClause(), consumer);
    }


    static SimpleExpression statementDigest(final PrimaryStatement statement, final Visible visible, final boolean literal) {
        final String name = "STATEMENT_DIGEST";
        assertPrimaryStatement(statement, name);
        return new StatementDigestFunc(name, statement, visible, literal, StringType.INSTANCE);
    }

    static SimpleExpression statementDigestText(final PrimaryStatement statement, final Visible visible, final boolean literal) {
        final String name = "STATEMENT_DIGEST_TEXT";
        assertPrimaryStatement(statement, name);
        return new StatementDigestFunc(name, statement, visible, literal, StringType.INSTANCE);
    }


    /**
     * @see #statementDigest(PrimaryStatement, Visible, boolean)
     * @see #statementDigestText(PrimaryStatement, Visible, boolean)
     */
    private static void assertPrimaryStatement(final PrimaryStatement statement, final String funcName) {
        if (statement instanceof _BatchStatement
                || statement instanceof _Statement._ChildStatement
                || (statement instanceof _DomainUpdate
                && ((_DomainUpdate) statement).table() instanceof ChildTableMeta)
                || (statement instanceof _DomainDelete
                && ((_DomainDelete) statement).table() instanceof ChildTableMeta)) {
            String m = String.format("%s support only simple statement", funcName);
            throw ContextStack.criteriaError(ContextStack.peek(), m);
        }
    }

    private static MappingType mapType(final TypeDef typeDef) {
        final MySQLType dataType;
        if (typeDef instanceof MySQLType) {
            dataType = (MySQLType) typeDef;
        } else if (!(typeDef instanceof TypeDefs)) {
            throw CriteriaUtils.unknownTypeDef();
        } else if (((TypeDefs) typeDef).dataType instanceof MySQLType) {
            dataType = (MySQLType) ((TypeDefs) typeDef).dataType;
        } else {
            throw CriteriaUtils.unknownTypeDef();
        }
        return dataType.mapType(CriteriaUtils::unknownTypeDef);
    }


    private static abstract class MySQLWindowFunction extends WindowFunctions.WindowFunction<MySQLWindow._PartitionBySpec>
            implements MySQLWindowFunctions._OverSpec, MySQLFunction {


        private MySQLWindowFunction(String name, TypeMeta returnType) {
            super(name, returnType);
        }


        @Override
        final boolean isDontSupportWindow(final Dialect dialect) {
            if (!(dialect instanceof MySQLDialect)) {
                throw dialectError(dialect);
            }
            return MySQLDialect.MySQL80.compareWith(dialect) < 0;
        }

        @Override
        final MySQLWindow._PartitionBySpec createAnonymousWindow(@Nullable String existingWindowName) {
            return MySQLSupports.anonymousWindow(this.outerContext, existingWindowName);
        }

    }//MySQLWindowFunction

    private static class NoArgWindowFunction extends MySQLWindowFunction implements FunctionUtils.NoArgFunction {

        private NoArgWindowFunction(String name, TypeMeta returnType) {
            super(name, returnType);
        }

        @Override
        final void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            //no argument,no-op
        }

        @Override
        final void argToString(StringBuilder builder) {
            //no argument,no-op
        }

    }//NoArgWindowFunc


    private static class OneArgWindowFunction extends MySQLWindowFunction {

        private final Object argument;

        private OneArgWindowFunction(String name, Object argument, TypeMeta returnType) {
            super(name, returnType);
            this.argument = argument;
        }

        @Override
        final void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            FuncExpUtils.appendLiteral(this.argument, sqlBuilder, context);
        }

        @Override
        final void argToString(final StringBuilder builder) {
            FuncExpUtils.literalToString(this.argument, builder);
        }


    }//OneArgWindowFunc


    private static class OneOptionArgWindowFunction extends MySQLWindowFunction {

        private final SQLWords option;

        private final ArmyExpression argument;


        private OneOptionArgWindowFunction(String name, @Nullable SQLWords option, ArmyExpression argument,
                                           TypeMeta returnType) {
            super(name, returnType);
            this.option = option;
            this.argument = argument;
        }

        @Override
        final void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            final SQLWords option = this.option;
            if (option != null) {
                sqlBuilder.append(option.spaceRender());
            }
            this.argument.appendSql(sqlBuilder, context);
        }

        @Override
        final void argToString(final StringBuilder builder) {
            final SQLWords option = this.option;
            if (option != null) {
                builder.append(_Constant.SPACE)
                        .append(option.spaceRender());
            }
            builder.append(this.argument);
        }


    }//OneOptionArgWindowFunction


    @Deprecated
    private static class MultiArgWindowFunction0 extends MySQLWindowFunction {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        private MultiArgWindowFunction0(String name, @Nullable SQLWords option, List<ArmyExpression> argList,
                                        TypeMeta returnType) {
            super(name, returnType);
            assert argList.size() > 0;
            this.option = option;
            this.argList = argList;
        }

        @Override
        final void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            FunctionUtils.appendArguments(this.option, this.argList, context);
        }

        @Override
        final void argToString(final StringBuilder builder) {
            FunctionUtils.argumentsToString(this.option, this.argList, builder);
        }


    }//MultiArgWindowFunction


    private static class MultiArgWindowFunction extends MySQLWindowFunction {

        private final List<? extends Expression> argList;

        private MultiArgWindowFunction(String name, List<? extends Expression> argList, TypeMeta returnType) {
            super(name, returnType);
            this.argList = argList;
        }

        @Override
        final void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            FuncExpUtils.appendLiteral(this.argList, sqlBuilder, context);
        }

        @Override
        final void argToString(StringBuilder builder) {
            FuncExpUtils.literalListToString(this.argList, builder);
        }


    } // MultiArgWindowFunction


    private static final class FromFirstLastMultiArgWindowFunc extends MultiArgWindowFunction0
            implements MySQLWindowFunctions._FromFirstLastOverSpec {

        private FromFirstLast fromFirstLast;

        private NullTreatment nullTreatment;

        /**
         * @see #twoArgFromFirstWindowFunc(String, Expression, Expression, TypeMeta)
         */
        public FromFirstLastMultiArgWindowFunc(String name, List<ArmyExpression> argList, TypeMeta returnType) {
            super(name, null, argList, returnType);
        }

        @Override
        public MySQLWindowFunctions._NullTreatmentOverSpec fromFirst() {
            this.fromFirstLast = FromFirstLast.FROM_FIRST;
            return this;
        }

        @Override
        public MySQLWindowFunctions._NullTreatmentOverSpec fromLast() {
            this.fromFirstLast = FromFirstLast.FROM_LAST;
            return this;
        }

        @Override
        public MySQLWindowFunctions._NullTreatmentOverSpec ifFromFirst(BooleanSupplier predicate) {
            this.fromFirstLast = predicate.getAsBoolean() ? FromFirstLast.FROM_FIRST : null;
            return this;
        }

        @Override
        public MySQLWindowFunctions._NullTreatmentOverSpec ifFromLast(BooleanSupplier predicate) {
            this.fromFirstLast = predicate.getAsBoolean() ? FromFirstLast.FROM_LAST : null;
            return this;
        }

        @Override
        public MySQLWindowFunctions._OverSpec respectNulls() {
            this.nullTreatment = NullTreatment.RESPECT_NULLS;
            return this;
        }

        @Override
        public MySQLWindowFunctions._OverSpec ignoreNulls() {
            this.nullTreatment = NullTreatment.IGNORE_NULLS;
            return this;
        }

        @Override
        public MySQLWindowFunctions._OverSpec ifRespectNulls(BooleanSupplier predicate) {
            this.nullTreatment = predicate.getAsBoolean() ? NullTreatment.RESPECT_NULLS : null;
            return this;
        }

        @Override
        public MySQLWindowFunctions._OverSpec ifIgnoreNulls(BooleanSupplier predicate) {
            this.nullTreatment = predicate.getAsBoolean() ? NullTreatment.IGNORE_NULLS : null;
            return this;
        }

        @Override
        void appendClauseBeforeOver(final StringBuilder sqlBuilder, final _SqlContext context) {
            final FromFirstLast fromFirstLast = this.fromFirstLast;
            final NullTreatment nullTreatment = this.nullTreatment;

            if (fromFirstLast != null || nullTreatment != null) {
                if (fromFirstLast != null) {
                    sqlBuilder.append(fromFirstLast);
                }
                if (nullTreatment != null) {
                    sqlBuilder.append(nullTreatment);
                }
            }
        }

        @Override
        void outerClauseToString(final StringBuilder builder) {
            final FromFirstLast fromFirstLast = this.fromFirstLast;
            final NullTreatment nullTreatment = this.nullTreatment;

            if (fromFirstLast != null) {
                builder.append(fromFirstLast);
            }
            if (nullTreatment != null) {
                builder.append(nullTreatment);
            }
        }


    }//FromFirstLastMultiArgWindowFunc


    private static final class OneArgAggregateWindowFunc extends OneArgWindowFunction
            implements MySQLWindowFunctions._ItemAggregateWindowFunc {

        private OneArgAggregateWindowFunc(String name, Object argument, TypeMeta returnType) {
            super(name, argument, returnType);
        }


    }//OneArgAggregateWindowFunc

    @Deprecated
    private static final class OneArgOptionAggregateWindowFunc extends OneOptionArgWindowFunction
            implements MySQLWindowFunctions._ItemAggregateWindowFunc {

        private OneArgOptionAggregateWindowFunc(String name, @Nullable SQLWords option, ArmyExpression argument,
                                                TypeMeta returnType) {
            super(name, option, argument, returnType);
        }


    }//OneArgAggregateWindowFunc


    @Deprecated
    private static final class MultiArgAggregateWindowFunc0 extends MultiArgWindowFunction0
            implements MySQLWindowFunctions._ItemAggregateWindowFunc {

        private MultiArgAggregateWindowFunc0(String name, @Nullable SQLWords option, List<ArmyExpression> argList,
                                             TypeMeta returnType) {
            super(name, option, argList, returnType);
        }

    }//MultiArgAggregateWindowFunc


    private static final class MultiArgAggregateWindowFunc extends MultiArgWindowFunction
            implements MySQLWindowFunctions._ItemAggregateWindowFunc {

        private MultiArgAggregateWindowFunc(String name, List<? extends Expression> argList, TypeMeta returnType) {
            super(name, argList, returnType);
        }


    } // MultiArgAggregateWindowFunc


    private static class CompositeWindowFunc extends MySQLWindowFunction {

        private final List<?> argList;

        private CompositeWindowFunc(String name, List<?> argList, TypeMeta returnType) {
            super(name, returnType);
            this.argList = argList;
        }

        @Override
        final void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            FuncExpUtils.appendCompositeList(this.name, this.argList, sqlBuilder, context);
        }

        @Override
        final void argToString(StringBuilder builder) {
            FuncExpUtils.compositeListToString(this.argList, builder);
        }


    } // CompositeWindowFunc


    private static class AggregateCompositeWindowFunc extends CompositeWindowFunc
            implements MySQLWindowFunctions._AggregateWindowFunc {


        private AggregateCompositeWindowFunc(String name, List<?> argList, TypeMeta returnType) {
            super(name, argList, returnType);
        }


    } // AggregateCompositeWindowFunc



    /**
     * @see #groupConcatClause(Consumer)
     */
    private static final class GroupConcatInnerClause
            extends OrderByClause.OrderByClauseClause<MySQLFunction._GroupConcatSeparatorClause, MySQLFunction._GroupConcatSeparatorClause>
            implements MySQLFunction._GroupConcatOrderBySpec, io.army.criteria.impl.ArmyFuncClause {


        private String stringValue;

        private GroupConcatInnerClause() {
            super(ContextStack.peek());
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<? extends SortItem> sortItemList = this.orderByList();
            final int sortSize = sortItemList.size();


            if (sortSize > 0) {
                sqlBuilder.append(_Constant.SPACE_ORDER_BY);
                for (int i = 0; i < sortSize; i++) {
                    if (i > 0) {
                        sqlBuilder.append(_Constant.SPACE_COMMA);
                    }
                    ((_SelfDescribed) sortItemList.get(i)).appendSql(sqlBuilder, context);
                }
            }

            final String stringValue = this.stringValue;
            if (stringValue != null) {
                sqlBuilder.append(" SEPARATOR ");
                context.appendLiteral(StringType.INSTANCE, stringValue);
            }
        }

        @Override
        public Clause separator(final @Nullable String strVal) {
            this.endOrderByClauseIfNeed();
            if (this.stringValue != null) {
                throw ContextStack.clearStackAndCriteriaError("duplicate separator");
            } else if (strVal == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            this.stringValue = strVal;
            return this;
        }

        @Override
        public Clause separator(Supplier<String> supplier) {
            return this.separator(supplier.get());
        }

        @Override
        public Clause ifSeparator(Supplier<String> supplier) {
            this.endOrderByClauseIfNeed();
            this.stringValue = supplier.get();
            return this;
        }


        @Override
        public void endClause() {
            this.endOrderByClauseIfNeed();
        }


    }//GroupConcatClause

    private static final class StatementDigestFunc extends OperationExpression.SqlFunctionExpression {


        private final PrimaryStatement statement;

        private final Visible visible;

        private final boolean literal;


        private StatementDigestFunc(String name, final PrimaryStatement statement, final Visible visible,
                                    final boolean literal, TypeMeta returnType) {
            super(name, returnType);
            this.statement = statement;
            this.visible = visible;
            this.literal = literal;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            final PrimaryStatement statement = this.statement;

            final Stmt stmt;
            if (statement instanceof SelectStatement) {
                stmt = context.parser().select((Select) statement, false, context.sessionSpec());
            } else if (statement instanceof InsertStatement) {
                stmt = context.parser().insert((InsertStatement) statement, context.sessionSpec());
            } else if (statement instanceof UpdateStatement) {
                stmt = context.parser().update((UpdateStatement) statement, false, context.sessionSpec());
            } else if (statement instanceof DeleteStatement) {
                stmt = context.parser().delete((DeleteStatement) statement, false, context.sessionSpec());
            } else if (statement instanceof Values) {
                stmt = context.parser().values((Values) statement, context.sessionSpec());
            } else if (statement instanceof DqlStatement) {
                stmt = context.parser().dialectDql((DqlStatement) statement, context.sessionSpec());
            } else if (statement instanceof DmlStatement) {
                stmt = context.parser().dialectDml((DmlStatement) statement, context.sessionSpec());
            } else {
                //no bug,never here
                throw new IllegalArgumentException();
            }

            if (!(stmt instanceof SimpleStmt)) {
                String m = String.format("the argument of %s must be simple statement.", this.name);
                throw new CriteriaException(m);
            }

            if (this.literal) {
                context.appendLiteral(StringType.INSTANCE, ((SimpleStmt) stmt).sqlText());
            } else {
                context.appendParam(SingleParam.build(StringType.INSTANCE, ((SimpleStmt) stmt).sqlText()));
            }
        }

        @Override
        void argToString(final StringBuilder builder) {
            //TODO
        }


    }//StatementDigestFunc


    private enum JsonValueWord {
        NULL(" NULL"),
        ERROR(" ERROR"),
        ON_EMPTY(" ON EMPTY"),
        ON_ERROR(" ON ERROR");

        private final String spaceWords;

        JsonValueWord(String spaceWords) {
            this.spaceWords = spaceWords;
        }


    }//NullOrError

    /**
     * @see JsonValueFunc#appendSql(StringBuilder, _SqlContext)
     */
    private static void appendOnEmptyOrErrorClause(final List<_Pair<Object, JsonValueWord>> actionList
            , final _SqlContext context) {
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        assert actionList.size() < 3;
        for (_Pair<Object, JsonValueWord> pair : actionList) {
            if (pair.first instanceof JsonValueWord) {
                assert pair.first == JsonValueWord.NULL || pair.first == JsonValueWord.ERROR;
                sqlBuilder.append(((JsonValueWord) pair.first).spaceWords);
            } else if (pair.first instanceof Expression) {
                sqlBuilder.append(_Constant.SPACE_DEFAULT);
                ((ArmyExpression) pair.first).appendSql(sqlBuilder, context);
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }
            assert pair.second == JsonValueWord.ON_EMPTY || pair.second == JsonValueWord.ON_ERROR;
            sqlBuilder.append(pair.second.spaceWords);

        }//for
    }

    /**
     * @see JsonValueFunc#toString()
     */
    private static void onEmptyOrErrorClauseToString(final List<_Pair<Object, JsonValueWord>> actionList
            , final StringBuilder builder) {
        assert actionList.size() < 3;
        for (_Pair<Object, JsonValueWord> pair : actionList) {
            if (pair.first instanceof JsonValueWord) {
                assert pair.first == JsonValueWord.NULL || pair.first == JsonValueWord.ERROR;
                builder.append(((JsonValueWord) pair.first).spaceWords);
            } else if (pair.first instanceof Expression) {
                builder.append(_Constant.SPACE_DEFAULT)
                        .append(pair.first);
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }
            assert pair.second == JsonValueWord.ON_EMPTY || pair.second == JsonValueWord.ON_ERROR;
            builder.append(pair.second.spaceWords);

        }//for
    }


    private static class ColumnEventClause implements MySQLFunction._ValueEmptyActionSpec,
            MySQLFunction._ValueOnEmptySpec {

        private Object temp;

        private Object actionOnEmpty;

        private Object actionOnError;

        private ColumnEventClause() {
        }

        @Override
        public final ColumnEventClause spaceNull() {
            this.temp = SQLs.NULL;
            return this;
        }

        @Override
        public final ColumnEventClause spaceDefault(final @Nullable Object jsonExp) {
            if (jsonExp == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (this.temp != null) {
                throw ContextStack.clearStackAndCriteriaError("duplication action");
            }
            this.temp = jsonExp;
            return this;
        }

        @Override
        public final ColumnEventClause spaceError() {
            this.temp = SQLs.ERROR;
            return this;
        }

        @Override
        public final Object onError() {
            final Object temp = this.temp;
            if (temp == null || this.actionOnError != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.temp = null;
            this.actionOnError = temp;
            return Collections.EMPTY_LIST;
        }

        @Override
        public final ColumnEventClause onEmpty() {
            final Object temp = this.temp;
            if (temp == null || this.actionOnEmpty != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.temp = null;
            this.actionOnEmpty = temp;
            return this;
        }


        void appendAction(final StringBuilder sqlBuilder, final _SqlContext context) {

            Object action;

            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    action = this.actionOnEmpty;
                } else {
                    action = this.actionOnError;
                }

                if (action == null) {
                    continue;
                }

                if (action == SQLs.NULL) {
                    sqlBuilder.append(_Constant.SPACE_NULL);
                } else if (action == SQLs.ERROR) {
                    sqlBuilder.append(SQLs.ERROR.spaceRender());
                } else {
                    sqlBuilder.append(_Constant.SPACE_DEFAULT);
                    if (action instanceof Expression) {
                        ((ArmyExpression) action).appendSql(sqlBuilder, context);
                    } else {
                        context.appendLiteral(JsonType.TEXT, action);
                    }
                }

                if (i == 0) {
                    sqlBuilder.append(" ON EMPTY");
                } else {
                    sqlBuilder.append(" ON ERROR");
                }


            } // for loop


        } // ColumnEventClause


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            eventToString(builder);
            return builder.toString();
        }

        final void eventToString(final StringBuilder builder) {
            Object action;
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    action = this.actionOnEmpty;
                } else {
                    action = this.actionOnError;
                }

                if (action == null) {
                    continue;
                }

                if (action == SQLs.NULL) {
                    builder.append(_Constant.SPACE_NULL);
                } else if (action == SQLs.ERROR) {
                    builder.append(SQLs.ERROR.spaceRender());
                } else {
                    builder.append(_Constant.SPACE_DEFAULT)
                            .append(action);
                }

                if (i == 0) {
                    builder.append(" ON EMPTY");
                } else {
                    builder.append(" ON ERROR");
                }
            }
        }

        final ColumnEventClause endEventClause() {
            if (this.temp != null) {
                throw ContextStack.clearStackAndCriteriaError("exists action clause, but no ON EMPTY/ON ERROR clause");
            }
            return this;
        }


    } // ColumnEventClause


    static final class JsonValueOptionClause extends ColumnEventClause
            implements MySQLFunction._JsonValueReturningSpec, _SelfDescribed {

        private MySQLCastType castType;

        private Object precision;

        private Object scale;

        private String charsetName;

        private String collateName;


        private JsonValueOptionClause() {
        }


        @Override
        public MySQLFunction._ValueEmptyActionSpec returning(MySQLCastType type) {
            if (this.castType != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            switch (type) {
                case FLOAT:
                case DOUBLE:
                case DECIMAL:
                case SIGNED:
                case UNSIGNED:
                case DATE:
                case TIME:
                case DATETIME:
                case YEAR:
                case CHAR:
                case JSON:
                    this.castType = type;
                    break;
                default:
                    String m = String.format("JSON_VALUE function don't support %s", type);
                    throw ContextStack.clearStackAndCriteriaError(m);
            }
            return this;
        }

        @Override
        public MySQLFunction._ValueEmptyActionSpec returning(MySQLCastType type, final Object length) {
            if (this.castType != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }

            FuncExpUtils.assertIntExp(length);

            switch (type) {
                case TIME:
                case DATETIME:
                case DECIMAL:
                case CHAR:
                    this.castType = type;
                    this.precision = length;
                    break;
                default:
                    String m = String.format("current method don't support %s", type);
                    throw ContextStack.clearStackAndCriteriaError(m);
            }
            return this;
        }

        @Override
        public MySQLFunction._ValueEmptyActionSpec returning(MySQLCastType type, final Object precision, final Object scale) {
            if (this.castType != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (type != MySQLCastType.DECIMAL) {
                String m = String.format("current method don't support %s", type);
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            FuncExpUtils.assertIntExp(precision);
            FuncExpUtils.assertIntExp(scale);
            this.castType = type;
            this.precision = precision;
            this.scale = scale;
            return this;
        }

        @Override
        public MySQLFunction._ValueEmptyActionSpec returning(MySQLCastType type, SQLs.WordsCharacterSet charSet, String charsetName) {
            return storeCharType(type, null, charsetName, null);
        }

        @Override
        public MySQLFunction._ValueEmptyActionSpec returning(MySQLCastType type, SQLs.WordsCharacterSet charSet, String charsetName, SQLs.WordCollate collate, String collation) {
            return storeCharType(type, null, charsetName, collation);
        }

        @Override
        public MySQLFunction._ValueEmptyActionSpec returning(MySQLCastType type, Object length, SQLs.WordsCharacterSet charSet, String charsetName) {
            return storeCharType(type, length, charsetName, null);
        }

        @Override
        public MySQLFunction._ValueEmptyActionSpec returning(MySQLCastType type, Object length, SQLs.WordsCharacterSet charSet, String charsetName, SQLs.WordCollate collate, String collation) {
            return storeCharType(type, length, charsetName, collation);
        }

        private MySQLFunction._ValueEmptyActionSpec storeCharType(final MySQLCastType type, final @Nullable Object length,
                                                                  final @Nullable String charsetName,
                                                                  final @Nullable String collation) {

            if (this.castType != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }

            if (type != MySQLCastType.CHAR) {
                String m = String.format("current method don't support %s", type);
                throw ContextStack.clearStackAndCriteriaError(m);
            }
            if (length != null) {
                FuncExpUtils.assertIntExp(length);
            }
            this.castType = type;
            this.precision = length;
            this.scale = null;
            this.charsetName = charsetName;
            this.collateName = collation;
            return this;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            final MySQLCastType type = this.castType;
            if (type == null) {
                appendAction(sqlBuilder, context);
                return;
            }

            sqlBuilder.append(_Constant.SPACE_RETURNING)
                    .append(_Constant.SPACE)
                    .append(type.typeName());

            final Object precision = this.precision;

            if (precision != null) {
                sqlBuilder.append(_Constant.LEFT_PAREN);
                FuncExpUtils.appendIntExp(precision, sqlBuilder, context);

                final Object scale = this.scale;
                if (scale != null) {
                    sqlBuilder.append(_Constant.COMMA);
                    FuncExpUtils.appendIntExp(scale, sqlBuilder, context);
                }

                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            final String charset = this.charsetName, collate = this.collateName;

            if (charset != null) {
                sqlBuilder.append(_Constant.SPACE_CHARACTER_SET_SPACE);
                context.parser().identifier(charset, sqlBuilder);
            }

            if (collate != null) {
                sqlBuilder.append(_Constant.SPACE_COLLATE_SPACE);
                context.parser().identifier(collate, sqlBuilder);
            }

            appendAction(sqlBuilder, context);

        }


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            final MySQLCastType type = this.castType;
            if (type == null) {
                eventToString(builder);
                return builder.toString();
            }


            builder.append(_Constant.SPACE_RETURNING)
                    .append(_Constant.SPACE)
                    .append(type.typeName());

            final Object precision = this.precision;

            if (precision != null) {
                builder.append(_Constant.LEFT_PAREN)
                        .append(precision);
                final Object scale = this.scale;
                if (scale != null) {
                    builder.append(_Constant.COMMA)
                            .append(scale);
                }

                builder.append(_Constant.RIGHT_PAREN);
            }

            final String charset = this.charsetName, collate = this.collateName;

            if (charset != null) {
                builder.append(_Constant.SPACE_CHARACTER_SET_SPACE)
                        .append(charset);
            }

            if (collate != null) {
                builder.append(_Constant.SPACE_COLLATE_SPACE)
                        .append(charset);
            }

            eventToString(builder);
            return builder.toString();
        }

        private MappingType obtainType() {
            final MySQLCastType castType = this.castType;
            if (castType == null) {
                return StringType.INSTANCE;
            }

            final MappingType type;
            switch (castType) {
                case FLOAT:
                    type = FloatType.INSTANCE;
                    break;
                case DOUBLE:
                    type = DoubleType.INSTANCE;
                    break;
                case DECIMAL:
                    type = BigDecimalType.INSTANCE;
                    break;
                case SIGNED:
                    type = LongType.INSTANCE;
                    break;
                case UNSIGNED:
                    type = UnsignedBigintType.INSTANCE;
                    break;
                case DATE:
                    type = LocalDateType.INSTANCE;
                    break;
                case TIME:
                    type = LocalTimeType.INSTANCE;
                    break;
                case DATETIME:
                    type = LocalDateTimeType.INSTANCE;
                    break;
                case YEAR:
                    type = YearType.INSTANCE;
                    break;
                case CHAR:
                    type = StringType.INSTANCE;
                    break;
                case JSON:
                    type = JsonType.TEXT;
                    break;
                default:
                    // no bug ,never here
                    throw _Exceptions.unexpectedEnum(castType);
            }
            return type;
        }


    } // JsonValueOptionClause


    private static final class JsonValueFunc extends OperationExpression.SqlFunctionExpression
            implements MySQLFunction {

        private final Object jsonDoc;

        private final Object path;

        private final JsonValueOptionClause optionClause;

        /**
         * @see #jsonValueFunc(Object, Object, Consumer)
         */
        private JsonValueFunc(Object jsonDoc, Object path, JsonValueOptionClause clause) {
            super("JSON_VALUE", clause.obtainType());
            this.jsonDoc = jsonDoc;
            this.path = path;
            this.optionClause = clause;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {

            FuncExpUtils.appendJsonDoc(this.jsonDoc, sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_COMMA);

            FuncExpUtils.appendPathExp(this.path, sqlBuilder, context);

            this.optionClause.appendSql(sqlBuilder, context);

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.jsonDoc)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.path)
                    .append(this.optionClause);
        }


    }//JsonValueFunction


    private interface JsonTableColumn extends _SelfDescribed {

    }


    private static final class JsonTableOrdinalityField extends FunctionField implements JsonTableColumn {

        private JsonTableOrdinalityField(String name) {
            super(name, UnsignedSqlIntType.INSTANCE);
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE);
            context.identifier(this.name, sqlBuilder);
            sqlBuilder.append(SQLs.FOR_ORDINALITY.spaceRender());
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(SQLs.FOR_ORDINALITY.spaceRender())
                    .toString();
        }

    } // JsonTableOrdinalityField


    private static final class JsonTablePathField extends FunctionField implements JsonTableColumn {

        private final TypeItem typeItem;

        private final boolean exists;

        private final Object pathExp;

        private final ColumnEventClause eventClause;

        private JsonTablePathField(String name, MappingType type, TypeItem typeItem, Object pathExp,
                                   @Nullable ColumnEventClause eventClause) {
            super(name, type);
            this.typeItem = typeItem;
            this.exists = false;
            this.pathExp = pathExp;
            this.eventClause = eventClause;
        }

        private JsonTablePathField(String name, MappingType type, TypeItem typeItem, Object pathExp) {
            super(name, type);
            this.typeItem = typeItem;
            this.exists = true;
            this.pathExp = pathExp;
            this.eventClause = null;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE);
            context.identifier(this.name, sqlBuilder);

            final TypeItem typeItem = this.typeItem;

            if (typeItem instanceof MappingType) {
                sqlBuilder.append(_Constant.SPACE);
                context.parser().typeName((MappingType) typeItem, sqlBuilder);
            } else if (typeItem instanceof MySQLType) {
                sqlBuilder.append(_Constant.SPACE);
                sqlBuilder.append(((MySQLType) typeItem).typeName());
            } else {
                ((_SelfDescribed) typeItem).appendSql(sqlBuilder, context);
            }

            if (this.exists) {
                sqlBuilder.append(SQLs.EXISTS.spaceRender());
            }
            sqlBuilder.append(SQLs.PATH.spaceRender());

            final Object pathExp = this.pathExp;
            if (pathExp instanceof String) {
                context.appendLiteral(JsonPathType.INSTANCE, pathExp);
            } else if (pathExp instanceof Expression) {
                ((ArmyExpression) pathExp).appendSql(sqlBuilder, context);
            } else {
                // no bug ,never here
                throw new IllegalStateException();
            }

            final ColumnEventClause eventClause = this.eventClause;
            if (eventClause != null) {
                eventClause.appendAction(sqlBuilder, context);
            }
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            builder.append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.SPACE)
                    .append(this.typeItem);

            if (this.exists) {
                builder.append(SQLs.EXISTS.spaceRender());
            }

            builder.append(SQLs.PATH.spaceRender())
                    .append(this.pathExp);

            final ColumnEventClause eventClause = this.eventClause;
            if (eventClause != null) {
                builder.append(eventClause);
            }
            return builder.toString();
        }


    } // JsonTablePathField

    private static final class JsonTableNestedField implements JsonTableColumn {

        private final boolean path;

        private final Object pathExp;

        private final List<JsonTableColumn> columnList;

        /**
         * @param columnList a unmodified list
         */
        private JsonTableNestedField(boolean path, Object pathExp, List<JsonTableColumn> columnList) {
            this.path = path;
            this.pathExp = pathExp;
            this.columnList = columnList;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE)
                    .append(SQLs.NESTED.spaceRender());

            if (this.path) {
                sqlBuilder.append(SQLs.PATH.spaceRender());
            }
            final Object pathExp = this.pathExp;
            if (pathExp instanceof String) {
                context.appendLiteral(StringType.INSTANCE, pathExp);
            } else if (pathExp instanceof Expression) {
                ((ArmyExpression) pathExp).appendSql(sqlBuilder, context);
            } else {
                // no bug ,never here
                throw new IllegalStateException();
            }

            JsonTableFunc.appendJsonTableColumns(this.columnList, sqlBuilder, context);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(_Constant.SPACE)
                    .append(SQLs.NESTED.spaceRender())
                    .append(this.pathExp);

            JsonTableFunc.jsonTableColumnsToString(this.columnList, builder);

            return builder.toString();
        }


    } // JsonTableNestedField


    private static final class MySQLJsonTableColumns implements MySQLFunction._JsonTableColumnSpaceClause,
            MySQLFunction._JsonTableColumnCommaClause,
            MySQLFunction._JsonTableColumnConsumerClause {


        private List<JsonTableColumn> fieldList;


        private MySQLJsonTableColumns() {
        }

        @Override
        public MySQLFunction._JsonTableColumnCommaClause space(String name, SQLs.WordsForOrdinality forOrdinality) {
            return comma(name, forOrdinality);
        }

        @Override
        public MySQLFunction._JsonTableColumnCommaClause space(String name, TypeItem type, SQLs.WordPath path, Object pathExp) {
            return comma(name, type, path, pathExp);
        }

        @Override
        public MySQLFunction._JsonTableColumnCommaClause space(String name, TypeItem type, SQLs.WordPath path, Object pathExp, Consumer<MySQLFunction._ValueEmptyActionSpec> consumer) {
            return comma(name, type, path, pathExp, consumer);
        }

        @Override
        public MySQLFunction._JsonTableColumnCommaClause space(String name, TypeItem type, SQLs.WordExists exists, SQLs.WordPath path, Object pathExp) {
            return comma(name, type, exists, path, pathExp);
        }

        @Override
        public MySQLFunction._JsonTableColumnCommaClause space(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, Consumer<MySQLFunction._JsonTableColumnSpaceClause> consumer) {
            return comma(nested, pathExp, columns, consumer);
        }

        @Override
        public MySQLFunction._JsonTableColumnCommaClause space(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<MySQLFunction._JsonTableColumnConsumerClause> consumer) {
            return comma(nested, pathExp, columns, space, consumer);
        }

        @Override
        public MySQLFunction._JsonTableColumnCommaClause space(SQLs.WordNested nested, SQLs.WordPath path, Object pathExp, SQLs.WordColumns columns, Consumer<MySQLFunction._JsonTableColumnSpaceClause> consumer) {
            return comma(nested, path, pathExp, columns, consumer);
        }

        @Override
        public MySQLFunction._JsonTableColumnCommaClause space(SQLs.WordNested nested, SQLs.WordPath path, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<MySQLFunction._JsonTableColumnConsumerClause> consumer) {
            return comma(nested, path, pathExp, columns, space, consumer);
        }

        @Override
        public MySQLJsonTableColumns comma(String name, SQLs.WordsForOrdinality forOrdinality) {
            return addField(new JsonTableOrdinalityField(name));
        }

        @Override
        public MySQLJsonTableColumns comma(String name, TypeItem type, SQLs.WordPath path, final Object pathExp) {
            return addPathField(name, type, false, pathExp, null);
        }

        @Override
        public MySQLJsonTableColumns comma(String name, TypeItem type, SQLs.WordPath path, Object pathExp, Consumer<MySQLFunction._ValueEmptyActionSpec> consumer) {
            final ColumnEventClause eventClause = new ColumnEventClause();
            CriteriaUtils.invokeConsumer(eventClause, consumer);

            return addPathField(name, type, false, pathExp, eventClause.endEventClause());
        }

        @Override
        public MySQLJsonTableColumns comma(String name, TypeItem type, SQLs.WordExists exists, SQLs.WordPath path, Object pathExp) {
            return addPathField(name, type, true, pathExp, null);
        }

        @Override
        public MySQLJsonTableColumns comma(SQLs.WordNested nested, final Object pathExp, SQLs.WordColumns columns, Consumer<MySQLFunction._JsonTableColumnSpaceClause> consumer) {
            return addNestedField(false, pathExp, consumer);
        }

        @Override
        public MySQLJsonTableColumns comma(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<MySQLFunction._JsonTableColumnConsumerClause> consumer) {
            return addNestedField(false, pathExp, consumer);
        }


        @Override
        public MySQLFunction._JsonTableColumnCommaClause comma(SQLs.WordNested nested, SQLs.WordPath path, Object pathExp, SQLs.WordColumns columns, Consumer<MySQLFunction._JsonTableColumnSpaceClause> consumer) {
            return addNestedField(true, pathExp, consumer);
        }

        @Override
        public MySQLFunction._JsonTableColumnCommaClause comma(SQLs.WordNested nested, SQLs.WordPath path, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<MySQLFunction._JsonTableColumnConsumerClause> consumer) {
            return addNestedField(true, pathExp, consumer);
        }

        @Override
        public MySQLFunction._JsonTableColumnConsumerClause column(String name, SQLs.WordsForOrdinality forOrdinality) {
            return comma(name, forOrdinality);
        }

        @Override
        public MySQLFunction._JsonTableColumnConsumerClause column(String name, TypeItem type, SQLs.WordPath path, Object pathExp) {
            return comma(name, type, path, pathExp);
        }

        @Override
        public MySQLFunction._JsonTableColumnConsumerClause column(String name, TypeItem type, SQLs.WordPath path, Object pathExp, Consumer<MySQLFunction._ValueEmptyActionSpec> consumer) {
            return comma(name, type, path, pathExp, consumer);
        }

        @Override
        public MySQLFunction._JsonTableColumnConsumerClause column(String name, TypeItem type, SQLs.WordExists exists, SQLs.WordPath path, Object pathExp) {
            return comma(name, type, exists, path, pathExp);
        }

        @Override
        public MySQLFunction._JsonTableColumnConsumerClause column(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, Consumer<MySQLFunction._JsonTableColumnSpaceClause> consumer) {
            return comma(nested, pathExp, columns, consumer);
        }

        @Override
        public MySQLFunction._JsonTableColumnConsumerClause column(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<MySQLFunction._JsonTableColumnConsumerClause> consumer) {
            return comma(nested, pathExp, columns, space, consumer);
        }

        @Override
        public MySQLFunction._JsonTableColumnCommaClause column(SQLs.WordNested nested, SQLs.WordPath path, Object pathExp, SQLs.WordColumns columns, Consumer<MySQLFunction._JsonTableColumnSpaceClause> consumer) {
            return comma(nested, path, pathExp, columns, consumer);
        }

        @Override
        public MySQLFunction._JsonTableColumnCommaClause column(SQLs.WordNested nested, SQLs.WordPath path, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<MySQLFunction._JsonTableColumnConsumerClause> consumer) {
            return comma(nested, path, pathExp, columns, space, consumer);
        }

        private List<JsonTableColumn> endClause() {
            List<JsonTableColumn> fieldList = this.fieldList;
            if (fieldList == null) {
                throw CriteriaUtils.dontAddAnyItem();
            } else if (fieldList instanceof ArrayList) {
                this.fieldList = fieldList = _Collections.unmodifiableList(fieldList);
            } else {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            return fieldList;
        }

        private MySQLJsonTableColumns addNestedField(final boolean path, final Object pathExp,
                                                     final Consumer<? super MySQLJsonTableColumns> consumer) {
            FuncExpUtils.assertPathExp(pathExp);

            final MySQLJsonTableColumns tableColumns;
            tableColumns = new MySQLJsonTableColumns();

            CriteriaUtils.invokeConsumer(tableColumns, consumer);

            return addField(new JsonTableNestedField(path, pathExp, tableColumns.endClause()));
        }


        private MySQLJsonTableColumns addPathField(final String name, final TypeItem typeItem, final boolean exists,
                                                   final Object path, final @Nullable ColumnEventClause eventClause) {
            FuncExpUtils.assertPathExp(path);

            final MappingType type;
            if (typeItem instanceof MappingType) {
                type = (MappingType) typeItem;
            } else if (typeItem instanceof TypeDef) {
                type = mapType((TypeDef) typeItem);
            } else {
                String m = String.format("don't support %s[%s]", TypeItem.class, ClassUtils.safeClassName(typeItem));
                throw ContextStack.clearStackAndCriteriaError(m);
            }

            final JsonTablePathField field;
            if (exists) {
                assert eventClause == null;
                field = new JsonTablePathField(name, type, typeItem, path);
            } else {
                field = new JsonTablePathField(name, type, typeItem, path, eventClause);
            }
            return this.addField(field);
        }

        private MySQLJsonTableColumns addField(final JsonTableColumn field) {
            List<JsonTableColumn> fieldList = this.fieldList;
            if (fieldList == null) {
                this.fieldList = fieldList = _Collections.arrayList();
            } else if (!(fieldList instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }

            fieldList.add(field);

            return this;
        }


    } // MySQLJsonTableColumns


    private static final class JsonTableFunc implements Functions._TabularFunction,
            _DerivedTable,
            _SelfDescribed {

        private static final String JSON_TABLE = "JSON_TABLE";

        private final Object jsonDoc;

        private final Object pathExp;

        private final List<JsonTableColumn> columnList;

        private final List<Selection> selectionList;

        private final Map<String, Selection> selectionMap;

        /**
         * @param columnList unmodified list
         * @see #jsonTable(Object, Object, Consumer)
         */
        private JsonTableFunc(Object jsonDoc, Object pathExp, final List<JsonTableColumn> columnList) {
            this.jsonDoc = jsonDoc;
            this.pathExp = pathExp;
            this.columnList = columnList;
            this.selectionList = obtainSelectionList(columnList);
            this.selectionMap = CriteriaUtils.createSelectionMap(this.selectionList);

            if (this.selectionMap.size() != this.selectionList.size()) {
                String m = String.format("%s column name duplication,please check", JSON_TABLE);
                throw ContextStack.clearStackAndCriteriaError(m);
            }
        }


        @Override
        public String name() {
            return JSON_TABLE;
        }

        @Nullable
        @Override
        public Selection refSelection(String name) {
            return this.selectionMap.get(name);
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            return this.selectionList;
        }


        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            context.appendFuncName(true, JSON_TABLE);

            sqlBuilder.append(_Constant.LEFT_PAREN);

            final Object jsonDoc = this.jsonDoc;
            if (jsonDoc instanceof Expression) {
                ((ArmyExpression) jsonDoc).appendSql(sqlBuilder, context);
            } else {
                context.appendLiteral(JsonType.TEXT, jsonDoc);
            }

            sqlBuilder.append(_Constant.SPACE_COMMA);

            final Object pathExp = this.pathExp;
            if (pathExp instanceof String) {
                context.appendLiteral(JsonPathType.INSTANCE, pathExp);
            } else if (pathExp instanceof Expression) {
                ((ArmyExpression) pathExp).appendSql(sqlBuilder, context);
            } else {
                // no bug ,never here
                throw new IllegalStateException();
            }

            appendJsonTableColumns(this.columnList, sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN); // right paren of function

        }


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            builder.append(_Constant.SPACE)
                    .append(JSON_TABLE)
                    .append(_Constant.LEFT_PAREN)
                    .append(this.jsonDoc)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.pathExp);

            jsonTableColumnsToString(this.columnList, builder);

            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }

        /*-------------------below static methods -------------------*/

        private static List<Selection> obtainSelectionList(final List<JsonTableColumn> columnList) {
            final int columnSize = columnList.size();

            final List<Selection> selectionList = _Collections.arrayList(columnSize);
            JsonTableColumn column;
            for (int i = 0; i < columnSize; i++) {
                column = columnList.get(i);
                if (column instanceof JsonTableNestedField) {
                    selectionList.addAll(obtainSelectionList(((JsonTableNestedField) column).columnList));
                } else {
                    selectionList.add((Selection) column);
                }
            }
            return _Collections.unmodifiableList(selectionList);
        }


        /**
         * @see #appendSql(StringBuilder, _SqlContext)
         * @see JsonTableNestedField#appendSql(StringBuilder, _SqlContext)
         */
        private static void appendJsonTableColumns(final List<JsonTableColumn> columnList,
                                                   final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(SQLs.COLUMNS.spaceRender())
                    .append(_Constant.LEFT_PAREN);

            final int columnSize = columnList.size();
            for (int i = 0; i < columnSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                columnList.get(i).appendSql(sqlBuilder, context);
            }

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        /**
         * @see #toString()
         * @see JsonTableNestedField#toString()
         */
        private static void jsonTableColumnsToString(final List<JsonTableColumn> columnList, final StringBuilder builder) {

            builder.append(SQLs.COLUMNS.spaceRender())
                    .append(_Constant.LEFT_PAREN);

            final int columnSize = columnList.size();
            for (int i = 0; i < columnSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(columnList.get(i));
            }

            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }


    } // JsonTableFunc


}
