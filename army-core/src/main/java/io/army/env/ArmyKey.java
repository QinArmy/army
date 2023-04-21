package io.army.env;

import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.session.DdlMode;
import io.army.session.QueryInsertMode;

import java.time.ZoneOffset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ArmyKey<T> {

    private static final ConcurrentMap<String, ArmyKey<?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static final ArmyKey<Boolean> READ_ONLY = new ArmyKey<>("readonly", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<DdlMode> DDL_MODE = new ArmyKey<>("ddl.mode", DdlMode.class, DdlMode.VALIDATE_UNIQUE);

    public static final ArmyKey<Dialect> DIALECT = new ArmyKey<>("dialect", Dialect.class, null);

    public static final ArmyKey<ZoneOffset> ZONE_OFFSET = new ArmyKey<>("zone.offset", ZoneOffset.class, null);

    public static final ArmyKey<Boolean> USE_QUALIFIED_TABLE_NAME = new ArmyKey<>("use.qualified.table.name", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<Boolean> TABLE_NAME_UPPER = new ArmyKey<>("table.name.upper", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<Boolean> COLUMN_NAME_UPPER = new ArmyKey<>("column.name.upper", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<QueryInsertMode> SUBQUERY_INSERT_MODE = new ArmyKey<>("subquery.insert.mode", QueryInsertMode.class, QueryInsertMode.ONLY_MIGRATION);

    public static final ArmyKey<Boolean> SQL_LOG_DYNAMIC = new ArmyKey<>("sql.log.dynamic", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<Boolean> SQL_LOG_SHOW = new ArmyKey<>("sql.log.show", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<Boolean> SQL_LOG_BEAUTIFY = new ArmyKey<>("sql.log.format", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<Boolean> SQL_LOG_DEBUG = new ArmyKey<>("sql.log.debug", Boolean.class, Boolean.FALSE);


    public final String name;

    public final Class<T> javaType;

    public final T defaultValue;

    ArmyKey(String name, Class<T> javaType, @Nullable T defaultValue) {
        if (INSTANCE_MAP.putIfAbsent(name, this) != null) {
            String m = String.format("name[%s] duplication.", name);
            throw new IllegalArgumentException(m);
        }
        this.name = name;
        this.javaType = javaType;
        this.defaultValue = defaultValue;
    }


    @Override
    public final int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof ArmyKey) {
            match = ((ArmyKey<?>) obj).name.equals(this.name);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public final String toString() {
        return String.format("[%s name:%s,javaType:%s,default:%s]"
                , this.getClass().getName(), this.name
                , this.javaType.getName(), this.defaultValue);
    }


}
