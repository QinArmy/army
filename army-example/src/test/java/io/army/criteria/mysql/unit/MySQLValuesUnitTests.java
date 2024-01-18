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

import io.army.criteria.*;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.mysql.MySQLValues;
import io.army.dialect.mysql.MySQLDialect;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.function.Supplier;

import static io.army.criteria.impl.SQLs.*;

public class MySQLValuesUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLValuesUnitTests.class);


    @Test
    public void simpleValues() {
        Values stmt;
        stmt = this.createSimpleValues(MySQLs::valuesStmt)
                .asValues();
        printStmt(stmt);

    }

    @Test
    public void unionValues() {
        Values stmt;
        stmt = this.createSimpleValues(MySQLs::valuesStmt)

                .orderBy(SQLs.refSelection("column_0"), SQLs.refSelection("column_1")::desc)
                .limit(SQLs::literal, 3)
                .asValues();

        printStmt(stmt);

    }

    @Test
    public void simpleSubValues() {
        Select stmt;
        stmt = MySQLs.query()
                .select(s -> s.space("s", PERIOD, ASTERISK))
                .from(this.createSimpleValues(MySQLs::subValues)
                        ::asValues
                ).as("c")
                .join(ChinaRegion_.T, AS, "c").on(SQLs.refField("s", "column_0")::equal, ChinaRegion_.id)
                .where(ChinaRegion_.id::equal, SQLs::literal, "1")
                .asQuery();

        printStmt(stmt);

    }

    @Test
    public void unionSubValues() {
        Select stmt;
        stmt = MySQLs.query()
                .select(s -> s.space("s", PERIOD, ASTERISK))
                .from(() -> this.createSimpleValues(MySQLs::subValues)
                        .asValues())
                .as("s")
                .join(ChinaRegion_.T, AS, "c").on(SQLs.refField("s", "column_0")::equal, ChinaRegion_.id)
                .where(ChinaRegion_.id::equal, SQLs::literal, "1")
                .asQuery();

        printStmt(stmt);

    }


    /**
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use the interface that start with {@code _ }
     * ,because army don't guarantee compatibility to future distribution.
     *
     */
    private <V extends ValuesQuery> MySQLValues._UnionOrderBySpec<V> createSimpleValues(Supplier<MySQLValues._ValueSpec<V>> supplier) {
        return supplier.get()
                .parens(s -> s.values()
                        .leftParen(SQLs::literalValue, 1, "海问香", new BigDecimal("9999.88"), LocalDate.now())
                        .comma(SQLs::literalValue, DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                        .rightParen()

                        .leftParen(SQLs::literalValue, 2, "大仓", new BigDecimal("9999.66"), LocalDate.now().plusDays(1))
                        .comma(SQLs::literalValue, DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                        .rightParen()

                        .leftParen(SQLs::literalValue, 3, "卡拉肖克·玲", new BigDecimal("6666.88"), LocalDate.now().minusDays(3))
                        .comma(SQLs::literalValue, DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                        .rightParen()

                        .leftParen(SQLs::literalValue, 4, "幽弥狂", new BigDecimal("8888.88"), LocalDate.now().minusDays(8))
                        .comma(SQLs::literalValue, DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                        .rightParen()

                        .orderBy(SQLs.refSelection("column_1"), SQLs.literalValue(2)::desc)
                        .limit(SQLs::literal, 4)
                        .asValues()
                );
    }


    private void printStmt(final PrimaryStatement statement) {
        String sql;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            if (dialect.compareWith(MySQLDialect.MySQL80) < 0) {
                continue;
            }
            sql = statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);
        }

    }


}
