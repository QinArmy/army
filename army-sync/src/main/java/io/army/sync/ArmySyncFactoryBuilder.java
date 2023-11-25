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
import io.army.session.DataAccessException;
import io.army.session.DdlMode;
import io.army.session.SessionFactoryException;
import io.army.session._ArmyFactoryBuilder;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.executor.SyncStmtExecutorFactory;
import io.army.sync.executor.SyncStmtExecutorFactoryProvider;
import io.army.util._Exceptions;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;

/**
 * <p>This class is base class of following :
 * <ul>
 *     <li>{@link ArmySyncLocalFactoryBuilder}</li>
 *     <li>{@link ArmySyncRmFactoryBuilder}</li>
 * </ul>
 *
 * @param <B> builder interface java type
 * @param <R> java type of {@link SyncSessionFactory}'s sub interface
 * @since 1.0
 */
abstract class ArmySyncFactoryBuilder<B, R extends SyncSessionFactory> extends _ArmyFactoryBuilder<B, R> {

    SyncStmtExecutorFactory stmtExecutorFactory;

    ArmySyncFactoryBuilder() {
    }

    @Override
    public final R buildAfterScanTableMeta(final String name, final Object dataSource, final ArmyEnvironment env) {
        final Logger log = getLogger();

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
            mappingEnv = MappingEnv.create(false, serverMeta, env.get(ArmyKey.ZONE_OFFSET), new MockJsonCodec());

            //5. create ExecutorEnv
            final ExecutorEnv executorEnv;
            executorEnv = createExecutorEnv(name, serverMeta, env, mappingEnv);

            //6. create LocalExecutorFactory
            final SyncStmtExecutorFactory executorFactory;
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
            final R sessionFactory;
            sessionFactory = createSessionFactory();
            if (log.isDebugEnabled()) {
                log.debug("Created {}", sessionFactory);
            }
            final ArmySyncSessionFactory factory = (ArmySyncSessionFactory) sessionFactory;
            assert name.equals(factory.name());
            assert factory.stmtExecutorFactory == this.stmtExecutorFactory;

            //9. invoke beforeInitialize
            if (factoryAdvice != null) {
                factoryAdvice.beforeInitialize(factory);
            }

            //10. invoke initializingFactory
            initializingFactory(factory);

            //7. invoke afterInitialize
            if (factoryAdvice != null) {
                factoryAdvice.afterInitialize(factory);
            }
            return sessionFactory;
        } catch (SessionFactoryException e) {
            throw e;
        } catch (Exception e) {
            throw new SessionFactoryException(e.getMessage(), e);
        }
    }

    @Override
    protected final R handleError(SessionFactoryException cause) {
        throw cause;
    }

    abstract R createSessionFactory();


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
                SyncLocalSessionFactory.class.getName(), sessionFactory.name(),
                DdlMode.class.getName(), ddlMode);

        log.info(msgPrefix);

        final long startTime;
        startTime = System.currentTimeMillis();

        final SyncStmtExecutorFactory executorFactory;
        executorFactory = sessionFactory.stmtExecutorFactory;

        try (MetaExecutor metaExecutor = executorFactory.metaExecutor()) {

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
                    if (ddlList.size() > 0) {
                        metaExecutor.executeDdl(ddlList);
                    }

                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(ddlMode);
            }
            log.info("{},cost {} ms.", msgPrefix, System.currentTimeMillis() - startTime);
        } catch (DataAccessException e) {
            String m = String.format("%s[%s] schema initializing failure."
                    , SyncLocalSessionFactory.class.getName(), sessionFactory.name());
            throw new SessionFactoryException(m, e);
        }


    }


}
