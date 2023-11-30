package io.army.session.suite.postgre;

import io.army.ArmyTestDataSupport;
import io.army.dialect.Database;
import io.army.session.FactoryUtils;
import io.army.sync.SyncLocalSession;
import io.army.sync.SyncSessionFactory;
import org.testng.ITestNGMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public abstract class PostgreSuiteTests extends ArmyTestDataSupport {

    protected static SyncSessionFactory syncSessionFactory;

    @BeforeClass
    public void beforeClass() {
        PostgreSuiteTests.syncSessionFactory = FactoryUtils.createArmyBankSyncFactory(Database.PostgreSQL);
    }

    @AfterClass
    public void afterClass() {
        syncSessionFactory.close();
    }


    @DataProvider(parallel = true)
    public static Object[][] getSession(final ITestNGMethod method) {

        final SyncLocalSession session;
        session = syncSessionFactory.localBuilder()
                .name(method.getMethodName())
                .allowQueryInsert(true)
                .build();

        return new Object[][]{{session}};
    }

    public void releaseSyncSession(SyncLocalSession session) {
        if (session.inTransaction()) {
            throw new IllegalArgumentException();
        }
        session.close();
    }

}
