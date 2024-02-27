package io.army.session;

import io.army.ArmyTestDataSupport;
import io.army.dialect.Database;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class SessionTestSupport extends ArmyTestDataSupport {


    protected static List<Long> extractRegionIdList(final List<? extends ChinaRegion<?>> regionList) {
        final List<Long> idList = new ArrayList<>(regionList.size());
        for (ChinaRegion<?> region : regionList) {
            idList.add(region.getId());
        }
        return Collections.unmodifiableList(idList);
    }

    protected static List<Map<String, Long>> extractRegionIdMapList(final List<? extends ChinaRegion<?>> regionList) {
        final List<Map<String, Long>> idList = new ArrayList<>(regionList.size());
        for (ChinaRegion<?> region : regionList) {
            idList.add(Collections.singletonMap(ChinaRegion_.ID, region.getId()));
        }
        return Collections.unmodifiableList(idList);
    }


    protected static void assertBatchSingleRows(final List<Long> rowList, final int batchSize, final long dataRows) {
        Assert.assertEquals(rowList.size(), batchSize);
        for (Long rows : rowList) {
            Assert.assertEquals(rows, dataRows);
        }
    }

    protected static void assertBatchUpdateChildRows(final List<Long> rowList, final int batchSize, final long dataRows) {
        Assert.assertEquals(rowList.size(), batchSize);
        for (Long rows : rowList) {
            Assert.assertEquals(rows, dataRows);
        }
    }


    protected static void assertDomainUpdateChildRows(final Session session, long rows, long dataRows) {
        assertDomainUpdate(session.sessionFactory().serverMeta().serverDatabase(), rows, dataRows);
    }

    protected static void assertBatchDomainUpdateChildRows(final Session session, final List<Long> rowList, final int batchSize, final long dataRows) {
        Assert.assertEquals(rowList.size(), batchSize);
        final Database database = session.sessionFactory().serverMeta().serverDatabase();
        for (Long rows : rowList) {
            assertDomainUpdate(database, rows, dataRows);
        }
    }



    protected static void statementCostTimeLog(final Session session, final Logger logger, long startNanoSecond) {
        final long costNano, millis, micro, nano;
        costNano = System.nanoTime() - startNanoSecond;

        millis = costNano / 1000_000L;
        micro = (costNano % 1000_000L) / 1000L;
        nano = costNano % 1000L;

        logger.debug("session[name : {}] create statement cost {} millis {} micro {} nano.", session.name(), millis, micro, nano);

    }


    protected static boolean isDontSupportWithClauseInInsert(final Session session) {
        final boolean match;
        switch (session.sessionFactory().serverMeta().serverDatabase()) {
            case MySQL: // MySQL INSERT statement don't support WITH clause
                match = true;
                break;
            case PostgreSQL:
            case SQLite:
            default:
                match = false;
        }
        return match;
    }


    protected static boolean isNotSupportParenthesizedQuery(final Session session) {
        final boolean dontSupport;
        switch (session.serverDatabase()) {
            case SQLite:  // SQLite don't support Parenthesized Query Expressions , https://www.sqlite.org/lang_select.html
                dontSupport = true;
                break;
            case PostgreSQL:
            case MySQL:
            default:
                dontSupport = false;
        }
        return dontSupport;
    }


    private static void assertDomainUpdate(final Database database, final long rows, final long dataRows) {
        switch (database) {
            case MySQL:
                Assert.assertEquals(rows >> 1, dataRows); // because mysql use multi-table dml
                break;
            case PostgreSQL:
            default:
                Assert.assertEquals(rows, dataRows);
        }
    }


}
