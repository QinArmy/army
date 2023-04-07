package io.army.criteria.postgre;

import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.pill.domain.PillUser_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.impl.SQLs.AS;

public class PostgreQueryUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreQueryUnitTests.class);

    @Test
    public void dynamicWindow() {
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literalFrom(1)::as, "r")
                .from(PillUser_.T, AS, "u")
                .windows(w -> {
                    w.window("w1").as(s -> s.partitionBy(PillUser_.userType).orderBy(PillUser_.id));
                    w.window("w2").as(s -> s.orderBy(PillUser_.id));
                })
                .asQuery();

        printStmt(LOG, stmt);
    }


}
