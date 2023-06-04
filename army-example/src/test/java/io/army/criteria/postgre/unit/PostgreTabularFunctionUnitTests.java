package io.army.criteria.postgre.unit;

import io.army.criteria.ArrayExpression;
import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.mapping.array.IntegerArrayType;
import io.army.mapping.postgre.PostgreTsVectorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.impl.Postgres.array;
import static io.army.criteria.impl.Postgres.unnest;
import static io.army.criteria.impl.SQLs.*;

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
                .from(unnest(SQLs.literal(PostgreTsVectorType.INSTANCE, "cat:3 fat:2,4 rat:5A"))
                        ::withOrdinality
                )
                .as("a")
                .where(SQLs.refThis("a", "lexeme")::equal, SQLs::literal, "cat")
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#unnest(io.army.criteria.ArrayExpression)
     */
    @Test
    public void unnestArray() {
        final Select stmt;
        stmt = Postgres.query()
                .select("a", PERIOD, ASTERISK)
                .comma("b", PERIOD, ASTERISK)
                .from(unnest(SQLs.literal(IntegerArrayType.LINEAR, new int[]{1, 2}))
                        ::withOrdinality
                )
                .as("a").parens("value", "ordinal")
                .crossJoin(unnest(array(1, 2, 3).castTo(IntegerArrayType.LINEAR))::withOrdinality)
                .as("b").parens("value", "ordinal")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#unnest(ArrayExpression, ArrayExpression)
     */
    @Test
    public void unnestMultiArray() {
        final Select stmt;
        stmt = Postgres.query()
                .select("a", PERIOD, ASTERISK)
                .from(unnest(array(1, 2, 3), array(1, 2, 3))::withOrdinality)
                .as("a").parens("value1", "value2", "original")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#unnest(ArrayExpression, ArrayExpression)
     */
    @Test
    public void dynamicUnnestMultiArray() {
        final Select stmt;
        stmt = Postgres.query()
                .select("a", PERIOD, ASTERISK)
                .from(unnest(c -> {
                    c.accept(array(1, 2, 3));
                    c.accept(array(1, 2, 3));
                })::withOrdinality)
                .as("a").parens("value1", "value2", "original")
                .asQuery();

        printStmt(LOG, stmt);
    }

    @Test
    public void unnestQueryArray() {
        final Select stmt;
        stmt = Postgres.query()
                .select("a", PERIOD, ASTERISK)
                .from(unnest(array(Postgres.subQuery()
                                .select(ChinaRegion_.id)
                                .from(ChinaRegion_.T, AS, "c")
                                .limit(SQLs::literal, 10)
                                .asQuery()
                        )
                )::withOrdinality)
                .as("a").parens("value", "original")
                .asQuery();

        printStmt(LOG, stmt);
    }


}
