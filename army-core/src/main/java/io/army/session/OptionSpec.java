package io.army.session;

import io.army.util._Exceptions;

import javax.annotation.Nullable;

public interface OptionSpec {

    @Nullable
    <T> T valueOf(ArmyOption<T> option);

    default <T> T nonNullOf(ArmyOption<T> option) {
        final T value;
        value = valueOf(option);
        if (value == null) {
            throw _Exceptions.optionValueIsNull(option);
        }
        return value;
    }


}
