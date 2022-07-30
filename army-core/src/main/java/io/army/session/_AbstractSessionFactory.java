package io.army.session;

import io.army.ArmyException;
import io.army.dialect._DialectEnv;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.util._Assert;

import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * a abstract GenericSessionFactoryAdvice
 *
 * @since 1.0
 */
public abstract class _AbstractSessionFactory implements GenericSessionFactory, _DialectEnv {

    private static final ConcurrentMap<String, Boolean> FACTORY_MAP = new ConcurrentHashMap<>(3);

    protected final String name;

    public final ArmyEnvironment env;

    protected final SchemaMeta schemaMeta;

    public final Map<Class<?>, TableMeta<?>> tableMap;

    protected final Function<ArmyException, RuntimeException> exceptionFunction;

    protected final SubQueryInsertMode subQueryInsertMode;

    protected final boolean readonly;

    protected final ZoneId zoneId;

    public final boolean uniqueCache;

    public final boolean sqlLogDynamic;


    private final Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap;

    private final boolean sqlLogShow;

    private final boolean sqlLogFormat;

    public final boolean sqlLogDebug;


    protected _AbstractSessionFactory(final FactoryBuilderSupport support) throws SessionFactoryException {
        final String name = _Assert.assertHasText(support.name, "factory name required");
        final ArmyEnvironment env = support.environment;
        assert env != null;

        if (FACTORY_MAP.putIfAbsent(name, Boolean.TRUE) != null) {
            throw new SessionFactoryException(String.format("factory name[%s] duplication", name));
        }
        this.name = name;
        this.env = env;
        this.schemaMeta = Objects.requireNonNull(support.schemaMeta);
        this.tableMap = Objects.requireNonNull(support.tableMap);
        this.exceptionFunction = exceptionFunction(support.exceptionFunction);

        this.subQueryInsertMode = env.getOrDefault(ArmyKey.SUBQUERY_INSERT_MODE);
        this.readonly = env.getOrDefault(ArmyKey.READ_ONLY);

        this.zoneId = support.zoneOffset;
        this.fieldGeneratorMap = support.generatorMap;

        final DdlMode ddlMode = support.ddlMode;
        assert ddlMode != null;
        this.uniqueCache = ddlMode != DdlMode.NONE;

        this.sqlLogDynamic = env.getOrDefault(ArmyKey.SQL_LOG_DYNAMIC);
        this.sqlLogShow = env.getOrDefault(ArmyKey.SQL_LOG_SHOW);
        this.sqlLogFormat = env.getOrDefault(ArmyKey.SQL_LOG_FORMAT);
        this.sqlLogDebug = env.getOrDefault(ArmyKey.SQL_LOG_DEBUG);
    }


    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final boolean uniqueCache() {
        return this.uniqueCache;
    }

    @Override
    public final ArmyEnvironment environment() {
        return this.env;
    }

    @Override
    public final ZoneId zoneId() {
        return this.mappingEnv().zoneId();
    }

    @Override
    public final Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap() {
        return this.fieldGeneratorMap;
    }

    @Override
    public final SchemaMeta schemaMeta() {
        return this.schemaMeta;
    }

    @Override
    public MappingEnv mappingEnv() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Map<Class<?>, TableMeta<?>> tableMap() {
        return this.tableMap;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public final <T> TableMeta<T> tableMeta(Class<T> domainClass) {
        return (TableMeta<T>) this.tableMap.get(domainClass);
    }

    @Override
    public final boolean readonly() {
        return this.readonly;
    }

    @Override
    public final Function<ArmyException, RuntimeException> exceptionFunction() {
        return this.exceptionFunction;
    }


    @Override
    public boolean factoryClosed() {
        return false;
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj == this;
    }


    @Nullable
    public final Function<String, String> getSqlFormatter() {
        final Function<String, String> function;
        if (this.sqlLogDynamic) {
            if (!this.env.getOrDefault(ArmyKey.SQL_LOG_SHOW)) {
                function = null;
            } else if (this.env.getOrDefault(ArmyKey.SQL_LOG_FORMAT)) {
                function = this::formatSql;
            } else {
                function = this::noFormatSql;
            }
        } else if (!this.sqlLogShow) {
            function = null;
        } else if (this.sqlLogFormat) {
            function = this::formatSql;
        } else {
            function = this::noFormatSql;
        }
        return function;
    }


    private String formatSql(String sql) {
        return sql;
    }

    private String noFormatSql(String sql) {
        return sql;
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

    private static byte tableCountPerDatabase(final int tableCount) {
        if (tableCount < 1 || tableCount > 99) {
            String m = String.format("Table count[%s] per database must great than 0 .", tableCount);
            throw new SessionFactoryException(m);
        }
        return (byte) tableCount;
    }


}
