package io.army.util;

import io.army.session.Option;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

public abstract class _FunctionUtils {

    private _FunctionUtils() {
        throw new UnsupportedOperationException();
    }


    public static Function<Option<?>, ?> mapFunc(final @Nullable Map<Option<?>, ?> optionMap) {
        final Function<Option<?>, ?> optionFunc;
        if (optionMap == null || optionMap.size() == 0) {
            optionFunc = Option.EMPTY_OPTION_FUNC;
        } else {
            optionFunc = optionMap::get;
        }
        return optionFunc;
    }


}
