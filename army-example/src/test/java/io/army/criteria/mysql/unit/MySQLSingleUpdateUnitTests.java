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

package io.army.criteria.mysql.unit;

import io.army.criteria.Update;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static io.army.criteria.impl.SQLs.AS;

/**
 * <p>
 * This class is unit test class of {@link MySQLs#singleUpdate()} and {@link MySQLs#batchSingleUpdate()}
 */
public class MySQLSingleUpdateUnitTests extends MySQLUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLSingleUpdateUnitTests.class);


    @Test//(invocationCount = 10)
    public void simpleUpdateParent() {
        final ChinaRegion<?> criteria = new ChinaRegion<>();
        criteria.setId(888L)
                .setRegionGdp(new BigDecimal("6666.00"));

        final Update stmt;
        stmt = MySQLs.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.name, SQLs::literal, this.randomProvince())
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, criteria.getRegionGdp())
                .whereIf(ChinaRegion_.id::equal, SQLs::param, criteria::getId)
                .and(ChinaRegion_.createTime::less, SQLs::literal, LocalDateTime.now().minusDays(2))
                .asUpdate();

        printStmt(LOG, stmt);

    }


}
