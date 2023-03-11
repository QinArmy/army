package io.army.criteria.standard.unit;

import io.army.criteria.DeleteStatement;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.impl.SQLs.AS;

public class StandardDeleteUnitTests extends StandardUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardDeleteUnitTests.class);


    @Test
    public void deleteSingle() {
        final DeleteStatement stmt;
        stmt = SQLs.singleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "r")
                .where(ChinaRegion_.id::equal, SQLs::param, () -> 1)
                .and(ChinaRegion_.name.equal(SQLs::param, "马鱼腮角"))
                .and(ChinaRegion_.version.equal(SQLs::param, 2))
                .asDelete();

        printStmt(LOG, stmt);

    }



}
