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

import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.standard.SQLs;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class MySQLLoadDataUnitTests extends MySQLUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLLoadDataUnitTests.class);


    @Test
    public void singleLoadData() throws Exception {
        final Path tempFile;
        tempFile = Files.createTempFile("mySQLLoadData", ".temp");

        try {
            final DmlCommand stmt;
            stmt = MySQLs.loadDataStmt()
                    .loadData(MySQLs.LOCAL)
                    .infile(tempFile)
                    .ignore()
                    .intoTable(ChinaRegion_.T)
                    .partition("p1")
                    .characterSet("utf8mb4")

                    .columns(s -> s.terminatedBy(" ")
                            .enclosedBy('t')
                            .escapedBy('f')
                    )
                    .lines(s -> s.startingBy("row:")
                            .terminatedBy("\n")
                    )
                    .ignore(1, SQLs.LINES)
                    .parens(s -> s.space(ChinaRegion_.name))

                    .set(ChinaRegion_.visible, SQLs::literal, true)
                    .asCommand();

            printStmt(LOG, stmt);
        } finally {
            Files.deleteIfExists(tempFile);
        }

    }


    @Test
    public void childLoadData() throws Exception {
        final Path parentTempFile, childTempFile;
        parentTempFile = Files.createTempFile("parent", ".temp");
        childTempFile = Files.createTempFile("child", ".temp");

        try {
            final DmlCommand stmt;
            stmt = MySQLs.loadDataStmt()
                    .loadData(MySQLs.LOCAL)
                    .infile(parentTempFile)
                    .ignore()
                    .intoTable(ChinaRegion_.T)
                    .partition("p1")
                    .characterSet("utf8mb4")

                    .columns(s -> s.terminatedBy(" ")
                            .enclosedBy('t')
                            .escapedBy('f')
                    )
                    .lines(s -> s.startingBy("row:")
                            .terminatedBy("\n")
                    )
                    .ignore(1, SQLs.LINES)
                    .parens(s -> s.space(ChinaRegion_.name))

                    .set(ChinaRegion_.visible, SQLs::literal, true)
                    .asCommand()

                    .child()

                    .loadData(MySQLs.LOCAL)
                    .infile(childTempFile)
                    .ignore()
                    .intoTable(ChinaProvince_.T)
                    .partition("p1")
                    .characterSet("utf8mb4")

                    .columns(s -> s.terminatedBy(" ")
                            .enclosedBy('t')
                            .escapedBy('f')
                    )
                    .lines(s -> s.startingBy("row:")
                            .terminatedBy("\n")
                    )
                    .ignore(1, SQLs.LINES)
                    .parens(s -> s.space(ChinaProvince_.governor))

                    .set(ChinaProvince_.governor, SQLs::literal, randomPerson())
                    .asCommand();


            printStmt(LOG, stmt);
        } finally {
            Files.deleteIfExists(parentTempFile);
            Files.deleteIfExists(childTempFile);
        }

    }


}
