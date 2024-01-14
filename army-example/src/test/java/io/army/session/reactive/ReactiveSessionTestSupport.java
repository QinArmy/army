/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.session.reactive;

import io.army.ArmyTestDataSupport;
import io.army.dialect.Database;
import io.army.reactive.ReactiveSession;
import io.army.reactive.ReactiveSessionFactory;
import io.army.session.FactoryUtils;
import io.army.util._Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public abstract class ReactiveSessionTestSupport extends ArmyTestDataSupport {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final List<Database> DATABASE_LIST = Collections.singletonList(Database.MySQL);

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

        for (Database db : DATABASE_LIST) {
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

        for (Database db : DATABASE_LIST) {
            factory = FACTORY_MAP.remove(db);
            if (factory != null) {
                factory.close()
                        .block();
            }
        }
    }


    @AfterMethod
    public final void closeSessionAfterTest(final ITestResult testResult) {
        ReactiveSession session;
        for (Object parameter : testResult.getParameters()) {
            if (!(parameter instanceof ReactiveSession)) {
                continue;
            }
            session = (ReactiveSession) parameter;
            session.close()
                    .block();
            LOG.debug("session[name : {} , hash : {}] have closed", session.name(), System.identityHashCode(session));
        }
    }


    @DataProvider(name = "localSessionProvider", parallel = true)
    public final Object[][] createLocalSession(final ITestNGMethod targetMethod) {
        return createDataSession(true, targetMethod);
    }

    @DataProvider(name = "rmSessionProvider", parallel = true)
    public final Object[][] createRmSession(final ITestNGMethod targetMethod) {
        return createDataSession(false, targetMethod);
    }


    private Object[][] createDataSession(final boolean local, final ITestNGMethod targetMethod) {
        final Database database = this.database;
        final Object[][] dataArray;
        if (database == null) {
            dataArray = new Object[DATABASE_LIST.size()][];
            for (int i = 0; i < dataArray.length; i++) {
                dataArray[i] = createDatabaseSession(local, DATABASE_LIST.get(i), targetMethod);
            }
        } else {
            final Object[] sessionInfo;
            sessionInfo = createDatabaseSession(local, database, targetMethod);
            dataArray = new Object[][]{sessionInfo};
        }

        return dataArray;
    }


    private Object[] createDatabaseSession(final boolean local, final Database database, final ITestNGMethod targetMethod) {

        final String methodName;
        methodName = targetMethod.getMethodName();

        final ReactiveSessionFactory sessionFactory;
        sessionFactory = FACTORY_MAP.get(database);
        assert sessionFactory != null;

        final ReactiveSession session;
        if (local) {
            session = sessionFactory.localBuilder()
                    .name(methodName + '#' + database.name())
                    .allowQueryInsert(true)
                    .build()
                    .block();
        } else {
            session = sessionFactory.rmBuilder()
                    .name(methodName + '#' + database.name())
                    .allowQueryInsert(true)
                    .build()
                    .block();
        }
        return new Object[]{session};
    }

    /*-------------------below protected static -------------------*/


}
