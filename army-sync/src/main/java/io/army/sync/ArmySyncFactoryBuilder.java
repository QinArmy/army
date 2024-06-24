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

package io.army.sync;

import io.army.advice.FactoryAdvice;
import io.army.dialect.DialectParser;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.SyncKey;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.schema.SchemaComparer;
import io.army.schema.SchemaInfo;
import io.army.schema.SchemaResult;
import io.army.session.DdlMode;
import io.army.session.SessionFactoryException;
import io.army.session._ArmyFactoryBuilder;
import io.army.sync.executor.SyncExecutorFactory;
import io.army.sync.executor.SyncExecutorFactoryProvider;
import io.army.sync.executor.SyncMetaExecutor;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * <p>This class is a implementation of {@link SyncFactoryBuilder}.
 * <p>This class is the builder of {@link ArmySyncSessionFactory}.
 *
 * @see ArmySyncSessionFactory
 * @since 0.6.0
 */
final class ArmySyncFactoryBuilder
        extends _ArmyFactoryBuilder<SyncFactoryBuilder, SyncSessionFactory> implements SyncFactoryBuilder {

    /**
     * @see SyncFactoryBuilder#builder()
     */
    static SyncFactoryBuilder create() {
        return new ArmySyncFactoryBuilder();
    }

    private static final Logger LOG = LoggerFactory.getLogger(ArmySyncFactoryBuilder.class);

    SyncExecutorFactory stmtExecutorFactory;

    /**
     * private constructor
     */
    private ArmySyncFactoryBuilder() {
    }


    @Override
    protected SyncSessionFactory buildAfterScanTableMeta(final String name, final Object dataSource, final ArmyEnvironment env) {

        try {

            // 1. create ExecutorProvider
            final SyncExecutorFactoryProvider executorProvider;
            executorProvider = createExecutorProvider(name, env, dataSource, SyncExecutorFactoryProvider.class,
                    SyncKey.EXECUTOR_PROVIDER, SyncKey.EXECUTOR_PROVIDER_MD5);

            // 2. create ServerMeta
            final ServerMeta serverMeta;
            serverMeta = executorProvider.createServerMeta(this.nameToDatabaseFunc);

            // 3. create DialectParser
            final DialectParser dialectParser;
            dialectParser = createDialectParser(name, false, serverMeta, env);

            // 4. create SyncExecutorFactory
            final SyncExecutorFactory executorFactory;
            executorFactory = executorProvider.createFactory(createExecutorEnv(name, env, dialectParser));

            final FactoryAdvice factoryAdvice;
            factoryAdvice = createFactoryAdviceComposite(this.factoryAdvices);
            // 5. invoke beforeInstance
            if (factoryAdvice != null) {
                factoryAdvice.beforeInstance(serverMeta, env);
            }

            // 6. create SessionFactoryImpl instance
            this.stmtExecutorFactory = executorFactory;
            this.ddlMode = env.getOrDefault(ArmyKey.DDL_MODE);
            final ArmySyncSessionFactory sessionFactory;
            sessionFactory = ArmySyncSessionFactory.create(this);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Created {}", sessionFactory);
            }
            assert name.equals(sessionFactory.name());
            assert sessionFactory.executorFactory == this.stmtExecutorFactory;

            //7. invoke beforeInitialize
            if (factoryAdvice != null) {
                factoryAdvice.beforeInitialize(sessionFactory);
            }

            // 8. invoke initializingFactory
            initializingFactory(sessionFactory);

            // 9. invoke afterInitialize
            if (factoryAdvice != null) {
                factoryAdvice.afterInitialize(sessionFactory);
            }
            return sessionFactory;
        } catch (SessionFactoryException e) {
            throw e;
        } catch (Exception e) {
            throw new SessionFactoryException(e.getMessage(), e);
        }
    }

    @Override
    protected SyncSessionFactory handleError(SessionFactoryException cause) {
        throw cause;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }


    /**
     * @see #buildAfterScanTableMeta(String, Object, ArmyEnvironment)
     */
    private void initializingFactory(final ArmySyncSessionFactory sessionFactory) throws SessionFactoryException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing {}", sessionFactory);
        }

        // initializing schema
        final DdlMode ddlMode;
        ddlMode = this.ddlMode;
        switch (ddlMode) {
            case NONE:
                // no-op
                break;
            case VALIDATE:
            case UPDATE:
            case DROP_CREATE://TODO detail
                initializingSchema(sessionFactory, ddlMode);
                break;
            default:
                throw _Exceptions.unexpectedEnum(ddlMode);
        }

    }


    /**
     * @see #initializingFactory(ArmySyncSessionFactory)
     */
    private void initializingSchema(final ArmySyncSessionFactory sessionFactory, final DdlMode ddlMode) {

        final long startTime;
        startTime = System.currentTimeMillis();

        final SyncExecutorFactory executorFactory;
        executorFactory = sessionFactory.executorFactory;

        try (SyncMetaExecutor executor = executorFactory.metaExecutor(dataSourceFunc())) {

            //1.extract schema info.
            final SchemaInfo schemaInfo;
            schemaInfo = executor.extractInfo();

            //2.compare schema meta and schema info.
            final SchemaResult schemaResult;
            switch (ddlMode) {
                case VALIDATE:
                case UPDATE: {
                    final SchemaComparer schemaComparer;
                    schemaComparer = SchemaComparer.create(sessionFactory.serverMeta());
                    final Collection<TableMeta<?>> tableCollection;
                    tableCollection = sessionFactory.tableMap().values();
                    schemaResult = schemaComparer.compare(schemaInfo, sessionFactory.schemaMeta(), tableCollection);
                }
                break;
                case DROP_CREATE: {
                    final Collection<TableMeta<?>> tableCollection;
                    tableCollection = sessionFactory.tableMap().values();
                    schemaResult = SchemaResult.dropCreate(schemaInfo.catalog(), schemaInfo.schema(), tableCollection);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(ddlMode);
            }

            //3.validate or execute ddl
            switch (ddlMode) {
                case VALIDATE: {
                    if (schemaResult.newTableList().size() > 0 || schemaResult.changeTableList().size() > 0) {
                        final SessionFactoryException error;
                        if ((error = validateSchema(sessionFactory, schemaResult)) != null) {
                            throw error;
                        }
                    }
                }
                break;
                case UPDATE:
                case DROP_CREATE: {
                    //create ddl
                    final List<String> ddlList;
                    ddlList = parseMetaDdl(sessionFactory, schemaResult);
                    final int ddlSize = ddlList.size();
                    if (ddlSize == 0) {
                        break;
                    }

                    LOG.info("{}:\n\n{}", sessionFactory, ddlToSqlLog(ddlList));
                    executor.executeDdl(ddlList);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(ddlMode);
            }

            LOG.info("Initializing database of {}[{}],{}[{}],cost {} ms.",
                    SyncSessionFactory.class.getName(),
                    sessionFactory.name(),
                    DdlMode.class.getName(),
                    ddlMode,
                    System.currentTimeMillis() - startTime
            );
        } catch (Exception e) {
            String m = String.format("%s[%s] schema initializing failure.", SyncSessionFactory.class.getName(),
                    sessionFactory.name());
            throw new SessionFactoryException(m, e);
        }


    }


}
