package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.List;

abstract class SQLFunctions extends OperationExpression implements Expression {

    SQLFunctions() {
    }

    /**
     * package method that is used by army developer.
     *
     * @param value {@link Expression} or parameter
     */
    static ArmyExpression funcParam(final @Nullable Object value) {
        final ArmyExpression expression;
        if (value == null) {
            expression = (ArmyExpression) SQLs.nullWord();
        } else if (value instanceof Expression) {
            expression = (ArmyExpression) value;
        } else {
            expression = (ArmyExpression) SQLs.param(value);
        }
        return expression;
    }

    static List<ArmyExpression> funcParamList(final List<?> argList) {
        final List<ArmyExpression> expList = new ArrayList<>(argList.size());
        for (Object o : argList) {
            expList.add(funcParam(o));
        }
        return expList;
    }

    static Expression noArgumentFunc(String name, MappingType returnType) {
        // return new NoArgumentFunc<>(name, returnType);
        return null;
    }

    @Deprecated
    static FuncExpression oneArgumentFunc(String name, @Nullable SQLWords option, @Nullable Object exp, MappingType returnType) {
        return new OneArgFunc(name, funcParam(exp), returnType);
    }

    static Expression twoArgumentFunc(String name, MappingType returnType, Expression one
            , Expression two) {
        //  return new TwoArgumentFunc<>(name, returnType, (_Expression) one, (_Expression) two);
        return null;
    }

    static Expression twoArgumentFunc(String name, MappingType returnType, List<String> format
            , Expression one, Expression two) {
        //  return new TwoArgumentFunc<>(name, returnType, format, (_Expression) one, (_Expression) two);
        return null;
    }

    static FuncExpression oneArgOptionFunc(String name, @Nullable SQLWords option
            , @Nullable Object expr, @Nullable Clause clause, ParamMeta returnType) {
        return new OneArgOptionFunc(name, option, SQLFunctions.funcParam(expr), clause, returnType);
    }


    static FuncExpression multiArgOptionFunc(String name, @Nullable SQLWords option
            , List<?> argList, @Nullable Clause clause, ParamMeta returnType) {
        return new MultiArgOptionFunc(name, option, funcParamList(argList), clause, returnType);
    }


    static void appendArguments(final @Nullable SQLWords option, final List<ArmyExpression> argList
            , final @Nullable Clause clause, final _SqlContext context) {

        final StringBuilder sqlBuilder = context.sqlBuilder();

        if (option != null) {
            sqlBuilder.append(option.render());
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
            builder.append(option.render());
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


    interface FunctionSpec extends _SelfDescribed, TypeInfer {


    }

    interface WinFuncSpec extends FunctionSpec, CriteriaContextSpec {

        void appendFunc(_SqlContext context);
    }

    static abstract class ArgumentClause implements Clause, _SelfDescribed, CriteriaContextSpec {

        final CriteriaContext criteriaContext;

        ArgumentClause() {
            this.criteriaContext = CriteriaContextStack.peek();
        }

        @Override
        public String toString() {
            return super.toString();
        }


    }//ArgumentClause


    static abstract class WindowFuncClause implements WinFuncSpec
            , SelectionSpec
            , Window._OverClause {

        final CriteriaContext criteriaContext;
        final String name;

        private ParamMeta returnType;

        private String existingWindowName;

        WindowFuncClause(String name, ParamMeta returnType) {
            this.criteriaContext = CriteriaContextStack.peek();
            this.name = name;
            this.returnType = returnType;
        }

        @Override
        public final CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }

        @Override
        public final ParamMeta paramMeta() {
            return this.returnType;
        }

        @Override
        public final SelectionSpec asType(final @Nullable ParamMeta paramMeta) {
            if (this.existingWindowName == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            if (paramMeta == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            this.returnType = paramMeta;
            return this;
        }

        @Override
        public final Selection as(String alias) {
            return Selections.forFunc(this, alias);
        }


        @Override
        public final SelectionSpec over(final String windowName) {
            if (this.existingWindowName != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.criteriaContext.onRefWindow(windowName);
            this.existingWindowName = windowName;
            return this;
        }


        @Override
        public final void appendSql(final _SqlContext context) {
            final String existingWindowName = this.existingWindowName;
            if (existingWindowName == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final StringBuilder sqlBuilder = context.sqlBuilder();
            final DialectParser parser = context.parser();
            //1. function
            sqlBuilder.append(_Constant.SPACE)
                    .append(this.name) // function name
                    .append(_Constant.LEFT_PAREN);

            this.appendArguments(context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            //2. OVER clause
            sqlBuilder.append(_Constant.SPACE_OVER_LEFT_PAREN);
            parser.identifier(existingWindowName, sqlBuilder);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }

        @Override
        public final void appendFunc(final _SqlContext context) {
            if (this.existingWindowName != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
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
            return super.toString();
        }

        abstract void appendArguments(final _SqlContext context);

        abstract void argumentToString(StringBuilder builder);

    }//WindowFuncClause


    static abstract class AggregateWindowFunc extends OperationExpression
            implements Window._OverClause
            , FuncExpression
            , WinFuncSpec {

        final CriteriaContext criteriaContext;

        final String name;

        private final ParamMeta returnType;

        private String existingWindowName;

        AggregateWindowFunc(String name, ParamMeta returnType) {
            this.criteriaContext = CriteriaContextStack.peek();
            this.name = name;
            this.returnType = returnType;
        }

        @Override
        public final CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }

        @Override
        public final String name() {
            return this.name;
        }

        @Override
        public final ParamMeta paramMeta() {
            return this.returnType;
        }

        @Override
        public final SelectionSpec over(final String windowName) {
            if (this.existingWindowName != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.criteriaContext.onRefWindow(windowName);
            this.existingWindowName = windowName;
            return this;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final String existingWindowName;
            if ((existingWindowName = this.existingWindowName) == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            //1. function
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name) // function name
                    .append(_Constant.LEFT_PAREN);

            this.appendArguments(context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            //2. OVER clause
            sqlBuilder.append(_Constant.SPACE_OVER_LEFT_PAREN);
            context.parser().identifier(existingWindowName, sqlBuilder);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        @Override
        public final void appendFunc(final _SqlContext context) {
            if (this.existingWindowName != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            //1. function
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
            return super.toString();
        }

        abstract void appendArguments(_SqlContext context);

        abstract void argumentToString(StringBuilder builder);

    }//AggregateOverClause


    static abstract class FunctionExpression extends OperationExpression implements FuncExpression
            , FunctionSpec {

        private final String name;

        private final ParamMeta returnType;

        FunctionExpression(String name, ParamMeta returnType) {
            this.name = name;
            this.returnType = returnType;
        }

        @Override
        public final String name() {
            return this.name;
        }

        @Override
        public final ParamMeta paramMeta() {
            return this.returnType;
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

    private static final class OneArgFunc extends FunctionExpression implements FuncExpression {

        private final ArmyExpression argument;

        private OneArgFunc(String name, ArmyExpression argument, MappingType returnType) {
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


    private static final class OneArgOptionFunc extends SQLFunctions.FunctionExpression {

        private final SQLWords option;

        private final ArmyExpression argument;

        private final Clause clause;

        private OneArgOptionFunc(String name, @Nullable SQLWords option
                , ArmyExpression argument, @Nullable Clause clause
                , ParamMeta returnType) {
            super(name, returnType);
            this.option = option;
            this.argument = argument;
            this.clause = clause;
        }

        @Override
        void appendArguments(final _SqlContext context) {
            final SQLWords option = this.option;
            if (option != null) {
                context.sqlBuilder().append(option.render());
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
                builder.append(option.render());
            }
            builder.append(this.argument);

            final Clause clause = this.clause;
            if (clause != null) {
                builder.append(clause);
            }

        }


    }//OneArgOptionFunc


    private static final class MultiArgOptionFunc extends SQLFunctions.FunctionExpression {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        private final Clause clause;

        private MultiArgOptionFunc(String name, @Nullable SQLWords option
                , List<ArmyExpression> argList, @Nullable Clause clause
                , ParamMeta returnType) {
            super(name, returnType);
            assert argList.size() > 0;

            this.option = option;
            this.argList = _CollectionUtils.unmodifiableList(argList);
            this.clause = clause;
        }

        @Override
        void appendArguments(final _SqlContext context) {
            SQLFunctions.appendArguments(this.option, this.argList, this.clause, context);
        }

        @Override
        void argumentsToString(final StringBuilder builder) {
            SQLFunctions.argumentsToString(this.option, this.argList, this.clause, builder);
        }


    }//MultiArgOptionFunc


}
