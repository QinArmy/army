package io.army.session.sync.standard;


import io.army.criteria.BatchDelete;
import io.army.criteria.Delete;
import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaProvince_;
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
        // , dependsOnGroups = "standardInsert"
)
public class DomainDeleteTests extends StandardSessionSupport {

    @Test
    public void deleteParent(final SyncLocalSession session) {
        final LocalDateTime now = LocalDateTime.now();

        final Select queryStmt;
        queryStmt = SQLs.query()
                .select(ChinaRegion_.id)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                .orderBy(ChinaRegion_.id::desc)
                .limit(SQLs::param, 2)
                .asQuery();

        final List<Long> idList;
        idList = session.queryList(queryStmt, Long.class);


        final Delete stmt;
        stmt = SQLs.domainDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, idList))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .asDelete();

        final long rows;
        rows = session.update(stmt);
        LOG.debug("session[name : {}] rows {}", session.name(), rows);
    }


    @Test
    public void batchDeleteParent(final SyncLocalSession session) {
        final LocalDateTime now = LocalDateTime.now();

        final Select queryStmt;
        queryStmt = SQLs.query()
                .select(ChinaRegion_.id)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                .orderBy(ChinaRegion_.id::desc)
                .limit(SQLs::param, 3)
                .asQuery();

        final List<Long> idList;
        idList = session.queryList(queryStmt, Long.class);

        final List<Map<String, Object>> paramList = new ArrayList<>(idList.size());
        for (Long id : idList) {
            paramList.add(Collections.singletonMap(ChinaRegion_.ID, id));
        }

        final BatchDelete stmt;
        stmt = SQLs.batchDomainDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .asDelete()
                .namedParamList(paramList);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);
        Assert.assertEquals(rowList.size(), paramList.size());
        LOG.debug("session[name : {}] rows {}", session.name(), rowList);
    }

    @Test
    public void deleteChild(final SyncLocalSession session) {
        final LocalDateTime now = LocalDateTime.now();

        final Select queryStmt;
        queryStmt = SQLs.query()
                .select(ChinaProvince_.id)
                .from(ChinaProvince_.T, AS, "p")
                .join(ChinaRegion_.T, AS, "c").on(ChinaRegion_.id::equal, ChinaProvince_.id)
                .orderBy(ChinaRegion_.id::desc)
                .limit(SQLs::param, 2)
                .asQuery();

        final List<Long> idList;
        idList = session.queryList(queryStmt, Long.class);


        final Delete stmt;
        stmt = SQLs.domainDelete()
                .deleteFrom(ChinaProvince_.T, AS, "p")
                .where(ChinaProvince_.id.in(SQLs::rowParam, idList))
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .asDelete();

        final long rows;
        rows = session.update(stmt);
        LOG.debug("session[name : {}] rows {}", session.name(), rows);
    }


    @Test
    public void batchDeleteChild(final SyncLocalSession session) {
        final LocalDateTime now = LocalDateTime.now();

        final Select queryStmt;
        queryStmt = SQLs.query()
                .select(ChinaProvince_.id)
                .from(ChinaProvince_.T, AS, "p")
                .join(ChinaRegion_.T, AS, "c").on(ChinaRegion_.id::equal, ChinaProvince_.id)
                .orderBy(ChinaRegion_.id::desc)
                .limit(SQLs::param, 2)
                .asQuery();

        final List<Long> idList;
        idList = session.queryList(queryStmt, Long.class);

        final List<Map<String, Object>> paramList = new ArrayList<>(idList.size());
        for (Long id : idList) {
            paramList.add(Collections.singletonMap(ChinaRegion_.ID, id));
        }

        final BatchDelete stmt;
        stmt = SQLs.batchDomainDelete()
                .deleteFrom(ChinaProvince_.T, AS, "p")
                .where(ChinaProvince_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .asDelete()
                .namedParamList(paramList);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);
        Assert.assertEquals(rowList.size(), paramList.size());
        LOG.debug("session[name : {}] rows {}", session.name(), rowList);
    }


}
