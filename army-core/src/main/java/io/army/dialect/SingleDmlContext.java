package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._SingleDml;

abstract class SingleDmlContext extends DomainDmlContext {

    //  final List<FieldMeta<?,?>> fieldList;


    SingleDmlContext(_SingleDml dml, byte tableIndex, Dialect dialect, Visible visible) {
        super(dml.table(), dml.tableAlias(), tableIndex, dialect, visible);

    }


}
