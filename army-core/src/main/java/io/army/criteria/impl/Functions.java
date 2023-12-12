package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.standard.SQLFunction;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.*;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.BooleanSupplier;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * <p>
 * This class is util class used to create standard sql element :
 * <ul>
 *     <li>statement parameter</li>
 *     <li>sql literal</li>
 *     <li>standard sql function</li>
 * </ul>
*
 * @see SQLs
 */
@SuppressWarnings("unused")
abstract class Functions {


    /**
     * package constructor,forbid application developer directly extend this util class.
     */
    Functions() {
        throw new UnsupportedOperationException();
    }

    public interface _WithOrdinalityClause {

        _TabularFunction withOrdinality();

        _TabularFunction ifWithOrdinality(BooleanSupplier predicate);

    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/queries-table-expressions.html#QUERIES-TABLEFUNCTIONS"> Table Functions<br/>
     * </a>
     */
    public interface _TabularFunction extends DerivedTable, SQLFunction {

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/queries-table-expressions.html#QUERIES-TABLEFUNCTIONS"> Table Functions<br/>
     * </a>
     */
    public interface _ColumnFunction extends _TabularFunction, SelectionSpec, TypeInfer.TypeUpdateSpec {

        @Override
        SelectionSpec mapTo(TypeMeta typeMeta);

    }

    public interface _TabularWithOrdinalityFunction extends _TabularFunction, _WithOrdinalityClause {

    }


    public interface _ColumnWithOrdinalityFunction extends _ColumnFunction, _TabularWithOrdinalityFunction {

    }

    interface _NullTreatmentClause<NR> {

        NR respectNulls();

        NR ignoreNulls();

        NR ifRespectNulls(BooleanSupplier predicate);

        NR ifIgnoreNulls(BooleanSupplier predicate);

    }

    public interface _FromFirstLastClause<FR> {
        FR fromFirst();

        FR fromLast();

        FR ifFromFirst(BooleanSupplier predicate);

        FR ifFromLast(BooleanSupplier predicate);

    }


    enum FuncWord implements SQLs.ArmyKeyWord {

        INTERVAL(" INTERVAL"),
        COMMA(_Constant.SPACE_COMMA),
        USING(_Constant.SPACE_USING),
        AT_TIME_ZONE(" AT TIME ZONE"),
        LEFT_PAREN(_Constant.SPACE_LEFT_PAREN),
        RIGHT_PAREN(_Constant.SPACE_RIGHT_PAREN);

        private final String spaceWords;

        FuncWord(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return String.format("%s.%s", FuncWord.class.getSimpleName(), this.name());
        }


    }//Word


    public static SQLFunction._CaseFuncWhenClause cases() {
        return FunctionUtils.caseFunction(null);
    }

    public static SQLFunction._CaseFuncWhenClause cases(Expression expression) {
        ContextStack.assertNonNull(expression);
        return FunctionUtils.caseFunction(expression);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  MappingType} of expr
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_abs">ABS(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">ABS(numeric_type)</a>
     */
    public static SimpleExpression abs(final Expression expr) {
        return FunctionUtils.oneArgFunc("ABS", expr, expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_acos">ACOS(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">acos ( double precision ) → double precision</a>
     */
    public static SimpleExpression acos(final Expression expr) {
        return FunctionUtils.oneArgFunc("ACOS", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_asin">ASIN(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">asin ( double precision ) → double precision</a>
     */
    public static SimpleExpression asin(final Expression expr) {
        return FunctionUtils.oneArgFunc("ASIN", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_atan">ATAN(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">atan ( double precision ) → double precision</a>
     */
    public static SimpleExpression atan(final Expression expr) {
        return FunctionUtils.oneArgFunc("ATAN", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_atan2">ATAN(X,y)</a>
     */
    public static SimpleExpression atan(final Expression x, final Expression y) {
        return FunctionUtils.twoArgFunc("ATAN", x, y, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>if {@link MappingType} of exp is number type,then {@link MappingType} of exp </li>
     *     <li>else {@link BigDecimalType} </li>
     * </ul>
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_ceil">CEIL(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">ceil ( numeric ) → numeric,ceil ( double precision ) → double precision</a>
     */
    public static SimpleExpression ceil(final Expression exp) {
        return FunctionUtils.oneArgFunc("CEIL", exp, _returnType(exp, Functions::_numberOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} or expr
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_conv">CONV(X)</a>
     */
    public static SimpleExpression conv(final Expression expr, final Expression fromBase, final Expression toBase) {
        return FunctionUtils.threeArgFunc("CONV", expr, fromBase, toBase, expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_cos">COS(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">cos ( double precision ) → double precision</a>
     */
    public static SimpleExpression cos(final Expression expr) {
        return FunctionUtils.oneArgFunc("COS", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_cot">COT(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">cot ( double precision ) → double precision</a>
     */
    public static SimpleExpression cot(final Expression expr) {
        return FunctionUtils.oneArgFunc("COT", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_crc32">CRC32(expr)</a>
     */
    public static SimpleExpression crc32(final Expression expr) {
        return FunctionUtils.oneArgFunc("CRC32", expr, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_degrees">DEGREES(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">degrees ( double precision )</a>
     */
    public static SimpleExpression degrees(final Expression expr) {
        return FunctionUtils.oneArgFunc("DEGREES", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_exp">EXP(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">exp ( numeric )</a>
     */
    public static SimpleExpression exp(final Expression exp) {
        return FunctionUtils.oneArgFunc("EXP", exp, _returnType(exp, Functions::_doubleOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_floor">FLOOR(x)</a>
     */
    public static SimpleExpression floor(final Expression expr) {
        return FunctionUtils.oneArgFunc("FLOOR", expr, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  StringType}
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_format">FORMAT(x,d)</a>
     */
    public static SimpleExpression format(final Expression x, final Expression d) {
        return FunctionUtils.twoArgFunc("FORMAT", x, d, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  StringType}
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_hex">HEX(n_or_s)</a>
     */
    public static SimpleExpression hex(final Expression numOrStr) {
        return FunctionUtils.oneArgFunc("HEX", numOrStr, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_ln">LN(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">ln ( numeric ) → numeric,ln ( double precision ) → double precision</a>
     */
    public static SimpleExpression ln(final Expression x) {
        return FunctionUtils.oneArgFunc("LN", x, _returnType(x, Functions::_doubleOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log">LOG(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">log ( numeric ) → numeric,log ( double precision ) → double precision</a>
     */
    public static SimpleExpression log(final Expression x) {
        return FunctionUtils.oneArgFunc("LOG", x, _returnType(x, Functions::_doubleOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BigDecimalType}
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log">LOG(B,X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">log ( b numeric, x numeric ) → numeric</a>
     */
    public static SimpleExpression log(final Expression b, final Expression x) {
        return FunctionUtils.twoArgFunc("LOG", b, x, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log10">LOG10(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">log10 ( numeric ) → numeric,log10 ( double precision ) → double precision</a>
     */
    public static SimpleExpression log10(final Expression x) {
        return FunctionUtils.oneArgFunc("LOG10", x, _returnType(x, Functions::_doubleOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of n.
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_mod">MOD(N,M), N % M, N MOD M</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">mod ( y numeric_type, x numeric_type ) → numeric_type</a>
     */
    public static SimpleExpression mod(final Expression n, final Expression m) {
        return FunctionUtils.twoArgFunc("MOD", n, m, n.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_pi">PI()</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">pi ( ) → double precision</a>
     */
    public static SimpleExpression pi() {
        return FunctionUtils.zeroArgFunc("PI", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link MappingType} of x
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_pow">POW(x,y)</a>
     */
    public static SimpleExpression pow(final Expression x, final Expression y) {
        return FunctionUtils.twoArgFunc("POW", x, y, x.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_radians">RADIANS(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">radians ( double precision ) → double precision</a>
     */
    public static SimpleExpression radians(final Expression x) {
        return FunctionUtils.oneArgFunc("RADIANS", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_rand">RAND([N])</a>
     */
    public static SimpleExpression rand() {
        return FunctionUtils.zeroArgFunc("RAND", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_rand">RAND([N])</a>
     */
    public static SimpleExpression rand(final Expression n) {
        return FunctionUtils.oneArgFunc("RAND", n, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is number type,then {@link MappingType} of exp</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_round">ROUND(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">round ( numeric ) → numeric</a>
     */
    public static SimpleExpression round(final Expression x) {
        return FunctionUtils.oneArgFunc("ROUND", x, _returnType(x, Functions::_numberOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType} .
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_round">ROUND(x,d)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">round ( v numeric, s integer ) → numeric</a>
     */
    public static SimpleExpression round(final Expression x, final Expression d) {
        return FunctionUtils.twoArgFunc("ROUND", x, d, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType} .
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sign">SIGN(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">sign ( numeric ) → numeric</a>
     */
    public static SimpleExpression sign(final Expression x) {
        return FunctionUtils.oneArgFunc("SIGN", x, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sin">SIN(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">sin ( numeric ) → numeric</a>
     */
    public static SimpleExpression sin(final Expression x) {
        return FunctionUtils.oneArgFunc("SIN", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sqrt">SQRT(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">sqrt ( numeric ) → numeric,sqrt ( double precision ) → double precision</a>
     */
    public static SimpleExpression sqrt(final Expression x) {
        return FunctionUtils.oneArgFunc("SQRT", x, _returnType(x, Functions::_doubleOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_tan">TAN(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">tan ( numeric ) → numeric</a>
     */
    public static SimpleExpression tan(final Expression x) {
        return FunctionUtils.oneArgFunc("TAN", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_truncate">TRUNCATE(x,d)</a>
     */
    public static SimpleExpression truncate(final Expression x, final Expression d) {
        return FunctionUtils.twoArgFunc("TRUNCATE", x, d, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * This function is standard sql92 functions
     *
     * <p>
     * The {@link MappingType} of function return type:the {@link  MappingType} of expr1
     *
     *
     * @throws CriteriaException throw when any arg is multi-value expression
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#function_nullif">NULLIF(expr1,expr2)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-NULLIF">NULLIF(expr1,expr2)</a>
     */
    public static SimpleExpression nullIf(final Expression expr1, final Expression expr2) {
        return FunctionUtils.twoArgFunc("NULLIF", expr1, expr2, _returnType(expr1, Expressions::identityType));
    }

    /**
     * <p>
     * standard sql92 functions,The {@link MappingType} of function return type: {@link IntegerType} .
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_length">LENGTH(str)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">length ( text ) → integer</a>
     */
    public static SimpleExpression length(Expression exp) {
        return FunctionUtils.oneArgFunc("LENGTH", exp, IntegerType.INSTANCE);
    }

    public static SimpleExpression countAsterisk() {
        return CountAsteriskFunction.INSTANCE;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     *
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static SimpleExpression count(Expression expr) {
        return FunctionUtils.oneArgFunc("COUNT", expr, LongType.INSTANCE);
    }




    /*################################## blow number function method ##################################*/



    /*-------------------below Aggregate Function-------------------*/

    public static SimpleExpression min(Expression exp) {
        return FunctionUtils.oneArgFunc("min", exp, _returnType(exp, Expressions::identityType));
    }

    public static SimpleExpression max(Expression exp) {
        return FunctionUtils.oneArgFunc("max", exp, _returnType(exp, Expressions::identityType));
    }

    public static SimpleExpression sum(Expression exp) {
        return FunctionUtils.oneArgFunc("sum", exp, _returnType(exp, Functions::_sumType));
    }



    /*################################## blow date time function method ##################################*/


    /*-------------------below custom function -------------------*/


    public static SimpleExpression myFunc(String name, TypeMeta returnType) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.zeroArgFunc(name, returnType);
    }

    public static IPredicate myFunc(String name) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.zeroArgFuncPredicate(name);
    }

    public static SimpleExpression myFunc(String name, Expression expr, TypeMeta returnType) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.oneArgFunc(name, expr, returnType);
    }

    public static IPredicate myFunc(String name, Expression expr) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.oneArgPredicateFunc(name, expr);
    }

    public static SimpleExpression myFunc(String name, Expression expr1, Expression expr2, TypeMeta returnType) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.twoArgFunc(name, expr1, expr2, returnType);
    }

    public static IPredicate myFunc(String name, Expression expr1, Expression expr2) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.twoArgPredicateFunc(name, expr1, expr2);
    }

    public static SimpleExpression myFunc(String name, List<Expression> expList, TypeMeta returnType) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList), returnType);
    }

    public static IPredicate myFunc(String name, List<Expression> expList) {
        if (!FUN_NAME_PATTER.matcher(name).matches()) {
            throw _customFuncNameError(name);
        }
        return FunctionUtils.complexArgPredicate(name, _createSimpleMultiArgList(expList));
    }

    static final Pattern FUN_NAME_PATTER = Pattern.compile("^[_a-zA-Z][_\\w]*$");


    static CriteriaException _customFuncNameError(String name) {
        String m = String.format("custom function name[%s] error.", name);
        return ContextStack.criteriaError(ContextStack.peek(), m);
    }


    /*################################## blow static inner class  ##################################*/


    /*-------------------below package method -------------------*/


    static TypeMeta _returnType(final Expression left, final Expression right,
                                final BinaryOperator<MappingType> function) {
        TypeMeta leftType, rightType;
        leftType = left.typeMeta();
        rightType = right.typeMeta();

        if (!(leftType instanceof MappingType)) {
            leftType = leftType.mappingType();
        }
        if (!(rightType instanceof MappingType)) {
            rightType = rightType.mappingType();
        }
        return function.apply((MappingType) leftType, (MappingType) rightType);
    }

    static TypeMeta _returnType(final Expression exp, final UnaryOperator<MappingType> function) {
        TypeMeta expType;
        expType = exp.typeMeta();
        if (!(expType instanceof MappingType)) {
            expType = expType.mappingType();
        }
        return function.apply((MappingType) expType);
    }


    @Deprecated
    static List<Object> _createSimpleMultiArgList(final List<Expression> expList) {
        final int expSize = expList.size();
        assert expSize > 1;
        final List<Object> argList = new ArrayList<>((expSize << 1) - 1);
        Expression expression;
        for (int i = 0; i < expSize; i++) {
            if (i > 0) {
                argList.add(FuncWord.COMMA);
            }
            expression = expList.get(i);
            if (expression instanceof SqlValueParam.MultiValue) {
                String m = "support multi parameter or literal";
                throw ContextStack.criteriaError(ContextStack.peek(), m);
            }
            argList.add(expression);
        }
        return argList;
    }


    @Deprecated
    static SimpleExpression _simpleTowArgFunc(final String name, final Expression g1,
                                              final Expression g2, final TypeMeta returnType) {
        if (g1 instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, g1);
        }
        if (g2 instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, g2);
        }
        final List<Object> argList = new ArrayList<>(3);
        argList.add(g1);
        argList.add(FuncWord.COMMA);
        argList.add(g2);
        return FunctionUtils.complexArgFunc(name, argList, returnType);
    }

    @Deprecated
    static Expression _simpleThreeArgFunc(final String name, final Expression e1
            , final Expression e2, final Expression e3, final TypeMeta returnType) {
        if (e1 instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, e1);
        }
        if (e2 instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, e2);
        }
        if (e3 instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, e3);
        }
        final List<Object> argList = new ArrayList<>(5);

        argList.add(e1);
        argList.add(FuncWord.COMMA);
        argList.add(e2);
        argList.add(FuncWord.COMMA);

        argList.add(e3);
        return FunctionUtils.complexArgFunc(name, argList, returnType);
    }

    @Deprecated
    static Expression _simpleMaxThreeArgFunc(final String name, final List<Expression> expList
            , final TypeMeta returnType) {
        final Expression func;
        switch (expList.size()) {
            case 1:
                func = FunctionUtils.oneArgFunc(name, expList.get(0), returnType);
                break;
            case 2:
                func = FunctionUtils.twoArgFunc(name, expList.get(0), expList.get(1), returnType);
                break;
            case 3:
                func = FunctionUtils.threeArgFunc(name, expList.get(0), expList.get(1), expList.get(2), returnType);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    @Deprecated
    static Expression _simpleMaxTwoArgFunc(final String name, final List<Expression> expList
            , final TypeMeta returnType) {
        final Expression func;
        switch (expList.size()) {
            case 1:
                func = FunctionUtils.oneArgFunc(name, expList.get(0), returnType);
                break;
            case 2:
                func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList), returnType);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    @Deprecated
    static Expression _singleAndMultiArgFunc(final String name, final Expression single, final Expression multi
            , final TypeMeta returnType) {
        if (single instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, single);
        }
        final List<Object> argLit = new ArrayList<>(3);
        argLit.add(single);
        argLit.add(FuncWord.COMMA);
        argLit.add(multi);
        return FunctionUtils.complexArgFunc(name, argLit, returnType);
    }

    @Deprecated
    static Expression _singleAndListFunc(final String name, final Expression expr
            , final TypeMeta elementType, final Object exprList, final TypeMeta returnType) {
        if (expr instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, expr);
        }
        final List<Object> argList;

        if (exprList instanceof List) {
            final List<?> actualExprList = (List<?>) exprList;
            final int exprSize = actualExprList.size();
            if (exprSize == 0) {
                throw CriteriaUtils.funcArgError(name, exprList);
            }
            argList = new ArrayList<>(((1 + exprSize) << 1) - 1);
            for (Object o : actualExprList) {
                argList.add(FuncWord.COMMA);
                if (o instanceof Expression) {
                    argList.add(o);
                } else {
                    argList.add(SQLs.literal(elementType.mappingType(), o));
                }
            }
        } else {
            argList = new ArrayList<>(3);
            argList.add(expr);
            argList.add(FuncWord.COMMA);
            if (exprList instanceof Expression) {
                argList.add(exprList);
            } else {
                argList.add(SQLs.literal(elementType.mappingType(), exprList));
            }
        }
        return FunctionUtils.complexArgFunc(name, argList, returnType);
    }


    @Deprecated
    static TypeMeta _doubleOrNumeric(final Expression exp) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    static TypeMeta _numericOrDecimal(final Expression exp) {
        throw new UnsupportedOperationException();
    }


    /**
     * @see #_doubleOrNumeric(Expression)
     */
    static MappingType _doubleOrNumberType(final MappingType type) {
        final MappingType returnType;
        if (type instanceof MappingType.SqlFloatType) {
            returnType = DoubleType.INSTANCE;
        } else if (type instanceof MappingType.SqlNumberType) {
            returnType = type;
        } else {
            returnType = BigDecimalType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see #round(Expression)
     */
    static MappingType _numberOrDecimal(final MappingType type) {
        final MappingType returnType;
        if (type instanceof MappingType.SqlNumberType) {
            returnType = type;
        } else {
            returnType = BigDecimalType.INSTANCE;
        }
        return returnType;
    }

    static MappingType _doubleOrDecimal(final MappingType type) {
        final MappingType returnType;
        if (type instanceof MappingType.SqlFloatType) {
            returnType = DoubleType.INSTANCE;
        } else {
            returnType = BigDecimalType.INSTANCE;
        }
        return returnType;
    }

    static MappingType _sqlStringType(final MappingType type) {
        final MappingType returnType;
        if (type instanceof MappingType.SqlStringType) {
            returnType = type;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If exp is {@link ByteType},then {@link ShortType}</li>
     *     <li>Else if exp is {@link ShortType},then {@link IntegerType}</li>
     *     <li>Else if exp is {@link MediumIntType},then {@link IntegerType}</li>
     *     <li>Else if exp is {@link LongType},then {@link BigIntegerType}</li>
     *     <li>Else if exp is {@link BigDecimalType},then {@link BigDecimalType}</li>
     *     <li>Else if exp is {@link FloatType},then {@link FloatType}</li>
     *     <li>Else if exp is sql float type,then {@link DoubleType}</li>
     *     <li>Else he {@link MappingType} of exp</li>
     * </ul>
     *
     */
    static MappingType _sumType(final MappingType type) {
        final MappingType returnType;
        if (type instanceof MappingType.SqlIntegerType) {
            final MappingType.LengthType length;
            length = ((MappingType.SqlIntegerType) type).lengthType();
            switch (length) {
                case DEFAULT:
                    returnType = LongType.INSTANCE;
                    break;
                case LONG:
                case BIG_LONG:
                    returnType = BigIntegerType.INSTANCE;
                    break;
                case TINY:
                    returnType = ShortType.INSTANCE;
                    break;
                case SMALL:
                case MEDIUM:
                    returnType = IntegerType.INSTANCE;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(length);
            }
        } else if (type instanceof MappingType.SqlDecimalType) {
            returnType = BigDecimalType.INSTANCE;
        } else if (type instanceof MappingType.SqlFloatType) {
            if (type instanceof FloatType) {
                returnType = type;
            } else {
                returnType = DoubleType.INSTANCE;
            }
        } else {
            returnType = type;
        }
        return returnType;
    }



    /*-------------------below private method-------------------*/

    @Deprecated
    private static String functionsKeyWordToString(final Enum<?> e) {
        return _StringUtils.builder()
                .append(Functions.class.getName())
                .append(_Constant.PERIOD)
                .append(e.name())
                .toString();
    }


    /**
     * private class, standard count(*) function expression
     *
     * @see #countAsterisk()
     * @since 1.0
     */
    private static final class CountAsteriskFunction extends OperationExpression.SqlFunctionExpression {

        private static final CountAsteriskFunction INSTANCE = new CountAsteriskFunction();

        private CountAsteriskFunction() {
            super("count", true, LongType.INSTANCE);
        }


        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            sqlBuilder.append(" *");
        }

        @Override
        void argToString(StringBuilder builder) {
            builder.append(" *");
        }


    }//CountStartFunction


}
