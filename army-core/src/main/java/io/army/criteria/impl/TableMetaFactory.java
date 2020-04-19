package io.army.criteria.impl;

import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

public abstract class TableMetaFactory {

    public static <T extends IDomain> TableMeta<T> createTableMeta(Class<T> entityClass) {
        return DefaultTableMeta.createTableInstance(entityClass);
    }

    public static <T extends IDomain> ParentTableMeta<T> createParentTableMta(Class<T> entityClass) {
        return DefaultTableMeta.createParentInstance(entityClass);
    }

    public static <T extends IDomain> ChildTableMeta<T> createChildTableMeta(ParentTableMeta<? super T> parentTableMeta
            , Class<T> entityClass) {
        return DefaultTableMeta.createChildInstance(parentTableMeta, entityClass);
    }

    public static <T extends IDomain> TableMeta<T> getTableMeta(Class<T> entityClass) throws IllegalArgumentException {
        return DefaultTableMeta.getTableMeta(entityClass);
    }

    public static <T extends IDomain> ParentTableMeta<T> getParentTableMeta(Class<T> entityClass)
            throws IllegalArgumentException {
        return DefaultTableMeta.getParentTableMeta(entityClass);
    }

    public static <T extends IDomain> ChildTableMeta<T> getChildTableMeta(Class<T> entityClass)
            throws IllegalArgumentException {
        return DefaultTableMeta.getChildTableMeta(entityClass);
    }

    public static void cleanCache() {
        if (MetaUtils.discriminatorCodeMap != null) {
            MetaUtils.discriminatorCodeMap.clear();
            MetaUtils.discriminatorCodeMap = null;
        }
    }

}
