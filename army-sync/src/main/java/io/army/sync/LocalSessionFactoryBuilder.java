package io.army.sync;

import io.army.ArmyException;
import io.army.advice.FactoryAdvice;
import io.army.advice.sync.DomainAdvice;
import io.army.codec.FieldCodec;
import io.army.codec.JsonCodec;
import io.army.criteria.impl._SchemaMetaFactory;
import io.army.criteria.impl._TableMetaFactory;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.env.SyncKey;
import io.army.generator.FieldGeneratorFactory;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.schema.*;
import io.army.session.DataAccessException;
import io.army.session.DdlMode;
import io.army.session.FactoryBuilderSupport;
import io.army.session.SessionFactoryException;
import io.army.sync.executor.ExecutorEnvironment;
import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.ExecutorProvider;
import io.army.sync.executor.MetaExecutor;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;

final class LocalSessionFactoryBuilder extends FactoryBuilderSupport implements FactoryBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(LocalSessionFactoryBuilder.class);

    Map<TableMeta<?>, DomainAdvice> domainAdviceMap = Collections.emptyMap();

    private Object dataSource;

    ExecutorFactory executorFactory;

    SessionContext sessionContext;


    @Override
    public FactoryBuilder name(String sessionFactoryName) {
        if (!_StringUtils.hasText(sessionFactoryName)) {
            throw new IllegalArgumentException("sessionFactoryName must have text.");
        }
        this.name = sessionFactoryName;
        return this;
    }

    @Override
    public FactoryBuilder schema(String catalog, String schema) {
        this.schemaMeta = _SchemaMetaFactory.getSchema(catalog, schema);
        return this;
    }

    @Override
    public FactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs) {
        this.fieldCodecs = fieldCodecs;
        return this;
    }

    @Override
    public FactoryBuilder environment(ArmyEnvironment environment) {
        this.environment = environment;
        return this;
    }


    @Override
    public FactoryBuilder factoryAdvice(Collection<FactoryAdvice> factoryAdvices) {
        this.factoryAdvices = factoryAdvices;
        return this;
    }

    @Override
    public FactoryBuilder exceptionFunction(Function<ArmyException, RuntimeException> exceptionFunction) {
        this.exceptionFunction = exceptionFunction;
        return this;
    }

    @Override
    public FactoryBuilder domainAdvice(Map<TableMeta<?>, DomainAdvice> domainAdviceMap) {
        this.domainAdviceMap = Objects.requireNonNull(domainAdviceMap);
        return this;
    }

    @Override
    public FactoryBuilder fieldGeneratorFactory(@Nullable FieldGeneratorFactory factory) {
        this.fieldGeneratorFactory = factory;
        return this;
    }

    @Override
    public FactoryBuilder datasource(Object dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public FactoryBuilder packagesToScan(List<String> packageList) {
        this.packagesToScan = _CollectionUtils.asUnmodifiableList(packageList);
        return this;
    }

    @Override
    public FactoryBuilder currentSessionContext(SessionContext context) {
        this.sessionContext = context;
        return this;
    }

    @Override
    public SessionFactory build() throws SessionFactoryException {

        try {
            final ArmyEnvironment env = Objects.requireNonNull(this.environment);
            //1. scan table meta
            this.scanSchema();

            //2. create ExecutorFactory
            final ExecutorFactory executorFactory;
            executorFactory = getExecutorProvider(env)
                    .createFactory(Objects.requireNonNull(this.dataSource), createFactoryInfo(env));

            final FactoryAdvice factoryAdvice;
            factoryAdvice = createFactoryAdviceComposite(this.factoryAdvices);
            //3. invoke beforeInstance
            if (factoryAdvice != null) {
                factoryAdvice.beforeInstance(executorFactory.serverMeta(), env);
            }
            //4. create SessionFactoryImpl instance
            this.executorFactory = executorFactory;
            this.ddlMode = env.getOrDefault(ArmyKey.DDL_MODE);
            initializingZoneOffset(env);
            final LocalSessionFactory sessionFactory;
            sessionFactory = new LocalSessionFactory(this);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Created {}[{}]", SessionFactory.class.getName(), sessionFactory.name());
            }
            //5. invoke beforeInitialize
            if (factoryAdvice != null) {
                factoryAdvice.beforeInitialize(sessionFactory);
            }

            //6. invoke initializingFactory
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


    private ExecutorEnvironment createFactoryInfo(ArmyEnvironment env) {
        final Map<FieldMeta<?>, FieldCodec> codecMap;
        codecMap = createCodecMap();
        return new FactoryInfoImpl(codecMap, env);
    }


    /**
     * @return a modified map
     * @see #createFactoryInfo(ArmyEnvironment)
     */
    private Map<FieldMeta<?>, FieldCodec> createCodecMap() {
        final Collection<FieldCodec> codecs = this.fieldCodecs;
        final Map<FieldMeta<?>, FieldCodec> map;
        if (codecs == null) {
            map = Collections.emptyMap();
        } else {
            map = new HashMap<>((int) (codecs.size() / 0.75F));
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
    private void initializingFactory(LocalSessionFactory sessionFactory) throws SessionFactoryException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing {}", sessionFactory);
        }

        final ArmyEnvironment env = sessionFactory.environment();
        // initializing schema
        final DdlMode ddlMode;
        ddlMode = this.ddlMode;
        switch (ddlMode) {
            case NONE:
                // no-op
                break;
            case VALIDATE:
            case UPDATE:
            case DROP_CREATE:
                initializingSchema(sessionFactory, ddlMode);
                break;
            default:
                throw _Exceptions.unexpectedEnum(ddlMode);
        }

    }

    /**
     * @see #initializingFactory(LocalSessionFactory)
     */
    private static void initializingSchema(final LocalSessionFactory sessionFactory, final DdlMode ddlMode) {

        final String msgPrefix;
        msgPrefix = String.format("Initializing schema of %s[%s],%s[%s]"
                , SessionFactory.class.getName(), sessionFactory.name()
                , DdlMode.class.getName(), ddlMode);
        LOG.info(msgPrefix);
        final long startTime;
        startTime = System.currentTimeMillis();
        final ExecutorFactory executorFactory;
        executorFactory = sessionFactory.executorFactory;

        try (MetaExecutor metaExecutor = executorFactory.createMetaExecutor()) {

            //1.extract schema info.
            final _SchemaInfo schemaInfo;
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
                    ddlList = sessionFactory.dialect.schemaDdl(schemaResult);
                    metaExecutor.executeDdl(ddlList);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(ddlMode);
            }
            LOG.info("{},cost {} ms.", msgPrefix, System.currentTimeMillis() - startTime);
        } catch (DataAccessException e) {
            String m = String.format("%s[%s] schema initializing failure."
                    , SessionFactory.class.getName(), sessionFactory.name());
            throw new SessionFactoryException(m, e);
        }


    }

    /**
     * @see #initializingSchema(LocalSessionFactory, DdlMode)
     */
    private static void validateSchema(LocalSessionFactory sessionFactory, _SchemaResult schemaResult) {

        final StringBuilder builder = new StringBuilder()
                .append(SessionFactory.class.getName())
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
                    if (field.sqlType() || field.defaultValue() || field.nullable()) {
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


    private static ExecutorProvider getExecutorProvider(final ArmyEnvironment env) {

        final Class<?> providerClass;
        final String className = env.getOrDefault(SyncKey.EXECUTOR_PROVIDER);
        try {
            providerClass = Class.forName(className);
        } catch (Exception e) {
            String m = String.format("Load class %s occur error.", ExecutorProvider.class.getName());
            throw new SessionFactoryException(m, e);
        }

        if (!ExecutorProvider.class.isAssignableFrom(providerClass)) {
            String m = String.format("%s value[%s] isn' the implementation of %s ."
                    , SyncKey.EXECUTOR_PROVIDER, providerClass.getName(), ExecutorProvider.class.getName());
            throw new SessionFactoryException(m);
        }

        try {
            final Method method;
            method = providerClass.getMethod("getInstance");
            final int modifiers;
            modifiers = method.getModifiers();
            final ExecutorProvider provider;
            if (Modifier.isPublic(modifiers)
                    && Modifier.isStatic(modifiers)
                    && method.getReturnType() == providerClass) {
                provider = (ExecutorProvider) method.invoke(null);
                if (provider == null) {
                    String m = String.format("%s getInstance return null.", providerClass.getName());
                    throw new NullPointerException(m);
                }
            } else {
                String m = String.format("%s not declared getInstance method.", providerClass.getName());
                throw new SessionFactoryException(m);
            }
            return provider;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            String m = String.format("%s getInstance invoke error:%s", providerClass.getName(), e.getMessage());
            throw new SessionFactoryException(e, m);
        }

    }


    private static final class FactoryInfoImpl implements ExecutorEnvironment {

        private final Map<FieldMeta<?>, FieldCodec> fieldCodecMap;

        private final ArmyEnvironment environment;

        private FactoryInfoImpl(Map<FieldMeta<?>, FieldCodec> fieldCodecMap, ArmyEnvironment environment) {
            final Map<FieldMeta<?>, FieldCodec> emptyMap = Collections.emptyMap();
            if (fieldCodecMap == emptyMap) {
                this.fieldCodecMap = emptyMap;
            } else {
                this.fieldCodecMap = Collections.unmodifiableMap(fieldCodecMap);
            }
            this.environment = environment;
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
        public boolean inBeanContainer() {
            return this.environment.getClass().getName().equals("io.army.env.SpringArmyEnvironment");
        }

        @Override
        public ZoneOffset zoneOffset() {
            throw new UnsupportedOperationException();
        }

        @Override
        public JsonCodec jsonCodec() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReactive() {
            //always false
            return false;
        }

    }//FactoryInfoImpl


}
