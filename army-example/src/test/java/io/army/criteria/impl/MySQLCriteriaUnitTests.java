package io.army.criteria.impl;

import io.army.criteria.TableField;
import io.army.criteria.Update;
import io.army.example.bank.domain.user.ChinaCity_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import io.army.example.common.Criteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.Supplier;

public class MySQLCriteriaUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLCriteriaUnitTests.class);

    @Test
    public void parentSingleUpdate() {
        final Criteria criteria = new Criteria();
        criteria.setRowCount(5L);

        final Update stmt;
        stmt = MySQLs.singleUpdate()
                .update(ChinaRegion_.T, "t")
                .set(ChinaRegion_.name, "五指礁")
                .where(ChinaRegion_.name.equal(""))
                .and(ChinaRegion_.regionType.equalLiteral(RegionType.CITY).or(ChinaRegion_.regionGdp.greatEqual("3333")))
                .orderBy(ChinaRegion_.id.desc())
                .limit(criteria::getRowCount)
                .asUpdate();
        LOG.debug("MySQL single update:\n{}", stmt);


    }

    @Test
    public void childSingleUpdate() {
        final Map<String, Object> map = new HashMap<>();
        map.put("rowCount", 5);

        final Update stmt;
        stmt = MySQLs.singleUpdate()
                .update(ChinaCity_.T, "t")
                .set(ChinaCity_.name, "五指礁")
                .where(ChinaCity_.name.equal(""))
                .and(ChinaCity_.name.equal(map.get("name")))
                .ifAnd(ChinaCity_.regionGdp.ifGreatEqual(map::get, "regionGdp"))
                .orderBy(ChinaCity_.id.desc())
                .limit(map::get, "rowCount")
                .asUpdate();
        LOG.debug("MySQL single update:\n{}", stmt);
    }

    @Test
    public void simpleBatchSingleUpdate() {

        final Supplier<List<TableField<?>>> supplier = () -> {
            List<TableField<?>> list = new ArrayList<>();
            list.add(ChinaRegion_.name);
            return list;
        };
        final Update stmt;
        stmt = MySQLs.batchSingleUpdate()
                .update(ChinaRegion_.T, "t")
                .set(supplier)
                .where(ChinaRegion_.id.equalNamed())
                .limit(10)
                .paramList(Collections::emptyList)
                .asUpdate();
    }


}
