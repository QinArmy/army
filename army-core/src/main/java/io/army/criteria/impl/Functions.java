package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.meta.TypeMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is util class used to create standard sql element :
 * <ul>
 *     <li>statement parameter</li>
 *     <li>sql literal</li>
 *     <li>standard sql function</li>
 * </ul>
 * </p>
 *
 * @see SQLs
 */
abstract class Functions {

    /**
     * package constructor,forbid application developer directly extend this util class.
     */
    Functions() {
        throw new UnsupportedOperationException();
    }


    interface _NullTreatmentClause<NR> {

        NR respectNulls();

        NR ignoreNulls();

    }

    public interface _FromFirstLastClause<FR> {
        FR fromFirst();

        FR fromLast();

    }

    public interface _FuncTypeUpdateClause extends TypeInfer.TypeUpdateSpec {

        @Override
        Expression asType(TypeMeta paramMeta);

    }

    public interface _CaseEndClause {

        _FuncTypeUpdateClause end();

    }

    public interface _CaseElseClause extends _CaseEndClause {
        _CaseEndClause elseExp(Expression expression);

        _CaseEndClause elseExp(Supplier<? extends Expression> supplier);

        _CaseEndClause elseExp(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseEndClause elseExp(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseEndClause elseExp(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseEndClause elseExp(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

        _CaseEndClause ifElse(Supplier<? extends Expression> supplier);

        _CaseEndClause ifElse(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseEndClause ifElse(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseEndClause ifElse(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseEndClause ifElse(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);


    }

    public interface _CaseThenClause {

        _CaseWhenSpec then(Expression expression);

        _CaseWhenSpec then(Supplier<? extends Expression> supplier);

        _CaseWhenSpec then(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseWhenSpec then(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseWhenSpec then(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseWhenSpec then(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);


    }


    public interface _CaseWhenClause {

        _CaseThenClause when(Expression expression);

        _CaseThenClause when(Supplier<? extends Expression> supplier);

        _CaseThenClause when(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseThenClause when(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseThenClause when(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseThenClause when(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

        _CaseThenClause ifWhen(Supplier<? extends Expression> supplier);

        _CaseThenClause ifWhen(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        _CaseThenClause ifWhen(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        _CaseThenClause ifWhen(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        _CaseThenClause ifWhen(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);


    }

    public interface _CaseWhenSpec extends _CaseWhenClause, _CaseElseClause {

    }


    public interface _FuncCommaClause<CR> {

        CR comma(Expression expression);

        CR comma(Supplier<? extends Expression> supplier);

        CR comma(Function<Object, ? extends Expression> operator, Supplier<?> supplier);

        CR comma(Function<Object, ? extends Expression> operator, Function<String, ?> function, String keyName);

        CR comma(BiFunction<Object, Object, ? extends Expression> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        CR comma(BiFunction<Object, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

    }

    public interface _FuncLastArgClause extends Functions._FuncCommaClause<Statement._RightParenClause<Expression>> {

    }

    public interface _FuncSecondArgClause extends Functions._FuncCommaClause<_FuncLastArgClause> {

    }

    public interface _FuncConditionClause<LR> {

        LR leftParen(IPredicate condition);

        LR leftParen(Supplier<? extends IPredicate> supplier);

        LR leftParen(Function<Object, ? extends IPredicate> operator, Supplier<?> supplier);

        LR leftParen(Function<Object, ? extends IPredicate> operator, Function<String, ?> function, String keyName);

        LR leftParen(BiFunction<Object, Object, ? extends IPredicate> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        LR leftParen(BiFunction<Object, Object, ? extends IPredicate> operator, Function<String, ?> function, String firstKey, String secondKey);

    }

    public interface _FuncConditionTowClause extends _FuncConditionClause<_FuncSecondArgClause> {

    }




    /*################################## blow number function method ##################################*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_abs">ABS(X)</a>
     */
    public static Expression abs(final @Nullable Object expr) {
        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        return SQLFunctions.oneArgOptionFunc("ABS", null, expression, null, expression.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_acos">ACOS(X)</a>
     */
    public static Expression acos(final @Nullable Object expr) {
        return SQLFunctions.oneArgOptionFunc("ACOS", null, SQLs._funcParam(expr), null, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_asin">ASIN(X)</a>
     */
    public static Expression asin(final @Nullable Object expr) {
        return SQLFunctions.oneArgOptionFunc("ASIN", null, SQLs._funcParam(expr), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_atan">ATAN(X)</a>
     */
    public static Expression atan(final @Nullable Object expr) {
        return SQLFunctions.oneArgOptionFunc("ATAN", null, SQLs._funcParam(expr), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_atan2">ATAN(X,y)</a>
     */
    public static Expression atan(final @Nullable Object expr1, final @Nullable Object expr2) {
        final List<ArmyExpression> argList;
        argList = Arrays.asList(SQLs._funcParam(expr1), SQLs._funcParam(expr2));
        return SQLFunctions.safeMultiArgOptionFunc("ATAN", null, argList, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_ceil">CEIL(X)</a>
     */
    public static Expression cell(final @Nullable Object expr) {
        return SQLFunctions.oneArgOptionFunc("CELL", null, SQLs._funcParam(expr), null, LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} or expr
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_conv">CONV(X)</a>
     */
    public static Expression conv(final @Nullable Object expr, final Object fromBase, final Object toBase) {
        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        final List<ArmyExpression> argList;
        argList = Arrays.asList(expression, SQLs._funcLiteral(fromBase), SQLs._funcLiteral(toBase));

        final TypeMeta returnType;
        if (expression instanceof SQLs.NullWord) {
            returnType = _NullType.INSTANCE;
        } else {
            returnType = expression.typeMeta();
        }
        return SQLFunctions.safeMultiArgOptionFunc("CONV", null, argList, null, returnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_cos">COS(X)</a>
     */
    public static Expression cos(final @Nullable Object expr) {
        return SQLFunctions.oneArgOptionFunc("COS", null, SQLs._funcParam(expr), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_cot">COT(X)</a>
     */
    public static Expression cot(final @Nullable Object expr) {
        return SQLFunctions.oneArgOptionFunc("COT", null, SQLs._funcParam(expr), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_crc32">CRC32(expr)</a>
     */
    public static Expression crc32(final @Nullable Object expr) {
        return SQLFunctions.oneArgOptionFunc("CRC32", null, SQLs._funcParam(expr), null, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_degrees">DEGREES(x)</a>
     */
    public static Expression degrees(final @Nullable Object expr) {
        return SQLFunctions.oneArgOptionFunc("DEGREES", null, SQLs._funcParam(expr), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_exp">EXP(x)</a>
     */
    public static Expression exp(final @Nullable Object expr) {
        return SQLFunctions.oneArgOptionFunc("EXP", null, SQLs._funcParam(expr), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_floor">FLOOR(x)</a>
     */
    public static Expression floor(final @Nullable Object expr) {
        return SQLFunctions.oneArgOptionFunc("FLOOR", null, SQLs._funcParam(expr), null, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  StringType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_format">FORMAT(x,d)</a>
     */
    public static Expression format(final @Nullable Object x, final Object d) {
        final List<ArmyExpression> argList;
        argList = Arrays.asList(SQLs._funcParam(x), SQLs._funcLiteral(d));
        return SQLFunctions.safeMultiArgOptionFunc("FORMAT", null, argList, null, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  StringType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_hex">HEX(n_or_s)</a>
     */
    public static Expression hex(final @Nullable Object numOrStr) {
        return SQLFunctions.oneArgOptionFunc("HEX", null, SQLs._funcParam(numOrStr), null, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_ln">LN(x)</a>
     */
    public static Expression ln(final @Nullable Object x) {
        return SQLFunctions.oneArgOptionFunc("LN", null, SQLs._funcParam(x), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log">LOG(x)</a>
     */
    public static Expression log(final @Nullable Object x) {
        return SQLFunctions.oneArgOptionFunc("LOG", null, SQLs._funcParam(x), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log">LOG(x)</a>
     */
    public static Expression log(final @Nullable Object b, final @Nullable Object x) {
        final List<ArmyExpression> argList;
        argList = Arrays.asList(SQLs._funcLiteral(b), SQLs._funcParam(x));
        return SQLFunctions.safeMultiArgOptionFunc("LOG", null, argList, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log2">LOG2(x)</a>
     */
    public static Expression log2(final @Nullable Object x) {
        return SQLFunctions.oneArgOptionFunc("LOG2", null, SQLs._funcParam(x), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log10">LOG10(x)</a>
     */
    public static Expression log10(final @Nullable Object x) {
        return SQLFunctions.oneArgOptionFunc("LOG10", null, SQLs._funcParam(x), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} or n.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log10">LOG10(x)</a>
     */
    public static Expression mod(final @Nullable Object n, final @Nullable Object m) {
        final ArmyExpression nExp;
        nExp = SQLs._funcParam(n);
        final List<ArmyExpression> argList;
        argList = Arrays.asList(nExp, SQLs._funcParam(m));
        return SQLFunctions.safeMultiArgOptionFunc("MOD", null, argList, null, nExp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_pi">PI()</a>
     */
    public static Expression pi() {
        return SQLFunctions.noArgFunc("PI", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_pi">PI()</a>
     */
    public static Expression pow(final @Nullable Object x, final @Nullable Object y) {
        final List<ArmyExpression> argList;
        argList = Arrays.asList(SQLs._funcParam(x), SQLs._funcParam(y));
        return SQLFunctions.safeMultiArgOptionFunc("POW", null, argList, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_radians">RADIANS(x)</a>
     */
    public static Expression radians(final @Nullable Object x) {
        return SQLFunctions.oneArgOptionFunc("RADIANS", null, SQLs._funcParam(x), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_rand">RAND([N])</a>
     */
    public static Expression rand() {
        return SQLFunctions.noArgFunc("RAND", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_rand">RAND([N])</a>
     */
    public static Expression rand(final @Nullable Object n) {
        return SQLFunctions.oneArgOptionFunc("RAND", null, SQLs._funcParam(n), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_round">ROUND(x)</a>
     */
    public static Expression round(final @Nullable Object x) {
        return SQLFunctions.oneArgOptionFunc("ROUND", null, SQLs._funcParam(x), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_round">ROUND(x,d)</a>
     */
    public static Expression round(final @Nullable Object x, @Nullable final Object d) {
        final List<ArmyExpression> argList;
        argList = Arrays.asList(SQLs._funcParam(x), SQLs._funcParam(d));
        return SQLFunctions.safeMultiArgOptionFunc("ROUND", null, argList, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sign">SIGN(x)</a>
     */
    public static Expression sign(final @Nullable Object x) {
        return SQLFunctions.oneArgOptionFunc("SIGN", null, SQLs._funcParam(x), null, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sin">SIN(x)</a>
     */
    public static Expression sin(final @Nullable Object x) {
        return SQLFunctions.oneArgOptionFunc("SIN", null, SQLs._funcParam(x), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sqrt">SQRT(x)</a>
     */
    public static Expression sqrt(final @Nullable Object x) {
        return SQLFunctions.oneArgOptionFunc("SQRT", null, SQLs._funcParam(x), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_tan">TAN(x)</a>
     */
    public static Expression tan(final @Nullable Object x) {
        return SQLFunctions.oneArgOptionFunc("TAN", null, SQLs._funcParam(x), null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_truncate">TRUNCATE(x,d)</a>
     */
    public static Expression truncate(final @Nullable Object x, @Nullable final Object d) {
        final List<ArmyExpression> argList;
        argList = Arrays.asList(SQLs._funcParam(x), SQLs._funcParam(d));
        return SQLFunctions.safeMultiArgOptionFunc("TRUNCATE", null, argList, null, DoubleType.INSTANCE);
    }



    /*################################## blow date time function method ##################################*/

    public static Expression now() {
        throw new UnsupportedOperationException();
    }


    public static _CaseWhenClause caseFunc() {
        return SQLFunctions.caseFunc(null);
    }


    /**
     * package class
     */
    static abstract class CaseValueFunctions extends Functions {

        /**
         * package constructor
         */
        CaseValueFunctions() {
        }

        /**
         * @param expression non-null {@link Expression} ,if null then use CASE WHEN condition THEN result syntax
         *                   ,else use CASE value WHEN compare_value THEN result syntax.
         * @see #caseFunc(Supplier)
         * @see #caseFunc(Function, Supplier)
         * @see #caseFunc(Function, Function, String)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseFunc(final Expression expression) {
            Objects.requireNonNull(expression);
            return SQLFunctions.caseFunc(expression);
        }

        /**
         * @param supplier supplier of nullable {@link Expression},if null then use CASE WHEN condition THEN result syntax
         *                 ,else use CASE value WHEN compare_value THEN result syntax.
         * @see #caseFunc(Expression)
         * @see #caseFunc(Function, Supplier)
         * @see #caseFunc(Function, Function, String)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseFunc(Supplier<? extends Expression> supplier) {
            final Expression caseValue;
            if ((caseValue = supplier.get()) == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            return SQLFunctions.caseFunc(caseValue);
        }

        /**
         * @param supplier supplier of non-null parameter,if null then use CASE WHEN condition THEN result syntax
         *                 ,else use CASE value WHEN compare_value THEN result syntax.
         * @see #caseFunc(Expression)
         * @see #caseFunc(Supplier)
         * @see #caseFunc(Function, Function, String)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseFunc(Function<Object, ? extends Expression> operator, Supplier<?> supplier) {
            final Object value;
            if ((value = supplier.get()) == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            final Expression caseValue;
            if ((caseValue = operator.apply(value)) == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            return SQLFunctions.caseFunc(caseValue);
        }

        /**
         * <p>
         * <pre>
         *          <code><br/>
         *              public void caseFunc(Map&lt;String,Object> criteria){
         *                   final Select stmt;
         *                   stmt = MySQLs.query(criteria)
         *                            .select(this::simpleCaseFunc)
         *                            .from(Numbers_.T, "u")
         *                            .asQuery();
         *                   printStmt(stmt);
         *              }
         *
         *               private void simpleCaseFunc(Map&lt;String,Object> criteria,Consumer<SelectItem> consumer) {
         *                   Selection selection;
         *                   selection = MySQLs.caseFunc(Numbers_.number::plusLiteral,criteria,"number")
         *                           .when(SQLs.literal(88))
         *                           .then(SQLs.literal(1))
         *
         *                           .when(SQLs.literal(66))
         *                           .then(SQLs.literal(2))
         *
         *                           .when(SQLs.literal(99))
         *                           .then(SQLs.literal(3))
         *
         *                           .elseExp(SQLs.literal(0))
         *
         *                           .end()
         *                           //.asType(StringType.INSTANCE)
         *                           .as("result");
         *
         *                           consumer.accept(selection);
         *                }
         *
         *          </code>
         *     </pre>
         * </p>
         *
         * @param function {@link Function#apply(Object keyName)} return non-null parameter
         *                 ,use CASE value WHEN compare_value THEN result syntax.
         * @param keyName  pass to {@link Function#apply(Object)} of function
         * @throws NullPointerException throw when function return null or operator return null.
         * @see #caseFunc(Expression)
         * @see #caseFunc(Supplier)
         * @see #caseFunc(Function, Supplier)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseFunc(Function<Object, ? extends Expression> operator
                , Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            final Expression caseValue;
            caseValue = operator.apply(value);
            if (caseValue == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            return SQLFunctions.caseFunc(caseValue);
        }

        /**
         * @param supplier supplier of nullable {@link Expression},if null then use CASE WHEN condition THEN result syntax
         *                 ,else use CASE value WHEN compare_value THEN result syntax.
         * @see #caseFunc(Expression)
         * @see #caseFunc(Function, Supplier)
         * @see #caseFunc(Function, Function, String)
         * @see #caseIf(Function, Supplier)
         * @see #caseIf(Function, Function, String)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseIf(Supplier<? extends Expression> supplier) {
            return SQLFunctions.caseFunc(supplier.get());
        }

        /**
         * <p>
         * <pre>
         *          <code><br/>
         *              public void caseFunc(Criteria criteria){
         *                   final Select stmt;
         *                   stmt = MySQLs.query(criteria)
         *                            .select(this::simpleCaseFunc)
         *                            .from(Numbers_.T, "u")
         *                            .asQuery();
         *                   printStmt(stmt);
         *              }
         *
         *               private void simpleCaseFunc(Criteria criteria,Consumer<SelectItem> consumer) {
         *                   Selection selection;
         *                   selection = MySQLs.caseFunc(Numbers_.number::plusLiteral,criteria::getNumber)
         *                           .when(SQLs.literal(88))
         *                           .then(SQLs.literal(1))
         *
         *                           .when(SQLs.literal(66))
         *                           .then(SQLs.literal(2))
         *
         *                           .when(SQLs.literal(99))
         *                           .then(SQLs.literal(3))
         *
         *                           .elseExp(SQLs.literal(0))
         *
         *                           .end()
         *                           //.asType(StringType.INSTANCE)
         *                           .as("result");
         *
         *                           consumer.accept(selection);
         *                }
         *
         *          </code>
         *     </pre>
         * </p>
         *
         * @param supplier supplier of nullable parameter,if null then use CASE WHEN condition THEN result syntax
         *                 ,else use CASE value WHEN compare_value THEN result syntax.
         * @throws NullPointerException throw when operator return null.
         * @throws CriteriaException    throw when invoking this method in non-statement context.
         * @see #caseFunc(Expression)
         * @see #caseFunc(Supplier)
         * @see #caseFunc(Function, Function, String)
         * @see #caseIf(Supplier)
         * @see #caseIf(Function, Function, String)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseIf(Function<Object, ? extends Expression> operator, Supplier<?> supplier) {
            final Object value;
            final Expression caseValue;
            if ((value = supplier.get()) == null) {
                caseValue = null;
            } else if ((caseValue = operator.apply(value)) == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            return SQLFunctions.caseFunc(caseValue);
        }

        /**
         * <p>
         * <pre>
         *          <code><br/>
         *              public void caseFunc(Map&lt;String,Object> criteria){
         *                   final Select stmt;
         *                   stmt = MySQLs.query(criteria)
         *                            .select(this::simpleCaseFunc)
         *                            .from(Numbers_.T, "u")
         *                            .asQuery();
         *                   printStmt(stmt);
         *              }
         *
         *               private void simpleCaseFunc(Map&lt;String,Object> criteria,Consumer<SelectItem> consumer) {
         *                   Selection selection;
         *                   selection = MySQLs.caseFunc(Numbers_.number::plusLiteral,criteria,"number")
         *                           .when(SQLs.literal(88))
         *                           .then(SQLs.literal(1))
         *
         *                           .when(SQLs.literal(66))
         *                           .then(SQLs.literal(2))
         *
         *                           .when(SQLs.literal(99))
         *                           .then(SQLs.literal(3))
         *
         *                           .elseExp(SQLs.literal(0))
         *
         *                           .end()
         *                           //.asType(StringType.INSTANCE)
         *                           .as("result");
         *
         *                           consumer.accept(selection);
         *                }
         *
         *          </code>
         *     </pre>
         * </p>
         *
         * @param function {@link Function#apply(Object keyName)} return nullable parameter,if null then use CASE WHEN condition THEN result syntax
         *                 ,else use CASE value WHEN compare_value THEN result syntax.
         * @param keyName  pass to {@link Function#apply(Object)} of function
         * @throws NullPointerException throw when operator return null.
         * @throws CriteriaException    throw when invoking this method in non-statement context.
         * @see #caseFunc(Expression)
         * @see #caseFunc(Supplier)
         * @see #caseFunc(Function, Supplier)
         * @see #caseFunc(Function, Function, String)
         * @see #caseIf(Supplier)
         * @see #caseIf(Function, Supplier)
         * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#operator_case">case function</a>
         */
        public static _CaseWhenClause caseIf(Function<Object, ? extends Expression> operator
                , Function<String, ?> function, String keyName) {
            final Object value;
            final Expression caseValue;
            if ((value = function.apply(keyName)) == null) {
                caseValue = null;
            } else if ((caseValue = operator.apply(value)) == null) {
                throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
            }
            return SQLFunctions.caseFunc(caseValue);
        }


    }

    /*################################## blow static inner class  ##################################*/


    /*-------------------below package method -------------------*/

    static TypeMeta _returnType(final ArmyExpression keyExpr, final ArmyExpression valueExpr
            , BiFunction<MappingType, MappingType, MappingType> function) {
        final TypeMeta keyType, valueType;
        keyType = keyExpr.typeMeta();
        valueType = valueExpr.typeMeta();
        final TypeMeta returnType;
        if (keyExpr instanceof SQLs.NullWord && valueExpr instanceof SQLs.NullWord) {
            returnType = _NullType.INSTANCE;
        } else if (keyType instanceof TypeMeta.Delay || valueType instanceof TypeMeta.Delay) {
            returnType = CriteriaSupports.delayParamMeta(keyType, valueType, function);
        } else {
            returnType = function.apply(keyType.mappingType(), valueType.mappingType());
        }
        return returnType;
    }

    static TypeMeta _returnType(ArmyExpression expression, Function<MappingType, MappingType> function) {
        final TypeMeta exprType, returnType;
        exprType = expression.typeMeta();
        if (expression instanceof SQLs.NullWord) {
            returnType = _NullType.INSTANCE;
        } else if (exprType instanceof TypeMeta.Delay) {
            returnType = CriteriaSupports.delayParamMeta((TypeMeta.Delay) exprType, function);
        } else {
            returnType = function.apply(exprType.mappingType());
        }
        return returnType;
    }

    static List<Object> _createSimpleMultiArgList(final List<Expression> expList) {
        final int expSize = expList.size();
        assert expSize > 1;
        final List<Object> argList = new ArrayList<>((expSize << 1) - 1);
        for (int i = 0; i < expSize; i++) {
            if (i > 0) {
                argList.add(SQLFunctions.FuncWord.COMMA);
            }
            argList.add(expList.get(i));
        }
        return argList;
    }


    static Expression _simpleTowArgFunc(final String name, final Expression g1
            , final Expression g2, final TypeMeta returnType) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(g1);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(g2);
        return SQLFunctions.safeComplexArgFunc(name, argList, returnType);
    }

    static Expression _simpleMaxThreeArgFunc(final String name, final List<Expression> expList
            , final TypeMeta returnType) {
        final Expression func;
        switch (expList.size()) {
            case 1:
                func = SQLFunctions.oneArgFunc(name, expList.get(0), returnType);
                break;
            case 2:
            case 3:
                func = SQLFunctions.safeComplexArgFunc(name, _createSimpleMultiArgList(expList), returnType);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    static Expression _simpleMaxTwoArgFunc(final String name, final List<Expression> expList
            , final TypeMeta returnType) {
        final Expression func;
        switch (expList.size()) {
            case 1:
                func = SQLFunctions.oneArgFunc(name, expList.get(0), returnType);
                break;
            case 2:
                func = SQLFunctions.safeComplexArgFunc(name, _createSimpleMultiArgList(expList), returnType);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }




    /*-------------------below private method -------------------*/


}
