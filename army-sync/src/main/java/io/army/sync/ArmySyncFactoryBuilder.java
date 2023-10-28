package io.army.sync;

import io.army.advice.FactoryAdvice;
import io.army.dialect.Dialect;
import io.army.dialect.DialectEnv;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.SyncKey;
import io.army.executor.ExecutorEnv;
import io.army.mapping.MappingEnv;
import io.army.meta.FieldMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.schema.*;
import io.army.session.DataAccessException;
import io.army.session.DdlMode;
import io.army.session.SessionFactoryException;
import io.army.session._ArmyFactoryBuilder;
import io.army.sync.executor.MetaExecutor;
import io.army.sync.executor.SyncStmtExecutorFactory;
import io.army.sync.executor.SyncStmtExecutorFactoryProvider;
import io.army.util._Exceptions;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
            executorProvider = createExecutorProvider(name, env, dataSource);

            final Dialect useDialect = env.getRequired(ArmyKey.DIALECT);

            //3. create ServerMeta
            final ServerMeta serverMeta;
            serverMeta = executorProvider.createServerMeta(useDialect, this.nameToDatabaseFunc);

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

        try (MetaExecutor metaExecutor = executorFactory.createMetaExecutor()) {

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
                        validateSchema(sessionFactory, schemaResult);
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


    /**
     * @see #initializingSchema(ArmySyncSessionFactory, DdlMode)
     */
    private static void validateSchema(ArmySyncSessionFactory sessionFactory, _SchemaResult schemaResult) {

        final StringBuilder builder = new StringBuilder()
                .append(SyncLocalSessionFactory.class.getName())
                .append('[')
                .append(sessionFactory.name())
                .append("] validate failure.\n");

        int differentCount;
        final List<TableMeta<?>> newTableList;
        newTableList = schemaResult.newTableList();
        differentCount = newTableList.size();
        if (differentCount > 0) {
            for (TableMeta<?> table : newTableList) {
                builder.append('\n')
                        .append(table.tableName())
                        .append(" not exists.");
            }
            builder.append('\n');
        }

        final List<_TableResult> tableResultList;
        tableResultList = schemaResult.changeTableList();
        if (tableResultList.size() > 0) {
            for (_TableResult tableResult : tableResultList) {
                builder.append('\n')
                        .append(tableResult.table())
                        .append(" not match:");
                differentCount += tableResult.newFieldList().size();
                for (FieldMeta<?> field : tableResult.newFieldList()) {
                    builder.append("\n\t")
                            .append(field)
                            .append(" not exists.");
                }
                for (_FieldResult field : tableResult.changeFieldList()) {
                    if (field.containSqlType() || field.containDefault() || field.containNullable()) {
                        builder.append("\n\t")
                                .append(field)
                                .append(" not match.");
                        differentCount++;
                    }
                }
                differentCount += tableResult.newIndexList().size();
                for (String index : tableResult.newIndexList()) {
                    builder.append("\n\tindex[")
                            .append(index)
                            .append("] not exists.");
                }
                differentCount += tableResult.changeIndexList().size();
                for (String index : tableResult.changeIndexList()) {
                    builder.append("\n\tindex[")
                            .append(index)
                            .append("] not match.");
                }

            }
            builder.append('\n');
        }

        if (differentCount > 0) {
            throw new SessionFactoryException(builder.toString());
        }

    }


    private static SyncStmtExecutorFactoryProvider createExecutorProvider(final String name, final ArmyEnvironment env,
                                                                          final Object dataSource) throws SessionFactoryException {

        final Class<?> providerClass;
        final String className = env.getOrDefault(SyncKey.EXECUTOR_PROVIDER);
        try {
            providerClass = Class.forName(className);
        } catch (Throwable e) {
            String m = String.format("Load class %s %s occur error.", SyncStmtExecutorFactoryProvider.class.getName(), className);
            throw new SessionFactoryException(m, e);
        }

        if (!SyncStmtExecutorFactoryProvider.class.isAssignableFrom(providerClass)) {
            String m = String.format("%s value[%s] isn' the implementation of %s .", SyncKey.EXECUTOR_PROVIDER,
                    className, SyncStmtExecutorFactoryProvider.class.getName());
            throw new SessionFactoryException(m);
        }

        final String methodName = "create";
        try {

            final Method method;
            method = providerClass.getMethod(methodName, Object.class, String.class, ArmyEnvironment.class);
            final int modifiers;
            modifiers = method.getModifiers();
            if (!(Modifier.isPublic(modifiers)
                    && Modifier.isStatic(modifiers)
                    && SyncStmtExecutorFactoryProvider.class.isAssignableFrom(method.getReturnType()))) {
                String m;
                m = String.format("%s not declared %s(Object,String,ArmyEnvironment) method.", providerClass.getName(), method);
                throw new SessionFactoryException(m);

            }
            final SyncStmtExecutorFactoryProvider provider;
            provider = (SyncStmtExecutorFactoryProvider) method.invoke(null, dataSource, name, env);
            if (provider == null) {
                String m = String.format("%s %s return null.", methodName, providerClass.getName());
                throw new NullPointerException(m);
            }
            return provider;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            String m = String.format("%s %s invoke error:%s", providerClass.getName(), methodName, e.getMessage());
            throw new SessionFactoryException(m, e);
        }

    }


}
