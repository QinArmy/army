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
import io.army.criteria.SimpleExpression;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._Window;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class WindowFunctions<T extends Window._WindowSpec> extends OperationExpression.SqlFunctionExpression
        implements Window._OverWindowClause<T>, FunctionUtils.FunctionOuterClause {

    private static final String GLOBAL_PLACE_HOLDER = "";

    protected final CriteriaContext outerContext;

    private String existingWindowName;

    private _Window anonymousWindow;

    protected WindowFunctions(String name, TypeMeta returnType) {
        super(name, returnType);
        this.outerContext = ContextStack.peek();
    }

    @Override
    public final SimpleExpression over() {
        if (this.existingWindowName != null || this.anonymousWindow != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        } else if (this.outerContext.dialect() == StandardDialect.STANDARD10) {
            throw CriteriaUtils.standard10DontSupportWindow(this.outerContext);
        }
        this.anonymousWindow = GlobalWindow.INSTANCE;
        return this;
    }

    @Override
    public final SimpleExpression over(final @Nullable String existingWindowName) {
        if (this.existingWindowName != null || this.anonymousWindow != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
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
            throw ContextStack.clearStackAndCastCriteriaApi();
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
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
        } else if (existingWindowName != null && anonymousWindow != null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
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


    protected abstract T createAnonymousWindow(@Nullable String existingWindowName);

    protected abstract boolean isDontSupportWindow(Dialect dialect);

    protected void appendClauseBeforeOver(StringBuilder sqlBuilder, _SqlContext context) {
        throw new UnsupportedOperationException();
    }

    protected void outerClauseToString(StringBuilder builder) {
        throw new UnsupportedOperationException();
    }


    protected final boolean isNotGlobalRowNumber() {
        return this.anonymousWindow != GlobalWindow.INSTANCE || !this.name.equalsIgnoreCase("row_number");
    }

    protected final CriteriaException dialectError(Dialect dialect) {
        String m = String.format("%s window function[%s]don't support %s.",
                this.getClass().getName(), this.name, dialect);
        throw ContextStack.criteriaError(this.outerContext, m);
    }


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

    } //GlobalWindow


}
