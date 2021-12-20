package io.army.criteria;

import io.army.stmt.ParamValue;

/**
 * <p>
 * This interface representing named param for batch update or batch delete.
 * </p>
 *
 * @param <E> java type of named parameter
 * @see io.army.criteria.impl.NonNullNamedParam
 */
public interface NamedParam<E> extends Expression<E>, ParamValue {

    String name();


    /**
     * <p>
     * Actual parameter value store in bean or map.
     * </p>
     *
     * @throws UnsupportedOperationException always throws.
     */
    @Override
    Object value() throws UnsupportedOperationException;

}
