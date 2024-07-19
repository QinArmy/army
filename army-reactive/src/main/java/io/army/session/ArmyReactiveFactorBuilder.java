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

import io.army.advice.FactoryAdvice;
import io.army.dialect.DialectParser;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.ReactiveKey;
import io.army.executor.ReactiveExecutorFactory;
import io.army.executor.ReactiveExecutorFactoryProvider;
import io.army.executor.ReactiveMetaExecutor;
import io.army.meta.TableMeta;
import io.army.schema.SchemaComparer;
import io.army.schema.SchemaInfo;
import io.army.schema.SchemaResult;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;


/**
 * <p>This class is a implementation of {@link ReactiveFactoryBuilder}.
 *
 * @see ReactiveFactoryBuilder#builder()
 * @see ArmyReactiveSessionFactory#create(ArmyReactiveFactorBuilder)
 * @since 0.6.0
 */
final class ArmyReactiveFactorBuilder extends ArmyFactoryBuilder<ReactiveFactoryBuilder, Mono<ReactiveSessionFactory>>
        implements ReactiveFactoryBuilder {

    static ArmyReactiveFactorBuilder create() {
        return new ArmyReactiveFactorBuilder();
    }

    private static final Logger LOG = LoggerFactory.getLogger(ArmyReactiveFactorBuilder.class);

    ReactiveExecutorFactory stmtExecutorFactory;

    /**
     * private constructor
     */
    private ArmyReactiveFactorBuilder() {
    }

    @Override
    protected Mono<ReactiveSessionFactory> buildAfterScanTableMeta(final String name, final Object dataSource,
                                                                   final ArmyEnvironment env) {

        final ReactiveExecutorFactoryProvider executorProvider;
        try {
            // 1. create ExecutorProvider
            executorProvider = createExecutorProvider(name, env, dataSource, ReactiveExecutorFactoryProvider.class,
                    ReactiveKey.EXECUTOR_PROVIDER, ReactiveKey.EXECUTOR_PROVIDER_MD5);

        } catch (Throwable e) {
            return Mono.error(e);
        }


        final FactoryAdvice factoryAdvice;
        factoryAdvice = createFactoryAdviceComposite(this.factoryAdvices);

        return executorProvider.createServerMeta(this.nameToDatabaseFunc)  // 2.  create serverMeta
                .flatMap(serverMeta -> {

                    // 3. create DialectParser
                    final DialectParser dialectParser;
                    dialectParser = createDialectParser(name, true, serverMeta, env);

                    return executorProvider.createFactory(createExecutorEnv(name, env, dialectParser)) // 4.  create executor factory
                            .flatMap(executorFactory -> {

                                // 5. invoke beforeInstance
                                if (factoryAdvice != null) {
                                    factoryAdvice.beforeInstance(serverMeta, env);
                                }

                                this.ddlMode = env.getOrDefault(ArmyKey.DDL_MODE);
                                this.stmtExecutorFactory = executorFactory;

                                // 7. create SessionFactoryImpl instance
                                final ArmyReactiveSessionFactory sessionFactory;
                                sessionFactory = ArmyReactiveSessionFactory.create(this);
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Created {}", sessionFactory);
                                }
                                assert name.equals(sessionFactory.name());
                                assert sessionFactory.executorFactory == executorFactory;

                                // 8. invoke beforeInitialize
                                if (factoryAdvice != null) {
                                    factoryAdvice.beforeInitialize(sessionFactory);
                                }
                                // 9. initializing session factory
                                return initializingFactory(sessionFactory)
                                        .thenReturn((ReactiveSessionFactory) sessionFactory);
                            });

                }).doOnSuccess(factory -> {
                    // 10. afterInitialize
                    if (factoryAdvice != null) {
                        factoryAdvice.afterInitialize(factory);
                    }
                });


    }

    @Override
    protected Mono<ReactiveSessionFactory> handleError(SessionFactoryException cause) {
        return Mono.error(cause);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
    /*-------------------below private instance methods -------------------*/

    private Mono<Void> initializingFactory(final ReactiveSessionFactory factory) {
        final Logger log = getLogger();

        if (log.isDebugEnabled()) {
            log.debug("Initializing {}", factory);
        }

        // initializing schema
        final DdlMode ddlMode;
        ddlMode = this.ddlMode;

        final Mono<Void> mono;
        switch (ddlMode) {
            case NONE:
                mono = Mono.empty();
                break;
            case VALIDATE:
            case UPDATE:
            case DROP_CREATE://TODO detail
                mono = initializingSchema((ArmyReactiveSessionFactory) factory, ddlMode);
                break;
            default:
                mono = Mono.error(_Exceptions.unexpectedEnum(ddlMode));
        }
        return mono;
    }


    private Mono<Void> initializingSchema(final ArmyReactiveSessionFactory sessionFactory, final DdlMode ddlMode) {

        final String msgPrefix;
        msgPrefix = String.format("Initializing database of %s[%s],%s[%s]",
                ReactiveSessionFactory.class.getName(), sessionFactory.name(),
                DdlMode.class.getName(), ddlMode);

        LOG.info(msgPrefix);

        final long startTime;
        startTime = System.currentTimeMillis();

        return sessionFactory.executorFactory.metaExecutor(dataSourceFunc())
                .flatMap(executor -> executor.extractInfo() // 1. extract schema info.
                        .flatMap(info -> updateSchemaIfNeed(sessionFactory, executor, info, ddlMode)) // 2. update schema
                        .then(Mono.defer(executor::close))  // normally close executor
                        .onErrorResume(error -> executor.close()
                                .then(Mono.error(error)))    //  on error close executor
                )
                .doOnSuccess(v -> LOG.info("{},cost {} ms.", msgPrefix, System.currentTimeMillis() - startTime))
                .then();

    }

    private Mono<Void> updateSchemaIfNeed(final ArmyReactiveSessionFactory sessionFactory,
                                          final ReactiveMetaExecutor executor, final SchemaInfo schemaInfo,
                                          final DdlMode ddlMode) {
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
        final Mono<Void> mono;
        switch (ddlMode) {
            case VALIDATE: {
                final SessionFactoryException error;
                if (schemaResult.newTableList().size() == 0 && schemaResult.changeTableList().size() == 0) {
                    mono = Mono.empty();
                } else if ((error = validateSchema(sessionFactory, schemaResult)) == null) {
                    mono = Mono.empty();
                } else {
                    mono = Mono.error(error);
                }
            }
            break;
            case UPDATE:
            case DROP_CREATE: {
                //create ddl
                final List<String> ddlList;
                ddlList = parseMetaDdl(sessionFactory, schemaResult);

                if (ddlList.size() > 0) {
                    LOG.info("{}:\n\n{}", sessionFactory, ddlToSqlLog(ddlList));
                    mono = executor.executeDdl(ddlList);
                } else {
                    mono = Mono.empty();
                }
            }
            break;
            default:
                mono = Mono.error(_Exceptions.unexpectedEnum(ddlMode));
        }

        return mono;

    }


}
