package io.army.session.sync.postgre;

import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sync.SyncLocalSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class PostgreQuerySuiteTests extends PostgreSuiteTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreQuerySuiteTests.class);

    @Test
    public void selectDomain(final SyncLocalSession syncSession) {
        final Select stmt;
        stmt = Postgres.query()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
//                .where(ChinaRegion_.name.equal(SQLs::param, "曲境"))
//                .and(ChinaRegion_.createTime::equal, SQLs::literal, LocalDateTime.now().minusDays(1))
                .limit(SQLs::literal, 1)
                .asQuery();

        final Supplier<ChinaRegion<?>> constructor = ChinaRegion::new;

        syncSession.queryObject(stmt, constructor)
                .forEach(c -> LOG.debug("{}", c.getName()));

    }

}
