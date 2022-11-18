package io.army.criteria.standard;

import io.army.criteria.Delete;
import io.army.criteria.PrimaryStatement;
import io.army.criteria.Visible;
import io.army.criteria.impl.SQLs;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static io.army.criteria.impl.SQLs.AS;

public class StandardDeleteUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardDeleteUnitTests.class);

    @Test
    public void singleDeleteParent() {
        final Delete stmt;
        stmt = SQLs.singleDelete()
                .deleteFrom(ChinaRegion_.T, AS, "r")
                .where(ChinaRegion_.id::equal, SQLs::param, () -> 1)
                .and(ChinaRegion_.name.equal(SQLs::param, "马鱼腮角"))
                .and(ChinaProvince_.version::equal, SQLs::param, () -> 2)
                .asDelete();

        printStmt(stmt);
    }


    @Test
    public void deleteParent() {
        final Delete stmt;
        stmt = SQLs.domainDelete()
                .deleteFrom(ChinaRegion_.T, AS, "r")
                .where(ChinaRegion_.id::equal, SQLs::param, () -> 1)
                .and(ChinaRegion_.name.equal(SQLs::param, "马鱼腮角"))
                .and(ChinaRegion_.version.equal(SQLs::param, 2))
                .asDelete();

        printStmt(stmt);

    }

    @Test
    public void deleteChild() {
        final Delete stmt;
        stmt = SQLs.domainDelete()
                .deleteFrom(ChinaProvince_.T, AS, "p")
                .where(ChinaProvince_.id::equal, SQLs::param, () -> 1)
                .and(ChinaRegion_.name.equal(SQLs::param, "江南省"))
                .and(ChinaProvince_.governor::equal, SQLs::param, () -> "无名")
                .and(ChinaRegion_.version.equal(SQLs::param, 2))
                .asDelete();

        printStmt(stmt);
    }


    @Test
    public void batchDeleteChild() {
        final Delete stmt;
        stmt = SQLs.batchDomainDelete()
                .deleteFrom(ChinaProvince_.T, AS, "p")
                .where(ChinaProvince_.id::equal, SQLs::namedParam)
                .and(ChinaProvince_.name::equal, SQLs::namedParam)
                .and(ChinaProvince_.governor::equal, SQLs::namedParam)
                .and(ChinaProvince_.regionGdp.plus(SQLs::namedParam).less(SQLs::literal, "6666.66"))
                .and(ChinaRegion_.version.equal(SQLs::param, 2))
                .paramList(this::createProvinceList)
                .asDelete();

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
