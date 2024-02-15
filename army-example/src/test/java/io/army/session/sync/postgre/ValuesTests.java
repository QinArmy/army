package io.army.session.sync.postgre;


import io.army.criteria.Select;
import io.army.criteria.Values;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sync.SyncLocalSession;
import io.army.util.Decimals;
import io.army.util.RowMaps;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static io.army.criteria.impl.SQLs.*;

@Test(dataProvider = "localSessionProvider")
public class ValuesTests extends SessionTestSupport {


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void simpleValues(final SyncLocalSession session) {

        final LocalDate now = LocalDate.now();

        final long startNanoSecond = System.nanoTime();

        final Values stmt;
        stmt = Postgres.valuesStmt()
                .values()
                .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), now)
                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), now.plusDays(1))
                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), now.minusDays(3))
                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), now.minusDays(8))
                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                )
                .orderBy(SQLs.refSelection("column2"), SQLs.refSelection(2)::desc)
                .offset(SQLs::literal, 0, ROWS)
                .fetch(FIRST, SQLs::literal, 4, ROWS, ONLY)
                .asValues();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);

        Assert.assertEquals(rowList.size(), 4);

    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void unionSimpleValues(final SyncLocalSession session) {

        final LocalDate now = LocalDate.now();

        final long startNanoSecond = System.nanoTime();

        final Values stmt;
        stmt = Postgres.valuesStmt()
                .values()
                .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), now)
                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), now.plusDays(1))
                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), now.minusDays(3))
                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), now.minusDays(8))
                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                )
                .unionAll()
                .values()
                .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), now)
                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), now.plusDays(1))
                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), now.minusDays(3))
                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), now.minusDays(8))
                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                )
                .orderBy(SQLs.refSelection("column2"), SQLs.refSelection(2)::desc)
                .limit(SQLs::literal, 8)
                .asValues();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);

        Assert.assertEquals(rowList.size(), 8);

    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void parensAndUnionValues(final SyncLocalSession session) {

        final LocalDate now = LocalDate.now();

        final long startNanoSecond = System.nanoTime();

        final Values stmt;
        stmt = Postgres.valuesStmt()
                .parens(v -> v.values()
                        .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), now)
                                .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), now.plusDays(1))
                                .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), now.minusDays(3))
                                .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), now.minusDays(8))
                                .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                        ).orderBy(SQLs.refSelection("column2"), SQLs.refSelection(2)::desc)
                        .limit(SQLs::literal, 4)
                        .asValues()
                )
                .unionAll()
                .values()
                .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), now)
                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), now.plusDays(1))
                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), now.minusDays(3))
                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), now.minusDays(8))
                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                )
                .orderBy(SQLs.refSelection("column2"), SQLs.refSelection(2)::desc)
                .limit(SQLs::literal, 8)
                .asValues();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);

        Assert.assertEquals(rowList.size(), 8);

    }

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void simpleSubValues(final SyncLocalSession session) {

        final LocalDate now = LocalDate.now();

        final long startNanoSecond = System.nanoTime();

        final Select stmt;
        stmt = Postgres.query()
                .select(s -> s.space("v", PERIOD, ASTERISK))
                .from(Postgres.subValues()
                        .values()
                        .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), now)
                                .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), now.plusDays(1))
                                .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), now.minusDays(3))
                                .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), now.minusDays(8))
                                .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                        ).orderBy(SQLs.refSelection("column2"), SQLs.refSelection(2)::desc)
                        .limit(SQLs::literal, 4)
                        .asValues()
                ).as("v")
                .asQuery();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);

        Assert.assertEquals(rowList.size(), 4);
    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void parensAndUnionSubValues(final SyncLocalSession session) {

        final LocalDate now = LocalDate.now();

        final long startNanoSecond = System.nanoTime();

        final Select stmt;
        stmt = Postgres.query()
                .select(s -> s.space("v", PERIOD, ASTERISK))
                .from(Postgres.subValues()
                        .parens(v -> v.values()
                                .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), now)
                                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                                ).comma()
                                .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), now.plusDays(1))
                                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                                ).comma()
                                .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), now.minusDays(3))
                                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                                ).comma()
                                .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), now.minusDays(8))
                                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                                ).orderBy(SQLs.refSelection("column2"), SQLs.refSelection(2)::desc)
                                .limit(SQLs::literal, 4)
                                .asValues()
                        )
                        .unionAll()
                        .values()
                        .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), now)
                                .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), now.plusDays(1))
                                .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), now.minusDays(3))
                                .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), now.minusDays(8))
                                .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                        )
                        .orderBy(SQLs.refSelection("column2"), SQLs.refSelection(2)::desc)
                        .limit(SQLs::literal, 8)
                        .asValues()
                ).as("v")
                .asQuery();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);

        Assert.assertEquals(rowList.size(), 8);
    }

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void simpleValueUnionSelect(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(4);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Values stmt;
        stmt = Postgres.valuesStmt()
                .values()
                .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), now)
                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), now.plusDays(1))
                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), now.minusDays(3))
                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), now.minusDays(8))
                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                )
                .unionAll()
                .select(ChinaRegion_.population, ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.createTime)
                .comma(SQLs.literalValue(DayOfWeek.TUESDAY).as("week"), FALSE.as("myBoolean"))
                .comma(SQLs.literalValue(81).as("number"))
                .from(ChinaRegion_.T, AS, "t")
                .where(ChinaRegion_.id.in(SQLs::rowLiteral, extractRegionIdList(regionList)))
                .asQuery();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);

        Assert.assertEquals(rowList.size(), 8);
    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void parensSelectUnionValues(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(4);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Values stmt;
        stmt = Postgres.valuesStmt()
                .parens(v -> v.values()
                        .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), now)
                                .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), now.plusDays(1))
                                .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), now.minusDays(3))
                                .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), now.minusDays(8))
                                .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                        ).unionAll()
                        .select(ChinaRegion_.population, ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.createTime)
                        .comma(SQLs.literalValue(DayOfWeek.TUESDAY).as("week"), FALSE.as("myBoolean"))
                        .comma(SQLs.literalValue(81).as("number"))
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowLiteral, extractRegionIdList(regionList)))
                        .asQuery()
                ).unionAll()
                .values()
                .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), now)
                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), now.plusDays(1))
                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), now.minusDays(3))
                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), now.minusDays(8))
                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                ).asValues();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);

        Assert.assertEquals(rowList.size(), regionList.size() + 8);
    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void simpleUnionParens(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(4);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Values stmt;
        stmt = Postgres.valuesStmt()
                .values()
                .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), now)
                        .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), now.plusDays(1))
                        .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), now.minusDays(3))
                        .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                ).comma()
                .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), now.minusDays(8))
                        .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                )
                .unionAll()
                .parens(v -> v.values()
                        .parens(s -> s.space(1, "海问香", Decimals.valueOf("9999.88"), now)
                                .comma(DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(2, "大仓", Decimals.valueOf("9999.66"), now.plusDays(1))
                                .comma(DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(3, "卡拉肖克·玲", Decimals.valueOf("6666.88"), now.minusDays(3))
                                .comma(DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                        ).comma()
                        .parens(s -> s.space(4, "幽弥狂", Decimals.valueOf("8888.88"), now.minusDays(8))
                                .comma(DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                        ).unionAll()
                        .select(ChinaRegion_.population, ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.createTime)
                        .comma(SQLs.literalValue(DayOfWeek.TUESDAY).as("week"), FALSE.as("myBoolean"))
                        .comma(SQLs.literalValue(81).as("number"))
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowLiteral, extractRegionIdList(regionList)))
                        .asQuery()
                )
                .asValues();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);

        Assert.assertEquals(rowList.size(), regionList.size() + 8);
    }


}
