package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.mapping.*;
import io.army.mapping.postgre.PostgreCidrType;
import io.army.mapping.postgre.PostgreInetType;
import io.army.mapping.postgre.StringArrayType;

import java.util.function.BiFunction;

/**
 * <p>
 * package class.
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreMiscellaneousFunctions extends PostgreGeometricFunctions {

    /**
     * package constructor
     */
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
    public static SimpleExpression numNonNulls(Expression first, Expression... rest) {
        return FunctionUtils.multiArgFunc("NUM_NONNULLS", IntegerType.INSTANCE, first, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html#FUNCTIONS-COMPARISON-FUNC-TABLE">Comparison Functions</a>
     */
    public static SimpleExpression numNulls(Expression first, Expression... rest) {
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
    public static SimpleExpression cbrt(Expression exp) {
        return FunctionUtils.oneArgFunc("CBRT", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of y
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">div ( y numeric, x numeric )</a>
     */
    public static SimpleExpression div(Expression y, Expression x) {
        return FunctionUtils.twoArgFunc("DIV", y, x, y.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BigDecimalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">factorial ( bigint ) → numeric</a>
     */
    public static SimpleExpression factorial(Expression exp) {
        return FunctionUtils.oneArgFunc("FACTORIAL", exp, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  MappingType} of exp
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">floor ( numeric ) → numeric,floor ( double precision ) → double precision</a>
     */
    public static SimpleExpression floor(final Expression exp) {
        return FunctionUtils.oneArgFunc("FLOOR", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  MappingType} of exp1
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">gcd ( numeric_type, numeric_type ) → numeric_type</a>
     */
    public static SimpleExpression gcd(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgFunc("GCD", exp1, exp2, exp1.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  MappingType} of exp1
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">lcm ( numeric_type, numeric_type ) → numeric_type</a>
     */
    public static SimpleExpression lcm(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgFunc("LCM", exp1, exp2, exp1.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">min_scale ( numeric ) → integer</a>
     */
    public static SimpleExpression minScale(final Expression exp) {
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
    public static SimpleExpression power(final Expression x, final Expression y) {
        return FunctionUtils.twoArgFunc("POWER", x, y, _returnType(x, Functions::_numberOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">scale ( numeric ) → integer</a>
     */
    public static SimpleExpression scale(final Expression x, final Expression y) {
        return FunctionUtils.twoArgFunc("SCALE", x, y, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">trim_scale ( numeric ) → numeric</a>
     */
    public static SimpleExpression trimScale(final Expression exp) {
        return FunctionUtils.oneArgFunc("TRIM_SCALE", exp, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:The {@link MappingType} of exp
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">trunc ( numeric ) → numeric,trunc ( double precision ) → double precision</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#MACADDR-FUNCTIONS-TABLE">trunc ( macaddr ) → macaddr<br/>
     * trunc ( macaddr8 ) → macaddr8
     * </a>
     */
    public static SimpleExpression trunc(final Expression exp) {
        return FunctionUtils.oneArgFunc("TRUNC", exp, _returnType(exp, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">trunc ( v numeric, s integer ) → numeric</a>
     */
    public static SimpleExpression trunc(final Expression v, final Expression s) {
        return FunctionUtils.twoArgFunc("TRUNC", v, s, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">width_bucket ( operand numeric, low numeric, high numeric, count integer ) → integer,width_bucket ( operand double precision, low double precision, high double precision, count integer ) → integer</a>
     */
    public static SimpleExpression widthBucket(final Expression operand, final Expression low, Expression high, Expression count) {
        return FunctionUtils.fourArgFunc("WIDTH_BUCKET", operand, low, high, count, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">width_bucket ( operand anycompatible, thresholds anycompatiblearray ) → integer</a>
     */
    public static SimpleExpression widthBucket(final Expression operand, final Expression thresholds) {
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
    public static SimpleExpression random() {
        return FunctionUtils.zeroArgFunc("RANDOM", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-RANDOM-TABLE">setseed ( double precision ) → void</a>
     */
    public static SimpleExpression setSeed(Expression exp) {
        return FunctionUtils.oneArgFunc("SETSEED", exp, StringType.INSTANCE);
    }


    /*-------------------below Trigonometric Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">acosd ( double precision ) → double precision</a>
     */
    public static SimpleExpression acosd(final Expression expr) {
        return FunctionUtils.oneArgFunc("ACOSD", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">asind ( double precision ) → double precision</a>
     */
    public static SimpleExpression asind(final Expression expr) {
        return FunctionUtils.oneArgFunc("ASIND", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">atand ( double precision ) → double precision</a>
     */
    public static SimpleExpression atand(final Expression expr) {
        return FunctionUtils.oneArgFunc("ATAND", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">atan2 ( y double precision, x double precision ) → double precision</a>
     */
    public static SimpleExpression atan2(Expression y, Expression x) {
        return FunctionUtils.twoArgFunc("ATAN2", y, x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">atan2d ( y double precision, x double precision ) → double precision</a>
     */
    public static SimpleExpression atan2d(Expression y, Expression x) {
        return FunctionUtils.twoArgFunc("ATAN2D", y, x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">cosd ( double precision ) → double precision</a>
     */
    public static SimpleExpression cosd(final Expression expr) {
        return FunctionUtils.oneArgFunc("COSD", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">cotd ( double precision ) → double precision</a>
     */
    public static SimpleExpression cotd(final Expression expr) {
        return FunctionUtils.oneArgFunc("COTD", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">sind ( double precision ) → double precision</a>
     */
    public static SimpleExpression sind(final Expression expr) {
        return FunctionUtils.oneArgFunc("SIND", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">tand ( double precision ) → double precision</a>
     */
    public static SimpleExpression tand(final Expression expr) {
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
    public static SimpleExpression sinh(final Expression expr) {
        return FunctionUtils.oneArgFunc("SINH", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">cosh ( double precision ) → double precision</a>
     */
    public static SimpleExpression cosh(final Expression expr) {
        return FunctionUtils.oneArgFunc("COSH", expr, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">tanh ( double precision ) → double precision</a>
     */
    public static SimpleExpression tanh(final Expression expr) {
        return FunctionUtils.oneArgFunc("TANH", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">asinh ( double precision ) → double precision</a>
     */
    public static SimpleExpression asinh(final Expression expr) {
        return FunctionUtils.oneArgFunc("ASINH", expr, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">acosh ( double precision ) → double precision</a>
     */
    public static SimpleExpression acosh(final Expression expr) {
        return FunctionUtils.oneArgFunc("ACOSH", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">atanh ( double precision ) → double precision</a>
     */
    public static SimpleExpression atanh(final Expression expr) {
        return FunctionUtils.oneArgFunc("ATANH", expr, DoubleType.INSTANCE);
    }

    /*-------------------below Data Type Formatting Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-formatting.html#FUNCTIONS-FORMATTING-TABLE">to_char ( timestamp, text ) → text <br/>
     * to_char ( timestamp with time zone, text ) → text <br/>
     * to_char ( interval, text ) → text <br/>
     * to_char ( numeric_type, text ) → text
     * </a>
     */
    public static SimpleExpression toChar(Expression exp, Expression format) {
        return FunctionUtils.twoArgFunc("TO_CHAR", exp, format, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-formatting.html#FUNCTIONS-FORMATTING-TABLE">to_date ( text, text ) → date</a>
     */
    public static SimpleExpression toDate(Expression exp, Expression format) {
        return FunctionUtils.twoArgFunc("TO_DATE", exp, format, LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-formatting.html#FUNCTIONS-FORMATTING-TABLE">to_number ( text, text ) → numeric</a>
     */
    public static SimpleExpression toNumber(Expression exp, Expression format) {
        return FunctionUtils.twoArgFunc("TO_NUMBER", exp, format, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-formatting.html#FUNCTIONS-FORMATTING-TABLE">to_timestamp ( text, text ) → timestamp with time zone</a>
     */
    public static SimpleExpression toTimestamp(Expression exp, Expression format) {
        return FunctionUtils.twoArgFunc("TO_TIMESTAMP", exp, format, OffsetDateTimeType.INSTANCE);
    }

    /*-------------------below Date/Time Functions and Operators-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">age ( timestamp ) → interval</a>
     */
    public static SimpleExpression age(Expression timestamp) {
        return FunctionUtils.oneArgFunc("AGE", timestamp, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">age ( timestamp, timestamp ) → interval</a>
     */
    public static SimpleExpression age(Expression timestamp1, Expression timestamp2) {
        return FunctionUtils.twoArgFunc("AGE", timestamp1, timestamp2, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">isfinite ( date ) → boolean<br/>
     * isfinite ( timestamp ) → boolean <br/>
     * isfinite ( interval ) → boolean <br/>
     * </a>
     */
    public static IPredicate isFinite(Expression exp) {
        return FunctionUtils.oneArgFuncPredicate("ISFINITE", exp);
    }


    /*-------------------below Delaying Execution function -------------------*/

    /*-------------------below Enum Support Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_first ( anyenum ) → anyenum</a>
     */
    public static SimpleExpression enumFirst(Expression anyEnum) {
        return FunctionUtils.oneArgFunc("ENUM_FIRST", anyEnum, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_first ( anyenum ) → anyenum</a>
     */
    public static SimpleExpression enumFirst(Expression anyEnum, MappingType returnType) {
        final String name = "ENUM_FIRST";
        if (!Enum.class.isAssignableFrom(returnType.javaType())) {
            throw CriteriaUtils.errorCustomReturnType(name, returnType);
        }
        return FunctionUtils.oneArgFunc(name, anyEnum, returnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_last ( anyenum ) → anyenum</a>
     */
    public static SimpleExpression enumLast(Expression anyEnum) {
        return FunctionUtils.oneArgFunc("ENUM_LAST", anyEnum, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_last ( anyenum ) → anyenum</a>
     */
    public static SimpleExpression enumLast(Expression anyEnum, MappingType returnType) {
        final String name = "ENUM_LAST";
        if (!Enum.class.isAssignableFrom(returnType.javaType())) {
            throw CriteriaUtils.errorCustomReturnType(name, returnType);
        }
        return FunctionUtils.oneArgFunc(name, anyEnum, returnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringArrayType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_range ( anyenum ) → anyarray</a>
     */
    public static SimpleExpression enumRange(Expression anyEnum) {
        return FunctionUtils.oneArgFunc("ENUM_RANGE", anyEnum, StringArrayType.from(String[].class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringArrayType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_range ( anyenum ) → anyarray</a>
     */
    public static SimpleExpression enumRange(Expression anyEnum, MappingType returnType) {
        final String name = "ENUM_RANGE";
        final Class<?> javaType;
        javaType = returnType.javaType();
        if (!javaType.isArray() || !javaType.getComponentType().isEnum()) {
            throw CriteriaUtils.errorCustomReturnType(name, returnType);
        }
        return FunctionUtils.oneArgFunc(name, anyEnum, returnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringArrayType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_range ( anyenum, anyenum ) → anyarray</a>
     */
    public static SimpleExpression enumRange(Expression leftEnum, Expression rightEnum) {
        return FunctionUtils.twoArgFunc("ENUM_RANGE", leftEnum, rightEnum, StringArrayType.from(String[].class));
    }

    public static SimpleExpression enumRange(Expression leftEnum, Expression rightEnum, MappingType returnType) {
        final String name = "ENUM_RANGE";
        final Class<?> javaType;
        javaType = returnType.javaType();
        if (!javaType.isArray() || !javaType.getComponentType().isEnum()) {
            throw CriteriaUtils.errorCustomReturnType(name, returnType);
        }
        return FunctionUtils.twoArgFunc(name, leftEnum, rightEnum, returnType);
    }

    /*-------------------below IP Address Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">abbrev ( inet ) → text<br/>
     * abbrev ( cidr ) → text
     * </a>
     */
    public static SimpleExpression abbrev(Expression exp) {
        return FunctionUtils.oneArgFunc("ABBREV", exp, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreInetType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">broadcast ( inet ) → inet<br/>
     * Computes the broadcast address for the address's network.
     * </a>
     */
    public static SimpleExpression broadcast(Expression inet) {
        return FunctionUtils.oneArgFunc("BROADCAST", inet, PostgreInetType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">family ( inet ) → integer<br/>
     * Returns the address's family: 4 for IPv4, 6 for IPv6.<br/>
     * family(inet '::1') → 6
     * </a>
     */
    public static SimpleExpression family(Expression inet) {
        return FunctionUtils.oneArgFunc("FAMILY", inet, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">host ( inet ) → text<br/>
     * Returns the IP address as text, ignoring the netmask.<br/>
     * host(inet '192.168.1.0/24') → 192.168.1.0
     * </a>
     */
    public static SimpleExpression host(Expression inet) {
        return FunctionUtils.oneArgFunc("HOST", inet, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreInetType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">hostmask ( inet ) → inet<br/>
     * Computes the host mask for the address's network.<br/>
     * hostmask(inet '192.168.23.20/30') → 0.0.0.3
     * </a>
     */
    public static SimpleExpression hostmask(Expression inet) {
        return FunctionUtils.oneArgFunc("HOSTMASK", inet, PostgreInetType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreCidrType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">inet_merge ( inet, inet ) → cidr<br/>
     * Computes the smallest network that includes both of the given networks.<br/>
     * inet_merge(inet '192.168.1.5/24', inet '192.168.2.5/24') → 192.168.0.0/22
     * </a>
     */
    public static SimpleExpression inetMerge(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgFunc("INET_MERGE", exp1, exp2, PostgreCidrType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">inet_same_family ( inet, inet ) → boolean<br/>
     * Tests whether the addresses belong to the same IP family.<br/>
     * inet_same_family(inet '192.168.1.5/24', inet '::1') → f
     * </a>
     */
    public static SimplePredicate inetSameFamily(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgPredicateFunc("INET_SAME_FAMILY", exp1, exp2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">masklen ( inet ) → integer<br/>
     * Returns the netmask length in bits.<br/>
     * masklen(inet '192.168.1.5/24') → 24
     * </a>
     */
    public static SimpleExpression maskLen(Expression inet) {
        return FunctionUtils.oneArgFunc("MASKLEN", inet, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreInetType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">netmask ( inet ) → inet<br/>
     * Computes the network mask for the address's network.<br/>
     * netmask(inet '192.168.1.5/24') → 255.255.255.0
     * </a>
     */
    public static SimpleExpression netmask(Expression inet) {
        return FunctionUtils.oneArgFunc("NETMASK", inet, PostgreInetType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreCidrType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">network ( inet ) → cidr<br/>
     * Returns the network part of the address, zeroing out whatever is to the right of the netmask. (This is equivalent to casting the value to cidr.)<br/>
     * network(inet '192.168.1.5/24') → 192.168.1.0/24
     * </a>
     */
    public static SimpleExpression network(Expression inet) {
        return FunctionUtils.oneArgFunc("NETWORK", inet, PostgreCidrType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>If exp1 type is {@link PostgreInetType},then {@link PostgreInetType}</li>
     * <li>If exp1 type is {@link PostgreCidrType},then {@link PostgreCidrType}</li>
     * <li>Else The {@link MappingType} of exp1</li>
     * </ul>
     * </p>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link IntegerType#INSTANCE}.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef.
     * @see #setMaskLen(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">set_masklen ( inet, integer ) → inet<br/>
     * Sets the netmask length for an inet value. The address part does not change.<br/>
     * set_masklen(inet '192.168.1.5/24', 16) → 192.168.1.5/16
     * </a>
     */
    public static <T> SimpleExpression setMaskLen(Expression exp1, BiFunction<MappingType, T, Expression> funcRef, T value) {
        return setMaskLen(exp1, funcRef.apply(IntegerType.INSTANCE, value));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>If exp1 type is {@link PostgreInetType},then {@link PostgreInetType}</li>
     * <li>If exp1 type is {@link PostgreCidrType},then {@link PostgreCidrType}</li>
     * <li>Else The {@link MappingType} of exp1</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">set_masklen ( inet, integer ) → inet<br/>
     * Sets the netmask length for an inet value. The address part does not change.<br/>
     * set_masklen(inet '192.168.1.5/24', 16) → 192.168.1.5/16
     * </a>
     */
    public static SimpleExpression setMaskLen(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgFunc("set_masklen", exp1, exp2, _returnType(exp1, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">text ( inet ) → text<br/>
     * Returns the unabbreviated IP address and netmask length as text. (This has the same result as an explicit cast to text.)<br/>
     * text(inet '192.168.1.5') → 192.168.1.5/32
     * </a>
     */
    public static SimpleExpression text(Expression inet) {
        return FunctionUtils.oneArgFunc("TEXT", inet, StringType.INSTANCE);
    }

    /*-------------------below  MAC Address Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: The {@link MappingType} of macAddr8
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">macaddr8_set7bit ( macaddr8 ) → macaddr8<br/>
     * Sets the 7th bit of the address to one, creating what is known as modified EUI-64, for inclusion in an IPv6 address.<br/>
     * macaddr8_set7bit(macaddr8 '00:34:56:ab:cd:ef') → 02:34:56:ff:fe:ab:cd:ef
     * </a>
     */
    public static SimpleExpression macAddr8Set7bit(Expression macAddr8) {
        return FunctionUtils.oneArgFunc("MACADDR8_SET7BIT", macAddr8, _returnType(macAddr8, Expressions::identityType));
    }

}
