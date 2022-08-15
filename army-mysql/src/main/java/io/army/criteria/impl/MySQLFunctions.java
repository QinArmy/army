package io.army.criteria.impl;

import io.army.criteria.Distinct;
import io.army.criteria.FuncExpression;
import io.army.criteria.Window;
import io.army.criteria.mysql.MySQLWindowFunc;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.DoubleType;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;

abstract class MySQLFunctions extends SQLFunctions {

    private MySQLFunctions() {
    }


    static MySQLWindowFunc._OverSpec oneArgWindowFunc(String name, @Nullable Object exp, MappingType returnType) {
        return new OneArgWindowFunc(name, SQLs._funcParam(exp), returnType);
    }

    static MySQLWindowFunc._AggregateOverSpec aggregateWindowFunc(String name, @Nullable Object exp, MappingType returnType) {
        return new OneArgAggregateWindowFunc(name, SQLs._funcParam(exp), returnType);
    }

    static FuncExpression avg(@Nullable Distinct distinct, @Nullable Object exp) {
        if (!(distinct == null || distinct == Distinct.DISTINCT)) {
            String m = String.format("MySQL AVG function support only %s", Distinct.DISTINCT);
            throw CriteriaContextStack.criteriaError(CriteriaContextStack.peek(), m);
        }
        return new AvgFunc(distinct, SQLs._funcParam(exp));
    }


    private static abstract class WindowFunc extends WindowFuncClause
            implements MySQLWindowFunc._OverSpec {


        private WindowFunc(String name, ParamMeta returnType) {
            super(name, returnType);
        }

        @Override
        public final Window._SimpleOverLestParenSpec over() {
            return SimpleWindow.simpleOverClause(this);
        }


    }//WindowFunc


    private static final class OneArgWindowFunc extends MySQLFunctions.WindowFunc {

        private final ArmyExpression argument;

        private OneArgWindowFunc(String name, ArmyExpression argument, ParamMeta returnType) {
            super(name, returnType);
            this.argument = argument;
        }

        @Override
        void appendArguments(final _SqlContext context) {
            this.argument.appendSql(context);
        }

        @Override
        void argumentToString(final StringBuilder builder) {
            builder.append(this.argument);
        }

    }//OneArgumentWindowFunc


    private static final class OneArgAggregateWindowFunc extends SQLFunctions.AggregateWindowFunc
            implements MySQLWindowFunc._AggregateOverSpec {

        private final ArmyExpression argument;

        private OneArgAggregateWindowFunc(String name, ArmyExpression argument, ParamMeta returnType) {
            super(name, returnType);
            this.argument = argument;
        }


        @Override
        void appendArguments(final _SqlContext context) {
            this.argument.appendSql(context);
        }

        @Override
        public Window._SimpleOverLestParenSpec over() {
            return SimpleWindow.simpleOverClause(this);
        }

        @Override
        void argumentToString(final StringBuilder builder) {
            builder.append(this.argument);
        }


    }//OneArgAggregateWindowFunc

    private static final class AvgFunc extends SQLFunctions.FunctionExpression {

        private final Distinct distinct;

        private final ArmyExpression argument;

        private AvgFunc(@Nullable Distinct distinct, ArmyExpression argument) {
            super("AVG", DoubleType.INSTANCE);
            this.distinct = distinct;
            this.argument = argument;
        }

        @Override
        void appendArguments(final _SqlContext context) {
            final Distinct distinct = this.distinct;
            if (distinct == Distinct.DISTINCT) {
                context.sqlBuilder().append(distinct.keyWords);
            } else {
                assert distinct == null;
            }
            this.argument.appendSql(context);

        }

        @Override
        void argumentsToString(final StringBuilder builder) {
            final Distinct distinct = this.distinct;
            if (distinct == Distinct.DISTINCT) {
                builder.append(distinct.keyWords);
            } else {
                assert distinct == null;
            }
            builder.append(this.argument);
        }


    }//AvgFunc


}
