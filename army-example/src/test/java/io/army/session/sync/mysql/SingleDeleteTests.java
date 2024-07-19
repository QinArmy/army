package io.army.session.sync.mysql;


import io.army.criteria.BatchDelete;
import io.army.criteria.Delete;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import io.army.mapping.LongType;
import io.army.session.SyncLocalSession;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static io.army.criteria.impl.SQLs.AND;
import static io.army.criteria.impl.SQLs.AS;


/**
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/optimizer-hints.html">MySQL 5.7 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/delete.html">DELETE Statement</a>
 */
@Test(dataProvider = "localSessionProvider")
public class SingleDeleteTests extends SessionTestSupport {


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void deleteParent(final SyncLocalSession session) {

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Delete stmt;
        stmt = MySQLs.singleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .orderBy(ChinaRegion_.id)
                .limit(SQLs::param, regionList.size())
                .asDelete();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);
        Assert.assertEquals(rows, regionList.size());
        LOG.debug("session[name : {}] rows {}", session.name(), rows);
    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void batchDeleteParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final BatchDelete stmt;
        stmt = MySQLs.batchSingleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id::spaceEqual, SQLs::namedParam)
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .orderBy(ChinaRegion_.id)
                .limit(SQLs::param, regionList.size())
                .asDelete()
                .namedParamList(extractRegionIdMapList(regionList));

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);
        assertBatchSingleRows(rowList, regionList.size(), 1);
        LOG.debug("session[name : {}] rows {}", session.name(), rowList);
    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void delete20Parent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);

        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Delete stmt;
        stmt = MySQLs.singleDelete()
                .with("idListCte").as(c -> c.select(ChinaRegion_.id)
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                        .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                        .asQuery()
                ).space()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id::in, SQLs.subQuery()
                        .select(s -> s.space(SQLs.refField("cte", "id")))
                        .from("idListCte", AS, "cte")
                        ::asQuery
                )
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .orderBy(ChinaRegion_.id)
                .limit(SQLs::param, regionList.size())
                .asDelete();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);
        Assert.assertEquals(rows, regionList.size());
        LOG.debug("session[name : {}] rows {}", session.name(), rows);
    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void batchDeleteParentStaticWithClause(final SyncLocalSession session) {

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final BatchDelete stmt;
        stmt = MySQLs.batchSingleDelete()
                .with("cte").as(c -> c.select(ChinaRegion_.id)
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                        .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                        .asQuery()
                ).space()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id::equal, SQLs.scalarSubQuery()
                        .select(s -> s.space(SQLs.refField("cte", "id")))
                        .from("cte")
                        .where(SQLs.refField("cte", "id").equal(SQLs.namedParam(LongType.INSTANCE, "id")))
                        ::asQuery
                )
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .orderBy(ChinaRegion_.id)
                .limit(SQLs::param, regionList.size())
                .asDelete()
                .namedParamList(extractRegionIdMapList(regionList));

        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);
        assertBatchSingleRows(rowList, regionList.size(), 1);
        LOG.debug("session[name : {}] rows {}", session.name(), rowList);
    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void hintAndModifier(final SyncLocalSession session) {

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Supplier<List<Hint>> hintSupplier = () -> Collections.singletonList(MySQLs.setVar("foreign_key_checks=OFF"));

        final Delete stmt;
        stmt = MySQLs.singleDelete()
                .delete(hintSupplier, Collections.singletonList(MySQLs.LOW_PRIORITY))
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .orderBy(ChinaRegion_.id)
                .limit(SQLs::param, regionList.size())
                .asDelete();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);
        Assert.assertEquals(rows, regionList.size());
        LOG.debug("session[name : {}] rows {}", session.name(), rows);
    }


}
