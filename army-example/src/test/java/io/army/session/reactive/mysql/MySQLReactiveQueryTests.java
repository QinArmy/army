package io.army.session.reactive.mysql;


import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.reactive.ReactiveLocalSession;
import org.testng.annotations.Test;

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class MySQLReactiveQueryTests extends MySQLReactiveSessionTestsSupport {


    @Test
    public void queryFields(final ReactiveLocalSession session) {
        final Select stmt;

        stmt = MySQLs.query()
                .select(ChinaRegion_.id, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .from(ChinaRegion_.T, AS, "c")
                .limit(SQLs::literal, 10)
                .asQuery();

        session.queryObject(stmt, ChinaRegion::create)
                .blockLast();
    }

    @Test
    public void queryDomain(final ReactiveLocalSession session) {
        final Select stmt;

        stmt = MySQLs.query()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .limit(SQLs::literal, 10)
                .asQuery();

        session.queryObject(stmt, ChinaRegion::create)
                .blockLast();
    }


}
