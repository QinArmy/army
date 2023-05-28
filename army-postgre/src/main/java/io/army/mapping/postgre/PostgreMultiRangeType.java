package io.army.mapping.postgre;

import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;

import java.util.function.Function;

public abstract class PostgreMultiRangeType<T> extends PostgreRangeType<T> {


    PostgreMultiRangeType(Class<?> javaType, Class<T> elementType, @Nullable RangeFunction<T, ?> rangeFunc,
                          Function<String, T> parseFunc) {
        super(javaType, elementType, rangeFunc, parseFunc);
    }

    @Override
    public final MappingType compatibleFor(Class<?> targetType) throws NoMatchMappingException {
        throw noMatchCompatibleMapping(this, targetType);
    }

    @Override
    public final boolean isSameType(MappingType type) {
        return super.isSameType(type);
    }


}
