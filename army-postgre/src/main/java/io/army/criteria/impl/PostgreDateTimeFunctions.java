package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;
import io.army.mapping.*;
import io.army.mapping.optional.IntervalType;
import io.army.type.Interval;

import java.util.Locale;
import java.util.function.Function;


/**
 * <p>
 * Package class,This class hold postgre data/time function methods.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">Date/Time Functions</a>
 * @since 1.0
 */
abstract class PostgreDateTimeFunctions extends PostgreStringFunctions {

    /**
     * package constructor
     */
    PostgreDateTimeFunctions() {
    }


    public interface ExtractTimeField {

    }


    public static final ExtractTimeField CENTURY = PostgreWords.WordExtractTimeField.CENTURY;
    public static final ExtractTimeField DAY = PostgreWords.WordExtractTimeField.DAY;
    public static final ExtractTimeField DECADE = PostgreWords.WordExtractTimeField.DECADE;
    public static final ExtractTimeField DOW = PostgreWords.WordExtractTimeField.DOW;
    public static final ExtractTimeField DOY = PostgreWords.WordExtractTimeField.DOY;
    public static final ExtractTimeField EPOCH = PostgreWords.WordExtractTimeField.EPOCH;
    public static final ExtractTimeField HOUR = PostgreWords.WordExtractTimeField.HOUR;
    public static final ExtractTimeField ISODOW = PostgreWords.WordExtractTimeField.ISODOW;
    public static final ExtractTimeField ISOYEAR = PostgreWords.WordExtractTimeField.ISOYEAR;
    public static final ExtractTimeField JULIAN = PostgreWords.WordExtractTimeField.JULIAN;
    public static final ExtractTimeField MICROSECONDS = PostgreWords.WordExtractTimeField.MICROSECONDS;
    public static final ExtractTimeField MILLENNIUM = PostgreWords.WordExtractTimeField.MILLENNIUM;
    public static final ExtractTimeField MILLISECONDS = PostgreWords.WordExtractTimeField.MILLISECONDS;
    public static final ExtractTimeField MINUTE = PostgreWords.WordExtractTimeField.MINUTE;
    public static final ExtractTimeField MONTH = PostgreWords.WordExtractTimeField.MONTH;
    public static final ExtractTimeField QUARTER = PostgreWords.WordExtractTimeField.QUARTER;
    public static final ExtractTimeField SECOND = PostgreWords.WordExtractTimeField.SECOND;
    public static final ExtractTimeField TIMEZONE = PostgreWords.WordExtractTimeField.TIMEZONE;
    public static final ExtractTimeField TIMEZONE_HOUR = PostgreWords.WordExtractTimeField.TIMEZONE_HOUR;
    public static final ExtractTimeField TIMEZONE_MINUTE = PostgreWords.WordExtractTimeField.TIMEZONE_MINUTE;
    public static final ExtractTimeField WEEK = PostgreWords.WordExtractTimeField.WEEK;
    public static final ExtractTimeField YEAR = PostgreWords.WordExtractTimeField.YEAR;
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


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">clock_timestamp ( ) → timestamp with time zone</a>
     */
    public static SimpleExpression clockTimestamp() {
        return FunctionUtils.zeroArgFunc("CLOCK_TIMESTAMP", OffsetDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">current_time ( integer ) → time with time zone</a>
     */
    public static SimpleExpression currentTime(Expression integer) {
        return FunctionUtils.oneArgFunc("CURRENT_TIME", integer, OffsetTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">current_timestamp ( integer ) → timestamp with time zone</a>
     */
    public static SimpleExpression currentTimestamp(Expression integer) {
        return FunctionUtils.oneArgFunc("CURRENT_TIMESTAMP", integer, OffsetDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">date_bin ( interval, timestamp, timestamp ) → timestamp</a>
     */
    public static SimpleExpression dateBin(Expression interval, Expression timestamp1, Expression timestamp2) {
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
    public static SimpleExpression datePart(final String field, final Expression source) {
        final String name = "DATE_PART";
        try {
            PostgreWords.WordExtractTimeField.valueOf(field.toUpperCase(Locale.ROOT));
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
    public static SimpleExpression datePart(Expression field, Expression source) {
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
    public static SimpleExpression dateTrunc(String field, Expression source) {
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
    public static SimpleExpression dateTrunc(String field, Expression source, String timeZone) {
        if (PostgreDateTimeFunctions.isErrorDateTruncField(field)) {
            throw PostgreDateTimeFunctions.errorDateTruncField(field);
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
    public static SimpleExpression dateTrunc(Expression field, Expression source) {
        return FunctionUtils.twoArgFunc("DATE_TRUNC", field, source,
                _returnType(source, PostgreDateTimeFunctions::intervalOrDateTime)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">date_trunc ( text, timestamp with time zone, text ) → timestamp with time zone</a>
     */
    public static SimpleExpression dateTrunc(Expression field, Expression source, Expression timeZone) {
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
    public static SimpleExpression extract(ExtractTimeField field, WordFrom from, Expression timestampOrInterval) {
        final String name = "EXTRACT";
        if (!(field instanceof PostgreWords.WordExtractTimeField)) {
            throw CriteriaUtils.funcArgError(name, from);
        } else if (from != SQLs.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        }
        return FunctionUtils.complexArgFunc(name, BigDecimalType.INSTANCE, field, from, timestampOrInterval);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntervalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">justify_days ( interval ) → interval</a>
     */
    public static SimpleExpression justifyDays(Expression exp) {
        return FunctionUtils.oneArgFunc("JUSTIFY_DAYS", exp, IntervalType.from(Interval.class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntervalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">justify_hours ( interval ) → interval</a>
     */
    public static SimpleExpression justifyHours(Expression exp) {
        return FunctionUtils.oneArgFunc("JUSTIFY_HOURS", exp, IntervalType.from(Interval.class));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntervalType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">justify_interval ( interval ) → interval</a>
     */
    public static SimpleExpression justifyInterval(Expression exp) {
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
    public static SimpleExpression localtime(Expression integer) {
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
    public static SimpleExpression localtimestamp(Expression integer) {
        return FunctionUtils.oneArgFunc("LOCALTIMESTAMP", integer, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_date ( year int, month int, day int ) → date</a>
     */
    public static SimpleExpression makeDate(Expression year, Expression month, Expression day) {
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
    public static SimpleExpression makeInterval(Expression years) {
        return FunctionUtils.oneNotationFunc("MAKE_INTERVAL",
                PostgreDateTimeFunctions::isErrorMakeIntervalNotation,
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
    public static SimpleExpression makeInterval(Expression years, Expression months) {
        return FunctionUtils.twoNotationFunc("MAKE_INTERVAL",
                PostgreDateTimeFunctions::isErrorMakeIntervalNotation,
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
    public static SimpleExpression makeInterval(Expression years, Expression months, Expression weeks) {
        return FunctionUtils.threeNotationFunc("MAKE_INTERVAL",
                PostgreDateTimeFunctions::isErrorMakeIntervalNotation,
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
    public static SimpleExpression makeInterval(Expression years, Expression months, Expression weeks, Expression days) {
        return FunctionUtils.fourNotationFunc("MAKE_INTERVAL",
                PostgreDateTimeFunctions::isErrorMakeIntervalNotation,
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
    public static SimpleExpression makeInterval(Expression years, Expression months, Expression weeks, Expression days,
                                                Expression hours) {
        return FunctionUtils.fiveNotationFunc("MAKE_INTERVAL",
                PostgreDateTimeFunctions::isErrorMakeIntervalNotation,
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
    public static SimpleExpression makeInterval(Expression years, Expression months, Expression weeks, Expression days,
                                                Expression hours, Expression mins) {
        return FunctionUtils.sixNotationFunc("MAKE_INTERVAL",
                PostgreDateTimeFunctions::isErrorMakeIntervalNotation,
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
    public static SimpleExpression makeInterval(Expression years, Expression months, Expression weeks, Expression days,
                                                Expression hours, Expression mins, Expression secs) {

        return FunctionUtils.sevenNotationFunc("MAKE_INTERVAL",
                PostgreDateTimeFunctions::isErrorMakeIntervalNotation,
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
    public static SimpleExpression makeTime(Expression hour, Expression min, Expression sec) {
        return FunctionUtils.threeArgFunc("MAKE_TIME", hour, min, sec, LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">make_timestamp ( year int, month int, day int, hour int, min int, sec double precision ) → timestamp</a>
     */
    public static SimpleExpression makeTimestamp(Expression year, Expression month, Expression day, Expression hour,
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
    public static SimpleExpression makeTimestampTz(Expression year, Expression month, Expression day, Expression hour,
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
    public static SimpleExpression makeTimestampTz(Expression year, Expression month, Expression day, Expression hour,
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
    public static SimpleExpression now() {
        return FunctionUtils.zeroArgFunc("NOW", OffsetDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">statement_timestamp ( ) → timestamp with time zone</a>
     */
    public static SimpleExpression statementTimestamp() {
        return FunctionUtils.zeroArgFunc("STATEMENT_TIMESTAMP", OffsetDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">timeofday ( ) → text</a>
     */
    public static SimpleExpression timeOfDay() {
        return FunctionUtils.zeroArgFunc("TIMEOFDAY", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">transaction_timestamp ( ) → timestamp with time zone</a>
     */
    public static SimpleExpression transactionTimestamp() {
        return FunctionUtils.zeroArgFunc("TRANSACTION_TIMESTAMP", OffsetDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link OffsetDateTimeType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">to_timestamp ( double precision ) → timestamp with time zone</a>
     */
    public static SimpleExpression toTimestamp(Expression exp) {
        return FunctionUtils.oneArgFunc("TO_TIMESTAMP", exp, OffsetDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-DELAY">pg_sleep ( double precision )</a>
     */
    public static SimpleExpression pgSleep(Expression seconds) {
        return FunctionUtils.oneArgFunc("PG_SLEEP", seconds, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-DELAY">pg_sleep_for ( interval )</a>
     */
    public static SimpleExpression pgSleepFor(Expression interval) {
        return FunctionUtils.oneArgFunc("PG_SLEEP_FOR", interval, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-DELAY">pg_sleep_until ( timestamp with time zone )</a>
     */
    public static SimpleExpression pgSleepUntil(Expression timestampTz) {
        return FunctionUtils.oneArgFunc("PG_SLEEP_UNTIL", timestampTz, StringType.INSTANCE);
    }



    /*-------------------below private -------------------*/

    /**
     * @see PostgreDateTimeFunctions#dateTrunc(Expression, Expression)
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
     * @see PostgreDateTimeFunctions#makeInterval(Expression, Expression, Expression, Expression, Expression, Expression, Expression)
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
     * @see PostgreDateTimeFunctions#dateTrunc(String, Expression)
     * @see PostgreDateTimeFunctions#dateTrunc(String, Expression, String)
     */
    private static CriteriaException errorDateTruncField(String field) {
        String m = String.format("'%s' isn't valid field for date_trunc() function.", field);
        return ContextStack.clearStackAndCriteriaError(m);
    }

    /**
     * @see PostgreDateTimeFunctions#dateTrunc(String, Expression)
     * @see PostgreDateTimeFunctions#dateTrunc(String, Expression, String)
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


}
