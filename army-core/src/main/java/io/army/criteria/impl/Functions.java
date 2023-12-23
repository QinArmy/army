package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.standard.SQLFunction;
import io.army.dialect._SqlContext;
import io.army.mapping.*;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.BooleanSupplier;
import java.util.function.UnaryOperator;

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

    /**
     * package interface,this interface only is implemented by class or enum,couldn't is extended by interface.
     */
    interface ArmyKeyWord extends SQLWords {

    }


    /**
     * <p>Create searched case function.
     */
    public static SQLFunction._CaseFuncWhenClause cases() {
        return LiteralFunctions.caseFunc(null);
    }

    /**
     * <p>Create simple case function.
     *
     * @param expression {@link Expression} instance or literal
     */
    public static SQLFunction._CaseFuncWhenClause cases(Expression expression) {
        ContextStack.assertNonNull(expression);
        return LiteralFunctions.caseFunc(expression);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link  MappingType} of expr
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>literal</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_abs">ABS(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">ABS(numeric_type)</a>
     */
    public static SimpleExpression abs(final Object expr) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(expr);
        return LiteralFunctions.oneArgFunc("ABS", expression, expression.typeMeta());
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  DoubleType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>literal</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_acos">ACOS(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">acos ( double precision ) → double precision</a>
     */
    public static SimpleExpression acos(final Object expr) {
        return LiteralFunctions.oneArgFunc("ACOS", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  DoubleType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>literal</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_asin">ASIN(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">asin ( double precision ) → double precision</a>
     */
    public static SimpleExpression asin(final Object expr) {
        return LiteralFunctions.oneArgFunc("ASIN", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  DoubleType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>literal</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_atan">ATAN(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">atan ( double precision ) → double precision</a>
     */
    public static SimpleExpression atan(final Object expr) {
        return LiteralFunctions.oneArgFunc("ATAN", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  DoubleType}
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @param y non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_atan2">ATAN(X,y)</a>
     */
    public static SimpleExpression atan(final Object x, final Object y) {
        return LiteralFunctions.twoArgFunc("ATAN", x, y, DoubleType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>if {@link MappingType} of exp is number type,then {@link MappingType} of exp </li>
     *     <li>else {@link BigDecimalType} </li>
     * </ul>
     *
     * @param exp non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>literal</li>
     *            </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_ceil">CEIL(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">ceil ( numeric ) → numeric,ceil ( double precision ) → double precision</a>
     */
    public static SimpleExpression ceil(final Object exp) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(exp);
        return LiteralFunctions.oneArgFunc("CEIL", expression, _returnType(expression, Functions::_numberOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} or expr
     *
     * @param expr     non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>literal</li>
     *                 </ul>
     * @param fromBase non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>literal</li>
     *                 </ul>
     * @param toBase   non-null, one of following :
     *                 <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>literal</li>
     *                 </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_conv">CONV(X)</a>
     */
    public static SimpleExpression conv(final Object expr, final Object fromBase, final Object toBase) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(expr);
        return LiteralFunctions.threeArgFunc("CONV", expression, fromBase, toBase, expression.typeMeta());
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  DoubleType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>literal</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_cos">COS(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">cos ( double precision ) → double precision</a>
     */
    public static SimpleExpression cos(final Object expr) {
        return LiteralFunctions.oneArgFunc("COS", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  DoubleType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>literal</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_cot">COT(X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">cot ( double precision ) → double precision</a>
     */
    public static SimpleExpression cot(final Object expr) {
        return LiteralFunctions.oneArgFunc("COT", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>literal</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_crc32">CRC32(expr)</a>
     */
    public static SimpleExpression crc32(final Object expr) {
        return LiteralFunctions.oneArgFunc("CRC32", expr, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  DoubleType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>literal</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_degrees">DEGREES(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">degrees ( double precision )</a>
     */
    public static SimpleExpression degrees(final Object expr) {
        return LiteralFunctions.oneArgFunc("DEGREES", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>literal</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_exp">EXP(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">exp ( numeric )</a>
     */
    public static SimpleExpression exp(final Object expr) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(expr);
        return LiteralFunctions.oneArgFunc("EXP", expression, _returnType(expression, Functions::_doubleOrDecimal));
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  LongType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>literal</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_floor">FLOOR(x)</a>
     */
    public static SimpleExpression floor(final Object expr) {
        return LiteralFunctions.oneArgFunc("FLOOR", expr, LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  StringType}
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @param d non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_format">FORMAT(x,d)</a>
     */
    public static SimpleExpression format(final Object x, final Object d) {
        return LiteralFunctions.twoArgFunc("FORMAT", x, d, StringType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_ln">LN(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">ln ( numeric ) → numeric,ln ( double precision ) → double precision</a>
     */
    public static SimpleExpression ln(final Object x) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(x);
        return LiteralFunctions.oneArgFunc("LN", expression, _returnType(expression, Functions::_doubleOrDecimal));
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log">LOG(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">log ( numeric ) → numeric,log ( double precision ) → double precision</a>
     */
    public static SimpleExpression log(final Object x) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(x);
        return LiteralFunctions.oneArgFunc("LOG", expression, _returnType(expression, Functions::_doubleOrDecimal));
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  BigDecimalType}
     *
     * @param b non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log">LOG(B,X)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">log ( b numeric, x numeric ) → numeric</a>
     */
    public static SimpleExpression log(final Object b, final Object x) {
        return LiteralFunctions.twoArgFunc("LOG", b, x, BigDecimalType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_log10">LOG10(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">log10 ( numeric ) → numeric,log10 ( double precision ) → double precision</a>
     */
    public static SimpleExpression log10(final Object x) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(x);
        return LiteralFunctions.oneArgFunc("LOG10", expression, _returnType(expression, Functions::_doubleOrDecimal));
    }

    /**
     * <p>The {@link MappingType} of function return type: the {@link MappingType} of n.
     *
     * @param n non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @param m non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_mod">MOD(N,M), N % M, N MOD M</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">mod ( y numeric_type, x numeric_type ) → numeric_type</a>
     */
    public static SimpleExpression mod(final Object n, final Object m) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(n);
        return LiteralFunctions.twoArgFunc("MOD", expression, m, expression.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_pi">PI()</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">pi ( ) → double precision</a>
     */
    public static SimpleExpression pi() {
        return LiteralFunctions.zeroArgFunc("PI", DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link MappingType} of x
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @param y non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_pow">POW(x,y)</a>
     */
    public static SimpleExpression pow(final Object x, final Object y) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(x);
        return LiteralFunctions.twoArgFunc("POW", expression, y, expression.typeMeta());
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType} .
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_radians">RADIANS(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">radians ( double precision ) → double precision</a>
     */
    public static SimpleExpression radians(final Object x) {
        return LiteralFunctions.oneArgFunc("RADIANS", x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType} .
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_rand">RAND([N])</a>
     */
    public static SimpleExpression rand() {
        return LiteralFunctions.zeroArgFunc("RAND", DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType} .
     *
     * @param n non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_rand">RAND([N])</a>
     */
    public static SimpleExpression rand(final Object n) {
        return LiteralFunctions.oneArgFunc("RAND", n, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is number type,then {@link MappingType} of exp</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_round">ROUND(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">round ( numeric ) → numeric</a>
     */
    public static SimpleExpression round(final Object x) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(x);
        return LiteralFunctions.oneArgFunc("ROUND", expression, _returnType(expression, Functions::_numberOrDecimal));
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link BigDecimalType} .
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @param d non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_round">ROUND(x,d)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">round ( v numeric, s integer ) → numeric</a>
     */
    public static SimpleExpression round(final Object x, final Object d) {
        return LiteralFunctions.twoArgFunc("ROUND", x, d, BigDecimalType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link IntegerType} .
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sign">SIGN(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">sign ( numeric ) → numeric</a>
     */
    public static SimpleExpression sign(final Object x) {
        return LiteralFunctions.oneArgFunc("SIGN", x, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType} .
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sin">SIN(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">sin ( numeric ) → numeric</a>
     */
    public static SimpleExpression sin(final Object x) {
        return LiteralFunctions.oneArgFunc("SIN", x, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_sqrt">SQRT(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">sqrt ( numeric ) → numeric,sqrt ( double precision ) → double precision</a>
     */
    public static SimpleExpression sqrt(final Object x) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(x);
        return LiteralFunctions.oneArgFunc("SQRT", expression, _returnType(expression, Functions::_doubleOrDecimal));
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType} .
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_tan">TAN(x)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">tan ( numeric ) → numeric</a>
     */
    public static SimpleExpression tan(final Object x) {
        return LiteralFunctions.oneArgFunc("TAN", x, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType} .
     *
     * @param x non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @param d non-null, one of following :
     *          <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>literal</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/mathematical-functions.html#function_truncate">TRUNCATE(x,d)</a>
     */
    public static SimpleExpression truncate(final Object x, final Object d) {
        return LiteralFunctions.twoArgFunc("TRUNCATE", x, d, DoubleType.INSTANCE);
    }

    /**
     * <p>This function is standard sql92 functions
     * <p>The {@link MappingType} of function return type:the {@link  MappingType} of expr1
     *
     * @param expr1 non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>literal</li>
     *              </ul>
     * @param expr2 non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>literal</li>
     *              </ul>
     * @throws CriteriaException throw when any arg is multi-value expression
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#function_nullif">NULLIF(expr1,expr2)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-NULLIF">NULLIF(expr1,expr2)</a>
     */
    public static SimpleExpression nullIf(final Object expr1, final Object expr2) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(expr1);
        return LiteralFunctions.twoArgFunc("NULLIF", expression, expr2, _returnType(expression, Expressions::identityType));
    }

    /**
     * <p>standard sql92 functions,The {@link MappingType} of function return type: {@link IntegerType} .
     *
     * @param exp non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>literal</li>
     *            </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_length">LENGTH(str)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">length ( text ) → integer</a>
     */
    public static SimpleExpression length(Object exp) {
        return LiteralFunctions.oneArgFunc("LENGTH", exp, IntegerType.INSTANCE);
    }

    public static SimpleExpression countAsterisk() {
        return CountAsteriskFunction.INSTANCE;
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  LongType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>literal</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static SimpleExpression count(Object expr) {
        return LiteralFunctions.oneArgFunc("COUNT", expr, LongType.INSTANCE);
    }


    /*################################## blow number function method ##################################*/



    /*-------------------below Aggregate Function-------------------*/

    public static SimpleExpression min(Object exp) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(exp);
        return LiteralFunctions.oneArgFunc("min", expression, _returnType(expression, Expressions::identityType));
    }

    public static SimpleExpression max(Object exp) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(exp);
        return LiteralFunctions.oneArgFunc("max", expression, _returnType(expression, Expressions::identityType));
    }

    public static SimpleExpression sum(Object exp) {
        final Expression expression;
        expression = SQLs._nonNullLiteral(exp);
        return LiteralFunctions.oneArgFunc("sum", expression, _returnType(expression, Functions::_sumType));
    }



    /*################################## blow date time function method ##################################*/


    /*-------------------below custom function -------------------*/

    /**
     * <p>User defined no argument function
     * <p>The {@link MappingType} of function return type is returnType
     *
     * @param name       function name
     * @param returnType function return type.
     */
    public static SimpleExpression myFunc(String name, TypeMeta returnType) {
        return LiteralFunctions.myZeroArgFunc(name, returnType);
    }

    /**
     * <p>User defined no argument boolean function
     * <p>The {@link MappingType} of function return type: {@link  BooleanType}
     *
     * @param name function name
     */
    public static SimplePredicate myFunc(String name) {
        return LiteralFunctions.myZeroArgPredicate(name);
    }

    /**
     * <p>User defined one argument function
     * <p>The {@link MappingType} of function return type is returnType
     *
     * @param name       function name
     * @param expr       argument
     * @param returnType function return type.
     */
    public static SimpleExpression myFunc(String name, Expression expr, TypeMeta returnType) {
        return LiteralFunctions.myOneArgFunc(name, expr, returnType);
    }

    /**
     * <p>User defined one argument boolean function
     * <p>The {@link MappingType} of function return type: {@link  BooleanType}
     *
     * @param name function name
     * @param expr argument
     */
    public static SimplePredicate myFunc(String name, Expression expr) {
        return LiteralFunctions.myOneArgPredicate(name, expr);
    }

    /**
     * <p>User defined two argument function
     * <p>The {@link MappingType} of function return type is returnType
     *
     * @param name       function name
     * @param expr1      argument
     * @param expr2      argument
     * @param returnType function return type.
     */
    public static SimpleExpression myFunc(String name, Expression expr1, Expression expr2, TypeMeta returnType) {
        return LiteralFunctions.myTwoArgFunc(name, expr1, expr2, returnType);
    }

    /**
     * <p>User defined two argument boolean function
     * <p>The {@link MappingType} of function return type: {@link  BooleanType}
     *
     * @param name  function name
     * @param expr1 argument
     * @param expr2 argument
     */
    public static SimplePredicate myFunc(String name, Expression expr1, Expression expr2) {
        return LiteralFunctions.myTwoArgPredicate(name, expr1, expr2);
    }

    /**
     * <p>User defined three argument function
     * <p>The {@link MappingType} of function return type is returnType
     *
     * @param name       function name
     * @param expr1      argument
     * @param expr2      argument
     * @param expr3      argument
     * @param returnType function return type.
     */
    public static SimpleExpression myFunc(String name, Expression expr1, Expression expr2, Expression expr3, TypeMeta returnType) {
        return LiteralFunctions.myThreeArgFunc(name, expr1, expr2, expr3, returnType);
    }

    /**
     * <p>User defined three argument boolean function
     * <p>The {@link MappingType} of function return type: {@link  BooleanType}
     *
     * @param name  function name
     * @param expr1 argument
     * @param expr2 argument
     * @param expr3 argument
     */
    public static SimplePredicate myFunc(String name, Expression expr1, Expression expr2, Expression expr3) {
        return LiteralFunctions.myThreeArgPredicate(name, expr1, expr2, expr3);
    }

    /**
     * <p>User defined multi-argument function
     * <p>The {@link MappingType} of function return type is returnType
     *
     * @param name       function name
     * @param expList    argument
     * @param returnType function return type.
     */
    public static SimpleExpression myFunc(String name, List<Expression> expList, TypeMeta returnType) {
        return LiteralFunctions.myMultiArgFunc(name, expList, returnType);
    }

    /**
     * <p>User defined multi-argument boolean function
     * <p>The {@link MappingType} of function return type: {@link  BooleanType}
     *
     * @param name function name
     */
    public static SimplePredicate myFunc(String name, List<Expression> expList) {
        return LiteralFunctions.myMultiArgPredicate(name, expList);
    }


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
                argList.add(SqlWords.FuncWord.COMMA);
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
        argList.add(SqlWords.FuncWord.COMMA);
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
        argList.add(SqlWords.FuncWord.COMMA);
        argList.add(e2);
        argList.add(SqlWords.FuncWord.COMMA);

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
        argLit.add(SqlWords.FuncWord.COMMA);
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
                argList.add(SqlWords.FuncWord.COMMA);
                if (o instanceof Expression) {
                    argList.add(o);
                } else {
                    argList.add(SQLs.literal(elementType.mappingType(), o));
                }
            }
        } else {
            argList = new ArrayList<>(3);
            argList.add(expr);
            argList.add(SqlWords.FuncWord.COMMA);
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
     * @see #round(Object)
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


    /**
     * private class, standard count(*) function expression
     *
     * @see #countAsterisk()
     * @since 0.6.0
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
