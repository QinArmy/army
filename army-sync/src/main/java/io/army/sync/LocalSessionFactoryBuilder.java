package io.army.sync;

import io.army.ArmyException;
import io.army.advice.FactoryAdvice;
import io.army.advice.sync.DomainAdvice;
import io.army.codec.FieldCodec;
import io.army.codec.JsonCodec;
import io.army.criteria.impl._SchemaMetaFactory;
import io.army.criteria.impl._TableMetaFactory;
import io.army.dialect.DialectEnv;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.SyncKey;
import io.army.executor.ExecutorEnv;
import io.army.generator.FieldGeneratorFactory;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.schema.*;
import io.army.session.DataAccessException;
import io.army.session.DdlMode;
import io.army.session.FactoryBuilderSupport;
import io.army.session.SessionFactoryException;
import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.ExecutorProvider;
import io.army.sync.executor.LocalExecutorFactory;
import io.army.sync.executor.MetaExecutor;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

final class LocalSessionFactoryBuilder extends FactoryBuilderSupport implements LocalFactoryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(LocalSessionFactoryBuilder.class);

    Map<TableMeta<?>, DomainAdvice> domainAdviceMap = Collections.emptyMap();

    private Object dataSource;

    LocalExecutorFactory executorFactory;

    SessionContext sessionContext;

    DialectEnv dialectEnv;

    @Override
    public LocalFactoryBuilder name(String sessionFactoryName) {
        if (!_StringUtils.hasText(sessionFactoryName)) {
            throw new IllegalArgumentException("sessionFactoryName must have text.");
        }
        this.name = sessionFactoryName;
        return this;
    }

    @Override
    public LocalFactoryBuilder schema(String catalog, String schema) {
        this.schemaMeta = _SchemaMetaFactory.getSchema(catalog, schema);
        return this;
    }

    @Override
    public LocalFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
        this.fieldCodecs = fieldCodecs;
        return this;
    }

    @Override
    public LocalFactoryBuilder environment(ArmyEnvironment environment) {
        this.environment = environment;
        return this;
    }


    @Override
    public LocalFactoryBuilder factoryAdvice(Collection<FactoryAdvice> factoryAdvices) {
        this.factoryAdvices = factoryAdvices;
        return this;
    }

    @Override
    public LocalFactoryBuilder exceptionFunction(Function<ArmyException, RuntimeException> exceptionFunction) {
        this.exceptionFunction = exceptionFunction;
        return this;
    }

    @Override
    public LocalFactoryBuilder domainAdvice(Map<TableMeta<?>, DomainAdvice> domainAdviceMap) {
        this.domainAdviceMap = Objects.requireNonNull(domainAdviceMap);
        return this;
    }

    @Override
    public LocalFactoryBuilder fieldGeneratorFactory(@Nullable FieldGeneratorFactory factory) {
        this.fieldGeneratorFactory = factory;
        return this;
    }

    @Override
    public LocalFactoryBuilder datasource(Object dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public LocalFactoryBuilder packagesToScan(List<String> packageList) {
        this.packagesToScan = _Collections.asUnmodifiableList(packageList);
        return this;
    }

    @Override
    public LocalFactoryBuilder currentSessionContext(SessionContext context) {
        this.sessionContext = context;
        return this;
    }

    @Override
    public LocalSessionFactory build() throws SessionFactoryException {

        try {
            final ArmyEnvironment env = Objects.requireNonNull(this.environment);
            //1. scan table meta
            this.scanSchema();

            //2. create ExecutorProvider
            final ExecutorProvider executorProvider;
            executorProvider = createExecutorProvider(env, Objects.requireNonNull(this.dataSource));
            //3. create ServerMeta
            final ServerMeta serverMeta;
            serverMeta = executorProvider.createServerMeta(env.getRequired(ArmyKey.DIALECT));

            //4. create MappingEnv
            final MappingEnv mappingEnv;
            mappingEnv = MappingEnv.create(false, serverMeta, env.get(ArmyKey.ZONE_OFFSET), new MockJsonCodec());

            //5. create ExecutorEnv
            final String factoryName = Objects.requireNonNull(this.name);
            final ExecutorEnv executorEnv;
            executorEnv = createExecutorEnv(factoryName, serverMeta, env, mappingEnv);
            //6. create LocalExecutorFactory
            final LocalExecutorFactory executorFactory;
            executorFactory = executorProvider.createLocalFactory(executorEnv);

            final FactoryAdvice factoryAdvice;
            factoryAdvice = createFactoryAdviceComposite(this.factoryAdvices);
            //7. invoke beforeInstance
            if (factoryAdvice != null) {
                factoryAdvice.beforeInstance(serverMeta, env);
            }
            //8. create DialectEnv
            this.dialectEnv = DialectEnv.builder()
                    .factoryName(factoryName)
                    .environment(env)
                    .fieldGeneratorMap(Collections.emptyMap())//TODO
                    .mappingEnv(mappingEnv)
                    .build();

            //9. create SessionFactoryImpl instance
            this.executorFactory = executorFactory;
            this.ddlMode = env.getOrDefault(ArmyKey.DDL_MODE);
            final SyncLocalSessionFactory sessionFactory;
            sessionFactory = new SyncLocalSessionFactory(this);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Created {}[{}]", LocalSessionFactory.class.getName(), sessionFactory.name());
            }
            assert factoryName.equals(sessionFactory.name());
            assert sessionFactory.executorFactory == executorFactory;
            assert sessionFactory.mappingEnv == mappingEnv;

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
            throw new SessionFactoryException(e, e.getMessage());
        }

    }


    private ExecutorEnv createExecutorEnv(String factoryName, ServerMeta serverMeta, ArmyEnvironment env,
                                          MappingEnv mappingEnv) {
        final Map<FieldMeta<?>, FieldCodec> codecMap;
        codecMap = createCodecMap();
        return new LocalExecutorEnvironment(factoryName, serverMeta, codecMap, env, mappingEnv);
    }


    /**
     * @return a modified map
     */
    private Map<FieldMeta<?>, FieldCodec> createCodecMap() {
        final Collection<FieldCodec> codecs = this.fieldCodecs;
        final Map<FieldMeta<?>, FieldCodec> map;
        if (codecs == null) {
            map = Collections.emptyMap();
        } else {
            map = _Collections.hashMap((int) (codecs.size() / 0.75F));
            for (FieldCodec codec : codecs) {
                for (FieldMeta<?> fieldMeta : codec.fieldMetaSet()) {
                    if (map.putIfAbsent(fieldMeta, codec) == null) {
                        continue;
                    }
                    String m = String.format("%s %s duplication.", fieldMeta, FieldCodec.class.getName());
                    throw new SessionFactoryException(m);
                }
            }
        }
        final SchemaMeta schemaMeta = Objects.requireNonNull(this.schemaMeta);
        for (FieldMeta<?> fieldMeta : _TableMetaFactory.codecFieldMetaSet()) {
            if (!fieldMeta.tableMeta().schema().equals(schemaMeta)) {
                continue;
            }
            if (!map.containsKey(fieldMeta)) {
                String m = String.format("%s not found %s.", fieldMeta, FieldCodec.class.getName());
                throw new SessionFactoryException(m);
            }
        }
        return map;
    }


    /**
     * @see #build()
     */
    private void initializingFactory(SyncLocalSessionFactory sessionFactory) throws SessionFactoryException {
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
     * @see #initializingFactory(SyncLocalSessionFactory)
     */
    private static void initializingSchema(final SyncLocalSessionFactory sessionFactory, final DdlMode ddlMode) {

        final String msgPrefix;
        msgPrefix = String.format("Initializing database of %s[%s],%s[%s]",
                LocalSessionFactory.class.getName(), sessionFactory.name(),
                DdlMode.class.getName(), ddlMode);
        LOG.info(msgPrefix);
        final long startTime;
        startTime = System.currentTimeMillis();
        final ExecutorFactory executorFactory;
        executorFactory = sessionFactory.executorFactory;

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
                    ddlList = sessionFactory.dialectParser.schemaDdl(schemaResult);
                    metaExecutor.executeDdl(ddlList);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(ddlMode);
            }
            LOG.info("{},cost {} ms.", msgPrefix, System.currentTimeMillis() - startTime);
        } catch (DataAccessException e) {
            String m = String.format("%s[%s] schema initializing failure."
                    , LocalSessionFactory.class.getName(), sessionFactory.name());
            throw new SessionFactoryException(m, e);
        }


    }

    /**
     * @see #initializingSchema(SyncLocalSessionFactory, DdlMode)
     */
    private static void validateSchema(SyncLocalSessionFactory sessionFactory, _SchemaResult schemaResult) {

        final StringBuilder builder = new StringBuilder()
                .append(LocalSessionFactory.class.getName())
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


    private static ExecutorProvider createExecutorProvider(final ArmyEnvironment env, final Object dataSource) {

        final Class<?> providerClass;
        final String className = env.getOrDefault(SyncKey.EXECUTOR_PROVIDER);
        try {
            providerClass = Class.forName(className);
        } catch (Exception e) {
            String m = String.format("Load class %s %s occur error.", ExecutorProvider.class.getName(), className);
            throw new SessionFactoryException(m, e);
        }

        if (!ExecutorProvider.class.isAssignableFrom(providerClass)) {
            String m = String.format("%s value[%s] isn' the implementation of %s ."
                    , SyncKey.EXECUTOR_PROVIDER, className, ExecutorProvider.class.getName());
            throw new SessionFactoryException(m);
        }

        final String methodName = "create";
        try {

            final Method method;
            method = providerClass.getMethod(methodName, Object.class);
            final int modifiers;
            modifiers = method.getModifiers();
            if (!(Modifier.isPublic(modifiers)
                    && Modifier.isStatic(modifiers)
                    && method.getReturnType() == providerClass)) {
                String m;
                m = String.format("%s not declared %s(Object) method.", methodName, providerClass.getName());
                throw new SessionFactoryException(m);

            }
            final ExecutorProvider provider;
            provider = (ExecutorProvider) method.invoke(null, dataSource);
            if (provider == null) {
                String m = String.format("%s %s return null.", methodName, providerClass.getName());
                throw new NullPointerException(m);
            }
            return provider;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            String m = String.format("%s %s invoke error:%s", methodName, providerClass.getName(), e.getMessage());
            throw new SessionFactoryException(e, m);
        }

    }


    private static final class LocalExecutorEnvironment implements ExecutorEnv {

        private final String factoryName;


        private final ServerMeta serverMeta;

        private final Map<FieldMeta<?>, FieldCodec> fieldCodecMap;

        private final ArmyEnvironment environment;

        private final MappingEnv mappingEnv;

        private LocalExecutorEnvironment(String factoryName, ServerMeta serverMeta, Map<FieldMeta<?>, FieldCodec> fieldCodecMap,
                                         ArmyEnvironment environment, MappingEnv mappingEnv) {

            this.factoryName = factoryName;
            this.serverMeta = serverMeta;
            final Map<FieldMeta<?>, FieldCodec> emptyMap = Collections.emptyMap();
            if (fieldCodecMap == emptyMap) {
                this.fieldCodecMap = emptyMap;
            } else {
                this.fieldCodecMap = Collections.unmodifiableMap(fieldCodecMap);
            }
            this.environment = environment;
            this.mappingEnv = mappingEnv;
        }

        @Override
        public String factoryName() {
            return this.factoryName;
        }

        @Override
        public ServerMeta serverMeta() {
            return this.serverMeta;
        }

        @Override
        public Map<FieldMeta<?>, FieldCodec> fieldCodecMap() {
            return this.fieldCodecMap;
        }

        @Override
        public ArmyEnvironment environment() {
            return this.environment;
        }

        @Override
        public MappingEnv mappingEnv() {
            return this.mappingEnv;
        }

        @Override
        public String toString() {
            return String.format("%s hash:%s,factory:%s[%s]", ExecutorEnv.class.getName()
                    , System.identityHashCode(this), LocalSessionFactory.class.getName(), this.factoryName);
        }


    }//LocalExecutorEnvironment


    private static final class MockJsonCodec implements JsonCodec {
        @Override
        public String encode(Object obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object decode(String json) {
            throw new UnsupportedOperationException();
        }

    }//MockJsonCodec


}
