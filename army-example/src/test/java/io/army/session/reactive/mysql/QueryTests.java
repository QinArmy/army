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


import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.reactive.ReactiveLocalSession;
import org.testng.annotations.Test;

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class QueryTests extends MySQLReactiveSessionTestsSupport {


    @Test
    public void queryFields(final ReactiveLocalSession session) {
        final Select stmt;

        stmt = MySQLs.query()
                .select(ChinaRegion_.id, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .from(ChinaRegion_.T, AS, "c")
                .limit(SQLs::literal, 10)
                .asQuery();

        session.query(stmt, ChinaRegion_.CLASS)
                .blockLast();
    }

    @Test
    public void queryDomain(final ReactiveLocalSession session) {
        final Select stmt;

        stmt = MySQLs.query()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .limit(SQLs::literal, 10)
                .asQuery();

        session.queryObject(stmt, ChinaRegion_::constructor)
                .blockLast();
    }


}
