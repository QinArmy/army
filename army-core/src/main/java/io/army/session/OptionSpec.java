package io.army.session;

import io.army.lang.Nullable;

import java.util.Objects;

public interface OptionSpec {

    @Nullable
    <T> T valueOf(Option<T> option);

    default <T> T nonNullOf(Option<T> option) {
        final T value;
        value = valueOf(option);
        Objects.requireNonNull(value);
        return value;
    }


}
