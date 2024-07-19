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
import io.army.codec.FieldCodec;
import io.army.codec.JsonCodec;
import io.army.codec.XmlCodec;
import io.army.criteria.impl._SchemaMetaFactory;
import io.army.criteria.impl._TableMetaFactory;
import io.army.dialect.*;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.executor.ExecutorEnv;
import io.army.executor.ExecutorFactoryProvider;
import io.army.generator.FieldGenerator;
import io.army.generator.FieldGeneratorFactory;
import io.army.mapping.MappingEnv;
import io.army.meta.*;
import io.army.option.Option;
import io.army.schema.FieldResult;
import io.army.schema.SchemaResult;
import io.army.schema.TableResult;
import io.army.util.HexUtils;
import io.army.util._Collections;
import io.army.util._FunctionUtils;
import io.army.util._StringUtils;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public abstract class _ArmyFactoryBuilder<B, R> implements FactoryBuilderSpec<B, R> {

    String name;

    ArmyEnvironment environment;

    private Object dataSource;
    protected Collection<FieldCodec> fieldCodecs;

    protected SchemaMeta schemaMeta = _SchemaMetaFactory.getSchema("", "");

    protected Map<FieldMeta<?>, FieldGenerator> generatorMap = Collections.emptyMap();

    protected FieldGeneratorFactory fieldGeneratorFactory;

    protected JsonCodec jsonCodec;

    protected XmlCodec xmlCodec;

    protected Collection<FactoryAdvice> factoryAdvices;

    protected List<String> packagesToScan;

    protected Function<String, Database> nameToDatabaseFunc;

    protected DdlMode ddlMode;


    private Map<Option<?>, Object> dataSourceOptionMap;

    private Function<Class<?>, Function<Object, ?>> converterFunc;


    /*################################## blow non-setter fields ##################################*/

    DialectParser dialectParser;

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
    public final B jsonCodec(@Nullable JsonCodec codec) {
        this.jsonCodec = codec;
        return (B) this;
    }

    @Override
    public final B xmlCodec(@Nullable XmlCodec codec) {
        this.xmlCodec = codec;
        return (B) this;
    }

    @Override
    public final B factoryAdvice(@Nullable Collection<FactoryAdvice> factoryAdvices) {
        this.factoryAdvices = factoryAdvices;
        return (B) this;
    }

    @Override
    public final B fieldGeneratorFactory(@Nullable FieldGeneratorFactory factory) {
        this.fieldGeneratorFactory = factory;
        return (B) this;
    }

    @Override
    public final B nameToDatabaseFunc(@Nullable Function<String, Database> function) {
        this.nameToDatabaseFunc = function;
        return (B) this;
    }

    @Override
    public final B executorFactoryProviderValidator(@Nullable Consumer<ExecutorFactoryProvider> consumer) {
        return (B) this;
    }

    @Override
    public final B columnConverterFunc(@Nullable Function<Class<?>, Function<Object, ?>> converterFunc) {
        this.converterFunc = converterFunc;
        return (B) this;
    }

    @Override
    public final <T> B dataSourceOption(final Option<T> option, final @Nullable T value) {
        Map<Option<?>, Object> map = this.dataSourceOptionMap;
        if (value == null && map == null) {
            return (B) this;
        }

        if (map == null) {
            this.dataSourceOptionMap = map = _Collections.hashMap();
        }
        if (value == null) {
            map.remove(option);
        } else {
            map.put(option, value);
        }
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


    protected final DialectParser createDialectParser(String factoryName, boolean reactive, ServerMeta serverMeta,
                                                      ArmyEnvironment env) {
        final Dialect dialect = env.getOrDefault(ArmyKey.DIALECT);
        if (serverMeta.usedDialect() != dialect) {
            String m = String.format("used Dialect[%s] and Environment Dialect[%s not match",
                    serverMeta.usedDialect().name(), dialect.name());
            throw new IllegalArgumentException(m);
        }
        final DialectEnv dialectEnv;
        //8. create DialectEnv
        dialectEnv = DialectEnv.builder()
                .factoryName(factoryName)
                .environment(env)
                .fieldGeneratorMap(createFieldGeneratorMap())
                .reactive(reactive)
                .serverMeta(serverMeta)
                .zoneOffset(env.get(ArmyKey.ZONE_OFFSET))
                .jsonCodec(this.jsonCodec)
                .xmlCodec(this.xmlCodec)
                .build();

        final DialectParser dialectParser;
        this.dialectParser = dialectParser = DialectParserFactory.createDialectParser(dialectEnv);
        return dialectParser;
    }


    protected final Function<Option<?>, ?> dataSourceFunc() {
        return _FunctionUtils.mapFunc(this.dataSourceOptionMap);
    }

    protected final ExecutorEnv createExecutorEnv(String factoryName, ArmyEnvironment env, DialectParser dialectParser) {
        final Map<FieldMeta<?>, FieldCodec> codecMap;
        codecMap = createCodecMap();
        return new ArmyExecutorEnvironment(factoryName, dialectParser.serverMeta(), codecMap, env, this.schemaMeta,
                dialectParser.mappingEnv(), this.converterFunc);
    }

    protected final Map<FieldMeta<?>, FieldGenerator> createFieldGeneratorMap() {
        // TODO
        return Collections.emptyMap();
    }

    protected final List<String> parseMetaDdl(_ArmySessionFactory sessionFactory, SchemaResult schemaResult) {
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


    protected static String ddlToSqlLog(final List<String> ddlList) {
        final int ddlSize = ddlList.size();
        final StringBuilder builder = new StringBuilder(ddlSize * 30);
        for (int i = 0; i < ddlSize; i++) {
            if (i > 0) {
                builder.append("\n\n");
            }
            builder.append(ddlList.get(i))
                    .append(_Constant.SPACE_SEMICOLON);
        }
        return builder.toString();
    }

    protected static DialectParser dialectParser(_ArmySessionFactory factory) {
        return factory.dialectParser;
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


    protected static <T> T createExecutorProvider(final String name, final ArmyEnvironment env, final Object dataSource,
                                                  final Class<T> providerTypeClass, final ArmyKey<String> providerKey,
                                                  final ArmyKey<String> providerMd5Key)
            throws SessionFactoryException {

        final Class<?> providerClass;
        final String className, providerMd5, validateMd5;

        className = env.getOrDefault(providerKey);
        providerMd5 = env.getOrDefault(providerMd5Key);

        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            // never here
            throw new SessionFactoryException(e);
        }
        validateMd5 = HexUtils.hexEscapesText(false, digest.digest(className.getBytes(StandardCharsets.UTF_8)));
        if (!validateMd5.equalsIgnoreCase(providerMd5)) {
            String m = String.format("SessionFactory[%s] executor provider md5 not match", name);
            throw new SessionFactoryException(m);
        }


        try {
            providerClass = Class.forName(className);
        } catch (Throwable e) {
            String m = String.format("Load class %s %s occur error.", providerTypeClass.getName(), className);
            throw new SessionFactoryException(m, e);
        }

        if (!providerTypeClass.isAssignableFrom(providerClass)) {
            String m = String.format("%s value[%s] isn' the implementation of %s .", providerKey,
                    className, providerTypeClass.getName());
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
                    && providerTypeClass.isAssignableFrom(method.getReturnType()))) {
                String m;
                m = String.format("%s not declared %s(Object,String,ArmyEnvironment) method.", providerClass.getName(), method);
                throw new SessionFactoryException(m);

            }
            final T provider;
            provider = providerTypeClass.cast(method.invoke(null, dataSource, name, env));
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

    /**
     * @return true : error
     */
    @Nullable
    protected static SessionFactoryException validateSchema(SessionFactory sessionFactory, SchemaResult schemaResult) {

        final StringBuilder builder = new StringBuilder()
                .append(sessionFactory)
                .append(" validate failure.\n");

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

        final List<TableResult> tableResultList;
        tableResultList = schemaResult.changeTableList();
        if (tableResultList.size() > 0) {
            for (TableResult tableResult : tableResultList) {
                builder.append('\n')
                        .append(tableResult.table())
                        .append(" not match:");
                differentCount += tableResult.newFieldList().size();
                for (FieldMeta<?> field : tableResult.newFieldList()) {
                    builder.append("\n\t")
                            .append(field)
                            .append(" not exists.");
                }
                for (FieldResult field : tableResult.changeFieldList()) {
                    if (field.containSqlType() || field.containDefault() || field.containNotNull()) {
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

        final SessionFactoryException error;
        if (differentCount > 0) {
            error = new SessionFactoryException(builder.toString());
        } else {
            error = null;
        }
        return error;

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


    private static final class ArmyExecutorEnvironment implements ExecutorEnv {

        private final String factoryName;

        private final ServerMeta serverMeta;

        private final Map<FieldMeta<?>, FieldCodec> fieldCodecMap;

        private final ArmyEnvironment environment;

        private final SchemaMeta schemaMeta;

        private final MappingEnv mappingEnv;

        private final Function<Class<?>, Function<Object, ?>> converterFunc;

        private ArmyExecutorEnvironment(String factoryName, ServerMeta serverMeta, Map<FieldMeta<?>, FieldCodec> fieldCodecMap,
                                        ArmyEnvironment environment, SchemaMeta schemaMeta, MappingEnv mappingEnv,
                                        @Nullable Function<Class<?>, Function<Object, ?>> converterFunc) {

            this.factoryName = factoryName;
            this.serverMeta = serverMeta;
            final Map<FieldMeta<?>, FieldCodec> emptyMap = Collections.emptyMap();
            if (fieldCodecMap == emptyMap) {
                this.fieldCodecMap = emptyMap;
            } else {
                this.fieldCodecMap = Collections.unmodifiableMap(fieldCodecMap);
            }
            this.environment = environment;
            this.schemaMeta = schemaMeta;
            this.mappingEnv = mappingEnv;
            this.converterFunc = converterFunc;
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
        public SchemaMeta schemaMeta() {
            return this.schemaMeta;
        }

        @Override
        public MappingEnv mappingEnv() {
            return this.mappingEnv;
        }


        @Override
        public Function<Class<?>, Function<Object, ?>> converterFunc() {
            return this.converterFunc;
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
