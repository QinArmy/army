package io.army.session.suite.postgre;

import io.army.ArmyTestDataSupport;
import io.army.dialect.Database;
import io.army.session.FactoryUtils;
import io.army.sync.LocalSession;
import io.army.sync.LocalSessionFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public abstract class PostgreSuiteTests extends ArmyTestDataSupport {

    protected static LocalSessionFactory syncSessionFactory;

    @BeforeClass
    public static void beforeClass() {
        PostgreSuiteTests.syncSessionFactory = FactoryUtils.createArmyBankSyncFactory(Database.PostgreSQL);
    }

    @AfterClass
    public static void afterClass() {
        syncSessionFactory.close();
    }


    @DataProvider
    public static Object[][] getSession() {
        return new Object[][]{{
                syncSessionFactory.builder()
                        .build()
        }};
    }

    public void releaseSyncSession(LocalSession session) {
        if (session.hasTransaction()) {
            throw new IllegalArgumentException();
        }
        session.close();
    }

}
