package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.impl.inner._Window;
import io.army.criteria.standard.StandardSqlFunction;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.function.BetweenOperator;
import io.army.function.BetweenValueOperator;
import io.army.function.ExpressionOperator;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.mapping.VoidType;
import io.army.meta.TypeMeta;
import io.army.util._CollectionUtils;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

abstract class FunctionUtils {

    FunctionUtils() {
        throw new UnsupportedOperationException();
    }

    private static final Pattern FUNC_NAME_PATTERN = Pattern.compile("^[_a-zA-Z]\\w*$");

    enum FuncWord implements SQLWords {

        INTERVAL,
        COMMA,
        FROM,
        USING,
        IN,
        AS,
        AT_TIME_ZONE,
        LEFT_PAREN,
        RIGHT_PAREN;

        @Override
        public final String render() {
            final String words;
            switch (this) {
                case COMMA:
                    words = ",";
                    break;
                case LEFT_PAREN:
                    words = "(";
                    break;
                case RIGHT_PAREN:
                    words = ")";
                    break;
                case AT_TIME_ZONE:
                    words = "AT TIME ZONE";
                    break;
                default:
                    words = this.name();
            }
            return words;
        }

        @Override
        public final String toString() {
            return String.format("%s.%s", FuncWord.class.getSimpleName(), this.name());
        }


    }//Word

    static List<ArmyExpression> funcParamList(final List<?> argList) {
        final List<ArmyExpression> expList = new ArrayList<>(argList.size());
        for (Object o : argList) {
            expList.add(SQLs._funcParam(o));
        }
        return expList;
    }


    static <R extends Item, I extends Item> StandardSqlFunction._CaseFuncWhenClause<R> caseFunction(
            final @Nullable Expression caseValue, final Function<_ItemExpression<I>, R> endFunc
            , final Function<Selection, I> asFunc) {
        return new CaseFunction<>((ArmyExpression) caseValue, endFunc, asFunc);
    }

    @Deprecated
    static Expression oneArgOptionFunc(String name, @Nullable SQLWords option
            , @Nullable Object expr, @Nullable Clause clause, TypeMeta returnType) {
        return new OneArgOptionFunc(name, option, SQLs._funcParam(expr), clause, returnType);
    }

    static Expression oneArgFunc(String name, @Nullable Object expr, TypeMeta returnType) {
        if (expr instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, expr);
        }
        return new OneArgFunc(name, SQLs._funcParam(expr), returnType);
    }

    static Expression twoArgFunc(final String name, final Expression one
            , final Expression two, TypeMeta returnType) {
        return new ComplexArgFunc(name, twoArgList(name, one, two), returnType);
    }

    static Expression threeArgFunc(final String name, final Expression one
            , final Expression two, final Expression three, TypeMeta returnType) {
        return new ComplexArgFunc(name, threeArgList(name, one, two, three), returnType);
    }


    static IPredicate twoArgPredicateFunc(final String name, final Expression one, final Expression two) {
        return new ComplexFuncPredicate(name, twoArgList(name, one, two));
    }

    static IPredicate threeArgPredicateFunc(final String name, final Expression one
            , final Expression two, final Expression three) {
        return new ComplexFuncPredicate(name, threeArgList(name, one, two, three));
    }


    static Expression oneOrMultiArgFunc(String name, Expression exp, TypeMeta returnType) {
        return new OneArgFunc(name, (ArmyExpression) exp, returnType);
    }

    static Expression noArgFunc(String name, TypeMeta returnType) {
        return new NoArgFuncExpression(name, returnType);
    }

    static IPredicate noArgFuncPredicate(final String name) {
        return new NoArgFuncPredicate(name);
    }

    static IPredicate oneArgFuncPredicate(final String name, final Expression argument) {
        if (argument instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, argument);
        }
        return new OneArgFuncPredicate(name, (ArmyExpression) argument);
    }

    static IPredicate complexArgPredicate(final String name, List<?> argList) {
        return new ComplexFuncPredicate(name, argList);
    }


    static Expression complexArgFunc(String name, List<?> argList, TypeMeta returnType) {
        return new ComplexArgFunc(name, argList, returnType);
    }

    static NamedExpression namedComplexArgFunc(String name, List<?> argList, TypeMeta returnType, String expAlias) {
        return new NamedComplexArgFunc(name, argList, returnType, expAlias);
    }

    static Expression jsonObjectFunc(String name, Map<String, Expression> expMap, TypeMeta returnType) {
        return new JsonObjectFunc(name, expMap, returnType);
    }

    @Deprecated
    static Expression safeMultiArgOptionFunc(String name, @Nullable SQLWords option
            , List<ArmyExpression> argList, @Nullable Clause clause, TypeMeta returnType) {
        return new MultiArgOptionFunc(name, option, argList, clause, returnType);
    }

    @Deprecated
    static Expression multiArgOptionFunc(String name, @Nullable SQLWords option
            , List<?> argList, @Nullable Clause clause, TypeMeta returnType) {
        return new MultiArgOptionFunc(name, option, funcParamList(argList), clause, returnType);
    }

    static Object sqlIdentifier(String identifier) {
        return new SQLIdentifier(identifier);
    }

    static Functions._FuncConditionTowClause conditionTwoFunc(final String name
            , BiFunction<MappingType, MappingType, MappingType> function) {
        return new ConditionTwoFunc(name, function);
    }


    static void appendArguments(final @Nullable SQLWords option, final List<ArmyExpression> argList
            , final @Nullable Clause clause, final _SqlContext context) {

        final StringBuilder sqlBuilder = context.sqlBuilder();

        if (option != null) {
            sqlBuilder.append(_Constant.SPACE)
                    .append(option.render());
        }

        final int argSize = argList.size();
        assert argSize > 0;

        for (int i = 0; i < argSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }

            argList.get(i).appendSql(context);
        }//for

        if (clause != null) {
            ((_SelfDescribed) clause).appendSql(context);
        }

    }

    static void argumentsToString(final @Nullable SQLWords option, final List<ArmyExpression> argList
            , final @Nullable Clause clause, final StringBuilder builder) {

        if (option != null) {
            builder.append(_Constant.SPACE)
                    .append(option.render());
        }

        final int argSize = argList.size();
        assert argSize > 0;
        for (int i = 0; i < argSize; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(argList.get(i));

        }//for

        if (clause != null) {
            builder.append(clause);
        }

    }


    static void appendComplexArg(final List<?> argumentList, final _SqlContext context) {
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        DialectParser parser = null;
        for (Object o : argumentList) {
            if (o instanceof Expression) {
                ((ArmyExpression) o).appendSql(context); // convert to ArmyExpression to avoid non-army expression
            } else if (o == FuncWord.LEFT_PAREN) {
                sqlBuilder.append(((SQLWords) o).render());
            } else if (o instanceof SQLWords) {
                sqlBuilder.append(_Constant.SPACE)
                        .append(((SQLWords) o).render());
            } else if (o instanceof SQLIdentifier) { // sql identifier
                sqlBuilder.append(_Constant.SPACE);
                if (parser == null) {
                    parser = context.parser();
                }
                parser.identifier(((SQLIdentifier) o).identifier, sqlBuilder);
            } else if (o instanceof Clause) {
                ((_SelfDescribed) o).appendSql(context);
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }

        }//for

    }

    static void complexArgToString(final List<?> argumentList, final StringBuilder builder) {
        for (Object o : argumentList) {
            if (o instanceof Expression || o instanceof Clause) {
                builder.append(o);
            } else if (o == FuncWord.LEFT_PAREN) {
                builder.append(((SQLWords) o).render());
            } else if (o instanceof SQLWords) {
                builder.append(_Constant.SPACE)
                        .append(((SQLWords) o).render());
            } else if (o instanceof SQLIdentifier) { // sql identifier
                builder.append(_Constant.SPACE)
                        .append(((SQLIdentifier) o).identifier);
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }

        }//for
    }

    static List<Object> twoArgList(final String name, Expression one, Expression two) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        final List<Object> argList = new ArrayList<>(3);
        argList.add(one);
        argList.add(FuncWord.COMMA);
        argList.add(two);
        return argList;
    }

    static List<Object> threeArgList(final String name, Expression one, Expression two, Expression three) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (three instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        final List<Object> argList = new ArrayList<>(3);

        argList.add(one);
        argList.add(FuncWord.COMMA);
        argList.add(two);
        argList.add(FuncWord.COMMA);

        argList.add(three);
        return argList;
    }

    static ArmyExpression oneArgVoidFunc(final String name, final Expression arg) {
        if (!Functions.FUN_NAME_PATTER.matcher(name).matches()) {
            throw Functions._customFuncNameError(name);
        }
        return new MultiArgVoidFunction(name, Collections.singletonList(arg));
    }

    static ArmyExpression multiArgVoidFunc(final String name, final List<? extends Expression> argList) {
        if (!Functions.FUN_NAME_PATTER.matcher(name).matches()) {
            throw Functions._customFuncNameError(name);
        } else if (argList.size() == 0) {
            throw CriteriaUtils.funcArgListIsEmpty(name);
        }
        return new MultiArgVoidFunction(name, _CollectionUtils.unmodifiableList(argList));
    }


    static void appendMultiArgFunc(final String name, final List<? extends Expression> argList
            , final _SqlContext context) {
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(_Constant.SPACE)
                .append(name)
                .append(_Constant.LEFT_PAREN);
        final int size = argList.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.COMMA);
            }
            ((ArmyExpression) argList.get(i)).appendSql(context);
        }

        sqlBuilder.append(_Constant.RIGHT_PAREN);
    }


    interface FunctionSpec extends _SelfDescribed, TypeInfer {

    }

    interface NoArgFunction {

    }


    enum NullTreatment implements SQLWords {

        RESPECT_NULLS(" RESPECT NULLS"),
        IGNORE_NULLS(" IGNORE NULLS");

        final String spaceWords;

        NullTreatment(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return CriteriaUtils.sqlWordsToString(this);
        }


    }//NullTreatment

    enum FromFirstLast implements SQLWords {

        FROM_FIRST(" FROM FIRST"),
        FROM_LAST(" FROM LAST");

        final String spaceWords;

        FromFirstLast(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return CriteriaUtils.sqlWordsToString(this);
        }

    }//FromFirstLast

    interface AggregateFunction {

    }


    static abstract class WindowFunction<OR, OE extends Expression, I extends Item> extends Expressions<I>
            implements Window._OverClause<OR, OE>, OperationExpression.MutableParamMetaSpec, CriteriaContextSpec {

        final CriteriaContext context;

        final String name;

        private final Function<_ItemExpression<I>, OE> endFunction;

        private TypeMeta returnType;

        private String existingWindowName;

        private _Window anonymousWindow;

        WindowFunction(String name, TypeMeta returnType, Function<_ItemExpression<I>, OE> endFunction
                , Function<Selection, I> aliasFunction) {
            super(aliasFunction);
            this.context = ContextStack.peek();
            this.name = name;
            this.endFunction = endFunction;
            this.returnType = returnType;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.returnType;
        }

        @Override
        public final void updateParamMeta(final TypeMeta typeMeta) {
            this.returnType = typeMeta;
        }

        @Override
        public final OE over(final String windowName) {
            if (this.existingWindowName != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.context.onRefWindow(windowName);
            this.existingWindowName = windowName;
            return this.endFunction.apply(this);
        }


        @Override
        public final void appendSql(final _SqlContext context) {
            //1. function
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name) // function name
                    .append(_Constant.LEFT_PAREN);

            if (!(this instanceof NoArgFunction)) {
                this.appendArguments(context);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            final String existingWindowName = this.existingWindowName;
            final _Window anonymousWindow = this.anonymousWindow;

            if (existingWindowName == null && anonymousWindow == null) {
                if (!(this instanceof AggregateFunction)) {
                    throw ContextStack.castCriteriaApi(this.context);
                }
            } else if (existingWindowName != null && anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else {
                //2. OVER clause
                sqlBuilder.append(_Constant.SPACE_OVER);
                if (anonymousWindow == null || anonymousWindow == GlobalWindow.INSTANCE) {
                    sqlBuilder.append(_Constant.LEFT_PAREN);
                    if (existingWindowName != null) {
                        sqlBuilder.append(_Constant.SPACE);
                        context.parser().identifier(existingWindowName, sqlBuilder);
                    }
                    sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
                } else {
                    anonymousWindow.appendSql(context);
                }
            }

        }

        @Override
        public final String toString() {
            //1. function
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name) // function name
                    .append(_Constant.LEFT_PAREN);

            if (!(this instanceof NoArgFunction)) {
                this.argumentToString(sqlBuilder);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            final String existingWindowName = this.existingWindowName;
            final _Window anonymousWindow = this.anonymousWindow;

            if (existingWindowName == null && anonymousWindow == null) {
                if (!(this instanceof AggregateFunction)) {
                    throw ContextStack.castCriteriaApi(this.context);
                }
            } else if (existingWindowName != null && anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else {
                //2. OVER clause
                sqlBuilder.append(_Constant.SPACE_OVER);
                if (anonymousWindow == null || anonymousWindow == GlobalWindow.INSTANCE) {
                    sqlBuilder.append(_Constant.LEFT_PAREN);
                    if (existingWindowName != null) {
                        sqlBuilder.append(_Constant.SPACE)
                                .append(existingWindowName);
                    }
                    sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
                } else {
                    sqlBuilder.append(anonymousWindow);
                }
            }
            return sqlBuilder.toString();
        }

        abstract void appendArguments(_SqlContext context);

        abstract void argumentToString(StringBuilder builder);

        final OE windowEnd(final _Window anonymousWindow) {
            if (this.anonymousWindow == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.anonymousWindow = anonymousWindow;
            return this.endFunction.apply(this);
        }

    }//AggregateOverClause

    private static class NoArgFuncExpression extends OperationExpression implements FunctionSpec {

        private final String name;

        private final TypeMeta returnType;

        private NoArgFuncExpression(String name, TypeMeta returnType) {
            this.name = name;
            this.returnType = returnType;
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.returnType;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN)
                    .append(_Constant.RIGHT_PAREN);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(this.name, this.returnType);
        }

        @Override
        public final boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NoArgFuncExpression) {
                final NoArgFuncExpression o = (NoArgFuncExpression) obj;
                match = o.name.equals(this.name) && o.returnType == this.returnType;
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN)
                    .append(_Constant.RIGHT_PAREN)
                    .toString();
        }


    }//NoArgFuncExpression

    private static final class NoArgFuncPredicate extends OperationPredicate implements FunctionSpec {

        private final String name;

        private NoArgFuncPredicate(String name) {
            this.name = name;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN)
                    .append(_Constant.SPACE_RIGHT_PAREN);
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN)
                    .append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//NoArgFuncPredicate


    private static final class OneArgFuncPredicate extends OperationPredicate implements FunctionSpec {

        private final String name;

        private final ArmyExpression argument;

        private OneArgFuncPredicate(String name, ArmyExpression argument) {
            this.name = name;
            this.argument = argument;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);

            this.argument.appendSql(context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }


        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN)
                    .append(this.argument)
                    .append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//OneArgFuncPredicate


    private static final class MultiArgFunction extends OperationExpression implements FunctionSpec {

        private final String name;

        private final List<? extends Expression> argList;

        private final TypeMeta returnType;

        private MultiArgFunction(String name, List<? extends Expression> argList, TypeMeta returnType) {
            this.name = name;
            this.argList = argList;
            this.returnType = returnType;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.returnType;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            FunctionUtils.appendMultiArgFunc(this.name, this.argList, context);
        }


    }//MultiArgFunction

    private static final class MultiArgVoidFunction extends NonOperationExpression implements FunctionSpec {

        private final String name;

        private final List<? extends Expression> argList;

        private MultiArgVoidFunction(String name, List<? extends Expression> argList) {
            this.name = name;
            this.argList = argList;
        }

        @Override
        public TypeMeta typeMeta() {
            return VoidType.INSTANCE;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            FunctionUtils.appendMultiArgFunc(this.name, this.argList, context);
        }


    }//MultiArgVoidFunction


    private static final class ComplexFuncPredicate extends OperationPredicate implements FunctionSpec {

        private final String name;

        private final List<?> argumentList;

        private ComplexFuncPredicate(String name, List<?> argumentList) {
            this.name = name;
            this.argumentList = argumentList;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(name)
                    .append(_Constant.LEFT_PAREN);

            FunctionUtils.appendComplexArg(this.argumentList, context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


        @Override
        public String toString() {
            final StringBuilder builder;
            builder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(name)
                    .append(_Constant.LEFT_PAREN);

            FunctionUtils.complexArgToString(this.argumentList, builder);
            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//ComplexFuncPredicate


    static abstract class FunctionExpression extends OperationExpression
            implements FunctionSpec, OperationExpression.MutableParamMetaSpec {

        final String name;

        private TypeMeta returnType;

        FunctionExpression(String name, TypeMeta returnType) {
            this.name = name;
            this.returnType = returnType;
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.returnType;
        }

        @Override
        public void updateParamMeta(final TypeMeta typeMeta) {
            this.returnType = typeMeta;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name) // function name
                    .append(_Constant.LEFT_PAREN);

            this.appendArguments(context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        @Override
        public final String toString() {
            final StringBuilder builder = new StringBuilder();

            builder.append(_Constant.SPACE)
                    .append(this.name) // function name
                    .append(_Constant.LEFT_PAREN);

            this.argumentsToString(builder);

            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }

        abstract void appendArguments(final _SqlContext context);

        abstract void argumentsToString(StringBuilder builder);

    }//FunctionExpression


    private static final class OneArgFunc extends FunctionExpression {

        private final ArmyExpression argument;

        private OneArgFunc(String name, ArmyExpression argument, TypeMeta returnType) {
            super(name, returnType);
            this.argument = argument;
        }

        @Override
        void appendArguments(final _SqlContext context) {
            this.argument.appendSql(context);
        }

        @Override
        void argumentsToString(final StringBuilder builder) {
            builder.append(this.argument);
        }


    }//OneArgFunc

    private static final class JsonObjectFunc extends FunctionExpression {

        private final Map<String, Expression> expMap;

        private JsonObjectFunc(String name, Map<String, Expression> expMap, TypeMeta returnType) {
            super(name, returnType);
            this.expMap = new HashMap<>(expMap);
        }

        @Override
        void appendArguments(final _SqlContext context) {
            final StringBuilder sqlBuilder = context.sqlBuilder();
            int index = 0;
            for (Map.Entry<String, Expression> e : this.expMap.entrySet()) {
                if (index > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                context.appendLiteral(StringType.INSTANCE, e.getKey());
                sqlBuilder.append(_Constant.SPACE_COMMA);
                ((ArmyExpression) e.getValue()).appendSql(context);
                index++;
            }

        }

        @Override
        void argumentsToString(final StringBuilder builder) {
            int index = 0;
            for (Map.Entry<String, Expression> e : this.expMap.entrySet()) {
                if (index > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(e.getKey())
                        .append(_Constant.SPACE_COMMA)
                        .append(e.getValue());
                index++;
            }

        }

    }//JsonMapFunc


    @Deprecated
    private static final class OneArgOptionFunc extends FunctionUtils.FunctionExpression {

        private final SQLWords option;

        private final ArmyExpression argument;

        private final Clause clause;

        private OneArgOptionFunc(String name, @Nullable SQLWords option
                , ArmyExpression argument, @Nullable Clause clause
                , TypeMeta returnType) {
            super(name, returnType);
            this.option = option;
            this.argument = argument;
            this.clause = clause;
        }

        @Override
        void appendArguments(final _SqlContext context) {
            final SQLWords option = this.option;
            if (option != null) {
                context.sqlBuilder()
                        .append(_Constant.SPACE)
                        .append(option.render());
            }
            this.argument.appendSql(context);

            final Clause clause = this.clause;
            if (clause != null) {
                ((_SelfDescribed) clause).appendSql(context);
            }

        }

        @Override
        void argumentsToString(final StringBuilder builder) {
            final SQLWords option = this.option;
            if (option != null) {
                builder.append(_Constant.SPACE)
                        .append(option.render());
            }
            builder.append(this.argument);

            final Clause clause = this.clause;
            if (clause != null) {
                builder.append(clause);
            }

        }


    }//OneArgOptionFunc

    private static final class SQLIdentifier {

        private final String identifier;

        private SQLIdentifier(String identifier) {
            this.identifier = identifier;
        }

    }//SQLIdentifier

    private static class ComplexArgFunc extends OperationExpression
            implements FunctionSpec, OperationExpression.MutableParamMetaSpec {

        private final String name;
        private final List<?> argList;

        private TypeMeta returnType;

        private ComplexArgFunc(String name, List<?> argList, TypeMeta returnType) {
            assert argList.size() > 0;
            this.name = name;
            this.argList = argList;
            this.returnType = returnType;
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.returnType;
        }

        @Override
        public final void updateParamMeta(final TypeMeta typeMeta) {
            this.returnType = typeMeta;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(name)
                    .append(_Constant.LEFT_PAREN);

            FunctionUtils.appendComplexArg(this.argList, context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }


        @Override
        public final String toString() {
            final StringBuilder builder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);

            for (Object o : this.argList) {
                if (o instanceof Expression) {
                    builder.append(o);
                } else if (o instanceof SQLWords) {
                    builder.append(_Constant.SPACE)
                            .append(((SQLWords) o).render());
                } else if (o instanceof Clause) {
                    builder.append(o);
                } else {
                    //no bug,never here
                    throw new IllegalStateException();
                }
            }//for
            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//ComplexArgFunc

    private static final class NamedComplexArgFunc extends ComplexArgFunc implements NamedExpression {

        private final String expAlias;

        private NamedComplexArgFunc(String name, List<?> argList, TypeMeta returnType, String expAlias) {
            super(name, argList, returnType);
            this.expAlias = expAlias;
        }

        @Override
        public String alias() {
            return this.expAlias;
        }


    }//NamedComplexArgFunc


    private static final class MultiArgOptionFunc extends FunctionUtils.FunctionExpression {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        private final Clause clause;

        private MultiArgOptionFunc(String name, @Nullable SQLWords option
                , List<ArmyExpression> argList, @Nullable Clause clause
                , TypeMeta returnType) {
            super(name, returnType);
            assert argList.size() > 0;

            this.option = option;
            this.argList = _CollectionUtils.unmodifiableList(argList);
            this.clause = clause;
        }

        @Override
        void appendArguments(final _SqlContext context) {
            FunctionUtils.appendArguments(this.option, this.argList, this.clause, context);
        }

        @Override
        void argumentsToString(final StringBuilder builder) {
            FunctionUtils.argumentsToString(this.option, this.argList, this.clause, builder);
        }


    }//MultiArgOptionFunc


    private static final class CaseFunction<R extends Item, I extends Item> extends Expressions<I>
            implements StandardSqlFunction._CaseWhenSpec<R>
            , StandardSqlFunction._CaseFuncWhenClause<R>
            , StandardSqlFunction._CaseThenClause<R>
            , FunctionSpec, CriteriaContextSpec
            , CaseWhens
            , StandardSqlFunction._DynamicCaseThenClause
            , OperationExpression.MutableParamMetaSpec {

        private final Function<_ItemExpression<I>, R> endFunc;

        private final ArmyExpression caseValue;

        private final CriteriaContext context;

        private List<_Pair<ArmyExpression, ArmyExpression>> expPairList;

        private ArmyExpression whenExpression;

        private ArmyExpression elseExpression;

        private TypeMeta returnType;

        private CaseFunction(@Nullable ArmyExpression caseValue, Function<_ItemExpression<I>, R> endFunc
                , Function<Selection, I> asFunc) {
            super(asFunc);
            this.caseValue = caseValue;
            this.endFunc = endFunc;
            this.context = ContextStack.peek();
        }

        @Override
        public CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public TypeMeta typeMeta() {
            final TypeMeta returnType = this.returnType;
            if (returnType == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return returnType;
        }

        @Override
        public void updateParamMeta(final TypeMeta typeMeta) {
            this.returnType = typeMeta;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final int pairSize;
            final List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
            if (expPairList == null || (pairSize = expPairList.size()) == 0) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" CASE");

            final ArmyExpression caseValue = this.caseValue;
            if (caseValue != null) {
                caseValue.appendSql(context);
            }
            _Pair<ArmyExpression, ArmyExpression> pair;
            for (int i = 0; i < pairSize; i++) {
                pair = expPairList.get(i);

                sqlBuilder.append(" WHEN");
                pair.first.appendSql(context);
                sqlBuilder.append(" THEN");
                pair.second.appendSql(context);

            }

            final ArmyExpression elseExpression = this.elseExpression;
            if (elseExpression != null) {
                sqlBuilder.append(" ELSE");
                elseExpression.appendSql(context);
            }

            sqlBuilder.append(" END");

        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            final int pairSize;
            final List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
            if (expPairList == null || (pairSize = expPairList.size()) == 0) {
                return super.toString();
            }
            builder.append(" CASE");

            final ArmyExpression caseValue = this.caseValue;
            if (caseValue != null) {
                builder.append(caseValue);
            }
            _Pair<ArmyExpression, ArmyExpression> pair;
            for (int i = 0; i < pairSize; i++) {
                pair = expPairList.get(i);

                builder.append(" WHEN")
                        .append(pair.first)
                        .append(" THEN")
                        .append(pair.second);

            }

            final ArmyExpression elseExpression = this.elseExpression;
            if (elseExpression != null) {
                builder.append(" ELSE")
                        .append(elseExpression);
            }
            return builder.append(" END")
                    .toString();
        }

        @Override
        public CaseFunction<R, I> when(Expression expression) {
            return null;
        }

        @Override
        public CaseFunction<R, I> when(Supplier<Expression> supplier) {
            return null;
        }

        @Override
        public <T> CaseFunction<R, I> when(Function<T, Expression> valueOperator, Supplier<T> getter) {
            return null;
        }

        @Override
        public CaseFunction<R, I> when(Function<Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            return null;
        }

        @Override
        public <T> CaseFunction<R, I> when(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand) {
            return null;
        }

        @Override
        public CaseFunction<R, I> when(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
            return null;
        }

        @Override
        public <T> CaseFunction<R, I> when(BetweenValueOperator<T> expOperator
                , BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter
                , SQLs.WordAnd and, Supplier<T> secondGetter) {
            return null;
        }

        @Override
        public CaseFunction<R, I> when(BetweenValueOperator<Object> expOperator
                , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
                , String firstKey, SQLs.WordAnd and, String secondKey) {
            return null;
        }

        @Override
        public CaseFunction<R, I> when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and
                , Expression second) {
            return null;
        }

        @Override
        public StandardSqlFunction._CaseEndClause<R> whens(Consumer<CaseWhens> consumer) {
            return null;
        }

        @Override
        public CaseFunction<R, I> ifWhen(Supplier<Expression> supplier) {
            return null;
        }

        @Override
        public <T> CaseFunction<R, I> ifWhen(Function<T, Expression> valueOperator, Supplier<T> getter) {
            return null;
        }

        @Override
        public CaseFunction<R, I> ifWhen(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
            return null;
        }

        @Override
        public <T> CaseFunction<R, I> ifWhen(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand) {
            return null;
        }

        @Override
        public CaseFunction<R, I> ifWhen(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
            return null;
        }

        @Override
        public <T> CaseFunction<R, I> ifWhen(BetweenValueOperator<T> expOperator
                , BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and
                , Supplier<T> secondGetter) {
            return null;
        }

        @Override
        public CaseFunction<R, I> ifWhen(BetweenValueOperator<Object> expOperator
                , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
                , String firstKey, SQLs.WordAnd and, String secondKey) {
            return null;
        }

        @Override
        public CaseFunction<R, I> then(Expression expression) {
            return null;
        }

        @Override
        public CaseFunction<R, I> then(Supplier<Expression> supplier) {
            return null;
        }

        @Override
        public <T> CaseFunction<R, I> then(Function<T, Expression> valueOperator, Supplier<T> getter) {
            return null;
        }

        @Override
        public CaseFunction<R, I> then(Function<Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            return null;
        }

        @Override
        public <T> CaseFunction<R, I> then(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand) {
            return null;
        }

        @Override
        public CaseFunction<R, I> then(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
            return null;
        }

        @Override
        public <T> CaseFunction<R, I> then(BetweenValueOperator<T> expOperator
                , BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and
                , Supplier<T> secondGetter) {
            return null;
        }

        @Override
        public CaseFunction<R, I> then(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator
                , Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey) {
            return null;
        }

        @Override
        public CaseFunction<R, I> then(BetweenOperator expOperator, Expression first, SQLs.WordAnd and
                , Expression second) {
            return null;
        }

        @Override
        public StandardSqlFunction._CaseEndClause<R> Else(Expression expression) {
            return null;
        }

        @Override
        public StandardSqlFunction._CaseEndClause<R> Else(Supplier<Expression> supplier) {
            return null;
        }

        @Override
        public <T> StandardSqlFunction._CaseEndClause<R> Else(Function<T, Expression> valueOperator
                , Supplier<T> getter) {
            return null;
        }

        @Override
        public StandardSqlFunction._CaseEndClause<R> Else(Function<Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            return null;
        }

        @Override
        public <T> StandardSqlFunction._CaseEndClause<R> Else(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand) {
            return null;
        }

        @Override
        public StandardSqlFunction._CaseEndClause<R> Else(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
            return null;
        }

        @Override
        public <T> StandardSqlFunction._CaseEndClause<R> Else(BetweenValueOperator<T> expOperator
                , BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and
                , Supplier<T> secondGetter) {
            return null;
        }

        @Override
        public StandardSqlFunction._CaseEndClause<R> Else(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator
                , Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey) {
            return null;
        }

        @Override
        public StandardSqlFunction._CaseEndClause<R> Else(BetweenOperator expOperator, Expression first
                , SQLs.WordAnd and, Expression second) {
            return null;
        }

        @Override
        public StandardSqlFunction._CaseEndClause<R> ifElse(Supplier<Expression> supplier) {
            return null;
        }

        @Override
        public <T> StandardSqlFunction._CaseEndClause<R> ifElse(Function<T, Expression> valueOperator
                , Supplier<T> getter) {
            return null;
        }

        @Override
        public StandardSqlFunction._CaseEndClause<R> ifElse(Function<Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            return null;
        }

        @Override
        public <T> StandardSqlFunction._CaseEndClause<R> ifElse(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand) {
            return null;
        }

        @Override
        public StandardSqlFunction._CaseEndClause<R> ifElse(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            return null;
        }

        @Override
        public <T> StandardSqlFunction._CaseEndClause<R> ifElse(BetweenValueOperator<T> expOperator
                , BiFunction<Expression, T, Expression> operator
                , Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
            return null;
        }

        @Override
        public StandardSqlFunction._CaseEndClause<R> ifElse(BetweenValueOperator<Object> expOperator
                , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey
                , SQLs.WordAnd and, String secondKey) {
            return null;
        }

        @Override
        public R end() {
            if (this.returnType != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returnType = StringType.INSTANCE;
            return this.endFunc.apply(this);
        }

        @Override
        public R end(final @Nullable TypeInfer type) {
            if (this.returnType != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (type == null) {
                throw ContextStack.nullPointer(this.context);
            } else if (type instanceof TypeMeta) {
                this.returnType = (TypeMeta) type;
            } else {
                this.returnType = type.typeMeta();
            }
            return this.endFunc.apply(this);
        }


    }//CaseFunc


    private static final class GlobalWindow implements _Window {

        private static final GlobalWindow INSTANCE = new GlobalWindow();

        private GlobalWindow() {
        }

        @Override
        public void appendSql(final _SqlContext context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void prepared() {
            //no-op
        }

        @Override
        public void clear() {
            //no-op
        }

    }//GlobalWindow


    private static abstract class ConditionFunc<LR> extends OperationExpression
            implements Functions._FuncConditionClause<LR>
            , FunctionSpec
            , Functions._FuncLastArgClause
            , Statement._RightParenClause<Expression>
            , OperationExpression.MutableParamMetaSpec {

        final CriteriaContext context;
        private final String name;

        private final Function<List<ArmyExpression>, TypeMeta> function;
        private TypeMeta returnType;

        private List<ArmyExpression> argList;

        private ConditionFunc(String name, @Nullable TypeMeta returnType) {
            this.context = ContextStack.peek();
            this.name = name;
            this.returnType = returnType;
            this.function = this::inferReturnType;
        }

        private ConditionFunc(String name, Function<List<ArmyExpression>, TypeMeta> function) {
            this.context = ContextStack.peek();
            this.name = name;
            this.returnType = null;
            this.function = function;
        }

        @Override
        public final TypeMeta typeMeta() {
            TypeMeta returnType = this.returnType;
            if (returnType == null) {
                final List<ArmyExpression> argList = this.argList;
                if (argList == null || argList instanceof ArrayList) {
                    throw ContextStack.castCriteriaApi(this.context);
                }
                this.returnType = returnType = this.function.apply(argList);
            }
            return returnType;
        }

        @Override
        public final void updateParamMeta(final TypeMeta typeMeta) {
            this.returnType = typeMeta;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final List<ArmyExpression> argList = this.argList;
            if (!(argList instanceof ArrayList) || argList.size() < 2) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);

            FunctionUtils.appendArguments(null, argList, null, context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }

        @Override
        public final LR leftParen(final @Nullable IPredicate condition) {
            if (condition == null) {
                throw ContextStack.nullPointer(this.context);
            }
            if (this.argList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            final List<ArmyExpression> argList = new ArrayList<>();
            argList.add((OperationPredicate) condition);
            this.argList = argList;
            return this.leftParenEnd();
        }

        @Override
        public final LR leftParen(Supplier<? extends IPredicate> supplier) {
            return this.leftParen(supplier.get());
        }

        @Override
        public final LR leftParen(Function<Object, ? extends IPredicate> operator, Supplier<?> supplier) {
            return this.leftParen(operator.apply(supplier.get()));
        }

        @Override
        public final LR leftParen(Function<Object, ? extends IPredicate> operator, Function<String, ?> function
                , String keyName) {
            return this.leftParen(operator.apply(function.apply(keyName)));
        }

        @Override
        public final LR leftParen(BiFunction<Object, Object, ? extends IPredicate> operator, Supplier<?> firstOperand
                , Supplier<?> secondOperand) {
            return this.leftParen(operator.apply(firstOperand.get(), secondOperand.get()));
        }

        @Override
        public final LR leftParen(BiFunction<Object, Object, ? extends IPredicate> operator, Function<String, ?> function
                , String firstKey, String secondKey) {
            return this.leftParen(operator.apply(function.apply(firstKey), function.apply(secondKey)));
        }

        @Override
        public final Statement._RightParenClause<Expression> comma(final @Nullable Expression expression) {
            if (expression == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final List<ArmyExpression> argList = this.argList;
            if (!(argList instanceof ArrayList) || argList.size() == 0) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            argList.add((ArmyExpression) expression);
            return this;
        }

        @Override
        public final Statement._RightParenClause<Expression> comma(Supplier<? extends Expression> supplier) {
            return this.comma(supplier.get());
        }

        @Override
        public final Statement._RightParenClause<Expression> comma(Function<Object, ? extends Expression> operator
                , Supplier<?> supplier) {
            return this.comma(operator.apply(supplier.get()));
        }

        @Override
        public final Statement._RightParenClause<Expression> comma(Function<Object, ? extends Expression> operator
                , Function<String, ?> function, String keyName) {
            return this.comma(operator.apply(function.apply(keyName)));
        }

        @Override
        public final Statement._RightParenClause<Expression> comma(BiFunction<Object, Object, ? extends Expression> operator
                , Supplier<?> firstOperand, Supplier<?> secondOperand) {
            return this.comma(operator.apply(firstOperand.get(), secondOperand.get()));
        }

        @Override
        public final Statement._RightParenClause<Expression> comma(BiFunction<Object, Object, ? extends Expression> operator
                , Function<String, ?> function, String firstKey, String secondKey) {
            return this.comma(operator.apply(function.apply(firstKey), function.apply(secondKey)));
        }

        @Override
        public final Expression rightParen() {
            final List<ArmyExpression> argList = this.argList;
            if (!(argList instanceof ArrayList) || argList.size() < 2) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.argList = Collections.unmodifiableList(argList);
            return this;
        }

        abstract LR leftParenEnd();

        final Functions._FuncLastArgClause argBeforeLastEnd(final ArmyExpression arg) {
            final List<ArmyExpression> argList = this.argList;
            if (!(argList instanceof ArrayList) || argList.size() == 0) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            argList.add(arg);
            return this;
        }


        /**
         * @param argList a unmodified list
         */
        TypeMeta inferReturnType(List<ArmyExpression> argList) {
            throw new UnsupportedOperationException();
        }


    }//ConditionFunc


    private static class FuncCommaClause<CR> implements Functions._FuncCommaClause<CR> {

        final CriteriaContext context;

        private Function<ArmyExpression, CR> function;

        private FuncCommaClause(CriteriaContext context, Function<ArmyExpression, CR> function) {
            this.context = context;
            this.function = function;
        }

        @Override
        public final CR comma(final @Nullable Expression expression) {
            if (expression == null) {
                throw ContextStack.nullPointer(this.context);
            }
            return this.function.apply((ArmyExpression) expression);
        }

        @Override
        public final CR comma(Supplier<? extends Expression> supplier) {
            return this.comma(supplier.get());
        }

        @Override
        public final CR comma(Function<Object, ? extends Expression> operator, Supplier<?> supplier) {
            return this.comma(operator.apply(supplier.get()));
        }

        @Override
        public final CR comma(Function<Object, ? extends Expression> operator, Function<String, ?> function
                , String keyName) {
            return this.comma(operator.apply(function.apply(keyName)));
        }

        @Override
        public final CR comma(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand
                , Supplier<?> secondOperand) {
            return this.comma(operator.apply(firstOperand.get(), secondOperand.get()));
        }

        @Override
        public final CR comma(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function
                , String firstKey, String secondKey) {
            return this.comma(operator.apply(function.apply(firstKey), function.apply(secondKey)));
        }


    }//FuncCommaClause


    private static final class FuncSecondArgcClause extends FuncCommaClause<Functions._FuncLastArgClause>
            implements Functions._FuncSecondArgClause {

        private FuncSecondArgcClause(CriteriaContext context
                , Function<ArmyExpression, Functions._FuncLastArgClause> function) {
            super(context, function);
        }

    }//FuncSecondArgcClause

    private static final class ConditionTwoFunc extends ConditionFunc<Functions._FuncSecondArgClause>
            implements Functions._FuncConditionTowClause {

        private final BiFunction<MappingType, MappingType, MappingType> function;

        private ConditionTwoFunc(String name, BiFunction<MappingType, MappingType, MappingType> function) {
            super(name, (TypeMeta) null);
            this.function = function;
        }

        @Override
        TypeMeta inferReturnType(final List<ArmyExpression> argList) {
            if (argList.size() != 3) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return Functions._returnType(argList.get(1), argList.get(2), this.function);
        }

        @Override
        Functions._FuncSecondArgClause leftParenEnd() {
            return new FuncSecondArgcClause(this.context, this::argBeforeLastEnd);
        }

    }//ThreeConditionFunc


}
