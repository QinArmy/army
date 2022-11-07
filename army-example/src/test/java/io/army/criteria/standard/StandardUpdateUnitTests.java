package io.army.criteria.standard;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.pill.domain.PillUser_;
import io.army.example.pill.struct.IdentityType;
import io.army.meta.TableMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.army.criteria.impl.SQLs.AND;

public class StandardUpdateUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardUpdateUnitTests.class);


    @Test
    public void updateParent() {
        final BigDecimal addGdp = new BigDecimal("888.8");
        final Map<String, Object> map = new HashMap<>();
        map.put("firstId", (byte) 1);
        map.put("secondId", "3");

        TableMeta<?> table = ChinaRegion_.T;

        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(table, "c")
                .set(ChinaRegion_.name, SQLs::param, "武侠江湖")
                .set(ChinaRegion_.regionGdp, SQLs::param, addGdp)
                .where(ChinaRegion_.id::between, SQLs::literal, map::get, "firstId", AND, "secondId")
                .and(ChinaRegion_.name::equal, SQLs::literal, "江湖")
                .and(ChinaRegion_.regionGdp::plus, SQLs::literal, new BigDecimal(1000), Expression::greatEqual, BigDecimal.ZERO)
                .asUpdate();

        printStmt(stmt);
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
                .where(ChinaProvince_.id::equal, SQLs::literal, "")
                .and(ChinaProvince_.name::equal, SQLs::param, "江湖")
                .and(ChinaProvince_.regionGdp::plus, SQLs::literal, gdpAmount, Expression::greatEqual, BigDecimal.ZERO)
                .and(ChinaProvince_.governor.equal(SQLs::param, "阳顶天").or(consumer -> {
                    IPredicate predicate;

                    predicate = ChinaProvince_.governor.equal(SQLs::param, "石教主");
                    consumer.accept(predicate);

                    predicate = ChinaProvince_.governor.equal(SQLs::param, "钟教主")
                            .and(ChinaProvince_.governor::equal, SQLs::param, "老钟");
                    consumer.accept(predicate);

                    predicate = ChinaProvince_.governor.equal(SQLs::param, "方腊");
                    consumer.accept(predicate);
                }))
                .asUpdate();

        printStmt(stmt);

    }

    @Test
    public void batchUpdateParent() {
        final Update stmt;
        stmt = SQLs.batchDomainUpdate()
                .update(ChinaProvince_.T, "p")
                .set(ChinaProvince_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .set(ChinaProvince_.governor, SQLs::namedNullableParam)
                .where(ChinaProvince_.id::equal, SQLs::namedParam)
                .and(ChinaProvince_.regionGdp::plus, SQLs::namedParam, ChinaProvince_.REGION_GDP, Expression::greatEqual, BigDecimal.ZERO)
                .and(ChinaProvince_.version::equal, SQLs::literal, "0")
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
                .update(PillUser_.T, "u")
                .set(PillUser_.identityType, SQLs::literal, IdentityType.PERSON)
                .set(PillUser_.identityId, SQLs::literal, 888)
                .set(PillUser_.nickName, SQLs::param, "令狐冲")
                .where(PillUser_.id::equal, SQLs::literal, "1")
                .and(PillUser_.nickName::equal, SQLs::param, "zoro")
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
