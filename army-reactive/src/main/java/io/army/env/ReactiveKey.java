package io.army.env;

import javax.annotation.Nullable;

public final class ReactiveKey<T> extends ArmyKey<T> {


    /**
     * @see #EXECUTOR_PROVIDER_MD5
     */
    public static final ReactiveKey<String> EXECUTOR_PROVIDER = new ReactiveKey<>("reactive.executor.provider", String.class, "io.army.jdbd.JdbdExecutorFactoryProvider");

    /**
     * @see #EXECUTOR_PROVIDER
     */
    public static final ReactiveKey<String> EXECUTOR_PROVIDER_MD5 = new ReactiveKey<>("reactive.executor.provider_md5", String.class, "6fbb231ebf613cc0c129439bd9504ae1");

    /**
     * private constructor
     */
    private ReactiveKey(String name, Class<T> javaType, @Nullable T defaultValue) {
        super(name, javaType, defaultValue);
    }


}
