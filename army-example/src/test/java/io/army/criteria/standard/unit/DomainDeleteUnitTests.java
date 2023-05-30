package io.army.criteria.standard.unit;

import io.army.criteria.DeleteStatement;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.army.criteria.impl.SQLs.AND;
import static io.army.criteria.impl.SQLs.AS;


public class DomainDeleteUnitTests extends StandardUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(DomainDeleteUnitTests.class);


    @Test
    public void deleteParent() {
        final Map<String, Object> map = new HashMap<>();
        map.put("firstId", (byte) 1);
        map.put("secondId", "3");

        final DeleteStatement stmt;
        stmt = SQLs.domainDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id::between, SQLs::literal, map.get("firstId"), AND, map.get("secondId"))
                .and(ChinaRegion_.name.equal(SQLs::literal, "江湖"))
                .asDelete();
        printStmt(LOG, stmt);
    }

    @Test
    public void deleteChild() {
        final DeleteStatement stmt;
        stmt = SQLs.domainDelete()
                .deleteFrom(ChinaProvince_.T, AS, "p")
                .where(ChinaProvince_.id.equal(SQLs::literal, 1))
                .and(ChinaRegion_.name::equal, SQLs::param, "江湖")
                .and(ChinaProvince_.governor.equal(SQLs::param, "石教主").or(consumer -> {
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "钟教主"));
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "老钟"));
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "方腊"));
                        })
                )
                .asDelete();

        printStmt(LOG, stmt);

    }

    @Test
    public void batchDeleteParent() {
        final DeleteStatement stmt;
        stmt = SQLs.batchDomainDelete()
                .deleteFrom(ChinaRegion_.T, AS, "cr")
                .where(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.version::equal, SQLs::param, "0")
                .namedParamList(this::createProvinceList)
                .asDelete();

        printStmt(LOG, stmt);

    }

    @Test
    public void batchDeleteChild() {

        final DeleteStatement stmt;
        stmt = SQLs.batchDomainDelete()
                .deleteFrom(ChinaProvince_.T, AS, "p")
                .where(ChinaProvince_.id.equal(SQLs::namedParam, ChinaRegion_.ID))
                .and(ChinaRegion_.name.equal(SQLs::namedParam, ChinaRegion_.NAME))
                .and(ChinaProvince_.governor.equal(SQLs::param, "石教主").or(consumer -> {
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "钟教主"));
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "老钟"));
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "方腊"));
                        })
                )
                .namedParamList(this::createProvinceList)
                .asDelete();

        printStmt(LOG, stmt);

    }


}
