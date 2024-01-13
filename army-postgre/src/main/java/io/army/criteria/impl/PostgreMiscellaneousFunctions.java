/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.mapping.*;
import io.army.mapping.array.BooleanArrayType;
import io.army.mapping.array.StringArrayType;
import io.army.mapping.array.TextArrayType;
import io.army.mapping.postgre.PostgreCidrType;
import io.army.mapping.postgre.PostgreInetType;
import io.army.mapping.postgre.PostgrePgLsnType;
import io.army.util.ArrayUtils;
import io.army.util._Collections;

import java.util.List;
import java.util.function.BiFunction;

/**
 * <p>
 * package class.
 *
 * @since 0.6.0
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
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html#FUNCTIONS-COMPARISON-FUNC-TABLE">Comparison Functions</a>
     */
    public static SimpleExpression numNonNulls(Expression first, Expression... rest) {
        return FunctionUtils.multiArgFunc("num_nonnulls", IntegerType.INSTANCE, first, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-comparison.html#FUNCTIONS-COMPARISON-FUNC-TABLE">Comparison Functions</a>
     */
    public static SimpleExpression numNulls(Expression first, Expression... rest) {
        return FunctionUtils.multiArgFunc("num_nulls", IntegerType.INSTANCE, first, rest);
    }


    /*-------------------below Mathematical Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">cbrt(double precision)</a>
     */
    public static SimpleExpression cbrt(Expression exp) {
        return FunctionUtils.oneArgFunc("cbrt", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of y
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">div ( y numeric, x numeric )</a>
     */
    public static SimpleExpression div(Expression y, Expression x) {
        return FunctionUtils.twoArgFunc("div", y, x, y.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BigDecimalType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">factorial ( bigint ) → numeric</a>
     */
    public static SimpleExpression factorial(Expression exp) {
        return FunctionUtils.oneArgFunc("factorial", exp, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  MappingType} of exp
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">floor ( numeric ) → numeric,floor ( double precision ) → double precision</a>
     */
    public static SimpleExpression floor(final Expression exp) {
        return FunctionUtils.oneArgFunc("floor", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  MappingType} of exp1
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">gcd ( numeric_type, numeric_type ) → numeric_type</a>
     */
    public static SimpleExpression gcd(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgFunc("gcd", exp1, exp2, exp1.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  MappingType} of exp1
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">lcm ( numeric_type, numeric_type ) → numeric_type</a>
     */
    public static SimpleExpression lcm(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgFunc("lcm", exp1, exp2, exp1.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">min_scale ( numeric ) → integer</a>
     */
    public static SimpleExpression minScale(final Expression exp) {
        return FunctionUtils.oneArgFunc("min_scale", exp, IntegerType.INSTANCE);
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
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">power ( a numeric, b numeric ) → numeric,power ( a double precision, b double precision ) → double precision</a>
     */
    public static SimpleExpression power(final Expression x, final Expression y) {
        return FunctionUtils.twoArgFunc("power", x, y, _returnType(x, Functions::_numberOrDecimal));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">scale ( numeric ) → integer</a>
     */
    public static SimpleExpression scale(final Expression x, final Expression y) {
        return FunctionUtils.twoArgFunc("scale", x, y, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">trim_scale ( numeric ) → numeric</a>
     */
    public static SimpleExpression trimScale(final Expression exp) {
        return FunctionUtils.oneArgFunc("trim_scale", exp, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:The {@link MappingType} of exp
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">trunc ( numeric ) → numeric,trunc ( double precision ) → double precision</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#MACADDR-FUNCTIONS-TABLE">trunc ( macaddr ) → macaddr<br/>
     * trunc ( macaddr8 ) → macaddr8
     * </a>
     */
    public static SimpleExpression trunc(final Expression exp) {
        return FunctionUtils.oneArgFunc("trunc", exp, _returnType(exp, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">trunc ( v numeric, s integer ) → numeric</a>
     */
    public static SimpleExpression trunc(final Expression v, final Expression s) {
        return FunctionUtils.twoArgFunc("trunc", v, s, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">width_bucket ( operand numeric, low numeric, high numeric, count integer ) → integer,width_bucket ( operand double precision, low double precision, high double precision, count integer ) → integer</a>
     */
    public static SimpleExpression widthBucket(final Expression operand, final Expression low, Expression high, Expression count) {
        return FunctionUtils.fourArgFunc("width_bucket", operand, low, high, count, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-FUNC-TABLE">width_bucket ( operand anycompatible, thresholds anycompatiblearray ) → integer</a>
     */
    public static SimpleExpression widthBucket(final Expression operand, final Expression thresholds) {
        return FunctionUtils.twoArgFunc("width_bucket", operand, thresholds, IntegerType.INSTANCE);
    }


    /*-------------------below Random Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-RANDOM-TABLE">random ( ) → double precision</a>
     */
    public static SimpleExpression random() {
        return FunctionUtils.zeroArgFunc("random", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-RANDOM-TABLE">setseed ( double precision ) → void</a>
     */
    public static SimpleExpression setSeed(Expression exp) {
        return FunctionUtils.oneArgFunc("setseed", exp, StringType.INSTANCE);
    }


    /*-------------------below Trigonometric Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">acosd ( double precision ) → double precision</a>
     */
    public static SimpleExpression acosd(final Expression expr) {
        return FunctionUtils.oneArgFunc("acosd", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">asind ( double precision ) → double precision</a>
     */
    public static SimpleExpression asind(final Expression expr) {
        return FunctionUtils.oneArgFunc("asind", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">atand ( double precision ) → double precision</a>
     */
    public static SimpleExpression atand(final Expression expr) {
        return FunctionUtils.oneArgFunc("atand", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">atan2 ( y double precision, x double precision ) → double precision</a>
     */
    public static SimpleExpression atan2(Expression y, Expression x) {
        return FunctionUtils.twoArgFunc("atan2", y, x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">atan2d ( y double precision, x double precision ) → double precision</a>
     */
    public static SimpleExpression atan2d(Expression y, Expression x) {
        return FunctionUtils.twoArgFunc("atan2d", y, x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">cosd ( double precision ) → double precision</a>
     */
    public static SimpleExpression cosd(final Expression expr) {
        return FunctionUtils.oneArgFunc("cosd", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">cotd ( double precision ) → double precision</a>
     */
    public static SimpleExpression cotd(final Expression expr) {
        return FunctionUtils.oneArgFunc("cotd", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">sind ( double precision ) → double precision</a>
     */
    public static SimpleExpression sind(final Expression expr) {
        return FunctionUtils.oneArgFunc("sind", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-TRIG-TABLE">tand ( double precision ) → double precision</a>
     */
    public static SimpleExpression tand(final Expression expr) {
        return FunctionUtils.oneArgFunc("tand", expr, DoubleType.INSTANCE);
    }

    /*-------------------below Hyperbolic Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">sinh ( double precision ) → double precision</a>
     */
    public static SimpleExpression sinh(final Expression expr) {
        return FunctionUtils.oneArgFunc("sinh", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">cosh ( double precision ) → double precision</a>
     */
    public static SimpleExpression cosh(final Expression expr) {
        return FunctionUtils.oneArgFunc("cosh", expr, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">tanh ( double precision ) → double precision</a>
     */
    public static SimpleExpression tanh(final Expression expr) {
        return FunctionUtils.oneArgFunc("tanh", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">asinh ( double precision ) → double precision</a>
     */
    public static SimpleExpression asinh(final Expression expr) {
        return FunctionUtils.oneArgFunc("asinh", expr, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">acosh ( double precision ) → double precision</a>
     */
    public static SimpleExpression acosh(final Expression expr) {
        return FunctionUtils.oneArgFunc("acosh", expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-HYP-TABLE">atanh ( double precision ) → double precision</a>
     */
    public static SimpleExpression atanh(final Expression expr) {
        return FunctionUtils.oneArgFunc("atanh", expr, DoubleType.INSTANCE);
    }

    /*-------------------below Data Type Formatting Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  StringType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-formatting.html#FUNCTIONS-FORMATTING-TABLE">to_char ( timestamp, text ) → text <br/>
     * to_char ( timestamp with time zone, text ) → text <br/>
     * to_char ( interval, text ) → text <br/>
     * to_char ( numeric_type, text ) → text
     * </a>
     */
    public static SimpleExpression toChar(Expression exp, Expression format) {
        return FunctionUtils.twoArgFunc("to_char", exp, format, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-formatting.html#FUNCTIONS-FORMATTING-TABLE">to_date ( text, text ) → date</a>
     */
    public static SimpleExpression toDate(Expression exp, Expression format) {
        return FunctionUtils.twoArgFunc("to_date", exp, format, LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-formatting.html#FUNCTIONS-FORMATTING-TABLE">to_number ( text, text ) → numeric</a>
     */
    public static SimpleExpression toNumber(Expression exp, Expression format) {
        return FunctionUtils.twoArgFunc("to_number", exp, format, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-formatting.html#FUNCTIONS-FORMATTING-TABLE">to_timestamp ( text, text ) → timestamp with time zone</a>
     */
    public static SimpleExpression toTimestamp(Expression exp, Expression format) {
        return FunctionUtils.twoArgFunc("to_timestamp", exp, format, OffsetDateTimeType.INSTANCE);
    }

    /*-------------------below Date/Time Functions and Operators-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">age ( timestamp ) → interval</a>
     */
    public static SimpleExpression age(Expression timestamp) {
        return FunctionUtils.oneArgFunc("age", timestamp, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">age ( timestamp, timestamp ) → interval</a>
     */
    public static SimpleExpression age(Expression timestamp1, Expression timestamp2) {
        return FunctionUtils.twoArgFunc("age", timestamp1, timestamp2, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">isfinite ( date ) → boolean<br/>
     * isfinite ( timestamp ) → boolean <br/>
     * isfinite ( interval ) → boolean <br/>
     * </a>
     */
    public static IPredicate isFinite(Expression exp) {
        return FunctionUtils.oneArgPredicateFunc("isfinite", exp);
    }


    /*-------------------below Delaying Execution function -------------------*/

    /*-------------------below Enum Support Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_first ( anyenum ) → anyenum</a>
     */
    public static SimpleExpression enumFirst(Expression anyEnum) {
        return FunctionUtils.oneArgFunc("enum_first", anyEnum, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_first ( anyenum ) → anyenum</a>
     */
    public static SimpleExpression enumFirst(Expression anyEnum, MappingType returnType) {
        final String name = "enum_first";
        if (!Enum.class.isAssignableFrom(returnType.javaType())) {
            throw CriteriaUtils.errorCustomReturnType(name, returnType);
        }
        return FunctionUtils.oneArgFunc(name, anyEnum, returnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_last ( anyenum ) → anyenum</a>
     */
    public static SimpleExpression enumLast(Expression anyEnum) {
        return FunctionUtils.oneArgFunc("enum_last", anyEnum, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_last ( anyenum ) → anyenum</a>
     */
    public static SimpleExpression enumLast(Expression anyEnum, MappingType returnType) {
        final String name = "enum_last";
        if (!Enum.class.isAssignableFrom(returnType.javaType())) {
            throw CriteriaUtils.errorCustomReturnType(name, returnType);
        }
        return FunctionUtils.oneArgFunc(name, anyEnum, returnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringArrayType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_range ( anyenum ) → anyarray</a>
     */
    public static SimpleExpression enumRange(Expression anyEnum) {
        return FunctionUtils.oneArgFunc("enum_range", anyEnum, StringArrayType.from(String[].class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringArrayType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_range ( anyenum ) → anyarray</a>
     */
    public static SimpleExpression enumRange(Expression anyEnum, MappingType returnType) {
        final String name = "enum_range";
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
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-enum.html">enum_range ( anyenum, anyenum ) → anyarray</a>
     */
    public static SimpleExpression enumRange(Expression leftEnum, Expression rightEnum) {
        return FunctionUtils.twoArgFunc("enum_range", leftEnum, rightEnum, StringArrayType.from(String[].class));
    }

    public static SimpleExpression enumRange(Expression leftEnum, Expression rightEnum, MappingType returnType) {
        final String name = "enum_range";
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
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">abbrev ( inet ) → text<br/>
     * abbrev ( cidr ) → text
     * </a>
     */
    public static SimpleExpression abbrev(Expression exp) {
        return FunctionUtils.oneArgFunc("abbrev", exp, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreInetType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">broadcast ( inet ) → inet<br/>
     * Computes the broadcast address for the address's network.
     * </a>
     */
    public static SimpleExpression broadcast(Expression inet) {
        return FunctionUtils.oneArgFunc("broadcast", inet, PostgreInetType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">family ( inet ) → integer<br/>
     * Returns the address's family: 4 for IPv4, 6 for IPv6.<br/>
     * family(inet '::1') → 6
     * </a>
     */
    public static SimpleExpression family(Expression inet) {
        return FunctionUtils.oneArgFunc("family", inet, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">host ( inet ) → text<br/>
     * Returns the IP address as text, ignoring the netmask.<br/>
     * host(inet '192.168.1.0/24') → 192.168.1.0
     * </a>
     */
    public static SimpleExpression host(Expression inet) {
        return FunctionUtils.oneArgFunc("host", inet, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreInetType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">hostmask ( inet ) → inet<br/>
     * Computes the host mask for the address's network.<br/>
     * hostmask(inet '192.168.23.20/30') → 0.0.0.3
     * </a>
     */
    public static SimpleExpression hostmask(Expression inet) {
        return FunctionUtils.oneArgFunc("hostmask", inet, PostgreInetType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreCidrType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">inet_merge ( inet, inet ) → cidr<br/>
     * Computes the smallest network that includes both of the given networks.<br/>
     * inet_merge(inet '192.168.1.5/24', inet '192.168.2.5/24') → 192.168.0.0/22
     * </a>
     */
    public static SimpleExpression inetMerge(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgFunc("inet_merge", exp1, exp2, PostgreCidrType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BooleanType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">inet_same_family ( inet, inet ) → boolean<br/>
     * Tests whether the addresses belong to the same IP family.<br/>
     * inet_same_family(inet '192.168.1.5/24', inet '::1') → f
     * </a>
     */
    public static SimplePredicate inetSameFamily(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgPredicateFunc("inet_same_family", exp1, exp2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">masklen ( inet ) → integer<br/>
     * Returns the netmask length in bits.<br/>
     * masklen(inet '192.168.1.5/24') → 24
     * </a>
     */
    public static SimpleExpression maskLen(Expression inet) {
        return FunctionUtils.oneArgFunc("masklen", inet, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreInetType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">netmask ( inet ) → inet<br/>
     * Computes the network mask for the address's network.<br/>
     * netmask(inet '192.168.1.5/24') → 255.255.255.0
     * </a>
     */
    public static SimpleExpression netmask(Expression inet) {
        return FunctionUtils.oneArgFunc("netmask", inet, PostgreInetType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreCidrType}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">network ( inet ) → cidr<br/>
     * Returns the network part of the address, zeroing out whatever is to the right of the netmask. (This is equivalent to casting the value to cidr.)<br/>
     * network(inet '192.168.1.5/24') → 192.168.1.0/24
     * </a>
     */
    public static SimpleExpression network(Expression inet) {
        return FunctionUtils.oneArgFunc("network", inet, PostgreCidrType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>If exp1 type is {@link PostgreInetType},then {@link PostgreInetType}</li>
     * <li>If exp1 type is {@link PostgreCidrType},then {@link PostgreCidrType}</li>
     * <li>Else The {@link MappingType} of exp1</li>
     * </ul>
     *
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
    public static <T> Expression setMaskLen(Expression exp1, BiFunction<MappingType, T, Expression> funcRef, T value) {
        return setMaskLen(exp1, funcRef.apply(IntegerType.INSTANCE, value));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>If exp1 type is {@link PostgreInetType},then {@link PostgreInetType}</li>
     * <li>If exp1 type is {@link PostgreCidrType},then {@link PostgreCidrType}</li>
     * <li>Else The {@link MappingType} of exp1</li>
     * </ul>
     *
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
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">text ( inet ) → text<br/>
     * Returns the unabbreviated IP address and netmask length as text. (This has the same result as an explicit cast to text.)<br/>
     * text(inet '192.168.1.5') → 192.168.1.5/32
     * </a>
     */
    public static SimpleExpression text(Expression inet) {
        return FunctionUtils.oneArgFunc("text", inet, StringType.INSTANCE);
    }

    /*-------------------below  MAC Address Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: The {@link MappingType} of macAddr8
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-net.html#CIDR-INET-FUNCTIONS-TABLE">macaddr8_set7bit ( macaddr8 ) → macaddr8<br/>
     * Sets the 7th bit of the address to one, creating what is known as modified EUI-64, for inclusion in an IPv6 address.<br/>
     * macaddr8_set7bit(macaddr8 '00:34:56:ab:cd:ef') → 02:34:56:ff:fe:ab:cd:ef
     * </a>
     */
    public static SimpleExpression macAddr8Set7bit(Expression macAddr8) {
        return FunctionUtils.oneArgFunc("macaddr8_set7bit", macAddr8, _returnType(macAddr8, Expressions::identityType));
    }




    /*-------------------below Comment Information Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-COMMENT-TABLE">col_description ( table oid, column integer ) → text<br/>
     * </a>
     */
    public static SimpleExpression colDescription(Expression table, Expression column) {
        return FunctionUtils.twoArgFunc("col_description", table, column, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-COMMENT-TABLE">obj_description ( object oid, catalog name ) → text<br/>
     * </a>
     */
    public static SimpleExpression objDescription(Expression object, Expression catalog) {
        return FunctionUtils.twoArgFunc("obj_description", object, catalog, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-COMMENT-TABLE">obj_description ( object oid ) → text<br/>
     * </a>
     */
    public static SimpleExpression objDescription(Expression object) {
        return FunctionUtils.oneArgFunc("obj_description", object, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-COMMENT-TABLE">shobj_description ( object oid, catalog name ) → text<br/>
     * </a>
     */
    public static SimpleExpression shObjDescription(Expression object, Expression catalog) {
        return FunctionUtils.twoArgFunc("shobj_description", object, catalog, TextType.INSTANCE);
    }

    /*-------------------below Transaction ID and Snapshot Information Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link LongType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-PG-SNAPSHOT">pg_current_xact_id () → xid8<br/>
     * </a>
     */
    public static SimpleExpression pgCurrentXactId() {
        return FunctionUtils.zeroArgFunc("pg_current_xact_id", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link LongType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-PG-SNAPSHOT">pg_current_xact_id_if_assigned () → xid8<br/>
     * </a>
     */
    public static SimpleExpression pgCurrentXactIdIfAssigned() {
        //TODO xid8 is binary ?
        return FunctionUtils.zeroArgFunc("pg_current_xact_id_if_assigned", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-PG-SNAPSHOT">pg_xact_status ( xid8 ) → text<br/>
     * </a>
     */
    public static SimpleExpression pgXactStatus(Expression xid8) {
        return FunctionUtils.oneArgFunc("pg_xact_status", xid8, TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-PG-SNAPSHOT">pg_current_snapshot () → pg_snapshot<br/>
     * </a>
     */
    public static SimpleExpression pgCurrentSnapshot() {
        //TODO pg_snapshot what type?
        return FunctionUtils.zeroArgFunc("pg_current_snapshot", TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: <ul>
     * <li> "Anonymous field" ( you must use as clause definite filed name) : {@link LongType#INSTANCE}</li>
     * <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-PG-SNAPSHOT">pg_snapshot_xip ( pg_snapshot ) → setof xid8<br/>
     * </a>
     */
    public static _ColumnWithOrdinalityFunction pgSnapshotXip(Expression pgSnapshot) {
        return DialectFunctionUtils.oneArgColumnFunction("pg_snapshot_xip", pgSnapshot, null, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link LongType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-PG-SNAPSHOT">pg_snapshot_xmax ( pg_snapshot ) → xid8<br/>
     * </a>
     */
    public static SimpleExpression pgSnapshotXMax(Expression pgSnapshot) {
        return FunctionUtils.oneArgFunc("pg_snapshot_xmax", pgSnapshot, LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link LongType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-PG-SNAPSHOT">pg_snapshot_xmin ( pg_snapshot ) → xid8<br/>
     * </a>
     */
    public static SimpleExpression pgSnapshotXMin(Expression pgSnapshot) {
        return FunctionUtils.oneArgFunc("pg_snapshot_xmin", pgSnapshot, LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-PG-SNAPSHOT">pg_visible_in_snapshot ( xid8, pg_snapshot ) → boolean<br/>
     * </a>
     */
    public static SimplePredicate pgVisibleInSnapshot(Expression xid8, Expression pgSnapshot) {
        return FunctionUtils.twoArgPredicateFunc("pg_visible_in_snapshot", xid8, pgSnapshot);
    }


    /*-------------------below Deprecated Transaction ID and Snapshot Information Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link LongType#INSTANCE}
     *
     * <p>
     * <strong>Deprecated</strong> as of postgre 13
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-TXID-SNAPSHOT">txid_current () → bigint<br/>
     * See pg_current_xact_id().
     * </a>
     */
    public static SimpleExpression txidCurrent() {
        return FunctionUtils.zeroArgFunc("txid_current", LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link LongType#INSTANCE}
     *
     * <p><strong>Deprecated</strong> as of postgre 13
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-TXID-SNAPSHOT">txid_current_if_assigned () → bigint
     * See pg_current_xact_id_if_assigned().
     * </a>
     */
    public static SimpleExpression txidCurrentIfAssigned() {
        return FunctionUtils.zeroArgFunc("txid_current_if_assigned", LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     * <p>
     * <strong>Deprecated</strong> as of postgre 13
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-TXID-SNAPSHOT">txid_current_snapshot () → txid_snapshot<br/>
     * See pg_current_snapshot().
     * </a>
     */
    public static SimpleExpression txidCurrentSnapshot() {
        //TODO what type is txid_snapshot ?
        return FunctionUtils.zeroArgFunc("txid_current_snapshot", TextType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: <ul>
     * <li> "Anonymous field" ( you must use as clause definite filed name) : {@link LongType#INSTANCE}</li>
     * <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     * <p>
     * <strong>Deprecated</strong> as of postgre 13
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-TXID-SNAPSHOT">txid_snapshot_xip ( txid_snapshot ) → setof bigint<br/>
     * see pg_snapshot_xip()
     * </a>
     */
    public static _ColumnWithOrdinalityFunction txidSnapshotXip(Expression txidSnapshot) {
        return DialectFunctionUtils.oneArgColumnFunction("txid_snapshot_xip", txidSnapshot, null, LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link LongType#INSTANCE}
     *
     * <p>
     * <strong>Deprecated</strong> as of postgre 13
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-TXID-SNAPSHOT">txid_snapshot_xmax ( txid_snapshot ) → bigint<br/>
     * See pg_snapshot_xmax().
     * </a>
     */
    public static SimpleExpression txidSnapshotXMax(Expression txidSnapshot) {
        return FunctionUtils.oneArgFunc("txid_snapshot_xmax", txidSnapshot, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link LongType#INSTANCE}
     *
     * <p>
     * <strong>Deprecated</strong> as of postgre 13
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-TXID-SNAPSHOT">txid_snapshot_xmin ( txid_snapshot ) → bigint<br/>
     * See pg_snapshot_xmin().
     * </a>
     */
    public static SimpleExpression txidSnapshotXMin(Expression txidSnapshot) {
        return FunctionUtils.oneArgFunc("txid_snapshot_xmin", txidSnapshot, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link BooleanType#INSTANCE}
     *
     * <p>
     * <strong>Deprecated</strong> as of postgre 13
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-TXID-SNAPSHOT">txid_visible_in_snapshot ( bigint, txid_snapshot ) → boolean<br/>
     * See pg_visible_in_snapshot().
     * </a>
     */
    public static SimplePredicate txidVisibleInSnapshot(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgPredicateFunc("txid_visible_in_snapshot", exp1, exp2);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link TextType#INSTANCE}
     *
     * <p>
     * <strong>Deprecated</strong> as of postgre 13
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-TXID-SNAPSHOT">txid_status ( bigint ) → text<br/>
     * See pg_xact_status().
     * </a>
     */
    public static SimpleExpression txidStatus(Expression exp) {
        return FunctionUtils.oneArgFunc("txid_status", exp, TextType.INSTANCE);
    }


    /*-------------------below Committed Transaction Information Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link OffsetDateTimeType#INSTANCE}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-COMMIT-TIMESTAMP">pg_xact_commit_timestamp ( xid ) → timestamp with time zone<br/>
     * </a>
     */
    public static SimpleExpression pgXactCommitTimestamp(Expression xid) {
        return FunctionUtils.oneArgFunc("pg_xact_commit_timestamp", xid, OffsetDateTimeType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>timestamp : {@link OffsetDateTimeType#INSTANCE}</li>
     *     <li>roident : {@link LongType#INSTANCE}</li>
     *     <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-COMMIT-TIMESTAMP">pg_xact_commit_timestamp_origin ( xid ) → record ( timestamp timestamp with time zone, roident oid)<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction pgXactCommitTimestampOrigin(Expression xid) {
        final List<Selection> fieldList;
        fieldList = ArrayUtils.of(
                ArmySelections.forName("timestamp", OffsetDateTimeType.INSTANCE),
                ArmySelections.forName("roident", LongType.INSTANCE)
        );
        return DialectFunctionUtils.oneArgTabularFunc("pg_xact_commit_timestamp_origin", xid, fieldList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>xid : {@link IntegerType#INSTANCE}</li>
     *     <li>timestamp : {@link OffsetDateTimeType#INSTANCE}</li>
     *     <li>roident : {@link LongType#INSTANCE}</li>
     *     <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-COMMIT-TIMESTAMP">pg_last_committed_xact () → record ( xid xid, timestamp timestamp with time zone, roident oid )<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction pgLastCommittedXact() {
        final List<Selection> fieldList;
        fieldList = ArrayUtils.of(
                ArmySelections.forName("xid", IntegerType.INSTANCE),
                ArmySelections.forName("timestamp", OffsetDateTimeType.INSTANCE),
                ArmySelections.forName("roident", LongType.INSTANCE)
        );
        return DialectFunctionUtils.zeroArgTabularFunc("pg_last_committed_xact", fieldList);
    }


    /*-------------------below Control Data Functions-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>checkpoint_lsn : {@link PostgrePgLsnType#LONG}</li>
     *     <li>redo_lsn : {@link PostgrePgLsnType#LONG}</li>
     *     <li>redo_wal_file : {@link TextType#INSTANCE}</li>
     *     <li>timeline_id : {@link IntegerType#INSTANCE}</li>
     *
     *     <li>prev_timeline_id : {@link IntegerType#INSTANCE}</li>
     *     <li>full_page_writes : {@link BooleanType#INSTANCE}</li>
     *     <li>next_xid : {@link TextType#INSTANCE}</li>
     *     <li>next_oid : {@link LongType#INSTANCE}</li>
     *
     *     <li>next_multixact_id : {@link IntegerType#INSTANCE}</li>
     *     <li>next_multi_offset : {@link IntegerType#INSTANCE}</li>
     *     <li>oldest_xid : {@link IntegerType#INSTANCE}</li>
     *     <li>oldest_xid_dbid : {@link LongType#INSTANCE}</li>
     *
     *     <li>oldest_active_xid : {@link IntegerType#INSTANCE}</li>
     *     <li>oldest_multi_xid : {@link IntegerType#INSTANCE}</li>
     *     <li>oldest_multi_dbid : {@link LongType#INSTANCE}</li>
     *     <li>oldest_commit_ts_xid : {@link IntegerType#INSTANCE}</li>
     *
     *     <li>newest_commit_ts_xid : {@link IntegerType#INSTANCE}</li>
     *     <li>checkpoint_time : {@link OffsetDateTimeType#INSTANCE}</li>
     *
     *     <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-COMMIT-TIMESTAMP">pg_control_checkpoint () → record<br/>
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-PG-CONTROL-CHECKPOINT">pg_control_checkpoint Output Columns<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction pgControlCheckpoint() {
        final List<Selection> fieldList;
        fieldList = _Collections.arrayList(18);

        fieldList.add(ArmySelections.forName("checkpoint_lsn", PostgrePgLsnType.LONG));
        fieldList.add(ArmySelections.forName("redo_lsn", PostgrePgLsnType.LONG));
        fieldList.add(ArmySelections.forName("redo_wal_file", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("timeline_id", IntegerType.INSTANCE));

        fieldList.add(ArmySelections.forName("prev_timeline_id", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("full_page_writes", BooleanType.INSTANCE));
        fieldList.add(ArmySelections.forName("next_xid", TextType.INSTANCE));
        fieldList.add(ArmySelections.forName("next_oid", LongType.INSTANCE));

        fieldList.add(ArmySelections.forName("next_multixact_id", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("next_multi_offset", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("oldest_xid", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("oldest_xid_dbid", LongType.INSTANCE));

        fieldList.add(ArmySelections.forName("oldest_active_xid", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("oldest_multi_xid", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("oldest_multi_dbid", LongType.INSTANCE));
        fieldList.add(ArmySelections.forName("oldest_commit_ts_xid", IntegerType.INSTANCE));

        fieldList.add(ArmySelections.forName("newest_commit_ts_xid", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("checkpoint_time", OffsetDateTimeType.INSTANCE));

        return DialectFunctionUtils.zeroArgTabularFunc("pg_control_checkpoint", fieldList);
    }


    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>pg_control_version : {@link IntegerType#INSTANCE}</li>
     *     <li>catalog_version_no : {@link IntegerType#INSTANCE}</li>
     *     <li>system_identifier : {@link LongType#INSTANCE}</li>
     *     <li>pg_control_last_modified : {@link OffsetDateTimeType#INSTANCE}</li>
     *     <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-COMMIT-TIMESTAMP">pg_control_system () → record<br/>
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-PG-CONTROL-SYSTEM">pg_control_system Output Columns<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction pgControlSystem() {
        final List<Selection> fieldList;
        fieldList = ArrayUtils.of(
                ArmySelections.forName("pg_control_version", IntegerType.INSTANCE),
                ArmySelections.forName("catalog_version_no", IntegerType.INSTANCE),
                ArmySelections.forName("system_identifier", LongType.INSTANCE),
                ArmySelections.forName("pg_control_last_modified", OffsetDateTimeType.INSTANCE)
        );

        return DialectFunctionUtils.zeroArgTabularFunc("pg_control_system", fieldList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>max_data_alignment : {@link IntegerType#INSTANCE}</li>
     *     <li>database_block_size : {@link IntegerType#INSTANCE}</li>
     *     <li>blocks_per_segment : {@link IntegerType#INSTANCE}</li>
     *     <li>wal_block_size : {@link IntegerType#INSTANCE}</li>
     *
     *     <li>bytes_per_wal_segment : {@link IntegerType#INSTANCE}</li>
     *     <li>max_identifier_length : {@link IntegerType#INSTANCE}</li>
     *     <li>max_index_columns : {@link IntegerType#INSTANCE}</li>
     *     <li>max_toast_chunk_size : {@link IntegerType#INSTANCE}</li>
     *
     *     <li>large_object_chunk_size : {@link IntegerType#INSTANCE}</li>
     *     <li>float8_pass_by_value : {@link BooleanType#INSTANCE}</li>
     *     <li>data_page_checksum_version : {@link IntegerType#INSTANCE}</li>
     *
     *     <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-COMMIT-TIMESTAMP">pg_control_init () → record<br/>
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-PG-CONTROL-INIT">pg_control_init Output Columns<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction pgControlInit() {
        final List<Selection> fieldList;
        fieldList = _Collections.arrayList(11);

        fieldList.add(ArmySelections.forName("max_data_alignment", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("database_block_size", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("blocks_per_segment", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("wal_block_size", IntegerType.INSTANCE));

        fieldList.add(ArmySelections.forName("bytes_per_wal_segment", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("max_identifier_length", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("max_index_columns", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("max_toast_chunk_size", IntegerType.INSTANCE));

        fieldList.add(ArmySelections.forName("large_object_chunk_size", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("float8_pass_by_value", BooleanType.INSTANCE));
        fieldList.add(ArmySelections.forName("data_page_checksum_version", IntegerType.INSTANCE));

        return DialectFunctionUtils.zeroArgTabularFunc("pg_control_init", fieldList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>min_recovery_end_lsn : {@link PostgrePgLsnType#LONG}</li>
     *     <li>min_recovery_end_timeline : {@link IntegerType#INSTANCE}</li>
     *     <li>backup_start_lsn : {@link PostgrePgLsnType#LONG}</li>
     *     <li>backup_end_lsn : {@link PostgrePgLsnType#LONG}</li>
     *
     *     <li>end_of_backup_record_required : {@link BooleanType#INSTANCE}</li>
     *
     *     <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-COMMIT-TIMESTAMP">pg_control_recovery () → record<br/>
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-PG-CONTROL-RECOVERY">pg_control_recovery Output Columns<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction pgControlRecovery() {
        final List<Selection> fieldList;
        fieldList = _Collections.arrayList(5);

        fieldList.add(ArmySelections.forName("min_recovery_end_lsn", PostgrePgLsnType.LONG));
        fieldList.add(ArmySelections.forName("min_recovery_end_timeline", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("backup_start_lsn", PostgrePgLsnType.LONG));
        fieldList.add(ArmySelections.forName("backup_end_lsn", PostgrePgLsnType.LONG));

        fieldList.add(ArmySelections.forName("end_of_backup_record_required", BooleanType.INSTANCE));

        return DialectFunctionUtils.zeroArgTabularFunc("pg_control_recovery", fieldList);
    }



    /*-------------------below Trigger Functions TODO ? -------------------*/


    /*-------------------below Event Trigger Functions TODO ? -------------------*/


    /*-------------------below Statistics Information Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>index : {@link IntegerType#INSTANCE}</li>
     *     <li>values : {@link TextArrayType#LINEAR}</li>
     *     <li>nulls : {@link BooleanArrayType#PRIMITIVE_LINEAR}</li>
     *     <li>frequency : {@link DoubleType#INSTANCE}</li>
     *
     *     <li>base_frequency : {@link DoubleType#INSTANCE}</li>
     *
     *     <li>ordinality (optional) : {@link LongType#INSTANCE} ,see {@link io.army.criteria.impl.Functions._WithOrdinalityClause}</li>
     * </ul>
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-statistics.html">Statistics Information Functions<br/>
     * </a>
     */
    public static _TabularWithOrdinalityFunction pgMcvListItems(Expression pgMcvList) {
        final List<Selection> fieldList = _Collections.arrayList(5);

        fieldList.add(ArmySelections.forName("index", IntegerType.INSTANCE));
        fieldList.add(ArmySelections.forName("values", TextArrayType.LINEAR));
        fieldList.add(ArmySelections.forName("nulls", BooleanArrayType.PRIMITIVE_LINEAR));
        fieldList.add(ArmySelections.forName("frequency", DoubleType.INSTANCE));

        fieldList.add(ArmySelections.forName("base_frequency", DoubleType.INSTANCE));

        return DialectFunctionUtils.oneArgTabularFunc("pg_mcv_list_items", pgMcvList, fieldList);
    }

    /*-------------------below sampling methods-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link VoidType#VOID}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html#SQL-FROM">TABLESAMPLE sampling_method ( argument [, ...] ) [ REPEATABLE ( seed ) ]<br/>
     * </a>
     */
    public static SimpleExpression bernoulli(Expression fraction) {
        return FunctionUtils.oneArgFunc("BERNOULLI", fraction, VoidType.VOID);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link VoidType#VOID}
     *
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html#SQL-FROM">TABLESAMPLE sampling_method ( argument [, ...] ) [ REPEATABLE ( seed ) ]<br/>
     * </a>
     */
    public static SimpleExpression system(Expression fraction) {
        return FunctionUtils.oneArgFunc("SYSTEM", fraction, VoidType.VOID);
    }

}
