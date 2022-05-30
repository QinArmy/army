package io.army.dialect;

import io.army.meta.TableMeta;

interface _SingleTableContext extends StmtContext {

    TableMeta<?> table();

    String tableAlias();

    String safeTableAlias();


}
