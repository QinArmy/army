package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.mapping.*;
import io.army.type.Interval;

import java.util.Locale;
import java.util.function.Function;

abstract class PostgreMiscellaneousFunctions extends PostgreStringFunctions {

    PostgreMiscellaneousFunctions() {
    }


    public interface ExtractTimeField {

    }


    public static final ExtractTimeField CENTURY = WordExtractTimeField.CENTURY;
    public static final ExtractTimeField DAY = WordExtractTimeField.DAY;
    public static final ExtractTimeField DECADE = WordExtractTimeField.DECADE;
    public static final ExtractTimeField DOW = WordExtractTimeField.DOW;

    public static final ExtractTimeField DOY = WordExtractTimeField.DOY;
    public static final ExtractTimeField EPOCH = WordExtractTimeField.EPOCH;
    public static final ExtractTimeField HOUR = WordExtractTimeField.HOUR;
    public static final ExtractTimeField ISODOW = WordExtractTimeField.ISODOW;

    public static final ExtractTimeField ISOYEAR = WordExtractTimeField.ISOYEAR;
    public static final ExtractTimeField JULIAN = WordExtractTimeField.JULIAN;
    public static final ExtractTimeField MICROSECONDS = WordExtractTimeField.MICROSECONDS;
    public static final ExtractTimeField MILLENNIUM = WordExtractTimeField.MILLENNIUM;

    public static final ExtractTimeField MILLISECONDS = WordExtractTimeField.MILLISECONDS;
    public static final ExtractTimeField MINUTE = WordExtractTimeField.MINUTE;
    public static final ExtractTimeField MONTH = WordExtractTimeField.MONTH;
    public static final ExtractTimeField QUARTER = WordExtractTimeField.QUARTER;

    public static final ExtractTimeField SECOND = WordExtractTimeField.SECOND;
    public static final ExtractTimeField TIMEZONE = WordExtractTimeField.TIMEZONE;
    public static final ExtractTimeField TIMEZONE_HOUR = WordExtractTimeField.TIMEZONE_HOUR;
    public static final ExtractTimeField TIMEZONE_MINUTE = WordExtractTimeField.TIMEZONE_MINUTE;

    public static final ExtractTimeField WEEK = WordExtractTimeField.WEEK;
    public static final ExtractTimeField YEAR = WordExtractTimeField.YEAR;


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">current_date → date</a>
     */
    public static final Expression CURRENT_DATE = FunctionUtils.noParensFunc("CURRENT_DATE", LocalDateType.INSTANCE);

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  OffsetTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">current_time</a>
     */
    public static final Expression CURRENT_TIME = FunctionUtils.noParensFunc("CURRENT_TIME", OffsetTimeType.INSTANCE);

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">current_timestamp → timestamp with time zone</a>
     */
    public static final Expression CURRENT_TIMESTAMP = FunctionUtils.noParensFunc("CURRENT_TIMESTAMP", OffsetDateTimeType.INSTANCE);

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">localtime → time</a>
     */
    public static final Expression LOCALTIME = FunctionUtils.noParensFunc("LOCALTIME", LocalTimeType.INSTANCE);

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">localtimestamp → timestamp</a>
     */
    public static final Expression LOCALTIMESTAMP = FunctionUtils.noParensFunc("LOCALTIMESTAMP", LocalDateTimeType.INSTANCE);

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
        return FunctionUtils.zeroArgFunc("RANDOM", DoubleType.INSTANCE);
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
    public static Expression toChar(Expression exp, Expression format) {
        return FunctionUtils.twoArgFunc("TO_CHAR", exp, format, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-formatting.html#FUNCTIONS-FORMATTING-TABLE">to_date ( text, text ) → date</a>
     */
    public static Expression toDate(Expression exp, Expression format) {
        return FunctionUtils.twoArgFunc("TO_DATE", exp, format, LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-formatting.html#FUNCTIONS-FORMATTING-TABLE">to_number ( text, text ) → numeric</a>
     */
    public static Expression toNumber(Expression exp, Expression format) {
        return FunctionUtils.twoArgFunc("TO_NUMBER", exp, format, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-formatting.html#FUNCTIONS-FORMATTING-TABLE">to_timestamp ( text, text ) → timestamp with time zone</a>
     */
    public static Expression toTimestamp(Expression exp, Expression format) {
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
    public static Expression age(Expression timestamp) {
        return FunctionUtils.oneArgFunc("AGE", timestamp, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">age ( timestamp, timestamp ) → interval</a>
     */
    public static Expression age(Expression timestamp1, Expression timestamp2) {
        return FunctionUtils.twoArgFunc("AGE", timestamp1, timestamp2, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">clock_timestamp ( ) → timestamp with time zone</a>
     */
    public static Expression clockTimestamp() {
        return FunctionUtils.zeroArgFunc("CLOCK_TIMESTAMP", OffsetDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">current_time ( integer ) → time with time zone</a>
     */
    public static Expression currentTime(Expression integer) {
        return FunctionUtils.oneArgFunc("CURRENT_TIME", integer, OffsetTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">current_timestamp ( integer ) → timestamp with time zone</a>
     */
    public static Expression currentTimestamp(Expression integer) {
        return FunctionUtils.oneArgFunc("CURRENT_TIMESTAMP", integer, OffsetDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">date_bin ( interval, timestamp, timestamp ) → timestamp</a>
     */
    public static Expression dateBin(Expression interval, Expression timestamp1, Expression timestamp2) {
        return FunctionUtils.threeArgFunc("DATE_BIN", interval, timestamp1, timestamp2, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType}
     * </p>
     *
     * @param source timestamp or interval {@link Expression}
     * @see #datePart(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-EXTRACT">field</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">date_part ( text, timestamp ) → double precision <br/>
     * date_part ( text, interval ) → double precision
     * </a>
     */
    public static Expression datePart(final String field, final Expression source) {
        final String name = "DATE_PART";
        try {
            WordExtractTimeField.valueOf(field.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            String m = String.format("%s don't support field['%s'].", name, field);
            throw ContextStack.clearStackAndCriteriaError(m);
        }
        return datePart(SQLs.literal(StringType.INSTANCE, field), source);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType}
     * </p>
     *
     * @param source timestamp or interval {@link Expression}
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-EXTRACT">field</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">date_part ( text, timestamp ) → double precision <br/>
     * date_part ( text, interval ) → double precision
     * </a>
     */
    public static Expression datePart(Expression field, Expression source) {
        return FunctionUtils.twoArgFunc("DATE_PART", field, source, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <u>
     * <li>If the {@link MappingType} of source is {@link IntervalType},then the {@link MappingType} of source</li>
     * <li>Else {@link LocalDateTimeType}</li>
     * </u>
     * </p>
     *
     * @param field  lower field ,valid values for field are :
     *               <ul>
     *                   <li>microseconds</li>
     *                   <li>milliseconds</li>
     *                   <li>second</li>
     *                   <li>minute</li>
     *                   <li>hour</li>
     *                   <li>day</li>
     *                   <li>week</li>
     *                   <li>month</li>
     *                   <li>quarter</li>
     *                   <li>year</li>
     *                   <li>decade</li>
     *                   <li>century</li>
     *                   <li>millennium</li>
     *               </ul>
     * @param source timestamp or interval {@link Expression}
     * @see #dateTrunc(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">date_trunc ( text, timestamp ) → timestamp <br/>
     * date_trunc ( text, interval ) → interval
     * </a>
     */
    public static Expression dateTrunc(String field, Expression source) {
        if (isErrorDateTruncField(field)) {
            throw errorDateTruncField(field);
        }
        return dateTrunc(SQLs.literal(StringType.INSTANCE, field), source);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @param field  lower field ,valid values for field are :
     *               <ul>
     *                   <li>microseconds</li>
     *                   <li>milliseconds</li>
     *                   <li>second</li>
     *                   <li>minute</li>
     *                   <li>hour</li>
     *                   <li>day</li>
     *                   <li>week</li>
     *                   <li>month</li>
     *                   <li>quarter</li>
     *                   <li>year</li>
     *                   <li>decade</li>
     *                   <li>century</li>
     *                   <li>millennium</li>
     *               </ul>
     * @param source timestamp  {@link Expression}
     * @see #dateTrunc(Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">date_trunc ( text, timestamp with time zone, text ) → timestamp with time zone</a>
     */
    public static Expression dateTrunc(String field, Expression source, String timeZone) {
        if (isErrorDateTruncField(field)) {
            throw errorDateTruncField(field);
        }
        return dateTrunc(SQLs.literal(StringType.INSTANCE, field),
                source,
                SQLs.literal(StringType.INSTANCE, timeZone));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <u>
     * <li>If the {@link MappingType} of source is {@link IntervalType},then the {@link MappingType} of source</li>
     * <li>Else {@link LocalDateTimeType}</li>
     * </u>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">date_trunc ( text, timestamp ) → timestamp <br/>
     * date_trunc ( text, interval ) → interval
     * </a>
     */
    public static Expression dateTrunc(Expression field, Expression source) {
        return FunctionUtils.twoArgFunc("DATE_TRUNC", field, source,
                _returnType(source, PostgreMiscellaneousFunctions::intervalOrDateTime)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">date_trunc ( text, timestamp with time zone, text ) → timestamp with time zone</a>
     */
    public static Expression dateTrunc(Expression field, Expression source, Expression timeZone) {
        return FunctionUtils.threeArgFunc("DATE_TRUNC", field, source, timeZone, OffsetDateTimeType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">extract ( field from timestamp ) → numeric<br/>
     * extract ( field from interval ) → numeric
     * </a>
     */
    public static Expression extract(ExtractTimeField field, WordFrom from, Expression timestampOrInterval) {
        final String name = "EXTRACT";
        if (!(field instanceof WordExtractTimeField)) {
            throw CriteriaUtils.funcArgError(name, from);
        } else if (from != Functions.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        }
        return FunctionUtils.complexArgFunc(name, BigDecimalType.INSTANCE, field, from, timestampOrInterval);
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


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntervalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">justify_days ( interval ) → interval</a>
     */
    public static Expression justifyDays(Expression exp) {
        return FunctionUtils.oneArgFunc("JUSTIFY_DAYS", exp, IntervalType.from(Interval.class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntervalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">justify_hours ( interval ) → interval</a>
     */
    public static Expression justifyHours(Expression exp) {
        return FunctionUtils.oneArgFunc("JUSTIFY_HOURS", exp, IntervalType.from(Interval.class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntervalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">justify_interval ( interval ) → interval</a>
     */
    public static Expression justifyInterval(Expression exp) {
        return FunctionUtils.oneArgFunc("JUSTIFY_INTERVAL", exp, IntervalType.from(Interval.class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalTimeType}
     * </p>
     *
     * @see #LOCALTIME
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">localtime ( integer ) → time</a>
     */
    public static Expression localtime(Expression integer) {
        return FunctionUtils.oneArgFunc("LOCALTIME", integer, LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateTimeType}
     * </p>
     *
     * @see #LOCALTIMESTAMP
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">localtimestamp ( integer ) → timestamp</a>
     */
    public static Expression localtimestamp(Expression integer) {
        return FunctionUtils.oneArgFunc("LOCALTIMESTAMP", integer, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_date ( year int, month int, day int ) → date</a>
     */
    public static Expression makeDate(Expression year, Expression month, Expression day) {
        return FunctionUtils.threeArgFunc("MAKE_DATE", year, month, day, LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntervalType}
     * </p>
     *
     * @param years if Positional Notation ,then representing years,else Named Notation
     * @see Postgres#namedNotation(String, Expression)
     * @see Postgres#namedNotation(String, Function, Object)
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-POSITIONAL">Using Positional Notation</a>
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-NAMED">Using Named Notation</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    public static Expression makeInterval(Expression years) {
        return FunctionUtils.oneNotationFunc("MAKE_INTERVAL",
                PostgreMiscellaneousFunctions::isErrorMakeIntervalNotation,
                years, IntervalType.from(Interval.class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntervalType}
     * </p>
     *
     * @param years  if Positional Notation ,then representing years,else Named Notation
     * @param months if Positional Notation ,then representing months,else Named Notation
     * @see Postgres#namedNotation(String, Expression)
     * @see Postgres#namedNotation(String, Function, Object)
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-POSITIONAL">Using Positional Notation</a>
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-NAMED">Using Named Notation</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    public static Expression makeInterval(Expression years, Expression months) {
        return FunctionUtils.twoNotationFunc("MAKE_INTERVAL",
                PostgreMiscellaneousFunctions::isErrorMakeIntervalNotation,
                years, months, IntervalType.from(Interval.class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntervalType}
     * </p>
     *
     * @param years  if Positional Notation ,then representing years,else Named Notation
     * @param months if Positional Notation ,then representing months,else Named Notation
     * @param weeks  if Positional Notation ,then representing weeks,else Named Notation
     * @see Postgres#namedNotation(String, Expression)
     * @see Postgres#namedNotation(String, Function, Object)
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-POSITIONAL">Using Positional Notation</a>
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-NAMED">Using Named Notation</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    public static Expression makeInterval(Expression years, Expression months, Expression weeks) {
        return FunctionUtils.threeNotationFunc("MAKE_INTERVAL",
                PostgreMiscellaneousFunctions::isErrorMakeIntervalNotation,
                years, months, weeks, IntervalType.from(Interval.class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntervalType}
     * </p>
     *
     * @param years  if Positional Notation ,then representing years,else Named Notation
     * @param months if Positional Notation ,then representing months,else Named Notation
     * @param weeks  if Positional Notation ,then representing weeks,else Named Notation
     * @param days   if Positional Notation ,then representing days,else Named Notation
     * @see Postgres#namedNotation(String, Expression)
     * @see Postgres#namedNotation(String, Function, Object)
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-POSITIONAL">Using Positional Notation</a>
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-NAMED">Using Named Notation</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    public static Expression makeInterval(Expression years, Expression months, Expression weeks, Expression days) {
        return FunctionUtils.fourNotationFunc("MAKE_INTERVAL",
                PostgreMiscellaneousFunctions::isErrorMakeIntervalNotation,
                years, months, weeks, days, IntervalType.from(Interval.class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntervalType}
     * </p>
     *
     * @param years  if Positional Notation ,then representing years,else Named Notation
     * @param months if Positional Notation ,then representing months,else Named Notation
     * @param weeks  if Positional Notation ,then representing weeks,else Named Notation
     * @param days   if Positional Notation ,then representing days,else Named Notation
     * @param hours  if Positional Notation ,then representing hours,else Named Notation
     * @see Postgres#namedNotation(String, Expression)
     * @see Postgres#namedNotation(String, Function, Object)
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-POSITIONAL">Using Positional Notation</a>
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-NAMED">Using Named Notation</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    public static Expression makeInterval(Expression years, Expression months, Expression weeks, Expression days,
                                          Expression hours) {
        return FunctionUtils.fiveNotationFunc("MAKE_INTERVAL",
                PostgreMiscellaneousFunctions::isErrorMakeIntervalNotation,
                years, months, weeks, days, hours, IntervalType.from(Interval.class));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntervalType}
     * </p>
     *
     * @param years  if Positional Notation ,then representing years,else Named Notation
     * @param months if Positional Notation ,then representing months,else Named Notation
     * @param weeks  if Positional Notation ,then representing weeks,else Named Notation
     * @param days   if Positional Notation ,then representing days,else Named Notation
     * @param hours  if Positional Notation ,then representing hours,else Named Notation
     * @param mins   if Positional Notation ,then representing mins,else Named Notation
     * @see Postgres#namedNotation(String, Expression)
     * @see Postgres#namedNotation(String, Function, Object)
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-POSITIONAL">Using Positional Notation</a>
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-NAMED">Using Named Notation</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    public static Expression makeInterval(Expression years, Expression months, Expression weeks, Expression days,
                                          Expression hours, Expression mins) {
        return FunctionUtils.sixNotationFunc("MAKE_INTERVAL",
                PostgreMiscellaneousFunctions::isErrorMakeIntervalNotation,
                years, months, weeks, days, hours, mins, IntervalType.from(Interval.class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntervalType}
     * </p>
     *
     * @param years  if Positional Notation ,then representing years,else Named Notation
     * @param months if Positional Notation ,then representing months,else Named Notation
     * @param weeks  if Positional Notation ,then representing weeks,else Named Notation
     * @param days   if Positional Notation ,then representing days,else Named Notation
     * @param hours  if Positional Notation ,then representing hours,else Named Notation
     * @param mins   if Positional Notation ,then representing mins,else Named Notation
     * @param secs   if Positional Notation ,then representing secs,else Named Notation
     * @see Postgres#namedNotation(String, Expression)
     * @see Postgres#namedNotation(String, Function, Object)
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-POSITIONAL">Using Positional Notation</a>
     * @see <a href="https://www.postgresql.org/docs/15/sql-syntax-calling-funcs.html#SQL-SYNTAX-CALLING-FUNCS-NAMED">Using Named Notation</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    public static Expression makeInterval(Expression years, Expression months, Expression weeks, Expression days,
                                          Expression hours, Expression mins, Expression secs) {

        return FunctionUtils.sevenNotationFunc("MAKE_INTERVAL",
                PostgreMiscellaneousFunctions::isErrorMakeIntervalNotation,
                years, months, weeks, days, hours, mins, secs,
                IntervalType.from(Interval.class));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_time ( hour int, min int, sec double precision ) → time</a>
     */
    public static Expression makeTime(Expression hour, Expression min, Expression sec) {
        return FunctionUtils.threeArgFunc("MAKE_TIME", hour, min, sec, LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_timestamp ( year int, month int, day int, hour int, min int, sec double precision ) → timestamp</a>
     */
    public static Expression makeTimestamp(Expression year, Expression month, Expression day, Expression hour,
                                           Expression min, Expression sec) {
        return FunctionUtils.sixArgFunc("MAKE_TIMESTAMP", year, month, day, hour, min, sec, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_timestamptz ( year int, month int, day int, hour int, min int, sec double precision [, timezone text ] ) → timestamp with time zone</a>
     */
    public static Expression makeTimestampTz(Expression year, Expression month, Expression day, Expression hour,
                                             Expression min, Expression sec) {
        return FunctionUtils.sixArgFunc("MAKE_TIMESTAMPTZ", year, month, day, hour, min, sec,
                OffsetDateTimeType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_timestamptz ( year int, month int, day int, hour int, min int, sec double precision [, timezone text ] ) → timestamp with time zone</a>
     */
    public static Expression makeTimestampTz(Expression year, Expression month, Expression day, Expression hour,
                                             Expression min, Expression sec, Expression timeZone) {
        return FunctionUtils.sevenArgFunc("MAKE_TIMESTAMPTZ",
                year, month, day, hour, min, sec, timeZone,
                OffsetDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">now ( ) → timestamp with time zone</a>
     */
    public static Expression now() {
        return FunctionUtils.zeroArgFunc("NOW", OffsetDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">statement_timestamp ( ) → timestamp with time zone</a>
     */
    public static Expression statementTimestamp() {
        return FunctionUtils.zeroArgFunc("STATEMENT_TIMESTAMP", OffsetDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">timeofday ( ) → text</a>
     */
    public static Expression timeOfDay() {
        return FunctionUtils.zeroArgFunc("TIMEOFDAY", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">transaction_timestamp ( ) → timestamp with time zone</a>
     */
    public static Expression transactionTimestamp() {
        return FunctionUtils.zeroArgFunc("TRANSACTION_TIMESTAMP", OffsetDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">to_timestamp ( double precision ) → timestamp with time zone</a>
     */
    public static Expression toTimestamp(Expression exp) {
        return FunctionUtils.oneArgFunc("TO_TIMESTAMP", exp, OffsetDateTimeType.INSTANCE);
    }





    /*-------------------below private -------------------*/

    /**
     * @see #dateTrunc(Expression, Expression)
     */
    private static MappingType intervalOrDateTime(final MappingType type) {
        final MappingType returnType;
        if (type instanceof IntervalType) {
            returnType = type;
        } else {
            returnType = LocalDateTimeType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see #makeInterval(Expression, Expression, Expression, Expression, Expression, Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_interval ( [ years int [, months int [, weeks int [, days int [, hours int [, mins int [, secs double precision ]]]]]]] ) → interval</a>
     */
    private static boolean isErrorMakeIntervalNotation(final String argName) {
        final boolean error;
        switch (argName) {
            case "years":
            case "months":
            case "weeks":
            case "days":
            case "hours":
            case "mins":
            case "secs":
                error = false;
                break;
            default:
                error = true;
        }
        return error;
    }


    /**
     * @see #dateTrunc(String, Expression)
     * @see #dateTrunc(String, Expression, String)
     */
    private static CriteriaException errorDateTruncField(String field) {
        String m = String.format("'%s' isn't valid field for date_trunc() function.", field);
        return ContextStack.clearStackAndCriteriaError(m);
    }

    /**
     * @see #dateTrunc(String, Expression)
     * @see #dateTrunc(String, Expression, String)
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">date_trunc ( text, timestamp with time zone, text ) → timestamp with time zone</a>
     */
    private static boolean isErrorDateTruncField(final String field) {
        final boolean error;
        switch (field) {
            case "microseconds":
            case "milliseconds":
            case "second":
            case "minute":

            case "hour":
            case "day":
            case "week":
            case "month":

            case "quarter":
            case "year":
            case "decade":
            case "century":

            case "millennium":
                error = false;
                break;
            default:
                error = true;
        }
        return error;
    }


    private enum WordExtractTimeField implements ExtractTimeField, ArmyKeyWord {

        CENTURY(" CENTURY"),
        DAY(" DAY"),
        DECADE(" DECADE"),
        DOW(" DOW"),

        DOY(" DOY"),
        EPOCH(" EPOCH"),
        HOUR(" HOUR"),
        ISODOW(" ISODOW"),

        ISOYEAR(" ISOYEAR"),
        JULIAN(" JULIAN"),
        MICROSECONDS(" MICROSECONDS"),
        MILLENNIUM(" MILLENNIUM"),

        MILLISECONDS(" MILLISECONDS"),
        MINUTE(" MINUTE"),
        MONTH(" MONTH"),
        QUARTER(" QUARTER"),

        SECOND(" SECOND"),
        TIMEZONE(" TIMEZONE"),
        TIMEZONE_HOUR(" TIMEZONE_HOUR"),
        TIMEZONE_MINUTE(" TIMEZONE_MINUTE"),

        WEEK(" WEEK"),
        YEAR(" YEAR");

        private final String spaceWord;

        WordExtractTimeField(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return Postgres.keyWordToString(this);
        }


    }//WordTimeField


}
