package io.army.criteria.postgre;

import io.army.criteria.BatchUpdate;
import io.army.criteria.Update;
import io.army.criteria.dialect.BatchReturningUpdate;
import io.army.criteria.dialect.ReturningUpdate;
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

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

public class PostgreUpdateUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreUpdateUnitTests.class);


    @Test
    public void updateParent() {
        final Update stmt;
        stmt = Postgres.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.name, SQLs::param, this::randomCity)
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::literal, new BigDecimal("100.00"))
                .set(ChinaRegion_.regionGdp, ChinaRegion_.population, () -> Postgres.subQuery()
                        .select(HistoryChinaRegion_.regionGdp, HistoryChinaRegion_.population)
                        .from(HistoryChinaRegion_.T, AS, "h")
                        .where(HistoryChinaRegion_.id::equal, SQLs::literal, 1)
                        .asQuery()
                )
                .from(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .asUpdate();

        printStmt(LOG, stmt);
    }

    @Test
    public void returningUpdateParent() {
        final ReturningUpdate stmt;
        stmt = Postgres.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.name, SQLs::param, this::randomCity)
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::literal, new BigDecimal("100.00"))
                .set(ChinaRegion_.regionGdp, ChinaRegion_.population, () -> Postgres.subQuery()
                        .select(HistoryChinaRegion_.regionGdp, HistoryChinaRegion_.population)
                        .from(HistoryChinaRegion_.T, AS, "h")
                        .where(HistoryChinaRegion_.id::equal, SQLs::literal, 1)
                        .asQuery()
                )
                .from(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .returning("c", PERIOD, ChinaRegion_.T)
                .comma(HistoryChinaRegion_.id)
                .asReturningUpdate();

        printStmt(LOG, stmt);
    }


    @Test
    public void batchUpdateParent() {
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


        final BatchUpdate stmt;
        stmt = Postgres.batchSingleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.name, SQLs::param, this::randomCity)
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .set(ChinaRegion_.regionGdp, ChinaRegion_.population, () -> Postgres.subQuery()
                        .select(HistoryChinaRegion_.regionGdp, HistoryChinaRegion_.population)
                        .from(HistoryChinaRegion_.T, AS, "h")
                        .where(HistoryChinaRegion_.id::equal, SQLs::literal, 1)
                        .asQuery()
                )
                .from(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .and(ChinaRegion_.id::equal, SQLs::namedParam)
                .namedParamList(paramList)
                .asUpdate();

        printStmt(LOG, stmt);
    }

    @Test
    public void batchReturningUpdateParent() {
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


        final BatchReturningUpdate stmt;
        stmt = Postgres.batchSingleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.name, SQLs::param, this::randomCity)
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .set(ChinaRegion_.regionGdp, ChinaRegion_.population, () -> Postgres.subQuery()
                        .select(HistoryChinaRegion_.regionGdp, HistoryChinaRegion_.population)
                        .from(HistoryChinaRegion_.T, AS, "h")
                        .where(HistoryChinaRegion_.id::equal, SQLs::literal, 1)
                        .asQuery()
                )
                .from(HistoryChinaRegion_.T, AS, "hc")
                .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                .and(ChinaRegion_.id::equal, SQLs::namedParam)
                .returning("c", PERIOD, ChinaRegion_.T)
                .namedParamList(paramList)
                .asReturningUpdate();


        printStmt(LOG, stmt);
    }


}
