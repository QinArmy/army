package io.army.sync;

import io.army.ArmyException;
import io.army.ArmyKeys;
import io.army.SessionFactoryException;
import io.army.advice.FactoryAdvice;
import io.army.advice.sync.DomainAdvice;
import io.army.codec.FieldCodec;
import io.army.context.spi.CurrentSessionContext;
import io.army.criteria.impl._SchemaMetaFactory;
import io.army.criteria.impl._TableMetaFactory;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.session.FactoryBuilderSupport;
import io.army.sync.executor.ExecutorFactory;
import io.army.sync.executor.ExecutorProvider;
import io.army.sync.executor.FactoryInfo;
import io.army.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

final class FactoryBuilderImpl extends FactoryBuilderSupport implements FactoryBuilder {

    Map<TableMeta<?>, DomainAdvice> domainAdviceMap = Collections.emptyMap();

    Object dataSource;

    ExecutorFactory executorFactory;

    CurrentSessionContext currentSessionContext;


    @Override
    public FactoryBuilder factoryName(String sessionFactoryName) {
        if (!StringUtils.hasText(sessionFactoryName)) {
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
    public FactoryBuilder tableCountPerDatabase(final int tableCountPerDatabase) {
        if (tableCountPerDatabase < 1) {
            throw new IllegalArgumentException("tableCountPerDatabase must be great than 0 .");
        }
        this.tableCountPerDatabase = tableCountPerDatabase;
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
    public FactoryBuilder fieldGenerator(Map<FieldMeta<?, ?>, FieldGenerator> generatorMap) {
        return this;
    }

    @Override
    public FactoryBuilder datasource(Object dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public FactoryBuilder currentSessionContext(CurrentSessionContext context) {
        this.currentSessionContext = context;
        return this;
    }

    @Override
    public SessionFactory build() throws SessionFactoryException {

        try {
            final ArmyEnvironment env = Objects.requireNonNull(this.environment);
            //1. create ExecutorFactory
            final ExecutorProvider provider;
            provider = getExecutorProvider(env);
            final ExecutorFactory executorFactory;
            executorFactory = provider.createTxFactory(Objects.requireNonNull(this.dataSource), createFactoryInfo());

            final FactoryAdvice factoryAdvice;
            factoryAdvice = createFactoryAdviceComposite(this.factoryAdvices);
            //2. invoke beforeInstance
            if (factoryAdvice != null) {
                factoryAdvice.beforeInstance(executorFactory.serverMeta(), env);
            }
            //3. create SessionFactoryImpl instance
            this.executorFactory = executorFactory;
            final SessionFactoryImpl sessionFactory;
            sessionFactory = new SessionFactoryImpl(this);

            //4. invoke beforeInitialize
            if (factoryAdvice != null) {
                factoryAdvice.beforeInitialize(sessionFactory);
            }
            //5. invoke initializingFactory
            initializingFactory(sessionFactory);
            //6. invoke afterInitialize
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


    private FactoryInfo createFactoryInfo() {
        final Map<FieldMeta<?, ?>, FieldCodec> codecMap;
        codecMap = createCodecMap();
        return new FactoryInfoImpl(codecMap, Objects.requireNonNull(this.environment));
    }


    /**
     * @return a modified map
     * @see #createFactoryInfo()
     */
    private Map<FieldMeta<?, ?>, FieldCodec> createCodecMap() {
        final Collection<FieldCodec> codecs = this.fieldCodecs;
        final Map<FieldMeta<?, ?>, FieldCodec> map;
        if (codecs == null) {
            map = Collections.emptyMap();
        } else {
            map = new HashMap<>((int) (codecs.size() / 0.75F));
            for (FieldCodec codec : codecs) {
                for (FieldMeta<?, ?> fieldMeta : codec.fieldMetaSet()) {
                    if (map.putIfAbsent(fieldMeta, codec) == null) {
                        continue;
                    }
                    String m = String.format("%s %s duplication.", fieldMeta, FieldCodec.class.getName());
                    throw new SessionFactoryException(m);
                }
            }
        }
        final SchemaMeta schemaMeta = Objects.requireNonNull(this.schemaMeta);
        for (FieldMeta<?, ?> fieldMeta : _TableMetaFactory.codecFieldMetaSet()) {
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


    private static void initializingFactory(SessionFactoryImpl factory) throws SessionFactoryException {

    }


    private static ExecutorProvider getExecutorProvider(final ArmyEnvironment env) {

        final Class<?> providerClass;
        final String className = env.get(ArmyKeys.executorProvider, String.class, "io.army.jdbc.JdbcExecutorProvider");
        try {
            providerClass = Class.forName(className);
        } catch (Exception e) {
            String m = String.format("Load class %s occur error.", ExecutorProvider.class.getName());
            throw new SessionFactoryException(e, m);
        }

        if (!ExecutorProvider.class.isAssignableFrom(providerClass)) {
            String m = String.format("%s value[%s] isn' the implementation of %s ."
                    , ArmyKeys.executorProvider, providerClass.getName(), ExecutorProvider.class.getName());
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


    private static final class FactoryInfoImpl implements FactoryInfo {

        private final Map<FieldMeta<?, ?>, FieldCodec> fieldCodecMap;

        private final ArmyEnvironment environment;

        private FactoryInfoImpl(Map<FieldMeta<?, ?>, FieldCodec> fieldCodecMap, ArmyEnvironment environment) {
            final Map<FieldMeta<?, ?>, FieldCodec> emptyMap = Collections.emptyMap();
            if (fieldCodecMap == emptyMap) {
                this.fieldCodecMap = emptyMap;
            } else {
                this.fieldCodecMap = Collections.unmodifiableMap(fieldCodecMap);
            }
            this.environment = environment;
        }

        @Override
        public Map<FieldMeta<?, ?>, FieldCodec> fieldCodecMap() {
            return this.fieldCodecMap;
        }

        @Override
        public ArmyEnvironment environment() {
            return this.environment;
        }

    }


}
