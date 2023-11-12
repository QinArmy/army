package io.army.criteria.standard.unit;

import io.army.criteria.BatchDelete;
import io.army.criteria.Delete;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.util._Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StandardDeleteUnitTests extends StandardUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardDeleteUnitTests.class);


    @Test
    public void deleteSingle() {
        final Delete stmt;
        stmt = SQLs.singleDelete()
                .deleteFrom(ChinaRegion_.T, SQLs.AS, "r")
                .where(ChinaRegion_.id::equal, SQLs::param, 1)
                .and(ChinaRegion_.name.equal(SQLs::param, "马鱼腮角"))
                .and(ChinaRegion_.version.equal(SQLs::param, 2))
                .asDelete();

        printStmt(LOG, stmt);

    }

    @Test
    public void batchSingleDelete() {
        final List<Map<String, String>> paramList = _Collections.arrayList();

        paramList.add(Collections.singletonMap(ChinaRegion_.NAME, "马鱼腮角"));
        paramList.add(Collections.singletonMap(ChinaRegion_.NAME, "五指礁"));


        final BatchDelete stmt;
        stmt = SQLs.batchSingleDelete()
                .deleteFrom(ChinaRegion_.T, SQLs.AS, "c")
                .where(ChinaRegion_.createTime::less, SQLs::literal, LocalDateTime.now())
                .and(ChinaRegion_.name.equalSpace(SQLs::namedParam))
                .and(ChinaRegion_.version.equal(SQLs::param, 2))
                .asDelete()
                .namedParamList(paramList);

        printStmt(LOG, stmt);
    }


}
