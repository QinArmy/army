package io.army.dialect;

import io.army.criteria.TabularItem;
import io.army.meta.TableMeta;

/**
 * <p>
 * This interface representing multi-table context,this interface is base interface of below:
 *     <ul>
 *         <li>{@link  _SimpleQueryContext}</li>
 *         <li>{@link  _MultiUpdateContext}</li>
 *         <li>{@link  _MultiDeleteContext}</li>
 *     </ul>
*
 * @since 1.0
 */
public interface _MultiTableContext extends SqlContextSpec {


    String safeTableAlias(TableMeta<?> table, String alias);

    String safeTableAlias(String alias);

    String saTableAliasOf(TableMeta<?> table);

    TabularItem tableItemOf(String tableAlias);


}
