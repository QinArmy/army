package io.army.criteria.standard.unit;

import io.army.criteria.PrimaryStatement;
import io.army.criteria.Visible;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import org.slf4j.Logger;

abstract class StandardUnitTests {

    static void printStmt(final Logger logger, final PrimaryStatement statement) {
        for (Dialect dialect : Database.MySQL.dialects()) {
            logger.debug("{}:\n{}", dialect.name(), statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
        }

    }


}
