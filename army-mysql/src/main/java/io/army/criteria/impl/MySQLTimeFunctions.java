package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;
import io.army.criteria.SqlValueParam;
import io.army.criteria.mysql.MySQLTimeFormat;
import io.army.criteria.mysql.MySQLTimeUnit;
import io.army.mapping.*;
import io.army.meta.TypeMeta;
import io.army.sqltype.MySQLType;
import io.army.util._Exceptions;

import java.util.Objects;

/**
 * package class
 *
 * @since 1.0
 */
@SuppressWarnings("unused")
abstract class MySQLTimeFunctions extends MySQLStringFunctions {

    MySQLTimeFunctions() {
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
     * @see #addDate(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_adddate">ADDDATE(date,INTERVAL expr unit)</a>
     */
    public static SimpleExpression addDate(final Expression date, final SQLs.WordInterval interval, final Expression expr
            , final MySQLTimeUnit unit) {
        return _dateIntervalFunc("ADDDATE", date, interval, expr, unit);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @param days nullable parameter or {@link Expression}
     * @see #addDate(Expression, SQLs.WordInterval, Expression, MySQLTimeUnit)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_adddate">ADDDATE(date,days)</a>
     */
    public static SimpleExpression addDate(final Expression date, final Expression days) {
        return FunctionUtils.twoArgFunc("ADDDATE", date, days, LocalDateType.INSTANCE);
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
     *             @see #subDate(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_subdate">SUBDATE(date,INTERVAL expr unit)</a>
     */
    public static SimpleExpression subDate(final Expression date, SQLs.WordInterval interval, final Expression expr
            , final MySQLTimeUnit unit) {
        return _dateIntervalFunc("SUBDATE", date, interval, expr, unit);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @param days nullable parameter or {@link Expression}
     * @see #subDate(Expression, SQLs.WordInterval, Expression, MySQLTimeUnit)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_subdate">SUBDATE(expr,days)</a>
     */
    public static SimpleExpression subDate(final Expression date, final Expression days) {
        return FunctionUtils.twoArgFunc("SUBDATE", date, days, LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link  MappingType} of expr1.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_addtime">ADDTIME(expr1,expr2)</a>
     */
    public static SimpleExpression addTime(final Expression expr1, final Expression expr2) {
        return FunctionUtils.twoArgFunc("ADDTIME", expr1, expr2, expr1.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link  MappingType} of expr1.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_subtime">SUBTIME(expr1,expr2)</a>
     */
    public static SimpleExpression subTime(final Expression expr1, final Expression expr2) {
        return FunctionUtils.twoArgFunc("SUBTIME", expr1, expr2, expr1.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateTimeType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_convert-tz">CONVERT_TZ(dt,from_tz,to_tz)</a>
     */
    public static SimpleExpression convertTz(Expression dt, Expression fromTz, Expression toTz) {
        return FunctionUtils.threeArgFunc("CONVERT_TZ", dt, fromTz, toTz, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_curdate">CURRENT_DATE()</a>
     */
    public static SimpleExpression currentDate() {
        return FunctionUtils.zeroArgFunc("CURRENT_DATE", LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalTimeType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_current-time">CURRENT_TIME()</a>
     */
    public static SimpleExpression currentTime() {
        return FunctionUtils.zeroArgFunc("CURRENT_TIME", LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalTimeType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_current-time">CURRENT_TIME(fsp)</a>
     */
    public static SimpleExpression currentTime(final Expression fsp) {
        return FunctionUtils.oneArgFunc("CURRENT_TIME", fsp, LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateTimeType}
     * </p>
     *
     * @see #currentTimestamp(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_current-timestamp">CURRENT_TIMESTAMP()</a>
     */
    public static SimpleExpression currentTimestamp() {
        return FunctionUtils.zeroArgFunc("CURRENT_TIMESTAMP", LocalDateTimeType.INSTANCE);
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
    public static SimpleExpression currentTimestamp(final Expression fsp) {
        return FunctionUtils.oneArgFunc("CURRENT_TIMESTAMP", fsp, LocalDateTimeType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateType}
     * </p>
     *
     * @param expr nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date">DATE(expr)</a>
     */
    public static SimpleExpression date(final Expression expr) {
        return FunctionUtils.oneArgFunc("DATE", expr, LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @param expr1 nullable parameter or {@link Expression}
     * @param expr2 nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_datediff">DATEDIFF(expr1,expr2)</a>
     */
    public static SimpleExpression dateDiff(final Expression expr1, final Expression expr2) {
        return FunctionUtils.twoArgFunc("DATEDIFF", expr1, expr2, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If date or expr is NULL, {@link SQLs._NullType}</li>
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
    public static SimpleExpression dateAdd(final Expression date, final SQLs.WordInterval interval, final Expression expr
            , final MySQLTimeUnit unit) {
        return _dateAddOrSub("DATE_ADD", date, interval, expr, unit);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If date or expr is NULL, {@link SQLs._NullType}</li>
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
    public static SimpleExpression dateSub(final Expression date, final SQLs.WordInterval interval, final Expression expr
            , final MySQLTimeUnit unit) {
        return _dateAddOrSub("DATE_SUB", date, interval, expr, unit);
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
    public static SimpleExpression dateFormat(final Expression date, final Expression format) {
        return FunctionUtils.twoArgFunc("DATE_FORMAT", date, format, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayofmonth">DAYOFMONTH(date)</a>
     */
    public static SimpleExpression dayOfMonth(final Expression date) {
        return FunctionUtils.oneArgFunc("DAYOFMONTH", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DayOfWeekType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayname">DAYNAME(date)</a>
     */
    public static SimpleExpression dayName(final Expression date) {
        return FunctionUtils.oneArgFunc("DAYNAME", date, DayOfWeekType.DEFAULT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DayOfWeekType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayofweek">DAYOFYEAR(date)</a>
     */
    public static SimpleExpression dayOfWeek(final Expression date) {
        return FunctionUtils.oneArgFunc("DAYOFWEEK", date, DayOfWeekType.DEFAULT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayofyear">DAYOFYEAR(date)</a>
     */
    public static SimpleExpression dayOfYear(final Expression date) {
        return FunctionUtils.oneArgFunc("DAYOFYEAR", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>unit {@link MySQLTimeUnit#YEAR}:{@link YearType}</li>
     *          <li>unit {@link MySQLTimeUnit#MONTH}:{@link MonthType}</li>
     *          <li>unit {@link MySQLTimeUnit#WEEK}:{@link DayOfWeekType}</li>
     *          <li>unit {@link MySQLTimeUnit#YEAR_MONTH}:{@link YearMonthType}</li>
     *          <li>otherwise:{@link IntegerType}</li>
     *      </ul>
     * </p>
     *
     * @param unit non-null
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_extract">EXTRACT(date)</a>
     */
    public static SimpleExpression extract(final MySQLTimeUnit unit, final SQLs.WordFrom from, final Expression date) {
        final String name = "EXTRACT";
        if (from != SQLs.FROM) {
            throw CriteriaUtils.funcArgError(name, from);
        } else if (!(date instanceof ArmyExpression)) {
            throw CriteriaUtils.funcArgError(name, date);
        }
        final MappingType returnType;
        switch (unit) {
            case YEAR:
                returnType = YearType.INSTANCE;
                break;
            case MONTH:
                returnType = MonthType.DEFAULT;
                break;
            case WEEK:
                returnType = DayOfWeekType.DEFAULT;
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
        return FunctionUtils.complexArgFunc(name, returnType, unit, from, date);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateType}
     * </p>
     *
     * @param n nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_from-days">FROM_DAYS(date)</a>
     */
    public static SimpleExpression fromDays(final Expression n) {
        return FunctionUtils.oneArgFunc("FROM_DAYS", n, LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @param unixTimestamp nullable parameter or {@link Expression}
     * @see #fromUnixTime(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_from-unixtime">FROM_UNIXTIME(unix_timestamp[,format])</a>
     */
    public static SimpleExpression fromUnixTime(final Expression unixTimestamp) {
        return FunctionUtils.oneArgFunc("FROM_UNIXTIME", unixTimestamp, LocalDateTimeType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param unixTimestamp nullable parameter or {@link Expression}
     * @param format        nullable parameter or {@link Expression}
     * @see #fromUnixTime(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_from-unixtime">FROM_UNIXTIME(unix_timestamp[,format])</a>
     */
    public static SimpleExpression fromUnixTime(final Expression unixTimestamp, final Expression format) {
        return FunctionUtils.twoArgFunc("FROM_UNIXTIME", unixTimestamp, format, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param type   non-null,should be below:
     *               <ul>
     *                      <li>{@link MySQLType#TIME}</li>
     *                      <li>{@link MySQLType#DATE}</li>
     *                      <li>{@link MySQLType#DATETIME}</li>
     *               </ul>
     * @param format nullable
     * @throws CriteriaException throw when 1.type error;2.invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_get-format">GET_FORMAT({DATE|TIME|DATETIME}, {'EUR'|'USA'|'JIS'|'ISO'|'INTERNAL'})</a>
     */
    public static SimpleExpression getFormat(final MySQLType type, final MySQLTimeFormat format) {
        final String name = "GET_FORMAT";
        switch (type) {
            case TIME:
            case DATE:
            case DATETIME:
                break;
            default:
                throw CriteriaUtils.funcArgError(name, type);
        }
        Objects.requireNonNull(format);
        return FunctionUtils.complexArgFunc(name, StringType.INSTANCE, type, Functions.FuncWord.COMMA, format);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param time nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_hour">HOUR(time)</a>
     */
    public static SimpleExpression hour(final Expression time) {
        return FunctionUtils.oneArgFunc("HOUR", time, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_last-day">LAST_DAY(date)</a>
     */
    public static SimpleExpression lastDay(final Expression date) {
        return FunctionUtils.oneArgFunc("LAST_DAY", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @see #now(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_now">NOW([fsp])</a>
     */
    public static SimpleExpression now() {
        return FunctionUtils.zeroArgFunc("NOW", LocalDateTimeType.INSTANCE);
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
    public static SimpleExpression now(final Expression fsp) {
        return FunctionUtils.oneArgFunc("NOW", fsp, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @see #sysDate(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_sysdate">SYSDATE([fsp])</a>
     */
    public static SimpleExpression sysDate() {
        return FunctionUtils.zeroArgFunc("SYSDATE", LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @see #sysDate()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_sysdate">SYSDATE([fsp])</a>
     */
    public static SimpleExpression sysDate(final Expression fsp) {
        return FunctionUtils.oneArgFunc("SYSDATE", fsp, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @see #timestamp(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timestamp">TIMESTAMP(expr)</a>
     */
    public static SimpleExpression timestamp(final Expression expr) {
        return FunctionUtils.oneArgFunc("TIMESTAMP", expr, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @see #timestamp(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timestamp">TIMESTAMP(expr1,expr2)</a>
     */
    public static SimpleExpression timestamp(final Expression expr1, final Expression expr2) {
        return FunctionUtils.twoArgFunc("TIMESTAMP", expr1, expr2, LocalDateTimeType.INSTANCE);
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
     *                          <li>{@link MySQLTimeUnit#MICROSECOND}</li>
     *                          <li>{@link MySQLTimeUnit#SECOND}</li>
     *                          <li>{@link MySQLTimeUnit#MINUTE}</li>
     *                          <li>{@link MySQLTimeUnit#HOUR}</li>
     *
     *                          <li>{@link MySQLTimeUnit#DAY}</li>
     *                          <li>{@link MySQLTimeUnit#WEEK}</li>
     *                          <li>{@link MySQLTimeUnit#MONTH}</li>
     *                          <li>{@link MySQLTimeUnit#QUARTER}</li>
     *
     *                          <li>{@link MySQLTimeUnit#YEAR}</li>
     *                     </ul>
     * @param interval     nullable parameter or {@link Expression}
     * @param datetimeExpr nullable parameter or {@link Expression}
     * @throws CriteriaException throw when unit error or invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timestampadd">TIMESTAMPADD(unit,interval,datetime_expr)</a>
     */
    public static SimpleExpression timestampAdd(final MySQLTimeUnit unit, final Expression interval
            , final Expression datetimeExpr) {

        final String name = "TIMESTAMPADD";
        if (!(interval instanceof ArmyExpression)) {
            throw CriteriaUtils.funcArgError(name, interval);
        } else if (!(datetimeExpr instanceof ArmyExpression)) {
            throw CriteriaUtils.funcArgError(name, datetimeExpr);
        }
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
                type = datetimeExpr.typeMeta();
                returnType = _timestampAdd(type.mappingType());
            }
            break;
            default:
                throw CriteriaUtils.funcArgError(name, unit);
        }
        return FunctionUtils.complexArgFunc(name, returnType, unit, Functions.FuncWord.COMMA, interval
                , Functions.FuncWord.COMMA, datetimeExpr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If {@link MySQLTimeUnit#MINUTE} or {@link MySQLTimeUnit#SECOND} or {@link MySQLTimeUnit#MICROSECOND} then {@link LongType}</li>
     *          <li>Else {@link IntegerType}</li>
     *      </ul>
     * </p>
     *
     * @param unit          non-null
     * @param datetimeExpr1 nullable parameter or {@link  Expression}
     * @param datetimeExpr2 nullable parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timestampdiff">TIMESTAMPDIFF(unit,datetime_expr1,datetime_expr2)</a>
     */
    public static SimpleExpression timestampDiff(final MySQLTimeUnit unit, final Expression datetimeExpr1
            , final Expression datetimeExpr2) {
        final String name = "TIMESTAMPADD";
        if (!(datetimeExpr1 instanceof ArmyExpression)) {
            throw CriteriaUtils.funcArgError(name, datetimeExpr1);
        } else if (!(datetimeExpr2 instanceof ArmyExpression)) {
            throw CriteriaUtils.funcArgError(name, datetimeExpr2);
        }

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

        return FunctionUtils.complexArgFunc(name, returnType, unit, Functions.FuncWord.COMMA, datetimeExpr1
                , Functions.FuncWord.COMMA, datetimeExpr2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param time   nullable parameter or {@link Expression}
     * @param format nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_time-format">TIME_FORMAT(time,format)</a>
     */
    public static SimpleExpression timeFormat(final Expression time, Expression format) {
        return FunctionUtils.twoArgFunc("TIME_FORMAT", time, format, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_to-days">TO_DAYS(date)</a>
     */
    public static SimpleExpression toDays(final Expression date) {
        return FunctionUtils.oneArgFunc("TO_DAYS", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @param expr nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_to-seconds">TO_SECONDS(expr)</a>
     */
    public static SimpleExpression toSeconds(final Expression expr) {
        return FunctionUtils.oneArgFunc("TO_SECONDS", expr, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_unix-timestamp">UNIX_TIMESTAMP()</a>
     */
    public static SimpleExpression unixTimestamp() {
        return FunctionUtils.zeroArgFunc("UNIX_TIMESTAMP", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_unix-timestamp">UNIX_TIMESTAMP(date)</a>
     */
    public static SimpleExpression unixTimestamp(final Expression date) {
        return FunctionUtils.oneArgFunc("UNIX_TIMESTAMP", date, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-date">UTC_DATE()</a>
     */
    public static SimpleExpression utcDate() {
        return FunctionUtils.zeroArgFunc("UTC_DATE", LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalTimeType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-time">UTC_TIME()</a>
     */
    public static SimpleExpression utcTime() {
        return FunctionUtils.zeroArgFunc("UTC_DATE", LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalTimeType}
     * </p>
     *
     * @param fsp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-time">UTC_TIME(fsp)</a>
     */
    public static SimpleExpression utcTime(final Expression fsp) {
        return FunctionUtils.oneArgFunc("UTC_TIME", fsp, LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-timestamp">UTC_TIMESTAMP()</a>
     */
    public static SimpleExpression utcTimestamp() {
        return FunctionUtils.zeroArgFunc("UTC_TIMESTAMP", LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     * </p>
     *
     * @param fsp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-timestamp">UTC_TIMESTAMP(fsp)</a>
     */
    public static SimpleExpression utcTimestamp(final Expression fsp) {
        return FunctionUtils.oneArgFunc("UTC_TIMESTAMP", fsp, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_week">WEEK(date)</a>
     */
    public static SimpleExpression week(final Expression date) {
        return FunctionUtils.oneArgFunc("WEEK", date, IntegerType.INSTANCE);
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
    public static SimpleExpression week(final Expression date, final Expression mode) {
        return FunctionUtils.twoArgFunc("WEEK", date, mode, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DayOfWeekType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_weekday">WEEKDAY(date)</a>
     */
    public static SimpleExpression weekDay(final Expression date) {
        return FunctionUtils.oneArgFunc("WEEKDAY", date, DayOfWeekType.DEFAULT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_weekofyear">WEEKOFYEAR(date)</a>
     */
    public static SimpleExpression weekOfYear(final Expression date) {
        return FunctionUtils.oneArgFunc("WEEKOFYEAR", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link YearType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_year">YEAR(date)</a>
     */
    public static SimpleExpression year(final Expression date) {
        return FunctionUtils.oneArgFunc("YEAR", date, YearType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_yearweek">YEARWEEK(date)</a>
     */
    public static SimpleExpression yearWeek(final Expression date) {
        return FunctionUtils.oneArgFunc("YEARWEEK", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_yearweek">YEARWEEK(date,mode)</a>
     */
    public static SimpleExpression yearWeek(final Expression date, final Expression mode) {
        return FunctionUtils.twoArgFunc("YEARWEEK", date, mode, IntegerType.INSTANCE);
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
    public static SimpleExpression makeDate(final Expression year, final Expression dayOfYear) {
        return FunctionUtils.twoArgFunc("MAKEDATE", year, dayOfYear, LocalDateType.INSTANCE);
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
    public static SimpleExpression makeTime(Expression hour, Expression minute, Expression second) {
        return FunctionUtils.threeArgFunc("MAKETIME", hour, minute, second, LocalTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param expr non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_microsecond">MICROSECOND(expr)</a>
     */
    public static SimpleExpression microSecond(final Expression expr) {
        return FunctionUtils.oneArgFunc("MICROSECOND", expr, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param time non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_minute">MINUTE(expr)</a>
     */
    public static SimpleExpression minute(final Expression time) {
        return FunctionUtils.oneArgFunc("MINUTE", time, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link MonthType}
     * </p>
     *
     * @param date non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_month">MONTH(date)</a>
     */
    public static SimpleExpression month(final Expression date) {
        return FunctionUtils.oneArgFunc("MONTH", date, MonthType.DEFAULT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link MonthType}
     * </p>
     *
     * @param date non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_monthname">MONTHNAME(date)</a>
     */
    public static SimpleExpression monthName(final Expression date) {
        return FunctionUtils.oneArgFunc("MONTHNAME", date, MonthType.DEFAULT);
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
    public static SimpleExpression periodAdd(final Expression p, final Expression n) {
        return FunctionUtils.twoArgFunc("PERIOD_ADD", p, n, YearMonthType.INSTANCE);
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
    public static SimpleExpression periodDiff(final Expression p1, final Expression p2) {
        return FunctionUtils.twoArgFunc("PERIOD_DIFF", p1, p2, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param date non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_quarter">QUARTER(date)</a>
     */
    public static SimpleExpression quarter(final Expression date) {
        return FunctionUtils.oneArgFunc("QUARTER", date, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalTimeType}
     * </p>
     *
     * @param expr nullable parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_time">TIME(expr)</a>
     */
    public static SimpleExpression time(final Expression expr) {
        return FunctionUtils.oneArgFunc("TIME", expr, LocalTimeType.INSTANCE);
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
    public static SimpleExpression timeDiff(final Expression expr1, final Expression expr2) {
        return FunctionUtils.twoArgFunc("TIMEDIFF", expr1, expr2, expr1.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param time non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_second">SECOND(time)</a>
     */
    public static SimpleExpression second(final Expression time) {
        return FunctionUtils.oneArgFunc("SECOND", time, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalTimeType}
     * </p>
     *
     * @param seconds non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_sec-to-time">SEC_TO_TIME(seconds)</a>
     */
    public static SimpleExpression secToTime(final Expression seconds) {
        return FunctionUtils.oneArgFunc("SEC_TO_TIME", seconds, LocalTimeType.INSTANCE);
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
    public static SimpleExpression strToDate(final Expression str, final Expression format) {
        final TypeMeta formatType;
        formatType = format.typeMeta();
        final TypeMeta returnType;
        returnType = _strToDateReturnType((ArmyExpression) format, formatType.mappingType());
        return FunctionUtils.twoArgFunc("STR_TO_DATE", str, format, returnType);
    }


    /*-------------------below private method-------------------*/


    /**
     * @see #addDate(Expression, SQLs.WordInterval, Expression, MySQLTimeUnit)
     * @see #subDate(Expression, SQLs.WordInterval, Expression, MySQLTimeUnit)
     */
    private static SimpleExpression _dateIntervalFunc(final String name, final Expression date
            , final SQLs.WordInterval interval, final Expression expr, final MySQLTimeUnit unit) {
        final TypeMeta returnType;
        if (unit.isTimePart()) {
            returnType = LocalDateTimeType.INSTANCE;
        } else {
            returnType = LocalDateType.INSTANCE;
        }

        if (!(date instanceof ArmyExpression)) {
            throw CriteriaUtils.funcArgError(name, date);
        } else if (interval != SQLs.INTERVAL) {
            throw CriteriaUtils.funcArgError(name, interval);
        }
        if (!(expr instanceof ArmyExpression)) {
            throw CriteriaUtils.funcArgError(name, date);
        }
        return FunctionUtils.complexArgFunc(name, returnType, date, Functions.FuncWord.COMMA, interval, expr, unit);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If date or expr is NULL, {@link SQLs._NullType}</li>
     *          <li>If date {@link MappingType} is {@link LocalDateType} and unit no time parts then {@link LocalDateType},otherwise {@link LocalDateTimeType}</li>
     *          <li>If date {@link MappingType} is {@link LocalTimeType} and unit no date parts then {@link LocalTimeType},otherwise {@link LocalDateTimeType}</li>
     *          <li>If date {@link MappingType} is {@link LocalDateTimeType} or {@link OffsetDateTimeType} or {@link ZonedDateTimeType} then {@link LocalDateTimeType}</li>
     *          <li>otherwise {@link StringType}</li>
     *      </ul>
     * </p>
     *
     * @param name DATE_ADD or DATE_SUB
     * @param date nullable parameter or {@link Expression}
     * @param expr nullable parameter or {@link Expression}
     * @param unit non-null
     * @see #dateAdd(Expression, SQLs.WordInterval, Expression, MySQLTimeUnit)
     * @see #dateSub(Expression, SQLs.WordInterval, Expression, MySQLTimeUnit)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date-add">DATE_ADD(date,INTERVAL expr unit), DATE_SUB(date,INTERVAL expr unit)</a>
     */
    private static SimpleExpression _dateAddOrSub(final String name, final Expression date
            , final SQLs.WordInterval interval, final Expression expr, final MySQLTimeUnit unit) {
        final TypeMeta type, returnType;
        type = date.typeMeta();
        returnType = _dateAddSubReturnType(type.mappingType(), unit);
        return FunctionUtils.complexArgFunc(name, returnType, date, Functions.FuncWord.COMMA, interval, expr, unit);
    }


    /**
     * @see #dateAdd(Expression, SQLs.WordInterval, Expression, MySQLTimeUnit)
     */
    private static MappingType _dateAddSubReturnType(final MappingType type, final MySQLTimeUnit unit) {
        final MappingType returnType;
        if (type instanceof SQLs._NullType) {
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
     * @see #timestampAdd(MySQLTimeUnit, Expression, Expression)
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
     * @see #strToDate(Expression, Expression)
     */
    private static MappingType _strToDateReturnType(final ArmyExpression formatExp, final MappingType type) {
        final MappingType returnType;
        if (formatExp instanceof SqlValueParam.SingleAnonymousValue
                && type instanceof StringType) {
            final Object value;
            value = ((SqlValueParam.SingleAnonymousValue) formatExp).value();
            if (value instanceof String) {
                returnType = _parseStrToDateReturnType((String) value);
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


}
