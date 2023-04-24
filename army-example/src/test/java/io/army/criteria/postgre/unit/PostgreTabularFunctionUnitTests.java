package io.army.criteria.postgre.unit;

import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.mapping.postgre.PostgreTsVectorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.impl.Postgres.unnest;
import static io.army.criteria.impl.SQLs.ASTERISK;
import static io.army.criteria.impl.SQLs.PERIOD;

public class PostgreTabularFunctionUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreTabularFunctionUnitTests.class);

    /**
     * @see Postgres#unnest(Expression)
     */
    @Test
    public void unnestFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select("a", PERIOD, ASTERISK)
                .from(() -> unnest(SQLs.literal(PostgreTsVectorType.INSTANCE, "cat:3 fat:2,4 rat:5A"))
                        .withOrdinality()
                )
                .as("a")
                .where(SQLs.refThis("a", "lexeme")::equal, SQLs::literal, "cat")
                .asQuery();

        printStmt(LOG, stmt);

    }


}
