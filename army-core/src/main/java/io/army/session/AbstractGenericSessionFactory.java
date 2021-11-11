package io.army.session;

import io.army.ArmyException;
import io.army.ArmyKey;
import io.army.SessionFactoryException;
import io.army.codec.FieldCodec;
import io.army.domain.IDomain;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * a abstract GenericSessionFactoryAdvice
 */
public abstract class AbstractGenericSessionFactory implements GenericSessionFactory {

    private static final ConcurrentMap<String, AbstractGenericSessionFactory> FACTORY_MAP = new ConcurrentHashMap<>(3);

    protected final String name;

    protected final ArmyEnvironment env;

    protected final ZoneId zoneId;

    protected final SchemaMeta schemaMeta;

    protected final Map<Class<?>, TableMeta<?>> tableMetaMap;

    protected final Map<FieldMeta<?, ?>, FieldGenerator> fieldGeneratorMap;

    protected final Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain;

    protected final Map<FieldMeta<?, ?>, FieldCodec> fieldCodecMap;

    protected final FactoryMode factoryMode;
    protected final Function<ArmyException, RuntimeException> exceptionFunction;

    protected final int tableCountPerDatabase;

    protected final boolean readOnly;

    protected final boolean supportSessionCache;

    protected final boolean shardingSubQueryInsert;

    protected final boolean allowSpanSharding;

    protected final boolean springApplication;

    protected final boolean compareDefaultOnMigrating;

    protected AbstractGenericSessionFactory(final FactoryBuilderSupport support) throws SessionFactoryException {
        final String name = Assert.assertHasText(support.name, "factory name required");
        final ArmyEnvironment env = Objects.requireNonNull(support.environment);

        if (FACTORY_MAP.putIfAbsent(name, this) != null) {
            throw new SessionFactoryException("factory name[%s] duplication", name);
        }
        this.name = name;
        this.env = env;
        this.schemaMeta = Objects.requireNonNull(support.schemaMeta);
        this.tableMetaMap = GenericSessionFactoryUtils.scanPackagesForMeta(this.schemaMeta, support.packagesToScan);
        this.tableCountPerDatabase = GenericSessionFactoryUtils.tableCountPerDatabase(support.tableCountPerDatabase);

        this.factoryMode = this.tableCountPerDatabase > 1 ? FactoryMode.SINGLE_DATABASE_SHARDING : FactoryMode.NO_SHARDING;
        this.exceptionFunction = GenericSessionFactoryUtils.createComposedExceptionFunction(support.exceptionFunction);

        GenericSessionFactoryUtils.GeneratorWrapper generatorWrapper =
                GenericSessionFactoryUtils.createGeneratorWrapper(this.tableMetaMap.values(), this);
        this.fieldGeneratorMap = generatorWrapper.getGeneratorChain();
        this.tableGeneratorChain = generatorWrapper.getTableGeneratorChain();

        this.readOnly = GenericSessionFactoryUtils.readOnly(this.name, this.env);
        this.fieldCodecMap = GenericSessionFactoryUtils.createTableFieldCodecMap(support.fieldCodecs());
        this.supportSessionCache = GenericSessionFactoryUtils.sessionCache(this.env, this.name);
        this.shardingSubQueryInsert = GenericSessionFactoryUtils.shardingSubQueryInsert(
                this.env, this.name, this.factoryMode);

        this.allowSpanSharding = GenericSessionFactoryUtils.allowSpanSharding(this.env, this.name, this.factoryMode);
        this.springApplication = support.springApplication();
        this.compareDefaultOnMigrating = GenericSessionFactoryUtils.compareDefaultOnMigrating(this.env, this.name);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public ArmyEnvironment environment() {
        return this.env;
    }

    @Override
    public ZoneOffset zoneOffset() {
        throw new UnsupportedOperationException();
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
    public FactoryMode shardingMode() {
        return this.factoryMode;
    }

    @Override
    public boolean supportSessionCache() {
        return this.supportSessionCache;
    }


    @Override
    public boolean readonly() {
        return this.readOnly;
    }

    @Override
    public boolean showSQL() {
        return env.get(String.format(ArmyKey.SHOW_SQL, this.name), Boolean.class, Boolean.FALSE);
    }

    @Override
    public boolean formatSQL() {
        return env.get(String.format(ArmyKey.FORMAT_SQL, this.name), Boolean.class, Boolean.FALSE);
    }

    @Override
    public boolean shardingSubQueryInsert() {
        return this.shardingSubQueryInsert;
    }

    @Override
    public boolean allowSpanSharding() {
        return this.allowSpanSharding;
    }


    /*################################## blow protected method ##################################*/

    protected final boolean notNeedMigrateMeta() {
        String key = String.format(ArmyKey.MIGRATION_MODE, this.name);
        return !this.env.get(key, Boolean.class, Boolean.FALSE);
    }

}
