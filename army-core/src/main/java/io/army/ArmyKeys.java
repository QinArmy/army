package io.army;

import io.army.lang.Nullable;

public enum ArmyKeys {

    readOnly(Boolean.class, "false"),
    sessionCache(Boolean.class, "true"),
    allowSpanSharding(Boolean.class, "false"),
    ddlMode(DdlMode.class, "UPDATE"),
    dialectMode(Dialect.class, null),
    executorProvider(String.class, null);

    public final Class<?> javaType;

    public final String defaultValue;

    ArmyKeys(Class<?> javaType, @Nullable String defaultValue) {
        this.javaType = javaType;
        this.defaultValue = defaultValue;
    }


}
