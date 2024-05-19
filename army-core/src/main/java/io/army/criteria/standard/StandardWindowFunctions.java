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

package io.army.criteria.standard;

import io.army.criteria.Expression;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.ArmyExpression;
import io.army.criteria.impl.FuncExpUtils;
import io.army.criteria.impl.FunctionUtils;
import io.army.criteria.impl.WindowFunctions;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.mysql.MySQLDialect;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.List;

abstract class StandardWindowFunctions {

    private StandardWindowFunctions() {
        throw new UnsupportedOperationException();
    }


    static Windows._OverSpec zeroArgWindowFunc(String name, TypeMeta returnType) {
        return new ZeroArgStandardWindowFunc(name, returnType);
    }

    static Windows._OverSpec oneArgWindowFunc(String name, Expression one, TypeMeta returnType) {
        return new OneArgStandardWindowFunc(name, one, returnType);
    }

    static Windows._OverSpec twoArgWindowFunc(String name, Expression one, Expression two, TypeMeta returnType) {
        return new TwoArgStandardWindowFunc(name, one, two, returnType);
    }

    static Windows._OverSpec compositeWindowFunc(String name, List<?> argList, TypeMeta returnType) {
        return new CompositeStandardWindowFunc(name, argList, returnType);
    }


    /*-------------------below Aggregate functions-------------------*/

    static Windows._WindowAggSpec oneArgWindowAggFunc(String name, Expression one, TypeMeta returnType) {
        return new OneArgStandardAggWidowFunc(name, one, returnType);
    }

    static Windows._WindowAggSpec twoArgAggWindow(String name, Expression one, Expression two, TypeMeta returnType) {
        return new TwoArgStandardAggWidowFunc(name, one, two, returnType);
    }

    static Windows._WindowAggSpec compositeWindowAggFunc(String name, List<?> argList, TypeMeta returnType) {
        return new CompositeStandardWindowAggFunc(name, argList, returnType);
    }


    private static final class ZeroArgStandardWindowFunc extends StandardWindowFunction
            implements FunctionUtils.NoArgFunction {

        private ZeroArgStandardWindowFunc(String name, TypeMeta returnType) {
            super(name, returnType);
        }


        @Override
        protected void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            //no-op
        }

        @Override
        protected void argToString(StringBuilder builder) {
            //no-op
        }


    }//ZeroArgStandardWindowFunc

    private static class OneArgStandardWindowFunc extends StandardWindowFunction {

        private final ArmyExpression one;

        private OneArgStandardWindowFunc(String name, Expression one, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
        }

        @Override
        protected final void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(sqlBuilder, context);
        }

        @Override
        protected final void argToString(StringBuilder builder) {
            builder.append(this.one);
        }


    } // OneArgStandardWindowFunc


    private static class TwoArgStandardWindowFunc extends StandardWindowFunction {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private TwoArgStandardWindowFunc(String name, Expression one, Expression two, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
        }

        @Override
        protected final void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            this.one.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            this.two.appendSql(sqlBuilder, context);
        }

        @Override
        protected final void argToString(StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two);
        }


    } // TwoArgStandardWindowFunc


    private static final class OneArgStandardAggWidowFunc extends OneArgStandardWindowFunc
            implements Windows._WindowAggSpec {


        private OneArgStandardAggWidowFunc(String name, Expression one, TypeMeta returnType) {
            super(name, one, returnType);
        }


    } // OneArgStandardAggWidowFunc


    private static final class TwoArgStandardAggWidowFunc extends TwoArgStandardWindowFunc
            implements Windows._WindowAggSpec {


        private TwoArgStandardAggWidowFunc(String name, Expression one, Expression two, TypeMeta returnType) {
            super(name, one, two, returnType);
        }


    } // TwoArgStandardAggWidowFunc


    private static class CompositeStandardWindowFunc extends StandardWindowFunction {

        private final List<?> argList;

        private CompositeStandardWindowFunc(String name, List<?> argList, TypeMeta returnType) {
            super(name, returnType);
            this.argList = argList;
        }


        @Override
        protected final void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            FuncExpUtils.appendCompositeList(this.name, this.argList, sqlBuilder, context);
        }

        @Override
        protected final void argToString(StringBuilder builder) {
            FuncExpUtils.compositeListToString(this.argList, builder);
        }


    } // CompositeStandardWindowFunc

    private static final class CompositeStandardWindowAggFunc extends CompositeStandardWindowFunc
            implements Windows._WindowAggSpec {

        private CompositeStandardWindowAggFunc(String name, List<?> argList, TypeMeta returnType) {
            super(name, argList, returnType);
        }


    } // CompositeStandardWindowAggFunc


    public abstract static class StandardWindowFunction extends WindowFunctions<Window._StandardPartitionBySpec>
            implements Windows._OverSpec {

        private StandardWindowFunction(String name, TypeMeta returnType) {
            super(name, returnType);
        }


        @Override
        protected final Window._StandardPartitionBySpec createAnonymousWindow(@Nullable String existingWindowName) {
            return StandardQueries.anonymousWindow(this.outerContext, existingWindowName);
        }

        @Override
        protected final boolean isDontSupportWindow(final Dialect dialect) {
            return switch (dialect.database()) {
                case MySQL -> dialect.compareWith(MySQLDialect.MySQL80) < 0;
                case SQLite, PostgreSQL -> false;
                default -> throw _Exceptions.unexpectedEnum((Enum<?>) dialect);
            };
        }


    }// StandardWindowFunction
}
