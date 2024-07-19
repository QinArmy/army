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

package io.army.session.sync.postgre;


import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.dialect.ReturningInsert;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._ReturningDml;
import io.army.example.bank.domain.user.*;
import io.army.option.Option;
import io.army.result.ResultStates;
import io.army.sync.SyncLocalSession;
import io.army.sync.SyncStmtOption;
import io.army.util.ImmutableArrayList;
import io.army.util.ImmutableHashMap;
import io.army.util.RowMaps;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static io.army.criteria.impl.SQLs.*;

@Test(dataProvider = "localSessionProvider")
public class InsertTests extends SessionTestSupport {


    // @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainInsertParent(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList;
        regionList = createReginListWithCount(3);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T).as("c")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertFalse(stmt instanceof _ReturningDml);

        Assert.assertEquals(session.update(stmt), regionList.size());
        assertChinaRegionAfterNoConflictInsert(regionList);

    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainInsertParentAsStates(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = Postgres.singleInsert()
                //  .literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::param, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::param, 0)
                .values(regionList)
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertFalse(stmt instanceof _ReturningDml);


        final ResultStates states;
        states = session.updateAsStates(stmt);

        Assert.assertEquals(states.affectedRows(), regionList.size());
        if (states.isSupportInsertId()) {
            Assert.assertEquals(states.lastInsertedId(), regionList.get(0).getId());
        }
        Assert.assertEquals(states.batchSize(), 0);
        Assert.assertEquals(states.resultNo(), 1);

        Assert.assertFalse(states.hasMoreResult());
        Assert.assertFalse(states.hasMoreFetch());
        Assert.assertFalse(states.inTransaction());
        Assert.assertTrue(states.hasColumn());      // army auto append RETURNING id clause,because ChinaRegion primary key is auto increment
        Assert.assertEquals(states.rowCount(), regionList.size());

        assertChinaRegionAfterNoConflictInsert(regionList);

    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void returningDomainInsertParent(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList;
        regionList = createReginListWithCount(3);

        final long startNanoSecond = System.nanoTime();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T).as("c")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .returningAll()
                .asReturningInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final boolean[] flagHolder = new boolean[1];

        final Consumer<ResultStates> statesConsumer;
        statesConsumer = states -> {
            flagHolder[0] = true;

            Assert.assertEquals(states.affectedRows(), regionList.size());
            if (states.isSupportInsertId()) {
                Assert.assertEquals(states.lastInsertedId(), 0L);
            }

            Assert.assertEquals(states.batchSize(), 0);
            Assert.assertEquals(states.resultNo(), 1);

            Assert.assertFalse(states.hasMoreResult());
            Assert.assertFalse(states.hasMoreFetch());
            Assert.assertFalse(states.inTransaction());
            Assert.assertTrue(states.hasColumn());
            Assert.assertEquals(states.rowCount(), regionList.size());


        };

        final List<ChinaRegion<?>> resultList;
        resultList = session.queryList(stmt, ChinaRegion_.CLASS, SyncStmtOption.stateConsumer(statesConsumer));

        Assert.assertEquals(resultList.size(), regionList.size());
        Assert.assertTrue(flagHolder[0]);

        assertChinaRegionAfterNoConflictInsert(regionList);

    }

    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void returningDomainInsertParentWithFetch(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final int fetchSize = 5;

        final List<ChinaRegion<?>> regionList;
        regionList = createReginListWithCount(fetchSize * 4 + 1);

        final long startNanoSecond = System.nanoTime();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T).as("c")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .returningAll()
                .asReturningInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final boolean[] flagHolder = new boolean[1];
        final int[] fetchRowHolder = new int[]{0};
        final Consumer<ResultStates> statesConsumer;
        statesConsumer = states -> {
            flagHolder[0] = true;

            final long currentFetchRow;
            currentFetchRow = states.rowCount();


            Assert.assertEquals(states.affectedRows(), currentFetchRow);
            fetchRowHolder[0] += (int) currentFetchRow;

            if (currentFetchRow < fetchSize) {
                Assert.assertFalse(states.hasMoreFetch());
            } else {
                Assert.assertEquals(currentFetchRow, fetchSize);
                if (fetchRowHolder[0] == regionList.size()) {
                    Assert.assertFalse(states.hasMoreFetch());
                } else {
                    Assert.assertTrue(states.hasMoreFetch());
                }
            }
            if (states.isSupportInsertId()) {
                Assert.assertEquals(states.lastInsertedId(), 0L);
            }

            Assert.assertEquals(states.batchSize(), 0);
            Assert.assertEquals(states.resultNo(), 1);

            Assert.assertFalse(states.hasMoreResult());
            Assert.assertTrue(states.inTransaction()); // postgre jdbc command autoCommit

        };

        final SyncStmtOption stmtOption;
        stmtOption = SyncStmtOption.builder()
                .stateConsumer(statesConsumer)
                .fetchSize(fetchSize)
                .build();

        final List<ChinaRegion<?>> rowList;
        rowList = session.queryList(stmt, ChinaRegion_.CLASS, stmtOption);

        Assert.assertEquals(rowList.size(), regionList.size());
        Assert.assertEquals(rowList.size(), fetchRowHolder[0]);
        Assert.assertTrue(flagHolder[0]);

        assertChinaRegionAfterNoConflictInsert(regionList);
    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainInsertParentWithDoNothing(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList;
        regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final long startNanoSecond = System.nanoTime();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .ignoreReturnIds()  // required ,because exists doNothing
                .insertInto(ChinaRegion_.T).as("c")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.parentId)
                        .comma(ChinaRegion_.regionGdp)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .onConflict()
                .doNothing()
                .returningAll()
                .asReturningInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final boolean[] flagHolder = new boolean[]{false};

        final Consumer<ResultStates> statesConsumer;
        statesConsumer = states -> {
            Assert.assertEquals(states.affectedRows(), 0L);
            if (states.isSupportInsertId()) {
                Assert.assertEquals(states.lastInsertedId(), 0L);
            }

            Assert.assertEquals(states.batchSize(), 0);
            Assert.assertEquals(states.resultNo(), 1);

            Assert.assertFalse(states.hasMoreResult());
            Assert.assertFalse(states.hasMoreFetch());
            Assert.assertFalse(states.inTransaction());
            Assert.assertTrue(states.hasColumn());

            Assert.assertEquals(states.rowCount(), states.affectedRows());

            flagHolder[0] = true;
        };

        final List<ChinaRegion<?>> resultList;
        resultList = session.queryList(stmt, ChinaRegion_.CLASS, SyncStmtOption.stateConsumer(statesConsumer));

        Assert.assertEquals(resultList.size(), 0L); // because conflict do nothing
        Assert.assertTrue(flagHolder[0]);


    }

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainInsertParentWithUpdateSet(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList;
        regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final long startNanoSecond = System.nanoTime();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .ignoreReturnIds()  // required ,because ChinaRegion_ contain visible field
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T).as("c")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.parentId)
                        .comma(ChinaRegion_.regionGdp)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .onConflict()
                .parens(s -> s.space(ChinaRegion_.name)
                        .comma(ChinaRegion_.regionType)
                )
                .doUpdate()
                .set(ChinaRegion_.name, Postgres::excluded)
                .returningAll()
                .asReturningInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertTrue(stmt instanceof _ReturningDml);

        List<ChinaRegion<?>> resultList;
        resultList = session.queryList(stmt, ChinaRegion_.CLASS);
        Assert.assertEquals(resultList.size(), regionList.size());

    }

    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainInsertChildWithTowStmtUpdateMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = createProvinceListWithCount(3);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .values(provinceList)
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values(provinceList)
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertFalse(stmt instanceof _ReturningDml);
        Assert.assertEquals(session.update(stmt), provinceList.size());
        assertChinaRegionAfterNoConflictInsert(provinceList);


    }

    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void returningDomainInsertChildWithTowStmtQueryMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = createProvinceListWithCount(3);

        final long startNanoSecond = System.nanoTime();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .values(provinceList)
                .returningAll()
                .asReturningInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values(provinceList)
                .returningAll()
                .asReturningInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final int[] flagHolder = new int[]{0};

        final Consumer<ResultStates> statesConsumer;
        statesConsumer = states -> {
            flagHolder[0]++;

            final Boolean secondDmlStates = states.valueOf(Option.SECOND_DML_QUERY_STATES);

            if (flagHolder[0] == 1) {
                Assert.assertTrue(secondDmlStates == null || !secondDmlStates);
            } else {
                Assert.assertEquals(secondDmlStates, Boolean.TRUE);
            }

            Assert.assertEquals(states.affectedRows(), provinceList.size());
            if (states.isSupportInsertId()) {
                Assert.assertEquals(states.lastInsertedId(), provinceList.get(0).getId());
            }

            Assert.assertEquals(states.batchSize(), 0);
            Assert.assertEquals(states.batchNo(), 0);
            Assert.assertEquals(states.resultNo(), 1);

            Assert.assertFalse(states.hasMoreResult());
            Assert.assertFalse(states.hasMoreFetch());
            Assert.assertTrue(states.inTransaction());
            Assert.assertTrue(states.hasColumn());

            Assert.assertEquals(states.rowCount(), states.affectedRows());


        };

        final List<ChinaProvince> resultList;
        resultList = session.queryList(stmt, ChinaProvince.class, ArrayList::new, SyncStmtOption.stateConsumer(statesConsumer));

        Assert.assertEquals(resultList.size(), provinceList.size());
        Assert.assertEquals(flagHolder[0], 2);
        assertChinaRegionAfterNoConflictInsert(provinceList);

    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void returningDomainInsertDiffMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        final long startNanoSecond = System.nanoTime();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .values(provinceList)
                .asInsert() // use asInsert not asReturningInsert

                .child()

                .insertInto(ChinaProvince_.T)
                .values(provinceList)
                .returningAll()
                .asReturningInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);
        Assert.assertTrue(stmt instanceof _ReturningDml);

        final List<ChinaProvince> resultList;
        resultList = session.queryList(stmt, ChinaProvince.class, ImmutableArrayList::arrayList);

        Assert.assertEquals(resultList.size(), provinceList.size());
        assertChinaRegionAfterNoConflictInsert(provinceList);
    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void returningDomainInsertChildMapWithTowStmtQueryMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .values(provinceList)
                .returningAll()
                .asReturningInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values(provinceList)
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);


        final List<Map<String, Object>> resultList;
        resultList = session.queryObjectList(stmt, RowMaps::hashMap, ImmutableArrayList::arrayList);

        Assert.assertEquals(resultList.size(), provinceList.size());

    }

    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void returningDomainInsertChildMapDiffMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .values(provinceList)
                .asInsert() // use asInsert not asReturningInsert

                .child()

                .insertInto(ChinaProvince_.T)
                .values(provinceList)
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final List<Map<String, Object>> resultList;
        resultList = session.queryObjectList(stmt, ImmutableHashMap::hashMap, ImmutableArrayList::arrayList);

        Assert.assertEquals(resultList.size(), provinceList.size());
        assertChinaRegionAfterNoConflictInsert(provinceList);

    }


    /*-------------------below values syntax tests -------------------*/

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void staticValuesInsertParent(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final Insert stmt;
        stmt = Postgres.singleInsert()
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .asInsert();

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, 2L);

    }

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void staticValuesReturningInsertParent(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .returningAll()
                .asReturningInsert();

        final List<ChinaRegion<?>> resultList;
        resultList = session.queryList(stmt, ChinaRegion_.CLASS, ImmutableArrayList::arrayList);

        Assert.assertEquals(resultList.size(), 2);
        assertChinaRegionAfterNoConflictInsert(resultList);

    }

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void valuesInsertParentWithDoNothing(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();
        final Insert stmt;
        stmt = Postgres.singleInsert()
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .onConflict()
                .doNothing()
                .asInsert();

        Assert.assertFalse(stmt instanceof _ReturningDml);

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, 2);

    }

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void valuesInsertParentWithUpdateSet(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList = createReginListWithCount(2);
        session.batchSave(regionList);

        final Random random = ThreadLocalRandom.current();

        // conflict stmt
        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T).as("c")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, regionList.get(0).getName())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, regionList.get(1).getName())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .onConflict()
                .parens(s -> s.space(ChinaRegion_.name)
                        .comma(ChinaRegion_.regionType)
                )
                .doUpdate()
                .set(ChinaRegion_.name, Postgres::excluded)
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final List<ChinaRegion<?>> resultList;
        resultList = session.queryList(stmt, ChinaRegion_.CLASS);

        Assert.assertEquals(resultList.size(), 2);


    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void valuesInsertChildWithTowStmtUpdateMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .asInsert();

        Assert.assertFalse(stmt instanceof _ReturningDml);


        Assert.assertEquals(session.update(stmt), 2);

    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void returningValuesInsertChildWithTowStmtQueryMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .returningAll()
                .asReturningInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final int[] flagsHolder = new int[]{0};
        final Consumer<ResultStates> statesConsumer;
        statesConsumer = states -> {
            flagsHolder[0]++;
            final Boolean secondDmlStates = states.valueOf(Option.SECOND_DML_QUERY_STATES);

            if (flagsHolder[0] == 1) {
                Assert.assertTrue(secondDmlStates == null || !secondDmlStates);
            } else {
                Assert.assertEquals(secondDmlStates, Boolean.TRUE);
            }

            Assert.assertTrue(states.inTransaction());
            Assert.assertEquals(states.batchSize(), 0);
            Assert.assertEquals(states.batchNo(), 0);
            Assert.assertTrue(states.hasColumn());

            Assert.assertEquals(states.affectedRows(), states.rowCount());
            Assert.assertEquals(states.rowCount(), 2L);
            Assert.assertFalse(states.hasMoreResult());
            Assert.assertFalse(states.hasMoreFetch());

        };


        final List<ChinaProvince> resultList;
        resultList = session.queryList(stmt, ChinaProvince.class, ArrayList::new, SyncStmtOption.stateConsumer(statesConsumer));

        Assert.assertEquals(resultList.size(), 2L);
        Assert.assertEquals(flagsHolder[0], 2);

    }

    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void returningValuesInsertDiffMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final List<ChinaProvince> resultList;
        resultList = session.queryList(stmt, ChinaProvince.class, ImmutableArrayList::arrayList);

        Assert.assertEquals(resultList.size(), 2);
    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void returningMapValuesInsertChildWithTowStmtQueryMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .returningAll()
                .asReturningInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final List<Map<String, Object>> resultList;
        resultList = session.queryObjectList(stmt, RowMaps::hashMap, ArrayList::new);

        Assert.assertEquals(resultList.size(), 2);

    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void returningMapValuesInsertDiffMode(final SyncLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .returningAll()
                .asReturningInsert();

        Assert.assertTrue(stmt instanceof _ReturningDml);

        final List<Map<String, Object>> resultList;
        resultList = session.queryObjectList(stmt, RowMaps::hashMap, ArrayList::new);

        Assert.assertEquals(resultList.size(), 2);

    }

    /*-------------------below query insert syntax-------------------*/

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void queryInsertParent(final SyncLocalSession session) {
        assert HistoryChinaRegion_.id.generatorType() == GeneratorType.POST;
        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final Insert stmt;
        stmt = Postgres.singleInsert()
                .migration()
                .insertInto(HistoryChinaRegion_.T)
                .space()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                .asQuery()
                .asInsert();

        Assert.assertEquals(session.update(stmt), regionList.size());

    }

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void returningQueryInsertParent(final SyncLocalSession session) {
        assert HistoryChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList = createReginListWithCount(3);
        session.batchSave(regionList);

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .migration()
                .insertInto(HistoryChinaRegion_.T)
                .space()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
                .and(ChinaRegion_.regionType::equal, SQLs::param, RegionType.NONE)
                .asQuery()
                .returningAll()
                .asReturningInsert();

        final List<ChinaRegion<?>> rowList;
        rowList = session.queryList(stmt, ChinaRegion_.CLASS, ImmutableArrayList::arrayList);

        Assert.assertEquals(rowList.size(), regionList.size());

    }

    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void returningQueryInsertChildWithTwoStmtQueryMode(final SyncLocalSession session) {
        //TODO consider child table query insert two stmt mode reasonable ？for example firebird
        assert HistoryChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> regionList = createProvinceListWithCount(3);
        session.batchSave(regionList);
        final List<Long> reginIdList = extractRegionIdList(regionList);

        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .migration()
                .insertInto(HistoryChinaRegion_.T)
                .space()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id::in, SPACE, SQLs::rowParam, reginIdList)
                .and(ChinaRegion_.regionType::equal, SQLs::literal, RegionType.PROVINCE)
                .orderBy(ChinaRegion_.id)
                .asQuery()
                .returningAll()
                .asReturningInsert()

                .child()

                .insertInto(HistoryChinaProvince_.T)
                .space()
                .select("p", PERIOD, ChinaProvince_.T)
                .from(ChinaProvince_.T, AS, "p")
                .join(ChinaRegion_.T, AS, "c").on(ChinaProvince_.id::equal, ChinaRegion_.id)
                .where(ChinaRegion_.id.in(SQLs::rowParam, reginIdList))
                .orderBy(ChinaProvince_.id)
                .asQuery()
                .returningAll()
                .asReturningInsert();


        final List<ChinaProvince> rowList;
        rowList = session.queryList(stmt, ChinaProvince.class, ArrayList::new);
        Assert.assertEquals(rowList.size(), reginIdList.size());

    }


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void subDomainInsertChild(final SyncLocalSession session) {
        assert HistoryChinaRegion_.id.generatorType() == GeneratorType.POST;
        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();
        final Insert stmt;
        stmt = Postgres.singleInsert()
                .literalMode(LiteralMode.LITERAL)
                .with("parent").as(sw -> sw.literalMode(LiteralMode.LITERAL)
                        .insertInto(ChinaRegion_.T)
                        .values(provinceList)
                        .returning(ChinaRegion_.id)
                        .asReturningInsert()
                ).comma("parent_row_id")
                .as(sw -> sw.select(s -> s.space(Postgres.rowNumber().over().as("rowNumber"))
                                        .comma(SQLs.refField("parent", ChinaRegion_.ID))
                                )
                                .from("parent")
                                .asQuery()
                ).space()
                .insertInto(ChinaProvince_.T)
                .defaultValue(ChinaProvince_.id, Postgres.scalarSubQuery()
                        .select(s -> s.space(SQLs.refField("p", ChinaRegion_.ID)))
                        .from("parent_row_id", AS, "p")
                        .where(SQLs.refField("p", "rowNumber")::equal, BATCH_NO_LITERAL)
                        .asQuery()
                )
                .values(provinceList)
                .asInsert();

        Assert.assertFalse(stmt instanceof _ReturningDml);
        Assert.assertEquals(session.update(stmt), provinceList.size());

        for (ChinaProvince province : provinceList) {
            Assert.assertNull(province.getId());
            Assert.assertEquals(province.getRegionType(), RegionType.PROVINCE);
        }

    }


}
