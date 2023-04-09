package io.army.criteria.postgre;

import io.army.criteria.Update;
import io.army.criteria.dialect.ReturningUpdate;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.HistoryChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static io.army.criteria.impl.SQLs.AS;

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
                .returning(ChinaRegion_.id)
                .asReturningUpdate();

        printStmt(LOG, stmt);
    }


}
