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
import io.army.dialect.Dialect;
import io.army.dialect.DialectEnv;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.SyncKey;
import io.army.executor.ExecutorEnv;
import io.army.mapping.MappingEnv;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.schema.SchemaInfo;
import io.army.schema._SchemaComparer;
import io.army.schema._SchemaResult;
import io.army.session.DdlMode;
import io.army.session.SessionFactoryException;
import io.army.session._ArmyFactoryBuilder;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.executor.SyncExecutorFactory;
import io.army.sync.executor.SyncStmtExecutorFactoryProvider;
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

            //2. create ExecutorProvider
            final SyncStmtExecutorFactoryProvider executorProvider;
            executorProvider = createExecutorProvider(name, env, dataSource, SyncStmtExecutorFactoryProvider.class,
                    SyncKey.EXECUTOR_PROVIDER, SyncKey.EXECUTOR_PROVIDER_MD5);

            final Dialect useDialect = env.getRequired(ArmyKey.DIALECT);

            //3. create ServerMeta
            final ServerMeta serverMeta;
            serverMeta = executorProvider.createServerMeta(useDialect, this.nameToDatabaseFunc);
            assert serverMeta.usedDialect() == useDialect;

            //4. create MappingEnv
            final MappingEnv mappingEnv;
            mappingEnv = MappingEnv.builder()
                    .serverMeta(serverMeta)
                    .zoneOffset(env.get(ArmyKey.ZONE_OFFSET))
                    .jsonCodec(this.jsonCodec)
                    .xmlCodec(this.xmlCodec)
                    .build();

            //5. create ExecutorEnv
            final ExecutorEnv executorEnv;
            executorEnv = createExecutorEnv(name, serverMeta, env, mappingEnv);

            //6. create LocalExecutorFactory
            final SyncExecutorFactory executorFactory;
            executorFactory = executorProvider.createFactory(executorEnv);

            final FactoryAdvice factoryAdvice;
            factoryAdvice = createFactoryAdviceComposite(this.factoryAdvices);
            //7. invoke beforeInstance
            if (factoryAdvice != null) {
                factoryAdvice.beforeInstance(serverMeta, env);
            }

            //8. create DialectEnv
            this.dialectEnv = DialectEnv.builder()
                    .factoryName(name)
                    .environment(env)
                    .fieldGeneratorMap(createFieldGeneratorMap())
                    .mappingEnv(mappingEnv)
                    .build();

            //9. create SessionFactoryImpl instance
            this.stmtExecutorFactory = executorFactory;
            this.ddlMode = env.getOrDefault(ArmyKey.DDL_MODE);
            final ArmySyncSessionFactory sessionFactory;
            sessionFactory = ArmySyncSessionFactory.create(this);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Created {}", sessionFactory);
            }
            assert name.equals(sessionFactory.name());
            assert sessionFactory.stmtExecutorFactory == this.stmtExecutorFactory;

            //9. invoke beforeInitialize
            if (factoryAdvice != null) {
                factoryAdvice.beforeInitialize(sessionFactory);
            }

            //10. invoke initializingFactory
            initializingFactory(sessionFactory);

            //7. invoke afterInitialize
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
        final Logger log = getLogger();

        if (log.isDebugEnabled()) {
            log.debug("Initializing {}", sessionFactory);
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
        final Logger log = getLogger();

        final String msgPrefix;
        msgPrefix = String.format("Initializing database of %s[%s],%s[%s]",
                SyncSessionFactory.class.getName(), sessionFactory.name(),
                DdlMode.class.getName(), ddlMode);

        log.info(msgPrefix);

        final long startTime;
        startTime = System.currentTimeMillis();

        final SyncExecutorFactory executorFactory;
        executorFactory = sessionFactory.stmtExecutorFactory;

        try (MetaExecutor metaExecutor = executorFactory.metaExecutor(dataSourceFunc())) {

            //1.extract schema info.
            final SchemaInfo schemaInfo;
            schemaInfo = metaExecutor.extractInfo();

            //2.compare schema meta and schema info.
            final _SchemaResult schemaResult;
            switch (ddlMode) {
                case VALIDATE:
                case UPDATE: {
                    final _SchemaComparer schemaComparer;
                    schemaComparer = _SchemaComparer.create(sessionFactory.serverMeta());
                    final Collection<TableMeta<?>> tableCollection;
                    tableCollection = sessionFactory.tableMap().values();
                    schemaResult = schemaComparer.compare(schemaInfo, sessionFactory.schemaMeta(), tableCollection);
                }
                break;
                case DROP_CREATE: {
                    final Collection<TableMeta<?>> tableCollection;
                    tableCollection = sessionFactory.tableMap().values();
                    schemaResult = _SchemaResult.dropCreate(schemaInfo.catalog(), schemaInfo.schema(), tableCollection);
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
                    metaExecutor.executeDdl(ddlList);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(ddlMode);
            }
            log.info("{},cost {} ms.", msgPrefix, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            String m = String.format("%s[%s] schema initializing failure.", SyncSessionFactory.class.getName(),
                    sessionFactory.name());
            throw new SessionFactoryException(m, e);
        }


    }


}
