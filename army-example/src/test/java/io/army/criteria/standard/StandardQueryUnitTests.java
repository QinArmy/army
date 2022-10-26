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
                .from(User_.T, SQLs.AS, "u")
                .groupBy(User_.userType)
                .having(User_.userType.equal(SQLs::literal, UserType.PERSON))// group by is empty ,so having clause no action
                .orderBy(User_.id.desc())
                .limit(SQLs::literal, 0, 10)
                .lock(LockMode0.READ)
                .asQuery();

        printStmt(stmt);
    }

    @Test
    public void simpleChildSelect() {
        final Select stmt;

        stmt = SQLs.query()
                .select(SQLs.childGroup(Person_.T, "p", "u"))
                .from(Person_.T, SQLs.AS, "p")
                .join(User_.T, SQLs.AS, "u").on(Person_.id.equal(User_.id))
                .where(Person_.id::equal, SQLs::literal, "1")
                .and(User_.nickName::equal, SQLs::param, "脉兽秀秀")
                //.and(User_.visible.equal(false))
                .groupBy(Person_.birthday)
                .having(User_.userType.equal(SQLs::literal, UserType.PERSON))
                .orderBy(Person_.id.desc())
                .limit(SQLs::literal, 0, 10)
                .lock(LockMode0.WRITE)
                .asQuery();

        printStmt(stmt);

    }

    @Test
    public void unionSelect() {
        final Select stmt;

        stmt = SQLs.parenQuery()
                .leftParen()
                .select(User_.id)
                .from(User_.T, SQLs.AS, "p")
                .where(User_.id::equal, SQLs::literal, 1)
                .and(User_.nickName::equal, SQLs::param, "脉兽秀秀")
                //.and(User_.visible.equal(false))
                .groupBy(User_.userType)
                .having(User_.userType.equal(SQLs::literal, UserType.PERSON))
                .orderBy(User_.id.desc())
                .limit(SQLs::literal, 0, 10)
                .asQuery()

                .rightParen()

                .unionAll()

                .leftParen()

                .select(User_.id)
                .from(User_.T, SQLs.AS, "p")
                .where(User_.id::equal, SQLs::literal, 2)
                .and(User_.nickName::equal, SQLs::param, "远浪舰长")
                //.and(User_.visible.equal(false))
                .groupBy(User_.userType)
                .having(User_.userType.equal(SQLs::literal, UserType.PERSON))
                .orderBy(User_.id.desc())
                .limit(SQLs::literal, 0, 10)
                .asQuery()

                .rightParen()

                .unionAll()

                .select(User_.id)
                .from(User_.T, SQLs.AS, "p")
                .where(User_.id::equal, SQLs::literal, 2)
                .and(User_.nickName::equal, SQLs::param, "蛮大人")
                //.and(User_.visible.equal(false))
                .groupBy(User_.userType)
                .having(User_.userType.equal(SQLs::literal, UserType.PERSON))
                .orderBy(User_.id.desc())
                .limit(SQLs::literal, 0, 10)


                .asQuery();

        printStmt(stmt);
    }

    @Test
    public void simpleSubQuery() {
        final Select stmt;
        stmt = SQLs.query()
                .select(User_.nickName)
                .from(User_.T, SQLs.AS, "u")
                .where(User_.nickName.equal(SQLs::param, "蛮吉"))
                .and(SQLs::exists, () -> SQLs.subQuery()
                        .select(ChinaProvince_.id)
                        .from(ChinaProvince_.T, SQLs.AS, "p")
                        .join(ChinaRegion_.T, SQLs.AS, "r").on(ChinaProvince_.id.equal(ChinaRegion_.id))
                        .where(ChinaProvince_.governor.equal(User_.nickName))
                        .asQuery()
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
        stmt = SQLs.query()
                .select(SQLs.ref("us", "one"), SQLs.derivedGroup("us"))
                .from(() -> SQLs.subQuery()
                        .select(SQLs.literal(1).as("one"), SQLs.group(User_.T, "u"))
                        .from(User_.T, SQLs.AS, "u")
                        .where(User_.createTime::equal, SQLs::literal, LocalDateTime.now())
                        .limit(SQLs::literal, criteria::get, "offset", "rowCount")
                        .asQuery())
                .as("us")
                .where(SQLs.ref("us", "one")::equal, SQLs::param, 1)
                .asQuery();

        printStmt(stmt);
    }

    @Test
    public void singleTableSubQueryInsert() {
        final Insert stmt;
        stmt = SQLs.singleInsert()
                .insertInto(ChinaRegion_.T)
                .leftParen(ChinaRegion_.id, ChinaRegion_.createTime)
                .comma(ChinaRegion_.updateTime, ChinaRegion_.regionType)
                .comma(ChinaRegion_.regionGdp)
                .rightParen()
                // below sub query is test case,not real.
                .space()
                .select(consumer -> {
                    consumer.accept(ChinaRegion_.id);
                    consumer.accept(ChinaRegion_.createTime);
                    consumer.accept(ChinaRegion_.updateTime);
                    consumer.accept(SQLs.literal(RegionType.CITY).as(ChinaRegion_.REGION_TYPE));
                    consumer.accept(ChinaRegion_.regionGdp);
                })
                .from(ChinaRegion_.T, SQLs.AS, "r")
                .asQuery()
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.id, ChinaProvince_.governor)
                .rightParen()
                .space()
                .select(consumer -> {
                    consumer.accept(ChinaProvince_.id);
                    consumer.accept(ChinaProvince_.governor);
                })
                .from(ChinaProvince_.T, SQLs.AS, "c")
                .asQuery()
                .asInsert();

        printStmt(stmt);
    }

    @Test
    public void childTableSubQueryInsert() {
        final Insert stmt;
        stmt = SQLs.singleInsert()
                .insertInto(ChinaRegion_.T)

                .leftParen(ChinaRegion_.id, ChinaRegion_.createTime)
                .comma(ChinaRegion_.updateTime, ChinaRegion_.regionType)
                .comma(ChinaRegion_.regionGdp)
                .rightParen()
                // below sub query is test case,not real.
                .space()
                .select(consumer -> {
                    consumer.accept(ChinaRegion_.id);
                    consumer.accept(ChinaRegion_.createTime);
                    consumer.accept(ChinaRegion_.updateTime);
                    consumer.accept(SQLs.literal(RegionType.CITY).as(ChinaRegion_.REGION_TYPE));
                    consumer.accept(ChinaRegion_.regionGdp);
                })
                .from(ChinaRegion_.T, SQLs.AS, "r")
                .asQuery()
                .asInsert()
                .child()

                .insertInto(ChinaCity_.T)
                .leftParen(ChinaCity_.id, ChinaCity_.mayorName)
                .rightParen()
                // below sub query is test case,not real.
                .space()
                .select(consumer -> {
                    consumer.accept(ChinaCity_.id);
                    consumer.accept(ChinaCity_.mayorName);
                })
                .from(ChinaCity_.T, SQLs.AS, "r")
                .asQuery()
                .asInsert();

        printStmt(stmt);
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
