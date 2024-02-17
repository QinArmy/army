package io.army.session.sync.mysql;


import com.alibaba.fastjson2.JSON;
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

import static io.army.criteria.impl.MySQLs.atAtSession;
import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class VariableTests extends SessionTestSupport {


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void rowNumber(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(10);
        session.batchSave(regionList);

        final Select stmt;
        stmt = MySQLs.query()
                .select(s -> s.space(MySQLs.at("my_row_number").increment().as("rowNumber"))
                        .comma("t", PERIOD, ChinaRegion_.T)
                )
                .from(ChinaRegion_.T, AS, "t")
                .crossJoin(SQLs.subQuery()
                        .select(MySQLs.at("my_row_number", SQLs.COLON_EQUAL, SQLs.LITERAL_0).as("n"))
                        .asQuery()
                ).as("s")
                .where(ChinaRegion_.id.in(SQLs::rowLiteral, extractRegionIdList(regionList)))
                .orderBy(ChinaRegion_.id)
                .asQuery();

        final List<Map<String, Object>> rowList;
        rowList = session.queryObjectList(stmt, RowMaps::hashMap);
        final int rowSize = rowList.size();
        Assert.assertEquals(rowSize, regionList.size());

        for (int i = 0; i < rowSize; i++) {
            Assert.assertEquals(rowList.get(i).get("rowNumber"), i + 1);
        }
    }

    @Test
    public void readSystemVariable(final SyncLocalSession session) {
        final Select stmt;
        stmt = MySQLs.query()
                .select(atAtSession("autocommit").as("autoCommit"), atAtSession("sql_mode").as("sqlMode"))
                .asQuery();

        final Map<String, Object> row;
        row = session.queryOneObject(stmt, RowMaps::hashMap);

        LOG.debug("{} row :\n{}", session.name(), JSON.toJSONString(row));
    }


}
