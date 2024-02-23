package io.army.session.sync.postgre;


import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.sync.SyncLocalSession;
import org.testng.annotations.Test;

@Test(dataProvider = "localSessionProvider")
public class CommandTests extends SessionTestSupport {


    @Test
    public void setTest(final SyncLocalSession session) {
        DmlCommand stmt;
        stmt = Postgres.setStmt()
                .set(SQLs.LOCAL, "timezone", SQLs.EQUAL, "+08:00")
                .asCommand();

        session.update(stmt);

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
