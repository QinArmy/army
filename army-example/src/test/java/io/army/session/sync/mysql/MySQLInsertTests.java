package io.army.session.sync.mysql;


import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.Visible;
import io.army.criteria.annotaion.VisibleMode;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.*;
import io.army.sync.SyncLocalSession;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class MySQLInsertTests extends MySQLSynSessionTestSupport {


    @Test(invocationCount = 3)
    public void domainInsertParent(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::param, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::param, 0)
                .values(regionList)
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, regionList.size());

        assertChinaRegionAfterNoConflictInsert(regionList);

    }


    @Transactional
    @Test(invocationCount = 3)
    public void domainInsertChild(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> regionList = createProvinceListWithCount(3);

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::literal, 0)
                .values(regionList)
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .defaultValue(ChinaProvince_.governor, SQLs::literal, randomPerson())
                .values(regionList)
                .asInsert();


        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, regionList.size());
        assertChinaRegionAfterNoConflictInsert(regionList);
    }

    @VisibleMode(Visible.BOTH)
    @Test//(invocationCount = 3)
    public void domainInsertParentWithConflict(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .ignoreReturnIds()
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.population, ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::param, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::param, 0)
                .values(regionList)
                .as("c")
                .onDuplicateKey()
                .update(ChinaRegion_.population, SQLs.field("c", ChinaRegion_.population))
                .comma(ChinaRegion_.regionGdp, SQLs.field("c", ChinaRegion_.regionGdp))
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, regionList.size());

        assertChinaRegionAfterNoConflictInsert(regionList);
    }


    /*-------------------below static VALUES insert -------------------*/

    @Test(invocationCount = 3)
    public void staticValuesInsertParent(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                // .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()

                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                )
                .comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomProvince(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                )
                .asInsert();


        Assert.assertEquals(session.update(stmt), 2L);

    }

    @Transactional
    @Test(invocationCount = 3)
    public void staticValuesInsertChild(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                // .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()

                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::param, 0)
                )
                .comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                        .comma(ChinaRegion_.parentId, SQLs::param, 0)
                )
                .asInsert()

                .child()

                .insertInto(ChinaCity_.T)
                .values()
                .parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, randomPerson(random)))
                .comma()
                .parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, randomPerson(random)))
                .asInsert();


        Assert.assertEquals(session.update(stmt), 2L);

    }



    /*-------------------below dynamic VALUES insert -------------------*/

    @Test(invocationCount = 3)
    public void dynamicValuesInsertParent(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final int rowCount = 100;

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(r -> {
                    for (int i = 0; i < rowCount; i++) {
                        r.parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                                .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                                .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                        );
                    }
                })
                .asInsert();

        Assert.assertEquals(session.update(stmt), rowCount);

    }

    @Transactional
    @Test(invocationCount = 3)
    public void dynamicValuesInsertChild(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final int rowCount = 200;

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(r -> {
                    for (int i = 0; i < rowCount; i++) {
                        r.parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                                .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                                .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                        );
                    }
                })
                .asInsert()

                .child()

                .insertInto(ChinaCity_.T)
                .values(r -> {
                    for (int i = 0; i < rowCount; i++) {
                        r.parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, randomPerson(random)));
                    }
                })
                .asInsert();

        Assert.assertEquals(session.update(stmt), rowCount);

    }


    /*-------------------below query insert -------------------*/

    @Test(invocationCount = 3)
    public void queryInsertParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> parentList;
        parentList = createReginListWithCount(3);
        session.batchSave(parentList); // save parentList

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .migration()
                .insertInto(HistoryChinaRegion_.T)
                .space()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(parentList)))
                .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                .asQuery()
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), parentList.size());

    }

    @Transactional
    @Test(invocationCount = 3)
    public void queryInsertChild(final SyncLocalSession session) {
        final List<ChinaProvince> parentList;
        parentList = createProvinceListWithCount(3);
        session.batchSave(parentList); // save parentList

        final List<Long> regionIdList = extractRegionIdList(parentList);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .migration()
                .insertInto(HistoryChinaRegion_.T)
                .space()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, regionIdList))
                .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.PROVINCE)
                .asQuery()
                .asInsert()

                .child()

                .insertInto(HistoryChinaProvince_.T)
                .space()
                .select("p", PERIOD, ChinaProvince_.T)
                .from(ChinaProvince_.T, AS, "p")
                .join(ChinaRegion_.T, AS, "c").on(ChinaRegion_.id::equal, ChinaProvince_.id)
                .where(ChinaProvince_.id.in(SQLs::rowParam, regionIdList))
                .asQuery()
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);
        Assert.assertEquals(session.update(stmt), regionIdList.size());

    }


}
