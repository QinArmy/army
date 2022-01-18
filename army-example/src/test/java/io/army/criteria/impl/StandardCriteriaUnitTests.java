package io.army.criteria.impl;

import io.army.DialectMode;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.example.domain.*;
import io.army.example.struct.IdentityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StandardCriteriaUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardCriteriaUnitTests.class);

    @Test
    public void insertParent() {

        final Insert insert;
        insert = SQLs.standardInsert(ChinaRegion_.T)
                .insertInto(ChinaRegion_.T)
                .set(ChinaRegion_.regionGdp, new BigDecimal("88888.88"))
                .set(ChinaRegion_.visible, true)
                .values(this::createRegionList)
                .asInsert();

        for (DialectMode mode : DialectMode.values()) {
            LOG.debug("{} {}", mode.name(), insert.mockAsString(mode));
        }

    }

    @Test
    public void insertChild() {
        final Insert insert;
        insert = SQLs.standardInsert(ChinaProvince_.T)
                .insertInto(ChinaProvince_.T)
                .values(this::createProvinceList)
                .asInsert();

        for (DialectMode mode : DialectMode.values()) {
            LOG.debug("{} {}", mode.name(), insert.mockAsString(mode));
        }
    }

    @Test
    public void updateParent() {
        final BigDecimal addGdp = new BigDecimal("888.8");
        final Update update;
        update = SQLs.standardUpdate()
                .update(ChinaRegion_.T, "c")
                .set(ChinaRegion_.name, "武侠江湖")
                .setPlus(ChinaRegion_.regionGdp, addGdp)
                .where(ChinaRegion_.id.equal(1))
                .and(ChinaRegion_.name.equal("江湖"))
                .and(ChinaRegion_.regionGdp.plus(addGdp).greatEqual(BigDecimal.ZERO))
                .asUpdate();

        for (DialectMode mode : DialectMode.values()) {
            LOG.debug("{} {}", mode.name(), update.mockAsString(mode));
        }
    }

    @Test
    public void updateChild() {
        final BigDecimal addGdp = new BigDecimal("888.8");
        final Update update;
        update = SQLs.standardUpdate()
                .update(ChinaProvince_.T, "p")
                .set(ChinaProvince_.name, "武侠江湖")
                .setPlus(ChinaProvince_.regionGdp, addGdp)
                .set(ChinaProvince_.provincialCapital, "光明顶")
                .set(ChinaProvince_.governor, "张无忌")
                .where(ChinaProvince_.id.equal(1))
                .and(ChinaProvince_.name.equal("江湖"))
                .and(ChinaProvince_.regionGdp.plus(addGdp).greatEqual(BigDecimal.ZERO))
                .and(ChinaProvince_.governor.equal("阳顶天").or(ChinaProvince_.governor.equal("石教主")))
                .asUpdate();

        for (DialectMode mode : DialectMode.values()) {
            LOG.debug("{} {}", mode.name(), update.mockAsString(mode));
        }
    }

    /**
     * @see io.army.annotation.UpdateMode
     */
    @Test
    public void updateParentWithOnlyNullMode() {
        final Update update;
        update = SQLs.standardUpdate()
                .update(User_.T, "u")
                .set(User_.identityType, IdentityType.PERSON)
                .set(User_.identityId, 888)
                .set(User_.nickName, "令狐冲")
                .where(User_.id.equal(1))
                .and(User_.nickName.equal("zoro"))
                .asUpdate();

        for (DialectMode mode : DialectMode.values()) {
            LOG.debug("{} {}", mode.name(), update.mockAsString(mode));
        }
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
            p.setProvincialCapital("总堂");
            domainList.add(p);
        }
        return domainList;
    }

}
