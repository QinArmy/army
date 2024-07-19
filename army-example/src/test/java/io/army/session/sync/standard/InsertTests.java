package io.army.session.sync.standard;

import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.Windows;
import io.army.example.bank.domain.user.*;
import io.army.result.ResultStates;
import io.army.sync.SyncLocalSession;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static io.army.criteria.impl.SQLs.*;

@Test(dataProvider = "localSessionProvider")
public class InsertTests extends SessionSupport {


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainInsertParent(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);

        final Insert stmt;
        stmt = SQLs.singleInsert()
                //.literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::param, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::param, 0)
                .values(regionList)
                .asInsert();

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, regionList.size());

        assertChinaRegionAfterNoConflictInsert(regionList);

    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainInsertParentAsStates(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);

        final Insert stmt;
        stmt = SQLs.singleInsert()
                //.literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::param, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::param, 0)
                .values(regionList)
                .asInsert();

        final ResultStates states;
        states = session.updateAsStates(stmt);

        Assert.assertEquals(states.batchSize(), 0);
        Assert.assertFalse(states.hasMoreResult());
        Assert.assertFalse(states.hasMoreFetch());

        Assert.assertEquals(states.affectedRows(), regionList.size());

        Assert.assertFalse(states.inTransaction());

        if (states.hasColumn()) {
            Assert.assertEquals(states.rowCount(), regionList.size());
        } else {
            Assert.assertEquals(states.rowCount(), 0L);
        }

        if (states.isSupportInsertId()) {
            Assert.assertEquals(states.lastInsertedId(), regionList.get(0).getId());
        }

        assertChinaRegionAfterNoConflictInsert(regionList);

    }


    @Transactional
    @Test
    public void domainInsertChild(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> regionList = createProvinceListWithCount(3);

        final Insert stmt;
        stmt = SQLs.singleInsert()
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, LITERAL_0)  // SQLite don't support DEFAULT in VALUES clause .
                .values(regionList)
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .defaultValue(ChinaProvince_.governor, SQLs::literal, randomPerson())
                .defaultValue(ChinaProvince_.relationId, LITERAL_0) // SQLite don't support DEFAULT in VALUES clause .
                .values(regionList)
                .asInsert();


        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, regionList.size());
        assertChinaRegionAfterNoConflictInsert(regionList);
    }

    @Test
    public void domainInsert20Parent(final SyncLocalSession session) {

        if (isDontSupportWithClauseInInsert(session)) {
            return;
        }


        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> parentList, regionList;
        parentList = createReginListWithCount(3);
        session.batchSave(parentList); // save parentList

        regionList = createReginListWithCount(parentList.size());

        final Insert stmt;
        stmt = SQLs.singleInsert20()
                .with("cte").as(s -> s.select(ChinaRegion_.id, Windows.rowNumber().over().as("rowNumber"))
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(parentList)))
                        .orderBy(ChinaRegion_.id)
                        .asQuery()
                ).space()
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::param, true)
                .defaultValue(ChinaRegion_.parentId, SQLs.scalarSubQuery()
                        .select(s -> s.space(SQLs.refField("cte", ChinaRegion_.ID)))
                        .from("cte")
                        .where(SQLs.refField("cte", "rowNumber").equal(SQLs.BATCH_NO_PARAM))
                        .asQuery()
                )
                .values(regionList)
                .asInsert();

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, regionList.size());
        assertChinaRegionAfterNoConflictInsert(regionList);

    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainInsert20Child(final SyncLocalSession session) {

        if (isDontSupportWithClauseInInsert(session)) {
            return;
        }


        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> parentList, regionList;
        parentList = createProvinceListWithCount(3);
        session.batchSave(parentList); // save parentList


        regionList = createProvinceListWithCount(parentList.size());

        final List<Long> regionIdList = extractRegionIdList(parentList);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = SQLs.singleInsert20()
                .with("cte").as(s -> s.select(ChinaRegion_.id, Windows.rowNumber().over().as("rowNumber"))
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, regionIdList))
                        .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.PROVINCE)
                        .orderBy(ChinaRegion_.id)
                        .asQuery()
                ).space()
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, BigDecimal.ZERO)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .defaultValue(ChinaRegion_.parentId, SQLs.scalarSubQuery()
                        .select(s -> s.space(SQLs.refField("cte", ChinaRegion_.ID)))
                        .from("cte")
                        .where(SQLs.refField("cte", "rowNumber").equal(SQLs.BATCH_NO_PARAM))
                        .asQuery()
                )
                .values(regionList)
                .asInsert()

                .child()

                .with("cte").as(s -> s.select(ChinaRegion_.id, Windows.rowNumber().over().as("rowNumber"))
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, regionIdList))
                        .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.PROVINCE)
                        .orderBy(ChinaRegion_.id)
                        .asQuery()
                ).space()
                .insertInto(ChinaProvince_.T)
                .defaultValue(ChinaProvince_.relationId, SQLs.scalarSubQuery()
                        .select(s -> s.space(SQLs.refField("cte", ChinaRegion_.ID)))
                        .from("cte")
                        .where(SQLs.refField("cte", "rowNumber").equal(SQLs.BATCH_NO_PARAM))
                        .asQuery()
                )
                .values(regionList)
                .asInsert();


        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, regionList.size());
        assertChinaRegionAfterNoConflictInsert(regionList);
    }

    /*-------------------below static VALUES insert -------------------*/

    @Test
    public void staticValuesInsertParent(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = SQLs.singleInsert()
                // .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, LITERAL_0) // SQLite don't support DEFAULT in VALUES clause .
                .defaultValue(ChinaRegion_.population, LITERAL_0) // SQLite don't support DEFAULT in VALUES clause .
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
    @Test
    public void staticValuesInsertChild(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = SQLs.singleInsert()
                // .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, LITERAL_0) // SQLite don't support DEFAULT in VALUES clause .
                .defaultValue(ChinaRegion_.population, LITERAL_0) // SQLite don't support DEFAULT in VALUES clause .
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


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void staticValuesInsert20Parent(final SyncLocalSession session) {

        if (isDontSupportWithClauseInInsert(session)) {
            return;
        }


        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> parentList;
        parentList = createReginListWithCount(2);
        session.batchSave(parentList); // save parentList

        final Random random = ThreadLocalRandom.current();

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = SQLs.singleInsert20()
                .with("cte").as(s -> s.select(ChinaRegion_.id, Windows.rowNumber().over().as("rowNumber"))
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(parentList)))
                        .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                        .orderBy(ChinaRegion_.id)
                        .asQuery()
                ).space()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, BigDecimal.ZERO)
                .defaultValue(ChinaRegion_.population, LITERAL_0) // SQLite don't support DEFAULT in VALUES clause .
                .defaultValue(ChinaRegion_.parentId, SQLs.scalarSubQuery()
                        .select(s -> s.space(SQLs.refField("cte", ChinaRegion_.ID)))
                        .from("cte")
                        .where(SQLs.refField("cte", "rowNumber").equal(SQLs.BATCH_NO_PARAM))
                        .asQuery()
                )
                .values()

                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                )
                .comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomProvince(random))
                )
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, parentList.size());

    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void staticValuesInsert20Child(final SyncLocalSession session) {

        if (isDontSupportWithClauseInInsert(session)) {
            return;
        }

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> parentList;
        parentList = createProvinceListWithCount(2);
        session.batchSave(parentList); // save parentList

        final List<Long> regionIdList = extractRegionIdList(parentList);

        final Random random = ThreadLocalRandom.current();

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = SQLs.singleInsert20()
                .with("cte").as(s -> s.select(ChinaRegion_.id, Windows.rowNumber().over().as("rowNumber"))
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, regionIdList))
                        .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.PROVINCE)
                        .orderBy(ChinaRegion_.id)
                        .asQuery()
                ).space()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, BigDecimal.ZERO)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .defaultValue(ChinaRegion_.population, LITERAL_0) // SQLite don't support DEFAULT in VALUES clause .
                .defaultValue(ChinaRegion_.parentId, SQLs.scalarSubQuery()
                        .select(s -> s.space(SQLs.refField("cte", ChinaRegion_.ID)))
                        .from("cte")
                        .where(SQLs.refField("cte", "rowNumber").equal(SQLs.BATCH_NO_PARAM))
                        .asQuery()
                )
                .values()

                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                )
                .comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                )
                .asInsert()

                .child()

                .with("cte").as(s -> s.select(ChinaRegion_.id, Windows.rowNumber().over().as("rowNumber"))
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, regionIdList))
                        .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.PROVINCE)
                        .orderBy(ChinaRegion_.id)
                        .asQuery()
                ).space()
                .insertInto(ChinaProvince_.T)
                .defaultValue(ChinaProvince_.relationId, SQLs.scalarSubQuery()
                        .select(s -> s.space(SQLs.refField("cte", ChinaRegion_.ID)))
                        .from("cte")
                        .where(SQLs.refField("cte", "rowNumber").equal(SQLs.BATCH_NO_PARAM))
                        .asQuery()
                )
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::param, randomPerson(random))
                )
                .comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::param, randomPerson(random))
                )
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), parentList.size());

    }

    /*-------------------below dynamic VALUES insert -------------------*/

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void dynamicValuesInsertParent(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final int rowCount = 100;

        final Insert stmt;
        stmt = SQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, LITERAL_0) // SQLite don't support DEFAULT in VALUES clause .
                .defaultValue(ChinaRegion_.population, LITERAL_0) // SQLite don't support DEFAULT in VALUES clause .
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

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void dynamicValuesInsertChild(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final int rowCount = 200;

        final Insert stmt;
        stmt = SQLs.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, LITERAL_0) // SQLite don't support DEFAULT in VALUES clause .
                .defaultValue(ChinaRegion_.population, LITERAL_0) // SQLite don't support DEFAULT in VALUES clause .
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

        session.startTransaction();
        try {
            Assert.assertEquals(session.update(stmt), rowCount);
            session.commit();
        } catch (RuntimeException e) {
            session.rollback();
            throw e;
        }

    }


    /*-------------------below query insert -------------------*/

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void queryInsertParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> parentList;
        parentList = createReginListWithCount(3);
        session.batchSave(parentList); // save parentList

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = SQLs.singleInsert()
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
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void queryInsertChild(final SyncLocalSession session) {
        final List<ChinaProvince> parentList;
        parentList = createProvinceListWithCount(3);
        session.batchSave(parentList); // save parentList

        final List<Long> regionIdList = extractRegionIdList(parentList);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = SQLs.singleInsert()
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
