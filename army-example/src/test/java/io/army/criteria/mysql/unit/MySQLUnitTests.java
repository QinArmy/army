/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.mysql.unit;

import io.army.criteria.CriteriaUnitTests;
import io.army.criteria.PrimaryStatement;
import io.army.criteria.Visible;
import io.army.dialect.MySQLDialect;
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
