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

import io.army.criteria.BatchUpdate;
import io.army.criteria.IllegalOneStmtModeException;
import io.army.criteria.Update;
import io.army.criteria.dialect.BatchReturningUpdate;
import io.army.criteria.dialect.ReturningUpdate;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.HistoryChinaRegion_;
import io.army.util._Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.army.criteria.impl.SQLs.*;

public class UpdateUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateUnitTests.class);


    @Test
    public void updateParent() {
        final Update stmt;
        stmt = Postgres.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.name, SQLs::param, this.randomCity())
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::literal, new BigDecimal("100.00"))
                .setRow(ChinaRegion_.regionGdp, ChinaRegion_.population, Postgres.subQuery()
                        .select(HistoryChinaRegion_.regionGdp, HistoryChinaRegion_.population)
                        .from(HistoryChinaRegion_.T, AS, "h")
                        .where(HistoryChinaRegion_.id::equal, SQLs::literal, 1)
                        ::asQuery
                )
                .from(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .asUpdate();

        printStmt(LOG, stmt);
    }

    @Test
    public void returningUpdateParent() {
        final ReturningUpdate stmt;
        stmt = Postgres.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.name, SQLs::param, this.randomCity())
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::literal, new BigDecimal("100.00"))
                .setRow(ChinaRegion_.regionGdp, ChinaRegion_.population, Postgres.subQuery()
                        .select(HistoryChinaRegion_.regionGdp, HistoryChinaRegion_.population)
                        .from(HistoryChinaRegion_.T, AS, "h")
                        .where(HistoryChinaRegion_.id::equal, SQLs::literal, 1)
                        ::asQuery
                ).ifSetRow(ChinaRegion_.regionGdp, ChinaRegion_.population, Postgres.subQuery()
                        .select(HistoryChinaRegion_.regionGdp, HistoryChinaRegion_.population)
                        .from(HistoryChinaRegion_.T, AS, "h")
                        .where(HistoryChinaRegion_.id::equal, SQLs::literal, 1)
                        ::asQuery
                )
                .from(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .returning("c", PERIOD, ChinaRegion_.T)
                .comma(HistoryChinaRegion_.id, SQLs.field("hc", HistoryChinaRegion_.regionGdp).as("myGdp"))
                .asReturningUpdate();

        printStmt(LOG, stmt);
    }


    @Test//(invocationCount = 10000)
    public void batchUpdateParent() {
        Map<String, Object> paramMap;
        final List<Map<String, Object>> paramList = _Collections.arrayList();

        paramMap = _Collections.hashMap();
        paramMap.put(HistoryChinaRegion_.ID, 1);
        paramMap.put(ChinaRegion_.REGION_GDP, new BigDecimal("100.00"));
        paramList.add(paramMap);

        paramMap = _Collections.hashMap();
        paramMap.put(HistoryChinaRegion_.ID, 2);
        paramMap.put(ChinaRegion_.REGION_GDP, new BigDecimal("999.00"));
        paramList.add(paramMap);


        final BatchUpdate stmt;
        stmt = Postgres.batchSingleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.name, SQLs::param, this.randomCity())
                .setSpace(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .setRow(ChinaRegion_.regionGdp, ChinaRegion_.population, Postgres.subQuery()
                        .select(HistoryChinaRegion_.regionGdp, HistoryChinaRegion_.population)
                        .from(HistoryChinaRegion_.T, AS, "h")
                        .where(HistoryChinaRegion_.id::equal, SQLs::literal, 1)
                        ::asQuery
                )
                .from(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .and(ChinaRegion_.id::equal, SQLs::namedParam)
                .asUpdate()
                .namedParamList(paramList);

        printStmt(LOG, stmt);
    }

    @Test//(invocationCount = 100)
    public void batchReturningUpdateParent() {
        Map<String, Object> paramMap;
        final List<Map<String, Object>> paramList = _Collections.arrayList();

        paramMap = _Collections.hashMap();
        paramMap.put(HistoryChinaRegion_.ID, 1);
        paramMap.put(ChinaRegion_.REGION_GDP, new BigDecimal("100.00"));
        paramList.add(paramMap);

        paramMap = _Collections.hashMap();
        paramMap.put(HistoryChinaRegion_.ID, 2);
        paramMap.put(ChinaRegion_.REGION_GDP, new BigDecimal("999.00"));
        paramList.add(paramMap);


        final BatchReturningUpdate stmt;
        stmt = Postgres.batchSingleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.name, SQLs::param, this.randomCity())
                .setSpace(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .setRow(ChinaRegion_.regionGdp, ChinaRegion_.population, Postgres.subQuery()
                        .select(HistoryChinaRegion_.regionGdp, HistoryChinaRegion_.population)
                        .from(HistoryChinaRegion_.T, AS, "h")
                        .where(HistoryChinaRegion_.id::equal, SQLs::literal, 1)
                        ::asQuery
                )
                .from(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .and(ChinaRegion_.id::equal, SQLs::namedParam)
                .returning("c", PERIOD, ChinaRegion_.T)
                .asReturningUpdate()
                .namedParamList(paramList);


        printStmt(LOG, stmt);

    }


    @Test(expectedExceptions = IllegalOneStmtModeException.class)
    public void updateChildBeforeParentMainError() {
        final List<Long> idList = Arrays.asList(1L, 2L, 3L, 4L);

        final Update stmt;
        stmt = Postgres.singleUpdate()
                .with("child_cte").as(sw -> sw.update(ChinaProvince_.T, AS, "p")
                        .set(ChinaProvince_.governor, SQLs::param, randomPerson())
                        .from(ChinaRegion_.T, AS, "c")
                        .where(ChinaProvince_.id.in(SQLs::rowParam, idList.subList(0, 3)))
                        .and(ChinaProvince_.id::equal, ChinaRegion_.id)
                        .returning(SQLs.field("p", ChinaProvince_.id).as("myId"))
                        .asReturningUpdate()
                ).space()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.regionGdp, SQLs::param, randomDecimal())
                .where(ChinaRegion_.id.in(SQLs::rowParam, idList))
                .asUpdate();

        Assert.fail();
        printStmt(LOG, stmt);

    }

    @Test(expectedExceptions = IllegalOneStmtModeException.class)
    public void updateChildBeforeParentCteError() {
        final List<Long> idList = Arrays.asList(1L, 2L, 3L, 4L);

        final Update stmt;
        stmt = Postgres.singleUpdate()
                .with("first_child_cte").as(sw -> sw.update(ChinaProvince_.T, AS, "p")
                        .set(ChinaProvince_.governor, SQLs::param, randomPerson())
                        .from(ChinaRegion_.T, AS, "c")
                        .where(ChinaProvince_.id.in(SQLs::rowParam, idList))
                        .and(ChinaProvince_.id::equal, ChinaRegion_.id)
                        .returning(SQLs.field("p", ChinaProvince_.id).as("myId"))
                        .asReturningUpdate()
                ).comma("child_cte").as(sw -> sw.update(ChinaProvince_.T, AS, "p")
                        .set(ChinaProvince_.governor, SQLs::param, randomPerson())
                        .from(ChinaRegion_.T, AS, "c")
                        .where(ChinaProvince_.id.in(SQLs::rowParam, idList))
                        .and(ChinaProvince_.id::equal, ChinaRegion_.id)
                        .returning(ChinaProvince_.id)
                        .asReturningUpdate()
                ).space()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.updateTime, UPDATE_TIME_PARAM_PLACEHOLDER)
                .from("child_cte")
                .where(ChinaRegion_.id::equal, SQLs.refField("child_cte", ChinaRegion_.ID))
                .asUpdate();

        Assert.fail();
        printStmt(LOG, stmt);

    }


    @Test(expectedExceptions = IllegalOneStmtModeException.class)
    public void updateChildAfterParentMainError() {
        final List<Long> idList = Arrays.asList(1L, 2L, 3L, 4L);

        final Update stmt;
        stmt = Postgres.singleUpdate()
                .with("parent_cte").as(sw -> sw.update(ChinaRegion_.T, AS, "p")
                        .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, randomDecimal())
                        .where(ChinaRegion_.id.in(SQLs::rowParam, idList))
                        .returning(ChinaRegion_.id)
                        .asReturningUpdate()
                ).space()
                .update(ChinaProvince_.T, AS, "c")
                .set(ChinaProvince_.governor, SQLs::param, randomPerson())
                .where(ChinaProvince_.id.in(SQLs::rowParam, idList))
                .asUpdate();

        Assert.fail();
        printStmt(LOG, stmt);

    }


}
