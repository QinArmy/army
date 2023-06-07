package io.army.criteria.mysql.unit;

import io.army.criteria.CriteriaUnitTests;
import io.army.criteria.PrimaryStatement;
import io.army.criteria.Visible;
import io.army.dialect.mysql.MySQLDialect;
import org.slf4j.Logger;

abstract class MySQLUnitTests extends CriteriaUnitTests {

    static void printStmt(Logger logger, PrimaryStatement statement) {
        printStmt(logger, statement, Visible.ONLY_VISIBLE);

    }

    static void printStmt(Logger logger, PrimaryStatement statement, Visible visible) {

        for (MySQLDialect dialect : MySQLDialect.values()) {
            // statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            logger.debug("{}:\n{}", dialect.name(), statement.mockAsString(dialect, visible, true));
            // break;
        }

    }

    static void print80Stmt(Logger logger, PrimaryStatement statement) {
        print80Stmt(logger, statement, Visible.ONLY_VISIBLE);
    }

    static void print80Stmt(Logger logger, PrimaryStatement statement, Visible visible) {

        for (MySQLDialect dialect : MySQLDialect.values()) {
            if (dialect.compareWith(MySQLDialect.MySQL80) < 0) {
                continue;
            }
            logger.debug("{}:\n{}", dialect.name(), statement.mockAsString(dialect, visible, true));
        }

    }


}
