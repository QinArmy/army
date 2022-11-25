package io.army.criteria.mysql;

import io.army.criteria.CriteriaUnitTests;
import io.army.criteria.PrimaryStatement;
import io.army.criteria.Visible;
import io.army.dialect.mysql.MySQLDialect;
import org.slf4j.Logger;

abstract class MySQLUnitTests extends CriteriaUnitTests {

    static void printStmt(Logger logger, PrimaryStatement statement) {

        for (MySQLDialect dialect : MySQLDialect.values()) {
            logger.debug("{}:\n{}", dialect.name(), statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
        }

    }

    static void print80Stmt(Logger logger, PrimaryStatement statement) {

        for (MySQLDialect dialect : MySQLDialect.values()) {
            if (dialect.version() < MySQLDialect.MySQL80.version()) {
                continue;
            }
            logger.debug("{}:\n{}", dialect.name(), statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
        }

    }


}
