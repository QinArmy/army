package io.army.criteria.impl;

import io.army.criteria.EmptyObject;
import io.army.criteria.UpdateAble;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

public abstract class ObjectSQLS extends AbstractSQLS {

    public static  <T extends IDomain> UpdateAble.AliasAble<T, EmptyObject> update(TableMeta<T> tableMeta) {
        return new ObjectUpdateAbleImpl<>(tableMeta, EmptyObject.getInstance());
    }

    public static  <T extends IDomain, C> UpdateAble.AliasAble<T, C> updateWithCriteria(
            TableMeta<T> tableMeta, C criteria) {
        return new ObjectUpdateAbleImpl<>(tableMeta, criteria);
    }


}
