package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLCharset;
import io.army.criteria.mysql.MySQLClause;
import io.army.criteria.mysql.MySQLUnit;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.mapping.StringType;
import io.army.meta.TypeMeta;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleParam;
import io.army.stmt.Stmt;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;

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
            throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
        }
        return new IntervalTimeFunc(name, SQLs._funcParam(date), SQLs._funcParam(expr), unit, returnType);
    }

    static MySQLFuncSyntax._OverSpec noArgWindowFunc(String name, TypeMeta returnType) {
        return new NoArgWindowFunc(name, returnType);
    }

    static MySQLFuncSyntax._OverSpec oneArgWindowFunc(String name, @Nullable SQLWords option
            , @Nullable Object expr, TypeMeta returnType) {
        return new OneArgWindowFunc(name, option, SQLs._funcParam(expr), returnType);
    }

    static MySQLFuncSyntax._OverSpec safeMultiArgWindowFunc(String name, @Nullable SQLWords option
            , List<ArmyExpression> argList, TypeMeta returnType) {
        return new MultiArgWindowFunc(name, option, argList, null, returnType);
    }

    static MySQLFuncSyntax._FromFirstLastSpec safeMultiArgFromFirstWindowFunc(String name, @Nullable SQLWords option
            , List<ArmyExpression> argList, TypeMeta returnType) {
        return new FromFirstLastMultiArgWindowFunc(name, option, argList, returnType);
    }

    static MySQLFuncSyntax._AggregateOverSpec aggregateWindowFunc(String name, @Nullable SQLWords option
            , @Nullable Object exp, TypeMeta returnType) {
        return new OneArgAggregateWindowFunc(name, option, SQLs._funcParam(exp), returnType);
    }

    static MySQLFuncSyntax._AggregateOverSpec safeMultiArgAggregateWindowFunc(String name, @Nullable SQLWords option
            , List<ArmyExpression> argList, @Nullable Clause clause, TypeMeta returnType) {
        return new MultiArgAggregateWindowFunc(name, option, argList, clause, returnType);
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


    static MySQLFuncSyntax._AggregateOverSpec multiArgAggregateWindowFunc(String name, @Nullable SQLWords option
            , List<?> argList, @Nullable Clause clause, TypeMeta returnType) {
        if (argList.size() == 0) {
            throw CriteriaUtils.funcArgError(name, argList);
        }
        final List<ArmyExpression> expList = new ArrayList<>(argList.size());
        for (Object o : argList) {
            expList.add(SQLs._funcParam(o));
        }
        return new MultiArgAggregateWindowFunc(name, option, expList, clause, returnType);
    }

    static GroupConcatClause groupConcatClause() {
        return new GroupConcatClause();
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

        private final SQLWords option;

        private final ArmyExpression argument;

        private OneArgWindowFunc(String name, @Nullable SQLWords option
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


    private static class NullTreatmentMultiArgWindowFunc extends MultiArgWindowFunc
            implements MySQLFuncSyntax._NullTreatmentSpec {

        private NullTreatment nullTreatment;

        private NullTreatmentMultiArgWindowFunc(String name, @Nullable SQLWords option
                , List<ArmyExpression> argumentList, TypeMeta returnType) {
            super(name, option, argumentList, null, returnType);
        }

        @Override
        public final MySQLFuncSyntax._OverSpec respectNulls() {
            if (this.nullTreatment != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.nullTreatment = NullTreatment.RESPECT_NULLS;
            return this;
        }

        @Override
        public final MySQLFuncSyntax._OverSpec ignoreNulls() {
            if (this.nullTreatment != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.nullTreatment = NullTreatment.IGNORE_NULLS;
            return this;
        }


    }//NullTreatmentMultiArgWindowFunc

    private static final class FromFirstLastMultiArgWindowFunc extends NullTreatmentMultiArgWindowFunc
            implements MySQLFuncSyntax._FromFirstLastSpec {

        private FromFirstLast fromFirstLast;

        private FromFirstLastMultiArgWindowFunc(String name, @Nullable SQLWords option
                , List<ArmyExpression> argumentList, TypeMeta returnType) {
            super(name, option, argumentList, returnType);
        }

        @Override
        public MySQLFuncSyntax._NullTreatmentSpec fromFirst() {
            if (this.fromFirstLast != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.fromFirstLast = FromFirstLast.FROM_LAST;
            return this;
        }

        @Override
        public MySQLFuncSyntax._NullTreatmentSpec fromLast() {
            if (this.fromFirstLast != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
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

        private OneArgAggregateWindowFunc(String name, @Nullable SQLWords option
                , ArmyExpression argument, TypeMeta returnType) {
            super(name, option, argument, returnType);
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


    static final class GroupConcatClause extends SQLFunctions.ArgumentClause
            implements MySQLClause._GroupConcatOrderBySpec {

        private final CriteriaContext criteriaContext;
        private List<ArmySortItem> orderByList;

        private String stringValue;

        private GroupConcatClause() {
            this.criteriaContext = CriteriaContextStack.peek();
        }

        @Override
        public CriteriaContext getContext() {
            return this.criteriaContext;
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
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            } else if (this.stringValue != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
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
            sqlBuilder = context.sqlBuilder();
            final List<ArmySortItem> orderByList = this.orderByList;

            if (orderByList != null && orderByList.size() > 0) {
                sqlBuilder.append(_Constant.SPACE_ORDER_BY);
                final int itemSize = orderByList.size();
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
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
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
            , MutableParamMetaSpec
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
            this.context = CriteriaContextStack.peek();
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
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.singletonList(type);
            return this;
        }

        @Override
        public MySQLClause._JsonValueOptionOnEmptySpec returning(final MySQLCastType type, Expression n) {
            if (this.returningList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
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
                throw CriteriaContextStack.castCriteriaApi(this.context);
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
                throw CriteriaContextStack.castCriteriaApi(this.context);
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
        public MySQLClause._JsonValueOnEmptySpec nullValue() {
            if (this.operateValue != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = JsonValueWord.NULL;
            return this;
        }

        @Override
        public MySQLClause._JsonValueOnEmptySpec error() {
            if (this.operateValue != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = JsonValueWord.ERROR;
            return this;
        }

        @Override
        public MySQLClause._JsonValueOnEmptySpec defaultValue(final Expression value) {
            if (this.operateValue != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.context);
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
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = null;//clear
            final List<_Pair<Object, JsonValueWord>> eventHandlerList = this.eventHandlerList;
            if (eventHandlerList == null) {
                this.eventHandlerList = Collections.singletonList(_Pair.create(operateValue, JsonValueWord.ON_ERROR));
            } else if (eventHandlerList.size() == 1) {
                eventHandlerList.add(_Pair.create(operateValue, JsonValueWord.ON_ERROR));
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            return this;
        }

        @Override
        public MySQLClause._JsonValueOptionSpec onEmpty() {
            final Object operateValue = this.operateValue;
            if (operateValue == null || this.eventHandlerList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = null;//clear
            final List<_Pair<Object, JsonValueWord>> eventHandlerList = new ArrayList<>(2);
            this.eventHandlerList = eventHandlerList;
            eventHandlerList.add(_Pair.create(operateValue, JsonValueWord.ON_EMPTY));
            return this;
        }

        private CriteriaException typeError(MySQLCastType type) {
            String m = String.format("%s error", type);
            return CriteriaContextStack.criteriaError(this.context, m);
        }


    }//JsonValueFunc


}
