package io.army.session;

import io.army.ArmyException;
import io.army.ArmyKeys;
import io.army.DdlMode;
import io.army.bean.ObjectAccessor;
import io.army.bean.ReadWrapper;
import io.army.codec.JsonCodec;
import io.army.dialect.FieldValueGenerator;
import io.army.dialect._AbstractFieldValuesGenerator;
import io.army.dialect._DialectEnvironment;
import io.army.domain.IDomain;
import io.army.env.ArmyEnvironment;
import io.army.env.MyKey;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.*;
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
public abstract class _AbstractSessionFactory implements GenericSessionFactory, _DialectEnvironment {

    private static final ConcurrentMap<String, Boolean> FACTORY_MAP = new ConcurrentHashMap<>(3);

    protected final String name;

    protected final ArmyEnvironment env;

    protected final SchemaMeta schemaMeta;

    public final Map<Class<?>, TableMeta<?>> tableMap;

    protected final Function<ArmyException, RuntimeException> exceptionFunction;

    protected final SubQueryInsertMode subQueryInsertMode;

    protected final FieldValueGenerator fieldValuesGenerator;

    protected final boolean readonly;

    protected final boolean supportSessionCache;

    protected final ZoneOffset zoneOffset;

    public final boolean uniqueCache;

    private final boolean sqlLogDynamic;

    private final boolean sqlLogShow;

    private final boolean sqlLogFormat;


    protected _AbstractSessionFactory(final FactoryBuilderSupport support) throws SessionFactoryException {
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

        this.subQueryInsertMode = env.get(ArmyKeys.SUBQUERY_INSERT_MODE, SubQueryInsertMode.class, SubQueryInsertMode.ONLY_MIGRATION);
        this.readonly = env.get(ArmyKeys.READ_ONLY, Boolean.class, Boolean.FALSE);

        this.zoneOffset = support.zoneOffset;

        this.supportSessionCache = env.get(ArmyKeys.sessionCache, Boolean.class, Boolean.TRUE);
        this.fieldValuesGenerator = new FieldValuesGeneratorImpl(this.zoneOffset, support.generatorMap);

        final DdlMode ddlMode = support.ddlMode;
        assert ddlMode != null;
        this.uniqueCache = ddlMode != DdlMode.NONE;
        this.sqlLogDynamic = env.getOrDefault(MyKey.SQL_LOG_DYNAMIC);
        this.sqlLogShow = env.getOrDefault(MyKey.SQL_LOG_SHOW);
        this.sqlLogFormat = env.getOrDefault(MyKey.SQL_LOG_FORMAT);
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


    @Override
    public final boolean supportSessionCache() {
        return this.supportSessionCache;
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
    public final FieldValueGenerator fieldValuesGenerator() {
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

        private final Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap;

        private FieldValuesGeneratorImpl(@Nullable ZoneOffset zoneOffset
                , Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap) {
            Objects.requireNonNull(fieldGeneratorMap);
            this.zoneOffset = zoneOffset;
            this.fieldGeneratorMap = fieldGeneratorMap;
        }

        @Override
        protected ZoneOffset zoneOffset() {
            final ZoneOffset zoneOffset = this.zoneOffset;
            return zoneOffset == null ? TimeUtils.systemZoneOffset() : zoneOffset;
        }

        @Override
        protected void generatorChan(TableMeta<?> table, IDomain domain, ObjectAccessor accessor, ReadWrapper wrapper) {
            final Map<FieldMeta<?>, FieldGenerator> fieldGeneratorMap = this.fieldGeneratorMap;
            FieldGenerator generator;
            if (table instanceof ChildTableMeta) {
                final ParentTableMeta<?> parent = ((ChildTableMeta<?>) table).parentMeta();
                for (FieldMeta<?> field : parent.fieldChain()) {
                    generator = fieldGeneratorMap.get(field);
                    assert generator != null;
                    accessor.set(domain, field.fieldName(), generator.next(field, wrapper));
                }
            }
            for (FieldMeta<?> field : table.fieldChain()) {
                generator = fieldGeneratorMap.get(field);
                assert generator != null;
                accessor.set(domain, field.fieldName(), generator.next(field, wrapper));
            }
        }

    }// FieldValuesGeneratorImpl


}
