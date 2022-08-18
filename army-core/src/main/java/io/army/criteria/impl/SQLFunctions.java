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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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


    static Functions._CaseWhenClause caseFunc(@Nullable Expression caseValue) {
        return new CaseFunc((ArmyExpression) caseValue);
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


    enum NullTreatment implements SQLWords {

        RESPECT_NULLS(" RESPECT NULLS"),
        IGNORE_NULLS(" IGNORE NULLS");

        final String words;

        NullTreatment(String words) {
            this.words = words;
        }

        @Override
        public final String render() {
            return this.words;
        }

        @Override
        public final String toString() {
            return String.format("%s.%s", NullTreatment.class.getSimpleName(), this.name());
        }


    }//NullTreatment

    enum FromFirstLast implements SQLWords {

        FROM_FIRST(" FROM FIRST"),
        FROM_LAST(" FROM LAST");

        final String words;

        FromFirstLast(String words) {
            this.words = words;
        }

        @Override
        public final String render() {
            return this.words;
        }

        @Override
        public final String toString() {
            return String.format("%s.%s", FromFirstLast.class.getSimpleName(), this.name());
        }

    }//FromFirstLast

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


    private static final class CaseFunc extends OperationExpression
            implements Functions._CaseWhenSpec, Functions._CaseThenClause
            , FunctionSpec, CriteriaContextSpec, MutableParamMetaSpec {

        private final ArmyExpression caseValue;

        private final CriteriaContext criteriaContext;

        private List<_Pair<ArmyExpression, ArmyExpression>> expPairList;

        private ArmyExpression whenExpression;

        private ArmyExpression elseExpression;

        private ParamMeta returnType;

        private CaseFunc(@Nullable ArmyExpression caseValue) {
            this.caseValue = caseValue;
            this.criteriaContext = CriteriaContextStack.peek();
        }

        @Override
        public CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }

        @Override
        public ParamMeta paramMeta() {
            ParamMeta returnType = this.returnType;
            if (returnType == null) {
                returnType = CriteriaSupports.delayParamMeta(this::inferAggregatedType);
            }
            return returnType;
        }

        @Override
        public void updateParamMeta(final ParamMeta paramMeta) {
            this.returnType = paramMeta;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final int pairSize;
            final List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
            if (expPairList == null || (pairSize = expPairList.size()) == 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
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
        public Functions._CaseThenClause when(final @Nullable Expression expression) {
            if (this.whenExpression != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            if (expression == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            this.whenExpression = (ArmyExpression) expression;
            return this;
        }

        @Override
        public Functions._CaseThenClause when(Supplier<? extends Expression> supplier) {
            return this.when(supplier.get());
        }

        @Override
        public Functions._CaseThenClause when(Function<Object, ? extends Expression> operator, Supplier<?> supplier) {
            return this.when(operator.apply(supplier.get()));
        }

        @Override
        public Functions._CaseThenClause when(Function<Object, ? extends Expression> operator
                , Function<String, ?> function, String keyName) {
            return this.when(operator.apply(function.apply(keyName)));
        }

        @Override
        public Functions._CaseThenClause when(BiFunction<Object, Object, ? extends Expression> operator
                , Supplier<?> firstOperand, Supplier<?> secondOperand) {
            return this.when(operator.apply(firstOperand.get(), secondOperand.get()));
        }

        @Override
        public Functions._CaseThenClause when(BiFunction<Object, Object, ? extends Expression> operator
                , Function<String, ?> function, String firstKey, String secondKey) {
            return this.when(operator.apply(function.apply(firstKey), function.apply(secondKey)));
        }

        @Override
        public Functions._CaseThenClause ifWhen(Supplier<? extends Expression> supplier) {
            final Expression expression;
            if ((expression = supplier.get()) != null) {
                this.when(expression);
            }
            return this;
        }

        @Override
        public Functions._CaseThenClause ifWhen(Function<Object, ? extends Expression> operator, Supplier<?> supplier) {
            final Object value;
            if ((value = supplier.get()) != null) {
                this.when(operator.apply(value));
            }
            return this;
        }

        @Override
        public Functions._CaseThenClause ifWhen(Function<Object, ? extends Expression> operator
                , Function<String, ?> function, String keyName) {
            final Object value;
            if ((value = function.apply(keyName)) != null) {
                this.when(operator.apply(value));
            }
            return this;
        }

        @Override
        public Functions._CaseThenClause ifWhen(BiFunction<Object, Object, ? extends Expression> operator
                , Supplier<?> firstOperand, Supplier<?> secondOperand) {
            final Object firstValue, secondValue;
            if ((firstValue = firstOperand.get()) != null && (secondValue = secondOperand.get()) != null) {
                this.when(operator.apply(firstValue, secondValue));
            }
            return this;
        }

        @Override
        public Functions._CaseThenClause ifWhen(BiFunction<Object, Object, ? extends Expression> operator
                , Function<String, ?> function, String firstKey, String secondKey) {
            final Object firstValue, secondValue;
            if ((firstValue = function.apply(firstKey)) != null && (secondValue = function.apply(secondKey)) != null) {
                this.when(operator.apply(firstValue, secondValue));
            }
            return this;
        }

        @Override
        public Functions._CaseWhenSpec then(final @Nullable Expression expression) {
            final ArmyExpression whenExpression = this.whenExpression;
            if (whenExpression != null) {
                this.whenExpression = null; //clear for next when clause.
                if (expression == null) {
                    throw CriteriaContextStack.nullPointer(this.criteriaContext);
                }
                List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
                if (expPairList == null) {
                    this.expPairList = expPairList = new ArrayList<>();
                } else if (!(expPairList instanceof ArrayList)) {
                    throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
                }
                expPairList.add(_Pair.create(whenExpression, (ArmyExpression) expression));
            }
            return this;
        }

        @Override
        public Functions._CaseWhenSpec then(Supplier<? extends Expression> supplier) {
            if (this.whenExpression != null) {
                this.then(supplier.get());
            }
            return this;
        }

        @Override
        public Functions._CaseWhenSpec then(Function<Object, ? extends Expression> operator, Supplier<?> supplier) {
            if (this.whenExpression != null) {
                this.then(operator.apply(supplier.get()));
            }
            return this;
        }

        @Override
        public Functions._CaseWhenSpec then(Function<Object, ? extends Expression> operator
                , Function<String, ?> function, String keyName) {
            if (this.whenExpression != null) {
                this.then(operator.apply(function.apply(keyName)));
            }
            return this;
        }

        @Override
        public Functions._CaseWhenSpec then(BiFunction<Object, Object, ? extends Expression> operator
                , Supplier<?> firstOperand, Supplier<?> secondOperand) {
            if (this.whenExpression != null) {
                this.then(operator.apply(firstOperand.get(), secondOperand.get()));
            }
            return this;
        }

        @Override
        public Functions._CaseWhenSpec then(BiFunction<Object, Object, ? extends Expression> operator
                , Function<String, ?> function, String firstKey, String secondKey) {
            if (this.whenExpression != null) {
                this.then(operator.apply(function.apply(firstKey), function.apply(secondKey)));
            }
            return this;
        }

        @Override
        public Functions._CaseEndClause elseExp(final @Nullable Expression expression) {
            if (this.elseExpression != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            if (expression == null) {
                throw CriteriaContextStack.nullPointer(this.criteriaContext);
            }
            this.elseExpression = (ArmyExpression) expression;
            return this;
        }

        @Override
        public Functions._CaseEndClause elseExp(Supplier<? extends Expression> supplier) {
            return this.elseExp(supplier.get());
        }

        @Override
        public Functions._CaseEndClause elseExp(Function<Object, ? extends Expression> operator, Supplier<?> supplier) {
            return this.elseExp(operator.apply(supplier.get()));
        }

        @Override
        public Functions._CaseEndClause elseExp(Function<Object, ? extends Expression> operator
                , Function<String, ?> function, String keyName) {
            return this.elseExp(operator.apply(function.apply(keyName)));
        }

        @Override
        public Functions._CaseEndClause elseExp(BiFunction<Object, Object, ? extends Expression> operator
                , Supplier<?> firstOperand, Supplier<?> secondOperand) {
            return this.elseExp(operator.apply(firstOperand.get(), secondOperand.get()));
        }

        @Override
        public Functions._CaseEndClause elseExp(BiFunction<Object, Object, ? extends Expression> operator
                , Function<String, ?> function, String firstKey, String secondKey) {
            return this.elseExp(operator.apply(function.apply(firstKey), function.apply(secondKey)));
        }

        @Override
        public Functions._CaseEndClause ifElse(Supplier<? extends Expression> supplier) {
            final Expression expression;
            if ((expression = supplier.get()) != null) {
                this.elseExp(expression);
            }
            return this;
        }

        @Override
        public Functions._CaseEndClause ifElse(Function<Object, ? extends Expression> operator, Supplier<?> supplier) {
            final Object value;
            if ((value = supplier.get()) != null) {
                this.elseExp(operator.apply(value));
            }
            return this;
        }

        @Override
        public Functions._CaseEndClause ifElse(Function<Object, ? extends Expression> operator
                , Function<String, ?> function, String keyName) {
            final Object value;
            if ((value = function.apply(keyName)) != null) {
                this.elseExp(operator.apply(value));
            }
            return this;
        }

        @Override
        public Functions._CaseEndClause ifElse(BiFunction<Object, Object, ? extends Expression> operator
                , Supplier<?> firstOperand, Supplier<?> secondOperand) {
            final Object firstValue, secondValue;
            if ((firstValue = firstOperand.get()) != null && (secondValue = secondOperand.get()) != null) {
                this.elseExp(operator.apply(firstValue, secondValue));
            }
            return this;
        }

        @Override
        public Functions._CaseEndClause ifElse(BiFunction<Object, Object, ? extends Expression> operator
                , Function<String, ?> function, String firstKey, String secondKey) {
            final Object firstValue, secondValue;
            if ((firstValue = function.apply(firstKey)) != null && (secondValue = function.apply(secondKey)) != null) {
                this.elseExp(operator.apply(firstValue, secondValue));
            }
            return this;
        }

        @Override
        public Expression end() {
            final List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
            if (expPairList == null || expPairList.size() == 0) {
                String m = "case function at least one when clause";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else if (expPairList instanceof ArrayList) {
                ParamMeta returnType = null;
                for (_Pair<ArmyExpression, ArmyExpression> pair : expPairList) {
                    if (pair.second instanceof OperationExpression) {
                        returnType = pair.second.paramMeta();
                        break;
                    }
                }
                if (returnType == null) {
                    String m = "at least one then clause is operation expression.";
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
                }
                this.expPairList = _CollectionUtils.unmodifiableList(expPairList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this;
        }

        private MappingType inferAggregatedType() {
            final List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
            final int pairSize;
            if (expPairList == null || (expPairList instanceof ArrayList) || (pairSize = expPairList.size()) == 0) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            _Pair<ArmyExpression, ArmyExpression> pair;
            ParamMeta resultType;
            for (int i = 0; i < pairSize; i++) {
                pair = expPairList.get(i);
                resultType = pair.second.paramMeta();
                if (!(resultType instanceof MappingType)) {
                    resultType = resultType.mappingType();
                }


            }
            //TODO
            throw new UnsupportedOperationException();
        }


    }//CaseFunc

    private enum CaseResultType {
        DOUBLE,
        DECIMAL,

    }


}
