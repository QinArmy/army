package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.mapping.*;

abstract class PostgreMiscellaneousFunctions extends PostgreStringFunctions {

    PostgreMiscellaneousFunctions() {
    }


    public interface TimeField {

    }


    public static final TimeField CENTURY = WordTimeField.CENTURY;
    public static final TimeField DAY = WordTimeField.DAY;
    public static final TimeField DECADE = WordTimeField.DECADE;
    public static final TimeField DOW = WordTimeField.DOW;

    public static final TimeField DOY = WordTimeField.DOY;
    public static final TimeField EPOCH = WordTimeField.EPOCH;
    public static final TimeField HOUR = WordTimeField.HOUR;
    public static final TimeField ISODOW = WordTimeField.ISODOW;

    public static final TimeField ISOYEAR = WordTimeField.ISOYEAR;
    public static final TimeField JULIAN = WordTimeField.JULIAN;
    public static final TimeField MICROSECONDS = WordTimeField.MICROSECONDS;
    public static final TimeField MILLENNIUM = WordTimeField.MILLENNIUM;

    public static final TimeField MILLISECONDS = WordTimeField.MILLISECONDS;
    public static final TimeField MINUTE = WordTimeField.MINUTE;
    public static final TimeField MONTH = WordTimeField.MONTH;
    public static final TimeField QUARTER = WordTimeField.QUARTER;

    public static final TimeField SECOND = WordTimeField.SECOND;
    public static final TimeField TIMEZONE = WordTimeField.TIMEZONE;
    public static final TimeField TIMEZONE_HOUR = WordTimeField.TIMEZONE_HOUR;
    public static final TimeField TIMEZONE_MINUTE = WordTimeField.TIMEZONE_MINUTE;

    public static final TimeField WEEK = WordTimeField.WEEK;
    public static final TimeField YEAR = WordTimeField.YEAR;


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
        return FunctionUtils.noArgFunc("CLOCK_TIMESTAMP", OffsetDateTimeType.INSTANCE);
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
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">date_part ( text, timestamp ) → double precision <br/>
     * date_part ( text, interval ) → double precision
     * </a>
     */
    public static Expression datePart(Expression text, Expression timestampOrInterval) {
        return FunctionUtils.twoArgFunc("DATE_PART", text, timestampOrInterval, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <u>
     * <li>If the {@link MappingType} of timestampOrInterval is {@link LocalDateTimeType},then the {@link MappingType} of timestampOrInterval</li>
     * <li>Else {@link StringType}</li>
     * </u>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">date_trunc ( text, timestamp ) → timestamp <br/>
     * date_trunc ( text, interval ) → interval
     * </a>
     */
    public static Expression dateTrunc(Expression text, Expression timestampOrInterval) {
        return FunctionUtils.twoArgFunc("DATE_TRUNC", text, timestampOrInterval, _returnType(timestampOrInterval, PostgreMiscellaneousFunctions::localDateTimeOrString));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">date_trunc ( text, timestamp with time zone, text ) → timestamp with time zone</a>
     */
    public static Expression dateTrunc(Expression text, Expression timestamp, Expression text2) {
        return FunctionUtils.threeArgFunc("DATE_TRUNC", text, timestamp, text2, OffsetDateTimeType.INSTANCE);
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
    public static Expression extract(TimeField field, WordFrom from, Expression timestampOrInterval) {
        final String name = "EXTRACT";
        if (!(field instanceof WordTimeField)) {
            throw CriteriaUtils.funcArgError(name, from);
        }
        if (from != Functions.FROM) {
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





    /*-------------------below private -------------------*/

    private static MappingType localDateTimeOrString(final MappingType type) {
        final MappingType returnType;
        if (type instanceof LocalDateTimeType) {
            returnType = type;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }


    private enum WordTimeField implements TimeField, ArmyKeyWord {

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

        WordTimeField(String spaceWord) {
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
