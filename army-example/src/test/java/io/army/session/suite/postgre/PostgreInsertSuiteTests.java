package io.army.session.suite.postgre;


import com.alibaba.fastjson2.JSON;
import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.dialect.ReturningInsert;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._ReturningDml;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sync.LocalSession;
import io.army.sync.LocalTransaction;
import io.army.tx.Isolation;
import io.army.util.ImmutableArrayList;
import io.army.util.ImmutableHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Test(dataProvider = "getSession")
public class PostgreInsertSuiteTests extends PostgreSuiteTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreInsertSuiteTests.class);

    @Test
    public void domainInsertParent(final LocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList;
        regionList = this.createReginList();
        final Insert stmt;
        stmt = Postgres.singleInsert()
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T).as("c")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .asInsert();


        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, regionList.size());

        for (ChinaRegion<?> region : regionList) {
            Assert.assertNotNull(region.getId());
        }

        releaseSyncSession(session);
    }

    @Test
    public void returningDomainInsertParent(final LocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList;
        regionList = this.createReginList();
        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T).as("c")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final List<ChinaRegion<?>> resultList;
        resultList = session.query(stmt, ChinaRegion_.CLASS);

        Assert.assertEquals(resultList.size(), regionList.size());

        Long id;
        for (ChinaRegion<?> region : regionList) {
            id = region.getId();
            Assert.assertNotNull(id);
        }

        LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));
        releaseSyncSession(session);

    }

    @Test
    public void domainInsertParentWithDoNothing(final LocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList;
        regionList = this.createReginList();
        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .ignoreReturnIds()  // required ,because exists doNothing
                .insertInto(ChinaRegion_.T).as("c")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.parentId)
                        .comma(ChinaRegion_.regionGdp)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .onConflict()
                .doNothing()
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final List<ChinaRegion<?>> resultList;
        resultList = session.query(stmt, ChinaRegion_.CLASS);

        Assert.assertEquals(resultList.size(), regionList.size());

        releaseSyncSession(session);
    }

    @Test
    public void domainInsertParentWithUpdateSet(final LocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList;
        regionList = this.createReginList();

        List<ChinaRegion<?>> resultList;

        ReturningInsert stmt;

        // insert data
        stmt = Postgres.singleInsert()
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T)
                .values(regionList)
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);
        resultList = session.query(stmt, ChinaRegion_.CLASS);
        Assert.assertEquals(resultList.size(), regionList.size());

        // conflict stmt

        stmt = Postgres.singleInsert()
                .ignoreReturnIds()  // required ,because ChinaRegion_ contain visible field
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T).as("c")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.parentId)
                        .comma(ChinaRegion_.regionGdp)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .onConflict()
                .leftParen(ChinaRegion_.name)
                .comma(ChinaRegion_.regionType)
                .rightParen()
                .doUpdate()
                .set(ChinaRegion_.name, Postgres::excluded)
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);
        resultList = session.query(stmt, ChinaRegion_.CLASS);
        Assert.assertEquals(resultList.size(), regionList.size());
        LOG.debug("{}", JSON.toJSONString(resultList));
        releaseSyncSession(session);

    }

    @Test
    public void domainInsertChildWithTowStmtUpdateMode(final LocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        final Insert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .values(provinceList)
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values(provinceList)
                .asInsert();

        Assert.assertFalse(stmt instanceof _ReturningDml);

        final LocalTransaction tx;
        tx = session.builder()
                .isolation(Isolation.READ_COMMITTED)
                .build();

        try {
            tx.start();
            Assert.assertEquals(session.update(stmt), provinceList.size());
            for (ChinaProvince province : provinceList) {
                Assert.assertNotNull(province.getId()); // database generated key
            }
            tx.commit();
        } catch (Exception e) {
            LOG.error("insert child error", e);
            tx.rollback();
            throw e;
        }

        releaseSyncSession(session);

    }

    @Test
    public void returningDomainInsertChildWithTowStmtQueryMode(final LocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .values(provinceList)
                .returningAll()
                .asReturningInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values(provinceList)
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final LocalTransaction tx;
        tx = session.builder()
                .isolation(Isolation.READ_COMMITTED)
                .build();

        try {
            tx.start();
            final List<ChinaProvince> resultList;
            resultList = session.query(stmt, ChinaProvince.class, ImmutableArrayList::arrayList);

            Assert.assertEquals(resultList.size(), provinceList.size());

            for (ChinaProvince province : provinceList) {
                Assert.assertNotNull(province.getId()); // database generated key
            }
            Assert.assertFalse(resultList instanceof ImmutableArrayList);
            for (ChinaProvince province : resultList) {
                Assert.assertNotNull(province.getId());

                // parent fields
                Assert.assertNotNull(province.getCreateTime());
                Assert.assertNotNull(province.getUpdateTime());

                // child fields
                Assert.assertNotNull(province.getGovernor());
                Assert.assertNotNull(province.getProvincialCapital());
            }
            tx.commit();
            LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));
        } catch (Exception e) {
            LOG.error("insert child error", e);
            tx.rollback();
            throw e;
        } finally {
            releaseSyncSession(session);
        }

    }

    @Test
    public void returningDomainInsertDiffMode(final LocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .values(provinceList)
                .asInsert() // use asInsert not asReturningInsert

                .child()

                .insertInto(ChinaProvince_.T)
                .values(provinceList)
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final LocalTransaction tx;
        tx = session.builder()
                .isolation(Isolation.READ_COMMITTED)
                .build();

        try {
            tx.start();
            final List<ChinaProvince> resultList;
            resultList = session.query(stmt, ChinaProvince.class, ImmutableArrayList::arrayList);

            Assert.assertEquals(resultList.size(), provinceList.size());

            for (ChinaProvince province : provinceList) {
                Assert.assertNotNull(province.getId()); // database generated key
            }
            Assert.assertFalse(resultList instanceof ImmutableArrayList);

            for (ChinaProvince province : resultList) {
                // parent fields
                Assert.assertNull(province.getCreateTime());
                Assert.assertNull(province.getUpdateTime());

                // child fields
                Assert.assertNotNull(province.getId());
                Assert.assertNotNull(province.getGovernor());
                Assert.assertNotNull(province.getProvincialCapital());

            }

            tx.commit();
            LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));
        } catch (Exception e) {
            LOG.error("insert child error", e);
            tx.rollback();
            throw e;
        } finally {
            releaseSyncSession(session);
        }

    }


    @Test
    public void returningDomainInsertChildMapWithTowStmtQueryMode(final LocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .values(provinceList)
                .returningAll()
                .asReturningInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values(provinceList)
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final LocalTransaction tx;
        tx = session.builder()
                .isolation(Isolation.READ_COMMITTED)
                .build();

        try {
            tx.start();
            final List<Map<String, Object>> resultList;
            resultList = session.queryMap(stmt, ImmutableHashMap::hashMap, ImmutableArrayList::arrayList);

            Assert.assertEquals(resultList.size(), provinceList.size());

            for (ChinaProvince province : provinceList) {
                Assert.assertNotNull(province.getId()); // database generated key
            }
            Assert.assertFalse(resultList instanceof ImmutableArrayList);
            for (Map<String, Object> map : resultList) {

                Assert.assertFalse(map instanceof ImmutableHashMap);
                Assert.assertNotNull(map.get(ChinaRegion_.ID));

                // parent fields
                Assert.assertNotNull(map.get(ChinaRegion_.CREATE_TIME));
                Assert.assertNotNull(map.get(ChinaRegion_.UPDATE_TIME));

                // child fields
                Assert.assertNotNull(map.get(ChinaProvince_.GOVERNOR));
                Assert.assertNotNull(map.get(ChinaProvince_.PROVINCIAL_CAPITAL));
            }
            tx.commit();
            LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));
        } catch (Exception e) {
            LOG.error("insert child error", e);
            tx.rollback();
            throw e;
        } finally {
            releaseSyncSession(session);
        }

    }


    @Test
    public void returningDomainInsertChildMapDiffMode(final LocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .values(provinceList)
                .asInsert() // use asInsert not asReturningInsert

                .child()

                .insertInto(ChinaProvince_.T)
                .values(provinceList)
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final LocalTransaction tx;
        tx = session.builder()
                .isolation(Isolation.READ_COMMITTED)
                .build();

        try {
            tx.start();
            final List<Map<String, Object>> resultList;
            resultList = session.queryMap(stmt, ImmutableHashMap::hashMap, ImmutableArrayList::arrayList);

            Assert.assertEquals(resultList.size(), provinceList.size());

            for (ChinaProvince province : provinceList) {
                Assert.assertNotNull(province.getId()); // database generated key
            }
            Assert.assertFalse(resultList instanceof ImmutableArrayList);
            for (Map<String, Object> map : resultList) {

                Assert.assertFalse(map instanceof ImmutableHashMap);

                // parent fields
                Assert.assertNull(map.get(ChinaRegion_.CREATE_TIME));
                Assert.assertNull(map.get(ChinaRegion_.UPDATE_TIME));

                // child fields
                Assert.assertNotNull(map.get(ChinaRegion_.ID));
                Assert.assertNotNull(map.get(ChinaProvince_.GOVERNOR));
                Assert.assertNotNull(map.get(ChinaProvince_.PROVINCIAL_CAPITAL));
            }
            tx.commit();
            LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));
        } catch (Exception e) {
            LOG.error("insert child error", e);
            tx.rollback();
            throw e;
        } finally {
            releaseSyncSession(session);
        }

    }


    /*-------------------below values syntax tests -------------------*/

    @Test
    public void staticValuesInsertParent(final LocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final Insert stmt;
        stmt = Postgres.singleInsert()
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .asInsert();

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, 2);

        releaseSyncSession(session);

    }


}
