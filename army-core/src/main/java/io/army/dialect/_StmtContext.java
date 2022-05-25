package io.army.dialect;

import io.army.criteria.Visible;
import io.army.meta.TableMeta;

/**
 * <p>
 * This interface representing a statement context,extends {@link _SqlContext}
 * and add method for the implementation of {@link _Dialect}.
 * </p>
 *
 * @since 1.0
 */
public interface _StmtContext extends _SqlContext {

    String safeTableAlias(TableMeta<?> table, String alias);

    Visible visible();

}
