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

import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.example.pill.domain.PillUser_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.standard.SQLs.UNBOUNDED_FOLLOWING;

public class MySQLWindowFuncUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLWindowFuncUnitTests.class);


    @Test
    public void rowNumber() {
        Select stmt;
        stmt = MySQLs.query()
                .select(MySQLs.rowNumber().over(s -> s.range(UNBOUNDED_FOLLOWING)).as("rowNumber"))
                .comma(PillUser_.id, PillUser_.createTime)
                .asQuery();
        LOG.debug("{}", stmt);
    }

}
