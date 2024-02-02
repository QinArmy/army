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

package io.army.session.reactive.mysql;


import com.alibaba.fastjson2.JSON;
import io.army.criteria.Select;
import io.army.criteria.TypeDef;
import io.army.criteria.impl.MySQLTimeUnit;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.reactive.ReactiveLocalSession;
import io.army.sqltype.MySQLType;
import io.army.util.RowMaps;
import io.army.util._TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.army.criteria.impl.MySQLs.format;
import static io.army.criteria.impl.MySQLs.*;
import static io.army.criteria.impl.SQLs.*;

@Test(dataProvider = "localSessionProvider")
public class FunctionTests extends SessionTestsSupport {

    private static final Logger LOG = LoggerFactory.getLogger(FunctionTests.class);


    /**
     * @see MySQLs#jsonTable(Object, Object, SQLs.WordColumns, Consumer)
     */
    @Test
    public void jsonTableStatic(final ReactiveLocalSession session) {
        final String jsonDocument;
        jsonDocument = "[{\"a\":\"3\"},{\"a\":2},{\"b\":1},{\"a\":0},{\"a\":[1,2]}]";

        final Select stmt;
        stmt = MySQLs.query()
                .select(s -> s.space("t", PERIOD, ASTERISK))
                .from(jsonTable(jsonDocument, "$[*]", COLUMNS, s -> s.space("rowId", FOR_ORDINALITY)
                                .comma("ac", MySQLType.VARCHAR.parens(100), PATH, "$.a", o -> o.spaceDefault("111").onEmpty().spaceDefault("999").onError())
                                .comma("aj", MySQLType.JSON, PATH, "$.a", o -> o.spaceDefault("{\"x\":333}").onEmpty())
                                .comma("bx", MySQLType.INT, EXISTS, PATH, "$.b")
                        )
                )
                .as("t")
                .asQuery();

        session.queryObject(stmt, RowMaps::hashMap)
                .doOnNext(row -> {
                    switch (((Number) row.get("rowId")).intValue()) {
                        case 1: {
                            Assert.assertEquals(row.get("ac"), "3");
                            Assert.assertEquals(row.get("aj"), "\"3\"");
                            Assert.assertEquals(row.get("bx"), 0);
                        }
                        break;
                        case 2: {
                            Assert.assertEquals(row.get("ac"), "2");
                            Assert.assertEquals(row.get("aj"), "2");
                            Assert.assertEquals(row.get("bx"), 0);
                        }
                        break;
                        case 3: {
                            Assert.assertEquals(row.get("ac"), "111");
                            final Map<?, ?> map;
                            map = JSON.parseObject((String) row.get("aj"), Map.class);
                            Assert.assertEquals(map, Collections.singletonMap("x", 333));
                            Assert.assertEquals(row.get("bx"), 1);
                        }
                        break;
                        case 4: {
                            Assert.assertEquals(row.get("ac"), "0");
                            Assert.assertEquals(row.get("aj"), "0");
                            Assert.assertEquals(row.get("bx"), 0);
                        }
                        break;
                        case 5: {
                            Assert.assertEquals(row.get("ac"), "999");
                            final List<Integer> list;
                            list = JSON.parseArray((String) row.get("aj"), Integer.class);
                            Assert.assertEquals(list, Arrays.asList(1, 2));
                            Assert.assertEquals(row.get("bx"), 0);
                        }
                        break;
                        default:
                            throw new IllegalStateException("unknown row");
                    }
                })
                .blockLast();

    }

    /**
     * @see MySQLs#exportSet(Object, Object, Object, Object, Object)
     */
    @Test//(invocationCount = 10)
    public void exportSetFunc(final ReactiveLocalSession session) {
        final Select stmt;
        stmt = MySQLs.query()
                .select(exportSet(6, "1", "0", ",", 10).as("bitStr"))
                .asQuery();

        final String row;
        row = session.queryOne(stmt, String.class)
                .block();

        Assert.assertEquals(row, "0,1,1,0,0,0,0,0,0,0");

    }

    /**
     * @see MySQLs#format(Object, Object, Object)
     */
    @Test
    public void formatFunc(final ReactiveLocalSession session) {
        final Select stmt;
        stmt = MySQLs.query()
                .select(format(12332.2, 2, "de_DE").as("numStr"))
                .asQuery();

        final String row;
        row = session.queryOne(stmt, String.class)
                .block();

        Assert.assertEquals(row, "12.332,20");

    }

    /**
     * @see MySQLs#fromBase64(Object)
     */
    @Test
    public void fromBase64Func(final ReactiveLocalSession session) {
        final String source = "QinArmy's army,I love army. 秦军的 army";
        final Select stmt;
        stmt = MySQLs.query()
                .select(fromBase64(toBase64(source)).as("source"))
                .asQuery();

        final String row;
        row = session.queryOne(stmt, String.class)
                .block();

        Assert.assertEquals(row, source);

    }


    /**
     * @see MySQLs#toBase64(Object)
     */
    @Test
    public void hexFunc(final ReactiveLocalSession session) {
        final String source = "QinArmy's army,I love army. 秦军的 army";
        final byte[] sourceBytes = source.getBytes(StandardCharsets.UTF_8);

        final Select stmt;
        stmt = MySQLs.query()
                .select(unhex(hex(source)).as("source"))
                .comma(unhex(hex(sourceBytes)).as("sourceBytes"))
                .asQuery();

        final Map<String, Object> row;
        row = session.queryOneObject(stmt, RowMaps::hashMap)
                .block();

        Assert.assertNotNull(row);

        Assert.assertEquals(row.get("source"), sourceBytes);
        Assert.assertEquals(row.get("sourceBytes"), sourceBytes);

    }

    /**
     * @see MySQLs#position(Object, SQLs.WordIn, Object)
     */
    @Test
    public void positionFunc(final ReactiveLocalSession session) {
        final String source = "QinArmy's army,I love army. 秦军的 army";
        final String subStr = "秦军的 army";

        final Select stmt;
        stmt = MySQLs.query()
                .select(position(subStr, IN, source).as("source"))
                .asQuery();

        final Integer row;
        row = session.queryOne(stmt, Integer.class)
                .block();

        Assert.assertNotNull(row);
        Assert.assertEquals(row.intValue(), source.indexOf(subStr) + 1);

    }

    /**
     * @see MySQLs#weightString(Object, WordAs, TypeDef, Object)
     */
    @Test
    public void weightStringFunc(final ReactiveLocalSession session) {
        final String source = "QinArmy's army,I love army. 秦军的 army";

        final Select stmt;
        stmt = MySQLs.query()
                .select(hex(weightString(source, AS, MySQLType.CHAR.parens(40))).as("source"))
                .asQuery();

        final String row;
        row = session.queryOne(stmt, String.class)
                .block();

        Assert.assertNotNull(row);
        LOG.debug("weightStringFunc result :  {}", row);
    }


    /**
     * @see MySQLs#addDate(Object, WordInterval, Object, MySQLTimeUnit)
     */
    @Test
    public void addDateFunc(final ReactiveLocalSession session) {
        final Select stmt;
        stmt = MySQLs.query()
                .select(addDate("2008-01-02", INTERVAL, 31, MySQLTimeUnit.DAY).as("date"))
                .asQuery();

        final LocalDate row;
        row = session.queryOne(stmt, LocalDate.class)
                .block();

        Assert.assertEquals(row, LocalDate.of(2008, 2, 2));
    }

    /**
     * @see MySQLs#addTime(Object, Object)
     */
    @Test
    public void addTimeFunc(final ReactiveLocalSession session) {

        final Select stmt;
        stmt = MySQLs.query()
                .select(addTime("2007-12-31 00:59:59.999999", "00:01:01.000002").as("exp1"))
                .comma(addTime("00:59:59.999999", "00:01:01.000002").as("exp2"))
                .comma(addTime("2007-12-31 00:59:59.999999+10:00", "00:01:01.000002").as("exp3"))
                .comma(addTime("2007-12-31 00:59:59.999999-10:00", "00:01:01.000002").as("exp4"))
                .comma(addTime(LocalDateTime.parse("2007-12-31 00:59:59.999999", _TimeUtils.DATETIME_FORMATTER_6), "00:01:01.000002").as("exp5"))
                .comma(addTime(LocalTime.parse("00:59:59.999999", _TimeUtils.TIME_FORMATTER_6), LocalTime.of(0, 0, 1)).as("exp6"))
                .comma(addTime(OffsetDateTime.parse("2007-12-31 00:59:59.999999+10:00", _TimeUtils.OFFSET_DATETIME_FORMATTER_6), LocalTime.of(0, 0, 1)).as("exp7"))
                .comma(addTime(ZonedDateTime.parse("2007-12-31 00:59:59.999999-10:00", _TimeUtils.OFFSET_DATETIME_FORMATTER_6), LocalTime.of(0, 0, 1)).as("exp8"))
                .asQuery();

        session.queryObject(stmt, RowMaps::hashMap)
                .blockLast();

    }

    @Test
    public void convertTzFunc(final ReactiveLocalSession session) {

        final Select stmt;
        stmt = MySQLs.query()
                .select(convertTz("2004-01-01 12:00:00", "GMT", "MET").as("exp1"))
                .comma(convertTz("2004-01-01 12:00:00", "+00:00", "+10:00").as("exp2"))
                .comma(convertTz("2004-01-01 12:00:00+10:00", "+00:00", "+10:00").as("exp3"))
                .comma(convertTz("2004-01-01 12:00:00-10:00", "+00:00", "+10:00").as("exp4"))
                .comma(convertTz(LocalDateTime.now(), "+00:00", "+10:00").as("exp5"))
                .comma(convertTz(OffsetDateTime.now(), "+00:00", "+10:00").as("exp6"))
                .comma(convertTz(ZonedDateTime.now(), "+00:00", "+10:00").as("exp7"))
                .asQuery();

        session.queryObject(stmt, RowMaps::hashMap)
                .blockLast();
    }

    /**
     * @see MySQLs#CURRENT_DATE
     * @see MySQLs#currentDate()
     */
    @Test
    public void currentDateFunc(final ReactiveLocalSession session) {

        final Select stmt;
        stmt = MySQLs.query()
                .select(CURRENT_DATE.as("exp1"))
                .comma(currentDate().as("exp2"))

                .comma(CURRENT_TIME.as("exp3"))
                .comma(currentTime().as("exp4"))

                .comma(CURRENT_TIMESTAMP.as("exp5"))
                .comma(currentTimestamp().as("exp6"))

                .asQuery();

        session.queryObject(stmt, RowMaps::hashMap)
                .blockLast();

    }

    /**
     * @see MySQLs#dateAdd(Object, WordInterval, Object, MySQLTimeUnit)
     * @see MySQLs#dateSub(Object, WordInterval, Object, MySQLTimeUnit)
     */
    @Test
    public void dateAddFunc(final ReactiveLocalSession session) {

        final Select stmt;
        stmt = MySQLs.query()
                .select(dateAdd("2018-05-01", INTERVAL, 1, MySQLTimeUnit.DAY).as("exp1"))
                .comma(dateAdd(LocalDate.now(), INTERVAL, 1, MySQLTimeUnit.DAY).as("exp2"))

                .comma(dateAdd("2020-12-31 23:59:59", INTERVAL, 1, MySQLTimeUnit.DAY).as("exp3"))
                .comma(dateAdd(LocalDateTime.now(), INTERVAL, 1, MySQLTimeUnit.DAY).as("exp4"))

                .comma(dateSub("2020-12-31 23:59:59+10:00", INTERVAL, 1, MySQLTimeUnit.DAY).as("exp5"))
                .comma(dateSub(OffsetDateTime.now(), INTERVAL, 1, MySQLTimeUnit.DAY).as("exp6"))

                .asQuery();

        session.queryObject(stmt, RowMaps::hashMap)
                .blockLast();

    }

    /**
     * @see MySQLs#dayName(Object)
     */
    @Test
    public void dayNameFunc(final ReactiveLocalSession session) {
        final LocalDate today = LocalDate.now();
        final DayOfWeek week = DayOfWeek.from(today);

        final Select stmt;
        stmt = MySQLs.query()
                .select(dayName(today).as("dayName"))
                .asQuery();

        final DayOfWeek row;
        row = session.queryOne(stmt, DayOfWeek.class)
                .block();

        Assert.assertEquals(row, week);

    }

    /**
     * @see MySQLs#dayOfWeek(Object)
     */
    @Test
    public void dayOfWeekFunc(final ReactiveLocalSession session) {
        final LocalDate today = LocalDate.now();
        final DayOfWeek week = DayOfWeek.from(today);

        final Select stmt;
        stmt = MySQLs.query()
                .select(dayOfWeek(today).as("dayCode"))
                .asQuery();

        final DayOfWeek row;
        row = session.queryOne(stmt, DayOfWeek.class)
                .block();

        Assert.assertEquals(row, week);

    }

    /**
     * @see MySQLs#timestampAdd(MySQLTimeUnit, Object, Object)
     */
    @Test
    public void timestampAddFunc(final ReactiveLocalSession session) {

        final Select stmt;
        stmt = MySQLs.query()
                .select(timestampAdd(MySQLTimeUnit.MINUTE, 1, "2003-01-02").as("dateMinute"))
                .comma(timestampAdd(MySQLTimeUnit.WEEK, 1, "2003-01-02").as("dateWeek"))
                .asQuery();

        final Map<String, Object> row;
        row = session.queryOneObject(stmt, RowMaps::hashMap)
                .block();

        Assert.assertNotNull(row);
        Assert.assertEquals(row.get("dateMinute"), LocalDateTime.parse("2003-01-02 00:01:00", _TimeUtils.DATETIME_FORMATTER_6));
        Assert.assertEquals(row.get("dateWeek"), LocalDate.parse("2003-01-09"));


    }


    /**
     * @see MySQLs#dayName(Object)
     */
    @Test
    public void monthNameFunc(final ReactiveLocalSession session) {
        final LocalDate today = LocalDate.now();
        final Month month = Month.from(today);

        final Select stmt;
        stmt = MySQLs.query()
                .select(monthName(today).as("monthName"))
                .asQuery();

        final Month row;
        row = session.queryOne(stmt, Month.class)
                .block();

        Assert.assertEquals(row, month);

    }

    /**
     * @see MySQLs#periodAdd(Object, Object)
     */
    @Test
    public void periodAddFunc(final ReactiveLocalSession session) {

        final Select stmt;
        stmt = MySQLs.query()
                .select(periodAdd(200801, 2).as("yearMonth"))
                .asQuery();

        final YearMonth row;
        row = session.queryOne(stmt, YearMonth.class)
                .block();

        Assert.assertEquals(row, YearMonth.of(2008, 3));

    }


}
