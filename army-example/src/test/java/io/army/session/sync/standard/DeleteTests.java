package io.army.session.sync.standard;


import io.army.criteria.BatchDelete;
import io.army.criteria.Delete;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import io.army.mapping.LongType;
import io.army.sync.SyncLocalSession;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

import static io.army.criteria.impl.SQLs.AND;
import static io.army.criteria.impl.SQLs.AS;

@Test(dataProvider = "localSessionProvider")
public class DeleteTests extends SessionSupport {


    @Test
    public void deleteParent(final SyncLocalSession session) {

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final Delete stmt;
        stmt = SQLs.singleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .asDelete();

        final long rows;
        rows = session.update(stmt);
        Assert.assertEquals(rows, regionList.size());
        LOG.debug("session[name : {}] rows {}", session.name(), rows);
    }


    @Test
    public void batchDeleteParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final BatchDelete stmt;
        stmt = SQLs.batchSingleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .asDelete()
                .namedParamList(extractRegionIdMapList(regionList));

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);
        assertBatchSingleRows(rowList, regionList.size(), 1);
        LOG.debug("session[name : {}] rows {}", session.name(), rowList);
    }


    @Test
    public void delete20Parent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);

        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final Delete stmt;
        stmt = SQLs.singleDelete20()
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
                .asDelete();

        final long rows;
        rows = session.update(stmt);
        Assert.assertEquals(rows, regionList.size());
        LOG.debug("session[name : {}] rows {}", session.name(), rows);
    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void batchDelete20Parent(final SyncLocalSession session) {

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final BatchDelete stmt;
        stmt = SQLs.batchSingleDelete20()
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
                .asDelete()
                .namedParamList(extractRegionIdMapList(regionList));

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);
        assertBatchSingleRows(rowList, regionList.size(), 1);
        LOG.debug("session[name : {}] rows {}", session.name(), rowList);
    }

    @Test
    public void qualifiedField(final SyncLocalSession session) {

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final Delete stmt;
        stmt = SQLs.singleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(SQLs.field("c", ChinaRegion_.id).in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(SQLs.field("c", ChinaRegion_.createTime)::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .asDelete();

        final long rows;
        rows = session.update(stmt);
        Assert.assertEquals(rows, regionList.size());
        LOG.debug("session[name : {}] rows {}", session.name(), rows);
    }



}
