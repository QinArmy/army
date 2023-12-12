package io.army.criteria.impl.inner;

import java.util.List;

/**
 * <p>
 * This interface representing batch dml(only update and delete).
 * The design for dialect instance parse sql.
*/
public interface _BatchStatement extends _Statement {

    /**
     * @return a unmodifiable list
     */
    List<?> paramList();

}
