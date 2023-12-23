package io.army.session.sync.postgre;


import com.alibaba.fastjson2.JSON;
import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.Select;
import io.army.criteria.dialect.ReturningInsert;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._ReturningDml;
import io.army.example.bank.domain.user.*;
import io.army.session.Isolation;
import io.army.session.TransactionOption;
import io.army.sync.SyncLocalSession;
import io.army.util.Groups;
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

import static io.army.criteria.impl.SQLs.*;

@Test(dataProvider = "localSessionProvider")
public class PostgreInsertSuiteTests extends PostgreSuiteTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreInsertSuiteTests.class);


    @Test(groups = Groups.DOMAIN_INSERT)
    public void domainInsertParent(final SyncLocalSession session) {

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

    }


    @Test(groups = Groups.DOMAIN_INSERT)
    public void returningDomainInsertParent(final SyncLocalSession session) {
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
        resultList = session.queryList(stmt, ChinaRegion_.CLASS);

        Assert.assertEquals(resultList.size(), regionList.size());

        for (ChinaRegion<?> region : regionList) {
            Assert.assertNotNull(region.getId());
        }

        LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));

    }

    @Test(groups = Groups.DOMAIN_INSERT)
    public void domainInsertParentWithDoNothing(final SyncLocalSession session) {
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
        resultList = session.queryList(stmt, ChinaRegion_.CLASS);

        Assert.assertEquals(resultList.size(), regionList.size());

    }

    @Test(groups = Groups.DOMAIN_INSERT)
    public void domainInsertParentWithUpdateSet(final SyncLocalSession session) {
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
        resultList = session.queryList(stmt, ChinaRegion_.CLASS);
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
        resultList = session.queryList(stmt, ChinaRegion_.CLASS);
        Assert.assertEquals(resultList.size(), regionList.size());
        LOG.debug("{}", JSON.toJSONString(resultList));


    }

    @Test(groups = Groups.DOMAIN_INSERT)
    public void domainInsertChildWithTowStmtUpdateMode(final SyncLocalSession session) {
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

        try {
            session.startTransaction(TransactionOption.option(Isolation.READ_COMMITTED));

            Assert.assertEquals(session.update(stmt), provinceList.size());
            for (ChinaProvince province : provinceList) {
                Assert.assertNotNull(province.getId()); // database generated key
            }
            session.commitIfExists();
        } catch (Exception e) {
            LOG.error("insert child error", e);
            session.rollbackIfExists();
            throw e;
        }


    }

    @Test(groups = Groups.DOMAIN_INSERT)
    public void returningDomainInsertChildWithTowStmtQueryMode(final SyncLocalSession session) {
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


        try {
            session.startTransaction(TransactionOption.option(Isolation.READ_COMMITTED));

            final List<ChinaProvince> resultList;
            resultList = session.queryList(stmt, ChinaProvince.class, ImmutableArrayList::arrayList);

            Assert.assertEquals(resultList.size(), provinceList.size());

            for (ChinaProvince province : provinceList) {
                Assert.assertNotNull(province.getId()); // database generated key
            }

            for (ChinaProvince province : resultList) {
                Assert.assertNotNull(province.getId());

                // parent fields
                Assert.assertNotNull(province.getCreateTime());
                Assert.assertNotNull(province.getUpdateTime());

                // child fields
                Assert.assertNotNull(province.getGovernor());
                Assert.assertNotNull(province.getProvincialCapital());
            }
            session.commitIfExists();
            LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));
        } catch (Exception e) {
            LOG.error("insert child error", e);
            session.rollbackIfExists();
            throw e;
        }

    }

    @Test(groups = Groups.DOMAIN_INSERT)
    public void returningDomainInsertDiffMode(final SyncLocalSession session) {
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


        try {
            session.startTransaction(TransactionOption.option(Isolation.READ_COMMITTED));
            final List<ChinaProvince> resultList;
            resultList = session.queryList(stmt, ChinaProvince.class, ImmutableArrayList::arrayList);

            Assert.assertEquals(resultList.size(), provinceList.size());

            for (ChinaProvince province : provinceList) {
                Assert.assertNotNull(province.getId()); // database generated key
            }


            for (ChinaProvince province : resultList) {
                // parent fields
                Assert.assertNull(province.getCreateTime());
                Assert.assertNull(province.getUpdateTime());

                // child fields
                Assert.assertNotNull(province.getId());
                Assert.assertNotNull(province.getGovernor());
                Assert.assertNotNull(province.getProvincialCapital());

            }

            session.commitIfExists();
            LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));
        } catch (Exception e) {
            LOG.error("insert child error", e);
            session.rollbackIfExists();
            throw e;
        }
    }


    @Test(groups = Groups.DOMAIN_INSERT)
    public void returningDomainInsertChildMapWithTowStmtQueryMode(final SyncLocalSession session) {
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

        try {

            session.startTransaction(TransactionOption.option(Isolation.READ_COMMITTED));

            final List<Map<String, Object>> resultList;
            resultList = session.queryObjectList(stmt, ImmutableHashMap::hashMap, ImmutableArrayList::arrayList);

            Assert.assertEquals(resultList.size(), provinceList.size());

            for (ChinaProvince province : provinceList) {
                Assert.assertNotNull(province.getId()); // database generated key
            }

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
            session.commitIfExists();
            LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));
        } catch (Exception e) {
            LOG.error("insert child error", e);
            session.rollbackIfExists();
            throw e;
        }

    }


    @Test(groups = Groups.DOMAIN_INSERT)
    public void returningDomainInsertChildMapDiffMode(final SyncLocalSession session) {
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


        try {
            session.startTransaction(TransactionOption.option(Isolation.READ_COMMITTED));

            final List<Map<String, Object>> resultList;
            resultList = session.queryObjectList(stmt, ImmutableHashMap::hashMap, ImmutableArrayList::arrayList);

            Assert.assertEquals(resultList.size(), provinceList.size());

            for (ChinaProvince province : provinceList) {
                Assert.assertNotNull(province.getId()); // database generated key
            }

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
            session.commitIfExists();
            LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));
        } catch (Exception e) {
            LOG.error("insert child error", e);

            session.rollbackIfExists();

            throw e;
        }

    }


    /*-------------------below values syntax tests -------------------*/

    @Test(groups = Groups.VALUES_INSERT)
    public void staticValuesInsertParent(final SyncLocalSession session) {
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

    }

    @Test(groups = Groups.VALUES_INSERT)
    public void staticValuesReturningInsertParent(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final ReturningInsert stmt;
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
                .returningAll()
                .asReturningInsert();

        final List<ChinaRegion<?>> resultList;
        resultList = session.queryList(stmt, ChinaRegion_.CLASS, ImmutableArrayList::arrayList);

        Assert.assertEquals(resultList.size(), 2);

        for (ChinaRegion<?> region : resultList) {
            Assert.assertNotNull(region.getId());

            Assert.assertNotNull(region.getCreateTime());
            Assert.assertNotNull(region.getUpdateTime());
        }

        LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));

    }

    @Test(groups = Groups.VALUES_INSERT)
    public void valuesInsertParentWithDoNothing(final SyncLocalSession session) {
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
                .onConflict()
                .doNothing()
                .asInsert();

        Assert.assertFalse(stmt instanceof _ReturningDml);

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, 2);

    }

    @Test(groups = Groups.VALUES_INSERT)
    public void valuesInsertParentWithUpdateSet(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();

        final String regionName1, regionName2;
        regionName1 = randomRegion(random);
        regionName2 = randomRegion(random);

        List<ChinaRegion<?>> resultList;

        ReturningInsert stmt;

        // insert data
        stmt = Postgres.singleInsert()
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, regionName1)
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, regionName2)
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        resultList = session.queryList(stmt, ChinaRegion_.CLASS);

        Assert.assertEquals(resultList.size(), 2);

        // conflict stmt

        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T).as("c")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, regionName1)
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, regionName2)
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .onConflict()
                .leftParen(ChinaRegion_.name)
                .comma(ChinaRegion_.regionType)
                .rightParen()
                .doUpdate()
                .set(ChinaRegion_.name, Postgres::excluded)
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        resultList = session.queryList(stmt, ChinaRegion_.CLASS);

        Assert.assertEquals(resultList.size(), 2);

        LOG.debug("{}", JSON.toJSONString(resultList));


    }


    @Test(groups = Groups.VALUES_INSERT)
    public void valuesInsertChildWithTowStmtUpdateMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .asInsert();

        Assert.assertFalse(stmt instanceof _ReturningDml);


        try {
            session.startTransaction(TransactionOption.option(Isolation.READ_COMMITTED));

            Assert.assertEquals(session.update(stmt), 2);
            session.commitIfExists();
        } catch (Exception e) {
            LOG.error("insert child error", e);
            session.rollbackIfExists();
            throw e;
        }

    }


    @Test(groups = Groups.VALUES_INSERT)
    public void returningValuesInsertChildWithTowStmtQueryMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .returningAll()
                .asReturningInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);


        try {
            session.startTransaction(TransactionOption.option(Isolation.READ_COMMITTED));

            final List<ChinaProvince> resultList;
            resultList = session.queryList(stmt, ChinaProvince.class, ImmutableArrayList::arrayList);

            Assert.assertEquals(resultList.size(), 2);


            for (ChinaProvince province : resultList) {
                Assert.assertNotNull(province.getId());

                // parent fields
                Assert.assertNotNull(province.getCreateTime());
                Assert.assertNotNull(province.getUpdateTime());

                // child fields
                Assert.assertNotNull(province.getGovernor());
                Assert.assertNotNull(province.getProvincialCapital());
            }

            session.commitIfExists();

            LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));
        } catch (Exception e) {
            LOG.error("insert child error", e);
            session.rollbackIfExists();
            throw e;
        }

    }

    @Test(groups = Groups.VALUES_INSERT)
    public void returningValuesInsertDiffMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        try {
            session.startTransaction(TransactionOption.option(Isolation.READ_COMMITTED));

            final List<ChinaProvince> resultList;
            resultList = session.queryList(stmt, ChinaProvince.class, ImmutableArrayList::arrayList);

            Assert.assertEquals(resultList.size(), 2);


            for (ChinaProvince province : resultList) {
                // parent fields
                Assert.assertNull(province.getCreateTime());
                Assert.assertNull(province.getUpdateTime());

                // child fields
                Assert.assertNotNull(province.getId());
                Assert.assertNotNull(province.getGovernor());
                Assert.assertNotNull(province.getProvincialCapital());

            }

            session.commitIfExists();
            LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));
        } catch (Exception e) {
            LOG.error("insert child error", e);
            session.rollbackIfExists();
            throw e;
        }
    }


    @Test(groups = Groups.VALUES_INSERT)
    public void returningMapValuesInsertChildWithTowStmtQueryMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .returningAll()
                .asReturningInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);


        try {
            session.startTransaction(TransactionOption.option(Isolation.READ_COMMITTED));

            final List<Map<String, Object>> resultList;
            resultList = session.queryObjectList(stmt, ImmutableHashMap::hashMap, ImmutableArrayList::arrayList);

            Assert.assertEquals(resultList.size(), 2);


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
            session.commitIfExists();
            LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));
        } catch (Exception e) {
            LOG.error("insert child error", e);
            session.rollbackIfExists();
            throw e;
        }

    }


    @Test(groups = Groups.VALUES_INSERT)
    public void returningMapValuesInsertDiffMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);


        try {
            session.startTransaction(TransactionOption.option(Isolation.READ_COMMITTED));

            final List<Map<String, Object>> resultList;
            resultList = session.queryObjectList(stmt, ImmutableHashMap::hashMap, ImmutableArrayList::arrayList);

            Assert.assertEquals(resultList.size(), 2);


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

            session.commitIfExists();
            LOG.debug("resultList:\n{}", JSON.toJSONString(resultList));
        } catch (Exception e) {
            LOG.error("insert child error", e);
            session.rollbackIfExists();
            throw e;
        }

    }

    /*-------------------below query insert syntax-------------------*/

    @Test(groups = Groups.QUERY_INSERT, dependsOnGroups = {Groups.DOMAIN_INSERT, Groups.VALUES_INSERT})
    public void queryInsertParent(final SyncLocalSession session) {
        assert HistoryChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Insert stmt;
        stmt = Postgres.singleInsert()
                .migration()
                .insertInto(HistoryChinaRegion_.T)
                .space()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.regionType::equal, SQLs::literal, RegionType.NONE)
                .and(SQLs::notExists, Postgres.subQuery()
                        .select(HistoryChinaRegion_.id)
                        .from(HistoryChinaRegion_.T, AS, "h")
                        .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                        ::asQuery
                )
                .limit(SQLs::literal, 2)
                .asQuery()
                .asInsert();

        final long rows;
        rows = session.update(stmt);
        LOG.debug("query insert rows : {}", rows);
        Assert.assertTrue(rows > 0);
    }

    @Test(groups = Groups.QUERY_INSERT, dependsOnGroups = {Groups.DOMAIN_INSERT, Groups.VALUES_INSERT})
    public void returningQueryInsertParent(final SyncLocalSession session) {
        assert HistoryChinaRegion_.id.generatorType() == GeneratorType.POST;

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .migration()
                .insertInto(HistoryChinaRegion_.T)
                .space()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.regionType::equal, SQLs::literal, RegionType.NONE)
                .and(SQLs::notExists, Postgres.subQuery()
                        .select(HistoryChinaRegion_.id)
                        .from(HistoryChinaRegion_.T, AS, "h")
                        .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                        ::asQuery
                )
                .limit(SQLs::literal, 2)
                .asQuery()
                .returningAll()
                .asReturningInsert();

        final List<ChinaRegion<?>> resultList;
        resultList = session.queryList(stmt, ChinaRegion_.CLASS, ImmutableArrayList::arrayList);


        Assert.assertTrue(resultList.size() > 0);
        LOG.debug("query insert rows : {}\n resultList : {}", resultList.size(), JSON.toJSONString(resultList));


    }

    @Test//(groups = Groups.QUERY_INSERT, dependsOnGroups = {Groups.DOMAIN_INSERT, Groups.VALUES_INSERT})
    public void returningQueryInsertChildWithTwoStmtQueryMode(final SyncLocalSession session) {
        //TODO consider child table query insert two stmt mode reasonable ？for example firebird
        assert HistoryChinaRegion_.id.generatorType() == GeneratorType.POST;

        final int maxRowCount = 10;

        final Select select;
        select = Postgres.query()
                .select(ChinaRegion_.id)
                .from(ChinaProvince_.T, AS, "p")
                .join(ChinaRegion_.T, AS, "c").on(ChinaProvince_.id::equal, ChinaRegion_.id)
                .where(SQLs::notExists, Postgres.subQuery()
                        .select(HistoryChinaProvince_.id)
                        .from(HistoryChinaProvince_.T, AS, "h")
                        .join(HistoryChinaRegion_.T, AS, "hc").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                        .where(HistoryChinaProvince_.id::equal, ChinaProvince_.id)
                        ::asQuery
                )
                .limit(SQLs::literal, maxRowCount)
                .asQuery();

        final List<Long> idList;
        idList = session.queryList(select, Long.class);

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .migration()
                .insertInto(HistoryChinaRegion_.T)
                .space()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id::in, SPACE, SQLs::rowParam, idList)
                .and(ChinaRegion_.regionType::equal, SQLs::literal, RegionType.PROVINCE)
                .orderBy(ChinaRegion_.id)
                .asQuery()
                .returningAll()
                .asReturningInsert()

                .child()

                .insertInto(HistoryChinaProvince_.T)
                .space()
                .select("p", PERIOD, ChinaProvince_.T)
                .from(ChinaProvince_.T, AS, "p")
                .join(ChinaRegion_.T, AS, "c").on(ChinaProvince_.id::equal, ChinaRegion_.id)
                .where(ChinaRegion_.id.in(SQLs::rowParam, idList))
                .orderBy(ChinaProvince_.id)
                .asQuery()
                .returningAll()
                .asReturningInsert();

        try {
            session.startTransaction(TransactionOption.option(Isolation.READ_COMMITTED));

            final List<ChinaProvince> resultList;
            resultList = session.queryList(stmt, ChinaProvince.class, ImmutableArrayList::arrayList);


            final int resultRows = resultList.size();
            Assert.assertTrue(resultRows > 0 && resultRows <= maxRowCount);

            session.commitIfExists();
            LOG.debug("query insert rows : {}\n resultList : {}", resultList.size(), JSON.toJSONString(resultList));
        } catch (Exception e) {
            session.rollbackIfExists();
            throw e;
        }

    }


    @Test
    public void subDomainInsertChild(final SyncLocalSession session) {
        assert HistoryChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        final Insert stmt;

        stmt = Postgres.singleInsert()
                .literalMode(LiteralMode.LITERAL)
                .with("parent").as(s -> s.literalMode(LiteralMode.LITERAL)
                        .insertInto(ChinaRegion_.T)
                        .values(provinceList)
                        .returning(ChinaRegion_.id)
                        .asReturningInsert()
                ).comma("parent_row_id").as(s -> s.select(ss -> ss.space(Postgres.rowNumber().over().as("rowNumber"))
                                        .comma(SQLs.refThis("parent", ChinaRegion_.ID))
                                )
                                .from("parent")
                                .asQuery()
                )
                .space()
                .insertInto(ChinaProvince_.T)
                .defaultValue(ChinaProvince_.id, Postgres.scalarSubQuery()
                        .select(s -> s.space(SQLs.refThis("p", ChinaRegion_.ID)))
                        .from("parent_row_id", AS, "p")
                        .where(SQLs.refThis("p", "rowNumber")::equal, BATCH_NO_LITERAL)
                        .asQuery()
                )
                .values(provinceList)
                .asInsert();

        Assert.assertFalse(stmt instanceof _ReturningDml);

        final long rows;
        rows = session.update(stmt);
        LOG.debug("{} rows : {}", session.name(), rows);
        Assert.assertEquals(rows, provinceList.size());

    }


}
