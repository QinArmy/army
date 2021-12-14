package io.army.criteria.impl.inner;

import io.army.meta.TableMeta;

public interface _SingleUpdate extends _Update, _SingleDml {

    TableMeta<?> table();

    int databaseIndex();

    int tableIndex();

}
