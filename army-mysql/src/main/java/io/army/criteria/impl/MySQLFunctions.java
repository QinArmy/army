package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLCharset;
import io.army.criteria.mysql.MySQLClause;
import io.army.criteria.mysql.MySQLUnit;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.mapping.StringType;
import io.army.meta.TypeMeta;
import io.army.sqltype.MySQLTypes;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleParam;
import io.army.stmt.Stmt;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MySQLFunctions extends SQLFunctions {

    private MySQLFunctions() {
    }

    static Expression intervalTimeFunc(String name, @Nullable Object date, @Nullable Object expr
            , @Nullable MySQLUnit unit, TypeMeta returnType) {
        if (unit == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        return new IntervalTimeFunc(name, SQLs._funcParam(date), SQLs._funcParam(expr), unit, returnType);
    }

    static MySQLFuncSyntax._OverSpec noArgWindowFunc(String name, TypeMeta returnType) {
        return new NoArgWindowFunc(name, returnType);
    }

    @Deprecated
    static MySQLFuncSyntax._OverSpec oneArgWindowFunc(String name, @Nullable SQLWords option
            , @Nullable Object expr, TypeMeta returnType) {
        return new OneArgWindowFunc_(name, option, SQLs._funcParam(expr), returnType);
    }

    static MySQLFuncSyntax._OverSpec oneArgWindowFunc(String name, Expression expr, TypeMeta returnType) {
        if (expr instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, expr);
        }
        return new OneArgWindowFunc(name, (ArmyExpression) expr, returnType);
    }

    static MySQLFuncSyntax._OverSpec twoArgWindow(String name, Expression one, Expression two, TypeMeta returnType) {
        return new ComplexArgWindowFunc(name, twoArgList(name, one, two), returnType);
    }

    static MySQLFuncSyntax._OverSpec threeArgWindow(String name, Expression one, Expression two
            , Expression three, TypeMeta returnType) {
        return new ComplexArgWindowFunc(name, threeArgList(name, one, two, three), returnType);
    }


    static MySQLFuncSyntax._OverSpec safeMultiArgWindowFunc(String name, @Nullable SQLWords option
            , List<ArmyExpression> argList, TypeMeta returnType) {
        return new MultiArgWindowFunc(name, option, argList, null, returnType);
    }

    static MySQLFuncSyntax._FromFirstLastSpec fromFirstWindowFunc(String name, List<?> argList
            , TypeMeta returnType) {
        return new FromFirstLastMultiArgWindowFunc(name, argList, returnType);
    }


    static MySQLFuncSyntax._AggregateOverSpec oneArgAggregateWindow(String name, Expression arg, TypeMeta returnType) {
        if (arg instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, arg);
        }
        return new OneArgAggregateWindowFunc(name, (ArmyExpression) arg, returnType);
    }

    static MySQLFuncSyntax._AggregateOverSpec twoArgAggregateWindow(String name, Expression one, Expression two
            , TypeMeta returnType) {
        return new ComplexArgAggregateWindowFunc(name, twoArgList(name, one, two), returnType);
    }

    static MySQLFuncSyntax._AggregateOverSpec complexAggregateWindow(String name, List<?> argList, TypeMeta returnType) {
        return new ComplexArgAggregateWindowFunc(name, argList, returnType);
    }


    static MySQLClause._JsonValueReturningSpec jsonValueFunc(final Expression jsonDoc, final Expression path) {
        final String name = "JSON_VALUE";
        if (jsonDoc instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, jsonDoc);
        }
        if (path instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, path);
        }
        return new JsonValueFunc((ArmyExpression) jsonDoc, (ArmyExpression) path);
    }


    static GroupConcatClause groupConcatClause(final boolean distinct, final Object exprOrList) {
        final List<ArmyExpression> list;
        if (exprOrList instanceof Expression) {
            list = Collections.singletonList((ArmyExpression) exprOrList);
        } else if (exprOrList instanceof List) {
            final List<?> argList = (List<?>) exprOrList;
            list = new ArrayList<>(((argList.size() << 1) - 1));
            for (Object arg : argList) {
                if (!(arg instanceof Expression)) {
                    throw CriteriaUtils.funcArgError("GROUP_CONCAT", exprOrList);
                }
                list.add((ArmyExpression) arg);
            }
        } else {
            throw CriteriaUtils.funcArgError("GROUP_CONCAT", exprOrList);
        }
        return new GroupConcatClause(distinct, list);
    }


    static Expression statementDigest(final PrimaryStatement statement, final Visible visible, final boolean literal) {
        return new StatementDigestFunc("STATEMENT_DIGEST", statement, visible, literal, StringType.INSTANCE);
    }

    static Expression statementDigest(final String statement, final Visible visible, final boolean literal) {
        return new StatementDigestFunc("STATEMENT_DIGEST", statement, visible, literal, StringType.INSTANCE);
    }

    static Expression statementDigestText(final PrimaryStatement statement, final Visible visible, final boolean literal) {
        return new StatementDigestFunc("STATEMENT_DIGEST_TEXT", statement, visible, literal, StringType.INSTANCE);
    }

    static Expression statementDigestText(final String statement, final Visible visible, final boolean literal) {
        return new StatementDigestFunc("STATEMENT_DIGEST_TEXT", statement, visible, literal, StringType.INSTANCE);
    }

    static MySQLClause._JsonTableColumnsClause<TabularItem> jsonTable(Expression expr, Expression path) {
        final String name = "JSON_TABLE";
        if (expr instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, expr);
        }
        if (path instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, expr);
        }
        return new JsonTable((ArmyExpression) expr, (ArmyExpression) path);
    }


    private static abstract class MySQLWindowFunc extends WindowFunc<Window._SimpleLeftParenClause<Void, Expression>>
            implements Window._SimpleLeftParenClause<Void, Expression>, MySQLFuncSyntax._OverSpec {

        private MySQLWindowFunc(String name, TypeMeta returnType) {
            super(name, returnType);
        }


        @Override
        public final Window._SimplePartitionBySpec<Void, Expression> leftParen() {
            return SimpleWindow.anonymousWindow(this.context, this::windowEnd)
                    .leftParen();
        }

        @Override
        public final Window._SimplePartitionBySpec<Void, Expression> leftParen(String existingWindowName) {
            return SimpleWindow.anonymousWindow(this.context, this::windowEnd)
                    .leftParen(existingWindowName);
        }

        @Override
        public final Window._SimplePartitionBySpec<Void, Expression> leftParen(Supplier<String> supplier) {
            return SimpleWindow.anonymousWindow(this.context, this::windowEnd)
                    .leftParen(supplier);
        }

        @Override
        public final Window._SimplePartitionBySpec<Void, Expression> leftParen(Function<Void, String> function) {
            return SimpleWindow.anonymousWindow(this.context, this::windowEnd)
                    .leftParen(function);
        }

        @Override
        public final Window._SimplePartitionBySpec<Void, Expression> leftParenIf(Supplier<String> supplier) {
            return SimpleWindow.anonymousWindow(this.context, this::windowEnd)
                    .leftParenIf(supplier);
        }

        @Override
        public final Window._SimplePartitionBySpec<Void, Expression> leftParenIf(Function<Void, String> function) {
            return SimpleWindow.anonymousWindow(this.context, this::windowEnd)
                    .leftParenIf(function);
        }


    }//MySQLWindowFunc

    private static class NoArgWindowFunc extends MySQLWindowFunc implements SQLFunctions.NoArgFunction {

        private NoArgWindowFunc(String name, TypeMeta returnType) {
            super(name, returnType);
        }

        @Override
        final void appendArguments(final _SqlContext context) {
            //no argument,no-op
        }

        @Override
        final void argumentToString(final StringBuilder builder) {
            //no argument,no-op
        }

    }//NoArgWindowFunc


    private static class OneArgWindowFunc extends MySQLWindowFunc {

        private final ArmyExpression argument;

        private OneArgWindowFunc(String name, ArmyExpression argument, TypeMeta returnType) {
            super(name, returnType);
            this.argument = argument;
        }

        @Override
        final void appendArguments(final _SqlContext context) {
            this.argument.appendSql(context);
        }

        @Override
        final void argumentToString(final StringBuilder builder) {
            builder.append(this.argument);
        }


    }//OneArgWindowFunc


    private static class OneArgWindowFunc_ extends MySQLWindowFunc {

        private final SQLWords option;

        private final ArmyExpression argument;

        private OneArgWindowFunc_(String name, @Nullable SQLWords option
                , ArmyExpression argument, TypeMeta returnType) {
            super(name, returnType);
            this.option = option;
            this.argument = argument;
        }


        @Override
        final void appendArguments(final _SqlContext context) {
            final SQLWords option = this.option;
            if (option != null) {
                context.sqlBuilder()
                        .append(_Constant.SPACE)
                        .append(option.render());
            }
            this.argument.appendSql(context);
        }

        @Override
        final void argumentToString(final StringBuilder builder) {
            final SQLWords option = this.option;
            if (option != null) {
                builder.append(_Constant.SPACE)
                        .append(option.render());
            }
            builder.append(this.argument);
        }


    }//OneArgAggregateWindowFunc

    private static class ComplexArgWindowFunc extends MySQLWindowFunc {

        private final List<?> argList;

        private ComplexArgWindowFunc(String name, List<?> argList, TypeMeta returnType) {
            super(name, returnType);
            this.argList = argList;
        }

        @Override
        final void appendArguments(final _SqlContext context) {
            SQLFunctions.appendComplexArg(this.argList, context);
        }

        @Override
        final void argumentToString(final StringBuilder builder) {
            SQLFunctions.complexArgToString(this.argList, builder);
        }


    }//ComplexArgWindowFunc

    private static final class ComplexArgAggregateWindowFunc extends ComplexArgWindowFunc
            implements MySQLFuncSyntax._AggregateOverSpec {

        private ComplexArgAggregateWindowFunc(String name, List<?> argList, TypeMeta returnType) {
            super(name, argList, returnType);
        }


    }//ComplexArgAggregateWindowFunc

    private static class MultiArgWindowFunc extends MySQLWindowFunc {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        private final Clause clause;

        private MultiArgWindowFunc(String name, @Nullable SQLWords option
                , List<ArmyExpression> argList, @Nullable Clause clause, TypeMeta returnType) {
            super(name, returnType);

            assert argList.size() > 0;
            this.option = option;
            this.argList = _CollectionUtils.unmodifiableList(argList);
            this.clause = clause;

        }


        @Override
        final void appendArguments(final _SqlContext context) {
            SQLFunctions.appendArguments(this.option, this.argList, this.clause, context);
        }

        @Override
        final void argumentToString(final StringBuilder builder) {
            SQLFunctions.argumentsToString(this.option, this.argList, this.clause, builder);

        }


    }//MultiArgAggregateWindowFunc


    private static class NullTreatmentMultiArgWindowFunc extends ComplexArgWindowFunc
            implements MySQLFuncSyntax._NullTreatmentSpec {

        private NullTreatment nullTreatment;

        private NullTreatmentMultiArgWindowFunc(String name, List<?> argumentList, TypeMeta returnType) {
            super(name, argumentList, returnType);
        }

        @Override
        public final MySQLFuncSyntax._OverSpec respectNulls() {
            if (this.nullTreatment != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.nullTreatment = NullTreatment.RESPECT_NULLS;
            return this;
        }

        @Override
        public final MySQLFuncSyntax._OverSpec ignoreNulls() {
            if (this.nullTreatment != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.nullTreatment = NullTreatment.IGNORE_NULLS;
            return this;
        }


    }//NullTreatmentMultiArgWindowFunc

    private static final class FromFirstLastMultiArgWindowFunc extends NullTreatmentMultiArgWindowFunc
            implements MySQLFuncSyntax._FromFirstLastSpec {

        private FromFirstLast fromFirstLast;

        private FromFirstLastMultiArgWindowFunc(String name, List<?> argumentList, TypeMeta returnType) {
            super(name, argumentList, returnType);
        }

        @Override
        public MySQLFuncSyntax._NullTreatmentSpec fromFirst() {
            if (this.fromFirstLast != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.fromFirstLast = FromFirstLast.FROM_LAST;
            return this;
        }

        @Override
        public MySQLFuncSyntax._NullTreatmentSpec fromLast() {
            if (this.fromFirstLast != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.fromFirstLast = FromFirstLast.FROM_LAST;
            return this;
        }


    }//FromFirstLastMultiArgWindowFunc


    private static final class NoArgAggregateWindowFunc extends NoArgWindowFunc
            implements MySQLFuncSyntax._AggregateOverSpec {

        private NoArgAggregateWindowFunc(String name, TypeMeta returnType) {
            super(name, returnType);
        }

    }//NoArgAggregateWindowFunc


    private static final class OneArgAggregateWindowFunc extends OneArgWindowFunc
            implements MySQLFuncSyntax._AggregateOverSpec {

        private OneArgAggregateWindowFunc(String name, ArmyExpression argument, TypeMeta returnType) {
            super(name, argument, returnType);
        }

    }//OneArgAggregateWindowFunc


    private static final class MultiArgAggregateWindowFunc extends MultiArgWindowFunc
            implements MySQLFuncSyntax._AggregateOverSpec {

        private MultiArgAggregateWindowFunc(String name, @Nullable SQLWords option
                , List<ArmyExpression> argList, @Nullable Clause clause
                , TypeMeta returnType) {
            super(name, option, argList, clause, returnType);
        }

    }//MultiArgAggregateWindowFunc


    static final class GroupConcatClause extends OperationExpression
            implements MySQLClause._GroupConcatOrderBySpec {

        private final CriteriaContext criteriaContext;

        private final boolean distinct;

        private final List<ArmyExpression> exprList;

        private List<ArmySortItem> orderByList;

        private String stringValue;

        private GroupConcatClause(boolean distinct, List<ArmyExpression> exprList) {
            this.criteriaContext = ContextStack.peek();
            this.distinct = distinct;
            this.exprList = exprList;
        }

        @Override
        public TypeMeta typeMeta() {
            return StringType.INSTANCE;
        }

        @Override
        public MySQLClause._GroupConcatSeparatorClause orderBy(SortItem sortItem) {
            this.orderByList = Collections.singletonList((ArmySortItem) sortItem);
            return this;
        }

        @Override
        public MySQLClause._GroupConcatSeparatorClause orderBy(SortItem sortItem1, SortItem sortItem2) {
            this.orderByList = ArrayUtils.asUnmodifiableList(
                    (ArmySortItem) sortItem1,
                    (ArmySortItem) sortItem2
            );
            return this;
        }

        @Override
        public MySQLClause._GroupConcatSeparatorClause orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
            this.orderByList = ArrayUtils.asUnmodifiableList(
                    (ArmySortItem) sortItem1,
                    (ArmySortItem) sortItem2,
                    (ArmySortItem) sortItem3
            );
            return this;
        }

        @Override
        public MySQLClause._GroupConcatSeparatorClause orderBy(Consumer<Consumer<SortItem>> consumer) {
            return CriteriaSupports.<Void, MySQLClause._GroupConcatSeparatorClause>orderByClause(this.criteriaContext, this::orderByEnd)
                    .orderBy(consumer);
        }

        @Override
        public MySQLClause._GroupConcatSeparatorClause orderBy(BiConsumer<Void, Consumer<SortItem>> consumer) {
            return CriteriaSupports.<Void, MySQLClause._GroupConcatSeparatorClause>voidOrderByClause(this.criteriaContext, this::orderByEnd)
                    .orderBy(consumer);
        }

        @Override
        public MySQLClause._GroupConcatSeparatorClause ifOrderBy(Consumer<Consumer<SortItem>> consumer) {
            return CriteriaSupports.<Void, MySQLClause._GroupConcatSeparatorClause>orderByClause(this.criteriaContext, this::orderByEnd)
                    .ifOrderBy(consumer);
        }

        @Override
        public MySQLClause._GroupConcatSeparatorClause ifOrderBy(BiConsumer<Void, Consumer<SortItem>> consumer) {
            return CriteriaSupports.<Void, MySQLClause._GroupConcatSeparatorClause>voidOrderByClause(this.criteriaContext, this::orderByEnd)
                    .ifOrderBy(consumer);
        }

        @Override
        public Clause separator(@Nullable String strVal) {
            if (strVal == null) {
                throw ContextStack.nullPointer(this.criteriaContext);
            } else if (this.stringValue != null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
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
            final String strValue;
            strValue = supplier.get();
            if (strValue != null) {
                this.separator(strValue);
            }
            return this;
        }


        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" GROUP_CONCAT(");

            if (this.distinct) {
                sqlBuilder.append(_Constant.SPACE)
                        .append(SQLs.DISTINCT.render());
            }
            final List<ArmyExpression> exprList = this.exprList;
            final int exprSize = exprList.size();
            for (int i = 0; i < exprSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                exprList.get(i).appendSql(context);
            }

            final List<ArmySortItem> orderByList = this.orderByList;
            final int itemSize;
            if (orderByList != null && (itemSize = orderByList.size()) > 0) {
                sqlBuilder.append(_Constant.SPACE_ORDER_BY);
                for (int i = 0; i < itemSize; i++) {
                    if (i > 0) {
                        sqlBuilder.append(_Constant.SPACE_COMMA);
                    }
                    orderByList.get(i).appendSql(context);
                }
            }//if

            final String strValue = this.stringValue;
            if (strValue != null) {
                sqlBuilder.append(_Constant.SPACE_SEPARATOR);
                context.appendLiteral(StringType.INSTANCE, strValue);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        @Override
        public String toString() {
            StringBuilder builder = null;

            final List<ArmySortItem> orderByList = this.orderByList;

            if (orderByList != null && orderByList.size() > 0) {
                builder = new StringBuilder();
                builder.append(_Constant.SPACE_ORDER_BY);
                final int itemSize = orderByList.size();
                for (int i = 0; i < itemSize; i++) {
                    if (i > 0) {
                        builder.append(_Constant.SPACE_COMMA);
                    }
                    builder.append(orderByList.get(i));
                }
            }//if

            final String strValue = this.stringValue;
            if (strValue != null) {
                if (builder == null) {
                    builder = new StringBuilder();
                }
                builder.append(_Constant.SPACE_SEPARATOR)
                        .append(_Constant.SPACE_QUOTE)
                        .append(strValue)
                        .append(_Constant.QUOTE);
            }
            return builder == null ? "" : builder.toString();
        }

        private MySQLClause._GroupConcatSeparatorClause orderByEnd(final List<ArmySortItem> itemList) {
            if (this.orderByList != null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.orderByList = itemList;
            return this;
        }


    }//GroupConcatClause

    private static final class StatementDigestFunc extends SQLFunctions.FunctionExpression {

        private final Object statement;

        private final Visible visible;

        private final boolean literal;

        private StatementDigestFunc(String name, final Object statement, final Visible visible
                , final boolean literal, TypeMeta returnType) {
            super(name, returnType);
            this.statement = statement;
            this.visible = visible;
            this.literal = literal;
        }

        @Override
        void appendArguments(final _SqlContext context) {
            final Object statement = this.statement;
            if (statement instanceof String) {
                if (this.literal) {
                    context.appendLiteral(StringType.INSTANCE, statement);
                } else {
                    context.appendParam(SingleParam.build(StringType.INSTANCE, statement));
                }
                return;
            }

            final Stmt stmt;
            if (!(statement instanceof PrimaryStatement)) {
                //no bug,never here
                throw new IllegalStateException();
            } else if (statement instanceof Select) {
                stmt = context.parser().select((Select) statement, this.visible);
            } else if (statement instanceof Insert) {
                stmt = context.parser().insert((Insert) statement, this.visible);
            } else if (statement instanceof Update) {
                stmt = context.parser().update((Update) statement, this.visible);
            } else if (statement instanceof Delete) {
                stmt = context.parser().delete((Delete) statement, this.visible);
            } else if (statement instanceof DialectStatement) {
                stmt = context.parser().dialectStmt((DialectStatement) statement, this.visible);
            } else {
                //no bug,never here
                throw new IllegalArgumentException();
            }

            if (!(stmt instanceof SimpleStmt)) {
                String m = String.format("the argument of %s must be simple statement.", this.name);
                throw new CriteriaException(m);
            }

            if (this.literal) {
                context.appendLiteral(StringType.INSTANCE, ((SimpleStmt) stmt).sql());
            } else {
                context.appendParam(SingleParam.build(StringType.INSTANCE, ((SimpleStmt) stmt).sql()));
            }

        }

        @Override
        void argumentsToString(final StringBuilder builder) {
            final Object statement = this.statement;
            if (statement instanceof String) {
                builder.append(_Constant.SPACE)
                        .append(statement);
                return;
            }
            if (!(statement instanceof PrimaryStatement)) {
                //no bug,never here
                throw new IllegalStateException();
            }
            final Stmt stmt;
            stmt = ((PrimaryStatement) statement).mockAsStmt(MySQLDialect.MySQL80, this.visible);

            if (!(stmt instanceof SimpleStmt)) {
                String m = String.format("the argument of %s must be simple statement.", this.name);
                throw new CriteriaException(m);
            }

            builder.append(_Constant.SPACE)
                    .append(((SimpleStmt) stmt).sql());

        }

    }//StatementDigestFunc

    @Deprecated
    private static final class IntervalTimeFunc extends SQLFunctions.FunctionExpression {

        private final ArmyExpression date;

        private final ArmyExpression expr;

        private final MySQLUnit unit;

        private IntervalTimeFunc(String name, ArmyExpression date, ArmyExpression expr, MySQLUnit unit
                , TypeMeta returnType) {
            super(name, returnType);
            this.date = date;
            this.expr = expr;
            this.unit = unit;
        }

        @Override
        void appendArguments(final _SqlContext context) {
            this.date.appendSql(context);
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_COMMA)
                    .append(_Constant.SPACE_INTERVAL);

            this.expr.appendSql(context);
            sqlBuilder.append(this.unit);
        }

        @Override
        void argumentsToString(final StringBuilder builder) {
            builder.append(this.date)
                    .append(_Constant.SPACE_COMMA)
                    .append(_Constant.SPACE_INTERVAL)
                    .append(this.expr)
                    .append(this.unit);
        }

    }//IntervalTimeFunc


    private enum JsonValueWord {
        NULL("NULL"),
        ERROR("ERROR"),
        ON_EMPTY("ON EMPTY"),
        ON_ERROR("ON ERROR");

        private final String words;

        JsonValueWord(String words) {
            this.words = words;
        }


    }//NullOrError


    private static final class JsonValueFunc extends OperationExpression
            implements SQLFunctions.FunctionSpec
            , OperationExpression.MutableParamMetaSpec
            , MySQLClause._JsonValueReturningSpec
            , MySQLClause._JsonValueOptionOnEmptySpec
            , MySQLClause._JsonValueOnEmptySpec {

        private final CriteriaContext context;

        private final ArmyExpression jsonDoc;

        private final ArmyExpression path;

        private TypeMeta returnType;

        private List<Object> returningList;

        private List<_Pair<Object, JsonValueWord>> eventHandlerList;

        private Object operateValue;

        private JsonValueFunc(ArmyExpression jsonDoc, ArmyExpression path) {
            this.context = ContextStack.peek();
            this.jsonDoc = jsonDoc;
            this.path = path;
        }

        @Override
        public TypeMeta typeMeta() {
            TypeMeta returnType = this.returnType;
            if (returnType == null) {
                final List<Object> returningList = this.returningList;
                if (returningList == null) {
                    returnType = StringType.INSTANCE;
                } else {
                    returnType = MySQLFuncSyntax._castReturnType((MySQLCastType) returningList.get(0));
                }
                this.returnType = returnType;
            }
            return returnType;
        }

        @Override
        public void updateParamMeta(final TypeMeta typeMeta) {
            this.returnType = typeMeta;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" JSON_VALUE(");

            this.jsonDoc.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            this.path.appendSql(context);

            final List<Object> returningList = this.returningList;
            if (returningList != null) {
                assert returningList.get(0) instanceof MySQLCastType;
                for (Object o : returningList) {
                    if (o instanceof MySQLCastType) {
                        sqlBuilder.append(_Constant.SPACE_RETURNING)
                                .append(_Constant.SPACE);
                        sqlBuilder.append(((MySQLCastType) o).render());
                    } else if (o == FuncWord.LEFT_PAREN) {
                        sqlBuilder.append(((FuncWord) o).render());
                    } else if (o instanceof FuncWord || o instanceof MySQLCharset) {
                        sqlBuilder.append(_Constant.SPACE)
                                .append(((SQLWords) o).render());
                    } else if (o instanceof Expression) {
                        ((ArmyExpression) o).appendSql(context);
                    } else {
                        //no bug,never here
                        throw new IllegalStateException();
                    }

                }//for
            }//if

            final List<_Pair<Object, JsonValueWord>> eventHandlerList = this.eventHandlerList;
            if (eventHandlerList != null) {
                assert eventHandlerList.size() < 3;
                for (_Pair<Object, JsonValueWord> pair : eventHandlerList) {
                    if (pair.first instanceof JsonValueWord) {
                        assert pair.first == JsonValueWord.NULL || pair.first == JsonValueWord.ERROR;
                        sqlBuilder.append(_Constant.SPACE)
                                .append(((JsonValueWord) pair.first).words);
                    } else if (pair.first instanceof Expression) {
                        sqlBuilder.append(_Constant.SPACE_DEFAULT);
                        ((ArmyExpression) pair.first).appendSql(context);
                    } else {
                        //no bug,never here
                        throw new IllegalStateException();
                    }
                    assert pair.second == JsonValueWord.ON_EMPTY || pair.second == JsonValueWord.ON_ERROR;
                    sqlBuilder.append(_Constant.SPACE)
                            .append(pair.second.words);

                }//for
            }//if

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder()
                    .append(" JSON_VALUE(")
                    .append(this.jsonDoc)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.path);

            final List<Object> returningList = this.returningList;
            if (returningList != null) {
                assert returningList.get(0) instanceof MySQLCastType;
                for (Object o : returningList) {
                    if (o instanceof MySQLCastType) {
                        builder.append(_Constant.SPACE_RETURNING)
                                .append(_Constant.SPACE)
                                .append(((MySQLCastType) o).render());
                    } else if (o == FuncWord.LEFT_PAREN) {
                        builder.append(((FuncWord) o).render());
                    } else if (o instanceof FuncWord || o instanceof MySQLCharset) {
                        builder.append(_Constant.SPACE)
                                .append(((SQLWords) o).render());
                    } else if (o instanceof Expression) {
                        builder.append(o);
                    } else {
                        //no bug,never here
                        throw new IllegalStateException();
                    }

                }//for

            }//if

            final List<_Pair<Object, JsonValueWord>> eventHandlerList = this.eventHandlerList;
            if (eventHandlerList != null) {
                assert eventHandlerList.size() < 3;
                for (_Pair<Object, JsonValueWord> pair : eventHandlerList) {
                    if (pair.first instanceof JsonValueWord) {
                        assert pair.first == JsonValueWord.NULL || pair.first == JsonValueWord.ERROR;
                        builder.append(_Constant.SPACE)
                                .append(((JsonValueWord) pair.first).words);
                    } else if (pair.first instanceof Expression) {
                        builder.append(_Constant.SPACE_DEFAULT)
                                .append(pair.first);
                    } else {
                        //no bug,never here
                        throw new IllegalStateException();
                    }
                    assert pair.second == JsonValueWord.ON_EMPTY || pair.second == JsonValueWord.ON_ERROR;
                    builder.append(_Constant.SPACE)
                            .append(pair.second.words);

                }//for
            }//if

            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }

        @Override
        public MySQLClause._JsonValueOptionOnEmptySpec returning(final MySQLCastType type) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.singletonList(type);
            return this;
        }

        @Override
        public MySQLClause._JsonValueOptionOnEmptySpec returning(final MySQLCastType type, Expression n) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (!MySQLUtils.isSingleParamType(type)) {
                throw typeError(type);
            }
            final List<Object> list = new ArrayList<>(4);

            list.add(type);
            list.add(FuncWord.LEFT_PAREN);
            list.add(n);
            list.add(FuncWord.RIGHT_PAREN);

            this.returningList = list;
            return this;
        }

        @Override
        public MySQLClause._JsonValueOptionOnEmptySpec returning(MySQLCastType type, Expression n, MySQLCharset charset) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (type != MySQLCastType.CHAR) {
                throw typeError(type);
            }
            final List<Object> list = new ArrayList<>(5);

            list.add(type);
            list.add(FuncWord.LEFT_PAREN);
            list.add(n);
            list.add(FuncWord.RIGHT_PAREN);

            list.add(charset);
            this.returningList = list;
            return this;
        }

        @Override
        public MySQLClause._JsonValueOptionOnEmptySpec returning(MySQLCastType type, Expression m, Expression d) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (type != MySQLCastType.DECIMAL) {
                throw typeError(type);
            }
            final List<Object> list = new ArrayList<>(6);

            list.add(type);
            list.add(FuncWord.LEFT_PAREN);
            list.add(m);
            list.add(FuncWord.COMMA);

            list.add(d);
            list.add(FuncWord.RIGHT_PAREN);
            this.returningList = list;
            return this;
        }

        @Override
        public MySQLClause._JsonValueOnEmptySpec nullWord() {
            if (this.operateValue != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = JsonValueWord.NULL;
            return this;
        }

        @Override
        public MySQLClause._JsonValueOnEmptySpec error() {
            if (this.operateValue != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = JsonValueWord.ERROR;
            return this;
        }

        @Override
        public MySQLClause._JsonValueOnEmptySpec defaultValue(final Expression value) {
            if (this.operateValue != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.context);
            }
            this.operateValue = value;
            return this;
        }

        @Override
        public MySQLClause._JsonValueOnEmptySpec defaultValue(Supplier<? extends Expression> supplier) {
            return this.defaultValue(supplier.get());
        }

        @Override
        public Expression onError() {
            final Object operateValue = this.operateValue;
            if (operateValue == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = null;//clear
            final List<_Pair<Object, JsonValueWord>> eventHandlerList = this.eventHandlerList;
            if (eventHandlerList == null) {
                this.eventHandlerList = Collections.singletonList(_Pair.create(operateValue, JsonValueWord.ON_ERROR));
            } else if (eventHandlerList.size() == 1) {
                eventHandlerList.add(_Pair.create(operateValue, JsonValueWord.ON_ERROR));
            } else {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this;
        }

        @Override
        public MySQLClause._JsonValueOptionSpec onEmpty() {
            final Object operateValue = this.operateValue;
            if (operateValue == null || this.eventHandlerList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = null;//clear
            final List<_Pair<Object, JsonValueWord>> eventHandlerList = new ArrayList<>(2);
            this.eventHandlerList = eventHandlerList;
            eventHandlerList.add(_Pair.create(operateValue, JsonValueWord.ON_EMPTY));
            return this;
        }

        private CriteriaException typeError(MySQLCastType type) {
            String m = String.format("%s error", type);
            return ContextStack.criteriaError(this.context, m);
        }


    }//JsonValueFunc


    private interface JsonTableColumn extends _SelfDescribed {

    }

    private static final class ColumnForOrdinality<R> implements MySQLClause._ForOrdinalityClause<R>, JsonTableColumn {

        private final String name;

        private final MySQLClause._JsonTableColumnCommaSpec<R> columnsClause;

        private boolean forOrdinalityClause;


        private ColumnForOrdinality(String name, MySQLClause._JsonTableColumnCommaSpec<R> columnsClause) {
            this.name = name;
            this.columnsClause = columnsClause;
        }

        @Override
        public MySQLClause._JsonTableColumnCommaSpec<R> forOrdinality() {
            this.forOrdinalityClause = true;
            return this.columnsClause;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            if (!this.forOrdinalityClause) {
                throw _Exceptions.castCriteriaApi();
            }
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);
            context.parser().identifier(this.name, sqlBuilder);
            sqlBuilder.append(" FOR ORDINALITY");
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(" ")
                    .append(this.name)
                    .append(" FOR ORDINALITY")
                    .toString();
        }


    }//ColumnForOrdinality


    private static class ColumnWithPath<R>
            implements JsonTableColumn
            , MySQLClause._JsonTableColumnPathClause<R>
            , MySQLClause._JsonTableOnEmptySpec<R> {

        private final String name;

        private final MySQLTypes type;

        private final long precision;

        private final int scale;

        final JsonTableColumnListClause<R> columnsClause;

        private boolean existsPath;

        private String stringPath;

        private List<Object> eventClauseList;

        private ColumnWithPath(String name, MySQLTypes type, JsonTableColumnListClause<R> clause) {
            this.name = name;
            this.type = type;
            this.precision = -1L;
            this.scale = -1;
            this.columnsClause = clause;
        }

        private ColumnWithPath(String name, MySQLTypes type, long precision, int scale
                , JsonTableColumnListClause<R> clause) {
            this.name = name;
            this.type = type;
            this.precision = precision;
            this.scale = scale;
            this.columnsClause = clause;
        }

        private ColumnWithPath(String name, MySQLTypes type, long precision
                , JsonTableColumnListClause<R> clause) {
            this.name = name;
            this.type = type;
            this.precision = precision;
            this.scale = -1;
            this.columnsClause = clause;
        }

        @Override
        public MySQLClause._JsonTableOnEmptyOptionClause<R> path(final @Nullable String stringPath) {
            if (stringPath == null) {
                throw ContextStack.nullPointer(this.columnsClause.context);
            }
            this.existsPath = false;
            this.stringPath = stringPath;
            return this.columnsClause;
        }

        @Override
        public final MySQLClause._JsonTableColumnCommaSpec<R> existsPath(final @Nullable String stringPath) {
            if (stringPath == null) {
                throw ContextStack.nullPointer(this.columnsClause.context);
            }
            this.existsPath = true;
            this.stringPath = stringPath;
            return this.columnsClause;
        }

        @Override
        public final MySQLClause._JsonTableColumnCommaSpec<R> onError() {
            final List<Object> eventClauseList = this.eventClauseList;
            if (eventClauseList == null) {
                throw ContextStack.castCriteriaApi(this.columnsClause.context);
            }
            final int clauseSize = eventClauseList.size();
            if (clauseSize != 1 && clauseSize != 3) {
                throw ContextStack.castCriteriaApi(this.columnsClause.context);
            }
            eventClauseList.add(JsonValueWord.ON_ERROR);
            this.eventClauseList = _CollectionUtils.unmodifiableList(eventClauseList);
            return this.columnsClause;
        }

        @Override
        public final MySQLClause._JsonTableColumnOnErrorOptionClause<R> onEmpty() {
            final List<Object> eventClauseList = this.eventClauseList;
            if (eventClauseList == null) {
                throw ContextStack.castCriteriaApi(this.columnsClause.context);
            }
            final int clauseSize = eventClauseList.size();
            if (clauseSize != 1) {
                throw ContextStack.castCriteriaApi(this.columnsClause.context);
            }
            eventClauseList.add(JsonValueWord.ON_EMPTY);
            return this.columnsClause;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);
            context.parser().identifier(this.name, sqlBuilder);

            final MySQLTypes type = this.type;
            sqlBuilder.append(_Constant.SPACE)
                    .append(type.render());

            if (this.precision > 0) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN)
                        .append(this.precision);
                if (this.scale > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA_SPACE)
                            .append(this.scale);
                }
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            if (this instanceof ColumnWithPathAndCharSet) {
                this.appendCharset(context);
            }
            if (this.existsPath) {
                sqlBuilder.append(" EXISTS");
            }
            sqlBuilder.append(" PATH");
            final String stringPath = this.stringPath;
            if (stringPath == null) {
                throw _Exceptions.castCriteriaApi();
            }
            context.appendLiteral(StringType.INSTANCE, stringPath);

            final List<Object> eventClauseList;
            if (!this.existsPath && (eventClauseList = this.eventClauseList) != null) {
                for (Object eventClause : eventClauseList) {
                    sqlBuilder.append(_Constant.SPACE);
                    if (eventClause instanceof JsonValueWord) {
                        sqlBuilder.append(((JsonValueWord) eventClause).words);
                    } else if (eventClause instanceof String) {
                        sqlBuilder.append(_Constant.DEFAULT);
                        context.appendLiteral(StringType.INSTANCE, eventClause);
                    } else {
                        //no bug,never here
                        throw new IllegalStateException();
                    }
                }
            }

        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(_Constant.SPACE)
                    .append(this.name);

            final MySQLTypes type = this.type;
            builder.append(_Constant.SPACE)
                    .append(type.render());

            if (this.precision > 0) {
                builder.append(_Constant.SPACE_LEFT_PAREN)
                        .append(this.precision);
                if (this.scale > 0) {
                    builder.append(_Constant.SPACE_COMMA_SPACE)
                            .append(this.scale);
                }
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            if (this instanceof ColumnWithPathAndCharSet) {
                final ColumnWithPathAndCharSet<?> column = (ColumnWithPathAndCharSet<?>) this;
                builder.append(" CHARACTER SET ");
                if (column.charset instanceof MySQLCharset) {
                    builder.append(((MySQLCharset) column.charset).render());
                } else if (column.charset instanceof String) {
                    builder.append(column.charset);
                } else {
                    throw _Exceptions.castCriteriaApi();
                }
                if (column.collate != null) {
                    builder.append(" COLLATE ")
                            .append(column.collate);
                }
            }
            if (this.existsPath) {
                builder.append(" EXISTS");
            }
            builder.append(" PATH");
            final String stringPath = this.stringPath;
            if (stringPath == null) {
                throw _Exceptions.castCriteriaApi();
            }
            builder.append(stringPath);

            final List<Object> eventClauseList;
            if (!this.existsPath && (eventClauseList = this.eventClauseList) != null) {
                for (Object eventClause : eventClauseList) {
                    builder.append(_Constant.SPACE);
                    if (eventClause instanceof JsonValueWord) {
                        builder.append(((JsonValueWord) eventClause).words);
                    } else if (eventClause instanceof String) {
                        builder.append(_Constant.DEFAULT)
                                .append(_Constant.SPACE)
                                .append(eventClause);
                    } else {
                        //no bug,never here
                        throw new IllegalStateException();
                    }
                }
            }
            return builder.toString();
        }


        void appendCharset(_SqlContext context) {
            throw new UnsupportedOperationException();
        }


        final MySQLClause._JsonTableOnEmptySpec<R> nullWord() {
            List<Object> eventClauseList = this.eventClauseList;
            if (eventClauseList == null) {
                this.eventClauseList = eventClauseList = new ArrayList<>();
            }
            eventClauseList.add(JsonValueWord.NULL);
            return this;
        }

        final MySQLClause._JsonTableOnEmptySpec<R> error() {
            List<Object> eventClauseList = this.eventClauseList;
            if (eventClauseList == null) {
                this.eventClauseList = eventClauseList = new ArrayList<>();
            }
            eventClauseList.add(JsonValueWord.ERROR);
            return this;
        }

        final MySQLClause._JsonTableOnEmptySpec<R> defaultValue(String jsonString) {
            List<Object> eventClauseList = this.eventClauseList;
            if (eventClauseList == null) {
                this.eventClauseList = eventClauseList = new ArrayList<>();
            }
            eventClauseList.add(jsonString);
            return this;
        }


    }//ColumnWithPath

    private static final class ColumnWithPathAndCharSet<R> extends ColumnWithPath<R> {

        private final Object charset;

        private final String collate;

        private ColumnWithPathAndCharSet(String name, MySQLTypes type
                , long precision, MySQLCharset charset, JsonTableColumnListClause<R> clause) {
            super(name, type, precision, clause);
            this.charset = charset;
            this.collate = null;
        }

        private ColumnWithPathAndCharSet(String name, MySQLTypes type
                , long precision, Object charset
                , final String collate, JsonTableColumnListClause<R> clause) {
            super(name, type, precision, clause);
            this.charset = charset;
            this.collate = collate;
        }

        @Override
        void appendCharset(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" CHARACTER SET ");
            DialectParser parser = null;
            if (this.charset instanceof MySQLCharset) {
                sqlBuilder.append(((MySQLCharset) this.charset).render());
            } else if (this.charset instanceof String) {
                parser = context.parser();
                parser.identifier((String) this.charset, sqlBuilder);
            } else {
                throw _Exceptions.castCriteriaApi();
            }
            if (this.collate != null) {
                sqlBuilder.append(" COLLATE ");
                if (parser == null) {
                    parser = context.parser();
                }
                parser.identifier(this.collate, sqlBuilder);
            }


        }


    }//ColumnWithPathAndCharSet


    private static abstract class JsonTableColumnListClause<R>
            implements MySQLClause._JsonTableColumnLeftParenClause<R>
            , MySQLClause._JsonTableColumnCommaSpec<R>
            , MySQLClause._JsonTableOnEmptyOptionClause<R>
            , _SelfDescribed {

        private final CriteriaContext context;

        private List<JsonTableColumn> columnList = new ArrayList<>();


        private JsonTableColumnListClause(CriteriaContext context) {
            this.context = context;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final List<JsonTableColumn> columnList = this.columnList;
            if (columnList instanceof ArrayList) {
                throw _Exceptions.castCriteriaApi();
            }
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();
            if (this instanceof JsonTable) {
                sqlBuilder.append(" JSON_TABLE(");
                final JsonTable jsonTable = (JsonTable) this;
                jsonTable.expr.appendSql(context);
                sqlBuilder.append(_Constant.SPACE_COMMA);
                jsonTable.path.appendSql(context);
            } else if (this instanceof NestedJsonTableColumnListClause) {
                sqlBuilder.append(" NESTED");
                final NestedJsonTableColumnListClause<?> nestedColumns = (NestedJsonTableColumnListClause<?>) this;
                if (nestedColumns.pathWord) {
                    sqlBuilder.append(" PATH");
                }
                context.appendLiteral(StringType.INSTANCE, nestedColumns.path);
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }

            sqlBuilder.append(" COLUMNS(");
            final int columnSize = columnList.size();
            for (int i = 0; i < columnSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                columnList.get(i).appendSql(context);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN); // right paren of column_list

            if (this instanceof JsonTable) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);// right paren of JSON_TABLE
            }
        }

        @Override
        public final MySQLClause._ForOrdinalityClause<R> leftParen(String name) {
            return this.comma(name);
        }

        @Override
        public final MySQLClause._JsonTableColumnPathClause<R> leftParen(String name, MySQLTypes type) {
            return this.comma(name, type);
        }

        @Override
        public final MySQLClause._JsonTableColumnPathClause<R> leftParen(String name, MySQLTypes type, long n) {
            return this.comma(name, type, n);
        }

        @Override
        public final MySQLClause._JsonTableColumnPathClause<R> leftParen(String name, MySQLTypes type, long n, MySQLCharset charset) {
            return this.comma(name, type, n, charset);
        }

        @Override
        public final MySQLClause._JsonTableColumnPathClause<R> leftParen(String name, MySQLTypes type, long n, MySQLCharset charset, String collate) {
            return this.comma(name, type, n, charset, collate);
        }

        @Override
        public final MySQLClause._JsonTableColumnPathClause<R> leftParen(String name, MySQLTypes type, long n, String charset, String collate) {
            return this.comma(name, type, n, charset, collate);
        }

        @Override
        public MySQLClause._JsonTableColumnPathClause<R> leftParen(String name, MySQLTypes type, long p, int m) {
            return this.comma(name, type, p, m);
        }

        @Override
        public final MySQLClause._JsonTableColumnsClause<MySQLClause._JsonTableColumnCommaSpec<R>> leftParenNested(String path) {
            return this.commaNested(path);
        }


        @Override
        public final MySQLClause._JsonTableColumnsClause<MySQLClause._JsonTableColumnCommaSpec<R>> leftParenNestedPath(String path) {
            return this.commaNestedPath(path);
        }


        @Override
        public final MySQLClause._ForOrdinalityClause<R> comma(String name) {
            final ColumnForOrdinality<R> column = new ColumnForOrdinality<>(name, this);
            this.columnList.add(column);
            return column;
        }

        @Override
        public final MySQLClause._JsonTableColumnPathClause<R> comma(String name, MySQLTypes type) {
            final ColumnWithPath<R> column = new ColumnWithPath<>(name, type, this);
            this.columnList.add(column);
            return column;
        }

        @Override
        public final MySQLClause._JsonTableColumnPathClause<R> comma(String name, MySQLTypes type, long n) {
            if (n < 1) {
                throw precisionError(n);
            }
            final ColumnWithPath<R> column = new ColumnWithPath<>(name, type, n, this);
            this.columnList.add(column);
            return column;
        }

        @Override
        public final MySQLClause._JsonTableColumnPathClause<R> comma(String name, MySQLTypes type, long n, MySQLCharset charset) {
            if (n < 1) {
                throw precisionError(n);
            }
            final ColumnWithPathAndCharSet<R> column = new ColumnWithPathAndCharSet<>(name, type, n, charset, this);
            this.columnList.add(column);
            return column;
        }

        @Override
        public final MySQLClause._JsonTableColumnPathClause<R> comma(String name, MySQLTypes type, long n, MySQLCharset charset, String collate) {
            if (n < 1) {
                throw precisionError(n);
            }
            final ColumnWithPathAndCharSet<R> column;
            column = new ColumnWithPathAndCharSet<>(name, type, n, charset, collate, this);
            this.columnList.add(column);
            return column;
        }

        @Override
        public final MySQLClause._JsonTableColumnPathClause<R> comma(String name, MySQLTypes type, long n, String charset, String collate) {
            if (n < 1) {
                throw precisionError(n);
            }
            final ColumnWithPathAndCharSet<R> column;
            column = new ColumnWithPathAndCharSet<>(name, type, n, charset, collate, this);
            this.columnList.add(column);
            return column;
        }

        @Override
        public final MySQLClause._JsonTableColumnPathClause<R> comma(String name, MySQLTypes type, long p, int m) {
            if (p < 1 || m < 0) {
                String message = String.format("precision[%s] or scale[%s] error.", p, m);
                throw ContextStack.criteriaError(this.context, message);
            }
            final ColumnWithPath<R> column = new ColumnWithPath<>(name, type, p, m, this);
            this.columnList.add(column);
            return column;
        }

        @Override
        public final MySQLClause._JsonTableColumnsClause<MySQLClause._JsonTableColumnCommaSpec<R>> commaNested(String path) {
            final NestedJsonTableColumnListClause<R> column = new NestedJsonTableColumnListClause<>(this, false, path);
            this.columnList.add(column);
            return column;
        }

        @Override
        public final MySQLClause._JsonTableColumnsClause<MySQLClause._JsonTableColumnCommaSpec<R>> commaNestedPath(String path) {
            final NestedJsonTableColumnListClause<R> column = new NestedJsonTableColumnListClause<>(this, true, path);
            this.columnList.add(column);
            return column;
        }


        @Override
        public final MySQLClause._JsonTableOnEmptySpec<R> nullWord() {
            return this.getEventClause().nullWord();
        }

        @Override
        public final MySQLClause._JsonTableOnEmptySpec<R> error() {
            return this.getEventClause().error();
        }

        @Override
        public final MySQLClause._JsonTableOnEmptySpec<R> defaultValue(String jsonString) {
            return this.getEventClause().defaultValue(jsonString);
        }


        final void endColumnList() {
            final List<JsonTableColumn> columnList = this.columnList;
            if (!(columnList instanceof ArrayList) || columnList.size() == 0) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.columnList = Collections.unmodifiableList(columnList);
        }


        private CriteriaException precisionError(long p) {
            String m = String.format("precision[%s] error", p);
            return ContextStack.criteriaError(this.context, m);
        }


        @SuppressWarnings("unchecked")
        private ColumnWithPath<R> getEventClause() {
            final List<JsonTableColumn> columnList = this.columnList;
            final int columnSize = columnList.size();
            if (columnSize == 0) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final JsonTableColumn column;
            column = columnList.get(columnSize - 1);
            if (!(column instanceof ColumnWithPath)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return (ColumnWithPath<R>) column;
        }


    }//JsonTableColumnListClause


    static final class JsonTable extends JsonTableColumnListClause<TabularItem>
            implements TabularItem, MySQLClause._JsonTableColumnsClause<TabularItem> {

        private final ArmyExpression expr;

        private final ArmyExpression path;

        private JsonTable(ArmyExpression expr, ArmyExpression path) {
            super(ContextStack.peek());
            this.expr = expr;
            this.path = path;
        }

        @Override
        public TabularItem rightParen() {
            this.endColumnList();
            return this;
        }

        @Override
        public MySQLClause._JsonTableColumnLeftParenClause<TabularItem> columns() {
            return this;
        }


    }//PrimaryJsonTableColumnListClause


    private static final class NestedJsonTableColumnListClause<R>
            extends JsonTableColumnListClause<MySQLClause._JsonTableColumnCommaSpec<R>>
            implements JsonTableColumn, MySQLClause._JsonTableColumnsClause<MySQLClause._JsonTableColumnCommaSpec<R>> {

        private final JsonTableColumnListClause<R> outerClause;

        private final boolean pathWord;

        private final String path;

        private NestedJsonTableColumnListClause(JsonTableColumnListClause<R> outerClause, boolean pathWord
                , String path) {
            super(outerClause.context);
            this.outerClause = outerClause;
            this.pathWord = pathWord;
            this.path = path;
        }

        @Override
        public MySQLClause._JsonTableColumnCommaSpec<R> rightParen() {
            this.endColumnList();
            return this.outerClause;
        }

        @Override
        public MySQLClause._JsonTableColumnLeftParenClause<MySQLClause._JsonTableColumnCommaSpec<R>> columns() {
            return this;
        }

    }//NestedJsonTableColumnListClause


}
