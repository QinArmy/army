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

package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._Window;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.mysql.MySQLDialect;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

abstract class WindowFunctions {

    private WindowFunctions() {
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


    static abstract class WindowFunction<T extends Window._WindowSpec> extends OperationExpression.SqlFunctionExpression
            implements Window._OverWindowClause<T>, FunctionUtils.FunctionOuterClause {

        private static final String GLOBAL_PLACE_HOLDER = "";

        final CriteriaContext outerContext;

        private String existingWindowName;

        private _Window anonymousWindow;

        WindowFunction(String name, TypeMeta returnType) {
            super(name, returnType);
            this.outerContext = ContextStack.peek();
        }

        @Override
        public final SimpleExpression over() {
            if (this.existingWindowName != null || this.anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (this.outerContext.dialect() == StandardDialect.STANDARD10) {
                throw CriteriaUtils.standard10DontSupportWindow(this.outerContext);
            }
            this.anonymousWindow = GlobalWindow.INSTANCE;
            return this;
        }

        @Override
        public final SimpleExpression over(final @Nullable String existingWindowName) {
            if (this.existingWindowName != null || this.anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (this.outerContext.dialect() == StandardDialect.STANDARD10) {
                throw CriteriaUtils.standard10DontSupportWindowFunc();
            }
            if (existingWindowName == null) {
                this.existingWindowName = GLOBAL_PLACE_HOLDER;
            } else {
                // context don't allow empty existingWindowName
                this.outerContext.onRefWindow(existingWindowName);
                this.existingWindowName = existingWindowName;
            }
            return this;
        }

        @Override
        public final SimpleExpression over(Consumer<T> consumer) {
            return this.over(null, consumer);
        }

        @Override
        public final SimpleExpression over(@Nullable String existingWindowName, Consumer<T> consumer) {
            if (this.outerContext.dialect() == StandardDialect.STANDARD10) {
                throw CriteriaUtils.standard10DontSupportWindow(this.outerContext);
            }
            final T window;
            window = this.createAnonymousWindow(existingWindowName);
            consumer.accept(window);
            if (this.existingWindowName != null || this.anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            ((ArmyWindow) window).endWindowClause();
            this.anonymousWindow = (ArmyWindow) window;
            return this;
        }

        @Override
        public final void appendFuncRest(final StringBuilder sqlBuilder, final _SqlContext context) {

            if (this instanceof _OuterClauseBeforeOver) {
                this.appendClauseBeforeOver(sqlBuilder, context);
            }

            final String existingWindowName = this.existingWindowName;
            final _Window anonymousWindow = this.anonymousWindow;

            final DialectParser parser;
            if (existingWindowName == null && anonymousWindow == null) {
                if (!(this instanceof AggregateFunction)) {
                    throw _Exceptions.castCriteriaApi();
                }
            } else if (existingWindowName != null && anonymousWindow != null) {
                throw _Exceptions.castCriteriaApi();
            } else if (this.isDontSupportWindow((parser = context.parser()).dialect())) {
                String m = String.format("%s don't support %s window function.", parser.dialect(), this.name);
                throw new CriteriaException(m);
            } else {
                sqlBuilder.append(_Constant.SPACE_OVER);
                if (anonymousWindow == GlobalWindow.INSTANCE || GLOBAL_PLACE_HOLDER.equals(existingWindowName)) {
                    sqlBuilder.append(_Constant.PARENS);
                } else if (existingWindowName != null) {
                    sqlBuilder.append(_Constant.SPACE);
                    parser.identifier(existingWindowName, sqlBuilder);
                } else {
                    anonymousWindow.appendSql(sqlBuilder, context);
                }
            }
        }

        @Override
        public final void funcRestToString(final StringBuilder builder) {
            if (this instanceof _OuterClauseBeforeOver) {
                this.outerClauseToString(builder);
            }
            final String existingWindowName = this.existingWindowName;
            final _Window anonymousWindow = this.anonymousWindow;

            if (existingWindowName == null && anonymousWindow == null) {
                if (!(this instanceof AggregateFunction)) {
                    throw ContextStack.castCriteriaApi(this.outerContext);
                }
            } else if (existingWindowName != null && anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else {
                //2. OVER clause
                builder.append(_Constant.SPACE_OVER);
                if (anonymousWindow == GlobalWindow.INSTANCE || GLOBAL_PLACE_HOLDER.equals(existingWindowName)) {
                    builder.append(_Constant.PARENS);
                } else if (existingWindowName != null) {
                    builder.append(_Constant.SPACE)
                            .append(existingWindowName);
                } else {
                    builder.append(anonymousWindow);
                }
            }
        }


        abstract T createAnonymousWindow(@Nullable String existingWindowName);

        abstract boolean isDontSupportWindow(Dialect dialect);

        void appendClauseBeforeOver(StringBuilder sqlBuilder, _SqlContext context) {
            throw new UnsupportedOperationException();
        }

        void outerClauseToString(StringBuilder builder) {
            throw new UnsupportedOperationException();
        }


        final boolean isNotGlobalRowNumber() {
            return this.anonymousWindow != GlobalWindow.INSTANCE || !this.name.equalsIgnoreCase("row_number");
        }

        final CriteriaException dialectError(Dialect dialect) {
            String m = String.format("%s window function[%s]don't support %s.",
                    this.getClass().getName(), this.name, dialect);
            throw ContextStack.criteriaError(this.outerContext, m);
        }


    }//AggregateOverClause

    private static final class GlobalWindow implements ArmyWindow {

        private static final GlobalWindow INSTANCE = new GlobalWindow();

        private GlobalWindow() {
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
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

    private static abstract class StandardWindowFunction extends WindowFunction<Window._StandardPartitionBySpec>
            implements Windows._OverSpec {

        private StandardWindowFunction(String name, TypeMeta returnType) {
            super(name, returnType);
        }


        @Override
        final Window._StandardPartitionBySpec createAnonymousWindow(@Nullable String existingWindowName) {
            return StandardQueries.anonymousWindow(this.outerContext, existingWindowName);
        }

        @Override
        final boolean isDontSupportWindow(final Dialect dialect) {
            final boolean dontSupport;
            switch (dialect.database()) {
                case MySQL:
                    dontSupport = dialect.compareWith(MySQLDialect.MySQL80) < 0;
                    break;
                case PostgreSQL:
                    dontSupport = false;
                    break;
                case H2:
                default:
                    throw _Exceptions.unexpectedEnum((Enum<?>) dialect);
            }
            return dontSupport;
        }


    }// StandardWindowFunction

    private static final class ZeroArgStandardWindowFunc extends StandardWindowFunction
            implements FunctionUtils.NoArgFunction {

        private ZeroArgStandardWindowFunc(String name, TypeMeta returnType) {
            super(name, returnType);
        }


        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            //no-op
        }

        @Override
        void argToString(StringBuilder builder) {
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
        final void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(sqlBuilder, context);
        }

        @Override
        final void argToString(StringBuilder builder) {
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
        final void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            this.one.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            this.two.appendSql(sqlBuilder, context);
        }

        @Override
        final void argToString(StringBuilder builder) {
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
        final void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            FuncExpUtils.appendCompositeList(this.name, this.argList, sqlBuilder, context);
        }

        @Override
        final void argToString(StringBuilder builder) {
            FuncExpUtils.compositeListToString(this.argList, builder);
        }


    } // CompositeStandardWindowFunc

    private static final class CompositeStandardWindowAggFunc extends CompositeStandardWindowFunc
            implements Windows._WindowAggSpec {

        private CompositeStandardWindowAggFunc(String name, List<?> argList, TypeMeta returnType) {
            super(name, argList, returnType);
        }


    } // CompositeStandardWindowAggFunc


}
