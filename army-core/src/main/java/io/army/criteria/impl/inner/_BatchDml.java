package io.army.criteria.impl.inner;

import java.util.List;

/**
 * <p>
 * This interface representing batch dml(only update and delete).
 * The design for dialect instance parse sql.
 * </p>
 */
public interface _BatchDml extends _Statement {

    /**
     * @return a unmodifiable list
     */
    List<?> paramList();


    /**
     * @return a unmodifiable list
     */
    List<_Predicate> predicateList();
}
