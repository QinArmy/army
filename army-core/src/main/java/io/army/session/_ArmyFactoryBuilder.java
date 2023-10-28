package io.army.session;

import io.army.advice.FactoryAdvice;
import io.army.codec.FieldCodec;
import io.army.codec.JsonCodec;
import io.army.criteria.impl._SchemaMetaFactory;
import io.army.criteria.impl._TableMetaFactory;
import io.army.dialect.Database;
import io.army.dialect.DialectEnv;
import io.army.env.ArmyEnvironment;
import io.army.executor.ExecutorEnv;
import io.army.generator.FieldGenerator;
import io.army.generator.FieldGeneratorFactory;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.meta.*;
import io.army.schema._SchemaResult;
import io.army.util._Collections;
import io.army.util._StringUtils;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Function;

public abstract class _ArmyFactoryBuilder<B, R> implements FactoryBuilderSpec<B, R> {

    String name;

    ArmyEnvironment environment;

    private Object dataSource;
    protected Collection<FieldCodec> fieldCodecs;

    protected SchemaMeta schemaMeta = _SchemaMetaFactory.getSchema("", "");

    protected Map<FieldMeta<?>, FieldGenerator> generatorMap = Collections.emptyMap();

    protected FieldGeneratorFactory fieldGeneratorFactory;

    protected Collection<FactoryAdvice> factoryAdvices;

    protected List<String> packagesToScan;

    protected Function<String, Database> nameToDatabaseFunc;

    protected DdlMode ddlMode;

    protected DialectEnv dialectEnv;


    /*################################## blow non-setter fields ##################################*/

    Map<Class<?>, TableMeta<?>> tableMap;


    protected _ArmyFactoryBuilder() {
    }

    @Override
    public final B name(String sessionFactoryName) {
        if (this.name != null) {
            throw new IllegalStateException("name non-null");
        }
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
        final String name = this.name;
        final Object dataSource = this.dataSource;
        final ArmyEnvironment env = this.environment;


        SessionFactoryException error;

        if (!_StringUtils.hasText(name)) {
            error = new SessionFactoryException("factory name is required");
        } else if (dataSource == null) {
            error = new SessionFactoryException("datasource is required");
        } else if (env == null) {
            error = new SessionFactoryException("environment is required");
        } else if (_Collections.isEmpty(this.packagesToScan)) {
            error = new SessionFactoryException("packagesToScan is required");
        } else {

            try {
                this.scanTableMeta();   // scan table meta
                error = null;
            } catch (SessionFactoryException e) {
                error = e;
            }
        }

        if (error != null) {
            return handleError(error);
        }

        return buildAfterScanTableMeta(name, dataSource, env);
    }

    protected abstract R buildAfterScanTableMeta(final String name, final Object dataSource, final ArmyEnvironment env);

    protected abstract R handleError(SessionFactoryException cause);


    protected abstract Logger getLogger();


    protected final ExecutorEnv createExecutorEnv(String factoryName, ServerMeta serverMeta, ArmyEnvironment env,
                                                  MappingEnv mappingEnv) {
        final Map<FieldMeta<?>, FieldCodec> codecMap;
        codecMap = createCodecMap();
        return new ArmyExecutorEnvironment(factoryName, serverMeta, codecMap, env, mappingEnv);
    }

    protected final Map<FieldMeta<?>, FieldGenerator> createFieldGeneratorMap() {
        // TODO
        return Collections.emptyMap();
    }

    protected final List<String> parseMetaDdl(_ArmySessionFactory sessionFactory, _SchemaResult schemaResult) {
        return sessionFactory.dialectParser.schemaDdl(schemaResult);
    }


    private void scanTableMeta() throws SessionFactoryException {

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
        if (_Collections.isEmpty(factoryAdvices)) {
            return null;
        }
        List<FactoryAdvice> orderedAdviceList;
        orderedAdviceList = _Collections.arrayList(factoryAdvices);
        orderedAdviceList.sort(Comparator.comparingInt(FactoryAdvice::order));
        orderedAdviceList = Collections.unmodifiableList(orderedAdviceList);
        return new SessionFactoryAdviceComposite(orderedAdviceList);
    }


    protected static final class MockJsonCodec implements JsonCodec {

        public MockJsonCodec() {
        }

        @Override
        public String encode(Object obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object decode(String json) {
            throw new UnsupportedOperationException();
        }

    }//MockJsonCodec


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


    private static final class ArmyExecutorEnvironment implements ExecutorEnv {

        private final String factoryName;

        private final ServerMeta serverMeta;

        private final Map<FieldMeta<?>, FieldCodec> fieldCodecMap;

        private final ArmyEnvironment environment;

        private final MappingEnv mappingEnv;

        private ArmyExecutorEnvironment(String factoryName, ServerMeta serverMeta, Map<FieldMeta<?>, FieldCodec> fieldCodecMap,
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
            return _StringUtils.builder(50)
                    .append(getClass().getName())
                    .append("[sessionFactoryName:")
                    .append(this.factoryName)
                    .append(",hash:")
                    .append(System.identityHashCode(this))
                    .append(']')
                    .toString();
        }


    } //ArmyExecutorEnvironment

}
