package io.army.criteria.impl.inner;

import io.army.beans.ReadWrapper;

import java.util.List;

/**
 * <p>
 * This interface representing batch dml(only update and delete).
 * The design for dialect instance parse sql.
 * </p>
 * <p>
 * This class is base interface of below :
 *     <ul>
 *         <li>{@link _BatchSingleUpdate}</li>
 *         <li>{@link _BatchDelete}</li>
 *     </ul>
 * </p>
 */
public interface _BatchDml extends _Statement {

    /**
     * @return a unmodifiable list
     */
    List<ReadWrapper> wrapperList();


    /**
     * @return a unmodifiable list
     */
    List<_Predicate> predicateList();
}
