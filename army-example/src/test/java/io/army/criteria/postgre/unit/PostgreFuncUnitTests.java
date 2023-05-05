package io.army.criteria.postgre.unit;

import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.function.Consumer;

import static io.army.criteria.impl.Postgres.*;

public class PostgreFuncUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreFuncUnitTests.class);

    /**
     * @see Postgres#cases(Expression)
     * @see Postgres#cases()
     */
    @Test
    public void caseFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(cases(SQLs.literalValue(3))
                        .when(SQLs::literalValue, 1)
                        .then(SQLs::literalValue, 2)
                        .elseValue(SQLs::literalValue, 0)
                        .end().as("a")
                ).asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#coalesce(Expression, Expression...)
     */
    @Test
    public void coalesceFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(coalesce(SQLs.literalValue(1), SQLs.literalValue(2))::as, "a")
                .comma(coalesce(c -> {
                    c.accept(SQLs.literalValue(1));
                    c.accept(SQLs.literalValue(3));
                })::as, "b")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#nullIf(Expression, Expression)
     */
    @Test
    public void nullIfFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(nullIf(SQLs.literalValue(1), SQLs.literalValue(2))::as, "a")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#greatest(Expression, Expression...)
     * @see Postgres#greatest(Consumer)
     */
    @Test
    public void greatestFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(greatest(SQLs.literalValue(1), SQLs.literalValue(2))::as, "a")
                .comma(greatest(c -> {
                    c.accept(SQLs.literalValue(1));
                    c.accept(SQLs.literalValue(3));
                })::as, "b")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#least(Expression, Expression...)
     * @see Postgres#least(Consumer)
     */
    @Test
    public void leastFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(least(SQLs.literalValue(1), SQLs.literalValue(2))::as, "a")
                .comma(least(c -> {
                    c.accept(SQLs.literalValue(1));
                    c.accept(SQLs.literalValue(3));
                })::as, "b")
                .asQuery();

        printStmt(LOG, stmt);
    }


}
