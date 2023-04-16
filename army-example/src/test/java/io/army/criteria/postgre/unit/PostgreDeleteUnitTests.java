package io.army.criteria.postgre.unit;

import io.army.criteria.BatchDelete;
import io.army.criteria.Delete;
import io.army.criteria.dialect.BatchReturningDelete;
import io.army.criteria.dialect.ReturningDelete;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.HistoryChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.army.criteria.impl.SQLs.*;

public class PostgreDeleteUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreDeleteUnitTests.class);


    @Test
    public void deleteParent() {
        final Delete stmt;
        stmt = Postgres.singleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .using(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .and(ChinaRegion_.id::equal, SQLs::literal, 1)
                .asDelete();

        printStmt(LOG, stmt);
    }

    @Test
    public void returningDeleteParent() {
        final ReturningDelete stmt;
        stmt = Postgres.singleDelete()
                .deleteFrom(ONLY, ChinaRegion_.T, ASTERISK, AS, "c")
                .using(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .and(ChinaRegion_.id::equal, SQLs::literal, 1)
                .returning("c", PERIOD, ChinaRegion_.T)
                .comma(HistoryChinaRegion_.id)
                .asReturningDelete();

        printStmt(LOG, stmt);
    }


    @Test
    public void batchDeleteParent() {
        Map<String, Object> paramMap;
        final List<Map<String, Object>> paramList = new ArrayList<>();

        paramMap = new HashMap<>();
        paramMap.put(HistoryChinaRegion_.ID, 1);
        paramMap.put(ChinaRegion_.REGION_GDP, new BigDecimal("100.00"));
        paramList.add(paramMap);

        paramMap = new HashMap<>();
        paramMap.put(HistoryChinaRegion_.ID, 2);
        paramMap.put(ChinaRegion_.REGION_GDP, new BigDecimal("999.00"));
        paramList.add(paramMap);


        final BatchDelete stmt;
        stmt = Postgres.batchSingleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .using(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .and(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.regionGdp::less, SQLs::namedParam)
                .namedParamList(paramList)
                .asDelete();

        printStmt(LOG, stmt);
    }

    @Test//(invocationCount = 100)
    public void batchReturningDeleteParent() {
        Map<String, Object> paramMap;
        final List<Map<String, Object>> paramList = new ArrayList<>();

        paramMap = new HashMap<>();
        paramMap.put(HistoryChinaRegion_.ID, 1);
        paramMap.put(ChinaRegion_.REGION_GDP, new BigDecimal("100.00"));
        paramList.add(paramMap);

        paramMap = new HashMap<>();
        paramMap.put(HistoryChinaRegion_.ID, 2);
        paramMap.put(ChinaRegion_.REGION_GDP, new BigDecimal("999.00"));
        paramList.add(paramMap);


        final BatchReturningDelete stmt;
        stmt = Postgres.batchSingleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .using(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .and(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.regionGdp::less, SQLs::namedParam)
                .returning("c", PERIOD, ChinaRegion_.T)
                .namedParamList(paramList)
                .asReturningDelete();


        printStmt(LOG, stmt);

    }


}
