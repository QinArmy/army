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
import io.army.criteria.NoColumnFuncFieldAliasException;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.mapping.JsonbType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.function.Consumer;

import static io.army.criteria.impl.Postgres.*;
import static io.army.criteria.impl.SQLs.ASTERISK;
import static io.army.criteria.impl.SQLs.PERIOD;

public class PostgreFuncUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreFuncUnitTests.class);

    /**
     * @see SQLs#cases()
     */
    @Test
    public void caseFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(cases(SQLs.space(3))
                        .when(1)
                        .then(2)
                        .elseValue(0)
                        .end().as("a")
                ).asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#coalesce(Expression, Expression...)
     */
    @Test
    public void coalesceFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(coalesce(SQLs.literalValue(1), SQLs.literalValue(2))::as, "a")
                .comma(coalesce(c -> {
                    c.accept(SQLs.literalValue(1));
                    c.accept(SQLs.literalValue(3));
                })::as, "b")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see SQLs#nullIf(Object, Object)
     */
    @Test
    public void nullIfFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(nullIf(SQLs.literalValue(1), SQLs.literalValue(2))::as, "a")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#greatest(Expression, Expression...)
     * @see Postgres#greatest(Consumer)
     */
    @Test
    public void greatestFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(greatest(SQLs.literalValue(1), SQLs.literalValue(2))::as, "a")
                .comma(greatest(c -> {
                    c.accept(SQLs.literalValue(1));
                    c.accept(SQLs.literalValue(3));
                })::as, "b")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#least(Expression, Expression...)
     * @see Postgres#least(Consumer)
     */
    @Test
    public void leastFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(least(SQLs.literalValue(1), SQLs.literalValue(2))::as, "a")
                .comma(least(c -> {
                    c.accept(SQLs.literalValue(1));
                    c.accept(SQLs.literalValue(3));
                })::as, "b")
                .asQuery();

        printStmt(LOG, stmt);
    }

    @Test(expectedExceptions = NoColumnFuncFieldAliasException.class)
    public void noColumnFuncFieldAliasError() {
        final String json, path;
        json = "{\"a\":[1,2,3,4,5]}";
        path = "$.a[*] ? (@ >= 2 && @ <= 4)";

        try {
            Postgres.query()
                    .select(s -> s.space("func", PERIOD, ASTERISK))
                    .from(jsonbPathQuery(SQLs.literal(JsonbType.TEXT, json), SQLs::literal, path))
                    .as("func") // here no specified jsonbPathQuery function field alias
                    .asQuery();
            Assert.fail();
        } catch (NoColumnFuncFieldAliasException e) {
            LOG.debug("{}", e.getMessage());
            throw e;
        }


    }


}
