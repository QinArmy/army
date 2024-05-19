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

import io.army.criteria.NotExistsNonRecursiveException;
import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.standard.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import io.army.example.pill.domain.PillUser;
import io.army.example.pill.domain.PillUser_;
import io.army.example.pill.struct.PillUserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static io.army.criteria.impl.MySQLs.cases;
import static io.army.criteria.impl.MySQLs.lag;
import static io.army.criteria.standard.SQLs.*;

public class MySQLQueryUnitTests extends MySQLUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLQueryUnitTests.class);

    @Test//(invocationCount = 300,threadPoolSize = 2)
    public void singleTable() {

        final LocalDateTime now = LocalDateTime.now();
        final Select stmt;
        stmt = MySQLs.query()
                .select(cases(ChinaRegion_.regionType)
                        .when(RegionType.NONE)
                        .then(RegionType.NONE.name())
                        .when(RegionType.PROVINCE)
                        .then(RegionType.PROVINCE.name())
                        .when(RegionType.CITY)
                        .then(RegionType.CITY.name())
                        .elseValue(NULL).end()::as, ChinaRegion_.REGION_TYPE
                )
                .comma(MySQLs.rowNumber().over()::as, "rowNumber")
                .comma(MySQLs.sum(ChinaRegion_.regionGdp).over(s -> s.partitionBy(ChinaRegion_.regionType))::as, "gdpSum")
                .comma(MySQLs.sum(MySQLs.DISTINCT, ChinaRegion_.regionGdp)
                        .over(s -> s.partitionBy(ChinaRegion_.regionType)).as("distinctGdpSum")
                )
                .comma(lag(ChinaRegion_.population, SQLs.literalValue(1))
                        .over("w", s -> s.orderBy(ChinaRegion_.id)
                                .rows().between(UNBOUNDED_PRECEDING, AND, CURRENT_ROW)
                        )::as, "log2"
                )
                .from(ChinaRegion_.T, AS, "cr")
                .where(ChinaRegion_.id::greaterEqual, SQLs::literal, 10)
                .and(ChinaRegion_.createTime::between, SQLs::literal, now.minusDays(1), AND, now)
                .window("w").as(s -> s.partitionBy(ChinaRegion_.regionType).orderBy(ChinaRegion_.id::desc))
                .orderBy(SQLs.refSelection("rowNumber")::desc)
                .limit(SQLs::literal, 1)
                .asQuery();

        print80Stmt(LOG, stmt);

    }


    @Test//(invocationCount = 300,threadPoolSize = 2)
    public void unionSelect() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        final PillUser<?> criteria = new PillUser<>()  //mock criteria from method invoker
                .setId(random.nextLong(Long.MAX_VALUE))
                .setNickName(randomPerson(random));

        final Select stmt;

        stmt = MySQLs.query()
                .parens(s -> s.select(PillUser_.id)
                        .from(PillUser_.T, AS, "p")
                        .whereIf(PillUser_.id::equal, SQLs::literal, criteria::getId)
                        .ifAnd(PillUser_.nickName::equal, SQLs::literal, criteria::getNickName)
                        //.and(User_.visible.equal(false))
                        .groupBy(PillUser_.userType)
                        .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))
                        .limit(SQLs::literal, 0, 10)
                        .asQuery()
                )
                .union()
                .parens(s -> s.select(PillUser_.id)
                        .from(PillUser_.T, AS, "p")
                        .where(PillUser_.id.equal(SQLs::param, 2))
                        .ifAnd(PillUser_.nickName::equal, SQLs::literal, this::randomPerson)
                        //.and(User_.visible.equal(false))
                        .groupBy(PillUser_.userType)
                        .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))

                        .union()

                        .select(PillUser_.id)
                        .from(PillUser_.T, AS, "p")
                        .where(PillUser_.id::equal, SQLs::literal, 2)
                        .ifAnd(PillUser_.nickName::equal, SQLs::param, this::randomPerson)
                        .and(PillUser_.version.equal(SQLs::literal, 2))
                        //.and(User_.version::equal, SQLs::literal, 2)
                        .groupBy(PillUser_.userType)
                        .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))

                        .asQuery()
                )
                .unionAll()

                .select(PillUser_.id)
                .from(PillUser_.T, AS, "p")
                .where(PillUser_.id::equal, SQLs::literal, 2)
                .ifAnd(PillUser_.nickName::equal, SQLs::param, this::randomPerson)
                .and(PillUser_.version.equal(SQLs::literal, 2))
                //.and(User_.version::equal, SQLs::literal, 2)
                .groupBy(PillUser_.userType)
                .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))

                .unionDistinct()
                .parens(s -> s.select(PillUser_.id)
                        .from(PillUser_.T, AS, "p")
                        .where(PillUser_.id::equal, SQLs::literal, 2)
                        .ifAnd(PillUser_.nickName::equal, SQLs::param, this::randomPerson)
                        .and(PillUser_.version.equal(SQLs::literal, 2))
                        //.and(User_.version::equal, SQLs::literal, 2)
                        .groupBy(PillUser_.userType)
                        .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))
                        .limit(SQLs::literal, 0, 10)
                        .asQuery()
                ).intersect()// as of 8.0
                .select(PillUser_.id)
                .from(PillUser_.T, AS, "p")
                .where(PillUser_.id::equal, SQLs::literal, 2)
                .ifAnd(PillUser_.nickName::equal, SQLs::param, this::randomPerson)
                .and(PillUser_.version.equal(SQLs::literal, 2))
                //.and(User_.version::equal, SQLs::literal, 2)
                .groupBy(PillUser_.userType)
                .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))

                .except() // as of 8.0

                .select(PillUser_.id)
                .from(PillUser_.T, AS, "p")
                .where(PillUser_.id::equal, SQLs::literal, 2)
                .ifAnd(PillUser_.nickName::equal, SQLs::param, this::randomPerson)
                .and(PillUser_.version.equal(SQLs::literal, 2))
                //.and(User_.version::equal, SQLs::literal, 2)
                .groupBy(PillUser_.userType)
                .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))

                .asQuery();

        print80Stmt(LOG, stmt);
    }

    @Test
    public void withClauseMigration() {
        final Select stmt;
        stmt = MySQLs.query()
                .withRecursive("cte").parens("n").as(s -> s.select(SQLs.literalValue(1)::as, "r")
                        .union()
                        .select(cs -> cs.space(refField("cte", "n").plus(SQLs.literalValue(1))::as, "n"))
                        .from("cte")
                        .where(refField("cte", "n").less(SQLs.literalValue(10)))
                        .asQuery()
                )// with As clause end
                .space()
                .parens(s -> s.select(cs -> cs.space(refField("cte", "n")))
                        .from("cte")
                        .asQuery()
                )
                .asQuery();

        print80Stmt(LOG, stmt);
    }

    @Test(expectedExceptions = NotExistsNonRecursiveException.class)
    public void notExistsRecursivePart() {
        final Select stmt;
        stmt = MySQLs.query()
                .withRecursive("cte").parens("n")
                .as(s -> s.select(cs -> cs.space(refField("cte", "n")
                                        .plus(SQLs.literalValue(1))::as, "n")
                                ).from("cte")
                        .where(refField("cte", "n").less(SQLs.literalValue(10)))
                                .asQuery()
                )// with As clause end
                .space() // WITH clause end
                .select(s -> s.space(refField("cte", "n")))
                .from("cte")
                .asQuery();

        print80Stmt(LOG, stmt);
    }


}
