package io.army.session.suite.postgre;


import io.army.criteria.Insert;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sync.LocalSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class PostgreInsertSuiteTests extends PostgreSuiteTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreInsertSuiteTests.class);

    @Test
    public void insertParent() {
        final List<ChinaRegion<?>> regionList;
        regionList = this.createReginList();
        final Insert stmt;
        stmt = Postgres.singleInsert()
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T).as("c")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .asInsert();

        final LocalSession session;
        session = getSession();

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, regionList.size());

        for (ChinaRegion<?> region : regionList) {
            Assert.assertNotNull(region.getId());
        }

        releaseSyncSession(session);
    }


}
