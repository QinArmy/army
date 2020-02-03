package io.army.criteria.impl;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Assert;

public abstract class TableMetaFactory {

    public static <T extends IDomain> TableMeta<T> createTableMeta(Class<T> entityClass) {
        return DefaultTableMeta.createInstance(null, entityClass);
    }

    public static <T extends IDomain> TableMeta<T> createTableMeta(@Nullable TableMeta<? super T> parentTableMeta
            , Class<T> entityClass) {
        return DefaultTableMeta.createInstance(parentTableMeta, entityClass);
    }

    public static <T extends IDomain> TableMeta<T> getTableMeta(Class<T> entityClass) throws IllegalArgumentException {
        Assert.notNull(entityClass, "entityClass required");
        return DefaultTableMeta.getMeta(entityClass);
    }

    public static void cleanCache() {
        if (MetaUtils.discriminatorCodeMap != null) {
            MetaUtils.discriminatorCodeMap.clear();
            MetaUtils.discriminatorCodeMap = null;
        }
    }

}
