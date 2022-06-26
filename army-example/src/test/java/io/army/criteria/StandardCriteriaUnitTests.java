package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.*;
import io.army.example.pill.domain.Person_;
import io.army.example.pill.domain.User_;
import io.army.example.pill.struct.IdentityType;
import io.army.example.pill.struct.UserType;
import io.army.stmt.BatchStmt;
import io.army.stmt.GeneratedKeyStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class StandardCriteriaUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardCriteriaUnitTests.class);

    @Test
    public void insertParent() {
        System.out.println(ChinaRegion_.visible.mappingType().getClass().getName());
        final Insert insert;
        insert = SQLs.valueInsert(ChinaRegion_.T)
                .preferLiteral(true)
                .insertInto(ChinaRegion_.T)
                .commonLiteral(ChinaRegion_.regionGdp, new BigDecimal("88888.88"))
                .commonLiteral(ChinaRegion_.visible, true)
                .values(this::createRegionList)
                .asInsert();

        for (Dialect dialect : Dialect.values()) {
            Stmt stmt;
            stmt = insert.mockAsStmt(dialect, Visible.ONLY_VISIBLE);
            assertTrue(stmt instanceof GeneratedKeyStmt);
            GeneratedKeyStmt keyStmt = (GeneratedKeyStmt) stmt;

            LOG.debug("{}:\n {}", dialect.name(), stmt);
            switch (dialect.database) {
                case MySQL:
                    assertNull(keyStmt.idField());
                    assertNull(keyStmt.idReturnAlias());
                    break;
                case PostgreSQL:
                case Oracle:
                case H2:
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(dialect.database);
            }

        }

    }


    @Test
    public void insertChild() {
        final Insert insert;
        insert = SQLs.valueInsert(ChinaProvince_.T)
                .preferLiteral(true)
                .insertInto(ChinaProvince_.T)
                .values(this::createProvinceList)
                .asInsert();

        for (Dialect mode : Dialect.values()) {
            LOG.debug("{} {}", mode.name(), insert.mockAsString(mode, Visible.ONLY_VISIBLE, true));
        }
    }

    @Test
    public void updateParent() {
        final BigDecimal addGdp = new BigDecimal("888.8");
        final Map<String, Object> map = new HashMap<>();
        map.put("firstId", (byte) 1);
        map.put("secondId", "3");

        final Update update;
        update = SQLs.domainUpdate()
                .update(ChinaRegion_.T, "c")
                .set(ChinaRegion_.name, "武侠江湖")
                .setPlus(ChinaRegion_.regionGdp, addGdp)
                .where(ChinaRegion_.id::between, map::get, "firstId", "secondId")
                .and(ChinaRegion_.name.equal("江湖"))
                .and(ChinaRegion_.regionGdp.plus(addGdp).greatEqualLiteral(BigDecimal.ZERO))
                .asUpdate();

        for (Dialect mode : Dialect.values()) {
            LOG.debug("{} {}", mode.name(), update.mockAsString(mode, Visible.ONLY_VISIBLE, true));
        }
    }

    @Test
    public void updateChild() {
        final BigDecimal addGdp = new BigDecimal("888.8");
        final Update update;
        update = SQLs.domainUpdate()
                .update(ChinaProvince_.T, "p")
                .set(ChinaProvince_.name, "武侠江湖")
                .setPlusLiteral(ChinaProvince_.regionGdp, addGdp)
                .set(ChinaProvince_.provincialCapital, "光明顶")
                .set(ChinaProvince_.governor, "张无忌")
                .where(ChinaProvince_.id.equalLiteral(1))
                .and(ChinaProvince_.name.equal("江湖"))
                .and(ChinaProvince_.regionGdp.plus(addGdp).greatEqual(BigDecimal.ZERO))
                .and(ChinaProvince_.governor.equal("阳顶天").or(list -> {
                    list.add(ChinaProvince_.governor.equal("石教主"));
                    list.add(ChinaProvince_.governor.equal("钟教主").and(ChinaProvince_.governor.equal("老钟")));
                    list.add(ChinaProvince_.governor.equal("方腊"));
                }))
                .asUpdate();

        for (Dialect mode : Dialect.values()) {
            LOG.debug("{} {}", mode.name(), update.mockAsString(mode, Visible.ONLY_VISIBLE, true));
        }
    }

    @Test
    public void batchUpdateParent() {
        final Update update;
        update = SQLs.batchDomainUpdate()
                .update(ChinaProvince_.T, "p")
                .setPlus(ChinaProvince_.regionGdp)
                .set(ChinaProvince_.governor)
                .where(ChinaProvince_.id.equalNamed())
                .and(ChinaProvince_.regionGdp.plusNamed().greatEqual(BigDecimal.ZERO))
                .and(ChinaProvince_.version.equal(0))
                .paramList(this::createProvinceList)
                .asUpdate();

        for (Dialect mode : Dialect.values()) {
            BatchStmt stmt;
            stmt = (BatchStmt) update.mockAsStmt(mode, Visible.ONLY_VISIBLE);
            assertTrue(stmt.hasOptimistic(), "optimistic lock");
            LOG.debug("batchUpdateParent\n{}\n{}", mode.name(), stmt.sql());
        }

    }


    /**
     * @see io.army.annotation.UpdateMode
     */
    @Test
    public void updateParentWithOnlyNullMode() {
        final Update update;
        update = SQLs.domainUpdate()
                .update(User_.T, "u")
                .set(User_.identityType, IdentityType.PERSON)
                .set(User_.identityId, 888)
                .set(User_.nickName, "令狐冲")
                .where(User_.id.equal(1))
                .and(User_.nickName.equal("zoro"))
                .asUpdate();

        for (Dialect mode : Dialect.values()) {
            LOG.debug("{} {}", mode.name(), update.mockAsString(mode, Visible.ONLY_VISIBLE, true));
        }
    }

    @Test
    public void deleteParent() {
        final Delete delete;
        delete = SQLs.domainDelete()
                .deleteFrom(ChinaRegion_.T, "r")
                .where(ChinaRegion_.id.equal(1))
                .and(ChinaRegion_.name.equal("马鱼腮角"))
                .and(ChinaProvince_.version.equal(2))
                .asDelete();

        for (Dialect mode : Dialect.values()) {
            SimpleStmt stmt;
            stmt = (SimpleStmt) delete.mockAsStmt(mode, Visible.ONLY_VISIBLE);
            assertTrue(stmt.hasOptimistic(), "optimistic lock");
            LOG.debug("deleteParent\n{}\n{}", mode.name(), stmt.sql());
        }

    }

    @Test
    public void deleteChild() {
        final Delete delete;
        delete = SQLs.domainDelete()
                .deleteFrom(ChinaProvince_.T, "p")
                .where(ChinaProvince_.id.equal(1))
                .and(ChinaProvince_.name.equal("江南省"))
                .and(ChinaProvince_.governor.equal("无名"))
                .and(ChinaProvince_.version.equal(2))
                .asDelete();

        for (Dialect mode : Dialect.values()) {
            SimpleStmt stmt;
            stmt = (SimpleStmt) delete.mockAsStmt(mode, Visible.ONLY_VISIBLE);
            assertTrue(stmt.hasOptimistic(), "optimistic lock");
            LOG.debug("deleteChild\n{}\n{}", mode.name(), stmt.sql());
        }
    }

    @Test
    public void batchDeleteChild() {
        final Delete delete;
        delete = SQLs.batchDomainDelete()
                .deleteFrom(ChinaProvince_.T, "p")
                .where(ChinaProvince_.id.equalNamed())
                .and(ChinaProvince_.name.equalNamed())
                .and(ChinaProvince_.governor.equalNamed())
                .and(ChinaProvince_.regionGdp.plusNamed().lessThan("6666.66"))
                .and(ChinaProvince_.version.equal(2))
                .paramList(this.createProvinceList())
                .asDelete();

        for (Dialect mode : Dialect.values()) {
            BatchStmt stmt;
            stmt = (BatchStmt) delete.mockAsStmt(mode, Visible.ONLY_VISIBLE);
            assertTrue(stmt.hasOptimistic(), "optimistic lock");
            LOG.debug("batchDeleteChild\n{}\n{}", mode.name(), stmt.sql());
        }
    }

    @Test
    public void simpleSingleSelect() {
        final Select select;

        select = SQLs.query()
                .select(SQLs.group(User_.T, "u"))
                .from(User_.T, "u")
                .ifGroupBy(Collections::emptyList)
                .having(User_.userType.equal(UserType.PERSON))// group by is empty ,so having clause no action
                .orderBy(User_.id.desc())
                .limit(0, 10)
                .lock(LockMode.READ)
                .asQuery();

        for (Dialect dialect : Dialect.values()) {
            LOG.debug("simpleSingleSelect:\n{}", select.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
        }
    }

    @Test
    public void simpleChildSelect() {
        final Select select;

        select = SQLs.query()
                .select(SQLs.childGroup(Person_.T, "p", "u"))
                .from(Person_.T, "p")
                .join(User_.T, "u").on(Person_.id.equal(User_.id))
                .where(Person_.id.equal("1"))
                .and(User_.nickName.equal("脉兽秀秀"))
                //.and(User_.visible.equal(false))
                .ifGroupBy(Collections::emptyList)
                .having(User_.userType.equal(UserType.PERSON))
                .orderBy(Person_.id.desc())
                .limit(0, 10)
                .lock(LockMode.WRITE)
                .asQuery();

        for (Dialect dialect : Dialect.values()) {
            LOG.debug("simpleChildSelect:\n{}", select.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
        }

    }

    @Test
    public void unionSelect() {
        final Select select;

        select = SQLs.query()
                .select(User_.id)
                .from(User_.T, "p")
                .where(User_.id.equal("1"))
                .and(User_.nickName.equal("脉兽秀秀"))
                //.and(User_.visible.equal(false))
                .ifGroupBy(Collections::emptyList)
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
                .ifGroupBy(Collections::emptyList)
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
                .ifGroupBy(Collections::emptyList)
                .having(User_.userType.equal(UserType.PERSON))
                .orderBy(User_.id.desc())
                .limit(0, 10)
                .bracket()// bracket this simple query

                .bracket() // bracket whole union query
                .orderBy(SQLs.ref(User_.ID)) // order by whole union query
                .limit(0, 5)// limit whole union query

                .asQuery();

        for (Dialect dialect : Dialect.values()) {
            LOG.debug("union select:\n{}", select.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
        }
    }

    @Test
    public void simpleSubQuery() {
        final Select select;
        select = SQLs.query()
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

        for (Dialect dialect : Dialect.values()) {
            LOG.debug("union select:\n{}", select.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
        }
    }

    @Test
    public void simpleSubQuerySelectItem() {
        final Map<String, Object> criteria = new HashMap<>();
        criteria.put("offset", 0L);
        criteria.put("rowCount", 100L);

        final Select select;
        select = SQLs.query(criteria)
                .select(SQLs.ref("us", "one"), SQLs.derivedGroup("us"))
                .from(this::userInfo, "us")
                .where(SQLs.ref("us", "one").equalLiteral(1))
                .asQuery();

        for (Dialect dialect : Dialect.values()) {
            LOG.debug("union select:\n{}", select.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
        }
    }

    @Test
    public void singleTableSubQueryInsert() {
        final Insert stmt;
        stmt = SQLs.subQueryInsert()
                .insertInto(ChinaRegion_.T)
                .leftParen(ChinaRegion_.id)
                .comma(ChinaRegion_.createTime)
                .comma(ChinaRegion_.updateTime)
                .comma(ChinaRegion_.regionType)
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
        stmt = SQLs.subQueryInsert()
                .insertInto(ChinaCity_.T)

                .leftParen(ChinaRegion_.id)
                .comma(ChinaRegion_.createTime)
                .comma(ChinaRegion_.updateTime)
                .comma(ChinaRegion_.regionType)
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

                .leftParen(ChinaCity_.id)
                .comma(ChinaCity_.mayorName)
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


    private List<ChinaRegion<?>> createRegionList() {
        List<ChinaRegion<?>> domainList = new ArrayList<>();
        ChinaRegion<?> region;

        for (int i = 0; i < 3; i++) {
            region = new ChinaRegion<>();
            region.setId((long) i);
            region.setName("江湖" + i);
            domainList.add(region);
        }
        return domainList;
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


    private static void printStmt(final Statement statement) {
        for (Dialect dialect : Dialect.values()) {
            LOG.debug("{}:\n{}", dialect.name(), statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
        }

    }

}
