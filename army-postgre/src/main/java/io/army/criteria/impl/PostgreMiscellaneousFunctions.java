package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.mapping.DoubleType;
import io.army.mapping.IntegerType;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;

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
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">div ( y numeric, x numeric )</a>
     */
    public static Expression div(Expression exp) {
        return FunctionUtils.oneArgFunc("DIV", exp, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">exp ( numeric ) → numeric,exp ( double precision ) → double precision</a>
     */
    public static Expression exp(final Expression expr) {
        //TODO modify return type
        return FunctionUtils.oneArgFunc("EXP", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">factorial ( bigint ) → numeric</a>
     */
    public static Expression factorial(Expression exp) {
        return FunctionUtils.oneArgFunc("FACTORIAL", exp, LongType.INSTANCE);
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
     * The {@link MappingType} of function return type:  {@link  MappingType} of exp
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">ln ( numeric ) → numeric,ln ( double precision ) → double precision</a>
     */
    public static Expression ln(final Expression exp) {
        return FunctionUtils.oneArgFunc("LN", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  MappingType} of exp
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">log ( numeric ) → numeric,log ( double precision ) → double precision</a>
     */
    public static Expression log(final Expression exp) {
        return FunctionUtils.oneArgFunc("LOG", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  MappingType} of exp
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">log10 ( numeric ) → numeric,log10 ( double precision ) → double precision</a>
     */
    public static Expression log10(final Expression exp) {
        return FunctionUtils.oneArgFunc("LOG10", exp, exp.typeMeta());
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
     * The {@link MappingType} of function return type: {@link MappingType} of x
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">power ( a numeric, b numeric ) → numeric,power ( a double precision, b double precision ) → double precision</a>
     */
    public static Expression power(final Expression x, final Expression y) {
        return FunctionUtils.twoArgFunc("POWER", x, y, x.typeMeta());
    }


}
