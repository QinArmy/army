package io.army.criteria.postgre.unit;

import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.impl.SQLs.AS;

public class PostgreOperatorUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreOperatorUnitTests.class);

    @Test
    public void similarTo() {
        Select stmt;
        stmt = Postgres.query()
                .select(ChinaRegion_.id)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.name::whiteSpace, Postgres::notSimilarTo, SQLs::literal, "%(b|d)%")
                .and(ChinaRegion_.name::whiteSpace, Postgres::similarTo, SQLs::literal, "%(b|d)%")
                .and(ChinaRegion_.name.whiteSpace(Postgres::notSimilarTo, SQLs::literal, "%(b|d)%", SQLs.ESCAPE, '|'))
                .and(ChinaRegion_.name.whiteSpace(Postgres::similarTo, SQLs::literal, "Hong Kong"))
                .and(ChinaRegion_.name.whiteSpace(Postgres::similarTo, SQLs::literal, "Hong |_ong", SQLs.ESCAPE, '|'))
                .asQuery();

        printStmt(LOG, stmt);
    }


}
