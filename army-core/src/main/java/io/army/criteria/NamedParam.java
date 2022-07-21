package io.army.criteria;

import io.army.stmt.SqlParam;

/**
 * <p>
 * This interface representing named param for batch update or batch delete.
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link NonNullNamedParam}</li>
 *     <li>{@link NamedElementParam}</li>
 * </ul>
 * </p>
 *
 * @see NonNullNamedParam
 */
public interface NamedParam extends Expression, SqlParam {

    String name();


    /**
     * <p>
     * Actual parameter value store in bean or map.
     * </p>
     *
     * @throws CriteriaException always throws.
     */
    @Override
    Object value() throws CriteriaException;

}
