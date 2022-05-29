package io.army.dialect;

import io.army.criteria.TableItem;
import io.army.meta.TableMeta;

interface _MultiTableContext extends _SqlContext {

    String safeTableAlias(TableMeta<?> table, String alias);

    String saTableAliasOf(TableMeta<?> table);

    TableItem tableItemOf(String tableAlias);


}
