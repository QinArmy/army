package io.army.session.sync.mysql;

import com.alibaba.fastjson2.JSON;
import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.TypeDefs;
import io.army.criteria.mysql.MySQLCastType;
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
import static io.army.criteria.impl.SQLs.ASTERISK;
import static io.army.criteria.impl.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class MySQLSyncFunctionTests extends MySQLSynSessionTestSupport {


    private static final Logger LOG = LoggerFactory.getLogger(MySQLSyncFunctionTests.class);


    /**
     * @see MySQLs#jsonTable(Object, Object, SQLs.WordColumns, Consumer)
     */
    @Test
    public void jsonTableStatic(final SyncLocalSession session) {
        final String jsonDocument;
        jsonDocument = "[{\"a\":\"3\"},{\"a\":2},{\"b\":1},{\"a\":0},{\"a\":[1,2]}]";

        final Select stmt;
        stmt = MySQLs.query()
                .select(s -> s.space("t", PERIOD, ASTERISK))
                .from(jsonTable(jsonDocument, "$[*]", COLUMNS, s -> s.space("rowId", FOR_ORDINALITY)
                                .comma("ac", TypeDefs.space(MySQLType.VARCHAR, 100), PATH, "$.a", o -> o.spaceDefault("111").onEmpty().spaceDefault("999").onError())
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
                .select(jsonValue(jsonDoc, "$.fname", s -> s.returning(MySQLCastType.CHAR, 3, CHARACTER_SET, "utf8mb4")).as("name"))
                .comma(jsonValue(jsonDoc, "$.date", s -> s.returning(MySQLCastType.DATE).spaceNull().onEmpty()).as("date"))
                .asQuery();

        final Map<String, Object> row;
        row = session.queryOneObject(stmt, RowMaps::hashMap);

        Assert.assertNotNull(row);
        Assert.assertEquals(row.get("name"), "Joe");
        Assert.assertEquals(row.get("date"), LocalDate.parse("2023-12-18"));
    }


}
