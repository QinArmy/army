package io.army.criteria.impl;

import io.army.criteria.ObjectUpdate;
import io.army.criteria.impl.inner.InnerObjectUpdate;
import io.army.domain.IDomain;
import io.army.meta.MappingMode;
import io.army.meta.TableMeta;

final class ObjectUpdateImpl<T extends IDomain, C> extends ContextualSingleUpdate<T, C>
        implements ObjectUpdate, InnerObjectUpdate {


    ObjectUpdateImpl(TableMeta<T> tableMeta, C criteria) {
        super(tableMeta, criteria);
        if (tableMeta.mappingMode() != MappingMode.CHILD
                || tableMeta.parentMeta() == null) {
            throw new IllegalArgumentException("ObjectUpdate only child mapping mode");
        }
    }


}
