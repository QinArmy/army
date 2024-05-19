package io.army.criteria.postgre.statement;

import io.army.criteria.Select;
import io.army.criteria.Values;
import io.army.criteria.ValuesQuery;
import io.army.criteria.Visible;
import io.army.criteria.impl.Postgres;
import io.army.criteria.postgre.PostgreValues;
import io.army.criteria.standard.SQLs;
import io.army.dialect.PostgreDialect;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.util.Decimals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.function.Supplier;

import static io.army.criteria.standard.SQLs.*;

public class ValuesTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(ValuesTests.class);


    private static final LocalDate NOW = LocalDate.parse("2024-01-19");


    @Test
    public void simpleValues() {

        final Values stmt;
        stmt = Postgres.valuesStmt()
                .values()
                .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), NOW)
                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), NOW.plusDays(1))
                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), NOW.minusDays(3))
                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), NOW.minusDays(8))
                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                )
                .orderBy(SQLs.refSelection("column2"), SQLs.refSelection(2)::desc)
                .offset(SQLs::literal, 1, ROWS)
                .fetch(FIRST, SQLs::literal, 4, ROWS, ONLY)
                .asValues();


        final String sql, expectedSql;
        expectedSql = "VALUES ( 1::INTEGER , VARCHAR '海问香' , 9999.88::DECIMAL , DATE '2024-01-19' , VARCHAR 'MONDAY' , TRUE , 1::INTEGER + 3::INTEGER ) , ( 2::INTEGER , VARCHAR '大仓' , 9999.66::DECIMAL , DATE '2024-01-20' , VARCHAR 'SUNDAY' , TRUE , 13::INTEGER - 3::INTEGER ) , ( 3::INTEGER , VARCHAR '卡拉肖克·玲' , 6666.88::DECIMAL , DATE '2024-01-16' , VARCHAR 'FRIDAY' , TRUE , 3::INTEGER - 3::INTEGER ) , ( 4::INTEGER , VARCHAR '幽弥狂' , 8888.88::DECIMAL , DATE '2024-01-11' , VARCHAR 'TUESDAY' , FALSE , 81::INTEGER / 3::INTEGER ) ORDER BY column2 , 2 DESC OFFSET 1 ROWS FETCH FIRST 4 ROWS ONLY";
        sql = stmt.mockAsString(PostgreDialect.POSTGRE16, Visible.ONLY_VISIBLE, false);

        LOG.debug("sql : \n{}", sql);
        Assert.assertEquals(sql, expectedSql);

    }

    @Test
    public void dynamicSimpleValues() {
        final Values stmt;
        stmt = Postgres.valuesStmt()
                .values(r -> r.parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), NOW)
                                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                                )
                                .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), NOW.plusDays(1))
                                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                                )
                                .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), NOW.minusDays(3))
                                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                                )
                                .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), NOW.minusDays(8))
                                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                                )
                )
                .orderBy(SQLs.refSelection("column2"), SQLs.refSelection(2)::desc)
                .limit(SQLs::literal, 4)
                .asValues();

        final String sql, expectedSql;
        expectedSql = "VALUES ( 1::INTEGER , VARCHAR '海问香' , 9999.88::DECIMAL , DATE '2024-01-19' , VARCHAR 'MONDAY' , TRUE , 1::INTEGER + 3::INTEGER ) , ( 2::INTEGER , VARCHAR '大仓' , 9999.66::DECIMAL , DATE '2024-01-20' , VARCHAR 'SUNDAY' , TRUE , 13::INTEGER - 3::INTEGER ) , ( 3::INTEGER , VARCHAR '卡拉肖克·玲' , 6666.88::DECIMAL , DATE '2024-01-16' , VARCHAR 'FRIDAY' , TRUE , 3::INTEGER - 3::INTEGER ) , ( 4::INTEGER , VARCHAR '幽弥狂' , 8888.88::DECIMAL , DATE '2024-01-11' , VARCHAR 'TUESDAY' , FALSE , 81::INTEGER / 3::INTEGER ) ORDER BY column2 , 2 DESC LIMIT 4";
        sql = stmt.mockAsString(PostgreDialect.POSTGRE16, Visible.ONLY_VISIBLE, false);
        LOG.debug("sql : \n{}", sql);
        Assert.assertEquals(sql, expectedSql);
    }

    @Test
    public void parensValues() {
        final Values stmt;
        stmt = createSimpleValues(Postgres::valuesStmt)
                .orderBy(SQLs.refSelection("column2"), SQLs.refSelection("column3")::desc)
                .limit(SQLs::literal, 3)
                .asValues();

        final String sql, expectedSql;
        expectedSql = "( VALUES ( 1::INTEGER , VARCHAR '海问香' , 9999.88::DECIMAL , DATE '2024-01-19' , VARCHAR 'MONDAY' , TRUE , 1::INTEGER + 3::INTEGER ) , ( 2::INTEGER , VARCHAR '大仓' , 9999.66::DECIMAL , DATE '2024-01-20' , VARCHAR 'SUNDAY' , TRUE , 13::INTEGER - 3::INTEGER ) , ( 3::INTEGER , VARCHAR '卡拉肖克·玲' , 6666.88::DECIMAL , DATE '2024-01-16' , VARCHAR 'FRIDAY' , TRUE , 3::INTEGER - 3::INTEGER ) , ( 4::INTEGER , VARCHAR '幽弥狂' , 8888.88::DECIMAL , DATE '2024-01-11' , VARCHAR 'TUESDAY' , FALSE , 81::INTEGER / 3::INTEGER ) ORDER BY column2 , 2 DESC LIMIT 4 )";
        sql = stmt.mockAsString(PostgreDialect.POSTGRE16, Visible.ONLY_VISIBLE, false);
        LOG.debug("sql : \n{}", sql);
        Assert.assertEquals(sql, expectedSql);

    }

    @Test
    public void simpleSubValues() {
        Select stmt;
        stmt = Postgres.query()
                .select(s -> s.space("s", PERIOD, ASTERISK))
                .from(createSimpleValues(Postgres::subValues)
                        ::asValues
                ).as("s")
                .join(ChinaRegion_.T, AS, "c").on(SQLs.refField("s", "column1")::equal, ChinaRegion_.id)
                .where(ChinaRegion_.id::equal, SQLs::literal, "1")
                .asQuery();

        final String sql, expectedSql;
        expectedSql = "SELECT s.column1 , s.column2 , s.column3 , s.column4 , s.column5 , s.column6 , s.column7 FROM ( ( VALUES ( 1::INTEGER , VARCHAR '海问香' , 9999.88::DECIMAL , DATE '2024-01-19' , VARCHAR 'MONDAY' , TRUE , 1::INTEGER + 3::INTEGER ) , ( 2::INTEGER , VARCHAR '大仓' , 9999.66::DECIMAL , DATE '2024-01-20' , VARCHAR 'SUNDAY' , TRUE , 13::INTEGER - 3::INTEGER ) , ( 3::INTEGER , VARCHAR '卡拉肖克·玲' , 6666.88::DECIMAL , DATE '2024-01-16' , VARCHAR 'FRIDAY' , TRUE , 3::INTEGER - 3::INTEGER ) , ( 4::INTEGER , VARCHAR '幽弥狂' , 8888.88::DECIMAL , DATE '2024-01-11' , VARCHAR 'TUESDAY' , FALSE , 81::INTEGER / 3::INTEGER ) ORDER BY column2 , 2 DESC LIMIT 4 ) ) AS s JOIN china_region AS c ON s.column1 = c.id WHERE c.id = 1::BIGINT AND c.visible = TRUE";
        sql = stmt.mockAsString(PostgreDialect.POSTGRE16, Visible.ONLY_VISIBLE, false);
        LOG.debug("sql : \n{}", sql);
        Assert.assertEquals(sql, expectedSql);

    }

    @Test
    public void unionValues() {
        final Values stmt;
        stmt = Postgres.valuesStmt()
                .values()
                .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), NOW)
                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), NOW.plusDays(1))
                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), NOW.minusDays(3))
                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), NOW.minusDays(8))
                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                )
                .unionAll()
                .values()
                .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), NOW)
                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), NOW.plusDays(1))
                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), NOW.minusDays(3))
                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), NOW.minusDays(8))
                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                )
                .orderBy(SQLs.refSelection("column2"), SQLs.refSelection(2)::desc)
                .limit(SQLs::literal, 8)
                .asValues();

        final String sql, expectedSql;
        expectedSql = "VALUES ( 1::INTEGER , VARCHAR '海问香' , 9999.88::DECIMAL , DATE '2024-01-19' , VARCHAR 'MONDAY' , TRUE , 1::INTEGER + 3::INTEGER ) , ( 2::INTEGER , VARCHAR '大仓' , 9999.66::DECIMAL , DATE '2024-01-20' , VARCHAR 'SUNDAY' , TRUE , 13::INTEGER - 3::INTEGER ) , ( 3::INTEGER , VARCHAR '卡拉肖克·玲' , 6666.88::DECIMAL , DATE '2024-01-16' , VARCHAR 'FRIDAY' , TRUE , 3::INTEGER - 3::INTEGER ) , ( 4::INTEGER , VARCHAR '幽弥狂' , 8888.88::DECIMAL , DATE '2024-01-11' , VARCHAR 'TUESDAY' , FALSE , 81::INTEGER / 3::INTEGER ) UNION ALL VALUES ( 1::INTEGER , VARCHAR '海问香' , 9999.88::DECIMAL , DATE '2024-01-19' , VARCHAR 'MONDAY' , TRUE , 1::INTEGER + 3::INTEGER ) , ( 2::INTEGER , VARCHAR '大仓' , 9999.66::DECIMAL , DATE '2024-01-20' , VARCHAR 'SUNDAY' , TRUE , 13::INTEGER - 3::INTEGER ) , ( 3::INTEGER , VARCHAR '卡拉肖克·玲' , 6666.88::DECIMAL , DATE '2024-01-16' , VARCHAR 'FRIDAY' , TRUE , 3::INTEGER - 3::INTEGER ) , ( 4::INTEGER , VARCHAR '幽弥狂' , 8888.88::DECIMAL , DATE '2024-01-11' , VARCHAR 'TUESDAY' , FALSE , 81::INTEGER / 3::INTEGER ) ORDER BY column2 , 2 DESC LIMIT 8";
        sql = stmt.mockAsString(PostgreDialect.POSTGRE16, Visible.ONLY_VISIBLE, false);
        LOG.debug("sql : \n{}", sql);
        Assert.assertEquals(sql, expectedSql);

    }


    /**
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use the interface that start with {@code _ }
     * ,because army don't guarantee compatibility to future distribution.
     */
    private <V extends ValuesQuery> PostgreValues._UnionOrderBySpec<V> createSimpleValues(Supplier<PostgreValues.ValuesSpec<V>> supplier) {

        return supplier.get()
                .parens(v -> v.values()
                        .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), NOW)
                                .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), NOW.plusDays(1))
                                .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), NOW.minusDays(3))
                                .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), NOW.minusDays(8))
                                .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                        )
                        .orderBy(SQLs.refSelection("column2"), SQLs.refSelection(2)::desc)
                        .limit(SQLs::literal, 4)
                        .asValues()
                );
    }


}
