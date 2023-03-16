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
    public void window() {
        final Select stmt;
        Postgres.query()
                .select(SQLs.literalFrom(1)::as, "r")
                .from(PillUser_.T, AS, "u")
                .windows(s -> {
                    s.window("w1").as().partitionBy(PillUser_.userType)
                            .orderBy(PillUser_.id);

                    s.window("w2").as().orderBy(PillUser_.id);
                })
                .asQuery();
    }


}
