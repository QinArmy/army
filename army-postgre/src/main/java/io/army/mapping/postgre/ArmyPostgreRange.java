package io.army.mapping.postgre;

import io.army.lang.Nullable;
import io.army.type.DaoLayer;

/**
 * <p>
 * This interface representing postgre  range type.
 * </p>
 * <p>
 * <strong>NOTE</strong> :
 *   <ul>
 *       <li>This interface present only in DAO layer,not service layer,business layer,web layer.</li>
 *       <li>Your class must declare the methods of this interface,but your class possibly isn't the subclass of this interface.</li>
 *   </ul>
 * </p>
 * @see RangeFunction
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html">Range Types</a>
 * @since 1.0
 */
@DaoLayer
public interface ArmyPostgreRange<T> {

    /**
     * @return true : empty
     * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-IO"> Range Input/Output</a>
     */
    boolean isEmpty();

    /**
     * @return true: when only include lower bound
     * @throws IllegalStateException when {@link #isEmpty()} is true.
     */
    boolean isIncludeLowerBound();

    /**
     * @return true: when only include upper bound
     * @throws IllegalStateException when {@link #isEmpty()} is true.
     */
    boolean isIncludeUpperBound();


    /**
     * <p>
     * null representing infinity bound.
     * </p>
     *
     * @throws IllegalStateException when {@link #isEmpty()} is true.
     */
    @Nullable
    T getLowerBound();

    /**
     * <p>
     * null representing infinity bound.
     * </p>
     *
     * @throws IllegalStateException when {@link #isEmpty()} is true.
     */
    @Nullable
    T getUpperBound();


}
