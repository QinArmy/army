package io.army.session;

import io.army.ArmyTestDataSupport;
import io.army.dialect.Database;
import io.army.sync.SyncSession;
import io.army.sync.SyncSessionFactory;
import io.army.util._Collections;
import io.jdbd.session.DatabaseSession;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

public abstract class SyncSessionTestSupport extends ArmyTestDataSupport {

    private static final ConcurrentMap<Database, SyncSessionFactory> FACTORY_MAP = _Collections.concurrentHashMap();

    private final Database database;

    protected SyncSessionTestSupport(Database database) {
        this.database = database;
    }

    @BeforeSuite
    @SuppressWarnings("all")
    public final void beforeSuiteCreateSessionFactory() {

        FACTORY_MAP.computeIfAbsent(this.database, FactoryUtils::createArmyBankSyncFactory);

    }

    @AfterSuite
    public final void afterSuiteCloseSessionFactory() {
        final SyncSessionFactory sessionFactory;
        sessionFactory = FACTORY_MAP.remove(this.database);
        if (sessionFactory != null) {
            sessionFactory.close();
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
        key = method.getDeclaringClass().getName() + '.' + method.getName() + "#session";

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

        final int currentInvocationCount = targetMethod.getCurrentInvocationCount() + 1;

        final String methodName, keyOfSession;
        methodName = targetMethod.getMethodName();
        keyOfSession = keyNameOfSession(targetMethod);

        final Class<?>[] parameterTypeArray;
        parameterTypeArray = targetMethod.getParameterTypes();


        int sessionIndex = -1, methodIndex = -1, readOnlyIndex = -1;

        Class<?> parameterType;
        for (int i = 0; i < parameterTypeArray.length; i++) {
            parameterType = parameterTypeArray[i];
            if (DatabaseSession.class.isAssignableFrom(parameterType)) {
                sessionIndex = i;
            } else if (parameterType == boolean.class) {
                readOnlyIndex = i;
            } else if (parameterType == String.class) {
                methodIndex = i;
            }
        }

        final boolean readOnly = (currentInvocationCount & 1) == 0;

        final SyncSessionFactory sessionFactory;
        sessionFactory = FACTORY_MAP.get(this.database);
        assert sessionFactory != null;

        final SyncSession session;
        if (local) {
            session = sessionFactory.localBuilder()
                    .name(methodName)
                    .readonly(readOnly)
                    .allowQueryInsert(true)
                    .build();
        } else {
            session = sessionFactory.rmBuilder()
                    .name(methodName)
                    .readonly(readOnly)
                    .allowQueryInsert(true)
                    .build();
        }

        context.setAttribute(keyOfSession, session);

        final Object[][] result;
        if (sessionIndex > -1 && methodIndex > -1 && readOnlyIndex > -1) {
            result = new Object[1][3];
            result[0][sessionIndex] = session;
            result[0][methodIndex] = methodName;
            result[0][readOnlyIndex] = readOnly;
        } else if (sessionIndex > -1 && readOnlyIndex > -1) {
            result = new Object[1][2];
            result[0][sessionIndex] = session;
            result[0][readOnlyIndex] = readOnly;
        } else if (sessionIndex > -1 && methodIndex > -1) {
            result = new Object[1][2];
            result[0][sessionIndex] = session;
            result[0][methodIndex] = methodName;
        } else {
            result = new Object[][]{{session}};
        }
        return result;
    }

    /*-------------------below protected static -------------------*/

    protected static String keyNameOfSession(final ITestNGMethod targetMethod) {
        return targetMethod.getRealClass().getName() + '.' + targetMethod.getMethodName() + "#session";
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
