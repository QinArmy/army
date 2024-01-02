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
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.impl.SQLs.AS;

public class PostgreOperatorUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreOperatorUnitTests.class);

    @Test
    public void similarTo() {
        Select stmt;
        stmt = Postgres.query()
                .select(ChinaRegion_.id)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.name::whiteSpace, Postgres::notSimilarTo, SQLs::literal, "%(b|d)%")
                .and(ChinaRegion_.name::whiteSpace, Postgres::similarTo, SQLs::literal, "%(b|d)%")
                .and(ChinaRegion_.name.whiteSpace(Postgres::notSimilarTo, SQLs::literal, "%(b|d)%", SQLs.ESCAPE, '|'))
                .and(ChinaRegion_.name.whiteSpace(Postgres::similarTo, SQLs::literal, "Hong Kong"))
                .and(ChinaRegion_.name.whiteSpace(Postgres::similarTo, SQLs::literal, "Hong |_ong", SQLs.ESCAPE, '|'))
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#collate(Expression, String)
     */
    @Test
    public void collateOpe() {
        Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literalValue("zoro").space(Postgres::collate, "de_DE").as("name"))
                .asQuery();

        printStmt(LOG, stmt);
    }


}
