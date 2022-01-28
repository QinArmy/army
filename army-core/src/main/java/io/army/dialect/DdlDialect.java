package io.army.dialect;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;

interface DdlDialect {


    <T extends IDomain> String createTable(TableMeta<T> table);


}
