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

import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.mapping.postgre.PostgreSingleRangeType;
import io.army.sqltype.PostgreType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.impl.Postgres.lower;
import static io.army.criteria.impl.Postgres.upper;

public class TypeUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(TypeUnitTests.class);

    /**
     * @see Postgres#upper(Expression)
     * @see Postgres#lower(Expression)
     * @see PostgreSingleRangeType#subtype()
     * @see PostgreSingleRangeType.UserDefinedRangeType#subtype()
     */
    @Test
    public void rangeSubtypeForLowerAndUpperFunc() {
        final PostgreSingleRangeType int4RangeType;
        int4RangeType = PostgreSingleRangeType.from(String.class, PostgreType.INT4RANGE);
        final Select stmt;
        stmt = Postgres.query()
                .select(lower(SQLs.literal(int4RangeType, "[1,4]")).as("lower subtype"))
                .comma(upper(SQLs.literal(int4RangeType, "[1,4]")).as("upper subtype"))
                .asQuery();

        printStmt(LOG, stmt);
    }


}
