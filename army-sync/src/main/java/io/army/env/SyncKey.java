package io.army.env;

public final class SyncKey<T> extends MyKey<T> {

    public static final MyKey<Boolean> JDBC_LARGE_UPDATE = new MyKey<>("jdbc.large.update", Boolean.class, Boolean.FALSE);


    private SyncKey(String name, Class<T> javaType, T defaultValue) {
        super(name, javaType, defaultValue);
    }

}
