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

package io.army.session;

import io.army.ArmyTestDataSupport;
import io.army.dialect.Database;
import io.army.sync.SyncSession;
import io.army.sync.SyncSessionFactory;
import io.army.util.ArrayUtils;
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
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public abstract class SyncSessionTestSupport extends ArmyTestDataSupport {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final List<Database> DATABASE_LIST = ArrayUtils.of(Database.MySQL, Database.PostgreSQL);

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

        for (Database db : DATABASE_LIST) {
            SYNC_FACTORY_MAP.computeIfAbsent(db, FactoryUtils::createArmyBankSyncFactory);
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

        for (Database db : DATABASE_LIST) {
            syncFactory = SYNC_FACTORY_MAP.remove(db);
            if (syncFactory != null) {
                syncFactory.close();
            }
        }
    }


    @AfterMethod
    public final void closeSessionAfterTest(final ITestResult testResult) {
        for (Object parameter : testResult.getParameters()) {
            if (parameter instanceof SyncSession) {
                ((SyncSession) parameter).close();
                LOG.debug("{} have closed", parameter);
            }
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

        final SyncSessionFactory sessionFactory;
        sessionFactory = SYNC_FACTORY_MAP.get(database);
        assert sessionFactory != null;

        final SyncSession session;
        if (local) {
            session = sessionFactory.localBuilder()
                    .name(methodName + '#' + database.name())
                    .allowQueryInsert(true)
                    .build();
        } else {
            session = sessionFactory.rmBuilder()
                    .name(methodName + '#' + database.name())
                    .allowQueryInsert(true)
                    .build();
        }
        return new Object[]{session};
    }

    /*-------------------below protected static -------------------*/


    /*-------------------below static class  -------------------*/



}
