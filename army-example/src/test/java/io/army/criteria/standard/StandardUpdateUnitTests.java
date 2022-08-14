package io.army.criteria.standard;

import io.army.criteria.PrimaryStatement;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.criteria.impl.SQLs;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.pill.domain.User_;
import io.army.example.pill.struct.IdentityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandardUpdateUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardUpdateUnitTests.class);


    @Test
    public void updateParent() {
        final BigDecimal addGdp = new BigDecimal("888.8");
        final Map<String, Object> map = new HashMap<>();
        map.put("firstId", (byte) 1);
        map.put("secondId", "3");

        final Update stmt;
        stmt = SQLs.singleUpdate()
                .update(ChinaRegion_.T, "c")
                .set(ChinaRegion_.name, "武侠江湖")
                .setPlusLiteral(ChinaRegion_.regionGdp, addGdp)
                .where(ChinaRegion_.id::between, map::get, "firstId", "secondId")
                .and(ChinaRegion_.name.equal("江湖"))
                .and(ChinaRegion_.regionGdp.plus(addGdp).greatEqualLiteral(BigDecimal.ZERO))
                .asUpdate();

        printStmt(stmt);
    }

    @Test
    public void updateChild() {
        final BigDecimal addGdp = new BigDecimal("888.8");
        final Update stmt;
        stmt = SQLs.domainUpdate()
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

        printStmt(stmt);

    }

    @Test
    public void batchUpdateParent() {
        final Update stmt;
        stmt = SQLs.batchDomainUpdate()
                .update(ChinaProvince_.T, "p")
                .setPlus(ChinaProvince_.regionGdp)
                .set(ChinaProvince_.governor)
                .where(ChinaProvince_.id.equalNamed())
                .and(ChinaProvince_.regionGdp.plusNamed().greatEqual(BigDecimal.ZERO))
                .and(ChinaProvince_.version.equal(0))
                .paramList(this::createProvinceList)
                .asUpdate();

        printStmt(stmt);

    }


    /**
     * @see io.army.annotation.UpdateMode
     */
    @Test
    public void updateParentWithOnlyNullMode() {
        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(User_.T, "u")
                .set(User_.identityType, IdentityType.PERSON)
                .set(User_.identityId, 888)
                .set(User_.nickName, "令狐冲")
                .where(User_.id.equal(1))
                .and(User_.nickName.equal("zoro"))
                .asUpdate();

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
