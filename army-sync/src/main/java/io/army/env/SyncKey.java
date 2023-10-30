package io.army.env;

import javax.annotation.Nullable;

public final class SyncKey<T> extends ArmyKey<T> {

    public static final SyncKey<Boolean> JDBC_FORBID_V18 = new SyncKey<>("sync.jdbc.v18", Boolean.class, Boolean.FALSE);

    public static final SyncKey<Boolean> SESSION_IDENTIFIER_ENABLE = new SyncKey<>("sync.session.identifier.enable", Boolean.class, Boolean.FALSE);

    /**
     * @see #EXECUTOR_PROVIDER_MD5
     */
    public static final SyncKey<String> EXECUTOR_PROVIDER = new SyncKey<>("sync.executor.provider", String.class, "io.army.jdbc.JdbcExecutorProvider");

    /**
     * @see #EXECUTOR_PROVIDER
     */
    public static final SyncKey<String> EXECUTOR_PROVIDER_MD5 = new SyncKey<>("sync.executor.provider_md5", String.class, "io.army.jdbc.JdbcExecutorProvider");

    public static final SyncKey<String> SESSION_CONTEXT = new SyncKey<>("sync.session.context", String.class, null);

    private SyncKey(String name, Class<T> javaType, @Nullable T defaultValue) {
        super(name, javaType, defaultValue);
    }

}
