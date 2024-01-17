package io.army.session.sync.standard;


import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.Windows;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sync.SyncLocalSession;
import io.army.util.RowMaps;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.UNBOUNDED_PRECEDING;

@Test(dataProvider = "localSessionProvider")
public class StandardQueryTests extends StandardSessionSupport {


    @Transactional
    @Test(invocationCount = 3)
    public void query20WindowClause(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(3);
        session.batchSave(regionList);

        final List<Long> regionIdList = extractRegionIdList(regionList);

        final long startNanoSecond = System.nanoTime();

        final Select stmt;
        stmt = SQLs.query20()
                .select(ChinaProvince_.id, Windows.sum(ChinaRegion_.regionGdp).over("w").as("gdpSum"))
                .from(ChinaProvince_.T, AS, "p")
                .join(ChinaRegion_.T, AS, "c").on(ChinaProvince_.id::equal, ChinaRegion_.id)
                .where(ChinaProvince_.id.in(SQLs::rowParam, regionIdList))
                .window("w").as(s -> s.partitionBy(ChinaRegion_.name).orderBy(ChinaRegion_.regionGdp::desc).rows(UNBOUNDED_PRECEDING))
                .asQuery();

        statementCostTimeLog(session, LOG, startNanoSecond);


        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);
        Assert.assertEquals(rowList.size(), regionIdList.size());
    }

}
