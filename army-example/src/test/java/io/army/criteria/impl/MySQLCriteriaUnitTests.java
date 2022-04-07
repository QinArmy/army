package io.army.criteria.impl;

import io.army.criteria.Hint;
import io.army.criteria.TableField;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.criteria.mysql.MySQLModifier;
import io.army.dialect.Dialect;
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
        map.put("name", "");
        Supplier<List<Hint>> supplier = () -> {
            List<Hint> list = new ArrayList<>();
            list.add(MySQLs.qbName("qb1"));
            list.add(MySQLs.maxExecutionTime(null));
            return list;
        };
        final Update stmt;
        stmt = MySQLs.singleUpdate()
                .update(supplier, Arrays.asList(MySQLModifier.LOW_PRIORITY, MySQLModifier.IGNORE), ChinaCity_.T)
                .partition("p2", "p1").as("t")
                .useIndex().forOrderBy(Collections.singletonList("PRIMARY"))
                .set(ChinaRegion_.name, "五指礁")
                .setPlusLiteral(ChinaRegion_.regionGdp, 100)
                .where(ChinaRegion_.name.equal(""))
                .and(ChinaRegion_.name.equal(map.get("name")))
                .ifAnd(ChinaRegion_.regionGdp.ifGreatEqual(map::get, "regionGdp"))
                .orderBy(ChinaRegion_.id.desc())
                .limit(map::get, "rowCount")
                .asUpdate();
        LOG.debug("MySQL single update:\n{}", stmt.mockAsString(Dialect.MySQL57, Visible.ONLY_VISIBLE, true));
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
