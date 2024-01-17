package io.army.session.sync.standard;

import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.Windows;
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

@Test(dataProvider = "localSessionProvider", groups = "standardInsert")
public class StandardInsertTests extends StandardSessionSupport {


    @Test//(invocationCount = 3)
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

    @Test
    public void domainInsert20Parent(final SyncLocalSession session) {

        switch (session.sessionFactory().serverMeta().serverDatabase()) {
            case MySQL: // MySQL INSERT statement don't support WITH clause
                return;
            case PostgreSQL:
            default:
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
    @Test
    public void domainInsert20Child(final SyncLocalSession session) {

        switch (session.sessionFactory().serverMeta().serverDatabase()) {
            case MySQL: // MySQL INSERT statement don't support WITH clause
                return;
            case PostgreSQL:
            default:
        }

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> parentList, regionList;
        parentList = createProvinceListWithCount(3);
        session.batchSave(parentList); // save parentList
        regionList = createProvinceListWithCount(parentList.size());

        final List<Long> regionIdList = extractRegionIdList(parentList);

        final Insert stmt;
        stmt = SQLs.singleInsert20()
                .with("cte").as(s -> s.select(ChinaRegion_.id, Windows.rowNumber().over().as("rowNumber"))
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, regionIdList))
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
                .asInsert()

                .child()

                .with("cte").as(s -> s.select(ChinaRegion_.id, Windows.rowNumber().over().as("rowNumber"))
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id.in(SQLs::rowParam, regionIdList))
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

    @Test
    public void staticValuesInsertChild(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = SQLs.singleInsert()
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


        session.startTransaction();

        try {
            Assert.assertEquals(session.update(stmt), 2L);
            session.commit();
        } catch (RuntimeException e) {
            session.rollback();
            throw e;
        }

    }

    /*-------------------below dynamic VALUES insert -------------------*/

    @Test
    public void dynamicValuesInsertParent(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final int rowCount = 100;

        final Insert stmt;
        stmt = SQLs.singleInsert()
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

    @Test
    public void dynamicValuesInsertChild(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final int rowCount = 200;

        final Insert stmt;
        stmt = SQLs.singleInsert()
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

    @Test(dependsOnMethods = {"domainInsertParent", "staticValuesInsertParent", "dynamicValuesInsertParent"})
    public void queryInsertParent(final SyncLocalSession session) {
        final Insert stmt;
        stmt = SQLs.singleInsert()
                .migration()
                .insertInto(HistoryChinaRegion_.T)
                .space()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                .and(SQLs::notExists, SQLs.subQuery()
                        .select(HistoryChinaRegion_.id)
                        .from(HistoryChinaRegion_.T, AS, "hc")
                        .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                        ::asQuery
                )
                .asQuery()
                .asInsert();

        session.update(stmt);

    }


    @Test(dependsOnMethods = {"domainInsertParent", "staticValuesInsertParent", "dynamicValuesInsertParent",
            "queryInsertParent", "domainInsertChild", "staticValuesInsertChild", "dynamicValuesInsertChild"})
    // dependsOn queryInsertParent to avoid deadlock
    public void queryInsertChild(final SyncLocalSession session) {
        final Insert stmt;
        stmt = SQLs.singleInsert()
                .migration()
                .insertInto(HistoryChinaRegion_.T)
                .space()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaProvince_.T, AS, "p")
                .join(ChinaRegion_.T, AS, "c").on(ChinaRegion_.id::equal, ChinaProvince_.id)
                .where(SQLs::notExists, SQLs.subQuery()
                        .select(HistoryChinaRegion_.id)
                        .from(HistoryChinaRegion_.T, AS, "hc")
                        .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                        ::asQuery
                )
                .asQuery()
                .asInsert()

                .child()

                .insertInto(HistoryChinaProvince_.T)
                .space()
                .select("p", PERIOD, ChinaProvince_.T)
                .from(ChinaProvince_.T, AS, "p")
                .join(ChinaRegion_.T, AS, "c").on(ChinaRegion_.id::equal, ChinaProvince_.id)
                .where(SQLs::notExists, SQLs.subQuery()
                        .select(HistoryChinaProvince_.id)
                        .from(HistoryChinaProvince_.T, AS, "hp")
                        .join(HistoryChinaRegion_.T, AS, "hc").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                        .where(HistoryChinaProvince_.id::equal, ChinaProvince_.id)
                        ::asQuery
                )
                .asQuery()
                .asInsert();

        session.startTransaction();
        try {
            session.update(stmt);
            session.commit();
        } catch (RuntimeException e) {
            session.rollback();
            throw e;
        }

    }


}
