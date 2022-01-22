package io.army.criteria.impl.inner;

import io.army.meta.TableMeta;

public interface _SingleDml extends _Dml {

    String tableAlias();

    TableMeta<?> table();

}
