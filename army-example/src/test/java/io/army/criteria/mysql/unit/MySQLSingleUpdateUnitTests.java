package io.army.criteria.mysql.unit;

import io.army.criteria.Update;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static io.army.criteria.impl.SQLs.AS;

/**
 * <p>
 * This class is unit test class of {@link MySQLs#singleUpdate()} and {@link MySQLs#batchSingleUpdate()}
 * </p>
 */
public class MySQLSingleUpdateUnitTests extends MySQLUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLSingleUpdateUnitTests.class);


    @Test//(invocationCount = 10)
    public void simpleUpdateParent() {
        final ChinaRegion<?> criteria = new ChinaRegion<>();
        criteria.setId(888L)
                .setRegionGdp(new BigDecimal("6666.00"));

        final Update stmt;
        stmt = MySQLs.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.name, SQLs::literal, this.randomProvince())
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, criteria.getRegionGdp())
                .whereIf(ChinaRegion_.id::equal, SQLs::param, criteria::getId)
                .and(ChinaRegion_.createTime::less, SQLs::literal, LocalDateTime.now().minusDays(2))
                .asUpdate();

        printStmt(LOG, stmt);

    }


}
