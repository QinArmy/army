package io.army.session.sync.postgre;


import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.sync.SyncLocalSession;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(dataProvider = "localSessionProvider")
public class CommandTests extends SessionTestSupport {


    @Transactional
    @Test
    public void setTest(final SyncLocalSession session) {
        DmlCommand stmt;
        String parameterValue;
        stmt = Postgres.setStmt()
                .set(SQLs.LOCAL, "timezone", SQLs.EQUAL, "+08:00")
                .asCommand();


        session.update(stmt);

        parameterValue = session.queryOne(Postgres.show("timezone"), String.class);
        Assert.assertEquals(parameterValue, "+08:00");

        stmt = Postgres.setStmt()
                .set(SQLs.SESSION, "timezone", SQLs.EQUAL, "+08:00")
                .asCommand();

        session.update(stmt);

        stmt = Postgres.setStmt()
                .set(SQLs.LOCAL, "client_encoding", SQLs.EQUAL, "UTF8")
                .asCommand();

        session.update(stmt);

        stmt = Postgres.setStmt()
                .set(SQLs.SESSION, "client_encoding", SQLs.TO, s -> s.accept("UTF8"))
                .asCommand();

        session.update(stmt);

    }


}
