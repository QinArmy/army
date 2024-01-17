package io.army.session.sync.standard;


import io.army.criteria.BatchDelete;
import io.army.criteria.Delete;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sync.SyncLocalSession;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

import static io.army.criteria.impl.SQLs.AND;
import static io.army.criteria.impl.SQLs.AS;

@Test(dataProvider = "localSessionProvider")
public class DomainDeleteTests extends StandardSessionSupport {


    @Test
    public void deleteParent(final SyncLocalSession session) {

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final Delete stmt;
        stmt = SQLs.domainDelete()
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
        stmt = SQLs.batchDomainDelete()
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

    @Transactional
    @Test
    public void deleteChild(final SyncLocalSession session) {

        final List<ChinaProvince> regionList = createProvinceListWithCount(3);

        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final Delete stmt;
        stmt = SQLs.domainDelete()
                .deleteFrom(ChinaProvince_.T, AS, "p")
                .where(ChinaProvince_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .asDelete();

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, regionList.size());
        LOG.debug("session[name : {}] rows {}", session.name(), rows);


    }

    @Transactional
    @Test
    public void batchDeleteChild(final SyncLocalSession session) {

        final List<ChinaProvince> regionList = createProvinceListWithCount(3);

        session.batchSave(regionList);

        final LocalDateTime now = LocalDateTime.now();

        final BatchDelete stmt;
        stmt = SQLs.batchDomainDelete()
                .deleteFrom(ChinaProvince_.T, AS, "p")
                .where(ChinaProvince_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now.plusSeconds(1))
                .asDelete()
                .namedParamList(extractRegionIdMapList(regionList));

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);
        assertBatchUpdateChildRows(rowList, regionList.size(), 1);
        LOG.debug("session[name : {}] rows {}", session.name(), rowList);
    }


}
