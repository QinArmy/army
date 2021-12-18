package io.army.dialect;

import io.army.meta.TableMeta;

public interface _Clause {

    TableMeta<?> table();

    String tableAlias();

    String safeTableAlias();

}
