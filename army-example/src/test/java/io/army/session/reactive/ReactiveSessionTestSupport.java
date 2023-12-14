package io.army.session.reactive;

import io.army.ArmyTestDataSupport;
import io.army.dialect.Database;
import io.army.reactive.ReactiveSession;
import io.army.reactive.ReactiveSessionFactory;
import io.army.session.FactoryUtils;
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

public abstract class ReactiveSessionTestSupport extends ArmyTestDataSupport {


    private static final Database[] DATABASE_VALUES = new Database[]{Database.MySQL};

    private static final ConcurrentMap<Database, ReactiveSessionFactory> FACTORY_MAP = _Collections.concurrentHashMap();

    private final Database database;

    protected ReactiveSessionTestSupport(@Nullable Database database) {
        this.database = database;
    }


    @BeforeSuite
    @SuppressWarnings("all")
    public final void beforeSuiteCreateSessionFactory() {
        final Database database = this.database;

        if (database != null) {
            FACTORY_MAP.computeIfAbsent(database, FactoryUtils::createArmyBankReactiveFactory);
            return;
        }

        for (Database db : DATABASE_VALUES) {
            FACTORY_MAP.computeIfAbsent(db, FactoryUtils::createArmyBankReactiveFactory);

        }// for loop

    }

    @AfterSuite
    public final void afterSuiteCloseSessionFactory() {
        final Database database = this.database;
        ReactiveSessionFactory factory;
        if (database != null) {
            factory = FACTORY_MAP.remove(database);
            if (factory != null) {
                factory.close()
                        .block();
            }
            return;
        }

        for (Database db : Database.values()) {
            factory = FACTORY_MAP.remove(db);
            if (factory != null) {
                factory.close()
                        .block();
            }
        }
    }


    @AfterMethod
    public final void closeSessionAfterTest(final Method method, final ITestContext context) {
        boolean match = false;
        for (Class<?> parameterType : method.getParameterTypes()) {
            if (ReactiveSession.class.isAssignableFrom(parameterType)) {
                match = true;
                break;
            }
        }
        if (!match) {
            return;
        }

        final String key;
        key = method.getDeclaringClass().getName() + '.' + method.getName() + "#reactiveSession";

        final Object value;
        value = context.getAttribute(key);
        if (value instanceof ReactiveSession) {
            context.removeAttribute(key);
            ((ReactiveSession) value).close()
                    .block();
        } else if (value instanceof TestSessionHolder && ((TestSessionHolder) value).close) {
            context.removeAttribute(key);
            ((TestSessionHolder) value).session.close()
                    .block();
        }
    }


    @DataProvider(name = "localSessionProvider", parallel = true)
    public final Object[][] createLocalSession(final ITestNGMethod targetMethod, final ITestContext context) {
        return createDataSession(true, targetMethod, context);
    }

    @DataProvider(name = "rmSessionProvider", parallel = true)
    public final Object[][] createRmSession(final ITestNGMethod targetMethod, final ITestContext context) {
        return createDataSession(false, targetMethod, context);
    }


    private Object[][] createDataSession(final boolean local, final ITestNGMethod targetMethod,
                                         final ITestContext context) {
        final Database database = this.database;

        if (database != null) {
            final Object[] sessionInfo;
            sessionInfo = createDatabaseSession(local, database, targetMethod, context);
            return new Object[][]{sessionInfo};
        }

        final int length = DATABASE_VALUES.length;

        final Object[][] dataArray = new Object[length][];

        for (int i = 0; i < length; i++) {
            dataArray[i] = createDatabaseSession(local, DATABASE_VALUES[i], targetMethod, context);
        }
        return dataArray;
    }


    private Object[] createDatabaseSession(final boolean local, final Database database, final ITestNGMethod targetMethod,
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
            if (ReactiveSession.class.isAssignableFrom(parameterType)) {
                sessionIndex = i;
            } else if (parameterType == boolean.class) {
                readOnlyIndex = i;
            } else if (parameterType == String.class) {
                methodIndex = i;
            }
        }

        final boolean readOnly = (currentInvocationCount & 1) == 0;

        final ReactiveSessionFactory sessionFactory;
        sessionFactory = FACTORY_MAP.get(database);
        assert sessionFactory != null;

        final ReactiveSession session;
        if (local) {
            session = sessionFactory.localBuilder()
                    .name(methodName)
                    .readonly(readOnly)
                    .allowQueryInsert(true)
                    .build()
                    .block();
        } else {
            session = sessionFactory.rmBuilder()
                    .name(methodName)
                    .readonly(readOnly)
                    .allowQueryInsert(true)
                    .build()
                    .block();
        }

        context.setAttribute(keyOfSession, session);

        final Object[] result;
        if (sessionIndex > -1 && methodIndex > -1 && readOnlyIndex > -1) {
            result = new Object[3];
            result[sessionIndex] = session;
            result[methodIndex] = methodName;
            result[readOnlyIndex] = readOnly;
        } else if (sessionIndex > -1 && readOnlyIndex > -1) {
            result = new Object[2];
            result[sessionIndex] = session;
            result[readOnlyIndex] = readOnly;
        } else if (sessionIndex > -1 && methodIndex > -1) {
            result = new Object[2];
            result[sessionIndex] = session;
            result[methodIndex] = methodName;
        } else {
            result = new Object[]{session};
        }
        return result;
    }

    /*-------------------below protected static -------------------*/

    protected static String keyNameOfSession(final ITestNGMethod targetMethod) {
        return targetMethod.getRealClass().getName() + '.' + targetMethod.getMethodName() + "#reactiveSession";
    }

    /*-------------------below static class  -------------------*/

    /**
     * for {@link #closeSessionAfterTest(Method, ITestContext)}
     */
    protected static final class TestSessionHolder {

        public final ReactiveSession session;

        public final boolean close;

        public TestSessionHolder(ReactiveSession session, boolean close) {
            this.session = session;
            this.close = close;
        }

    }// TestSessionHolder


}
