package io.army.session;

import io.army.advice.FactoryAdvice;
import io.army.codec.FieldCodec;
import io.army.criteria.impl._SchemaMetaFactory;
import io.army.criteria.impl._TableMetaFactory;
import io.army.dialect.Database;
import io.army.dialect.DialectEnv;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.executor.ExecutorEnv;
import io.army.generator.FieldGenerator;
import io.army.generator.FieldGeneratorFactory;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.meta.*;
import io.army.util._Collections;

import java.util.*;
import java.util.function.Function;

public abstract class _ArmyFactoryBuilder<B, R> implements FactoryBuilderSpec<B, R> {

    protected String name;

    protected ArmyEnvironment environment;

    protected Object dataSource;
    protected Collection<FieldCodec> fieldCodecs;

    protected SchemaMeta schemaMeta = _SchemaMetaFactory.getSchema("", "");

    protected Map<FieldMeta<?>, FieldGenerator> generatorMap = Collections.emptyMap();

    protected FieldGeneratorFactory fieldGeneratorFactory;

    protected Collection<FactoryAdvice> factoryAdvices;

    protected List<String> packagesToScan;

    private Function<String, Database> nameToDatabaseFunc;

    protected DdlMode ddlMode;

    protected DialectEnv dialectEnv;


    /*################################## blow non-setter fields ##################################*/

    Map<Class<?>, TableMeta<?>> tableMap;


    protected _ArmyFactoryBuilder() {
    }

    @Override
    public final B name(String sessionFactoryName) {
        this.name = sessionFactoryName;
        return (B) this;
    }

    @Override
    public final B environment(ArmyEnvironment environment) {
        this.environment = environment;
        return (B) this;
    }

    @Override
    public final B datasource(Object dataSource) {
        this.dataSource = dataSource;
        return (B) this;
    }

    @Override
    public final B packagesToScan(List<String> packageList) {
        this.packagesToScan = packageList;
        return (B) this;
    }

    @Override
    public final B schema(String catalog, String schema) {
        this.schemaMeta = _SchemaMetaFactory.getSchema(catalog, schema);
        return (B) this;
    }

    @Override
    public final B factoryAdvice(Collection<FactoryAdvice> factoryAdvices) {
        this.factoryAdvices = factoryAdvices;
        return (B) this;
    }


    @Override
    public final B fieldGeneratorFactory(FieldGeneratorFactory factory) {
        this.fieldGeneratorFactory = factory;
        return (B) this;
    }

    @Override
    public final B nameToDatabaseFunc(@Nullable Function<String, Database> function) {
        this.nameToDatabaseFunc = function;
        return (B) this;
    }

    @Override
    public final R build() {

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
            final ArmySyncLocalSessionFactory sessionFactory;
            sessionFactory = new ArmySyncLocalSessionFactory(this);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Created {}[{}]", SyncLocalSessionFactory.class.getName(), sessionFactory.name());
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


    protected final void scanSchema() {

        final List<String> packagesToScan = this.packagesToScan;
        if (packagesToScan == null || packagesToScan.isEmpty()) {
            throw new SessionFactoryException("No specified package to scan.");
        }
        SchemaMeta schemaMeta = this.schemaMeta;
        if (schemaMeta == null) {
            schemaMeta = _SchemaMetaFactory.getSchema("", "");
        }
        final Map<Class<?>, TableMeta<?>> tableMetaMap;
        tableMetaMap = _TableMetaFactory.getTableMetaMap(schemaMeta, packagesToScan);
        if (tableMetaMap.isEmpty()) {
            String m;
            if (schemaMeta.defaultSchema()) {
                m = String.format("Not found any %s for default schema.", TableMeta.class.getName());
            } else {
                m = String.format("Not found any %s for %s.", TableMeta.class.getName(), schemaMeta);
            }
            throw new SessionFactoryException(m);
        }

        final FieldGeneratorFactory generatorFactory = this.fieldGeneratorFactory;
        List<FieldMeta<?>> fieldChain;
        GeneratorMeta meta;

        final Map<FieldMeta<?>, FieldGenerator> generatorMap = _Collections.hashMap();
        FieldGenerator generator;
        for (TableMeta<?> table : tableMetaMap.values()) {
            fieldChain = table.fieldChain();
            if (fieldChain.size() == 0) {
                continue;
            }
            for (FieldMeta<?> field : fieldChain) {
                meta = field.generator();
                assert meta != null;
                if (generatorFactory == null) {
                    throw notSpecifiedFieldGeneratorFactory(field);
                }
                generator = generatorFactory.get(field);
                if (!meta.javaType().isInstance(generator)) {
                    throw fieldGeneratorTypeError(meta, generator);
                }
                generatorMap.put(field, generator);
            }
        }
        if (generatorMap.size() > 0) {
            this.generatorMap = Collections.unmodifiableMap(generatorMap);
        }
        this.tableMap = tableMetaMap;
    }

    private SessionFactoryException fieldGeneratorTypeError(GeneratorMeta meta, @Nullable FieldGenerator generator) {
        String m = String.format("%s %s type %s isn't %s."
                , meta.field(), FieldGenerator.class.getName(), generator, meta.javaType().getName());
        throw new SessionFactoryException(m);
    }

    private SessionFactoryException notSpecifiedFieldGeneratorFactory(FieldMeta<?> field) {
        String m = String.format("%s has %s ,but not specified %s."
                , field, GeneratorMeta.class.getName(), FieldGeneratorFactory.class.getName());
        throw new SessionFactoryException(m);
    }


    @Nullable
    protected static FactoryAdvice createFactoryAdviceComposite(Collection<FactoryAdvice> factoryAdvices) {
        if (factoryAdvices == null || factoryAdvices.isEmpty()) {
            return null;
        }
        List<FactoryAdvice> orderedAdviceList;
        orderedAdviceList = new ArrayList<>(factoryAdvices);
        orderedAdviceList.sort(Comparator.comparingInt(FactoryAdvice::order));
        orderedAdviceList = Collections.unmodifiableList(orderedAdviceList);
        return new SessionFactoryAdviceComposite(orderedAdviceList);
    }


    protected static final class SessionFactoryAdviceComposite implements FactoryAdvice {

        private final List<FactoryAdvice> adviceList;

        private SessionFactoryAdviceComposite(List<FactoryAdvice> adviceList) {
            this.adviceList = adviceList;
        }

        @Override
        public int order() {
            return 0;
        }

        @Override
        public void beforeInstance(ServerMeta serverMeta, ArmyEnvironment environment) {
            for (FactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.beforeInstance(serverMeta, environment);
            }
        }


        @Override
        public void beforeInitialize(SessionFactory sessionFactory) {
            for (FactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.beforeInitialize(sessionFactory);
            }
        }

        @Override
        public void afterInitialize(SessionFactory sessionFactory) {
            for (FactoryAdvice factoryAdvice : this.adviceList) {
                factoryAdvice.afterInitialize(sessionFactory);
            }
        }

    }

}
