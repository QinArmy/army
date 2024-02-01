package io.army.session.sync.mysql;


import io.army.criteria.BatchUpdate;
import io.army.criteria.Expression;
import io.army.criteria.Update;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.*;
import io.army.sync.SyncLocalSession;
import io.army.util.Decimals;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static io.army.criteria.impl.SQLs.*;


/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/update.html">UPDATE Statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html">MySQL 8.0 Optimizer Hints</a>
 */
@Test(dataProvider = "localSessionProvider")
public class MultiUpdateTests extends SessionTestSupport {


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void simpleUpdate(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(3);
        session.batchSave(regionList);

        final BigDecimal gdpAmount = Decimals.valueOf("88888.66");
        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Update stmt;
        stmt = MySQLs.multiUpdate()
                .with("cte").as(sw -> sw.select(ChinaRegion_.id)
                        .from(ChinaRegion_.T, AS, "c")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                        .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.PROVINCE)
                        .asQuery()
                ).space()
                .update(ChinaProvince_.T, AS, "p").useIndex(FOR, JOIN, "PRIMARY")
                .join(ChinaRegion_.T, AS, "c").useIndex(FOR, JOIN, "PRIMARY").on(ChinaRegion_.id::equal, ChinaProvince_.id)
                .join("cte").on(ChinaRegion_.id::equal, SQLs.refField("cte", ChinaRegion_.ID))
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .where(ChinaRegion_.id::in, SQLs.subQuery()
                        .select(s -> s.space(SQLs.refField("subCte", ChinaRegion_.ID)))
                        .from("cte", AS, "subCte")
                        ::asQuery
                )
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, gdpAmount, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .asUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), regionList.size());

    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void batchUpdate(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(3);
        session.batchSave(regionList);

        final BigDecimal gdpAmount = Decimals.valueOf("88888.66");
        final LocalDateTime now = LocalDateTime.now();

        final List<Map<String, Long>> idMapList = extractRegionIdMapList(regionList);

        final long startNanoSecond = System.nanoTime();

        final BatchUpdate stmt;
        stmt = MySQLs.batchMultiUpdate()
                .with("cte").as(sw -> sw.select(ChinaRegion_.id)
                        .from(ChinaRegion_.T, AS, "c")
                        .where(ChinaRegion_.id::equal, SQLs::namedParam)
                        .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.PROVINCE)
                        .asQuery()
                ).space()
                .update(ChinaProvince_.T, AS, "p").useIndex(FOR, JOIN, "PRIMARY")
                .join(ChinaRegion_.T, AS, "c").useIndex(FOR, JOIN, "PRIMARY").on(ChinaRegion_.id::equal, ChinaProvince_.id)
                .join("cte").on(ChinaRegion_.id::equal, SQLs.refField("cte", ChinaRegion_.ID))
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .where(ChinaRegion_.id::equal, SQLs.scalarSubQuery()
                        .select(s -> s.space(SQLs.refField("subCte", ChinaRegion_.ID)))
                        .from("cte", AS, "subCte")
                        ::asQuery
                )
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, gdpAmount, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .asUpdate()
                .namedParamList(idMapList);

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);
        assertBatchSingleRows(rowList, idMapList.size(), 1);

    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void updateSelf(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final BigDecimal gdpAmount = Decimals.valueOf("88888.66");
        final LocalDateTime now = LocalDateTime.now();

        final List<Long> idList = extractRegionIdList(regionList);

        final long startNanoSecond = System.nanoTime();

        final Update stmt;
        stmt = MySQLs.multiUpdate()
                .update(ChinaRegion_.T, AS, "c").useIndex(FOR, JOIN, "PRIMARY")
                .join(SQLs.subQuery()
                        .select(ChinaRegion_.id)
                        .from(ChinaRegion_.T, AS, "ss")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, idList))
                        .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                        .asQuery()
                ).as("sc").on(ChinaRegion_.id::equal, SQLs.refField("sc", ChinaRegion_.ID))
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .where(ChinaRegion_.id.in(SQLs::rowParam, idList))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, gdpAmount, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .asUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);
        Assert.assertEquals(rows, idList.size());
    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void hintAndModifier(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(3);
        session.batchSave(regionList);

        final BigDecimal gdpAmount = Decimals.valueOf("88888.66");
        final LocalDateTime now = LocalDateTime.now();


        final long startNanoSecond = System.nanoTime();

        final Supplier<List<Hint>> hintSupplier = () -> Collections.singletonList(MySQLs.setVar("foreign_key_checks=OFF"));

        final Update stmt;
        stmt = MySQLs.multiUpdate()
                .with("cte").as(sw -> sw.select(ChinaRegion_.id)
                        .from(ChinaRegion_.T, AS, "c")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                        .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.PROVINCE)
                        .asQuery()
                ).space()
                .update(hintSupplier, Collections.singletonList(MySQLs.LOW_PRIORITY))
                .space(ChinaProvince_.T, AS, "p").useIndex(FOR, JOIN, "PRIMARY")
                .join(ChinaRegion_.T, AS, "c").useIndex(FOR, JOIN, "PRIMARY").on(ChinaRegion_.id::equal, ChinaProvince_.id)
                .join("cte").on(ChinaRegion_.id::equal, SQLs.refField("cte", ChinaRegion_.ID))
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .where(ChinaRegion_.id::in, SQLs.subQuery()
                        .select(s -> s.space(SQLs.refField("subCte", ChinaRegion_.ID)))
                        .from("cte", AS, "subCte")
                        ::asQuery
                )
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, gdpAmount, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .asUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), regionList.size());
    }


}
