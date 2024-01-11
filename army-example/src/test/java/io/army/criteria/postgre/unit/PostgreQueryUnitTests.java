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
                .selectsDistinctOn(s -> {
                            s.accept(SQLs.refSelection("aa\\nbb"));
                            SQLs.refSelection(2);
                        }, s -> s.selection(SQLs.literalValue("aa'")::as, "aa\\nbb", PillUser_.id)
                ).from(PillUser_.T, SQLs.AS, "u")
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
                .select(s -> s.space(SQLs.refField("func", "value")::as, "json1")
                        .comma(SQLs.refField("func2", "value")::as, "json2")
                        .comma(SQLs.refField("func2", "ordinal")::as, "json3")
                )
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
                .select(s -> s.space(SQLs.refField("func", "myFunc")::as, "json1")) // func.myFunc not exists
                .from(jsonbPathQueryTz(jsonField, SQLs::literal, path))
                .as("func").parens("value")
                .asQuery();

    }


}
