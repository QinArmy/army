package io.army.session;

import io.army.*;
import io.army.domain.IDomain;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * a abstract GenericSessionFactoryAdvice
 */
public abstract class AbstractSessionFactory implements GenericSessionFactory {

    private static final ConcurrentMap<String, Boolean> FACTORY_MAP = new ConcurrentHashMap<>(3);

    protected final String name;

    protected final ArmyEnvironment env;

    protected final SchemaMeta schemaMeta;

    protected final Map<Class<?>, TableMeta<?>> tableMetaMap;

    protected final Map<FieldMeta<?, ?>, FieldGenerator> fieldGeneratorMap;
    protected final Function<ArmyException, RuntimeException> exceptionFunction;

    protected final int tableCountPerDatabase;

    protected final boolean readOnly;

    protected final boolean supportSessionCache;

    protected final boolean allowSpanSharding;

    protected final DdlMode ddlMode;


    protected AbstractSessionFactory(final FactoryBuilderSupport support) throws SessionFactoryException {
        final String name = Assert.assertHasText(support.name, "factory name required");
        final ArmyEnvironment env = Objects.requireNonNull(support.environment);

        if (FACTORY_MAP.putIfAbsent(name, Boolean.TRUE) != null) {
            throw new SessionFactoryException("factory name[%s] duplication", name);
        }
        this.name = name;
        this.env = env;
        this.schemaMeta = Objects.requireNonNull(support.schemaMeta);
        this.tableMetaMap = FactoryUtils.scanShema(this.schemaMeta, support.packagesToScan);

        this.tableCountPerDatabase = FactoryUtils.tableCountPerDatabase(support.tableCountPerDatabase);
        this.exceptionFunction = FactoryUtils.exceptionFunction(support.exceptionFunction);
        this.fieldGeneratorMap = Objects.requireNonNull(support.generatorMap);
        this.readOnly = env.getOrDefault(ArmyKeys.readOnly, Boolean.class);

        this.supportSessionCache = env.getOrDefault(ArmyKeys.sessionCache, Boolean.class);
        this.allowSpanSharding = this.tableCountPerDatabase > 1 && env.getOrDefault(ArmyKeys.allowSpanSharding, Boolean.class);
        this.ddlMode = env.getOrDefault(ArmyKeys.ddlMode, DdlMode.class);

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
    public boolean allowSpanSharding() {
        return this.allowSpanSharding;
    }

    @Override
    public final DdlMode ddlMode() {
        return this.ddlMod;
    }

    /*################################## blow protected method ##################################*/


}
