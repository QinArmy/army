package io.army.criteria.mysql.unit;

import io.army.criteria.*;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.mysql.MySQLValues;
import io.army.dialect.mysql.MySQLDialect;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.function.Supplier;

import static io.army.criteria.impl.SQLs.*;

public class MySQLValuesUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLValuesUnitTests.class);


    @Test
    public void simpleValues() {
        Values stmt;
        stmt = this.createSimpleValues(MySQLs::primaryValues)
                .asValues();
        printStmt(stmt);

    }

    @Test
    public void unionValues() {
        Values stmt;
        stmt = this.createSimpleValues(MySQLs::primaryValues)

                .orderBy(SQLs.refSelection("column_0"), SQLs.refSelection("column_1")::desc)
                .limit(SQLs::literal, 3)
                .asValues();

        printStmt(stmt);

    }

    @Test
    public void simpleSubValues() {
        Select stmt;
        stmt = MySQLs.query()
                .select(s -> s.space("s", PERIOD, ASTERISK))
                .from(this.createSimpleValues(MySQLs::subValues)
                        ::asValues
                ).as("c")
                .join(ChinaRegion_.T, AS, "c").on(SQLs.refThis("s", "column_0")::equal, ChinaRegion_.id)
                .where(ChinaRegion_.id::equal, SQLs::literal, "1")
                .asQuery();

        printStmt(stmt);

    }

    @Test
    public void unionSubValues() {
        Select stmt;
        stmt = MySQLs.query()
                .select(s -> s.space("s", PERIOD, ASTERISK))
                .from(() -> this.createSimpleValues(MySQLs::subValues)
                        .asValues())
                .as("s")
                .join(ChinaRegion_.T, AS, "c").on(SQLs.refThis("s", "column_0")::equal, ChinaRegion_.id)
                .where(ChinaRegion_.id::equal, SQLs::literal, "1")
                .asQuery();

        printStmt(stmt);

    }


    /**
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use the interface that start with {@code _ }
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     */
    private <V extends ValuesQuery> MySQLValues._UnionOrderBySpec<V> createSimpleValues(Supplier<MySQLValues._ValueSpec<V>> supplier) {
        return supplier.get()
                .parens(s -> s.values()
                        .leftParen(SQLs::literalValue, 1, "海问香", new BigDecimal("9999.88"), LocalDate.now())
                        .comma(SQLs::literalValue, DayOfWeek.MONDAY, TRUE, SQLs.literalValue(1).plus(SQLs::literal, 3))
                        .rightParen()

                        .leftParen(SQLs::literalValue, 2, "大仓", new BigDecimal("9999.66"), LocalDate.now().plusDays(1))
                        .comma(SQLs::literalValue, DayOfWeek.SUNDAY, TRUE, SQLs.literalValue(13).minus(SQLs::literal, 3))
                        .rightParen()

                        .leftParen(SQLs::literalValue, 3, "卡拉肖克·玲", new BigDecimal("6666.88"), LocalDate.now().minusDays(3))
                        .comma(SQLs::literalValue, DayOfWeek.FRIDAY, TRUE, SQLs.literalValue(3).minus(SQLs::literal, 3))
                        .rightParen()

                        .leftParen(SQLs::literalValue, 4, "幽弥狂", new BigDecimal("8888.88"), LocalDate.now().minusDays(8))
                        .comma(SQLs::literalValue, DayOfWeek.TUESDAY, FALSE, SQLs.literalValue(81).divide(SQLs::literal, 3))
                        .rightParen()

                        .orderBy(SQLs.refSelection("column_1"), SQLs.literalValue(2)::desc)
                        .limit(SQLs::literal, 4)
                        .asValues()
                );
    }


    private void printStmt(final PrimaryStatement statement) {
        String sql;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            if (dialect.compareWith(MySQLDialect.MySQL80) < 0) {
                continue;
            }
            sql = statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);
        }

    }


}
