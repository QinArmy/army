package io.army.session.reactive.mysql;


import com.alibaba.fastjson2.JSON;
import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.TypeDefs;
import io.army.reactive.ReactiveLocalSession;
import io.army.sqltype.MySQLType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

        final Supplier<Map<String, Object>> constructor = HashMap::new;

        session.queryObject(stmt, constructor)
                .doOnNext(row -> LOG.debug(JSON.toJSONString(row)))
                .blockLast();

    }


}
