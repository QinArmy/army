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

package io.army.criteria.postgre.statement;

import io.army.criteria.BatchDelete;
import io.army.criteria.Delete;
import io.army.criteria.IllegalOneStmtModeException;
import io.army.criteria.Select;
import io.army.criteria.dialect.BatchReturningDelete;
import io.army.criteria.dialect.ReturningDelete;
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

public class DeleteUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteUnitTests.class);


    @Test
    public void deleteParent() {
        final Delete stmt;
        stmt = Postgres.singleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .using(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .and(ChinaRegion_.id::equal, SQLs::literal, 1)
                .asDelete();

        printStmt(LOG, stmt);
    }

    @Test
    public void returningDeleteParent() {
        final ReturningDelete stmt;
        stmt = Postgres.singleDelete()
                .deleteFrom(ChinaRegion_.T, ASTERISK, AS, "c")
                .using(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .and(ChinaRegion_.id::equal, SQLs::literal, 1)
                .returning("c", PERIOD, ChinaRegion_.T)
                .comma(HistoryChinaRegion_.id)
                .asReturningDelete();

        printStmt(LOG, stmt);
    }


    @Test
    public void batchDeleteParent() {
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


        final BatchDelete stmt;
        stmt = Postgres.batchSingleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .using(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .and(ChinaRegion_.id::spaceEqual, SQLs::namedParam)
                .and(ChinaRegion_.regionGdp::spaceLess, SQLs::namedParam)
                .asDelete()
                .namedParamList(paramList);

        printStmt(LOG, stmt);
    }

    @Test//(invocationCount = 100)
    public void batchReturningDeleteParent() {
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


        final BatchReturningDelete stmt;
        stmt = Postgres.batchSingleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .using(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .and(ChinaRegion_.id::spaceEqual, SQLs::namedParam)
                .and(ChinaRegion_.regionGdp::spaceLess, SQLs::namedParam)
                .returning("c", PERIOD, ChinaRegion_.T)
                .asReturningDelete()
                .namedParamList(paramList);


        printStmt(LOG, stmt);

    }


    @Test(expectedExceptions = IllegalOneStmtModeException.class)
    public void updateChildBeforeParentMainError() {
        final List<Long> idList = Arrays.asList(1L, 2L, 3L, 4L);

        final Delete stmt;
        stmt = Postgres.singleDelete()
                .with("child_cte").as(sw -> sw.deleteFrom(ChinaProvince_.T, AS, "p")
                        .using(ChinaRegion_.T, AS, "c")
                        .where(ChinaProvince_.id.in(SQLs::rowParam, idList.subList(0, 3)))
                        .and(ChinaProvince_.id::equal, ChinaRegion_.id)
                        .returning(SQLs.field("p", ChinaProvince_.id).as("myId"))
                        .asReturningDelete()
                ).space()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, idList))
                .asDelete();

        Assert.fail();
        printStmt(LOG, stmt);

    }

    @Test(expectedExceptions = IllegalOneStmtModeException.class)
    public void updateChildBeforeParentCteError() {
        final List<Long> idList = Arrays.asList(1L, 2L, 3L, 4L);

        final Select stmt;
        stmt = Postgres.query()
                .with("cte").as(sw -> sw.deleteFrom(ChinaProvince_.T, AS, "p")
                        .using(ChinaRegion_.T, AS, "c")
                        .where(ChinaProvince_.id.in(SQLs::rowParam, idList))
                        .and(ChinaProvince_.id::equal, ChinaRegion_.id)
                        .returning(SQLs.field("p", ChinaProvince_.id).as("myId"))
                        .asReturningDelete()
                ).space()
                .select(s -> s.space(SQLs.refField("cte", "myId")))
                .from("cte")
                .asQuery();

        Assert.fail();
        printStmt(LOG, stmt);

    }


}
