package io.army.criteria.impl.inner;

import io.army.dialect.DialectParser;

/**
 * <p>
 * This interface representing the window that can be access by army {@link DialectParser}.
 * </p>
 *
 * @since 1.0
 */
public interface _Window extends _SelfDescribed {

    void prepared();

    void clear();

}
