package io.army.dialect;

import io.army.meta.TableMeta;

public interface _SingleSetClause extends _SetClause {

    TableMeta<?> table();

    String safeTableAlias();

}
