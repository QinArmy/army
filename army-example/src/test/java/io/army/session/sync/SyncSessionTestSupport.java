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

package io.army.session.sync;

import io.army.criteria.annotaion.VisibleMode;
import io.army.dialect.Database;
import io.army.session.*;
import io.army.sync.SyncLocalSession;
import io.army.sync.SyncSession;
import io.army.sync.SyncSessionFactory;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public abstract class SyncSessionTestSupport extends SessionTestSupport {

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
        } else for (Database db : DATABASE_LIST) {
            syncFactory = SYNC_FACTORY_MAP.remove(db);
            if (syncFactory == null) {
                continue;
            }
            syncFactory.close();
        }
    }

    @BeforeMethod
    public final void startLocalTransactionIfNeed(final ITestResult testResult) {
        final Transactional transactional;
        transactional = testResult.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Transactional.class);
        if (transactional == null) {
            return;
        }
        SyncLocalSession session;
        Isolation isolation;
        for (Object parameter : testResult.getParameters()) {
            if (!(parameter instanceof SyncLocalSession)) {
                continue;
            }
            session = (SyncLocalSession) parameter;
            if (session.inAnyTransaction()) {
                continue;
            }

            switch (transactional.isolation()) {
                case DEFAULT:
                    isolation = null;
                    break;
                case READ_COMMITTED:
                    isolation = Isolation.READ_COMMITTED;
                    break;
                case REPEATABLE_READ:
                    isolation = Isolation.REPEATABLE_READ;
                    break;
                case SERIALIZABLE:
                    isolation = Isolation.SERIALIZABLE;
                    break;
                case READ_UNCOMMITTED:
                    isolation = Isolation.READ_UNCOMMITTED;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(transactional.isolation());
            }

            session.startTransaction(TransactionOption.option(isolation));

        }


    }


    @AfterMethod
    public final void closeSessionAfterTest(final ITestResult testResult) {
        final Throwable error = testResult.getThrowable();

        SyncSession session;
        for (Object parameter : testResult.getParameters()) {
            if (!(parameter instanceof SyncSession)) {
                continue;
            }
            session = (SyncSession) parameter;
            if (session instanceof SyncLocalSession && session.inAnyTransaction()) {
                if (error == null) {
                    ((SyncLocalSession) session).commit(Option.EMPTY_FUNC);
                } else {
                    ((SyncLocalSession) session).rollback(Option.EMPTY_FUNC);
                }
            }
            session.close();
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

        final VisibleMode visibleMode;
        visibleMode = targetMethod.getConstructorOrMethod().getMethod().getAnnotation(VisibleMode.class);

        final SyncSessionFactory sessionFactory;
        sessionFactory = SYNC_FACTORY_MAP.get(database);
        assert sessionFactory != null;

        final SyncSession session;
        if (local) {
            final SyncSessionFactory.LocalSessionBuilder builder;
            builder = sessionFactory.localBuilder()
                    .name(methodName + '#' + database.name())
                    .allowQueryInsert(true);

            if (visibleMode != null) {
                builder.visibleMode(visibleMode.value());
            }

            session = builder.build();
        } else {
            final SyncSessionFactory.RmSessionBuilder builder;
            builder = sessionFactory.rmBuilder()
                    .name(methodName + '#' + database.name())
                    .allowQueryInsert(true);

            if (visibleMode != null) {
                builder.visibleMode(visibleMode.value());
            }
            session = builder.build();
        }
        return new Object[]{session};
    }

    /*-------------------below protected static -------------------*/


    /*-------------------below static class  -------------------*/


}
