package io.army.criteria.postgre.unit;

import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.mapping.postgre.PostgreSingleRangeType;
import io.army.sqltype.PostgreSqlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.impl.Postgres.lower;
import static io.army.criteria.impl.Postgres.upper;

public class PostgreTypeUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreTypeUnitTests.class);

    /**
     * @see Postgres#upper(Expression)
     * @see Postgres#lower(Expression)
     * @see PostgreSingleRangeType#subtype()
     * @see PostgreSingleRangeType.UserDefinedRangeType#subtype()
     */
    @Test
    public void rangeSubtypeForLowerAndUpperFunc() {
        final PostgreSingleRangeType int4RangeType;
        int4RangeType = PostgreSingleRangeType.from(String.class, PostgreSqlType.INT4RANGE);
        final Select stmt;
        stmt = Postgres.query()
                .select(lower(SQLs.literal(int4RangeType, "[1,4]")).as("lower subtype"))
                .comma(upper(SQLs.literal(int4RangeType, "[1,4]")).as("upper subtype"))
                .asQuery();

        printStmt(LOG, stmt);
    }


}
