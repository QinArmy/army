package io.army.session;

import io.army.ArmyTestDataSupport;
import io.army.dialect.Database;
import io.army.sync.SyncSession;
import io.army.sync.SyncSessionFactory;
import io.army.util._Collections;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

public abstract class SyncSessionTestSupport extends ArmyTestDataSupport {

    private static final ConcurrentMap<Database, SyncSessionFactory> SYNC_FACTORY_MAP = _Collections.concurrentHashMap();

    private final Database database;

    protected SyncSessionTestSupport(@Nullable Database database) {
        this.database = database;
    }


    @BeforeSuite
    @SuppressWarnings("all")
    public final void beforeSuiteCreateSessionFactory() {
        final Database database = this.database;

        if (database != null) {
            SYNC_FACTORY_MAP.computeIfAbsent(database, FactoryUtils::createArmyBankSyncFactory);
            return;
        }

        for (Database db : Database.values()) {
            switch (db) {
                case MySQL:
                case PostgreSQL:
                    SYNC_FACTORY_MAP.computeIfAbsent(db, FactoryUtils::createArmyBankSyncFactory);
                    break;
                default:
                    // no-op
            } // switch
        }// for loop

    }

    @AfterSuite
    public final void afterSuiteCloseSessionFactory() {
        final Database database = this.database;
        SyncSessionFactory syncFactory;
        if (database != null) {
            syncFactory = SYNC_FACTORY_MAP.remove(database);
            if (syncFactory != null) {
                syncFactory.close();
            }
            return;
        }

        for (Database db : Database.values()) {
            syncFactory = SYNC_FACTORY_MAP.remove(db);
            if (syncFactory != null) {
                syncFactory.close();
            }
        }
    }


    @AfterMethod
    public final void closeSessionAfterTest(final Method method, final ITestContext context) {
        boolean match = false;
        for (Class<?> parameterType : method.getParameterTypes()) {
            if (SyncSession.class.isAssignableFrom(parameterType)) {
                match = true;
                break;
            }
        }
        if (!match) {
            return;
        }

        final String key;
        key = method.getDeclaringClass().getName() + '.' + method.getName() + "#syncSession";

        final Object value;
        value = context.getAttribute(key);
        if (value instanceof SyncSession) {
            context.removeAttribute(key);
            ((SyncSession) value).close();
        } else if (value instanceof TestSessionHolder && ((TestSessionHolder) value).close) {
            context.removeAttribute(key);
            ((TestSessionHolder) value).session.close();
        }
    }

    @DataProvider(name = "localSessionProvider", parallel = true)
    public final Object[][] createLocalSession(final ITestNGMethod targetMethod, final ITestContext context) {
        return createDatabaseSession(true, targetMethod, context);
    }

    @DataProvider(name = "rmSessionProvider", parallel = true)
    public final Object[][] createRmSession(final ITestNGMethod targetMethod, final ITestContext context) {
        return createDatabaseSession(false, targetMethod, context);
    }


    private Object[][] createDatabaseSession(final boolean local, final ITestNGMethod targetMethod,
                                             final ITestContext context) {

        final String methodName, keyOfSession;
        methodName = targetMethod.getMethodName();
        keyOfSession = keyNameOfSession(targetMethod);

        final SyncSessionFactory sessionFactory;
        sessionFactory = SYNC_FACTORY_MAP.get(this.database);
        assert sessionFactory != null;

        final SyncSession session;
        if (local) {
            session = sessionFactory.localBuilder()
                    .name(methodName)
                    .allowQueryInsert(true)
                    .build();
        } else {
            session = sessionFactory.rmBuilder()
                    .name(methodName)
                    .allowQueryInsert(true)
                    .build();
        }

        context.setAttribute(keyOfSession, session);
        return new Object[][]{{session}};
    }

    /*-------------------below protected static -------------------*/

    protected static String keyNameOfSession(final ITestNGMethod targetMethod) {
        return targetMethod.getRealClass().getName() + '.' + targetMethod.getMethodName() + "#syncSession";
    }

    /*-------------------below static class  -------------------*/

    /**
     * for {@link #closeSessionAfterTest(Method, ITestContext)}
     */
    protected static final class TestSessionHolder {

        public final SyncSession session;

        public final boolean close;

        public TestSessionHolder(SyncSession session, boolean close) {
            this.session = session;
            this.close = close;
        }

    }// TestSessionHolder


}
