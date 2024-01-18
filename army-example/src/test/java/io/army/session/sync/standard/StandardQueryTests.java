package io.army.session.sync.standard;


import com.alibaba.fastjson2.JSON;
import io.army.criteria.BatchSelect;
import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.Windows;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sync.SyncLocalSession;
import io.army.util.RowMaps;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static io.army.criteria.impl.SQLs.*;

@Test(dataProvider = "localSessionProvider")
public class StandardQueryTests extends StandardSessionSupport {


    @Transactional
    @Test(invocationCount = 3)
    public void query20WindowClause(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(3);
        session.batchSave(regionList);

        final List<Long> regionIdList = extractRegionIdList(regionList);

        final long startNanoSecond = System.nanoTime();

        final Select stmt;
        stmt = SQLs.query20()
                .with("cte").as(s -> s.select(ChinaRegion_.id)
                        .from(ChinaRegion_.T, AS, "c")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, regionIdList))
                        .asQuery()
                )
                .space()
                .select(ChinaProvince_.id, Windows.sum(ChinaRegion_.regionGdp).over("w").as("gdpSum"))
                .from(ChinaProvince_.T, AS, "p")
                .join(ChinaRegion_.T, AS, "c").on(ChinaProvince_.id::equal, ChinaRegion_.id)
                .where(ChinaProvince_.id::in, SQLs.subQuery()
                        .select(s -> s.space(SQLs.refField("cte", ChinaRegion_.ID)))
                        .from("cte")
                        ::asQuery
                )
                .window("w").as(s -> s.partitionBy(ChinaRegion_.name).orderBy(ChinaRegion_.regionGdp::desc).rows(UNBOUNDED_PRECEDING))
                .orderBy(ChinaProvince_.id)
                .limit(SQLs::param, regionIdList.size())
                .asQuery();

        statementCostTimeLog(session, LOG, startNanoSecond);


        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);
        Assert.assertEquals(rowList.size(), regionIdList.size());

        LOG.debug("{} rowList : \n{}", session.name(), JSON.toJSONString(rowList));
    }


    @Transactional
    @Test(invocationCount = 3)
    public void query20FullClause(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(3);
        session.batchSave(regionList);

        final List<Long> regionIdList = extractRegionIdList(regionList);
        final LocalDateTime now = LocalDateTime.now();

        final long startNanoSecond = System.nanoTime();

        final Select stmt;
        stmt = SQLs.query20()
                .with("cte").as(s -> s.select(ChinaRegion_.id, SQLs.countAsterisk().as("count"))
                        .from(ChinaRegion_.T, AS, "c")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, regionIdList))
                        .groupBy(ChinaRegion_.id)
                        .having(ChinaRegion_.id.greater(SQLs::param, 0))
                        .asQuery()
                ).comma("cte_order").as(c -> c.select(s -> s.space("cte", PERIOD, ASTERISK)
                                .comma(Windows.rowNumber().over().as("rowNumber")))
                        .from("cte")
                        .asQuery()
                )
                .space()
                .select(ChinaRegion_.population.plus(SQLs::param, 1).as("populationPlus"), Windows.sum(ChinaRegion_.regionGdp).over("w").as("gdpSum"))
                .comma("c", PERIOD, ChinaRegion_.T, "p", PERIOD, ChinaProvince_.T)
                .from(ChinaProvince_.T, AS, "p")
                .join(ChinaRegion_.T, AS, "c").on(ChinaProvince_.id::equal, ChinaRegion_.id)
                .where(ChinaProvince_.id::in, SQLs.subQuery()
                        .select(s -> s.space(SQLs.refField("cte_order", ChinaRegion_.ID)))
                        .from("cte_order")
                        ::asQuery
                )
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(1), AND, now.plusSeconds(1))
                .window("w").as(s -> s.partitionBy(ChinaRegion_.name).orderBy(ChinaRegion_.regionGdp::desc).rows(UNBOUNDED_PRECEDING))
                .orderBy(ChinaProvince_.id)
                .spaceComma(ChinaRegion_.createTime::desc, ChinaRegion_.version::asc)
                .limit(SQLs::param, regionIdList.size())
                .asQuery();

        statementCostTimeLog(session, LOG, startNanoSecond);


        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);
        Assert.assertEquals(rowList.size(), regionIdList.size());
    }

    @Transactional
    @Test//(invocationCount = 3)
    public void batchQuery20WindowClause(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(3);
        session.batchSave(regionList);

        final long startNanoSecond = System.nanoTime();

        final BatchSelect stmt;
        stmt = SQLs.batchQuery20()
                .with("cte").as(s -> s.select(ChinaRegion_.id)
                        .from(ChinaRegion_.T, AS, "c")
                        .where(ChinaRegion_.id::equal, SQLs::namedParam)
                        .asQuery()
                )
                .space()
                .select(ChinaProvince_.id, Windows.sum(ChinaRegion_.regionGdp).over("w").as("gdpSum"))
                .from(ChinaProvince_.T, AS, "p")
                .join(ChinaRegion_.T, AS, "c").on(ChinaProvince_.id::equal, ChinaRegion_.id)
                .where(ChinaProvince_.id::equal, SQLs.scalarSubQuery()
                        .select(s -> s.space(SQLs.refField("cte", ChinaRegion_.ID)))
                        .from("cte")
                        ::asQuery
                )
                .window("w").as(s -> s.partitionBy(ChinaRegion_.name).orderBy(ChinaRegion_.regionGdp::desc).rows(UNBOUNDED_PRECEDING))
                .orderBy(ChinaProvince_.id)
                .limit(SQLs::param, 1)
                .asQuery()
                .namedParamList(extractRegionIdMapList(regionList));

        statementCostTimeLog(session, LOG, startNanoSecond);

        final int regionIdListSize = regionList.size();

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);
        Assert.assertEquals(rowList.size(), regionIdListSize);

        for (int i = 0; i < regionIdListSize; i++) {

            Assert.assertEquals(regionList.get(i).getId(), rowList.get(i).get(ChinaRegion_.ID));
        }


        LOG.debug("{} rowList : \n{}", session.name(), JSON.toJSONString(rowList));
    }


}
