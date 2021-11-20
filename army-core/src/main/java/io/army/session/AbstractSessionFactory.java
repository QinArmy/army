package io.army.session;

import io.army.ArmyException;
import io.army.ArmyKey;
import io.army.ArmyKeys;
import io.army.SessionFactoryException;
import io.army.criteria.impl._TableMetaFactory;
import io.army.domain.IDomain;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.time.ZoneOffset;
import java.util.List;
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


    protected AbstractSessionFactory(final FactoryBuilderSupport support) throws SessionFactoryException {
        final String name = Assert.assertHasText(support.name, "factory name required");
        final ArmyEnvironment env = Objects.requireNonNull(support.environment);

        if (FACTORY_MAP.putIfAbsent(name, Boolean.TRUE) != null) {
            throw new SessionFactoryException("factory name[%s] duplication", name);
        }
        this.name = name;
        this.env = env;
        this.schemaMeta = Objects.requireNonNull(support.schemaMeta);
        this.tableMetaMap = scanShema(this.schemaMeta, support.packagesToScan);

        this.tableCountPerDatabase = tableCountPerDatabase(support.tableCountPerDatabase);
        this.exceptionFunction = exceptionFunction(support.exceptionFunction);
        this.fieldGeneratorMap = Objects.requireNonNull(support.generatorMap);
        this.readOnly = env.getOrDefault(ArmyKeys.readOnly, Boolean.class);

        this.supportSessionCache = env.getOrDefault(ArmyKeys.sessionCache, Boolean.class);
        this.allowSpanSharding = this.tableCountPerDatabase > 1 && env.getOrDefault(ArmyKeys.allowSpanSharding, Boolean.class);

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
    public final Function<ArmyException, RuntimeException> exceptionFunction() {
        return this.exceptionFunction;
    }

    /*################################## blow protected method ##################################*/


    /*################################## blow private static method ##################################*/

    private static Function<ArmyException, RuntimeException> exceptionFunction(
            @Nullable Function<ArmyException, RuntimeException> function) {
        if (function == null) {
            function = e -> e;
        }
        return function;
    }

    private static Map<Class<?>, TableMeta<?>> scanShema(SchemaMeta schemaMeta, List<String> packageList) {
        final Map<Class<?>, TableMeta<?>> tableMetaMap;
        tableMetaMap = _TableMetaFactory.getTableMetaMap(schemaMeta
                , Objects.requireNonNull(packageList));
        if (tableMetaMap.isEmpty()) {
            String m;
            if (schemaMeta.defaultSchema()) {
                m = String.format("Not found any %s for default schema.", TableMeta.class.getName());
            } else {
                m = String.format("Not found any %s for %s.", TableMeta.class.getName(), schemaMeta);
            }
            throw new SessionFactoryException(m);
        }
        return tableMetaMap;
    }

    private static int tableCountPerDatabase(final int tableCount) {
        if (tableCount < 1) {
            String m = String.format("Table count[%s] per database must great than 0 .", tableCount);
            throw new SessionFactoryException(m);
        }
        return tableCount;
    }


}
