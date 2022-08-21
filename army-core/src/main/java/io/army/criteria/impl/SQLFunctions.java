package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.impl.inner._Window;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TypeMeta;
import io.army.util._CollectionUtils;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class SQLFunctions extends OperationExpression implements Expression {

    SQLFunctions() {
    }

    enum FuncWord implements SQLWords {

        INTERVAL(_Constant.SPACE_INTERVAL),
        COMMA(_Constant.SPACE_COMMA),
        FROM(_Constant.SPACE_FROM),
        USING(_Constant.SPACE_USING);

        private final String word;

        FuncWord(String word) {
            this.word = word;
        }

        @Override
        public final String render() {
            return this.word;
        }

        @Override
        public final String toString() {
            return String.format("%s.%s", FuncWord.class.getSimpleName(), this.name());
        }


    }//Word

    static List<ArmyExpression> funcParamList(final List<?> argList) {
        final List<ArmyExpression> expList = new ArrayList<>(argList.size());
        for (Object o : argList) {
            expList.add(SQLs._funcParam(o));
        }
        return expList;
    }


    static Functions._CaseWhenClause caseFunc(@Nullable Expression caseValue) {
        return new CaseFunc((ArmyExpression) caseValue);
    }


    static Expression oneArgOptionFunc(String name, @Nullable SQLWords option
            , @Nullable Object expr, @Nullable Clause clause, TypeMeta returnType) {
        return new OneArgOptionFunc(name, option, SQLs._funcParam(expr), clause, returnType);
    }

    static Expression oneArgFunc(String name, @Nullable Object expr, TypeMeta returnType) {
        return new OneArgFunc(name, SQLs._funcParam(expr), returnType);
    }

    static Expression noArgFunc(String name, TypeMeta returnType) {
        return new NoArgFuncExpression(name, returnType);
    }

    static Expression safeComplexArgFunc(String name, List<?> argList, TypeMeta returnType) {
        return new ComplexArgFunc(name, argList, returnType);
    }


    static Expression safeMultiArgOptionFunc(String name, @Nullable SQLWords option
            , List<ArmyExpression> argList, @Nullable Clause clause, TypeMeta returnType) {
        return new MultiArgOptionFunc(name, option, argList, clause, returnType);
    }

    static Expression multiArgOptionFunc(String name, @Nullable SQLWords option
            , List<?> argList, @Nullable Clause clause, TypeMeta returnType) {
        return new MultiArgOptionFunc(name, option, funcParamList(argList), clause, returnType);
    }

    static Functions._FuncConditionTowClause conditionTwoFunc(final String name
            , BiFunction<MappingType, MappingType, MappingType> function) {
        return new ConditionTwoFunc(name, function);
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

    interface ImmutableFunctionSpec extends FunctionSpec {

    }

    interface NoArgFunction {

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


    static abstract class WindowFunc<OR> extends OperationExpression
            implements Window._OverClause<OR>, MutableParamMetaSpec, CriteriaContextSpec {

        final CriteriaContext context;

        final String name;

        private TypeMeta returnType;

        private String existingWindowName;

        private _Window anonymousWindow;

        WindowFunc(String name, TypeMeta returnType) {
            this.context = CriteriaContextStack.peek();
            this.name = name;
            this.returnType = returnType;
        }

        @Override
        public final CriteriaContext getContext() {
            return this.context;
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.returnType;
        }

        @Override
        public final void updateParamMeta(final TypeMeta paramMeta) {
            this.returnType = paramMeta;
        }

        @Override
        public final Expression over(final String windowName) {
            if (this.existingWindowName != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.context.onRefWindow(windowName);
            this.existingWindowName = windowName;
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final OR over() {
            if (this.anonymousWindow != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.anonymousWindow = GlobalWindow.INSTANCE;
            return (OR) this;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final String existingWindowName = this.existingWindowName;
            final _Window anonymousWindow = this.anonymousWindow;
            if (existingWindowName != null && anonymousWindow != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
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

            //2. OVER clause
            if (existingWindowName != null) {
                sqlBuilder.append(_Constant.SPACE_OVER)
                        .append(_Constant.LEFT_PAREN);
                context.parser().identifier(existingWindowName, sqlBuilder);
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            } else if (anonymousWindow instanceof GlobalWindow) {
                sqlBuilder.append(_Constant.SPACE_OVER)
                        .append(_Constant.LEFT_PAREN)
                        .append(_Constant.RIGHT_PAREN);
            } else if (anonymousWindow != null) {
                sqlBuilder.append(_Constant.SPACE_OVER);
                anonymousWindow.appendSql(context);
            } else if (!(this instanceof Window._AggregateWindowFunc)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
        }

        @Override
        public final String toString() {
            final String existingWindowName = this.existingWindowName;
            final _Window anonymousWindow = this.anonymousWindow;

            if (existingWindowName != null && anonymousWindow != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            //1. function
            final StringBuilder sqlBuilder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name) // function name
                    .append(_Constant.LEFT_PAREN);

            if (!(this instanceof NoArgFunction)) {
                this.argumentToString(sqlBuilder);
            }
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            //2. OVER clause
            if (existingWindowName != null) {
                sqlBuilder.append(_Constant.SPACE_OVER)
                        .append(_Constant.LEFT_PAREN)
                        .append(existingWindowName)
                        .append(_Constant.SPACE_RIGHT_PAREN);
            } else if (anonymousWindow instanceof GlobalWindow) {
                sqlBuilder.append(_Constant.SPACE_OVER)
                        .append(_Constant.LEFT_PAREN)
                        .append(_Constant.RIGHT_PAREN);
            } else if (anonymousWindow != null) {
                sqlBuilder.append(_Constant.SPACE_OVER)
                        .append(anonymousWindow);
            } else if (!(this instanceof Window._AggregateWindowFunc)) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            return sqlBuilder.toString();
        }

        abstract void appendArguments(_SqlContext context);

        abstract void argumentToString(StringBuilder builder);

        final Expression windowEnd(final _Window anonymousWindow) {
            if (this.anonymousWindow == null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.anonymousWindow = anonymousWindow;
            return this;
        }

    }//AggregateOverClause

    private static final class NoArgFuncExpression extends OperationExpression implements ImmutableFunctionSpec {

        private final String name;

        private final TypeMeta returnType;

        private NoArgFuncExpression(String name, TypeMeta returnType) {
            this.name = name;
            this.returnType = returnType;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.returnType;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN)
                    .append(_Constant.RIGHT_PAREN);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NoArgFuncExpression) {
                final NoArgFuncExpression o = (NoArgFuncExpression) obj;
                match = o.name.equals(this.name) && o.returnType == this.returnType;
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN)
                    .append(_Constant.RIGHT_PAREN)
                    .toString();
        }


    }//NoArgFuncExpression


    static abstract class FunctionExpression extends OperationExpression
            implements FunctionSpec, MutableParamMetaSpec {

        private final String name;

        private TypeMeta returnType;

        FunctionExpression(String name, TypeMeta returnType) {
            this.name = name;
            this.returnType = returnType;
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.returnType;
        }

        @Override
        public void updateParamMeta(final TypeMeta paramMeta) {
            this.returnType = paramMeta;
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


    private static final class OneArgFunc extends FunctionExpression {

        private final ArmyExpression argument;

        private OneArgFunc(String name, ArmyExpression argument, TypeMeta returnType) {
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
                , TypeMeta returnType) {
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

    private static final class ComplexArgFunc extends OperationExpression
            implements FunctionSpec, MutableParamMetaSpec {

        private final String name;
        private final List<?> argList;

        private TypeMeta returnType;

        private ComplexArgFunc(String name, List<?> argList, TypeMeta returnType) {
            assert argList.size() > 1;
            this.name = name;
            this.argList = Collections.unmodifiableList(argList);
            this.returnType = returnType;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.returnType;
        }

        @Override
        public void updateParamMeta(final TypeMeta paramMeta) {
            this.returnType = paramMeta;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);

            for (Object o : this.argList) {
                if (o instanceof Expression) {
                    ((ArmyExpression) o).appendSql(context);
                } else if (o instanceof SQLWords) {
                    sqlBuilder.append(((SQLWords) o).render());
                } else if (o instanceof Clause) {
                    ((_SelfDescribed) o).appendSql(context);
                } else {
                    //no bug,never here
                    throw new IllegalStateException();
                }
            }//for

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);

            for (Object o : this.argList) {
                if (o instanceof Expression) {
                    builder.append(o);
                } else if (o instanceof SQLWords) {
                    builder.append(((SQLWords) o).render());
                } else if (o instanceof Clause) {
                    builder.append(o);
                } else {
                    //no bug,never here
                    throw new IllegalStateException();
                }
            }//for
            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//ComplexArgFunc


    private static final class MultiArgOptionFunc extends SQLFunctions.FunctionExpression {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        private final Clause clause;

        private MultiArgOptionFunc(String name, @Nullable SQLWords option
                , List<ArmyExpression> argList, @Nullable Clause clause
                , TypeMeta returnType) {
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
            , FunctionSpec, CriteriaContextSpec, MutableParamMetaSpec
            , Functions._FuncTypeUpdateClause {

        private final ArmyExpression caseValue;

        private final CriteriaContext criteriaContext;

        private List<_Pair<ArmyExpression, ArmyExpression>> expPairList;

        private ArmyExpression whenExpression;

        private ArmyExpression elseExpression;

        private TypeMeta returnType;

        private CaseFunc(@Nullable ArmyExpression caseValue) {
            this.caseValue = caseValue;
            this.criteriaContext = CriteriaContextStack.peek();
        }

        @Override
        public CriteriaContext getContext() {
            return this.criteriaContext;
        }

        @Override
        public TypeMeta typeMeta() {
            final TypeMeta returnType = this.returnType;
            if (returnType == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return returnType;
        }

        @Override
        public void updateParamMeta(final TypeMeta paramMeta) {
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
        public Functions._FuncTypeUpdateClause end() {
            final List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
            if (expPairList == null || expPairList.size() == 0) {
                String m = "case function at least one when clause";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else if (expPairList instanceof ArrayList) {
                this.expPairList = _CollectionUtils.unmodifiableList(expPairList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return this;
        }

    }//CaseFunc


    private static final class GlobalWindow implements _Window {

        private static final GlobalWindow INSTANCE = new GlobalWindow();

        private GlobalWindow() {
        }

        @Override
        public void appendSql(final _SqlContext context) {
            throw new UnsupportedOperationException();
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


    private static abstract class ConditionFunc<LR> extends OperationExpression
            implements Functions._FuncConditionClause<LR>
            , FunctionSpec
            , Functions._FuncLastArgClause
            , Statement._RightParenClause<Expression>
            , MutableParamMetaSpec {

        final CriteriaContext context;
        private final String name;

        private final Function<List<ArmyExpression>, TypeMeta> function;
        private TypeMeta returnType;

        private List<ArmyExpression> argList;

        private ConditionFunc(String name, @Nullable TypeMeta returnType) {
            this.context = CriteriaContextStack.peek();
            this.name = name;
            this.returnType = returnType;
            this.function = this::inferReturnType;
        }

        private ConditionFunc(String name, Function<List<ArmyExpression>, TypeMeta> function) {
            this.context = CriteriaContextStack.peek();
            this.name = name;
            this.returnType = null;
            this.function = function;
        }

        @Override
        public final TypeMeta typeMeta() {
            TypeMeta returnType = this.returnType;
            if (returnType == null) {
                final List<ArmyExpression> argList = this.argList;
                if (argList == null || argList instanceof ArrayList) {
                    throw CriteriaContextStack.castCriteriaApi(this.context);
                }
                this.returnType = returnType = this.function.apply(argList);
            }
            return returnType;
        }

        @Override
        public final void updateParamMeta(final TypeMeta paramMeta) {
            this.returnType = paramMeta;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final List<ArmyExpression> argList = this.argList;
            if (!(argList instanceof ArrayList) || argList.size() < 2) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);

            SQLFunctions.appendArguments(null, argList, null, context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }

        @Override
        public final LR leftParen(final @Nullable IPredicate condition) {
            if (condition == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            if (this.argList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            final List<ArmyExpression> argList = new ArrayList<>();
            argList.add((OperationPredicate) condition);
            this.argList = argList;
            return this.leftParenEnd();
        }

        @Override
        public final LR leftParen(Supplier<? extends IPredicate> supplier) {
            return this.leftParen(supplier.get());
        }

        @Override
        public final LR leftParen(Function<Object, ? extends IPredicate> operator, Supplier<?> supplier) {
            return this.leftParen(operator.apply(supplier.get()));
        }

        @Override
        public final LR leftParen(Function<Object, ? extends IPredicate> operator, Function<String, ?> function
                , String keyName) {
            return this.leftParen(operator.apply(function.apply(keyName)));
        }

        @Override
        public final LR leftParen(BiFunction<Object, Object, ? extends IPredicate> operator, Supplier<?> firstOperand
                , Supplier<?> secondOperand) {
            return this.leftParen(operator.apply(firstOperand.get(), secondOperand.get()));
        }

        @Override
        public final LR leftParen(BiFunction<Object, Object, ? extends IPredicate> operator, Function<String, ?> function
                , String firstKey, String secondKey) {
            return this.leftParen(operator.apply(function.apply(firstKey), function.apply(secondKey)));
        }

        @Override
        public final Statement._RightParenClause<Expression> comma(final @Nullable Expression expression) {
            if (expression == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            final List<ArmyExpression> argList = this.argList;
            if (!(argList instanceof ArrayList) || argList.size() == 0) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            argList.add((ArmyExpression) expression);
            return this;
        }

        @Override
        public final Statement._RightParenClause<Expression> comma(Supplier<? extends Expression> supplier) {
            return this.comma(supplier.get());
        }

        @Override
        public final Statement._RightParenClause<Expression> comma(Function<Object, ? extends Expression> operator
                , Supplier<?> supplier) {
            return this.comma(operator.apply(supplier.get()));
        }

        @Override
        public final Statement._RightParenClause<Expression> comma(Function<Object, ? extends Expression> operator
                , Function<String, ?> function, String keyName) {
            return this.comma(operator.apply(function.apply(keyName)));
        }

        @Override
        public final Statement._RightParenClause<Expression> comma(BiFunction<Object, Object, ? extends Expression> operator
                , Supplier<?> firstOperand, Supplier<?> secondOperand) {
            return this.comma(operator.apply(firstOperand.get(), secondOperand.get()));
        }

        @Override
        public final Statement._RightParenClause<Expression> comma(BiFunction<Object, Object, ? extends Expression> operator
                , Function<String, ?> function, String firstKey, String secondKey) {
            return this.comma(operator.apply(function.apply(firstKey), function.apply(secondKey)));
        }

        @Override
        public final Expression rightParen() {
            final List<ArmyExpression> argList = this.argList;
            if (!(argList instanceof ArrayList) || argList.size() < 2) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            this.argList = Collections.unmodifiableList(argList);
            return this;
        }

        abstract LR leftParenEnd();

        final Functions._FuncLastArgClause argBeforeLastEnd(final ArmyExpression arg) {
            final List<ArmyExpression> argList = this.argList;
            if (!(argList instanceof ArrayList) || argList.size() == 0) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            argList.add(arg);
            return this;
        }


        /**
         * @param argList a unmodified list
         */
        TypeMeta inferReturnType(List<ArmyExpression> argList) {
            throw new UnsupportedOperationException();
        }


    }//ConditionFunc


    private static class FuncCommaClause<CR> implements Functions._FuncCommaClause<CR> {

        final CriteriaContext context;

        private Function<ArmyExpression, CR> function;

        private FuncCommaClause(CriteriaContext context, Function<ArmyExpression, CR> function) {
            this.context = context;
            this.function = function;
        }

        @Override
        public final CR comma(final @Nullable Expression expression) {
            if (expression == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            return this.function.apply((ArmyExpression) expression);
        }

        @Override
        public final CR comma(Supplier<? extends Expression> supplier) {
            return this.comma(supplier.get());
        }

        @Override
        public final CR comma(Function<Object, ? extends Expression> operator, Supplier<?> supplier) {
            return this.comma(operator.apply(supplier.get()));
        }

        @Override
        public final CR comma(Function<Object, ? extends Expression> operator, Function<String, ?> function
                , String keyName) {
            return this.comma(operator.apply(function.apply(keyName)));
        }

        @Override
        public final CR comma(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand
                , Supplier<?> secondOperand) {
            return this.comma(operator.apply(firstOperand.get(), secondOperand.get()));
        }

        @Override
        public final CR comma(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function
                , String firstKey, String secondKey) {
            return this.comma(operator.apply(function.apply(firstKey), function.apply(secondKey)));
        }


    }//FuncCommaClause


    private static final class FuncSecondArgcClause extends FuncCommaClause<Functions._FuncLastArgClause>
            implements Functions._FuncSecondArgClause {

        private FuncSecondArgcClause(CriteriaContext context
                , Function<ArmyExpression, Functions._FuncLastArgClause> function) {
            super(context, function);
        }

    }//FuncSecondArgcClause

    private static final class ConditionTwoFunc extends ConditionFunc<Functions._FuncSecondArgClause>
            implements Functions._FuncConditionTowClause {

        private final BiFunction<MappingType, MappingType, MappingType> function;

        private ConditionTwoFunc(String name, BiFunction<MappingType, MappingType, MappingType> function) {
            super(name, (TypeMeta) null);
            this.function = function;
        }

        @Override
        TypeMeta inferReturnType(final List<ArmyExpression> argList) {
            if (argList.size() != 3) {
                throw CriteriaContextStack.castCriteriaApi(this.context);
            }
            return Functions._returnType(argList.get(1), argList.get(2), this.function);
        }

        @Override
        Functions._FuncSecondArgClause leftParenEnd() {
            return new FuncSecondArgcClause(this.context, this::argBeforeLastEnd);
        }

    }//ThreeConditionFunc


}
