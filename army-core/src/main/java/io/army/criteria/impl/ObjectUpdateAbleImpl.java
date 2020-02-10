package io.army.criteria.impl;

import io.army.criteria.ObjectUpdateAble;
import io.army.criteria.impl.inner.InnerObjectUpdateAble;
import io.army.domain.IDomain;
import io.army.meta.MappingMode;
import io.army.meta.TableMeta;
import io.army.util.Assert;

final class ObjectUpdateAbleImpl<T extends IDomain, C> extends UpdateAbleImpl<T, C>
        implements ObjectUpdateAble, InnerObjectUpdateAble {


    ObjectUpdateAbleImpl(TableMeta<T> tableMeta, C criteria) {
        super(tableMeta, criteria);
        if (tableMeta.mappingMode() != MappingMode.CHILD
                || tableMeta.parentMeta() == null) {
            throw new IllegalArgumentException("ObjectUpdate only child mapping mode");
        }
    }


}
