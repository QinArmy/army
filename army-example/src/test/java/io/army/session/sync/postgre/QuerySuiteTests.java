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
import io.army.type.SqlRecord;
import io.army.util.RowMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class QuerySuiteTests extends SessionTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(QuerySuiteTests.class);

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

    @Test
    public void singleColumnSearchClause(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(10);
        session.batchSave(regionList);

        final Select stmt;
        stmt = Postgres.query()
                .withRecursive("cte").as(sw -> sw.select(ChinaRegion_.id, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.createTime)
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                        .union()
                        .select(ChinaRegion_.id, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.createTime)
                        .from(ChinaRegion_.T, AS, "t")
                        .join("cte").on(ChinaRegion_.id::equal, SQLs.refField("cte", ChinaRegion_.PARENT_ID))
                        .asQuery()
                ).search(s -> s.depthFirstBy(ChinaRegion_.ID).set("orderCol"))
                .space()
                .select(s -> s.space("t", PERIOD, ChinaRegion_.T))
                .from(ChinaRegion_.T, AS, "t")
                .asQuery();

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);

        SqlRecord[] orderCol;
        for (Map<String, Object> row : rowList) {
            orderCol = (SqlRecord[]) row.get("orderCol");
            for (SqlRecord record : orderCol) {
                Assert.assertEquals(record.size(), 1);
                Assert.assertTrue(record.get(0) instanceof Long);
            }
        }

    }


    @Test
    public void simpleColumnSearchClause(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(10);
        session.batchSave(regionList);

        final Select stmt;
        stmt = Postgres.query()
                .withRecursive("cte").as(sw -> sw.select(ChinaRegion_.id, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.createTime)
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                        .orderBy(ChinaRegion_.id)
                        .asQuery()
                ).search(s -> s.depthFirstBy(ChinaRegion_.ID, ChinaRegion_.CREATE_TIME).set("orderCol"))
                .space()
                .select(s -> s.space("t", PERIOD, ChinaRegion_.T))
                .from(ChinaRegion_.T, AS, "t")
                .asQuery();

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);

        SqlRecord[] orderCol;
        for (Map<String, Object> row : rowList) {
            orderCol = (SqlRecord[]) row.get("orderCol");
            for (SqlRecord record : orderCol) {
                Assert.assertEquals(record.size(), 2);
                Assert.assertTrue(record.get(0) instanceof Long);
                Assert.assertTrue(record.get(1) instanceof LocalDateTime);
            }
        }

    }


}
