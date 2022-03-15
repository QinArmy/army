package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect.Dialect;
import io.army.example.domain.*;
import io.army.example.struct.IdentityType;
import io.army.example.struct.UserType;
import io.army.stmt.BatchStmt;
import io.army.stmt.SimpleStmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.testng.Assert.assertTrue;

public class StandardCriteriaUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardCriteriaUnitTests.class);

    @Test
    public void insertParent() {

        final Insert insert;
        insert = SQLs.valueInsert(ChinaRegion_.T)
                .insertInto(ChinaRegion_.T)
                .set(ChinaRegion_.regionGdp, new BigDecimal("88888.88"))
                .set(ChinaRegion_.visible, true)
                .values(this::createRegionList)
                .asInsert();

        for (Dialect mode : Dialect.values()) {
            LOG.debug("{} {}", mode.name(), insert.mockAsString(mode, Visible.ONLY_VISIBLE, true));
        }

    }


    @Test
    public void insertChild() {
        final Insert insert;
        insert = SQLs.valueInsert(ChinaProvince_.T)
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
        final Update update;
        update = SQLs.singleUpdate()
                .update(ChinaRegion_.T, "c")
                .set(ChinaRegion_.name, "武侠江湖")
                .setPlus(ChinaRegion_.regionGdp, addGdp)
                .where(ChinaRegion_.id.equal("1"))
                .and(ChinaRegion_.name.equal("江湖"))
                .and(ChinaRegion_.regionGdp.plus(addGdp).greatEqual(BigDecimal.ZERO))
                .asUpdate();

        for (Dialect mode : Dialect.values()) {
            LOG.debug("{} {}", mode.name(), update.mockAsString(mode, Visible.ONLY_VISIBLE, true));
        }
    }

    @Test
    public void updateChild() {
        final BigDecimal addGdp = new BigDecimal("888.8");
        final Update update;
        update = SQLs.singleUpdate()
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
        update = SQLs.batchUpdate()
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
        update = SQLs.singleUpdate()
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
        delete = SQLs.singleDelete()
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
        delete = SQLs.singleDelete()
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
        delete = SQLs.batchDelete()
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
                .lock(LockMode.WRITE)
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
                .lock(LockMode.WRITE)
                .bracket()

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
                .lock(LockMode.WRITE)
                .bracket()

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
                .lock(LockMode.WRITE)
                .bracket()
                .orderBy(SQLs.ref(User_.ID))
                .limit(0, 5)
                .bracket()
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

    /**
     * @see #simpleSubQuerySelectItem()
     */
    private SubQuery userInfo(Map<String, Object> criteria) {
        return SQLs.subQuery()
                .select(SQLs.literal(1).as("one"), SQLs.group(User_.T, "u"))
                .from(User_.T, "u")
                .where(User_.createTime.lessEqualLiteral(LocalDateTime.now()))
                //.limit((Long)criteria.get("offset"),(Long)criteria.get("rowCount")) //this style is ugly.
                .limit(criteria::get, "offset", "rowCount")
                .asQuery();
    }


    private List<ChinaRegion> createRegionList() {
        List<ChinaRegion> domainList = new ArrayList<>();
        ChinaRegion region;

        for (int i = 0; i < 2; i++) {
            region = new ChinaRegion();
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

}
