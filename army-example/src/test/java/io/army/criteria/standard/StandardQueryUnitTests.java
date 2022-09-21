package io.army.criteria.standard;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.*;
import io.army.example.pill.domain.Person_;
import io.army.example.pill.domain.User_;
import io.army.example.pill.struct.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandardQueryUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardQueryUnitTests.class);


    @Test
    public void simpleSingleSelect() {
        final Select stmt;

        stmt = SQLs.query()
                .select(SQLs.group(User_.T, "u"))
                .from(User_.T, "u")
                .groupBy(User_.userType)
                .having(User_.userType.equal(UserType.PERSON))// group by is empty ,so having clause no action
                .orderBy(User_.id.desc())
                .limit(0, 10)
                .lock(LockMode.READ)
                .asQuery();

        printStmt(stmt);
    }

    @Test
    public void simpleChildSelect() {
        final Select stmt;

        stmt = SQLs.query()
                .select(SQLs.childGroup(Person_.T, "p", "u"))
                .from(Person_.T, "p")
                .join(User_.T, "u").on(Person_.id.equal(User_.id))
                .where(Person_.id.equal("1"))
                .and(User_.nickName.equal("脉兽秀秀"))
                //.and(User_.visible.equal(false))
                .groupBy(Person_.birthday)
                .having(User_.userType.equal(UserType.PERSON))
                .orderBy(Person_.id.desc())
                .limit(0, 10)
                .lock(LockMode.WRITE)
                .asQuery();

        printStmt(stmt);

    }

    @Test
    public void unionSelect() {
        final Select stmt;

        stmt = SQLs.query()
                .select(User_.id)
                .from(User_.T, "p")
                .where(User_.id.equal("1"))
                .and(User_.nickName.equal("脉兽秀秀"))
                //.and(User_.visible.equal(false))
                .groupBy(User_.userType)
                .having(User_.userType.equal(UserType.PERSON))
                .orderBy(User_.id.desc())
                .limit(0, 10)
                .bracket()// bracket this simple query

                .union()

                .select(User_.id)
                .from(User_.T, "p")
                .where(User_.id.equal("2"))
                .and(User_.nickName.equal("远浪舰长"))
                //.and(User_.visible.equal(false))
                .groupBy(User_.userType)
                .having(User_.userType.equal(UserType.PERSON))
                .orderBy(User_.id.desc())
                .limit(0, 10)
                .bracket() // bracket this simple query

                .unionAll()

                .select(User_.id)
                .from(User_.T, "p")
                .where(User_.id.equal("3"))
                .and(User_.nickName.equal("蛮大人"))
                //.and(User_.visible.equal(false))
                .groupBy(User_.userType)
                .having(User_.userType.equal(UserType.PERSON))
                .orderBy(User_.id.desc())
                .limit(0, 10)
                .bracket()// bracket this simple query

                .bracket() // bracket whole union query
                .orderBy(SQLs.ref(User_.ID)) // order by whole union query
                .limit(0, 5)// limit whole union query

                .asQuery();

        printStmt(stmt);
    }

    @Test
    public void simpleSubQuery() {
        final Select stmt;
        stmt = SQLs.query()
                .select(User_.nickName)
                .from(User_.T, "u")
                .where(User_.nickName.equal("蛮吉"))
                .and(SQLs.exists(() -> SQLs.subQuery()
                        .select(ChinaProvince_.id)
                        .from(ChinaProvince_.T, "p")
                        .join(ChinaRegion_.T, "r").on(ChinaProvince_.id.equal(ChinaRegion_.id))
                        .where(ChinaProvince_.governor.equal(User_.nickName))
                        .asQuery())
                )
                .asQuery();

        printStmt(stmt);
    }

    @Test
    public void simpleSubQuerySelectItem() {
        final Map<String, Object> criteria = new HashMap<>();
        criteria.put("offset", 0L);
        criteria.put("rowCount", 100L);

        final Select stmt;
        stmt = SQLs.query(criteria)
                .select(SQLs.ref("us", "one"), SQLs.derivedGroup("us"))
                .from(this::userInfo, "us")
                .where(SQLs.ref("us", "one").equalLiteral(1))
                .asQuery();

        printStmt(stmt);
    }

    @Test
    public void singleTableSubQueryInsert() {
        final Insert stmt;
        stmt = SQLs.rowSetInsert()
                .insertInto(ChinaRegion_.T)
                .leftParen(ChinaRegion_.id, ChinaRegion_.createTime)
                .comma(ChinaRegion_.updateTime, ChinaRegion_.regionType)
                .comma(ChinaRegion_.regionGdp)
                .rightParen()
                // below sub query is test case,not real.
                .space(() -> SQLs.subQuery()
                        .select(consumer -> {
                            consumer.accept(ChinaRegion_.id);
                            consumer.accept(ChinaRegion_.createTime);
                            consumer.accept(ChinaRegion_.updateTime);
                            consumer.accept(SQLs.literal(RegionType.CITY).as(ChinaRegion_.REGION_TYPE));
                            consumer.accept(ChinaRegion_.regionGdp);
                        })
                        .from(ChinaRegion_.T, "r")
                        .asQuery())
                .asInsert();

        printStmt(stmt);
    }

    @Test
    public void childTableSubQueryInsert() {
        final Insert stmt;
        stmt = SQLs.rowSetInsert()
                .insertInto(ChinaRegion_.T)

                .leftParen(ChinaRegion_.id, ChinaRegion_.createTime)
                .comma(ChinaRegion_.updateTime, ChinaRegion_.regionType)
                .comma(ChinaRegion_.regionGdp)
                .rightParen()
                // below sub query is test case,not real.
                .space(() -> SQLs.subQuery()
                        .select(consumer -> {
                            consumer.accept(ChinaRegion_.id);
                            consumer.accept(ChinaRegion_.createTime);
                            consumer.accept(ChinaRegion_.updateTime);
                            consumer.accept(SQLs.literal(RegionType.CITY).as(ChinaRegion_.REGION_TYPE));
                            consumer.accept(ChinaRegion_.regionGdp);
                        })
                        .from(ChinaRegion_.T, "r")
                        .asQuery())
                .child()
                .insertInto(ChinaCity_.T)
                .leftParen(ChinaCity_.id, ChinaCity_.mayorName)
                .rightParen()
                // below sub query is test case,not real.
                .space(() -> SQLs.subQuery()
                        .select(consumer -> {
                            consumer.accept(ChinaCity_.id);
                            consumer.accept(ChinaCity_.mayorName);
                        })
                        .from(ChinaCity_.T, "r")
                        .asQuery())
                .asInsert();

        printStmt(stmt);
    }


    /**
     * @see #simpleSubQuerySelectItem()
     */
    private SubQuery userInfo(final Map<String, Object> criteria) {
        return SQLs.subQuery()
                .select(SQLs.literal(1).as("one"), SQLs.group(User_.T, "u"))
                .from(User_.T, "u")
                .where(User_.createTime.lessEqualLiteral(LocalDateTime.now()))
                //.limit((Long)criteria.get("offset"),(Long)criteria.get("rowCount")) //this style is ugly.
                .limit(criteria::get, "offset", "rowCount")
                .asQuery();
    }


    private List<ChinaProvince> createProvinceList() {
        List<ChinaProvince> domainList = new ArrayList<>();
        ChinaProvince p;
        for (int i = 0; i < 2; i++) {
            p = new ChinaProvince();
            p.setId((long) i);
            p.setName("江湖" + i);
            p.setGovernor("盟主");
            p.setRegionGdp(new BigDecimal("8888.88"));
            p.setProvincialCapital("总堂");
            domainList.add(p);
        }
        return domainList;
    }


    private static void printStmt(final PrimaryStatement statement) {
        for (Database database : Database.values()) {
            for (Dialect dialect : database.dialects()) {
                LOG.debug("{}:\n{}", dialect.name(), statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
            }
        }

    }


}