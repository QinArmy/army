package io.army.env;

import javax.annotation.Nullable;

public final class ReactiveKey<T> extends ArmyKey<T> {

    private ReactiveKey(String name, Class<T> javaType, @Nullable T defaultValue) {
        super(name, javaType, defaultValue);
    }


}
