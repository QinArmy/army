package io.army.env;

import io.army.lang.Nullable;

public final class SyncKey<T> extends ArmyKey<T> {

    public static final SyncKey<Boolean> JDBC_FORBID_V18 = new SyncKey<>("sync.jdbc.v18", Boolean.class, Boolean.FALSE);

    public static final SyncKey<String> EXECUTOR_PROVIDER = new SyncKey<>("sync.executor.provider", String.class, "io.army.jdbc.JdbcExecutorProvider");

    public static final SyncKey<String> SESSION_CONTEXT = new SyncKey<>("sync.session.context", String.class, null);

    private SyncKey(String name, Class<T> javaType, @Nullable T defaultValue) {
        super(name, javaType, defaultValue);
    }

}
