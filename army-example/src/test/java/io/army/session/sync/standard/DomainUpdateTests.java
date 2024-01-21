package io.army.session.sync.standard;


import io.army.criteria.BatchUpdate;
import io.army.criteria.Expression;
import io.army.criteria.Update;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sync.SyncLocalSession;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static io.army.criteria.impl.SQLs.AND;
import static io.army.criteria.impl.SQLs.AS;

@Test(dataProvider = "localSessionProvider")
public class DomainUpdateTests extends StandardSessionSupport {

    @Test(invocationCount = 3)
    public void updateParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final BigDecimal gdpAmount = new BigDecimal("88888.66");
        final LocalDateTime now = LocalDateTime.now();

        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, gdpAmount, Expression::greaterEqual, BigDecimal.ZERO)
                .asUpdate();

        final long rows;
        rows = session.update(stmt);
        LOG.debug("session[name : {}] update {} rows", session.name(), rows);
    }


    @Test(invocationCount = 3)
    public void batchUpdateParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final Random random = ThreadLocalRandom.current();
        final LocalDateTime now = LocalDateTime.now();

        final List<Map<String, Object>> paramList = new ArrayList<>(regionList.size());
        Map<String, Object> map;

        for (ChinaRegion<?> region : regionList) {
            map = new HashMap<>(5);
            map.put(ChinaRegion_.ID, region.getId());
            map.put(ChinaRegion_.REGION_GDP, randomDecimal(random));
            paramList.add(map);
        }


        final BatchUpdate stmt;
        stmt = SQLs.batchDomainUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .setSpace(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .where(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, BigDecimal.ZERO)
                .asUpdate()
                .namedParamList(paramList);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);

        Assert.assertEquals(rowList.size(), paramList.size());
        LOG.debug("session[name : {}] update {} rows", session.name(), rowList);

    }

    @Transactional
    @Test(invocationCount = 3)
    public void updateChild(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(3);
        session.batchSave(regionList);

        final BigDecimal gdpAmount = new BigDecimal("88888.66");
        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(ChinaProvince_.T, AS, "p")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .set(ChinaProvince_.governor, SQLs::param, randomPerson())
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, gdpAmount, Expression::greaterEqual, BigDecimal.ZERO)
                .and(ChinaProvince_.governor.isNotNull())
                .asUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);
        assertDomainUpdateChildRows(session, rows, regionList.size());

    }


    @Transactional
    @Test(invocationCount = 3)
    public void batchUpdateChild(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(3);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();
        final Random random = ThreadLocalRandom.current();

        final List<Map<String, Object>> paramList = new ArrayList<>(regionList.size());
        Map<String, Object> map;

        for (ChinaProvince province : regionList) {
            map = new HashMap<>();
            map.put(ChinaRegion_.ID, province.getId());
            map.put(ChinaRegion_.REGION_GDP, randomDecimal(random));
            map.put(ChinaProvince_.GOVERNOR, randomPerson(random));

            paramList.add(map);
        }

        final long startNanoSecond = System.nanoTime();

        final BatchUpdate stmt;
        stmt = SQLs.batchDomainUpdate()
                .update(ChinaProvince_.T, AS, "p")
                .setSpace(ChinaProvince_.governor, SQLs::namedParam)
                .setSpace(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .where(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, BigDecimal.ZERO)
                .asUpdate()
                .namedParamList(paramList);

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);

        assertBatchDomainUpdateChildRows(session, rowList, regionList.size(), 1);
        LOG.debug("session[name : {}] update {} rows", session.name(), rowList);

    }


}
