package io.army.criteria.impl;

import io.army.criteria.DeleteAble;
import io.army.criteria.EmptyObject;

public abstract class ObjectSQLS extends AbstractSQLS {
/*
    public static <T extends IDomain> Update.AliasAble<T, EmptyObject> update(TableMeta<T> tableMeta) {
        return new ObjectUpdateImpl<>(tableMeta, EmptyObject.getInstance());
    }

    public static <T extends IDomain, C> Update.AliasAble<T, C> multiUpdate(
            TableMeta<T> tableMeta, C criteria) {
        return new ObjectUpdateImpl<>(tableMeta, criteria);
    }*/

    public static DeleteAble.FromAble<EmptyObject> delete() {
        return new ObjectDeleteAbleImpl<>(EmptyObject.getInstance());
    }

    public static <C> DeleteAble.FromAble<C> prepareDelete(C criteria){
        return new ObjectDeleteAbleImpl<>(criteria);
    }


}
