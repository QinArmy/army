package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.mysql.MySQLClause;
import io.army.criteria.mysql.MySQLFormat;
import io.army.criteria.mysql.MySQLTimes;
import io.army.criteria.mysql.MySQLUnit;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.*;
import io.army.meta.ParamMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQLs}</li>
 *     </ul>
 * </p>
 * <p>
 *     This class provide MySQL function method.
 * </p>
 * package class
 */
abstract class MySQLFuncSyntax extends MySQLSyntax {

    /**
     * package constructor
     */
    MySQLFuncSyntax() {
    }

    public interface _OverSpec extends Window._OverClause<Window._SimpleLeftParenClause<Void, Expression>> {


    }

    public interface _NullTreatmentSpec extends Functions._NullTreatmentClause<_OverSpec>, _OverSpec {


    }

    public interface _FromFirstLastSpec extends Functions._FromFirstLastClause<_NullTreatmentSpec>, _NullTreatmentSpec {


    }


    public interface _AggregateOverSpec
            extends Window._AggregateWindowFunc<Window._SimpleLeftParenClause<Void, Expression>> {

    }

    /*-------------------below Date and Time Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_adddate">ADDDATE(date,INTERVAL expr unit)</a>
     */
    public static Expression addDate(@Nullable Object date, @Nullable Object expr, MySQLUnit unit) {
        return MySQLFunctions.intervalTimeFunc("ADDDATE", date, expr, unit, LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_adddate">ADDDATE(date,INTERVAL expr unit)</a>
     */
    public static Expression addDate(final @Nullable Object date, final @Nullable Object days) {
        final List<ArmyExpression> argList;
        argList = Arrays.asList(SQLs._funcParam(date), SQLs._funcParam(days));
        return SQLFunctions.safeMultiArgOptionFunc("ADDDATE", null, argList, null, LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link  MappingType} of expr1.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_addtime">ADDTIME(expr1,expr2)</a>
     */
    public static Expression addTime(final @Nullable Object expr1, final @Nullable Object expr2) {
        final ArmyExpression expression1;
        expression1 = SQLs._funcParam(expr1);
        final List<ArmyExpression> argList;
        argList = Arrays.asList(expression1, SQLs._funcParam(expr2));
        return SQLFunctions.safeMultiArgOptionFunc("ADDTIME", null, argList, null, expression1.paramMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateTimeType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_convert-tz">CONVERT_TZ(dt,from_tz,to_tz)</a>
     */
    public static Expression convertTz(@Nullable Object dt, @Nullable Object fromTz, @Nullable Object toTz) {
        final List<ArmyExpression> argList;
        argList = Arrays.asList(SQLs._funcParam(dt), SQLs._funcParam(fromTz)
                , SQLs._funcParam(toTz));
        return SQLFunctions.safeMultiArgOptionFunc("ADDTIME", null, argList, null, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_curdate">CURRENT_DATE()</a>
     */
    public static Expression currentDate() {
        return SQLFunctions.noArgFunc("CURRENT_DATE", LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalTimeType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_current-time">CURRENT_TIME()</a>
     */
    public static Expression currentTime() {
        return SQLFunctions.noArgFunc("CURRENT_TIME", LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalTimeType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_current-time">CURRENT_TIME(fsp)</a>
     */
    public static Expression currentTime(final Object fsp) {
        final String funcName = "CURRENT_TIME";
        final ArmyExpression expression;
        expression = SQLs._funcParam(fsp);
        if (expression instanceof NonOperationExpression) {
            throw CriteriaUtils.funcArgError(funcName, fsp);
        }
        return SQLFunctions.oneArgFunc(funcName, expression, LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateTimeType}
     * </p>
     *
     * @see #currentTimestamp(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_current-timestamp">CURRENT_TIMESTAMP()</a>
     */
    public static Expression currentTimestamp() {
        return SQLFunctions.noArgFunc("CURRENT_TIMESTAMP", LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateTimeType}
     * </p>
     *
     * @param fsp non-null parameter or {@link Expression}, fsp in [0,6]
     * @see #currentTimestamp()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_current-time">CURRENT_TIMESTAMP(fsp)</a>
     */
    public static Expression currentTimestamp(final Object fsp) {
        final String funcName = "CURRENT_TIMESTAMP";
        final ArmyExpression expression;
        expression = SQLs._funcParam(fsp);
        if (expression instanceof NonOperationExpression) {
            throw CriteriaUtils.funcArgError(funcName, fsp);
        }
        return SQLFunctions.oneArgOptionFunc(funcName, null, expression, null, LocalDateTimeType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateType}
     * </p>
     *
     * @param expr nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date">DATE(expr)</a>
     */
    public static Expression date(final @Nullable Object expr) {
        return SQLFunctions.oneArgFunc("DATE", expr, LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @param expr1 nullable parameter or {@link Expression}
     * @param expr2 nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date">DATE(expr)</a>
     */
    public static Expression dateDiff(final @Nullable Object expr1, final @Nullable Object expr2) {
        final List<ArmyExpression> argList;
        argList = Arrays.asList(SQLs._funcParam(expr1), SQLs._funcParam(expr2));
        return SQLFunctions.safeMultiArgOptionFunc("DATEDIFF", null, argList, null, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If date or expr is NULL, {@link _NullType}</li>
     *          <li>If date {@link MappingType} is {@link LocalDateType} and unit no time parts then {@link LocalDateType},otherwise {@link LocalDateTimeType}</li>
     *          <li>If date {@link MappingType} is {@link LocalTimeType} and unit no date parts then {@link LocalTimeType},otherwise {@link LocalDateTimeType}</li>
     *          <li>If date {@link MappingType} is {@link LocalDateTimeType} or {@link OffsetDateTimeType} or {@link ZonedDateTimeType} then {@link LocalDateTimeType}</li>
     *          <li>otherwise {@link StringType}</li>
     *      </ul>
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @param expr nullable parameter or {@link Expression}
     * @param unit non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date-add">DATE_ADD(date,INTERVAL expr unit)</a>
     */
    public static Expression dateAdd(final @Nullable Object date, final @Nullable Object expr, final MySQLUnit unit) {
        return _dateAddOrSub("DATE_ADD", date, expr, unit);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If date or expr is NULL, {@link _NullType}</li>
     *          <li>If date {@link MappingType} is {@link LocalDateType} and unit no time parts then {@link LocalDateType},otherwise {@link LocalDateTimeType}</li>
     *          <li>If date {@link MappingType} is {@link LocalTimeType} and unit no date parts then {@link LocalTimeType},otherwise {@link LocalDateTimeType}</li>
     *          <li>If date {@link MappingType} is {@link LocalDateTimeType} or {@link OffsetDateTimeType} or {@link ZonedDateTimeType} then {@link LocalDateTimeType}</li>
     *          <li>otherwise {@link StringType}</li>
     *      </ul>
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @param expr nullable parameter or {@link Expression}
     * @param unit non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date-add">DATE_SUB(date,INTERVAL expr unit)</a>
     */
    public static Expression dateSub(final @Nullable Object date, final @Nullable Object expr, final MySQLUnit unit) {
        return _dateAddOrSub("DATE_SUB", date, expr, unit);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param date   nullable parameter or {@link Expression}
     * @param format nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date-format">DATE_FORMAT(date,format)</a>
     */
    public static Expression dateFormat(final @Nullable Object date, final @Nullable Object format) {
        final List<ArmyExpression> argList;
        argList = Arrays.asList(SQLs._funcParam(date), SQLs._funcParam(format));
        return SQLFunctions.safeMultiArgOptionFunc("DATE_FORMAT", null, argList, null, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayofmonth">DAYOFMONTH(date)</a>
     */
    public static Expression dayOfMonth(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("DAYOFMONTH", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DayOfWeekType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayname">DAYNAME(date)</a>
     */
    public static Expression dayName(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("DAYNAME", date, DayOfWeekType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DayOfWeekType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayofweek">DAYOFYEAR(date)</a>
     */
    public static Expression dayOfWeek(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("DAYOFWEEK", date, DayOfWeekType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayofyear">DAYOFYEAR(date)</a>
     */
    public static Expression dayOfYear(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("DAYOFYEAR", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>unit {@link MySQLUnit#YEAR}:{@link YearType}</li>
     *          <li>unit {@link MySQLUnit#MONTH}:{@link MonthType}</li>
     *          <li>unit {@link MySQLUnit#WEEK}:{@link DayOfWeekType}</li>
     *          <li>unit {@link MySQLUnit#YEAR_MONTH}:{@link YearMonthType}</li>
     *          <li>otherwise:{@link IntegerType}</li>
     *      </ul>
     * </p>
     *
     * @param unit non-null
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_extract">EXTRACT(date)</a>
     */
    public static Expression extract(final MySQLUnit unit, final @Nullable Object date) {
        final MappingType returnType;
        switch (unit) {
            case YEAR:
                returnType = YearType.INSTANCE;
                break;
            case MONTH:
                returnType = MonthType.INSTANCE;
                break;
            case WEEK:
                returnType = DayOfWeekType.INSTANCE;
                break;
            case YEAR_MONTH:
                returnType = YearMonthType.INSTANCE;
                break;
            case QUARTER:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
            case DAY_HOUR:
            case DAY_MINUTE:
            case DAY_SECOND:
            case DAY_MICROSECOND:
            case HOUR_MINUTE:
            case HOUR_SECOND:
            case HOUR_MICROSECOND:
            case MINUTE_SECOND:
            case MINUTE_MICROSECOND:
            case SECOND_MICROSECOND:
            case MICROSECOND:
                returnType = IntegerType.INSTANCE;
                break;
            default:
                throw _Exceptions.unexpectedEnum(unit);
        }
        final List<Object> argList;
        argList = Arrays.asList(unit, SQLFunctions.FuncWord.FROM, SQLs._funcParam(date));
        return SQLFunctions.safeComplexArgFunc("EXTRACT", argList, returnType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateType}
     * </p>
     *
     * @param n nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_from-days">FROM_DAYS(date)</a>
     */
    public static Expression fromDays(final @Nullable Object n) {
        return SQLFunctions.oneArgFunc("FROM_DAYS", SQLs._funcParam(n), LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @param unixTimestamp nullable parameter or {@link Expression}
     * @see #fromUnixTime(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_from-unixtime">FROM_UNIXTIME(unix_timestamp[,format])</a>
     */
    public static Expression fromUnixTime(final @Nullable Object unixTimestamp) {
        return SQLFunctions.oneArgFunc("FROM_UNIXTIME", unixTimestamp, LocalDateTimeType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param unixTimestamp nullable parameter or {@link Expression}
     * @param format        nullable parameter or {@link Expression}
     * @see #fromUnixTime(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_from-unixtime">FROM_UNIXTIME(unix_timestamp[,format])</a>
     */
    public static Expression fromUnixTime(final @Nullable Object unixTimestamp, final @Nullable Object format) {
        final List<Object> argList;
        argList = Arrays.asList(
                SQLs._funcParam(unixTimestamp)
                , SQLFunctions.FuncWord.COMMA
                , SQLs._funcParam(format));
        return SQLFunctions.safeComplexArgFunc("FROM_UNIXTIME", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param type   non-null
     * @param format nullable
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_get-format">GET_FORMAT({DATE|TIME|DATETIME}, {'EUR'|'USA'|'JIS'|'ISO'|'INTERNAL'})</a>
     */
    public static Expression getFormat(MySQLTimes type, @Nullable MySQLFormat format) {
        Objects.requireNonNull(type);
        final List<Object> argList = new ArrayList<>(3);
        argList.add(type);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(format);
        return SQLFunctions.safeComplexArgFunc("GET_FORMAT", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param time nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_hour">HOUR(time)</a>
     */
    public static Expression hour(final @Nullable Object time) {
        return SQLFunctions.oneArgFunc("HOUR", time, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_last-day">LAST_DAY(date)</a>
     */
    public static Expression lastDay(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("LAST_DAY", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @see #now(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_now">NOW([fsp])</a>
     */
    public static Expression now() {
        return SQLFunctions.noArgFunc("NOW", LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @param fsp non-null parameter or {@link Expression}
     * @see #now()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_now">NOW([fsp])</a>
     */
    public static Expression now(final Object fsp) {
        Objects.requireNonNull(fsp);
        return SQLFunctions.oneArgFunc("NOW", fsp, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateType}
     * </p>
     *
     * @param year      nullable parameter or {@link Expression}
     * @param dayOfYear nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_makedate">MAKEDATE(year,dayofyear)</a>
     */
    public static Expression makeDate(final @Nullable Object year, final @Nullable Object dayOfYear) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(year));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(dayOfYear));
        return SQLFunctions.safeComplexArgFunc("MAKEDATE", argList, LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalTimeType}
     * </p>
     *
     * @param hour   nullable parameter or {@link Expression}
     * @param minute nullable parameter or {@link Expression}
     * @param second nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_maketime">MAKETIME(hour,minute,second)</a>
     */
    public static Expression makeTime(@Nullable Object hour, @Nullable Object minute, @Nullable Object second) {
        final List<Object> argList = new ArrayList<>(5);
        argList.add(SQLs._funcParam(hour));

        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(minute));

        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(second));
        return SQLFunctions.safeComplexArgFunc("MAKETIME", argList, LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param expr non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_microsecond">MICROSECOND(expr)</a>
     */
    public static Expression microSecond(final @Nullable Object expr) {
        return SQLFunctions.oneArgFunc("MICROSECOND", expr, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param time non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_minute">MINUTE(expr)</a>
     */
    public static Expression minute(final @Nullable Object time) {
        return SQLFunctions.oneArgFunc("MINUTE", time, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link MonthType}
     * </p>
     *
     * @param date non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_month">MONTH(date)</a>
     */
    public static Expression month(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("MONTH", date, MonthType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link MonthType}
     * </p>
     *
     * @param date non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_monthname">MONTHNAME(date)</a>
     */
    public static Expression monthName(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("MONTHNAME", date, MonthType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link YearMonthType}
     * </p>
     *
     * @param p non-null parameter or {@link Expression}
     * @param n non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_period-add">PERIOD_ADD(p,n)</a>
     */
    public static Expression periodAdd(final @Nullable Object p, final @Nullable Object n) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(p));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(n));
        return SQLFunctions.safeComplexArgFunc("PERIOD_ADD", argList, YearMonthType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param p1 non-null parameter or {@link Expression}
     * @param p2 non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_period-diff">PERIOD_DIFF(P1,P2)</a>
     */
    public static Expression periodDiff(final @Nullable Object p1, final @Nullable Object p2) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(p1));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(p2));
        return SQLFunctions.safeComplexArgFunc("PERIOD_DIFF", argList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_quarter">QUARTER(date)</a>
     */
    public static Expression quarter(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("QUARTER", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param time non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_second">SECOND(time)</a>
     */
    public static Expression second(final @Nullable Object time) {
        return SQLFunctions.oneArgFunc("SECOND", time, IntegerType.INSTANCE);
    }































    /*-------------------below Aggregate Function  -------------------*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec avg(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("AVG", null, expr, DoubleType.INSTANCE);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static Expression avg(@Nullable SQLs.Modifier distinct, @Nullable Object exp) {
        if (distinct != null && distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError("AVG", distinct);
        }
        return SQLFunctions.oneArgOptionFunc("AVG", distinct, exp, null, DoubleType.INSTANCE);
    }

    /**
     * @see #bitAnd(Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec bitAnd(final @Nullable Object expr) {
        return bitAnd(expr, _bitwiseFuncReturnType(expr));
    }

    /**
     * @see #bitAnd(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec bitAnd(@Nullable Object expr, MappingType mappingType) {
        return MySQLFunctions.aggregateWindowFunc("BIT_AND", null, expr, mappingType);
    }


    /**
     * @see #bitOr(Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-or">BIT_OR(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec bitOr(final @Nullable Object expr) {
        return bitOr(expr, _bitwiseFuncReturnType(expr));
    }

    /**
     * @see #bitOr(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-or">BIT_OR(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec bitOr(@Nullable Object expr, MappingType mappingType) {
        return MySQLFunctions.aggregateWindowFunc("BIT_OR", null, expr, mappingType);
    }

    /**
     * @see #bitXor(Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-xor">BIT_XOR(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec bitXor(final @Nullable Object expr) {
        return bitXor(expr, _bitwiseFuncReturnType(expr));
    }

    /**
     * @see #bitXor(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-xor">BIT_XOR(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec bitXor(@Nullable Object expr, MappingType mappingType) {
        return MySQLFunctions.aggregateWindowFunc("BIT_XOR", null, expr, mappingType);
    }

    /**
     * @see #count(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec count() {
        return _count(null, SQLs.star());
    }

    /**
     * @see #count()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec count(@Nullable Expression expr) {
        return _count(null, expr);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec count(SQLs.Modifier distinct, @Nullable Expression expr) {
        Objects.requireNonNull(distinct);
        return _count(distinct, expr);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec count(SQLs.Modifier distinct, List<Expression> list) {
        Objects.requireNonNull(distinct);
        return _count(distinct, list);
    }


    /**
     * @see #groupConcat(SQLs.Modifier, Object, Supplier)
     * @see <a href="">COUNT(expr) [over_clause]</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static MySQLClause._GroupConcatOrderBySpec groupConcatClause() {
        return MySQLFunctions.groupConcatClause();
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param expressions parameter or {@link Expression} or List(element:null or parameter or {@link Expression})
     * @see #groupConcat(SQLs.Modifier, Object)
     * @see #groupConcat(SQLs.Modifier, Object, Supplier)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static Expression groupConcat(@Nullable Object expressions) {
        return _groupConcat(null, expressions, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param distinct    null or {@link  SQLs.Modifier#DISTINCT}
     * @param expressions parameter or {@link Expression} or List(element:null or parameter or {@link Expression})
     * @see #groupConcat(Object)
     * @see #groupConcat(SQLs.Modifier, Object, Supplier)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static Expression groupConcat(@Nullable SQLs.Modifier distinct, @Nullable Object expressions) {
        return _groupConcat(distinct, expressions, (Clause) null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param distinct    null or {@link  SQLs.Modifier#DISTINCT}
     * @param expressions parameter or {@link Expression} or List(element:null or parameter or {@link Expression})
     * @param supplier    supplier of {@link  #groupConcatClause()},allow to return null
     * @see #groupConcat(Object)
     * @see #groupConcat(SQLs.Modifier, Object)
     * @see #groupConcatClause()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static Expression groupConcat(@Nullable SQLs.Modifier distinct, @Nullable Object expressions, Supplier<Clause> supplier) {
        return _groupConcat(distinct, expressions, supplier.get());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link JsonListType}
     * </p>
     *
     * @param expr parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #jsonArrayAgg(Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-arrayagg">JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     */
    public static _AggregateOverSpec jsonArrayAgg(final Object expr) {
        return _jsonArrayAgg(expr, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link JsonListType}
     * </p>
     *
     * @param expr parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #jsonArrayAgg(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-arrayagg">JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     */
    public static _AggregateOverSpec jsonArrayAgg(final Object expr, final MappingType returnType) {
        return _jsonArrayAgg(expr, returnType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link JsonMapType}
     * </p>
     *
     * @param key   non-null parameter or {@link Expression},but couldn't be null.
     * @param value non-null parameter or {@link Expression},but couldn't be null.
     * @see #jsonObjectAgg(Object, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-objectagg">JSON_OBJECTAGG(key, value) [over_clause]</a>
     */
    public static _AggregateOverSpec jsonObjectAgg(final Object key, final Object value) {
        return _jsonObjectAgg(key, value, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: returnType
     * </p>
     *
     * @param key        non-null parameter or {@link Expression},but couldn't be null.
     * @param value      non-null parameter or {@link Expression},but couldn't be null.
     * @param returnType function return type,should prefer {@link JsonBeanType} and {@link  JsonMapType}
     * @see #jsonObjectAgg(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-objectagg">JSON_OBJECTAGG(key, value) [over_clause]</a>
     */
    public static _AggregateOverSpec jsonObjectAgg(final Object key, final Object value, MappingType returnType) {
        return _jsonObjectAgg(key, value, returnType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #max(SQLs.Modifier, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec max(final Object expr) {
        return _minOrMax("MAX", null, expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param distinct null or {@link  SQLs.Modifier#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #max(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec max(final @Nullable SQLs.Modifier distinct, final Object expr) {
        return _minOrMax("MAX", distinct, expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #min(SQLs.Modifier, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec min(final Object expr) {
        return _minOrMax("MIN", null, expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param distinct null or {@link  SQLs.Modifier#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #min(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec min(final @Nullable SQLs.Modifier distinct, final Object expr) {
        return _minOrMax("MIN", distinct, expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_std">STD(xpr) [over_clause]</a>
     */
    public static _AggregateOverSpec std(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("STD", null, expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev">STDDEV(xpr) [over_clause]</a>
     */
    public static _AggregateOverSpec stdDev(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("STDDEV", null, expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-pop">STDDEV_POP(xpr) [over_clause]</a>
     */
    public static _AggregateOverSpec stdDevPop(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("STDDEV_POP", null, expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-samp">STDDEV_SAMP(xpr) [over_clause]</a>
     */
    public static _AggregateOverSpec stdDevSamp(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("STDDEV_SAMP", null, expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #sum(SQLs.Modifier, Object)
     * @see #sum(SQLs.Modifier, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec sum(Object expr) {
        return _sum(null, expr, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param distinct null or {@link  SQLs.Modifier#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #sum(Object)
     * @see #sum(SQLs.Modifier, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec sum(@Nullable SQLs.Modifier distinct, Object expr) {
        return _sum(distinct, expr, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: returnType or the {@link MappingType} of expr.
     * </p>
     *
     * @param distinct   null or {@link  SQLs.Modifier#DISTINCT}
     * @param expr       non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @param returnType nullable,if null ,then {@link MappingType} of function return type is the {@link MappingType} of expr
     * @see #sum(Object)
     * @see #sum(SQLs.Modifier, Object)
     * @see #sum(SQLs.Modifier, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec sum(@Nullable SQLs.Modifier distinct, Object expr
            , @Nullable MappingType returnType) {
        return _sum(distinct, expr, returnType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-pop">VAR_POP(xpr) [over_clause]</a>
     */
    public static _AggregateOverSpec varPop(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("VAR_POP", null, expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VAR_SAMP(xpr) [over_clause]</a>
     */
    public static _AggregateOverSpec varSamp(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("VAR_SAMP", null, expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VARIANCE(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec variance(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("VARIANCE", null, expr, DoubleType.INSTANCE);
    }

    /*-------------------below window function -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_cume-dist">CUME_DIST() over_clause</a>
     */
    public static _OverSpec cumeDist() {
        return MySQLFunctions.noArgWindowFunc("CUME_DIST", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_dense-rank">DENSE_RANK() over_clause</a>
     */
    public static _OverSpec denseRank() {
        return MySQLFunctions.noArgWindowFunc("DENSE_RANK", LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_first-value">FIRST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static _OverSpec firstValue(final Object expr) {
        return _nonNullArgWindowFunc("FIRST_VALUE", expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_last-value">LAST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static _OverSpec lastValue(final Object expr) {
        return _nonNullArgWindowFunc("LAST_VALUE", expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @see #lag(Object, Object, boolean)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(final Object expr) {
        return _lagOrLead("LAG", expr, null, false);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr       non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @param n          nullable,probably is below:
     *                   <ul>
     *                       <li>null</li>
     *                       <li>{@link Long} type</li>
     *                       <li>{@link Integer} type</li>
     *                       <li>{@link SQLs#param(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                       <li>{@link SQLs#literal(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                   </ul>
     * @param useDefault if n is non-nul and useDefault is true,output sql key word {@code DEFAULT}
     * @see #lag(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(final Object expr, final @Nullable Object n, final boolean useDefault) {
        return _lagOrLead("LAG", expr, n, useDefault);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @see #lag(Object, Object, boolean)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(final Object expr) {
        return _lagOrLead("LEAD", expr, null, false);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr       non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @param n          nullable,probably is below:
     *                   <ul>
     *                       <li>null</li>
     *                       <li>{@link Long} type</li>
     *                       <li>{@link Integer} type</li>
     *                       <li>{@link SQLs#param(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                       <li>{@link SQLs#literal(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                   </ul>
     * @param useDefault if n is non-nul and useDefault is true,output sql key word {@code DEFAULT}
     * @see #lag(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(final Object expr, final @Nullable Object n, final boolean useDefault) {
        return _lagOrLead("LEAD", expr, n, useDefault);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null {@link  Expression}
     * @param n    positive.output literal.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_nth-value">NTH_VALUE(expr, N) [from_first_last] [null_treatment] over_clause</a>
     */
    public static _FromFirstLastSpec nthValue(final Expression expr, final long n) {

        final String funcName = "NTH_VALUE";

        if (expr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        } else if (n < 1L) {
            throw CriteriaUtils.funcArgError(funcName, n);
        }
        final List<ArmyExpression> argList;
        argList = Arrays.asList((ArmyExpression) expr, (ArmyExpression) SQLs.literal(LongType.INSTANCE, n));
        return MySQLFunctions.safeMultiArgFromFirstWindowFunc(funcName, null, argList, expr.paramMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}
     * </p>
     *
     * @param n positive number or {@link  Expression}.in any of the following forms:
     *          <ul>
     *               <li>positive number:
     *                      <ul>
     *                           <li>{@link  Long}</li>
     *                           <li>{@link  Integer}</li>
     *                           <li>{@link  Short}</li>
     *                           <li>{@link  Byte}</li>
     *                      </ul>
     *               </li>
     *               <li>positive number parameter {@link  Expression},eg:{@link SQLs#param(Object)}</li>
     *               <li>positive number literal {@link  Expression},eg:{@link SQLs#literal(Object)}</li>
     *               <li>variable {@link  Expression}</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_ntile">NTILE(N) over_clause</a>
     */
    public static _OverSpec ntile(final Object n) {
        //TODO a local variable in a stored routine?
        final String funcName = "NTILE";
        if (n instanceof Long) {
            if (((Long) n) < 1L) {
                throw CriteriaUtils.funcArgError(funcName, n);
            }
        } else if (n instanceof Number) {
            if (!(n instanceof Integer || n instanceof Short || n instanceof Byte)) {
                throw CriteriaUtils.funcArgError(funcName, n);
            }
            if (((Number) n).intValue() < 1) {
                throw CriteriaUtils.funcArgError(funcName, n);
            }
        } else if (!(n instanceof Expression)) {
            throw CriteriaUtils.funcArgError(funcName, n);
        }

        final ArmyExpression expression;
        expression = SQLs._funcParam(n);
        if (expression instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, n);
        }
        return MySQLFunctions.oneArgWindowFunc(funcName, null, expression, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">PERCENT_RANK() over_clause</a>
     */
    public static _OverSpec percentRank() {
        return MySQLFunctions.noArgWindowFunc("PERCENT_RANK", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">RANK() over_clause</a>
     */
    public static _OverSpec rank() {
        return MySQLFunctions.noArgWindowFunc("RANK", LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_row-number">ROW_NUMBER() over_clause</a>
     */
    public static _OverSpec rowNumber() {
        return MySQLFunctions.noArgWindowFunc("ROW_NUMBER", LongType.INSTANCE);
    }

    /*-------------------below Flow Control Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If the {@link MappingType} of expr2 and the {@link MappingType} of expr3 same,then return type is the {@link MappingType} of expr2</li>
     *          <li>If expr2 or expr3 produce a string, the result is {@link StringType}</li>
     *          <li>If expr2 and expr3 are both not numeric, the result is {@link StringType}</li>
     *          <li>If expr2 or expr3 produce a floating-point value,the result is {@link DoubleType}</li>
     *          <li>If expr2 or expr3 produce unsigned numeric:
     *              <ul>
     *                  <li>If expr2 or expr3 {@link MappingType} is {@link  UnsignedBigDecimalType},the result is {@link UnsignedBigDecimalType}</li>
     *                  <li>If expr2 or expr3 {@link MappingType} is {@link  UnsignedBigIntegerType},the result is {@link UnsignedBigIntegerType}</li>
     *                  <li>If expr2 or expr3 {@link MappingType} is {@link UnsignedLongType},the result is {@link UnsignedLongType}</li>
     *                  <li>Otherwise the result is {@link IntegerType}</li>
     *              </ul>
     *          </li>
     *          <li>If expr2 or expr3 {@link MappingType} is {@link  BigDecimalType},the result is {@link BigDecimalType}</li>
     *          <li>If expr2 or expr3 {@link MappingType} is {@link  BigIntegerType},the result is {@link BigIntegerType}</li>
     *          <li>If expr2 or expr3 {@link MappingType} is {@link LongType},the result is {@link LongType}</li>
     *          <li>Otherwise the result is {@link IntegerType}</li>
     *      </ul>
     * </p>
     *
     * @throws CriteriaException throw when expr2 and expr3 are both non-operate {@link Expression},eg:{@link SQLs#nullWord()}
     * @see #ifFunc()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#function_if">IF(expr1,expr2,expr3)</a>
     */
    public static Expression ifFunc(final IPredicate expr1, final @Nullable Object expr2
            , final @Nullable Object expr3) {
        Objects.requireNonNull(expr1);
        final ArmyExpression expression2, expression3;
        expression2 = SQLs._funcParam(expr2);
        expression3 = SQLs._funcParam(expr3);

        if (expression2 instanceof NonOperationExpression && expression3 instanceof NonOperationExpression) {
            String m = "couldn't bo both non-operate " + Expression.class.getName();
            throw CriteriaContextStack.criteriaError(CriteriaContextStack.peek(), m);
        }

        final List<ArmyExpression> argList;
        argList = Arrays.asList((ArmyExpression) expr1, expression2, expression3);

        final ParamMeta returnType;
        returnType = Functions._returnType(expression2, expression3, MySQLFuncSyntax::ifFuncReturnType);

        return SQLFunctions.safeMultiArgOptionFunc("IF", null, argList, null, returnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If the {@link MappingType} of expr2 and the {@link MappingType} of expr3 same,then return type is the {@link MappingType} of expr2</li>
     *          <li>If expr2 or expr3 produce a string, the result is {@link StringType}</li>
     *          <li>If expr2 and expr3 are both not numeric, the result is {@link StringType}</li>
     *          <li>If expr2 or expr3 produce a floating-point value,the result is {@link DoubleType}</li>
     *          <li>If expr2 or expr3 produce unsigned numeric:
     *              <ul>
     *                  <li>If expr2 or expr3 {@link MappingType} is {@link  UnsignedBigDecimalType},the result is {@link UnsignedBigDecimalType}</li>
     *                  <li>If expr2 or expr3 {@link MappingType} is {@link  UnsignedBigIntegerType},the result is {@link UnsignedBigIntegerType}</li>
     *                  <li>If expr2 or expr3 {@link MappingType} is {@link UnsignedLongType},the result is {@link UnsignedLongType}</li>
     *                  <li>Otherwise the result is {@link IntegerType}</li>
     *              </ul>
     *          </li>
     *          <li>If expr2 or expr3 {@link MappingType} is {@link  BigDecimalType},the result is {@link BigDecimalType}</li>
     *          <li>If expr2 or expr3 {@link MappingType} is {@link  BigIntegerType},the result is {@link BigIntegerType}</li>
     *          <li>If expr2 or expr3 {@link MappingType} is {@link LongType},the result is {@link LongType}</li>
     *          <li>Otherwise the result is {@link IntegerType}</li>
     *      </ul>
     * </p>
     *
     * @throws CriteriaException throw when expr2 and expr3 are both non-operate {@link Expression},eg:{@link SQLs#nullWord()}
     * @see #ifFunc(IPredicate, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#function_if">IF(expr1,expr2,expr3)</a>
     */
    public static _FuncConditionTowClause ifFunc() {
        return SQLFunctions.conditionTwoFunc("IF", MySQLFuncSyntax::ifFuncReturnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * </p>
     *
     * @throws CriteriaException throw when expr2 and expr3 are both non-operate {@link Expression},eg:{@link SQLs#nullWord()}
     * @see #ifFunc(IPredicate, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#function_ifnull">IFNULL(expr1,expr2)</a>
     */
    public static Expression ifNull(@Nullable Object expr1, @Nullable Object expr2) {
        final ArmyExpression expression1, expression2;
        expression1 = SQLs._funcParam(expr1);
        expression2 = SQLs._funcParam(expr2);
        final ParamMeta returnType;
        returnType = Functions._returnType(expression1, expression2, MySQLFuncSyntax::ifNullReturnType);
        return SQLFunctions.safeMultiArgOptionFunc("IFNULL", null, Arrays.asList(expression1, expression2), null
                , returnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link  MappingType} of expr1
     * </p>
     *
     * @throws CriteriaException throw when expr2 and expr3 are both non-operate {@link Expression},eg:{@link SQLs#nullWord()}
     * @see #ifFunc(IPredicate, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html#function_ifnull">IFNULL(expr1,expr2)</a>
     */
    public static Expression nullIf(@Nullable Object expr1, @Nullable Object expr2) {
        final ArmyExpression expression1, expression2;
        expression1 = SQLs._funcParam(expr1);
        expression2 = SQLs._funcParam(expr2);
        return SQLFunctions.safeMultiArgOptionFunc("NULLIF", null, Arrays.asList(expression1, expression2), null
                , expression1.paramMeta());
    }


    /*-------------------below private method -------------------*/

    /**
     * @see #count()
     * @see #count(Expression)
     * @see #count(SQLs.Modifier, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    private static _AggregateOverSpec _count(final @Nullable SQLs.Modifier distinct
            , final @Nullable Object expressions) {

        final String funcName = "COUNT";

        if (distinct != null && distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        final _AggregateOverSpec func;
        if (!(expressions instanceof List)) {
            if (distinct != null && expressions == null) {
                String m = String.format("function %s option[%s] but expr is null.", funcName, distinct);
                throw CriteriaContextStack.criteriaError(CriteriaContextStack.peek(), m);
            }
            func = MySQLFunctions.aggregateWindowFunc(funcName, distinct, expressions, LongType.INSTANCE);
        } else if (distinct == null) {
            throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
        } else {
            func = MySQLFunctions.multiArgAggregateWindowFunc(funcName, distinct, (List<?>) expressions
                    , null, LongType.INSTANCE);
        }
        return func;
    }

    /**
     * @see #bitAnd(Object)
     * @see #bitOr(Object)
     * @see #bitXor(Object)
     */
    private static MappingType _bitwiseFuncReturnType(final @Nullable Object expr) {
        final MappingType returnType;

        if (expr == null) {
            returnType = LongType.INSTANCE;
        } else if (expr instanceof String) {
            returnType = _StringUtils.isBinary((String) expr) ? StringType.INSTANCE : LongType.INSTANCE;
        } else if (!(expr instanceof Expression)) {
            returnType = LongType.INSTANCE;
        } else {
            final ParamMeta paramMeta = ((Expression) expr).paramMeta();
            if (paramMeta instanceof ParamMeta.Delay) {
                returnType = StringType.INSTANCE; //unknown,compatibility
            } else if (!(paramMeta.mappingType() instanceof StringType)) {
                returnType = LongType.INSTANCE;
            } else if (!(expr instanceof SqlValueParam.SingleNonNamedValue)) {
                returnType = StringType.INSTANCE; //unknown,compatibility
            } else {
                final Object value;
                value = ((SqlValueParam.SingleNonNamedValue) expr).value();
                if (value instanceof String && _StringUtils.isBinary((String) value)) {
                    returnType = StringType.INSTANCE;
                } else {
                    returnType = LongType.INSTANCE;
                }
            }
        }
        return returnType;
    }


    private static Expression _groupConcat(@Nullable SQLs.Modifier distinct, @Nullable Object expressions
            , @Nullable Clause clause) {

        final String funcName = "GROUP_CONCAT";

        if (distinct != null && distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        if (clause != null && !(clause instanceof MySQLFunctions.GroupConcatClause)) {
            throw CriteriaUtils.funcArgError(funcName, clause);
        }
        final Expression func;
        if (expressions instanceof List) {
            func = SQLFunctions.multiArgOptionFunc(funcName, distinct, (List<?>) expressions
                    , clause, StringType.INSTANCE);
        } else {
            func = SQLFunctions.oneArgOptionFunc(funcName, distinct, expressions, clause, StringType.INSTANCE);
        }
        return func;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: returnType or {@link JsonListType}
     * </p>
     *
     * @param expr       parameter or {@link Expression},but couldn't be null.
     * @param returnType if null,then the {@link MappingType} of function return type is {@link JsonListType}.
     * @see #jsonArrayAgg(Object)
     * @see #jsonArrayAgg(Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-arrayagg">JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     */
    private static _AggregateOverSpec _jsonArrayAgg(final Object expr, final @Nullable ParamMeta returnType) {
        final String funcName = "JSON_ARRAYAGG";
        if (expr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        final ParamMeta elementType = expression.paramMeta();

        final ParamMeta actualReturnType;
        if (returnType != null) {
            actualReturnType = returnType;
        } else if (elementType instanceof ParamMeta.Delay) {
            actualReturnType = CriteriaSupports.delayParamMeta((ParamMeta.Delay) elementType, JsonListType::from);
        } else {
            actualReturnType = JsonListType.from(elementType.mappingType());
        }
        return MySQLFunctions.aggregateWindowFunc(funcName, null, expression, actualReturnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: returnType or {@link  JsonMapType}
     * </p>
     *
     * @param key        non-null parameter or {@link Expression},but couldn't be null.
     * @param value      non-null parameter or {@link Expression},but couldn't be null.
     * @param returnType function return type,if null,then The {@link MappingType} of function return type is {@link  JsonMapType}.
     * @see #jsonObjectAgg(Object, Object)
     * @see #jsonObjectAgg(Object, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-objectagg">JSON_OBJECTAGG(key, value) [over_clause]</a>
     */
    private static _AggregateOverSpec _jsonObjectAgg(final Object key, final Object value
            , final @Nullable ParamMeta returnType) {

        final String funcName = "JSON_OBJECTAGG";

        final ArmyExpression keyExpr, valueExpr;
        keyExpr = SQLs._funcParam(key);
        valueExpr = SQLs._funcParam(value);

        if (keyExpr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, key);
        }
        if (valueExpr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, value);
        }
        final ParamMeta actualReturnType;
        if (returnType != null) {
            actualReturnType = returnType;
        } else {
            actualReturnType = Functions._returnType(keyExpr, valueExpr, JsonMapType::from);
        }
        return MySQLFunctions.safeMultiArgAggregateWindowFunc(funcName, null
                , Arrays.asList(keyExpr, valueExpr), null
                , actualReturnType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param funcName MIN or MAX
     * @param distinct null or {@link  SQLs.Modifier#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #min(Object)
     * @see #min(SQLs.Modifier, Object)
     * @see #max(Object)
     * @see #max(SQLs.Modifier, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    private static _AggregateOverSpec _minOrMax(final String funcName, final @Nullable SQLs.Modifier distinct
            , final Object expr) {
        if (!(funcName.equals("MAX") || funcName.equals("MIN"))) {
            //no bug,never here
            throw new IllegalArgumentException();
        }
        if (distinct != null && distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        if (expression instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        return MySQLFunctions.aggregateWindowFunc(funcName, distinct, expression, expression.paramMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: returnType or the {@link MappingType} of expr.
     * </p>
     *
     * @param distinct   null or {@link  SQLs.Modifier#DISTINCT}
     * @param expr       non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @param returnType nullable,if null ,then {@link MappingType} of function return type is the {@link MappingType} of expr
     * @see #sum(Object)
     * @see #sum(SQLs.Modifier, Object)
     * @see #sum(SQLs.Modifier, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    private static _AggregateOverSpec _sum(final @Nullable SQLs.Modifier distinct, final @Nullable Object expr
            , final @Nullable ParamMeta returnType) {
        if (expr == null) {
            throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
        }
        final String funcName = "SUM";
        if (distinct != null && distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        if (expression instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        final ParamMeta actualReturnType;
        if (returnType != null) {
            actualReturnType = returnType;
        } else {
            actualReturnType = expression.paramMeta();
        }
        return MySQLFunctions.aggregateWindowFunc(funcName, distinct, expression, actualReturnType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param funcName   LAG or LEAD
     * @param expr       non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @param n          nullable,probably is below:
     *                   <ul>
     *                       <li>null</li>
     *                       <li>{@link Long} type</li>
     *                       <li>{@link Integer} type</li>
     *                       <li>{@link SQLs#param(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                       <li>{@link SQLs#literal(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                   </ul>
     * @param useDefault if n is non-nul and useDefault is true,output sql key word {@code DEFAULT}
     * @see #lag(Object)
     * @see #lag(Object, Object, boolean)
     * @see #lead(Object)
     * @see #lead(Object, Object, boolean)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    private static _OverSpec _lagOrLead(final String funcName, final Object expr
            , final @Nullable Object n, final boolean useDefault) {

        assert funcName.equals("LAG") || funcName.equals("LEAD");

        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        if (expr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }

        final ArmyExpression nExp;
        final ParamMeta nType;
        if (n == null) {
            nExp = null;
            nType = null;
        } else {
            nExp = SQLs._funcParam(n);
            nType = nExp.paramMeta();
        }

        final _OverSpec overSpec;
        if (nExp == null) {
            overSpec = MySQLFunctions.oneArgWindowFunc(funcName, null, expression, expression.paramMeta());
        } else if (!(nExp instanceof ParamExpression.SingleParamExpression
                || nExp instanceof LiteralExpression.SingleLiteralExpression)) {
            throw CriteriaUtils.funcArgError(funcName, n);
        } else if (nExp.isNullValue()) {
            throw CriteriaUtils.funcArgError(funcName, n);
        } else if (!(nType instanceof LongType || nType instanceof IntegerType)) {
            throw CriteriaUtils.funcArgError(funcName, n);
        } else if (useDefault) {
            final List<ArmyExpression> argList;
            argList = Arrays.asList(expression, nExp, (ArmyExpression) SQLs.defaultWord());
            overSpec = MySQLFunctions.safeMultiArgWindowFunc(funcName, null, argList, expression.paramMeta());
        } else {
            final List<ArmyExpression> argList;
            argList = Arrays.asList(expression, nExp);
            overSpec = MySQLFunctions.safeMultiArgWindowFunc(funcName, null, argList, expression.paramMeta());
        }
        return overSpec;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @see #firstValue(Object)
     * @see #lastValue(Object)
     */
    private static _OverSpec _nonNullArgWindowFunc(final String funcName, final Object expr) {
        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        if (expr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        return MySQLFunctions.oneArgWindowFunc(funcName, null, expression, expression.paramMeta());
    }

    /**
     * @see #ifFunc(IPredicate, Object, Object)
     * @see #ifFunc()
     */
    private static MappingType ifFuncReturnType(final MappingType expr2Type, final MappingType expr3Type) {
        final MappingType returnType;
        if (expr2Type.getClass() == expr3Type.getClass()) {
            returnType = expr2Type;
        } else if (expr2Type instanceof _SQLStringType || expr3Type instanceof _SQLStringType) {
            returnType = StringType.INSTANCE;
        } else if (!(expr2Type instanceof _NumericType || expr3Type instanceof _NumericType)) {
            returnType = StringType.INSTANCE;
        } else if (expr2Type instanceof _NumericType._FloatNumericType
                || expr3Type instanceof _NumericType._FloatNumericType) {
            returnType = DoubleType.INSTANCE;
        } else if (expr2Type instanceof _NumericType._UnsignedNumeric
                || expr3Type instanceof _NumericType._UnsignedNumeric) {
            if (expr2Type instanceof UnsignedBigDecimalType || expr3Type instanceof UnsignedBigDecimalType) {
                returnType = UnsignedBigDecimalType.INSTANCE;
            } else if (expr2Type instanceof UnsignedBigIntegerType || expr3Type instanceof UnsignedBigIntegerType) {
                returnType = UnsignedBigIntegerType.INSTANCE;
            } else if (expr2Type instanceof UnsignedLongType || expr3Type instanceof UnsignedLongType) {
                returnType = UnsignedLongType.INSTANCE;
            } else {
                returnType = UnsignedIntegerType.INSTANCE;
            }
        } else if (expr2Type instanceof BigDecimalType || expr3Type instanceof BigDecimalType) {
            returnType = BigDecimalType.INSTANCE;
        } else if (expr2Type instanceof BigIntegerType || expr3Type instanceof BigIntegerType) {
            returnType = BigIntegerType.INSTANCE;
        } else if (expr2Type instanceof LongType || expr3Type instanceof LongType) {
            returnType = LongType.INSTANCE;
        } else {
            returnType = IntegerType.INSTANCE;
        }
        return returnType;
    }


    /**
     * @see #ifNull(Object, Object)
     */
    private static MappingType ifNullReturnType(final MappingType expr1Type, final MappingType expr2Type) {
        final MappingType returnType;
        if (expr1Type.getClass() == expr2Type.getClass()) {
            returnType = expr1Type;
        } else if (!(expr1Type instanceof _NumericType && expr2Type instanceof _NumericType)) {
            returnType = StringType.INSTANCE;
        } else if (expr1Type instanceof _NumericType._DecimalNumeric && expr2Type instanceof _NumericType._DecimalNumeric) {
            if (expr1Type instanceof _NumericType._UnsignedNumeric
                    && expr2Type instanceof _NumericType._UnsignedNumeric) {
                returnType = UnsignedBigDecimalType.INSTANCE;
            } else {
                returnType = BigDecimalType.INSTANCE;
            }
        } else if (!(expr1Type instanceof _NumericType._IntegerNumeric
                || expr2Type instanceof _NumericType._IntegerNumeric)) {
            returnType = DoubleType.INSTANCE;
        } else if (expr1Type instanceof _NumericType._UnsignedNumeric
                && expr2Type instanceof _NumericType._UnsignedNumeric) {
            if (expr1Type instanceof UnsignedBigIntegerType || expr2Type instanceof UnsignedBigIntegerType) {
                returnType = UnsignedBigIntegerType.INSTANCE;
            } else if (expr1Type instanceof UnsignedLongType || expr2Type instanceof UnsignedLongType) {
                returnType = UnsignedLongType.INSTANCE;
            } else {
                returnType = UnsignedIntegerType.INSTANCE;
            }
        } else if (expr1Type instanceof BigIntegerType || expr2Type instanceof BigIntegerType) {
            returnType = BigIntegerType.INSTANCE;
        } else if (expr1Type instanceof LongType || expr2Type instanceof LongType) {
            returnType = LongType.INSTANCE;
        } else {
            returnType = IntegerType.INSTANCE;
        }
        return returnType;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If date or expr is NULL, {@link _NullType}</li>
     *          <li>If date {@link MappingType} is {@link LocalDateType} and unit no time parts then {@link LocalDateType},otherwise {@link LocalDateTimeType}</li>
     *          <li>If date {@link MappingType} is {@link LocalTimeType} and unit no date parts then {@link LocalTimeType},otherwise {@link LocalDateTimeType}</li>
     *          <li>If date {@link MappingType} is {@link LocalDateTimeType} or {@link OffsetDateTimeType} or {@link ZonedDateTimeType} then {@link LocalDateTimeType}</li>
     *          <li>otherwise {@link StringType}</li>
     *      </ul>
     * </p>
     *
     * @param funcName DATE_ADD or DATE_SUB
     * @param date     nullable parameter or {@link Expression}
     * @param expr     nullable parameter or {@link Expression}
     * @param unit     non-null
     * @see #dateAdd(Object, Object, MySQLUnit)
     * @see #dateSub(Object, Object, MySQLUnit)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date-add">DATE_ADD(date,INTERVAL expr unit), DATE_SUB(date,INTERVAL expr unit)</a>
     */
    private static Expression _dateAddOrSub(final String funcName, final @Nullable Object date
            , final @Nullable Object expr, final MySQLUnit unit) {
        final ArmyExpression dateExpr;
        dateExpr = SQLs._funcParam(date);
        final ParamMeta type, returnType;
        type = dateExpr.paramMeta();
        if (type instanceof ParamMeta.Delay) {
            returnType = CriteriaSupports.delayParamMeta((ParamMeta.Delay) type, t -> _dateAddSubReturnType(t, unit));
        } else {
            returnType = _dateAddSubReturnType(type.mappingType(), unit);
        }
        return MySQLFunctions.intervalTimeFunc(funcName, dateExpr, expr, unit, returnType);
    }


    /**
     * @see #dateAdd(Object, Object, MySQLUnit)
     */
    private static MappingType _dateAddSubReturnType(final MappingType type, final MySQLUnit unit) {
        final MappingType returnType;
        if (type instanceof _NullType) {
            returnType = type;
        } else if (type instanceof LocalDateType) {
            switch (unit) {
                case YEAR:
                case QUARTER:
                case MONTH:
                case WEEK:
                case DAY:
                    returnType = LocalDateType.INSTANCE;
                    break;
                default:
                    returnType = LocalDateTimeType.INSTANCE;
            }
        } else if (type instanceof LocalTimeType || type instanceof OffsetTimeType) {
            switch (unit) {
                case HOUR:
                case MINUTE:
                case SECOND:
                case MICROSECOND:
                    returnType = LocalTimeType.INSTANCE;
                    break;
                default:
                    returnType = LocalDateTimeType.INSTANCE;
            }
        } else if (type instanceof LocalDateTimeType
                || type instanceof OffsetDateTimeType
                || type instanceof ZonedDateTimeType) {
            returnType = LocalDateTimeType.INSTANCE;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }


}
