package io.army.criteria.impl;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;

public abstract class TableMetaFactory {

    public static <T extends IDomain> TableMeta<T> createTableMeta(Class<T> entityClass) {
        return new DefaultTableMeta<>(entityClass);
    }

    public static <T extends IDomain> TableMeta<T> createTableMeta(TableMeta<? super T> parentTableMeta, Class<T> entityClass) {
        return new DefaultTableMeta<>(parentTableMeta, entityClass);
    }

    public static void cleanCache() {
        if (MetaUtils.discriminatorCodeMap != null) {
            MetaUtils.discriminatorCodeMap.clear();
            MetaUtils.discriminatorCodeMap = null;
        }
    }

}
