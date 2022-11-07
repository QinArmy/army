package io.army.criteria.standard;

import io.army.criteria.Insert;
import io.army.criteria.PrimaryStatement;
import io.army.criteria.Select;
import io.army.criteria.Visible;
import io.army.criteria.impl.SQLs;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.*;
import io.army.example.pill.domain.PillPerson_;
import io.army.example.pill.domain.PillUser_;
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

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

public class StandardQueryUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardQueryUnitTests.class);


    @Test
    public void selectCaseFunc() {
        final Select stmt;
        stmt = SQLs.query()
                .select(SQLs::Case)
                .when(PillUser_.userType::equal, SQLs::param, 1)
                .then(PillUser_.userType)
                .end()
                .as("a")
                .asQuery();


    }


    @Test
    public void simpleSingleSelect() {
        final Select stmt;

        stmt = SQLs.query()
                .select("u", PERIOD, PillUser_.T)
                .from(PillUser_.T, AS, "u")
                .groupBy(PillUser_.userType)
                .having(PillUser_.userType.equal(SQLs::literal, UserType.PERSON))// group by is empty ,so having clause no action
                .orderBy(PillUser_.id, SQLs.DESC)
                .limit(SQLs::literal, 0, 10)
                .forUpdate()
                .asQuery();

        printStmt(stmt);
    }

    @Test
    public void simpleChildSelect() {
        final Select stmt;

        stmt = SQLs.query()
                .select("u", PERIOD, PillUser_.T, "p", PERIOD, PillPerson_.T)
                .from(PillPerson_.T, SQLs.AS, "p")
                .join(PillUser_.T, SQLs.AS, "u").on(PillPerson_.id.equal(PillUser_.id))
                .where(PillPerson_.id::equal, SQLs::literal, "1")
                .and(PillUser_.nickName::equal, SQLs::param, "脉兽秀秀")
                //.and(User_.visible.equal(false))
                .groupBy(PillPerson_.birthday)
                .having(PillUser_.userType.equal(SQLs::literal, UserType.PERSON))
                .orderBy(PillPerson_.id, SQLs.DESC)
                .limit(SQLs::literal, 0, 10)
                .forUpdate()
                .asQuery();

        printStmt(stmt);

    }

    @Test
    public void unionSelect() {
        final Select stmt;

        stmt = SQLs.query()
                .leftParen()
                .select(PillUser_.id)
                .from(PillUser_.T, SQLs.AS, "p")
                .where(PillUser_.id::equal, SQLs::literal, "1")
                .and(PillUser_.nickName::equal, SQLs::param, "脉兽秀秀")
                //.and(User_.visible.equal(false))
                .groupBy(PillUser_.userType)
                .having(PillUser_.userType.equal(SQLs::literal, UserType.PERSON))
                .orderBy(PillUser_.id, SQLs.DESC)
                .limit(SQLs::literal, 0, 10)
                .asQuery()

                .rightParen()

                .unionAll()

                .leftParen()

                .select(PillUser_.id)
                .from(PillUser_.T, SQLs.AS, "p")
                .where(PillUser_.id::equal, SQLs::literal, "2")
                .and(PillUser_.nickName::equal, SQLs::param, "远浪舰长")
                //.and(User_.visible.equal(false))
                .groupBy(PillUser_.userType)
                .having(PillUser_.userType.equal(SQLs::literal, UserType.PERSON))
                .orderBy(PillUser_.id, SQLs.DESC)
                .limit(SQLs::literal, 0, 10)
                .asQuery()

                .rightParen()

                .unionAll()

                .select(PillUser_.id)
                .from(PillUser_.T, SQLs.AS, "p")
                .where(PillUser_.id::equal, SQLs::literal, 2)
                .and(PillUser_.nickName::equal, SQLs::param, "蛮大人")
                .and(PillUser_.version.equal(SQLs::literal, 2))
                //.and(User_.version::equal, SQLs::literal, 2)
                .groupBy(PillUser_.userType)
                .having(PillUser_.userType.equal(SQLs::literal, UserType.PERSON))
                .orderBy(PillUser_.id, SQLs.DESC)
                .limit(SQLs::literal, 0, 10)


                .asQuery();

        printStmt(stmt);
    }

    @Test
    public void simpleSubQuery() {
        final Select stmt;
        stmt = SQLs.query()
                .select(PillUser_.nickName)
                .from(PillUser_.T, SQLs.AS, "u")
                .where(PillUser_.nickName.equal(SQLs::param, "蛮吉"))
                .and(SQLs::exists, () -> SQLs.subQuery()
                        .select(ChinaProvince_.id)
                        .from(ChinaProvince_.T, SQLs.AS, "p")
                        .join(ChinaRegion_.T, SQLs.AS, "r").on(ChinaProvince_.id.equal(ChinaRegion_.id))
                        .where(ChinaProvince_.governor.equal(PillUser_.nickName))
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
                        .select(SQLs.literal(1).as("one"), SQLs.group(PillUser_.T, "u"))
                        .from(PillUser_.T, SQLs.AS, "u")
                        .where(PillUser_.createTime::equal, SQLs::literal, LocalDateTime.now())
                        .limit(SQLs::literal, criteria::get, "offset", "rowCount")
                        .asQuery())
                .as("us")
                .where(SQLs.ref("us", "one")::equal, SQLs::param, "1")
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
