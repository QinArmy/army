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

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;
import io.army.criteria.SqlValueParam;
import io.army.mapping.*;
import io.army.meta.TypeMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.util._TimeUtils;

import java.time.*;
import java.util.Arrays;

/**
 * package class
 *
 * @since 0.6.0
 */
@SuppressWarnings("unused")
abstract class MySQLTimeFunctions extends MySQLStringFunctions {

    MySQLTimeFunctions() {
    }




    /*-------------------below Date and Time Functions-------------------*/

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If unit is time part:{@link  LocalDateTimeType}</li>
     *     <li>else :{@link  LocalDateType}</li>
     * </ul>
     *
     * @param date non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link java.time.LocalDate} instance</li>
     *                  <li>date {@link String} literal,eg : {@code "2008-01-02"}</li>
     *             </ul>
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link Integer} literal</li>
     *             </ul>
     * @param unit non-null
     * @throws CriteriaException throw when argument error
     * @see #addDate(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_adddate">ADDDATE(date,INTERVAL expr unit)</a>
     */
    public static SimpleExpression addDate(Object date, SQLs.WordInterval interval, Object expr, MySQLTimeUnit unit) {
        return _dateIntervalFunc("ADDDATE", date, interval, expr, unit);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link LocalDateType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link java.time.LocalDate} instance</li>
     *                  <li>date {@link String} literal,eg : {@code "2008-01-02"}</li>
     *             </ul>
     * @param days non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link Integer} literal</li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see #addDate(Object, SQLs.WordInterval, Object, MySQLTimeUnit)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_adddate">ADDDATE(date,days)</a>
     */
    public static SimpleExpression addDate(Object date, final Object days) {
        date = FuncExpUtils.localDateLiteralExp(date);
        FuncExpUtils.assertIntExp(days);
        return LiteralFunctions.twoArgFunc("ADDDATE", date, days, LocalDateType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If unit is time part:{@link  LocalDateTimeType}</li>
     *     <li>else :{@link  LocalDateType}</li>
     * </ul>
     *
     * @param date non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link java.time.LocalDate} instance</li>
     *                  <li>date {@link String} literal,eg : {@code "2008-01-02"}</li>
     *             </ul>
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link Integer} literal</li>
     *             </ul>
     * @param unit non-null
     * @see #subDate(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_subdate">SUBDATE(date,INTERVAL expr unit)</a>
     */
    public static SimpleExpression subDate(Object date, SQLs.WordInterval interval, Object expr, MySQLTimeUnit unit) {
        return _dateIntervalFunc("SUBDATE", date, interval, expr, unit);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalDateType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link java.time.LocalDate} instance</li>
     *                  <li>date {@link String} literal,eg : {@code "2008-01-02"}</li>
     *             </ul>
     * @param days non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link Integer} literal</li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see #subDate(Object, SQLs.WordInterval, Object, MySQLTimeUnit)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_subdate">SUBDATE(expr,days)</a>
     */
    public static SimpleExpression subDate(Object date, final Object days) {
        date = FuncExpUtils.localDateLiteralExp(date);
        FuncExpUtils.assertIntExp(days);
        return LiteralFunctions.twoArgFunc("SUBDATE", date, days, LocalDateType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>expr1 is {@link Expression} : return the {@link  MappingType} of expr1.</li>
     *     <li>expr1 is {@link LocalDateTime} : return {@link  LocalDateTimeType} </li>
     *     <li>expr1 is {@link LocalTime} : return {@link  LocalTimeType} </li>
     *     <li>expr1 is {@link OffsetDateTime} : return {@link  LocalDateTimeType} </li>
     *     <li>expr1 is {@link ZonedDateTime} : return {@link  LocalDateTimeType} </li>
     *     <li>expr1 is datetime literal : return {@link  LocalDateTimeType} </li>
     *     <li>expr1 is time literal : return {@link  LocalTimeType} </li>
     *     <li>expr1 is datetime with zone literal: return {@link  LocalDateTimeType} </li>
     * </ul>
     *
     * @param expr1 non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link java.time.LocalTime} instance</li>
     *                   <li>{@link java.time.LocalDateTime} instance</li>
     *                   <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                   <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                   <li>{@link String} literal,eg : {@code "01:00:00"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *              </ul>
     * @param expr2 non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link java.time.LocalTime} instance</li>
     *                   <li>{@link String} literal,eg : {@code "01:00:00"}</li>
     *              </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_addtime">ADDTIME(expr1,expr2)</a>
     */
    public static SimpleExpression addTime(Object expr1, final Object expr2) {
        return _addOrSubTime("ADDTIME", expr1, expr2);
    }


    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>expr1 is {@link Expression} : return the {@link  MappingType} of expr1.</li>
     *     <li>expr1 is {@link LocalDateTime} : return {@link  LocalDateTimeType} </li>
     *     <li>expr1 is {@link LocalTime} : return {@link  LocalTimeType} </li>
     *     <li>expr1 is {@link OffsetDateTime} : return {@link  LocalDateTimeType} </li>
     *     <li>expr1 is {@link ZonedDateTime} : return {@link  LocalDateTimeType} </li>
     *     <li>expr1 is datetime literal : return {@link  LocalDateTimeType} </li>
     *     <li>expr1 is time literal : return {@link  LocalTimeType} </li>
     *     <li>expr1 is datetime with zone literal: return {@link  LocalDateTimeType} </li>
     * </ul>
     *
     * @param expr1 non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link java.time.LocalTime} instance</li>
     *                   <li>{@link java.time.LocalDateTime} instance</li>
     *                   <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                   <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                   <li>{@link String} literal,eg : {@code "01:00:00"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *              </ul>
     * @param expr2 non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link java.time.LocalTime} instance</li>
     *                   <li>{@link String} literal,eg : {@code "01:00:00"}</li>
     *              </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_subtime">SUBTIME(expr1,expr2)</a>
     */
    public static SimpleExpression subTime(Object expr1, Object expr2) {
        return _addOrSubTime("SUBTIME", expr1, expr2);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  LocalDateTimeType}
     *
     * @param dt     non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link java.time.LocalDateTime} instance</li>
     *                   <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                   <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                   <li>{@link String} literal,eg : {@code "01:00:00"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *               </ul>
     * @param fromTz non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @param toTz   non-null, one of following :
     *               <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link String} literal</li>
     *               </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_convert-tz">CONVERT_TZ(dt,from_tz,to_tz)</a>
     */
    public static SimpleExpression convertTz(Object dt, final Object fromTz, final Object toTz) {
        dt = FuncExpUtils.localOffsetDateTimeLiteralExp(dt);
        FuncExpUtils.assertTextExp(fromTz);
        FuncExpUtils.assertTextExp(toTz);
        return LiteralFunctions.threeArgFunc("CONVERT_TZ", dt, fromTz, toTz, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  LocalDateType}
     *
     * @see MySQLs#CURRENT_DATE
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_curdate">CURRENT_DATE()</a>
     */
    public static SimpleExpression currentDate() {
        return LiteralFunctions.zeroArgFunc("CURRENT_DATE", LocalDateType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  LocalTimeType}
     *
     * @see MySQLs#CURRENT_TIME
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_current-time">CURRENT_TIME()</a>
     */
    public static SimpleExpression currentTime() {
        return LiteralFunctions.zeroArgFunc("CURRENT_TIME", LocalTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  LocalTimeType}
     *
     * @param fsp non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link Integer} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_current-time">CURRENT_TIME(fsp)</a>
     */
    public static SimpleExpression currentTime(final Object fsp) {
        FuncExpUtils.assertIntExp(fsp);
        return LiteralFunctions.oneArgFunc("CURRENT_TIME", fsp, LocalTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  LocalDateTimeType}
     *
     * @throws CriteriaException throw when argument error
     * @see #currentTimestamp(Object)
     * @see MySQLs#CURRENT_TIMESTAMP
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_current-timestamp">CURRENT_TIMESTAMP()</a>
     */
    public static SimpleExpression currentTimestamp() {
        return LiteralFunctions.zeroArgFunc("CURRENT_TIMESTAMP", LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateTimeType}
     *
     * @param fsp non-null, one of following :
     *            <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link Integer} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see #currentTimestamp()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_current-time">CURRENT_TIMESTAMP(fsp)</a>
     */
    public static SimpleExpression currentTimestamp(final Object fsp) {
        FuncExpUtils.assertIntExp(fsp);
        return LiteralFunctions.oneArgFunc("CURRENT_TIMESTAMP", fsp, LocalDateTimeType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link  LocalDateType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link java.time.LocalDate} instance</li>
     *                  <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg : {@code "2007-12-31"}  {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date">DATE(expr)</a>
     */
    public static SimpleExpression date(Object expr) {
        expr = mysqlTimeTypeLiteralExp(expr);
        if (expr instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("DATE", expr, LocalDateType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  IntegerType}
     *
     * @param expr1 non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link java.time.LocalDate} instance</li>
     *                   <li>{@link java.time.LocalDateTime} instance</li>
     *                  <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                  <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                  <li>{@link String} literal,eg : {@code "2007-12-31"}  {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *              </ul>
     * @param expr2 non-null, one of following :
     *              <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link java.time.LocalDate} instance</li>
     *                   <li>{@link java.time.LocalDateTime} instance</li>
     *                  <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                  <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                  <li>{@link String} literal,eg : {@code "2007-12-31"}  {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *              </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_datediff">DATEDIFF(expr1,expr2)</a>
     */
    public static SimpleExpression dateDiff(Object expr1, Object expr2) {
        expr1 = mysqlTimeTypeLiteralExp(expr1);
        if (expr1 instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        expr2 = mysqlTimeTypeLiteralExp(expr2);
        if (expr2 instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.twoArgFunc("DATEDIFF", expr1, expr2, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If date {@link MappingType} is {@link LocalDateType} and unit no time parts then {@link LocalDateType},otherwise {@link LocalDateTimeType}</li>
     *     <li>If date {@link MappingType} is {@link LocalTimeType} and unit no date parts then {@link LocalTimeType},otherwise {@link LocalDateTimeType}</li>
     *     <li>If date {@link MappingType} is {@link LocalDateTimeType} or {@link OffsetDateTimeType} or {@link ZonedDateTimeType} then {@link LocalDateTimeType}</li>
     *     <li>otherwise {@link StringType}</li>
     * </ul>
     *
     * @param date     non-null, one of following :
     *                 <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link java.time.LocalDate} instance</li>
     *                     <li>{@link java.time.LocalDateTime} instance</li>
     *                     <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                     <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                     <li>{@link String} literal,eg : {@code "2007-12-31"}  {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *                 </ul>
     * @param interval see {@link SQLs#INTERVAL}
     * @param expr     non-null, one of following :
     *                 <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link Integer} literal</li>
     *                 </ul>
     * @param unit     non-null
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date-add">DATE_ADD(date,INTERVAL expr unit)</a>
     */
    public static SimpleExpression dateAdd(Object date, SQLs.WordInterval interval, Object expr, MySQLTimeUnit unit) {
        return _dateAddOrSub("DATE_ADD", date, interval, expr, unit);
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If date {@link MappingType} is {@link LocalDateType} and unit no time parts then {@link LocalDateType},otherwise {@link LocalDateTimeType}</li>
     *     <li>If date {@link MappingType} is {@link LocalTimeType} and unit no date parts then {@link LocalTimeType},otherwise {@link LocalDateTimeType}</li>
     *     <li>If date {@link MappingType} is {@link LocalDateTimeType} or {@link OffsetDateTimeType} or {@link ZonedDateTimeType} then {@link LocalDateTimeType}</li>
     *     <li>otherwise {@link StringType}</li>
     * </ul>
     *
     * @param date     non-null, one of following :
     *                 <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link java.time.LocalDate} instance</li>
     *                     <li>{@link java.time.LocalDateTime} instance</li>
     *                     <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                     <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                     <li>{@link String} literal,eg : {@code "2007-12-31"}  {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *                 </ul>
     * @param interval see {@link SQLs#INTERVAL}
     * @param expr     non-null, one of following :
     *                 <ul>
     *                     <li>{@link Expression} instance</li>
     *                     <li>{@link Integer} literal</li>
     *                 </ul>
     * @param unit     non-null
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date-add">DATE_SUB(date,INTERVAL expr unit)</a>
     */
    public static SimpleExpression dateSub(Object date, SQLs.WordInterval interval, Object expr, MySQLTimeUnit unit) {
        return _dateAddOrSub("DATE_SUB", date, interval, expr, unit);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param date   non-null, one of following :
     *               <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link java.time.LocalTime} instance</li>
     *                   <li>{@link java.time.LocalDate} instance</li>
     *                   <li>{@link java.time.LocalDateTime} instance</li>
     *                   <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                   <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                   <li>{@link String} literal,eg : {@code "23:59:59"} ,{@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *               </ul>
     * @param format non-null, one of following :
     *               <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link String} literal</li>
     *               </ul>
     * @throws CriteriaException throw when argument error
     * @see #timeFormat(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date-format">DATE_FORMAT(date,format)</a>
     */
    public static SimpleExpression dateFormat(Object date, final Object format) {
        date = mysqlTimeTypeLiteralExp(date);
        FuncExpUtils.assertTextExp(format);
        return LiteralFunctions.twoArgFunc("DATE_FORMAT", date, format, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg : {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayofmonth">DAYOFMONTH(date)</a>
     */
    public static SimpleExpression dayOfMonth(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("DAYOFMONTH", date, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     * <pre>
     *     <code><br/>
     *    &#64;Test
     *    public void dayNameFunc(final ReactiveLocalSession session){
     *        final LocalDate today =LocalDate.now();
     *        final DayOfWeek week = DayOfWeek.from(today);
     *
     *        final Select stmt;
     *        stmt = MySQLs.query()
     *                .select(dayName(today).as("dayName"))
     *                .asQuery();
     *
     *        final DayOfWeek row;
     *        row =  session.queryOne(stmt, DayOfWeek.class) // army can find compatible {@link MappingType} by {@link MappingType#compatibleFor(DataType, Class)} method.
     *                .block();
     *
     *        Assert.assertEquals(row,week);
     *
     *    }
     *     </code>
     * </pre>
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg : {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayname">DAYNAME(date)</a>
     */
    public static SimpleExpression dayName(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("DAYNAME", date, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     * <pre>
     *     <code><br/>
     *    &#64;Test
     *    public void dayOfWeekFunc(final ReactiveLocalSession session){
     *        final LocalDate today =LocalDate.now();
     *        final DayOfWeek week = DayOfWeek.from(today);
     *
     *        final Select stmt;
     *        stmt = MySQLs.query()
     *                .select(dayOfWeek(today).as("dayCode"))
     *                .asQuery();
     *
     *        final DayOfWeek row;
     *        row =  session.queryOne(stmt, DayOfWeek.class) // army can find compatible {@link MappingType} by {@link MappingType#compatibleFor(DataType, Class)} method.
     *                .block();
     *
     *        Assert.assertEquals(row,week);
     *
     *    }
     *     </code>
     * </pre>
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg : {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see #weekDay(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayofweek">DAYOFYEAR(date)</a>
     */
    public static SimpleExpression dayOfWeek(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("DAYOFWEEK", date, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg : {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_dayofyear">DAYOFYEAR(date)</a>
     */
    public static SimpleExpression dayOfYear(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("DAYOFYEAR", date, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link LongType}
     *
     * @param unit non-null
     * @param from see {@link SQLs#FROM}
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg : {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_extract">EXTRACT(date)</a>
     */
    public static SimpleExpression extract(final MySQLTimeUnit unit, final SQLs.WordFrom from, Object date) {
        FuncExpUtils.assertWord(from, SQLs.FROM);
        ContextStack.assertNonNull(unit);

        date = mysqlTimeTypeLiteralExp(date);

        return LiteralFunctions.compositeFunc("EXTRACT", Arrays.asList(unit, from, date), LongType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link LocalDateType}
     *
     * @param n non-null, one of following :
     *          <ul>
     *              <li>{@link Expression} instance</li>
     *              <li>{@link Integer} literal</li>
     *          </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_from-days">FROM_DAYS(date)</a>
     */
    public static SimpleExpression fromDays(Object n) {
        FuncExpUtils.assertIntExp(n);
        return LiteralFunctions.oneArgFunc("FROM_DAYS", n, LocalDateType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalDateTimeType}
     *
     * @param unixTimestamp non-null, one of following :
     *                      <ul>
     *                          <li>{@link Expression} instance</li>
     *                          <li>{@link LocalDateTime} instance</li>
     *                          <li>{@link OffsetDateTime} instance</li>
     *                          <li>{@link ZonedDateTime} instance</li>
     *                          <li>{@link Integer} literal</li>
     *                          <li>{@link Long} literal</li>
     *                          <li>{@link Float} literal</li>
     *                          <li>{@link Double} literal</li>
     *                          <li>{@link java.math.BigDecimal} literal</li>
     *                          <li>{@link String} literal,eg :  {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *                      </ul>
     * @throws CriteriaException throw when argument error
     * @see #fromUnixTime(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_from-unixtime">FROM_UNIXTIME(unix_timestamp[,format])</a>
     */
    public static SimpleExpression fromUnixTime(Object unixTimestamp) {
        if (unixTimestamp instanceof String && ((String) unixTimestamp).indexOf(':') > 0) {
            unixTimestamp = mysqlTimeTypeLiteralExp(unixTimestamp);
        }
        return LiteralFunctions.oneArgFunc("FROM_UNIXTIME", unixTimestamp, LocalDateTimeType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     * @param unixTimestamp non-null, one of following :
     *                      <ul>
     *                          <li>{@link Expression} instance</li>
     *                          <li>{@link LocalDateTime} instance</li>
     *                          <li>{@link OffsetDateTime} instance</li>
     *                          <li>{@link ZonedDateTime} instance</li>
     *                          <li>{@link Integer} literal</li>
     *                          <li>{@link Long} literal</li>
     *                          <li>{@link Float} literal</li>
     *                          <li>{@link Double} literal</li>
     *                          <li>{@link java.math.BigDecimal} literal</li>
     *                          <li>{@link String} literal,eg :  {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *                      </ul>
     * @param format        non-null, one of following :
     *                      <ul>
     *                          <li>{@link Expression} instance</li>
     *                          <li>{@link String} literal </li>
     *                      </ul>
     * @throws CriteriaException throw when argument error
     * @see #fromUnixTime(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_from-unixtime">FROM_UNIXTIME(unix_timestamp[,format])</a>
     */
    public static SimpleExpression fromUnixTime(Object unixTimestamp, final Object format) {
        if (unixTimestamp instanceof String && ((String) unixTimestamp).indexOf(':') > 0) {
            unixTimestamp = mysqlTimeTypeLiteralExp(unixTimestamp);
        }
        FuncExpUtils.assertTextExp(format);
        return LiteralFunctions.twoArgFunc("FROM_UNIXTIME", unixTimestamp, format, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param type   non-null,should be below:
     *               <ul>
     *                      <li>{@link MySQLType#TIME}</li>
     *                      <li>{@link MySQLType#DATE}</li>
     *                      <li>{@link MySQLType#DATETIME}</li>
     *               </ul>
     * @param format non-null, one of following :
     *               <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link String} literal </li>
     *               </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_get-format">GET_FORMAT({DATE|TIME|DATETIME}, {'EUR'|'USA'|'JIS'|'ISO'|'INTERNAL'})</a>
     */
    public static SimpleExpression getFormat(final MySQLType type, final Object format) {
        final String name = "GET_FORMAT";
        switch (type) {
            case TIME:
            case DATE:
            case DATETIME:
                break;
            default:
                throw CriteriaUtils.funcArgError(name, type);
        }
        FuncExpUtils.assertTextExp(format);
        return LiteralFunctions.compositeFunc(name, Arrays.asList(type, SQLs.COMMA, format), StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param time non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalTime} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg : {@code "23:59:59"} , {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_hour">HOUR(time)</a>
     */
    public static SimpleExpression hour(Object time) {
        time = mysqlTimeTypeLiteralExp(time);
        return LiteralFunctions.oneArgFunc("HOUR", time, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalDateType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_last-day">LAST_DAY(date)</a>
     */
    public static SimpleExpression lastDay(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("LAST_DAY", date, LocalDateType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     *
     * @see #now(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_now">NOW([fsp])</a>
     */
    public static SimpleExpression now() {
        return LiteralFunctions.zeroArgFunc("NOW", LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalDateTimeType}
     *
     * @param fsp non-null, one of following :
     *            <ul>
     *                <li>{@link Expression} instance</li>
     *                <li>{@link Integer} literal </li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see #now()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_now">NOW([fsp])</a>
     */
    public static SimpleExpression now(Object fsp) {
        FuncExpUtils.assertIntExp(fsp);
        return LiteralFunctions.oneArgFunc("NOW", fsp, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalDateTimeType}
     *
     * @see #sysDate(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_sysdate">SYSDATE([fsp])</a>
     */
    public static SimpleExpression sysDate() {
        return LiteralFunctions.zeroArgFunc("SYSDATE", LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalDateTimeType}
     *
     * @param fsp non-null, one of following :
     *            <ul>
     *                <li>{@link Expression} instance</li>
     *                <li>{@link Integer} literal </li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see #sysDate()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_sysdate">SYSDATE([fsp])</a>
     */
    public static SimpleExpression sysDate(final Object fsp) {
        FuncExpUtils.assertIntExp(fsp);
        return LiteralFunctions.oneArgFunc("SYSDATE", fsp, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalDateTimeType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see #timestamp(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timestamp">TIMESTAMP(expr)</a>
     */
    public static SimpleExpression timestamp(Object expr) {
        expr = mysqlTimeTypeLiteralExp(expr);
        if (expr instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("TIMESTAMP", expr, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LocalDateTimeType}
     *
     * @param expr1 non-null, one of following :
     *              <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link java.time.LocalDateTime} instance</li>
     *                  <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                  <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                  <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *              </ul>
     * @param expr2 non-null, one of following :
     *              <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link java.time.LocalTime} instance</li>
     *                  <li>{@link String} literal,eg :  {@code "23:59:59"}</li>
     *              </ul>
     * @throws CriteriaException throw when argument error
     * @see #timestamp(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timestamp">TIMESTAMP(expr1,expr2)</a>
     */
    public static SimpleExpression timestamp(Object expr1, Object expr2) {
        expr1 = mysqlTimeTypeLiteralExp(expr1);
        if (expr1 instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        expr2 = mysqlTimeTypeLiteralExp(expr2);
        if (!(expr2 instanceof Expression || expr2 instanceof LocalTime)) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.twoArgFunc("TIMESTAMP", expr1, expr2, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If datetimeExpr is date expression and unit isn't time part , then return {@link LocalDateType}</li>
     *     <li>Else {@link LocalDateTimeType}</li>
     * </ul>
     *
     * @param unit         non-null
     * @param interval     non-null, one of following :
     *                     <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link Integer} literal</li>
     *                     </ul>
     * @param datetimeExpr non-null, one of following :
     *                     <ul>
     *                         <li>{@link Expression} instance</li>
     *                         <li>{@link java.time.LocalDate} instance</li>
     *                         <li>{@link java.time.LocalDateTime} instance</li>
     *                         <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                         <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                         <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *                     </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timestampadd">TIMESTAMPADD(unit,interval,datetime_expr)</a>
     */
    public static SimpleExpression timestampAdd(final MySQLTimeUnit unit, final Object interval, Object datetimeExpr) {
        ContextStack.assertNonNull(unit);
        FuncExpUtils.assertIntExp(interval);

        datetimeExpr = mysqlTimeTypeLiteralExp(datetimeExpr);
        if (datetimeExpr instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }

        TypeMeta returnType;
        if (datetimeExpr instanceof LocalDate) {
            if (unit.isTimePart()) {
                returnType = LocalDateTimeType.INSTANCE;
            } else {
                returnType = LocalDateType.INSTANCE;
            }
        } else if (!(datetimeExpr instanceof Expression)) {
            returnType = LocalDateTimeType.INSTANCE;
        } else {
            returnType = ((Expression) datetimeExpr).typeMeta();
            if (!(returnType instanceof MappingType)) {
                returnType = returnType.mappingType();
            }

            if (!(returnType instanceof MappingType.SqlLocalDateType)) {
                returnType = LocalDateTimeType.INSTANCE;
            } else if (unit.isTimePart()) {
                returnType = LocalDateTimeType.INSTANCE;
            } else {
                returnType = LocalDateType.INSTANCE;
            }
        }

        return LiteralFunctions.compositeFunc("TIMESTAMPADD",
                Arrays.asList(unit, SQLs.COMMA, interval, SQLs.COMMA, datetimeExpr),
                returnType);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link LongType}
     *
     * @param unit          non-null
     * @param datetimeExpr1 non-null, one of following :
     *                      <ul>
     *                          <li>{@link Expression} instance</li>
     *                          <li>{@link java.time.LocalDate} instance</li>
     *                          <li>{@link java.time.LocalDateTime} instance</li>
     *                          <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                          <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                          <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *                      </ul>
     * @param datetimeExpr2 non-null, one of following :
     *                      <ul>
     *                          <li>{@link Expression} instance</li>
     *                          <li>{@link java.time.LocalDate} instance</li>
     *                          <li>{@link java.time.LocalDateTime} instance</li>
     *                          <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                          <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                          <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *                      </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timestampdiff">TIMESTAMPDIFF(unit,datetime_expr1,datetime_expr2)</a>
     */
    public static SimpleExpression timestampDiff(final MySQLTimeUnit unit, Object datetimeExpr1, Object datetimeExpr2) {
        ContextStack.assertNonNull(unit);
        datetimeExpr1 = mysqlTimeTypeLiteralExp(datetimeExpr1);
        datetimeExpr2 = mysqlTimeTypeLiteralExp(datetimeExpr2);

        if (datetimeExpr1 instanceof LocalTime || datetimeExpr2 instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.compositeFunc("TIMESTAMPDIFF",
                Arrays.asList(unit, SQLs.COMMA, datetimeExpr1, SQLs.COMMA, datetimeExpr2),
                LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param time   non-null, one of following :
     *               <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link java.time.LocalTime} instance</li>
     *                   <li>{@link String} literal,eg :  {@code "23:59:59"} </li>
     *               </ul>
     * @param format non-null, one of following :
     *               <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link String} literal</li>
     *               </ul>
     * @throws CriteriaException throw when argument error
     * @see #dateFormat(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_time-format">TIME_FORMAT(time,format)</a>
     */
    public static SimpleExpression timeFormat(Object time, final Object format) {
        time = FuncExpUtils.localTimeLiteralExp(time);
        FuncExpUtils.assertTextExp(format);
        return LiteralFunctions.twoArgFunc("TIME_FORMAT", time, format, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"}</li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_to-days">TO_DAYS(date)</a>
     */
    public static SimpleExpression toDays(Object date) {
        date = FuncExpUtils.localDateLiteralExp(date);
        return LiteralFunctions.oneArgFunc("TO_DAYS", date, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LongType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_to-seconds">TO_SECONDS(expr)</a>
     */
    public static SimpleExpression toSeconds(Object expr) {
        expr = mysqlTimeTypeLiteralExp(expr);
        if (expr instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("TO_SECONDS", expr, LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LongType}
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_unix-timestamp">UNIX_TIMESTAMP()</a>
     */
    public static SimpleExpression unixTimestamp() {
        return LiteralFunctions.zeroArgFunc("UNIX_TIMESTAMP", LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LongType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_unix-timestamp">UNIX_TIMESTAMP(date)</a>
     */
    public static SimpleExpression unixTimestamp(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("UNIX_TIMESTAMP", date, LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalDateType}
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-date">UTC_DATE()</a>
     */
    public static SimpleExpression utcDate() {
        return LiteralFunctions.zeroArgFunc("UTC_DATE", LocalDateType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalTimeType}
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-time">UTC_TIME()</a>
     */
    public static SimpleExpression utcTime() {
        return LiteralFunctions.zeroArgFunc("UTC_DATE", LocalTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalTimeType}
     *
     * @param fsp non-null, one of following :
     *            <ul>
     *                <li>{@link Expression} instance</li>
     *                <li>{@link Integer} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-time">UTC_TIME(fsp)</a>
     */
    public static SimpleExpression utcTime(final Object fsp) {
        FuncExpUtils.assertIntExp(fsp);
        return LiteralFunctions.oneArgFunc("UTC_TIME", fsp, LocalTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalDateTimeType}
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-timestamp">UTC_TIMESTAMP()</a>
     */
    public static SimpleExpression utcTimestamp() {
        return LiteralFunctions.zeroArgFunc("UTC_TIMESTAMP", LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalDateTimeType}
     *
     * @param fsp non-null, one of following :
     *            <ul>
     *                <li>{@link Expression} instance</li>
     *                <li>{@link Integer} literal</li>
     *            </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_utc-timestamp">UTC_TIMESTAMP(fsp)</a>
     */
    public static SimpleExpression utcTimestamp(final Object fsp) {
        FuncExpUtils.assertIntExp(fsp);
        return LiteralFunctions.oneArgFunc("UTC_TIMESTAMP", fsp, LocalDateTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_week">WEEK(date)</a>
     */
    public static SimpleExpression week(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("WEEK", date, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @param mode non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link Integer} literal</li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_week">WEEK(date)</a>
     */
    public static SimpleExpression week(Object date, final Object mode) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        FuncExpUtils.assertIntExp(mode);
        return LiteralFunctions.twoArgFunc("WEEK", date, mode, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     * <pre>
     *     <code><br/>
     *    &#64;Test
     *    public void weekDayFunc(final ReactiveLocalSession session){
     *        final LocalDate today =LocalDate.now();
     *        final DayOfWeek week = DayOfWeek.from(today);
     *
     *        final Select stmt;
     *        stmt = MySQLs.query()
     *                .select(weekDay(today).as("dayCode"))
     *                .asQuery();
     *
     *        final DayOfWeek row;
     *        row =  session.queryOne(stmt, DayOfWeek.class) // army can find compatible {@link MappingType} by {@link MappingType#compatibleFor(DataType, Class)} method.
     *                .block();
     *
     *        Assert.assertEquals(row,week);
     *
     *    }
     *     </code>
     * </pre>
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see #dayOfWeek(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_weekday">WEEKDAY(date)</a>
     */
    public static SimpleExpression weekDay(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("WEEKDAY", date, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_weekofyear">WEEKOFYEAR(date)</a>
     */
    public static SimpleExpression weekOfYear(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("WEEKOFYEAR", date, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link YearType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_year">YEAR(date)</a>
     */
    public static SimpleExpression year(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("YEAR", date, YearType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_yearweek">YEARWEEK(date)</a>
     */
    public static SimpleExpression yearWeek(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("YEARWEEK", date, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @param mode non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link Integer} literal</li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_yearweek">YEARWEEK(date,mode)</a>
     */
    public static SimpleExpression yearWeek(Object date, final Object mode) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.twoArgFunc("YEARWEEK", date, mode, IntegerType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link LocalDateType}
     *
     * @param year      non-null, one of following :
     *                  <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link java.time.Year} instance</li>
     *                      <li>{@link Integer} literal</li>
     *                  </ul>
     * @param dayOfYear non-null, one of following :
     *                  <ul>
     *                      <li>{@link Expression} instance</li>
     *                      <li>{@link Integer} literal</li>
     *                  </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_makedate">MAKEDATE(year,dayofyear)</a>
     */
    public static SimpleExpression makeDate(Object year, final Object dayOfYear) {
        if (!(year instanceof Year)) {
            FuncExpUtils.assertIntExp(year);
        }
        FuncExpUtils.assertIntExp(dayOfYear);
        return LiteralFunctions.twoArgFunc("MAKEDATE", year, dayOfYear, LocalDateType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalTimeType}
     *
     * @param hour   non-null, one of following :
     *               <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link Integer} literal</li>
     *               </ul>
     * @param minute non-null, one of following :
     *               <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link Integer} literal</li>
     *               </ul>
     * @param second non-null, one of following :
     *               <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link Integer} literal</li>
     *               </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_maketime">MAKETIME(hour,minute,second)</a>
     */
    public static SimpleExpression makeTime(Object hour, Object minute, Object second) {
        FuncExpUtils.assertIntExp(hour);
        FuncExpUtils.assertIntExp(minute);
        FuncExpUtils.assertIntExp(second);
        return LiteralFunctions.threeArgFunc("MAKETIME", hour, minute, second, LocalTimeType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link LongType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalTime} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg : {@code "23:59:59"} , {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_microsecond">MICROSECOND(expr)</a>
     */
    public static SimpleExpression microSecond(Object expr) {
        expr = mysqlTimeTypeLiteralExp(expr);
        return LiteralFunctions.oneArgFunc("MICROSECOND", expr, LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param time non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalTime} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg : {@code "23:59:59"} , {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_minute">MINUTE(expr)</a>
     */
    public static SimpleExpression minute(Object time) {
        time = mysqlTimeTypeLiteralExp(time);
        return LiteralFunctions.oneArgFunc("MINUTE", time, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     * <pre>
     *     <code><br/>
     *    &#64;Test
     *    public void monthFunc(final ReactiveLocalSession session){
     *        final LocalDate today =LocalDate.now();
     *        final Month month = Month.from(today);
     *
     *        final Select stmt;
     *        stmt = MySQLs.query()
     *                .select(month(today).as("monthCode"))
     *                .asQuery();
     *
     *        final Month row;
     *        row =  session.queryOne(stmt, Month.class) // army can find compatible {@link MappingType} by {@link MappingType#compatibleFor(DataType, Class)} method.
     *                .block();
     *
     *        Assert.assertEquals(row,month);
     *
     *    }
     *     </code>
     * </pre>
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_month">MONTH(date)</a>
     */
    public static SimpleExpression month(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("MONTH", date, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     * <pre>
     *     <code><br/>
     *    &#64;Test
     *    public void monthNameFunc(final ReactiveLocalSession session){
     *        final LocalDate today =LocalDate.now();
     *        final Month month = Month.from(today);
     *
     *        final Select stmt;
     *        stmt = MySQLs.query()
     *                .select(monthName(today).as("monthName"))
     *                .asQuery();
     *
     *        final Month row;
     *        row =  session.queryOne(stmt, Month.class) // army can find compatible {@link MappingType} by {@link MappingType#compatibleFor(DataType, Class)} method.
     *                .block();
     *
     *        Assert.assertEquals(row,month);
     *
     *    }
     *     </code>
     * </pre>
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_monthname">MONTHNAME(date)</a>
     */
    public static SimpleExpression monthName(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("MONTHNAME", date, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param p non-null, one of following :
     *          <ul>
     *              <li>{@link Expression} instance</li>
     *              <li>{@link Integer} literal</li>
     *          </ul>
     * @param n non-null, one of following :
     *          <ul>
     *              <li>{@link Expression} instance</li>
     *              <li>{@link Integer} literal</li>
     *          </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_period-add">PERIOD_ADD(p,n)</a>
     */
    public static SimpleExpression periodAdd(final Object p, final Object n) {
        FuncExpUtils.assertIntExp(p);
        FuncExpUtils.assertIntExp(n);
        return LiteralFunctions.twoArgFunc("PERIOD_ADD", p, n, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param p1 non-null, one of following :
     *           <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>{@link Integer} literal</li>
     *           </ul>
     * @param p2 non-null, one of following :
     *           <ul>
     *               <li>{@link Expression} instance</li>
     *               <li>{@link Integer} literal</li>
     *           </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_period-diff">PERIOD_DIFF(P1,P2)</a>
     */
    public static SimpleExpression periodDiff(final Object p1, final Object p2) {
        FuncExpUtils.assertIntExp(p1);
        FuncExpUtils.assertIntExp(p2);
        return LiteralFunctions.twoArgFunc("PERIOD_DIFF", p1, p2, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param date non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalDate} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "2007-12-31"} , {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_quarter">QUARTER(date)</a>
     */
    public static SimpleExpression quarter(Object date) {
        date = mysqlTimeTypeLiteralExp(date);
        if (date instanceof LocalTime) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("QUARTER", date, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalTimeType}
     *
     * @param expr non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalTime} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "23:59:59"} ,  {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_time">TIME(expr)</a>
     */
    public static SimpleExpression time(Object expr) {
        expr = mysqlTimeTypeLiteralExp(expr);
        if (expr instanceof LocalDate) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("TIME", expr, LocalTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: the {@link DurationType} of expr1.
     *
     * @param expr1 non-null, one of following :
     *              <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link java.time.LocalTime} instance</li>
     *                  <li>{@link java.time.LocalDateTime} instance</li>
     *                  <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                  <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                  <li>{@link String} literal,eg :  {@code "23:59:59"} ,  {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *              </ul>
     * @param expr2 non-null, one of following :
     *              <ul>
     *                  <li>{@link Expression} instance</li>
     *                  <li>{@link java.time.LocalTime} instance</li>
     *                  <li>{@link java.time.LocalDateTime} instance</li>
     *                  <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                  <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                  <li>{@link String} literal,eg :  {@code "23:59:59"} ,  {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *              </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_timediff">TIMEDIFF(expr1,expr2)</a>
     */
    public static SimpleExpression timeDiff(Object expr1, Object expr2) {
        expr1 = mysqlTimeTypeLiteralExp(expr1);
        if (expr1 instanceof LocalDate) {
            throw CriteriaUtils.funcArgExpError();
        }
        expr2 = mysqlTimeTypeLiteralExp(expr2);
        if (expr2 instanceof LocalDate) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.twoArgFunc("TIMEDIFF", expr1, expr2, DurationType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link IntegerType}
     *
     * @param time non-null, one of following :
     *             <ul>
     *                 <li>{@link Expression} instance</li>
     *                 <li>{@link java.time.LocalTime} instance</li>
     *                 <li>{@link java.time.LocalDateTime} instance</li>
     *                 <li>{@link java.time.OffsetDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link java.time.ZonedDateTime} instance,as of MySQL 8.0.19</li>
     *                 <li>{@link String} literal,eg :  {@code "23:59:59"} ,  {@code "2007-12-31 23:59:59"}, {@code "2020-01-01 10:10:10+05:30"} (as of MySQL 8.0.19) </li>
     *             </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_second">SECOND(time)</a>
     */
    public static SimpleExpression second(Object time) {
        time = mysqlTimeTypeLiteralExp(time);
        if (time instanceof LocalDate) {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.oneArgFunc("SECOND", time, IntegerType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LocalTimeType}
     *
     * @param seconds non-null, one of following :
     *                <ul>
     *                    <li>{@link Expression} instance</li>
     *                    <li>{@link Integer} literal</li>
     *                </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_sec-to-time">SEC_TO_TIME(seconds)</a>
     */
    public static SimpleExpression secToTime(final Object seconds) {
        FuncExpUtils.assertIntExp(seconds);
        return LiteralFunctions.oneArgFunc("SEC_TO_TIME", seconds, LocalTimeType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link StringType}
     *
     * @param str    non-null, one of following :
     *               <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link String} literal</li>
     *               </ul>
     * @param format non-null, one of following :
     *               <ul>
     *                   <li>{@link Expression} instance</li>
     *                   <li>{@link String} literal</li>
     *               </ul>
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_str-to-date">STR_TO_DATE(str,format)</a>
     */
    public static SimpleExpression strToDate(final Object str, final Object format) {
        FuncExpUtils.assertTextExp(str);
        FuncExpUtils.assertTextExp(format);
        return LiteralFunctions.twoArgFunc("STR_TO_DATE", str, format, StringType.INSTANCE);
    }


    /*-------------------below private method-------------------*/


    /**
     * @see #addDate(Object, SQLs.WordInterval, Object, MySQLTimeUnit)
     * @see #subDate(Object, SQLs.WordInterval, Object, MySQLTimeUnit)
     */
    private static SimpleExpression _dateIntervalFunc(final String name, Object date, final SQLs.WordInterval interval,
                                                      final Object expr, final MySQLTimeUnit unit) {
        date = FuncExpUtils.localDateLiteralExp(date);

        FuncExpUtils.assertWord(interval, SQLs.INTERVAL);
        FuncExpUtils.assertIntExp(expr);

        final MappingType returnType;
        if (unit.isTimePart()) {
            returnType = LocalDateTimeType.INSTANCE;
        } else {
            returnType = LocalDateType.INSTANCE;
        }
        return LiteralFunctions.compositeFunc(name, Arrays.asList(date, SQLs.COMMA, interval, expr, unit), returnType);
    }


    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If date {@link MappingType} is {@link LocalDateType} and unit no time parts then {@link LocalDateType},otherwise {@link LocalDateTimeType}</li>
     *     <li>If date {@link MappingType} is {@link LocalTimeType} and unit no date parts then {@link LocalTimeType},otherwise {@link LocalDateTimeType}</li>
     *     <li>If date {@link MappingType} is {@link LocalDateTimeType} or {@link OffsetDateTimeType} or {@link ZonedDateTimeType} then {@link LocalDateTimeType}</li>
     *     <li>otherwise {@link StringType}</li>
     * </ul>
     *
     * @param name DATE_ADD or DATE_SUB
     * @param date nullable parameter or {@link Expression}
     * @param expr nullable parameter or {@link Expression}
     * @param unit non-null
     * @see #dateAdd(Object, SQLs.WordInterval, Object, MySQLTimeUnit)
     * @see #dateSub(Object, SQLs.WordInterval, Object, MySQLTimeUnit)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_date-add">DATE_ADD(date,INTERVAL expr unit), DATE_SUB(date,INTERVAL expr unit)</a>
     */
    private static SimpleExpression _dateAddOrSub(final String name, Object date, final SQLs.WordInterval interval,
                                                  final Object expr, final MySQLTimeUnit unit) {

        FuncExpUtils.assertWord(interval, SQLs.INTERVAL);
        FuncExpUtils.assertIntExp(expr);
        ContextStack.assertNonNull(unit);

        date = mysqlTimeTypeLiteralExp(date);

        final TypeMeta type, returnType;
        if (date instanceof Expression) {
            type = ((Expression) date).typeMeta();
        } else if (date instanceof LocalDate) {
            type = LocalDateType.INSTANCE;
        } else if (date instanceof LocalDateTime
                || date instanceof OffsetDateTime
                || date instanceof ZonedDateTime) {
            type = LocalDateTimeType.INSTANCE;
        } else {
            throw CriteriaUtils.funcArgExpError();
        }

        returnType = _dateAddSubReturnType(type.mappingType(), unit);
        return LiteralFunctions.compositeFunc(name, Arrays.asList(date, SQLs.COMMA, interval, expr, unit), returnType);
    }


    /**
     * @see #dateAdd(Object, SQLs.WordInterval, Object, MySQLTimeUnit)
     */
    private static MappingType _dateAddSubReturnType(final MappingType type, final MySQLTimeUnit unit) {
        final MappingType returnType;
        if (type instanceof MappingType.SqlLocalDateType) {
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
        } else if (type instanceof MappingType.SqlLocalTimeType || type instanceof MappingType.SqlOffsetTimeType) {
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
        } else if (type instanceof MappingType.SqlLocalDateTimeType
                || type instanceof MappingType.SqlOffsetDateTimeType) {
            returnType = LocalDateTimeType.INSTANCE;
        } else {
            returnType = StringType.INSTANCE;
        }
        return returnType;
    }


    /**
     * @see #timestampAdd(MySQLTimeUnit, Object, Object)
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
     * @see #strToDate(Object, Object)
     */
    private static MappingType _strToDateReturnType(final Expression formatExp, final MappingType type) {
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
     * @see #_strToDateReturnType(Expression, MappingType)
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
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>expr1 is {@link Expression} : return the {@link  MappingType} of expr1.</li>
     *     <li>expr1 is {@link LocalDateTime} : return {@link  LocalDateTimeType} </li>
     *     <li>expr1 is {@link LocalTime} : return {@link  LocalTimeType} </li>
     *     <li>expr1 is {@link OffsetDateTime} : return {@link  LocalDateTimeType} </li>
     *     <li>expr1 is {@link ZonedDateTime} : return {@link  LocalDateTimeType} </li>
     *     <li>expr1 is datetime literal : return {@link  LocalDateTimeType} </li>
     *     <li>expr1 is time literal : return {@link  LocalTimeType} </li>
     *     <li>expr1 is datetime with zone literal: return {@link  LocalDateTimeType} </li>
     * </ul>
     *
     * @see #addTime(Object, Object)
     * @see #subTime(Object, Object)
     */
    private static SimpleExpression _addOrSubTime(final String name, Object expr1, final Object expr2) {
        final TypeMeta returnType;
        expr1 = mysqlTimeTypeLiteralExp(expr1);
        if (expr1 instanceof Expression) {
            returnType = ((Expression) expr1).typeMeta();
        } else if (expr1 instanceof LocalTime) {
            returnType = LocalTimeType.INSTANCE;
        } else if (expr1 instanceof LocalDateTime
                || expr1 instanceof OffsetDateTime
                || expr1 instanceof ZonedDateTime) {
            returnType = LocalDateTimeType.INSTANCE;
        } else {
            throw CriteriaUtils.funcArgExpError();
        }
        return LiteralFunctions.twoArgFunc(name, expr1, FuncExpUtils.localTimeLiteralExp(expr2), returnType);
    }

    /**
     * @see #_addOrSubTime(String, Object, Object)
     * @see #date(Object)
     */
    private static Object mysqlTimeTypeLiteralExp(final Object expr) {
        final Object literalExp;
        if (expr instanceof Expression) {
            literalExp = expr;
        } else if (expr instanceof String) {
            final String str = (String) expr;
            try {
                final int length;
                final char ch;
                if (str.indexOf('-') < 0) {
                    literalExp = LocalTime.parse(str, _TimeUtils.TIME_FORMATTER_6);
                } else if ((length = str.length()) > 24 && ((ch = str.charAt(length - 6)) == '-' || ch == '+')) {
                    literalExp = OffsetDateTime.parse(str, _TimeUtils.OFFSET_DATETIME_FORMATTER_6);
                } else if (str.indexOf(':') < 0) {
                    literalExp = LocalDate.parse(str);
                } else {
                    literalExp = LocalDateTime.parse(str, _TimeUtils.DATETIME_FORMATTER_6);
                }
            } catch (DateTimeException e) {
                throw ContextStack.clearStackAndCause(e, "date/time error");
            }
        } else if (expr instanceof LocalDateTime
                || expr instanceof LocalTime
                || expr instanceof LocalDate
                || expr instanceof OffsetDateTime
                || expr instanceof ZonedDateTime) {
            literalExp = expr;
        } else {
            throw ContextStack.clearStackAndCriteriaError("date/time error");
        }
        return literalExp;
    }


}
