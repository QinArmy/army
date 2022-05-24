package io.army.dialect;

import io.army.criteria.impl.inner._MultiDelete;
import io.army.stmt.SimpleStmt;

/**
 * @since 1.0
 */
public interface _MultiDeleteContext extends _DeleteContext {

    @Override
    SimpleStmt build();

    _MultiDelete statement();

}
