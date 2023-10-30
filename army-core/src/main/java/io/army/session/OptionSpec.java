package io.army.session;

import javax.annotation.Nullable;

import io.army.util._Exceptions;

public interface OptionSpec {

    @Nullable
    <T> T valueOf(Option<T> option);

    default <T> T nonNullOf(Option<T> option) {
        final T value;
        value = valueOf(option);
        if (value == null) {
            throw _Exceptions.optionValueIsNull(option);
        }
        return value;
    }


}
