package io.army.session.sync.mysql;


import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.sync.SyncLocalSession;
import org.testng.annotations.Test;


/**
 * <p>This class is test class of {@link MySQLs#setStmt()}.
 */
@Test(dataProvider = "localSessionProvider")
public class SetStmtTests extends SessionTestSupport {


    /**
     * <p>Test {@link MySQLs#setStmt()}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/set-variable.html">SET Syntax for Variable Assignment</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/server-system-variables.html">Server System Variables</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/user-variables.html">User-Defined Variables</a>
     * @since 0.6.6
     */
    @Test
    public void simple(final SyncLocalSession session) {

        final DmlCommand stmt;
        stmt = MySQLs.setStmt()
                .set(MySQLs.AT, "my_null", null, MySQLs.AT, "my_scalar", SQLs.scalarSubQuery()
                        .select(SQLs.space("1").as("r"))
                        .asQuery()
                ).comma(MySQLs.SESSION, "autocommit", false)
                .comma(MySQLs.SESSION, "sql_mode", SQLs.scalarSubQuery()
                        .select(MySQLs.atAtSession("sql_mode").as("r"))
                        .asQuery()
                ).asCommand();

        final long rows;
        rows = session.update(stmt);
        LOG.debug("{} rows : {}", session.name(), rows);

    }


}
