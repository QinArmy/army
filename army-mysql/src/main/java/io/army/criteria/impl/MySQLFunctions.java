package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.MySQLClause;
import io.army.criteria.mysql.MySQLUnit;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.StringType;
import io.army.meta.TypeMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MySQLFunctions extends SQLFunctions {

    private MySQLFunctions() {
    }

    static Expression intervalTimeFunc(String name, @Nullable Object date, @Nullable Object expr
            , @Nullable MySQLUnit unit, TypeMeta returnType) {
        if (unit == null) {
            throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
        }
        return new IntervalTimeFunc(name, SQLs._funcParam(date), SQLs._funcParam(expr), unit, returnType);
    }

    static MySQLFuncSyntax._OverSpec noArgWindowFunc(String name, TypeMeta returnType) {
        return new NoArgWindowFunc(name, returnType);
    }

    static MySQLFuncSyntax._OverSpec oneArgWindowFunc(String name, @Nullable SQLWords option
            , @Nullable Object expr, TypeMeta returnType) {
        return new OneArgWindowFunc(name, option, SQLs._funcParam(expr), returnType);
    }

    static MySQLFuncSyntax._OverSpec safeMultiArgWindowFunc(String name, @Nullable SQLWords option
            , List<ArmyExpression> argList, TypeMeta returnType) {
        return new MultiArgWindowFunc(name, option, argList, null, returnType);
    }

    static MySQLFuncSyntax._FromFirstLastSpec safeMultiArgFromFirstWindowFunc(String name, @Nullable SQLWords option
            , List<ArmyExpression> argList, TypeMeta returnType) {
        return new FromFirstLastMultiArgWindowFunc(name, option, argList, returnType);
    }

    static MySQLFuncSyntax._AggregateOverSpec aggregateWindowFunc(String name, @Nullable SQLWords option
            , @Nullable Object exp, TypeMeta returnType) {
        return new OneArgAggregateWindowFunc(name, option, SQLs._funcParam(exp), returnType);
    }

    static MySQLFuncSyntax._AggregateOverSpec safeMultiArgAggregateWindowFunc(String name, @Nullable SQLWords option
            , List<ArmyExpression> argList, @Nullable Clause clause, TypeMeta returnType) {
        return new MultiArgAggregateWindowFunc(name, option, argList, clause, returnType);
    }


    static MySQLFuncSyntax._AggregateOverSpec multiArgAggregateWindowFunc(String name, @Nullable SQLWords option
            , List<?> argList, @Nullable Clause clause, TypeMeta returnType) {
        if (argList.size() == 0) {
            throw CriteriaUtils.funcArgError(name, argList);
        }
        final List<ArmyExpression> expList = new ArrayList<>(argList.size());
        for (Object o : argList) {
            expList.add(SQLs._funcParam(o));
        }
        return new MultiArgAggregateWindowFunc(name, option, expList, clause, returnType);
    }

    static GroupConcatClause groupConcatClause() {
        return new GroupConcatClause();
    }


    private static abstract class MySQLWindowFunc extends WindowFunc<Window._SimpleLeftParenClause<Void, Expression>>
            implements Window._SimpleLeftParenClause<Void, Expression>, MySQLFuncSyntax._OverSpec {

        private MySQLWindowFunc(String name, TypeMeta returnType) {
            super(name, returnType);
        }


        @Override
        public final Window._SimplePartitionBySpec<Void, Expression> leftParen() {
            return SimpleWindow.anonymousWindow(this.context, this::windowEnd)
                    .leftParen();
        }

        @Override
        public final Window._SimplePartitionBySpec<Void, Expression> leftParen(String existingWindowName) {
            return SimpleWindow.anonymousWindow(this.context, this::windowEnd)
                    .leftParen(existingWindowName);
        }

        @Override
        public final Window._SimplePartitionBySpec<Void, Expression> leftParen(Supplier<String> supplier) {
            return SimpleWindow.anonymousWindow(this.context, this::windowEnd)
                    .leftParen(supplier);
        }

        @Override
        public final Window._SimplePartitionBySpec<Void, Expression> leftParen(Function<Void, String> function) {
            return SimpleWindow.anonymousWindow(this.context, this::windowEnd)
                    .leftParen(function);
        }

        @Override
        public final Window._SimplePartitionBySpec<Void, Expression> leftParenIf(Supplier<String> supplier) {
            return SimpleWindow.anonymousWindow(this.context, this::windowEnd)
                    .leftParenIf(supplier);
        }

        @Override
        public final Window._SimplePartitionBySpec<Void, Expression> leftParenIf(Function<Void, String> function) {
            return SimpleWindow.anonymousWindow(this.context, this::windowEnd)
                    .leftParenIf(function);
        }


    }//MySQLWindowFunc

    private static class NoArgWindowFunc extends MySQLWindowFunc implements SQLFunctions.NoArgFunction {

        private NoArgWindowFunc(String name, TypeMeta returnType) {
            super(name, returnType);
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


    private static class OneArgWindowFunc extends MySQLWindowFunc {

        private final SQLWords option;

        private final ArmyExpression argument;

        private OneArgWindowFunc(String name, @Nullable SQLWords option
                , ArmyExpression argument, TypeMeta returnType) {
            super(name, returnType);
            this.option = option;
            this.argument = argument;
        }


        @Override
        final void appendArguments(final _SqlContext context) {
            final SQLWords option = this.option;
            if (option != null) {
                context.sqlBuilder().append(option.render());
            }
            this.argument.appendSql(context);
        }

        @Override
        final void argumentToString(final StringBuilder builder) {
            final SQLWords option = this.option;
            if (option != null) {
                builder.append(option.render());
            }
            builder.append(this.argument);
        }


    }//OneArgAggregateWindowFunc

    private static class MultiArgWindowFunc extends MySQLWindowFunc {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        private final Clause clause;

        private MultiArgWindowFunc(String name, @Nullable SQLWords option
                , List<ArmyExpression> argList, @Nullable Clause clause, TypeMeta returnType) {
            super(name, returnType);

            assert argList.size() > 0;
            this.option = option;
            this.argList = _CollectionUtils.unmodifiableList(argList);
            this.clause = clause;

        }


        @Override
        final void appendArguments(final _SqlContext context) {
            SQLFunctions.appendArguments(this.option, this.argList, this.clause, context);
        }

        @Override
        final void argumentToString(final StringBuilder builder) {
            SQLFunctions.argumentsToString(this.option, this.argList, this.clause, builder);

        }


    }//MultiArgAggregateWindowFunc


    private static class NullTreatmentMultiArgWindowFunc extends MultiArgWindowFunc
            implements MySQLFuncSyntax._NullTreatmentSpec {

        private NullTreatment nullTreatment;

        private NullTreatmentMultiArgWindowFunc(String name, @Nullable SQLWords option
                , List<ArmyExpression> argumentList, TypeMeta returnType) {
            super(name, option, argumentList, null, returnType);
        }

        @Override
        public final MySQLFuncSyntax._OverSpec respectNulls() {
            if (this.nullTreatment != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.nullTreatment = NullTreatment.RESPECT_NULLS;
            return this;
        }

        @Override
        public final MySQLFuncSyntax._OverSpec ignoreNulls() {
            if (this.nullTreatment != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.nullTreatment = NullTreatment.IGNORE_NULLS;
            return this;
        }


    }//NullTreatmentMultiArgWindowFunc

    private static final class FromFirstLastMultiArgWindowFunc extends NullTreatmentMultiArgWindowFunc
            implements MySQLFuncSyntax._FromFirstLastSpec {

        private FromFirstLast fromFirstLast;

        private FromFirstLastMultiArgWindowFunc(String name, @Nullable SQLWords option
                , List<ArmyExpression> argumentList, TypeMeta returnType) {
            super(name, option, argumentList, returnType);
        }

        @Override
        public MySQLFuncSyntax._NullTreatmentSpec fromFirst() {
            if (this.fromFirstLast != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.fromFirstLast = FromFirstLast.FROM_LAST;
            return this;
        }

        @Override
        public MySQLFuncSyntax._NullTreatmentSpec fromLast() {
            if (this.fromFirstLast != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.fromFirstLast = FromFirstLast.FROM_LAST;
            return this;
        }


    }//FromFirstLastMultiArgWindowFunc


    private static final class NoArgAggregateWindowFunc extends NoArgWindowFunc
            implements MySQLFuncSyntax._AggregateOverSpec {

        private NoArgAggregateWindowFunc(String name, TypeMeta returnType) {
            super(name, returnType);
        }

    }//NoArgAggregateWindowFunc


    private static final class OneArgAggregateWindowFunc extends OneArgWindowFunc
            implements MySQLFuncSyntax._AggregateOverSpec {

        private OneArgAggregateWindowFunc(String name, @Nullable SQLWords option
                , ArmyExpression argument, TypeMeta returnType) {
            super(name, option, argument, returnType);
        }

    }//OneArgAggregateWindowFunc


    private static final class MultiArgAggregateWindowFunc extends MultiArgWindowFunc
            implements MySQLFuncSyntax._AggregateOverSpec {

        private MultiArgAggregateWindowFunc(String name, @Nullable SQLWords option
                , List<ArmyExpression> argList, @Nullable Clause clause
                , TypeMeta returnType) {
            super(name, option, argList, clause, returnType);
        }

    }//MultiArgAggregateWindowFunc


    static final class GroupConcatClause extends SQLFunctions.ArgumentClause
            implements MySQLClause._GroupConcatOrderBySpec {

        private final CriteriaContext criteriaContext;
        private List<ArmySortItem> orderByList;

        private String stringValue;

        private GroupConcatClause() {
            this.criteriaContext = CriteriaContextStack.peek();
        }

        @Override
        public CriteriaContext getContext() {
            return this.criteriaContext;
        }


        @Override
        public MySQLClause._GroupConcatSeparatorClause orderBy(SortItem sortItem) {
            this.orderByList = Collections.singletonList((ArmySortItem) sortItem);
            return this;
        }

        @Override
        public MySQLClause._GroupConcatSeparatorClause orderBy(SortItem sortItem1, SortItem sortItem2) {
            this.orderByList = ArrayUtils.asUnmodifiableList(
                    (ArmySortItem) sortItem1,
                    (ArmySortItem) sortItem2
            );
            return this;
        }

        @Override
        public MySQLClause._GroupConcatSeparatorClause orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
            this.orderByList = ArrayUtils.asUnmodifiableList(
                    (ArmySortItem) sortItem1,
                    (ArmySortItem) sortItem2,
                    (ArmySortItem) sortItem3
            );
            return this;
        }

        @Override
        public MySQLClause._GroupConcatSeparatorClause orderBy(Consumer<Consumer<SortItem>> consumer) {
            return CriteriaSupports.<Void, MySQLClause._GroupConcatSeparatorClause>orderByClause(this.criteriaContext, this::orderByEnd)
                    .orderBy(consumer);
        }

        @Override
        public MySQLClause._GroupConcatSeparatorClause orderBy(BiConsumer<Void, Consumer<SortItem>> consumer) {
            return CriteriaSupports.<Void, MySQLClause._GroupConcatSeparatorClause>voidOrderByClause(this.criteriaContext, this::orderByEnd)
                    .orderBy(consumer);
        }

        @Override
        public MySQLClause._GroupConcatSeparatorClause ifOrderBy(Consumer<Consumer<SortItem>> consumer) {
            return CriteriaSupports.<Void, MySQLClause._GroupConcatSeparatorClause>orderByClause(this.criteriaContext, this::orderByEnd)
                    .ifOrderBy(consumer);
        }

        @Override
        public MySQLClause._GroupConcatSeparatorClause ifOrderBy(BiConsumer<Void, Consumer<SortItem>> consumer) {
            return CriteriaSupports.<Void, MySQLClause._GroupConcatSeparatorClause>voidOrderByClause(this.criteriaContext, this::orderByEnd)
                    .ifOrderBy(consumer);
        }

        @Override
        public Clause separator(@Nullable String strVal) {
            if (strVal == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            } else if (this.stringValue != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
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
            sqlBuilder = context.sqlBuilder();
            final List<ArmySortItem> orderByList = this.orderByList;

            if (orderByList != null && orderByList.size() > 0) {
                sqlBuilder.append(_Constant.SPACE_ORDER_BY);
                final int itemSize = orderByList.size();
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

        private MySQLClause._GroupConcatSeparatorClause orderByEnd(final List<ArmySortItem> itemList) {
            if (this.orderByList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.orderByList = itemList;
            return this;
        }


    }//GroupConcatClause

    private static final class IntervalTimeFunc extends SQLFunctions.FunctionExpression {

        private final ArmyExpression date;

        private final ArmyExpression expr;

        private final MySQLUnit unit;

        private IntervalTimeFunc(String name, ArmyExpression date, ArmyExpression expr, MySQLUnit unit
                , TypeMeta returnType) {
            super(name, returnType);
            this.date = date;
            this.expr = expr;
            this.unit = unit;
        }

        @Override
        void appendArguments(final _SqlContext context) {
            this.date.appendSql(context);
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_COMMA)
                    .append(_Constant.SPACE_INTERVAL);

            this.expr.appendSql(context);
            sqlBuilder.append(this.unit);
        }

        @Override
        void argumentsToString(final StringBuilder builder) {
            builder.append(this.date)
                    .append(_Constant.SPACE_COMMA)
                    .append(_Constant.SPACE_INTERVAL)
                    .append(this.expr)
                    .append(this.unit);
        }

    }//IntervalTimeFunc


}
