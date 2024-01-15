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

package io.army.session.sync.mysql;

import com.alibaba.fastjson2.JSON;
import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.mysql.MySQLCastType;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sqltype.MySQLType;
import io.army.sync.SyncLocalSession;
import io.army.util.RowMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.army.criteria.impl.MySQLs.*;
import static io.army.criteria.impl.SQLs.*;

@Test(dataProvider = "localSessionProvider")
public class MySQLSyncFunctionTests extends MySQLSynSessionTestSupport {


    private static final Logger LOG = LoggerFactory.getLogger(MySQLSyncFunctionTests.class);


    /**
     * @see MySQLs#jsonTable(Object, Object, SQLs.WordColumns, Consumer)
     */
    @Test
    public void jsonTableFuncStatic(final SyncLocalSession session) {
        final String jsonDocument;
        jsonDocument = "[{\"a\":\"3\"},{\"a\":2},{\"b\":1},{\"a\":0},{\"a\":[1,2]}]";

        final Select stmt;
        stmt = MySQLs.query()
                .select(s -> s.space("t", PERIOD, ASTERISK))
                .from(jsonTable(jsonDocument, "$[*]", COLUMNS, s -> s.space("rowId", FOR_ORDINALITY)
                                .comma("ac", MySQLType.VARCHAR.parens(100).characterSet("utf8mb4").collate("utf8mb4_unicode_ci"),
                                        PATH, "$.a", o -> o.spaceDefault("111").onEmpty().spaceDefault("999").onError())
                                .comma("aj", MySQLType.JSON, PATH, "$.a", o -> o.spaceDefault("{\"x\":333}").onEmpty())
                                .comma("bx", MySQLType.INT, EXISTS, PATH, "$.b")
                        )
                )
                .as("t")
                .asQuery();


        session.queryObject(stmt, RowMaps::hashMap)
                .forEach(row -> {
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
                });

    }

    /**
     * @see MySQLs#jsonTable(Object, Object, SQLs.WordColumns, Consumer)
     */
    @Test
    public void jsonTableFuncNested(final SyncLocalSession session) {
        final String jsonDocument;
        jsonDocument = "[{\"a\": 1, \"b\": [11,111]}, {\"a\": 2, \"b\": [22,222]}]";

        final Select stmt;
        stmt = MySQLs.query()
                .select(s -> s.space("t", PERIOD, ASTERISK))
                .from(jsonTable(jsonDocument, "$[*]", COLUMNS, s -> s.space("rowId", FOR_ORDINALITY)
                                .comma("a", MySQLType.INT, PATH, "$.a")
                                .comma(NESTED, PATH, "$.b[*]", COLUMNS, ns -> ns.space("b1", MySQLType.INT, PATH, "$"))
                                .comma(NESTED, PATH, "$.b[*]", COLUMNS, ns -> ns.space("b2", MySQLType.INT, PATH, "$"))
                        )
                )
                .as("t")
                .asQuery();

        session.queryObject(stmt, RowMaps::hashMap)
                .forEach(row -> LOG.debug(JSON.toJSONString(row)));


    }

    /**
     * @see MySQLs#jsonValue(Object, Object, Consumer)
     */
    @Test
    public void jsonValueFunc(final SyncLocalSession session) {
        final String jsonDoc = "{\"fname\": \"Joe\", \"lname\": \"Palmer\",\"date\":\"2023-12-18\"}";

        final Select stmt;
        stmt = MySQLs.query()
                .select(jsonValue(jsonDoc, "$.fname", s -> s.returning(MySQLCastType.CHAR, 3, SQLs.CHARACTER_SET, "utf8mb4")).as("name"))
                .comma(jsonValue(jsonDoc, "$.date", s -> s.returning(MySQLCastType.DATE).spaceNull().onEmpty()).as("date"))
                .asQuery();

        final Map<String, Object> row;
        row = session.queryOneObject(stmt, RowMaps::hashMap);

        Assert.assertNotNull(row);
        Assert.assertEquals(row.get("name"), "Joe");
        Assert.assertEquals(row.get("date"), LocalDate.parse("2023-12-18"));
    }

    /**
     * @see MySQLs#jsonSearch(Object, Object, Object, Object, Object)
     * @see MySQLs#jsonSearch(Object, Object, Object, Object, Object, Object, Object...)
     */
    @Test
    public void jsonSearchFunc(final SyncLocalSession session) {
        final String jsonDoc = "[\"abc\", [{\"k\": \"10\"}, \"def\"], {\"x\":\"abc\"}, {\"y\":\"bcd\"}]";

        final Select stmt;
        stmt = MySQLs.query()
                .select(jsonSearch(jsonDoc, "one", "abc", null, "$[0]").as("simple"))
                .comma(jsonSearch(jsonDoc, "one", "abc", null, "$[0]", "$[1]").as("variadic"))
                .comma(jsonSearch(jsonDoc, "all", "abc", null, "$[0]", "$[2].x").as("variadic2"))
                .asQuery();

        final Map<String, Object> row;
        row = session.queryOneObject(stmt, RowMaps::hashMap);

        Assert.assertNotNull(row);

        Assert.assertEquals(row.get("simple"), "\"$[0]\"");
        Assert.assertEquals(row.get("variadic"), "\"$[0]\"");
        Assert.assertEquals(JSON.parseArray((String) row.getOrDefault("variadic2", "[]"), String.class), Arrays.asList("$[0]", "$[2].x"));
    }

    /**
     * @see MySQLs#elt(Object, Object, Object, Object, Object...)
     * @see MySQLs#elt(Object, Consumer)
     */
    @Test
    public void eltFunc(final SyncLocalSession session) {
        final Select stmt;
        stmt = MySQLs.query()
                .select(elt(1, "Aa", "Bb", "Cc", "Dd").as("str"))
                .comma(elt(2, s -> s.space("Aa")
                                .comma("Bb")
                                .comma("Cc")
                                .comma("Dd")
                        ).as("strStatic")
                )
                .asQuery();

        final Map<String, Object> row;
        row = session.queryOneObject(stmt, RowMaps::hashMap);

        Assert.assertNotNull(row);
        Assert.assertEquals(row.get("str"), "Aa");
        Assert.assertEquals(row.get("strStatic"), "Bb");
    }

    /**
     * @see MySQLs#exportSet(Object, Object, Object, Object, Object)
     */
    @Test//(invocationCount = 10)
    public void exportSetFunc(final SyncLocalSession session) {
        final Select stmt;
        stmt = MySQLs.query()
                .select(exportSet(6, "1", "0", ",", 10).as("bitStr"))
                .asQuery();

        final String row;
        row = session.queryOne(stmt, String.class);

        Assert.assertEquals(row, "0,1,1,0,0,0,0,0,0,0");

    }

    /**
     * @see MySQLs#fromBase64(Object)
     */
    @Test
    public void fromBase64Func(final SyncLocalSession session) {
        final String source = "QinArmy's army,I love army. 秦军的 army";
        final Select stmt;
        stmt = MySQLs.query()
                .select(fromBase64(toBase64(source)).as("source"))
                .asQuery();

        final String row;
        row = session.queryOne(stmt, String.class);

        Assert.assertEquals(row, source);

    }

    /**
     * @see MySQLs#groupConcat(SQLs.ArgDistinct, Consumer, Consumer)
     */
    @Test
    public void groupConcatFunc(final SyncLocalSession session) {
        final Select stmt;
        stmt = MySQLs.query()
                .select(groupConcat(SQLs.DISTINCT, s -> s.space(ChinaRegion_.name)
                                .comma(ChinaRegion_.createTime)
                                .comma(ChinaRegion_.regionGdp), s -> s.orderBy(ChinaRegion_.name).separator(",")
                        ).as("nameGroup")
                ).from(ChinaRegion_.T, AS, "t")
                .groupBy(ChinaRegion_.name, ChinaRegion_.createTime)
                .limit(SQLs::literal, 1)
                .asQuery();

        final String row;
        row = session.queryOne(stmt, String.class);

        Assert.assertNotNull(row);

        LOG.debug("{} row : {}", session.name(), row);

    }


}
