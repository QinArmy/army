package io.army.session.sync.mysql;


import com.alibaba.fastjson2.JSON;
import io.army.criteria.Select;
import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.standard.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sync.SyncLocalSession;
import io.army.util.RowMaps;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.army.criteria.impl.MySQLs.atAtGlobal;
import static io.army.criteria.impl.MySQLs.atAtSession;
import static io.army.criteria.standard.SQLs.AS;
import static io.army.criteria.standard.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class VariableTests extends SessionTestSupport {


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void rowNumber(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(10);
        session.batchSave(regionList);

        final Select stmt;
        stmt = MySQLs.query()
                .select(s -> s.space(MySQLs.at("my_row_number").increment().as("rowNumber")) // NOTE : here is defer SELECT clause, so SELECT clause is executed after FROM clause
                        .comma("t", PERIOD, ChinaRegion_.T)
                )
                .from(ChinaRegion_.T, AS, "t")
                .crossJoin(SQLs.subQuery()
                        .select(MySQLs.at("my_row_number", SQLs.COLON_EQUAL, SQLs.LITERAL_0).as("n"))
                        .asQuery()
                ).as("s")
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
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

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void readSystemVariable(final SyncLocalSession session) {
        final Select stmt;
        stmt = MySQLs.query()
                .select(atAtSession("autocommit").as("sessionAutoCommit"), atAtSession("sql_mode").as("sessionSqlMode"))
                .comma(atAtGlobal("autocommit").as("globalAutoCommit"), atAtGlobal("sql_mode").as("globalSqlMode"))
                .asQuery();

        final Map<String, Object> row;
        row = session.queryOneObject(stmt, RowMaps::hashMap);

        Assert.assertNotNull(row);
        Assert.assertEquals(row.size(), 4);
        Assert.assertEquals(row.get("sessionAutoCommit"), true);
        Assert.assertTrue(row.get("sessionSqlMode") instanceof String);

        Assert.assertEquals(row.get("globalAutoCommit"), true);
        Assert.assertTrue(row.get("globalSqlMode") instanceof String);

        LOG.debug("{} row :\n{}", session.name(), JSON.toJSONString(row));
    }


    /**
     * <p>Test {@link MySQLs#setStmt()}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/set-variable.html">SET Syntax for Variable Assignment</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/server-system-variables.html">Server System Variables</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/user-variables.html">User-Defined Variables</a>
     * @since 0.6.6
     */
    @Test
    public void simpleSetStmt(final SyncLocalSession session) {

        final DmlCommand stmt;
        stmt = MySQLs.setStmt()
                .set(SQLs.AT, "my_null", null, SQLs.AT, "my_scalar", SQLs.scalarSubQuery()
                        .select(SQLs.space("1").as("r"))
                        .asQuery()
                ).comma(SQLs.SESSION, "autocommit", false)
                .comma(SQLs.SESSION, "sql_mode", SQLs.scalarSubQuery()
                        .select(MySQLs.atAtSession("sql_mode").as("r"))
                        .asQuery()
                ).asCommand();

        final long rows;
        rows = session.update(stmt);
        LOG.debug("{} rows : {}", session.name(), rows);

    }


}
