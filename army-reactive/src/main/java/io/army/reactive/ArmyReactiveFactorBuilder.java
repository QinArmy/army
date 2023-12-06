package io.army.reactive;

import io.army.advice.FactoryAdvice;
import io.army.dialect.Dialect;
import io.army.dialect.DialectEnv;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.ReactiveKey;
import io.army.mapping.MappingEnv;
import io.army.meta.TableMeta;
import io.army.reactive.executor.ReactiveExecutorFactory;
import io.army.reactive.executor.ReactiveExecutorFactoryProvider;
import io.army.reactive.executor.ReactiveMetaExecutor;
import io.army.schema.SchemaInfo;
import io.army.schema._SchemaComparer;
import io.army.schema._SchemaResult;
import io.army.session.DdlMode;
import io.army.session.SessionFactoryException;
import io.army.session._ArmyFactoryBuilder;
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
 * @since 1.0
 */
final class ArmyReactiveFactorBuilder extends _ArmyFactoryBuilder<ReactiveFactoryBuilder, Mono<ReactiveSessionFactory>>
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

        final Dialect useDialect = env.getRequired(ArmyKey.DIALECT);

        final FactoryAdvice factoryAdvice;
        factoryAdvice = createFactoryAdviceComposite(this.factoryAdvices);

        return executorProvider.createServerMeta(useDialect, this.nameToDatabaseFunc)  // 2.  create serverMeta
                .flatMap(serverMeta -> {
                    assert serverMeta.usedDialect() == useDialect;

                    // 3. create MappingEnv
                    final MappingEnv mappingEnv;
                    mappingEnv = MappingEnv.builder()
                            .serverMeta(serverMeta)
                            .zoneOffset(env.get(ArmyKey.ZONE_OFFSET))
                            .jsonCodec(this.jsonCodec)
                            .xmlCodec(this.xmlCodec)
                            .build();

                    return executorProvider.createFactory(createExecutorEnv(name, serverMeta, env, mappingEnv)) // 4.  create executor factory
                            .flatMap(executorFactory -> {

                                // 5. invoke beforeInstance
                                if (factoryAdvice != null) {
                                    factoryAdvice.beforeInstance(serverMeta, env);
                                }
                                // 6. create DialectEnv
                                this.dialectEnv = DialectEnv.builder()
                                        .factoryName(name)
                                        .environment(env)
                                        .fieldGeneratorMap(createFieldGeneratorMap())
                                        .mappingEnv(mappingEnv)
                                        .build();

                                this.ddlMode = env.getOrDefault(ArmyKey.DDL_MODE);
                                this.stmtExecutorFactory = executorFactory;

                                // 7. create SessionFactoryImpl instance
                                final ArmyReactiveSessionFactory sessionFactory;
                                sessionFactory = ArmyReactiveSessionFactory.create(this);
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Created {}", sessionFactory);
                                }
                                assert name.equals(sessionFactory.name());
                                assert sessionFactory.stmtExecutorFactory == executorFactory;

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

        return sessionFactory.stmtExecutorFactory.metaExecutor(dataSourceFunc())
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
