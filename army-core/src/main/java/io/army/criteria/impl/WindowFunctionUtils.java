package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.SimpleExpression;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._Window;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;

import java.util.function.Consumer;

abstract class WindowFunctionUtils {

    private WindowFunctionUtils() {
        throw new UnsupportedOperationException();
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
            }
            this.anonymousWindow = GlobalWindow.INSTANCE;
            return this;
        }

        @Override
        public final SimpleExpression over(final @Nullable String existingWindowName) {
            if (this.existingWindowName != null || this.anonymousWindow != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
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
                    anonymousWindow.appendSql(context);
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
