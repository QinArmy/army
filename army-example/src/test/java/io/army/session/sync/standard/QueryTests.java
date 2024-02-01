package io.army.session.sync.standard;


import com.alibaba.fastjson2.JSON;
import io.army.criteria.BatchSelect;
import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.Windows;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion;
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
public class QueryTests extends SessionSupport {


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void query20WindowClause(final SyncLocalSession session) {
        final int regionSize = 3;
        final List<ChinaProvince> regionList = createProvinceListWithCount(regionSize);
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
                .limit(SQLs::param, regionSize)
                .asQuery();

        statementCostTimeLog(session, LOG, startNanoSecond);


        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);
        Assert.assertEquals(rowList.size(), regionSize);

        for (int i = 0; i < regionSize; i++) {

            Assert.assertEquals(regionList.get(i).getId(), rowList.get(i).get(ChinaRegion_.ID));
        }

    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void query20FullClause(final SyncLocalSession session) {
        final int regionSize = 3;
        final List<ChinaProvince> regionList = createProvinceListWithCount(regionSize);
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
                .limit(SQLs::param, regionSize)
                .asQuery();

        statementCostTimeLog(session, LOG, startNanoSecond);


        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);
        Assert.assertEquals(rowList.size(), regionSize);

        for (int i = 0; i < regionSize; i++) {

            Assert.assertEquals(regionList.get(i).getId(), rowList.get(i).get(ChinaRegion_.ID));
        }

    }

    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
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

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void unionQuery(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final List<ChinaRegion<?>> regionList2 = createReginListWithCount(3);
        session.batchSave(regionList2);

        final long startNanoSecond = System.nanoTime();

        final Select stmt;
        stmt = SQLs.query()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .union()
                .parens(s -> s.select("c2", PERIOD, ChinaRegion_.T)
                        .from(ChinaRegion_.T, AS, "c2")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList2)))
                        .asQuery()
                )
                .orderBy(SQLs.refSelection(ChinaRegion_.ID))
                .asQuery();


        statementCostTimeLog(session, LOG, startNanoSecond);

        final List<ChinaRegion<?>> rowList;
        rowList = session.queryList(stmt, ChinaRegion_.CLASS);

        final int regionSize, regionSize2;
        regionSize = regionList.size();
        regionSize2 = regionList2.size();

        Assert.assertEquals(rowList.size(), regionSize + regionSize2);

        for (int i = 0; i < regionSize; i++) {
            Assert.assertEquals(rowList.get(i).getId(), regionList.get(i).getId());
        }

        for (int i = 0, rowIndex = regionSize; i < regionSize2; i++, rowIndex++) {
            Assert.assertEquals(rowList.get(rowIndex).getId(), regionList2.get(i).getId());
        }


    }

    /**
     * <p>Test following :
     * <ul>
     *     <li>Bracket CriteriaContext migration</li>
     *     <li>WITH clause migration</li>
     *     <li>parens WITH clause parsing</li>
     * </ul>
     */
    @Test
    public void contextMigration(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(2);
        session.batchSave(regionList);

        final Long firstId, secondId;
        firstId = regionList.get(0).getId();
        secondId = regionList.get(1).getId();
        assert firstId != null && secondId != null;

        final Select stmt;
        stmt = SQLs.query20()
                .with("cte").as(s -> s.select(ChinaRegion_.id)
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id::equal, SQLs::literal, firstId)
                        .asQuery()
                ).space()
                .parens(c -> c.select(s -> s.space("cte", PERIOD, ASTERISK))
                        .from("cte")
                        .where(SQLs.refField("cte", ChinaRegion_.ID).equal(SQLs.literalValue(firstId)))
                        .asQuery()
                ).union()
                .parens(p -> p.with("cte20").as(s -> s.select(ChinaRegion_.id)
                                        .from(ChinaRegion_.T, AS, "t")
                                        .where(ChinaRegion_.id::equal, SQLs::literal, secondId)
                                        .asQuery()
                                ).space()
                                .parens(c -> c.select(s -> s.space("cte20", PERIOD, ASTERISK))
                                        .from("cte20")
                                        .where(SQLs.refField("cte20", ChinaRegion_.ID).equal(SQLs.literalValue(secondId)))
                                        .asQuery()
                                ).asQuery()
                )
                .orderBy(SQLs.refSelection(1)::desc) // test ref left context selection
                .limit(SQLs::literal, 4)
                .asQuery();

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);
        Assert.assertEquals(rowList.size(), 2);

    }

    @Test
    public void dynamicUnion(final SyncLocalSession session) {
//        final Select stmt;
//        stmt = query()
//                .select(ChinaRegion_.id)
//                .from(ChinaRegion_.T, AS, "c")
//                .whiteSpace(su -> {
//                    for (int i = 0; i < 3; i++) {
//                        su.union()
//                                .select(ChinaRegion_.id)
//                                .from(ChinaRegion_.T, AS, "c")
//                                .asQuery();
//                    }
//                })
//                .asQuery();
    }


}
