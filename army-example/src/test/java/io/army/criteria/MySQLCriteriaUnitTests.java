package io.army.criteria;

import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.account.BankAccount_;
import io.army.example.bank.domain.user.*;
import io.army.example.common.Criteria;
import io.army.example.pill.domain.User_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
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

        printStmt(stmt);


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
                .ignoreIndex().forOrderBy(Collections.singletonList("uni_name_region_type"))
                .set(ChinaRegion_.name, "五指礁")
                .setPlusLiteral(ChinaRegion_.regionGdp, 100)
                .where(ChinaRegion_.name.equal(""))
                .and(ChinaRegion_.parentId.equal(map.get("parentId")).or(ChinaRegion_.regionType.equalLiteral(RegionType.CITY)))
                .and(ChinaRegion_.regionGdp.plusLiteral(100).greatEqualLiteral(0))
                .orderBy(ChinaRegion_.name.desc())
                .limit(map::get, "rowCount")
                .asUpdate();

        printStmt(stmt);
    }

    @Test
    public void simpleBatchSingleUpdate() {

        final List<Map<String, Object>> paramList = new ArrayList<>(3);
        Map<String, Object> paramMap;

        paramMap = new HashMap<>();
        paramMap.put(ChinaRegion_.ID, 3);
        paramMap.put(ChinaRegion_.NAME, "五指礁");
        paramMap.put(ChinaRegion_.REGION_GDP, 100);
        paramList.add(paramMap);

        paramMap = new HashMap<>();
        paramMap.put(ChinaRegion_.ID, 8L);
        paramMap.put(ChinaRegion_.NAME, "荒海");
        paramMap.put(ChinaRegion_.REGION_GDP, new BigDecimal("324342"));
        paramList.add(paramMap);


        final Update stmt;
        stmt = MySQLs.batchSingleUpdate()
                .update(ChinaRegion_.T, "t")
                .setFields(clause -> {
                    clause.accept(ChinaRegion_.name);
                    clause.accept(ChinaRegion_.regionGdp);
                })
                .where(ChinaRegion_.id.equalNamed())
                .limit(10)
                .paramList(paramList)
                .asUpdate();

        printStmt(stmt);
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

            printStmt(stmt);
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

            printStmt(stmt);
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
    public void multiDelete() {

        //daoMethod mock dao method
        final Consumer<Map<String, Object>> daoMethod = map -> {

            final Supplier<List<Hint>> hintSupplier = () -> {
                final List<Hint> hintList = new ArrayList<>(2);
                hintList.add(MySQLs.qbName("regionDelete"));
                hintList.add(MySQLs.orderIndex("regionDelete", "r", Collections.singletonList("PRIMARY")));
                return hintList;
            };

            final List<MySQLWords> modifierList;
            modifierList = Arrays.asList(MySQLWords.LOW_PRIORITY, MySQLWords.QUICK, MySQLWords.IGNORE);
            final List<String> deleteTarget = Arrays.asList("c", "r", "u");
            final Delete stmt;
            stmt = MySQLs.multiDelete()
                    .delete(hintSupplier, modifierList, deleteTarget)
                    .from(ChinaCity_.T).partition("P1").as("c")
                    .join(ChinaRegion_.T).partition("P1").as("r").on(ChinaCity_.id::equal, ChinaRegion_.id)
                    .join(BankUser_.T, "u").on(BankUser_.id::equal, ChinaCity_.id)// delete lonely parent testing
                    .where(ChinaRegion_.createTime::betweenLiteral, map::get, "startTime", "endTIme")
                    .and(ChinaRegion_.updateTime::between, map::get, "startTime", "endTIme")
                    .ifAnd(ChinaRegion_.version::equalLiteral, map::get, "version")
                    .asDelete();

            printStmt(stmt);
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
    public void batchMultiDelete() {

        //daoMethod mock dao method
        final Consumer<Map<String, Object>> daoMethod = map -> {

            final Supplier<List<Hint>> hintSupplier = () -> {
                final List<Hint> hintList = new ArrayList<>(2);
                hintList.add(MySQLs.qbName("regionDelete"));
                hintList.add(MySQLs.orderIndex("regionDelete", "r", Collections.singletonList("PRIMARY")));
                return hintList;
            };

            final List<Map<String, Object>> paramList = new ArrayList<>();
            paramList.add(Collections.singletonMap(ChinaCity_.ID, "33"));
            paramList.add(Collections.singletonMap(ChinaCity_.ID, (byte) 22));
            paramList.add(Collections.singletonMap(ChinaCity_.ID, (short) 44));
            paramList.add(Collections.singletonMap(ChinaCity_.ID, 22));
            paramList.add(Collections.singletonMap(ChinaCity_.ID, 88L));

            final List<MySQLWords> modifierList;
            modifierList = Arrays.asList(MySQLWords.LOW_PRIORITY, MySQLWords.QUICK, MySQLWords.IGNORE);
            final List<String> deleteTarget = Arrays.asList("c", "r");
            final Delete stmt;
            stmt = MySQLs.batchMultiDelete()
                    .delete(hintSupplier, modifierList, deleteTarget)
                    .from(ChinaCity_.T).partition("P1").as("c")
                    .join(ChinaRegion_.T).partition("P1").as("r").on(ChinaCity_.id::equal, ChinaRegion_.id)
                    .where(ChinaRegion_.id.equalNamed())
                    .and(ChinaRegion_.createTime::betweenLiteral, map::get, "startTime", "endTIme")
                    .and(ChinaRegion_.updateTime::between, map::get, "startTime", "endTIme")
                    .ifAnd(ChinaRegion_.version::equalLiteral, map::get, "version")
                    .paramList(paramList)
                    .asDelete();

            printStmt(stmt);
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
    public void multiUpdateWithMapCriteria() {
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
                    .update(hintSupplier, Arrays.asList(MySQLWords.LOW_PRIORITY, MySQLWords.IGNORE), BankUser_.T)
                    .partition("P1").as("u")
                    .useIndex()
                    .forJoin(Collections.singletonList("PRIMARY"))
                    .join(BankAccount_.T, "a")
                    .ignoreIndex(Collections.singletonList("idx_account_id"))
                    .on(BankUser_.id::equal, BankAccount_.id)
                    .ifSet(BankUser_.nickName, map::get, "newNickName")
                    .ifSet(BankAccount_.balance, SQLs::plusEqual, map::get, "amount")
                    .where(BankUser_.partnerUserId::equalLiteral, map::get, "identityId")
                    .ifAnd(BankUser_.nickName::equal, map::get, "oldNickName")
                    .ifAnd(BankAccount_.createTime::betweenLiteral, map::get, "startTime", "endTime")
                    .ifAnd(BankAccount_.version::equalLiteral, map::get, "version")
                    .ifNonNullAnd(BankAccount_.balance::plus, map.get("amount"), Expression::greatEqualLiteral, 0)
                    .asUpdate();

            printStmt(stmt);

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
    public void multiUpdateChildFromLeft() {
        //daoMethod mock dao method
        final Consumer<Map<String, Object>> daoMethod = map -> {

            final Update stmt;
            stmt = MySQLs.multiUpdate()
                    .update(BankUser_.T, "u")
                    .join(Person_.T, "p").on(BankUser_.id::equal, Person_.id)
                    .join(PartnerUser_.T, "up").on(BankUser_.id::equal, PartnerUser_.id)
                    .join(BankAccount_.T, "a").on(BankUser_.id::equal, BankAccount_.userId)
                    .set(BankUser_.nickName, map.get("newNickName"))
                    .ifSet(BankAccount_.balance, SQLs::plusEqual, map::get, "amount")
                    .where(BankUser_.partnerUserId::equalLiteral, map::get, "identityId")
                    .ifAnd(BankUser_.nickName::equal, map::get, "oldNickName")
                    .ifAnd(BankAccount_.createTime::betweenLiteral, map::get, "startTime", "endTime")
                    .ifAnd(BankAccount_.version::equalLiteral, map::get, "version")
                    .ifNonNullAnd(BankAccount_.balance::plus, map.get("amount"), Expression::greatEqualLiteral, 0)
                    .asUpdate();

            printStmt(stmt);

        };//mock dao method end

        final Map<String, Object> map = new HashMap<>();
        final LocalDateTime now = LocalDateTime.now();

        map.put("amount", "888888.88");
        map.put("startTime", now.minusDays(15));
        map.put("endTIme", now.plusDays(6));
        map.put("version", "8");

        map.put("identityId", "6668888");
        map.put("oldNickName", "zoro");
        map.put("newNickName", "索隆");

        //below,mock dao method invoking
        daoMethod.accept(map);

    }


    @Test
    public void multiUpdateChildFromRight() {
        //daoMethod mock dao method
        final Consumer<Map<String, Object>> daoMethod = map -> {

            final Update stmt;
            stmt = MySQLs.multiUpdate()
                    .update(PartnerUser_.T, "up")
                    .join(BankUser_.T, "u").on(BankUser_.id::equal, PartnerUser_.id)
                    .join(BankAccount_.T, "a").on(BankUser_.id::equal, BankAccount_.userId)
                    .set(BankUser_.nickName, map.get("newNickName"))
                    .set(PartnerUser_.legalPersonId, "66666666")
                    .ifSet(BankAccount_.balance, SQLs::plusEqual, map::get, "amount")
                    .where(BankUser_.partnerUserId::equalLiteral, map::get, "identityId")
                    .ifAnd(BankUser_.nickName::equal, map::get, "oldNickName")
                    .ifAnd(BankAccount_.createTime::betweenLiteral, map::get, "startTime", "endTime")
                    .ifAnd(BankAccount_.version::equalLiteral, map::get, "version")
                    .ifNonNullAnd(BankAccount_.balance::plus, map.get("amount"), Expression::greatEqualLiteral, 0)
                    .asUpdate();

            printStmt(stmt);

        };//mock dao method end

        final Map<String, Object> map = new HashMap<>();
        final LocalDateTime now = LocalDateTime.now();

        map.put("amount", "888888.88");
        map.put("startTime", now.minusDays(15));
        map.put("endTIme", now.plusDays(6));
        map.put("version", "8");

        map.put("identityId", "6668888");
        map.put("oldNickName", "zoro");
        map.put("newNickName", "索隆");

        //below,mock dao method invoking
        daoMethod.accept(map);

    }

    @Test
    public void batchMultiUpdateWithMapCriteria() {
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
            paramMap.put("nickName", "索隆1");
            paramMap.put("balance", "666888.00");
            paramList.add(paramMap);

            paramMap = new HashMap<>();
            paramMap.put("nickName", "索隆2");
            paramMap.put("balance", new BigDecimal("888666.00"));
            paramList.add(paramMap);


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
                    .paramList(paramList)
                    .asUpdate();

            printStmt(stmt);

        };//mock dao method end

        final Map<String, Object> map = new HashMap<>();
        final LocalDateTime now = LocalDateTime.now();

        map.put("amount", "888888.88");
        map.put("startTime", now.minusDays(15));
        map.put("endTime", now.plusDays(6));
        map.put("version", "0");

        map.put("identityId", "6668888");

        //map.put("oldNickName","zoro");
        //map.put("newNickName","索隆");

        //below,mock dao method invoking
        daoMethod.accept(map);


    }


    @Test
    public void singleSelect() {

        final Consumer<Map<String, Object>> mockDaoMethod = criteria -> {

            final Select stmt;
            stmt = MySQLs.query()
                    .select(SQLs.group(Captcha_.T, "c"))
                    .from(Captcha_.T, "c")
                    .where(Captcha_.id.inOptimizing(criteria.get("ids")))
                    .and(Captcha_.createTime::between, criteria::get, "startTime", "endTime")
                    .and(Captcha_.deadline.greatEqualLiteral(LocalDateTime.now()))
                    .asQuery();

            printStmt(stmt);

        };


        final LocalDateTime now = LocalDateTime.now();
        final Map<String, Object> map = new HashMap<>();


        map.put("ids", Arrays.asList(11, 22, 33, 44, 55, 66, 77));
        map.put("startTime", now.minusDays(5));
        map.put("endTime", now.plusDays(5));

        mockDaoMethod.accept(map);

    }

    @Test
    public void multiSelect() {
        final Consumer<Map<String, Object>> mockDaoMethod = criteria -> {

            final Select stmt;
            stmt = MySQLs.query()
                    .select(SQLs.group(BankUser_.T, "u"))
                    .from(BankUser_.T, "u")
                    .join(BankAccount_.T, "a").on(BankUser_.id::equal, BankAccount_.id)
                    .where(BankUser_.id.inOptimizing(criteria.get("ids")))
                    .and(BankAccount_.createTime::between, criteria::get, "startTime", "endTime")
                    .and(BankUser_.updateTime.greatEqualLiteral(LocalDateTime.now()))
                    .and(SQLs.exists(() -> MySQLs.subQuery()
                            .select(RegisterRecord_.id)
                            .from(RegisterRecord_.T, "r")
                            .where(RegisterRecord_.userId::equal, BankUser_.id)
                            .asQuery()))
                    .asQuery();

            printStmt(stmt);

        };


        final LocalDateTime now = LocalDateTime.now();
        final Map<String, Object> map = new HashMap<>();


        map.put("ids", Arrays.asList(11, 22, 33, 44, 55, 66, 77));
        map.put("startTime", now.minusDays(5));
        map.put("endTime", now.plusDays(5));

        mockDaoMethod.accept(map);
    }


    private void printStmt(final Statement statement) {
        for (Dialect dialect : Dialect.values()) {
            if (dialect.database != Database.MySQL) {
                continue;
            }
            LOG.debug("{}:\n{}", dialect.name(), statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
        }

    }


}
