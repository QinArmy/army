package io.army.boot;

import io.army.ArmyConfigConstant;
import io.army.ErrorCode;
import io.army.SessionFactoryException;
import io.army.ShardingMode;
import io.army.codec.FieldCodec;
import io.army.domain.IDomain;
import io.army.env.Environment;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class AbstractGenericSessionFactory implements InnerGenericSessionFaction {

    private static final Map<String, AbstractGenericSessionFactory> FACTORY_MAP = new ConcurrentHashMap<>(3);


    final String name;

    final Environment env;

    final ZoneId zoneId;

    final SchemaMeta schemaMeta;

    final Map<Class<?>, TableMeta<?>> tableMetaMap;

    final Map<FieldMeta<?, ?>, FieldGenerator> fieldGeneratorMap;

    final Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain;

    final Map<FieldMeta<?, ?>, FieldCodec> fieldCodecMap;

    final ShardingMode shardingMode;

    final FieldValuesGenerator fieldValuesGenerator = FieldValuesGenerator.build(this);

    final boolean readOnly;

    final boolean supportSessionCache;

    final DomainProxyFactory domainProxyFactory;

    AbstractGenericSessionFactory(String name, Environment env, Collection<FieldCodec> fieldCodecs) {
        Assert.hasText(name, "name required");
        Assert.notNull(env, "env required");
        if (FACTORY_MAP.putIfAbsent(name, this) != null) {
            throw new SessionFactoryException(ErrorCode.FACTORY_NAME_DUPLICATION, "factory name[%s] duplication", name);
        }
        this.name = name;
        this.env = env;
        this.schemaMeta = SessionFactoryUtils.obtainSchemaMeta(this.name, env);
        this.zoneId = SessionFactoryUtils.createZoneId(env, this.name);

        this.tableMetaMap = SessionFactoryUtils.scanPackagesForMeta(this.schemaMeta, this.name, env);
        this.shardingMode = SessionFactoryUtils.shardingMode(this.name, env);
        SessionFactoryUtils.GeneratorWrapper generatorWrapper =
                SessionFactoryUtils.createGeneratorWrapper(this.tableMetaMap.values(), this);
        this.fieldGeneratorMap = generatorWrapper.getGeneratorChain();
        this.tableGeneratorChain = generatorWrapper.getTableGeneratorChain();

        this.readOnly = SessionFactoryUtils.readOnly(this.name, this.env);
        this.fieldCodecMap = SessionFactoryUtils.createTableFieldCodecMap(fieldCodecs);
        this.supportSessionCache = SessionFactoryUtils.sessionCache(this.env, this.name);
        this.domainProxyFactory = DomainProxyFactory.build(this);
    }


    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Environment environment() {
        return this.env;
    }

    @Override
    public ZoneId zoneId() {
        return this.zoneId;
    }

    @Override
    public SchemaMeta schemaMeta() {
        return this.schemaMeta;
    }

    @Override
    public Map<Class<?>, TableMeta<?>> tableMetaMap() {
        return this.tableMetaMap;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends IDomain> TableMeta<T> tableMeta(Class<T> domainClass) {
        return (TableMeta<T>) tableMetaMap.get(domainClass);
    }

    @Nullable
    @Override
    public FieldGenerator fieldGenerator(FieldMeta<?, ?> fieldMeta) {
        return this.fieldGeneratorMap.get(fieldMeta);
    }

    @Override
    public Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain() {
        return this.tableGeneratorChain;
    }

    @Override
    public List<FieldMeta<?, ?>> generatorChain(TableMeta<?> tableMeta) {
        return this.tableGeneratorChain.getOrDefault(tableMeta, Collections.emptyList());
    }


    @Override
    public FieldValuesGenerator fieldValuesGenerator() {
        return this.fieldValuesGenerator;
    }

    @Nullable
    @Override
    public FieldCodec fieldCodec(FieldMeta<?, ?> fieldMeta) {
        return this.fieldCodecMap.get(fieldMeta);
    }

    @Override
    public ShardingMode shardingMode() {
        return this.shardingMode;
    }

    @Override
    public boolean supportSessionCache() {
        return this.supportSessionCache;
    }

    @Override
    public DomainProxyFactory domainProxyFactory() {
        return this.domainProxyFactory;
    }

    @Override
    public boolean readonly() {
        return this.readOnly;
    }

    @Override
    public boolean showSQL() {
        return env.getProperty(String.format(ArmyConfigConstant.SHOW_SQL, this.name), Boolean.class, Boolean.FALSE);
    }

    @Override
    public boolean formatSQL() {
        return env.getProperty(String.format(ArmyConfigConstant.FORMAT_SQL, this.name), Boolean.class, Boolean.FALSE);
    }
}
