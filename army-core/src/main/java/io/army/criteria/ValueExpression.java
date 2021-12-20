package io.army.criteria;

import io.army.lang.Nullable;

/**
 * @param <E> java type of expression
 */
public interface ValueExpression<E> extends Expression<E> {

    /**
     * @return a simple java object or {@link java.util.Collection}
     */
    @Nullable
    Object value();
}
