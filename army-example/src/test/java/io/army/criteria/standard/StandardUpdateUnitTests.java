package io.army.criteria.standard;

import io.army.criteria.*;
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
                .where(ChinaRegion_.id::between, SQLs::literal, map::get, "firstId", "secondId")
                .and(ChinaRegion_.name::equal, SQLs::literal, "江湖")
                .and(ChinaRegion_.regionGdp::plus, SQLs::literal, new BigDecimal(1000), Expression::greatEqual, BigDecimal.ZERO)
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
                .where(ChinaProvince_.id::equal, SQLs::literal, 1)
                .and(ChinaProvince_.name::equal, SQLs::param, "江湖")
                .and(ChinaProvince_.regionGdp::plus, SQLs::literal, addGdp, Expression::greatEqual, BigDecimal.ZERO)
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
                .setPlus(ChinaProvince_.regionGdp)
                .set(ChinaProvince_.governor)
                .where(ChinaProvince_.id.equalNamed())
                .and(ChinaProvince_.regionGdp.plusNamed().greatEqual(SQLs::literal, BigDecimal.ZERO))
                .and(ChinaProvince_.version::equal, SQLs::literal, 0)
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
                .where(User_.id::equal, SQLs::literal, 1)
                .and(User_.nickName::equal, SQLs::literal, "zoro")
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
