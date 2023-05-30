package io.army.criteria.mysql.unit;

import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.example.pill.domain.PillUser_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.impl.SQLs.UNBOUNDED_FOLLOWING;

public class MySQLWindowFuncUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLWindowFuncUnitTests.class);


    @Test
    public void rowNumber() {
        Select stmt;
        stmt = MySQLs.query()
                .select(MySQLs.rowNumber().over(s -> s.range(UNBOUNDED_FOLLOWING)).as("rowNumber"))
                .comma(PillUser_.id, PillUser_.createTime)
                .asQuery();
        LOG.debug("{}", stmt);
    }

}
