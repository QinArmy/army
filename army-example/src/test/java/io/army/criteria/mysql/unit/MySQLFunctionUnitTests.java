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

import io.army.criteria.PrimaryStatement;
import io.army.criteria.Select;
import io.army.criteria.Visible;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.standard.SQLs;
import io.army.dialect.MySQLDialect;
import io.army.example.pill.domain.PillUser_;
import io.army.example.pill.struct.PillUserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.standard.SQLs.cases;

public class MySQLFunctionUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLFunctionUnitTests.class);

    @Test
    public void nthValue() {

    }

    @Test
    public void caseFunc() {
        final Select stmt;
        stmt = MySQLs.query()
                .select(cases(PillUser_.userType)
                        .when(SQLs.literalValue(PillUserType.NONE))
                        .then(SQLs.literalValue(1))

                        .when(SQLs.literalValue(PillUserType.PARTNER))
                        .then(SQLs.literalValue(2))

                        .when(SQLs.literalValue(PillUserType.ENTERPRISE))
                        .then(SQLs.literalValue(3))

                        .elseValue(SQLs.literalValue(0))

                        .end()
                        .plus(SQLs.literalValue(1)).as("userType"))
                .from(PillUser_.T, SQLs.AS, "u")
                .asQuery();
        printStmt(stmt);
    }


    private void printStmt(final PrimaryStatement statement) {
        String sql;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            sql = statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);
        }

    }


}
