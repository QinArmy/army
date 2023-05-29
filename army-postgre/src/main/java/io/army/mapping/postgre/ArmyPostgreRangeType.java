package io.army.mapping.postgre;

import io.army.lang.Nullable;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.util.ArrayUtils;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * Package class This class is base class of below:
 *     <ul>
 *         <li>{@link PostgreRangeType}</li>
 *         <li>{@link PostgreSingleRangeArrayType}</li>
 *         <li>{@link PostgreMultiRangeArrayType}</li>
 *     </ul>
 * </p>
 *
 * @param <T> java type of subtype of range
 * @since 1.0
 */
abstract class ArmyPostgreRangeType<T> extends _ArmyNoInjectionMapping {


    final Class<?> javaType;

    final RangeFunction<T, ?> rangeFunc;

    final Function<String, T> parseFunc;

    final PostgreSingleRangeType.MockRangeFunction<T> mockFunction;

    /**
     * <p>
     * package constructor
     * </p>
     *
     * @param elementType null when only javaType is {@link String#getClass()}
     */
    ArmyPostgreRangeType(Class<?> javaType, Class<T> elementType, @Nullable RangeFunction<T, ?> rangeFunc,
                         Function<String, T> parseFunc) {
        final Class<?> underlyingType;
        if (javaType == String.class) {
            underlyingType = javaType;
        } else {
            underlyingType = ArrayUtils.underlyingComponent(javaType);
        }
        assert rangeFunc != null || underlyingType == String.class;
        this.javaType = javaType;
        this.rangeFunc = rangeFunc;
        this.parseFunc = parseFunc;
        if (underlyingType == String.class || ArmyPostgreRange.class.isAssignableFrom(javaType)) {
            this.mockFunction = null;
        } else {
            this.mockFunction = PostgreRangeType.createMockFunction(javaType, elementType);
        }
    }

    @Override
    public final Class<?> javaType() {
        return this.javaType;
    }


    abstract void boundToText(T bound, Consumer<String> appender);

    abstract Class<T> boundJavaType();


}
