package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;

import java.util.List;

abstract class SQLFunctions extends OperationExpression implements Expression {

    SQLFunctions() {
    }

    static Expression noArgumentFunc(String name, MappingType returnType) {
        // return new NoArgumentFunc<>(name, returnType);
        return null;
    }

    static FuncExpression oneArgumentFunc(String name, @Nullable Object exp, MappingType returnType) {
        return new OneArgFunc(name, SQLs._funcParam(exp), returnType);
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


    interface FunctionSpec extends _SelfDescribed {


        MappingType returnType();

    }

    interface WinFuncSpec extends FunctionSpec, CriteriaContextSpec {

        void appendFunc(_SqlContext context);
    }


    static abstract class WindowFuncClause implements WinFuncSpec
            , SelectionSpec
            , Window._OverClause {

        final CriteriaContext criteriaContext;
        final String name;

        private MappingType returnType;

        private String existingWindowName;

        WindowFuncClause(String name, ParamMeta returnType) {
            this.criteriaContext = CriteriaContextStack.peek();
            this.name = name;
            this.returnType = returnType.mappingType();
        }

        @Override
        public final CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }

        @Override
        public final SelectionSpec asType(ParamMeta paramMeta) {
            if (this.existingWindowName == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            if (paramMeta instanceof MappingType) {
                this.returnType = (MappingType) paramMeta;
            } else {
                this.returnType = paramMeta.mappingType();
            }
            return this;
        }

        @Override
        public final Selection as(String alias) {
            return Selections.forFunc(this, alias);
        }

        @Override
        public final MappingType returnType() {
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
        public final void appendSql(final _SqlContext context) {
            final String existingWindowName = this.existingWindowName;
            if (existingWindowName == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final StringBuilder sqlBuilder = context.sqlBuilder();
            final DialectParser parser = context.dialect();
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

        private final MappingType returnType;

        private String existingWindowName;

        AggregateWindowFunc(String name, ParamMeta returnType) {
            this.criteriaContext = CriteriaContextStack.peek();
            this.name = name;
            this.returnType = returnType.mappingType();
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
        public final MappingType returnType() {
            return this.returnType;
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
            context.dialect().identifier(existingWindowName, sqlBuilder);
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

        private final MappingType returnType;

        FunctionExpression(String name, MappingType returnType) {
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
        public final MappingType returnType() {
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


}
