package io.army.env;

import io.army.lang.Nullable;

public class MyKey<T> {

    public static final MyKey<Boolean> READ_ONLY = new MyKey<>("readonly", Boolean.class, Boolean.FALSE);

    public static final MyKey<String> ZONE_OFFSET_ID = new MyKey<>("zone.offset.id", String.class, null);

    public static final MyKey<String> CURRENT_SESSION_CONTEXT = new MyKey<>("current.session.context", String.class, null);

    public static final MyKey<Boolean> SQL_LOG_DYNAMIC = new MyKey<>("sql.log.dynamic", Boolean.class, Boolean.FALSE);

    public static final MyKey<Boolean> SQL_LOG_SHOW = new MyKey<>("sql.log.show", Boolean.class, Boolean.FALSE);

    public static final MyKey<Boolean> SQL_LOG_FORMAT = new MyKey<>("sql.log.format", Boolean.class, Boolean.FALSE);

    public static final MyKey<Boolean> SQL_LOG_DEBUG = new MyKey<>("sql.log.debug", Boolean.class, Boolean.FALSE);


    public final String name;

    public final Class<T> javaType;

    public final T defaultValue;

    MyKey(String name, Class<T> javaType, @Nullable T defaultValue) {
        this.name = name;
        this.javaType = javaType;
        this.defaultValue = defaultValue;
    }


}
