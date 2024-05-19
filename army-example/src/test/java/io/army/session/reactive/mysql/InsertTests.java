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

package io.army.session.reactive.mysql;

import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.inner._ReturningDml;
import io.army.criteria.standard.SQLs;
import io.army.example.bank.domain.user.*;
import io.army.reactive.ReactiveLocalSession;
import io.army.record.ResultStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static io.army.criteria.standard.SQLs.AS;
import static io.army.criteria.standard.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class InsertTests extends SessionTestsSupport {


    private static final Logger LOG = LoggerFactory.getLogger(InsertTests.class);


    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainInsertParent(final ReactiveLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList;
        regionList = createReginListWithCount(3);

        final long startNanoSecond = System.nanoTime();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .asInsert();

        statementCostTimeLog(session, LOG, startNanoSecond);


        final ResultStates states;

        states = session.update(stmt)
                .block();

        Assert.assertNotNull(states);

        Assert.assertEquals(states.affectedRows(), regionList.size());

        for (ChinaRegion<?> region : regionList) {
            Assert.assertNotNull(region.getId());
        }

    }


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void domainInsertChildWithTowStmtUpdateMode(final ReactiveLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .values(provinceList)
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values(provinceList)
                .asInsert();

        final ResultStates states;
        states = session.update(stmt)
                .block();

        Assert.assertNotNull(states);
        Assert.assertEquals(states.affectedRows(), provinceList.size());

    }

    /*-------------------below values syntax tests -------------------*/

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void staticValuesInsertParent(final ReactiveLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs.TRUE)
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

        session.update(stmt)
                .doOnNext(states -> Assert.assertEquals(states.affectedRows(), 2))
                .block();

    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void valuesInsertChildWithTowStmtUpdateMode(final ReactiveLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
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

        final ResultStates states;
        states = session.update(stmt)
                .block();

        Assert.assertNotNull(states);
        Assert.assertEquals(states.affectedRows(), 2);


    }

    /*-------------------below query insert syntax-------------------*/

    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void queryInsertParent(final ReactiveLocalSession session) {
        assert HistoryChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .migration()
                .insertInto(HistoryChinaRegion_.T)
                .space()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.regionType::equal, SQLs::literal, RegionType.NONE)
                .and(SQLs::notExists, MySQLs.subQuery()
                        .select(HistoryChinaRegion_.id)
                        .from(HistoryChinaRegion_.T, AS, "h")
                        .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                        ::asQuery
                )
                .limit(SQLs::literal, 2)
                .asQuery()
                .asInsert();

        session.update(stmt)
                .doOnNext(states -> Assert.assertTrue(states.affectedRows() > 0))
                .block();


    }


}
