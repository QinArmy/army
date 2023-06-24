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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Test(dataProvider = "getSession")
public class PostgreInsertSuiteTests extends PostgreSuiteTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreInsertSuiteTests.class);

    @Test
    public void insertParent(final LocalSession session) {
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
    public void returningInsertParent(final LocalSession session) {
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

        for (ChinaRegion<?> region : resultList) {
            Assert.assertNotNull(region.getId());
        }
        releaseSyncSession(session);

    }

    @Test
    public void insertParentWithDoNothing(final LocalSession session) {
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
    public void insertParentWithUpdateSet(final LocalSession session) {
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
    public void insertChildWithTowStmtMode(final LocalSession session) {
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

        final LocalTransaction tx;
        tx = session.builder()
                .isolation(Isolation.READ_COMMITTED)
                .build();

        try {
            tx.start();
            Assert.assertEquals(session.update(stmt), provinceList.size());
            for (ChinaProvince province : provinceList) {
                Assert.assertNotNull(province.getId());
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
    public void returningInsertChildWithTowStmtMode(final LocalSession session) {
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

        final LocalTransaction tx;
        tx = session.builder()
                .isolation(Isolation.READ_COMMITTED)
                .build();

        try {
            tx.start();
            final List<ChinaProvince> resultList;
            resultList = session.query(stmt, ChinaProvince.class);

            Assert.assertEquals(resultList.size(), provinceList.size());

            for (ChinaProvince province : provinceList) {
                Assert.assertNotNull(province.getId());
            }
            tx.commit();
            LOG.debug("resultList:\n{}", resultList);
        } catch (Exception e) {
            LOG.error("insert child error", e);
            tx.rollback();
            throw e;
        }

        releaseSyncSession(session);

    }


}
