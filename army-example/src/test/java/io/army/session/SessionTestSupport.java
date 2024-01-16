package io.army.session;

import io.army.ArmyTestDataSupport;
import io.army.dialect.Database;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
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


    protected static void assertSingleUpdateChildRows(final Session session, final long actualRows, final long dataRows) {
        final Database database = session.sessionFactory().serverMeta().serverDatabase();
        Assert.assertEquals(actualRows, dataRows);
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




}
