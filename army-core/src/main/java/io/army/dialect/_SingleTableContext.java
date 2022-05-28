package io.army.dialect;

import io.army.meta.TableMeta;

interface _SingleTableContext extends _StmtContext {

    TableMeta<?> table();

    String safeAlias();


}
