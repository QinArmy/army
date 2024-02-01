package io.army.session.sync.standard;


import io.army.criteria.BatchUpdate;
import io.army.criteria.Expression;
import io.army.criteria.Update;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.session.OptimisticLockException;
import io.army.session.record.ResultStates;
import io.army.sync.SyncLocalSession;
import io.army.util._Collections;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static io.army.criteria.impl.SQLs.*;

@Test(dataProvider = "localSessionProvider")
public class DomainUpdateTests extends SessionSupport {

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void updateParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final BigDecimal gdpAmount = new BigDecimal("88888.66");
        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, gdpAmount, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .asUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);
        LOG.debug("session[name : {}] update {} rows", session.name(), rows);
    }

    @Test(invocationCount = 3, expectedExceptions = OptimisticLockException.class)
    // because first execution time contain class loading time and class initialization time
    public void updateParentWithOptimisticLock(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final BigDecimal gdpAmount = new BigDecimal("88888.66");
        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, gdpAmount, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .and(ChinaRegion_.version::equal, SQLs::param, 20) // error version
                .asUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);
        Assert.fail();
        Assert.assertEquals(rows, regionList.size());
    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
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
            map.put(ChinaRegion_.VERSION, region.getVersion());
            paramList.add(map);
        }

        final long startNanoSecond = System.nanoTime();

        final BatchUpdate stmt;
        stmt = SQLs.batchDomainUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .setSpace(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .where(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .and(ChinaRegion_.version::equal, SQLs::namedParam)
                .asUpdate()
                .namedParamList(paramList);

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);

        Assert.assertEquals(rowList.size(), paramList.size());


    }


    @Test(invocationCount = 3, expectedExceptions = OptimisticLockException.class)
    // because first execution time contain class loading time and class initialization time
    public void batchUpdateParentWithOptimisticLock(final SyncLocalSession session) {
        final int rowSize = 3, lastIndex = rowSize - 1;
        final List<ChinaRegion<?>> regionList = createReginListWithCount(rowSize);
        session.batchSave(regionList);

        final Random random = ThreadLocalRandom.current();
        final LocalDateTime now = LocalDateTime.now();

        final List<Map<String, Object>> paramList = new ArrayList<>(rowSize);
        Map<String, Object> map;

        ChinaRegion<?> region;
        for (int i = 0; i < rowSize; i++) {
            region = regionList.get(i);
            map = new HashMap<>(5);

            map.put(ChinaRegion_.ID, region.getId());
            map.put(ChinaRegion_.REGION_GDP, randomDecimal(random));
            if (i < lastIndex) {
                map.put(ChinaRegion_.VERSION, region.getVersion());
            } else {
                map.put(ChinaRegion_.VERSION, 20);
            }

            paramList.add(map);
        }

        final long startNanoSecond = System.nanoTime();

        final BatchUpdate stmt;
        stmt = SQLs.batchDomainUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .setSpace(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .where(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .and(ChinaRegion_.version::equal, SQLs::namedParam)
                .asUpdate()
                .namedParamList(paramList);

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);

        Assert.fail();
        Assert.assertEquals(rowList.size(), paramList.size());


    }

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void batchUpdateParentAsStates(final SyncLocalSession session) {
        final int rowSize = 3, lastIndex = rowSize - 1;
        final List<ChinaRegion<?>> regionList = createReginListWithCount(rowSize);
        session.batchSave(regionList);

        final Random random = ThreadLocalRandom.current();
        final LocalDateTime now = LocalDateTime.now();

        final List<Map<String, Object>> paramList = new ArrayList<>(rowSize);
        Map<String, Object> map;

        ChinaRegion<?> region;
        for (int i = 0; i < rowSize; i++) {
            region = regionList.get(i);
            map = new HashMap<>(5);

            map.put(ChinaRegion_.ID, region.getId());
            map.put(ChinaRegion_.REGION_GDP, randomDecimal(random));
            map.put(ChinaRegion_.VERSION, region.getVersion());

            paramList.add(map);
        }


        final BatchUpdate stmt;
        stmt = SQLs.batchDomainUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .setSpace(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .where(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .and(ChinaRegion_.version::equal, SQLs::namedParam)
                .asUpdate()
                .namedParamList(paramList);

        final List<ResultStates> rowList;

        rowList = session.batchUpdateAsStates(stmt)
                .collect(Collectors.toCollection(_Collections::arrayList));


        Assert.assertEquals(rowSize, paramList.size());


        ResultStates states;
        for (int i = 0; i < rowSize; i++) {
            states = rowList.get(i);

            if (i < lastIndex) {
                Assert.assertTrue(states.hasMoreResult());
            } else {
                Assert.assertFalse(states.hasMoreResult());
            }
            Assert.assertFalse(states.hasColumn());
            Assert.assertFalse(states.hasMoreFetch());
        }


    }

    @Test(invocationCount = 3, expectedExceptions = OptimisticLockException.class)
    // because first execution time contain class loading time and class initialization time
    public void batchUpdateParentAsStatesWithOptimisticLock(final SyncLocalSession session) {
        final int rowSize = 3, lastIndex = rowSize - 1;
        final List<ChinaRegion<?>> regionList = createReginListWithCount(rowSize);
        session.batchSave(regionList);

        final Random random = ThreadLocalRandom.current();
        final LocalDateTime now = LocalDateTime.now();

        final List<Map<String, Object>> paramList = new ArrayList<>(rowSize);
        Map<String, Object> map;

        ChinaRegion<?> region;
        for (int i = 0; i < rowSize; i++) {
            region = regionList.get(i);
            map = new HashMap<>(5);

            map.put(ChinaRegion_.ID, region.getId());
            map.put(ChinaRegion_.REGION_GDP, randomDecimal(random));
            if (i < lastIndex) {
                map.put(ChinaRegion_.VERSION, region.getVersion());
            } else {
                map.put(ChinaRegion_.VERSION, 20);
            }

            paramList.add(map);
        }


        final BatchUpdate stmt;
        stmt = SQLs.batchDomainUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .setSpace(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .where(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .and(ChinaRegion_.version::equal, SQLs::namedParam)
                .asUpdate()
                .namedParamList(paramList);

        final List<ResultStates> rowList;
        rowList = session.batchUpdateAsStates(stmt)
                .collect(Collectors.toCollection(_Collections::arrayList));

        Assert.fail();
        Assert.assertNotNull(rowList);

    }

    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
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
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, gdpAmount, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .and(ChinaProvince_.governor.isNotNull())
                .asUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);
        assertDomainUpdateChildRows(session, rows, regionList.size());

    }

    @Transactional
    @Test(invocationCount = 3, expectedExceptions = OptimisticLockException.class)
    // because first execution time contain class loading time and class initialization time
    public void updateChildWithOptimisticLock(final SyncLocalSession session) {
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
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, gdpAmount, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .and(ChinaProvince_.governor.isNotNull())
                .and(ChinaRegion_.version::equal, SQLs::param, 20) // error version
                .asUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);
        Assert.fail();
        assertDomainUpdateChildRows(session, rows, regionList.size());

    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
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
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .asUpdate()
                .namedParamList(paramList);

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);

        assertBatchDomainUpdateChildRows(session, rowList, regionList.size(), 1);
        LOG.debug("session[name : {}] update {} rows", session.name(), rowList);

    }

    @Transactional
    @Test(invocationCount = 3, expectedExceptions = OptimisticLockException.class)
    // because first execution time contain class loading time and class initialization time
    public void batchUpdateChildWithOptimisticLock(final SyncLocalSession session) {
        final int rowSize = 3, lastIndex = rowSize - 1;
        final List<ChinaProvince> regionList = createProvinceListWithCount(rowSize);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();
        final Random random = ThreadLocalRandom.current();

        final List<Map<String, Object>> paramList = new ArrayList<>(regionList.size());
        Map<String, Object> map;

        ChinaProvince province;
        for (int i = 0; i < rowSize; i++) {
            province = regionList.get(i);

            map = new HashMap<>();

            map.put(ChinaRegion_.ID, province.getId());
            map.put(ChinaRegion_.REGION_GDP, randomDecimal(random));
            map.put(ChinaProvince_.GOVERNOR, randomPerson(random));

            if (i < lastIndex) {
                map.put(ChinaRegion_.VERSION, province.getVersion());
            } else {
                map.put(ChinaRegion_.VERSION, 20); // error version
            }
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
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .and(ChinaRegion_.version::equal, SQLs::namedParam)
                .asUpdate()
                .namedParamList(paramList);

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);
        Assert.fail();

        assertBatchDomainUpdateChildRows(session, rowList, regionList.size(), 1);

    }


    @Transactional
    @Test(invocationCount = 3, expectedExceptions = OptimisticLockException.class)
    // because first execution time contain class loading time and class initialization time
    public void batchUpdateChildAsStatesWithOptimisticLock(final SyncLocalSession session) {
        final int rowSize = 3, lastIndex = rowSize - 1;
        final List<ChinaProvince> regionList = createProvinceListWithCount(rowSize);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();
        final Random random = ThreadLocalRandom.current();

        final List<Map<String, Object>> paramList = new ArrayList<>(regionList.size());
        Map<String, Object> map;

        ChinaProvince province;
        for (int i = 0; i < rowSize; i++) {
            province = regionList.get(i);

            map = new HashMap<>();

            map.put(ChinaRegion_.ID, province.getId());
            map.put(ChinaRegion_.REGION_GDP, randomDecimal(random));
            map.put(ChinaProvince_.GOVERNOR, randomPerson(random));

            if (i < lastIndex) {
                map.put(ChinaRegion_.VERSION, province.getVersion());
            } else {
                map.put(ChinaRegion_.VERSION, 20); // error version
            }
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
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .and(ChinaRegion_.version::equal, SQLs::namedParam)
                .asUpdate()
                .namedParamList(paramList);

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<ResultStates> rowList;
        rowList = session.batchUpdateAsStates(stmt)
                .collect(Collectors.toCollection(_Collections::arrayList));

        Assert.fail();
        Assert.assertEquals(rowList.size(), regionList.size());

    }


}
