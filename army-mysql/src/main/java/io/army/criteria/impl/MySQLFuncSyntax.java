package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.mysql.*;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.JsonType;
import io.army.mapping.optional.OffsetDateTimeType;
import io.army.mapping.optional.OffsetTimeType;
import io.army.mapping.optional.ZonedDateTimeType;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;

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
@SuppressWarnings("unused")
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
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If unit is time part:{@link  LocalDateTimeType}</li>
     *          <li>else :{@link  LocalDateType}</li>
     *      </ul>
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @param expr nullable parameter or {@link Expression}
     * @param unit non-null
     * @see #addDate(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_adddate">ADDDATE(date,INTERVAL expr unit)</a>
     */
    public static Expression addDate(final @Nullable Object date, final @Nullable Object expr, final MySQLUnit unit) {
        return _dateIntervalFunc("ADDDATE", date, expr, unit);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @param days nullable parameter or {@link Expression}
     * @see #addDate(Object, Object, MySQLUnit)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_adddate">ADDDATE(date,days)</a>
     */
    public static Expression addDate(final @Nullable Object date, final @Nullable Object days) {
        return _operateDateFunc("ADDDATE", date, days);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If unit is time part:{@link  LocalDateTimeType}</li>
     *          <li>else :{@link  LocalDateType}</li>
     *      </ul>
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @param expr nullable parameter or {@link Expression}
     * @param unit non-null
     *             @see #subDate(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_subdate">SUBDATE(date,INTERVAL expr unit)</a>
     */
    public static Expression subDate(final @Nullable Object date, final @Nullable Object expr, final MySQLUnit unit) {
        return _dateIntervalFunc("SUBDATE", date, expr, unit);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @param days nullable parameter or {@link Expression}
     * @see #subDate(Object, Object, MySQLUnit)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_subdate">SUBDATE(expr,days)</a>
     */
    public static Expression subDate(final @Nullable Object date, final @Nullable Object days) {
        return _operateDateFunc("SUBDATE", date, days);
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

        final List<Object> argList = new ArrayList<>(3);
        argList.add(expression1);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(expr2));

        return SQLFunctions.safeComplexArgFunc("ADDTIME", argList, expression1.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link  MappingType} of expr1.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_subtime">SUBTIME(expr1,expr2)</a>
     */
    public static Expression subTime(final @Nullable Object expr1, final @Nullable Object expr2) {
        final ArmyExpression expression1;
        expression1 = SQLs._funcParam(expr1);

        final List<Object> argList = new ArrayList<>(3);
        argList.add(expression1);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(expr2));

        return SQLFunctions.safeComplexArgFunc("SUBTIME", argList, expression1.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateTimeType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_convert-tz">CONVERT_TZ(dt,from_tz,to_tz)</a>
     */
    public static Expression convertTz(@Nullable Object dt, @Nullable Object fromTz, @Nullable Object toTz) {
        final List<Object> argList = new ArrayList<>(5);

        argList.add(SQLs._funcParam(dt));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(fromTz));
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(SQLs._funcParam(toTz));
        return SQLFunctions.safeComplexArgFunc("CONVERT_TZ", argList, LocalDateTimeType.INSTANCE);
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
     * @param type   non-null,should be below:
     *               <ul>
     *                      <li>{@link MySQLWords#TIME}</li>
     *                      <li>{@link MySQLWords#DATE}</li>
     *                      <li>{@link MySQLWords#DATETIME}</li>
     *               </ul>
     * @param format nullable
     * @throws CriteriaException throw when 1.type error;2.invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_get-format">GET_FORMAT({DATE|TIME|DATETIME}, {'EUR'|'USA'|'JIS'|'ISO'|'INTERNAL'})</a>
     */
    public static Expression getFormat(final MySQLWords type, final @Nullable MySQLFormat format) {
        final String funcName = "GET_FORMAT";
        switch (type) {
            case TIME:
            case DATE:
            case DATETIME:
                break;
            default:
                throw CriteriaUtils.funcArgError(funcName, type);
        }
        final List<Object> argList = new ArrayList<>(3);
        argList.add(type);
        argList.add(SQLFunctions.FuncWord.COMMA);
        if (format == null) {
            argList.add(SQLs.nullWord());
        } else {
            argList.add(format);
        }
        return SQLFunctions.safeComplexArgFunc(funcName, argList, StringType.INSTANCE);
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
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @see #sysDate(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_sysdate">SYSDATE([fsp])</a>
     */
    public static Expression sysDate() {
        return SQLFunctions.noArgFunc("SYSDATE", LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @see #sysDate(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_sysdate">SYSDATE([fsp])</a>
     */
    public static Expression sysDate(final Object fsp) {
        Objects.requireNonNull(fsp);
        return SQLFunctions.oneArgFunc("SYSDATE", fsp, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @see #timestamp(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timestamp">TIMESTAMP(expr)</a>
     */
    public static Expression timestamp(final @Nullable Object expr) {
        return SQLFunctions.oneArgFunc("TIMESTAMP", expr, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @see #timestamp(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timestamp">TIMESTAMP(expr1,expr2)</a>
     */
    public static Expression timestamp(final @Nullable Object expr1, final @Nullable Object expr2) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(expr1));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(expr2));
        return SQLFunctions.safeComplexArgFunc("TIMESTAMP", argList, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If unit is time part then {@link LocalDateTimeType}</li>
     *          <li>Else if datetimeExpr {@link MappingType}  {@link LocalDateType}</li>
     *          <li>Else if datetimeExpr {@link MappingType}  {@link LocalDateTimeType} or {@link ZonedDateTimeType} or {@link  OffsetDateTimeType} then {@link LocalDateTimeType }</li>
     *          <li>Else {@link StringType}</li>
     *      </ul>
     * </p>
     *
     * @param unit         non-null,should be one of below:
     *                     <ul>
     *                          <li>{@link MySQLUnit#MICROSECOND}</li>
     *                          <li>{@link MySQLUnit#SECOND}</li>
     *                          <li>{@link MySQLUnit#MINUTE}</li>
     *                          <li>{@link MySQLUnit#HOUR}</li>
     *
     *                          <li>{@link MySQLUnit#DAY}</li>
     *                          <li>{@link MySQLUnit#WEEK}</li>
     *                          <li>{@link MySQLUnit#MONTH}</li>
     *                          <li>{@link MySQLUnit#QUARTER}</li>
     *
     *                          <li>{@link MySQLUnit#YEAR}</li>
     *                     </ul>
     * @param interval     nullable parameter or {@link Expression}
     * @param datetimeExpr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when unit error or invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timestampadd">TIMESTAMPADD(unit,interval,datetime_expr)</a>
     */
    public static Expression timestampAdd(final MySQLUnit unit, final @Nullable Object interval
            , final @Nullable Object datetimeExpr) {

        final String funcName = "TIMESTAMPADD";
        final ArmyExpression datetimeExpression;
        datetimeExpression = SQLs._funcParam(datetimeExpr);
        final TypeMeta returnType;
        switch (unit) {
            case HOUR:
            case MINUTE:
            case SECOND:
            case MICROSECOND:
                returnType = LocalDateTimeType.INSTANCE;
                break;
            case YEAR:
            case QUARTER:
            case MONTH:
            case DAY:
            case WEEK: {
                final TypeMeta type;
                type = datetimeExpression.typeMeta();
                if (type instanceof TypeMeta.Delay) {
                    returnType = CriteriaSupports.delayParamMeta((TypeMeta.Delay) type, MySQLFuncSyntax::_timestampAdd);
                } else {
                    returnType = _timestampAdd(type.mappingType());
                }
            }
            break;
            default:
                throw CriteriaUtils.funcArgError(funcName, unit);
        }
        final List<Object> argList = new ArrayList<>(5);

        argList.add(unit);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(interval));
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(datetimeExpression);
        return SQLFunctions.safeComplexArgFunc(funcName, argList, returnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If {@link MySQLUnit#MINUTE} or {@link MySQLUnit#SECOND} or {@link MySQLUnit#MICROSECOND} then {@link LongType}</li>
     *          <li>Else {@link IntegerType}</li>
     *      </ul>
     * </p>
     *
     * @param unit          non-null
     * @param datetimeExpr1 nullable parameter or {@link  Expression}
     * @param datetimeExpr2 nullable parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timestampdiff">TIMESTAMPDIFF(unit,datetime_expr1,datetime_expr2)</a>
     */
    public static Expression timestampDiff(final MySQLUnit unit, final @Nullable Object datetimeExpr1
            , final @Nullable Object datetimeExpr2) {
        final TypeMeta returnType;
        switch (unit) {
            case MINUTE:
            case SECOND:
            case MICROSECOND:
                returnType = LongType.INSTANCE;
                break;
            case HOUR:
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
            case YEAR:
            case QUARTER:
            case YEAR_MONTH:
            case MONTH:
            case WEEK:
            case DAY:
                returnType = IntegerType.INSTANCE;
                break;
            default:
                throw _Exceptions.unexpectedEnum(unit);
        }

        final List<Object> argList = new ArrayList<>(5);

        argList.add(unit);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(datetimeExpr1));
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(SQLs._funcParam(datetimeExpr2));
        return SQLFunctions.safeComplexArgFunc("TIMESTAMPDIFF", argList, returnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param timeFormat nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_time-format">TIME_FORMAT(time,format)</a>
     */
    public static Expression timeFormat(final @Nullable Object timeFormat) {
        return SQLFunctions.oneArgFunc("TIME_FORMAT", timeFormat, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_to-days">TO_DAYS(date)</a>
     */
    public static Expression toDays(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("TO_DAYS", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @param expr nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_to-seconds">TO_SECONDS(expr)</a>
     */
    public static Expression toSeconds(final @Nullable Object expr) {
        return SQLFunctions.oneArgFunc("TO_SECONDS", expr, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_unix-timestamp">UNIX_TIMESTAMP()</a>
     */
    public static Expression unixTimestamp() {
        return SQLFunctions.noArgFunc("UNIX_TIMESTAMP", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_unix-timestamp">UNIX_TIMESTAMP(date)</a>
     */
    public static Expression unixTimestamp(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("UNIX_TIMESTAMP", date, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-date">UTC_DATE()</a>
     */
    public static Expression utcDate() {
        return SQLFunctions.noArgFunc("UTC_DATE", LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalTimeType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-time">UTC_TIME()</a>
     */
    public static Expression utcTime() {
        return SQLFunctions.noArgFunc("UTC_DATE", LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalTimeType}
     * </p>
     *
     * @param fsp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-time">UTC_TIME(fsp)</a>
     */
    public static Expression utcTime(final Object fsp) {
        return SQLFunctions.oneArgFunc("UTC_TIME", fsp, LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-timestamp">UTC_TIMESTAMP()</a>
     */
    public static Expression utcTimestamp() {
        return SQLFunctions.noArgFunc("UTC_TIMESTAMP", LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @param fsp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-timestamp">UTC_TIMESTAMP(fsp)</a>
     */
    public static Expression utcTimestamp(final Object fsp) {
        return SQLFunctions.oneArgFunc("UTC_TIMESTAMP", fsp, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_week">WEEK(date)</a>
     */
    public static Expression week(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("WEEK", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @param mode non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_week">WEEK(date)</a>
     */
    public static Expression week(final @Nullable Object date, final Object mode) {
        Objects.requireNonNull(mode);

        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(date));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(mode));
        return SQLFunctions.safeComplexArgFunc("WEEK", argList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DayOfWeekType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_weekday">WEEKDAY(date)</a>
     */
    public static Expression weekDay(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("WEEKDAY", date, DayOfWeekType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_weekofyear">WEEKOFYEAR(date)</a>
     */
    public static Expression weekOfYear(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("WEEKOFYEAR", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link YearType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_year">YEAR(date)</a>
     */
    public static Expression year(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("YEAR", date, YearType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_yearweek">YEARWEEK(date)</a>
     */
    public static Expression yearWeek(final @Nullable Object date) {
        return SQLFunctions.oneArgFunc("YEARWEEK", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_yearweek">YEARWEEK(date,mode)</a>
     */
    public static Expression yearWeek(final @Nullable Object date, final Object mode) {
        Objects.requireNonNull(mode);

        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(date));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(mode));
        return SQLFunctions.safeComplexArgFunc("YEARWEEK", argList, IntegerType.INSTANCE);
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
     * The {@link MappingType} of function return type:{@link LocalTimeType}
     * </p>
     *
     * @param expr nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_time">TIME(expr)</a>
     */
    public static Expression time(final @Nullable Object expr) {
        return SQLFunctions.oneArgFunc("TIME", expr, LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link LocalTimeType} of expr1.
     * </p>
     *
     * @param expr1 nullable parameter or {@link Expression}
     * @param expr2 nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timediff">TIMEDIFF(expr1,expr2)</a>
     */
    public static Expression timeDiff(final @Nullable Object expr1, final @Nullable Object expr2) {
        final ArmyExpression expression1;
        expression1 = SQLs._funcParam(expr1);

        final List<Object> argList = new ArrayList<>(3);
        argList.add(expression1);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(expr2));

        return SQLFunctions.safeComplexArgFunc("TIMEDIFF", argList, expression1.typeMeta());
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

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalTimeType}
     * </p>
     *
     * @param seconds non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_sec-to-time">SEC_TO_TIME(seconds)</a>
     */
    public static Expression secToTime(final @Nullable Object seconds) {
        return SQLFunctions.oneArgFunc("SEC_TO_TIME", seconds, LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalTimeType}
     * </p>
     *
     * @param str    non-null parameter or {@link Expression}
     * @param format non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_str-to-date">STR_TO_DATE(str,format)</a>
     */
    public static Expression strToDate(final @Nullable Object str, final @Nullable Object format) {
        final ArmyExpression formatExp;
        formatExp = SQLs._funcParam(format);
        final TypeMeta formatType;
        formatType = formatExp.typeMeta();

        final TypeMeta returnType;
        if (formatType instanceof TypeMeta.Delay) {
            final Function<MappingType, MappingType> function = t -> _strToDateReturnType(formatExp, t);
            returnType = CriteriaSupports.delayParamMeta((TypeMeta.Delay) formatType, function);
        } else {
            returnType = _strToDateReturnType(formatExp, formatType.mappingType());
        }

        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(str));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(formatExp);
        return SQLFunctions.safeComplexArgFunc("STR_TO_DATE", argList, returnType);
    }



    /*-------------------below String Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_ascii">ASCII(str)</a>
     */
    public static Expression ascii(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("ASCII", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_bin">BIN(n)</a>
     */
    public static Expression bin(final @Nullable Object n) {
        return SQLFunctions.oneArgFunc("BIN", n, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_bit-length">BIT_LENGTH(str)</a>
     */
    public static Expression binLength(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("BIT_LENGTH", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n nullable parameter or {@link Collection} or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    public static Expression charFunc(final @Nullable Object n) {
        final ArmyExpression expression;
        if (n == null) {
            expression = SQLs._nullParam();
        } else if (n instanceof Expression) {
            expression = (ArmyExpression) n;
        } else if (n instanceof Collection) {
            expression = (ArmyExpression) SQLs.params(IntegerType.INSTANCE, (Collection<?>) n);
        } else {
            expression = SQLs._funcParam(n);
        }
        return SQLFunctions.oneArgFunc("CHAR", expression, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n           nullable parameter or non-empty {@link List} or {@link Expression}
     * @param charsetName non-null, {@link io.army.criteria.mysql.MySQLCharset} or {@link String} ,output identifier
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR(N,... [USING charset_name])</a>
     */
    public static Expression charFunc(final @Nullable Object n, final Object charsetName) {
        final String funcName = "CHAR";

        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParamList(StringType.INSTANCE, n));
        argList.add(SQLFunctions.FuncWord.USING);
        if (charsetName instanceof MySQLCharset) {
            argList.add(charsetName);
        } else if (charsetName instanceof String) {
            argList.add(SQLFunctions.sqlIdentifier((String) charsetName));// sql identifier
        } else {
            throw CriteriaUtils.funcArgError(funcName, charsetName);
        }
        return SQLFunctions.safeComplexArgFunc(funcName, argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_char">CHAR_LENGTH(str)</a>
     */
    public static Expression charLength(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("CHAR_LENGTH", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Collection} or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat">CONCAT(str1,str2,...)</a>
     */
    public static Expression concat(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("CONCAT", SQLs._funcParamList(StringType.INSTANCE, str), StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param separator nullable parameter or {@link Expression}
     * @param str       nullable parameter or {@link Collection} or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_concat-ws">CONCAT_WS(separator,str1,str2,...)</a>
     */
    public static Expression concatWs(final @Nullable Object separator, final @Nullable Object str) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(StringType.INSTANCE, separator));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParamList(StringType.INSTANCE, str));
        return SQLFunctions.safeComplexArgFunc("CONCAT_WS", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n       non-null parameter or {@link Expression}
     * @param strList non-null parameter or non-empty {@link List} or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_elt">ELT(N,str1,str2,str3,...)</a>
     */
    public static Expression elt(final Object n, final Object strList) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(IntegerType.INSTANCE, n));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParamList(StringType.INSTANCE, strList));
        return SQLFunctions.safeComplexArgFunc("ELT", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param bits non-null parameter or {@link Expression}
     * @param on   non-null parameter or {@link Expression}
     * @param off  non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #exportSet(Object, Object, Object, Object)
     * @see #exportSet(Object, Object, Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_export-set">EXPORT_SET(bits,on,off[,separator[,number_of_bits]])</a>
     */
    public static Expression exportSet(final Object bits, final Object on, Object off) {
        final List<Object> argList = new ArrayList<>(5);

        argList.add(SQLs._funcParam(bits));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(on));
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(SQLs._funcParam(off));
        return SQLFunctions.safeComplexArgFunc("EXPORT_SET", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param bits non-null parameter or {@link Expression}
     * @param on   non-null parameter or {@link Expression}
     * @param off  non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #exportSet(Object, Object, Object)
     * @see #exportSet(Object, Object, Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_export-set">EXPORT_SET(bits,on,off[,separator[,number_of_bits]])</a>
     */
    public static Expression exportSet(final Object bits, final Object on, Object off, final Object separator) {
        final List<Object> argList = new ArrayList<>(7);

        argList.add(SQLs._funcParam(bits));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(on));
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(SQLs._funcParam(off));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(separator));
        return SQLFunctions.safeComplexArgFunc("EXPORT_SET", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param bits non-null parameter or {@link Expression}
     * @param on   non-null parameter or {@link Expression}
     * @param off  non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #exportSet(Object, Object, Object)
     * @see #exportSet(Object, Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_export-set">EXPORT_SET(bits,on,off[,separator[,number_of_bits]])</a>
     */
    public static Expression exportSet(final Object bits, final Object on, Object off, final Object separator
            , final Object numberOfBits) {
        final List<Object> argList = new ArrayList<>(9);

        argList.add(SQLs._funcParam(bits));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(on));
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(SQLs._funcParam(off));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(separator));
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(SQLs._funcParam(numberOfBits));
        return SQLFunctions.safeComplexArgFunc("EXPORT_SET", argList, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str     nullable parameter or {@link Expression}
     * @param strList non-null parameter or non-empty {@link List} or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_field">FIELD(str,str1,str2,str3,...)</a>
     */
    public static Expression field(final @Nullable Object str, final Object strList) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParamList(StringType.INSTANCE, strList));
        return SQLFunctions.safeComplexArgFunc("FIELD", argList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str     nullable parameter or {@link Expression}
     * @param strList nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_find-in-set">FIND_IN_SET(str,strlist)</a>
     */
    public static Expression fieldInSet(final @Nullable Object str, final @Nullable Object strList) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(StringType.INSTANCE, strList));
        return SQLFunctions.safeComplexArgFunc("FIND_IN_SET", argList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param x nullable parameter or {@link Expression}
     * @param d nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #format(Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_format">FORMAT(X,D[,locale])</a>
     */
    public static Expression format(final @Nullable Object x, final @Nullable Object d) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(x));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(IntegerType.INSTANCE, d));
        return SQLFunctions.safeComplexArgFunc("FORMAT", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param x      nullable parameter or {@link Expression}
     * @param d      nullable parameter or {@link Expression}
     * @param locale nullable {@link MySQLLocale} or {@link String} or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #format(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_format">FORMAT(X,D[,locale])</a>
     */
    public static Expression format(final @Nullable Object x, final @Nullable Object d, final @Nullable Object locale) {
        final String funcName = "FORMAT";
        final List<Object> argList = new ArrayList<>(5);

        argList.add(SQLs._funcParam(x));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(IntegerType.INSTANCE, d));
        argList.add(SQLFunctions.FuncWord.COMMA);

        if (locale == null) {
            argList.add(SQLs._nullParam());
        } else if (locale instanceof MySQLLocale || locale instanceof ArmyExpression) { //must be ArmyExpression not Expression
            argList.add(locale);
        } else if (locale instanceof String) {
            argList.add(SQLFunctions.sqlIdentifier((String) locale));
        } else {
            throw CriteriaUtils.funcArgError(funcName, locale);
        }
        return SQLFunctions.safeComplexArgFunc(funcName, argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #toBase64(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_from-base64">FROM_BASE64(str)</a>
     */
    public static Expression fromBase64(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("FROM_BASE64", SQLs._funcParam(StringType.INSTANCE, str), StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #fromBase64(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_to-base64">TO_BASE64(str)</a>
     */
    public static Expression toBase64(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("TO_BASE64", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param strOrNum nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #unhex(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_hex">HEX(str), HEX(N)</a>
     */
    public static Expression hex(final @Nullable Object strOrNum) {
        return SQLFunctions.oneArgFunc("HEX", strOrNum, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #hex(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_unhex">UNHEX(str)</a>
     */
    public static Expression unhex(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("UNHEX", SQLs._funcParam(StringType.INSTANCE, str), StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str    nullable parameter or {@link Expression}
     * @param pos    nullable parameter or {@link Expression}
     * @param len    nullable parameter or {@link Expression}
     * @param newStr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_insert">INSERT(str,pos,len,newstr)</a>
     */
    public static Expression insert(final @Nullable Object str, final @Nullable Object pos
            , final @Nullable Object len, final @Nullable Object newStr) {
        final List<Object> argList = new ArrayList<>(7);

        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(IntegerType.INSTANCE, pos));
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(SQLs._funcParam(IntegerType.INSTANCE, len));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(StringType.INSTANCE, newStr));
        return SQLFunctions.safeComplexArgFunc("INSERT", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str    nullable parameter or {@link Expression}
     * @param substr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_instr">INSTR(str,substr)</a>
     */
    public static Expression instr(final @Nullable Object str, final @Nullable Object substr) {
        final List<Object> argList = new ArrayList<>(3);

        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(StringType.INSTANCE, substr));
        return SQLFunctions.safeComplexArgFunc("INSTR", argList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_lower">LOWER(str)</a>
     */
    public static Expression lower(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("LOWER", SQLs._funcParam(StringType.INSTANCE, str), StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #lower(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_upper">UPPER(str)</a>
     */
    public static Expression upper(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("UPPER", SQLs._funcParam(StringType.INSTANCE, str), StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @param len nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_left">LEFT(str,len)</a>
     */
    public static Expression left(final @Nullable Object str, final @Nullable Object len) {
        final List<Object> argList = new ArrayList<>(3);

        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(IntegerType.INSTANCE, len));
        return SQLFunctions.safeComplexArgFunc("LEFT", argList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_length">LENGTH(str)</a>
     */
    public static Expression length(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("LENGTH", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param fileName non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_load-file">LOAD_FILE(fileName)</a>
     */
    public static Expression loadFile(final Object fileName) {
        return SQLFunctions.oneArgFunc("LOAD_FILE", fileName, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param substr nullable parameter or {@link Expression}
     * @param str    nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #locate(Object, Object, Object)
     * @see #position(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_locate">LOCATE(substr,str)</a>
     */
    public static Expression locate(final @Nullable Object substr, final @Nullable Object str) {
        final List<Object> argList = new ArrayList<>(3);

        argList.add(SQLs._funcParam(StringType.INSTANCE, substr));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(IntegerType.INSTANCE, str));
        return SQLFunctions.safeComplexArgFunc("LOCATE", argList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param substr nullable parameter or {@link Expression}
     * @param str    nullable parameter or {@link Expression}
     * @param pos    nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #locate(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_locate">LOCATE(substr,str,pos)</a>
     */
    public static Expression locate(final @Nullable Object substr, final @Nullable Object str
            , final @Nullable Object pos) {
        final List<Object> argList = new ArrayList<>(5);

        argList.add(SQLs._funcParam(StringType.INSTANCE, substr));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(SQLs._funcParam(IntegerType.INSTANCE, pos));
        return SQLFunctions.safeComplexArgFunc("LOCATE", argList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str    nullable parameter or {@link Expression}
     * @param len    nullable parameter or {@link Expression}
     * @param padstr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #rpad(Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_lpad">LPAD(str,len,padstr)</a>
     */
    public static Expression lpad(final @Nullable Object str, final @Nullable Object len
            , final @Nullable Object padstr) {
        return _leftOrRightPad("LPAD", str, len, padstr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str    nullable parameter or {@link Expression}
     * @param len    nullable parameter or {@link Expression}
     * @param padstr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #lpad(Object, Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_rpad">RPAD(str,len,padstr)</a>
     */
    public static Expression rpad(final @Nullable Object str, final @Nullable Object len
            , final @Nullable Object padstr) {
        return _leftOrRightPad("RPAD", str, len, padstr);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #rtrim(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_ltrim">LTRIM(str)</a>
     */
    public static Expression ltrim(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("LTRIM", SQLs._funcParam(StringType.INSTANCE, str), StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #ltrim(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_rtrim">RTRIM(str)</a>
     */
    public static Expression rtrim(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("RTRIM", SQLs._funcParam(StringType.INSTANCE, str), StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param bits    non-null {@link Long} or {@link Integer} or {@link BitSet} or {@link Expression}
     * @param strList non-null {@link String} or {@link  List} or  {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring">MAKE_SET(bits,str1,str2,...)</a>
     */
    public static Expression makeSet(final Object bits, final Object strList) {
        final String funcName = "MAKE_SET";
        final List<Object> argList = new ArrayList<>(3);

        if (bits instanceof Long || bits instanceof Integer) {
            argList.add(SQLs._funcParam(LongType.INSTANCE, bits));
        } else if (bits instanceof BitSet) {
            argList.add(SQLs._funcParam(BitSetType.INSTANCE, bits));
        } else if (bits instanceof ArmyExpression) {
            argList.add(bits);
        } else {
            throw CriteriaUtils.funcArgError(funcName, bits);
        }
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParamList(StringType.INSTANCE, strList));
        return SQLFunctions.safeComplexArgFunc(funcName, argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @param pos nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring">SUBSTRING(str,pos)</a>
     */
    public static Expression subString(final @Nullable Object str, final @Nullable Object pos) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(IntegerType.INSTANCE, pos));
        return SQLFunctions.safeComplexArgFunc("SUBSTRING", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @param pos nullable parameter or {@link Expression}
     * @param len nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring">SUBSTRING(str,pos,len)</a>
     */
    public static Expression subString(final @Nullable Object str, final @Nullable Object pos
            , final @Nullable Object len) {
        final List<Object> argList = new ArrayList<>(5);

        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(IntegerType.INSTANCE, pos));
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(SQLs._funcParam(IntegerType.INSTANCE, len));
        return SQLFunctions.safeComplexArgFunc("SUBSTRING", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_oct">OCT(N)</a>
     */
    public static Expression oct(final @Nullable Object n) {
        return SQLFunctions.oneArgFunc("OCT", n, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_ord">ORD(str)</a>
     */
    public static Expression ord(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("ORD", SQLs._funcParam(StringType.INSTANCE, str), IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param substr nullable parameter or {@link Expression}
     * @param str    nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #locate(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_position">POSITION(substr IN str)</a>
     */
    public static Expression position(final @Nullable Object substr, final @Nullable Object str) {
        final List<Object> argList = new ArrayList<>(3);

        argList.add(SQLs._funcParam(StringType.INSTANCE, substr));
        argList.add(SQLFunctions.FuncWord.IN);
        argList.add(SQLs._funcParam(IntegerType.INSTANCE, str));
        return SQLFunctions.safeComplexArgFunc("POSITION", argList, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_quote">QUOTE(str)</a>
     */
    public static Expression quote(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("QUOTE", SQLs._funcParam(StringType.INSTANCE, str), StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str   nullable parameter or {@link Expression}
     * @param count nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_repeat">REPEAT(str,count)</a>
     */
    public static Expression repeat(final @Nullable Object str, final @Nullable Object count) {
        final List<Object> argList = new ArrayList<>(3);

        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(IntegerType.INSTANCE, count));
        return SQLFunctions.safeComplexArgFunc("REPEAT", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str     nullable parameter or {@link Expression}
     * @param fromStr nullable parameter or {@link Expression}
     * @param toStr   nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_replace">REPLACE(str,from_str,to_str)</a>
     */
    public static Expression replace(final @Nullable Object str, final @Nullable Object fromStr
            , final @Nullable Object toStr) {
        final List<Object> argList = new ArrayList<>(5);

        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(StringType.INSTANCE, fromStr));
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(SQLs._funcParam(StringType.INSTANCE, toStr));
        return SQLFunctions.safeComplexArgFunc("REPLACE", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_reverse">REVERSE(str)</a>
     */
    public static Expression reverse(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("REVERSE", SQLs._funcParam(StringType.INSTANCE, str), StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @param len nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_right">RIGHT(str,len)</a>
     */
    public static Expression right(final @Nullable Object str, final @Nullable Object len) {
        final List<Object> argList = new ArrayList<>(3);

        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(IntegerType.INSTANCE, len));
        return SQLFunctions.safeComplexArgFunc("RIGHT", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_soundex">SOUNDEX(str)</a>
     */
    public static Expression soundex(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("SOUNDEX", SQLs._funcParam(StringType.INSTANCE, str), StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param n nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_space">SPACE(n)</a>
     */
    public static Expression space(final @Nullable Object n) {
        return SQLFunctions.oneArgFunc("SPACE", SQLs._funcParam(IntegerType.INSTANCE, n), StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str   nullable parameter or {@link Expression}
     * @param delim nullable parameter or {@link Expression}
     * @param count nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_substring-index">SUBSTRING_INDEX(str,delim,count)</a>
     */
    public static Expression substringIndex(final @Nullable Object str, final @Nullable Object delim
            , final @Nullable Object count) {
        final List<Object> argList = new ArrayList<>(5);

        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(StringType.INSTANCE, delim));
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(SQLs._funcParam(IntegerType.INSTANCE, count));
        return SQLFunctions.safeComplexArgFunc("SUBSTRING_INDEX", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM(str)</a>
     */
    public static Expression trim(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("TRIM", SQLs._funcParam(StringType.INSTANCE, str), StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param remstr nullable parameter or {@link Expression}
     * @param str    nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM(remstr FROM str)</a>
     */
    public static Expression trim(final @Nullable Object remstr, final @Nullable Object str) {
        final List<Object> argList = new ArrayList<>(3);

        argList.add(SQLs._funcParam(StringType.INSTANCE, remstr));
        argList.add(SQLFunctions.FuncWord.FROM);
        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        return SQLFunctions.safeComplexArgFunc("TRIM", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param position non-null,should be below:
     *                 <ul>
     *                      <li>{@link MySQLWords#BOTH}</li>
     *                      <li>{@link MySQLWords#LEADING}</li>
     *                      <li>{@link MySQLWords#TRAILING}</li>
     *                 </ul>
     * @param remstr   nullable parameter or {@link Expression}
     * @param str      nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_trim">TRIM([BOTH | LEADING | TRAILING] remstr FROM str), TRIM([remstr FROM] str),TRIM(remstr FROM str)</a>
     */
    public static Expression trim(final MySQLWords position, final @Nullable Object remstr
            , final @Nullable Object str) {
        final String funcName = "TRIM";
        switch (position) {
            case BOTH:
            case LEADING:
            case TRAILING:
                break;
            default:
                throw CriteriaUtils.funcArgError(funcName, position);
        }

        final List<Object> argList = new ArrayList<>(4);

        argList.add(position);
        argList.add(SQLs._funcParam(StringType.INSTANCE, remstr));
        argList.add(SQLFunctions.FuncWord.FROM);
        argList.add(SQLs._funcParam(StringType.INSTANCE, str));

        return SQLFunctions.safeComplexArgFunc(funcName, argList, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_weight-string">WEIGHT_STRING(str)</a>
     */
    public static Expression weightString(final @Nullable Object str) {
        return SQLFunctions.oneArgFunc("WEIGHT_STRING", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str  nullable parameter or {@link Expression}
     * @param type non-null {@link  MySQLCastType#CHAR} or {@link  MySQLCastType#BINARY}
     * @param n    non-null parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_weight-string">WEIGHT_STRING(str [AS {CHAR|BINARY}(N)]</a>
     */
    public static Expression weightString(final @Nullable Object str, final MySQLCastType type, final Object n) {
        final String funcName = "WEIGHT_STRING";
        switch (type) {
            case CHAR:
            case BINARY:
                break;
            default:
                throw CriteriaUtils.funcArgError(funcName, type);
        }
        final List<Object> argList = new ArrayList<>(6);

        argList.add(SQLs._funcParam(str));
        argList.add(SQLFunctions.FuncWord.AS);
        argList.add(type);
        argList.add(SQLFunctions.FuncWord.LEFT_PAREN);

        argList.add(SQLs._funcParam(IntegerType.INSTANCE, n));
        argList.add(SQLFunctions.FuncWord.RIGHT_PAREN);

        return SQLFunctions.safeComplexArgFunc(funcName, argList, StringType.INSTANCE);
    }

    /*-------------------below Cast Functions and Operators -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If type is {@link MySQLCastType#BINARY }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#CHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#NCHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#TIME }then {@link LocalTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATE }then {@link LocalDateType}</li>
     *          <li>Else if type is {@link MySQLCastType#YEAR }then {@link YearType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATETIME }then {@link LocalDateTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#SIGNED_INTEGER }then {@link LongType}</li>
     *          <li>Else if type is {@link MySQLCastType#UNSIGNED_INTEGER }then {@link UnsignedBigIntegerType}</li>
     *          <li>Else if type is {@link MySQLCastType#DECIMAL }then {@link BigDecimalType}</li>
     *          <li>Else if type is {@link MySQLCastType#FLOAT }then {@link FloatType}</li>
     *          <li>Else if type is {@link MySQLCastType#REAL }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#DOUBLE }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link JsonType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link ByteArrayType}</li>
     *      </ul>
     * </p>
     *
     * @param exp  non-null {@link Expression}
     * @param type non-null {@link  MySQLCastType}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #cast(Expression, MySQLCastType, Expression)
     * @see #cast(Expression, MySQLCastType, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(expr AS type [ARRAY])</a>
     */
    public static Expression cast(final Expression exp, final MySQLCastType type) {
        CriteriaContextStack.assertNonNull(exp);
        CriteriaContextStack.assertNonNull(type);

        final MappingType returnType;
        final List<Object> argList = new ArrayList<>(3);

        argList.add(exp);
        argList.add(SQLFunctions.FuncWord.AS);
        argList.add(type);
        return SQLFunctions.safeComplexArgFunc("CAST", argList, _castReturnType(type));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If type is {@link MySQLCastType#BINARY }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#CHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#NCHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#TIME }then {@link LocalTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATE }then {@link LocalDateType}</li>
     *          <li>Else if type is {@link MySQLCastType#YEAR }then {@link YearType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATETIME }then {@link LocalDateTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#SIGNED_INTEGER }then {@link LongType}</li>
     *          <li>Else if type is {@link MySQLCastType#UNSIGNED_INTEGER }then {@link UnsignedBigIntegerType}</li>
     *          <li>Else if type is {@link MySQLCastType#DECIMAL }then {@link BigDecimalType}</li>
     *          <li>Else if type is {@link MySQLCastType#FLOAT }then {@link FloatType}</li>
     *          <li>Else if type is {@link MySQLCastType#REAL }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#DOUBLE }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link JsonType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link ByteArrayType}</li>
     *      </ul>
     * </p>
     *
     * @param exp  non-null {@link Expression}
     * @param type non-null {@link  MySQLCastType} ,should be below:
     *             <ul>
     *                  <li>{@link  MySQLCastType#BINARY}</li>
     *                  <li>{@link  MySQLCastType#CHAR}</li>
     *                  <li>{@link  MySQLCastType#NCHAR}</li>
     *                  <li>{@link  MySQLCastType#TIME}</li>
     *                  <li>{@link  MySQLCastType#DATETIME}</li>
     *                  <li>{@link  MySQLCastType#DECIMAL}</li>
     *                  <li>{@link  MySQLCastType#FLOAT}</li>
     *             </ul>
     * @param n    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #cast(Expression, MySQLCastType)
     * @see #cast(Expression, MySQLCastType, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(expr AS type)</a>
     */
    public static Expression cast(final Expression exp, final MySQLCastType type, final Expression n) {
        CriteriaContextStack.assertNonNull(exp);
        final String funcName = "CAST";
        if (!(n instanceof LiteralExpression.SingleLiteral || n instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, n);
        }

        switch (type) {
            case BINARY:
            case CHAR:
            case NCHAR:
            case TIME:
            case DATETIME:
            case DECIMAL:
            case FLOAT:
                break;
            default:
                throw CriteriaUtils.funcArgError(funcName, type);
        }

        final MappingType returnType;
        final List<Object> argList = new ArrayList<>(6);

        argList.add(exp);
        argList.add(SQLFunctions.FuncWord.AS);
        argList.add(type);
        argList.add(SQLFunctions.FuncWord.LEFT_PAREN);

        argList.add(n);
        argList.add(SQLFunctions.FuncWord.RIGHT_PAREN);
        return SQLFunctions.safeComplexArgFunc(funcName, argList, _castReturnType(type));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link BigDecimalType}
     * </p>
     *
     * @param exp  non-null  {@link Expression}
     * @param type currently,support only {@link MySQLCastType#DECIMAL}
     * @param m    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @param d    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #cast(Expression, MySQLCastType)
     * @see #cast(Expression, MySQLCastType, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(expr AS DECIMAL(M,D))</a>
     */
    public static Expression cast(final Expression exp, final MySQLCastType type, final Expression m
            , final Expression d) {
        CriteriaContextStack.assertNonNull(exp);
        final String funcName = "CAST";

        if (type != MySQLCastType.DECIMAL) {
            throw CriteriaUtils.funcArgError(funcName, type);
        }
        if (!(m instanceof LiteralExpression.SingleLiteral || m instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, m);
        }
        if (!(d instanceof LiteralExpression.SingleLiteral || d instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, d);
        }

        final List<Object> argList = new ArrayList<>(8);

        argList.add(exp);
        argList.add(SQLFunctions.FuncWord.AS);
        argList.add(type);
        argList.add(SQLFunctions.FuncWord.LEFT_PAREN);

        argList.add(m);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(d);
        argList.add(SQLFunctions.FuncWord.RIGHT_PAREN);
        return SQLFunctions.safeComplexArgFunc(funcName, argList, BigDecimalType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateTimeType}
     * </p>
     *
     * @param timestampValue    non-null {@link Expression}
     * @param timezoneSpecifier non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(timestamp_value AT TIME ZONE timezone_specifier AS DATETIME[(precision)])</a>
     */
    public static Expression cast(final Expression timestampValue, final Expression timezoneSpecifier) {
        CriteriaContextStack.assertNonNull(timestampValue);
        final String funcName = "CAST";

        if (!(timezoneSpecifier instanceof LiteralExpression.SingleLiteral || timezoneSpecifier instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, timezoneSpecifier);
        }

        final List<Object> argList = new ArrayList<>(5);

        argList.add(timestampValue);
        argList.add(SQLFunctions.FuncWord.AT_TIME_ZONE);
        argList.add(timezoneSpecifier);
        argList.add(SQLFunctions.FuncWord.AS);

        argList.add(MySQLCastType.DATETIME);
        return SQLFunctions.safeComplexArgFunc(funcName, argList, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LocalDateTimeType}
     * </p>
     *
     * @param timestampValue    non-null {@link Expression}
     * @param timezoneSpecifier non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_cast">CAST(timestamp_value AT TIME ZONE timezone_specifier AS DATETIME[(precision)])</a>
     */
    public static Expression cast(final Expression timestampValue, final Expression timezoneSpecifier
            , final Expression precision) {
        CriteriaContextStack.assertNonNull(timestampValue);
        final String funcName = "CAST";

        if (!(timezoneSpecifier instanceof LiteralExpression.SingleLiteral || timezoneSpecifier instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, timezoneSpecifier);
        }
        if (!(precision instanceof LiteralExpression.SingleLiteral || precision instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, precision);
        }

        final List<Object> argList = new ArrayList<>(8);

        argList.add(timestampValue);
        argList.add(SQLFunctions.FuncWord.AT_TIME_ZONE);
        argList.add(timezoneSpecifier);
        argList.add(SQLFunctions.FuncWord.AS);

        argList.add(MySQLCastType.DATETIME);
        argList.add(SQLFunctions.FuncWord.LEFT_PAREN);
        argList.add(precision);
        argList.add(SQLFunctions.FuncWord.RIGHT_PAREN);
        return SQLFunctions.safeComplexArgFunc(funcName, argList, LocalDateTimeType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If type is {@link MySQLCastType#BINARY }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#CHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#NCHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#TIME }then {@link LocalTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATE }then {@link LocalDateType}</li>
     *          <li>Else if type is {@link MySQLCastType#YEAR }then {@link YearType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATETIME }then {@link LocalDateTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#SIGNED_INTEGER }then {@link LongType}</li>
     *          <li>Else if type is {@link MySQLCastType#UNSIGNED_INTEGER }then {@link UnsignedBigIntegerType}</li>
     *          <li>Else if type is {@link MySQLCastType#DECIMAL }then {@link BigDecimalType}</li>
     *          <li>Else if type is {@link MySQLCastType#FLOAT }then {@link FloatType}</li>
     *          <li>Else if type is {@link MySQLCastType#REAL }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#DOUBLE }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link JsonType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link ByteArrayType}</li>
     *      </ul>
     * </p>
     *
     * @param exp             non-null   {@link Expression}
     * @param transcodingName non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_convert">CONVERT(expr USING transcoding_name)</a>
     */
    public static Expression convert(final Expression exp, final MySQLCharset transcodingName) {
        CriteriaContextStack.assertNonNull(exp);
        CriteriaContextStack.assertNonNull(transcodingName);

        final MappingType returnType;
        final List<Object> argList = new ArrayList<>(3);

        argList.add(exp);
        argList.add(SQLFunctions.FuncWord.USING);
        argList.add(transcodingName);
        return SQLFunctions.safeComplexArgFunc("CONVERT", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If type is {@link MySQLCastType#BINARY }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#CHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#NCHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#TIME }then {@link LocalTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATE }then {@link LocalDateType}</li>
     *          <li>Else if type is {@link MySQLCastType#YEAR }then {@link YearType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATETIME }then {@link LocalDateTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#SIGNED_INTEGER }then {@link LongType}</li>
     *          <li>Else if type is {@link MySQLCastType#UNSIGNED_INTEGER }then {@link UnsignedBigIntegerType}</li>
     *          <li>Else if type is {@link MySQLCastType#DECIMAL }then {@link BigDecimalType}</li>
     *          <li>Else if type is {@link MySQLCastType#FLOAT }then {@link FloatType}</li>
     *          <li>Else if type is {@link MySQLCastType#REAL }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#DOUBLE }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link JsonType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link ByteArrayType}</li>
     *      </ul>
     * </p>
     *
     * @param exp  non-null
     * @param type non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_convert">CONVERT(expr,type)</a>
     */
    public static Expression convert(final Expression exp, final MySQLCastType type) {
        CriteriaContextStack.assertNonNull(exp);
        CriteriaContextStack.assertNonNull(type);

        final MappingType returnType;
        final List<Object> argList = new ArrayList<>(3);

        argList.add(exp);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(type);
        return SQLFunctions.safeComplexArgFunc("CONVERT", argList, _castReturnType(type));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If type is {@link MySQLCastType#BINARY }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#CHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#NCHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#TIME }then {@link LocalTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATE }then {@link LocalDateType}</li>
     *          <li>Else if type is {@link MySQLCastType#YEAR }then {@link YearType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATETIME }then {@link LocalDateTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#SIGNED_INTEGER }then {@link LongType}</li>
     *          <li>Else if type is {@link MySQLCastType#UNSIGNED_INTEGER }then {@link UnsignedBigIntegerType}</li>
     *          <li>Else if type is {@link MySQLCastType#DECIMAL }then {@link BigDecimalType}</li>
     *          <li>Else if type is {@link MySQLCastType#FLOAT }then {@link FloatType}</li>
     *          <li>Else if type is {@link MySQLCastType#REAL }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#DOUBLE }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link JsonType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link ByteArrayType}</li>
     *      </ul>
     * </p>
     *
     * @param exp  nullable parameter or {@link Expression}
     * @param type non-null {@link  MySQLCastType} ,should be below:
     *             <ul>
     *                  <li>{@link  MySQLCastType#BINARY}</li>
     *                  <li>{@link  MySQLCastType#CHAR}</li>
     *                  <li>{@link  MySQLCastType#NCHAR}</li>
     *                  <li>{@link  MySQLCastType#TIME}</li>
     *                  <li>{@link  MySQLCastType#DATETIME}</li>
     *                  <li>{@link  MySQLCastType#DECIMAL}</li>
     *                  <li>{@link  MySQLCastType#FLOAT}</li>
     *             </ul>
     * @param n    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_convert">CONVERT(expr,type)</a>
     */
    public static Expression convert(final Expression exp, final MySQLCastType type, final Expression n) {
        CriteriaContextStack.assertNonNull(exp);
        final String funcName = "CONVERT";
        switch (type) {
            case BINARY:
            case CHAR:
            case NCHAR:
            case TIME:
            case DATETIME:
            case DECIMAL:
            case FLOAT:
                break;
            default:
                throw CriteriaUtils.funcArgError(funcName, type);
        }
        if (!(n instanceof LiteralExpression.SingleLiteral || n instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, n);
        }
        final MappingType returnType;
        final List<Object> argList = new ArrayList<>(6);

        argList.add(exp);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(type);
        argList.add(SQLFunctions.FuncWord.LEFT_PAREN);

        argList.add(n);
        argList.add(SQLFunctions.FuncWord.RIGHT_PAREN);
        return SQLFunctions.safeComplexArgFunc(funcName, argList, _castReturnType(type));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BigDecimalType}
     * </p>
     *
     * @param exp  non-null
     * @param type currently must be {@link MySQLCastType#DECIMAL}
     * @param m    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @param d    non-null literal {@link Expression},couldn't be parameter {@link  Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_convert">CONVERT(expr,type)</a>
     */
    public static Expression convert(final Expression exp, final MySQLCastType type, final Expression m
            , final Expression d) {
        CriteriaContextStack.assertNonNull(exp);
        final String funcName = "CONVERT";

        if (type != MySQLCastType.DECIMAL) {
            throw CriteriaUtils.funcArgError(funcName, type);
        }
        if (!(m instanceof LiteralExpression.SingleLiteral || m instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, m);
        }
        if (!(d instanceof LiteralExpression.SingleLiteral || d instanceof LiteralExpression.NamedSingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, d);
        }

        final MappingType returnType;
        final List<Object> argList = new ArrayList<>(8);

        argList.add(exp);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(type);
        argList.add(SQLFunctions.FuncWord.LEFT_PAREN);

        argList.add(m);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(d);
        argList.add(SQLFunctions.FuncWord.RIGHT_PAREN);

        return SQLFunctions.safeComplexArgFunc(funcName, argList, BigDecimalType.INSTANCE);
    }



    /*-------------------below Bit Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param n nullable parameter or {@link Expression}
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/bit-functions.html#function_bit-count">BIT_COUNT(N)</a>
     */
    public static Expression bitCount(final @Nullable Object n) {
        return SQLFunctions.oneArgFunc("BIT_COUNT", n, IntegerType.INSTANCE);
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

        final TypeMeta returnType;
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
        final TypeMeta returnType;
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
                , expression1.typeMeta());
    }


    /*-------------------below private method -------------------*/




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
        final TypeMeta type, returnType;
        type = dateExpr.typeMeta();
        if (type instanceof TypeMeta.Delay) {
            returnType = CriteriaSupports.delayParamMeta((TypeMeta.Delay) type, t -> _dateAddSubReturnType(t, unit));
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


    /**
     * @see #strToDate(Object, Object)
     */
    private static MappingType _strToDateReturnType(final ArmyExpression formatExp, final MappingType type) {
        final MappingType returnType;
        if (formatExp instanceof SqlValueParam.SingleNonNamedValue
                && type instanceof StringType) {
            final Object value;
            value = ((SqlValueParam.SingleNonNamedValue) formatExp).value();
            if (value instanceof String) {
                returnType = MySQLFuncSyntax._parseStrToDateReturnType((String) value);
            } else {
                returnType = StringType.INSTANCE;
            }
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see #_strToDateReturnType(ArmyExpression, MappingType)
     */
    private static MappingType _parseStrToDateReturnType(final String format) {
        final char[] array = format.toCharArray();
        final int last = array.length - 1;
        boolean date = false, time = false;
        outerFor:
        for (int i = 0; i < array.length; i++) {
            if (array[i] != '%' || i == last) {
                continue;
            }
            switch (array[i + 1]) {
                case 'a'://Abbreviated weekday name (Sun..Sat)
                case 'b'://Abbreviated month name (Jan..Dec)
                case 'c'://Month, numeric (0..12)
                case 'D'://Day of the month with English suffix (0th, 1st, 2nd, 3rd, …)
                case 'd'://Day of the month, numeric (00..31)
                case 'e'://Day of the month, numeric (0..31)
                case 'j'://Day of year (001..366)
                case 'M'://Month name (January..December)
                case 'U'://Week (00..53), where Sunday is the first day of the week; WEEK() mode 0
                case 'u'://Week (00..53), where Monday is the first day of the week; WEEK() mode 1
                case 'V'://Week (01..53), where Sunday is the first day of the week; WEEK() mode 2; used with %X
                case 'v'://Week (01..53), where Monday is the first day of the week; WEEK() mode 3; used with %x
                case 'W'://Weekday name (Sunday..Saturday)
                case 'w'://Day of the week (0=Sunday..6=Saturday)
                case 'X'://Year for the week where Sunday is the first day of the week, numeric, four digits; used with %V
                case 'x'://Year for the week, where Monday is the first day of the week, numeric, four digits; used with %v
                case 'Y'://Year, numeric, four digits
                case 'y': {//Year, numeric (two digits)
                    date = true;
                    if (time) {
                        break outerFor;
                    }
                }
                break;
                case 'H'://Hour (00..23)
                case 'h'://Hour (01..12)
                case 'I'://Hour (01..12)
                case 'k'://Hour (0..23)
                case 'l'://Hour (1..12)
                case 'P'://AM or PM
                case 'i'://Minutes, numeric (00..59)
                case 'r'://Time, 12-hour (hh:mm:ss followed by AM or PM)
                case 'S'://Seconds (00..59)
                case 's'://Seconds (00..59)
                case 'T'://Time, 24-hour (hh:mm:ss)
                case 'f': {//Microseconds (000000..999999)
                    time = true;
                    if (date) {
                        break outerFor;
                    }
                }
                break;
                default:
                    //A literal % character
                    //x, for any “x” not listed above
            }

            i++;
        }

        final MappingType type;
        if (date && time) {
            type = LocalDateTimeType.INSTANCE;
        } else if (date) {
            type = LocalDateType.INSTANCE;
        } else if (time) {
            type = LocalTimeType.INSTANCE;
        } else {
            type = StringType.INSTANCE;
        }
        return type;
    }


    /**
     * @see #addDate(Object, Object, MySQLUnit)
     * @see #subDate(Object, Object, MySQLUnit)
     */
    private static Expression _dateIntervalFunc(final String funcName, final @Nullable Object date
            , final @Nullable Object expr, final MySQLUnit unit) {
        final TypeMeta returnType;
        if (unit.isTimePart()) {
            returnType = LocalDateTimeType.INSTANCE;
        } else {
            returnType = LocalDateType.INSTANCE;
        }

        final List<Object> argList = new ArrayList<>(5);

        argList.add(SQLs._funcParam(date));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLFunctions.FuncWord.INTERVAL);
        argList.add(SQLs._funcParam(expr));

        argList.add(unit);
        return SQLFunctions.safeComplexArgFunc(funcName, argList, returnType);
    }


    /**
     * @see #addDate(Object, Object)
     * @see #subDate(Object, Object)
     */
    private static Expression _operateDateFunc(final String funcName, final @Nullable Object date
            , final @Nullable Object days) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(SQLs._funcParam(date));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(days));
        return SQLFunctions.safeComplexArgFunc(funcName, argList, LocalDateType.INSTANCE);
    }

    /**
     * @see #timestampAdd(MySQLUnit, Object, Object)
     */
    private static MappingType _timestampAdd(final MappingType type) {
        final MappingType returnType;
        if (type instanceof LocalDateType) {
            returnType = LocalDateType.INSTANCE;
        } else if (type instanceof LocalDateTimeType
                || type instanceof ZonedDateTimeType
                || type instanceof OffsetDateTimeType) {
            returnType = LocalDateTimeType.INSTANCE;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }


    /**
     * @see #lpad(Object, Object, Object)
     * @see #rpad(Object, Object, Object)
     */
    private static Expression _leftOrRightPad(final String funcName, final @Nullable Object str
            , final @Nullable Object len, final @Nullable Object padstr) {
        final List<Object> argList = new ArrayList<>(5);

        argList.add(SQLs._funcParam(StringType.INSTANCE, str));
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(SQLs._funcParam(IntegerType.INSTANCE, len));
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(SQLs._funcParam(StringType.INSTANCE, padstr));
        return SQLFunctions.safeComplexArgFunc(funcName, argList, StringType.INSTANCE);
    }


    /**
     * @see #cast(Expression, MySQLCastType)
     * @see #convert(Expression, MySQLCastType)
     * @see #convert(Expression, MySQLCastType, Expression)
     */
    private static MappingType _castReturnType(final MySQLCastType type) {
        final MappingType returnType;
        switch (type) {
            case BINARY:
            case Point:
            case MultiPoint:
            case MultiLineString:
            case LineString:
            case Polygon:
            case MultiPolygon:
            case GeometryCollection:
                returnType = ByteArrayType.INSTANCE;
                break;
            case CHAR:
            case NCHAR:
                returnType = StringType.INSTANCE;
                break;
            case TIME:
                returnType = LocalTimeType.INSTANCE;
                break;
            case DATE:
                returnType = LocalDateType.INSTANCE;
                break;
            case YEAR:
                returnType = YearType.INSTANCE;
                break;
            case DATETIME:
                returnType = LocalDateTimeType.INSTANCE;
                break;
            case SIGNED_INTEGER:
                returnType = LongType.INSTANCE;
                break;
            case UNSIGNED_INTEGER:
                returnType = UnsignedBigIntegerType.INSTANCE;
                break;
            case DECIMAL:
                returnType = BigDecimalType.INSTANCE;
                break;
            case FLOAT:
                returnType = FloatType.INSTANCE;
                break;
            case REAL:
            case DOUBLE://Added in MySQL 8.0.17.
                returnType = DoubleType.INSTANCE;
                break;
            case JSON:
                returnType = JsonType.INSTANCE;
                break;
            default:
                throw _Exceptions.unexpectedEnum(type);

        }
        return returnType;
    }


}
