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

package io.army.criteria.postgre.statement;

import io.army.criteria.CriteriaUnitTests;
import io.army.criteria.PrimaryStatement;
import io.army.criteria.Visible;
import io.army.dialect.PostgreDialect;
import org.slf4j.Logger;

abstract class PostgreUnitTests extends CriteriaUnitTests {


    static void printStmt(Logger logger, PrimaryStatement statement) {
        printStmt(logger, statement, Visible.ONLY_VISIBLE);
    }


    static void printStmt(Logger logger, PrimaryStatement statement, Visible visible) {

        for (PostgreDialect dialect : PostgreDialect.values()) {
            // statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            logger.debug("{}:\n{}", dialect.name(), statement.mockAsString(dialect, visible, true));
        }

    }


}
