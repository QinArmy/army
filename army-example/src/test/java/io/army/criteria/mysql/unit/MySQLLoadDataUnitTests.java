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

import io.army.criteria.Visible;
import io.army.criteria.dialect.SQLCommand;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.dialect.mysql.MySQLDialect;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class MySQLLoadDataUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLLoadDataUnitTests.class);


    @Test
    public void singleLoadData() throws Exception {
        final Path tempFile;
        tempFile = Files.createTempFile("mySQLLoadData", ".temp");

        try {
            final SQLCommand stmt;
            stmt = MySQLs.loadDataCommand()
                    .loadData(MySQLs.LOCAL)
                    .infile(tempFile)
                    .ignore()
                    .intoTable(ChinaRegion_.T)
                    .partition()
                    .leftParen("p1")
                    .rightParen()
                    .characterSet("utf8mb4")

                    .columns()
                    .terminatedBy(" ")
                    .optionally()
                    .enclosedBy('t')
                    .escapedBy('f')

                    .lines()
                    .startingBy("row:")
                    .terminatedBy("\n")
                    .ignore(1)
                    .rows()

                    .leftParen(ChinaRegion_.name)
                    .rightParen()

                    .set(ChinaRegion_.visible, SQLs::literal, true)
                    .asCommand();

            printStmt(stmt);
        } finally {
            Files.deleteIfExists(tempFile);
        }

    }


    private static void printStmt(final SQLCommand load) {
        String sql;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            sql = load.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);
        }

    }


}
