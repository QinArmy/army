package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.impl.inner._Window;
import io.army.criteria.standard.SQLFunction;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.function.BetweenOperator;
import io.army.function.BetweenValueOperator;
import io.army.function.ExpressionOperator;
import io.army.lang.Nullable;
import io.army.mapping.StringType;
import io.army.mapping.VoidType;
import io.army.meta.TypeMeta;
import io.army.util._CollectionUtils;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.function.*;

abstract class FunctionUtils {

    FunctionUtils() {
        throw new UnsupportedOperationException();
    }


    static SQLFunction._CaseFuncWhenClause caseFunction(final @Nullable Expression caseValue) {
        return new CaseFunction((ArmyExpression) caseValue);
    }


    static Expression oneArgFunc(String name, Expression expr, TypeMeta returnType) {
        if (expr instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, expr);
        }
        return new OneArgFuncExpression(name, (ArmyExpression) expr, returnType);
    }


    static Expression twoArgFunc(final String name, final Expression one
            , final Expression two, TypeMeta returnType) {
        final List<ArmyExpression> argList;
        argList = twoExpList(name, one, two);
        return new MultiArgFunctionExpression(name, null, argList, returnType);
    }

    static Expression threeArgFunc(final String name, final Expression one
            , final Expression two, final Expression three, TypeMeta returnType) {
        final List<ArmyExpression> argList;
        argList = threeExpList(name, one, two, three);
        return new MultiArgFunctionExpression(name, null, argList, returnType);
    }


    static IPredicate twoArgPredicateFunc(final String name, final Expression one, final Expression two) {
        return new MultiArgFuncPredicate(name, null, twoExpList(name, one, two));
    }

    static IPredicate threeArgPredicateFunc(final String name, final Expression one
            , final Expression two, final Expression three) {
        return new MultiArgFuncPredicate(name, null, threeExpList(name, one, two, three));
    }


    static Expression noArgFunc(String name, TypeMeta returnType) {
        return new NoArgFuncExpression(name, returnType);
    }

    static Expression oneOrMultiArgFunc(String name, Expression exp, TypeMeta returnType) {
        return new OneArgFuncExpression(name, (ArmyExpression) exp, returnType);
    }

    static Expression twoOrMultiArgFunc(final String name, final Expression one
            , final Expression two, TypeMeta returnType) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        final List<ArmyExpression> argList;
        argList = Arrays.asList((ArmyExpression) one, (ArmyExpression) two);
        return new MultiArgFunctionExpression(name, null, argList, returnType);
    }

    static Expression oneAndMultiArgFunc(final String name, final Expression exp, final List<Expression> expList
            , final TypeMeta returnType) {
        if (exp instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, exp);
        }
        final int size = expList.size();
        if (size == 0) {
            throw CriteriaUtils.funcArgListIsEmpty(name);
        }
        final List<ArmyExpression> argList = new ArrayList<>(1 + size);
        argList.add((ArmyExpression) exp);
        for (Expression e : expList) {
            argList.add((ArmyExpression) e);
        }
        return new MultiArgFunctionExpression(name, null, argList, returnType);
    }

    static Expression twoAndMultiArgFunc(final String name, final Expression exp1, Expression exp2
            , final List<Expression> expList, final TypeMeta returnType) {
        return new MultiArgFunctionExpression(name, null, twoAndMultiExpList(name, exp1, exp2, expList), returnType);
    }

    static Expression multiArgFunc(String name, List<Expression> argList, TypeMeta returnType) {
        return new MultiArgFunctionExpression(name, null, expList(name, argList), returnType);
    }

    static Expression oneAndRestFunc(String name, TypeMeta returnType, Expression first, Expression... rest) {
        if (first instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, first);
        }
        final Expression func;
        if (rest.length == 0) {
            func = new OneArgFuncExpression(name, (ArmyExpression) first, returnType);
        } else {
            final List<ArmyExpression> argList = new ArrayList<>(1 + rest.length);
            argList.add((ArmyExpression) first);
            addRestExp(argList, rest);
            func = new MultiArgFunctionExpression(name, null, argList, returnType);
        }
        return func;
    }


    static Expression twoAndMaxRestForSingleExpFunc(String name, TypeMeta returnType
            , Expression one, Expression two, final int maxRest, Expression... rest) {
        assert maxRest > 0;
        if (rest.length > maxRest) {
            String m = String.format("function[%s] at most take %s argument", name, maxRest);
            throw ContextStack.criteriaError(ContextStack.peek(), m);
        }
        final List<ArmyExpression> argList;
        argList = new ArrayList<>(2 + rest.length);
        appendTwoSingleExp(argList, name, one, two);
        for (Expression arg : rest) {
            if (arg instanceof SqlValueParam.MultiValue) {
                throw CriteriaUtils.funcArgError(name, arg);
            }
            argList.add((ArmyExpression) arg);
        }
        return new MultiArgFunctionExpression(name, null, argList, returnType);
    }


    static Expression multiArgFunc(String name, TypeMeta returnType, Expression firstArg, Expression... exps) {
        final List<ArmyExpression> argList = new ArrayList<>(1 + exps.length);
        argList.add((ArmyExpression) firstArg);
        for (Expression exp : exps) {
            if (exp instanceof SqlValueParam) {
                throw CriteriaUtils.funcArgError(name, exp);
            }
            argList.add((ArmyExpression) exp);
        }
        return new MultiArgFunctionExpression(name, null, argList, returnType);
    }

    static Expression safeMultiArgFunc(String name, List<ArmyExpression> argList, TypeMeta returnType) {
        return new MultiArgFunctionExpression(name, null, argList, returnType);
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

    static IPredicate multiArgFuncPredicate(String name, List<Expression> expList) {
        final int size = expList.size();
        final IPredicate function;
        switch (size) {
            case 0:
                throw CriteriaUtils.funcArgError(name, expList);
            case 1:
                function = new OneArgFuncPredicate(name, (ArmyExpression) expList.get(0));
                break;
            default: {
                final List<ArmyExpression> argList = new ArrayList<>(size);
                appendExpList(argList, expList);
                function = new MultiArgFuncPredicate(name, null, argList);
            }
        }
        return function;
    }


    static IPredicate twoAndMultiArgFuncPredicate(final String name, final Expression exp1, Expression exp2
            , final List<Expression> expList) {
        return new MultiArgFuncPredicate(name, null, twoAndMultiExpList(name, exp1, exp2, expList));
    }


    static IPredicate complexArgPredicate(final String name, List<?> argList) {
        return new ComplexArgFuncPredicate(name, argList);
    }

    static IPredicate oneAndRestFuncPredicate(String name, Expression first, Expression... rest) {
        if (first instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, first);
        }
        final IPredicate func;
        if (rest.length == 0) {
            func = new OneArgFuncPredicate(name, (ArmyExpression) first);
        } else {
            final List<ArmyExpression> argList = new ArrayList<>(1 + rest.length);
            argList.add((ArmyExpression) first);
            addRestExp(argList, rest);
            func = new MultiArgFuncPredicate(name, null, argList);
        }
        return func;
    }

    static IPredicate complexArgPredicateFrom(final String name, Object firstArg, @Nullable Object... args) {
        final List<Object> argList;
        if (args == null) {
            argList = Collections.singletonList(firstArg);
        } else {
            argList = new ArrayList<>(args.length + 1);
            argList.add(firstArg);
            for (Object arg : args) {
                if (arg != null) {
                    argList.add(arg);
                }
            }
        }
        return new ComplexArgFuncPredicate(name, argList);
    }


    static Expression complexArgFunc(String name, List<?> argList, TypeMeta returnType) {
        return new ComplexArgFuncExpression(name, argList, returnType);
    }

    static Expression complexArgFunc(String name, TypeMeta returnType, Object... args) {
        final List<Object> argList = new ArrayList<>(args.length);
        argList.addAll(Arrays.asList(args));
        return new ComplexArgFuncExpression(name, argList, returnType);
    }

    static NamedExpression namedComplexArgFunc(String name, List<?> argList, TypeMeta returnType, String expAlias) {
        return new NamedComplexArgFunc(name, argList, returnType, expAlias);
    }

    static Expression jsonObjectFunc(String name, Map<String, Expression> expMap, TypeMeta returnType) {
        return new JsonObjectFunc(name, expMap, returnType);
    }


    static void appendArguments(final @Nullable SQLWords option, final List<ArmyExpression> argList
            , final _SqlContext context) {

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();

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


    }

    static void argumentsToString(final @Nullable SQLWords option, final List<ArmyExpression> argList
            , final StringBuilder builder) {

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


    }


    static void appendComplexArg(final List<?> argumentList, final _SqlContext context) {
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        DialectParser parser = null;
        for (Object o : argumentList) {
            if (o instanceof Expression) {
                ((ArmyExpression) o).appendSql(context); // convert to ArmyExpression to avoid non-army expression
            } else if (o == SQLSyntax.FuncWord.LEFT_PAREN) {
                sqlBuilder.append(_Constant.LEFT_PAREN);
            } else if (o instanceof SQLWords) {
                sqlBuilder.append(((SQLWords) o).render());
            } else if (o instanceof SQLIdentifier) { // sql identifier
                sqlBuilder.append(_Constant.SPACE);
                if (parser == null) {
                    parser = context.parser();
                }
                parser.identifier(((SQLIdentifier) o).render(), sqlBuilder);
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
            } else if (o == SQLSyntax.FuncWord.LEFT_PAREN) {
                builder.append(((SQLWords) o).render());
            } else if (o instanceof SQLWords) {
                builder.append(_Constant.SPACE)
                        .append(((SQLWords) o).render());
            } else if (o instanceof SQLIdentifier) { // sql identifier
                builder.append(((SQLIdentifier) o).render());
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }

        }//for
    }


    static void addRestExp(List<ArmyExpression> expList, Expression... rest) {
        for (Expression exp : rest) {
            expList.add((ArmyExpression) exp);
        }
    }

    static List<Object> twoArgList(final String name, Expression one, Expression two) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        final List<Object> argList = new ArrayList<>(3);
        argList.add(one);
        argList.add(SQLSyntax.FuncWord.COMMA);
        argList.add(two);
        return argList;
    }

    static List<ArmyExpression> twoExpList(final String name, Expression one, Expression two) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return Arrays.asList((ArmyExpression) one, (ArmyExpression) two);
    }

    static List<ArmyExpression> threeExpList(final String name, Expression one, Expression two, Expression three) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (three instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        return Arrays.asList((ArmyExpression) one, (ArmyExpression) two, (ArmyExpression) three);
    }

    static void appendExpList(final List<ArmyExpression> argList, final List<Expression> expList) {
        for (Expression exp : expList) {
            argList.add((ArmyExpression) exp);
        }
    }

    /**
     * @see #twoAndMaxRestForSingleExpFunc(String, TypeMeta, Expression, Expression, int, Expression...)
     */
    static void appendTwoSingleExp(List<ArmyExpression> argList, String name
            , Expression one, Expression two) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        argList.add((ArmyExpression) one);
        argList.add((ArmyExpression) two);
    }

    static void appendThreeSingleExp(List<ArmyExpression> argList, String name
            , Expression one, Expression two, Expression three) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (three instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        argList.add((ArmyExpression) one);
        argList.add((ArmyExpression) two);
        argList.add((ArmyExpression) three);
    }


    static List<ArmyExpression> twoAndMultiExpList(final String name, final Expression exp1, Expression exp2
            , final List<Expression> expList) {
        final int size = expList.size();
        if (size == 0) {
            throw CriteriaUtils.funcArgListIsEmpty(name);
        }
        final List<ArmyExpression> argList;
        argList = twoExpList(name, exp1, exp2);
        for (Expression e : expList) {
            argList.add((ArmyExpression) e);
        }
        return argList;
    }

    static List<ArmyExpression> expList(final String name, final List<Expression> expList) {
        final int size = expList.size();
        if (size == 0) {
            throw CriteriaUtils.funcArgListIsEmpty(name);
        }
        final List<ArmyExpression> argList = new ArrayList<>(expList.size());
        for (Expression exp : expList) {
            argList.add((ArmyExpression) exp);
        }
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
        final List<Object> argList = new ArrayList<>(5);

        argList.add(one);
        argList.add(SQLSyntax.FuncWord.COMMA);
        argList.add(two);
        argList.add(SQLSyntax.FuncWord.COMMA);

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


    static abstract class WindowFunction extends Expressions
            implements Window._OverClause
            , CriteriaContextSpec
            , SQLFunction {

        final CriteriaContext context;

        final String name;

        private String existingWindowName;

        private _Window anonymousWindow;

        WindowFunction(String name, TypeMeta returnType) {
            super(returnType);
            this.context = ContextStack.peek();
            this.name = name;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final Expression over(final String windowName) {
            if (this.existingWindowName != null || this.anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.context.onRefWindow(windowName);
            this.existingWindowName = windowName;
            return this;
        }

        @Override
        public final Expression over() {
            if (this.existingWindowName != null || this.anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.anonymousWindow = GlobalWindow.INSTANCE;
            return this;
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

            if (this instanceof SQLFunction._OuterClauseBeforeOver) {
                this.appendOuterClause(context);
            }

            final String existingWindowName = this.existingWindowName;
            final _Window anonymousWindow = this.anonymousWindow;

            if (existingWindowName == null && anonymousWindow == null) {
                if (!(this instanceof SQLFunction.AggregateFunction)) {
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

            if (this instanceof SQLFunction._OuterClauseBeforeOver) {
                this.outerClauseToString(sqlBuilder);
            }

            final String existingWindowName = this.existingWindowName;
            final _Window anonymousWindow = this.anonymousWindow;

            if (existingWindowName == null && anonymousWindow == null) {
                if (!(this instanceof SQLFunction.AggregateFunction)) {
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

        void appendOuterClause(_SqlContext context) {
            throw new UnsupportedOperationException();
        }

        void outerClauseToString(StringBuilder builder) {
            throw new UnsupportedOperationException();
        }

        final Expression endWindow(final ArmyWindow anonymousWindow) {
            if (this.existingWindowName != null || this.anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.anonymousWindow = anonymousWindow.endWindowClause();
            return this;
        }

    }//AggregateOverClause

    private static class NoArgFuncExpression extends Expressions implements FunctionSpec
            , NoArgFunction {

        private final String name;

        private NoArgFuncExpression(String name, TypeMeta returnType) {
            super(returnType);
            this.name = name;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.PARENS);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(this.name, this.expType);
        }

        @Override
        public final boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NoArgFuncExpression) {
                final NoArgFuncExpression o = (NoArgFuncExpression) obj;
                match = o.name.equals(this.name) && o.expType == this.expType;
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
                    .append(_Constant.PARENS)
                    .toString();
        }


    }//NoArgFuncExpression


    private static abstract class FunctionExpression extends Expressions
            implements SQLFunction {

        final String name;

        FunctionExpression(String name, TypeMeta returnType) {
            super(returnType);
            this.name = name;
        }


        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name) // function name
                    .append(_Constant.LEFT_PAREN);

            if (this instanceof OneArgFuncExpression) {
                ((OneArgFuncExpression) this).argument.appendSql(context);
            } else if (this instanceof MultiArgFunctionExpression) {
                final MultiArgFunctionExpression e = (MultiArgFunctionExpression) this;
                FunctionUtils.appendArguments(e.option, e.argList, context);
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        @Override
        public final String toString() {
            final StringBuilder builder = new StringBuilder();

            builder.append(_Constant.SPACE)
                    .append(this.name) // function name
                    .append(_Constant.LEFT_PAREN);
            if (this instanceof OneArgFuncExpression) {
                builder.append(((OneArgFuncExpression) this).argument);
            } else if (this instanceof MultiArgFunctionExpression) {
                final MultiArgFunctionExpression e = (MultiArgFunctionExpression) this;
                FunctionUtils.argumentsToString(e.option, e.argList, builder);
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }
            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//FunctionExpression

    private static class OneArgFuncExpression extends FunctionExpression {

        private final ArmyExpression argument;

        private OneArgFuncExpression(String name, ArmyExpression argument, TypeMeta returnType) {
            super(name, returnType);
            this.argument = argument;
        }


    }//OneArgFuncExpression


    private static final class MultiArgFunctionExpression extends FunctionExpression {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        private MultiArgFunctionExpression(String name, @Nullable final SQLWords option
                , List<ArmyExpression> argList, TypeMeta returnType) {
            super(name, returnType);
            assert argList.size() > 0;
            this.option = option;
            this.argList = argList;
        }


    }//MultiArgFunctionExpression

    /**
     * <p>
     * This class is base class of below:
     *     <ul>
     *         <li>{@link NoArgFuncPredicate}</li>
     *         <li>{@link OneArgFuncPredicate}</li>
     *         <li>{@link MultiArgFuncPredicate}</li>
     *     </ul>
     * </p>
     */
    private static abstract class FunctionPredicate extends OperationPredicate {

        final String name;

        FunctionPredicate(String name) {
            this.name = name;
        }


        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);

            if (this instanceof OneArgFuncPredicate) {
                ((OneArgFuncPredicate) this).argument.appendSql(context);
            } else if (this instanceof MultiArgFuncPredicate) {
                final MultiArgFuncPredicate p = (MultiArgFuncPredicate) this;
                FunctionUtils.appendArguments(p.option, p.argList, context);
            } else if (!(this instanceof NoArgFuncPredicate)) {
                //no bug,never here
                throw new IllegalStateException();
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        @Override
        public final String toString() {
            final StringBuilder builder;
            builder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);
            if (this instanceof OneArgFuncPredicate) {
                builder.append(((OneArgFuncPredicate) this).argument);
            } else if (this instanceof MultiArgFuncPredicate) {
                final MultiArgFuncPredicate p = (MultiArgFuncPredicate) this;
                FunctionUtils.argumentsToString(p.option, p.argList, builder);
            } else if (!(this instanceof NoArgFuncPredicate)) {
                //no bug,never here
                throw new IllegalStateException();
            }
            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }

    }//FunctionPredicate


    private static final class NoArgFuncPredicate extends FunctionPredicate implements NoArgFunction {

        private NoArgFuncPredicate(String name) {
            super(name);
        }


        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NoArgFuncPredicate) {
                match = ((NoArgFuncPredicate) obj).name.equals(this.name);
            } else {
                match = false;
            }
            return match;
        }


    }//NoArgFuncPredicate


    private static final class OneArgFuncPredicate extends FunctionPredicate {


        private final ArmyExpression argument;

        private OneArgFuncPredicate(String name, ArmyExpression argument) {
            super(name);
            this.argument = argument;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.argument);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof OneArgFuncPredicate) {
                final OneArgFuncPredicate o = (OneArgFuncPredicate) obj;
                match = o.name.equals(this.name)
                        && o.argument.equals(this.argument);
            } else {
                match = false;
            }
            return match;
        }


    }//OneArgFuncPredicate

    /**
     * @see #threeArgPredicateFunc(String, Expression, Expression, Expression)
     */
    private static final class MultiArgFuncPredicate extends FunctionPredicate {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        MultiArgFuncPredicate(String name, @Nullable SQLWords option, List<ArmyExpression> argList) {
            super(name);
            assert argList.size() > 0;
            this.option = option;
            this.argList = argList;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.option, this.argList);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof MultiArgFuncPredicate) {
                final MultiArgFuncPredicate o = (MultiArgFuncPredicate) obj;
                match = o.name.equals(this.name)
                        && Objects.equals(o.option, this.option)
                        && o.argList.equals(this.argList);
            } else {
                match = false;
            }
            return match;
        }


    }//MultiArgFuncPredicate


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


    /**
     * @see ComplexArgFuncExpression
     */
    private static final class ComplexArgFuncPredicate extends OperationPredicate implements SQLFunction {

        private final String name;

        private final List<?> argumentList;

        /**
         * @see #complexArgPredicate(String, List)
         */
        private ComplexArgFuncPredicate(String name, List<?> argumentList) {
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
        public int hashCode() {
            return Objects.hash(this.name, this.argumentList);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof ComplexArgFuncPredicate) {
                final ComplexArgFuncPredicate o = (ComplexArgFuncPredicate) obj;
                match = o.name.equals(this.name) && o.argumentList.equals(this.argumentList);
            } else {
                match = false;
            }
            return match;
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


    }//ComplexArgFuncPredicate


    /**
     * @see ComplexArgFuncPredicate
     */
    private static class ComplexArgFuncExpression extends Expressions
            implements SQLFunction {

        private final String name;
        private final List<?> argList;

        /**
         * @see #complexArgFunc(String, TypeMeta, Object...)
         */
        private ComplexArgFuncExpression(String name, List<?> argList, TypeMeta returnType) {
            super(returnType);
            assert argList.size() > 0;
            this.name = name;
            this.argList = argList;
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

            FunctionUtils.complexArgToString(this.argList, builder);
            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//ComplexArgFuncExpression

    private static final class JsonObjectFunc extends Expressions
            implements SQLFunction {

        private final String name;

        private final Map<String, Expression> expMap;

        private JsonObjectFunc(String name, Map<String, Expression> expMap, TypeMeta returnType) {
            super(returnType);
            assert expMap.size() > 0;
            this.name = name;
            this.expMap = new HashMap<>(expMap);
        }


        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);

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

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }

        @Override
        public String toString() {
            final StringBuilder sqlBuilder;
            sqlBuilder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);

            int index = 0;
            for (Map.Entry<String, Expression> e : this.expMap.entrySet()) {
                if (index > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                sqlBuilder.append(_Constant.SPACE)
                        .append(e.getKey())
                        .append(_Constant.SPACE_COMMA)
                        .append(e.getValue());
                index++;
            }

            return sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();

        }

    }//JsonMapFunc


    private static final class NamedComplexArgFunc extends ComplexArgFuncExpression implements NamedExpression {

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


    private static final class CaseFunction extends Expressions
            implements SQLFunction._CaseWhenSpec
            , SQLFunction._CaseFuncWhenClause
            , SQLFunction._CaseThenClause
            , CriteriaContextSpec
            , CaseWhens
            , SQLFunction._DynamicCaseThenClause
            , SQLFunction {

        private final ArmyExpression caseValue;

        private final CriteriaContext context;

        private List<_Pair<ArmyExpression, ArmyExpression>> expPairList;

        private ArmyExpression whenExpression;

        private ArmyExpression elseExpression;

        private CaseFunction(@Nullable ArmyExpression caseValue) {
            super(StringType.INSTANCE);
            this.caseValue = caseValue;
            this.context = ContextStack.peek();
        }

        @Override
        public CriteriaContext getContext() {
            return this.context;
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
        public CaseFunction when(final @Nullable Expression expression) {
            if (this.whenExpression != null) {
                throw ContextStack.criteriaError(this.context, "last when clause not end.");
            } else if (expression == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.whenExpression = (ArmyExpression) expression;
            return this;
        }

        @Override
        public CaseFunction when(Supplier<Expression> supplier) {
            return this.when(supplier.get());
        }

        @Override
        public <T> CaseFunction when(Function<T, Expression> valueOperator, Supplier<T> getter) {
            return this.when(valueOperator.apply(getter.get()));
        }

        @Override
        public CaseFunction when(Function<Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            return this.when(valueOperator.apply(function.apply(keyName)));
        }

        @Override
        public <T> CaseFunction when(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter) {
            return this.when(expOperator.apply(valueOperator, getter.get()));
        }

        @Override
        public CaseFunction when(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            return this.when(expOperator.apply(valueOperator, function.apply(keyName)));
        }

        @Override
        public <T> CaseFunction when(BetweenValueOperator<T> expOperator
                , BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter
                , SQLs.WordAnd and, Supplier<T> secondGetter) {
            return this.when(expOperator.apply(operator, firstGetter.get(), and, secondGetter.get()));
        }

        @Override
        public CaseFunction when(BetweenValueOperator<Object> expOperator
                , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
                , String firstKey, SQLs.WordAnd and, String secondKey) {
            return this.when(expOperator.apply(operator, function.apply(firstKey), and, function.apply(secondKey)));
        }

        @Override
        public CaseFunction when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and
                , Expression second) {
            return this.when(expOperator.apply(first, and, second));
        }

        @Override
        public <T> CaseFunction when(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<T> expOperator
                , BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter
                , SQLsSyntax.WordAnd and, Supplier<T> secondGetter) {
            final IPredicate predicate;
            predicate = expOperator.apply(operator, firstGetter.get(), and, secondGetter.get());
            return this.when(predicateOperator.apply(predicate));
        }

        @Override
        public CaseFunction when(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<Object> expOperator
                , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey
                , SQLs.WordAnd and, String secondKey) {
            final IPredicate predicate;
            predicate = expOperator.apply(operator, function.apply(firstKey), and, function.apply(secondKey));
            return this.when(predicateOperator.apply(predicate));
        }

        @Override
        public CaseFunction when(UnaryOperator<IPredicate> predicateOperator, BetweenOperator expOperator
                , Expression first, SQLs.WordAnd and, Expression second) {
            return this.when(predicateOperator.apply(expOperator.apply(first, and, second)));
        }

        @Override
        public SQLFunction._CaseElseClause whens(Consumer<CaseWhens> consumer) {
            consumer.accept(this);
            return this;
        }

        @Override
        public CaseFunction ifWhen(Supplier<Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression != null) {
                this.when(expression);
            }
            return this;
        }

        @Override
        public <T> CaseFunction ifWhen(Function<T, Expression> valueOperator, Supplier<T> getter) {
            final T operand;
            operand = getter.get();
            if (operand != null) {
                this.when(valueOperator.apply(operand));
            }
            return this;
        }

        @Override
        public CaseFunction ifWhen(Function<Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            final Object operand;
            operand = function.apply(keyName);
            if (operand != null) {
                this.when(valueOperator.apply(operand));
            }
            return this;
        }

        @Override
        public <T> CaseFunction ifWhen(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter) {
            final T operand;
            operand = getter.get();
            if (operand != null) {
                this.when(expOperator.apply(valueOperator, operand));
            }
            return this;
        }

        @Override
        public CaseFunction ifWhen(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            final Object operand;
            operand = function.apply(keyName);
            if (operand != null) {
                this.when(expOperator.apply(valueOperator, operand));
            }
            return this;
        }

        @Override
        public <T> CaseFunction ifWhen(BetweenValueOperator<T> expOperator
                , BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and
                , Supplier<T> secondGetter) {
            final T first, second;
            if ((first = firstGetter.get()) != null && (second = secondGetter.get()) != null) {
                this.when(expOperator.apply(operator, first, and, second));
            }
            return this;
        }

        @Override
        public CaseFunction ifWhen(BetweenValueOperator<Object> expOperator
                , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
                , String firstKey, SQLs.WordAnd and, String secondKey) {
            final Object first, second;
            if ((first = function.apply(firstKey)) != null && (second = function.apply(secondKey)) != null) {
                this.when(expOperator.apply(operator, first, and, second));
            }
            return this;
        }

        @Override
        public <T> CaseFunction ifWhen(UnaryOperator<IPredicate> predicateOperator
                , BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator
                , Supplier<T> firstGetter, SQLsSyntax.WordAnd and, Supplier<T> secondGetter) {
            final T first, second;
            if ((first = firstGetter.get()) != null && (second = secondGetter.get()) != null) {
                this.when(predicateOperator.apply(expOperator.apply(operator, first, and, second)));
            }
            return this;
        }

        @Override
        public CaseFunction ifWhen(UnaryOperator<IPredicate> predicateOperator
                , BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator
                , Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey) {
            final Object first, second;
            if ((first = function.apply(firstKey)) != null && (second = function.apply(secondKey)) != null) {
                this.when(predicateOperator.apply(expOperator.apply(operator, first, and, second)));
            }
            return this;
        }

        @Override
        public CaseFunction then(final @Nullable Expression expression) {
            final ArmyExpression whenExpression = this.whenExpression;
            if (whenExpression != null) {
                if (expression == null) {
                    throw ContextStack.nullPointer(this.context);
                }
                this.whenExpression = null; //clear for next when clause
                List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
                if (expPairList == null) {
                    expPairList = new ArrayList<>();
                    this.expPairList = expPairList;
                } else if (!(expPairList instanceof ArrayList)) {
                    throw ContextStack.castCriteriaApi(this.context);
                }
                expPairList.add(_Pair.create(whenExpression, (ArmyExpression) expression));
            }
            return this;
        }

        @Override
        public CaseFunction then(Supplier<Expression> supplier) {
            if (this.whenExpression != null) {
                this.then(supplier.get());
            }
            return this;
        }

        @Override
        public <T> CaseFunction then(Function<T, Expression> valueOperator, Supplier<T> getter) {
            if (this.whenExpression != null) {
                this.then(valueOperator.apply(getter.get()));
            }
            return this;
        }

        @Override
        public CaseFunction then(Function<Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            if (this.whenExpression != null) {
                this.then(valueOperator.apply(function.apply(keyName)));
            }
            return this;
        }

        @Override
        public <T> CaseFunction then(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter) {
            if (this.whenExpression != null) {
                this.then(expOperator.apply(valueOperator, getter.get()));
            }
            return this;
        }

        @Override
        public CaseFunction then(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            if (this.whenExpression != null) {
                this.then(expOperator.apply(valueOperator, function.apply(keyName)));
            }
            return this;
        }


        @Override
        public SQLFunction._CaseEndClause elseValue(final @Nullable Expression expression) {
            if (this.expPairList == null) {
                throw noWhenClause();
            } else if (this.whenExpression != null) {
                throw lastWhenClauseNotEnd();
            } else if (this.elseExpression != null) {
                throw ContextStack.criteriaError(this.context, "duplicate else clause.");
            } else if (expression == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.elseExpression = (ArmyExpression) expression;
            return this;
        }

        @Override
        public SQLFunction._CaseEndClause elseValue(Supplier<Expression> supplier) {
            return this.elseValue(supplier.get());
        }

        @Override
        public <T> SQLFunction._CaseEndClause elseValue(Function<T, Expression> valueOperator
                , Supplier<T> getter) {
            return this.elseValue(valueOperator.apply(getter.get()));
        }

        @Override
        public SQLFunction._CaseEndClause elseValue(Function<Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            return this.elseValue(valueOperator.apply(function.apply(keyName)));
        }

        @Override
        public <T> SQLFunction._CaseEndClause elseValue(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter) {
            return this.elseValue(expOperator.apply(valueOperator, getter.get()));
        }

        @Override
        public SQLFunction._CaseEndClause elseValue(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            return this.elseValue(expOperator.apply(valueOperator, function.apply(keyName)));
        }

        @Override
        public SQLFunction._CaseEndClause ifElse(Supplier<Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression != null) {
                this.elseValue(expression);
            }
            return this;
        }

        @Override
        public <T> SQLFunction._CaseEndClause ifElse(Function<T, Expression> valueOperator
                , Supplier<T> getter) {
            final T operand;
            operand = getter.get();
            if (operand != null) {
                this.elseValue(valueOperator.apply(operand));
            }
            return this;
        }

        @Override
        public SQLFunction._CaseEndClause ifElse(Function<Object, Expression> valueOperator
                , Function<String, ?> function, String keyName) {
            final Object operand;
            operand = function.apply(keyName);
            if (operand != null) {
                this.elseValue(valueOperator.apply(operand));
            }
            return this;
        }

        @Override
        public <T> SQLFunction._CaseEndClause ifElse(ExpressionOperator<Expression, T, Expression> expOperator
                , BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter) {
            final T operand;
            operand = getter.get();
            if (operand != null) {
                this.elseValue(expOperator.apply(valueOperator, operand));
            }
            return this;
        }

        @Override
        public SQLFunction._CaseEndClause ifElse(ExpressionOperator<Expression, Object, Expression> expOperator
                , BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function
                , String keyName) {
            final Object operand;
            operand = function.apply(keyName);
            if (operand != null) {
                this.elseValue(expOperator.apply(valueOperator, operand));
            }
            return this;
        }

        @Override
        public Expression end() {
            if (this.whenExpression != null) {
                throw lastWhenClauseNotEnd();
            }
            final List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
            if (expPairList == null) {
                throw noWhenClause();
            } else if (expPairList instanceof ArrayList) {
                this.expPairList = _CollectionUtils.unmodifiableList(expPairList);
            } else {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this;
        }


        @Override
        public Expression end(final @Nullable TypeInfer type) {
            this.end();
            if (type == null) {
                throw ContextStack.nullPointer(this.context);
            }
            final TypeMeta typeMeta;
            if (type instanceof TypeMeta) {
                typeMeta = (TypeMeta) type;
            } else {
                typeMeta = type.typeMeta();
            }
            return this.mapTo(typeMeta);
        }

        private CriteriaException noWhenClause() {
            return ContextStack.criteriaError(this.context, "Not found any when clause.");
        }

        private CriteriaException lastWhenClauseNotEnd() {
            return ContextStack.criteriaError(this.context, "current when clause not end");
        }


    }//CaseFunc


    private static final class GlobalWindow implements ArmyWindow {

        private static final GlobalWindow INSTANCE = new GlobalWindow();

        private GlobalWindow() {
        }

        @Override
        public void appendSql(final _SqlContext context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArmyWindow endWindowClause() {
            return this;
        }

        @Override
        public String windowName() {
            throw new IllegalStateException("this is global window");
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


}
