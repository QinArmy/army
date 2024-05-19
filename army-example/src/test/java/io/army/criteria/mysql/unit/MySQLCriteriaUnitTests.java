/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.mysql.unit;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.standard.SQLs;
import io.army.example.bank.domain.account.BankAccount_;
import io.army.example.bank.domain.user.*;
import io.army.example.common.Criteria;
import io.army.example.pill.domain.PillUser_;
import io.army.util._Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.army.criteria.standard.SQLs.*;

public class MySQLCriteriaUnitTests extends MySQLUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLCriteriaUnitTests.class);

    @Test
    public void parentSingleUpdate() {
        final Criteria criteria = new Criteria();
        criteria.setRowCount(5L);

        final Update stmt;
        stmt = MySQLs.singleUpdate()
                .update(ChinaRegion_.T, AS, "t")
                .set(ChinaRegion_.name, SQLs::param, "五指礁")
                .where(ChinaRegion_.name::equal, SQLs::param, "索隆")
                .and(ChinaRegion_.regionType.equal(SQLs::literal, RegionType.CITY).or(ChinaRegion_.regionGdp.greaterEqual(SQLs::literal, "3333")))
                .orderBy(ChinaRegion_.id)
                .limit(SQLs::param, criteria::getRowCount)
                .asUpdate();

        print80Stmt(LOG, stmt);


    }

    @Test
    public void childSingleUpdate() {
        final Map<String, Object> map = _Collections.hashMap();
        map.put("rowCount", 5);
        map.put("parentId", "1");
        Supplier<List<Hint>> supplier = () -> {
            List<Hint> list = _Collections.arrayList();
            list.add(MySQLs.qbName("qb1"));
            list.add(MySQLs.orderIndex("qb1", "t", Collections.singletonList("uni_name_region_type")));
            return list;
        };

        final Update stmt;
        stmt = MySQLs.singleUpdate()
                .update(supplier, Arrays.asList(MySQLs.LOW_PRIORITY, MySQLs.IGNORE))
                .space(ChinaRegion_.T)
                .partition("p2", "p1")
                .as("t")
                .useIndex(FOR, ORDER_BY, "uni_name_region_type")
                .ignoreIndex(FOR, ORDER_BY, "uni_name_region_type")

                .set(ChinaRegion_.name, SQLs::param, "五指礁")
                .set(ChinaRegion_.regionGdp, SQLs::literal, 100)
                .where(ChinaRegion_.name.equal(SQLs::literal, ""))
                .and(ChinaRegion_.parentId.equal(SQLs::param, map.get("parentId"))
                        .or(ChinaRegion_.regionType.equal(SQLs::literal, RegionType.CITY))
                )
                .and(ChinaRegion_.regionGdp::plus, SQLs::literal, 100, Expression::greaterEqual, LITERAL_0)
                .orderBy(ChinaRegion_.name::desc)
                .limit(SQLs::param, map::get, "rowCount")
                .asUpdate();

        print80Stmt(LOG, stmt);
    }

    @Test
    public void simpleBatchSingleUpdate() {

        final List<Map<String, Object>> paramList = _Collections.arrayList(3);
        Map<String, Object> paramMap;

        paramMap = _Collections.hashMap();
        paramMap.put(ChinaRegion_.ID, 3);
        paramMap.put(ChinaRegion_.NAME, "五指礁");
        paramMap.put(ChinaRegion_.REGION_GDP, 100);
        paramList.add(paramMap);

        paramMap = _Collections.hashMap();
        paramMap.put(ChinaRegion_.ID, 8L);
        paramMap.put(ChinaRegion_.NAME, "荒海");
        paramMap.put(ChinaRegion_.REGION_GDP, new BigDecimal("324342"));
        paramList.add(paramMap);


        final BatchUpdate stmt;
        stmt = MySQLs.batchSingleUpdate()
                .update(ChinaRegion_.T, AS, "t")
                .setSpace(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .setSpace(ChinaRegion_.name, SQLs::namedParam)
                .where(ChinaRegion_.id::equal, SQLs::literal, paramMap.get(ChinaRegion_.ID))
                .limit(SQLs::param, 10)
                .asUpdate()
                .namedParamList(paramList);

        print80Stmt(LOG, stmt);
    }


    @Test
    public void singleDelete57WithCriteriaMap() {
        final Map<String, Object> map = _Collections.hashMap();
        final LocalDateTime now = LocalDateTime.now();
        map.put("startTime", now.minusDays(15));
        map.put("endTIme", now.plusDays(6));
        map.put("version", "0");

        //map.put("rowCount",(byte)36);

        final Supplier<List<Hint>> hintSupplier = () -> {
            final List<Hint> hintList = new ArrayList<>(2);
            hintList.add(MySQLs.qbName("regionDelete"));
            hintList.add(MySQLs.orderIndex("regionDelete", "r", Collections.singletonList("PRIMARY")));
            return hintList;
        };
        final Delete stmt;
        stmt = MySQLs.singleDelete()
                .delete(hintSupplier, Arrays.asList(MySQLs.LOW_PRIORITY, MySQLs.QUICK, MySQLs.IGNORE))
                .from(ChinaRegion_.T, AS, "r")
                .partition("p1")
                .where(ChinaRegion_.createTime::between, SQLs::literal, map.get("startTime"), AND, map.get("endTIme"))
                .and(ChinaRegion_.updateTime::between, SQLs::param, map.get("startTime"), AND, map.get("endTIme"))
                .ifAnd(ChinaRegion_.version::equal, SQLs::literal, map::get, "version")
                .orderBy(ChinaRegion_.name::desc, ChinaRegion_.id)
                .ifLimit(SQLs::param, map::get, "rowCount")
                .asDelete();

        print80Stmt(LOG, stmt);

    }

    @Test
    public void batchSingleDelete57WithCriteriaMap() {

        final Map<String, Object> map = _Collections.hashMap();
        final LocalDateTime now = LocalDateTime.now();
        map.put("startTime", now.minusDays(15));
        map.put("endTIme", now.plusDays(6));
        map.put("version", "0");

        final Supplier<List<Hint>> hintSupplier = () -> {
            final List<Hint> hintList = new ArrayList<>(2);
            hintList.add(MySQLs.qbName("regionDelete"));
            hintList.add(MySQLs.orderIndex("regionDelete", "r", Collections.singletonList("PRIMARY")));
            return hintList;
        };

        final List<Map<String, Object>> paramList = _Collections.arrayList();
        Map<String, Object> paramMap;

        paramMap = _Collections.hashMap();
        paramMap.put("name", "水城");
        paramMap.put("regionGdp", "39999.00");

        paramList.add(paramMap);

        paramMap = _Collections.hashMap();
        paramMap.put("name", "凉都");
        paramMap.put("regionGdp", new BigDecimal("99999.00"));

        paramList.add(paramMap);


        final BatchDelete stmt;
        stmt = MySQLs.batchSingleDelete()
                .delete(hintSupplier, Arrays.asList(MySQLs.LOW_PRIORITY, MySQLs.QUICK, MySQLs.IGNORE))
                .from(ChinaRegion_.T, AS, "r")
                .partition("p1")
                .where(ChinaRegion_.name::equal, SQLs::namedParam) // batch parameter
                .and(ChinaRegion_.regionGdp::equal, SQLs::namedParam)// batch parameter
                .and(ChinaRegion_.updateTime::between, SQLs::literal, map.get("startTime"), AND, map.get("endTIme"))
                .ifAnd(ChinaRegion_.version::equal, SQLs::literal, map::get, "version")// common parameter
                .orderBy(ChinaRegion_.name::desc)
                .ifLimit(SQLs::param, map::get, "rowCount")
                .asDelete()
                .namedParamList(paramList);

        print80Stmt(LOG, stmt);

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

            final List<MySQLs.Modifier> modifierList;
            modifierList = Arrays.asList(MySQLs.LOW_PRIORITY, MySQLs.QUICK, MySQLs.IGNORE);
            final Delete stmt;
            stmt = MySQLs.multiDelete()
                    .delete(hintSupplier, modifierList)
                    .from("c", "r", "u")
                    .using(ChinaCity_.T)
                    .partition("p1")
                    .as("c")
                    .join(ChinaRegion_.T)
                    .partition("p1")
                    .as("r").on(ChinaCity_.id::equal, ChinaRegion_.id)
                    .join(BankUser_.T, AS, "u").on(BankUser_.id::equal, ChinaCity_.id)// delete lonely parent testing
                    .where(ChinaRegion_.createTime::between, SQLs::literal, map.get("startTime"), AND, map.get("endTIme"))
                    .and(ChinaRegion_.updateTime::between, SQLs::literal, map.get("startTime"), AND, map.get("endTIme"))
                    .ifAnd(ChinaRegion_.version::equal, SQLs::literal, map::get, "version")
                    .asDelete();

            print80Stmt(LOG, stmt);
        };


        final Map<String, Object> map = _Collections.hashMap();
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

            final List<Map<String, Object>> paramList = _Collections.arrayList();
            paramList.add(Collections.singletonMap(ChinaCity_.ID, "33"));
            paramList.add(Collections.singletonMap(ChinaCity_.ID, (byte) 22));
            paramList.add(Collections.singletonMap(ChinaCity_.ID, (short) 44));
            paramList.add(Collections.singletonMap(ChinaCity_.ID, 22));
            paramList.add(Collections.singletonMap(ChinaCity_.ID, 88L));

            final BatchDelete stmt;
            stmt = MySQLs.batchMultiDelete()
                    .delete(hintSupplier, Arrays.asList(MySQLs.LOW_PRIORITY, MySQLs.QUICK, MySQLs.IGNORE))
                    .from("c", "r")
                    .using(ChinaCity_.T)
                    .partition("p1")
                    .as("c")
                    .join(ChinaRegion_.T)
                    .partition("p1")
                    .as("r").on(ChinaCity_.id::equal, ChinaRegion_.id)
                    .where(ChinaRegion_.id::equal, SQLs::namedParam)
                    .and(ChinaRegion_.createTime::between, SQLs::literal, map.get("startTime"), AND, map.get("endTIme"))
                    .and(ChinaRegion_.updateTime::between, SQLs::literal, map.get("startTime"), AND, map.get("endTIme"))
                    .ifAnd(ChinaRegion_.version::equal, SQLs::literal, map::get, "version")
                    .asDelete()
                    .namedParamList(paramList);

            print80Stmt(LOG, stmt);
        };


        final Map<String, Object> map = _Collections.hashMap();
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

        final Map<String, Object> map = _Collections.hashMap();
        final LocalDateTime now = LocalDateTime.now();

        map.put("amount", "888888.88");
        map.put("startTime", now.minusDays(15));
        map.put("endTIme", now.plusDays(6));
        map.put("version", "0");

        map.put("identityId", "6668888");


        final Supplier<List<Hint>> hintSupplier = () -> {
            final List<Hint> hintList = new ArrayList<>(2);
            hintList.add(MySQLs.qbName("regionDelete"));
            hintList.add(MySQLs.orderIndex("regionDelete", "r", Collections.singletonList("PRIMARY")));
            return hintList;
        };

        final Object amount = map.get("amount");

        final Update stmt;
        stmt = MySQLs.multiUpdate()
                .update(hintSupplier, Arrays.asList(MySQLs.LOW_PRIORITY, MySQLs.IGNORE))
                .space(BankUser_.T).partition("p1").as("u").useIndex(FOR, JOIN, "PRIMARY")
                .join(BankAccount_.T, AS, "a").ignoreIndex("idx_account_id")
                .on(BankUser_.id::equal, BankAccount_.id)
                .ifSet(BankUser_.nickName, SQLs::param, map::get, "newNickName")
                .ifSet(BankAccount_.balance, SQLs::plusEqual, SQLs::literal, () -> amount)
                .whereIf(BankUser_.partnerUserId::equal, SQLs::literal, map::get, "identityId")
                .ifAnd(BankUser_.nickName::equal, SQLs::param, map::get, "oldNickName")
                .ifAnd(BankAccount_.createTime::between, SQLs::literal, map::get, "startTime", AND, "endTime")
                .ifAnd(BankAccount_.version::equal, SQLs::literal, map::get, "version")
                .ifAnd(BankAccount_.balance::plus, SQLs::literal, amount, Expression::greaterEqual, LITERAL_0)
                .asUpdate();

        print80Stmt(LOG, stmt);
    }

    @Test
    public void multiUpdateChildFromLeft() {

        final Map<String, Object> map = _Collections.hashMap();
        final LocalDateTime now = LocalDateTime.now();

        map.put("amount", new BigDecimal("888888.88"));
        map.put("startTime", now.minusDays(15));
        map.put("endTIme", now.plusDays(6));
        map.put("version", "8");

        map.put("identityId", "6668888");
        map.put("oldNickName", "zoro");
        map.put("newNickName", "索隆");

        final BigDecimal amount;
        amount = (BigDecimal) map.get("amount");

        final Update stmt;
        stmt = MySQLs.multiUpdate()
                .update(BankUser_.T, AS, "u")
                .join(BankPerson_.T, AS, "p").on(BankUser_.id::equal, BankPerson_.id)
                .join(PartnerUser_.T, AS, "up").on(BankUser_.id::equal, PartnerUser_.id)
                .join(BankAccount_.T, AS, "a").on(BankUser_.id::equal, BankAccount_.userId)
                .set(BankUser_.nickName, SQLs::param, map.get("newNickName"))
                .ifSet(BankAccount_.balance, SQLs::plusEqual, SQLs::literal, () -> amount)
                .whereIf(BankUser_.partnerUserId::equal, SQLs::literal, map::get, "identityId")
                .ifAnd(BankUser_.nickName::equal, SQLs::param, map::get, "oldNickName")
                .ifAnd(BankAccount_.createTime::between, SQLs::literal, map::get, "startTime", AND, "endTime")
                .ifAnd(BankAccount_.version::equal, SQLs::literal, map::get, "version")
                .ifAnd(BankAccount_.balance::plus, SQLs::literal, amount, Expression::greaterEqual, LITERAL_0)
                .asUpdate();

        print80Stmt(LOG, stmt);
    }


    @Test
    public void multiUpdateChildFromRight() {

        final Map<String, Object> map = _Collections.hashMap();
        final LocalDateTime now = LocalDateTime.now();

        map.put("amount", "888888.88");
        map.put("startTime", now.minusDays(15));
        map.put("endTIme", now.plusDays(6));
        map.put("version", "8");

        map.put("identityId", "6668888");
        map.put("oldNickName", "zoro");
        map.put("newNickName", "索隆");

        final Update stmt;
        stmt = MySQLs.multiUpdate()
                .update(PartnerUser_.T, AS, "up")
                .join(BankUser_.T, AS, "u").on(BankUser_.id::equal, PartnerUser_.id)
                .join(BankAccount_.T, AS, "a").on(BankUser_.id::equal, BankAccount_.userId)
                .set(BankUser_.nickName, SQLs::param, map::get, "newNickName")
                .set(PartnerUser_.legalPersonId, SQLs::literal, "66666666")
                .ifSet(BankAccount_.balance, SQLs::plusEqual, SQLs::literal, map::get, "amount")
                .whereIf(BankUser_.partnerUserId::equal, SQLs::literal, map::get, "identityId")
                .ifAnd(BankUser_.nickName::equal, SQLs::literal, map::get, "oldNickName")
                .ifAnd(BankAccount_.createTime::between, SQLs::literal, map::get, "startTime", AND, "endTime")
                .ifAnd(BankAccount_.version::equal, SQLs::literal, map::get, "version")
                .ifAnd(BankAccount_.balance::plus, SQLs::literal, map.get("amount"), Expression::greaterEqual, LITERAL_0)
                .asUpdate();

        print80Stmt(LOG, stmt);

    }

    @Test
    public void batchMultiUpdateWithMapCriteria() {

        final Map<String, Object> map = _Collections.hashMap();
        final LocalDateTime now = LocalDateTime.now();

        map.put("amount", "888888.88");
        map.put("startTime", now.minusDays(15));
        map.put("endTime", now.plusDays(6));
        map.put("version", "0");

        map.put("identityId", "6668888");


        final List<Map<String, Object>> paramList = _Collections.arrayList();
        Map<String, Object> paramMap;

        paramMap = _Collections.hashMap();
        paramMap.put("nickName", "索隆1");
        paramMap.put("balance", "666888.00");
        paramList.add(paramMap);

        paramMap = _Collections.hashMap();
        paramMap.put("nickName", "索隆2");
        paramMap.put("balance", new BigDecimal("888666.00"));
        paramList.add(paramMap);


        final Supplier<List<Hint>> hintSupplier = () -> {
            final List<Hint> hintList = new ArrayList<>(2);
            hintList.add(MySQLs.qbName("regionDelete"));
            hintList.add(MySQLs.orderIndex("regionDelete", "r", Collections.singletonList("PRIMARY")));
            return hintList;
        };


        final BatchUpdate stmt;
        stmt = MySQLs.batchMultiUpdate()
                .update(hintSupplier, Arrays.asList(MySQLs.LOW_PRIORITY, MySQLs.IGNORE))
                .space(PillUser_.T).partition("p1").as("u").useIndex(FOR, JOIN, "PRIMARY")
                .join(BankAccount_.T, AS, "a").ignoreIndex(FOR, JOIN, "idx_account_id").on(PillUser_.id::equal, BankAccount_.id)
                .setSpace(PillUser_.nickName, SQLs::namedParam)
                .setSpace(BankAccount_.balance, SQLs::plusEqual, SQLs::namedParam)
                .whereIf(PillUser_.identityId::equal, SQLs::literal, map::get, "identityId")
                .ifAnd(PillUser_.nickName::equal, SQLs::literal, map::get, "oldNickName")
                .ifAnd(BankAccount_.createTime::between, SQLs::literal, map::get, "startTime", AND, "endTime")
                .ifAnd(BankAccount_.createTime::between, SQLs::literal, map::get, "startTime", AND, "endTime")
                .ifAnd(BankAccount_.version::equal, SQLs::literal, map::get, "version")
                .asUpdate()
                .namedParamList(paramList);

        print80Stmt(LOG, stmt);


    }


    @Test
    public void singleSelect() {

        final LocalDateTime now = LocalDateTime.now();
        final Map<String, Object> map = _Collections.hashMap();

        map.put("ids", Arrays.asList(11, 22, 33, 44, 55, 66, 77));
        map.put("startTime", now.minusDays(5));
        map.put("endTime", now.plusDays(5));

        List<Long> idList = Arrays.asList(1L, 2L);
        final Select stmt;
        stmt = MySQLs.query()
                .select("c", SQLs.PERIOD, Captcha_.T)
                .from(Captcha_.T, AS, "c")
                .where(Captcha_.id.in(SQLs::rowParam, idList))
                .ifAnd(Captcha_.createTime::between, SQLs::literal, map::get, "startTime", AND, "endTime")
                .ifAnd(Captcha_.deadline::greaterEqual, SQLs::literal, LocalDateTime::now)
                .asQuery();

        print80Stmt(LOG, stmt);

    }

    @Test
    public void multiSelect() {
        final Consumer<Map<String, Object>> mockDaoMethod = criteria -> {
            final Select stmt;
            stmt = MySQLs.query()
                    .select("u", PERIOD, BankUser_.T)
                    .from(BankUser_.T, AS, "u")
                    .join(BankAccount_.T, AS, "a").on(BankUser_.id::equal, BankAccount_.id)
                    .where(BankAccount_.id.equal(BankAccount_.id))
                    //.where(BankUser_.id::in, SQLs::multiLiteral, (Collection<?>) criteria.get("ids")) //TODO
                    .ifAnd(BankAccount_.createTime::between, SQLs::literal, criteria::get, "startTime", AND, "endTime")
                    .ifAnd(BankUser_.updateTime::greaterEqual, SQLs::literal, LocalDateTime::now)
                    .and(SQLs::exists, () -> MySQLs.subQuery()
                            .select(RegisterRecord_.id)
                            .from(RegisterRecord_.T, AS, "r")
                            .where(RegisterRecord_.userId::equal, BankUser_.id)
                            .asQuery())
                    .asQuery();

            print80Stmt(LOG, stmt);

        };


        final LocalDateTime now = LocalDateTime.now();
        final Map<String, Object> map = _Collections.hashMap();


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
                .partition("P1", "P2")
                .value(new ChinaRegion<>())
                .onDuplicateKey()
                .update(ChinaRegion_.name, SQLs::param, "荒海")
                .asInsert();

        print80Stmt(LOG, stmt, Visible.BOTH);
    }

    @Test
    public void assignmentInsert() {
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .insert(Collections::emptyList, Collections.singletonList(MySQLs.HIGH_PRIORITY))
                .into(ChinaRegion_.T)
                .partition("P1", "P2")
                .set(ChinaRegion_.name, SQLs::param, randomCity())
                .as("c")
                .onDuplicateKey()
                .update(ChinaRegion_.population, SQLs::param, 0)
                .asInsert();

        print80Stmt(LOG, stmt, Visible.BOTH);
    }


}
