package io.army.criteria.mysql;

import io.army.criteria.Visible;
import io.army.criteria.impl.MySQLSyntax;
import io.army.criteria.impl.MySQLs;
import io.army.dialect.mysql.MySQLDialect;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class MySQLLoadDataUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLLoadDataUnitTests.class);


    @Test
    public void singleLoadData() throws Exception {
        final Path tempFile;
        tempFile = Files.createTempFile("mySQLLoadData", ".temp");

        try {
            MySQLLoadData stmt;
            stmt = MySQLs.loadDataCommand()
                    .loadData(Collections.singletonList(MySQLSyntax._MySQLModifier.LOCAL))
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

                    .set(ChinaRegion_.visible, true)
                    .asLoadData();

            printStmt(stmt);
        } finally {
            Files.deleteIfExists(tempFile);
        }

    }


    private static void printStmt(final MySQLLoadData load) {
        String sql;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            sql = load.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);
        }

    }


}
