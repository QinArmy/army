package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.function.Supplier;

public class MySQLValuesUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLValuesUnitTests.class);


    @Test
    public void simpleValues() {
        Values stmt;
        stmt = this.createSimpleValues(MySQLs::valuesStmt)
                .asValues();
        printStmt(stmt);

    }

    @Test
    public void unionValues() {
        Values stmt;
        stmt = this.createSimpleValues(MySQLs::valuesStmt)
                .bracket()
                .union(() -> this.createSimpleValues(MySQLs::valuesStmt)
                        .bracket()
                        .asValues())
                .bracket()
                .orderBy(SQLs.ref("column_0"))
                .limit(3)
                .asValues();
        printStmt(stmt);

    }

    @Test
    public void simpleSubValues() {
        Select stmt;
        stmt = MySQLs.query()
                .select(SQLs.derivedGroup("s"))
                .from(() -> this.createSimpleValues(MySQLs::subValues)
                        .asValues(), "s")
                .join(ChinaRegion_.T, "c").on(SQLs.ref("s", "column_0")::equal, ChinaRegion_.id)
                .where(ChinaRegion_.id.equalLiteral(1))
                .asQuery();

        printStmt(stmt);

    }

    @Test
    public void unionSubValues() {
        Select stmt;
        stmt = MySQLs.query()
                .select(SQLs.derivedGroup("s"))
                .from(() -> this.createSimpleValues(MySQLs::subValues)
                        .bracket()
                        .unionAll(() -> this.createSimpleValues(MySQLs::subValues)
                                .bracket()
                                .asValues())
                        .asValues(), "s")
                .join(ChinaRegion_.T, "c").on(SQLs.ref("s", "column_0")::equal, ChinaRegion_.id)
                .where(ChinaRegion_.id.equalLiteral(1))
                .asQuery();

        printStmt(stmt);

    }


    /**
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use the interface that start with {@code _ }
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     */
    private <V extends RowSet.DqlValues> MySQLDqlValues._UnionSpec<Void, V> createSimpleValues(Supplier<MySQLDqlValues._ValuesStmtValuesClause<Void, V>> supplier) {
        return supplier.get()
                .values()

                .row()
                .leftParen(1, "海问香", new BigDecimal("9999.88"), LocalDate.now())
                .comma(DayOfWeek.MONDAY, SQLs.trueWord(), SQLs.literal(1).plusLiteral(3))
                .rightParen()

                .row()
                .leftParen(2, "大仓", new BigDecimal("9999.66"), LocalDate.now().plusDays(1))
                .comma(DayOfWeek.SUNDAY, SQLs.trueWord(), SQLs.literal(13).minusLiteral(3))
                .rightParen()

                .row()
                .leftParen(3, "卡拉肖克·玲", new BigDecimal("6666.88"), LocalDate.now().minusDays(3))
                .comma(DayOfWeek.FRIDAY, SQLs.trueWord(), SQLs.literal(3).minusLiteral(3))
                .rightParen()

                .row()
                .leftParen(4, "幽弥狂", new BigDecimal("8888.88"), LocalDate.now().minusDays(8))
                .comma(DayOfWeek.TUESDAY, SQLs.trueWord(), SQLs.literal(81).divideLiteral(3))
                .rightParen()

                .orderBy(SQLs.ref("column_1"), SQLs.literal(2).desc())
                .limit(4);
    }


    private void printStmt(final PrimaryStatement statement) {
        String sql;
        for (Dialect dialect : Dialect.values()) {
            if (dialect.database != Database.MySQL || dialect.version() < Dialect.MySQL80.version()) {
                continue;
            }
            sql = statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);
        }

    }


}
