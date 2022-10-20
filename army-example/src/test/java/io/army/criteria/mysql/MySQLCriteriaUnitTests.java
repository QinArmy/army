package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.impl.MySQLSyntax;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.dialect.mysql.MySQLDialect;
import io.army.example.bank.domain.account.BankAccount_;
import io.army.example.bank.domain.user.*;
import io.army.example.common.Criteria;
import io.army.example.pill.domain.User_;
import io.army.meta.FieldMeta;
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
                .set(ChinaRegion_.name, SQLs::param, "五指礁")
                .where(ChinaRegion_.name::equal, SQLs::param, "")
                .and(ChinaRegion_.regionType.equal(SQLs::literal, RegionType.CITY).or(ChinaRegion_.regionGdp.greatEqual(SQLs::literal, "3333")))
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
                .update(supplier, Arrays.asList(MySQLSyntax._MySQLModifier.LOW_PRIORITY, MySQLSyntax._MySQLModifier.IGNORE), ChinaCity_.T)
                .partition()
                .leftParen("p2", "p1")
                .rightParen()
                .as("t")

                .useIndex()
                .forOrderBy()
                .leftParen("uni_name_region_type")
                .rightParen()

                .ignoreIndex()
                .forOrderBy()
                .leftParen("uni_name_region_type")
                .rightParen()

                .set(ChinaRegion_.name, SQLs::param, "五指礁")
                .set(ChinaRegion_.regionGdp, SQLs::literal, 100)
                .where(ChinaRegion_.name::equal, SQLs::literal, "")
                .and(ChinaRegion_.parentId.equal(SQLs::param, map.get("parentId")).or(ChinaRegion_.regionType.equal(SQLs::literal, RegionType.CITY)))
                .and(ChinaRegion_.regionGdp::plus, SQLs::literal, 100, Expression::greatEqual, 0)
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


        final List<FieldMeta<ChinaRegion<?>>> fieldList = new ArrayList<>();
        fieldList.add(ChinaRegion_.name);
        fieldList.add(ChinaRegion_.regionGdp);
        final Update stmt;
        stmt = MySQLs.batchSingleUpdate()
                .update(ChinaRegion_.T, "t")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .setList(fieldList, SQLs::namedNullableParam)
                .where(ChinaRegion_.id::equal, SQLs::literal, paramMap::get, ChinaRegion_.ID)
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
                    .delete(hintSupplier, Arrays.asList(MySQLSyntax._MySQLModifier.LOW_PRIORITY, MySQLSyntax._MySQLModifier.QUICK, MySQLSyntax._MySQLModifier.IGNORE))
                    .from(ChinaRegion_.T, "r")
                    .partition()
                    .leftParen("p1")
                    .rightParen()
                    .where(ChinaRegion_.createTime::between, SQLs::literal, map::get, "startTime", "endTIme")
                    .and(ChinaRegion_.updateTime::between, SQLs::param, map::get, "startTime", "endTIme")
                    .ifAnd(ChinaRegion_.version::equal, SQLs::literal, map::get, "version")
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
                    .delete(hintSupplier, Arrays.asList(MySQLSyntax._MySQLModifier.LOW_PRIORITY, MySQLSyntax._MySQLModifier.QUICK, MySQLSyntax._MySQLModifier.IGNORE))
                    .from(ChinaRegion_.T, "r")
                    .partition()
                    .leftParen("p1")
                    .rightParen()
                    .where(ChinaRegion_.name::equal, SQLs::namedParam) // batch parameter
                    .and(ChinaRegion_.regionGdp::equal, SQLs::namedParam)// batch parameter
                    .and(ChinaRegion_.updateTime::between, SQLs::literal, map::get, "startTime", "endTIme")// common parameter
                    .ifAnd(ChinaRegion_.version::equal, SQLs::literal, map::get, "version")// common parameter
                    .orderBy(ChinaRegion_.name.desc())
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

            final List<MySQLSyntax._MySQLModifier> modifierList;
            modifierList = Arrays.asList(MySQLSyntax._MySQLModifier.LOW_PRIORITY, MySQLSyntax._MySQLModifier.QUICK, MySQLSyntax._MySQLModifier.IGNORE);
            final List<String> deleteTarget = Arrays.asList("c", "r", "u");
            final Delete stmt;
            stmt = MySQLs.multiDelete()
                    .delete(hintSupplier, modifierList, deleteTarget)
                    .from(ChinaCity_.T)
                    .partition()
                    .leftParen("p1")
                    .rightParen()
                    .as("c")
                    .join(ChinaRegion_.T)
                    .partition()
                    .leftParen("p1")
                    .rightParen()
                    .as("r").on(ChinaCity_.id::equal, ChinaRegion_.id)
                    .join(BankUser_.T, "u").on(BankUser_.id::equal, ChinaCity_.id)// delete lonely parent testing
                    .where(ChinaRegion_.createTime::between, SQLs::literal, map::get, "startTime", "endTIme")
                    .and(ChinaRegion_.updateTime::between, SQLs::literal, map::get, "startTime", "endTIme")
                    .ifAnd(ChinaRegion_.version::equal, SQLs::literal, map::get, "version")
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

            final List<MySQLSyntax._MySQLModifier> modifierList;
            modifierList = Arrays.asList(MySQLSyntax._MySQLModifier.LOW_PRIORITY, MySQLSyntax._MySQLModifier.QUICK, MySQLSyntax._MySQLModifier.IGNORE);
            final List<String> deleteTarget = Arrays.asList("c", "r");
            final Delete stmt;
            stmt = MySQLs.batchMultiDelete()
                    .delete(hintSupplier, modifierList, deleteTarget)
                    .from(ChinaCity_.T)
                    .partition()
                    .leftParen("p1")
                    .rightParen()
                    .as("c")
                    .join(ChinaRegion_.T)
                    .partition()
                    .leftParen("p1")
                    .rightParen()
                    .as("r").on(ChinaCity_.id::equal, ChinaRegion_.id)
                    .where(ChinaRegion_.id::equal, SQLs::namedParam)
                    .and(ChinaRegion_.createTime::between, SQLs::literal, map::get, "startTime", "endTIme")
                    .and(ChinaRegion_.updateTime::between, SQLs::literal, map::get, "startTime", "endTIme")
                    .ifAnd(ChinaRegion_.version::equal, SQLs::literal, map::get, "version")
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

            final Object amount = map.get("amount");

            final Update stmt;
            stmt = MySQLs.multiUpdate()
                    .update(hintSupplier, Arrays.asList(MySQLSyntax._MySQLModifier.LOW_PRIORITY, MySQLSyntax._MySQLModifier.IGNORE), BankUser_.T)
                    .partition()
                    .leftParen("p1")
                    .rightParen()
                    .as("u")
                    .useIndex()
                    .forJoin()
                    .leftParen("PRIMARY")
                    .rightParen()
                    .join(BankAccount_.T, "a")
                    .ignoreIndex()
                    .leftParen("idx_account_id")
                    .rightParen()
                    .on(BankUser_.id::equal, BankAccount_.id)
                    .ifSet(BankUser_.nickName, SQLs::param, map::get, "newNickName")
                    .ifSet(BankAccount_.balance, SQLs::plusEqual, SQLs::literal, amount)
                    .where(BankUser_.partnerUserId::equal, SQLs::literal, map::get, "identityId")
                    .ifAnd(BankUser_.nickName::equal, SQLs::param, map::get, "oldNickName")
                    .ifAnd(BankAccount_.createTime::between, SQLs::literal, map::get, "startTime", "endTime")
                    .ifAnd(BankAccount_.version::equal, SQLs::literal, map::get, "version")
                    .ifAnd(BankAccount_.balance::plus, SQLs::literal, amount, Expression::greatEqual, 0)
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
            final BigDecimal amount;
            amount = (BigDecimal) map.get("amount");

            final Update stmt;
            stmt = MySQLs.multiUpdate()
                    .update(BankUser_.T, "u")
                    .join(Person_.T, "p").on(BankUser_.id::equal, Person_.id)
                    .join(PartnerUser_.T, "up").on(BankUser_.id::equal, PartnerUser_.id)
                    .join(BankAccount_.T, "a").on(BankUser_.id::equal, BankAccount_.userId)
                    .set(BankUser_.nickName, SQLs::param, map.get("newNickName"))
                    .ifSet(BankAccount_.balance, SQLs::plusEqual, SQLs::literal, amount)
                    .where(BankUser_.partnerUserId::equal, SQLs::literal, map::get, "identityId")
                    .ifAnd(BankUser_.nickName::equal, SQLs::param, map::get, "oldNickName")
                    .ifAnd(BankAccount_.createTime::between, SQLs::literal, map::get, "startTime", "endTime")
                    .ifAnd(BankAccount_.version::equal, SQLs::literal, map::get, "version")
                    .ifAnd(BankAccount_.balance::plus, SQLs::literal, amount, Expression::greatEqual, 0)
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
                    .set(BankUser_.nickName, SQLs::param, map::get, "newNickName")
                    .set(PartnerUser_.legalPersonId, SQLs::literal, "66666666")
                    .ifSet(BankAccount_.balance, SQLs::plusEqual, SQLs::literal, map::get, "amount")
                    .where(BankUser_.partnerUserId::equal, SQLs::literal, map::get, "identityId")
                    .ifAnd(BankUser_.nickName::equal, SQLs::literal, map::get, "oldNickName")
                    .ifAnd(BankAccount_.createTime::between, SQLs::literal, map::get, "startTime", "endTime")
                    .ifAnd(BankAccount_.version::equal, SQLs::literal, map::get, "version")
                    .ifAnd(BankAccount_.balance::plus, SQLs::literal, map.get("amount"), Expression::greatEqual, 0)
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
                    .update(hintSupplier, Arrays.asList(MySQLSyntax._MySQLModifier.LOW_PRIORITY, MySQLSyntax._MySQLModifier.IGNORE), User_.T)
                    .partition()
                    .leftParen("p1")
                    .rightParen()
                    .as("u")

                    .useIndex()
                    .forJoin()
                    .leftParen("PRIMARY")
                    .rightParen()

                    .join(BankAccount_.T, "a")

                    .ignoreIndex()
                    .forJoin()
                    .leftParen("idx_account_id")
                    .rightParen()

                    .on(User_.id::equal, BankAccount_.id)
                    .set(User_.createTime, SQLs::namedParam)
                    .set(BankAccount_.balance, SQLs::plusEqual, SQLs::namedParam)
                    .where(User_.identityId::equal, SQLs::literal, map::get, "identityId")
                    .ifAnd(User_.nickName::equal, SQLs::literal, map::get, "oldNickName")
                    .and(BankAccount_.createTime::between, SQLs::literal, map::get, "startTime", "endTime")
                    .ifAnd(BankAccount_.createTime::between, SQLs::literal, map::get, "startTime", "endTime")
                    .ifAnd(BankAccount_.version::equal, SQLs::literal, map::get, "version")
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
            List<Long> idList = new ArrayList<>();
            final Select stmt;
            stmt = MySQLs.query()
                    .select(SQLs.group(Captcha_.T, "c"))
                    .from(Captcha_.T, "c")
                    .where(Captcha_.id::in, SQLs::multiLiterals, idList)
                    .and(Captcha_.createTime::between, SQLs::literal, criteria::get, "startTime", "endTime")
                    .and(Captcha_.deadline::greatEqual, SQLs::literal, LocalDateTime.now())
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
                    .where(BankUser_.id::in, SQLs::multiLiterals, (Collection<?>) criteria.get("ids"))
                    .and(BankAccount_.createTime::between, SQLs::literal, criteria::get, "startTime", "endTime")
                    .and(BankUser_.updateTime::greatEqual, SQLs::literal, LocalDateTime.now())
                    .and(SQLs::exists, () -> MySQLs.subQuery()
                            .select(RegisterRecord_.id)
                            .from(RegisterRecord_.T, "r")
                            .where(RegisterRecord_.userId::equal, BankUser_.id)
                            .asQuery())
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
    public void parentDomainInsert() {
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(Collections::emptyList, Collections.singletonList(MySQLs.HIGH_PRIORITY))
                .into(ChinaRegion_.T)
                //.insertInto(ChinaCity_.T)
                .partition()
                .leftParen("P1", "P2")
                .rightParen()
                .value(new ChinaRegion<>())
                .onDuplicateKey()
                .set(ChinaCity_.name, SQLs::param, "荒海")
                .asInsert();

        printStmt(stmt);
    }

    @Test
    public void assignmentInsert() {
//        final Insert stmt;
//        stmt = MySQLs.assignmentInsert()
//                .insert(Collections::emptyList, Collections.singletonList(MySQLWords.HIGH_PRIORITY))
//                .into(ChinaCity_.T)
//                .partition("P1", "P2")
//                .set(ChinaCity_.mayorName, "脉兽秀秀")
//                .setDefault(ChinaCity_.regionGdp)
//                .as("c")
//                .leftParen(ChinaCity_.ID)
//                .rightParen()
//                .onDuplicateKeyUpdate()
//                .set(ChinaCity_.updateTime, LocalDateTime.now())
//                .set(ChinaCity_.version, SQLs::plusEqual, 1)
//                .asInsert();
//        printStmt(stmt);
    }


    private List<ChinaCity> createCityList() {
        return Collections.emptyList();
    }


    private void printStmt(final PrimaryStatement statement) {
        for (MySQLDialect dialect : MySQLDialect.values()) {
            LOG.debug("{}:\n{}", dialect.name(), statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
        }

    }


}
