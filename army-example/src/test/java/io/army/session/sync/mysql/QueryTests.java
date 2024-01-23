package io.army.session.sync.mysql;

import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sync.SyncLocalSession;
import io.army.util.RowMaps;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.army.criteria.impl.SQLs.*;


@Test(dataProvider = "localSessionProvider")
public class QueryTests extends SynSessionTestSupport {


    /**
     * <p>Test following :
     * <ul>
     *     <li>Bracket CriteriaContext migration</li>
     *     <li>WITH clause migration</li>
     *     <li>parens WITH clause parsing</li>
     * </ul>
     */
    @Test(invocationCount = 3)
    public void contextMigration(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(2);
        session.batchSave(regionList);

        final Long firstId, secondId;
        firstId = regionList.get(0).getId();
        secondId = regionList.get(1).getId();
        assert firstId != null && secondId != null;

        final Select stmt;
        stmt = MySQLs.query()
                .with("cte").as(s -> s.select(ChinaRegion_.id, ChinaRegion_.population)
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.id::equal, SQLs::literal, firstId)
                        .asQuery()
                ).space()
                .parens(c -> c.select(s -> s.space("cte", PERIOD, ASTERISK))
                        .from("cte")
                        .where(SQLs.refField("cte", ChinaRegion_.ID).equal(SQLs.literalValue(firstId)))
                        .asQuery()
                ).unionAll()
                .parens(p -> p.with("cte20").as(s -> s.select(ChinaRegion_.id, ChinaRegion_.population)
                                        .from(ChinaRegion_.T, AS, "t")
                                        .where(ChinaRegion_.id::equal, SQLs::literal, secondId)
                                        .asQuery()
                                ).space()
                                .parens(c -> c.select(s -> s.space("cte20", PERIOD, ASTERISK))
                                        .from("cte20")
                                        .where(SQLs.refField("cte20", ChinaRegion_.ID).equal(SQLs.literalValue(secondId)))
                                        .asQuery()
                                ).asQuery()
                ).unionAll()
                .values()
                .row(r -> r.space(LITERAL_1, LITERAL_1))
                .unionAll()
                .parens(p -> p.values()
                        .row(r -> r.space(LITERAL_0, LITERAL_1))
                        .asValues()
                ).orderBy(SQLs.refSelection(ChinaRegion_.ID), SQLs.refSelection(2)) // test ref left context selection
                .limit(SQLs::literal, 4)
                .asValues();

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);
        Assert.assertEquals(rowList.size(), 4);

    }
}
