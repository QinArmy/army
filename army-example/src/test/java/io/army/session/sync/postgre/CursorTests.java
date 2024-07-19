package io.army.session.sync.postgre;


import com.alibaba.fastjson2.JSON;
import io.army.criteria.SimpleDmlStatement;
import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.executor.DriverException;
import io.army.result.*;
import io.army.session.SyncLocalSession;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class CursorTests extends SessionTestSupport {


    @Transactional(readOnly = true)
    @Test
    public void readOnlyCursor(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(300);
        session.batchSave(regionList);

        final DmlCommand stmt;
        stmt = Postgres.declareStmt()
                .declare("my_china_region_cursor").cursor()
                .forSpace()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .orderBy(ChinaRegion_.id)
                .limit(SQLs::literal, regionList.size())
                .asQuery()
                .asCommand();

        final ResultStates states;
        states = session.updateAsStates(stmt);

        try (SyncStmtCursor cursor = states.nonNullOf(SyncStmtCursor.SYNC_STMT_CURSOR)) {
            ChinaRegion<?> firstRow;
            long rowCount = 0;
            while (cursor.next(ChinaRegion_.CLASS) != null) {
                rowCount++;
                if (rowCount > 200) {
                    break;
                }
            }
            firstRow = cursor.fetchOneObject(Direction.FIRST, ChinaRegion_::constructor, ResultStates.IGNORE_STATES);
            LOG.debug("{} firstRow : {}", session.name(), JSON.toJSONString(firstRow));
            cursor.move(Direction.ABSOLUTE, 0);

            rowCount = cursor.fetchObject(Direction.FORWARD_ALL, ChinaRegion_::constructor, ResultStates.IGNORE_STATES)
                    .count();

            Assert.assertEquals(rowCount, regionList.size());

        }

    }

    @Transactional(readOnly = true)
    @Test
    public void resultItemStream(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(10);
        session.batchSave(regionList);

        final DmlCommand stmt;
        stmt = Postgres.declareStmt()
                .declare("my_china_region_result_item_cursor").cursor()
                .forSpace()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .orderBy(ChinaRegion_.id)
                .asQuery()
                .asCommand();

        final ResultStates states;
        states = session.updateAsStates(stmt);

        try (SyncStmtCursor cursor = states.nonNullOf(SyncStmtCursor.SYNC_STMT_CURSOR)) {
            final ResultRecordMeta[] recordMetaHolder = new ResultRecordMeta[1];
            final ResultStates[] recordStatesHolder = new ResultStates[1];
            final int[] rowCountHolder = new int[]{0};

            cursor.fetch(Direction.FORWARD_ALL)
                    .forEach(item -> {
                        if (item instanceof ResultRecord) {
                            rowCountHolder[0]++;
                        } else if (item instanceof ResultStates) {
                            Assert.assertNull(recordStatesHolder[0]);
                            recordStatesHolder[0] = (ResultStates) item;
                        } else if (item instanceof ResultRecordMeta) {
                            Assert.assertNull(recordMetaHolder[0]);
                            recordMetaHolder[0] = (ResultRecordMeta) item;
                        } else {
                            Assert.fail();
                        }
                    });

            Assert.assertNotNull(recordStatesHolder[0]);
            Assert.assertNotNull(recordMetaHolder[0]);
            Assert.assertEquals(rowCountHolder[0], regionList.size());

            Assert.assertEquals(recordStatesHolder[0].resultNo(), 1);
            Assert.assertEquals(recordStatesHolder[0].rowCount(), regionList.size());

        } catch (DriverException e) {
            LOG.error("sqlState : {} ,  code:{}", e.getSqlState(), e.getVendorCode());
            throw e;
        }


    }


    @Transactional(readOnly = true)
    @Test
    public void closeCursor(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(10);
        session.batchSave(regionList);

        SimpleDmlStatement stmt;
        stmt = Postgres.closeAllCursor();

        session.update(stmt);

        final String cursorName = "my_item_cursorForClose";
        stmt = Postgres.declareStmt()
                .declare(cursorName).cursor()
                .forSpace()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .orderBy(ChinaRegion_.id)
                .asQuery()
                .asCommand();

        final ResultStates states;
        states = session.updateAsStates(stmt);
        Assert.assertNotNull(states);

        stmt = Postgres.closeCursor(cursorName);
        session.update(stmt);


    }


}
