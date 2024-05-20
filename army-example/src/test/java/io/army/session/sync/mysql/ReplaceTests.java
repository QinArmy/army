package io.army.session.sync.mysql;


import io.army.annotation.GeneratorType;
import io.army.criteria.ErrorChildInsertException;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.Visible;
import io.army.criteria.annotaion.VisibleMode;
import io.army.criteria.dialect.Hint;
import io.army.criteria.mysql.MySQLs;
import io.army.criteria.standard.SQLs;
import io.army.example.bank.domain.user.*;
import io.army.sync.SyncLocalSession;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static io.army.criteria.standard.SQLs.AS;
import static io.army.criteria.standard.SQLs.PERIOD;


/**
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html">MySQL 8.0 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/optimizer-hints.html">MySQL 5.7 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/replace.html">REPLACE Statement</a>
 * @since 0.6.0
 */
@Test(dataProvider = "localSessionProvider")
public class ReplaceTests extends SessionTestSupport {


    @VisibleMode(Visible.BOTH)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainReplaceOneParent(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final ChinaRegion<?> region;
        region = createReginListWithCount(1).get(0);
        region.setId(null);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .replaceInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::param, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::param, 0)
                .value(region)
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), 1L);
        Assert.assertNotNull(region.getId());

    }

    @VisibleMode(Visible.BOTH)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainReplaceOneParentWithConflict(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final ChinaRegion<?> region;
        region = createReginListWithCount(1).get(0);
        session.save(region);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .literalMode(LiteralMode.LITERAL)
                .replaceInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::param, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::param, 0)
                .value(region)
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final Long originalId;
        originalId = region.getId();
        Assert.assertNotNull(originalId);

        region.setId(null);

        Assert.assertEquals(session.update(stmt), 2L); // because of conflict
        final Long newRowId;
        newRowId = region.getId();
        Assert.assertNotNull(newRowId);
        Assert.assertTrue(newRowId > originalId); // old row is deleted and before the new row is inserted

    }

    @VisibleMode(Visible.BOTH)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainReplaceOneParentWithHintAndModifier(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final ChinaRegion<?> region;
        region = createReginListWithCount(1).get(0);
        region.setId(null);

        final long startNanoSecond = System.nanoTime();

        final Supplier<List<Hint>> hintSupplier = () -> Collections.singletonList(MySQLs.setVar("foreign_key_checks=OFF"));

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .replace(hintSupplier, Collections.singletonList(MySQLs.LOW_PRIORITY))
                .into(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::param, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::param, 0)
                .value(region)
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), 1L);
        Assert.assertNotNull(region.getId());

    }


    @VisibleMode(Visible.BOTH)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainReplaceParent(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .ignoreReturnIds() // due to you use "domain" api replace multi-row , so you have to use ignoreReturnIds() option clause,because database couldn't return correct multi-row primary key value.
                .replaceInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::param, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::param, 0)
                .values(regionList)  // here , "domain" api
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), regionList.size());

        for (ChinaRegion<?> region : regionList) {
            Assert.assertNull(region.getId()); // because ignoreReturnIds() option clause.
        }

    }

    @Transactional
    @VisibleMode(Visible.BOTH)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainReplaceOneChild(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final ChinaProvince province;
        province = createProvinceListWithCount(1).get(0);
        province.setId(null);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .replaceInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::literal, 0)
                .value(province)
                .asInsert()

                .child()

                .replaceInto(ChinaProvince_.T)
                .defaultValue(ChinaProvince_.governor, SQLs::literal, randomPerson())
                .value(province)
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), 1L);
        Assert.assertNotNull(province.getId());

    }


    @Transactional
    @VisibleMode(Visible.BOTH)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainReplaceOneChildWithHintAndModifier(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final ChinaProvince province;
        province = createProvinceListWithCount(1).get(0);
        province.setId(null);

        final long startNanoSecond = System.nanoTime();

        final Supplier<List<Hint>> hintSupplier = () -> Collections.singletonList(MySQLs.setVar("foreign_key_checks=OFF"));

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .replace(hintSupplier, Collections.singletonList(MySQLs.LOW_PRIORITY))
                .into(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::literal, 0)
                .value(province)
                .asInsert()

                .child()

                .replace(hintSupplier, Collections.singletonList(MySQLs.LOW_PRIORITY))
                .into(ChinaProvince_.T)
                .defaultValue(ChinaProvince_.governor, SQLs::literal, randomPerson())
                .value(province)
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), 1L);
        Assert.assertNotNull(province.getId());

    }


    @Test(expectedExceptions = ErrorChildInsertException.class)
    public void domainReplaceChild(final SyncLocalSession session) { // don't use session

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> regionList = createProvinceListWithCount(3);

        MySQLs.singleReplace()
                .ignoreReturnIds()
                .replaceInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::literal, 0)
                .values(regionList)
                .asInsert()

                .child()

                .replaceInto(ChinaProvince_.T)
                .defaultValue(ChinaProvince_.governor, SQLs::literal, randomPerson())
                .values(regionList)
                .asInsert();

    }



    /*-------------------below static VALUES insert -------------------*/

    @VisibleMode(Visible.BOTH)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void staticValuesReplaceParent(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .replaceInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomProvince(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                ).asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), 2L);

    }


    @Transactional
    @VisibleMode(Visible.BOTH)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void staticValuesReplaceOneChild(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .replaceInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()

                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::param, 0)
                ).asInsert()

                .child()

                .replaceInto(ChinaCity_.T)
                .values()
                .parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, randomPerson(random)))
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), 1L);

    }


    @Test(expectedExceptions = ErrorChildInsertException.class)
    public void staticValuesReplaceChild(final SyncLocalSession session) { // don't use session
        final Random random = ThreadLocalRandom.current();

        MySQLs.singleReplace()
                .replaceInto(ChinaRegion_.T)
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
                ).asInsert()

                .child()

                .replaceInto(ChinaCity_.T)
                .values()
                .parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, randomPerson(random)))
                .comma()
                .parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, randomPerson(random)))
                .asInsert();

    }



    /*-------------------below dynamic VALUES insert -------------------*/

    @VisibleMode(Visible.BOTH)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void dynamicValuesReplaceParent(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final int rowCount = 3;

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .replaceInto(ChinaRegion_.T)
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
    @VisibleMode(Visible.BOTH)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void dynamicValuesReplaceOneChild(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final int rowCount = 1;

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .replaceInto(ChinaRegion_.T)
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

                .replaceInto(ChinaCity_.T)
                .values(r -> {
                    for (int i = 0; i < rowCount; i++) {
                        r.parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, randomPerson(random)));
                    }
                })
                .asInsert();

        Assert.assertEquals(session.update(stmt), rowCount);

    }

    @Test(expectedExceptions = ErrorChildInsertException.class)
    public void dynamicValuesReplaceChild(final SyncLocalSession session) { // don't use session
        final Random random = ThreadLocalRandom.current();

        final int rowCount = 3;

        MySQLs.singleReplace()
                .replaceInto(ChinaRegion_.T)
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

                .replaceInto(ChinaCity_.T)
                .values(r -> {
                    for (int i = 0; i < rowCount; i++) {
                        r.parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, randomPerson(random)));
                    }
                })
                .asInsert();

    }


    /*-------------------below assignment insert -------------------*/

    @VisibleMode(Visible.BOTH)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void assignmentReplaceParent(final SyncLocalSession session) {

        final Random random = ThreadLocalRandom.current();
        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .replaceInto(ChinaRegion_.T)
                .set(ChinaRegion_.name, SQLs::param, randomRegion(random))
                .set(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                .set(ChinaRegion_.population, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), 1L);

    }

    @VisibleMode(Visible.BOTH)
    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void assignmentReplaceOneChild(final SyncLocalSession session) {
        final ChinaProvince province = createProvinceListWithCount(1).get(0);
        province.setId(null);

        final Random random = ThreadLocalRandom.current();
        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .literalMode(LiteralMode.LITERAL)
                .replaceInto(ChinaRegion_.T)
                .set(ChinaRegion_.name, SQLs::literal, province.getName())
                .set(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                .set(ChinaRegion_.population, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .asInsert()

                .child()

                .replaceInto(ChinaProvince_.T)
                .set(ChinaProvince_.provincialCapital, SQLs::literal, province.getProvincialCapital())
                .set(ChinaProvince_.governor, SQLs::literal, province.getGovernor())
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), 1L);

    }

    @VisibleMode(Visible.BOTH)
    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void assignmentReplaceOneChildWithConflict(final SyncLocalSession session) {
        final ChinaProvince province = createProvinceListWithCount(1).get(0);
        session.save(province);

        final Random random = ThreadLocalRandom.current();
        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .literalMode(LiteralMode.LITERAL)
                .replaceInto(ChinaRegion_.T)
                .set(ChinaRegion_.name, SQLs::literal, province.getName())
                .set(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                .set(ChinaRegion_.population, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .asInsert()

                .child()

                .replaceInto(ChinaProvince_.T)
                .set(ChinaProvince_.provincialCapital, SQLs::literal, province.getProvincialCapital())
                .set(ChinaProvince_.governor, SQLs::literal, province.getGovernor())
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertEquals(session.update(stmt), 2L); // because database return child's affected row count

    }

    /*-------------------below query insert -------------------*/

    @VisibleMode(Visible.BOTH)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void queryReplaceParent(final SyncLocalSession session) {
        final List<ChinaRegion<?>> parentList;
        parentList = createReginListWithCount(3);
        session.batchSave(parentList); // save parentList

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .migration()
                .replaceInto(HistoryChinaRegion_.T)
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
    @VisibleMode(Visible.BOTH)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void queryReplaceChild(final SyncLocalSession session) {
        final List<ChinaProvince> parentList;
        parentList = createProvinceListWithCount(3);
        session.batchSave(parentList); // save parentList

        final List<Long> regionIdList = extractRegionIdList(parentList);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleReplace()
                .migration()
                .replaceInto(HistoryChinaRegion_.T)
                .space()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, regionIdList))
                .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.PROVINCE)
                .asQuery()
                .asInsert()

                .child()

                .replaceInto(HistoryChinaProvince_.T)
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
