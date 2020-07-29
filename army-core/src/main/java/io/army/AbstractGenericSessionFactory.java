package io.army;

import io.army.cache.SessionCacheFactory;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractGenericSessionFactory implements GenericSessionFactory {

    private static final ConcurrentMap<String, AbstractGenericSessionFactory> FACTORY_MAP = new ConcurrentHashMap<>(3);

    protected final String name;

    protected final Environment env;

    protected final ZoneId zoneId;

    protected final SchemaMeta schemaMeta;

    protected final Map<Class<?>, TableMeta<?>> tableMetaMap;

    protected final Map<FieldMeta<?, ?>, FieldGenerator> fieldGeneratorMap;

    protected final Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain;

    protected final Map<FieldMeta<?, ?>, FieldCodec> fieldCodecMap;

    protected final ShardingMode shardingMode;

    protected final boolean readOnly;

    protected final boolean supportSessionCache;

    protected final SessionCacheFactory sessionCacheFactory;

    protected final boolean shardingSubQueryInsert;

    protected final boolean allowSpanSharding;

    protected AbstractGenericSessionFactory(GenericSessionFactoryParams factoryParams) {
        String name = factoryParams.getName();
        Environment env = factoryParams.getEnvironment();
        Assert.hasText(name, "name required");
        Assert.notNull(env, "env required");

        if (FACTORY_MAP.putIfAbsent(name, this) != null) {
            throw new SessionFactoryException(ErrorCode.FACTORY_NAME_DUPLICATION, "factory name[%s] duplication", name);
        }
        this.name = name;
        this.env = env;
        this.schemaMeta = GenericSessionFactoryUtils.obtainSchemaMeta(this.name, env);
        this.zoneId = GenericSessionFactoryUtils.createZoneId(env, this.name);

        this.tableMetaMap = GenericSessionFactoryUtils.scanPackagesForMeta(this.schemaMeta, this.name, env);
        this.shardingMode = GenericSessionFactoryUtils.shardingMode(this.name, env);
        GenericSessionFactoryUtils.GeneratorWrapper generatorWrapper =
                GenericSessionFactoryUtils.createGeneratorWrapper(this.tableMetaMap.values(), this);
        this.fieldGeneratorMap = generatorWrapper.getGeneratorChain();
        this.tableGeneratorChain = generatorWrapper.getTableGeneratorChain();

        this.readOnly = GenericSessionFactoryUtils.readOnly(this.name, this.env);
        this.fieldCodecMap = GenericSessionFactoryUtils.createTableFieldCodecMap(factoryParams.getFieldCodecs());
        this.supportSessionCache = GenericSessionFactoryUtils.sessionCache(this.env, this.name);
        this.sessionCacheFactory = SessionCacheFactory.build(this);

        this.shardingSubQueryInsert = GenericSessionFactoryUtils.shardingSubQueryInsert(
                this.env, this.name, this.shardingMode);
        this.allowSpanSharding = GenericSessionFactoryUtils.allowSpanSharding(this.env, this.name, this.shardingMode);

    }

    protected AbstractGenericSessionFactory(AbstractGenericSessionFactory tmSessionFactory, int factoryIndex) {
        Assert.isTrue(tmSessionFactory.shardingMode != ShardingMode.SHARDING, "ShardingMode isn't SHARDING.");
        Assert.isTrue(factoryIndex > 0, "factoryIndex must great than 0 .");

        this.name = tmSessionFactory.name + "-" + factoryIndex;
        this.env = tmSessionFactory.env;
        this.schemaMeta = tmSessionFactory.schemaMeta;
        this.zoneId = tmSessionFactory.zoneId;

        this.tableMetaMap = tmSessionFactory.tableMetaMap;
        this.shardingMode = tmSessionFactory.shardingMode;
        this.fieldGeneratorMap = tmSessionFactory.fieldGeneratorMap;
        this.tableGeneratorChain = tmSessionFactory.tableGeneratorChain;

        this.readOnly = tmSessionFactory.readOnly;
        this.fieldCodecMap = tmSessionFactory.fieldCodecMap;
        this.supportSessionCache = tmSessionFactory.supportSessionCache;
        this.sessionCacheFactory = tmSessionFactory.sessionCacheFactory;

        this.shardingSubQueryInsert = tmSessionFactory.shardingSubQueryInsert;
        this.allowSpanSharding = tmSessionFactory.allowSpanSharding;
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

    public SessionCacheFactory sessionCacheFactory() {
        return this.sessionCacheFactory;
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

    @Override
    public boolean shardingSubQueryInsert() {
        return this.shardingSubQueryInsert;
    }

    @Override
    public boolean allowSpanSharding() {
        return this.allowSpanSharding;
    }
}
