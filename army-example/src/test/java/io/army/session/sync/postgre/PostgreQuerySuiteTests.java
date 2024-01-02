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

package io.army.session.sync.postgre;

import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sync.SyncLocalSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class PostgreQuerySuiteTests extends PostgreSuiteTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreQuerySuiteTests.class);

    @Test
    public void selectDomain(final SyncLocalSession syncSession) {
        final Select stmt;
        stmt = Postgres.query()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
//                .where(ChinaRegion_.name.equal(SQLs::param, "曲境"))
//                .and(ChinaRegion_.createTime::equal, SQLs::literal, LocalDateTime.now().minusDays(1))
                .limit(SQLs::literal, 1)
                .asQuery();

        final Supplier<ChinaRegion<?>> constructor = ChinaRegion::new;

        syncSession.queryObject(stmt, constructor)
                .forEach(c -> LOG.debug("{}", c.getName()));

    }

}
