package io.army.session.reactive.mysql;


import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import io.army.reactive.ReactiveLocalSession;
import io.army.reactive.ReactiveStmtOption;
import io.army.session.MyPaths;
import io.army.session.record.ResultStates;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Test(dataProvider = "localSessionProvider")
public class LoadDataTests extends SessionTestsSupport {

    @Test(enabled = false)
    public void singleLoadData(final ReactiveLocalSession session) {
        final Path csvFile;
        csvFile = Paths.get(MyPaths.testResourcesPath().toString(), "my-local/china_region.csv");
        if (Files.notExists(csvFile)) {
            return;
        }

        final DmlCommand stmt;
        stmt = MySQLs.loadDataStmt()
                .loadData(MySQLs.LOCAL)
                .infile(csvFile)
                .ignore()
                .intoTable(ChinaRegion_.T)
                .characterSet("utf8mb4")
                .columns(s -> s.terminatedBy(","))
                .ignore(1, SQLs.LINES)
                .set(ChinaRegion_.visible, SQLs::literal, true)
                .set(ChinaRegion_.regionType, SQLs::literal, RegionType.NONE)
                .asCommand();

        final ResultStates states;
        states = session.update(stmt, ReactiveStmtOption.preferServerPrepare(false))
                .block();

        Assert.assertNotNull(states);

        LOG.debug("session[name : {}] rows {}", session.name(), states.affectedRows());
    }


}
