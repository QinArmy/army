package io.army.criteria.mysql.unit;

import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
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

import static io.army.criteria.impl.SQLs.*;

public class MySQLQueryUnitTests extends MySQLUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLQueryUnitTests.class);

    @Test//(invocationCount = 300,threadPoolSize = 2)
    public void singleTable() {

        final LocalDateTime now = LocalDateTime.now();
        final Select stmt;
        stmt = MySQLs.query()
                .select(MySQLs::cases, ChinaRegion_.regionType)
                .when(SQLs::literalFrom, RegionType.NONE)
                .then(SQLs::literalFrom, RegionType.NONE.name())
                .when(SQLs::literalFrom, RegionType.PROVINCE)
                .then(SQLs::literalFrom, RegionType.PROVINCE.name())
                .when(SQLs::literalFrom, RegionType.CITY)
                .then(SQLs::literalFrom, RegionType.CITY.name())
                .elseValue(NULL).end().as(ChinaRegion_.REGION_TYPE)
                .comma(MySQLs::rowNumber).over().as("rowNumber")
                .comma(MySQLs::sum, ChinaRegion_.regionGdp).over(s -> s.partitionBy(ChinaRegion_.regionType)).as("gdpSum")
                .comma(MySQLs::sum, DISTINCT, ChinaRegion_.regionGdp).over(s -> s.partitionBy(ChinaRegion_.regionType)).as("distinctGdpSum")
                .comma(MySQLs::lag, ChinaRegion_.population, SQLs.literalFrom(1))
                .over("w", s -> s.orderBy(ChinaRegion_.id).rows().between().unboundedPreceding().and().currentRow()).as("lag2")
                .from(ChinaRegion_.T, AS, "cr")
                .where(ChinaRegion_.id::greatEqual, SQLs::literal, 10)
                .and(ChinaRegion_.createTime::between, SQLs::literal, now.minusDays(1), AND, now)
                .window("w").as(s -> s.partitionBy(ChinaRegion_.regionType).orderBy(ChinaRegion_.id, DESC))
                .orderBy(SQLs.ref("rowNumber"), DESC)
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

                .leftParen()
                .select(PillUser_.id)
                .from(PillUser_.T, SQLs.AS, "p")
                .where(PillUser_.id::equal, SQLs::literal, criteria::getId)
                .and(PillUser_.nickName::equal, SQLs::literal, criteria::getNickName)
                //.and(User_.visible.equal(false))
                .groupBy(PillUser_.userType)
                .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))
                .limit(SQLs::literal, 0, 10)
                .asQuery()
                .rightParen()

                .union()

                .leftParen()
                .select(PillUser_.id)
                .from(PillUser_.T, SQLs.AS, "p")
                .where(PillUser_.id.equal(SQLs::param, 2))
                .and(PillUser_.nickName::equal, SQLs::literal, this::randomPerson)
                //.and(User_.visible.equal(false))
                .groupBy(PillUser_.userType)
                .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))
                .limit(SQLs::literal, 0, 10)
                .asQuery()
                .rightParen()


                .unionAll()

                .parens(() -> MySQLs.query()
                        .select(PillUser_.id)
                        .from(PillUser_.T, SQLs.AS, "p")
                        .where(PillUser_.id::equal, SQLs::literal, () -> 2)
                        .and(PillUser_.nickName::equal, SQLs::param, this::randomPerson)
                        .and(PillUser_.version.equal(SQLs::literal, 2))
                        //.and(User_.version::equal, SQLs::literal, 2)
                        .groupBy(PillUser_.userType)
                        .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))
                        .limit(SQLs::literal, 0, 10)
                        .asQuery()
                ).rightParen()

                .unionDistinct()

                .leftParen()
                .select(PillUser_.id)
                .from(PillUser_.T, SQLs.AS, "p")
                .where(PillUser_.id::equal, SQLs::literal, () -> 2)
                .and(PillUser_.nickName::equal, SQLs::param, this::randomPerson)
                .and(PillUser_.version.equal(SQLs::literal, 2))
                //.and(User_.version::equal, SQLs::literal, 2)
                .groupBy(PillUser_.userType)
                .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))
                .limit(SQLs::literal, 0, 10)
                .asQuery()
                .rightParen()

                .asQuery();

        printStmt(LOG, stmt);
    }

    @Test
    public void withClauseMigration() {
        final Select stmt;
        stmt = MySQLs.query()
                .withRecursive("cte").as(s -> s.select(SQLs::literalFrom, 1, AS, "n")
                        .union()
                        .select(SQLs.refThis("cte", "n").plus(SQLs::literal, 1), AS, "n")
                        .from("cte")
                        .asQuery()
                        .asCte()
                )
                .space()
                .leftParen()
                .select(SQLs.refThis("cte", "n"))
                .from("cte")
                .asQuery()
                .rightParen()
                .asQuery();

        print80Stmt(LOG, stmt);
    }


}
