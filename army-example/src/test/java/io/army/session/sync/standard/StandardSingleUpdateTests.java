package io.army.session.sync.standard;


import io.army.criteria.BatchUpdate;
import io.army.criteria.Expression;
import io.army.criteria.Update;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sync.SyncLocalSession;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.army.criteria.impl.SQLs.AND;
import static io.army.criteria.impl.SQLs.AS;

@Test(dataProvider = "localSessionProvider"
        , dependsOnGroups = "standardInsert"
)
public class StandardSingleUpdateTests extends StandardSyncSessionSupport {


    @Test
    public void updateParent(final SyncLocalSession session) {
        final BigDecimal gdpAmount = new BigDecimal("88888.66");
        final LocalDateTime now = LocalDateTime.now();

        final Update stmt;
        stmt = SQLs.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .where(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, gdpAmount, Expression::greaterEqual, BigDecimal.ZERO)
                .asUpdate();

        final long rows;
        rows = session.update(stmt);
        LOG.debug("session[name : {}] update {} rows", session.name(), rows);

    }


    @Test
    public void batchUpdateParent(final SyncLocalSession session) {
        final LocalDateTime now = LocalDateTime.now();

        final List<Map<String, Object>> paramList = new ArrayList<>();
        Map<String, Object> map;

        map = new HashMap<>();
        map.put(ChinaRegion_.REGION_GDP, new BigDecimal("88888.66"));
        paramList.add(map);

        map = new HashMap<>();
        map.put(ChinaRegion_.REGION_GDP, new BigDecimal("9999.66"));
        paramList.add(map);

        final BatchUpdate stmt;
        stmt = SQLs.batchSingleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .setSpace(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .where(ChinaRegion_.createTime::between, SQLs::param, now.minusMinutes(10), AND, now)
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, BigDecimal.ZERO)
                .asUpdate()
                .namedParamList(paramList);

        final List<Long> rowList;
        rowList = session.batchUpdate(stmt);

        Assert.assertEquals(rowList.size(), paramList.size());
        LOG.debug("session[name : {}] update {} rows", session.name(), rowList);

    }


}
