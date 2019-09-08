package io.army.dialect;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;

/**
 * A common interface to all dialect of database.
 * created  on 2019-02-22.
 */
public interface Dialect {

    Func func();

    <T extends IDomain> String tableDefinition(TableMeta<T> tableMeta);

}
