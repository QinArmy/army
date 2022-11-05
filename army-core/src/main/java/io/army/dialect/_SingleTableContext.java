package io.army.dialect;

import io.army.meta.TableMeta;

interface _SingleTableContext extends StmtContext {

    TableMeta<?> targetTable();

    TableMeta<?> domainTable();

    String tableAlias();

    String safeTableAlias();


}
