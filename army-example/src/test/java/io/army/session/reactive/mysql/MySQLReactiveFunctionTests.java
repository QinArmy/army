package io.army.session.reactive.mysql;


import com.alibaba.fastjson2.JSON;
import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.TypeDefs;
import io.army.reactive.ReactiveLocalSession;
import io.army.sqltype.MySQLType;
import io.army.util.RowMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.army.criteria.impl.MySQLs.*;
import static io.army.criteria.impl.SQLs.ASTERISK;
import static io.army.criteria.impl.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class MySQLReactiveFunctionTests extends MySQLReactiveSessionTestsSupport {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLReactiveFunctionTests.class);


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
                                .comma("ac", TypeDefs.space(MySQLType.VARCHAR, 100), PATH, "$.a", o -> o.spaceDefault("111").onEmpty().spaceDefault("999").onError())
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
        stmt = query()
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


}
