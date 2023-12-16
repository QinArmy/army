package io.army.mapping.postgre;

import javax.annotation.Nullable;

import io.army.type.DaoLayer;

/**
 * <p>
 * This interface representing the function that create postgre range instance.
 * <p>
 * <strong>NOTE</strong> :This interface present only in DAO layer,not service layer,business layer,web layer.
 *
 * @see PostgreSingleRangeType
 * @since 0.6.0
 */
@DaoLayer
public interface RangeFunction<T, R> {

    /**
     * @param lower null representing infinity
     * @param upper null representing infinity
     */
    R apply(boolean includeLower, @Nullable T lower, @Nullable T upper, boolean includeUpper);

}
