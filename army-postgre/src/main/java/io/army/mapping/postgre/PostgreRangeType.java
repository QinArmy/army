package io.army.mapping.postgre;

import io.army.lang.Nullable;
import io.army.mapping.MappingType;

import java.util.function.Function;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link PostgreSingleRangeType}</li>
 *         <li>{@link PostgreMultiRangeType}</li>
 *     </ul>
 * </p>
 *
 * @param <T> java class of subtype of range
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-BUILTIN">Built-in Range and Multirange Types</a>
 */
public abstract class PostgreRangeType<T> extends ArmyPostgreRangeType<T> {


    /**
     * package constructor
     */
    PostgreRangeType(Class<?> javaType, Class<T> elementType, @Nullable RangeFunction<T, ?> rangeFunc,
                     Function<String, T> parseFunc) {
        super(javaType, elementType, rangeFunc, parseFunc);
    }


    public abstract MappingType subtype();





}
