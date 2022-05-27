package io.army.criteria;

import io.army.criteria.impl.MySQLs;
import io.army.criteria.mysql.MySQLWords;
import io.army.example.bank.domain.account.BankAccount_;
import io.army.example.bank.domain.user.ChinaCity_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import io.army.example.common.Criteria;
import io.army.example.pill.domain.User_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
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

        final Supplier<List<TableField>> supplier = () -> {
            List<TableField> list = new ArrayList<>();
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

    @Test
    public void batchSingleDelete57WithCriteriaMap() {

        //daoMethod mock dao method
        final Consumer<Map<String, Object>> daoMethod = map -> {

            final Supplier<List<Hint>> hintSupplier = () -> {
                final List<Hint> hintList = new ArrayList<>(2);
                hintList.add(MySQLs.qbName("regionDelete"));
                hintList.add(MySQLs.orderIndex("regionDelete", "r", Collections.singletonList("PRIMARY")));
                return hintList;
            };

            final List<Map<String, Object>> paramList = new ArrayList<>();
            Map<String, Object> paramMap;

            paramMap = new HashMap<>();
            paramMap.put("name", "水城");
            paramMap.put("regionGdp", "39999.00");

            paramList.add(paramMap);

            paramMap = new HashMap<>();
            paramMap.put("name", "凉都");
            paramMap.put("regionGdp", new BigDecimal("99999.00"));

            paramList.add(paramMap);


            final Delete stmt;
            stmt = MySQLs.batchSingleDelete()
                    .delete(hintSupplier, Arrays.asList(MySQLWords.LOW_PRIORITY, MySQLWords.QUICK, MySQLWords.IGNORE))
                    .from(ChinaRegion_.T, "r")
                    .partition("P1")
                    .where(ChinaRegion_.name.equalNamed()) // batch parameter
                    .and(ChinaRegion_.regionGdp.greatEqualNamed())// batch parameter
                    .and(ChinaRegion_.updateTime::between, map::get, "startTime", "endTIme")// common parameter
                    .ifAnd(ChinaRegion_.version::equalLiteral, map::get, "version")// common parameter
                    .orderBy(ChinaRegion_.name.desc(), ChinaRegion_.id)
                    .ifLimit(map::get, "rowCount")
                    .paramList(paramList)
                    .asDelete();

            System.out.println(stmt);
        }; // mock dao method end


        final Map<String, Object> map = new HashMap<>();
        final LocalDateTime now = LocalDateTime.now();
        map.put("startTime", now.minusDays(15));
        map.put("endTIme", now.plusDays(6));
        map.put("version", "0");

        //map.put("rowCount",(byte)36);

        //below,mock dao method invoking
        daoMethod.accept(map);

    }

    @Test
    public void multiUpdate57WithMapCriteria() {
        //daoMethod mock dao method
        final Consumer<Map<String, Object>> daoMethod = map -> {

            final Supplier<List<Hint>> hintSupplier = () -> {
                final List<Hint> hintList = new ArrayList<>(2);
                hintList.add(MySQLs.qbName("regionDelete"));
                hintList.add(MySQLs.orderIndex("regionDelete", "r", Collections.singletonList("PRIMARY")));
                return hintList;
            };

            final Update stmt;
            stmt = MySQLs.multiUpdate()
                    .update(hintSupplier, Arrays.asList(MySQLWords.LOW_PRIORITY, MySQLWords.IGNORE), User_.T)
                    .partition("P1").as("u")
                    .useIndex()
                    .forJoin(Collections.singletonList("PRIMARY"))
                    .join(BankAccount_.T, "a")
                    .ignoreIndex(Collections.singletonList("idx_account_id"))
                    .on(User_.id::equal, BankAccount_.id)
                    .ifSet(User_.nickName, map::get, "newNickName")
                    .ifSetPlus(BankAccount_.balance, map::get, "amount")
                    .where(User_.identityId::equalLiteral, map::get, "identityId")
                    .ifAnd(User_.nickName::equal, map::get, "oldNickName")
                    .ifAnd(BankAccount_.createTime::betweenLiteral, map::get, "startTime", "endTime")
                    .ifAnd(BankAccount_.version::equalLiteral, map::get, "version")
                    .ifNonNullAnd(BankAccount_.balance::plus, map.get("amount"), Expression::greatEqualLiteral, 0)
                    .asUpdate();
            System.out.println(stmt);

        };//mock dao method end

        final Map<String, Object> map = new HashMap<>();
        final LocalDateTime now = LocalDateTime.now();

        map.put("amount", "888888.88");
        map.put("startTime", now.minusDays(15));
        map.put("endTIme", now.plusDays(6));
        map.put("version", "0");

        map.put("identityId", "6668888");

        //map.put("oldNickName","zoro");
        //map.put("newNickName","索隆");

        //below,mock dao method invoking
        daoMethod.accept(map);
    }

    @Test
    public void batchMultiUpdate57WithMapCriteria() {

        final Supplier<List<Map<String, Object>>> paramListSupplier = () -> {
            final List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> paramMap;

            paramMap = new HashMap<>();
            paramMap.put("nickName", "索隆1");
            paramMap.put("balance", "666888.00");
            list.add(paramMap);

            paramMap = new HashMap<>();
            paramMap.put("nickName", "索隆2");
            paramMap.put("balance", new BigDecimal("888666.00"));
            list.add(paramMap);
            return list;
        };

        //daoMethod mock dao method
        final Consumer<Map<String, Object>> daoMethod = map -> {

            final Supplier<List<Hint>> hintSupplier = () -> {
                final List<Hint> hintList = new ArrayList<>(2);
                hintList.add(MySQLs.qbName("regionDelete"));
                hintList.add(MySQLs.orderIndex("regionDelete", "r", Collections.singletonList("PRIMARY")));
                return hintList;
            };


            final Update stmt;
            stmt = MySQLs.batchMultiUpdate()
                    .update(hintSupplier, Arrays.asList(MySQLWords.LOW_PRIORITY, MySQLWords.IGNORE), User_.T)
                    .partition("P1").as("u")
                    .useIndex()
                    .forJoin(Collections.singletonList("PRIMARY"))
                    .join(BankAccount_.T, "a")
                    .ignoreIndex(Collections.singletonList("idx_account_id"))
                    .on(User_.id::equal, BankAccount_.id)
                    .set(User_.nickName)
                    .setPlus(BankAccount_.balance)
                    .where(User_.identityId::equalLiteral, map::get, "identityId")
                    .ifAnd(User_.nickName::equal, map::get, "oldNickName")
                    .and(BankAccount_.createTime::betweenLiteral, map::get, "startTime", "endTime")
                    .ifAnd(BankAccount_.createTime::betweenLiteral, map::get, "startTime", "endTime")
                    .ifAnd(BankAccount_.version::equalLiteral, map::get, "version")
                    .paramList(paramListSupplier)
                    .asUpdate();
            System.out.println(stmt);

        };//mock dao method end

        final Map<String, Object> map = new HashMap<>();
        final LocalDateTime now = LocalDateTime.now();

        map.put("amount", "888888.88");
        map.put("startTime", now.minusDays(15));
        map.put("endTIme", now.plusDays(6));
        map.put("version", "0");

        map.put("identityId", "6668888");

        //map.put("oldNickName","zoro");
        //map.put("newNickName","索隆");

        //below,mock dao method invoking
        daoMethod.accept(map);

        BiFunction<? extends Expression, Object, IPredicate> function = Expression::equal;


    }


}
