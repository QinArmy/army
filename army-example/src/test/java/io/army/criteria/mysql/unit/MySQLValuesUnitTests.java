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
import io.army.criteria.Values;
import io.army.criteria.ValuesQuery;
import io.army.criteria.Visible;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.mysql.MySQLValues;
import io.army.dialect.mysql.MySQLDialect;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.util.Decimals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.function.Supplier;

import static io.army.criteria.impl.SQLs.*;

public class MySQLValuesUnitTests extends MySQLUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLValuesUnitTests.class);

    private static final LocalDate NOW = LocalDate.parse("2024-01-19");


    @Test
    public void simpleValues() {

        final Values stmt;
        stmt = MySQLs.valuesStmt()
                .values()
                .row(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), NOW)
                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                ).comma()
                .row(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), NOW.plusDays(1))
                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                ).comma()
                .row(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), NOW.minusDays(3))
                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                ).comma()
                .row(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), NOW.minusDays(8))
                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                )
                .orderBy(SQLs.refSelection("column_1"), SQLs.refSelection(2)::desc)
                .limit(SQLs::literal, 4)
                .asValues();


        final String sql, expectedSql;
        expectedSql = "VALUES ROW( 1 , '海问香' , 9999.88 , DATE '2024-01-19' , 'MONDAY' , TRUE , 1 + 3 ) , ROW( 2 , '大仓' , 9999.66 , DATE '2024-01-20' , 'SUNDAY' , TRUE , 13 - 3 ) , ROW( 3 , '卡拉肖克·玲' , 6666.88 , DATE '2024-01-16' , 'FRIDAY' , TRUE , 3 - 3 ) , ROW( 4 , '幽弥狂' , 8888.88 , DATE '2024-01-11' , 'TUESDAY' , FALSE , 81 / 3 ) ORDER BY column_1 , 2 DESC LIMIT 4";
        sql = stmt.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, false);

        Assert.assertEquals(sql, expectedSql);

    }

    @Test
    public void dynamicSimpleValues() {
        final Values stmt;
        stmt = MySQLs.valuesStmt()
                .values(r -> r.row(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), NOW)
                                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                                )
                        .row(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), NOW.plusDays(1))
                                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                                )
                        .row(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), NOW.minusDays(3))
                                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                                )
                        .row(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), NOW.minusDays(8))
                                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                                )
                )
                .orderBy(SQLs.refSelection("column_1"), SQLs.refSelection(2)::desc)
                .limit(SQLs::literal, 4)
                .asValues();

        final String sql, expectedSql;
        expectedSql = "VALUES ROW( 1 , '海问香' , 9999.88 , DATE '2024-01-19' , 'MONDAY' , TRUE , 1 + 3 ) , ROW( 2 , '大仓' , 9999.66 , DATE '2024-01-20' , 'SUNDAY' , TRUE , 13 - 3 ) , ROW( 3 , '卡拉肖克·玲' , 6666.88 , DATE '2024-01-16' , 'FRIDAY' , TRUE , 3 - 3 ) , ROW( 4 , '幽弥狂' , 8888.88 , DATE '2024-01-11' , 'TUESDAY' , FALSE , 81 / 3 ) ORDER BY column_1 , 2 DESC LIMIT 4";
        sql = stmt.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, false);

        Assert.assertEquals(sql, expectedSql);
    }

    @Test
    public void parensValues() {
        final Values stmt;
        stmt = createSimpleValues(MySQLs::valuesStmt)
                .orderBy(SQLs.refSelection("column_0"), SQLs.refSelection("column_1")::desc)
                .limit(SQLs::literal, 3)
                .asValues();

        final String sql, expectedSql;
        expectedSql = "( VALUES ROW( 1 , '海问香' , 9999.88 , DATE '2024-01-19' , 'MONDAY' , TRUE , 1 + 3 ) , ROW( 2 , '大仓' , 9999.66 , DATE '2024-01-20' , 'SUNDAY' , TRUE , 13 - 3 ) , ROW( 3 , '卡拉肖克·玲' , 6666.88 , DATE '2024-01-16' , 'FRIDAY' , TRUE , 3 - 3 ) , ROW( 4 , '幽弥狂' , 8888.88 , DATE '2024-01-11' , 'TUESDAY' , FALSE , 81 / 3 ) ORDER BY column_1 , 2 DESC LIMIT 4 )";
        sql = stmt.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, false);
        Assert.assertEquals(sql, expectedSql);

    }

    @Test
    public void simpleSubValues() {
        Select stmt;
        stmt = MySQLs.query()
                .select(s -> s.space("s", PERIOD, ASTERISK))
                .from(createSimpleValues(MySQLs::subValues)
                        ::asValues
                ).as("s")
                .join(ChinaRegion_.T, AS, "c").on(SQLs.refField("s", "column_0")::equal, ChinaRegion_.id)
                .where(ChinaRegion_.id::equal, SQLs::literal, "1")
                .asQuery();

        final String sql, expectedSql;
        expectedSql = "SELECT s.column_0 AS column_0 , s.column_1 AS column_1 , s.column_2 AS column_2 , s.column_3 AS column_3 , s.column_4 AS column_4 , s.column_5 AS column_5 , s.column_6 AS column_6 FROM ( ( VALUES ROW( 1 , '海问香' , 9999.88 , DATE '2024-01-19' , 'MONDAY' , TRUE , 1 + 3 ) , ROW( 2 , '大仓' , 9999.66 , DATE '2024-01-20' , 'SUNDAY' , TRUE , 13 - 3 ) , ROW( 3 , '卡拉肖克·玲' , 6666.88 , DATE '2024-01-16' , 'FRIDAY' , TRUE , 3 - 3 ) , ROW( 4 , '幽弥狂' , 8888.88 , DATE '2024-01-11' , 'TUESDAY' , FALSE , 81 / 3 ) ORDER BY column_1 , 2 DESC LIMIT 4 ) ) AS s JOIN china_region AS c ON s.column_0 = c.id WHERE c.id = 1 AND c.`visible` = TRUE";
        sql = stmt.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, false);
        Assert.assertEquals(sql, expectedSql);

    }

    @Test
    public void unionValues() {
        final Values stmt;
        stmt = MySQLs.valuesStmt()
                .values()
                .row(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), NOW)
                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                ).comma()
                .row(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), NOW.plusDays(1))
                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                ).comma()
                .row(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), NOW.minusDays(3))
                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                ).comma()
                .row(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), NOW.minusDays(8))
                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                )
                .unionAll()
                .values()
                .row(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), NOW)
                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                ).comma()
                .row(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), NOW.plusDays(1))
                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                ).comma()
                .row(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), NOW.minusDays(3))
                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                ).comma()
                .row(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), NOW.minusDays(8))
                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                )
                .orderBy(SQLs.refSelection("column_1"), SQLs.refSelection(2)::desc)
                .limit(SQLs::literal, 8)
                .asValues();

        final String sql, expectedSql;
        expectedSql = "VALUES ROW( 1 , '海问香' , 9999.88 , DATE '2024-01-19' , 'MONDAY' , TRUE , 1 + 3 ) , ROW( 2 , '大仓' , 9999.66 , DATE '2024-01-20' , 'SUNDAY' , TRUE , 13 - 3 ) , ROW( 3 , '卡拉肖克·玲' , 6666.88 , DATE '2024-01-16' , 'FRIDAY' , TRUE , 3 - 3 ) , ROW( 4 , '幽弥狂' , 8888.88 , DATE '2024-01-11' , 'TUESDAY' , FALSE , 81 / 3 ) UNION ALL VALUES ROW( 1 , '海问香' , 9999.88 , DATE '2024-01-19' , 'MONDAY' , TRUE , 1 + 3 ) , ROW( 2 , '大仓' , 9999.66 , DATE '2024-01-20' , 'SUNDAY' , TRUE , 13 - 3 ) , ROW( 3 , '卡拉肖克·玲' , 6666.88 , DATE '2024-01-16' , 'FRIDAY' , TRUE , 3 - 3 ) , ROW( 4 , '幽弥狂' , 8888.88 , DATE '2024-01-11' , 'TUESDAY' , FALSE , 81 / 3 ) ORDER BY column_1 , 2 DESC LIMIT 8";
        sql = stmt.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, false);
        Assert.assertEquals(sql, expectedSql);

    }


    /**
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use the interface that start with {@code _ }
     * ,because army don't guarantee compatibility to future distribution.
     */
    private <V extends ValuesQuery> MySQLValues._UnionOrderBySpec<V> createSimpleValues(Supplier<MySQLValues.ValuesSpec<V>> supplier) {

        return supplier.get()
                .parens(v -> v.values()
                        .row(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), NOW)
                                .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                        ).comma()
                        .row(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), NOW.plusDays(1))
                                .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                        ).comma()
                        .row(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), NOW.minusDays(3))
                                .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                        ).comma()
                        .row(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), NOW.minusDays(8))
                                .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                        )
                        .orderBy(SQLs.refSelection("column_1"), SQLs.refSelection(2)::desc)
                        .limit(SQLs::literal, 4)
                        .asValues()
                );
    }


}
