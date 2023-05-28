package io.army.mapping.postgre;

import io.army.lang.Nullable;
import io.army.type.DaoLayer;

/**
 * <p>
 * This interface representing the function that create postgre range instance.
 * </p>
 * <p>
 * <strong>NOTE</strong> :This interface present only in DAO layer,not service layer,business layer,web layer.
 * </p>
 *
 * @see PostgreSingleRangeType
 * @since 1.0
 */
@DaoLayer
public interface RangeFunction<T, R> {

    /**
     * @param lower null representing infinity
     * @param upper null representing infinity
     */
    R apply(boolean includeLower, @Nullable T lower, @Nullable T upper, boolean includeUpper);

}
