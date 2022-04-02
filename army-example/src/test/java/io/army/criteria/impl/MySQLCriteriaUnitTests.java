package io.army.criteria.impl;

import io.army.criteria.Update;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class MySQLCriteriaUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLCriteriaUnitTests.class);

    @Test
    public void singleUpdate() {
        Update stmt;
        stmt = MySQLs.singleUpdate()
                .update(ChinaRegion_.T, "t")
                .set(ChinaRegion_.name, "五指礁")
                .where(ChinaRegion_.name.equal(""))
                .and(ChinaRegion_.regionType.equalLiteral(RegionType.CITY).or(ChinaRegion_.regionGdp.greatEqual("3333")))
                .orderBy(ChinaRegion_.id.desc())
                .limit(10)
                .asUpdate();
        LOG.debug("MySQL single update:\n{}", stmt);

    }


}
