package io.army.session;

import io.army.ArmyException;
import io.army.ArmyKey;
import io.army.ArmyKeys;
import io.army.bean.ObjectAccessor;
import io.army.codec.JsonCodec;
import io.army.dialect.FieldGenerator;
import io.army.dialect._AbstractFieldValuesGenerator;
import io.army.dialect._DialectEnvironment;
import io.army.domain.IDomain;
import io.army.env.ArmyEnvironment;
import io.army.env.MyKey;
import io.army.generator._FieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.util.TimeUtils;
import io.army.util._Assert;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * a abstract GenericSessionFactoryAdvice
 */
public abstract class AbstractSessionFactory implements GenericSessionFactory, _DialectEnvironment {

    private static final ConcurrentMap<String, Boolean> FACTORY_MAP = new ConcurrentHashMap<>(3);

    protected final String name;

    protected final ArmyEnvironment env;

    protected final SchemaMeta schemaMeta;

    protected final Map<Class<?>, TableMeta<?>> tableMap;

    protected final Map<FieldMeta<?>, _FieldGenerator> fieldGeneratorMap;

    protected final Function<ArmyException, RuntimeException> exceptionFunction;

    protected final SubQueryInsertMode subQueryInsertMode;

    protected final FieldGenerator fieldValuesGenerator;

    protected final boolean readonly;

    protected final boolean supportSessionCache;

    protected final ZoneOffset zoneOffset;

    private final boolean sqlLogDynamic;

    private final boolean sqlLogShow;

    private final boolean sqlLogFormat;

    protected AbstractSessionFactory(final FactoryBuilderSupport support) throws SessionFactoryException {
        final String name = _Assert.assertHasText(support.name, "factory name required");
        final ArmyEnvironment env = Objects.requireNonNull(support.environment);

        if (FACTORY_MAP.putIfAbsent(name, Boolean.TRUE) != null) {
            throw new SessionFactoryException(String.format("factory name[%s] duplication", name));
        }
        this.name = name;
        this.env = env;
        this.schemaMeta = Objects.requireNonNull(support.schemaMeta);
        this.tableMap = Objects.requireNonNull(support.tableMap);
        this.exceptionFunction = exceptionFunction(support.exceptionFunction);
        this.fieldGeneratorMap = Objects.requireNonNull(support.generatorMap);

        this.subQueryInsertMode = env.get(ArmyKeys.SUBQUERY_INSERT_MODE, SubQueryInsertMode.class, SubQueryInsertMode.ONLY_MIGRATION);
        this.readonly = env.get(ArmyKeys.READ_ONLY, Boolean.class, Boolean.FALSE);

        this.zoneOffset = support.zoneOffset;

        this.supportSessionCache = env.get(ArmyKeys.sessionCache, Boolean.class, Boolean.TRUE);
        this.fieldValuesGenerator = new FieldValuesGeneratorImpl(this.zoneOffset, this.fieldGeneratorMap);

        this.sqlLogDynamic = env.getOrDefault(MyKey.SQL_LOG_DYNAMIC);
        this.sqlLogShow = env.getOrDefault(MyKey.SQL_LOG_SHOW);
        this.sqlLogFormat = env.getOrDefault(MyKey.SQL_LOG_FORMAT);
    }


    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final ArmyEnvironment environment() {
        return this.env;
    }

    @Override
    public final ZoneOffset zoneOffset() {
        final ZoneOffset zoneOffset = this.zoneOffset;
        return zoneOffset == null ? TimeUtils.systemZoneOffset() : zoneOffset;
    }

    @Override
    public final SchemaMeta schemaMeta() {
        return this.schemaMeta;
    }

    @Override
    public final Map<Class<?>, TableMeta<?>> tableMap() {
        return this.tableMap;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public final <T extends IDomain> TableMeta<T> tableMeta(Class<T> domainClass) {
        return (TableMeta<T>) this.tableMap.get(domainClass);
    }

    @Nullable
    @Override
    public final _FieldGenerator fieldGenerator(FieldMeta<?> fieldMeta) {
        return this.fieldGeneratorMap.get(fieldMeta);
    }


    @Override
    public final boolean supportSessionCache() {
        return this.supportSessionCache;
    }


    @Override
    public final boolean readonly() {
        return this.readonly;
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
    public final Function<ArmyException, RuntimeException> exceptionFunction() {
        return this.exceptionFunction;
    }

    @Override
    public final FieldGenerator fieldValuesGenerator() {
        return this.fieldValuesGenerator;
    }

    @Override
    public final JsonCodec jsonCodec() {
        return _DialectEnvironment.super.jsonCodec();
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
    public final Function<String, String> getSqlFormat() {
        Function<String, String> function;
        if (this.sqlLogDynamic) {
            if (!this.env.getOrDefault(MyKey.SQL_LOG_SHOW)) {
                function = null;
            } else if (this.env.getOrDefault(MyKey.SQL_LOG_FORMAT)) {
                function = this::formatSql;
            } else {
                function = this::noFormatSql;
            }
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

    private static final class FieldValuesGeneratorImpl extends _AbstractFieldValuesGenerator {

        private final ZoneOffset zoneOffset;

        private final Map<FieldMeta<?>, _FieldGenerator> fieldGeneratorMap;

        private FieldValuesGeneratorImpl(@Nullable ZoneOffset zoneOffset
                , Map<FieldMeta<?>, _FieldGenerator> fieldGeneratorMap) {
            this.zoneOffset = zoneOffset;
            this.fieldGeneratorMap = fieldGeneratorMap;
        }

        @Override
        protected ZoneOffset zoneOffset() {
            final ZoneOffset zoneOffset = this.zoneOffset;
            return zoneOffset == null ? TimeUtils.systemZoneOffset() : zoneOffset;
        }

        @Override
        protected void generatorChan(TableMeta<?> table, IDomain domain, ObjectAccessor accessor) {
            //TODO no-op
        }

    }// FieldValuesGeneratorImpl

}
