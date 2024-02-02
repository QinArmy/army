package io.army.session.sync.mysql;


import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import io.army.session.MyPaths;
import io.army.sync.SyncLocalSession;
import io.army.sync.SyncStmtOption;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

@Test(dataProvider = "localSessionProvider")
public class LoadDataTests extends SessionTestSupport {


    @Test
    public void singleLoadData(final SyncLocalSession session) {
        final Path csvFile;
        csvFile = MyPaths.myLocal("china_region.csv");
        if (Files.notExists(csvFile)) {
            return;
        }

        final long startNanoSecond = System.nanoTime();

        final DmlCommand stmt;
        stmt = MySQLs.loadDataStmt()
                .loadData(MySQLs.LOCAL)
                .infile(csvFile)
                .ignore()
                .intoTable(ChinaRegion_.T)
                .characterSet("utf8mb4")
                .columns(s -> s.terminatedBy(","))
                .lines(s -> s.terminatedBy("\n"))
                .ignore(1, SQLs.LINES)
                .set(ChinaRegion_.visible, SQLs::literal, true)
                .set(ChinaRegion_.regionType, SQLs::literal, RegionType.NONE)
                .asCommand();

        statementCostTimeLog(session, LOG, startNanoSecond);

        final long rows;
        rows = session.update(stmt, SyncStmtOption.preferServerPrepare(false));
        LOG.debug("session[name : {}] rows {}", session.name(), rows);
    }


    @Test
    public void childLoadData(final SyncLocalSession session) throws Exception {
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


            session.update(stmt);
        } finally {
            Files.deleteIfExists(parentTempFile);
            Files.deleteIfExists(childTempFile);
        }

    }
}
