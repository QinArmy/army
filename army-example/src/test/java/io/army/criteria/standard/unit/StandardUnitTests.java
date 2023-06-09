package io.army.criteria.standard.unit;

import io.army.criteria.PrimaryStatement;
import io.army.criteria.Visible;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.ChinaProvince;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

abstract class StandardUnitTests {


    final List<ChinaProvince> createProvinceList() {
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


    static void printStmt(final Logger logger, final PrimaryStatement statement) {
        for (Database database : Database.values()) {
            switch (database) {
                case MySQL:
                case Postgre:
                    break;
                default:
                    continue;
            }
            for (Dialect dialect : database.dialects()) {
                logger.debug("{}:\n{}", dialect.name(), statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
            }
        }


    }


}
