package io.army.criteria;

import io.army.criteria.impl.MySQLs;
import io.army.criteria.mysql.MySQLWords;
import io.army.example.bank.domain.user.ChinaCity_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import io.army.example.common.Criteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
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
        map.put("parentId", "1");
        Supplier<List<Hint>> supplier = () -> {
            List<Hint> list = new ArrayList<>();
            list.add(MySQLs.qbName("qb1"));
            list.add(MySQLs.orderIndex("qb1", "t", Collections.singletonList("uni_name_region_type")));
            return list;
        };
        final Update stmt;
        stmt = MySQLs.singleUpdate()
                .update(supplier, Arrays.asList(MySQLWords.LOW_PRIORITY, MySQLWords.IGNORE), ChinaCity_.T)
                .partition("p2", "p1").as("t")
                .useIndex().forOrderBy(Collections.singletonList("uni_name_region_type"))
                .set(ChinaRegion_.name, "五指礁")
                .setPlusLiteral(ChinaRegion_.regionGdp, 100)
                .where(ChinaRegion_.name.equal(""))
                .and(ChinaRegion_.parentId.equal(map.get("parentId")).or(ChinaRegion_.regionType.equalLiteral(RegionType.CITY)))
                .ifAnd(ChinaRegion_.regionGdp::greatEqualLiteral, map::get, "regionGdp")
                .orderBy(ChinaRegion_.name.desc())
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


    @Test
    public void singleDelete57WithCriteriaMap() {

        //daoMethod mock dao method
        final Consumer<Map<String, Object>> daoMethod = map -> {

            final Supplier<List<Hint>> hintSupplier = () -> {
                final List<Hint> hintList = new ArrayList<>(2);
                hintList.add(MySQLs.qbName("regionDelete"));
                hintList.add(MySQLs.orderIndex("regionDelete", "r", Collections.singletonList("PRIMARY")));
                return hintList;
            };

            final Delete stmt;
            stmt = MySQLs.singleDelete()
                    .delete(hintSupplier, Arrays.asList(MySQLWords.LOW_PRIORITY, MySQLWords.QUICK, MySQLWords.IGNORE))
                    .from(ChinaRegion_.T, "r")
                    .partition("P1")
                    .where(ChinaRegion_.createTime::betweenLiteral, map::get, "startTime", "endTIme")
                    .and(ChinaRegion_.updateTime::between, map::get, "startTime", "endTIme")
                    .ifAnd(ChinaRegion_.version::equalLiteral, map::get, "version")
                    .orderBy(ChinaRegion_.name.desc(), ChinaRegion_.id)
                    .ifLimit(map::get, "rowCount")
                    .asDelete();

            System.out.println(stmt);
        };


        final Map<String, Object> map = new HashMap<>();
        final LocalDateTime now = LocalDateTime.now();
        map.put("startTime", now.minusDays(15));
        map.put("endTIme", now.plusDays(6));
        map.put("version", "0");

        //map.put("rowCount",(byte)36);

        //below,mock dao method invoking
        daoMethod.accept(map);

    }


}
