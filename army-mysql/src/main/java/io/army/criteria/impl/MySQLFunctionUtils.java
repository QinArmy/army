package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.criteria.mysql.*;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.meta.ChildTableMeta;
import io.army.meta.TypeMeta;
import io.army.sqltype.MySQLType;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleParam;
import io.army.stmt.Stmt;
import io.army.util._Collections;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MySQLFunctionUtils extends FunctionUtils {

    private MySQLFunctionUtils() {
    }


    static MySQLWindowFunctions._OverSpec noArgWindowFunc(String name, TypeMeta returnType) {
        return new NoArgWindowFunction(name, returnType);
    }

    static MySQLWindowFunctions._OverSpec oneArgWindowFunc(
            String name, Expression arg, TypeMeta returnType) {
        if (arg instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, arg);
        }
        return new OneArgWindowFunction(name, (ArmyExpression) arg, returnType);
    }

    static MySQLWindowFunctions._OverSpec twoArgWindowFunc(
            String name, Expression one, Expression two, TypeMeta returnType) {
        return new MultiArgWindowFunction(name, null, twoExpList(name, one, two), returnType);
    }

    static MySQLWindowFunctions._OverSpec threeArgWindow(String name, Expression one, Expression two, Expression three,
                                                         TypeMeta returnType) {
        return new MultiArgWindowFunction(name, null, threeExpList(name, one, two, three), returnType);
    }


    static MySQLWindowFunctions._FromFirstLastOverSpec twoArgFromFirstWindowFunc(String name, Expression one,
                                                                                 Expression two, TypeMeta returnType) {
        return new FromFirstLastMultiArgWindowFunc(name, twoExpList(name, one, two), returnType);
    }


    static MySQLWindowFunctions._ItemAggregateWindowFunc oneArgAggregate(String name, Expression arg,
                                                                         TypeMeta returnType) {
        if (arg instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, arg);
        }
        return new OneArgAggregateWindowFunc(name, (ArmyExpression) arg, returnType);
    }

    static MySQLWindowFunctions._ItemAggregateWindowFunc oneArgAggregate(String name, @Nullable SQLWords option
            , Expression arg, TypeMeta returnType) {
        assert option == null || option == SQLs.DISTINCT || option == MySQLs.DISTINCT;
        if (arg instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, arg);
        }
        return new OneArgOptionAggregateWindowFunc(name, option, (ArmyExpression) arg, returnType);
    }

    static MySQLWindowFunctions._ItemAggregateWindowFunc multiArgAggregateWindowFunc(
            String name, @Nullable SQLWords option, List<Expression> argList, TypeMeta returnType) {
        assert option == null || option == SQLs.DISTINCT || option == MySQLs.DISTINCT;
        final List<ArmyExpression> expList = new ArrayList<>(argList.size());
        for (Expression arg : argList) {
            expList.add((ArmyExpression) arg);
        }
        return new MultiArgAggregateWindowFunc(name, option, expList, returnType);
    }


    static JsonValueClause jsonValueInnerClause() {
        return new JsonValueClause();
    }

    static SimpleExpression jsonValueFunc(Expression jsonDoc, Expression path, JsonValueClause clause) {
        final String name = "JSON_VALUE";
        if (jsonDoc instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, jsonDoc);
        } else if (path instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, path);
        }
        return new JsonValueFunction((ArmyExpression) jsonDoc, (ArmyExpression) path, clause);
    }


    static GroupConcatInnerClause groupConcatClause() {
        return new GroupConcatInnerClause();
    }

    static Expression groupConcatFunc(final @Nullable SQLs.ArgDistinct distinct, final Expression exp
            , @Nullable GroupConcatInnerClause clause) {
        return new GroupConcatFunction(distinct, Collections.singletonList((ArmyExpression) exp), clause);
    }

    static Expression groupConcatFunc(final @Nullable SQLs.ArgDistinct distinct, final List<Expression> expList
            , @Nullable GroupConcatInnerClause clause) {
        final int expSize = expList.size();
        if (expSize == 0) {
            throw CriteriaUtils.funcArgError("GROUP_CONCAT", expList);
        }
        final List<ArmyExpression> argList = new ArrayList<>(expSize);
        appendExpList(argList, expList);
        return new GroupConcatFunction(distinct, argList, clause);
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


    static <I extends Item> MySQLFunction._JsonTableLeftParenClause<I> jsonTable(Function<DerivedTable, I> function) {
        return new JsonTableFunction<>(function);
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


    private static abstract class MySQLWindowFunction extends WindowFunctionUtils.WindowFunction<MySQLWindow._PartitionBySpec>
            implements MySQLWindowFunctions._OverSpec, MySQLFunction {


        private MySQLWindowFunction(String name, TypeMeta returnType) {
            super(name, returnType);
        }


        @Override
        final boolean isDontSupportWindow(final Dialect dialect) {
            if (!(dialect instanceof MySQLDialect)) {
                throw dialectError(dialect);
            }
            return MySQLDialect.MySQL80.compareWith((MySQLDialect) dialect) < 0;
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

        private final ArmyExpression argument;

        private OneArgWindowFunction(String name, ArmyExpression argument, TypeMeta returnType) {
            super(name, returnType);
            this.argument = argument;
        }

        @Override
        final void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            this.argument.appendSql(sqlBuilder, context);
        }

        @Override
        final void argToString(final StringBuilder builder) {
            builder.append(this.argument);
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


    private static class MultiArgWindowFunction extends MySQLWindowFunction {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        private MultiArgWindowFunction(String name, @Nullable SQLWords option, List<ArmyExpression> argList,
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


    private static final class FromFirstLastMultiArgWindowFunc extends MultiArgWindowFunction
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

        private OneArgAggregateWindowFunc(String name, ArmyExpression argument, TypeMeta returnType) {
            super(name, argument, returnType);
        }


    }//OneArgAggregateWindowFunc

    private static final class OneArgOptionAggregateWindowFunc extends OneOptionArgWindowFunction
            implements MySQLWindowFunctions._ItemAggregateWindowFunc {

        /**
         * @see #oneArgAggregate(String, SQLWords, Expression, TypeMeta)
         */
        private OneArgOptionAggregateWindowFunc(String name, @Nullable SQLWords option, ArmyExpression argument,
                                                TypeMeta returnType) {
            super(name, option, argument, returnType);
        }


    }//OneArgAggregateWindowFunc


    private static final class MultiArgAggregateWindowFunc extends MultiArgWindowFunction
            implements MySQLWindowFunctions._ItemAggregateWindowFunc {

        private MultiArgAggregateWindowFunc(String name, @Nullable SQLWords option, List<ArmyExpression> argList,
                                            TypeMeta returnType) {
            super(name, option, argList, returnType);
        }

    }//MultiArgAggregateWindowFunc


    /**
     * @see #groupConcatFunc(SQLs.ArgDistinct, List, GroupConcatInnerClause)
     */
    private static final class GroupConcatFunction extends OperationExpression.SqlFunctionExpression {

        private final SQLs.ArgDistinct distinct;

        private final List<ArmyExpression> expList;

        private final GroupConcatInnerClause clause;

        private GroupConcatFunction(@Nullable SQLs.ArgDistinct distinct, List<ArmyExpression> expList
                , @Nullable GroupConcatInnerClause clause) {
            super("GROUP_CONCAT", StringType.INSTANCE);
            assert expList.size() > 0;
            this.distinct = distinct;
            this.expList = expList;
            this.clause = clause;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.distinct, this.expList, this.clause, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof GroupConcatFunction) {
                final GroupConcatFunction o = (GroupConcatFunction) obj;
                match = o.name.equals(this.name)
                        && o.distinct == this.distinct
                        && o.expList.equals(this.expList)
                        && Objects.equals(o.clause, this.clause)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {

            if (this.distinct != null) {
                sqlBuilder.append(this.distinct.spaceRender());
            }
            FunctionUtils.appendArguments(this.distinct, this.expList, context);

            if (this.clause != null) {
                this.clause.appendSql(sqlBuilder, context);
            }
        }

        @Override
        void argToString(final StringBuilder builder) {
            if (this.distinct != null) {
                builder.append(this.distinct.spaceRender());
            }
            FunctionUtils.argumentsToString(this.distinct, this.expList, builder);

            if (this.clause != null) {
                builder.append(this.clause);
            }
        }


    }//GroupConcatFunction

    /**
     * @see #groupConcatClause()
     */
    static final class GroupConcatInnerClause
            extends OrderByClause.OrderByClauseClause<MySQLFunction._GroupConcatSeparatorClause, Item>
            implements MySQLFunction._GroupConcatOrderBySpec, ArmyFuncClause, _SelfDescribed {


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
                context.identifier(stringValue, sqlBuilder);
            }
        }

        @Override
        public Clause separator(final @Nullable String strVal) {
            this.endOrderByClauseIfNeed();
            if (this.stringValue != null) {
                throw ContextStack.criteriaError(this.context, "duplicate separator");
            } else if (strVal == null) {
                throw ContextStack.nullPointer(this.context);
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
                stmt = context.parser().select((Select) statement, false, this.visible);
            } else if (statement instanceof InsertStatement) {
                stmt = context.parser().insert((InsertStatement) statement, this.visible);
            } else if (statement instanceof UpdateStatement) {
                stmt = context.parser().update((UpdateStatement) statement, false, this.visible);
            } else if (statement instanceof DeleteStatement) {
                stmt = context.parser().delete((DeleteStatement) statement, false, this.visible);
            } else if (statement instanceof Values) {
                stmt = context.parser().values((Values) statement, this.visible);
            } else if (statement instanceof DqlStatement) {
                stmt = context.parser().dialectDql((DqlStatement) statement, this.visible);
            } else if (statement instanceof DmlStatement) {
                stmt = context.parser().dialectDml((DmlStatement) statement, this.visible);
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
     * @see JsonValueFunction#appendSql(StringBuilder, _SqlContext)
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
     * @see JsonValueFunction#toString()
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


    @SuppressWarnings("unchecked")
    private static abstract class OnEmptyOrErrorAction<S extends OnEmptyOrErrorAction<S>>
            implements MySQLFunction._OnEmptyOrErrorActionClause
            , MySQLFunction._OnErrorClause
            , MySQLFunction._OnEmptyClause {

        final CriteriaContext context;

        List<_Pair<Object, JsonValueWord>> actionList;

        private Object operateValue;

        private OnEmptyOrErrorAction(CriteriaContext context) {
            this.context = context;
        }


        @Override
        public final S nullWord() {
            if (this.operateValue != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = JsonValueWord.NULL;
            return (S) this;
        }

        @Override
        public final S error() {
            if (this.operateValue != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = JsonValueWord.ERROR;
            return (S) this;
        }

        @Override
        public final S defaultValue(final Expression value) {
            if (this.operateValue != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.context);
            }
            this.operateValue = value;
            return (S) this;
        }

        @Override
        public final S defaultValue(Supplier<Expression> supplier) {
            return this.defaultValue(supplier.get());
        }

        @Override
        public final <T> S defaultValue(Function<T, Expression> valueOperator, T value) {
            return this.defaultValue(valueOperator.apply(value));
        }

        @Override
        public final S defaultValue(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
            return this.defaultValue(valueOperator.apply(function.apply(keyName)));
        }

        @Override
        public final S onError() {
            final Object operateValue = this.operateValue;
            if (operateValue == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = null;//clear
            final List<_Pair<Object, JsonValueWord>> eventHandlerList = this.actionList;
            if (eventHandlerList == null) {
                this.actionList = Collections.singletonList(_Pair.create(operateValue, JsonValueWord.ON_ERROR));
            } else if (eventHandlerList.size() == 1) {
                eventHandlerList.add(_Pair.create(operateValue, JsonValueWord.ON_ERROR));
            } else {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return (S) this;
        }

        @Override
        public final S onEmpty() {
            final Object operateValue = this.operateValue;
            if (operateValue == null || this.actionList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = null;//clear
            final List<_Pair<Object, JsonValueWord>> eventHandlerList = new ArrayList<>(2);
            this.actionList = eventHandlerList;
            eventHandlerList.add(_Pair.create(operateValue, JsonValueWord.ON_EMPTY));
            return (S) this;
        }


    }//JsonTableOnEemptyOrErrorAction


    static final class JsonValueClause extends OnEmptyOrErrorAction<JsonValueClause>
            implements MySQLFunction._JsonValueReturningSpec
            , MySQLFunction._JsonValueOptionOnEmptySpec
            , MySQLFunction._JsonValueOnEmptySpec {

        private List<Object> returningList;

        private JsonValueClause() {
            super(ContextStack.peek());
        }


        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec returning(final MySQLCastType type) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.singletonList(type);
            return this;
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec returning(final MySQLCastType type, Expression n) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (!MySQLUtils.isSingleParamType(type)) {
                throw typeError(type);
            }
            final List<Object> list = new ArrayList<>(4);

            list.add(type);
            list.add(Functions.FuncWord.LEFT_PAREN);
            list.add(n);
            list.add(Functions.FuncWord.RIGHT_PAREN);

            this.returningList = list;
            return this;
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec returning(MySQLCastType type, Expression n, SQLElement charset) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (type != MySQLCastType.CHAR) {
                throw typeError(type);
            } else if (!(charset instanceof MySQLCharset || charset instanceof SQLs.SQLIdentifierImpl)) {
                throw CriteriaUtils.funcArgError("JSON_VALUE", charset);
            }
            final List<Object> list = new ArrayList<>(5);

            list.add(type);
            list.add(Functions.FuncWord.LEFT_PAREN);
            list.add(n);
            list.add(Functions.FuncWord.RIGHT_PAREN);

            list.add(charset);
            this.returningList = list;
            return this;
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec returning(MySQLCastType type, Expression m, Expression d) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (type != MySQLCastType.DECIMAL) {
                throw typeError(type);
            }
            final List<Object> list = new ArrayList<>(6);

            list.add(type);
            list.add(Functions.FuncWord.LEFT_PAREN);
            list.add(m);
            list.add(Functions.FuncWord.COMMA);

            list.add(d);
            list.add(Functions.FuncWord.RIGHT_PAREN);
            this.returningList = list;
            return this;
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec returning(MySQLCastType type, int n) {
            return this.returning(type, SQLs.literal(IntegerType.INSTANCE, n));
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec returning(MySQLCastType type, int m, int d) {
            return this.returning(type, SQLs.literal(IntegerType.INSTANCE, m), SQLs.literal(IntegerType.INSTANCE, d));
        }


        private CriteriaException typeError(MySQLCastType type) {
            String m = String.format("%s error", type);
            return ContextStack.criteriaError(this.context, m);
        }


    }//JsonValueClause


    private static final class JsonValueFunction extends OperationExpression.SqlFunctionExpression implements MySQLFunction {

        private final ArmyExpression jsonDoc;

        private final ArmyExpression path;

        private final List<Object> returningList;

        private final List<_Pair<Object, JsonValueWord>> eventHandlerList;


        private JsonValueFunction(ArmyExpression jsonDoc, ArmyExpression path, JsonValueClause clause) {
            super("JSON_VALUE", StringType.INSTANCE);
            this.jsonDoc = jsonDoc;
            this.path = path;
            this.returningList = clause.returningList;
            this.eventHandlerList = clause.actionList;

        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.jsonDoc, this.path, this.returningList, this.eventHandlerList,
                    this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof JsonValueFunction) {
                final JsonValueFunction o = (JsonValueFunction) obj;
                match = o.name.equals(this.name)
                        && o.jsonDoc.equals(this.jsonDoc)
                        && Objects.equals(o.path, this.path)
                        && Objects.equals(o.returningList, this.returningList)
                        && Objects.equals(o.eventHandlerList, this.eventHandlerList)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {

            this.jsonDoc.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            this.path.appendSql(sqlBuilder, context);

            final List<Object> returningList = this.returningList;
            if (returningList != null) {
                assert returningList.get(0) instanceof MySQLCastType;
                for (Object o : returningList) {
                    if (o instanceof MySQLCastType) {
                        sqlBuilder.append(_Constant.SPACE_RETURNING)
                                .append(((MySQLCastType) o).spaceRender());
                    } else if (o == Functions.FuncWord.LEFT_PAREN) {
                        sqlBuilder.append(_Constant.LEFT_PAREN);
                    } else if (o instanceof SQLWords) {
                        sqlBuilder.append(((SQLWords) o).spaceRender());
                    } else if (o instanceof Expression) {
                        ((ArmyExpression) o).appendSql(sqlBuilder, context);
                    } else if (o instanceof SQLIdentifier) {
                        sqlBuilder.append(_Constant.SPACE);
                        context.identifier(((SQLIdentifier) o).render(), sqlBuilder);
                    } else {
                        //no bug,never here
                        throw new IllegalStateException();
                    }

                }//for
            }//if

            final List<_Pair<Object, JsonValueWord>> eventHandlerList = this.eventHandlerList;
            if (eventHandlerList != null) {
                appendOnEmptyOrErrorClause(eventHandlerList, context);
            }//if

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.jsonDoc)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.path);

            final List<Object> returningList = this.returningList;
            if (returningList != null) {
                assert returningList.get(0) instanceof MySQLCastType;
                for (Object o : returningList) {
                    if (o instanceof MySQLCastType) {
                        builder.append(_Constant.SPACE_RETURNING)
                                .append(((MySQLCastType) o).spaceRender());
                    } else if (o == Functions.FuncWord.LEFT_PAREN) {
                        builder.append(_Constant.LEFT_PAREN);
                    } else if (o instanceof SQLWords) {
                        builder.append(((SQLWords) o).spaceRender());
                    } else if (o instanceof Expression) {
                        builder.append(o);
                    } else if (o instanceof SQLIdentifier) {
                        builder.append(_Constant.SPACE);
                        builder.append(o);
                    } else {
                        //no bug,never here
                        throw new IllegalStateException();
                    }

                }//for
            }//if

            final List<_Pair<Object, JsonValueWord>> eventHandlerList = this.eventHandlerList;
            if (eventHandlerList != null) {
                onEmptyOrErrorClauseToString(eventHandlerList, builder);
            }//if
        }


    }//JsonValueFunction


    private interface JsonTableColumn extends _SelfDescribed {

    }


    private static final class JsonTableForOrdinalityColumn
            implements JsonTableColumn, Selection {

        private final String name;

        private JsonTableForOrdinalityColumn(String name) {
            this.name = name;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            sqlBuilder.append(_Constant.SPACE);
            context.parser().identifier(this.name, sqlBuilder);
            sqlBuilder.append(MySQLs.FOR_ORDINALITY.spaceRender());
        }

        @Override
        public String alias() {
            return this.name;
        }

        @Override
        public TypeMeta typeMeta() {
            return LongType.INSTANCE;
        }


    }//JsonTableForOrdinalityColumn


    private static final class JsonTableOnEmptyOrErrorAction
            extends OnEmptyOrErrorAction<JsonTableOnEmptyOrErrorAction> {

        private JsonTableOnEmptyOrErrorAction(CriteriaContext context) {
            super(context);
        }


    }//JsonTableOnEmptyOrErrorAction


    private static final class JsonTablePathColumn implements JsonTableColumn
            , Selection {

        private final String name;

        private final MappingType typeMeta;
        private final MySQLType type;

        private final ArmyExpression n;

        private final ArmyExpression d;

        private final SQLElement charset;

        private final SQLIdentifier collate;

        private final SQLWords pathWord;

        private final ArmyExpression path;

        private JsonTableOnEmptyOrErrorAction actionClause;

        private JsonTablePathColumn(String name, MySQLType type
                , SQLWords pathWord, Expression path) {
            this.name = name;
            this.type = type;
            this.typeMeta = type.mappingType();
            this.n = null;

            this.d = null;
            this.charset = null;
            this.collate = null;
            this.pathWord = pathWord;

            this.path = (ArmyExpression) path;
        }

        private JsonTablePathColumn(String name, MySQLType type
                , Expression n, SQLWords pathWord
                , Expression path) {
            this.name = name;
            this.type = type;
            this.typeMeta = type.mappingType();
            this.n = (ArmyExpression) n;

            this.d = null;
            this.charset = null;
            this.collate = null;
            this.pathWord = pathWord;

            this.path = (ArmyExpression) path;
        }

        private JsonTablePathColumn(String name, MySQLType type
                , Expression n, SQLElement charset
                , @Nullable SQLIdentifier collate, SQLWords pathWord
                , Expression path) {
            this.name = name;
            this.type = type;
            this.typeMeta = type.mappingType();
            this.n = (ArmyExpression) n;

            this.d = null;
            this.charset = charset;
            this.collate = collate;
            this.pathWord = pathWord;

            this.path = (ArmyExpression) path;
        }

        private JsonTablePathColumn(String name, MySQLType type
                , Expression n, Expression d
                , SQLWords pathWord, Expression path) {
            this.name = name;
            this.type = type;
            this.typeMeta = type.mappingType();
            this.n = (ArmyExpression) n;

            this.d = (ArmyExpression) d;
            this.charset = null;
            this.collate = null;
            this.pathWord = pathWord;

            this.path = (ArmyExpression) path;
        }

        @Override
        public String alias() {
            return this.name;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.typeMeta;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            sqlBuilder.append(_Constant.SPACE);
            context.parser().identifier(this.name, sqlBuilder)
                    .append(this.type.spaceRender());

            if (this.n != null) {
                sqlBuilder.append(_Constant.LEFT_PAREN);
                this.n.appendSql(sqlBuilder, context);
                if (this.d != null) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                    this.d.appendSql(sqlBuilder, context);
                }
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            final SQLElement charset = this.charset;
            DialectParser parser = null;
            if (charset != null) {
                sqlBuilder.append(" CHARACTER SET");
                if (charset instanceof MySQLCharset) {
                    sqlBuilder.append(((MySQLCharset) charset).spaceRender());
                } else if (charset instanceof SQLIdentifier) {
                    parser = context.parser();
                    parser.identifier(((SQLIdentifier) charset).render(), sqlBuilder);
                } else {
                    //no bug,never here
                    throw new IllegalStateException();
                }
            }
            final SQLIdentifier collate = this.collate;
            if (collate != null) {
                sqlBuilder.append(" COLLATE ");
                if (parser == null) {
                    parser = context.parser();
                }
                parser.identifier(collate.render(), sqlBuilder);
            }

            sqlBuilder.append(this.pathWord.spaceRender());
            this.path.appendSql(sqlBuilder, context);

            final JsonTableOnEmptyOrErrorAction actionClause = this.actionClause;
            if (actionClause != null) {
                assert this.pathWord == MySQLs.PATH;
                final List<_Pair<Object, JsonValueWord>> actionList = actionClause.actionList;
                if (actionList != null) {
                    MySQLFunctionUtils.appendOnEmptyOrErrorClause(actionList, context);
                }
            }

        }


        @Override
        public String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(this.type.spaceRender());

            if (this.n != null) {
                sqlBuilder.append(_Constant.LEFT_PAREN)
                        .append(this.n);
                if (this.d != null) {
                    sqlBuilder.append(_Constant.SPACE_COMMA)
                            .append(this.d);
                }
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            final SQLElement charset = this.charset;
            if (charset != null) {
                sqlBuilder.append(" CHARACTER SET");
                if (charset instanceof MySQLCharset) {
                    sqlBuilder.append(((MySQLCharset) charset).spaceRender());
                } else if (charset instanceof SQLIdentifier) {
                    sqlBuilder.append(_Constant.SPACE)
                            .append(((SQLIdentifier) charset).render());
                } else {
                    //no bug,never here
                    throw new IllegalStateException();
                }
            }
            final SQLIdentifier collate = this.collate;
            if (collate != null) {
                sqlBuilder.append(" COLLATE ")
                        .append(collate.render());
            }

            sqlBuilder.append(this.pathWord.spaceRender())
                    .append(this.path);

            final JsonTableOnEmptyOrErrorAction actionClause = this.actionClause;
            if (actionClause != null) {
                assert this.pathWord == MySQLs.PATH;
                final List<_Pair<Object, JsonValueWord>> actionList = actionClause.actionList;
                if (actionList != null) {
                    MySQLFunctionUtils.onEmptyOrErrorClauseToString(actionList, sqlBuilder);
                }
            }

            return sqlBuilder.toString();
        }


    }//JsonTablePathColumn


    private static abstract class JsonTableColumnsClause<R extends Item>
            implements MySQLFunction._JsonTableColumnLeftParenClause<R>
            , MySQLFunction._JsonTableColumnCommaSpec<R>
            , MySQLFunction._JsonTableOnEmptyActionSpec<R>
            , MySQLFunction._JsonTableColumnsClause<R>
            , MySQLFunction._JsonTableOnEmptySpec<R>
            , MySQLJsonColumnClause
            , MySQLFunction._JsonTableDynamicOnEmptyActionSpec
            , MySQLFunction._JsonTableDynamicOnEmptySpec {

        final CriteriaContext context;

        private final Consumer<Selection> selectionConsumer;

        List<JsonTableColumn> columnList;

        private JsonTableColumnsClause(CriteriaContext context) {
            assert this instanceof JsonTableFunction;
            this.context = context;
            this.selectionConsumer = this::onAddSelect;
        }

        private JsonTableColumnsClause(CriteriaContext context, Consumer<Selection> selectionConsumer) {
            assert this instanceof JsonTableNestedColumn;
            this.context = context;
            this.selectionConsumer = selectionConsumer;
        }


        @Override
        public final MySQLFunction._JsonTableColumnLeftParenClause<R> columns() {
            if (this.columnList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this;
        }

        @Override
        public final R columns(Consumer<MySQLJsonColumnClause> consumer) {
            if (this.columnList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            consumer.accept(this);
            if (this.columnList == null) {
                throw ContextStack.criteriaError(this.context, "You don't add any column");
            }
            return this.rightParen();
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, SQLs.WordsForOrdinality forOrdinality) {
            return this.comma(name, forOrdinality);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLType type, SQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLType type
                , Expression n, SQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLType type
                , int n, SQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLType type
                , Expression n, SQLElement charset
                , SQLs.WordPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, n, charset, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLType type
                , int n, SQLElement charset, SQLIdentifier collate
                , SQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLType type
                , Expression n, SQLElement charset
                , SQLIdentifier collate, SQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLType type
                , int p, int m
                , SQLs.WordPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLType type
                , Expression p, Expression m
                , SQLs.WordPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLType type
                , MySQLs.WordExistsPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLType type
                , Expression n, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLType type
                , int n, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLType type
                , Expression n, SQLElement charset
                , MySQLs.WordExistsPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, n, charset, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLType type
                , int n, SQLElement charset
                , SQLIdentifier collate, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLType type
                , Expression n, SQLElement charset
                , SQLIdentifier collate, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLType type
                , int p, int m
                , MySQLs.WordExistsPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLType type
                , Expression p, Expression m
                , MySQLs.WordExistsPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause nested(Expression path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function) {
            return this.commaNested(path, function);
        }

        @Override
        public final MySQLJsonColumnClause nested(Function<String, Expression> operator, String path
                , Function<MySQLJsonNestedClause, MySQLJsonColumns> function) {
            return this.commaNested(operator.apply(path), function);
        }

        @Override
        public final MySQLJsonColumnClause nestedPath(Expression path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function) {
            return this.commaNestedPath(path, function);
        }

        @Override
        public final MySQLJsonColumnClause nestedPath(Function<String, Expression> operator, String path
                , Function<MySQLJsonNestedClause, MySQLJsonColumns> function) {
            return this.commaNestedPath(operator.apply(path), function);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, SQLs.WordsForOrdinality forOrdinality) {
            return this.comma(name, forOrdinality);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type
                , SQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, Expression n
                , SQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, int n
                , SQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, Expression n
                , SQLElement charset, SQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, int n
                , SQLElement charset, SQLIdentifier collate, SQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, Expression n
                , SQLElement charset, SQLIdentifier collate, SQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, int p, int m
                , SQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, Expression p
                , Expression m, SQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, Expression n
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, int n
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, Expression n
                , SQLElement charset, MySQLs.WordExistsPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, n, charset, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, int n
                , SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, Expression n
                , SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, int p, int m
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLType type, Expression p
                , Expression m, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParenNested(Expression path
                , Function<MySQLJsonNestedClause, MySQLJsonColumns> function) {
            return this.commaNested(path, function);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParenNested(Function<String, Expression> operator
                , String path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function) {
            return this.commaNested(operator.apply(path), function);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParenNestedPath(Expression path
                , Function<MySQLJsonNestedClause, MySQLJsonColumns> function) {
            return this.commaNestedPath(path, function);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParenNestedPath(Function<String, Expression> operator
                , String path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function) {
            return this.commaNestedPath(operator.apply(path), function);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name
                , SQLs.WordsForOrdinality forOrdinality) {
            assert forOrdinality == MySQLs.FOR_ORDINALITY;
            this.onAddColumn(new JsonTableForOrdinalityColumn(name));
            return this;
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type
                , SQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.PATH;
            if (!type.isNoPrecision()) {
                throw MySQLUtils.noPrecision(this.context, type);
            }
            this.onAddColumn(new JsonTablePathColumn(name, type, path, operator.apply(stringPath)));
            return this;
        }


        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type, Expression n
                , SQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.PATH;
            if (!type.isSupportPrecision()) {
                throw MySQLUtils.dontSupportPrecision(this.context, type);
            }
            final JsonTablePathColumn column;
            column = new JsonTablePathColumn(name, type, n, path, operator.apply(stringPath));
            return this.onAddColumn(column);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type, int n
                , SQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, SQLs.literal(IntegerType.INSTANCE, n), path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type, Expression n
                , SQLElement charset, SQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.PATH;
            return this.addColumnWithCharset(name, type, n, charset, null, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type, int n
                , SQLElement charset, SQLIdentifier collate, SQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            final Expression expOfn;
            expOfn = SQLs.literal(IntegerType.INSTANCE, n);
            return this.comma(name, type, expOfn, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type
                , Expression n, SQLElement charset, @Nullable SQLIdentifier collate, SQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.PATH;
            if (collate == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return this.addColumnWithCharset(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type, int p, int m
                , SQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            final Expression expOfp, expOfm;
            expOfp = SQLs.literal(IntegerType.INSTANCE, p);
            expOfm = SQLs.literal(IntegerType.INSTANCE, m);
            return this.comma(name, type, expOfp, expOfm, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type, Expression p
                , Expression m, SQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.PATH;
            if (!type.isSupportPrecisionScale()) {
                throw MySQLUtils.dontSupportPrecisionScale(this.context, type);
            }
            final JsonTablePathColumn column;
            column = new JsonTablePathColumn(name, type, p, m, path, operator.apply(stringPath));
            return this.onAddColumn(column);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.EXISTS_PATH;
            if (!type.isNoPrecision()) {
                throw MySQLUtils.noPrecision(this.context, type);
            }
            this.onAddColumn(new JsonTablePathColumn(name, type, path, operator.apply(stringPath)));
            return this;
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type, Expression n
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.EXISTS_PATH;
            if (!type.isSupportPrecision()) {
                throw MySQLUtils.dontSupportPrecision(this.context, type);
            }
            final JsonTablePathColumn column;
            column = new JsonTablePathColumn(name, type, n, path, operator.apply(stringPath));
            return this.onAddColumn(column);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type, int n
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, SQLs.literal(IntegerType.INSTANCE, n), path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type, Expression n
                , SQLElement charset, MySQLs.WordExistsPath path, Function<String, Expression> operator
                , String stringPath) {
            assert path == MySQLs.EXISTS_PATH;
            return this.addColumnWithCharset(name, type, n, charset, null, path, operator, stringPath);
        }


        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type, int n
                , SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            final Expression expOfn;
            expOfn = SQLs.literal(IntegerType.INSTANCE, n);
            return this.comma(name, type, expOfn, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type, Expression n
                , SQLElement charset, @Nullable SQLIdentifier collate, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.EXISTS_PATH;
            if (collate == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return this.addColumnWithCharset(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type, int p, int m
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            final Expression expOfp, expOfm;
            expOfp = SQLs.literal(IntegerType.INSTANCE, p);
            expOfm = SQLs.literal(IntegerType.INSTANCE, m);
            return this.comma(name, type, expOfp, expOfm, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLType type, Expression p
                , Expression m, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            if (!type.isSupportPrecisionScale()) {
                throw MySQLUtils.dontSupportPrecisionScale(this.context, type);
            }
            assert path == MySQLs.EXISTS_PATH;
            final JsonTablePathColumn column;
            column = new JsonTablePathColumn(name, type, p, m, path, operator.apply(stringPath));
            return this.onAddColumn(column);
        }

        @Override
        public final JsonTableColumnsClause<R> commaNested(final @Nullable Expression path
                , Function<MySQLJsonNestedClause, MySQLJsonColumns> function) {
            return this.handleNested(path, function, false);
        }

        @Override
        public final JsonTableColumnsClause<R> commaNested(Function<String, Expression> operator
                , String path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function) {
            return this.handleNested(operator.apply(path), function, false);
        }

        @Override
        public final JsonTableColumnsClause<R> commaNestedPath(Expression path
                , Function<MySQLJsonNestedClause, MySQLJsonColumns> function) {
            return this.handleNested(path, function, true);
        }

        @Override
        public final JsonTableColumnsClause<R> commaNestedPath(Function<String, Expression> operator
                , String path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function) {
            return this.handleNested(operator.apply(path), function, true);
        }


        @Override
        public final JsonTableColumnsClause<R> nullWord() {
            this.getActionClause().nullWord();
            return this;
        }

        @Override
        public final JsonTableColumnsClause<R> error() {
            this.getActionClause().error();
            return this;
        }

        @Override
        public final JsonTableColumnsClause<R> defaultValue(Expression value) {
            this.getActionClause().defaultValue(value);
            return this;
        }

        @Override
        public final <T> JsonTableColumnsClause<R> defaultValue(Function<T, Expression> valueOperator, T value) {
            return this.defaultValue(valueOperator.apply(value));
        }

        @Override
        public final JsonTableColumnsClause<R> defaultValue(Function<Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            return this.defaultValue(valueOperator.apply(function.apply(keyName)));
        }

        @Override
        public final JsonTableColumnsClause<R> defaultValue(Supplier<Expression> supplier) {
            return this.defaultValue(supplier.get());
        }

        @Override
        public final JsonTableColumnsClause<R> onError() {
            this.getActionClause().onError();
            return this;
        }

        @Override
        public final JsonTableColumnsClause<R> onEmpty() {
            this.getActionClause().onEmpty();
            return this;
        }


        @Override
        public final R rightParen() {
            final List<JsonTableColumn> columnList = this.columnList;
            if (!(columnList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.columnList = _Collections.unmodifiableList(columnList);
            return this.onRightParen();
        }


        abstract R onRightParen();

        void onAddSelect(Selection selection) {
            throw new UnsupportedOperationException();
        }

        private JsonTableColumnsClause<R> onAddColumn(final JsonTableColumn column) {
            List<JsonTableColumn> columnList = this.columnList;
            if (columnList == null) {
                columnList = _Collections.arrayList();
                this.columnList = columnList;
            } else if (!(columnList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            columnList.add(column);
            if (!(column instanceof JsonTableNestedColumn)) {
                this.selectionConsumer.accept((Selection) column);
            }
            return this;
        }

        private JsonTableColumnsClause<R> addColumnWithCharset(String name, MySQLType type, Expression n
                , SQLElement charset, @Nullable SQLIdentifier collate
                , SQLWords path, Function<String, Expression> operator
                , String stringPath) {
            if (!type.isSupportPrecision()) {
                throw MySQLUtils.dontSupportPrecision(this.context, type);
            } else if (!type.isSupportCharset()) {
                throw MySQLUtils.dontSupportCharset(this.context, type);
            } else if (!(charset instanceof MySQLCharset || charset instanceof SQLs.SQLIdentifierImpl)) {
                throw CriteriaUtils.funcArgError("JSON_TABLE", charset);
            } else if (!(collate == null || collate instanceof SQLs.SQLIdentifierImpl)) {
                throw CriteriaUtils.funcArgError("JSON_TABLE", collate);
            }
            final JsonTablePathColumn column;
            column = new JsonTablePathColumn(name, type, n, charset, collate, path, operator.apply(stringPath));
            return this.onAddColumn(column);
        }

        private JsonTableOnEmptyOrErrorAction getActionClause() {
            final List<JsonTableColumn> columnList = this.columnList;
            if (columnList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final JsonTableColumn column;
            column = columnList.get(columnList.size() - 1);
            if (!(column instanceof JsonTablePathColumn)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final JsonTablePathColumn pathColumn = (JsonTablePathColumn) column;
            if (pathColumn.pathWord != MySQLs.PATH) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            JsonTableOnEmptyOrErrorAction actionClause = pathColumn.actionClause;
            if (actionClause == null) {
                actionClause = new JsonTableOnEmptyOrErrorAction(this.context);
                pathColumn.actionClause = actionClause;
            }
            return actionClause;
        }

        private JsonTableColumnsClause<R> handleNested(final @Nullable Expression path
                , Function<MySQLJsonNestedClause, MySQLJsonColumns> function, final boolean fullWord) {
            if (path == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final JsonTableNestedColumn column;
            column = new JsonTableNestedColumn(this.context, fullWord, path, this.selectionConsumer, this::onAddColumn);
            if (function.apply(column) != column) {
                String m = String.format("error %s", MySQLJsonColumns.class.getName());
                throw ContextStack.criteriaError(this.context, m);
            } else if (column.columnList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this;
        }


        static void appendColumnList(List<JsonTableColumn> columnList, final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" COLUMNS(");

            final int size = columnList.size();
            assert size > 0;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                columnList.get(i).appendSql(sqlBuilder, context);
            }

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        static void columnListToString(List<JsonTableColumn> columnList, final StringBuilder sqlBuilder) {
            sqlBuilder.append(" COLUMNS(");
            final int size = columnList.size();
            assert size > 0;
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                sqlBuilder.append(columnList.get(i));
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


    }//JsonTableColumnsClause


    private static final class JsonTableNestedColumn
            extends JsonTableColumnsClause<MySQLJsonColumns>
            implements JsonTableColumn, MySQLJsonColumns
            , MySQLJsonNestedClause {

        private final boolean fullPathWord;

        private final ArmyExpression path;

        private final Consumer<JsonTableNestedColumn> consumer;

        private JsonTableNestedColumn(CriteriaContext context, boolean fullPathWord
                , final Expression path, Consumer<Selection> selectionConsumer
                , Consumer<JsonTableNestedColumn> consumer) {
            super(context, selectionConsumer);
            this.fullPathWord = fullPathWord;
            this.path = (ArmyExpression) path;
            this.consumer = consumer;

        }


        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            sqlBuilder.append(" NESTED");
            if (this.fullPathWord) {
                sqlBuilder.append(" PATH");
            }
            this.path.appendSql(sqlBuilder, context);

            final List<JsonTableColumn> columnList = this.columnList;
            assert columnList != null;
            appendColumnList(columnList, context);

        }


        @Override
        public int hashCode() {
            return Objects.hash(this.fullPathWord, this.path, this.columnList);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof JsonTableNestedColumn) {
                final JsonTableNestedColumn o = (JsonTableNestedColumn) obj;
                match = o.fullPathWord == this.fullPathWord
                        && o.path.equals(this.path)
                        && Objects.equals(o.columnList, this.columnList);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder()
                    .append(" NESTED");
            if (this.fullPathWord) {
                sqlBuilder.append(" PATH");
            }
            sqlBuilder.append(this.path);
            final List<JsonTableColumn> columnList = this.columnList;
            if (columnList == null) {
                sqlBuilder.append(" undefined");
            } else {
                columnListToString(columnList, sqlBuilder);
            }
            return sqlBuilder.toString();
        }


        @Override
        MySQLJsonColumns onRightParen() {
            this.consumer.accept(this);
            return this;
        }


    }//JsonTableNestedColumn

    static final class JsonTableFunction<R extends Item> extends JsonTableColumnsClause<R>
            implements TabularItem, MySQLFunction, _SelfDescribed
            , MySQLFunction._JsonTableLeftParenClause<R>
            , _DerivedTable {

        private ArmyExpression expr;

        private ArmyExpression path;

        private final Function<DerivedTable, R> function;

        private List<_Selection> selectionList = _Collections.arrayList();

        private Map<String, Selection> selectionMap = _Collections.hashMap();

        private List<String> columnAliasList;

        private JsonTableFunction(Function<DerivedTable, R> function) {
            super(ContextStack.peek());
            this.function = function;
        }

        @Override
        public String name() {
            return "JSON_TABLE";
        }

        @Override
        public _JsonTableColumnsClause<R> leftParen(final Expression expr, final Expression path) {
            if (this.expr != null || this.path != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (!(expr instanceof ArmyExpression && path instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.context);
            }
            this.expr = (ArmyExpression) expr;
            this.path = (ArmyExpression) path;
            return this;
        }

        @Override
        public _JsonTableColumnsClause<R> leftParen(Expression expr, Function<String, Expression> valueOperator
                , String path) {
            return this.leftParen(expr, valueOperator.apply(path));
        }

        @Override
        public _JsonTableColumnsClause<R> leftParen(String expr, Function<String, Expression> valueOperator
                , String path) {
            return this.leftParen(SQLs.param(StringType.INSTANCE, expr), valueOperator.apply(path));
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.expr, this.path, this.columnList);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof JsonTableFunction) {
                final JsonTableFunction<?> o = (JsonTableFunction<?>) obj;
                match = Objects.equals(o.expr, this.expr)
                        && Objects.equals(o.path, this.path)
                        && Objects.equals(o.columnList, this.columnList);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(" JSON_TABLE(");

            final ArmyExpression expr = this.expr, path = this.path;
            assert expr != null && path != null;

            expr.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            path.appendSql(sqlBuilder, context);

            final List<JsonTableColumn> columnList = this.columnList;
            assert columnList != null;
            appendColumnList(columnList, context);


        }

        @Override
        public String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder()
                    .append(" JSON_TABLE(");

            final ArmyExpression expr = this.expr, path = this.path;
            final List<JsonTableColumn> columnList = this.columnList;

            if (expr == null || path == null || columnList == null) {
                sqlBuilder.append(" undefined , undefined undefined");
            } else {
                sqlBuilder.append(expr)
                        .append(_Constant.SPACE_COMMA)
                        .append(path);
                columnListToString(columnList, sqlBuilder);
            }
            return sqlBuilder.toString();
        }


        @Override
        public List<? extends Selection> refAllSelection() {
            final List<_Selection> selectionList = this.selectionList;
            if (selectionList == null || selectionList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return selectionList;
        }

        @Override
        public Selection refSelection(final String derivedAlias) {
            final Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null || selectionMap instanceof HashMap) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return selectionMap.get(derivedAlias);
        }


        @Override
        void onAddSelect(final Selection selection) {
            final List<_Selection> selectionList = this.selectionList;
            final Map<String, Selection> selectionMap = this.selectionMap;
            if (!(selectionList instanceof ArrayList && selectionMap instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (selectionMap.putIfAbsent(selection.alias(), selection) != null) {
                String m = String.format("Duplicate column name[%s]", selection.alias());
                throw ContextStack.criteriaError(this.context, m);
            }
            selectionList.add((_Selection) selection);
        }

        @Override
        R onRightParen() {
            if (this.expr == null || this.path == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final List<_Selection> selectionList = this.selectionList;
            final Map<String, Selection> selectionMap = this.selectionMap;
            if (!(selectionList instanceof ArrayList && selectionMap instanceof HashMap && selectionList.size() > 0)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.selectionList = _Collections.unmodifiableList(selectionList);
            this.selectionMap = _Collections.unmodifiableMap(selectionMap);
            return this.function.apply(this);
        }


    }//JsonTableFunction


}
