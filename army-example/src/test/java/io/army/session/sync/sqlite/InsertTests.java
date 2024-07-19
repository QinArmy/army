package io.army.session.sync.sqlite;


import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.session.SyncLocalSession;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Test(dataProvider = "localSessionProvider")
public class InsertTests extends SessionTestSupport {

    @Test(enabled = false)
    public void ddl(final SyncLocalSession session) {

    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainInsertParent(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = SQLs.singleInsert()
                .literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::literal, 0)
                .values(regionList)
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, regionList.size());

        assertChinaRegionAfterNoConflictInsert(regionList);

    }

}
