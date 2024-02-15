package io.army.session.sync.postgre;


import com.alibaba.fastjson2.JSON;
import io.army.criteria.DeclareCursor;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.session.Direction;
import io.army.session.record.ResultRecord;
import io.army.session.record.ResultRecordMeta;
import io.army.session.record.ResultStates;
import io.army.sync.SyncLocalSession;
import io.army.sync.SyncStmtCursor;
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

        final DeclareCursor stmt;
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
            ChinaRegion<?> region, firstRow;
            int rowCount = 0;
            while ((region = cursor.next(ChinaRegion_.CLASS)) != null) {
                LOG.debug("region : {}", JSON.toJSONString(region));
                rowCount++;
                if (rowCount > 200) {
                    break;
                }
            }
            firstRow = cursor.fetchOneObject(Direction.FIRST, ChinaRegion_::constructor, ResultStates.IGNORE_STATES);
            LOG.debug("{} firstRow : {}", session.name(), firstRow);
            cursor.move(Direction.LAST);
        }

    }

    @Transactional(readOnly = true)
    @Test
    public void resultItemStream(final SyncLocalSession session) {
        final List<ChinaRegion<?>> regionList = createReginListWithCount(10);
        session.batchSave(regionList);

        final DeclareCursor stmt;
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
                        }
                    });

            Assert.assertNotNull(recordStatesHolder[0]);
            Assert.assertNotNull(recordMetaHolder[0]);
            Assert.assertEquals(rowCountHolder[0], regionList.size());

        }


    }


}
