package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.mysql.*;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.meta.TypeMeta;
import io.army.sqltype.MySQLTypes;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleParam;
import io.army.stmt.Stmt;
import io.army.util._CollectionUtils;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MySQLFunctionUtils extends FunctionUtils {

    private MySQLFunctionUtils() {
    }


    static <W extends Item, I extends Item> MySQLFunctionSyntax._OverSpec<W, I> noArgWindowFunc(String name
            , TypeMeta returnType, Function<_ItemWindow<I>, W> endFunction
            , Function<TypeInfer, I> asFunction) {
        return new NoArgWindowFunction<>(name, returnType, endFunction, asFunction);
    }

    static <W extends Item, I extends Item> MySQLFunctionSyntax._OverSpec<W, I> oneArgWindowFunc(String name
            , Expression arg, TypeMeta returnType, Function<_ItemWindow<I>, W> endFunction
            , Function<TypeInfer, I> asFunction) {
        if (arg instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, arg);
        }
        return new OneArgWindowFunction<>(name, (ArmyExpression) arg, returnType, endFunction, asFunction);
    }

    static <W extends Item, I extends Item> MySQLFunctionSyntax._OverSpec<W, I> twoArgWindowFunc(String name
            , Expression one, Expression two, TypeMeta returnType, Function<_ItemWindow<I>, W> endFunction
            , Function<TypeInfer, I> asFunction) {
        return new MultiArgWindowFunction<>(name, null, twoExpList(name, one, two), returnType, endFunction, asFunction);
    }

    static <W extends Item, I extends Item> MySQLFunctionSyntax._OverSpec<W, I> threeArgWindow(
            String name, Expression one
            , Expression two, Expression three
            , TypeMeta returnType, Function<_ItemWindow<I>, W> endFunction
            , Function<TypeInfer, I> asFunction) {
        return new MultiArgWindowFunction<>(name, null, threeExpList(name, one, two, three), returnType, endFunction, asFunction);
    }


    static <W extends Item, I extends Item> MySQLFunctionSyntax._FromFirstLastOverSpec<W, I> twoArgFromFirstWindowFunc(
            String name, Expression one, Expression two
            , TypeMeta returnType, Function<_ItemWindow<I>, W> endFunction
            , Function<TypeInfer, I> asFunction) {
        return new FromFirstLastMultiArgWindowFunc<>(name, twoExpList(name, one, two), returnType, endFunction, asFunction);
    }


    static <W extends Item, I extends Item> MySQLFunctionSyntax._AggregateWindowFunc<W, I> oneArgAggregateWindow(
            String name, Expression arg, TypeMeta returnType, Function<_ItemWindow<I>, W> endFunction
            , Function<TypeInfer, I> asFunction) {
        if (arg instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, arg);
        }
        return new OneArgAggregateWindowFunc<>(name, (ArmyExpression) arg, returnType, endFunction, asFunction);
    }

    static <W extends Item, I extends Item> MySQLFunctionSyntax._AggregateWindowFunc<W, I> oneArgAggregate(
            String name, @Nullable SQLWords option, Expression arg, TypeMeta returnType
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        assert option == null || option == SQLs.DISTINCT || option == MySQLs.DISTINCT;
        if (arg instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, arg);
        }
        return new OneArgOptionAggregateWindowFunc<>(name, option, (ArmyExpression) arg, returnType, endFunction, asFunction);
    }

    static <W extends Item, I extends Item> MySQLFunctionSyntax._AggregateWindowFunc<W, I> multiArgAggregateWindowFunc(
            String name, @Nullable SQLWords option, List<Expression> argList, TypeMeta returnType
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        assert option == null || option == SQLs.DISTINCT || option == MySQLs.DISTINCT;
        final List<ArmyExpression> expList = new ArrayList<>(argList.size());
        for (Expression arg : argList) {
            expList.add((ArmyExpression) arg);
        }
        return new MultiArgAggregateWindowFunc<>(name, option, expList, returnType, endFunction, asFunction);
    }


    static <E extends Expression, I extends Item> MySQLFunction._JsonValueLeftParenClause<E> jsonValueFunc(
            Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return new JsonValueClause<>(expFunc, endFunc);
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

    static <I extends Item> MySQLFunction._JsonTableColumnsClause<I> jsonTable(Function<TabularItem, I> function) {
        return new JsonTableFunction<>(function);
    }


    private static abstract class MySQLWindowFunction<W extends Item, I extends Item> extends WindowFunction<
            Window._SimpleLeftParenClause<W>,
            W,
            I> implements MySQLFunctionSyntax._OverSpec<W, I>
            , MySQLFunction {


        private MySQLWindowFunction(String name, TypeMeta returnType, Function<_ItemWindow<I>, W> endFunction
                , Function<TypeInfer, I> aliasFunction) {
            super(name, returnType, endFunction, aliasFunction);
        }

        @Override
        public final Window._SimpleLeftParenClause<W> over() {
            return WindowClause.anonymousWindow(this.context, this::windowEnd);
        }


    }//MySQLWindowFunction

    private static class NoArgWindowFunction<W extends Item, I extends Item>
            extends MySQLWindowFunction<W, I>
            implements FunctionUtils.NoArgFunction {

        private NoArgWindowFunction(String name, TypeMeta returnType, Function<_ItemWindow<I>, W> endFunction
                , Function<TypeInfer, I> aliasFunction) {
            super(name, returnType, endFunction, aliasFunction);
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


    private static class OneArgWindowFunction<W extends Item, I extends Item>
            extends MySQLWindowFunction<W, I> {

        private final ArmyExpression argument;

        private OneArgWindowFunction(String name, ArmyExpression argument, TypeMeta returnType
                , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> aliasFunction) {
            super(name, returnType, endFunction, aliasFunction);
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


    private static class OneOptionArgWindowFunction<W extends Item, I extends Item>
            extends MySQLWindowFunction<W, I> {

        private final SQLWords option;

        private final ArmyExpression argument;


        private OneOptionArgWindowFunction(String name, @Nullable SQLWords option, ArmyExpression argument, TypeMeta returnType
                , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> aliasFunction) {
            super(name, returnType, endFunction, aliasFunction);
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


    }//OneOptionArgWindowFunction


    private static class MultiArgWindowFunction<W extends Item, I extends Item>
            extends MySQLWindowFunction<W, I> {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        private MultiArgWindowFunction(String name, @Nullable SQLWords option, List<ArmyExpression> argList
                , TypeMeta returnType, Function<_ItemWindow<I>, W> endFunction
                , Function<TypeInfer, I> aliasFunction) {
            super(name, returnType, endFunction, aliasFunction);
            assert argList.size() > 0;
            this.option = option;
            this.argList = argList;
        }

        @Override
        final void appendArguments(final _SqlContext context) {
            FunctionUtils.appendArguments(this.option, this.argList, context);
        }

        @Override
        final void argumentToString(final StringBuilder builder) {
            FunctionUtils.argumentsToString(this.option, this.argList, builder);
        }


    }//MultiArgWindowFunction


    private static final class FromFirstLastMultiArgWindowFunc<W extends Item, I extends Item>
            extends MultiArgWindowFunction<W, I>
            implements MySQLFunctionSyntax._FromFirstLastOverSpec<W, I> {


        private FromFirstLast fromFirstLast;

        private NullTreatment nullTreatment;

        /**
         * @see #twoArgFromFirstWindowFunc(String, Expression, Expression, TypeMeta, Function, Function)
         */
        public FromFirstLastMultiArgWindowFunc(String name, List<ArmyExpression> argList, TypeMeta returnType
                , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> aliasFunction) {
            super(name, null, argList, returnType, endFunction, aliasFunction);
        }

        @Override
        public MySQLFunctionSyntax._NullTreatmentOverSpec<W, I> fromFirst() {
            this.fromFirstLast = FromFirstLast.FROM_FIRST;
            return this;
        }

        @Override
        public MySQLFunctionSyntax._NullTreatmentOverSpec<W, I> fromLast() {
            this.fromFirstLast = FromFirstLast.FROM_LAST;
            return this;
        }

        @Override
        public MySQLFunctionSyntax._NullTreatmentOverSpec<W, I> ifFromFirst(BooleanSupplier predicate) {
            this.fromFirstLast = predicate.getAsBoolean() ? FromFirstLast.FROM_FIRST : null;
            return this;
        }

        @Override
        public MySQLFunctionSyntax._NullTreatmentOverSpec<W, I> ifFromLast(BooleanSupplier predicate) {
            this.fromFirstLast = predicate.getAsBoolean() ? FromFirstLast.FROM_LAST : null;
            return this;
        }

        @Override
        public MySQLFunctionSyntax._OverSpec<W, I> respectNulls() {
            this.nullTreatment = NullTreatment.RESPECT_NULLS;
            return this;
        }

        @Override
        public MySQLFunctionSyntax._OverSpec<W, I> ignoreNulls() {
            this.nullTreatment = NullTreatment.IGNORE_NULLS;
            return this;
        }

        @Override
        public MySQLFunctionSyntax._OverSpec<W, I> ifRespectNulls(BooleanSupplier predicate) {
            this.nullTreatment = predicate.getAsBoolean() ? NullTreatment.RESPECT_NULLS : null;
            return this;
        }

        @Override
        public MySQLFunctionSyntax._OverSpec<W, I> ifIgnoreNulls(BooleanSupplier predicate) {
            this.nullTreatment = predicate.getAsBoolean() ? NullTreatment.IGNORE_NULLS : null;
            return this;
        }

        @Override
        void appendOuterClause(final _SqlContext context) {
            final FromFirstLast fromFirstLast = this.fromFirstLast;
            final NullTreatment nullTreatment = this.nullTreatment;

            if (fromFirstLast != null || nullTreatment != null) {
                final StringBuilder sqlBuilder;
                sqlBuilder = context.sqlBuilder();
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


    private static final class OneArgAggregateWindowFunc<W extends Item, I extends Item>
            extends OneArgWindowFunction<W, I>
            implements MySQLFunctionSyntax._AggregateWindowFunc<W, I> {

        private OneArgAggregateWindowFunc(String name, ArmyExpression argument, TypeMeta returnType
                , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> aliasFunction) {
            super(name, argument, returnType, endFunction, aliasFunction);
        }


    }//OneArgAggregateWindowFunc

    private static final class OneArgOptionAggregateWindowFunc<W extends Item, I extends Item>
            extends OneOptionArgWindowFunction<W, I>
            implements MySQLFunctionSyntax._AggregateWindowFunc<W, I> {

        /**
         * @see #oneArgAggregate(String, SQLWords, Expression, TypeMeta, Function, Function)
         */
        private OneArgOptionAggregateWindowFunc(String name, @Nullable SQLWords option, ArmyExpression argument
                , TypeMeta returnType, Function<_ItemWindow<I>, W> endFunction
                , Function<TypeInfer, I> aliasFunction) {
            super(name, option, argument, returnType, endFunction, aliasFunction);
        }


    }//OneArgAggregateWindowFunc


    private static final class MultiArgAggregateWindowFunc<W extends Item, I extends Item>
            extends MultiArgWindowFunction<W, I>
            implements MySQLFunctionSyntax._AggregateWindowFunc<W, I> {

        private MultiArgAggregateWindowFunc(String name, @Nullable SQLWords option, List<ArmyExpression> argList
                , TypeMeta returnType, Function<_ItemWindow<I>, W> endFunction
                , Function<TypeInfer, I> aliasFunction) {
            super(name, option, argList, returnType, endFunction, aliasFunction);
        }

    }//MultiArgAggregateWindowFunc


    static final class GroupConcatClause extends OperationExpression
            implements MySQLFunction._GroupConcatOrderBySpec {

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
        public MySQLFunction._GroupConcatSeparatorClause orderBy(Expression exp) {
            //TODO
            return null;
        }

        @Override
        public MySQLFunction._GroupConcatSeparatorClause orderBy(Expression exp, Statement.AscDesc ascDesc) {
            return null;
        }

        @Override
        public MySQLFunction._GroupConcatSeparatorClause orderBy(Expression exp1, Expression exp2) {
            return null;
        }

        @Override
        public MySQLFunction._GroupConcatSeparatorClause orderBy(Expression exp1, Statement.AscDesc ascDesc1, Expression exp2) {
            return null;
        }

        @Override
        public MySQLFunction._GroupConcatSeparatorClause orderBy(Expression exp1, Expression exp2, Statement.AscDesc ascDesc2) {
            return null;
        }

        @Override
        public MySQLFunction._GroupConcatSeparatorClause orderBy(Expression exp1, Statement.AscDesc ascDesc1, Expression exp2, Statement.AscDesc ascDesc2) {
            return null;
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

        private MySQLFunction._GroupConcatSeparatorClause orderByEnd(final List<ArmySortItem> itemList) {
            if (this.orderByList != null) {
                throw ContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.orderByList = itemList;
            return this;
        }


    }//GroupConcatClause

    private static final class StatementDigestFunc extends FunctionUtils.FunctionExpression {

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
     * @see JsonValueFunction#appendSql(_SqlContext)
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
                ((ArmyExpression) pair.first).appendSql(context);
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


    private static final class JsonValueClause<E extends Expression, I extends Item>
            extends OnEmptyOrErrorAction<JsonValueClause<E, I>>
            implements MySQLFunction._JsonValueLeftParenClause<E>
            , MySQLFunction._JsonValueReturningSpec<E>
            , MySQLFunction._JsonValueOptionOnEmptySpec<E>
            , MySQLFunction._JsonValueOnEmptySpec<E>
            , Statement._RightParenClause<E> {

        private final Function<_ItemExpression<I>, E> expFunc;

        private final Function<TypeInfer, I> endFunc;

        private ArmyExpression jsonDoc;

        private ArmyExpression path;

        private List<Object> returningList;

        private List<_Pair<Object, JsonValueWord>> eventHandlerList;

        private Object operateValue;

        /**
         * @see #jsonValueFunc(Function, Function)
         */
        private JsonValueClause(Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
            super(ContextStack.peek());
            this.expFunc = expFunc;
            this.endFunc = endFunc;
        }


        @Override
        public MySQLFunction._JsonValueReturningSpec<E> leftParen(final @Nullable Expression jsonDoc, final @Nullable Expression path) {
            if (this.jsonDoc != null || this.path != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (jsonDoc == null || path == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.jsonDoc = (ArmyExpression) jsonDoc;
            this.path = (ArmyExpression) path;
            return this;
        }

        @Override
        public MySQLFunction._JsonValueReturningSpec<E> leftParen(Expression jsonDoc, String path) {
            return this.leftParen(jsonDoc, SQLs.literal(StringType.INSTANCE, path));
        }

        @Override
        public MySQLFunction._JsonValueReturningSpec<E> leftParen(String jsonDoc, String path) {
            return this.leftParen(SQLs.param(StringType.INSTANCE, jsonDoc), SQLs.literal(StringType.INSTANCE, path));
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec<E> returning(final MySQLCastType type) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.singletonList(type);
            return this;
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec<E> returning(final MySQLCastType type, Expression n) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (!MySQLUtils.isSingleParamType(type)) {
                throw typeError(type);
            }
            final List<Object> list = new ArrayList<>(4);

            list.add(type);
            list.add(SQLSyntax.FuncWord.LEFT_PAREN);
            list.add(n);
            list.add(SQLSyntax.FuncWord.RIGHT_PAREN);

            this.returningList = list;
            return this;
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec<E> returning(MySQLCastType type, Expression n, SQLElement charset) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (type != MySQLCastType.CHAR) {
                throw typeError(type);
            } else if (!(charset instanceof MySQLCharset || charset instanceof SQLs.SQLIdentifierImpl)) {
                throw CriteriaUtils.funcArgError("JSON_VALUE", charset);
            }
            final List<Object> list = new ArrayList<>(5);

            list.add(type);
            list.add(SQLSyntax.FuncWord.LEFT_PAREN);
            list.add(n);
            list.add(SQLSyntax.FuncWord.RIGHT_PAREN);

            list.add(charset);
            this.returningList = list;
            return this;
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec<E> returning(MySQLCastType type, Expression m, Expression d) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (type != MySQLCastType.DECIMAL) {
                throw typeError(type);
            }
            final List<Object> list = new ArrayList<>(6);

            list.add(type);
            list.add(SQLSyntax.FuncWord.LEFT_PAREN);
            list.add(m);
            list.add(SQLSyntax.FuncWord.COMMA);

            list.add(d);
            list.add(SQLSyntax.FuncWord.RIGHT_PAREN);
            this.returningList = list;
            return this;
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec<E> returning(MySQLCastType type, int n) {
            return this.returning(type, SQLs.literal(IntegerType.INSTANCE, n));
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec<E> returning(MySQLCastType type, int m, int d) {
            return this.returning(type, SQLs.literal(IntegerType.INSTANCE, m), SQLs.literal(IntegerType.INSTANCE, d));
        }


        @Override
        public E rightParen() {
            if (this.jsonDoc == null || this.path == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this.expFunc.apply(new JsonValueFunction<>(this));
        }

        private CriteriaException typeError(MySQLCastType type) {
            String m = String.format("%s error", type);
            return ContextStack.criteriaError(this.context, m);
        }


    }//JsonValueClause


    private static final class JsonValueFunction<I extends Item> extends OperationExpression<I>
            implements MySQLFunction
            , OperationExpression.MutableParamMetaSpec {

        private final ArmyExpression jsonDoc;

        private final ArmyExpression path;

        private final List<Object> returningList;

        private final List<_Pair<Object, JsonValueWord>> eventHandlerList;

        private TypeMeta returnType;


        private JsonValueFunction(JsonValueClause<?, I> clause) {
            super(clause.endFunc);
            this.jsonDoc = clause.jsonDoc;
            this.path = clause.path;
            this.returningList = clause.returningList;
            this.eventHandlerList = clause.eventHandlerList;

        }

        @Override
        public TypeMeta typeMeta() {
            TypeMeta returnType = this.returnType;
            if (returnType == null) {
                final List<Object> returningList = this.returningList;
                if (returningList == null) {
                    returnType = StringType.INSTANCE;
                } else {
                    returnType = MySQLFunctions._castReturnType((MySQLCastType) returningList.get(0));
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
                                .append(((MySQLCastType) o).render());
                    } else if (o == SQLSyntax.FuncWord.LEFT_PAREN) {
                        sqlBuilder.append(_Constant.LEFT_PAREN);
                    } else if (o instanceof SQLWords) {
                        sqlBuilder.append(((SQLWords) o).render());
                    } else if (o instanceof Expression) {
                        ((ArmyExpression) o).appendSql(context);
                    } else if (o instanceof SQLIdentifier) {
                        sqlBuilder.append(_Constant.SPACE);
                        context.parser().identifier(((SQLIdentifier) o).render(), sqlBuilder);
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

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


        @Override
        public String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder()
                    .append(" JSON_VALUE(");

            sqlBuilder.append(this.jsonDoc)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.path);

            final List<Object> returningList = this.returningList;
            if (returningList != null) {
                assert returningList.get(0) instanceof MySQLCastType;
                for (Object o : returningList) {
                    if (o instanceof MySQLCastType) {
                        sqlBuilder.append(_Constant.SPACE_RETURNING)
                                .append(((MySQLCastType) o).render());
                    } else if (o == SQLSyntax.FuncWord.LEFT_PAREN) {
                        sqlBuilder.append(_Constant.LEFT_PAREN);
                    } else if (o instanceof SQLWords) {
                        sqlBuilder.append(((SQLWords) o).render());
                    } else if (o instanceof Expression) {
                        sqlBuilder.append(o);
                    } else if (o instanceof SQLIdentifier) {
                        sqlBuilder.append(_Constant.SPACE);
                        sqlBuilder.append(o);
                    } else {
                        //no bug,never here
                        throw new IllegalStateException();
                    }

                }//for
            }//if

            final List<_Pair<Object, JsonValueWord>> eventHandlerList = this.eventHandlerList;
            if (eventHandlerList != null) {
                onEmptyOrErrorClauseToString(eventHandlerList, sqlBuilder);
            }//if

            return sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
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
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);
            context.parser().identifier(this.name, sqlBuilder);
            sqlBuilder.append(MySQLs.FOR_ORDINALITY.render());
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
        private final MySQLTypes type;

        private final ArmyExpression n;

        private final ArmyExpression d;

        private final SQLElement charset;

        private final SQLIdentifier collate;

        private final SQLWords pathWord;

        private final ArmyExpression path;

        private JsonTableOnEmptyOrErrorAction actionClause;

        private JsonTablePathColumn(String name, MySQLTypes type
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

        private JsonTablePathColumn(String name, MySQLTypes type
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

        private JsonTablePathColumn(String name, MySQLTypes type
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

        private JsonTablePathColumn(String name, MySQLTypes type
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
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);
            context.parser().identifier(this.name, sqlBuilder)
                    .append(this.type.render());

            if (this.n != null) {
                sqlBuilder.append(_Constant.LEFT_PAREN);
                this.n.appendSql(context);
                if (this.d != null) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                    this.d.appendSql(context);
                }
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            final SQLElement charset = this.charset;
            DialectParser parser = null;
            if (charset != null) {
                sqlBuilder.append(" CHARACTER SET");
                if (charset instanceof MySQLCharset) {
                    sqlBuilder.append(((MySQLCharset) charset).render());
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

            sqlBuilder.append(this.pathWord.render());
            this.path.appendSql(context);

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
                    .append(this.type.render());

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
                    sqlBuilder.append(((MySQLCharset) charset).render());
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

            sqlBuilder.append(this.pathWord.render())
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
        public final JsonTableColumnsClause<R> column(String name, MySQLs.WordsForOrdinality forOrdinality) {
            return this.comma(name, forOrdinality);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLTypes type, MySQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLTypes type
                , Expression n, MySQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLTypes type
                , int n, MySQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLTypes type
                , Expression n, SQLElement charset
                , MySQLs.WordPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, n, charset, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLTypes type
                , int n, SQLElement charset, SQLIdentifier collate
                , MySQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLTypes type
                , Expression n, SQLElement charset
                , SQLIdentifier collate, MySQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLTypes type
                , int p, int m
                , MySQLs.WordPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> column(String name, MySQLTypes type
                , Expression p, Expression m
                , MySQLs.WordPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLTypes type
                , MySQLs.WordExistsPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLTypes type
                , Expression n, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLTypes type
                , int n, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLTypes type
                , Expression n, SQLElement charset
                , MySQLs.WordExistsPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, n, charset, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLTypes type
                , int n, SQLElement charset
                , SQLIdentifier collate, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLTypes type
                , Expression n, SQLElement charset
                , SQLIdentifier collate, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLTypes type
                , int p, int m
                , MySQLs.WordExistsPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final MySQLJsonColumnClause column(String name, MySQLTypes type
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
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLs.WordsForOrdinality forOrdinality) {
            return this.comma(name, forOrdinality);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type
                , MySQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, Expression n
                , MySQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, int n
                , MySQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, Expression n
                , SQLElement charset, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, int n
                , SQLElement charset, SQLIdentifier collate, MySQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, Expression n
                , SQLElement charset, SQLIdentifier collate, MySQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, int p, int m
                , MySQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, Expression p
                , Expression m, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, Expression n
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, int n
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, Expression n
                , SQLElement charset, MySQLs.WordExistsPath path, Function<String, Expression> operator
                , String stringPath) {
            return this.comma(name, type, n, charset, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, int n
                , SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, Expression n
                , SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, int p, int m
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, p, m, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> leftParen(String name, MySQLTypes type, Expression p
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
                , MySQLs.WordsForOrdinality forOrdinality) {
            assert forOrdinality == MySQLs.FOR_ORDINALITY;
            this.onAddColumn(new JsonTableForOrdinalityColumn(name));
            return this;
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type
                , MySQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.PATH;
            if (!type.isNoPrecision()) {
                throw MySQLUtils.noPrecision(this.context, type);
            }
            this.onAddColumn(new JsonTablePathColumn(name, type, path, operator.apply(stringPath)));
            return this;
        }


        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type, Expression n
                , MySQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.PATH;
            if (!type.isSupportPrecision()) {
                throw MySQLUtils.dontSupportPrecision(this.context, type);
            }
            final JsonTablePathColumn column;
            column = new JsonTablePathColumn(name, type, n, path, operator.apply(stringPath));
            return this.onAddColumn(column);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type, int n
                , MySQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, SQLs.literal(IntegerType.INSTANCE, n), path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type, Expression n
                , SQLElement charset, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.PATH;
            return this.addColumnWithCharset(name, type, n, charset, null, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type, int n
                , SQLElement charset, SQLIdentifier collate, MySQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            final Expression expOfn;
            expOfn = SQLs.literal(IntegerType.INSTANCE, n);
            return this.comma(name, type, expOfn, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type
                , Expression n, SQLElement charset, @Nullable SQLIdentifier collate, MySQLs.WordPath path
                , Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.PATH;
            if (collate == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return this.addColumnWithCharset(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type, int p, int m
                , MySQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            final Expression expOfp, expOfm;
            expOfp = SQLs.literal(IntegerType.INSTANCE, p);
            expOfm = SQLs.literal(IntegerType.INSTANCE, m);
            return this.comma(name, type, expOfp, expOfm, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type, Expression p
                , Expression m, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.PATH;
            if (!type.isSupportPrecisionScale()) {
                throw MySQLUtils.dontSupportPrecisionScale(this.context, type);
            }
            final JsonTablePathColumn column;
            column = new JsonTablePathColumn(name, type, p, m, path, operator.apply(stringPath));
            return this.onAddColumn(column);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.EXISTS_PATH;
            if (!type.isNoPrecision()) {
                throw MySQLUtils.noPrecision(this.context, type);
            }
            this.onAddColumn(new JsonTablePathColumn(name, type, path, operator.apply(stringPath)));
            return this;
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type, Expression n
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
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type, int n
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            return this.comma(name, type, SQLs.literal(IntegerType.INSTANCE, n), path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type, Expression n
                , SQLElement charset, MySQLs.WordExistsPath path, Function<String, Expression> operator
                , String stringPath) {
            assert path == MySQLs.EXISTS_PATH;
            return this.addColumnWithCharset(name, type, n, charset, null, path, operator, stringPath);
        }


        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type, int n
                , SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            final Expression expOfn;
            expOfn = SQLs.literal(IntegerType.INSTANCE, n);
            return this.comma(name, type, expOfn, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type, Expression n
                , SQLElement charset, @Nullable SQLIdentifier collate, MySQLs.WordExistsPath path
                , Function<String, Expression> operator, String stringPath) {
            assert path == MySQLs.EXISTS_PATH;
            if (collate == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return this.addColumnWithCharset(name, type, n, charset, collate, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type, int p, int m
                , MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath) {
            final Expression expOfp, expOfm;
            expOfp = SQLs.literal(IntegerType.INSTANCE, p);
            expOfm = SQLs.literal(IntegerType.INSTANCE, m);
            return this.comma(name, type, expOfp, expOfm, path, operator, stringPath);
        }

        @Override
        public final JsonTableColumnsClause<R> comma(String name, MySQLTypes type, Expression p
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
            this.columnList = _CollectionUtils.unmodifiableList(columnList);
            return this.onRightParen();
        }


        abstract R onRightParen();

        void onAddSelect(Selection selection) {
            throw new UnsupportedOperationException();
        }

        private JsonTableColumnsClause<R> onAddColumn(final JsonTableColumn column) {
            List<JsonTableColumn> columnList = this.columnList;
            if (columnList == null) {
                columnList = new ArrayList<>();
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

        private JsonTableColumnsClause<R> addColumnWithCharset(String name, MySQLTypes type, Expression n
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
                columnList.get(i).appendSql(context);
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
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" NESTED");
            if (this.fullPathWord) {
                sqlBuilder.append(" PATH");
            }
            this.path.appendSql(context);

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
            , ArmyDerivedTable {

        private ArmyExpression expr;

        private ArmyExpression path;

        private final Function<TabularItem, R> function;

        private List<Selection> selectionList = new ArrayList<>();

        private Map<String, Selection> selectionMap = new HashMap<>();

        private List<String> columnAliasList;

        private JsonTableFunction(Function<TabularItem, R> function) {
            super(ContextStack.peek());
            this.function = function;
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
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" JSON_TABLE(");

            final ArmyExpression expr = this.expr, path = this.path;
            assert expr != null && path != null;

            expr.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            path.appendSql(context);

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
        public List<Selection> selectItemList() {
            final List<Selection> selectionList = this.selectionList;
            if (selectionList == null || selectionList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return selectionList;
        }

        @Override
        public Selection selection(final String derivedAlias) {
            final Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null || selectionMap instanceof HashMap) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (this.columnAliasList == null) {
                this.columnAliasList = Collections.emptyList();
            }
            return selectionMap.get(derivedAlias);
        }

        @Override
        public void setColumnAliasList(final List<String> aliasList) {
            final List<Selection> selectionList = this.selectionList;
            final int selectionSize;
            if (this.columnAliasList != null) {
                //no bug,never here
                throw new IllegalStateException("columnAliasList non-null");
            } else if (selectionList == null || selectionList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (aliasList.size() != (selectionSize = selectionList.size())) {
                String m = String.format("JSON_TABLE alias list size[%s] and column list size[%s] not match."
                        , aliasList.size(), selectionList.size());
                throw ContextStack.criteriaError(this.context, m);
            }

            Map<String, Selection> aliasToSelection = new HashMap<>((int) (selectionSize / 0.75f));
            String alias;
            for (int i = 0; i < selectionSize; i++) {
                alias = aliasList.get(i);
                if (aliasToSelection.putIfAbsent(alias, selectionList.get(i)) != null) {
                    String m = String.format("Duplicate column alias[%s]", alias);
                    throw ContextStack.criteriaError(this.context, m);
                }
            }
            aliasToSelection = _CollectionUtils.unmodifiableMap(aliasToSelection);
            assert aliasToSelection.size() == selectionSize;
            this.selectionMap = aliasToSelection;
            this.columnAliasList = aliasList;
        }

        @Override
        public List<String> columnAliasList() {
            List<String> aliasList = this.columnAliasList;
            if (aliasList == null) {
                aliasList = Collections.emptyList();
                this.columnAliasList = aliasList;
            }
            return aliasList;
        }


        @Override
        void onAddSelect(final Selection selection) {
            final List<Selection> selectionList = this.selectionList;
            final Map<String, Selection> selectionMap = this.selectionMap;
            if (!(selectionList instanceof ArrayList && selectionMap instanceof HashMap)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (selectionMap.putIfAbsent(selection.alias(), selection) != null) {
                String m = String.format("Duplicate column name[%s]", selection.alias());
                throw ContextStack.criteriaError(this.context, m);
            }
            selectionList.add(selection);
        }

        @Override
        R onRightParen() {
            if (this.expr == null || this.path == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final List<Selection> selectionList = this.selectionList;
            final Map<String, Selection> selectionMap = this.selectionMap;
            if (!(selectionList instanceof ArrayList && selectionMap instanceof HashMap && selectionList.size() > 0)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.selectionList = _CollectionUtils.unmodifiableList(selectionList);
            this.selectionMap = _CollectionUtils.unmodifiableMap(selectionMap);
            return this.function.apply(this);
        }


    }//JsonTableFunction


}
