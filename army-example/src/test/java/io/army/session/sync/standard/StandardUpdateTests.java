package io.army.session.sync.standard;


import io.army.criteria.BatchUpdate;
import io.army.criteria.Expression;
import io.army.criteria.Update;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import io.army.sync.SyncLocalSession;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static io.army.criteria.impl.SQLs.AND;
import static io.army.criteria.impl.SQLs.AS;

@Test(dataProvider = "localSessionProvider")
public class StandardUpdateTests extends StandardSessionSupport {


    @Test(invocationCount = 3)
    public void updateParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final BigDecimal gdpAmount = new BigDecimal("88888.66");
        final LocalDateTime now = LocalDateTime.now();

        final Update stmt;
        stmt = SQLs.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, gdpAmount, Expression::greaterEqual, BigDecimal.ZERO)
                .asUpdate();

        final long rows;
        rows = session.update(stmt);
        Assert.assertEquals(rows, regionList.size());

    }


    @Test(invocationCount = 3)
    public void batchUpdateParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);


        final List<Map<String, Object>> paramList = new ArrayList<>(regionList.size());

        final Random random = ThreadLocalRandom.current();
        Map<String, Object> map;

        for (ChinaRegion<?> region : regionList) {
            map = new HashMap<>();
            map.put(ChinaRegion_.ID, region.getId());
            map.put(ChinaRegion_.REGION_GDP, randomDecimal(random));
            paramList.add(map);
        }

        final LocalDateTime now = LocalDateTime.now();

        final BatchUpdate stmt;
        stmt = SQLs.batchSingleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .setSpace(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .where(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, BigDecimal.ZERO)
                .asUpdate()
                .namedParamList(paramList);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);
        assertBatchSingleRows(rowList, regionList.size(), 1);
    }

    @Test(invocationCount = 3)
    public void dynamicUpdateParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final ChinaRegion<?> criteria = new ChinaRegion<>();
        criteria.setRegionGdp(new BigDecimal("88888.66"));

        final Update stmt;
        stmt = SQLs.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.population, SQLs::plusEqual, SQLs::param, 999)
                .ifSet(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, criteria::getRegionGdp)
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .ifAnd(ChinaRegion_.regionGdp::plus, SQLs::param, criteria.getRegionGdp(), Expression::greaterEqual, BigDecimal.ZERO)
                .asUpdate();

        final long rows;
        rows = session.update(stmt);
        Assert.assertEquals(rows, regionList.size());

    }


    @Test(invocationCount = 3)
    public void dynamicWhereUpdateParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final ChinaRegion<?> criteria = new ChinaRegion<>();
        criteria.setRegionGdp(new BigDecimal("88888.66"));

        final Update stmt;
        stmt = SQLs.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.population, SQLs::plusEqual, SQLs::param, 999)
                .ifSet(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, criteria::getRegionGdp)
                .where(c -> {
                    c.accept(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)));
                    c.accept(ChinaRegion_.createTime.between(SQLs::param, now.minusMinutes(10), AND, now));
                    if (criteria.getRegionGdp() != null) {
                        c.accept(ChinaRegion_.regionGdp.plus(SQLs::param, criteria.getRegionGdp()).greaterEqual(SQLs.literalValue(BigDecimal.ZERO)));
                    }
                })
                .asUpdate();

        final long rows;
        rows = session.update(stmt);
        Assert.assertEquals(rows, regionList.size());
    }

    @Test(invocationCount = 3)
    public void update20Parent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final BigDecimal gdpAmount = new BigDecimal("88888.66");
        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Update stmt;
        stmt = SQLs.singleUpdate20()
                .with("cte").as(c -> c.select(ChinaRegion_.id)
                        .from(ChinaRegion_.T, AS, "c")
                        .where(ChinaRegion_.id::in, SQLs.SPACE, SQLs::rowParam, extractRegionIdList(regionList))
                        .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                        .asQuery()
                ).space()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .where(ChinaRegion_.id::in, SQLs.subQuery()
                        .select(s -> s.space(SQLs.refField("cte", "id")))
                        .from("cte")
                        ::asQuery
                )
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, gdpAmount, Expression::greaterEqual, BigDecimal.ZERO)
                .asUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), regionList.size());
    }


}
