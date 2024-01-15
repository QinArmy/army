package io.army.session.sync.standard;


import io.army.criteria.BatchDelete;
import io.army.criteria.Delete;
import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import io.army.sync.SyncLocalSession;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.army.criteria.impl.SQLs.AND;
import static io.army.criteria.impl.SQLs.AS;

@Test(dataProvider = "localSessionProvider"
        , groups = "standardDelete"
        , dependsOnGroups = "standardInsert"
)
public class StandardDeleteTests extends StandardSessionSupport {


    @Test
    public void deleteParent(final SyncLocalSession session) {
        final LocalDateTime now = LocalDateTime.now();

        final Select queryStmt;
        queryStmt = SQLs.query()
                .select(ChinaRegion_.id)
                .from(ChinaRegion_.T, AS, "cc")
                .where(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                .orderBy(ChinaRegion_.id)
                .limit(SQLs::param, 2)
                .asQuery();

        final List<Long> idList;
        idList = session.queryList(queryStmt, Long.class);


        final Delete stmt;
        stmt = SQLs.singleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.id.in(SQLs::rowParam, idList))
                .asDelete();

        final long rows;
        rows = session.update(stmt);
        LOG.debug("session[name : {}] rows {}", session.name(), rows);
    }


    @Test(dependsOnMethods = {"deleteParent"})  // avoid deadlock
    public void batchDeleteParent(final SyncLocalSession session) {
        final LocalDateTime now = LocalDateTime.now();

        final Select queryStmt;
        queryStmt = SQLs.query()
                .select(ChinaRegion_.id)
                .from(ChinaRegion_.T, AS, "cc")
                .where(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                .orderBy(ChinaRegion_.id)
                .limit(SQLs::param, 3)
                .asQuery();

        final List<Long> idList;
        idList = session.queryList(queryStmt, Long.class);

        final List<Map<String, Object>> paramList = new ArrayList<>(idList.size());
        for (Long id : idList) {
            paramList.add(Collections.singletonMap(ChinaRegion_.ID, id));
        }


        final BatchDelete stmt;
        stmt = SQLs.batchSingleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.id::equal, SQLs::namedParam)
                .asDelete()
                .namedParamList(paramList);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);
        Assert.assertEquals(rowList.size(), paramList.size());
        LOG.debug("session[name : {}] rows {}", session.name(), rowList);
    }

    @Test(dependsOnMethods = {"deleteParent", "batchDeleteParent"})  // avoid deadlock
    public void delete20Parent(final SyncLocalSession session) {
        final LocalDateTime now = LocalDateTime.now();

        final Delete stmt;
        stmt = SQLs.singleDelete20()
                .with("idListCte").as(c -> c.select(ChinaRegion_.id)
                        .from(ChinaRegion_.T, AS, "t")
                        .where(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                        .orderBy(ChinaRegion_.id)
                        .limit(SQLs::param, 2)
                        .asQuery()
                ).space()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id::in, SQLs.subQuery()
                        .select(s -> s.space(SQLs.refField("cte", "id")))
                        .from("idListCte", AS, "cte")
                        ::asQuery
                )
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .asDelete();

        final long rows;
        rows = session.update(stmt);
        LOG.debug("session[name : {}] rows {}", session.name(), rows);
    }


    @Test(dependsOnMethods = {"deleteParent", "batchDeleteParent", "delete20Parent"}) // avoid deadlock
    public void batchDelete20Parent(final SyncLocalSession session) {
        final LocalDateTime now = LocalDateTime.now();

        final Select queryStmt;
        queryStmt = SQLs.query()
                .select(ChinaRegion_.id)
                .from(ChinaRegion_.T, AS, "cc")
                .where(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                .orderBy(ChinaRegion_.id)
                .limit(SQLs::param, 3)
                .asQuery();

        final List<Long> idList;
        idList = session.queryList(queryStmt, Long.class);

        final List<Map<String, Object>> paramList = new ArrayList<>(idList.size());
        for (Long id : idList) {
            paramList.add(Collections.singletonMap(ChinaRegion_.ID, id));
        }


        final BatchDelete stmt;
        stmt = SQLs.batchSingleDelete20()
                .with("cte").as(c -> c.select(ChinaRegion_.name)
                        .from(ChinaRegion_.T, AS, "cc")
                        .where(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                        .orderBy(ChinaRegion_.id)
                        .limit(SQLs::param, 3)
                        .asQuery()
                ).space()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.name::in, SQLs.subQuery()
                        .select(s -> s.space(SQLs.refField("cte", "name")))
                        .from("cte")
                        ::asQuery
                )
                .asDelete()
                .namedParamList(paramList);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);
        Assert.assertEquals(rowList.size(), paramList.size());
        LOG.debug("session[name : {}] rows {}", session.name(), rowList);
    }


}
