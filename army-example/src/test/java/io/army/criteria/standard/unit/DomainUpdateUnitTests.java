package io.army.criteria.standard.unit;

import io.army.annotation.UpdateMode;
import io.army.criteria.BatchUpdate;
import io.army.criteria.Expression;
import io.army.criteria.Update;
import io.army.criteria.impl.SQLs;
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
import java.util.HashMap;
import java.util.Map;

import static io.army.criteria.impl.SQLs.AND;
import static io.army.criteria.impl.SQLs.AS;


public class DomainUpdateUnitTests extends StandardUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(DomainUpdateUnitTests.class);

    @Test
    public void domainUpdateParent() {
        final BigDecimal addGdp = new BigDecimal("888.8");
        final Map<String, Object> map = new HashMap<>();
        map.put("firstId", (byte) 1);
        map.put("secondId", "3");

        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(ChinaRegion_.T, AS, "c")
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
                .update(ChinaProvince_.T, AS, "p")
                .set(ChinaRegion_.name, SQLs::param, "武侠江湖")
                .set(ChinaRegion_.name, ChinaProvince_.provincialCapital) // test SET child field in parent SET clause
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .set(ChinaProvince_.provincialCapital, SQLs::param, "光明顶")
                .set(ChinaProvince_.governor, SQLs::param, "张无忌")
                .where(ChinaProvince_.id.equal(SQLs::literal, 1))
                .and(ChinaRegion_.name::equal, SQLs::param, "江湖")
                .and(ChinaRegion_.regionGdp::plus, SQLs::literal, gdpAmount, Expression::greatEqual, BigDecimal.ZERO)
                .and(ChinaProvince_.governor.equal(SQLs::param, "石教主").or(consumer -> {
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "钟教主"));
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "老钟"));
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "方腊"));
                        })
                )
                .asUpdate();

        printStmt(LOG, stmt);

    }

    @Test
    public void batchUpdateParent() {
        final BatchUpdate stmt;
        stmt = SQLs.batchDomainUpdate()
                .update(ChinaProvince_.T, AS, "p")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .set(ChinaProvince_.governor, SQLs::namedParam)
                .where(ChinaProvince_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greatEqual, BigDecimal.ZERO)
                .and(ChinaRegion_.version::equal, SQLs::param, "0")
                .namedParamList(this::createProvinceList)
                .asUpdate();

        printStmt(LOG, stmt);

    }

    @Test
    public void batchUpdateChild() {
        final BigDecimal gdpAmount = new BigDecimal("888.8");

        final BatchUpdate stmt;
        stmt = SQLs.batchDomainUpdate()
                .update(ChinaProvince_.T, AS, "p")
                .set(ChinaRegion_.name, SQLs::param, "武侠江湖")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .set(ChinaProvince_.provincialCapital, SQLs::param, "光明顶")
                .set(ChinaProvince_.governor, SQLs::param, "张无忌")
                .where(ChinaProvince_.id.equal(SQLs::namedParam, ChinaRegion_.ID))
                .and(ChinaRegion_.name.equal(SQLs::namedParam, ChinaRegion_.NAME))
                .and(ChinaRegion_.regionGdp::plus, SQLs::literal, gdpAmount, Expression::greatEqual, BigDecimal.ZERO)
                .and(ChinaProvince_.governor.equal(SQLs::param, "石教主").or(consumer -> {
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "钟教主"));
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "老钟"));
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "方腊"));
                        })
                )
                .namedParamList(this::createProvinceList)
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
                .update(PillUser_.T, AS, "u")
                .set(PillUser_.identityType, SQLs::literal, IdentityType.PERSON)
                .set(PillUser_.identityId, SQLs::literal, 888)
                .set(PillUser_.nickName, SQLs::param, "令狐冲")
                .where(PillUser_.id::equal, SQLs::literal, "1")
                .and(PillUser_.nickName::equal, SQLs::param, "zoro")
                .asUpdate();

        printStmt(LOG, stmt);
    }

    @Test
    public void updateOnlyParentField() {
        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(PillPerson_.T, AS, "up")
                .set(PillUser_.identityType, SQLs::literal, IdentityType.PERSON)
                .set(PillUser_.identityId, SQLs::literal, 888)
                .set(PillUser_.nickName, SQLs::param, "令狐冲")
                .where(PillPerson_.id::equal, SQLs::literal, "1")
                .and(PillUser_.nickName::equal, SQLs::param, "zoro")
                .and(PillPerson_.birthday::equal, SQLs::param, LocalDate::now)
                .asUpdate();

        printStmt(LOG, stmt);
    }

    @Test
    public void dynamicSetUpdateOnlyParentField() {
        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(PillPerson_.T, AS, "up")
                .sets(s -> s.set(PillUser_.identityType, SQLs::literal, IdentityType.PERSON)
                        .set(PillUser_.identityId, SQLs::literal, 888)
                        .set(PillUser_.nickName, SQLs::param, "令狐冲")
                )
                .where(PillPerson_.id::equal, SQLs::literal, "1")
                .and(PillUser_.nickName::equal, SQLs::param, "zoro")
                .and(PillPerson_.birthday::equal, SQLs::param, LocalDate::now)
                .asUpdate();

        printStmt(LOG, stmt);
    }





}
