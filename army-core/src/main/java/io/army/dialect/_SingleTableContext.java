package io.army.dialect;

import io.army.meta.TableMeta;

interface _SingleTableContext extends _PrimaryContext {

    TableMeta<?> targetTable();

    TableMeta<?> domainTable();

    String tableAlias();

    String safeTableAlias();


}
