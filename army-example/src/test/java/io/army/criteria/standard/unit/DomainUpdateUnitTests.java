package io.army.criteria.standard.unit;

import io.army.annotation.UpdateMode;
import io.army.criteria.Expression;
import io.army.criteria.PrimaryStatement;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.criteria.impl.SQLs;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.pill.domain.PillPerson_;
import io.army.example.pill.domain.PillUser_;
import io.army.example.pill.struct.IdentityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.army.criteria.impl.SQLs.AND;

public class DomainUpdateUnitTests extends StandardUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(DomainUpdateUnitTests.class);


    @Test
    public void updateParent() {
        final BigDecimal addGdp = new BigDecimal("888.8");
        final Map<String, Object> map = new HashMap<>();
        map.put("firstId", (byte) 1);
        map.put("secondId", "3");

        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(ChinaRegion_.T, "c")
                .set(ChinaRegion_.name, SQLs::param, "武侠江湖")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, addGdp)
                .where(ChinaRegion_.id::between, SQLs::literal, map::get, "firstId", AND, "secondId")
                .and(ChinaRegion_.name.equal(SQLs::literal, "江湖"))
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, addGdp, Expression::greatEqual, BigDecimal.ZERO)
                .asUpdate();

        printStmt(LOG, stmt);
    }

    @Test
    public void updateChild() {
        final BigDecimal gdpAmount = new BigDecimal("888.8");
        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(ChinaProvince_.T, "p")
                .set(ChinaProvince_.name, SQLs::param, "武侠江湖")
                .set(ChinaProvince_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .set(ChinaProvince_.provincialCapital, SQLs::param, "光明顶")
                .set(ChinaProvince_.governor, SQLs::param, "张无忌")
                .where(ChinaProvince_.id.equal(SQLs::literal, 1))
                .and(ChinaProvince_.name::equal, SQLs::param, () -> "江湖")
                .and(ChinaProvince_.regionGdp::plus, SQLs::literal, gdpAmount, Expression::greatEqual, BigDecimal.ZERO)
                .and(() ->
                        ChinaProvince_.governor.equal(SQLs::param, "石教主")
                                .or(ChinaProvince_.governor::equal, SQLs::param, () -> "钟教主")
                                .or(ChinaProvince_.governor::equal, SQLs::param, () -> "老钟")
                                .or(ChinaProvince_.governor::equal, SQLs::param, () -> "方腊")
                )
                .asUpdate();

        printStmt(LOG, stmt);

    }

    @Test
    public void batchUpdateParent() {
        final Update stmt;
        stmt = SQLs.batchDomainUpdate()
                .update(ChinaProvince_.T, "p")
                .set(ChinaProvince_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .set(ChinaProvince_.governor, SQLs::namedParam)
                .where(ChinaProvince_.id::equal, SQLs::namedParam)
                .and(ChinaProvince_.regionGdp::plus, SQLs::namedParam, ChinaProvince_.REGION_GDP, Expression::greatEqual, BigDecimal.ZERO)
                .and(ChinaProvince_.version::equal, SQLs::param, () -> "0")
                .paramList(this::createProvinceList)
                .asUpdate();

        printStmt(LOG, stmt);

    }


    /**
     * @see io.army.annotation.UpdateMode
     */
    @Test
    public void updateParentWithOnlyNullMode() {
        assert PillUser_.identityId.updateMode() == UpdateMode.ONLY_NULL;
        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(PillUser_.T, "u")
                .set(PillUser_.identityType, SQLs::literal, IdentityType.PERSON)
                .set(PillUser_.identityId, SQLs::literal, 888)
                .set(PillUser_.nickName, SQLs::param, "令狐冲")
                .where(PillUser_.id::equal, SQLs::literal, () -> "1")
                .and(PillUser_.nickName::equal, SQLs::param, () -> "zoro")
                .asUpdate();

        printStmt(LOG, stmt);
    }

    @Test
    public void updateOnlyParentField() {
        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(PillPerson_.T, "up")
                .set(PillUser_.identityType, SQLs::literal, IdentityType.PERSON)
                .set(PillUser_.identityId, SQLs::literal, 888)
                .set(PillUser_.nickName, SQLs::param, "令狐冲")
                .where(PillUser_.id::equal, SQLs::literal, () -> "1")
                .and(PillUser_.nickName::equal, SQLs::param, () -> "zoro")
                .and(PillPerson_.birthday::equal, SQLs::param, LocalDate::now)
                .asUpdate();

        printStmt(LOG, stmt);
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
