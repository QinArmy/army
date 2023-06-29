package io.army.env;

import io.army.annotation.Column;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.session.AllowMode;
import io.army.session.DdlMode;
import io.army.util._ClassUtils;
import io.army.util._Collections;
import io.army.util._StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class ArmyKey<T> {

    private static final ConcurrentMap<String, ArmyKey<?>> INSTANCE_MAP = _Collections.concurrentHashMap();

    public static final ArmyKey<Boolean> READ_ONLY = new ArmyKey<>("readonly", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<DdlMode> DDL_MODE = new ArmyKey<>("ddl.mode", DdlMode.class, DdlMode.VALIDATE);

    public static final ArmyKey<Database> DATABASE = new ArmyKey<>("database", Database.class, null);

    public static final ArmyKey<Dialect> DIALECT = new ArmyKey<>("dialect", Dialect.class, null);

    public static final ArmyKey<String> DATASOURCE_CLOSE_METHOD = new ArmyKey<>("datasource.close.method", String.class, null);

    public static final ArmyKey<ZoneOffset> ZONE_OFFSET = new ArmyKey<>("zone_offset", ZoneOffset.class, null);

    public static final ArmyKey<Boolean> QUALIFIED_TABLE_NAME_ENABLE = new ArmyKey<>("qualified_table_name.enable", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<NameMode> DATABASE_NAME_MODE = new ArmyKey<>("database.name.mode", NameMode.class, NameMode.DEFAULT);

    public static final ArmyKey<NameMode> TABLE_NAME_MODE = new ArmyKey<>("table.name.mode", NameMode.class, NameMode.DEFAULT);
    public static final ArmyKey<NameMode> COLUMN_NAME_MODE = new ArmyKey<>("column.name.mode", NameMode.class, NameMode.DEFAULT);

    public static final ArmyKey<AllowMode> VISIBLE_MODE = new ArmyKey<>("visible.mode", AllowMode.class, AllowMode.NEVER);

    public static final ArmyKey<Boolean> DATABASE_SESSION_HOLDER_ENABLE = new ArmyKey<>("database.session_holder.enable", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<String> VISIBLE_SESSION_WHITE_LIST = new ArmyKey<>("visible.session_white_list", String.class, null);

    public static final ArmyKey<AllowMode> QUERY_INSERT_MODE = new ArmyKey<>("query.insert.mode", AllowMode.class, AllowMode.WHITE_LIST);

    public static final ArmyKey<String> QUERY_INSERT_SESSION_WHITE_LIST = new ArmyKey<>("query.insert.session_white_list", String.class, null);

    /**
     * @see Column#scale()
     * @see FieldMeta#scale()
     */
    public static final ArmyKey<Boolean> TRUNCATED_TIME_TYPE = new ArmyKey<>("truncated.time_type", Boolean.class, Boolean.TRUE);

    public static final ArmyKey<NameMode> FUNC_NAME_MODE = new ArmyKey<>("func.name.mode", NameMode.class, NameMode.DEFAULT);
    public static final ArmyKey<Boolean> SQL_LOG_DYNAMIC = new ArmyKey<>("sql.log.dynamic", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<SqlLogMode> SQL_LOG_MODE = new ArmyKey<>("sql.log.mode", SqlLogMode.class, SqlLogMode.OFF);


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
        return _StringUtils.builder()
                .append(this.getClass().getName())
                .append("[ name : ")
                .append(this.name)
                .append(" , javaType : ")
                .append(this.javaType.getName())
                .append(" , default : ")
                .append(this.defaultValue)
                .append(" ]")
                .toString();
    }

    public static List<ArmyKey<?>> keyList() {
        return ArmyKeyListHolder.ARMY_KEY_LIST;
    }


    private static void addAllKey(final Class<?> keyClass, final List<ArmyKey<?>> list)
            throws IllegalAccessException {
        int modifier;
        for (Field field : keyClass.getDeclaredFields()) {
            modifier = field.getModifiers();
            if (ArmyKey.class.isAssignableFrom(field.getType())
                    && Modifier.isPublic(modifier)
                    && Modifier.isStatic(modifier)
                    && Modifier.isFinal(modifier)) {
                list.add((ArmyKey<?>) field.get(null));
            }

        }
    }

    private static final class ArmyKeyListHolder {

        private static final List<ArmyKey<?>> ARMY_KEY_LIST;

        static {
            final Class<?> syncKeyClass, reactiveKeyClass;
            syncKeyClass = _ClassUtils.tryLoadClass("io.army.env.SyncKey", ArmyKey.class.getClassLoader());
            reactiveKeyClass = _ClassUtils.tryLoadClass("io.army.env.ReactiveKey", ArmyKey.class.getClassLoader());


            final List<ArmyKey<?>> list = _Collections.arrayList(ArmyKey.INSTANCE_MAP.size());

            try {
                addAllKey(ArmyKey.class, list);
                if (syncKeyClass != null) {
                    addAllKey(syncKeyClass, list);
                }
                if (reactiveKeyClass != null) {
                    addAllKey(reactiveKeyClass, list);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            ARMY_KEY_LIST = _Collections.unmodifiableList(list);
        }


    }//ArmyKeyListHolder


}
