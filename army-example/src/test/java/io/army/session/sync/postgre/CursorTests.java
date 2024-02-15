package io.army.session.sync.postgre;


import com.alibaba.fastjson2.JSON;
import io.army.criteria.DeclareCursor;
import io.army.criteria.impl.Postgres;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.session.Direction;
import io.army.session.record.ResultStates;
import io.army.sync.SyncLocalSession;
import io.army.sync.SyncStmtCursor;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class CursorTests extends SessionTestSupport {


    @Transactional(readOnly = true)
    @Test
    public void readOnlyCursor(final SyncLocalSession session) {
        final DeclareCursor stmt;
        stmt = Postgres.declareStmt()
                .declare("my_china_region_cursor").cursor()
                .forSpace()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .orderBy(ChinaRegion_.id)
                .asQuery()
                .asCommand();

        final ResultStates states;
        states = session.updateAsStates(stmt);

        try (SyncStmtCursor cursor = states.nonNullOf(SyncStmtCursor.SYNC_STMT_CURSOR)) {
            ChinaRegion<?> region, firstRow;
            int rowCount = 0;
            while ((region = cursor.next(ChinaRegion_.CLASS)) != null) {
                LOG.debug("region : {}", JSON.toJSONString(region));
                rowCount++;
                if (rowCount > 200) {
                    break;
                }
            }
            firstRow = cursor.fetchOne(Direction.FIRST, ChinaRegion_.CLASS, ResultStates.IGNORE_STATES);
            LOG.debug("{} firstRow : {}", session.name(), firstRow);
        }

    }


}
