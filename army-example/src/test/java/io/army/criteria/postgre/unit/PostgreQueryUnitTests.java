package io.army.criteria.postgre.unit;

import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.UnknownDerivedFieldException;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.pill.domain.PillUser_;
import io.army.mapping.JsonbType;
import io.army.mapping.optional.JsonPathType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.impl.Postgres.jsonbPathQueryTz;

public class PostgreQueryUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreQueryUnitTests.class);


    @Test
    public void distinctOnClause() {
        final Select stmt;
        stmt = Postgres.query()
                .selectDistinctOn(SQLs.refSelection("aa\\nbb"), SQLs.ref(2))
                .space(SQLs.literalValue("aa'")::as, "aa\\nbb", PillUser_.id)
                .from(PillUser_.T, SQLs.AS, "u")
                .orderBy(PillUser_.id)
                .asQuery();

        printStmt(LOG, stmt);
    }

    @Test//(invocationCount = 100)
    public void dynamicWindow() {
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literalValue(1)::as, "r")
                .from(PillUser_.T, SQLs.AS, "u")
                .windows(w -> {
                    w.window("w1").as(s -> s.partitionBy(PillUser_.userType).orderBy(PillUser_.id));
                    w.window("w2").as(s -> s.orderBy(PillUser_.id));
                })
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see #undoneColumnFuncErrorColumnAlias()
     */
    @Test
    public void undoneColumnFuncColumnAlias() {
        final String json, path, varPath, vars;
        json = "{\n" +
                "  \"a\": [\n" +
                "    \"2015-08-01 12:00:00-05\",\n" +
                "    \"2015-08-02 12:00:00-05\",\n" +
                "    \"2015-08-03 12:00:00-05\",\n" +
                "    \"2015-08-04 12:00:00-05\",\n" +
                "    \"2015-08-05 12:00:00-05\"\n" +
                "  ]\n" +
                "}";
        path = "$.a[*] ? (@.datetime() < \"2015-08-05\".datetime())";
        varPath = "$.a[*] ? (@.datetime() < $d.datetime())";
        vars = "{\"d\":\"2015-08-05\"}";

        final Expression jsonField;
        jsonField = SQLs.literal(JsonbType.TEXT, json);
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.refThis("func", "value")::as, "json1")
                .comma(SQLs.refThis("func2", "value")::as, "json2")
                .comma(SQLs.refThis("func2", "ordinal")::as, "json3")
                .from(jsonbPathQueryTz(jsonField, SQLs.literal(JsonPathType.INSTANCE, path)))
                .as("func").parens("value")
                .crossJoin(jsonbPathQueryTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars)::withOrdinality)
                .as("func2").parens("value", "ordinal")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see #undoneColumnFuncColumnAlias()
     */
    @Test(expectedExceptions = UnknownDerivedFieldException.class)
    public void undoneColumnFuncErrorColumnAlias() {
        final String json, path;
        json = "{\n" +
                "  \"a\": [\n" +
                "    \"2015-08-01 12:00:00-05\",\n" +
                "    \"2015-08-02 12:00:00-05\",\n" +
                "    \"2015-08-03 12:00:00-05\",\n" +
                "    \"2015-08-04 12:00:00-05\",\n" +
                "    \"2015-08-05 12:00:00-05\"\n" +
                "  ]\n" +
                "}";
        path = "$.a[*] ? (@.datetime() < \"2015-08-05\".datetime())";

        final Expression jsonField;
        jsonField = SQLs.literal(JsonbType.TEXT, json);
        Postgres.query()
                .select(SQLs.refThis("func", "myFunc")::as, "json1") // func.myFunc not exists
                .from(jsonbPathQueryTz(jsonField, SQLs::literal, path))
                .as("func").parens("value")
                .asQuery();

    }


}
