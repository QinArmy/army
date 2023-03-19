package io.army.criteria.standard.unit;

import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.account.BankAccount_;
import io.army.example.bank.domain.user.*;
import io.army.example.pill.domain.PillPerson_;
import io.army.example.pill.domain.PillUser_;
import io.army.example.pill.struct.PillUserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static io.army.criteria.impl.SQLs.*;

public class StandardQueryUnitTests extends StandardUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardQueryUnitTests.class);


    @Test//(invocationCount = 10)
    public void caseFunction() {
        Select stmt;
        stmt = SQLs.query()
                .select(SQLs.cases()
                        .when(SQLs.literalFrom(1).is(TRUE))
                        .then(SQLs::literalFrom, PillUserType.PARTNER)
                        .elseValue(SQLs.literalFrom(PillUserType.NONE))
                        .end()
                        .plus(SQLs.literalFrom(1)).times(SQLs::literal, 5).as("a")
                )
                .asQuery();
        printStmt(LOG, stmt);
    }

    @Test
    public void scalarSubQuery() {
        Select stmt;
        stmt = SQLs.query()
                .select(SQLs.scalarSubQuery()
                        .select(PillUser_.nickName)
                        .from(PillUser_.T, AS, "u")
                        .where(PillUser_.id::equal, SQLs::param, () -> 1)
                        .asQuery().as("r")
                )
                .asQuery();

        printStmt(LOG, stmt);
    }


    @Test
    public void singleDomain() {
        final Select stmt;

        stmt = SQLs.query()
                .select("u", PERIOD, PillUser_.T)
                .from(PillUser_.T, AS, "u")
                .orderBy(PillUser_.id::desc)
                .limit(SQLs::literal, 0, 10)
                .forUpdate()
                .asQuery();

        printStmt(LOG, stmt);
    }

    @Test
    public void complexDomain() {
        final Select stmt;
        stmt = SQLs.query()
                .select("u", PERIOD, PillUser_.T, "p", PERIOD, PillPerson_.T)
                .from(PillPerson_.T, SQLs.AS, "p")
                .join(PillUser_.T, SQLs.AS, "u").on(PillUser_.id::equal, PillPerson_.id)
                .where(PillPerson_.id.equal(SQLs::literal, 1))
                .and(PillUser_.nickName::equal, SQLs::param, "脉兽秀秀")
                .and(PillUser_.createTime.notBetween(SQLs::literal, LocalDateTime.now().minusDays(1), AND, LocalDateTime.now()))
                .and(PillUser_.id::in, () -> SQLs.subQuery()
                        .select(RegisterRecord_.userId)
                        .from(RegisterRecord_.T, AS, "r")
                        .where(RegisterRecord_.createTime::between, SQLs::literal, () -> LocalDateTime.now().minusDays(1), AND, LocalDateTime::now)
                        .asQuery()
                )
                //.and(User_.visible.equal(false))
                .orderBy(PillPerson_.id::desc)
                .limit(SQLs::literal, 0, 10)
                .forUpdate()
                .asQuery();

        printStmt(LOG, stmt);

    }

    @Test
    public void unionSelect() {
        final Select stmt;

        stmt = SQLs.query()

                .parens(s -> s.select(PillUser_.id)
                        .from(PillUser_.T, SQLs.AS, "p")
                        .where(PillUser_.id.equal(SQLs::literal, 1))
                        .and(PillUser_.nickName::equal, SQLs::param, () -> "脉兽秀秀")
                        //.and(User_.visible.equal(false))
                        .groupBy(PillUser_.userType)
                        .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))
                        .orderBy(PillUser_.id::desc)
                        .limit(SQLs::literal, 0, 10)
                        .asQuery()
                )
                .union()
                .parens(s -> s.select(PillUser_.id)
                        .from(PillUser_.T, SQLs.AS, "p")
                        .where(PillUser_.id.equal(SQLs::param, "2"))
                        .and(PillUser_.nickName::equal, SQLs::param, () -> "远浪舰长")
                        //.and(User_.visible.equal(false))
                        .groupBy(PillUser_.userType)
                        .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))
                        .orderBy(PillUser_.id::desc)
                        .limit(SQLs::literal, 0, 10)
                        .asQuery()
                )
                .unionAll()
                .parens(s -> s.select(PillUser_.id)
                        .from(PillUser_.T, SQLs.AS, "p")
                        .where(PillUser_.id::equal, SQLs::literal, () -> 2)
                        .and(PillUser_.nickName::equal, SQLs::param, () -> "蛮大人")
                        .and(PillUser_.version.equal(SQLs::literal, 2))
                        //.and(User_.version::equal, SQLs::literal, 2)
                        .groupBy(PillUser_.userType)
                        .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))
                        .orderBy(PillUser_.id::desc)
                        .limit(SQLs::literal, 0, 10)
                        .asQuery()
                )
                .unionDistinct()

                .select(PillUser_.id)
                .from(PillUser_.T, SQLs.AS, "p")
                .where(PillUser_.id::equal, SQLs::literal, () -> 2)
                .and(PillUser_.nickName::equal, SQLs::param, () -> "蛮大人")
                .and(PillUser_.version.equal(SQLs::literal, 2))
                //.and(User_.version::equal, SQLs::literal, 2)
                .groupBy(PillUser_.userType)
                .having(PillUser_.userType.equal(SQLs::literal, PillUserType.PERSON))
                .orderBy(PillUser_.id::desc)
                .limit(SQLs::literal, 0, 10)

                .asQuery();
        printStmt(LOG, stmt);
    }

    @Test
    public void simpleSubQuery() {

        final Map<String, Object> map = new HashMap<>();
        map.put("nickName", "蛮吉");

        final Select stmt;
        stmt = SQLs.query()
                .select(PillUser_.nickName)
                .from(PillUser_.T, AS, "u")
                .where(PillUser_.nickName::equal, SQLs::param, map::get, "nickName")
                .and(SQLs::exists, () -> SQLs.subQuery()
                        .select(ChinaProvince_.id)
                        .from(ChinaProvince_.T, AS, "p")
                        .join(ChinaRegion_.T, AS, "r").on(ChinaProvince_.id::equal, ChinaRegion_.id)
                        .where(ChinaProvince_.governor::equal, PillUser_.nickName)
                        .asQuery()
                )
                .asQuery();

        printStmt(LOG, stmt);
    }

    @Test
    public void simpleSubQuerySelectItem() {
        final Map<String, Object> criteria = new HashMap<>();
        criteria.put("offset", 0L);
        criteria.put("rowCount", 100L);

        final Select stmt;
        stmt = SQLs.query()
                .select(SQLs.refThis("us", "one"))
                .comma("us", PERIOD, START)
                .from(() -> SQLs.subQuery()
                        .select(SQLs.literalFrom(1)::as, "one")
                        .comma("u", PERIOD, PillUser_.T)
                        .from(PillUser_.T, AS, "u")
                        .where(PillUser_.createTime::equal, SQLs::literal, LocalDateTime::now)
                        .limit(SQLs::literal, criteria::get, "offset", "rowCount")
                        .asQuery()
                )
                .as("us")
                .where(SQLs.refThis("us", "one")::equal, SQLs::param, () -> "1")
                .asQuery();

        printStmt(LOG, stmt);
    }

    @Test
    public void nestedJoin() {
        final Select stmt;
        stmt = SQLs.query()
                .select(BankPerson_.id::as, "userId", SQLs.refThis("cr", "id")::as, "regionId")
                .comma(SQLs.refThis("cr", "name")::as, "regionName")
                .from(s -> s.leftParen(BankPerson_.T, AS, "up")
                        .join(BankUser_.T, AS, "u").on(BankPerson_.id::equal, BankUser_.id)
                        .rightParen()
                ).join(BankAccount_.T, AS, "a").on(BankPerson_.id::equal, BankAccount_.userId)
                .crossJoin(() -> SQLs.subQuery()
                        .select(ChinaRegion_.id, ChinaRegion_.name)
                        .from(ChinaRegion_.T, AS, "c")
                        .where(ChinaRegion_.name::equal, SQLs::literal, "荒''''\n\032'海")
                        .asQuery()
                ).as("cr")
                .asQuery();

        printStmt(LOG, stmt);

    }

    @Test
    public void dynamicJoin() {
        final Select stmt;
        stmt = SQLs.query()
                .select(BankPerson_.id::as, "userId", SQLs.refThis("cr", "id")::as, "regionId")
                .comma(SQLs.refThis("cr", "name")::as, "regionName")
                .from(s -> s.leftParen(BankPerson_.T, AS, "up")
                        .join(BankUser_.T, AS, "u").on(BankPerson_.id::equal, BankUser_.id)
                        .rightParen()
                )
                .join(BankAccount_.T, AS, "a").on(BankPerson_.id::equal, BankAccount_.userId)
                .ifCrossJoin(
                        s -> s.tabular(() -> SQLs.subQuery()
                                        .select(ChinaRegion_.id, ChinaRegion_.name)
                                        .from(ChinaRegion_.T, AS, "c")
                                        .where(ChinaRegion_.name::equal, SQLs::literal, () -> "荒''''\n\032'海")
                                        .asQuery()

                                )
                                .as("cr")
                )
                .asQuery();

        printStmt(LOG, stmt);

    }

    @Test//(invocationCount = 10)
    public void bracketQuery() {
        final Select stmt;
        stmt = SQLs.query()
                .parens(s -> s.select(ChinaRegion_.id, ChinaRegion_.name)
                        .from(ChinaRegion_.T, AS, "r")
                        .where(ChinaRegion_.name.equal(SQLs::literal, "万诗之海"))
                        .asQuery()
                )
                .asQuery();

        printStmt(LOG, stmt);

    }


}
