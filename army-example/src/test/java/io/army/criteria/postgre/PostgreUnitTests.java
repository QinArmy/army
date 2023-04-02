package io.army.criteria.postgre;

import io.army.criteria.CriteriaUnitTests;
import io.army.criteria.PrimaryStatement;
import io.army.criteria.Visible;
import io.army.dialect.postgre.PostgreDialect;
import org.slf4j.Logger;

abstract class PostgreUnitTests extends CriteriaUnitTests {


    static void printStmt(Logger logger, PrimaryStatement statement) {

        for (PostgreDialect dialect : PostgreDialect.values()) {
            // statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            logger.debug("{}:\n{}", dialect.name(), statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
            // break;
        }

    }

}
