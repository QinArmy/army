package io.army.session.suite.postgre;

import io.army.dialect.Database;
import io.army.session.FactoryUtils;
import io.army.sync.LocalSessionFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public abstract class PostgreSuiteTests {

    protected static LocalSessionFactory syncSessionFactory;

    @BeforeClass
    public static void beforeClass() {
        PostgreSuiteTests.syncSessionFactory = FactoryUtils.createArmyBankSyncFactory(Database.PostgreSQL);
    }

}
