package io.army.env;

import io.army.lang.Nullable;
import io.army.session.DdlMode;
import io.army.session.SubQueryInsertMode;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ArmyKey<T> {

    private static final ConcurrentMap<String, ArmyKey<?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static final ArmyKey<Boolean> READ_ONLY = new ArmyKey<>("readonly", Boolean.class, Boolean.FALSE);


    public static final ArmyKey<DdlMode> DDL_MODE = new ArmyKey<>("ddl.mode", DdlMode.class, DdlMode.VALIDATE_UNIQUE);

    public static final ArmyKey<String> ZONE_OFFSET_ID = new ArmyKey<>("zone.offset.id", String.class, null);

    public static final ArmyKey<SubQueryInsertMode> SUBQUERY_INSERT_MODE = new ArmyKey<>("subquery.insert.mode", SubQueryInsertMode.class, SubQueryInsertMode.ONLY_MIGRATION);

    public static final ArmyKey<Boolean> SQL_LOG_DYNAMIC = new ArmyKey<>("sql.log.dynamic", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<Boolean> SQL_LOG_SHOW = new ArmyKey<>("sql.log.show", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<Boolean> SQL_LOG_FORMAT = new ArmyKey<>("sql.log.format", Boolean.class, Boolean.FALSE);

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
