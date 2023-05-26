package io.army.mapping.postgre;

import io.army.type.DaoLayer;

/**
 * <p>
 * This interface representing postgre int8range type.
 * </p>
 * <p>
 * <strong>NOTE</strong> :
 *   <ul>
 *       <li>This interface present only in DAO layer,not service layer,business layer,web layer.</li>
 *       <li>Your class must declare the methods of this interface,but your class possibly isn't the subclass of this interface.</li>
 *   </ul>
 * </p>
 *
 * @since 1.0
 */
@DaoLayer
public interface ArmyPostgreInt8Range extends ArmyPostgreRange {

    /**
     * @throws IllegalStateException when {@link #isEmpty()} is ture.
     */
    long getLowerBound();

    /**
     * @throws IllegalStateException when {@link #isEmpty()} is ture.
     */
    long getUpperBound();


}
