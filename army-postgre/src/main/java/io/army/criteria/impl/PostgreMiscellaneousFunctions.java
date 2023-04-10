package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.mapping.*;

abstract class PostgreMiscellaneousFunctions extends PostgreFuncSyntax {

    PostgreMiscellaneousFunctions() {
    }


    /*-------------------below Comparison Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html#FUNCTIONS-COMPARISON-FUNC-TABLE">Comparison Functions</a>
     */
    public static Expression numNonNulls(Expression first, Expression... rest) {
        return FunctionUtils.multiArgFunc("NUM_NONNULLS", IntegerType.INSTANCE, first, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html#FUNCTIONS-COMPARISON-FUNC-TABLE">Comparison Functions</a>
     */
    public static Expression numNulls(Expression first, Expression... rest) {
        return FunctionUtils.multiArgFunc("NUM_NULLS", IntegerType.INSTANCE, first, rest);
    }


    /*-------------------below Mathematical Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">cbrt(double precision)</a>
     */
    public static Expression cbrt(Expression exp) {
        return FunctionUtils.oneArgFunc("CBRT", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of y
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">div ( y numeric, x numeric )</a>
     */
    public static Expression div(Expression y, Expression x) {
        return FunctionUtils.twoArgFunc("DIV", y, x, y.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BigDecimalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">factorial ( bigint ) → numeric</a>
     */
    public static Expression factorial(Expression exp) {
        return FunctionUtils.oneArgFunc("FACTORIAL", exp, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  MappingType} of exp
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">floor ( numeric ) → numeric,floor ( double precision ) → double precision</a>
     */
    public static Expression floor(final Expression exp) {
        return FunctionUtils.oneArgFunc("FLOOR", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  MappingType} of exp1
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">gcd ( numeric_type, numeric_type ) → numeric_type</a>
     */
    public static Expression gcd(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgFunc("GCD", exp1, exp2, exp1.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  MappingType} of exp1
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">lcm ( numeric_type, numeric_type ) → numeric_type</a>
     */
    public static Expression lcm(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgFunc("LCM", exp1, exp2, exp1.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">min_scale ( numeric ) → integer</a>
     */
    public static Expression minScale(final Expression exp) {
        return FunctionUtils.oneArgFunc("MIN_SCALE", exp, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">power ( a numeric, b numeric ) → numeric,power ( a double precision, b double precision ) → double precision</a>
     */
    public static Expression power(final Expression x, final Expression y) {
        return FunctionUtils.twoArgFunc("POWER", x, y, _returnType(x, Functions::_numberOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">scale ( numeric ) → integer</a>
     */
    public static Expression scale(final Expression x, final Expression y) {
        return FunctionUtils.twoArgFunc("SCALE", x, y, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">trim_scale ( numeric ) → numeric</a>
     */
    public static Expression trimScale(final Expression exp) {
        return FunctionUtils.oneArgFunc("TRIM_SCALE", exp, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If the {@link MappingType} of exp is float number type,then {@link DoubleType}</li>
     *     <li>Else {@link BigDecimalType}</li>
     * </ul>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">trunc ( numeric ) → numeric,trunc ( double precision ) → double precision</a>
     */
    public static Expression trunc(final Expression exp) {
        return FunctionUtils.oneArgFunc("TRUNC", exp, _returnType(exp, Functions::_doubleOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">trunc ( v numeric, s integer ) → numeric</a>
     */
    public static Expression trunc(final Expression v, final Expression s) {
        return FunctionUtils.twoArgFunc("TRUNC", v, s, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">width_bucket ( operand numeric, low numeric, high numeric, count integer ) → integer,width_bucket ( operand double precision, low double precision, high double precision, count integer ) → integer</a>
     */
    public static Expression widthBucket(final Expression operand, final Expression low, Expression high, Expression count) {
        return FunctionUtils.fourArgFunc("WIDTH_BUCKET", operand, low, high, count, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">width_bucket ( operand anycompatible, thresholds anycompatiblearray ) → integer</a>
     */
    public static Expression widthBucket(final Expression operand, final Expression thresholds) {
        return FunctionUtils.twoArgFunc("WIDTH_BUCKET", operand, thresholds, IntegerType.INSTANCE);
    }


    /*-------------------below Random Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-RANDOM-TABLE">random ( ) → double precision</a>
     */
    public static Expression random() {
        return FunctionUtils.noArgFunc("RANDOM", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link VoidType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-RANDOM-TABLE">setseed ( double precision ) → void</a>
     */
    public static Expression setSeed(Expression exp) {
        return FunctionUtils.oneArgFunc("SETSEED", exp, VoidType.INSTANCE);
    }


    /*-------------------below Trigonometric Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">acosd ( double precision ) → double precision</a>
     */
    public static Expression acosd(final Expression expr) {
        return FunctionUtils.oneArgFunc("ACOSD", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">asind ( double precision ) → double precision</a>
     */
    public static Expression asind(final Expression expr) {
        return FunctionUtils.oneArgFunc("ASIND", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">atand ( double precision ) → double precision</a>
     */
    public static Expression atand(final Expression expr) {
        return FunctionUtils.oneArgFunc("ATAND", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">atan2 ( y double precision, x double precision ) → double precision</a>
     */
    public static Expression atan2(Expression y, Expression x) {
        return FunctionUtils.twoArgFunc("ATAN2", y, x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">atan2d ( y double precision, x double precision ) → double precision</a>
     */
    public static Expression atan2d(Expression y, Expression x) {
        return FunctionUtils.twoArgFunc("ATAN2D", y, x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">cosd ( double precision ) → double precision</a>
     */
    public static Expression cosd(final Expression expr) {
        return FunctionUtils.oneArgFunc("COSD", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">cotd ( double precision ) → double precision</a>
     */
    public static Expression cotd(final Expression expr) {
        return FunctionUtils.oneArgFunc("COTD", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">sind ( double precision ) → double precision</a>
     */
    public static Expression sind(final Expression expr) {
        return FunctionUtils.oneArgFunc("SIND", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">tand ( double precision ) → double precision</a>
     */
    public static Expression tand(final Expression expr) {
        return FunctionUtils.oneArgFunc("TAND", expr, DoubleType.INSTANCE);
    }

    /*-------------------below Hyperbolic Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">sinh ( double precision ) → double precision</a>
     */
    public static Expression sinh(final Expression expr) {
        return FunctionUtils.oneArgFunc("SINH", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">cosh ( double precision ) → double precision</a>
     */
    public static Expression cosh(final Expression expr) {
        return FunctionUtils.oneArgFunc("COSH", expr, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">tanh ( double precision ) → double precision</a>
     */
    public static Expression tanh(final Expression expr) {
        return FunctionUtils.oneArgFunc("TANH", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">asinh ( double precision ) → double precision</a>
     */
    public static Expression asinh(final Expression expr) {
        return FunctionUtils.oneArgFunc("ASINH", expr, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">acosh ( double precision ) → double precision</a>
     */
    public static Expression acosh(final Expression expr) {
        return FunctionUtils.oneArgFunc("ACOSH", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">atanh ( double precision ) → double precision</a>
     */
    public static Expression atanh(final Expression expr) {
        return FunctionUtils.oneArgFunc("ATANH", expr, DoubleType.INSTANCE);
    }


}
