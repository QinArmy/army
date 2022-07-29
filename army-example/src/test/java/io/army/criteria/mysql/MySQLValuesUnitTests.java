package io.army.criteria.mysql;

import io.army.criteria.RowSet;
import io.army.criteria.Values;
import io.army.criteria.Visible;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
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
                .limit(3)
                .asValues();
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
                .leftParenLiteral(2, "大仓", new BigDecimal("9999.66"), LocalDate.now().plusDays(1))
                .commaLiteral(DayOfWeek.SUNDAY, SQLs.trueWord(), SQLs.literal(13).minus(3))
                .rightParen()

                .row()
                .leftParenLiteral(3, "卡拉肖克·玲", new BigDecimal("6666.88"), LocalDate.now().minusDays(3))
                .commaLiteral(DayOfWeek.FRIDAY, SQLs.trueWord(), SQLs.literal(3).times(3))
                .rightParen()

                .row()
                .leftParenLiteral(4, "幽弥狂", new BigDecimal("8888.88"), LocalDate.now().minusDays(8))
                .commaLiteral(DayOfWeek.TUESDAY, SQLs.trueWord(), SQLs.literal(81).divideLiteral(3))
                .rightParen()

                .orderBy(SQLs.ref("column_1"), SQLs.literal(2).desc())
                .limit(4);
    }


    private void printStmt(final Values values) {
        String sql;
        for (Dialect dialect : Dialect.values()) {
            if (dialect.database != Database.MySQL) {
                continue;
            }
            sql = values.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);
        }

    }


}
