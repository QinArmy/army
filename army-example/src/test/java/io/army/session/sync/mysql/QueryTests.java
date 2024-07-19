package io.army.session.sync.mysql;

import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.session.SyncLocalSession;
import io.army.util.RowMaps;
import io.army.util._Collections;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.army.criteria.impl.SQLs.*;


@Test(dataProvider = "localSessionProvider")
public class QueryTests extends SessionTestSupport {


    /**
     * <p>Test following :
     * <ul>
     *     <li>Bracket CriteriaContext migration</li>
     *     <li>WITH clause migration</li>
     *     <li>parens WITH clause parsing</li>
     * </ul>
     */
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
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
                ).orderBy(SQLs.refSelection(ChinaRegion_.ID)::desc, SQLs.refSelection(2)::asc) // test ref left context selection
                .limit(SQLs::literal, 4)
                .asValues();

        final List<Map<String, Object>> rowList;
        rowList = session.queryObject(stmt, RowMaps::hashMap)
                .collect(Collectors.toCollection(_Collections::arrayList));

        Assert.assertEquals(rowList.size(), 4);

    }

    @Test
    public void cteValues(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(2);
        session.batchSave(regionList);

        final Select stmt;
        stmt = MySQLs.query()
                .with("data").as(sw -> sw.values()
                        .row(s -> s.space(1))
                        .asValues()
                ).space()
                .select(ChinaRegion_.id)
                .from(ChinaRegion_.T, AS, "t")
                .crossJoin("data")
                .where(ChinaRegion_.id.in(SQLs::rowLiteral, extractRegionIdList(regionList)))
                .asQuery();

        final List<Integer> rowList;
        rowList = session.queryList(stmt, Integer.class);
        Assert.assertEquals(rowList.size(), regionList.size());
    }


}
