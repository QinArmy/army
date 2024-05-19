package io.army.session.sync.postgre;


import io.army.criteria.Expression;
import io.army.criteria.Update;
import io.army.criteria.dialect.ReturningUpdate;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._ReturningDml;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.record.ResultStates;
import io.army.sync.SyncLocalSession;
import io.army.sync.SyncStmtOption;
import io.army.util.Decimals;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

import static io.army.criteria.impl.SQLs.*;

@Test(dataProvider = "localSessionProvider")
public class UpdateTests extends SessionTestSupport {


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void updateParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final BigDecimal amount = Decimals.valueOf("8866.66");
        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Update stmt;
        stmt = Postgres.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, amount)
                .set(ChinaRegion_.population, SQLs::plusEqual, SQLs::param, 8888)
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(1), AND, now.plusSeconds(1))
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, amount, Expression::greaterEqual, SQLs.LITERAL_DECIMAL_0)
                .asUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertFalse(stmt instanceof _ReturningDml);

        Assert.assertEquals(session.update(stmt), regionList.size());

    }

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void updateParentStates(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final BigDecimal amount = Decimals.valueOf("8866.66");
        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Update stmt;
        stmt = Postgres.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, amount)
                .set(ChinaRegion_.population, SQLs::plusEqual, SQLs::param, 8888)
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(1), AND, now.plusSeconds(1))
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, amount, Expression::greaterEqual, SQLs.LITERAL_DECIMAL_0)
                .asUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final ResultStates states;
        states = session.updateAsStates(stmt);

        Assert.assertEquals(states.affectedRows(), regionList.size());
        Assert.assertFalse(states.hasColumn());
        Assert.assertFalse(states.inTransaction());
        Assert.assertEquals(states.batchSize(), 0);

        Assert.assertEquals(states.batchNo(), 0);
        Assert.assertFalse(states.hasMoreResult());
        Assert.assertFalse(states.hasMoreFetch());

    }

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void returningUpdateParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final BigDecimal amount = Decimals.valueOf("8866.66");
        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final ReturningUpdate stmt;
        stmt = Postgres.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, amount)
                .set(ChinaRegion_.population, SQLs::plusEqual, SQLs::param, 8888)
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(1), AND, now.plusSeconds(1))
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, amount, Expression::greaterEqual, SQLs.LITERAL_DECIMAL_0)
                .returningAll()
                .asReturningUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final boolean[] flagsHolder = new boolean[]{false};
        final Consumer<ResultStates> statesConsumer;
        statesConsumer = states -> {
            flagsHolder[0] = true;

            Assert.assertEquals(states.affectedRows(), regionList.size());
            Assert.assertTrue(states.hasColumn());
            Assert.assertFalse(states.inTransaction());
            Assert.assertEquals(states.batchSize(), 0);

            Assert.assertEquals(states.batchNo(), 0);
            Assert.assertFalse(states.hasMoreResult());
            Assert.assertFalse(states.hasMoreFetch());

        };

        final List<ChinaRegion<?>> rowList;
        rowList = session.queryList(stmt, ChinaRegion_.CLASS, SyncStmtOption.stateConsumer(statesConsumer));

        Assert.assertEquals(rowList.size(), regionList.size());
        Assert.assertTrue(flagsHolder[0]);

        for (ChinaRegion<?> region : rowList) {
            Assert.assertNotNull(region.getRegionGdp());
            Assert.assertNotNull(region.getPopulation());
        }

    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void updateChildBeforeParent(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(6);
        session.batchSave(regionList);

        final List<Long> idList = extractRegionIdList(regionList);

        final long startNanoSecond = System.nanoTime();

        final Update stmt;
        stmt = Postgres.singleUpdate()
                .with("first_child_cte").as(sw -> sw.update(ChinaProvince_.T, AS, "p")
                        .set(ChinaProvince_.governor, SQLs::param, randomPerson())
                        .from(ChinaRegion_.T, AS, "c")
                        .where(ChinaProvince_.id.in(SQLs::rowParam, idList.subList(0, 3)))
                        .and(ChinaProvince_.id::equal, ChinaRegion_.id)
                        .returning(SQLs.field("p", ChinaProvince_.id).as("myId"))
                        .asReturningUpdate()
                ).comma("first_parent_cte").as(sw -> sw.update(ChinaRegion_.T, AS, "c")
                        .set(ChinaRegion_.updateTime, UPDATE_TIME_PARAM_PLACEHOLDER)
                        .from("first_child_cte")
                        .where(ChinaRegion_.id::equal, SQLs.refField("first_child_cte", "myId"))
                        .asUpdate()
                ).comma("child_cte").as(sw -> sw.update(ChinaProvince_.T, AS, "p")
                        .set(ChinaProvince_.governor, SQLs::param, randomPerson())
                        .from(ChinaRegion_.T, AS, "c")
                        .where(ChinaProvince_.id.in(SQLs::rowParam, idList.subList(3, 6)))
                        .and(ChinaProvince_.id::equal, ChinaRegion_.id)
                        .returning(ChinaProvince_.id)
                        .asReturningUpdate()
                ).space()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.updateTime, UPDATE_TIME_PARAM_PLACEHOLDER)
                .from("child_cte")
                .where(ChinaRegion_.id::equal, SQLs.refField("child_cte", ChinaRegion_.ID))
                .asUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);
        Assert.assertEquals(session.update(stmt), 3L);
    }

    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void updateChildAfterParent(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(6);
        session.batchSave(regionList);

        final List<Long> idList = extractRegionIdList(regionList);

        final long startNanoSecond = System.nanoTime();

        final Update stmt;
        stmt = Postgres.singleUpdate()
                .with("first_parent_cte").as(sw -> sw.update(ChinaRegion_.T, AS, "p")
                        .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, randomDecimal())
                        .where(ChinaRegion_.id.in(SQLs::rowParam, idList.subList(0, 3)))
                        .returning(SQLs.field("p", ChinaRegion_.id).as("myId"))
                        .asReturningUpdate()
                ).comma("first_child_cte").as(sw -> sw.update(ChinaProvince_.T, AS, "c")
                        .set(ChinaProvince_.governor, SQLs::param, randomPerson())
                        .from("first_parent_cte")
                        .where(ChinaProvince_.id::equal, SQLs.refField("first_parent_cte", "myId"))
                        .asUpdate()
                ).comma("parent_cte").as(sw -> sw.update(ChinaRegion_.T, AS, "p")
                        .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, randomDecimal())
                        .where(ChinaRegion_.id.in(SQLs::rowParam, idList.subList(3, 6)))
                        .returning(ChinaRegion_.id)
                        .asReturningUpdate()
                ).space()
                .update(ChinaProvince_.T, AS, "c")
                .set(ChinaProvince_.governor, SQLs::param, randomPerson())
                .from("parent_cte")
                .where(ChinaProvince_.id::equal, SQLs.refField("parent_cte", ChinaProvince_.ID))
                .asUpdate();

        statementCostTimeLog(session, LOG, startNanoSecond);
        Assert.assertEquals(session.update(stmt), 3L);
    }


}
